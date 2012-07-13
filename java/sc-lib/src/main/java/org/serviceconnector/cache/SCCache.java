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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

	private Set<String> mgdDataKeysInInitialState;
	private Map<String, List<String>> mgdDataAssignedToGuardian;

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
		this.mgdDataAssignedToGuardian = new HashMap<String, List<String>>();
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

			if (reqMessage.isRequest() == true && metaEntry.isLoadingInitial() == true
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
			String dataEntryCid = metaEntryCid + Constants.SLASH + appendixNr + Constants.PIPE + reqPartNr;
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

			if (metaEntry.isLoadingInitial() == true) {
				// requested message is loading
				if (metaEntry.isLoadingSessionId(sessionId) == true) {
					// requested message is being loaded by current session - continue loading
					int nextPartNrToLoad = metaEntry.getNrOfParts(metaEntryCid + Constants.SLASH + appendixNr + Constants.PIPE
							+ "0") + 1;
					int reqPartNrInt = Integer.parseInt(reqPartNr);
					if (reqPartNrInt != nextPartNrToLoad) {
						// requested partNr does not match current loading state - remove message from cache
						LOGGER.warn("Requested partNr=" + reqPartNr + " does not match current loading state (numberOfParts="
								+ nextPartNrToLoad + ").");
						this.removeMetaAndDataEntries(sessionId, metaEntryCid, "Requested partNr=" + reqPartNr
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
			newMetaEntry.setCacheEntryState(SC_CACHE_ENTRY_STATE.LOADING_INITIAL);

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
		String metaEntryCacheId = resCacheId;
		SCCacheMetaEntry metaEntry = metaDataCacheModule.get(metaEntryCacheId);

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
			this.removeMetaAndDataEntries(sid, metaEntryCacheId,
					"Wrong sid loads MetaEntry, expected sid=" + metaEntry.getLoadingSessionId() + " loading sid= " + sid);
			return;
		}

		// cache the message now!
		try {
			int nrOfAppendix = metaEntry.getNrOfAppendix();
			String baseCid = metaEntryCacheId + Constants.SLASH + nrOfAppendix;
			String currentMsgCid = baseCid + "|0";
			Integer recvCachePartNr = resMessage.getHeaderInt(SCMPHeaderAttributeKey.CACHE_PARTN_NUMBER);

			SC_CACHING_METHOD recvCachingMethod = SC_CACHING_METHOD.getCachingMethod(resMessage
					.getHeader(SCMPHeaderAttributeKey.CACHING_METHOD));
			if (recvCachingMethod == SC_CACHING_METHOD.APPEND && (recvCachePartNr == null || recvCachePartNr == 1)) {
				// first message of appendix - increments number of appendix and create new cache id for message
				nrOfAppendix = metaEntry.incrementNrOfAppendix();
				baseCid = metaEntryCacheId + Constants.SLASH + nrOfAppendix;
				currentMsgCid = baseCid + "|0";
			}

			// part message arrived - increment part number
			int nrOfParts = metaEntry.incrementNrOfPartsForDataMsg(currentMsgCid);
			if (nrOfParts == 0 && nrOfAppendix == 0) {
				// first part received - extract number of appendix to be loaded
				Integer expectedAppendix = resMessage.getHeaderInt(SCMPHeaderAttributeKey.NR_OF_APPENDIX);
				metaEntry.setExpectedAppendix(expectedAppendix);
			}

			// refresh the meta entry
			metaEntry.setLastModified();
			// create cache id for received message
			String partCacheId = baseCid + Constants.PIPE + nrOfParts;
			// set the correct partNr+1 in received message and cache it, partNr points to the next part!
			resMessage.setHeader(SCMPHeaderAttributeKey.CACHE_PARTN_NUMBER, metaEntry.getNrOfParts(currentMsgCid) + 1);

			if (recvCachingMethod == SC_CACHING_METHOD.INITIAL) {
				// managed data received add to data in initial state list
				this.mgdDataKeysInInitialState.add(metaEntryCacheId);
			}

			Statistics.getInstance().incrementCachedMessages(resMessage.getBodyLength());
			if (CacheLogger.isEnabled()) {
				CacheLogger.putMessageToCache(partCacheId, nrOfParts, metaEntry.getLoadingSessionId(), resMessage.getBodyLength(),
						metaEntry.getSCCacheEntryState().name(), recvCachingMethod.name());
			}

			if (resMessage.isPart() == false && metaEntry.getNrOfAppendix() == metaEntry.getExpectedAppendix()) {
				// last part of message received - refresh meta entry state and expire time
				metaEntry.setCacheEntryState(SC_CACHE_ENTRY_STATE.LOADED);

				if (recvCachingMethod == SC_CACHING_METHOD.INITIAL || recvCachingMethod == SC_CACHING_METHOD.APPEND) {
					// managed data received - no expiration time
					metaDataCacheModule.replace(metaEntryCacheId, metaEntry);
				} else {
					// evaluate TTL for meta entry
					int timeToLiveSeconds = this.evalTimeToLiveSeconds(resMessage);
					metaDataCacheModule.replace(metaEntryCacheId, metaEntry, timeToLiveSeconds);
				}
				// remove sessionId from loading sessionIds map
				loadingSessionIds.remove(sid);
				CacheLogger.finishLoadingCacheMessage(metaEntry.getCacheId(), metaEntry.getLoadingSessionId(),
						metaEntry.getNrOfParts(currentMsgCid), metaEntry.getNrOfAppendix());
			} else {
				// refresh meta entry state
				metaDataCacheModule.replace(metaEntryCacheId, metaEntry, metaEntry.getLoadingTimeoutMillis()
						/ Constants.SEC_TO_MILLISEC_FACTOR);
			}
			// cache data entry - no expiration time
			dataCacheModule.putOrUpdate(partCacheId, resMessage);
		} catch (ParseException e) {
			LOGGER.error("Parsing of expirationDate failed", e);
			this.removeMetaAndDataEntries(sid, metaEntryCacheId, "Parsing of expirationDate failed");
			return;
		} catch (SCMPValidatorException e) {
			LOGGER.error("Validation of expirationDate failed", e);
			this.removeMetaAndDataEntries(sid, metaEntryCacheId, "Validation of expirationDate failed");
			return;
		} catch (Exception e) {
			LOGGER.error("Caching message failed", e);
			this.removeMetaAndDataEntries(sid, metaEntryCacheId, "Caching message failed");
			return;
		}
	}

	/**
	 * Cached managed data. Called for caching managed data. Never used in a polling procedure of a client.
	 * 
	 * @param resMessage
	 *            the res message
	 */
	public synchronized void cachedManagedData(SCMPMessage resMessage) {
		String metaEntryCid = resMessage.getCacheId();
		String currGuardian = resMessage.getServiceName();
		String sessionId = resMessage.getSessionId();

		// lookup up meta entry - no managing of cached data possible without
		SCCacheMetaEntry metaEntry = metaDataCacheModule.get(metaEntryCid);

		if (metaEntry == null) {
			// no meta entry found, clean up - no managing of cached data possible
			LOGGER.error("Missing metaEntry message can not be applied to existing data, cid=" + metaEntryCid);
			this.removeMetaAndDataEntries(sessionId, metaEntryCid, "Missing metaEntry message can not be applied to existing data.");
			return;
		}

		String guardianOfCachedMsg = metaEntry.getCacheGuardianName();
		if (guardianOfCachedMsg.equals("unset")) {
			// no cache guardian assigned to message, set it now!
			metaEntry.setCacheGuardianName(currGuardian);
			// remove cache id from initial data list
			boolean deleteResult = this.mgdDataKeysInInitialState.remove(metaEntryCid);

			if (deleteResult == false) {
				// deletion failed - update retrieved for non-managed data
				LOGGER.error("Update retrieved for non-managed data, update ignored. (metaEntryCacheId=" + metaEntryCid
						+ ", resCacheGuardianr=" + currGuardian + ", guardianOfCachedMsg=" + guardianOfCachedMsg + ")");
				return;
			}
			// add cache id to assigned data list
			if (this.mgdDataAssignedToGuardian.containsKey(currGuardian) == false) {
				this.mgdDataAssignedToGuardian.put(currGuardian, new ArrayList<String>());
			}
			this.mgdDataAssignedToGuardian.get(currGuardian).add(metaEntryCid);
		} else if (guardianOfCachedMsg.equals(currGuardian) == false) {
			// managed data retrieved of different cache guardian - ignore data
			LOGGER.trace("Managed data ignored, different cache guardian responsible for treatment. (metaEntryCid=" + metaEntryCid
					+ ", resCacheGuardianr=" + currGuardian + ", guardianOfCachedMsg=" + guardianOfCachedMsg + ")");
			return;
		}

		SC_CACHING_METHOD resCachingMethod = SC_CACHING_METHOD.getCachingMethod(resMessage
				.getHeader(SCMPHeaderAttributeKey.CACHING_METHOD));

		if (resCachingMethod == SC_CACHING_METHOD.NOT_MANAGED) {
			LOGGER.warn("Wrong cachingMethod in received message cmt=" + resCachingMethod + " metaEntryCid= " + metaEntryCid + ".");
			return;
		}

		if (resCachingMethod == SC_CACHING_METHOD.REMOVE) {
			// remove received
			// TODO
			this.removeMetaAndDataEntries("sid unknown", metaEntryCid, "Remove requested from server for cacheId=" + metaEntryCid);
			return;
		}

		if (resCachingMethod == SC_CACHING_METHOD.INITIAL) {
			// initial received - replace existing
			// TODO
			return;
		}

		if (metaEntry.isLoadingInitial() == true) {
			LOGGER.error("Appendix reveived, initial message still loading - delete data to avoid inconsistency.");
			this.removeMetaAndDataEntries(sessionId, metaEntryCid, "Appendix reveived, initial message still loading.");
		} else if (resCachingMethod == SC_CACHING_METHOD.APPEND) {
			if (metaEntry.isLoadingAppendix() == true) {
				// meta entry loading appendix
				int appendixNr = metaEntry.getNrOfAppendix();
				String initialAppendixCid = metaEntryCid + Constants.SLASH + appendixNr + "|0";
				int nrOfPartsForAppendix = metaEntry.incrementNrOfPartsForDataMsg(initialAppendixCid);
				String appendixPartCid = metaEntryCid + Constants.SLASH + appendixNr + Constants.PIPE + nrOfPartsForAppendix;
				// set the correct partNr+1 in received message and cache it, partNr points to the next part!
				resMessage.setHeader(SCMPHeaderAttributeKey.CACHE_PARTN_NUMBER, metaEntry.getNrOfParts(initialAppendixCid) + 1);

				// cache appendix, managed data no expiration
				dataCacheModule.putOrUpdate(appendixPartCid, resMessage);

				if (resMessage.isPart() == true) {
					// part of large appendix received, update meta entry
					metaDataCacheModule.replace(metaEntryCid, metaEntry, metaEntry.getLoadingTimeoutMillis()
							/ Constants.SEC_TO_MILLISEC_FACTOR);
					CacheLogger.putManagedDataToCache(appendixPartCid, currGuardian, appendixNr, nrOfPartsForAppendix);
				} else {
					// end of large appendix received
					metaEntry.setCacheEntryState(SC_CACHE_ENTRY_STATE.LOADED);
					// update meta entry, no expiration time anymore
					metaDataCacheModule.replace(metaEntryCid, metaEntry);
					CacheLogger.finishCachingAppendix(appendixPartCid, currGuardian, nrOfPartsForAppendix);
				}
			} else {
				// meta entry is loaded - appendix received, increment counter
				int appendixNr = metaEntry.incrementNrOfAppendix();
				String appendixCid = metaEntryCid + Constants.SLASH + appendixNr + "|0";
				// increment number of parts for appendix in meta entry
				int nrOfPart = metaEntry.incrementNrOfPartsForDataMsg(appendixCid);

				// update initial message with correct number of appendix
				String initialMsgCid = metaEntryCid + Constants.SLASH + "0|0";
				SCMPMessage initialMsg = dataCacheModule.get(initialMsgCid);
				initialMsg.setHeader(SCMPHeaderAttributeKey.NR_OF_APPENDIX, appendixNr);
				dataCacheModule.putOrUpdate(initialMsgCid, initialMsg);

				if (resMessage.isPart() == true) {
					// start of large appendix received, update meta entry
					metaEntry.setCacheEntryState(SC_CACHE_ENTRY_STATE.LOADING_APPENDIX);
					// set the correct partNr+1 in received message and cache it, partNr points to the next part!
					resMessage.setHeader(SCMPHeaderAttributeKey.CACHE_PARTN_NUMBER, nrOfPart + 1);
					// update meta entry, expiration time
					metaDataCacheModule.replace(metaEntryCid, metaEntry, metaEntry.getLoadingTimeoutMillis()
							/ Constants.SEC_TO_MILLISEC_FACTOR);
					CacheLogger.startCachingAppendix(appendixCid, currGuardian, metaEntry.getLoadingTimeoutMillis()
							/ Constants.SEC_TO_MILLISEC_FACTOR);
				} else {
					// appendix received, update meta entry, no expiration time
					metaDataCacheModule.replace(metaEntryCid, metaEntry);
					CacheLogger.putManagedDataToCache(appendixCid, currGuardian, appendixNr, nrOfPart);
				}
				// cache appendix, managed data no expiration
				dataCacheModule.putOrUpdate(appendixCid, resMessage);
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
		String metaEntryCacheId = metaEntry.getCacheId();
		int nrOfAppendices = metaEntry.getNrOfAppendix();

		// remove data entries - appendices belonging to the message
		for (int i = 0; i <= nrOfAppendices; i++) {
			String appendixCid = metaEntryCacheId + Constants.SLASH + i + "|0";
			int nrOfPartsOfAppendix = metaEntry.getNrOfParts(appendixCid);

			for (int index = 0; index <= nrOfPartsOfAppendix; index++) {
				String dataCid = metaEntryCacheId + Constants.SLASH + i + Constants.PIPE + index;
				dataCacheModule.remove(dataCid);
				CacheLogger.removeMessageFromCache(dataCid, removeReason);
			}
		}
		this.loadingSessionIds.remove(metaEntry.getLoadingSessionId());
	}

	/**
	 * Removes the managed data for guardian. Any data assigned to the specific cache guardian will be deleted.
	 * 
	 * @param cacheGuardian
	 *            the cache guardian
	 */
	public synchronized void removeManagedDataForGuardian(String cacheGuardian) {

		// remove managed data in initial state
		for (String metaEntryCacheId : this.mgdDataKeysInInitialState) {
			this.removeMetaAndDataEntries("unknown", metaEntryCacheId, "Broken Cache Guardian, name=" + cacheGuardian);
		}

		// removed managed data assigned to cache guardian
		List<String> metaEntryCacheIds = this.mgdDataAssignedToGuardian.get(cacheGuardian);
		if (metaEntryCacheIds == null) {
			// no managed data to delete
			return;
		}
		for (String metaEntryCacheId : metaEntryCacheIds) {
			this.removeMetaAndDataEntries("unknown", metaEntryCacheId, "Broken Cache Guardian, name=" + cacheGuardian);
		}
	}

	/**
	 * Evaluates time to live in seconds.
	 * 
	 * @param messageToCache
	 *            the message to cache
	 * @return number of seconds to live
	 * @throws SCMPValidatorException
	 *             the SCMP validator exception
	 * @throws ParseException
	 *             the parse exception
	 */
	private int evalTimeToLiveSeconds(SCMPMessage messageToCache) throws SCMPValidatorException, ParseException {
		// use expire time from header field
		String cacheExpirationDateTime = messageToCache.getHeader(SCMPHeaderAttributeKey.CACHE_EXPIRATION_DATETIME);
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
