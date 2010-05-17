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
import com.stabilit.sc.srv.net.server.netty.http.NettyHttpServerConnection;
import com.stabilit.sc.srv.net.server.netty.tcp.NettyTcpServerConnection;
import com.stabilit.sc.srv.net.server.nio.http.NioHttpServer;
import com.stabilit.sc.srv.net.server.nio.tcp.NioTcpServer;
import com.stabilit.sc.srv.server.IServerConnection;

/**
 * A factory for creating ServerConnection objects. Provides access to concrete server instances. Possible servers
 * are shown in key string constants below.
 */
public class ServerConnectionFactory extends Factory {

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
	public ServerConnectionFactory() {
		// jboss netty http server
		IServerConnection nettyHttpServer = new NettyHttpServerConnection();
		add(DEFAULT, nettyHttpServer);
		add(NETTY_HTTP, nettyHttpServer);
		// jboss netty tcp server
		IServerConnection nettyTCPServer = new NettyTcpServerConnection();
		add(NETTY_TCP, nettyTCPServer);
		// nio tcp Server
		IServerConnection nioTcpServer = new NioTcpServer();
		add(NIO_TCP, nioTcpServer);
		// nio http Server
		IServerConnection nioHttpServer = new NioHttpServer();
		add(NIO_HTTP, nioHttpServer);
	}

	/** {@inheritDoc} */
	public IServerConnection newInstance() {
		return newInstance(DEFAULT);
	}

	/**
	 * New instance.
	 * 
	 * @param key
	 *            the key
	 * @return the i server connection
	 */
	public IServerConnection newInstance(String key) {
		IFactoryable factoryInstance = super.newInstance(key);
		return (IServerConnection) factoryInstance; // should be a clone if implemented
	}
}
