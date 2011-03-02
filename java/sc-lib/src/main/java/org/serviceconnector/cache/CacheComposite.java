/*
 * Copyright © 2010 STABILIT Informatik AG, Switzerland *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License"); *
 * you may not use this file except in compliance with the License. *
 * You may obtain a copy of the License at *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0 *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS, *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and *
 * limitations under the License. *
 */
package org.serviceconnector.cache;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.log.CacheLogger;
import org.serviceconnector.util.DateTimeUtility;

/**
 * The Class CacheComposite represents the cache head instance for each cache entry.
 * Every message in the cache belongs to a composite instance. Each composite is identified
 * by its unique {@link CacheId} (String). Each message is identified by <CacheId>/<SequenceNr>. The CacheId
 * links to the cache composite instance. A message can't exist without the heading composite instance.
 */
public class CacheComposite implements Serializable {

	private static final long serialVersionUID = 6142075299284577556L;

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private final static Logger LOGGER = Logger.getLogger(CacheComposite.class);

	/**
	 * The Enum CACHE_STATE.
	 */
	public enum CACHE_STATE {
		/** The UNDEFINDED. */
		UNDEFINDED,
		/** The LOADING state, the cache is loading. */
		LOADING,
		/** The PART_LOADING state, the cache is loading part. */
		PART_LOADING,
		/** The LOADED state, the cache has been loaded and is accessible. */
		LOADED;
	};

	/** The size, tells how many messages exists for this composite. */
	private int size;

	/** The creation time, tells what time this composite has been created. */
	private Date creationTime;

	/** The creation time (ms), {@link CacheComposite#creationTime} */
	private long creationTimeMillis;

	/**
	 * The last modified time, tells what time the last messages has been put.
	 * {@link Cache#putMessage(org.serviceconnector.scmp.SCMPMessage)}
	 */
	private Date lastModifiedTime;

	/** The last modified time (ms), {@link CacheComposite#lastModifiedTime} */
	private long lastModifiedTimeMillis;

	/** The expiration date/time string. {@link Constants#SCMP_FORMAT_OF_DATE_TIME} */
	private String expiration;

	/** The expiration timestamp, this is the timestamp in milliseconds sind 1.1.1970 */
	private long expirationTimestamp;

	/** The cache state. {@link CACHE_STATE} */
	private CACHE_STATE cacheState;

	/** The loading session id. */
	private String loadingSessionId;

	/** The loading timeout (ms). This timeout tells, how long we can wait in the loading state. */
	private long loadingTimeout;

	/**
	 * Instantiates a new SCMP cache root.
	 */
	public CacheComposite() {
		this(null);
	}

	/**
	 * Instantiates a new SCMP cache root.
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
		this.loadingSessionId = null;
		this.loadingTimeout = -1L;
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
	 * @param sessiondId
	 *            the sessiond id
	 * @return true, if is loading session id
	 */
	public boolean isLoadingSessionId(String sessionId) {
		if (this.loadingSessionId == null) {
			return false;
		}
		return this.loadingSessionId.equals(sessionId);
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
	 * Sets the expiration date time.
	 * Inside this method the expiration timestamp will be set.
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
		// transform expiration date and get timestamp
		try {
			Date expirationDate = DateTimeUtility.parseDateString(expiration);
			this.expirationTimestamp = expirationDate.getTime();
		} catch (ParseException e) {
			CacheLogger.error("invalidate expiration date/time format=" + expiration, e);
		}
	}

	/**
	 * Gets the expiration timestamp
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
	 * Gets the cache state. {@link CACHE_STATE}
	 * 
	 * @return the cache state
	 */
	public CACHE_STATE getCacheState() {
		return cacheState;
	}

	/**
	 * Checks if this composite is loading. {@link CACHE_STATE}
	 * 
	 * @return true, if is loading
	 */
	public boolean isLoading() {
		if (this.cacheState == CACHE_STATE.LOADING) {
			return true;
		}
	    return this.isPartLoading();
	}

	/**
	 * Checks if this composite is part loading. {@link CACHE_STATE}
	 * 
	 * @return true, if is part loading
	 */
	public boolean isPartLoading() {
		if (this.cacheState == CACHE_STATE.PART_LOADING) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if is this composite is loaded and is accessible. {@link CACHE_STATE}
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
	 * @param loadingTimeout
	 *            the new loading timeout
	 */
	public void setLoadingTimeout(int loadingTimeout) {
		this.loadingTimeout = loadingTimeout;
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
		// Calendar c = Calendar.getInstance();
		// Date currentTime = c.getTime();
		// long currentMillis = currentTime.getTime();
		long currentMillis = System.currentTimeMillis(); // current time in millis UTC
		long expirationMillis = this.getExpirationTimestamp(); // expiration timestamp
		if (currentMillis > expirationMillis) {
			CacheLogger.debug("cache is expired, expirationTime=" + this.expiration + ", currentMillis=" + currentMillis
					+ ", expirationMillis=" + expirationMillis);
			return true;
			// } else {
			// CacheLogger.debug("cache is not expired, expirationTime = " + this.expiration + ", expirationMillis = " +
			// expirationMillis + ", currentTime = " + currentTime + ", currentMillis = " + currentMillis);
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
			CacheLogger.debug("cache loading is expired, creationTime=" + this.creationTime + ", currentMillis=" + currentMillis
					+ ", creationMillis=" + creationMillis);
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
		long lastModifiedExpired = this.lastModifiedTimeMillis + Constants.DEFAULT_CACHE_RESPONSE_TIMEOUT_MILLIS;
		if (lastModifiedExpired < current) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if is last message.
	 * 
	 * @param cacheMessage
	 *            the cache message
	 * @return true, if is last message
	 * @throws CacheException
	 *             the cache exception
	 */
	public boolean isLastMessage(CacheMessage cacheMessage) throws CacheException {
		if (cacheMessage == null) {
			throw new CacheException("cacheMessage is null");
		}
		CacheId cacheId = cacheMessage.getCacheId();
		if (cacheId == null) {
			throw new CacheException("cacheMessage cacheId is null");
		}
		String sSequenceNr = cacheId.getSequenceNr();
		if (sSequenceNr == null) {
			throw new CacheException("cacheMessage cacheId has illegal sequenceNr (null)");
		}
		int sequenceNr = Integer.parseInt(sSequenceNr);
		return sequenceNr == this.size;
	}

	/**
	 * Checks if is valid cache id. This method checks if the cache ids sequence nr is within the valid
	 * range 1 ... cache composite size.
	 * 
	 * @param cacheId
	 *            the cache id
	 * @return true, if is valid cache id
	 */
	public boolean isValidCacheId(CacheId cacheId) {
		if (cacheId == null) {
			return false;
		}
		try {
			int sequenceNr = cacheId.getSequenceNrInt();
			if (sequenceNr <= 0) {
				return false;
			}
			if (sequenceNr > this.size) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
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
}