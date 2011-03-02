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

import net.sf.ehcache.config.InvalidConfigurationException;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.serviceconnector.Constants;
import org.serviceconnector.conf.ListenerConfiguration;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.net.res.EndpointAdapter;
import org.serviceconnector.net.res.IResponder;

/**
 * The Class NettyTcpProxyEndpoint.
 */
public class NettyTcpProxyEndpoint extends EndpointAdapter implements Runnable {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(NettyTcpProxyEndpoint.class);
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
	public void setResponder(IResponder resp) {
		super.setResponder(resp);
		ListenerConfiguration listenerConfig = resp.getListenerConfig();
		RemoteNodeConfiguration remoteNodeConfig = listenerConfig.getRemoteNodeConfiguration();
		if (remoteNodeConfig == null) {
			throw new InvalidConfigurationException("remote host configuration is missing for responder="+resp.getListenerConfig().getName());
		}
		this.remoteHost = remoteNodeConfig.getHost();
		this.remotePort = remoteNodeConfig.getPort();
		this.maxConnectionPoolSize = remoteNodeConfig.getMaxPoolSize();
		try {
			// limit threads
			this.endpointChannelFactory = new NioServerSocketChannelFactory(Executors
					.newFixedThreadPool(this.maxConnectionPoolSize), Executors.newFixedThreadPool(this.maxConnectionPoolSize));
			// no thread limit required
			this.clientChannelFactory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors
					.newCachedThreadPool());
		} catch (Exception e) {
			LOGGER.error("setResponder", e);
		}
	}
}
