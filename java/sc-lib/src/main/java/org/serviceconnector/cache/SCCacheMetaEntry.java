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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.serviceconnector.Constants;
import org.serviceconnector.util.DateTimeUtility;

/**
 * The Class SCCacheMetaEntry. The cache meta entry instances are stored in META_DATA_CACHE. It represents either a single message
 * or a group of part messages they belong to a large response. Meta entries are identified by the cache id. SCMPMessages in
 * DATA_CACHE are identified by cacheId/partnNr. The meta entry controls the state of the message. Loading means storing the message
 * in cache has started but is not completed yet. Meta entry count number of parts, saves loading session id and lots of other meta
 * data. The state Loading marks a message to be ready for delivery out of the cache.
 */
public class SCCacheMetaEntry implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6142075299284577556L;
	/** The cache id. */
	private String cacheId = null;
	/** The header containing any header attributes of request SCMP message. */
	private Map<String, String> header;
	/** The cache state. {@link SC_CACHE_ENTRY_STATE} */
	private SC_CACHE_ENTRY_STATE cacheEntryState;
	/** The loading session id. */
	private String loadingSessionId;
	/** The loading timeout (milliseconds). This timeout tells, how long we can stay in the loading state. */
	private int loadingTimeoutMillis;
	/** The creation time, tells what time this meta entry has been created. */
	private Date creationTime;
	/** The last modified time. */
	private Date lastModifiedTime;
	/** The number of appendix the meta entry contains. */
	private int numberOfAppendix;
	/** The expected number of appendix, helper variable in polling appendix procedure. */
	private int expectedAppendix;
	/** The cache guardian name assigned to the meta entry. */
	private String cacheGuardianName;
	/** The data message part info. Holds number of parts available for specific data message meta entry contains. */
	private Map<String, Integer> dataMessagePartInfo;
	/** The expiration date time string. */
	private String expirationDateTimeString;
	/** The caching method. {@link SC_CACHING_METHOD} */
	private SC_CACHING_METHOD cachingMethod;

	/**
	 * Instantiates a new cache meta entry.
	 * 
	 * @param cacheId
	 *            the cache id
	 */
	public SCCacheMetaEntry(String cacheId) {
		this.cacheId = cacheId;
		this.cacheEntryState = SC_CACHE_ENTRY_STATE.UNDEFINDED;
		this.cachingMethod = SC_CACHING_METHOD.NOT_MANAGED;
		this.creationTime = DateTimeUtility.getCurrentTime();
		this.lastModifiedTime = this.creationTime;
		this.loadingSessionId = null;
		this.loadingTimeoutMillis = -1;
		this.header = new HashMap<String, String>();
		this.numberOfAppendix = 0;
		this.expectedAppendix = 0;
		this.dataMessagePartInfo = new HashMap<String, Integer>();
		this.cacheGuardianName = "unset";
	}

	/**
	 * Instantiates a new meta entry. Copy constructor. Make a copy of given object.
	 * 
	 * @param toCopyObject
	 *            the object to be copied
	 */
	public SCCacheMetaEntry(SCCacheMetaEntry toCopyObject) {
		this(toCopyObject.getCacheId());
		// putting the hashMap in order to make a proper copy
		this.header.putAll(toCopyObject.getHeader());
		this.setCacheEntryState(toCopyObject.getSCCacheEntryState());
		this.setLoadingSessionId(toCopyObject.getLoadingSessionId());
		this.setLoadingTimeoutMillis(toCopyObject.getLoadingTimeoutMillis());
		// create new date object in order to make a proper copy of date
		this.creationTime = new Date(toCopyObject.getCreationTime().getTime());
		// create new date object in order to make a proper copy of date
		this.lastModifiedTime = new Date(toCopyObject.getLastModifiedTime().getTime());
		this.numberOfAppendix = toCopyObject.getNrOfAppendix();
		this.setExpectedAppendix(toCopyObject.getExpectedAppendix());
		this.setCacheGuardianName(toCopyObject.getCacheGuardianName());
		// putting the hashMap in order to make a proper copy
		this.dataMessagePartInfo.putAll(toCopyObject.dataMessagePartInfo);
		this.setExpDateTimeStr(toCopyObject.getExpDateTimeStr());
		this.setCachingMethod(toCopyObject.cachingMethod);
	}

	/**
	 * Gets the cache id.
	 * 
	 * @return the cache id
	 */
	public String getCacheId() {
		return cacheId;
	}

	/**
	 * Gets the loading session id.
	 * 
	 * @return the loading session id
	 */
	public String getLoadingSessionId() {
		return loadingSessionId;
	}

	/**
	 * Sets the loading session id.
	 * 
	 * @param loadingSessionId
	 *            the new loading session id
	 */
	public void setLoadingSessionId(String loadingSessionId) {
		this.loadingSessionId = loadingSessionId;
	}

	/**
	 * Checks if is loading session id.
	 * 
	 * @param sessionId
	 *            the session id
	 * @return true, if is loading session id
	 */
	public boolean isLoadingSessionId(String sessionId) {
		if (this.loadingSessionId == null) {
			return false;
		}
		return this.loadingSessionId.equals(sessionId);
	}

	/**
	 * Sets the cache entry state.
	 * 
	 * @param cacheEntryState
	 *            the new cache entry state
	 */
	public synchronized void setCacheEntryState(SC_CACHE_ENTRY_STATE cacheEntryState) {
		this.cacheEntryState = cacheEntryState;
	}

	/**
	 * Gets the SC cache entry state.
	 * 
	 * @return the SC cache entry state
	 */
	public SC_CACHE_ENTRY_STATE getSCCacheEntryState() {
		return this.cacheEntryState;
	}

	/**
	 * Sets the caching method.
	 * 
	 * @param cachingMethod
	 *            the new caching method
	 */
	public void setCachingMethod(SC_CACHING_METHOD cachingMethod) {
		this.cachingMethod = cachingMethod;
	}

	/**
	 * Checks if is managed.
	 * 
	 * @return true, if is managed
	 */
	public boolean isManaged() {
		if (this.cachingMethod == SC_CACHING_METHOD.NOT_MANAGED) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if is loading.
	 * 
	 * @return true, if is loading
	 */
	public boolean isLoading() {
		if (this.cacheEntryState == SC_CACHE_ENTRY_STATE.LOADING) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if is loading appendix.
	 * 
	 * @return true, if is loading appendix
	 */
	public boolean isLoadingAppendix() {
		if (this.cacheEntryState == SC_CACHE_ENTRY_STATE.LOADING_APPENDIX) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if is this composite is loaded and is accessible. {@link SC_CACHE_ENTRY_STATE}
	 * 
	 * @return true, if is loaded
	 */
	public boolean isLoaded() {
		if (this.cacheEntryState == SC_CACHE_ENTRY_STATE.LOADED) {
			return true;
		}
		return false;
	}

	/**
	 * Gets the creation time.
	 * 
	 * @return the creation time
	 */
	public Date getCreationTime() {
		return creationTime;
	}

	/**
	 * Gets the last modified time.
	 * 
	 * @return the last modified time
	 */
	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	/**
	 * Sets the last modified.
	 */
	public void setLastModified() {
		this.lastModifiedTime = DateTimeUtility.getCurrentTime();
	}

	/**
	 * Gets the loading timeout milliseconds.
	 * 
	 * @return the loading timeout milliseconds
	 */
	public int getLoadingTimeoutMillis() {
		return loadingTimeoutMillis;
	}

	/**
	 * Sets the loading timeout milliseconds.
	 * 
	 * @param loadingTimeoutMillis
	 *            the new loading timeout milliseconds
	 */
	public void setLoadingTimeoutMillis(int loadingTimeoutMillis) {
		this.loadingTimeoutMillis = loadingTimeoutMillis;
	}

	/**
	 * Clear the actual header and puts all header attributes from parameter
	 * instance.
	 * 
	 * @param header
	 *            the header
	 */
	public void setHeader(Map<String, String> header) {
		if (header == null) {
			return;
		}
		this.header.clear();
		for (Entry<String, String> entry : header.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (value == null) {
				value = "";
			}
			this.header.put(key, value);
		}
	}

	/**
	 * Gets the header.
	 * 
	 * @return the header
	 */
	public Map<String, String> getHeader() {
		return this.header;
	}

	/**
	 * Gets the number of appendix.
	 * 
	 * @return the number of appendix
	 */
	public int getNrOfAppendix() {
		return this.numberOfAppendix;
	}

	/**
	 * Increment number of appendix.
	 * 
	 * @return incremented number of appendix
	 */
	public int incrementNrOfAppendix() {
		this.numberOfAppendix++;
		return this.numberOfAppendix;
	}

	/**
	 * Gets the cache guardian name.
	 * 
	 * @return the cache guardian name
	 */
	public String getCacheGuardianName() {
		return this.cacheGuardianName;
	}

	/**
	 * Sets the cache guardian name.
	 * 
	 * @param cacheGuardianName
	 *            the new cache guardian name
	 */
	public void setCacheGuardianName(String cacheGuardianName) {
		this.cacheGuardianName = cacheGuardianName;
	}

	/**
	 * Sets the expiration date time string.
	 * 
	 * @param cacheExpirationDateTime
	 *            the new expiration date time string
	 */
	public void setExpDateTimeStr(String cacheExpirationDateTime) {
		this.expirationDateTimeString = cacheExpirationDateTime;
	}

	/**
	 * Gets the expiration date time string.
	 * 
	 * @return the expiration date time string
	 */
	public String getExpDateTimeStr() {
		return this.expirationDateTimeString;
	}

	/**
	 * Gets the number of parts.
	 * 
	 * @param cacheId
	 *            the cache id
	 * @return the number of parts
	 */
	public int getNrOfParts(String cacheId) {
		Integer nrOfParts = this.dataMessagePartInfo.get(cacheId);
		if (nrOfParts == null) {
			return -1;
		}
		return nrOfParts;
	}

	/**
	 * Gets the expected number of appendix.
	 * 
	 * @return the expected number of appendix
	 */
	public int getExpectedAppendix() {
		return this.expectedAppendix;
	}

	/**
	 * Sets the expected number of appendix.
	 * 
	 * @param expectedAppendix
	 *            the new expected number of appendix
	 */
	public void setExpectedAppendix(Integer expectedAppendix) {
		if (expectedAppendix == null) {
			this.expectedAppendix = 0;
		} else {
			this.expectedAppendix = expectedAppendix;
		}
	}

	/**
	 * Increment number of parts for cached data message.
	 * 
	 * @param cacheId
	 *            the cache id
	 * @return the number of parts
	 */
	public int incrementNrOfPartsForDataMsg(String cacheId) {
		Integer nrOfParts = this.dataMessagePartInfo.get(cacheId);
		if (nrOfParts == null) {
			// first part received - initialize
			nrOfParts = 0;
		} else {
			nrOfParts++;
		}
		this.dataMessagePartInfo.put(cacheId, nrOfParts);
		return nrOfParts;
	}

	/**
	 * Number of parts by appendix as string.
	 * 
	 * @return the string
	 */
	public String nrOfPartsByAppendixAsString() {
		StringBuilder sb = new StringBuilder();

		List<String> sortedCids = new ArrayList<String>(this.dataMessagePartInfo.keySet());
		Collections.sort(sortedCids, new CacheIdComparator());

		for (String cid : sortedCids) {
			sb.append(cid + Constants.EQUAL_SIGN + this.dataMessagePartInfo.get(cid) + Constants.AMPERSAND_SIGN);
		}
		return sb.toString();
	}
}