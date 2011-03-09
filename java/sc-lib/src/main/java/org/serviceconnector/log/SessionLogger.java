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
public class SessionLogger {

	/** The Constant sessionLogger. */
	private static final Logger sessionLogger = Logger.getLogger(Loggers.SESSION.getValue());

	/** The CREAT e_ sessio n_ str. */
	private static String CREATE_SESSION_STR = "create session sid=%s eci=%s";
	
	/** The DELET e_ sessio n_ str. */
	private static String DELETE_SESSION_STR = "delete session:%s";
	
	/** The ABOR t_ sessio n_ str. */
	private static String ABORT_SESSION_STR = "abort session:%s";
	
	/** The TIMEOU t_ sessio n_ str. */
	private static String TIMEOUT_SESSION_STR = "timeout session:%s";

	/**
	 * Private constructor for singleton use.
	 */
	private SessionLogger() {
	}

	/**
	 * Log create session.
	 * 
	 * @param className
	 *            the class name
	 * @param sessionId
	 *            the session id
	 * @param eci
	 *            the eci
	 */
	public static synchronized void logCreateSession(String className, String sessionId, double eci) {
		if (sessionLogger.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(CREATE_SESSION_STR, sessionId, eci);
			sessionLogger.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Log delete session.
	 * 
	 * @param className
	 *            the class name
	 * @param sessionId
	 *            the session id
	 */
	public static synchronized void logDeleteSession(String className, String sessionId) {
		if (sessionLogger.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(DELETE_SESSION_STR, sessionId);
			sessionLogger.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Log timeout session.
	 * 
	 * @param className
	 *            the class name
	 * @param sessionId
	 *            the session id
	 */
	public static synchronized void logTimeoutSession(String className, String sessionId) {
		if (sessionLogger.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(TIMEOUT_SESSION_STR, sessionId);
			sessionLogger.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Log abort session.
	 * 
	 * @param className
	 *            the class name
	 * @param sessionId
	 *            the session id
	 */
	public static synchronized void logAbortSession(String className, String sessionId) {
		if (sessionLogger.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(ABORT_SESSION_STR, sessionId);
			sessionLogger.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Checks if is enabled.
	 * 
	 * @return true, if is enabled
	 */
	public static boolean isEnabled() {
		return sessionLogger.isTraceEnabled();
	}

	/**
	 * Warn.
	 * 
	 * @param message
	 *            the message
	 */
	public static void warn(String message) {
		if (sessionLogger.isEnabledFor(Level.WARN)) {
			sessionLogger.warn(message);
		}
	}

	/**
	 * Fatal.
	 * 
	 * @param message
	 *            the message
	 */
	public static void fatal(String message) {
		if (sessionLogger.isEnabledFor(Level.FATAL)) {
			sessionLogger.fatal(message);
		}
	}

	/**
	 * Error.
	 * 
	 * @param message
	 *            the message
	 */
	public static void error(String message) {
		if (sessionLogger.isEnabledFor(Level.ERROR)) {
			sessionLogger.error(message);
		}
	}

	/**
	 * Debug.
	 * 
	 * @param message
	 *            the message
	 */
	public static void debug(String message) {
		if (sessionLogger.isEnabledFor(Level.DEBUG)) {
			sessionLogger.debug(message);
		}
	}
}
