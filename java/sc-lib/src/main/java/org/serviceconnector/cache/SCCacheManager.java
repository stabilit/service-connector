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
package org.serviceconnector.cache;

import java.io.File;
import java.io.FileFilter;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cache.ehcache.SCCacheFactory;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.conf.SCCacheConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.CacheLogger;
import org.serviceconnector.registry.CacheRegistry;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.util.DateTimeUtility;
import org.serviceconnector.util.Statistics;
import org.serviceconnector.util.ValidatorUtility;
import org.serviceconnector.util.XMLDumpWriter;

/**
 * The Class SCCacheManager. The cache manager is responsible for handling the cache in the Service Connector. The AppContext gives
 * access to the SC cache manager instance.
 * The manager controls the cache life cycles. Loading and destroying procedure are important to be called. <br>
 * <br>
 * Caching concept provides to physical caches (SC_CACHE_TYPE.DATA_CACHE, SC_CACHE_TYPE.META_DATA_CACHE). Whenever someone is
 * interested to insert or load from cache the META_DATA_CACHE gets accessed first. It contains a list of CacheMetaEntry instances,
 * which holds information about the stored SCMPMessage in DATA_CACHE. CacheMetaEntry are identified by the cacheKey
 * serviceName_cachedId, cached messages in DATA_CACHE by the cacheKey serviceName_cacheId/partNr.
 * A meta cache entry gets created and cached when client request contains a cacheId. Other clients requesting the same cacheId
 * later, return with "cache retry later" error. This error is returned as long as the first client is not finished with loading the
 * message completely. Only the first client (session) is allowed to load the message. When the message is complete all parts
 * transfered, it is ready to be loaded from the cache. As long as the message is not completely loaded the meta entry has an
 * expiration time of OTI given by the client. After completion it gets expiration time of the message given by the server. Control
 * of the expiration is done by the ISCCache implementation.
 * There are several circumstances they can stop the loading process and clear the message:
 * - Server returns a fault message.
 * - Server returns no cacheId.
 * - Server returns a different cacheId than the requested one.
 * - Server returns no expirationDate.
 * - Server returns expirationDate with wrong format.
 * - Server returns expirationDate in the past.
 * - Caching of message fails for some reason.
 */
public class SCCacheManager {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(SCCacheManager.class);
	/** The cache configuration. */
	private SCCacheConfiguration scCacheConfiguration;
	/** Map of current session id's which are loading messages into cache, (sid, cid). */
	private HashMap<String, String> loadingSessionIds;

	/** The meta data cache. */
	ISCCache<SCCacheMetaEntry> metaDataCache = null;
	/** The data cache. */
	ISCCache<SCMPMessage> dataCache = null;

	/**
	 * Instantiates a new SC cache manager.
	 */
	public SCCacheManager() {
		this.scCacheConfiguration = null;
		this.loadingSessionIds = new HashMap<String, String>();
	}

	/**
	 * Loads the SCCacheManager. Initializes the caches and removes old cache files.
	 * 
	 * @param cacheConfiguration
	 *            the cache configuration
	 */
	@SuppressWarnings("unchecked")
	public void load(SCCacheConfiguration cacheConfiguration) {
		this.scCacheConfiguration = cacheConfiguration;
		if (this.scCacheConfiguration.isCacheEnabled() == false) {
			return;
		}
		// clean up old cache files when loading a new SC cache manager
		cleanUpCacheFiles();

		CacheRegistry caches = AppContext.getCacheRegistry();

		// create necessary caches (from ENUM)
		for (SC_CACHE_TYPE cacheType : SC_CACHE_TYPE.values()) {
			ISCCache<?> cache = SCCacheFactory.createDefaultSCCache(cacheConfiguration, cacheType);
			caches.addCache(cacheType.name(), cache);
		}
		metaDataCache = (ISCCache<SCCacheMetaEntry>) caches.getCache(SC_CACHE_TYPE.META_DATA_CACHE.name());
		dataCache = (ISCCache<SCMPMessage>) caches.getCache(SC_CACHE_TYPE.DATA_CACHE.name());
	}

