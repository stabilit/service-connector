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
package org.serviceconnector.net.res.netty;

import java.net.InetSocketAddress;
import java.util.Set;

import org.serviceconnector.Constants;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.registry.ServerRegistry;
import org.serviceconnector.server.Server;
import org.serviceconnector.server.StatefulServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * The Class NettyResponderRequestHandlerAdapter.
 */
public abstract class NettyResponderRequestHandlerAdapter extends ChannelInboundHandlerAdapter {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyResponderRequestHandlerAdapter.class);

	/**
	 * Message received.
	 *
	 * @param request the request
	 * @param response the response
	 * @param channel the channel
	 */
	public void messageReceived(IRequest request, IResponse response, Channel channel) {
		// set up responderRequestHandlerTask to take care of the request
		NettyResponderRequestHandlerTask responseHandlerTask = new NettyResponderRequestHandlerTask(request, response);
		responseHandlerTask.process();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
		if (AppContext.isScEnvironment()) {
			// if in sc environment - clean up server
			this.cleanUpDeadServer(socketAddress.getHostName(), socketAddress.getPort());
		}
	}

	/**
	 * Clean up dead server.
	 *
	 * @param host the host
	 * @param port the port
	 */
	private void cleanUpDeadServer(String host, int port) {
		String wildKey = "_" + host + Constants.SLASH + port;
		ServerRegistry serverRegistry = AppContext.getServerRegistry();
		Set<String> keySet = serverRegistry.keySet();

		for (String key : keySet) {
			try {
				if (key.endsWith(wildKey)) {
					Server server = serverRegistry.getServer(key);
					if ((server instanceof StatefulServer) == false) {
						continue;
					}
					LOGGER.debug("clean up dead server with wild key " + wildKey);
					StatefulServer statefulServer = (StatefulServer) server;
					statefulServer.abortSessionsAndDestroy("clean up dead server");
				}
			} catch (Exception e) {
				LOGGER.error("cleaning up server=" + key + "throws exception", e);
				continue;
			}
		}
	}
}
