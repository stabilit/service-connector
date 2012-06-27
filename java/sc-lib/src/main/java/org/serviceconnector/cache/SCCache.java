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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cache.ehcache.SCCacheFactory;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.conf.SCCacheConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.CacheLogger;
import org.serviceconnector.registry.CacheModuleRegistry;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.util.DateTimeUtility;
import org.serviceconnector.util.Statistics;
import org.serviceconnector.util.ValidatorUtility;
import org.serviceconnector.util.XMLDumpWriter;

/**
 * The Class SCCache. The cache is responsible for handling caching actions in the Service Connector. The AppContext gives
 * access to the SC cache instance.
 * It controls the life cycles of caching. Loading and destroying procedure are important to be called. <br>
 * <br>
 * The cache contains two physical cache modules (SC_CACHE_TYPE.DATA_CACHE_MODULE, SC_CACHE_TYPE.META_DATA_CACHE_MODULE).
 * Whenever someone is interested to insert or load from cache the META_DATA_CACHE_MODULE gets accessed first. It contains a list of
 * CacheMetaEntry instances, which holds information about the stored SCMPMessage in DATA_CACHE_MODULE. CacheMetaEntry are
 * identified by the cacheKey serviceName_cachedId, cached messages in DATA_CACHE_MODULE by the cacheKey serviceName_cacheId/partNr.
 * A meta cache entry gets created and cached when client request contains a cacheId. Other clients requesting the same cacheId
 * later, return with "cache retry later" error. This error is returned as long as the first client is not finished with loading the
 * message completely. Only the first client (session) is allowed to load the message. When the message is complete all parts
 * transfered, it is ready to be loaded from the cache. As long as the message is not completely loaded the meta entry has an
 * expiration time of OTI given by the client. After completion it gets expiration time of the message given by the server. Control
 * of the expiration is done by the ISCCacheModule implementation.
 * There are several circumstances they can stop the loading process and clear the message:
 * - Server returns a fault message.
 * - Server returns no cacheId.
 * - Server returns a different cacheId than the requested one.
 * - Server returns no expirationDate.
 * - Server returns expirationDate with wrong format.
 * - Server returns expirationDate in the past.
 * - Caching of message fails for some reason.
 */
public class SCCache {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(SCCache.class);
	/** The cache configuration. */
	private SCCacheConfiguration scCacheConfiguration;
	/** Map of current session id's which are loading messages into cache, (sid, cid). */
	private HashMap<String, String> loadingSessionIds;

	private List<String> mgdDataKeysInInitialState;
	private Map<String, List<String>> mgdDataKeysAssignedToRetriever;

	/** The meta data cache module. */
	private ISCCacheModule<SCCacheMetaEntry> metaDataCacheModule = null;
	/** The data cache module. */
	private ISCCacheModule<SCMPMessage> dataCacheModule = null;

	/**
	 * Instantiates a new SC cache.
	 */
	public SCCache() {
		this.scCacheConfiguration = null;
		this.loadingSessionIds = new HashMap<String, String>();
		this.mgdDataKeysInInitialState = new ArrayList<String>();
		this.mgdDataKeysAssignedToRetriever = new HashMap<String, List<String>>();
	}

