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
package org.serviceconnector.net.req.netty.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.connection.ConnectionContext;
import org.serviceconnector.net.req.netty.NettyIdleHandler;
import org.serviceconnector.net.res.netty.NettySCMPFrameDecoder;

/**
 * A factory for creating NettyTcpRequesterPipelineFactory objects.
 *
 * @author JTraber
 */
public class NettyTcpRequesterPipelineFactory extends ChannelInitializer<SocketChannel> {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyTcpRequesterPipelineFactory.class);

	/** The context. */
	private ConnectionContext context;

	/**
	 * Instantiates a new NettyTcpRequesterPipelineFactory.
	 *
	 * @param context the context
	 */
	public NettyTcpRequesterPipelineFactory(ConnectionContext context) {
		this.context = context;
	}

	/** {@inheritDoc} */
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// responsible for observing idle timeout - Netty
		ch.pipeline().addLast("idleTimeout", new NettyIdleHandler(this.context, 0, 0, this.context.getIdleTimeoutSeconds()));
		// logging handler
		ch.pipeline().addLast("LOGGER", new LoggingHandler());		
		// responsible for reading until SCMP frame is complete
		ch.pipeline().addLast("framer", new NettySCMPFrameDecoder());
		// responsible for handle response - Stabilit
		ch.pipeline().addLast(AppContext.getSCWorkerThreadPool(), "requesterResponseHandler", new NettyTcpRequesterResponseHandler());
	}
}
