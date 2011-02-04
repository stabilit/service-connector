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
package org.serviceconnector.net.res.netty.web;

import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.res.ResponderRegistry;
import org.serviceconnector.net.res.netty.NettyHttpResponse;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.web.IWebRequest;
import org.serviceconnector.web.IWebResponse;
import org.serviceconnector.web.cmd.IWebCommand;
import org.serviceconnector.web.ctx.WebContext;
import org.serviceconnector.web.netty.NettyWebRequest;
import org.serviceconnector.web.netty.NettyWebResponse;

public class NettyWebResponderRequestHandler extends SimpleChannelUpstreamHandler {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(NettyWebResponderRequestHandler.class);

	/** {@inheritDoc} */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception {
		// needs to set a key in thread local to identify thread later and get
		// access to the responder
		Channel channel = ctx.getChannel();
		ResponderRegistry responderRegistry = AppContext.getResponderRegistry();
		int port = ((InetSocketAddress) channel.getLocalAddress()).getPort();
		responderRegistry.setThreadLocal(port);
		HttpRequest httpRequest = (HttpRequest) event.getMessage();
		HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		IWebRequest webRequest = new NettyWebRequest(httpRequest);
		IWebResponse webResponse = new NettyWebResponse(httpResponse);
		IWebCommand webCommand = WebContext.getWebCommand(webRequest);
		webCommand.run(webRequest, webResponse);
		ChannelBuffer buffer = ChannelBuffers.copiedBuffer(webResponse.getBytes());
		httpResponse.setContent(buffer);
		httpResponse.setHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(buffer.readableBytes()));
		// encode any cookies
		CookieEncoder ce = ((NettyWebResponse) webResponse).getCookieEncoder();
		if (ce != null) {
			String encodedCookies = ce.encode();
			if (encodedCookies != null && encodedCookies.length() > 0) {
				// logger.debug(encodedCookies);
				httpResponse.addHeader("Set-Cookie", encodedCookies);
			}
		}
		// Write the response.
		event.getChannel().write(httpResponse);
	}

	/** {@inheritDoc} */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		Throwable th = e.getCause();
		logger.debug(th.toString());
		NettyHttpResponse response = new NettyHttpResponse(e);
		if (th instanceof ClosedChannelException) {
			// never reply in case of channel closed exception
			return;
		}
		SCMPMessageFault fault = new SCMPMessageFault(SCMPError.SC_ERROR, th.getMessage());
		fault.setMessageType(SCMPMsgType.UNDEFINED);
		response.setSCMP(fault);
		response.write();
	}
}
