/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package org.serviceconnector.common.net.req;

/**
 * The Interface IConnectionPool. Reveals functionality of a connection pool.
 * 
 * @author JTraber
 */
public interface IConnectionPool {

	/**
	 * Gets a connection of the pool.
	 * 
	 * @return the connection
	 * @throws Exception
	 *             the exception
	 */
	public abstract IConnection getConnection() throws Exception;

	/**
	 * Free connection. Gives connection back for other interested parties.
	 * 
	 * @param connection
	 *            the connection
	 * @throws Exception
	 *             the exception
	 */
	public abstract void freeConnection(IConnection connection) throws Exception;

	/**
	 * Force closing specific connection.
	 * 
	 * @param connection
	 *            the connection
	 * @throws Exception
	 *             the exception
	 */
	public abstract void forceClosingConnection(IConnection connection) throws Exception;

	/**
	 * Sets the max connections for the pool.
	 * 
	 * @param maxConnections
	 *            the new max connections
	 */
	public abstract void setMaxConnections(int maxConnections);

	/**
	 * Sets the minimum connections for the pool.
	 * 
	 * @param minConnections
	 *            the new minimum connections
	 */
	public abstract void setMinConnections(int minConnections);

	/**
	 * Sets the close on free. Indicates that connection should be closed at the time they get freed.
	 * 
	 * @param closeOnFree
	 *            the new close on free
	 */
	public abstract void setCloseOnFree(boolean closeOnFree);

	/**
	 * Initiates the minimum connections. The minimum of connections gets active immediately.
	 */
	public abstract void initMinConnections();

	/**
	 * Destroy the pool.
	 */
	public abstract void destroy();

	/**
	 * Gets the max connections.
	 * 
	 * @return the max connections
	 */
	public abstract int getMaxConnections();

	/**
	 * Checks for free connections in the pool.
	 * 
	 * @return true, if successful
	 */
	public abstract boolean hasFreeConnections();

	/**
	 * Connection idle. Process idle event of connection.
	 * 
	 * @param connection
	 *            the connection
	 * @throws Exception
	 *             the exception
	 */
	public abstract void connectionIdle(IConnection connection) throws Exception;

	/**
	 * Gets the keep alive interval the pool is observing. 0 means disabled.
	 * 
	 * @return the keep alive interval
	 */
	public abstract int getKeepAliveInterval();

	/**
	 * Gets the host.
	 * 
	 * @return the host
	 */
	public abstract String getHost();

	/**
	 * Gets the port.
	 * 
	 * @return the port
	 */
	public abstract int getPort();

	/**
	 * Gets the number of busy connections at this time.
	 * 
	 * @return the busy connections
	 */
	public abstract int getBusyConnections();
}
