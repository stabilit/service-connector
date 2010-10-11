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
package org.serviceconnector.net.res.netty.tcp;

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
import org.serviceconnector.cmd.IAsyncCommand;
import org.serviceconnector.cmd.ICommand;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.PerformanceLogger;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.ResponderRegistry;
import org.serviceconnector.net.res.SCMPSessionCompositeRegistry;
import org.serviceconnector.net.res.netty.NettyTcpRequest;
import org.serviceconnector.net.res.netty.NettyTcpResponse;
import org.serviceconnector.registry.ServerRegistry;
import org.serviceconnector.registry.SessionRegistry;
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
import org.serviceconnector.service.AbstractSession;
import org.serviceconnector.service.Server;
import org.serviceconnector.service.Session;

/**
 * The Class NettyTcpResponderRequestHandler. This class is responsible for handling Tcp requests. Is called from the
 * Netty framework by catching events (message received, exception caught). Functionality to handle large messages is
 * also inside.
 * 
 * @author JTraber
 */
public class NettyTcpResponderRequestHandler extends SimpleChannelUpstreamHandler implements IResponderCallback {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(NettyTcpResponderRequestHandler.class);

	/** The Constant performanceLogger. */
	private final static PerformanceLogger performanceLogger = PerformanceLogger.getInstance();

	private final static SCMPSessionCompositeRegistry compositeRegistry = SCMPSessionCompositeRegistry
			.getCurrentInstance();

	/** {@inheritDoc} */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception {
		NettyTcpResponse response = new NettyTcpResponse(event);
		try {
			Channel channel = ctx.getChannel();
			InetSocketAddress localSocketAddress = (InetSocketAddress) channel.getLocalAddress();
			InetSocketAddress remoteSocketAddress = (InetSocketAddress) channel.getRemoteAddress();
			IRequest request = new NettyTcpRequest(event, localSocketAddress, remoteSocketAddress);
			SCMPMessage scmpReq = request.getMessage();
			String sessionId = scmpReq.getSessionId();
			SCMPMessageId messageId = NettyTcpResponderRequestHandler.compositeRegistry.getSCMPMessageId(sessionId);

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
			ResponderRegistry responderRegistry = AppContext.getCurrentContext().getResponderRegistry();
			responderRegistry.setThreadLocal(channel.getParent().getId());

			request.read();

			AppContext appContext = AppContext.getCurrentContext();
			ICommand command = appContext.getCommandFactory().getCommand(request.getKey());
			// gets the command
			// ICommand command = CommandFactory.getCurrentCommandFactory().getCommand(request.getKey());
			if (command == null) {
				this.sendUnknownRequestError(response, scmpReq);
				return;
			}

			if (command.isPassThroughPartMsg() == false) {
				// large messages needs to be handled
				SCMPCompositeSender compositeSender = NettyTcpResponderRequestHandler.compositeRegistry
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
					NettyTcpResponderRequestHandler.compositeRegistry.removeSCMPCompositeSender(sessionId);
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
				NettyTcpResponderRequestHandler.compositeRegistry.removeSCMPCompositeReceiver(sessionId);
			}

			// validate request and run command
			try {
				command.validate(request);
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
				logger.error("messageReceived", ex);
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
				NettyTcpResponderRequestHandler.compositeRegistry.addSCMPCompositeSender(sessionId, compositeSender);
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
		NettyTcpResponse response = new NettyTcpResponse(e);
		logger.info("exceptionCaught " + e.getCause().getMessage());
		InetSocketAddress socketAddress = (InetSocketAddress) e.getChannel().getRemoteAddress();
		this.cleanUpDeadServer(socketAddress.getHostName(), socketAddress.getPort());
		Throwable th = e.getCause();
		if (th instanceof ClosedChannelException) {
			// never reply in case of channel closed exception
			return;
		}
		if (th instanceof HasFaultResponseException) {
			((HasFaultResponseException) th).setFaultResponse(response);
			response.write();
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
				SCMPMessageId messageId = NettyTcpResponderRequestHandler.compositeRegistry.getSCMPMessageId(sessionId);
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
				NettyTcpResponderRequestHandler.compositeRegistry.addSCMPCompositeSender(sessionId, compositeSender);
			}
			response.write();
		} catch (Exception ex) {
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
		logger.error("callback " + ex.getMessage());
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
		} catch (Throwable thr) {
		}
	}

	private void sendUnknownRequestError(IResponse response, SCMPMessage scmpReq) throws Exception {
		SCMPFault scmpFault = new SCMPFault(SCMPError.BAD_REQUEST, "messagType " + scmpReq.getMessageType());
		scmpFault.setMessageType(scmpReq.getMessageType());
		scmpFault.setLocalDateTime();
		response.setSCMP(scmpFault);
		response.write();
	}

	private SCMPCompositeReceiver getCompositeReceiver(IRequest request, IResponse response) throws Exception {
		SCMPMessage scmpReq = request.getMessage();
		String sessionId = scmpReq.getSessionId();
		SCMPCompositeReceiver compositeReceiver = compositeRegistry.getSCMPCompositeReceiver(scmpReq.getSessionId());

		if (compositeReceiver == null) {
			// no compositeReceiver used before
			if (scmpReq.isPart() == false) {
				// request not chunk
				return compositeReceiver;
			}
			// first part of a large request received - introduce composite receiver
			compositeReceiver = new SCMPCompositeReceiver(scmpReq, (SCMPMessage) scmpReq);
			// add compositeReceiver to the registry
			NettyTcpResponderRequestHandler.compositeRegistry.addSCMPCompositeReceiver(sessionId, compositeReceiver);
			// need to increment message number here
			SCMPMessageId messageId = NettyTcpResponderRequestHandler.compositeRegistry.getSCMPMessageId(sessionId);
			messageId.incrementMsgSequenceNr();
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

		ServerRegistry serverRegistry = AppContext.getCurrentContext().getServerRegistry();
		SessionRegistry sessionRegistry = AppContext.getCurrentContext().getSessionRegistry();
		Set<String> keySet = serverRegistry.keySet();

		for (String key : keySet) {
			if (key.endsWith(wildKey)) {
				if (logger.isDebugEnabled()) {
					logger.debug("clean up server: " + wildKey);
				}
				Server server = serverRegistry.getServer(key);
				// deregister server from service
				server.getService().removeServer(server);
				List<AbstractSession> serverSessions = server.getSessions();

				// aborts session on server - carefully don't modify list in loop ConcurrentModificationException
				for (AbstractSession session : serverSessions) {
					sessionRegistry.removeSession((Session) session);
					NettyTcpResponderRequestHandler.compositeRegistry.removeSession(session.getId());
				}
				// release all resources used by server, disconnects requester
				server.destroy();
				serverRegistry.removeServer(key);
			}
		}
	}
}
