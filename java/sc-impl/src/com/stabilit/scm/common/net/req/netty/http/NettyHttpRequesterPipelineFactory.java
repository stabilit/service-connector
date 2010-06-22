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

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.handler.timeout.WriteTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

import com.stabilit.scm.common.conf.IConstants;
import com.stabilit.scm.common.scmp.SCMPMessage;

/**
 * A factory for creating NettyHttpRequesterPipeline objects.
 * 
 * @author JTraber
 */
public class NettyHttpRequesterPipelineFactory implements ChannelPipelineFactory {

	/** The timer to observe timeouts. */
	private Timer timer;
	private int keepAliveInterval;

	/**
	 * Instantiates a new NettyHttpRequesterPipelineFactory.
	 */
	public NettyHttpRequesterPipelineFactory(int keepAliveInterval) {
		this.timer = new HashedWheelTimer();
		this.keepAliveInterval = keepAliveInterval;
	}

	/** {@inheritDoc} */
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();
		// responsible for decoding responses - Netty
		pipeline.addLast("decoder", new HttpResponseDecoder());
		// responsible for encoding requests - Netty
		pipeline.addLast("encoder", new HttpRequestEncoder());
		// responsible for aggregate chunks - Netty
		pipeline.addLast("aggregator", new HttpChunkAggregator(SCMPMessage.LARGE_MESSAGE_LIMIT + 4 << 10));
		// responsible for observing read timeout - Netty
		pipeline.addLast("readTimeout", new ReadTimeoutHandler(this.timer, IConstants.READ_TIMEOUT));
		// responsible for observing write timeout - Netty
		pipeline.addLast("writeTimeout", new WriteTimeoutHandler(this.timer, IConstants.WRITE_TIMEOUT));
		// responsible for handle responses - Stabilit
		// TODO verify Timer
		pipeline.addLast("idleHandler", new NettyIdleHandler(this.timer, 0, 0, this.keepAliveInterval));
		pipeline.addLast("handler", new NettyHttpRequesterResponseHandler());
		return pipeline;
	}
}
