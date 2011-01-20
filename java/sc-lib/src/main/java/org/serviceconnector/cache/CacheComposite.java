/*
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
 */
package org.serviceconnector.cache;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.log.CacheLogger;
import org.serviceconnector.util.DateTimeUtility;


/**
 * The Class CacheComposite.
 */
public class CacheComposite implements Serializable {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(CacheComposite.class);

	/**
	 * The Enum CACHE_STATE.
	 */
	public enum CACHE_STATE {
		/** The UNDEFINDED. */
		UNDEFINDED,		
		/** The LOADING. */
		LOADING,
		/** The LOADED. */
		LOADED;
	};

	/** The size. */
	private int size;

	/** The creation time. */
	private Date creationTime;

	/** The creation time (ms). */
	private long creationTimeMillis;

	/** The last modified time. */
	private Date lastModifiedTime;

	/** The last modified time (ms). */
	private long lastModifiedTimeMillis;

	/** The expiration. */
	private String expiration;
	
	/** The expiration timestamp UTC */
	private long expirationTimestamp;

	/** The cache state. */
	private CACHE_STATE cacheState;

	/** The loading timeout (ms). */
	private long loadingTimeout;
	/**
	 * Instantiates a new sCMP cache root.
	 */
	public CacheComposite() {
		this(null);
	}

	/**
	 * Instantiates a new sCMP cache root.
	 * 
	 * @param expiration
	 *            the expiration
	 */
	public CacheComposite(String expiration) {
		this.expiration = expiration;
		this.size = 0;
		this.cacheState = CACHE_STATE.UNDEFINDED;
		this.creationTime = DateTimeUtility.getCurrentTime();
		this.creationTimeMillis = this.creationTime.getTime();
		this.lastModifiedTime = this.creationTime;
		this.lastModifiedTimeMillis = this.creationTimeMillis;
		this.loadingTimeout = -1L;
	}

	/**
	 * Gets the size.
	 * 
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Sets the size.
	 * 
	 * @param size
	 *            the new size
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Gets the expiration.
	 * 
	 * @return the expiration
	 */
	public String getExpiration() {
		return expiration;
	}

	/**
	 * Sets the expiration.
	 * 
	 * @param expiration
	 *            the new expiration
	 */
	public void setExpiration(String expiration) {
		this.expiration = expiration;
		if (this.expiration == null) {
			this.expirationTimestamp = 0;
			return;
		}
		// transform expiration date to UTC and get timestamp
		try {
			Date expirationDate = DateTimeUtility.parseDateString(expiration);
			this.expirationTimestamp = expirationDate.getTime();
		} catch (ParseException e) {
			CacheLogger.error("invalidate expiration date/time format", e);
		}		
	}

	/**
	 * Gets the expiration timestamp (UTC).
	 *
	 * @return the expiration timestamp
	 */
	public long getExpirationTimestamp() {
		return expirationTimestamp;
	}
		
	/**
	 * Sets the cache state.
	 * 
	 * @param cacheState
	 *            the new cache state
	 */
	public void setCacheState(CACHE_STATE cacheState) {
		this.cacheState = cacheState;
	}
	
	/**
	 * Gets the cache state.
	 *
	 * @return the cache state
	 */
	public CACHE_STATE getCacheState() {
		return cacheState;
	}

	/**
	 * Checks if is loading.
	 * 
	 * @return true, if is loading
	 */
	public boolean isLoading() {
		return this.cacheState == CACHE_STATE.LOADING;
	}

	/**
	 * Checks if is loaded.
	 * 
	 * @return true, if is loaded
	 */
	public boolean isLoaded() {
		return this.cacheState == CACHE_STATE.LOADED;
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
	 * Gets the creation time millis.
	 *
	 * @return the creation time millis
	 */
	public long getCreationTimestamp() {
		return creationTimeMillis;
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
	 * Gets the last modified time millis.
	 *
	 * @return the last modified time millis
	 */
	public long getLastModifiedTimeMillis() {
		return lastModifiedTimeMillis;
	}
	
	/**
	 * Sets the last modified.
	 */
	public void setLastModified() {
		this.lastModifiedTime = DateTimeUtility.getCurrentTime();
		this.lastModifiedTimeMillis = this.lastModifiedTime.getTime();
		
	}

	/**
	 * Gets the loading timeout.
	 *
	 * @return the loading timeout
	 */
	public long getLoadingTimeout() {
		return loadingTimeout;
	}
	
	/**
	 * Sets the loading timeout.
	 *
	 * @param loadingTimeout the new loading timeout
	 */
	public void setLoadingTimeout(int loadingTimeout) {
		this.loadingTimeout = loadingTimeout;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SCMPCacheRoot [size=");
		builder.append(size);
		builder.append(", expiration=");
		builder.append(expiration);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Checks if is expired.
	 * 
	 * @return true, if is expired
	 */
	public boolean isExpired() {
		if (this.expiration == null) {
			return false;
		}
//		Calendar c = Calendar.getInstance();
//		Date currentTime = c.getTime();
//		long currentMillis = currentTime.getTime();
		long currentMillis = System.currentTimeMillis(); // current time in millis UTC
		long expirationMillis = this.getExpirationTimestamp(); // expiration timestamp 
		if (currentMillis > expirationMillis) {
			CacheLogger.debug("cache is expired, expirationTime = " + this.expiration + ", currentMillis = " + currentMillis + ", expirationMillis = " + expirationMillis);
			return true;
//		} else {
//			CacheLogger.debug("cache is not expired, expirationTime = " + this.expiration + ", expirationMillis = " + expirationMillis + ", currentTime = " + currentTime + ", currentMillis = " + currentMillis);		
		}
		return false;
	}

	/**
	 * Checks if loading timeout did expire.
	 * 
	 * @return true, if loading timeout did expire
	 */
	public boolean isLoadingExpired() {
		if (this.isLoading() == false) {
			return false;
		}
		if (this.loadingTimeout <= 0L) {
			return false;
		}
		if (this.creationTime == null) {
			return false;
		}
		long currentMillis = System.currentTimeMillis(); // current time in millis UTC
		long creationMillis = this.getCreationTimestamp(); // creation timestamp 
		if (currentMillis > creationMillis) {
			CacheLogger.debug("cache loading is expired, creationTime = " + this.creationTime + ", currentMillis = " + currentMillis + ", creationMillis = " + creationMillis);
			return true;
		}
		return false;
	}

	/**
	 * Checks if is modification expired.
	 *
	 * @return true, if is modification expired
	 */
	public boolean isModificationExpired() {
		long current = System.currentTimeMillis();
		return this.lastModifiedTimeMillis + Constants.DEFAULT_CACHE_RESPONSE_TIMEOUT_MILLIS < current;
	}

	/**
	 * Checks if is last message.
	 *
	 * @param cacheMessage the cache message
	 * @return true, if is last message
	 * @throws CacheException the cache exception
	 */
	public boolean isLastMessage(CacheMessage cacheMessage) throws CacheException {
		if (cacheMessage == null) {
			throw  new CacheException("cacheMessage is null");
		}
		CacheId cacheId = cacheMessage.getCacheId();
		if (cacheId == null) {
			throw  new CacheException("cacheMessage cacheId is null");			
		}
		String sSequenceNr = cacheId.getSequenceNr();
		if (sSequenceNr == null) {
			throw  new CacheException("cacheMessage cacheId has illegal sequenceNr (null)");					
		}
		int sequenceNr = Integer.parseInt(sSequenceNr);		
		return sequenceNr == this.size;
	}

}