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
package org.serviceconnector.net.res.netty.http;

import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.res.netty.NettyHttpRequest;
import org.serviceconnector.net.res.netty.NettyHttpResponse;
import org.serviceconnector.net.res.netty.NettyResponderRequestHandlerAdapter;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.scmp.SCMPVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class NettyHttpResponderRequestHandler. This class is responsible for handling Http requests. Is called from the Netty framework by catching events (message received,
 * exception caught). Functionality to handle large messages is also inside.
 *
 * @author JTraber
 */
public class NettyHttpResponderRequestHandler extends NettyResponderRequestHandlerAdapter {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyHttpResponderRequestHandler.class);

	/** {@inheritDoc} */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Channel channel = ctx.channel();
		NettyHttpResponse response = new NettyHttpResponse(channel);
		FullHttpRequest httpRequest = (FullHttpRequest) msg;
		InetSocketAddress localSocketAddress = (InetSocketAddress) channel.localAddress();
		InetSocketAddress remoteSocketAddress = (InetSocketAddress) channel.remoteAddress();
		IRequest request = new NettyHttpRequest(httpRequest, localSocketAddress, remoteSocketAddress);
		// process request in super class
		super.channelRead(request, response, channel);
	}

	/** {@inheritDoc} */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable th) throws Exception {
		NettyHttpResponse response = new NettyHttpResponse(ctx.channel());
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
		SCMPMessageFault fault = new SCMPMessageFault(SCMPVersion.LOWEST, SCMPError.SC_ERROR, th.getMessage());
		fault.setMessageType(SCMPMsgType.UNDEFINED);
		response.setSCMP(fault);
		response.write();
	}
}
