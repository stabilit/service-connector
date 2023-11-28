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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.nio.NioEventLoopGroup;
import org.serviceconnector.conf.ListenerConfiguration;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.res.EndpointAdapter;
import org.serviceconnector.net.res.IResponder;

import net.sf.ehcache.config.InvalidConfigurationException;

/**
 * The Class NettyTcpProxyEndpoint.
 */
public class NettyTcpProxyEndpoint extends EndpointAdapter {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyTcpProxyEndpoint.class);
	/** The host. */
	private String remoteHost;
	/** The port. */
	private int remotePort;

	

	/**
	 * Instantiates a new NettyTcpProxyEndpoint.
	 */
	public NettyTcpProxyEndpoint() {
		super();
		this.remoteHost = null;
		this.remotePort = 0;
	}

	/** {@inheritDoc} */
	@Override
	public void create() {
		super.create();
		// Set up the event pipeline factory.
		this.bootstrap.childHandler(new NettyTcpProxyResponderPipelineFactory(remoteHost, remotePort));
	}

	/** {@inheritDoc} */
	@Override
	public void setResponder(IResponder resp) {
		super.setResponder(resp);
		ListenerConfiguration listenerConfig = resp.getListenerConfig();
		RemoteNodeConfiguration remoteNodeConfig = listenerConfig.getRemoteNodeConfiguration();
		if (remoteNodeConfig == null) {
			throw new InvalidConfigurationException("remote host configuration is missing for responder=" + resp.getListenerConfig().getName());
		}
		this.remoteHost = remoteNodeConfig.getHost();
		this.remotePort = remoteNodeConfig.getPort();
		try {
			// limit threads to maxIOThreads
			this.bossGroup = new NioEventLoopGroup(AppContext.getBasicConfiguration().getMaxIOThreads());
			this.workerGroup = new NioEventLoopGroup(AppContext.getBasicConfiguration().getMaxIOThreads());
		} catch (Exception e) {
			LOGGER.error("setResponder", e);
		}
	}
}
