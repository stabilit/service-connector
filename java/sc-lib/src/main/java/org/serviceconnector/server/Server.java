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
import java.util.concurrent.ScheduledFuture;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.net.req.Requester;
import org.serviceconnector.util.TimeoutWrapper;

/**
 * The Class Server. Represents a server instance on a backend Server. Serves a service. Has control over the max of sessions and
 * holds a connection pool to communicate to a backend server. Servers activity is observed by a timer. It gets initialized by
 * adding server to server registry.
 * 
 * @author JTraber
 */
public abstract class Server implements IServer {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(Server.class);

	/** The remote node configuration. */
	protected RemoteNodeConfiguration remoteNodeConfiguration;
	/** The socket address. */
	protected InetSocketAddress socketAddress;
	/** The requester. */
	protected Requester requester;
	/** The server key. */
	protected String serverKey;
	/** The destroyed, marks if server has been destroyed. */
	protected volatile boolean destroyed;
	/** The operation timeout multiplier. */
	protected final double operationTimeoutMultiplier = AppContext.getBasicConfiguration().getOperationTimeoutMultiplier();
	/** The server timeout milliseconds. */
	private double serverTimeoutMillis;
	/** The server timeout. */
	private ScheduledFuture<TimeoutWrapper> timeout;
	/** The timeouter task. */
	private TimeoutWrapper timeouterTask;

	/**
	 * Instantiates a new server.
	 * 
	 * @param remoteNodeConfiguration
	 *            the remote node configuration
	 * @param socketAddress
	 *            the socket address
	 */
	public Server(RemoteNodeConfiguration remoteNodeConfiguration, InetSocketAddress socketAddress) {
		this.requester = new Requester(remoteNodeConfiguration);
		this.remoteNodeConfiguration = remoteNodeConfiguration;
		this.serverKey = "_" + socketAddress.getHostName() + Constants.SLASH + socketAddress.getPort();
		this.socketAddress = socketAddress;
		this.destroyed = false;

		// timeout observation only for stateful server necessary!
		if (this instanceof StatefulServer) {
			// calculate server timeout: multiply check registration interval with checkRegistrationIntervalMultiplier!
			this.serverTimeoutMillis = (remoteNodeConfiguration.getCheckRegistrationIntervalSeconds()
					* Constants.SEC_TO_MILLISEC_FACTOR * AppContext.getBasicConfiguration()
					.getCheckRegistrationIntervalMultiplier());
		} else {
			this.serverTimeoutMillis = 0;
		}
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
	 * Gets the socket address.
	 * 
	 * @return the socket address
	 */
	public SocketAddress getSocketAddress() {
		return socketAddress;
	}

	/**
	 * Gets the server timeout.
	 * 
	 * @return the server timeout
	 */
	public ScheduledFuture<TimeoutWrapper> getTimeout() {
		return timeout;
	}

	/**
	 * Sets the server timeout.
	 * 
	 * @param timeout
	 *            the new server timeout
	 */
	public void setTimeout(ScheduledFuture<TimeoutWrapper> timeout) {
		this.timeout = timeout;
	}

	/**
	 * Sets the timeouter task.
	 * 
	 * @param timeouterTask
	 *            the new timeouter task
	 */
	public void setTimeouterTask(TimeoutWrapper timeouterTask) {
		this.timeouterTask = timeouterTask;
	}

	/**
	 * Gets the timeouter task.
	 * 
	 * @return the timeouter task
	 */
	public TimeoutWrapper getTimeouterTask() {
		return this.timeouterTask;
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
	 * Gets the connection type
	 * 
	 * @return the host
	 */
	public String getConnectionType() {
		return this.remoteNodeConfiguration.getConnectionType();
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
	 * Gets the server timeout milliseconds.
	 * 
	 * @return the server timeout milliseconds
	 */
	public double getServerTimeoutMillis() {
		return this.serverTimeoutMillis;
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

	/**
	 * Checks if the server has been destroyed.
	 * 
	 * @return true, if is destroyed
	 */
	public boolean isDestroyed() {
		return this.destroyed;
	}

	/**
	 * Destroy server.
	 */
	public void destroy() {
		LOGGER.info("server destroy " + this.serverKey);
		this.destroyed = true;
		this.requester.destroy();
		AppContext.getServerRegistry().removeServer(this.getServerKey());
		this.requester = null;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.getServerKey() + ":" + this.remoteNodeConfiguration.getPort();
	}
}
