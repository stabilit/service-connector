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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.SessionLogger;
import org.serviceconnector.server.IServer;
import org.serviceconnector.service.Session;
import org.serviceconnector.util.ITimeout;
import org.serviceconnector.util.NamedPriorityThreadFactory;
import org.serviceconnector.util.TimeoutWrapper;
import org.serviceconnector.util.XMLDumpWriter;

/**
 * The Class SessionRegistry. Registry stores entries for properly created sessions. Registry is also responsible for observing the session timeout and initiating clean up in case
 * of a broken session. Session timer gets initialized by adding the session. Resetting the timer needs to be done outside the registry by calling reset method.
 *
 * @author JTraber
 */
public class SessionRegistry extends Registry<String, Session> {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionRegistry.class);

	private ScheduledThreadPoolExecutor sessionScheduler;

	/**
	 * Instantiates a SessionRegistry.
	 */
	public SessionRegistry() {
		this.sessionScheduler = new ScheduledThreadPoolExecutor(1, new NamedPriorityThreadFactory("SessionTimeout"));
	}

	/**
	 * Adds the session.
	 *
	 * @param key the key
	 * @param session the session
	 */
	public void addSession(String key, Session session) {
		SessionLogger.logCreateSession(session.getId(), session.getSessionTimeoutMillis());
		this.put(key, session);
		this.scheduleSessionTimeout(session, session.getSessionTimeoutMillis());
	}

	/**
	 * Removes the session.
	 *
	 * @param session the session
	 */
	public void removeSession(Session session) {
		if (session == null) {
			return;
		}
		synchronized (session) {
			// sync on session avoids timer schedule and removing race condition
			this.cancelSessionTimeout(session);
			// clears message in cache if in loading state
			AppContext.getSCCache().clearLoading(session.getId());
			super.remove(session.getId());
		}
		SessionLogger.logDeleteSession(session.getId());
	}

	/**
	 * Removes the session.
	 *
	 * @param key the key
	 */
	public void removeSession(String key) {
		this.removeSession(this.getSession(key));
	}

	/**
	 * Gets the session.
	 *
	 * @param key the key
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
	 * @param session the session
	 */
	@SuppressWarnings("unchecked")
	private void scheduleSessionTimeout(Session session, double newTimeoutMillis) {
		if (session == null || newTimeoutMillis == 0) {
			// no scheduling of session timeout
			return;
		}
		// always cancel old timeouter before setting up a new one
		this.cancelSessionTimeout(session);
		// sets up session timeout
		TimeoutWrapper sessionTimeouter = new TimeoutWrapper(new SessionTimeout(session, session.getSessionTimeoutMillis()));
		// schedule sessionTimeouter in registry timer
		ScheduledFuture<TimeoutWrapper> timeout = (ScheduledFuture<TimeoutWrapper>) this.sessionScheduler.schedule(sessionTimeouter, (long) newTimeoutMillis,
				TimeUnit.MILLISECONDS);
		if (SessionLogger.isTraceEnabled()) {
			SessionLogger.logScheduleTimeout(session.getId(), newTimeoutMillis, timeout.getDelay(TimeUnit.MILLISECONDS));
		}
		session.setTimeout(timeout);
		session.setTimeouterTask(sessionTimeouter);
	}

	/**
	 * Cancel session timeout.
	 *
	 * @param session the session
	 */
	private void cancelSessionTimeout(Session session) {
		if (session == null) {
			return;
		}
		ScheduledFuture<TimeoutWrapper> sessionTimeout = session.getTimeout();
		if (sessionTimeout == null) {
			// no session timeout has been set up for this session
			return;
		}
		if (SessionLogger.isTraceEnabled()) {
			SessionLogger.logCancelTimeout(session.getId());
		}
		boolean cancelSuccess = sessionTimeout.cancel(false);
		if (cancelSuccess == false) {
			LOGGER.error("cancel of session timeout failed sid=" + session.getId() + " delay=" + sessionTimeout.getDelay(TimeUnit.MILLISECONDS) + " ms");
			boolean remove = this.sessionScheduler.remove(session.getTimeouterTask());
			if (remove == false) {
				LOGGER.error("remove of session timeout failed sid=" + session.getId() + " delay=" + sessionTimeout.getDelay(TimeUnit.MILLISECONDS) + " ms");
			}
		}
		this.sessionScheduler.purge();
		// important to set timeouter null - rescheduling of same instance not possible
		session.setTimeout(null);
	}

	/**
	 * Reset session timeout.
	 *
	 * @param session the session
	 * @param newTimeoutMillis the new timeout in milliseconds
	 */
	public void resetSessionTimeout(Session session, double newTimeoutMillis) {
		synchronized (session) {
			// sync on session avoids removing race condition
			if (this.containsKey(session.getId()) == false) {
				// session got deleted in meantime - don't schedule timer again
				return;
			}
			this.cancelSessionTimeout(session);
			this.scheduleSessionTimeout(session, newTimeoutMillis);
		}
	}

	/**
	 * The Class SessionTimeout. Gets control when a session times out. Responsible for cleaning up when session gets broken.
	 */
	private class SessionTimeout implements ITimeout {
		/** The session. */
		private Session session;
		/** The timeout. */
		private double timeoutMillis;

		/**
		 * Instantiates a new session timer run.
		 *
		 * @param session the session
		 */
		public SessionTimeout(Session session, double timeoutMillis) {
			this.session = session;
			this.timeoutMillis = session.getSessionTimeoutMillis();
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
			SessionLogger.logTimeoutSession(session);
		}

		/** {@inheritDoc} */
		@Override
		public int getTimeoutMillis() {
			return (int) this.timeoutMillis;
		}
	}

	/**
	 * Dump the sessions into the xml writer.
	 *
	 * @param writer the writer
	 * @throws Exception the exception
	 */
	public void dump(XMLDumpWriter writer) throws Exception {
		writer.writeStartElement("sessions");
		writer.writeAttribute("sessionScheduler_poolSize", this.sessionScheduler.getPoolSize());
		writer.writeAttribute("sessionScheduler_maximumPoolSize", this.sessionScheduler.getMaximumPoolSize());
		writer.writeAttribute("sessionScheduler_corePoolSize", this.sessionScheduler.getCorePoolSize());
		writer.writeAttribute("sessionScheduler_largestPoolSize", this.sessionScheduler.getLargestPoolSize());
		writer.writeAttribute("sessionScheduler_activeCount", this.sessionScheduler.getActiveCount());

		Set<Entry<String, Session>> sessionEntries = this.registryMap.entrySet();
		for (Entry<String, Session> sessionEntry : sessionEntries) {
			Session session = sessionEntry.getValue();
			session.dump(writer);
		}
		writer.writeEndElement(); // end of sessions
	}
}
