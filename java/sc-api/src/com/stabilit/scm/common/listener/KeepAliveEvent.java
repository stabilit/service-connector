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
package com.stabilit.scm.common.listener;

import java.util.EventObject;

import com.stabilit.scm.common.net.req.IConnection;

/**
 * The Class KeepAliveEvent. Event telling that given connection did run in idle timeout.
 */
public class KeepAliveEvent extends EventObject {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4103386157728170188L;
	/** The connection. */
	private IConnection connection;

	/**
	 * Instantiates a new keep alive event.
	 *
	 * @param source the source
	 * @param connection the connection
	 */
	public KeepAliveEvent(Object source, IConnection connection) {
		super(source);
		this.connection = connection;
	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 */
	public IConnection getConnection() {
		return connection;
	}
}
