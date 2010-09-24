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
import org.serviceconnector.net.res.ResponderRegistry;
import org.serviceconnector.web.IWebRequest;
import org.serviceconnector.web.IWebResponse;
import org.serviceconnector.web.cmd.IWebCommand;
import org.serviceconnector.web.cmd.WebCommandFactory;
import org.serviceconnector.web.netty.NettyWebRequest;
import org.serviceconnector.web.netty.NettyWebResponse;

public class NettyWebResponderRequestHandler extends
		SimpleChannelUpstreamHandler {

	/** The Constant logger. */
	protected final static Logger logger = Logger
			.getLogger(NettyWebResponderRequestHandler.class);

	private int counter = 0;

	/** {@inheritDoc} */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent event)
			throws Exception {
		// needs to set a key in thread local to identify thread later and get
		// access to the responder
		Channel channel = ctx.getChannel();
		ResponderRegistry respRegistry = ResponderRegistry.getCurrentInstance();
		respRegistry.setThreadLocal(channel.getParent().getId());
		HttpRequest httpRequest = (HttpRequest) event.getMessage();
		HttpResponse httpResponse = new DefaultHttpResponse(
				HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		WebCommandFactory webCommandFactory = WebCommandFactory
				.getCurrentWebCommandFactory();
		IWebRequest webRequest = new NettyWebRequest(httpRequest);
		IWebResponse webResponse = new NettyWebResponse(httpResponse);
		IWebCommand webCommand = webCommandFactory.getWebCommand(webRequest);
		webCommand.run(webRequest, webResponse);
		ChannelBuffer buffer = ChannelBuffers.copiedBuffer(webResponse
				.getBytes());
		httpResponse.setContent(buffer);
		httpResponse.setHeader(HttpHeaders.Names.CONTENT_LENGTH,
				String.valueOf(buffer.readableBytes()));
		// encode any cookies
		CookieEncoder ce = ((NettyWebResponse) webResponse).getCookieEncoder();
		if (ce != null) {
			String encodedCookies = ce.encode();
			if (encodedCookies != null && encodedCookies.length() > 0) {
				logger.info(encodedCookies);
				httpResponse.addHeader("Set-Cookie", encodedCookies);
			}
		}
		// Write the response.
		event.getChannel().write(httpResponse);
	}

	/** {@inheritDoc} */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		logger.error(e.toString());
//		e.getChannel().write("Hello Error World!");
	}

}
