/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 *-----------------------------------------------------------------------------*/
package org.serviceconnector.net.res.netty.http;

import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.serviceconnector.cmd.CommandFactory;
import org.serviceconnector.cmd.IAsyncCommand;
import org.serviceconnector.cmd.ICommand;
import org.serviceconnector.cmd.ICommandValidator;
import org.serviceconnector.cmd.IPassThroughPartMsg;
import org.serviceconnector.log.PerformanceLogger;
import org.serviceconnector.net.IResponderCallback;
import org.serviceconnector.net.res.ResponderRegistry;
import org.serviceconnector.net.res.SCMPSessionCompositeRegistry;
import org.serviceconnector.net.res.netty.NettyHttpRequest;
import org.serviceconnector.net.res.netty.NettyHttpResponse;
import org.serviceconnector.sc.registry.ServerRegistry;
import org.serviceconnector.sc.registry.SessionRegistry;
import org.serviceconnector.sc.service.Server;
import org.serviceconnector.sc.service.Session;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPCompositeReceiver;
import org.serviceconnector.scmp.SCMPCompositeSender;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPFault;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageId;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.scmp.SCMPPart;


/**
 * The Class NettyHttpResponderRequestHandler. This class is responsible for handling Http requests. Is called from the
 * Netty framework by catching events (message received, exception caught). Functionality to handle large messages is
 * also inside.
 * 
 * @author JTraber
 */
public class NettyHttpResponderRequestHandler extends SimpleChannelUpstreamHandler implements IResponderCallback {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(NettyHttpResponderRequestHandler.class);

	/** The Constant performanceLogger. */
	private final static PerformanceLogger performanceLogger = PerformanceLogger.getInstance();

	private final static SCMPSessionCompositeRegistry compositeRegistry = SCMPSessionCompositeRegistry
			.getCurrentInstance();

