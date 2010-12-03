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
import java.util.Date;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.util.DateTimeUtility;

// TODO: Auto-generated Javadoc
/**
 * The Class CacheComposite.
 */
public class CacheComposite implements Serializable {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(CacheComposite.class);

	/**
	 * The Enum CACHE_STATE.
	 */
	enum CACHE_STATE {
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
	private Date expiration;

	/** The cache state. */
	private CACHE_STATE cacheState;

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
	public CacheComposite(Date expiration) {
		this.expiration = expiration;
		this.size = 0;
		this.cacheState = CACHE_STATE.UNDEFINDED;
		this.creationTime = DateTimeUtility.getCurrentTime();
		this.creationTimeMillis = this.creationTime.getTime();
		this.lastModifiedTime = this.creationTime;
		this.lastModifiedTimeMillis = this.creationTimeMillis;
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
	public Date getExpiration() {
		return expiration;
	}

	/**
	 * Sets the expiration.
	 * 
	 * @param expiration
	 *            the new expiration
	 */
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
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
	public long getCreationTimeMillis() {
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
		long currentMillis = System.currentTimeMillis();
		long expirationMillis = this.expiration.getTime();
		return currentMillis > expirationMillis;
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