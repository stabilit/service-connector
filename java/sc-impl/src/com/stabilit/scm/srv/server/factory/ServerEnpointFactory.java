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
package com.stabilit.sc.srv.server.factory;

import com.stabilit.sc.factory.Factory;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.srv.net.server.netty.http.NettyHttpEndpoint;
import com.stabilit.sc.srv.net.server.netty.tcp.NettyTcpEnpoint;
import com.stabilit.sc.srv.net.server.nio.http.NioHttpEndpoint;
import com.stabilit.sc.srv.net.server.nio.tcp.NioTcpEndpoint;
import com.stabilit.sc.srv.server.IEndpoint;

/**
 * A factory for creating ServerConnection objects. Provides access to concrete server instances. Possible servers
 * are shown in key string constants below.
 */
public class ServerEnpointFactory extends Factory {

	/** The Constant NIO_HTTP. */
	private static final String NIO_HTTP = "nio.http";
	/** The Constant NIO_TCP. */
	private static final String NIO_TCP = "nio.tcp";
	/** The Constant NETTY_TCP. */
	private static final String NETTY_TCP = "netty.tcp";
	/** The Constant NETTY_HTTP. */
	private static final String NETTY_HTTP = "netty.http";

	/**
	 * Instantiates a new server connection factory.
	 */
	public ServerEnpointFactory() {
		// jboss netty http server
		IEndpoint nettyHttpServer = new NettyHttpEndpoint();
		add(DEFAULT, nettyHttpServer);
		add(NETTY_HTTP, nettyHttpServer);
		// jboss netty tcp server
		IEndpoint nettyTCPServer = new NettyTcpEnpoint();
		add(NETTY_TCP, nettyTCPServer);
		// nio tcp Server
		IEndpoint nioTcpServer = new NioTcpEndpoint();
		add(NIO_TCP, nioTcpServer);
		// nio http Server
		IEndpoint nioHttpServer = new NioHttpEndpoint();
		add(NIO_HTTP, nioHttpServer);
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
	 * @return the i server connection
	 */
	public IEndpoint newInstance(String key) {
		IFactoryable factoryInstance = super.newInstance(key);
		return (IEndpoint) factoryInstance; // should be a clone if implemented
	}
}
