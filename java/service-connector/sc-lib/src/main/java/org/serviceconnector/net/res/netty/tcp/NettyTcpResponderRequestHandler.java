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
package org.serviceconnector.net.res.netty.tcp;

import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;

import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.res.netty.NettyResponderRequestHandlerAdapter;
import org.serviceconnector.net.res.netty.NettyTcpRequest;
import org.serviceconnector.net.res.netty.NettyTcpResponse;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.scmp.SCMPVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * The Class NettyTcpResponderRequestHandler. This class is responsible for
 * handling Tcp requests. Is called from the Netty framework by catching events
 * (message received, exception caught). Functionality to handle large messages
 * is also inside.
 *
 * @author JTraber
 */
public class NettyTcpResponderRequestHandler extends NettyResponderRequestHandlerAdapter {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyTcpResponderRequestHandler.class);
	
	/** {@inheritDoc} */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Channel channel = ctx.channel();
		
		if(this.remoteSocketAddress == null) {
			this.localSocketAddress = (InetSocketAddress) channel.localAddress();
			this.remoteSocketAddress = (InetSocketAddress) channel.remoteAddress();			
			this.remoteHostName = this.remoteSocketAddress.getHostName();
			this.remoteHostPort = this.remoteSocketAddress.getPort();
		}
		
		NettyTcpResponse response = new NettyTcpResponse(channel);		
		byte[] buffer = (byte[]) msg;
		IRequest request = new NettyTcpRequest(buffer, this.localSocketAddress, this.remoteSocketAddress);
		// process request in super class
		super.messageReceived(request, response, channel);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable th) throws Exception {
		NettyTcpResponse response = new NettyTcpResponse(ctx.channel());
		if (th instanceof ClosedChannelException) {
			// never reply in case of channel closed exception
			return;
		}
		if (th instanceof java.io.IOException) {
			LOGGER.debug("regular disconnect"); // regular disconnect causes this expected exception
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
