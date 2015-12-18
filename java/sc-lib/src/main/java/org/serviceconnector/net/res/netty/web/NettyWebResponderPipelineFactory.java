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
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.serviceconnector.Constants;
import org.serviceconnector.ctx.AppContext;

/**
 * A factory for creating NettyWebResponderPipeline objects.
 */
public class NettyWebResponderPipelineFactory implements ChannelPipelineFactory {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(NettyWebResponderPipelineFactory.class);

	/** {@inheritDoc} */
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();
		// logging handler
		pipeline.addLast("logger", new LoggingHandler());
		// responsible for decoding requests - Netty
		pipeline.addLast("decoder", new HttpRequestDecoder());
		// responsible for encoding responses - Netty
		pipeline.addLast("encoder", new HttpResponseEncoder());
		// responsible for aggregate chunks - Netty
		pipeline.addLast("aggregator", new HttpChunkAggregator(Constants.MAX_HTTP_CONTENT_LENGTH));
		// executer to run NettyWebResponderRequestHandler in own thread
		pipeline.addLast("executor", new ExecutionHandler(AppContext.getSCWorkerThreadPool()));
		// responsible for handle requests - Stabilit
		pipeline.addLast("handler", new NettyWebResponderRequestHandler());
		return pipeline;
	}
}
