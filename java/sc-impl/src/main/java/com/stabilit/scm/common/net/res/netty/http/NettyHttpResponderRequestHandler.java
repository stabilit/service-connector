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
package com.stabilit.scm.common.net.res.netty.http;

import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.stabilit.scm.common.cmd.IAsyncCommand;
import com.stabilit.scm.common.cmd.ICommand;
import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.IPassThroughPartMsg;
import com.stabilit.scm.common.cmd.factory.CommandFactory;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.listener.PerformancePoint;
import com.stabilit.scm.common.net.IResponderCallback;
import com.stabilit.scm.common.net.res.ResponderRegistry;
import com.stabilit.scm.common.net.res.SCMPSessionCompositeRegistry;
import com.stabilit.scm.common.net.res.netty.NettyHttpRequest;
import com.stabilit.scm.common.net.res.netty.NettyHttpResponse;
import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMessageId;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.scmp.internal.SCMPCompositeReceiver;
import com.stabilit.scm.common.scmp.internal.SCMPCompositeSender;
import com.stabilit.scm.common.scmp.internal.SCMPPart;

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
				if (LoggerPoint.getInstance().isDebug()) {
					LoggerPoint.getInstance().fireDebug(this, "Run command [" + command.getKey() + "]");
				}
				PerformancePoint.getInstance().fireBegin(command, "run");
				if (command.isAsynchronous()) {
					((IAsyncCommand) command).run(request, response, this);
					return;
				}
				command.run(request, response);
				PerformancePoint.getInstance().fireEnd(command, "run");
			} catch (HasFaultResponseException ex) {
				// exception carries response inside
				logger.error("messageReceived "+ex.getMessage(), ex);
				ExceptionPoint.getInstance().fireException(this, ex);
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
			ExceptionPoint.getInstance().fireException(this, th);
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
		logger.error("exceptionCaught "+e.getCause().getMessage(), e.getCause());
		ExceptionPoint.getInstance().fireException(this, e.getCause());
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
		logger.error("callback "+ex.getMessage(), ex);
		ExceptionPoint.getInstance().fireException(this, ex);
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
			logger.error("callback "+th.getMessage(), th);
			ExceptionPoint.getInstance().fireException(this, th);
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
}
