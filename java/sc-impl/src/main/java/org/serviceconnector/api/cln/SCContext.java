package org.serviceconnector.api.cln;

import org.serviceconnector.net.connection.ConnectionPool;

/**
 * The Class SCContext.
 */
public class SCContext {

	/** The connection pool. */
	private ConnectionPool connectionPool;
	/** The sc client. */
	private SCClient scClient;

	/**
	 * Instantiates a new SCContext.
	 * 
	 * @param connectionPool
	 *            the connection pool
	 * @param scClient
	 *            the sc client
	 */
	public SCContext(SCClient scClient) {
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
	 * Sets the connection pool.
	 *
	 * @param connectionPool the new connection pool
	 */
	public void setConnectionPool(ConnectionPool connectionPool) {
		this.connectionPool = connectionPool;
	}

	/**
	 * Gets the sC client.
	 * 
	 * @return the sC client
	 */
	public SCClient getSCClient() {
		return this.scClient;
	}
}
