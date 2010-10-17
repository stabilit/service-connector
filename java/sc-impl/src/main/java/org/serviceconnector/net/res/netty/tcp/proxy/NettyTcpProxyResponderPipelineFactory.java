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

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;

public class NettyTcpProxyResponderPipelineFactory implements ChannelPipelineFactory {

	private final ClientSocketChannelFactory cf;
	private final String remoteHost;
	private final int remotePort;

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(NettyTcpProxyResponderPipelineFactory.class);

	public NettyTcpProxyResponderPipelineFactory(ClientSocketChannelFactory cf, String remoteHost, int remotePort) {
		this.cf = cf;
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
	}

	/** {@inheritDoc} */
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();
		// responsible for decoding requests - Netty
		// pipeline.addLast("decoder", new HttpRequestDecoder());
		// responsible for encoding responses - Netty
		// pipeline.addLast("encoder", new HttpResponseEncoder());
		// responsible for aggregate chunks - Netty
		// pipeline.addLast("aggregator", new
		// HttpChunkAggregator(Constants.MAX_HTTP_CONTENT_LENGTH));
		// responsible for handle requests - Stabilit
		pipeline.addLast("handler", new NettyTcpProxyResponderRequestHandler(cf, remoteHost, remotePort));
		return pipeline;
	}

}
