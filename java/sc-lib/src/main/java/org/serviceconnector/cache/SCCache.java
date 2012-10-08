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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
 * identified by the cacheKey cachedId, cached messages in DATA_CACHE_MODULE by the cacheKey cacheId/appendixNr/partNr.
 * A meta cache entry gets created and cached when client request contains a cacheId. Other clients requesting the same cacheId
 * later, return with "cache retry later" error. This error is returned as long as the first client is not finished with loading the
 * message completely. Only the first client (session) is allowed to load the message. At the time the message is complete and all
 * parts transfered, it is ready to be loaded from the cache. As long as the message is not completely loaded the meta entry has an
 * expiration time of OTI given by the client. After completion it gets expiration time of the message given by the server. Control
 * of the expiration is done by the ISCCacheModule implementation. Data entries never (0 means forever) have an expiration time.
 * When a meta entry expires every data entry belonging to this meta entry will be deleted. Managed (loaded with cmt=initial) data
 * with an empty expiration never expire. They stay as long as no remove is received.<br>
 * <br>
 * Cache identifiers naming:
 * cacheId/appendixNr/partNr
 * |---baseDataCid---|
 * |--------dataCid--------| <br>
 * <br>
 * The cache identifier with appendix zero and part number zero (e.g. 700/0/0) is called initialDataCid.<br>
 * <br>
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

	/** The managed data keys in initial state. */
	private Set<String> mgdDataKeysInInitialState;
	/** The managed data assigned to guardian. */
	private HashMap<String, Set<String>> mgdDataAssignedToGuardian;

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
		this.mgdDataKeysInInitialState = new HashSet<String>();
		this.mgdDataAssignedToGuardian = new HashMap<String, Set<String>>();
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
		String metaEntryCid = reqMessage.getCacheId();
		if (metaEntryCid == null) {
			// no caching requested from client
			return null;
		}
		// lookup cache meta entry
		String sessionId = reqMessage.getSessionId();
		String serviceName = reqMessage.getServiceName();
		SCCacheMetaEntry metaEntry = metaDataCacheModule.get(metaEntryCid);

		if (metaEntry != null) {

			if (reqMessage.isPart() == true && reqMessage.isPollRequest() == false) {
				// part but no poll (PRQ) - large request and meta entry in cache, forward to next node!
				return null;
			}

			if (reqMessage.isReqCompleteAfterMarshallingPart() == true && metaEntry.isLoading() == true
					&& metaEntry.isLoadingSessionId(sessionId) == true) {
				// REQ & sid is loading session - ending REQ of large request, forward to next node!
				return null;
			}

			String appendixNr = reqMessage.getHeader(SCMPHeaderAttributeKey.APPENDIX_NR);
			if (appendixNr == null) {
				// set default appendixNr = 0 when it is missing in the request
				appendixNr = "0";
			}

			String reqPartNr = reqMessage.getHeader(SCMPHeaderAttributeKey.CACHE_PARTN_NUMBER);
			if (reqPartNr == null) {
				// set default partNr = 0 when it is missing in the request
				reqPartNr = "0";
			}
			// build dataEntryCid
			String dataEntryCid = metaEntryCid + Constants.SLASH + appendixNr + Constants.SLASH + reqPartNr;
			CacheLogger.tryGetMessageFromCache(metaEntryCid, sessionId, reqPartNr, appendixNr);
			// meta entry exists
			if (metaEntry.isLoaded() == true) {
				// message already loaded - return message
				SCMPMessage cachedMessage = dataCacheModule.get(dataEntryCid);
				if (cachedMessage != null) {
					// message found adapt header fields for requester
					cachedMessage.setServiceName(serviceName);
					cachedMessage.setMessageType(reqMessage.getMessageType());
					cachedMessage.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, reqMessage.getMessageSequenceNr());
					cachedMessage.setSessionId(sessionId);
					if (CacheLogger.isEnabled()) {
						CacheLogger.gotMessageFromCache(dataEntryCid, sessionId, cachedMessage.getBodyLength());
					}
				} else {
					LOGGER.error("Cache error, data-cache and meta-cache are not consistent. cacheKey=" + dataEntryCid);
				}
				return cachedMessage;
			}

			if (metaEntry.isLoadingAppendix() == true) {
				// requested message gets an updated by an appendix (loading appendix)
				SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.CACHE_LOADING, "service="
						+ serviceName + " cacheId=" + metaEntryCid);
				scmpCommandException.setMessageType(reqMessage.getMessageType());
				throw scmpCommandException;
			}

			if (metaEntry.isLoading() == true) {
				// requested message is loading
				if (metaEntry.isLoadingSessionId(sessionId) == true) {
					// requested message is being loaded by current session - continue loading
					int nextPartNrToLoad = metaEntry.getNrOfParts(metaEntryCid + Constants.SLASH + appendixNr + Constants.SLASH
							+ "0") + 1;
					int reqPartNrInt = Integer.parseInt(reqPartNr);
					if (reqPartNrInt != nextPartNrToLoad) {
						// requested partNr does not match current loading state - remove message from cache
						LOGGER.warn("Requested partNr=" + reqPartNr + " does not match current loading state (numberOfParts="
								+ nextPartNrToLoad + ").");
						this.removeMetaAndDataEntries(metaEntryCid, "Requested partNr=" + reqPartNr
								+ " does not match current loading state (numberOfParts=" + nextPartNrToLoad + ").");
						// do return an error here to stop current request loading this message and avoid parallel loading problems
						SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.CACHE_ERROR,
								"cache cleared message invalid partNr in request service=" + serviceName + " cacheId="
										+ metaEntryCid);
						scmpCommandException.setMessageType(reqMessage.getMessageType());
						throw scmpCommandException;
					}
					return null;
				}
				SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.CACHE_LOADING, "service="
						+ serviceName + " cacheId=" + metaEntryCid);
				scmpCommandException.setMessageType(reqMessage.getMessageType());
				throw scmpCommandException;
			} else {
				LOGGER.error("Cache error, bad state of meta entry cacheKey=" + metaEntryCid);
				return null;
			}
		} else {
			if (reqMessage.isPollRequest()) {
				// poll large response and no meta entry: Cache got destroyed in meantime or no caching required!
				LOGGER.trace("Poll large response with cacheId=" + metaEntryCid + " but no meta entry.");
				return null;
			}
			// start loading message to cache
			SCCacheMetaEntry newMetaEntry = new SCCacheMetaEntry(metaEntryCid);
			// take original OTI so transporting message would stop before metaEntry expires!
			int otiMillis = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
			newMetaEntry.setHeader(reqMessage.getHeader()); // save all header attributes
			newMetaEntry.setLoadingSessionId(sessionId);
			newMetaEntry.setLoadingTimeoutMillis(otiMillis);
			newMetaEntry.setCacheEntryState(SC_CACHE_ENTRY_STATE.LOADING);

			// put meta entry to cache
			this.metaDataCacheModule.putOrUpdate(metaEntryCid, newMetaEntry, otiMillis / Constants.SEC_TO_MILLISEC_FACTOR);
			this.loadingSessionIds.put(sessionId, metaEntryCid);
			CacheLogger.startLoadingCacheMessage(metaEntryCid, sessionId, otiMillis);
			return null;
		}
	}

	/**
	 * Cache message. Tries to cache a message. If caching the message for some reason fails, the meta entry of specific cachId gets
	 * removed. Basically a clean up is done! Cache message is called in a polling procedure of a client. Published appendices are
	 * cached by the method "cacheManagedData".
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
		String loadingSid = reqMessage.getSessionId();
		SC_CACHING_METHOD recvCachingMethod = SC_CACHING_METHOD.getCachingMethod(resMessage
				.getHeader(SCMPHeaderAttributeKey.CACHING_METHOD));

		if (resMessage.isFault() == true || (resCacheId == null && reqCacheId != null)) {
			// response is faulty, clean up
			String scErrorCode = reqMessage.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE);
			this.removeMetaAndDataEntries(reqCacheId, "Reply faulty (" + scErrorCode + ") or resCacheId=null and reqCacheId="
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
			this.removeMetaAndDataEntries(reqCacheId, "cache message (" + reqCacheId
					+ ") removed, server did reply different cache key, cache (" + resCacheId + ")");
			return;
		}
		// lookup up meta entry
		String metaEntryCid = resCacheId;
		SCCacheMetaEntry metaEntry = metaDataCacheModule.get(metaEntryCid);

		if (metaEntry == null) {
			// no meta entry found, clean up
			LOGGER.error("Missing metaEntry message can not be cached.");
			this.removeMetaAndDataEntries(reqCacheId, "Missing metaEntry message can not be cached.");
			return;
		}

		if (metaEntry.isLoadingSessionId(loadingSid) == false) {
			// meta entry gets loaded by another sessionId, not allowed clean up
			LOGGER.error("MetaEntry gets loaded by wrong session, not allowed expected sid=" + metaEntry.getLoadingSessionId()
					+ " loading sid= " + loadingSid);
			this.removeMetaAndDataEntries(metaEntryCid,
					"Wrong sid loads MetaEntry, expected sid=" + metaEntry.getLoadingSessionId() + " loading sid=" + loadingSid);
			return;
		}

		// cache the message now!
		try {
			int nrOfAppendix = metaEntry.getNrOfAppendix();
			String baseDataEntryCid = metaEntryCid + Constants.SLASH + nrOfAppendix;
			String dataEntryCid = baseDataEntryCid + Constants.SLASH + "0";
			Integer recvCachePartNr = resMessage.getHeaderInt(SCMPHeaderAttributeKey.CACHE_PARTN_NUMBER);

			if (recvCachingMethod == SC_CACHING_METHOD.APPEND && (recvCachePartNr == null || recvCachePartNr == 1)) {
				// first message of appendix - increments number of appendix and create new cache id for message
				nrOfAppendix = metaEntry.incrementNrOfAppendix();
				baseDataEntryCid = metaEntryCid + Constants.SLASH + nrOfAppendix;
				dataEntryCid = baseDataEntryCid + Constants.SLASH + "0";
			}

			String expDateTimeStr = metaEntry.getExpDateTimeStr();
			// part message arrived - increment part number
			int nrOfParts = metaEntry.incrementNrOfPartsForDataMsg(dataEntryCid);
			if (nrOfParts == 0 && nrOfAppendix == 0) {
				// first part received no appendix - extract number of appendix to be loaded
				Integer expectedAppendix = resMessage.getHeaderInt(SCMPHeaderAttributeKey.NR_OF_APPENDIX);
				metaEntry.setExpectedAppendix(expectedAppendix);
				// evaluate TTL for meta entry - 0 means forever valid (use expire time from header field)
				expDateTimeStr = resMessage.getHeader(SCMPHeaderAttributeKey.CACHE_EXPIRATION_DATETIME);
				metaEntry.setExpDateTimeStr(expDateTimeStr);
			}
			int timeToLiveSeconds = this.evalTimeToLiveSeconds(expDateTimeStr);

			// refresh the meta entry
			metaEntry.setLastModified();
			// create cache id for received message
			dataEntryCid = baseDataEntryCid + Constants.SLASH + nrOfParts;
			// set the correct partNr+1 in received message and cache it, partNr points to the next part!
			resMessage.setHeader(SCMPHeaderAttributeKey.CACHE_PARTN_NUMBER, nrOfParts + 1);

			if (recvCachingMethod == SC_CACHING_METHOD.INITIAL) {
				// managed data received add to data in initial state list
				this.mgdDataKeysInInitialState.add(metaEntryCid);
				metaEntry.setCachingMethod(recvCachingMethod);
			}

			Statistics.getInstance().incrementCachedMessages(resMessage.getBodyLength());
			if (CacheLogger.isEnabled()) {
				CacheLogger.putMessageToCache(dataEntryCid, nrOfParts, metaEntry.getLoadingSessionId(), resMessage.getBodyLength(),
						metaEntry.getSCCacheEntryState().name(), recvCachingMethod.name());
			}

			if (resMessage.isPart() == false && metaEntry.getNrOfAppendix() == metaEntry.getExpectedAppendix()) {
				// last part of message received - refresh meta entry state and expire time
				metaEntry.setCacheEntryState(SC_CACHE_ENTRY_STATE.LOADED);

				metaDataCacheModule.replace(metaEntryCid, metaEntry, timeToLiveSeconds);

				// remove sessionId from loading sessionIds map
				loadingSessionIds.remove(loadingSid);
				CacheLogger.finishLoadingCacheMessage(metaEntry.getCacheId(), metaEntry.getLoadingSessionId(), nrOfParts,
						metaEntry.getNrOfAppendix());
			} else {
				// refresh meta entry state
				metaDataCacheModule.replace(metaEntryCid, metaEntry, metaEntry.getLoadingTimeoutMillis()
						/ Constants.SEC_TO_MILLISEC_FACTOR);
			}
			// cache data entry - expiration time forever for data entries
			dataCacheModule.putOrUpdate(dataEntryCid, resMessage, 0);
		} catch (ParseException e) {
			LOGGER.error("Parsing of expirationDate failed", e);
			this.removeMetaAndDataEntries(metaEntryCid, "Parsing of expirationDate failed");
			return;
		} catch (SCMPValidatorException e) {
			LOGGER.error("Validation of expirationDate failed", e);
			this.removeMetaAndDataEntries(metaEntryCid, "Validation of expirationDate failed");
			return;
		} catch (Exception e) {
			LOGGER.error("Caching message failed", e);
			this.removeMetaAndDataEntries(metaEntryCid, "Caching message failed");
			return;
		}
	}

	/**
	 * Cached managed data. Called for caching managed data. Never used in a polling procedure of a client.
	 * 
	 * @param resMessage
	 *            the res message
	 * @throws ParseException
	 * @throws SCMPValidatorException
	 */
	public synchronized void cachedManagedData(SCMPMessage resMessage) throws SCMPValidatorException, ParseException {
		String metaEntryCid = resMessage.getCacheId();
		String currGuardian = resMessage.getServiceName();
		String sid = resMessage.getSessionId();

		// lookup up meta entry - no managing of cached data possible without
		SCCacheMetaEntry metaEntry = metaDataCacheModule.get(metaEntryCid);

		if (metaEntry == null) {
			// no meta entry found, clean up - no managing of cached data possible
			LOGGER.info("Missing metaEntry message can not be applied, cid=" + metaEntryCid);
			this.removeMetaAndDataEntries(metaEntryCid, "Missing metaEntry message can not be applied.");
			return;
		}

		SC_CACHING_METHOD resCachingMethod = SC_CACHING_METHOD.getCachingMethod(resMessage
				.getHeader(SCMPHeaderAttributeKey.CACHING_METHOD));

		if (resCachingMethod == SC_CACHING_METHOD.REMOVE) {
			// remove received - remove existing
			LOGGER.trace("Remove data received from server (cid=" + metaEntryCid + ", guardian=" + currGuardian + ")");
			this.removeMetaAndDataEntries(metaEntryCid, "Remove requested from server for cacheId=" + metaEntryCid);
			return;
		}

		if (metaEntry.isManaged() == false) {
			LOGGER.error("Managed data received for unmanged existing data in cache. Can not be applied, cid=" + metaEntryCid);
			return;
		}

		if (resCachingMethod == SC_CACHING_METHOD.NOT_MANAGED) {
			// not managed received
			LOGGER.error("Wrong cachingMethod in received message cmt=" + resCachingMethod + " metaEntryCid=" + metaEntryCid + ".");
			return;
		}

		if (resCachingMethod == SC_CACHING_METHOD.INITIAL) {
			// initial received - replace existing
			if (metaEntry.isLoadingAppendix() == true || (metaEntry.isLoading() && metaEntry.isLoadingSessionId(sid) == false)) {
				// appendix is loading or initial message by another session - delete existing
				this.removeMetaAndDataEntries(metaEntryCid, "Initial (replace) requested from server for cacheId=" + metaEntryCid);
				LOGGER.error("Initial message over guardian retrieved while initial message over session service is still loading or appendix is loading. (metaEntryCacheId="
						+ metaEntryCid + ", guardian=" + currGuardian + ")");
				return;
			}

			SCCacheMetaEntry affectedMetaEntry = null;
			int timeToLiveSeconds = -1;
			String baseDataEntryCid = metaEntryCid + Constants.SLASH + "0";
			String initialDataCid = baseDataEntryCid + Constants.SLASH + "0";

			if (metaEntry.isLoading() == true) {
				// large request in process - metaEntry stays
				affectedMetaEntry = metaEntry;

				if (resMessage.isPart() == false) {
					// large replacement finished
					affectedMetaEntry.setCacheEntryState(SC_CACHE_ENTRY_STATE.LOADED);
					timeToLiveSeconds = this.evalTimeToLiveSeconds(metaEntry.getExpDateTimeStr());
					CacheLogger.stopLoadingReplacement(metaEntryCid, currGuardian,
							affectedMetaEntry.getNrOfParts(initialDataCid) + 1);
				} else {
					// large replacements continues
					timeToLiveSeconds = metaEntry.getLoadingTimeoutMillis() / Constants.SEC_TO_MILLISEC_FACTOR;
				}
			} else {
				// no large request in process - delete existing
				this.removeMetaAndDataEntries(metaEntryCid, "Initial (replace) requested from server for cacheId=" + metaEntryCid);
				// initial received - replace existing
				LOGGER.trace("initial data received replace existing (cid=" + metaEntryCid + ", guardian=" + currGuardian + ")");
				// create new metaEntry
				affectedMetaEntry = new SCCacheMetaEntry(metaEntryCid);
				affectedMetaEntry.setLoadingTimeoutMillis(metaEntry.getLoadingTimeoutMillis());
				affectedMetaEntry.setCacheGuardianName(currGuardian);
				// add cache id to assigned data list
				if (this.mgdDataAssignedToGuardian.containsKey(currGuardian) == false) {
					this.mgdDataAssignedToGuardian.put(currGuardian, new HashSet<String>());
				}
				this.mgdDataAssignedToGuardian.get(currGuardian).add(metaEntryCid);
				affectedMetaEntry.setExpDateTimeStr(metaEntry.getExpDateTimeStr());
				affectedMetaEntry.setHeader(metaEntry.getHeader());
				affectedMetaEntry.setCachingMethod(SC_CACHING_METHOD.INITIAL);
				affectedMetaEntry.setLoadingSessionId(sid);
				if (resMessage.isPart() == true) {
					// incoming message first part of large request - switch state to loading
					affectedMetaEntry.setCacheEntryState(SC_CACHE_ENTRY_STATE.LOADING);
					timeToLiveSeconds = metaEntry.getLoadingTimeoutMillis() / Constants.SEC_TO_MILLISEC_FACTOR;
					CacheLogger.startLoadingReplacement(metaEntryCid, currGuardian);
				} else {
					affectedMetaEntry.setCacheEntryState(SC_CACHE_ENTRY_STATE.LOADED);
					timeToLiveSeconds = this.evalTimeToLiveSeconds(metaEntry.getExpDateTimeStr());
					CacheLogger.replaceExistingData(metaEntryCid, currGuardian);
				}
				// refresh the meta entry
				affectedMetaEntry.setLastModified();
			}

			// part message arrived - increment part number
			int nrOfParts = affectedMetaEntry.incrementNrOfPartsForDataMsg(initialDataCid);
			String dataEntryCid = baseDataEntryCid + Constants.SLASH + nrOfParts;

			metaDataCacheModule.putOrUpdate(metaEntryCid, affectedMetaEntry, timeToLiveSeconds);
			// set the correct partNr+1 in received message and cache it, partNr points to the next part!
			resMessage.setHeader(SCMPHeaderAttributeKey.CACHE_PARTN_NUMBER, nrOfParts + 1);
			// cache data entry - expiration time forever for data entries
			dataCacheModule.putOrUpdate(dataEntryCid, resMessage, 0);
			CacheLogger.putManagedDataToCache(dataEntryCid, currGuardian, 0, nrOfParts);
			return;
		}

		String guardianOfCachedMsg = metaEntry.getCacheGuardianName();
		if (guardianOfCachedMsg.equals("unset") == true) {
			// no cache guardian assigned to message, set it now!
			metaEntry.setCacheGuardianName(currGuardian);
			// remove cache id from initial data list
			boolean deleteResult = this.mgdDataKeysInInitialState.remove(metaEntryCid);

			if (deleteResult == false) {
				// deletion failed - update retrieved for non-managed data
				LOGGER.error("Update retrieved for non-managed data, update ignored. (metaEntryCacheId=" + metaEntryCid
						+ ", resCacheGuardian=" + currGuardian + ", guardianOfCachedMsg=" + guardianOfCachedMsg + ")");
				return;
			}
			// add cache id to assigned data list
			if (this.mgdDataAssignedToGuardian.containsKey(currGuardian) == false) {
				this.mgdDataAssignedToGuardian.put(currGuardian, new HashSet<String>());
			}
			this.mgdDataAssignedToGuardian.get(currGuardian).add(metaEntryCid);
		} else if (guardianOfCachedMsg.equals(currGuardian) == false) {
			// managed data retrieved of different cache guardian - ignore data
			LOGGER.trace("Managed data ignored, different cache guardian responsible for treatment. (metaEntryCid=" + metaEntryCid
					+ ", resCacheGuardian=" + currGuardian + ", guardianOfCachedMsg=" + guardianOfCachedMsg + ")");
			return;
		}

		if (metaEntry.isLoading() == true) {
			// message is being loaded
			LOGGER.error("Appendix reveived, base message still loading - delete data to avoid inconsistency.");
			this.removeMetaAndDataEntries(metaEntryCid, "Appendix reveived, base message still loading.");
		} else if (resCachingMethod == SC_CACHING_METHOD.APPEND) {
			// append received - append to existing
			if (metaEntry.isLoadingAppendix() == true) {
				// meta entry loading appendix
				int appendixNr = metaEntry.getNrOfAppendix();
				String dataEntryCid = metaEntryCid + Constants.SLASH + appendixNr + Constants.SLASH + "0";
				int nrOfPartsForAppendix = metaEntry.incrementNrOfPartsForDataMsg(dataEntryCid);
				dataEntryCid = metaEntryCid + Constants.SLASH + appendixNr + Constants.SLASH + nrOfPartsForAppendix;
				// set the correct partNr+1 in received message and cache it, partNr points to the next part!
				resMessage.setHeader(SCMPHeaderAttributeKey.CACHE_PARTN_NUMBER, nrOfPartsForAppendix + 1);

				// cache appendix, managed data expiration time forever
				dataCacheModule.putOrUpdate(dataEntryCid, resMessage, 0);

				if (resMessage.isPart() == true) {
					// part of large appendix received, update meta entry
					metaDataCacheModule.replace(metaEntryCid, metaEntry, metaEntry.getLoadingTimeoutMillis()
							/ Constants.SEC_TO_MILLISEC_FACTOR);
					CacheLogger.putManagedDataToCache(dataEntryCid, currGuardian, appendixNr, nrOfPartsForAppendix);
				} else {
					// end of large appendix received
					metaEntry.setCacheEntryState(SC_CACHE_ENTRY_STATE.LOADED);
					// update meta entry
					int timeToLive = this.evalTimeToLiveSeconds(metaEntry.getExpDateTimeStr());
					metaDataCacheModule.replace(metaEntryCid, metaEntry, timeToLive);
					CacheLogger.finishCachingAppendix(dataEntryCid, currGuardian, nrOfPartsForAppendix);
				}
			} else {
				// meta entry is loaded - appendix received, increment counter
				int appendixNr = metaEntry.incrementNrOfAppendix();
				String dataEntryCid = metaEntryCid + Constants.SLASH + appendixNr + Constants.SLASH + "0";
				// increment number of parts for appendix in meta entry
				int nrOfPart = metaEntry.incrementNrOfPartsForDataMsg(dataEntryCid);

				// update initial message with correct number of appendix
				String initialDataCid = metaEntryCid + Constants.SLASH + "0" + Constants.SLASH + "0";
				SCMPMessage initialData = dataCacheModule.get(initialDataCid);
				initialData.setHeader(SCMPHeaderAttributeKey.NR_OF_APPENDIX, appendixNr);
				// expiration time forever for data entries
				dataCacheModule.putOrUpdate(initialDataCid, initialData, 0);

				if (resMessage.isPart() == true) {
					// start of large appendix received, update meta entry
					metaEntry.setCacheEntryState(SC_CACHE_ENTRY_STATE.LOADING_APPENDIX);
					// set the correct partNr+1 in received message and cache it, partNr points to the next part!
					resMessage.setHeader(SCMPHeaderAttributeKey.CACHE_PARTN_NUMBER, nrOfPart + 1);
					// update meta entry, expiration time
					metaDataCacheModule.replace(metaEntryCid, metaEntry, metaEntry.getLoadingTimeoutMillis()
							/ Constants.SEC_TO_MILLISEC_FACTOR);
					CacheLogger.startCachingAppendix(dataEntryCid, currGuardian, metaEntry.getLoadingTimeoutMillis()
							/ Constants.SEC_TO_MILLISEC_FACTOR);
				} else {
					// appendix received, update meta entry, expiration time
					int timeToLive = this.evalTimeToLiveSeconds(metaEntry.getExpDateTimeStr());
					metaDataCacheModule.replace(metaEntryCid, metaEntry, timeToLive);
					CacheLogger.putManagedDataToCache(dataEntryCid, currGuardian, appendixNr, nrOfPart);
				}
				// cache appendix, managed data expiration forever
				dataCacheModule.putOrUpdate(dataEntryCid, resMessage, 0);
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
	private synchronized void removeMetaAndDataEntries(String metaEntryCacheId, String removeReason) {
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
		CacheLogger.removeMessageFromCache(metaEntryCacheId, removeReason);
		this.removeDataEntriesByMetaEntry(metaEntry, removeReason);
	}

	/**
	 * Removes the data entries by meta entry.
	 * 
	 * @param metaEntry
	 *            the meta entry
	 * @param removeReason
	 *            the remove reason
	 */
	public synchronized void removeDataEntriesByMetaEntry(SCCacheMetaEntry metaEntry, String removeReason) {
		String metaEntryCid = metaEntry.getCacheId();
		int nrOfAppendices = metaEntry.getNrOfAppendix();

		// remove data entries - appendices belonging to the message
		for (int i = 0; i <= nrOfAppendices; i++) {
			String appendixCid = metaEntryCid + Constants.SLASH + i + Constants.SLASH + "0";
			int nrOfPartsOfAppendix = metaEntry.getNrOfParts(appendixCid);

			for (int index = 0; index <= nrOfPartsOfAppendix; index++) {
				String dataCid = metaEntryCid + Constants.SLASH + i + Constants.SLASH + index;
				dataCacheModule.remove(dataCid);
				CacheLogger.removeMessageFromCache(dataCid, removeReason);
			}
		}
		this.loadingSessionIds.remove(metaEntry.getLoadingSessionId());
		this.mgdDataKeysInInitialState.remove(metaEntryCid);
		Set<String> cids = this.mgdDataAssignedToGuardian.get(metaEntry.getCacheGuardianName());
		if (cids != null) {
			cids.remove(metaEntryCid);
		}
	}

	/**
	 * Removes the managed data for guardian. Any data assigned to the specific cache guardian will be deleted.
	 * 
	 * @param cacheGuardian
	 *            the cache guardian
	 */
	public synchronized void removeManagedDataForGuardian(String cacheGuardian) {

		// remove managed data in initial state
		String[] metaEntryCacheIds = (String[]) this.mgdDataKeysInInitialState.toArray(new String[0]);
		for (String metaEntryCacheId : this.mgdDataKeysInInitialState) {
			this.removeMetaAndDataEntries(metaEntryCacheId, "Broken or inactive Cache Guardian, name=" + cacheGuardian);
		}

		// removed managed data assigned to cache guardian
		if (this.mgdDataAssignedToGuardian.containsKey(cacheGuardian) == false) {
			// no managed data to delete
			return;
		}
		metaEntryCacheIds = (String[]) this.mgdDataAssignedToGuardian.get(cacheGuardian).toArray(new String[0]);
		for (String metaEntryCacheId : metaEntryCacheIds) {
			this.removeMetaAndDataEntries(metaEntryCacheId, "Broken or inactive Cache Guardian, name=" + cacheGuardian);
		}
		this.mgdDataAssignedToGuardian.remove(cacheGuardian);
	}

	/**
	 * Evaluates time to live in seconds.
	 * 
	 * @param messageToCache
	 *            the message to cache
	 * @return number of seconds to live
	 *         0 if forever
	 * @throws SCMPValidatorException
	 *             the SCMP validator exception
	 * @throws ParseException
	 *             the parse exception
	 */
	private int evalTimeToLiveSeconds(String cacheExpirationDateTime) throws SCMPValidatorException, ParseException {
		if (cacheExpirationDateTime == null) {
			// no expiration time set - 0 means forever valid
			return 0;
		}

		// validate expiration date time format, null is throwing an exception
		ValidatorUtility.validateDateTime(cacheExpirationDateTime, SCMPError.HV_WRONG_CED);
		Date expirationDate = DateTimeUtility.parseDateString(cacheExpirationDateTime);
		long expireMillis = expirationDate.getTime();

		int ttl = (int) ((expireMillis - System.currentTimeMillis()) / Constants.SEC_TO_MILLISEC_FACTOR);
		if (ttl == 0) {
			// time to live is 0, 0 causes message to stay forever (internal caching rule)
			ttl = 1; // change to 1 second!
		}
		return ttl;
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
			this.removeMetaAndDataEntries(cachekey, "ClearLoading requested for sid=" + sessionId);
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
