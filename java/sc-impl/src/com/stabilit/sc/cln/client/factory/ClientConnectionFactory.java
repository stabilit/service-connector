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
package com.stabilit.sc.cln.client.factory;

import com.stabilit.sc.cln.client.IClientConnection;
import com.stabilit.sc.cln.net.client.netty.http.NettyHttpClientConnection;
import com.stabilit.sc.cln.net.client.netty.tcp.NettyTcpClientConnection;
import com.stabilit.sc.cln.net.client.nio.http.NioHttpClientConnection;
import com.stabilit.sc.cln.net.client.nio.tcp.NioTcpClientConnection;
import com.stabilit.sc.factory.Factory;
import com.stabilit.sc.factory.IFactoryable;

/**
 * A factory for creating ClientConnection objects. Provides access to concrete client instances. 
 * Possible client connection types are shown as constants below.
 */
public class ClientConnectionFactory extends Factory {

	/** The Constant NIO_HTTP. */
	private static final String NIO_HTTP = "nio.http";
	/** The Constant NIO_TCP. */
	private static final String NIO_TCP = "nio.tcp";
	/** The Constant NETTY_TCP. */
	private static final String NETTY_TCP = "netty.tcp";
	/** The Constant NETTY_HTTP. */
	private static final String NETTY_HTTP = "netty.http";

	/**
	 * Instantiates a new client connection factory.
	 */
	public ClientConnectionFactory() {
		// jboss netty http client
		IClientConnection nettyHttpClient = new NettyHttpClientConnection();
		add(DEFAULT, nettyHttpClient);
		add(NETTY_HTTP, nettyHttpClient);
		// jboss netty tcp client
		IClientConnection nettyTCPClient = new NettyTcpClientConnection();
		add(NETTY_TCP, nettyTCPClient);
		// nio tcp client
		IClientConnection nioTCPClient = new NioTcpClientConnection();
		add(NIO_TCP, nioTCPClient);
		// nio http client
		IClientConnection nioHttpClient = new NioHttpClientConnection();
		add(NIO_HTTP, nioHttpClient);
	}

	/** {@inheritDoc} */
	public IClientConnection newInstance() {
		return newInstance(DEFAULT);
	}

	/**
	 * New instance.
	 * 
	 * @param key
	 *            the key designating the connection type 
	 * @return the i client connection
	 */
	public IClientConnection newInstance(String key) {
		IFactoryable factoryInstance = super.newInstance(key);
		return (IClientConnection) factoryInstance; // should be a clone if implemented
	}
}