	/**
	 * Loads the SCCache. Initializes the cache modules and removes old cache files.
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
		// clean up old cache files when loading a new SC cache
		cleanUpCacheFiles();

		CacheModuleRegistry cacheModules = AppContext.getCacheModuleRegistry();

		// create necessary cache modules (from ENUM)
		for (SC_CACHE_MODULE_TYPE cacheModuleType : SC_CACHE_MODULE_TYPE.values()) {
			ISCCacheModule<?> cacheModule = SCCacheFactory.createDefaultSCCache(cacheConfiguration, cacheModuleType);
			cacheModules.addCacheModule(cacheModuleType.name(), cacheModule);
		}
		metaDataCacheModule = (ISCCacheModule<SCCacheMetaEntry>) cacheModules.getCache(SC_CACHE_MODULE_TYPE.META_DATA_CACHE_MODULE
				.name());
		dataCacheModule = (ISCCacheModule<SCMPMessage>) cacheModules.getCache(SC_CACHE_MODULE_TYPE.DATA_CACHE_MODULE.name());
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
		// TODO prefixing of serviceName must be removed!
		// String reqCacheKey = serviceName + Constants.UNDERLINE + cacheId;
		SCCacheMetaEntry metaEntry = metaDataCacheModule.get(cacheId);

		if (metaEntry != null) {

			if (reqMessage.isPart() && reqMessage.isPollRequest() == false) {
				// part but no poll - large request and meta entry in cache, ignore now!
				return null;
			}

			String reqPartNr = reqMessage.getHeader(SCMPHeaderAttributeKey.CACHE_PARTN_NUMBER);
			if (reqPartNr == null) {
				// set default partNr = 0 when it is missing in the request
				reqPartNr = "0";
			}
			CacheLogger.tryGetMessageFromCache(cacheId, sessionId, reqPartNr);
			// meta entry exists
			if (metaEntry.isLoaded() == true) {
				// message already loaded - return message
				SCMPMessage cachedMessage = dataCacheModule.get(metaEntry.getCacheKey() + Constants.SLASH + reqPartNr);
				if (cachedMessage != null) {
					// message found adapt header fields for requester
					cachedMessage.setServiceName(serviceName);
					cachedMessage.setMessageType(reqMessage.getMessageType());
					cachedMessage.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, reqMessage.getMessageSequenceNr());
					cachedMessage.setSessionId(sessionId);
					if (CacheLogger.isEnabled()) {
						CacheLogger.gotMessageFromCache(cacheId, sessionId, cachedMessage.getBodyLength());
					}
				} else {
					LOGGER.error("Cache error, data-cache and meta-cache are not consistent. cacheKey=" + cacheId + Constants.SLASH
							+ reqPartNr);
				}
				return cachedMessage;
			}

			if (metaEntry.isLoading() == true) {
				// requested message is loading
				if (metaEntry.isLoadingSessionId(sessionId) == true) {
					// requested message is being loaded by current session - continue loading
					int nextPartNrToLoad = metaEntry.getNumberOfParts() + 1;
					int reqPartNrInt = Integer.parseInt(reqPartNr);
					if (reqPartNrInt != nextPartNrToLoad) {
						// requested partNr does not match current loading state - remove message from cache
						LOGGER.warn("Requested partNr=" + reqPartNr + " does not match current loading state (numberOfParts="
								+ nextPartNrToLoad + ").");
						this.removeMetaAndDataEntries(sessionId, cacheId, "Requested partNr=" + reqPartNr
								+ " does not match current loading state (numberOfParts=" + nextPartNrToLoad + ").");
						// do return an error here to stop current request loading this message and avoid parallel loading problems
						SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.CACHE_ERROR,
								"cache cleared message invalid partNr in request service=" + serviceName + " cacheId=" + cacheId);
						scmpCommandException.setMessageType(reqMessage.getMessageType());
						throw scmpCommandException;
					}
					return null;
				}
				SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.CACHE_LOADING, "service="
						+ serviceName + " cacheId=" + cacheId);
				scmpCommandException.setMessageType(reqMessage.getMessageType());
				throw scmpCommandException;
			} else {
				LOGGER.error("Cache error, bad state of meta entry cacheKey=" + cacheId);
				return null;
			}
		} else {
			if (reqMessage.isPollRequest()) {
				// poll large response and no meta entry: Cache got destroyed in meantime or no caching required!
				LOGGER.trace("Poll large response with cacheId=" + cacheId + " but no meta entry.");
				return null;
			}
			// start loading message to cache
			SCCacheMetaEntry newMetaEntry = new SCCacheMetaEntry(cacheId);
			// take original OTI so transporting message would stop before metaEntry expires!
			int otiMillis = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
			newMetaEntry.setHeader(reqMessage.getHeader()); // save all header attributes
			newMetaEntry.setLoadingSessionId(sessionId);
			newMetaEntry.setNumberOfParts(-1); // increment to 0 when initial message arrives
			newMetaEntry.setCacheId(cacheId);
			newMetaEntry.setLoadingTimeoutMillis(otiMillis);
			newMetaEntry.setCacheEntryState(SC_CACHE_ENTRY_STATE.LOADING);

			// put meta entry to cache
			metaDataCacheModule.putOrUpdate(cacheId, newMetaEntry, otiMillis / Constants.SEC_TO_MILLISEC_FACTOR);
			loadingSessionIds.put(sessionId, cacheId);
			CacheLogger.startLoadingCacheMessage(cacheId, sessionId, otiMillis);
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
	public synchronized void cacheMessage(SCMPMessage reqMessage, SCMPMessage resMessage) {
		if (resMessage.isPollRequest() == true) {
			// no caching - large request in process
			return;
		}

		String reqServiceName = reqMessage.getServiceName();
		String resServiceName = resMessage.getServiceName();
		String reqCacheId = reqMessage.getCacheId();
		String resCacheId = resMessage.getCacheId();
		String sid = reqMessage.getSessionId();

		if (resMessage.isFault() == true || (resCacheId == null && reqCacheId != null)) {
			// response is faulty, clean up
			String scErrorCode = reqMessage.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE);
			this.removeMetaAndDataEntries(sid, reqCacheId, "Reply faulty (" + scErrorCode + ") or resCacheId=null and reqCacheId="
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

		if (resCacheId.equals(reqCacheId) == false) {
			// requested cache id differs replied cache id, clean up
			LOGGER.error("cache message (" + reqCacheId + ") removed, server did reply different cache key, cache (" + resCacheId
					+ ")");
			this.removeMetaAndDataEntries(sid, reqCacheId, "cache message (" + reqCacheId
					+ ") removed, server did reply different cache key, cache (" + resCacheId + ")");
			return;
		}
		// lookup up meta entry
		SCCacheMetaEntry metaEntry = metaDataCacheModule.get(resCacheId);

		if (metaEntry == null) {
			// no meta entry found, clean up
			LOGGER.error("Missing metaEntry message can not be cached.");
			this.removeMetaAndDataEntries(sid, reqCacheId, "Missing metaEntry message can not be cached.");
			return;
		}

		if (metaEntry.isLoadingSessionId(sid) == false) {
			// meta entry gets loaded by another sessionId, not allowed clean up
			LOGGER.error("MetaEntry gets loaded by wrong session, not allowed expected sid=" + metaEntry.getLoadingSessionId()
					+ " loading sid= " + sid);
			this.removeMetaAndDataEntries(sid, reqCacheId,
					"Wrong sid loads MetaEntry, expected sid=" + metaEntry.getLoadingSessionId() + " loading sid= " + sid);
			return;
		}

		// cache the message now!
		try {
			int nrOfParts = metaEntry.getNumberOfParts();
			// part message arrived - increment part number
			nrOfParts++;
			// refresh the meta entry, new number of parts
			metaEntry.setNumberOfParts(nrOfParts);
			metaEntry.setLastModified();

			// create cache key for received message
			String cacheKey = reqCacheId + Constants.SLASH + nrOfParts;
			// set the correct partNr+1 in received message and cache it, partNr points to the next part!
			resMessage.setHeader(SCMPHeaderAttributeKey.CACHE_PARTN_NUMBER, metaEntry.getNumberOfParts() + 1);
			// evaluate time to live
			int timeToLiveSeconds = this.evalTimeToLive(resMessage, metaEntry);
			SC_CACHING_METHOD resCachingMethod = SC_CACHING_METHOD.getCachingMethod(resMessage
					.getHeader(SCMPHeaderAttributeKey.CACHING_METHOD));

			if (resCachingMethod == SC_CACHING_METHOD.INITIAL && resMessage.isPart() == false) {
				// managed data received & no part message - no expiration time
				dataCacheModule.putOrUpdate(cacheKey, resMessage);
			} else {
				// an negative timeToLiveSeconds (expirationDate in the past) will throw an exception, handled by the cache
				dataCacheModule.putOrUpdate(cacheKey, resMessage, timeToLiveSeconds);
			}

			Statistics.getInstance().incrementCachedMessages(resMessage.getBodyLength());
			if (CacheLogger.isEnabled()) {
				CacheLogger.putMessageToCache(cacheKey, metaEntry.getLoadingSessionId(),
						DateTimeUtility.getDateTimeAsString(metaDataCacheModule.getExpirationTime(resCacheId)),
						resMessage.getBodyLength());
			}

			if (resMessage.isPart() == false) {
				// last part of message received - refresh meta entry state and expire time
				metaEntry.setCacheEntryState(SC_CACHE_ENTRY_STATE.LOADED);

				if (resCachingMethod == SC_CACHING_METHOD.INITIAL) {
					// managed data received - no expiration time
					metaDataCacheModule.replace(resCacheId, metaEntry);
					this.mgdDataKeysInInitialState.add(resCacheId);
				} else {
					// reduce TTL for meta entry 2 seconds this assures meta entry expires first
					metaDataCacheModule.replace(resCacheId, metaEntry, timeToLiveSeconds - 2);
				}
				// remove sessionId from loading sessionIds map
				loadingSessionIds.remove(sid);
				CacheLogger.finishLoadingCacheMessage(metaEntry.getCacheKey(), metaEntry.getLoadingSessionId(),
						metaEntry.getNumberOfParts());
			} else {
				// refresh meta entry state
				metaDataCacheModule.replace(resCacheId, metaEntry, metaEntry.getLoadingTimeoutMillis()
						/ Constants.SEC_TO_MILLISEC_FACTOR);
			}
		} catch (ParseException e) {
			LOGGER.error("Parsing of expirationDate failed", e);
			this.removeMetaAndDataEntries(sid, reqCacheId, "Parsing of expirationDate failed");
			return;
		} catch (SCMPValidatorException e) {
			LOGGER.error("Validation of expirationDate failed", e);
			this.removeMetaAndDataEntries(sid, reqCacheId, "Validation of expirationDate failed");
			return;
		} catch (Exception e) {
			LOGGER.error("Caching message failed", e);
			this.removeMetaAndDataEntries(sid, reqCacheId, "Caching message failed");
			return;
		}
	}

	// TODO
	public synchronized void manageCachedData(SCMPMessage resMessage) {
		String resCacheId = resMessage.getCacheId();
		String currUpdateRetrieverName = resMessage.getServiceName();

		// lookup up meta entry - no managing of cached data possible without
		SCCacheMetaEntry metaEntry = metaDataCacheModule.get(resCacheId);

		if (metaEntry == null) {
			// no meta entry found, clean up - no managing of cached data possible
			LOGGER.error("Missing metaEntry message can not be applied to existing data.");
			this.removeMetaAndDataEntries("sid unknown", resCacheId,
					"Missing metaEntry message can not be applied to existing data.");
			return;
		}

		String updateRetrieverOfCachedMsg = metaEntry.getUpdateRetrieverName();
		if (updateRetrieverOfCachedMsg.equals("unset")) {
			// no update retriever assigned to message, set it now!
			metaEntry.setUpdateRetrieverName(currUpdateRetrieverName);
			// remove cache id from initial data list
			boolean deleteResult = this.mgdDataKeysInInitialState.remove(resCacheId);

			if (deleteResult == false) {
				// deletion failed - update retrieved for non-managed data
				LOGGER.error("Update retrieved for non-managed data, update ignored. (cachid=" + resCacheId
						+ ", resUpdateRetriever=" + currUpdateRetrieverName + ", updateRetrieverOfCachedMsg="
						+ updateRetrieverOfCachedMsg + ")");
				return;
			}
			// add cache id to assigned data list
			if (this.mgdDataKeysAssignedToRetriever.containsKey(currUpdateRetrieverName) == false) {
				this.mgdDataKeysAssignedToRetriever.put(currUpdateRetrieverName, new ArrayList<String>());
			}
			this.mgdDataKeysAssignedToRetriever.get(currUpdateRetrieverName).add(resCacheId);
		} else if (updateRetrieverOfCachedMsg.equals(currUpdateRetrieverName) == false) {
			// managed data retrieved of different update retriever - ignore data
			LOGGER.trace("Managed data ignored, different update retriever responsible for treatment. (cachid=" + resCacheId
					+ ", resUpdateRetriever=" + currUpdateRetrieverName + ", updateRetrieverOfMessage="
					+ updateRetrieverOfCachedMsg + ")");
			return;
		}

		SC_CACHING_METHOD resCachingMethod = SC_CACHING_METHOD.getCachingMethod(resMessage
				.getHeader(SCMPHeaderAttributeKey.CACHING_METHOD));

		if (resCachingMethod == SC_CACHING_METHOD.UNDEFINED) {
			LOGGER.warn("Wrong cachingMethod in received message cmt=" + resCachingMethod + " cacheId= " + resCacheId + ".");
			return;
		}

		if (resCachingMethod == SC_CACHING_METHOD.REMOVE) {
			// remove received
			this.removeMetaAndDataEntries("sid unknown", resCacheId, "Remove requested from server for cacheId=" + resCacheId);
			return;
		}

		if (resCachingMethod == SC_CACHING_METHOD.INITIAL) {
			// initial received - replace existing
			return;
		}

		if (resCachingMethod == SC_CACHING_METHOD.APPEND) {

			// get initial message and update number of appendices
			int numberOfAppendices = metaEntry.getNumberOfAppendices();
			String initialCacheId = resCacheId + "/0";
			SCMPMessage initialMessage = dataCacheModule.get(initialCacheId);
			String baseAppendixCacheId = initialCacheId + "|" + numberOfAppendices + "/";

			if (metaEntry.isLoading() == true) {
				int currLoadingAppendixPartNr = metaEntry.incrementCurrLoadingAppendixPartNr();
				// existing message in cache in loading state
				String newAppendixCacheId = baseAppendixCacheId + currLoadingAppendixPartNr;
				if (resMessage.isPart() == true) {
					// part message of append received, cache appendix
					metaEntry.saveAppendixInfo(baseAppendixCacheId + "0");
					// update meta entry
					metaDataCacheModule.replace(resCacheId, metaEntry, metaEntry.getLoadingTimeoutMillis());
					// update initial message
					dataCacheModule.putOrUpdate(initialCacheId, initialMessage, metaEntry.getLoadingTimeoutMillis());
					// cache appendix
					dataCacheModule.putOrUpdate(newAppendixCacheId, resMessage, metaEntry.getLoadingTimeoutMillis());
				} else {
					// last part of appendix message received - set state to loaded, no expiration times
					metaEntry.setCacheEntryState(SC_CACHE_ENTRY_STATE.LOADED);
					metaEntry.saveAppendixInfo(baseAppendixCacheId + "0");
					// update meta entry
					metaDataCacheModule.replace(resCacheId, metaEntry);
					// update initial message
					dataCacheModule.putOrUpdate(initialCacheId, initialMessage);
					// cache appendix
					dataCacheModule.putOrUpdate(newAppendixCacheId, resMessage);
				}
			} else {
				// appendix arrived increment counter
				numberOfAppendices = metaEntry.incrementNrOfAppendices();
				initialMessage.setHeader(SCMPHeaderAttributeKey.NR_OF_APPENDICES, numberOfAppendices);
				baseAppendixCacheId += "0";
				metaEntry.setCurrLoadingAppendixPartNr(0);

				// existing message in cache NOT in loading state
				if (resMessage.isPart() == true) {
					// received message is part message - set meta entry to loading state, expiration times
					metaEntry.setCacheEntryState(SC_CACHE_ENTRY_STATE.LOADING);
					metaEntry.saveAppendixInfo(baseAppendixCacheId);
					// update meta entry
					metaDataCacheModule.replace(resCacheId, metaEntry, metaEntry.getLoadingTimeoutMillis());
					// update initial message
					dataCacheModule.putOrUpdate(initialCacheId, initialMessage, metaEntry.getLoadingTimeoutMillis());
					// cache appendix
					dataCacheModule.putOrUpdate(baseAppendixCacheId, resMessage, metaEntry.getLoadingTimeoutMillis());
				} else {
					// no part message received - common procedure, no expiration times!
					metaEntry.saveAppendixInfo(baseAppendixCacheId);
					// update meta entry
					metaDataCacheModule.replace(resCacheId, metaEntry);
					// update initial message
					dataCacheModule.putOrUpdate(initialCacheId, initialMessage);
					// cache appendix
					dataCacheModule.putOrUpdate(baseAppendixCacheId, resMessage);
				}
			}
		}
	}

	/**
	 * Removes the meta and data entries. Cleans up the cache for given cacheId.
	 * 
	 * @param sessionId
	 *            the session id
	 * @param metaEntryCacheId
	 *            the cache id
	 * @param removeReason
	 *            the remove reason
	 */
	private synchronized void removeMetaAndDataEntries(String sessionId, String metaEntryCacheId, String removeReason) {
		if (metaEntryCacheId == null) {
			// cacheId null no remove possible
			return;
		}
		// remove meta entry
		SCCacheMetaEntry metaEntry = metaDataCacheModule.remove(metaEntryCacheId);
		if (metaEntry == null) {
			// no entry found
			return;
		}

		int nrOfParts = metaEntry.getNumberOfParts();
		CacheLogger.removeMessageFromCache(metaEntryCacheId, sessionId, nrOfParts, removeReason);

		// remove data entries - parts belonging to the message
		for (int i = 0; i < nrOfParts; i++) {
			dataCacheModule.remove(metaEntryCacheId + Constants.SLASH + i);
		}

		// remove data entries - appendices belonging to the message
		int nrOfAppendices = metaEntry.getNumberOfAppendices();
		for (int i = 0; i < nrOfAppendices; i++) {
			int nrOfPartsOfAppendix = metaEntry.getNrOfPartsOfAppendix(metaEntryCacheId + Constants.SLASH + "0|" + nrOfAppendices);

			for (int index = 0; index < nrOfPartsOfAppendix; index++) {
				dataCacheModule.remove(metaEntryCacheId + Constants.SLASH + "0|" + nrOfAppendices + "/" + index);
			}
		}
		this.loadingSessionIds.remove(sessionId);
	}

