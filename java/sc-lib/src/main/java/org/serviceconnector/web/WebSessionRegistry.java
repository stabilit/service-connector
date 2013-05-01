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
package org.serviceconnector.web;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.registry.Registry;
import org.serviceconnector.util.ITimeout;
import org.serviceconnector.util.TimeoutWrapper;

/**
 * The Class WebSessionRegistry.
 */
public final class WebSessionRegistry extends Registry<String, WebSession> {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(WebSessionRegistry.class);
	
	/** The timer. Timer instance is responsible to observe session timeouts. */
	private ScheduledThreadPoolExecutor sessionScheduler;

	/**
	 * Instantiates a new web session registry.
	 */
	public WebSessionRegistry() {
		this.sessionScheduler = new ScheduledThreadPoolExecutor(1);
	}

	/**
	 * Create new session.
	 * 
	 * @return the i web session
	 */
	public WebSession createSession() {
		WebSession webSession = new WebSession();
		this.put(webSession.getId(), webSession);
		this.scheduleSessionTimeout(webSession);
		return webSession;
	}

	/**
	 * Gets the session.
	 * 
	 * @param sessionId
	 *            the session id
	 * @return the session
	 */
	public WebSession getSession(String sessionId) {
		WebSession webSession = this.get(sessionId);
		if (webSession != null) {
			this.scheduleSessionTimeout(webSession);
		}
		return webSession;
	}

	/**
	 * Removes the session.
	 * 
	 * @param session
	 *            the session
	 */
	public void removeSession(WebSession session) {
		if (session == null) {
			return;
		}
		this.cancelSessionTimeout(session);
		this.remove(session.getId());
	}

	/**
	 * Schedule session timeout.
	 * 
	 * @param session
	 *            the session
	 */
	@SuppressWarnings("unchecked")
	private void scheduleSessionTimeout(WebSession session) {
		if (session == null) {
			// no scheduling of session timeout
			return;
		}
		// always cancel old timeouter before setting up a new one
		this.cancelSessionTimeout(session);
		// sets up session timeout
		TimeoutWrapper sessionTimeouter = new TimeoutWrapper(new WebSessionTimeout(session));
		// schedule sessionTimeouter in registry timer
		ScheduledFuture<TimeoutWrapper> timeout = (ScheduledFuture<TimeoutWrapper>) this.sessionScheduler.schedule(
				sessionTimeouter, (long) session.getSessionTimeoutSeconds(), TimeUnit.SECONDS);
		session.setTimeout(timeout);
		session.setTimeouterTask(sessionTimeouter);
	}

	/**
	 * Cancel session timeout.
	 * 
	 * @param session
	 *            the session
	 */
	private void cancelSessionTimeout(WebSession session) {
		if (session == null) {
			return;
		}
		ScheduledFuture<TimeoutWrapper> sessionTimeout = session.getTimeout();
		if (sessionTimeout == null) {
			// no session timeout has been set up for this session
			return;
		}
		boolean cancelSuccess = sessionTimeout.cancel(false);
		if (cancelSuccess == false) {
			LOGGER.error("cancel web session timeout failed sid=" + session.getId() + " delay="
					+ sessionTimeout.getDelay(TimeUnit.MILLISECONDS)+" ms");
			boolean remove = this.sessionScheduler.remove(session.getTimeouterTask());
			if (remove == false) {
				LOGGER.error("remove web session timeout failed sid=" + session.getId() + " delay="
						+ sessionTimeout.getDelay(TimeUnit.MILLISECONDS)+" ms");
			}
		}
		this.sessionScheduler.purge();
		// important to set timeouter null - rescheduling of same instance not possible
		session.setTimeout(null);
	}

	/**
	 * The Class WebSessionTimeout. Gets control when a session times out. Responsible for cleaning up when session gets broken.
	 */
	private class WebSessionTimeout implements ITimeout {
		/** The session. */
		private WebSession session;
		/** The timeout. */
		private double timeoutSeconds;

		/**
		 * Instantiates a new session timer run.
		 * 
		 * @param session
		 *            the session
		 */
		public WebSessionTimeout(WebSession session) {
			this.session = session;
			this.timeoutSeconds = session.getSessionTimeoutSeconds();
		}

		/**
		 * Timeout. Session timeout run out.
		 */
		@Override
		public void timeout() {
			WebSessionRegistry.this.removeSession(session);
		}

		/** {@inheritDoc} */
		@Override
		public int getTimeoutMillis() {
			return (int) (this.timeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		}
	}
}
