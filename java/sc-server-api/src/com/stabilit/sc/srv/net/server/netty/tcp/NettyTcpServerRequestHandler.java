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

import com.stabilit.sc.common.io.IFaultResponse;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPFault;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.io.SCMPResponseComposite;
import com.stabilit.sc.common.net.netty.NettyTcpRequest;
import com.stabilit.sc.common.net.netty.NettyTcpResponse;
import com.stabilit.sc.common.util.Lock;
import com.stabilit.sc.common.util.Lockable;
import com.stabilit.sc.srv.cmd.ICommand;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.net.server.netty.NettyCommandRequest;
import com.stabilit.sc.srv.registry.ServerRegistry;

/**
 * @author JTraber
 * 
 */
@ChannelPipelineCoverage("one")
public class NettyTcpServerRequestHandler extends SimpleChannelUpstreamHandler {

	private Logger log = Logger.getLogger(NettyTcpServerRequestHandler.class);
	private NettyCommandRequest commandRequest = null;
	private SCMPResponseComposite scmpResponseComposite = null;
	private final Lock<Object> lock = new Lock<Object>(); // faster than synchronized

	private Lockable<Object> commandRequestLock = new Lockable<Object>() {

		@Override
		public Object run(Object... params) throws Exception {
			// we are locked here
			if (commandRequest != null) {
				return commandRequest;
			}
			commandRequest = new NettyCommandRequest();
			return commandRequest;
		}
	};

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
				response.write();
				return;
			}
			if (command == null) {
				scmpReq = request.getSCMP();
				SCMPFault scmpFault = new SCMPFault(SCMPErrorCode.REQUEST_UNKNOWN);
				scmpFault.setMessageType(scmpReq.getMessageType());
				scmpFault.setLocalDateTime();
				response.setSCMP(scmpFault);
				response.write();
				return;
			}

			ICommandValidator commandValidator = command.getCommandValidator();
			try {
				commandValidator.validate(request, response);
				command.run(request, response);
			} catch (Throwable ex) {
				if (ex instanceof IFaultResponse) {
					((IFaultResponse) ex).setFaultResponse(response);
				}
			}
			// TODO error handling immer antworten?
		} catch (Throwable th) {
			SCMPFault scmpFault = new SCMPFault(SCMPErrorCode.SERVER_ERROR);
			scmpFault.setMessageType(SCMPMsgType.UNDEFINED.getResponseName());
			scmpFault.setLocalDateTime();
			response.setSCMP(scmpFault);
		}
		// check if response is large, if so create a composite for this reply
		if (response.isLarge()) {
			this.scmpResponseComposite = new SCMPResponseComposite(response);
			SCMP firstSCMP = this.scmpResponseComposite.getFirst();
			response.setSCMP(firstSCMP);
		}
		response.write();
		commandRequest = null;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		log.error("Exception :" + e.getCause().getMessage());
	}
}
