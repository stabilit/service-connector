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
package org.serviceconnector.net.connection;

import java.security.InvalidParameterException;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.netty.http.NettyHttpConnection;
import org.serviceconnector.net.req.netty.tcp.NettyTcpConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A factory for creating connection objects. Provides access to concrete client instances. Possible connection types are shown as constants below.
 */
public class ConnectionFactory {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionFactory.class);

	/**
	 * NETTY stuff<br />
	 * Configures client with Thread Pool, Boss Threads and Worker Threads. A boss thread accepts incoming connections on a socket. A worker thread performs non-blocking read and
	 * write on a channel.
	 */
	private static EventLoopGroup bossGroup;
	private static EventLoopGroup workerGroup;

	static {
		ConnectionFactory.init();
	}

	/**
	 * Creates a new Connection object.
	 *
	 * @param key the key
	 * @return the i connection
	 */
	public IConnection createConnection(String key) {
		if (ConnectionType.NETTY_HTTP.getValue().equalsIgnoreCase(key)) {
			return new NettyHttpConnection(ConnectionFactory.workerGroup);
		} else if (ConnectionType.NETTY_TCP.getValue().equalsIgnoreCase(key)) {
			return new NettyTcpConnection(ConnectionFactory.workerGroup);
		} else {
			LOGGER.error("key : " + key + " not found!");
			throw new InvalidParameterException("key : " + key + " not found!");
		}
	}

	/**
	 * Shutdown connection factory.<br />
	 * This method shuts down every resource needed by connections. Should only be used if whole application shuts down. Be very careful if you use this method - every connection
	 * in relation to this channelFactory must be closed otherwise you end up in indefinitely loop. In most cases closing the connections is good enough NETTY will release other
	 * resources. http://docs.jboss.org/netty/3.2/api/org/jboss/netty/channel/socket/nio/NioClientSocketChannelFactory.html
	 */
	public static void shutdownConnectionFactory() {
		if (ConnectionFactory.bossGroup != null) {
			ConnectionFactory.bossGroup.shutdownGracefully();
			ConnectionFactory.bossGroup = null;
		}
		if (ConnectionFactory.workerGroup != null) {
			ConnectionFactory.workerGroup.shutdownGracefully();
			ConnectionFactory.workerGroup = null;
		}
	}

	/**
	 * Initialize the connection factory.
	 */
	public static void init() {
		if (ConnectionFactory.bossGroup == null) {
			ConnectionFactory.bossGroup = new NioEventLoopGroup(AppContext.getBasicConfiguration().getMaxIOThreads());
		}
		if (ConnectionFactory.workerGroup == null) {
			ConnectionFactory.workerGroup = new NioEventLoopGroup(AppContext.getBasicConfiguration().getMaxIOThreads());
		}
	}
}
