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

import org.apache.log4j.Logger;

import com.stabilit.scm.srv.IIdleCallback;

/**
 * The Class ConnectionContext.
 * 
 * @author JTraber
 */
public class ConnectionContext implements IConnectionContext {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ConnectionContext.class);
	
	/** The connection. */
	private IConnection connection;
	/** The idle timeout. */
	private int idleTimeout;
	/** The idle callback. */
	private IIdleCallback idleCallback;

	/**
	 * Instantiates a new connection context.
	 * 
	 * @param connection
	 *            the connection
	 * @param idleCallback
	 *            the idle callback
	 * @param idleTimeout
	 *            the idle timeout
	 */
	public ConnectionContext(IConnection connection, IIdleCallback idleCallback, int idleTimeout) {
		this.connection = connection;
		this.idleTimeout = idleTimeout;
		this.idleCallback = idleCallback;
	}

	/** {@inheritDoc} */
	@Override
	public IConnection getConnection() {
		return this.connection;
	}

	/** {@inheritDoc} */
	@Override
	public int getIdleTimeout() {
		return this.idleTimeout;
	}

	/** {@inheritDoc} */
	@Override
	public IIdleCallback getIdleCallback() {
		return this.idleCallback;
	}
}
