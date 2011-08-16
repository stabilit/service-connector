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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
	/** The numberOfParts, tells how many messages exists for this meta entry. */
	private int numberOfParts;
	/** The creation time, tells what time this meta entry has been created. */
	private Date creationTime;
	/** The last modified time. */
	private Date lastModifiedTime;
	/** The cache state. {@link SC_CACHE_ENTRY_STATE} */
	private volatile SC_CACHE_ENTRY_STATE cacheEntryState;
	/** The loading session id. */
	private String loadingSessionId;
	/** The loading timeout (ms). This timeout tells, how long we can stay in the loading state. */
	private int loadingTimeoutMillis;
	/** The header containing any header attributes of request SCMP message. */
	private Map<String, String> header;
	/** The cache key. */
	private String cacheKey = null;
	/** The cache id. */
	private String cacheId = null;

	/**
	 * Instantiates a new cache meta entry.
	 * 
	 * @param cacheKey
	 *            the cache id
	 */
	public SCCacheMetaEntry(String cacheKey) {
		this.cacheKey = cacheKey;
		this.numberOfParts = 0;
		this.cacheEntryState = SC_CACHE_ENTRY_STATE.UNDEFINDED;
		this.creationTime = DateTimeUtility.getCurrentTime();
		this.lastModifiedTime = this.creationTime;
		this.loadingSessionId = null;
		this.cacheId = null;
		this.loadingTimeoutMillis = -1;
		this.header = new HashMap<String, String>();
	}

	/**
	 * Gets the cache key.
	 * 
	 * @return the cache key
	 */
	public String getCacheKey() {
		return cacheKey;
	}

	/**
	 * Sets the cache id.
	 * 
	 * @param cacheId
	 *            the new cache id
	 */
	public void setCacheId(String cacheId) {
		this.cacheId = cacheId;
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
	 * Gets the number of parts.
	 * 
	 * @return the number of parts
	 */
	public int getNumberOfParts() {
		return numberOfParts;
	}

	/**
	 * Sets the number of parts.
	 * 
	 * @param numberOfParts
	 *            the new number of parts
	 */
	public void setNumberOfParts(int numberOfParts) {
		this.numberOfParts = numberOfParts;
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
	 * Checks if this composite is loading. {@link SC_CACHE_ENTRY_STATE}
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
		return header;
	}
}