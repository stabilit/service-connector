package org.serviceconnector.net.res.netty;

import java.net.InetSocketAddress;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.IAsyncCommand;
import org.serviceconnector.cmd.ICommand;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.PerformanceLogger;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.ResponderRegistry;
import org.serviceconnector.net.res.SCMPSessionCompositeRegistry;
import org.serviceconnector.registry.ServerRegistry;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPCompositeReceiver;
import org.serviceconnector.scmp.SCMPCompositeSender;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMessageSequenceNr;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.scmp.SCMPPart;
import org.serviceconnector.server.Server;
import org.serviceconnector.server.StatefulServer;

public abstract class NettyResponderRequestHandlerAdapter extends SimpleChannelUpstreamHandler implements IResponderCallback {
	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(NettyResponderRequestHandlerAdapter.class);
	/** The Constant performanceLogger. */
	private final static PerformanceLogger performanceLogger = PerformanceLogger.getInstance();
	/** The composite registry. */
	private static SCMPSessionCompositeRegistry compositeRegistry = AppContext.getSCMPSessionCompositeRegistry();

	/** {@inheritDoc} */
	@Override
	public abstract void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception;

	public void messageReceived(IRequest request, IResponse response, Channel channel) throws Exception {
		SCMPMessage scmpReq = request.getMessage();
		String sessionId = scmpReq.getSessionId();

		SCMPMessageSequenceNr msgSequenceNr = NettyResponderRequestHandlerAdapter.compositeRegistry.getSCMPMsgSequenceNr(sessionId);

		if (scmpReq == null) {
			// no scmp protocol used - nothing to return
			this.sendUnknownRequestError(response);
			return;
		}
		if (scmpReq.isKeepAlive()) {
			scmpReq.setIsReply(true);
			response.setSCMP(scmpReq);
			response.write();
			return;
		}
		if (scmpReq.isFault()) {
			// fault received nothing to to return - delete largeRequest/largeResponse
			this.sendBadRequestError(response, scmpReq);
			NettyResponderRequestHandlerAdapter.compositeRegistry.removeSCMPLargeResponse(sessionId);
			NettyResponderRequestHandlerAdapter.compositeRegistry.removeSCMPLargeRequest(sessionId);
			return;
		}

		// needs to set a key in thread local to identify thread later and get access to the responder
		ResponderRegistry responderRegistry = AppContext.getResponderRegistry();
		responderRegistry.setThreadLocal(channel.getParent().getId());

		request.read();
		ICommand command = AppContext.getCommandFactory().getCommand(request.getKey());
		// gets the command
		if (command == null) {
			this.sendBadRequestError(response, scmpReq);
			return;
		}

		if (command.isPassThroughPartMsg() == false) {
			// large messages needs to be handled
			SCMPCompositeSender largeResponse = NettyResponderRequestHandlerAdapter.compositeRegistry
					.getSCMPLargeResponse(sessionId);

			if (largeResponse != null && scmpReq.isPart()) {
				// sending of a large response has already been started and incoming scmp is a pull request
				if (largeResponse.hasNext()) {
					// there are still parts to send to complete request
					SCMPMessage nextSCMP = largeResponse.getNext();
					response.setSCMP(nextSCMP);
					// handling msgSequenceNr
					if (SCMPMessageSequenceNr.necessaryToWrite(nextSCMP.getMessageType())) {
						msgSequenceNr.incrementMsgSequenceNr();
						nextSCMP.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.getCurrentNr());
					}
					response.write();
					return;
				}
				NettyResponderRequestHandlerAdapter.compositeRegistry.removeSCMPLargeResponse(sessionId);
			}
			// gets the large request or creates a new one if necessary
			SCMPCompositeReceiver largeRequest = this.getSCMPLargeRequest(request, response);

			if (largeRequest != null && largeRequest.isComplete() == false) {
				// request is not complete yet
				SCMPMessage message = response.getSCMP();
				// handling msgSequenceNr
				if (SCMPMessageSequenceNr.necessaryToWrite(message.getMessageType())) {
					msgSequenceNr.incrementMsgSequenceNr();
					message.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.getCurrentNr());
				}
				response.write();
				return;
			}
			// removes largeResponse - request is complete don't need to know preceding messages any more
			NettyResponderRequestHandlerAdapter.compositeRegistry.removeSCMPLargeRequest(sessionId);
		}
		// validate request and run command
		try {
			if (Constants.COMMAND_VALIDATION_ENABLED) {
				command.validate(request);
			}
			performanceLogger.begin(this.getClass().getSimpleName(), "run");
			if (command.isAsynchronous()) {
				((IAsyncCommand) command).run(request, response, this);
				return;
			}
			command.run(request, response);
			performanceLogger.end(this.getClass().getSimpleName(), "run");
		} catch (HasFaultResponseException ex) {
			// exception carries response inside
			logger.warn("messageReceived " + ex.toString());
			ex.setSessionIdAndServiceName(request);
			ex.setFaultResponse(response);
		}
		if (response.isLarge() && command.isPassThroughPartMsg() == false) {
			// response is large, create a large response for reply
			SCMPCompositeSender largeResponse = new SCMPCompositeSender(response.getSCMP());
			SCMPMessage firstSCMP = largeResponse.getFirst();
			response.setSCMP(firstSCMP);
			// handling msgSequenceNr
			if (SCMPMessageSequenceNr.necessaryToWrite(firstSCMP.getMessageType())) {
				// no incrementation necessary - already done inside commands
				firstSCMP.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.getCurrentNr());
			}
			// adding compositeReceiver to the composite registry
			NettyResponderRequestHandlerAdapter.compositeRegistry.addSCMPLargeResponse(sessionId, largeResponse);
		}
		response.write();
	}

	/** {@inheritDoc} */
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		super.channelClosed(ctx, e);
		InetSocketAddress socketAddress = (InetSocketAddress) e.getChannel().getRemoteAddress();
		if (AppContext.isScEnvironment()) {
			// if in sc environment - clean up server
			this.cleanUpDeadServer(socketAddress.getHostName(), socketAddress.getPort());
		}
	}

	/**
	 * Gets the SCMP large request.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the sCMP large response
	 * @throws Exception
	 *             the exception
	 */
	private SCMPCompositeReceiver getSCMPLargeRequest(IRequest request, IResponse response) throws Exception {
		SCMPMessage scmpReq = request.getMessage();
		String sessionId = scmpReq.getSessionId();
		SCMPCompositeReceiver largeRequest = NettyResponderRequestHandlerAdapter.compositeRegistry.getSCMPLargeRequest(scmpReq
				.getSessionId());

		if (largeRequest == null) {
			// no compositeReceiver used before
			if (scmpReq.isPart() == false) {
				// request not chunk
				return largeRequest;
			}
			// first part of a large request received - create large response
			largeRequest = new SCMPCompositeReceiver(scmpReq, (SCMPMessage) scmpReq);
			// add largeResponse to the registry
			NettyResponderRequestHandlerAdapter.compositeRegistry.addSCMPLargeRequest(sessionId, largeRequest);
		} else {
			// next part of a large request received - add to large response
			largeRequest.add(scmpReq);
		}

		if (scmpReq.isPart()) {
			// received message part - request not complete yet
			largeRequest.incomplete();
			// set up poll request
			SCMPMessage scmpReply = new SCMPPart(true);
			scmpReply.setIsReply(true);
			scmpReply.setMessageType(scmpReq.getMessageType());
			response.setSCMP(scmpReply);
		} else {
			// last message of a chunk message received - request complete now
			largeRequest.complete();
			request.setMessage(largeRequest);
		}
		return largeRequest;
	}

	/** {@inheritDoc} */
	@Override
	public void responseCallback(IRequest request, IResponse response) {
		try {
			SCMPMessage scmpRequest = request.getMessage();
			String sessionId = scmpRequest.getSessionId();
			if (response.isLarge()) {
				// response is large, create a large response for reply
				SCMPCompositeSender largeResponse = new SCMPCompositeSender(response.getSCMP());
				SCMPMessage firstSCMP = largeResponse.getFirst();
				response.setSCMP(firstSCMP);
				// adding compositeReceiver to the composite registry
				NettyResponderRequestHandlerAdapter.compositeRegistry.addSCMPLargeResponse(sessionId, largeResponse);
			}
			response.write();
		} catch (Exception ex) {
			logger.error("send response", ex);
			this.faultResponseCallback(response, ex);
		}
	}

	/**
	 * Callback in case of an error.
	 * 
	 * @param response
	 *            the response
	 * @param ex
	 *            the error
	 */
	private void faultResponseCallback(IResponse response, Exception ex) {
		if (ex instanceof HasFaultResponseException) {
			((HasFaultResponseException) ex).setFaultResponse(response);
		} else {
			SCMPMessageFault scmpFault = new SCMPMessageFault(SCMPError.SERVER_ERROR, ex.getMessage());
			scmpFault.setMessageType(SCMPMsgType.UNDEFINED);
			scmpFault.setLocalDateTime();
			response.setSCMP(scmpFault);
		}
		try {
			response.write();
		} catch (Throwable th) {
			logger.error("send fault", th);
		}
	}

	/**
	 * Clean up dead server.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 */
	private void cleanUpDeadServer(String host, int port) {
		String wildKey = "_" + host + "/" + port;
		ServerRegistry serverRegistry = AppContext.getServerRegistry();
		Set<String> keySet = serverRegistry.keySet();

		for (String key : keySet) {
			if (key.endsWith(wildKey)) {
				Server server = serverRegistry.getServer(key);
				if ((server instanceof StatefulServer) == false) {
					continue;
				}
				logger.debug("clean up dead server with key " + wildKey);
				StatefulServer statefulServer = (StatefulServer) server;
				statefulServer.abortSessionsAndDestroy("clean up dead server");
			}
		}
	}

	/**
	 * Send unknown request error.
	 * 
	 * @param response
	 *            the response
	 * @throws Exception
	 *             the exception
	 */
	protected void sendUnknownRequestError(IResponse response) throws Exception {
		SCMPMessage message = new SCMPMessage();
		message.setMessageType(SCMPMsgType.UNDEFINED);
		this.sendBadRequestError(response, message);
	}

	/**
	 * Send bad request error.
	 * 
	 * @param response
	 *            the response
	 * @param scmpReq
	 *            the scmp req
	 * @throws Exception
	 *             the exception
	 */
	protected void sendBadRequestError(IResponse response, SCMPMessage scmpReq) throws Exception {
		SCMPMessageFault scmpFault = new SCMPMessageFault(SCMPError.BAD_REQUEST, "messagType=" + scmpReq.getMessageType());
		scmpFault.setMessageType(scmpReq.getMessageType());
		scmpFault.setLocalDateTime();
		response.setSCMP(scmpFault);
		response.write();
	}
}
