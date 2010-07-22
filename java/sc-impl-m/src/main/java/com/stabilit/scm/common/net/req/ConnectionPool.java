package com.stabilit.scm.common.net.req;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.scmp.SCMPKeepAlive;
import com.stabilit.scm.common.util.SynchronousCallback;
import com.stabilit.scm.srv.IIdleCallback;

/**
 * @author JTraber
 */
public class ConnectionPool implements IConnectionPool {

	private int port;
	private String host;
	private String conType;
	private int maxConnections;
	private int minConnections;
	private boolean closeOnFree;
	private int keepAliveInterval;
	private int numberOfThreads;
	private List<IConnection> freeConnections;
	private List<IConnection> usedConnections;
	private ConnectionFactory connectionFactory;

	public ConnectionPool(String host, int port, String conType, int keepAliveInterval, int numberOfThreads) {
		this.host = host;
		this.port = port;
		this.conType = conType;
		this.closeOnFree = false; // default = false
		this.maxConnections = Constants.DEFAULT_MAX_CONNECTIONS;
		this.minConnections = 1;
		this.freeConnections = Collections.synchronizedList(new ArrayList<IConnection>());
		this.usedConnections = Collections.synchronizedList(new ArrayList<IConnection>());
		this.connectionFactory = new ConnectionFactory();
		this.keepAliveInterval = keepAliveInterval;
		this.numberOfThreads = numberOfThreads;
	}

	public ConnectionPool(String host, int port, String conType) {
		this(host, port, conType, Constants.DEFAULT_KEEP_ALIVE_INTERVAL, Constants.DEFAULT_NR_OF_THREADS);
	}

	public ConnectionPool(String host, int port, int keepAliveInterval) {
		this(host, port, Constants.DEFAULT_CLIENT_CON, keepAliveInterval, Constants.DEFAULT_NR_OF_THREADS);
	}

	public ConnectionPool(String host, int port) {
		this(host, port, Constants.DEFAULT_CLIENT_CON, Constants.DEFAULT_KEEP_ALIVE_INTERVAL,
				Constants.DEFAULT_NR_OF_THREADS);
	}

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

	private IConnection createNewConnection() throws Exception {
		IConnection connection = null;
		if (usedConnections.size() >= maxConnections) {
			// we can't create a new one - limit reached
			throw new ConnectionPoolException("Unable to create new connection - limit of : " + maxConnections
					+ "reached!");
		}
		// we create a new one
		connection = connectionFactory.newInstance(this.conType);
		connection.setHost(this.host);
		connection.setPort(this.port);
		connection.setIdleTimeout(this.keepAliveInterval);
		connection.setNumberOfThreads(this.numberOfThreads);
		IIdleCallback idleCallback = new IdleCallback();
		IConnectionContext connectionContext = new ConnectionContext(connection, idleCallback, this.keepAliveInterval);
		connection.setContext(connectionContext);
		try {
			connection.connect(); // can throw an exception
		} catch (Throwable th) {
			throw new ConnectionPoolException("Unable to establish new connection.", th);
		}
		return connection;
	}

	@Override
	public void freeConnection(IConnection connection) {
		if (this.usedConnections.remove(connection) == false) {
			LoggerPoint.getInstance().fireWarn(this, "connection does not exist - not possible to free");
		}
		if (closeOnFree) {
			// do not add the connection to free pool array - just close it immediately!
			this.destroyConnection(connection);
			return;
		}
		connection.resetNrOfIdles();
		this.freeConnections.add(connection);
	}

	@Override
	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	@Override
	public void destroy() {
		this.destroyConnections(this.usedConnections);
		this.destroyConnections(this.freeConnections);
	}

	private void destroyConnections(List<IConnection> connections) {
		IConnection connection;
		for (int index = 0; index < connections.size(); index++) {
			connection = connections.remove(0);
			this.destroyConnection(connection);
		}
	}

	private void destroyConnection(IConnection connection) {
		try {
			connection.disconnect();
		} catch (Exception e) {
			LoggerPoint.getInstance().fireException(this,
					"Exception when connection pool destroys - connection destroy failed");
		} finally {
			connection.destroy();
		}
	}

	@Override
	public void setCloseOnFree(boolean closeOnFree) {
		this.closeOnFree = closeOnFree;
	}

	@Override
	public void setMinConnections(int minConnections) {
		this.minConnections = minConnections;
	}

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
				LoggerPoint.getInstance().fireException(this,
						"Exception when starting connection pool - create, connect connection failed");
				return;
			}
			this.freeConnections.add(connection);
		}
	}

	@Override
	public int getMaxConnections() {
		return maxConnections;
	}

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

	@Override
	public void connectionIdle(IConnection connection) {
		if (this.freeConnections.remove(connection) == false) {
			// this connection is no more free - no keep alive necessary
			return;
		}
		if (connection.getNrOfIdlesInSequence() > Constants.DEFAULT_NR_OF_KEEP_ALIVES_TO_CLOSE) {
			// connection has been idle for the DEFAULT_NR_OF_KEEP_ALIVES_TO_CLOSE times
			if (this.freeConnections.size() > 1) {
				// there is still more than one connection free - destroy this one
				this.destroyConnection(connection);
				return;
			}
		}
		SCMPKeepAlive keepAliveMessage = new SCMPKeepAlive();
		try {
			ConnectionPoolCallback callback = new ConnectionPoolCallback();
			connection.send(keepAliveMessage, callback);
			callback.getMessageSync(Constants.SERVICE_LEVEL_OPERATION_TIMEOUT_MILLIS);
			connection.incrementNrOfIdles();
			this.freeConnections.add(connection);
		} catch (Exception e) {
			ExceptionPoint.getInstance().fireException(this, e);
		}
	}

	@Override
	public int getKeepAliveInterval() {
		return this.keepAliveInterval;
	}

	private class IdleCallback implements IIdleCallback {

		@Override
		public void connectionIdle(IConnection connection) {
			ConnectionPool.this.connectionIdle(connection);
		}
	}

	private class ConnectionPoolCallback extends SynchronousCallback {
		// nothing to implement in this case everything is done in super-class
	}
}
