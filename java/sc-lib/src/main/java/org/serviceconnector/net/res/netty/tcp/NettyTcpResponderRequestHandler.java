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

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.serviceconnector.net.res.netty.NettyResponderRequestHandlerAdapter;
import org.serviceconnector.net.res.netty.NettyTcpRequest;
import org.serviceconnector.net.res.netty.NettyTcpResponse;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPFault;
import org.serviceconnector.scmp.SCMPMsgType;

/**
 * The Class NettyTcpResponderRequestHandler. This class is responsible for handling Tcp requests. Is called from the Netty framework
 * by catching events (message received, exception caught). Functionality to handle large messages is also inside.
 * 
 * @author JTraber
 */
public class NettyTcpResponderRequestHandler extends NettyResponderRequestHandlerAdapter {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(NettyTcpResponderRequestHandler.class);

	/** {@inheritDoc} */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception {
		NettyTcpResponse response = new NettyTcpResponse(event);
		try {
			Channel channel = ctx.getChannel();
			InetSocketAddress localSocketAddress = (InetSocketAddress) channel.getLocalAddress();
			InetSocketAddress remoteSocketAddress = (InetSocketAddress) channel.getRemoteAddress();
			IRequest request = new NettyTcpRequest(event, localSocketAddress, remoteSocketAddress);
			// super class processes message
			super.messageReceived(request, response, channel);
		} catch (Exception e) {
			logger.error("messageReceived", e);
			SCMPFault scmpFault = new SCMPFault(SCMPError.SERVER_ERROR, e.getMessage());
			scmpFault.setMessageType(SCMPMsgType.UNDEFINED);
			scmpFault.setLocalDateTime();
			response.setSCMP(scmpFault);
			response.write();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		Throwable th = e.getCause();
		NettyTcpResponse response = new NettyTcpResponse(e);
		if (th instanceof ClosedChannelException) {
			// never reply in case of channel closed exception
			return;
		}
		if (th instanceof java.io.IOException) {
			logger.warn(th.toString()); // regular disconnect causes this expected exception
		} else {
			logger.error("Response error", th);
		}
		if (th instanceof HasFaultResponseException) {
			((HasFaultResponseException) th).setFaultResponse(response);
			response.write();
		}
		SCMPFault fault = new SCMPFault(SCMPError.SC_ERROR, th.getMessage());
		response.setSCMP(fault);
		response.write();
	}
}
