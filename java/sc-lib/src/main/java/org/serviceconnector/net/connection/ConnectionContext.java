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
package org.serviceconnector.net.connection;

import org.apache.log4j.Logger;

/**
 * The Class ConnectionContext.
 * 
 * @author JTraber
 */
public class ConnectionContext {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(ConnectionContext.class);
	
	/** The connection. */
	private IConnection connection;
	/** The idle timeout. */
	private int idleTimeoutSeconds;
	/** The idle callback. */
	private IIdleConnectionCallback idleCallback;

	/**
	 * Instantiates a new connection context.
	 * 
	 * @param connection
	 *            the connection
	 * @param idleCallback
	 *            the idle callback
	 * @param idleTimeoutSeconds
	 *            the idle timeout
	 */
	public ConnectionContext(IConnection connection, IIdleConnectionCallback idleCallback, int idleTimeoutSeconds) {
		this.connection = connection;
		this.idleTimeoutSeconds = idleTimeoutSeconds;
		this.idleCallback = idleCallback;
	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 */
	public IConnection getConnection() {
		return this.connection;
	}

	/**
	 * Gets the idle timeout in seconds.
	 *
	 * @return the idle timeout
	 */
	public int getIdleTimeoutSeconds() {
		return this.idleTimeoutSeconds;
	}

	/**
	 * Gets the idle callback.
	 *
	 * @return the idle callback
	 */
	public IIdleConnectionCallback getIdleCallback() {
		return this.idleCallback;
	}
}
