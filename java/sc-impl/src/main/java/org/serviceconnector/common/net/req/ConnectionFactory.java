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
package org.serviceconnector.common.net.req;

import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.serviceconnector.common.conf.Constants;
import org.serviceconnector.common.factory.Factory;
import org.serviceconnector.common.factory.IFactoryable;
import org.serviceconnector.common.net.req.IConnection;
import org.serviceconnector.common.net.req.netty.http.NettyHttpConnection;
import org.serviceconnector.common.net.req.netty.tcp.NettyTcpConnection;


/**
 * A factory for creating connection objects. Provides access to concrete client instances. Possible connection types
 * are shown as constants below.
 */
public class ConnectionFactory extends Factory {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ConnectionFactory.class);

	/** The Constant NETTY_TCP. */
	private static final String NETTY_TCP = "netty.tcp";
	/** The Constant NETTY_HTTP. */
	private static final String NETTY_HTTP = "netty.http";
	/** ConnectionFactory instance */
	private static final ConnectionFactory instance = new ConnectionFactory();

	/**
	 * NETTY stuff<br>
	 * Configures client with Thread Pool, Boss Threads and Worker Threads. A boss thread accepts incoming connections
	 * on a socket. A worker thread performs non-blocking read and write on a channel.
	 */
	private static NioClientSocketChannelFactory channelFactory;
	/** The Constant timer, responsible component to observe timeouts in a connection. */
	private static Timer timer;

	{
		ConnectionFactory.channelFactory = new NioClientSocketChannelFactory(Executors
				.newFixedThreadPool(Constants.DEFAULT_NR_OF_THREADS_CLIENT), Executors
				.newFixedThreadPool(Constants.DEFAULT_NR_OF_THREADS_CLIENT));
		ConnectionFactory.timer = new HashedWheelTimer();
	}

	/**
	 * Gets the current instance.
	 * 
	 * @return the current instance
	 */
	public static ConnectionFactory getCurrentInstance() {
		return ConnectionFactory.instance;
	}

	/**
	 * Instantiates a new ConnectionFactory.
	 */
	private ConnectionFactory() {
		// jboss netty http client
		IConnection nettyHttpConnection = new NettyHttpConnection(ConnectionFactory.channelFactory,
				ConnectionFactory.timer);
		add(NETTY_HTTP, nettyHttpConnection);
		// jboss netty tcp client
		IConnection nettyTCPConnection = new NettyTcpConnection(ConnectionFactory.channelFactory,
				ConnectionFactory.timer);
		add(NETTY_TCP, nettyTCPConnection);
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

	//TODO FJU this shutdown is never called
	//TODO FJU http://docs.jboss.org/netty/3.2/api/org/jboss/netty/channel/socket/nio/NioClientSocketChannelFactory.html
	/**
	 * Shutdown connection factory.<br>
	 * This method shuts down every resource needed by connections. Should only be used if whole application shuts down.
	 * Be very careful if you use this method - every connection in relation to this channelFactory must be closed
	 * otherwise you end up in indefinitely loop. In most cases closing the connections is good enough NETTY will
	 * release other resources.
	 */
	public static void shutdownConnectionFactory() {
		ConnectionFactory.timer.stop();
		ConnectionFactory.channelFactory.releaseExternalResources();
	}
}