	/** {@inheritDoc} */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception {

		NettyHttpResponse response = new NettyHttpResponse(event);

		try {
			HttpRequest httpRequest = (HttpRequest) event.getMessage();
			Channel channel = ctx.getChannel();
			InetSocketAddress localSocketAddress = (InetSocketAddress) channel.getLocalAddress();
			InetSocketAddress remoteSocketAddress = (InetSocketAddress) channel.getRemoteAddress();
			IRequest request = new NettyHttpRequest(httpRequest, localSocketAddress, remoteSocketAddress);
			SCMPMessage scmpReq = request.getMessage();
			String sessionId = scmpReq.getSessionId();
			SCMPMessageId messageId = NettyHttpResponderRequestHandler.compositeRegistry.getSCMPMessageId(sessionId);

			if (scmpReq == null) {
				// no scmp protocol used - nothing to return
				return;
			}
			if (scmpReq.isKeepAlive()) {
				scmpReq.setIsReply(true);
				response.setSCMP(scmpReq);
				response.write();
				return;
			}

			// needs to set a key in thread local to identify thread later and get access to the responder
			ResponderRegistry respRegistry = ResponderRegistry.getCurrentInstance();
			respRegistry.setThreadLocal(channel.getParent().getId());

			request.read();

			// gets the command
			ICommand command = CommandFactory.getCurrentCommandFactory().getCommand(request);
			if (command == null) {
				this.sendUnknownRequestError(response, scmpReq);
				return;
			}

			if ((command instanceof IPassThroughPartMsg) == false) {
				// large messages needs to be handled
				SCMPCompositeSender compositeSender = NettyHttpResponderRequestHandler.compositeRegistry
						.getSCMPCompositeSender(sessionId);

				if (compositeSender != null && scmpReq.isPart()) {
					// sending of a large response has already been started and incoming scmp is a pull request
					if (compositeSender.hasNext()) {
						// there are still parts to send to complete request
						SCMPMessage nextSCMP = compositeSender.getNext();
						response.setSCMP(nextSCMP);
						// handling messageId
						if (SCMPMessageId.necessaryToWrite(nextSCMP.getMessageType())) {
							if (compositeSender.hasNext()) {
								// there are more parts to send - just increment part number
								messageId.incrementPartSequenceNr();
							} else {
								// last part to send - will be a RES message increment message number
								messageId.incrementMsgSequenceNr();
							}
							nextSCMP.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, messageId.getCurrentMessageID());
						}
						response.write();
						return;
					}
					NettyHttpResponderRequestHandler.compositeRegistry.removeSCMPCompositeSender(sessionId);
				}
				// command needs buffered message - buffer message
				SCMPCompositeReceiver compositeReceiver = this.getCompositeReceiver(request, response);

				if (compositeReceiver != null && compositeReceiver.isComplete() == false) {
					// request is not complete yet
					SCMPMessage message = response.getSCMP();
					// handling messageId
					if (SCMPMessageId.necessaryToWrite(message.getMessageType())) {
						messageId.incrementPartSequenceNr();
						message.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, messageId.getCurrentMessageID());
					}
					response.write();
					return;
				}
				// removes compositeReceiver - request is complete don't need to know preceding messages any more
				NettyHttpResponderRequestHandler.compositeRegistry.removeSCMPCompositeReceiver(scmpReq.getSessionId());
			}
			// validate request and run command
			ICommandValidator commandValidator = command.getCommandValidator();
			try {
				commandValidator.validate(request);
				performanceLogger.begin(this.getClass().getSimpleName(), "run");
				if (command.isAsynchronous()) {
					if (logger.isDebugEnabled()) {
						logger.debug("run command async " + command.getKey());
					}
					((IAsyncCommand) command).run(request, response, this);
					return;
				}
				if (logger.isDebugEnabled()) {
					logger.debug("run command sync " + command.getKey());
				}
				command.run(request, response);
				performanceLogger.end(this.getClass().getSimpleName(), "run");
			} catch (HasFaultResponseException ex) {
				// exception carries response inside
				logger.info("messageReceived "+ex.getMessage());
				ex.setFaultResponse(response);
			}
			if (response.isLarge()) {
				// response is large, create a large response for reply
				SCMPCompositeSender compositeSender = new SCMPCompositeSender(response.getSCMP());
				SCMPMessage firstSCMP = compositeSender.getFirst();
				response.setSCMP(firstSCMP);
				// handling messageId
				if (SCMPMessageId.necessaryToWrite(firstSCMP.getMessageType())) {
					// override messageId now - because parts need to be sent
					messageId.incrementPartSequenceNr();
					firstSCMP.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, messageId.getCurrentMessageID());
				}
				// adding compositeReceiver to the composite registry
				NettyHttpResponderRequestHandler.compositeRegistry.addSCMPCompositeSender(sessionId, compositeSender);
			}
		} catch (Throwable th) {
			logger.error("messageReceived", th);
			SCMPFault scmpFault = new SCMPFault(SCMPError.SERVER_ERROR, th.getMessage());
			scmpFault.setMessageType(SCMPMsgType.UNDEFINED);
			scmpFault.setLocalDateTime();
			response.setSCMP(scmpFault);
		}
		response.write();
	}

	/** {@inheritDoc} */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		NettyHttpResponse response = new NettyHttpResponse(e);
		logger.error("exceptionCaught", e.getCause());
		InetSocketAddress socketAddress = (InetSocketAddress) e.getChannel().getRemoteAddress();
		this.cleanUpDeadServer(socketAddress.getHostName(), socketAddress.getPort());
		Throwable th = e.getCause();
		if (th instanceof ClosedChannelException) {
			// never reply in case of channel closed exception
			return;
		}
		if (th instanceof HasFaultResponseException) {
			((HasFaultResponseException) e).setFaultResponse(response);
			response.write();
			return;
		}
		SCMPFault fault = new SCMPFault(SCMPError.SC_ERROR, th.getMessage());
		response.setSCMP(fault);
		response.write();
	}

	/** {@inheritDoc} */
	@Override
	public void callback(IRequest request, IResponse response) {
		try {
			SCMPMessage scmpRequest = request.getMessage();
			String sessionId = scmpRequest.getSessionId();
			if (response.isLarge()) {
				SCMPMessageId messageId = NettyHttpResponderRequestHandler.compositeRegistry
						.getSCMPMessageId(sessionId);
				// response is large, create a large response for reply
				SCMPCompositeSender compositeSender = new SCMPCompositeSender(response.getSCMP());
				SCMPMessage firstSCMP = compositeSender.getFirst();
				response.setSCMP(firstSCMP);
				// handling messageId
				if (SCMPMessageId.necessaryToWrite(firstSCMP.getMessageType())) {
					// override messageId now - because parts need to be sent
					messageId.incrementPartSequenceNr();
					firstSCMP.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, messageId.getCurrentMessageID());
				}
				// adding compositeReceiver to the composite registry
				NettyHttpResponderRequestHandler.compositeRegistry.addSCMPCompositeSender(sessionId, compositeSender);
			}
			response.write();
		} catch (Exception ex) {
			logger.error("callback", ex);
			this.callback(response, ex);
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
	public void callback(IResponse response, Exception ex) {
		logger.error("callback", ex);
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
			logger.error("callback", th);
		}
	}

	/**
	 * Send unknown request error.
	 * 
	 * @param response
	 *            the response
	 * @param scmpReq
	 *            the scmp req
	 * @throws Exception
	 *             the exception
	 */
	private void sendUnknownRequestError(IResponse response, SCMPMessage scmpReq) throws Exception {
		SCMPFault scmpFault = new SCMPFault(SCMPError.BAD_REQUEST, "messagType " + scmpReq.getMessageType());
		scmpFault.setMessageType(scmpReq.getMessageType());
		scmpFault.setLocalDateTime();
		response.setSCMP(scmpFault);
		response.write();
	}

	/**
	 * Gets the composite receiver.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the composite receiver
	 * @throws Exception
	 *             the exception
	 */
	private SCMPCompositeReceiver getCompositeReceiver(IRequest request, IResponse response) throws Exception {
		SCMPMessage scmpReq = request.getMessage();
		String sessionId = scmpReq.getSessionId();
		SCMPCompositeReceiver compositeReceiver = NettyHttpResponderRequestHandler.compositeRegistry
				.getSCMPCompositeReceiver(scmpReq.getSessionId());

		if (compositeReceiver == null) {
			// no compositeReceiver used before
			if (scmpReq.isPart() == false) {
				// request not chunk
				return compositeReceiver;
			}
			// first part of a large request received - introduce composite receiver
			compositeReceiver = new SCMPCompositeReceiver(scmpReq, (SCMPMessage) scmpReq);
			// add compositeReceiver to the registry
			NettyHttpResponderRequestHandler.compositeRegistry.addSCMPCompositeReceiver(sessionId, compositeReceiver);
		} else {
			// next part of a large request received - add to composite receiver
			compositeReceiver.add(scmpReq);
		}

		if (scmpReq.isPart()) {
			// received message part - request not complete yet
			compositeReceiver.uncomplete();
			// set up pull request
			SCMPMessage scmpReply = new SCMPPart();
			scmpReply.setIsReply(true);
			scmpReply.setMessageType(scmpReq.getMessageType());
			response.setSCMP(scmpReply);
		} else {
			// last message of a chunk message received - request complete now
			compositeReceiver.complete();
			request.setMessage(compositeReceiver);
		}
		return compositeReceiver;
	}

	private void cleanUpDeadServer(String host, int port) {
		String wildKey = "_" + host + "/" + port;
		// TODO JOT
		ServerRegistry serverRegistry = ServerRegistry.getCurrentInstance();
		Set<String> keySet = serverRegistry.keySet();

		for (String key : keySet) {
			if (key.endsWith(wildKey)) {
				Server server = serverRegistry.getServer(key);
				// deregister server from service
				server.getService().removeServer(server);
				List<Session> serverSessions = server.getSessions();

				// aborts session on server - carefully don't modify list in loop ConcurrentModificationException
				for (Session session : serverSessions) {
					SessionRegistry.getCurrentInstance().removeSession(session);
				}
				// release all resources used by server, disconnects requester
				server.destroy();
				serverRegistry.removeServer(key);
			}
		}
	}
}
