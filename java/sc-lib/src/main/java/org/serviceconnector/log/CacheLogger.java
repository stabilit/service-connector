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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * The Class CacheLogger.
 */
public final class CacheLogger {

	/** The Constant cacheLogger. */
	private static final Logger CACHE_LOGGER = Logger.getLogger(Loggers.CACHE.getValue());

	/**
	 * Private constructor for singleton use.
	 */
	private CacheLogger() {
	}

	/**
	 * Checks if is enabled.
	 * 
	 * @return true, if is enabled
	 */
	public static boolean isEnabled() {
		return CACHE_LOGGER.isTraceEnabled();
	}

	/**
	 * Debug.
	 * 
	 * @param message
	 *            the message
	 */
	public static void debug(String message) {
		if (CACHE_LOGGER.isDebugEnabled()) {
			CACHE_LOGGER.debug(message);
		}
	}

	/**
	 * Warn.
	 * 
	 * @param message
	 *            the message
	 */
	public static void warn(String message) {
		if (CACHE_LOGGER.isEnabledFor(Level.WARN)) {
			CACHE_LOGGER.warn(message);
		}
	}

	/**
	 * Info.
	 * 
	 * @param message
	 *            the message
	 */
	public static void info(String message) {
		if (CACHE_LOGGER.isEnabledFor(Level.INFO)) {
			CACHE_LOGGER.info(message);
		}
	}

	/**
	 * Error.
	 * 
	 * @param message
	 *            the message
	 */
	public static void error(String message) {
		if (CACHE_LOGGER.isEnabledFor(Level.ERROR)) {
			CACHE_LOGGER.error(message);
		}
	}

	/**
	 * Error.
	 * 
	 * @param message
	 *            the message
	 * @param e
	 *            the e
	 */
	public static void error(String message, Exception e) {
		if (CACHE_LOGGER.isEnabledFor(Level.ERROR)) {
			CACHE_LOGGER.error(message, e);
		}
	}
}
