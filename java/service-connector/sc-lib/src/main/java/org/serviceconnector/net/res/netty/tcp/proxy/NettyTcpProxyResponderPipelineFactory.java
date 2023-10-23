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
package org.serviceconnector.net.res.netty.tcp.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.serviceconnector.ctx.AppContext;

/**
 * A factory for creating NettyTcpProxyResponderPipeline objects.
 */
public class NettyTcpProxyResponderPipelineFactory extends ChannelInitializer<SocketChannel> {

	/** The remote host. */
	private final String remoteHost;
	/** The remote port. */
	private final int remotePort;

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyTcpProxyResponderPipelineFactory.class);

	/**
	 * Instantiates a new netty tcp proxy responder pipeline factory.
	 *
	 * @param remoteHost the remote host
	 * @param remotePort the remote port
	 */
	public NettyTcpProxyResponderPipelineFactory(String remoteHost, int remotePort) {
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
	}

	/**
	 * Gets the pipeline.
	 *
	 * @throws Exception the exception {@inheritDoc}
	 */
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// logging handler
		ch.pipeline().addLast("logger", new LoggingHandler());
		// responsible for handle requests - Stabilit
		ch.pipeline().addLast(AppContext.getOrderedSCWorkerThreadPool(), "handler", new NettyTcpProxyResponderRequestHandler(remoteHost, remotePort));
	}
}
