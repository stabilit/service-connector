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
import org.serviceconnector.cache.CacheException;

public class CacheLogger {

	private static final Logger cacheLogger = Logger.getLogger(Loggers.CACHE.getValue());

	/**
	 * Private constructor for singleton use.
	 */
	private CacheLogger() {
	}

	/**
	 * @return
	 */
	public static boolean isEnabled() {
		return cacheLogger.isTraceEnabled();
	}

	public static void debug(String message) {
		if (cacheLogger.isDebugEnabled()) {
			cacheLogger.debug(message);
		}
	}

	public static void warn(String message) {
		if (cacheLogger.isEnabledFor(Level.WARN)) {
			cacheLogger.warn(message);
		}
	}

	public static void info(String message) {
		if (cacheLogger.isEnabledFor(Level.INFO)) {
			cacheLogger.info(message);
		}
	}

	public static void error(String message, Exception e) {
		if (cacheLogger.isEnabledFor(Level.ERROR)) {
			cacheLogger.error(message, e);
		}
	}
}
