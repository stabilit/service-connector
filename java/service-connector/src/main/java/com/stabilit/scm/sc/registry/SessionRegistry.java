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

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.listener.SessionPoint;
import com.stabilit.scm.common.log.Loggers;
import com.stabilit.scm.common.registry.Registry;
import com.stabilit.scm.common.scmp.ISCMPCallback;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.util.ITimerRun;
import com.stabilit.scm.common.util.TimerTaskWrapper;
import com.stabilit.scm.sc.service.Server;
import com.stabilit.scm.sc.service.Session;

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
	protected final static Logger sessionLogger = Logger.getLogger(Loggers.SESSION.getValue());
	
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
		SessionPoint.getInstance().fireCreate(this, session.getId());
		sessionLogger.info("new session [" + session.getId() + "]");
		this.put(key, session);
		if (session.getEchoIntervalSeconds() != 0) {
			// session timeout necessary needs to be set up
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
		this.cancelSessionTimeout(session);
		super.remove(key);
		sessionLogger.info("end session [" + key + "]");
		SessionPoint.getInstance().fireDelete(this, (String) key);
	}

	/**
	 * Gets the session. Session timeout resets if session is requested.
	 * 
	 * @param key
	 *            the key
	 * @return the session
	 */
	public Session getSession(String key) {
		Session session = super.get(key);
		if (session != null && session.getEchoIntervalSeconds() != 0) {
			// rescheduling session timeout - cancel an old timeouter is done inside
			this.scheduleSessionTimeout(session);
		}
		return session;
	}

	/**
	 * Schedule session timeout.
	 * 
	 * @param session
	 *            the session
	 */
	private void scheduleSessionTimeout(Session session) {
		// always cancel old timeouter before setting up a new one
		this.cancelSessionTimeout(session);
		TimerTaskWrapper sessionTimeouter = session.getSessionTimeouter();

		// sets up session timeout
		sessionTimeouter = new TimerTaskWrapper(new SessionTimerRun(session));
		session.setSessionTimeouter(sessionTimeouter);
		// schedule sessionTimeouter in registry timer
		this.timer.schedule(sessionTimeouter, session.getEchoIntervalSeconds() * Constants.SEC_TO_MILISEC_FACTOR);
	}

	/**
	 * Cancel session timeout.
	 * 
	 * @param session
	 *            the session
	 */
	private void cancelSessionTimeout(Session session) {
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
	 * The Class SessionTimerRun. Gets control when a session times out. Responsible for cleaning up when sessin gets
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
			server.serverAbortSession(abortMessage, callback, Constants.OPERATION_TIMEOUT_MILLIS_SHORT);
			// removes session on server
			session.getServer().removeSession(session);
			logger.warn("session [" + session.getId() + "] aborted");
			SessionPoint.getInstance().fireAbort(session, session.getId());
		}

		/** {@inheritDoc} */
		@Override
		public int getTimeoutSeconds() {
			return this.timeoutSeconds;
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