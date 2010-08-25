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
package com.stabilit.scm.common.net.req.netty.http;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.util.Timer;

import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.net.req.IConnectionContext;
import com.stabilit.scm.common.net.req.netty.NettyIdleHandler;
import com.stabilit.scm.common.net.req.netty.NettyIdleTimeoutHandler;

/**
 * A factory for creating NettyHttpRequesterPipeline objects.
 * 
 * @author JTraber
 */
public class NettyHttpRequesterPipelineFactory implements ChannelPipelineFactory {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(NettyHttpRequesterPipelineFactory.class);
	
	/** The timer to observe timeouts. */
	private Timer timer;
	private IConnectionContext context;

	/**
	 * Instantiates a new NettyHttpRequesterPipelineFactory.
	 */
	public NettyHttpRequesterPipelineFactory(IConnectionContext context, Timer timer) {
		this.timer = timer;
		this.context = context;
	}

	/** {@inheritDoc} */
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();
		// responsible for observing idle timeout - Netty
		pipeline.addLast("operationTimeout", new NettyIdleTimeoutHandler(this.timer, 0, 0, this.context
				.getOperationTimeoutMillis(), TimeUnit.MILLISECONDS));
		// responsible for observing idle timeout - Netty
		pipeline.addLast("idleTimeout", new NettyIdleHandler(this.context, this.timer, 0, 0, this.context
				.getIdleTimeout()));
		// responsible for decoding responses - Netty
		pipeline.addLast("decoder", new HttpResponseDecoder());
		// responsible for encoding requests - Netty
		pipeline.addLast("encoder", new HttpRequestEncoder());
		// responsible for aggregate chunks - Netty
		pipeline.addLast("aggregator", new HttpChunkAggregator(Constants.MAX_HTTP_CONTENT_LENGTH));
		// responsible for handle responses - Stabilit
		pipeline.addLast("requesterResponseHandler", new NettyHttpRequesterResponseHandler());
		return pipeline;
	}
}
