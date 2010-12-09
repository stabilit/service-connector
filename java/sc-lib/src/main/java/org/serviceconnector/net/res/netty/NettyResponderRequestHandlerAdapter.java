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
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPFault;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPLargeRequest;
import org.serviceconnector.scmp.SCMPLargeResponse;
import org.serviceconnector.scmp.SCMPMessage;
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
			NettyResponderRequestHandlerAdapter.compositeRegistry.removeSCMPLargeRequest(sessionId);
			NettyResponderRequestHandlerAdapter.compositeRegistry.removeSCMPLargeResponse(sessionId);
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
			SCMPLargeRequest compositeSender = NettyResponderRequestHandlerAdapter.compositeRegistry.getSCMPLargeRequest(sessionId);

			if (compositeSender != null && scmpReq.isPart()) {
				// sending of a large response has already been started and incoming scmp is a pull request
				if (compositeSender.hasNext()) {
					// there are still parts to send to complete request
					SCMPMessage nextSCMP = compositeSender.getNext();
					response.setSCMP(nextSCMP);
					// handling msgSequenceNr
					if (SCMPMessageSequenceNr.necessaryToWrite(nextSCMP.getMessageType())) {
						msgSequenceNr.incrementMsgSequenceNr();
						nextSCMP.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.getCurrentNr());
					}
					response.write();
					return;
				}
				NettyResponderRequestHandlerAdapter.compositeRegistry.removeSCMPLargeRequest(sessionId);
			}
			// command needs buffered message - buffer message
			SCMPLargeResponse largeResponse = this.getSCMPLargeResponse(request, response);

			if (largeResponse != null && largeResponse.isComplete() == false) {
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
			NettyResponderRequestHandlerAdapter.compositeRegistry.removeSCMPLargeResponse(sessionId);
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
		if (response.isLarge()) {
			// response is large, create a large response for reply
			SCMPLargeRequest compositeSender = new SCMPLargeRequest(response.getSCMP());
			SCMPMessage firstSCMP = compositeSender.getFirst();
			response.setSCMP(firstSCMP);
			// handling msgSequenceNr
			if (SCMPMessageSequenceNr.necessaryToWrite(firstSCMP.getMessageType())) {
				msgSequenceNr.incrementMsgSequenceNr();
				firstSCMP.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.getCurrentNr());
			}
			// adding compositeReceiver to the composite registry
			NettyResponderRequestHandlerAdapter.compositeRegistry.addSCMPLargeRequest(sessionId, compositeSender);
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
	 * Gets the sCMP large response.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the sCMP large response
	 * @throws Exception
	 *             the exception
	 */
	private SCMPLargeResponse getSCMPLargeResponse(IRequest request, IResponse response) throws Exception {
		SCMPMessage scmpReq = request.getMessage();
		String sessionId = scmpReq.getSessionId();
		SCMPLargeResponse largeResponse = NettyResponderRequestHandlerAdapter.compositeRegistry.getSCMPLargeResponse(scmpReq
				.getSessionId());

		if (largeResponse == null) {
			// no compositeReceiver used before
			if (scmpReq.isPart() == false) {
				// request not chunk
				return largeResponse;
			}
			// first part of a large request received - create large response
			largeResponse = new SCMPLargeResponse(scmpReq, (SCMPMessage) scmpReq);
			// add largeResponse to the registry
			NettyResponderRequestHandlerAdapter.compositeRegistry.addSCMPLargeResponse(sessionId, largeResponse);
		} else {
			// next part of a large request received - add to large response
			largeResponse.add(scmpReq);
		}

		if (scmpReq.isPart()) {
			// received message part - request not complete yet
			largeResponse.incomplete();
			// set up poll request
			SCMPMessage scmpReply = new SCMPPart(true);
			scmpReply.setIsReply(true);
			scmpReply.setMessageType(scmpReq.getMessageType());
			response.setSCMP(scmpReply);
		} else {
			// last message of a chunk message received - request complete now
			largeResponse.complete();
			request.setMessage(largeResponse);
		}
		return largeResponse;
	}

	/** {@inheritDoc} */
	@Override
	public void responseCallback(IRequest request, IResponse response) {
		try {
			SCMPMessage scmpRequest = request.getMessage();
			String sessionId = scmpRequest.getSessionId();
			if (response.isLarge()) {
				// response is large, create a large response for reply
				SCMPLargeRequest compositeSender = new SCMPLargeRequest(response.getSCMP());
				SCMPMessage firstSCMP = compositeSender.getFirst();
				response.setSCMP(firstSCMP);
				// adding compositeReceiver to the composite registry
				NettyResponderRequestHandlerAdapter.compositeRegistry.addSCMPLargeRequest(sessionId, compositeSender);
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
			SCMPFault scmpFault = new SCMPFault(SCMPError.SERVER_ERROR, ex.getMessage());
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
		logger.debug("clean up dead server with key " + wildKey);
		ServerRegistry serverRegistry = AppContext.getServerRegistry();
		Set<String> keySet = serverRegistry.keySet();

		for (String key : keySet) {
			if (key.endsWith(wildKey)) {
				Server server = serverRegistry.getServer(key);
				if ((server instanceof StatefulServer) == false) {
					continue;
				}
				StatefulServer statefulServer = (StatefulServer) server;
				statefulServer.abortSessionsAndDestroy();
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
		SCMPFault scmpFault = new SCMPFault(SCMPError.BAD_REQUEST, "messagType " + scmpReq.getMessageType());
		scmpFault.setMessageType(scmpReq.getMessageType());
		scmpFault.setLocalDateTime();
		response.setSCMP(scmpFault);
		response.write();
	}
}
