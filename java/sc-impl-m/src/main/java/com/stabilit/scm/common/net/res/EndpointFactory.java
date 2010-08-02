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
package com.stabilit.scm.common.net.res;

import java.util.concurrent.Executors;

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.factory.Factory;
import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.net.res.netty.http.NettyHttpEndpoint;
import com.stabilit.scm.common.net.res.netty.tcp.NettyTcpEnpoint;
import com.stabilit.scm.common.res.IEndpoint;

/**
 * A factory for creating Endpoint objects. Provides access to concrete endpoint instances. Possible endpoints are
 * shown in key string constants below.
 */
public class EndpointFactory extends Factory {

	/** EndpointFactory instance */
	private static final EndpointFactory instance = new EndpointFactory();
	/** The Constant NETTY_TCP. */
	private static final String NETTY_TCP = "netty.tcp";
	/** The Constant NETTY_HTTP. */
	private static final String NETTY_HTTP = "netty.http";
	/** Netty stuff */
	/*
	 * Configures client with Thread Pool, Boss Threads and Worker Threads. A boss thread accepts incoming
	 * connections on a socket. A worker thread performs non-blocking read and write on a channel.
	 */
	private static NioServerSocketChannelFactory channelFactory;

	{
		EndpointFactory.channelFactory = new NioServerSocketChannelFactory(Executors
				.newFixedThreadPool(Constants.DEFAULT_NR_OF_THREADS_SERVER), Executors
				.newFixedThreadPool(Constants.DEFAULT_NR_OF_THREADS_SERVER));
	}

	/**
	 * Gets the current instance.
	 * 
	 * @return the current instance
	 */
	public static EndpointFactory getCurrentInstance() {
		return EndpointFactory.instance;
	}

	/**
	 * Instantiates a new EnpointFactory.
	 */
	private EndpointFactory() {
		// jboss netty http endpoint
		IEndpoint nettyHttpEndpoint = new NettyHttpEndpoint(EndpointFactory.channelFactory);
		add(DEFAULT, nettyHttpEndpoint);
		add(NETTY_HTTP, nettyHttpEndpoint);
		// jboss netty tcp endpoint
		IEndpoint nettyTCPEndpoint = new NettyTcpEnpoint(EndpointFactory.channelFactory);
		add(NETTY_TCP, nettyTCPEndpoint);
	}

	/** {@inheritDoc} */
	public IEndpoint newInstance() {
		return newInstance(DEFAULT);
	}

	/**
	 * New instance.
	 * 
	 * @param key
	 *            the key
	 * @return the endpoint
	 */
	public IEndpoint newInstance(String key) {
		IFactoryable factoryInstance = super.newInstance(key);
		return (IEndpoint) factoryInstance; // should be a clone if implemented
	}

	/**
	 * Shutdown connection factory. This method shuts down every resource needed by connections. Should only be
	 * used if whole application shuts down.
	 */
	public static void shutdownConnectionFactory() {
		EndpointFactory.channelFactory.releaseExternalResources();
	}
}
