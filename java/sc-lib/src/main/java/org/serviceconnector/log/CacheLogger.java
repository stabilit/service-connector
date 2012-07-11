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
	/** The start loading string. */
	private static String startLoadingStr = "start loading message cid=%s sid=%s timeout=%sms";
	/** The start caching appendix string. */
	private static String startCachingAppStr = "start caching appendix cid=%s cacheGuardian=%s metaEntryTimeout=%ssec";
	/** The caching appendix string. */
	private static String cachingAppStr = "cache appendix cid=%s cacheGuardian=%s";
	/** The try loading string. */
	private static String tryGetMsgStr = "try to get message from cache cid=%s sid=%s cpn=%s anr=%s";
	/** The got message string. */
	private static String gotMessageStr = "got message from cache cid=%s sid=%s length=%s";
	/** The finish loading string. */
	private static String finishLoadingStr = "finish loading message cacheId=%s sid=%s numberOfParts=%s numberOfAppendix=%s";
	/** The finish caching appendix string. */
	private static String finishCachingAppStr = "finish caching appendix cid=%s cacheGuardian=%s numberOfParts=%s";
	/** The abort loading string. */
	private static String abortLoadingStr = "abort loading message cid=%s sid=%s";
	/** The put message string. */
	private static String putMessageStr = "put message into cache cid=%s nrOfParts=%s loading sid=%s length=%s loadingState=%s cmt=%s";
	/** The put managed data string. */
	private static String putManagedDataStr = "put managed data into cache cacheId=%s cacheGuardian=%s appendixNr=%s partNr=%s";
	/** The remove message from cache string. */
	private static String removeMsgFromCacheStr = "remove message from cache cid=%s reason=%s";
	/** The message expired string. */
	private static String msgExpiredStr = "message expired cid=%s";
	/** The clear cache string. */
	private static String clearCacheStr = "clear cache, all messages removed.";

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
	 * Try to get message from cache.
	 * 
	 * @param cacheId
	 *            the cache identifier
	 * @param sessionId
	 *            the session id
	 * @param requestedPart
	 *            the requested part
	 * @param appendixNr
	 *            the appendix number
	 */
	public static void tryGetMessageFromCache(String cacheId, String sessionId, String requestedPart, String appendixNr) {
		if (CACHE_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(tryGetMsgStr, cacheId, sessionId, requestedPart, appendixNr);
			CACHE_LOGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Got message from cache.
	 * 
	 * @param cacheId
	 *            the cache identifier
	 * @param sessionId
	 *            the session id
	 */
	public static void gotMessageFromCache(String cacheId, String sessionId, int length) {
		if (CACHE_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(gotMessageStr, cacheId, sessionId, length);
			CACHE_LOGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Start loading cache message.
	 * 
	 * @param cacheId
	 *            the cache identifier
	 * @param sessionId
	 *            the session id
	 * @param timeout
	 *            the loading timeout in milliseconds
	 */
	public static void startLoadingCacheMessage(String cacheId, String sessionId, int timeout) {
		if (CACHE_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(startLoadingStr, cacheId, sessionId, timeout);
			CACHE_LOGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Start caching appendix.
	 * 
	 * @param cacheId
	 *            the cache id
	 * @param cacheGuardian
	 *            the cache guardian
	 * @param timeout
	 *            the timeout
	 */
	public static void startCachingAppendix(String cacheId, String cacheGuardian, int timeout) {
		if (CACHE_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(startCachingAppStr, cacheId, cacheGuardian, timeout);
			CACHE_LOGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Cache appendix.
	 * 
	 * @param cacheId
	 *            the cache id
	 * @param cacheGuardian
	 *            the cache guardian name
	 */
	public static void cacheAppendix(String cacheId, String cacheGuardian) {
		if (CACHE_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(cachingAppStr, cacheId, cacheGuardian);
			CACHE_LOGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Finish loading cache message.
	 * 
	 * @param cacheId
	 *            the cache identifier
	 * @param sessionId
	 *            the session id
	 * @param numberOfParts
	 *            the number of parts
	 * @param numberOfAppendix
	 *            the number of appendix
	 */
	public static void finishLoadingCacheMessage(String cacheId, String sessionId, int numberOfParts, int numberOfAppendix) {
		if (CACHE_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(finishLoadingStr, cacheId, sessionId, numberOfParts, numberOfAppendix);
			CACHE_LOGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Finish caching appendix.
	 * 
	 * @param cacheId
	 *            the cache identifier
	 * @param cacheGuardian
	 *            the cache guardian name
	 * @param numberOfParts
	 *            the number of parts
	 */
	public static void finishCachingAppendix(String cacheId, String cacheGuardian, int numberOfParts) {
		if (CACHE_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(finishCachingAppStr, cacheId, cacheGuardian, numberOfParts);
			CACHE_LOGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Abort loading message.
	 * 
	 * @param cacheId
	 *            the cache identifier
	 * @param sessionId
	 *            the session id
	 */
	public static void abortLoadingMessage(String cacheId, String sessionId) {
		if (CACHE_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(abortLoadingStr, cacheId, sessionId);
			CACHE_LOGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Put message to cache.
	 * 
	 * @param cacheId
	 *            the cache identifier
	 * @param loadingSessionId
	 *            the loading session id
	 */
	public static void putMessageToCache(String cacheId, int nrOfParts, String loadingSessionId, int length, String loadingState,
			String cmt) {
		if (CACHE_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(putMessageStr, cacheId, nrOfParts, loadingSessionId, length, loadingState, cmt);
			CACHE_LOGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Put appendix part to cache.
	 * 
	 * @param cacheId
	 *            the cache identifier
	 * @param cacheGuardian
	 *            the cache guardian
	 * @param nrOfAppendix
	 *            the number of appendix
	 * @param appendixNr
	 *            the appendix number
	 * @param partNr
	 *            the part number
	 */
	public static void putManagedDataToCache(String cacheId, String cacheGuardian, int appendixNr, int partNr) {
		if (CACHE_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(putManagedDataStr, cacheId, cacheGuardian, appendixNr, partNr);
			CACHE_LOGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Removes the message from cache.
	 * 
	 * @param cid
	 *            the cache identifier
	 */
	public static void removeMessageFromCache(String cid, String reason) {
		if (CACHE_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(removeMsgFromCacheStr, cid, reason);
			CACHE_LOGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Message expired.
	 * 
	 * @param cid
	 *            the cache identifier
	 */
	public static void messageExpired(String cid) {
		if (CACHE_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(msgExpiredStr, cid);
			CACHE_LOGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Clear cache.
	 */
	public static void clearCache() {
		if (CACHE_LOGGER.isTraceEnabled()) {
			CACHE_LOGGER.trace(clearCacheStr);
		}
	}
}