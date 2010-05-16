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
package com.stabilit.sc.srv.net.server.netty.http;

import java.net.SocketAddress;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.stabilit.sc.listener.ExceptionListenerSupport;
import com.stabilit.sc.listener.LoggerListenerSupport;
import com.stabilit.sc.listener.PerformanceListenerSupport;
import com.stabilit.sc.net.netty.NettyHttpRequest;
import com.stabilit.sc.net.netty.NettyHttpResponse;
import com.stabilit.sc.scmp.IFaultResponse;
import com.stabilit.sc.scmp.IRequest;
import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.scmp.SCMPErrorCode;
import com.stabilit.sc.scmp.SCMPFault;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMessageID;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.scmp.internal.SCMPCompositeSender;
import com.stabilit.sc.srv.cmd.ICommand;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.net.server.netty.NettyCommandRequest;
import com.stabilit.sc.srv.registry.ServerRegistry;
import com.stabilit.sc.util.Lock;
import com.stabilit.sc.util.LockAdapter;
import com.stabilit.sc.util.Lockable;

/**
 * The Class NettyHttpServerRequestHandler. This class is responsible for handling Http requests. Is called from
 * the Netty framework by catching events (message received, exception caught). Functionality to handle large
 * messages is also inside.
 * 
 * @author JTraber
 */
@ChannelPipelineCoverage("one")
public class NettyHttpServerRequestHandler extends SimpleChannelUpstreamHandler {

	/** The command request. */
	private NettyCommandRequest commandRequest = null;
	/** The large response sender. */
	private SCMPCompositeSender compositeSender = null;
	/** The msg id. */
	private SCMPMessageID msgID;
	/** The lock. */
	private final Lock<Object> lock = new Lock<Object>(); // faster than synchronized
	/** The command request lock. */
	private Lockable<Object> commandRequestLock = new LockAdapter<Object>() {

		@Override
		public Object run() throws Exception {
			// we are locked here
			if (commandRequest != null) {
				return commandRequest;
			}
			commandRequest = new NettyCommandRequest();
			return commandRequest;
		}
	};

	/**
	 * Instantiates a new netty http server request handler.
	 */
	public NettyHttpServerRequestHandler() {
		msgID = new SCMPMessageID();
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(org.jboss.netty.channel.
	 * ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception {
		NettyHttpResponse response = new NettyHttpResponse(event);
		HttpRequest httpRequest = (HttpRequest) event.getMessage();
		Channel channel = ctx.getChannel();
		SocketAddress socketAddress = channel.getRemoteAddress();
		IRequest request = new NettyHttpRequest(httpRequest, socketAddress);
		SCMPMessage scmpReq = request.getMessage();

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
		}

		try {
			// needs to set a key in thread local to identify thread later and get access to the server
			ServerRegistry serverRegistry = ServerRegistry.getCurrentInstance();
			serverRegistry.setThreadLocal(channel.getParent().getId());
			lock.runLocked(commandRequestLock); // init commandRequest if not set
			ICommand command = this.commandRequest.readCommand(request, response);
			if (commandRequest.isComplete() == false) {
				// request is not complete yet
				SCMPMessage message = response.getSCMP();
				msgID.incrementPartSequenceNr();
				message.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
				response.write();
				return;
			}
			scmpReq = request.getMessage();
			if (command == null) {
				if (LoggerListenerSupport.getInstance().isDebug()) {
					LoggerListenerSupport.getInstance().fireDebug(this, "Request unkown, " + request);
				}
				SCMPFault scmpFault = new SCMPFault(SCMPErrorCode.REQUEST_UNKNOWN);
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
				if (LoggerListenerSupport.getInstance().isDebug()) {
					LoggerListenerSupport.getInstance().fireDebug(this, "Run command [" + command.getKey() + "]");
				}
				if (PerformanceListenerSupport.getInstance().isOn()) {
					PerformanceListenerSupport.getInstance().fireBegin(this, System.currentTimeMillis());
					command.run(request, response);
					PerformanceListenerSupport.getInstance().fireEnd(this, System.currentTimeMillis());
				} else {
					command.run(request, response);
				}
			} catch (Throwable ex) {
				ExceptionListenerSupport.getInstance().fireException(this, ex);
				if (ex instanceof IFaultResponse) {
					((IFaultResponse) ex).setFaultResponse(response);
				} else {
					SCMPFault scmpFault = new SCMPFault(SCMPErrorCode.SERVER_ERROR);
					scmpFault.setMessageType(scmpReq.getMessageType());
					scmpFault.setLocalDateTime();
					response.setSCMP(scmpFault);
				}
			}
		} catch (Throwable th) {
			ExceptionListenerSupport.getInstance().fireException(this, th);
			SCMPFault scmpFault = new SCMPFault(SCMPErrorCode.SERVER_ERROR);
			scmpFault.setMessageType(SCMPMsgType.CONNECT.getResponseName());
			scmpFault.setLocalDateTime();
			response.setSCMP(scmpFault);
		}

		if (response.isLarge()) {
			// response is large, create a large response for reply
			this.compositeSender = new SCMPCompositeSender(response.getSCMP());
			SCMPMessage firstSCMP = this.compositeSender.getFirst();
			response.setSCMP(firstSCMP);
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
		commandRequest = null;
		// needed for testing
		if ("true".equals(response.getSCMP().getHeader("kill"))) {
			ctx.getChannel().disconnect();
			return;
		}
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.jboss.netty.channel.SimpleChannelUpstreamHandler#exceptionCaught(org.jboss.netty.channel.
	 * ChannelHandlerContext, org.jboss.netty.channel.ExceptionEvent)
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		ExceptionListenerSupport.getInstance().fireException(this, e.getCause());
	}
}
