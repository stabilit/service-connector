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
package org.serviceconnector.log;

import java.util.Date;
import java.util.Formatter;

import org.apache.log4j.Logger;
import org.serviceconnector.service.Session;

/**
 * The Class SessionLogger.
 */
public final class SessionLogger {

	/** The Constant sessionLogger. */
	private static final Logger SESSION_LOGGGER = Logger.getLogger(Loggers.SESSION.getValue());

	/** The create session string. */
	private static String createSessionStr = "create session sid=%s eci=%sms";
	/** The delete session string. */
	private static String deleteSessionStr = "delete session sid=%s";
	/** The abort session string. */
	private static String abortSessionStr = "abort session sid=%s eci=%sms creationTime=%3$tH:%3$tM:%3$tS.%3$tL lastExecuteTime=%4$tH:%4$tM:%4$tS.%4$tL reason=%5$s";
	/** The schedule timeout string. */
	private static String scheduleTimeoutStr = "schedule session sid=%s, timeout=%sms, delay=%sms";
	/** The cancel timeout string. */
	private static String cancelTimeoutStr = "cancel timeout session sid=%s";
	/** The timeout session string. */
	private static String timeoutSessionStr = "timeout session sid=%s eci=%sms creationTime=%3$tH:%3$tM:%3$tS.%3$tL lastExecuteTime=%4$tH:%4$tM:%4$tS.%4$tL";
	/** The reject session string. */
	private static String rejectSessionStr = "reject session sid=%s";

	/**
	 * Private constructor for singleton use.
	 */
	private SessionLogger() {
	}

	/**
	 * Checks if is trace enabled.
	 * 
	 * @return true, if is trace enabled
	 */
	public static boolean isTraceEnabled() {
		return SESSION_LOGGGER.isTraceEnabled();
	}

	/**
	 * Checks if is debug enabled.
	 * 
	 * @return true, if is debug enabled
	 */
	public static boolean isDebugEnabled() {
		return SESSION_LOGGGER.isDebugEnabled();
	}

	/**
	 * Log create session.
	 * 
	 * @param sessionId
	 *            the session id
	 * @param eci
	 *            the eci
	 */
	public static synchronized void logCreateSession(String sessionId, double eci) {
		if (SESSION_LOGGGER.isDebugEnabled()) {
			Formatter format = new Formatter();
			format.format(createSessionStr, sessionId, eci);
			SESSION_LOGGGER.debug(format.toString());
			format.close();
		}
	}

	/**
	 * Log delete session.
	 * 
	 * @param sessionId
	 *            the session id
	 */
	public static synchronized void logDeleteSession(String sessionId) {
		if (SESSION_LOGGGER.isDebugEnabled()) {
			Formatter format = new Formatter();
			format.format(deleteSessionStr, sessionId);
			SESSION_LOGGGER.debug(format.toString());
			format.close();
		}
	}

	/**
	 * Log start timeout scheduling.
	 * 
	 * @param sessionId
	 *            the session id
	 */
	public static synchronized void logScheduleTimeout(String sessionId, double timeout, long delay) {
		if (SESSION_LOGGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(scheduleTimeoutStr, sessionId, timeout, delay);
			SESSION_LOGGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Log cancel timeout.
	 * 
	 * @param sessionId
	 *            the session id
	 */
	public static synchronized void logCancelTimeout(String sessionId) {
		if (SESSION_LOGGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(cancelTimeoutStr, sessionId);
			SESSION_LOGGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Log timeout session.
	 * 
	 * @param session
	 *            the session
	 */
	public static synchronized void logTimeoutSession(Session session) {
		if (SESSION_LOGGGER.isInfoEnabled()) {
			Formatter format = new Formatter();
			String sessionId = session.getId();
			double eci = session.getSessionTimeoutMillis();
			Date creationTime = session.getCreationTime();
			Date lastExecuteTime = session.getLastExecuteTime();
			format.format(timeoutSessionStr, sessionId, eci, creationTime, lastExecuteTime);
			SESSION_LOGGGER.info(format.toString());
			format.close();
		}
	}

	/**
	 * Log abort session.
	 * 
	 * @param session
	 *            the session
	 * @param reason
	 *            the reason
	 */
	public static synchronized void logAbortSession(Session session, String reason) {
		if (SESSION_LOGGGER.isInfoEnabled()) {
			Formatter format = new Formatter();
			String sessionId = session.getId();
			double eci = session.getSessionTimeoutMillis();
			Date creationTime = session.getCreationTime();
			Date lastExecuteTime = session.getLastExecuteTime();
			format.format(abortSessionStr, sessionId, eci, creationTime, lastExecuteTime, reason);
			SESSION_LOGGGER.info(format.toString());
			format.close();
		}
	}

	/**
	 * Log reject session.
	 * 
	 * @param sessionId
	 *            the session id
	 */
	public static synchronized void logRejectSession(String sessionId) {
		if (SESSION_LOGGGER.isDebugEnabled()) {
			Formatter format = new Formatter();
			format.format(rejectSessionStr, sessionId);
			SESSION_LOGGGER.debug(format.toString());
			format.close();
		}
	}
}
