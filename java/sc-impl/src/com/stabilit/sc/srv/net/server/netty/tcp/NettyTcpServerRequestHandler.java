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
package com.stabilit.sc.srv.net.server.netty.tcp;

import java.net.SocketAddress;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.stabilit.sc.listener.ExceptionListenerSupport;
import com.stabilit.sc.net.netty.NettyTcpRequest;
import com.stabilit.sc.net.netty.NettyTcpResponse;
import com.stabilit.sc.scmp.IFaultResponse;
import com.stabilit.sc.scmp.IRequest;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.scmp.SCMPErrorCode;
import com.stabilit.sc.scmp.SCMPFault;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMessageID;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.scmp.internal.SCMPLargeResponse;
import com.stabilit.sc.srv.cmd.ICommand;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.net.server.netty.NettyCommandRequest;
import com.stabilit.sc.srv.registry.ServerRegistry;
import com.stabilit.sc.util.Lock;
import com.stabilit.sc.util.LockAdapter;
import com.stabilit.sc.util.Lockable;

/**
 * @author JTraber
 * 
 */
@ChannelPipelineCoverage("one")
public class NettyTcpServerRequestHandler extends SimpleChannelUpstreamHandler {

	private Logger log = Logger.getLogger(NettyTcpServerRequestHandler.class);
	private NettyCommandRequest commandRequest = null;
	private SCMPLargeResponse scmpResponseComposite = null;
	private final Lock<Object> lock = new Lock<Object>(); // faster than synchronized
	private SCMPMessageID msgID;

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

	public NettyTcpServerRequestHandler() {
		msgID = new SCMPMessageID();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception {
		NettyTcpResponse response = new NettyTcpResponse(event);
		SocketAddress socketAddress = ctx.getChannel().getLocalAddress();
		IRequest request = new NettyTcpRequest(event, socketAddress);
		SCMP scmpReq = request.getSCMP();

		if (this.scmpResponseComposite != null && scmpReq.isPart()) {
			if (this.scmpResponseComposite.hasNext()) {
				SCMP nextSCMP = this.scmpResponseComposite.getNext();
				response.setSCMP(nextSCMP);
				msgID.incrementPartSequenceNr();
				nextSCMP.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
				response.write();
				if (this.scmpResponseComposite.hasNext() == false) {
					this.scmpResponseComposite = null;
				}
				return;
			}
			this.scmpResponseComposite = null;
		}
		try {
			Channel channel = ctx.getChannel();
			ServerRegistry serverRegistry = ServerRegistry.getCurrentInstance();
			serverRegistry.setThreadLocal(channel.getParent().getId());

			lock.runLocked(commandRequestLock); // init commandRequest if not set
			ICommand command = this.commandRequest.readCommand(request, response);
			if (commandRequest.isComplete() == false) {
				SCMP scmp = response.getSCMP();
				msgID.incrementPartSequenceNr();
				scmp.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
				response.write();
				return;
			}
			if (command == null) {
				scmpReq = request.getSCMP();
				SCMPFault scmpFault = new SCMPFault(SCMPErrorCode.REQUEST_UNKNOWN);
				scmpFault.setMessageType(scmpReq.getMessageType());
				scmpFault.setLocalDateTime();
				response.setSCMP(scmpFault);
				scmpFault.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
				msgID.incrementMsgSequenceNr();
				response.write();
				return;
			}

			ICommandValidator commandValidator = command.getCommandValidator();
			try {
				commandValidator.validate(request);
				command.run(request, response);
			} catch (Exception  ex) {
				ExceptionListenerSupport.getInstance().fireException(this, ex);
				if (ex instanceof IFaultResponse) {
					((IFaultResponse) ex).setFaultResponse(response);
				}
			}
			// TODO error handling immer antworten?
		} catch (Throwable th) {
			ExceptionListenerSupport.getInstance().fireException(this, th);
			SCMPFault scmpFault = new SCMPFault(SCMPErrorCode.SERVER_ERROR);
			scmpFault.setMessageType(SCMPMsgType.UNDEFINED.getResponseName());
			scmpFault.setLocalDateTime();
			response.setSCMP(scmpFault);
		}
		// check if response is large, if so create a composite for this reply
		if (response.isLarge()) {
			this.scmpResponseComposite = new SCMPLargeResponse(response);
			SCMP firstSCMP = this.scmpResponseComposite.getFirst();
			response.setSCMP(firstSCMP);
			msgID.incrementPartSequenceNr();
			firstSCMP.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
		} else {
			SCMP scmp = response.getSCMP();
			if (scmp.isPart() || scmpReq.isPart()) {			
				msgID.incrementPartSequenceNr();
				scmp.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
			} else {
				scmp.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
				msgID.incrementMsgSequenceNr();
			}
		}
		response.write();
		commandRequest = null;
		if ("true".equals(response.getSCMP().getHeader("kill"))) {
			ctx.getChannel().disconnect();
			return;
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		log.error("Exception :" + e.getCause().getMessage());
	}
}
