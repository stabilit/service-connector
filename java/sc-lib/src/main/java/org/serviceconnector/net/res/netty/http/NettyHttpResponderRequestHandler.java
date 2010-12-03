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
import java.util.Set;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.IAsyncCommand;
import org.serviceconnector.cmd.ICommand;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.PerformanceLogger;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.ResponderRegistry;
import org.serviceconnector.net.res.SCMPSessionCompositeRegistry;
import org.serviceconnector.net.res.netty.NettyHttpRequest;
import org.serviceconnector.net.res.netty.NettyHttpResponse;
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

/**
 * The Class NettyHttpResponderRequestHandler. This class is responsible for handling Http requests. Is called from the Netty
 * framework by catching events (message received, exception caught). Functionality to handle large messages is also inside.
 * 
 * @author JTraber
 */
public class NettyHttpResponderRequestHandler extends SimpleChannelUpstreamHandler implements IResponderCallback {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(NettyHttpResponderRequestHandler.class);
	/** The Constant performanceLogger. */
	private final static PerformanceLogger performanceLogger = PerformanceLogger.getInstance();

	private static SCMPSessionCompositeRegistry compositeRegistry = AppContext.getSCMPSessionCompositeRegistry();

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

			SCMPMessageSequenceNr msgSequenceNr = NettyHttpResponderRequestHandler.compositeRegistry.getSCMPMsgSequenceNr(sessionId);

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
			ResponderRegistry responderRegistry = AppContext.getResponderRegistry();
			responderRegistry.setThreadLocal(channel.getParent().getId());

			request.read();
			ICommand command = AppContext.getCommandFactory().getCommand(request.getKey());
			// gets the command
			// ICommand command = CommandFactory.getCurrentCommandFactory().getCommand(request.getKey());
			if (command == null) {
				this.sendUnknownRequestError(response, scmpReq);
				return;
			}

			if (command.isPassThroughPartMsg() == false) {
				// large messages needs to be handled
				SCMPLargeRequest compositeSender = NettyHttpResponderRequestHandler.compositeRegistry.getSCMPLargeRequest(sessionId);

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
					NettyHttpResponderRequestHandler.compositeRegistry.removeSCMPLargeRequest(sessionId);
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
				NettyHttpResponderRequestHandler.compositeRegistry.removeSCMPLargeResponse(scmpReq.getSessionId());
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
				NettyHttpResponderRequestHandler.compositeRegistry.addSCMPLargeRequest(sessionId, compositeSender);
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
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		super.channelClosed(ctx, e);
		InetSocketAddress socketAddress = (InetSocketAddress) e.getChannel().getRemoteAddress();
		if (AppContext.isScEnvironment()) {
			this.cleanUpDeadServer(socketAddress.getHostName(), socketAddress.getPort());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		Throwable th = e.getCause();
		logger.warn(th.toString());		// TODO JOT eventually more output. (TCP protocol connected to HTTP port)
		NettyHttpResponse response = new NettyHttpResponse(e);
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
	public void responseCallback(IRequest request, IResponse response) {
		try {
			SCMPMessage scmpRequest = request.getMessage();
			String sessionId = scmpRequest.getSessionId();
			if (response.isLarge()) {
				SCMPMessageSequenceNr msgSequenceNr = NettyHttpResponderRequestHandler.compositeRegistry
						.getSCMPMsgSequenceNr(sessionId);
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
				NettyHttpResponderRequestHandler.compositeRegistry.addSCMPLargeRequest(sessionId, compositeSender);
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

	private void sendUnknownRequestError(IResponse response, SCMPMessage scmpReq) throws Exception {
		SCMPFault scmpFault = new SCMPFault(SCMPError.BAD_REQUEST, "messagType " + scmpReq.getMessageType());
		scmpFault.setMessageType(scmpReq.getMessageType());
		scmpFault.setLocalDateTime();
		response.setSCMP(scmpFault);
		response.write();
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
		SCMPLargeResponse largeResponse = NettyHttpResponderRequestHandler.compositeRegistry.getSCMPLargeResponse(scmpReq
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
			NettyHttpResponderRequestHandler.compositeRegistry.addSCMPLargeResponse(sessionId, largeResponse);
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
}
