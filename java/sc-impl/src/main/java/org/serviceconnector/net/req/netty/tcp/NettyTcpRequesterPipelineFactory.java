/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.util.Timer;
import org.serviceconnector.net.connection.IConnectionContext;
import org.serviceconnector.net.req.netty.NettyIdleHandler;
import org.serviceconnector.net.res.SCMPBasedFrameDecoder;


/**
 * A factory for creating NettyTcpRequesterPipelineFactory objects.
 * 
 * @author JTraber
 */
public class NettyTcpRequesterPipelineFactory implements ChannelPipelineFactory {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(NettyTcpRequesterPipelineFactory.class);
	
	/** The timer to observe timeouts. */
	private Timer timer;
	private IConnectionContext context;

	/**
	 * Instantiates a new NettyTcpRequesterPipelineFactory.
	 */
	public NettyTcpRequesterPipelineFactory(IConnectionContext context, Timer timer) {
		this.timer = timer;
		this.context = context;
	}

	/** {@inheritDoc} */
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();
		// responsible for observing idle timeout - Netty
		pipeline.addLast("idleTimeout", new NettyIdleHandler(this.context, this.timer, 0, 0, this.context
				.getIdleTimeout()));
		// responsible for reading until SCMP frame is complete
		pipeline.addLast("framer", new SCMPBasedFrameDecoder());
		// responsible for handling response
		pipeline.addLast("requesterResponseHandler", new NettyTcpRequesterResponseHandler());
		return pipeline;
	}
}
