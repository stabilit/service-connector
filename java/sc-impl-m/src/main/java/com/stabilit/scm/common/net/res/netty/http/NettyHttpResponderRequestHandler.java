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

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.stabilit.scm.common.cmd.IAsyncCommand;
import com.stabilit.scm.common.cmd.ICommand;
import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.listener.PerformancePoint;
import com.stabilit.scm.common.net.IResponderCallback;
import com.stabilit.scm.common.net.res.ResponderRegistry;
import com.stabilit.scm.common.net.res.netty.NettyCommandRequest;
import com.stabilit.scm.common.net.res.netty.NettyHttpRequest;
import com.stabilit.scm.common.net.res.netty.NettyHttpResponse;
import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMessageID;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.scmp.internal.SCMPCompositeSender;

/**
 * The Class NettyHttpResponderRequestHandler. This class is responsible for handling Http requests. Is called from the
 * Netty framework by catching events (message received, exception caught). Functionality to handle large messages is
 * also inside.
 * 
 * @author JTraber
 */
@ChannelPipelineCoverage("one")
public class NettyHttpResponderRequestHandler extends SimpleChannelUpstreamHandler implements IResponderCallback {

	/** The command request. */
	private NettyCommandRequest commandRequest = null;
	/** The large response sender. */
	private SCMPCompositeSender compositeSender = null;
	/** The msg id. */
	private SCMPMessageID msgID;

	/**
	 * Instantiates a new NettyHttpResponderRequestHandler.
	 */
	public NettyHttpResponderRequestHandler() {
		msgID = new SCMPMessageID();
	}

	/** {@inheritDoc} */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception {
		NettyHttpResponse response = new NettyHttpResponse(event);
		HttpRequest httpRequest = (HttpRequest) event.getMessage();
		Channel channel = ctx.getChannel();
		InetSocketAddress localSocketAddress = (InetSocketAddress) channel.getLocalAddress();
		InetSocketAddress remoteSocketAddress = (InetSocketAddress) channel.getRemoteAddress();
		IRequest request = new NettyHttpRequest(httpRequest, localSocketAddress, remoteSocketAddress);
		SCMPMessage scmpReq = request.getMessage();

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
		if (this.compositeSender != null && scmpReq.isPart()) {
			// sending of a large response has already been started and incoming scmp is a pull request
			if (this.compositeSender.hasNext()) {
				// there are still parts to send to complete request
				SCMPMessage nextSCMP = this.compositeSender.getNext();
				response.setSCMP(nextSCMP);
				msgID.incrementPartSequenceNr();
				nextSCMP.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
				response.write();
				if (this.compositeSender.hasNext() == false) {
					this.compositeSender = null;
				}
				return;
			}
			this.compositeSender = null;
			msgID.incrementMsgSequenceNr();
		}

		try {
			// needs to set a key in thread local to identify thread later and get access to the responder
			ResponderRegistry respRegistry = ResponderRegistry.getCurrentInstance();
			respRegistry.setThreadLocal(channel.getParent().getId());

			// init commandRequest if not set
			if (commandRequest == null) {
				this.commandRequest = new NettyCommandRequest();
			}

			ICommand command = this.commandRequest.readCommand(request, response);
			if (commandRequest.isComplete() == false) {
				// request is not complete yet
				SCMPMessage message = response.getSCMP();
				msgID.incrementPartSequenceNr();
				message.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
				response.write();
				return;
			}
			// sets the command request null - request is complete don't need to know about preceding messages any more
			commandRequest = null;
			scmpReq = request.getMessage();
			if (command == null) {
				if (LoggerPoint.getInstance().isDebug()) {
					LoggerPoint.getInstance().fireDebug(this, "Request unkown, " + request);
				}
				SCMPFault scmpFault = new SCMPFault(SCMPError.REQUEST_UNKNOWN);
				scmpFault.setMessageType(scmpReq.getMessageType());
				scmpFault.setLocalDateTime();
				response.setSCMP(scmpFault);
				scmpFault.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
				msgID.incrementMsgSequenceNr();
				response.write();
				return;
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
				ExceptionPoint.getInstance().fireException(this, ex);
				ex.setFaultResponse(response);
			}
		} catch (Throwable th) {
			ExceptionPoint.getInstance().fireException(this, th);
			SCMPFault scmpFault = new SCMPFault(SCMPError.SERVER_ERROR);
			scmpFault.setMessageType(SCMPMsgType.UNDEFINED.getValue());
			scmpFault.setLocalDateTime();
			response.setSCMP(scmpFault);
		}

		if (response.isLarge()) {
			// response is large, create a large response for reply
			this.compositeSender = new SCMPCompositeSender(response.getSCMP());
			SCMPMessage firstSCMP = this.compositeSender.getFirst();
			response.setSCMP(firstSCMP);
			msgID.incrementMsgSequenceNr();
			msgID.incrementPartSequenceNr();
			firstSCMP.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
		} else {
			SCMPMessage message = response.getSCMP();
			if (message.isPart() || scmpReq.isPart()) {
				msgID.incrementPartSequenceNr();
				message.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
			} else {
				message.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
				msgID.incrementMsgSequenceNr();
			}
		}
		response.write();
	}

	/** {@inheritDoc} */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		NettyHttpResponse response = new NettyHttpResponse(e);
		ExceptionPoint.getInstance().fireException(this, e.getCause());
		if (e instanceof HasFaultResponseException) {
			((HasFaultResponseException) e).setFaultResponse(response);
			response.write();
		}
	}

	@Override
	public void callback(IRequest request, IResponse response) {
		try {
			SCMPMessage scmpRequest = request.getMessage();
			if (response.isLarge()) {
				// response is large, create a large response for reply
				SCMPCompositeSender compositeSender = new SCMPCompositeSender(response.getSCMP());
				SCMPMessage firstSCMP = compositeSender.getFirst();
				response.setSCMP(firstSCMP);
				msgID.incrementMsgSequenceNr();
				msgID.incrementPartSequenceNr();
				firstSCMP.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
			} else {
				SCMPMessage message = response.getSCMP();
				if (message.isPart() || scmpRequest.isPart()) {
					msgID.incrementPartSequenceNr();
					message.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
				} else {
					message.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
					msgID.incrementMsgSequenceNr();
				}
			}
			response.write();
		} catch (Throwable th) {
			this.callback(response, th);
		}
	}

	@Override
	public void callback(IResponse response, Throwable th) {
		ExceptionPoint.getInstance().fireException(this, th);
		if (th instanceof HasFaultResponseException) {
			((HasFaultResponseException) th).setFaultResponse(response);
		} else {
			SCMPFault scmpFault = new SCMPFault(SCMPError.SERVER_ERROR);
			scmpFault.setMessageType(SCMPMsgType.UNDEFINED.getValue());
			scmpFault.setLocalDateTime();
			response.setSCMP(scmpFault);
		}
		try {
			response.write();
		} catch (Throwable thr) {
		}
	}
}
