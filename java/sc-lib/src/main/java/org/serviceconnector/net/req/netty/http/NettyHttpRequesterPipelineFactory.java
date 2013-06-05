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

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.util.Timer;
import org.serviceconnector.Constants;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.connection.ConnectionContext;
import org.serviceconnector.net.req.netty.NettyIdleHandler;

/**
 * A factory for creating NettyHttpRequesterPipeline objects.
 * 
 * @author JTraber
 */
public class NettyHttpRequesterPipelineFactory implements ChannelPipelineFactory {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(NettyHttpRequesterPipelineFactory.class);

	/** The timer to observe timeouts. */
	private Timer timer;
	/** The context. */
	private ConnectionContext context;

	/**
	 * Instantiates a new NettyHttpRequesterPipelineFactory.
	 * 
	 * @param context
	 *            the context
	 * @param timer
	 *            the timer
	 */
	public NettyHttpRequesterPipelineFactory(ConnectionContext context, Timer timer) {
		this.timer = timer;
		this.context = context;
	}

	/** {@inheritDoc} */
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();
		// logging handler
		pipeline.addLast("LOGGER", new LoggingHandler());
		// responsible for observing idle timeout - Netty
		pipeline.addLast("idleTimeout", new NettyIdleHandler(this.context, this.timer, 0, 0, this.context.getIdleTimeoutSeconds()));
		// responsible for decoding responses - Netty
		pipeline.addLast("decoder", new HttpResponseDecoder());
		// responsible for encoding requests - Netty
		pipeline.addLast("encoder", new HttpRequestEncoder());
		// responsible for aggregate chunks - Netty
		pipeline.addLast("aggregator", new HttpChunkAggregator(Constants.MAX_HTTP_CONTENT_LENGTH));
		// executer to run NettyHttpRequesterResponseHandler in own thread
		pipeline.addLast("executor", new ExecutionHandler(AppContext.getSCWorkerThreadPool()));
		// responsible for handle responses - Stabilit
		pipeline.addLast("requesterResponseHandler", new NettyHttpRequesterResponseHandler());
		return pipeline;
	}
}
