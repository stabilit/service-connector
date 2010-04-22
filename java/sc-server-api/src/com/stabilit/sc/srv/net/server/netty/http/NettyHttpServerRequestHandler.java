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

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.stabilit.sc.common.io.IFaultResponse;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPFault;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.io.SCMPResponseComposite;
import com.stabilit.sc.common.net.netty.NettyHttpRequest;
import com.stabilit.sc.common.net.netty.NettyHttpResponse;
import com.stabilit.sc.common.util.Lock;
import com.stabilit.sc.common.util.Lockable;
import com.stabilit.sc.srv.cmd.ICommand;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.net.server.netty.NettyCommandRequest;
import com.stabilit.sc.srv.registry.ServerRegistry;

@ChannelPipelineCoverage("one")
public class NettyHttpServerRequestHandler extends SimpleChannelUpstreamHandler {

	private static Logger log = Logger.getLogger(NettyHttpServerRequestHandler.class);
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

	public NettyHttpServerRequestHandler() {
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception {
		NettyHttpResponse response = new NettyHttpResponse(event);
		HttpRequest httpRequest = (HttpRequest) event.getMessage();
		Channel channel = ctx.getChannel();
		SocketAddress socketAddress = channel.getRemoteAddress();
		IRequest request = new NettyHttpRequest(httpRequest, socketAddress);
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
			ServerRegistry serverRegistry = ServerRegistry.getCurrentInstance();
			serverRegistry.setThreadLocal(channel.getParent().getId());

			lock.runLocked(commandRequestLock); // init commandRequest if not set
			ICommand command = this.commandRequest.readCommand(request, response);
			if (commandRequest.isComplete() == false) {
				response.write();
				return;
			}
			scmpReq = request.getSCMP();
			if (command == null) {
				log.debug("Request unkown, " + request);
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
				} else {
					SCMPFault scmpFault = new SCMPFault(SCMPErrorCode.SERVER_ERROR);
					scmpFault.setMessageType(scmpReq.getMessageType());
					scmpFault.setLocalDateTime();
					response.setSCMP(scmpFault);
				}
			}
		} catch (Throwable th) {
			SCMPFault scmpFault = new SCMPFault(SCMPErrorCode.SERVER_ERROR);
			scmpFault.setMessageType(SCMPMsgType.CONNECT.getResponseName());
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
		e.getCause().printStackTrace();
		e.getChannel().close();
	}
}
