/*
 * Copyright 2009 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
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
import com.stabilit.sc.common.net.netty.NettyHttpRequest;
import com.stabilit.sc.common.net.netty.NettyHttpResponse;
import com.stabilit.sc.srv.cmd.ICommand;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.NettyCommandRequest;
import com.stabilit.sc.srv.registry.ServerRegistry;

@ChannelPipelineCoverage("one")
public class NettyHttpServerRequestHandler extends SimpleChannelUpstreamHandler {

	private static Logger log = Logger.getLogger(NettyHttpServerRequestHandler.class);
	private NettyCommandRequest commandRequest = null;

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception {
		NettyHttpResponse response = new NettyHttpResponse(event);
		HttpRequest httpRequest = (HttpRequest) event.getMessage();

		try {
			Channel channel = ctx.getChannel();
			SocketAddress socketAddress = channel.getRemoteAddress();
			ServerRegistry serverRegistry = ServerRegistry.getCurrentInstance();
			serverRegistry.setThreadLocal(channel.getParent().getId());
			IRequest request = new NettyHttpRequest(httpRequest, socketAddress);
			response = new NettyHttpResponse(event);
			// ICommand command = CommandFactory.getCurrentCommandFactory().newCommand(request);

			if (commandRequest == null) {
				commandRequest = new NettyCommandRequest(request, response);
			}
			ICommand command = commandRequest.readCommand(request, response);
			if (commandRequest.isComplete() == false) {
				response.write();
				return;
			}
			SCMP scmpReq = request.getSCMP();
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
		response.write();
		commandRequest = null;
	}

	//
	// private void writeResponse(NettyHttpResponse response) throws Exception {
	// MessageEvent event = response.getEvent();
	// HttpRequest httpRequest = (HttpRequest) event.getMessage();
	//
	// // Decide whether to close the connection or not.
	// boolean close = !httpRequest.isKeepAlive();
	// // TODO ?? keepAlive close?
	//
	// // Build the response object.
	// HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
	// ChannelBuffer buffer = response.getBuffer();
	//
	// httpResponse.setContent(buffer);
	//
	// if (!close) {
	// // There's no need to add 'Content-Length' header
	// // if this is the last response.
	// httpResponse.setHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(buffer.readableBytes()));
	// }
	// // Write the response.
	// ChannelFuture future = event.getChannel().write(httpResponse);
	//
	// // Close the connection after the write operation is done if necessary.
	// if (close) {
	// future.addListener(ChannelFutureListener.CLOSE);
	// }
	// }

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		e.getCause().printStackTrace();
		e.getChannel().close();
	}
}
