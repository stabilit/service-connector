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

import org.serviceconnector.conf.ListenerConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.res.IResponder;
import org.serviceconnector.net.res.ResponderRegistry;
import org.serviceconnector.net.res.netty.NettyTcpResponse;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.scmp.SCMPVersion;
import org.serviceconnector.web.IWebRequest;
import org.serviceconnector.web.IWebResponse;
import org.serviceconnector.web.WebCredentials;
import org.serviceconnector.web.cmd.WebCommand;
import org.serviceconnector.web.ctx.WebContext;
import org.serviceconnector.web.netty.NettyWebRequest;
import org.serviceconnector.web.netty.NettyWebResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * The Class NettyWebResponderRequestHandler.
 */
public class NettyWebResponderRequestHandler extends ChannelInboundHandlerAdapter {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyWebResponderRequestHandler.class);

	/** {@inheritDoc} */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// needs to set a key in thread local to identify thread later and get access to
		// the responder
		Channel channel = ctx.channel();
		ResponderRegistry responderRegistry = AppContext.getResponderRegistry();
		InetSocketAddress localAddress = (InetSocketAddress) channel.localAddress();
		InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
		int port = ((InetSocketAddress) channel.localAddress()).getPort();
		responderRegistry.setThreadLocal(port);

		IResponder responder = AppContext.getResponderRegistry().getCurrentResponder();
		ListenerConfiguration respConfig = responder.getListenerConfig();
		String contextUserid = respConfig.getUsername();
		String contextPassword = respConfig.getPassword();
		WebContext.setSCWebCredentials(new WebCredentials(contextUserid, contextPassword));

		FullHttpRequest httpRequest = (FullHttpRequest) msg;
		FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		IWebRequest webRequest = new NettyWebRequest(httpRequest, localAddress, remoteAddress);
		IWebResponse webResponse = new NettyWebResponse(httpResponse);
		WebCommand webCommand = WebContext.getWebCommand();
		webCommand.run(webRequest, webResponse);
		ByteBuf buffer = Unpooled.copiedBuffer(webResponse.getBytes());

		FullHttpResponse finalHttpResponse = new DefaultFullHttpResponse(httpResponse.getProtocolVersion(),
				httpResponse.getStatus(), buffer);
		finalHttpResponse.headers().set(httpResponse.headers());
		finalHttpResponse.trailingHeaders().set(httpResponse.trailingHeaders());
		finalHttpResponse.headers().set(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(buffer.readableBytes()));
		// Write the response.
		channel.write(finalHttpResponse);
	}

	/** {@inheritDoc} */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable th) throws Exception {
		NettyTcpResponse response = new NettyTcpResponse(ctx.channel());
		if (th instanceof ClosedChannelException) {
			// never reply in case of channel closed exception
			return;
		}
		if (th instanceof java.io.IOException) {
			LOGGER.info("regular disconnect", th); // regular disconnect causes this expected exception
			return;
		} else {
			LOGGER.error("Responder error", th);
		}
		if (th instanceof HasFaultResponseException) {
			((HasFaultResponseException) th).setFaultResponse(response);
			response.write();
			return;
		}
		SCMPMessageFault fault = new SCMPMessageFault(SCMPVersion.LOWEST, SCMPError.SC_ERROR,
				th.getMessage() + " caught exception in web responder request handler");
		fault.setMessageType(SCMPMsgType.UNDEFINED);
		response.setSCMP(fault);
		response.write();
	}
}
