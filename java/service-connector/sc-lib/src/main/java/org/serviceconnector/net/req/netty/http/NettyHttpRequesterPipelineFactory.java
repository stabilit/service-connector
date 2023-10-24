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
package org.serviceconnector.net.req.netty.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.logging.LoggingHandler;

import org.serviceconnector.Constants;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.connection.ConnectionContext;
import org.serviceconnector.net.req.netty.NettyIdleHandler;

/**
 * A factory for creating NettyHttpRequesterPipeline objects.
 *
 * @author JTraber
 */
public class NettyHttpRequesterPipelineFactory extends ChannelInitializer<SocketChannel> {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyHttpRequesterPipelineFactory.class);


	/** The context. */
	private ConnectionContext context;

	/**
	 * Instantiates a new NettyHttpRequesterPipelineFactory.
	 *
	 * @param context the context
	 */
	public NettyHttpRequesterPipelineFactory(ConnectionContext context) {
		this.context = context;
	}

	/** {@inheritDoc} */
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// responsible for observing idle timeout - Netty
		ch.pipeline().addLast("idleTimeout", new NettyIdleHandler(this.context, 0, 0, this.context.getIdleTimeoutSeconds()));
		// logging handler
		ch.pipeline().addLast("LOGGER", new LoggingHandler());		
		// responsible for decoding responses - Netty
		ch.pipeline().addLast("decoder", new HttpResponseDecoder());
		// responsible for aggregate chunks - Netty
		ch.pipeline().addLast("aggregator", new HttpObjectAggregator(Constants.MAX_HTTP_CONTENT_LENGTH));
		// responsible for encoding requests - Netty
		ch.pipeline().addLast("encoder", new HttpRequestEncoder());
		// responsible for handle responses - Stabilit
		ch.pipeline().addLast(AppContext.getSCWorkerThreadPool(), "requesterResponseHandler", new NettyHttpRequesterResponseHandler());
	}

	
}
