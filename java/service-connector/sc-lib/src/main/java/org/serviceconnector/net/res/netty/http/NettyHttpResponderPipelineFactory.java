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

import org.serviceconnector.Constants;
import org.serviceconnector.ctx.AppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LoggingHandler;

/**
 * A factory for creating NettyHttpServerPipeline objects.
 *
 * @author JTraber
 */
public class NettyHttpResponderPipelineFactory extends ChannelInitializer<SocketChannel> {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyHttpResponderPipelineFactory.class);

	/** {@inheritDoc} */
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {		
		// logging handler
		ch.pipeline().addLast("logger", new LoggingHandler());
		// responsible for decoding requests - Netty
		ch.pipeline().addLast("decoder", new HttpRequestDecoder());
		// responsible for encoding responses - Netty
		ch.pipeline().addLast("encoder", new HttpResponseEncoder());
		// responsible for aggregate chunks - Netty
		ch.pipeline().addLast("aggregator", new HttpObjectAggregator(Constants.MAX_HTTP_CONTENT_LENGTH));
		// responsible for handle requests - Stabilit
		ch.pipeline().addLast(AppContext.getSCWorkerThreadPool(), "handler", new NettyHttpResponderRequestHandler());
	}
}
