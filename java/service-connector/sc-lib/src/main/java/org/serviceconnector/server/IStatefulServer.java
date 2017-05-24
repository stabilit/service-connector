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
package org.serviceconnector.server;

import java.util.List;

import org.serviceconnector.service.AbstractSession;

/**
 * The Interface IStatefulServer.
 */
public interface IStatefulServer extends IServer {

	/** {@inheritDoc} */
	@Override
	public abstract ServerType getType();

	/** {@inheritDoc} */
	@Override
	public abstract void abortSession(AbstractSession session, String string);

	/**
	 * Removes an allocated session from the server.
	 *
	 * @param abstractSession the abstract session
	 */
	public abstract void removeSession(AbstractSession abstractSession);

	/**
	 * Adds an allocated session to the server.
	 *
	 * @param session the session
	 */
	public abstract void addSession(AbstractSession session);

	/**
	 * Gets the sessions.
	 *
	 * @return the sessions
	 */
	public abstract List<AbstractSession> getSessions();

	/**
	 * Gets the session count.
	 *
	 * @return the session count
	 */
	public abstract int getSessionCount();

	/**
	 * Checks for free session.
	 *
	 * @return true, if successful
	 */
	public abstract boolean hasFreeSession();

	/**
	 * Gets the max sessions.
	 *
	 * @return the max sessions
	 */
	public abstract int getMaxSessions();
}