	/**
	 * Try get message from cache. Returns the requested message if already stored in cache. If requested message is in loaded state
	 * because it gets loaded by another client an SCMPCommandException is returned. Otherwise cache marks requested message to be
	 * in loaded state and returns null.
	 * 
	 * @param reqMessage
	 *            the request message
	 * @return the SCMP message
	 * @throws SCMPCommandException
	 *             requested message in loading state, gets already loaded by another client<br>
	 */
	public synchronized SCMPMessage tryGetMessageFromCacheOrLoad(SCMPMessage reqMessage) throws SCMPCommandException {

		// get and check cache-id
		String cacheId = reqMessage.getCacheId();
		if (cacheId == null) {
			// no caching requested from client
			return null;
		}
		// lookup cache meta entry
		String sessionId = reqMessage.getSessionId();
		String serviceName = reqMessage.getServiceName();
		String reqCacheKey = serviceName + Constants.UNDERLINE + cacheId;
		SCCacheMetaEntry metaEntry = metaDataCache.get(reqCacheKey);

		if (metaEntry != null) {
			String partNr = reqMessage.getHeader(SCMPHeaderAttributeKey.CACHE_PARTN_NUMBER);
			CacheLogger.tryGetMessageFromCache(reqCacheKey, sessionId, partNr);
			// meta entry exists
			if (metaEntry.isLoaded() == true) {
				// message already loaded - return message
				if (partNr == null) {
					// set default partNr = 1 when it is missing in the request
					partNr = "1";
				}
				SCMPMessage cachedMessage = dataCache.get(metaEntry.getCacheKey() + Constants.SLASH + partNr);
				if (cachedMessage != null) {
					// message found adapt header fields for requester
					cachedMessage.setServiceName(reqMessage.getServiceName());
					cachedMessage.setMessageType(reqMessage.getMessageType());
					cachedMessage.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, reqMessage.getMessageSequenceNr());
					cachedMessage.setSessionId(sessionId);
					if (CacheLogger.isEnabled()) {
						CacheLogger.gotMessageFromCache(reqCacheKey, sessionId, cachedMessage.getBodyLength());
					}
				} else {
					LOGGER.error("Cache error, data-cache and meta-cache are not consistent. cacheKey=" + reqCacheKey
							+ Constants.SLASH + partNr);
				}
				return cachedMessage;
			}

			if (metaEntry.isLoading() == true) {
				// requested message is loading
				if (metaEntry.isLoadingSessionId(reqMessage.getSessionId()) == true) {
					// TODO JOT get requested cpn and compare with current cpn in metaDataCache
					// cpn has to be the same!! otherwise delete all and return error!

					// requested message is being loaded by current session - continue loading
					return null;
				}
				SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.CACHE_LOADING, "service="
						+ reqMessage.getServiceName() + " cacheId=" + reqMessage.getCacheId());
				scmpCommandException.setMessageType(reqMessage.getMessageType());
				throw scmpCommandException;
			} else {
				LOGGER.error("Cache error, bad state of meta entry cacheKey=" + reqCacheKey);
				return null;
			}
		} else {
			if (reqMessage.isPollRequest()) {
				// request for large message, but no meta entry in cache yet, ignore!
				return null;
			}
			// start loading message to cache
			SCCacheMetaEntry newMetaEntry = new SCCacheMetaEntry(reqCacheKey);
			// take original OTI so transporting message would stop before metaEntry expires!
			int otiMillis = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
			newMetaEntry.setHeader(reqMessage.getHeader()); // save all header attributes
			newMetaEntry.setLoadingSessionId(sessionId);
			newMetaEntry.setNumberOfParts(0);
			newMetaEntry.setCacheId(cacheId);
			newMetaEntry.setLoadingTimeoutMillis(otiMillis);
			newMetaEntry.setCacheEntryState(SC_CACHE_ENTRY_STATE.LOADING);

			// put meta entry to cache
			metaDataCache.putOrUpdate(reqCacheKey, newMetaEntry, otiMillis / Constants.SEC_TO_MILLISEC_FACTOR);
			loadingSessionIds.put(sessionId, reqCacheKey);
			CacheLogger.startLoadingCacheMessage(reqCacheKey, sessionId, otiMillis);
			return null;
		}
	}

	/**
	 * Cache message. Tries to cache a message. If caching the message for some reason fails, the meta entry of specific cachId gets
	 * removed. Basically a clean up is done!
	 * 
	 * @param reqMessage
	 *            the request message
	 * @param resMessage
	 *            the response message
	 */
	public void cacheMessage(SCMPMessage reqMessage, SCMPMessage resMessage) {
		if (resMessage.isPollRequest() == true) {
			// no caching - large request in process
			return;
		}

		String reqServiceName = reqMessage.getServiceName();
		String resServiceName = resMessage.getServiceName();
		String reqCacheId = reqMessage.getCacheId();
		String resCacheId = resMessage.getCacheId();
		String sid = reqMessage.getSessionId();
		String reqCacheKey = reqServiceName + Constants.UNDERLINE + reqCacheId;

		if (resMessage.isFault() == true || (resCacheId == null && reqCacheId != null)) {
			// response is faulty, clean up
			String scErrorCode = reqMessage.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE);
			this.removeMetaAndDataEntries(sid, reqCacheKey, "Reply faulty (" + scErrorCode + ") or resCacheId=null and reqCacheId="
					+ reqCacheId);
			return;

		}
		if (resCacheId == null) {
			// no cache id replied no caching requested
			return;
		}
		// this happens here because fault replies doesn't have serviceName set.
		if (resServiceName == null) {
			LOGGER.error("server did not reply service name (null), response service name set to request serviceName="
					+ reqServiceName);
			resServiceName = reqServiceName;
		}
		String resCacheKey = resServiceName + Constants.UNDERLINE + resCacheId;

		if (resCacheKey.equals(reqCacheKey) == false) {
			// requested cache id differs replied cache id, clean up
			LOGGER.error("cache message (" + reqCacheKey + ") removed, server did reply different cache key, cache (" + resCacheKey
					+ ")");
			this.removeMetaAndDataEntries(sid, reqCacheKey, "cache message (" + reqCacheKey
					+ ") removed, server did reply different cache key, cache (" + resCacheKey + ")");
			return;
		}
		// lookup up meta entry
		SCCacheMetaEntry metaEntry = metaDataCache.get(resCacheKey);

		if (metaEntry == null) {
			// no meta entry found, clean up
			LOGGER.error("Missing metaEntry message can not be cached.");
			this.removeMetaAndDataEntries(sid, reqCacheKey, "Missing metaEntry message can not be cached.");
			return;
		}

		if (metaEntry.isLoadingSessionId(sid) == false) {
			// meta entry gets loaded by another sessionId, not allowed clean up
			LOGGER.error("MetaEntry gets loaded by wrong session, not allowed expected sid=" + metaEntry.getLoadingSessionId()
					+ " loading sid= " + sid);
			this.removeMetaAndDataEntries(sid, reqCacheKey,
					"Wrong sid loads MetaEntry, expected sid=" + metaEntry.getLoadingSessionId() + " loading sid= " + sid);
			return;
		}

		String cacheExpirationDateTime = resMessage.getHeader(SCMPHeaderAttributeKey.CACHE_EXPIRATION_DATETIME);
		long expireMillis = 0;
		try {
			// validate expiration date time format, null is throwing an exception
			ValidatorUtility.validateDateTime(cacheExpirationDateTime, SCMPError.HV_WRONG_CED);
			Date expirationDate = DateTimeUtility.parseDateString(cacheExpirationDateTime);
			expireMillis = expirationDate.getTime();
		} catch (Exception e) {
			LOGGER.error("Parsing of expirationDate failed", e);
			this.removeMetaAndDataEntries(sid, reqCacheKey, "Parsing of expirationDate failed");
			return;
		}
		int timeToLiveSeconds = (int) ((expireMillis - System.currentTimeMillis()) / Constants.SEC_TO_MILLISEC_FACTOR);
		int partCounter = metaEntry.getNumberOfParts();

		// cache the message now!
		try {
			// refresh the meta entry, increment number of parts and adapt time to live
			metaEntry.setNumberOfParts(partCounter + 1);
			metaEntry.setLastModified();

			// set the correct partNr+1 for message to cache and cache it!
			resMessage.setHeader(SCMPHeaderAttributeKey.CACHE_PARTN_NUMBER, metaEntry.getNumberOfParts() + 1);
			// an negative timeToLiveSeconds (expirationDate in the past) will throw an exception, handled by the cache
			String cacheKey = reqCacheKey + Constants.SLASH + metaEntry.getNumberOfParts();
			dataCache.putOrUpdate(cacheKey, resMessage, timeToLiveSeconds);

			Statistics.getInstance().incrementCachedMessages(resMessage.getBodyLength());
			if (CacheLogger.isEnabled()) {
				CacheLogger.putMessageToCache(cacheKey, metaEntry.getLoadingSessionId(),
						DateTimeUtility.getDateTimeAsString(metaDataCache.getExpirationTime(resCacheKey)),
						resMessage.getBodyLength());
			}

			if (resMessage.isPart() == false) {
				// large response ended, last message of message received - refresh meta entry state and expire time
				metaEntry.setCacheEntryState(SC_CACHE_ENTRY_STATE.LOADED);
				metaDataCache.replace(resCacheKey, metaEntry, timeToLiveSeconds);
				// remove sessionId from loading sessionIds map
				loadingSessionIds.remove(sid);
				CacheLogger.finishLoadingCacheMessage(metaEntry.getCacheKey(), metaEntry.getLoadingSessionId(),
						metaEntry.getNumberOfParts());
			} else {
				// refresh meta entry state
				metaDataCache.replace(resCacheKey, metaEntry, metaEntry.getLoadingTimeoutMillis());
			}
		} catch (Exception e) {
			LOGGER.error("Caching message failed", e);
			this.removeMetaAndDataEntries(sid, reqCacheKey, "Caching message failed");
			return;
		}
	}

	/**
	 * Removes the meta and data entries. Cleans up the cache for given cacheId.
	 * 
	 * @param sessionId
	 *            the session id
	 * @param cacheKey
	 *            the cache id
	 * @param removeReason
	 *            the remove reason
	 */
	private synchronized void removeMetaAndDataEntries(String sessionId, String cacheKey, String removeReason) {
		if (cacheKey == null) {
			// cacheId null no remove possible
			return;
		}
		// remove meta entry
		SCCacheMetaEntry metaEntry = metaDataCache.remove(cacheKey);
		if (metaEntry == null) {
			// no entry found
			return;
		}
		CacheLogger.removeMessageFromCache(cacheKey, sessionId, removeReason);
		int size = metaEntry.getNumberOfParts();

		// remove data entries
		for (int i = 1; i < size; i++) {
			dataCache.remove(cacheKey + "/" + size);
		}
		this.loadingSessionIds.remove(sessionId);
	}

	/**
	 * Clean up cache files. Deletes files related to the SC cache. Location is taken from scCacheConfiguration.
	 */
	private void cleanUpCacheFiles() {
		String diskStorePath = scCacheConfiguration.getDiskPath();
		File diskStorePathFile = new File(diskStorePath);
		if (diskStorePathFile.exists()) {
			File[] files = diskStorePathFile.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					String fileName = pathname.getName();
					if (fileName.endsWith(".data")) {
						return true;
					}
					if (fileName.endsWith(".index")) {
						return true;
					}
					return false;
				}
			});

			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					files[i].delete();
				}
			}
		}
	}

	/**
	 * Checks if is cache enabled.
	 * 
	 * @return true, if is cache enabled
	 */
	public boolean isCacheEnabled() {
		if (this.scCacheConfiguration == null) {
			return false;
		}
		return this.scCacheConfiguration.isCacheEnabled();
	}

	/**
	 * Gets the cache configuration.
	 * 
	 * @return the cache configuration
	 */
	public SCCacheConfiguration getCacheConfiguration() {
		return scCacheConfiguration;
	}

	/**
	 * Gets the loading session ids.
	 * 
	 * @return the loading session ids
	 */
	public HashMap<String, String> getLoadingSessionIds() {
		return this.loadingSessionIds;
	}

	/**
	 * Clear loading cache message for session. Clears every cache message in loading state which is related to given sessionId.
	 * Useful when session times out and gets destroyed.
	 * 
	 * @param sessionId
	 *            the session id
	 */
	public synchronized void clearLoading(String sessionId) {
		String cachekey = this.loadingSessionIds.remove(sessionId);
		if (cachekey != null) {
			CacheLogger.abortLoadingMessage(cachekey, sessionId);
			this.removeMetaAndDataEntries(sessionId, cachekey, "ClearLoading requested for sid=" + sessionId);
		}
	}

	/**
	 * Clear all caches.
	 */
	public synchronized void clearAll() {
		CacheLogger.clearCache();
		metaDataCache.removeAll();
		dataCache.removeAll();
		this.loadingSessionIds.clear();
	}

	/**
	 * Destroy all caches controlled by this cache manager. Destroys the cache factory.
	 */
	public void destroy() {
		LOGGER.trace("destroy cache manager and active caches");
		AppContext.getCacheRegistry().removeCache(dataCache.getCacheName());
		dataCache.removeAll();
		dataCache.destroy();

		AppContext.getCacheRegistry().removeCache(metaDataCache.getCacheName());
		metaDataCache.removeAll();
		metaDataCache.destroy();
		CacheLogger.clearCache();

		SCCacheFactory.destroy();
		this.cleanUpCacheFiles();
	}

	/**
	 * Dump the cache manager into the xml writer.
	 * 
	 * @param writer
	 *            the writer
	 * @throws Exception
	 *             the exception
	 */
	public void dump(XMLDumpWriter writer) throws Exception {

		// dump cache manager
		writer.writeStartElement("cache-manager");
		writer.writeAttribute("enabled", this.isCacheEnabled());
		writer.writeAttribute("diskPath", this.getCacheConfiguration().getDiskPath());
		writer.writeAttribute("maxElementsInMemory", this.getCacheConfiguration().getMaxElementsInMemory());
		writer.writeAttribute("maxElementsOnDisk", this.getCacheConfiguration().getMaxElementsOnDisk());
		writer.writeEndElement(); // end of cache-manager
	}
}