	//TODO
	public synchronized void removeManagedDataForRetriever(String updateRetrieverName) {

		// remove managed data in initial state
		for (String metaEntryCacheId : this.mgdDataKeysInInitialState) {
			this.removeMetaAndDataEntries("unknown", metaEntryCacheId, "Broken Update Retriever, name=" + updateRetrieverName);
		}

		// removed managed data assigned to update retriever
		for (String metaEntryCacheId : this.mgdDataKeysAssignedToRetriever.get(updateRetrieverName)) {
			this.removeMetaAndDataEntries("unknown", metaEntryCacheId, "Broken Update Retriever, name=" + updateRetrieverName);
		}
	}

	// TODO
	private int evalTimeToLive(SCMPMessage messageToCache, SCCacheMetaEntry metaEntry) throws SCMPValidatorException,
			ParseException {

		// use expire time from header field
		String cacheExpirationDateTime = messageToCache.getHeader(SCMPHeaderAttributeKey.CACHE_EXPIRATION_DATETIME);
		// validate expiration date time format, null is throwing an exception
		ValidatorUtility.validateDateTime(cacheExpirationDateTime, SCMPError.HV_WRONG_CED);
		Date expirationDate = DateTimeUtility.parseDateString(cacheExpirationDateTime);
		long expireMillis = expirationDate.getTime();
		return (int) ((expireMillis - System.currentTimeMillis()) / Constants.SEC_TO_MILLISEC_FACTOR);
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
		metaDataCacheModule.removeAll();
		dataCacheModule.removeAll();
		this.loadingSessionIds.clear();
	}

