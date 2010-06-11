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
package com.stabilit.scm.sc.registry;

import com.stabilit.scm.common.log.listener.SessionPoint;
import com.stabilit.scm.common.registry.Registry;
import com.stabilit.scm.sc.service.Session;

/**
 * The Class SessionRegistry. Registry stores entries for properly created sessions.
 * 
 * @author JTraber
 */
public class SessionRegistry extends Registry {

	/** The instance. */
	private static SessionRegistry instance = new SessionRegistry();

	/**
	 * Instantiates a new session registry.
	 */
	public SessionRegistry() {
	}

	/**
	 * Gets the current instance.
	 * 
	 * @return the current instance
	 */
	public static SessionRegistry getCurrentInstance() {
		return instance;
	}

	public void addSession(Object key, Session session) {
		SessionPoint.getInstance().fireCreate(this, session.getId());
		this.put(key, session);
	}

	public void removeSession(Session session) {
		this.removeSession(session.getId());
	}

	public void removeSession(Object key) {
		super.remove(key);
		SessionPoint.getInstance().fireDelete(this, (String) key);
	}

	public Session getSession(Object key) {
		return (Session) super.get(key);
	}
}
