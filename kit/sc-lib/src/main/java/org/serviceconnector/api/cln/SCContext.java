/*
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
 */
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
	 * @param connectionPool
	 *            the new connection pool
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
