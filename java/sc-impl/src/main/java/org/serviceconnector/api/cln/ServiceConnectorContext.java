package org.serviceconnector.api.cln;

import org.serviceconnector.net.connection.ConnectionPool;

/**
 * The Class ServiceConnectorContext.
 */
public class ServiceConnectorContext {

	/** The connection pool. */
	private ConnectionPool connectionPool;
	
	/** The sc client. */
	private ISCClient scClient;

	/**
	 * Instantiates a new service connector context.
	 *
	 * @param connectionPool the connection pool
	 * @param scClient the sc client
	 */
	public ServiceConnectorContext(ConnectionPool connectionPool, ISCClient scClient) {
		this.connectionPool = connectionPool;
		this.scClient = scClient;
	}

	/**
	 * Gets the connection pool.
	 *
	 * @return the connection pool
	 */
	public ConnectionPool getConnectionPool() {
		return this.connectionPool;
	}

	/**
	 * Gets the service connector.
	 *
	 * @return the service connector
	 */
	public ISCClient getServiceConnector() {
		return this.scClient;
	}
}
