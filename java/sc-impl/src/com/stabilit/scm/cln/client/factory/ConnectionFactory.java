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
package com.stabilit.scm.cln.client.factory;

import com.stabilit.scm.cln.client.IConnection;
import com.stabilit.scm.cln.net.client.netty.http.NettyHttpConnection;
import com.stabilit.scm.cln.net.client.netty.tcp.NettyTcpConnection;
import com.stabilit.scm.cln.net.client.nio.http.NioHttpConnection;
import com.stabilit.scm.cln.net.client.nio.tcp.NioTcpConnection;
import com.stabilit.scm.factory.Factory;
import com.stabilit.scm.factory.IFactoryable;

/**
 * A factory for creating connection objects. Provides access to concrete client instances. 
 * Possible connection types are shown as constants below.
 */
public class ConnectionFactory extends Factory {

	/** The Constant NIO_HTTP. */
	private static final String NIO_HTTP = "nio.http";
	/** The Constant NIO_TCP. */
	private static final String NIO_TCP = "nio.tcp";
	/** The Constant NETTY_TCP. */
	private static final String NETTY_TCP = "netty.tcp";
	/** The Constant NETTY_HTTP. */
	private static final String NETTY_HTTP = "netty.http";

	/**
	 * Instantiates a new ConnectionFactory.
	 */
	public ConnectionFactory() {
		// jboss netty http client
		IConnection nettyHttpClient = new NettyHttpConnection();
		add(DEFAULT, nettyHttpClient);
		add(NETTY_HTTP, nettyHttpClient);
		// jboss netty tcp client
		IConnection nettyTCPClient = new NettyTcpConnection();
		add(NETTY_TCP, nettyTCPClient);
		// nio tcp client
		IConnection nioTCPClient = new NioTcpConnection();
		add(NIO_TCP, nioTCPClient);
		// nio http client
		IConnection nioHttpClient = new NioHttpConnection();
		add(NIO_HTTP, nioHttpClient);
	}

	/** {@inheritDoc} */
	public IConnection newInstance() {
		return newInstance(DEFAULT);
	}

	/**
	 * New instance.
	 * 
	 * @param key
	 *            the key designating the connection type 
	 * @return the i client connection
	 */
	public IConnection newInstance(String key) {
		IFactoryable factoryInstance = super.newInstance(key);
		return (IConnection) factoryInstance; // should be a clone if implemented
	}
}
