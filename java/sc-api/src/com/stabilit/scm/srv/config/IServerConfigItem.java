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
package com.stabilit.sc.srv.config;

/**
 * The Interface IServerConfigItem.
 * 
 * @author JTraber
 */
public interface IServerConfigItem {

	/**
	 * Gets the server name.
	 * 
	 * @return the server name
	 */
	public abstract String getServerName();

	/**
	 * Sets the server name.
	 * 
	 * @param serverName
	 *            the new server name
	 */
	public abstract void setServerName(String serverName);

	/**
	 * Gets the port.
	 * 
	 * @return the port
	 */
	public abstract int getPort();

	/**
	 * Sets the port.
	 * 
	 * @param port
	 *            the new port
	 */
	public abstract void setPort(int port);

	/**
	 * Gets the host.
	 * 
	 * @return the host
	 */
	public abstract String getHost();

	/**
	 * Sets the host.
	 * 
	 * @param host
	 *            the new host
	 */
	public abstract void setHost(String host);

	/**
	 * Gets the connection. Connection identifies concrete implementation of a server.
	 * 
	 * @return the connection
	 */
	public abstract String getConnection();

	/**
	 * Sets the connection.
	 * 
	 * @param connection
	 *            the new connection
	 */
	public abstract void setConnection(String connection);

	/**
	 * Sets the number of threads.
	 * 
	 * @param numberOfThreads
	 *            the new number of threads
	 */
	public void setNumberOfThreads(int numberOfThreads);

	/**
	 * Gets the number of threads.
	 * 
	 * @return the number of threads
	 */
	public int getNumberOfThreads();
}