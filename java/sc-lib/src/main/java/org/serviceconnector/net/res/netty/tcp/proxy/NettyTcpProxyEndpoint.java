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

import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.serviceconnector.Constants;
import org.serviceconnector.conf.CommunicatorConfig;
import org.serviceconnector.conf.SystemConfigurationException;
import org.serviceconnector.net.res.EndpointAdapter;
import org.serviceconnector.net.res.IResponder;

/**
 * The Class NettyTcpProxyEndpoint.
 */
public class NettyTcpProxyEndpoint extends EndpointAdapter implements Runnable {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(NettyTcpProxyEndpoint.class);
	/** The host. */
	private String remoteHost;
	/** The port. */
	private int remotePort;
	/** The max connection pool size. */
	private int maxConnectionPoolSize;

	private NioClientSocketChannelFactory clientChannelFactory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
			Executors.newCachedThreadPool());

	/**
	 * Instantiates a new NettyTcpProxyEndpoint.
	 */
	public NettyTcpProxyEndpoint() {
		super();
		this.remoteHost = null;
		this.remotePort = 0;
		this.maxConnectionPoolSize = Constants.DEFAULT_MAX_CONNECTION_POOL_SIZE;
		this.endpointChannelFactory = null;
	}

	/** {@inheritDoc} */
	@Override
	public void create() {
		super.create();
		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new NettyTcpProxyResponderPipelineFactory(clientChannelFactory, remoteHost, remotePort));
	}

	/** {@inheritDoc} */
	@Override
	// TODO TRN why is this necessary here or missing in the other end points?
	public void setResponder(IResponder resp) {
		super.setResponder(resp);
		CommunicatorConfig remoteHostConfig = null;
		try {
			CommunicatorConfig communicatorConfig = resp.getResponderConfig();
			remoteHostConfig = communicatorConfig.getRemoteHostConfiguration();
			if (remoteHostConfig == null) {
				throw new SystemConfigurationException("no remote host configuration");
			}
			String remoteHost = remoteHostConfig.getInterfaces().get(0);
			int remotePort = remoteHostConfig.getPort();
			this.remoteHost = remoteHost;
			this.remotePort = remotePort;
			this.maxConnectionPoolSize = communicatorConfig.getMaxPoolSize();
			if (this.maxConnectionPoolSize < 1) {
				this.maxConnectionPoolSize = Constants.DEFAULT_MAX_CONNECTION_POOL_SIZE;
			}
			// limit threads
			this.endpointChannelFactory = new NioServerSocketChannelFactory(
					Executors.newFixedThreadPool(this.maxConnectionPoolSize), Executors
							.newFixedThreadPool(this.maxConnectionPoolSize));
			// no thread limit required
			this.clientChannelFactory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors
					.newCachedThreadPool());
		} catch (Exception e) {
			logger.error("setResponder", e);
		}
	}
}
