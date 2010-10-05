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

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.log.SessionLogger;
import org.serviceconnector.scmp.ISCMPCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.Server;
import org.serviceconnector.service.Session;
import org.serviceconnector.util.ITimerRun;
import org.serviceconnector.util.TimerTaskWrapper;

/**
 * The Class SessionRegistry. Registry stores entries for properly created sessions. Registry is also responsible for
 * observing the session timeout and initiating clean up in case of a broken session.
 * 
 * @author JTraber
 */
public class SessionRegistry extends Registry<String, Session> {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SessionRegistry.class);

	/** The Constant sessionLogger. */
	private final static SessionLogger sessionLogger = SessionLogger.getInstance();

	/** The instance. */
	private static SessionRegistry instance = new SessionRegistry();
	/** The timer. Timer instance is responsible to observe session timeouts. */
	private Timer timer;

	/**
	 * Instantiates a SessionRegistry.
	 */
	public SessionRegistry() {
		this.timer = new Timer("SessionRegistryTimer");
	}

	/**
	 * Gets the current instance.
	 * 
	 * @return the current instance
	 */
	public static SessionRegistry getCurrentInstance() {
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
		if (session.getEchoIntervalSeconds() != 0) {
			// TODO TRN handle = session timeout necessary needs to be set up
			this.scheduleSessionTimeout(session);
		}
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
		this.cancelSessionTimeout(session);
		super.remove(key);
		sessionLogger.logDeleteSession(this.getClass().getName(), session.getId());
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
	 * Schedule session timeout.
	 * 
	 * @param session
	 *            the session
	 */
	public void scheduleSessionTimeout(Session session) {
		if (session == null || session.getEchoIntervalSeconds() == 0) {
			// no scheduling of session timeout
			return;
		}
		// always cancel old timeouter before setting up a new one
		this.cancelSessionTimeout(session);
		TimerTaskWrapper sessionTimeouter = session.getSessionTimeouter();

		// sets up session timeout
		sessionTimeouter = new TimerTaskWrapper(new SessionTimerRun(session));
		session.setSessionTimeouter(sessionTimeouter);
		// schedule sessionTimeouter in registry timer
		this.timer.schedule(sessionTimeouter, session.getEchoIntervalSeconds() * Constants.SEC_TO_MILLISEC_FACTOR);
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
		TimerTask sessionTimeouter = session.getSessionTimeouter();
		if (sessionTimeouter == null) {
			// no session timeout has been set up for this session
			return;
		}
		sessionTimeouter.cancel();
		// important to set timeouter null - rescheduling of same instance not possible
		session.setSessionTimeouter(null);
	}

	/**
	 * The Class SessionTimerRun. Gets control when a session times out. Responsible for cleaning up when session gets
	 * broken.
	 */
	private class SessionTimerRun implements ITimerRun {

		/** Error text in case of a session abortion. */
		private static final String ABORT_SESSION_ERROR_STRING = "session timed out";
		/** The session. */
		private Session session;
		/** The timeout. */
		private int timeoutSeconds;
		/** The callback, callback to send abort session. */
		private ISCMPCallback callback;
		/** The abort message, message to send to server in case of a session abortion. */
		private SCMPMessage abortMessage;

		/**
		 * Instantiates a new session timer run.
		 * 
		 * @param session
		 *            the session
		 */
		public SessionTimerRun(Session session) {
			this.session = session;
			this.timeoutSeconds = session.getEchoIntervalSeconds();
			this.callback = new SessionTimerRunCallback();
			this.abortMessage = new SCMPMessage();
			this.abortMessage.setHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE, SCMPError.SESSION_ABORT.getErrorCode());
			this.abortMessage.setHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT, ABORT_SESSION_ERROR_STRING);
		}

		/**
		 * Timeout. Session timeout run out.
		 */
		@Override
		public void timeout() {
			/**
			 * broken session procedure<br>
			 * 1. remove session from session registry<br>
			 * 2. abort session on backend server<br>
			 * 3. remove session from server<br>
			 */
			SessionRegistry.this.removeSession(session);
			Server server = session.getServer();
			// aborts session on server
			abortMessage.setServiceName(server.getServiceName());
			abortMessage.setSessionId(session.getId());
			server.serverAbortSession(abortMessage, callback, Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS
					* Constants.SEC_TO_MILLISEC_FACTOR);
			// removes session on server
			session.getServer().removeSession(session);
			SessionLogger sessionLogger = SessionLogger.getInstance();
			sessionLogger.logAbortSession(this.getClass().getName(), session.getId());
		}

		/** {@inheritDoc} */
		@Override
		public int getTimeoutMillis() {
			return this.timeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR;
		}

		/**
		 * The Class SessionTimerRunCallback. For abort session callback is irrelevant. Nobody is going to wait/evaluate
		 * for the response.
		 */
		private class SessionTimerRunCallback implements ISCMPCallback {

			@Override
			public void callback(SCMPMessage scmpReply) throws Exception {
				// nothing to do in callback
			}

			@Override
			public void callback(Exception ex) {
				// nothing to do in callback
			}
		}
	}
}