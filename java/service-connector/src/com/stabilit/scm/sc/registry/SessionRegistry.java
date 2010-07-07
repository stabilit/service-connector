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
	private Timer timer;

	/**
	 * Instantiates a new session registry.
	 */
	public SessionRegistry() {
		this.timer = new Timer("SessionRegistry");
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
		this.scheduleTask(session);
	}

	public void removeSession(Session session) {
		this.removeSession(session.getId());
	}

	public void removeSession(Object key) {
		Session session = (Session) super.get(key);
		this.cancelTask(session);
		super.remove(key);
		SessionPoint.getInstance().fireDelete(this, (String) key);
	}

	public Session getSession(Object key) {
		Session session = (Session) super.get(key);
		this.rescheduleTask(session);
		return session;
	}

	private void scheduleTask(Session session) {
		if (session == null || session.getEchoInterval() == 0) {
			return;
		}
		ITimerRun timerRun = session.getTimerRun();
		if (timerRun == null) {
			timerRun = new SessionTimerRun(session); // sets timer task inside session too
		}
		this.timer.schedule(new TimerTaskWrapper(timerRun), session.getEchoInterval() * 1000);
	}

	private void rescheduleTask(Session session) {
		this.cancelTask(session);
		this.scheduleTask(session);
	}

	private void cancelTask(Session session) {
		if (session == null) {
			return;
		}
		ITimerRun timerRun = session.getTimerRun();
		if (timerRun != null) {
			if (timerRun.getTimerTask() != null) {
				timerRun.getTimerTask().cancel();
				timerRun.setTimerTask(null);
			}
		}
	}

	class SessionTimerRun implements ITimerRun {

		private Session session;
		private TimerTask timerTask;

		public SessionTimerRun(Session session) {
			this.session = session;
			this.session.setTimerRun(this);
			this.timerTask = null;
		}

		@Override
		public void run() {
			// cancel timer
			SessionRegistry.this.cancelTask(session);
			// TODO abort session
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
