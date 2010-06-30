package com.stabilit.scm.common.net.req;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.stabilit.scm.common.conf.IConstants;
import com.stabilit.scm.common.listener.IKeepAliveListener;
import com.stabilit.scm.common.listener.KeepAliveEvent;
import com.stabilit.scm.common.listener.KeepAlivePoint;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.scmp.SCMPKeepAlive;

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
	private IKeepAliveListener keepAliveListener;

	public ConnectionPool(String host, int port, String conType, int keepAliveInterval, int numberOfThreads) {
		this.host = host;
		this.port = port;
		this.conType = conType;
		this.closeOnFree = false; // default = false
		this.maxConnections = IConstants.DEFAULT_MAX_CONNECTIONS;
		this.minConnections = 1;
		this.freeConnections = Collections.synchronizedList(new ArrayList<IConnection>());
		this.usedConnections = Collections.synchronizedList(new ArrayList<IConnection>());
		this.connectionFactory = new ConnectionFactory();
		this.keepAliveInterval = keepAliveInterval;
		this.numberOfThreads = numberOfThreads;

		if (this.keepAliveInterval != 0) {
			this.keepAliveListener = new ConnectionPoolKeepAliveListener();
			KeepAlivePoint.getInstance().addListener(keepAliveListener);
		}
	}

	public ConnectionPool(String host, int port, String conType) {
		this(host, port, conType, IConstants.DEFAULT_KEEP_ALIVE_INTERVAL, IConstants.DEFAULT_NR_OF_THREADS);
	}

	public ConnectionPool(String host, int port, int keepAliveInterval) {
		this(host, port, IConstants.DEFAULT_CLIENT_CON, keepAliveInterval, IConstants.DEFAULT_NR_OF_THREADS);
	}

	public ConnectionPool(String host, int port) {
		this(host, port, IConstants.DEFAULT_CLIENT_CON, IConstants.DEFAULT_KEEP_ALIVE_INTERVAL,
				IConstants.DEFAULT_NR_OF_THREADS);
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
		IConnection connection;
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
			// do not add the connection to free pool array - just close it immediate!
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

	private void keepAliveConnection(IConnection connection) throws Exception {
		if (this.freeConnections.remove(connection) == false) {
			// this connection is no more free - no keep alive necessary
			return;
		}
		if (connection.getNrOfIdlesInSequence() > IConstants.DEFAULT_NR_OF_KEEP_ALIVES_TO_CLOSE) {
			this.destroyConnection(connection);
			return;
		}
		SCMPKeepAlive keepAliveMessage = new SCMPKeepAlive();
		connection.sendAndReceive(keepAliveMessage);
		connection.incrementNrOfIdles();
		this.freeConnections.add(connection);
	}

	private class ConnectionPoolKeepAliveListener implements IKeepAliveListener {

		@Override
		public void keepAliveEvent(KeepAliveEvent keepAliveEvent) throws Exception {
			IConnection connection = keepAliveEvent.getConnection();
			ConnectionPool.this.keepAliveConnection(connection);
		}
	}
}
