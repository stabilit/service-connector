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
package org.serviceconnector.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.log4j.Logger;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.net.req.Requester;

/**
 * The Class Server. Represents a server instance on a backend Server. Serves a service. Has control over the max of sessions and
 * holds a connection pool to communicate to backend server.
 * 
 * @author JTraber
 */
public abstract class Server implements IServer {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(Server.class);

	protected RemoteNodeConfiguration remoteNodeConfiguration;

	/** The socket address. */
	protected InetSocketAddress socketAddress;
	/** The requester. */
	protected Requester requester;
	/** The server key. */
	protected String serverKey;

	/** The operation timeout mulitplier. */
	protected final double operationTimeoutMultiplier = AppContext.getBasicConfiguration().getOperationTimeoutMultiplier();

	public Server(RemoteNodeConfiguration remoteNodeConfiguration, InetSocketAddress socketAddress) {
		this.requester = new Requester(remoteNodeConfiguration);
		this.remoteNodeConfiguration = remoteNodeConfiguration;
		this.serverKey = "_" + socketAddress.getHostName() + "/" + socketAddress.getPort();
		this.socketAddress = socketAddress;
	}

	/**
	 * Gets the socket address.
	 * 
	 * @return the socket address
	 */
	public SocketAddress getSocketAddress() {
		return socketAddress;
	}

	/**
	 * Immediate connect.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void immediateConnect() throws Exception {
		this.requester.immediateConnect();
	}

	/**
	 * Destroy server.
	 */
	public void destroy() {
		LOGGER.info("server destroy " + this.serverKey);
		this.requester.destroy();
		AppContext.getServerRegistry().removeServer(this.getServerKey());
		this.requester = null;
	}

	/**
	 * Gets the host.
	 * 
	 * @return the host
	 */
	public String getHost() {
		return this.remoteNodeConfiguration.getHost();
	}

	/**
	 * Gets the port number.
	 * 
	 * @return the port number
	 */
	public int getPortNr() {
		return this.remoteNodeConfiguration.getPort();
	}

	/**
	 * Gets the max connections.
	 * 
	 * @return the max connections
	 */
	public int getMaxConnections() {
		return this.remoteNodeConfiguration.getMaxPoolSize();
	}

	/** {@inheritDoc} */
	@Override
	public ServerType getType() {
		return this.remoteNodeConfiguration.getServerType();
	}

	/**
	 * Gets the server key.
	 * 
	 * @return the server key
	 */
	public String getServerKey() {
		return serverKey;
	}

	/**
	 * Gets the requester.
	 * 
	 * @return the requester
	 */
	public IRequester getRequester() {
		return requester;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.getServerKey() + ":" + this.remoteNodeConfiguration.getPort();
	}
}
