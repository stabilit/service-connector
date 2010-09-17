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
package org.serviceconnector.sc.registry;

import org.apache.log4j.Logger;
import org.serviceconnector.log.SessionLogger;
import org.serviceconnector.registry.Registry;
import org.serviceconnector.sc.service.Session;


/**
 * The Class SessionRegistry. Registry stores entries for properly created subscriptions.
 * 
 * @author JTraber
 */
public class SubscriptionSessionRegistry extends Registry<String, Session> {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(SubscriptionSessionRegistry.class);
	
	/** The Constant sessionLogger. */
	private final static SessionLogger sessionLogger = SessionLogger.getInstance();
	
	/** The instance. */
	private static SubscriptionSessionRegistry instance = new SubscriptionSessionRegistry();

	/**
	 * Gets the current instance.
	 * 
	 * @return the current instance
	 */
	public static SubscriptionSessionRegistry getCurrentInstance() {
		return instance;
	}

	/**
	 * Adds the session.
	 * 
	 * @param key
	 *            the key
	 * @param session
	 *            the session
	 */
	public void addSession(String key, Session session) {
		sessionLogger.logCreateSession(this.getClass().getName(), session.getId());
		this.put(key, session);
	}

	/**
	 * Removes the session.
	 * 
	 * @param session
	 *            the session
	 */
	public void removeSession(Session session) {
		this.removeSession(session.getId());
	}

	/**
	 * Removes the session.
	 * 
	 * @param key
	 *            the key
	 */
	public void removeSession(String key) {
		super.remove(key);
		sessionLogger.logDeleteSession(this.getClass().getName(), key);
	}

	/**
	 * Gets the session.
	 * 
	 * @param key
	 *            the key
	 * @return the session
	 */
	public Session getSession(String key) {
		Session session = super.get(key);
		return session;
	}
}
