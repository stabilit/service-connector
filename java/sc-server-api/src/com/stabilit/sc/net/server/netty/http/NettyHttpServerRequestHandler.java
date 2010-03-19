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
package com.stabilit.sc.net.server.netty.http;

import java.net.SocketAddress;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.cmd.ICommandValidator;
import com.stabilit.sc.cmd.ValidatorException;
import com.stabilit.sc.cmd.factory.CommandFactory;
import com.stabilit.sc.io.IFaultResponse;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.io.SCMPErrorCode;
import com.stabilit.sc.io.SCMPFault;
import com.stabilit.sc.net.netty.NettyHttpRequest;
import com.stabilit.sc.net.netty.NettyHttpResponse;

@ChannelPipelineCoverage("one")
public class NettyHttpServerRequestHandler extends SimpleChannelUpstreamHandler {

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception {
		HttpRequest httpRequest = (HttpRequest) event.getMessage();
		SocketAddress socketAddress = ctx.getChannel().getRemoteAddress();
		IRequest request = new NettyHttpRequest(httpRequest, socketAddress);
		NettyHttpResponse response = new NettyHttpResponse(event);
		ICommand command = CommandFactory.getCurrentCommandFactory().newCommand(request);
		if (command == null) {
			SCMP scmpReq = request.getSCMP();
			SCMPFault scmpFault = new SCMPFault(SCMPErrorCode.REQUEST_UNKNOWN);
			scmpFault.setMessageType(scmpReq.getMessageType());
			scmpFault.setLocalDateTime();
			response.setSCMP(scmpFault);
			writeResponse(response);
			return;
		}
		try {
			ICommandValidator commandValidator = command.getCommandValidator();
			if (commandValidator != null) {
				try {
					commandValidator.validate(request, response);
				} catch (ValidatorException e) {
					if (e instanceof IFaultResponse) {
						((IFaultResponse)e).setFaultResponse(response);
					}
					writeResponse(response);
					return;
				}
			}
			command.run(request, response);
		} catch (CommandException e) {
			e.printStackTrace();
		}
		writeResponse(response);
	}

	private void writeResponse(NettyHttpResponse response) throws Exception {
		MessageEvent event = response.getEvent();
		HttpRequest httpRequest = (HttpRequest) event.getMessage();

		// Decide whether to close the connection or not.
		boolean close = !httpRequest.isKeepAlive();

		// Build the response object.
		HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		ChannelBuffer buffer = response.getBuffer();

		httpResponse.setContent(buffer);

		if (!close) {
			// There's no need to add 'Content-Length' header
			// if this is the last response.
			httpResponse.setHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(buffer.readableBytes()));
		}
		// Write the response.
		ChannelFuture future = event.getChannel().write(httpResponse);

		// Close the connection after the write operation is done if necessary.
		if (close) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		e.getCause().printStackTrace();
		e.getChannel().close();
	}
}
