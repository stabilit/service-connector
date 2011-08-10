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

import org.apache.log4j.Logger;

/**
 * The Class CacheLogger.
 */
public final class CacheLogger {

	/** The Constant cacheLogger. */
	private static final Logger CACHE_LOGGER = Logger.getLogger(Loggers.CACHE.getValue());
	/** The start loading str. */
	private static String startLoadingStr = "start loading cache message cid=%s sid=%s oti=%sms";
	/** The put message str. */
	private static String putMessageStr = "put message to cache cid=%s loading sid=%s expiration=%s parntNr=%s";
	/** The stop loading str. */
	private static String stopLoadingStr = "stop loading message complete cid=%s sid=%s";
	/** The abort loading str. */
	private static String abortLoadingStr = "abort loading message cid=%s sid=%s";
	/** The remove msg from cache str. */
	private static String removeMsgFromCacheStr = "remove message from cache cid=%s sid=%s";

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
	 * Start loading cache message.
	 * 
	 * @param cacheId
	 *            the cache id
	 * @param sessionId
	 *            the session id
	 * @param otiMillis
	 *            the oti millis
	 */
	public static void startLoadingCacheMessage(String cacheId, String sessionId, int otiMillis) {
		if (CACHE_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(startLoadingStr, cacheId, sessionId, otiMillis);
			CACHE_LOGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Stop loading cache message.
	 * 
	 * @param cacheId
	 *            the cache id
	 * @param sessionId
	 *            the session id
	 */
	public static void stopLoadingCacheMessage(String cacheId, String sessionId) {
		if (CACHE_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(stopLoadingStr, cacheId, sessionId);
			CACHE_LOGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Abort loading message.
	 * 
	 * @param cacheKey
	 *            the cache id
	 * @param sessionId
	 *            the session id
	 */
	public static void abortLoadingMessage(String cacheKey, String sessionId) {
		if (CACHE_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(abortLoadingStr, cacheKey, sessionId);
			CACHE_LOGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Put message to cache.
	 * 
	 * @param cacheId
	 *            the cache id
	 * @param loadingSessionId
	 *            the loading session id
	 * @param expiration
	 *            the expiration
	 * @param partNr
	 *            the part nr
	 */
	public static void putMessageToCache(String cacheId, String loadingSessionId, String expiration, int partNr) {
		if (CACHE_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(putMessageStr, cacheId, loadingSessionId, expiration, partNr);
			CACHE_LOGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Removes the message from cache.
	 * 
	 * @param cacheId
	 *            the cache id
	 * @param sessionId
	 *            the session id
	 */
	public static void removeMessageFromCache(String cacheId, String sessionId) {
		if (CACHE_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(removeMsgFromCacheStr, cacheId, sessionId);
			CACHE_LOGGER.trace(format.toString());
			format.close();
		}
	}
}