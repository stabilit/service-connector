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
package org.serviceconnector.registry;

import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.SessionLogger;
import org.serviceconnector.server.IServer;
import org.serviceconnector.service.Session;
import org.serviceconnector.util.ITimeout;
import org.serviceconnector.util.TimeoutWrapper;

/**
 * The Class SessionRegistry. Registry stores entries for properly created sessions. Registry is also responsible for observing the
 * session timeout and initiating clean up in case of a broken session.
 * 
 * @author JTraber
 */
public class SessionRegistry extends Registry<String, Session> {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(SessionRegistry.class);

	/** The timer. Timer instance is responsible to observe session timeouts. */
	private ScheduledThreadPoolExecutor sessionScheduler;

	/**
	 * Instantiates a SessionRegistry.
	 */
	public SessionRegistry() {
		this.sessionScheduler = new ScheduledThreadPoolExecutor(1);
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
		SessionLogger.logCreateSession(session.getId(), session.getSessionTimeoutSeconds());
		this.put(key, session);
		this.scheduleSessionTimeout(session);
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
		Session session = super.get(key);
		if (session == null) {
			return;
		}
		// clears message in cache if in loading state
		AppContext.getCacheManager().clearLoading(session.getId());
		this.cancelSessionTimeout(session);
		super.remove(key);
		SessionLogger.logDeleteSession(session.getId());
	}

	/**
	 * Gets the session.
	 * 
	 * @param key
	 *            the key
	 * @return the session
	 */
	public Session getSession(String key) {
		return super.get(key);
	}

	/**
	 * Gets all sessions.
	 * 
	 * @return the sessions
	 */
	public Session[] getSessions() {
		try {
			Set<Entry<String, Session>> entries = this.registryMap.entrySet();
			Session[] sessions = new Session[entries.size()];
			int index = 0;
			for (Entry<String, Session> entry : entries) {
				// String key = entry.getKey();
				Session session = entry.getValue();
				sessions[index++] = session;
			}
			return sessions;
		} catch (Exception e) {
			LOGGER.error("getSessions", e);
		}
		return null;
	}

	/**
	 * Schedule session timeout.
	 * 
	 * @param session
	 *            the session
	 */
	@SuppressWarnings("unchecked")
	public void scheduleSessionTimeout(Session session) {
		if (session == null || session.getSessionTimeoutSeconds() == 0) {
			// no scheduling of session timeout
			return;
		}
		// always cancel old timeouter before setting up a new one
		this.cancelSessionTimeout(session);
		// sets up session timeout
		TimeoutWrapper sessionTimeouter = new TimeoutWrapper(new SessionTimeout(session));
		// schedule sessionTimeouter in registry timer
		ScheduledFuture<TimeoutWrapper> timeout = (ScheduledFuture<TimeoutWrapper>) this.sessionScheduler.schedule(
				sessionTimeouter, (long) session.getSessionTimeoutSeconds(), TimeUnit.SECONDS);
		SessionLogger.trace("schedule session " + session.getId() + " timeout in seconds "
				+ (long) session.getSessionTimeoutSeconds() + " delay time in seconds" + timeout.getDelay(TimeUnit.SECONDS));
		session.setTimeout(timeout);
		session.setTimeouterTask(sessionTimeouter);
	}

	/**
	 * Cancel session timeout.
	 * 
	 * @param session
	 *            the session
	 */
	public void cancelSessionTimeout(Session session) {
		if (session == null) {
			return;
		}
		ScheduledFuture<TimeoutWrapper> sessionTimeout = session.getTimeout();
		if (sessionTimeout == null) {
			// no session timeout has been set up for this session
			return;
		}
		SessionLogger.trace("cancel session timeout " + session.getId());
		boolean cancelSuccess = sessionTimeout.cancel(false);
		if (cancelSuccess == false) {
			SessionLogger.error("cancel of session timeout failed :" + session.getId() + " delay millis: "
					+ sessionTimeout.getDelay(TimeUnit.MILLISECONDS));
			boolean remove = this.sessionScheduler.remove(session.getTimeouterTask());
			if (remove == false) {
				SessionLogger.error("remove of session timeout failed :" + session.getId() + " delay millis: "
						+ sessionTimeout.getDelay(TimeUnit.MILLISECONDS));
			}
		}
		this.sessionScheduler.purge();
		// important to set timeouter null - rescheduling of same instance not possible
		session.setTimeout(null);
	}

	/**
	 * The Class SessionTimeout. Gets control when a session times out. Responsible for cleaning up when session gets broken.
	 */
	private class SessionTimeout implements ITimeout {
		/** The session. */
		private Session session;
		/** The timeout. */
		private double timeoutSeconds;

		/**
		 * Instantiates a new session timer run.
		 * 
		 * @param session
		 *            the session
		 */
		public SessionTimeout(Session session) {
			this.session = session;
			this.timeoutSeconds = session.getSessionTimeoutSeconds();
		}

		/**
		 * Timeout. Session timeout run out.
		 */
		@Override
		public void timeout() {
			/**
			 * broken session procedure<br />
			 * 1. remove session from session registry<br />
			 * 2. abort session on backend server<br />
			 */
			SessionRegistry.this.removeSession(session);
			IServer server = session.getServer();
			// aborts session on server
			server.abortSession(session, "session timed out in session registry");
			SessionLogger.logTimeoutSession(session.getId());
		}

		/** {@inheritDoc} */
		@Override
		public int getTimeoutMillis() {
			return (int) (this.timeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		}
	}
}