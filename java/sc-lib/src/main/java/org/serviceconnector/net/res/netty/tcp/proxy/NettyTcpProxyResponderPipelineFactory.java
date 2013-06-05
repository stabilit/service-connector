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
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.serviceconnector.ctx.AppContext;

/**
 * A factory for creating NettyTcpProxyResponderPipeline objects.
 */
public class NettyTcpProxyResponderPipelineFactory implements ChannelPipelineFactory {

	/** The cf. */
	private final ClientSocketChannelFactory cf;
	/** The remote host. */
	private final String remoteHost;
	/** The remote port. */
	private final int remotePort;

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(NettyTcpProxyResponderPipelineFactory.class);

	/**
	 * Instantiates a new netty tcp proxy responder pipeline factory.
	 * 
	 * @param cf
	 *            the cf
	 * @param remoteHost
	 *            the remote host
	 * @param remotePort
	 *            the remote port
	 */
	public NettyTcpProxyResponderPipelineFactory(ClientSocketChannelFactory cf, String remoteHost, int remotePort) {
		this.cf = cf;
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
	}

	/**
	 * Gets the pipeline.
	 * 
	 * @return the pipeline
	 * @throws Exception
	 *             the exception {@inheritDoc}
	 */
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();
		// logging handler
		pipeline.addLast("logger", new LoggingHandler());
		// executer to run NettyTcpProxyResponderRequestHandler in own thread
		pipeline.addLast("executor", new ExecutionHandler(AppContext.getOrderedSCWorkerThreadPool()));
		// responsible for handle requests - Stabilit
		pipeline.addLast("handler", new NettyTcpProxyResponderRequestHandler(cf, remoteHost, remotePort));
		return pipeline;
	}
}