	/**
	 * Destroy all cache modules controlled by this cache. Destroys the cache factory.
	 */
	public void destroy() {
		LOGGER.trace("destroy cache and active cache modules");
		AppContext.getCacheModuleRegistry().removeCache(dataCacheModule.getCacheModuleName());
		dataCacheModule.removeAll();
		dataCacheModule.destroy();

		AppContext.getCacheModuleRegistry().removeCache(metaDataCacheModule.getCacheModuleName());
		metaDataCacheModule.removeAll();
		metaDataCacheModule.destroy();
		CacheLogger.clearCache();

		SCCacheFactory.destroy();
		this.cleanUpCacheFiles();
	}

	/**
	 * Dump the cache into the xml writer.
	 * 
	 * @param writer
	 *            the writer
	 * @throws Exception
	 *             the exception
	 */
	public void dump(XMLDumpWriter writer) throws Exception {

		// dump cache
		writer.writeStartElement("cache");
		writer.writeAttribute("enabled", this.isCacheEnabled());
		writer.writeAttribute("diskPath", this.getCacheConfiguration().getDiskPath());
		writer.writeAttribute("maxElementsInMemory", this.getCacheConfiguration().getMaxElementsInMemory());
		writer.writeAttribute("maxElementsOnDisk", this.getCacheConfiguration().getMaxElementsOnDisk());
		writer.writeEndElement(); // end of cache
	}
}
