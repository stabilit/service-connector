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

import com.stabilit.scm.common.conf.IConstants;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.listener.SessionPoint;
import com.stabilit.scm.common.registry.Registry;
import com.stabilit.scm.common.util.ITimerRun;
import com.stabilit.scm.common.util.TimerTaskWrapper;
import com.stabilit.scm.sc.service.Session;

/**
 * The Class SessionRegistry. Registry stores entries for properly created sessions.
 * 
 * @author JTraber
 */
public class SessionRegistry extends Registry {

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

	public void addSession(Object key, Session session) {
		SessionPoint.getInstance().fireCreate(this, session.getId());
		this.put(key, session);
		if (session.getEchoInterval() != 0) {
			// session timeout necessary needs to be set up
			this.scheduleSessionTimeout(session);
		}
	}

	public void removeSession(Session session) {
		this.removeSession(session.getId());
	}

	public void removeSession(Object key) {
		Session session = (Session) super.get(key);
		this.cancelSessionTimeout(session);
		super.remove(key);
		SessionPoint.getInstance().fireDelete(this, (String) key);
	}

	/**
	 * Gets the session. Session timeout resets if session is requested.
	 * 
	 * @param key
	 *            the key
	 * @return the session
	 */
	// TODO verify rescheduling timeout at this point
	public Session getSession(Object key) {
		Session session = (Session) super.get(key);
		if (session != null && session.getEchoInterval() != 0) {
			// rescheduling session timeout
			this.cancelSessionTimeout(session);
			this.scheduleSessionTimeout(session);
		}
		return session;
	}

	private void scheduleSessionTimeout(Session session) {
		TimerTaskWrapper sessionTimeouter = session.getSessionTimeouter();
		if (sessionTimeouter == null) {
			// sets up session timeout
			sessionTimeouter = new TimerTaskWrapper(new SessionTimerRun(session));
			session.setSessionTimouter(sessionTimeouter);
		}
		// schedule sessionTimeouter in registry timer
		this.timer.schedule(sessionTimeouter, session.getEchoInterval() * IConstants.SEC_TO_MILISEC_FACTOR);
	}

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
		// very important to set timeouter null - rescheduling of same instance not possible
		session.setSessionTimouter(null);
	}

	private class SessionTimerRun implements ITimerRun {

		private Session session;
		private TimerTask timerTask;

		public SessionTimerRun(Session session) {
			this.session = session;
			this.session.setTimerRun(this);
			this.timerTask = null;
		}

		@Override
		public void timeout() {
			// cancel timer
			SessionRegistry.this.cancelSessionTimeout(session);
			// TODO abort session clean up
			// we assume that this session is dead
			LoggerPoint.getInstance().fireWarn(session, "session [" + session.getId() + "] aborted");
			SessionPoint.getInstance().fireAbort(session, session.getId());
		}

		@Override
		public TimerTask getTimerTask() {
			return this.timerTask;
		}

		@Override
		public void setTimerTask(TimerTask timerTask) {
			this.timerTask = timerTask;
		}

		@Override
		public int getTimeout() {
			return 0;
		}
	}
}
