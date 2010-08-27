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
package com.stabilit.scm.common.net.req;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.log.IExceptionLogger;
import com.stabilit.scm.common.log.impl.ExceptionLogger;
import com.stabilit.scm.common.scmp.SCMPKeepAlive;
import com.stabilit.scm.common.util.SynchronousCallback;
import com.stabilit.scm.srv.IIdleCallback;

/**
 * The Class ConnectionPool. Concrete implementation of connection pooling.<br>
 * <br>
 * This connection pool takes care of following listed points:<br>
 * - creating / destroying of connections<br>
 * - observing the max numbers of connections<br>
 * - keeping a minimum of connections active<br>
 * - disconnect connection after Constants.DEFAULT_NR_OF_KEEP_ALIVES_TO_CLOSE <br>
 * - destroying connection pool, destroys all connections <br>
 * <br>
 * optional functions:<br>
 * - closing connection after getting it back<br>
 * - initializing pool by starting a minimum of connections immediately<br>
 * - observing connection idle timeout and sending keep alive messages to refresh firewall<br>
 * - force closing of a specific connection, very useful if connection has a curious state
 * 
 * @author JTraber
 */
public class ConnectionPool implements IConnectionPool {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ConnectionPool.class);

	/** The port. */
	private int port;
	/** The host. */
	private String host;
	/** The connection type. */
	private String connectionType;
	/** The maximum connections. */
	private int maxConnections;
	/** The minimum connections. */
	private int minConnections;
	/** The close on free. */
	private boolean closeOnFree;
	/** The keep alive interval. */
	private int keepAliveInterval;
	/** The free connections. */
	private List<IConnection> freeConnections;
	/** The used connections. */
	private List<IConnection> usedConnections;
	/** The connection factory. */
	private ConnectionFactory connectionFactory;

	/**
	 * Instantiates a new connection pool.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param conType
	 *            the connection type
	 * @param keepAliveInterval
	 *            the keep alive interval
	 */
	public ConnectionPool(String host, int port, String conType, int keepAliveInterval) {
		this.host = host;
		this.port = port;
		this.connectionType = conType;
		// default = false connection will not be closed at the time they are freed
		this.closeOnFree = false;
		this.maxConnections = Constants.DEFAULT_MAX_CONNECTIONS;
		// the minimum of connection is 1 per default - means there is always one connection active
		this.minConnections = 1;
		this.freeConnections = Collections.synchronizedList(new ArrayList<IConnection>());
		this.usedConnections = Collections.synchronizedList(new ArrayList<IConnection>());
		this.connectionFactory = ConnectionFactory.getCurrentInstance();
		this.keepAliveInterval = keepAliveInterval;
	}

	/**
	 * Instantiates a new connection pool.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param conType
	 *            the connection type
	 */
	public ConnectionPool(String host, int port, String conType) {
		this(host, port, conType, Constants.DEFAULT_KEEP_ALIVE_INTERVAL);
	}

	/**
	 * Instantiates a new connection pool.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param keepAliveInterval
	 *            the keep alive interval
	 */
	public ConnectionPool(String host, int port, int keepAliveInterval) {
		this(host, port, Constants.DEFAULT_CLIENT_CON, keepAliveInterval);
	}

	/**
	 * Instantiates a new connection pool.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 */
	public ConnectionPool(String host, int port) {
		this(host, port, Constants.DEFAULT_CLIENT_CON, Constants.DEFAULT_KEEP_ALIVE_INTERVAL);
	}

	/** {@inheritDoc} */
	@Override
	public IConnection getConnection() throws Exception {
		IConnection connection = null;
		synchronized (freeConnections) {
			if (freeConnections.size() > 0) {
				connection = freeConnections.remove(0);
			}
		}
		if (connection == null) {
			// no free connection available - try to create a new one
			connection = this.createNewConnection();
		}
		this.usedConnections.add(connection);
		return connection;
	}

	/**
	 * Creates the new connection.
	 * 
	 * @return the i connection
	 * @throws Exception
	 *             the exception
	 */
	private IConnection createNewConnection() throws Exception {
		IConnection connection = null;
		if (usedConnections.size() >= maxConnections) {
			// we can't create a new one - limit reached
			throw new ConnectionPoolBusyException("Unable to create new connection - limit of : " + maxConnections
					+ " reached!");
		}
		// we create a new one
		connection = connectionFactory.newInstance(this.connectionType);
		connection.setHost(this.host);
		connection.setPort(this.port);
		connection.setIdleTimeout(this.keepAliveInterval);
		IIdleCallback idleCallback = new IdleCallback();
		IConnectionContext connectionContext = new ConnectionContext(connection, idleCallback, this.keepAliveInterval);
		connection.setContext(connectionContext);
		try {
			connection.connect(); // can throw an exception
		} catch (Exception ex) {
			throw new ConnectionPoolConnectException("Unable to establish new connection.", ex);
		}
		return connection;
	}

	/** {@inheritDoc} */
	@Override
	public void freeConnection(IConnection connection) {
		if (this.usedConnections.remove(connection) == false) {
			logger.warn("connection does not exist in pool - not possible to free");
			return;
		}
		if (closeOnFree && this.freeConnections.size() > 0) {
			// do not add the connection to free pool array - just close it immediately!
			// at least keep one connection alive
			this.disconnectConnection(connection);
			return;
		}
		connection.resetNrOfIdles();
		this.freeConnections.add(connection);
	}

	/** {@inheritDoc} */
	@Override
	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	/** {@inheritDoc} */
	@Override
	public void destroy() {
		this.destroyConnections(this.usedConnections);
		this.destroyConnections(this.freeConnections);
	}

	/**
	 * Destroy connections.
	 * 
	 * @param connections
	 *            the connections
	 */
	private void destroyConnections(List<IConnection> connections) {
		IConnection connection;
		for (int index = 0; index < connections.size(); index++) {
			connection = connections.remove(0);
			this.destroyConnection(connection);
		}
	}

	/**
	 * Destroy connection. Careful in use - to be called only if pool gets destroyed. Destroying a single connection may
	 * affect others because of shared stuff (timer) etc.
	 * 
	 * @param connection
	 *            the connection
	 */
	private void destroyConnection(IConnection connection) {
		try {
			connection.disconnect();
		} catch (Exception e) {
			IExceptionLogger exceptionLogger = ExceptionLogger.getInstance();
			exceptionLogger.logDebugException(logger, this.getClass().getName(), e);
		} finally {
			connection.destroy();
		}
	}

	/**
	 * Disconnect connection.
	 * 
	 * @param connection
	 *            the connection
	 */
	private void disconnectConnection(IConnection connection) {
		try {
			connection.disconnect();
		} catch (Exception e) {
			IExceptionLogger exceptionLogger = ExceptionLogger.getInstance();
			exceptionLogger.logDebugException(logger, this.getClass().getName(), e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void forceClosingConnection(IConnection connection) {
		// assure connection is nowhere registered
		this.usedConnections.remove(connection);
		this.freeConnections.remove(connection);

		try {
			connection.disconnect();
		} catch (Exception e) {
			IExceptionLogger exceptionLogger = ExceptionLogger.getInstance();
			exceptionLogger.logDebugException(logger, this.getClass().getName(), e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void setCloseOnFree(boolean closeOnFree) {
		this.closeOnFree = closeOnFree;
	}

	/** {@inheritDoc} */
	@Override
	public void setMinConnections(int minConnections) {
		this.minConnections = minConnections;
	}

	/** {@inheritDoc} */
	@Override
	public void initMinConnections() {
		IConnection connection = null;
		int con = usedConnections.size() + freeConnections.size();
		for (int countCon = con; countCon < this.minConnections; countCon++) {
			try {
				connection = this.createNewConnection();
				if (connection == null) {
					// connection null at the time maxConnections is reached - stop creating
					return;
				}
			} catch (Exception e) {
				IExceptionLogger exceptionLogger = ExceptionLogger.getInstance();
				exceptionLogger.logDebugException(logger, this.getClass().getName(), e);
				return;
			}
			this.freeConnections.add(connection);
		}
	}

	/** {@inheritDoc} */
	@Override
	public int getMaxConnections() {
		return maxConnections;
	}

	/** {@inheritDoc} */
	@Override
	public int getBusyConnections() {
		return this.usedConnections.size();
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasFreeConnections() {
		if (freeConnections.size() > 0) {
			// we have free connections left
			return true;
		}
		if (usedConnections.size() < maxConnections) {
			// we can create new connections if necessary
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public void connectionIdle(IConnection connection) {
		if (this.freeConnections.remove(connection) == false) {
			// this connection is no more free - no keep alive necessary
			return;
		}
		if (connection.getNrOfIdlesInSequence() > Constants.DEFAULT_NR_OF_KEEP_ALIVES_TO_CLOSE) {
			// connection has been idle for the DEFAULT_NR_OF_KEEP_ALIVES_TO_CLOSE times
			if (this.freeConnections.size() + this.usedConnections.size() > this.minConnections) {
				// there are still enough (totalCons > minConnections) free - disconnect this one
				this.disconnectConnection(connection);
				return;
			}
		}
		// send a keep alive message
		SCMPKeepAlive keepAliveMessage = new SCMPKeepAlive();
		try {
			ConnectionPoolCallback callback = new ConnectionPoolCallback();
			connection.send(keepAliveMessage, callback);
			callback.getMessageSync(Constants.OPERATION_TIMEOUT_MILLIS_SHORT);
			connection.incrementNrOfIdles();
			this.freeConnections.add(connection);
		} catch (Exception e) {
			logger.error("connectionIdle "+e.getMessage(), e);
			ExceptionPoint.getInstance().fireException(this, e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public int getKeepAliveInterval() {
		return this.keepAliveInterval;
	}

	/** {@inheritDoc} */
	@Override
	public String getHost() {
		return this.host;
	}

	/** {@inheritDoc} */
	@Override
	public int getPort() {
		return this.port;
	}

	/**
	 * The Class IdleCallback. Gets informed when connection runs into an idle timeout.
	 */
	private class IdleCallback implements IIdleCallback {

		/** {@inheritDoc} */
		@Override
		public void connectionIdle(IConnection connection) {
			ConnectionPool.this.connectionIdle(connection);
		}
	}

	/**
	 * The Class ConnectionPoolCallback.
	 */
	private class ConnectionPoolCallback extends SynchronousCallback {
		// nothing to implement in this case everything is done in super-class
	}

	/** {@inheritDoc} */
	@Override
	protected void finalize() throws Throwable {
		this.destroy();
	}
}
