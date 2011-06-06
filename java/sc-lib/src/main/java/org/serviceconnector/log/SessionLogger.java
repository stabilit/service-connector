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

import java.util.Formatter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

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
	private static String abortSessionStr = "abort session sid=%s";
	/** The timeout session string. */
	private static String timeoutSessionStr = "timeout session sid=%s";
	/** The timeout reject session string. */
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
	 * Log timeout session.
	 * 
	 * @param sessionId
	 *            the session id
	 */
	public static synchronized void logTimeoutSession(String sessionId) {
		if (SESSION_LOGGGER.isDebugEnabled()) {
			Formatter format = new Formatter();
			format.format(timeoutSessionStr, sessionId);
			SESSION_LOGGGER.debug(format.toString());
			format.close();
		}
	}

	/**
	 * Log abort session.
	 * 
	 * @param sessionId
	 *            the session id
	 */
	public static synchronized void logAbortSession(String sessionId) {
		if (SESSION_LOGGGER.isDebugEnabled()) {
			Formatter format = new Formatter();
			format.format(abortSessionStr, sessionId);
			SESSION_LOGGGER.debug(format.toString());
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

	/**
	 * Warn.
	 * 
	 * @param message
	 *            the message
	 */
	public static void warn(String message) {
		if (SESSION_LOGGGER.isEnabledFor(Level.WARN)) {
			SESSION_LOGGGER.warn(message);
		}
	}

	/**
	 * Error.
	 * 
	 * @param message
	 *            the message
	 */
	public static void error(String message) {
		if (SESSION_LOGGGER.isEnabledFor(Level.ERROR)) {
			SESSION_LOGGGER.error(message);
		}
	}

	/**
	 * Fatal.
	 * 
	 * @param message
	 *            the message
	 */
	public static void fatal(String message) {
		if (SESSION_LOGGGER.isEnabledFor(Level.FATAL)) {
			SESSION_LOGGGER.fatal(message);
		}
	}

	/**
	 * Debug.
	 * 
	 * @param message
	 *            the message
	 */
	public static void debug(String message) {
		if (SESSION_LOGGGER.isEnabledFor(Level.DEBUG)) {
			SESSION_LOGGGER.debug(message);
		}
	}

	/**
	 * Trace.
	 * 
	 * @param message
	 *            the message
	 */
	public static void trace(String message) {
		if (SESSION_LOGGGER.isTraceEnabled()) {
			SESSION_LOGGGER.trace(message);
		}
	}
}
