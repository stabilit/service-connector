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

import java.util.Iterator;

import org.serviceconnector.cache.CacheComposite.CACHE_STATE;
import org.serviceconnector.cache.impl.CacheImplFactory;
import org.serviceconnector.cache.impl.ICacheImpl;
import org.serviceconnector.log.CacheLogger;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.util.Statistics;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class Cache.
 */
public class Cache {
	/** The manager. */
	private CacheManager manager;

	/** The service name. */
	private String serviceName;

	/** The cache impl. */
	private ICacheImpl cacheImpl;

	/**
	 * Instantiates a new SCMP cache.
	 * 
	 * @param manager
	 *            the manager
	 * @param serviceName
	 *            the service name
	 */
	public Cache(CacheManager manager, String serviceName) {
		this.manager = manager;
		this.serviceName = serviceName;
		this.cacheImpl = CacheImplFactory.getDefaultCacheImpl(manager.getCacheConfiguration(), serviceName);
	}

	/**
	 * Gets the composite keys.
	 * 
	 * @return the composite keys
	 */
	public synchronized Object[] getCompositeKeys() {
		CacheCompositeRegistry compositeRegistry = (CacheCompositeRegistry) this.cacheImpl.get(CacheCompositeRegistry.ID);
		if (compositeRegistry == null) {
			return null;
		}
		Object[] keys = compositeRegistry.keySet().toArray();
		return keys;
	}

	/**
	 * Gets the composite size.
	 * 
	 * @return the composite size
	 */
	public synchronized int getCompositeSize() {
		CacheCompositeRegistry compositeRegistry = (CacheCompositeRegistry) this.cacheImpl.get(CacheCompositeRegistry.ID);
		if (compositeRegistry == null) {
			return 0;
		}
		return compositeRegistry.getSize();
	}

	/**
	 * Gets the composite.
	 * 
	 * @param cacheId
	 *            the cache id
	 * @return the composite
	 * @throws CacheException
	 *             the cache exception
	 */
	public CacheComposite getComposite(String cacheId) throws CacheException {
		if (cacheId == null) {
			throw new CacheException("no cacheId");
		}
		return getComposite(new CacheId(cacheId));
	}

	public synchronized CacheComposite getComposite(CacheId cacheId) throws CacheException {
		if (cacheId == null) {
			throw new CacheException("no cacheId");
		}
		CacheKey compositeCacheKey = null;
		@SuppressWarnings("unused")
		CacheComposite cacheComposite = null;
		// check if this message is part of cache
		compositeCacheKey = new CacheKey(cacheId.getCacheId());
		Object value = this.cacheImpl.get(compositeCacheKey);
		if (value == null) {
			return null;
		}
		if (value != null && value instanceof CacheComposite) {
			return (CacheComposite) value;
		}
		return null;
	}

	/**
	 * Gets the message.
	 * 
	 * @param msg
	 *            the msg
	 * @return the message
	 * @throws CacheException
	 *             the cache exception
	 */
	public synchronized CacheMessage getMessage(String cacheId) throws CacheException {
		if (cacheId == null) {
			throw new CacheException("no cacheId");
		}
		CacheId scmpCacheId = new CacheId(cacheId);
		CacheMessage message = this.getMessage(scmpCacheId);
		if (message != null) {
			return message;
		}
		if (scmpCacheId.isCompositeId()) {
			scmpCacheId.setSequenceNr("1");
		}
		message = this.getMessage(scmpCacheId);
		return message;
	}

	/**
	 * Gets the message.
	 * 
	 * @param scmpCacheId
	 *            the scmp cache id
	 * @return the message
	 * @throws CacheException
	 *             the cache exception
	 */
	public synchronized CacheMessage getMessage(CacheId scmpCacheId) throws CacheException {
		if (scmpCacheId == null) {
			return null;
		}
		if (scmpCacheId.isCompositeId()) {
			return null;
		}
		CacheKey compositeCacheKey = null;
		CacheComposite cacheComposite = null;
		// check if this message is part of cache
		compositeCacheKey = new CacheKey(scmpCacheId.getCacheId());
		Object value = this.cacheImpl.get(compositeCacheKey);
		if (value == null) {
			return null;
		}
		if (value != null && value instanceof CacheComposite) {
			cacheComposite = (CacheComposite) value;
		}
		// check if cache is expired or not
		if (cacheComposite.isExpired()) {
			CacheLogger.debug("expired composite=" + compositeCacheKey + " found in cache, expiration time=" + cacheComposite.getExpiration());
			return null;
		}
		CacheKey msgCacheKey = new CacheKey(scmpCacheId.getFullCacheId());
		Object obj = this.cacheImpl.get(msgCacheKey);
		if (obj == null) {
			return null;
		}
		if (obj instanceof CacheMessage) {
			return (CacheMessage) obj;
		}
		throw new CacheException("invalid cache key");
	}

	/**
	 * Put scmp.
	 * 
	 * @param message
	 *            the scmp reply
	 * @return the SCMP cache id
	 * @throws CacheException
	 *             the SCMP cache exception
	 */
	public synchronized CacheId putMessage(SCMPMessage message) throws CacheException {
		try {
			String cacheId = message.getCacheId();
			if (cacheId == null) {
				throw new CacheException("no cacheId");
			}
			CacheId scmpCacheId = new CacheId(cacheId);
			String messageSequenceNr = message.getMessageSequenceNr();
			if (messageSequenceNr == null) {
				throw new CacheException("no messageSequenceNr");
			}
			CacheKey cacheKey = null;
			CacheComposite cacheComposite = null;
			// check if this message is part of cache
			cacheKey = new CacheKey(cacheId);
			Object value = this.cacheImpl.get(cacheKey);
			if (value != null && value instanceof CacheComposite) {
				cacheComposite = (CacheComposite) value;
			}
			if (cacheComposite != null) {
				if (cacheComposite.isLoaded()) {
					// cache is loaded, we MUST replace this cache message
					CacheLogger.warn("cacheId=" + cacheId + " is already loaded!");
					throw new CacheLoadedException("cacheId=" + cacheId + " is already loaded!");
				}
				if (cacheComposite.isExpired()) {
					// we remove this composite from cache
					CacheLogger.debug("cache composite=" + cacheKey + " is expired!");
					throw new CacheExpiredException("cache composite=" + cacheKey + " is expired!");
				}
			}
			if ((cacheComposite == null)) {
				// this is the first message, check if expiration date time is availabled
				String cacheExpirationDateTime = message.getHeader(SCMPHeaderAttributeKey.CACHE_EXPIRATION_DATETIME);
				if (cacheExpirationDateTime == null) {
					throw new CacheException("cacheExpirationDateTime is missing");
				}
				cacheComposite = new CacheComposite();
				// insert cache composite
				cacheComposite.setSize(0);
				cacheComposite.setCacheState(CACHE_STATE.LOADING);
				this.putRegistry(cacheKey);
				Statistics.getInstance().incrementCachedMessages(0);
				this.cacheImpl.put(cacheKey, cacheComposite);
				value = this.cacheImpl.get(cacheKey);
				if (value == null) {
					throw new CacheException("no cache composite (root)");
				}
				cacheComposite = (CacheComposite) value;
			}
			int newSize = cacheComposite.getSize() + 1;
			cacheComposite.setSize(newSize); // increment size
			String cacheExpirationDateTime = message.getHeader(SCMPHeaderAttributeKey.CACHE_EXPIRATION_DATETIME);
			if (newSize == 1 && cacheExpirationDateTime == null) {
				this.removeComposite(cacheKey);
				throw new CacheException("cacheExpirationDateTime is missing, composite has been removed");			    
			}
			// check for expiration date time, but only if this is the first message part
			if (newSize == 1 && cacheExpirationDateTime != null) {
				// validate expiration date time format
				ValidatorUtility.validateDateTime(cacheExpirationDateTime, SCMPError.HV_WRONG_CED);
				cacheComposite.setExpiration(cacheExpirationDateTime);
				if (cacheComposite.isExpired()) {
					CacheLogger.info("composite=" + scmpCacheId + " is expired, expiration=" + cacheExpirationDateTime);
					this.removeComposite(cacheKey);
					throw new CacheException("composite=" + scmpCacheId + " is expired");					
				}
			}
			CacheId msgCacheId = new CacheId(scmpCacheId.getCacheId(), String.valueOf(newSize));
			CacheKey msgCacheKey = new CacheKey(msgCacheId.getFullCacheId());
			CacheMessage scmpCacheMessage = new CacheMessage(messageSequenceNr, message.getBody());
			if (message.isCompressed()) {
				scmpCacheMessage.setCompressed(true);
			}
			scmpCacheMessage.setCacheId(msgCacheKey.getCacheId());
			this.cacheImpl.put(msgCacheKey, scmpCacheMessage);
			// update last modification time
			cacheComposite.setLastModified();
			if (message.isPart() == false && message.isPollRequest() == false) {
				CacheLogger.debug("cache has been loaded, cacheId=" + cacheId);
				cacheComposite.setCacheState(CACHE_STATE.LOADED);
			}
			this.cacheImpl.put(cacheKey, cacheComposite);
			CacheLogger.info("Put cacheId=" + scmpCacheId + " expiration=" + cacheExpirationDateTime);
			return msgCacheId;
		} catch (CacheException e) {
			throw e;
		} catch (Exception e) {
			throw new CacheException(e.toString());
		}
	}

	/**
	 * Removes the cache composite and all its children
	 * 
	 * @param cacheKey
	 *            the cache key
	 */
	public void removeComposite(String cacheId) {
		this.removeComposite(new CacheKey(cacheId));
	}
	
	public synchronized void removeComposite(CacheKey cacheKey) {
		// remove all parts
		CacheComposite cacheComposite = null;
		Object value = this.cacheImpl.get(cacheKey);
		if (value != null && value instanceof CacheComposite) {
			cacheComposite = (CacheComposite) value;
		}
		if (cacheComposite == null) {
			return;
		}
		String cacheId = cacheKey.getCacheId();
		int size = cacheComposite.getSize();
		CacheKey localCacheKey = new CacheKey(cacheId);
		this.removeRegistry(cacheKey);
		Statistics.getInstance().decrementCachedMessages(0);
		boolean ret = this.cacheImpl.remove(cacheKey);
		if (ret == false) {
			return;
		}
		CacheId scmpCacheId = new CacheId(cacheId);
		for (int i = 1; i < size; i++) {
			scmpCacheId.setSequenceNr(String.valueOf(i));
			localCacheKey.setCacheId(scmpCacheId.getFullCacheId());
			ret = this.cacheImpl.remove(localCacheKey);
			if (ret == false) {
				return;
			}
		}
		return;
	}

	/**
	 * Removes the cache composite if expired.
	 * 
	 * @param cacheKey
	 *            the cache key
	 */
	public synchronized void removeExpiredComposite(CacheKey cacheKey) {
		// remove all parts
		CacheComposite cacheComposite = null;
		Object value = this.cacheImpl.get(cacheKey);
		if (value != null && value instanceof CacheComposite) {
			cacheComposite = (CacheComposite) value;
		}
		if (cacheComposite == null) {
			return;
		}
		if (cacheComposite.isExpired() == false && cacheComposite.isLoadingExpired() == false) {
			return;
		}		
		String cacheId = cacheKey.getCacheId();
		int size = cacheComposite.getSize();
		CacheLogger.debug("Remove expired composite=" + cacheKey);
		CacheKey localCacheKey = new CacheKey(cacheId);
		this.removeRegistry(cacheKey);
		boolean ret = this.cacheImpl.remove(cacheKey);
		if (ret == false) {
			return;
		}
		CacheId scmpCacheId = new CacheId(cacheId);
		for (int i = 1; i <= size; i++) {
			scmpCacheId.setSequenceNr(String.valueOf(i));
			localCacheKey.setCacheId(scmpCacheId.getFullCacheId());
			CacheLogger.debug("Remove expired composite" + cacheKey + " cacheId=" + localCacheKey);
			ret = this.cacheImpl.remove(localCacheKey);
			if (ret == false) {
				return;
			}
		}
		CacheLogger.info("Expired composite=" + cacheKey + " succesfully removed");
		return;
	}

	/**
	 * Removes the expired.
	 */
	public synchronized void removeExpired() {
		Object[] keys = this.getCompositeKeys();
		if (keys == null) {
			return;
		}
		for (Object key : keys) {
			CacheKey cacheKey = (CacheKey) key;
			this.removeExpiredComposite(cacheKey);
		}
	}

	/**
	 * Gets the manager.
	 * 
	 * @return the manager
	 */
	public CacheManager getManager() {
		return manager;
	}

	/**
	 * Gets the service name.
	 * 
	 * @return the service name
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * Gets the cache name.
	 * 
	 * @return the cache name
	 */
	public String getCacheName() {
		return this.cacheImpl.getCacheName();
	}

	/**
	 * Gets the element size. Each stored instance in the cache belong to a key and represents an element. This is not the size in
	 * bytes.
	 * 
	 * @return the element size
	 */
	public int getElementSize() {
		return this.cacheImpl.getElementSize();
	}

	/**
	 * Gets the size in bytes.
	 * 
	 * @return the size in bytes
	 */
	public long getSizeInBytes() {
		return this.cacheImpl.getSizeInBytes();
	}

	/**
	 * Gets the memory store size.
	 * 
	 * @return the memory store size
	 */
	public long getMemoryStoreSize() {
		return this.cacheImpl.getMemoryStoreSize();
	}

	/**
	 * Gets the disk store size.
	 * 
	 * @return the disk store size
	 */
	public long getDiskStoreSize() {
		return this.cacheImpl.getDiskStoreSize();
	}

	/**
	 * Gets the iterator.
	 * 
	 * @param cacheId
	 *            the cache id
	 * @return the iterator
	 */
	public Iterator iterator(String cacheId) {
		return new CacheIterator(cacheId);
	}

	/**
	 * The Class CacheIterator.
	 */
	private class CacheIterator implements Iterator<CacheMessage> {

		/** The scmp cache id. */
		private CacheId scmpCacheId;

		/** The index. */
		private int index;

		/** The cache composite. */
		private CacheComposite cacheComposite;

		/**
		 * Instantiates a new cache iterator.
		 * 
		 * @param cacheId
		 *            the cache id
		 */
		public CacheIterator(String cacheId) {
			this.index = 1;
			this.scmpCacheId = new CacheId(cacheId);
			try {
				this.cacheComposite = Cache.this.getComposite(cacheId);
			} catch (CacheException e) {
				this.cacheComposite = null;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			if (scmpCacheId == null || this.cacheComposite == null) {
				return false;
			}
			if (this.index > this.cacheComposite.getSize()) {
				return false;
			}
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#next()
		 */
		@Override
		public CacheMessage next() {
			try {
				this.scmpCacheId.setSequenceNr(String.valueOf(this.index));
				CacheMessage cacheMessage = Cache.this.getMessage(this.scmpCacheId);
				this.index++;
				return cacheMessage;
			} catch (CacheException e) {
			}
			throw new IllegalStateException("invalid iterator");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException("not supported");
		}

	}

	/**
	 * Put registry.
	 * 
	 * @param cacheKey
	 *            the cache key
	 */
	private void putRegistry(CacheKey cacheKey) {
		CacheCompositeRegistry compositeRegistry = (CacheCompositeRegistry) this.cacheImpl.get(CacheCompositeRegistry.ID);
		if (compositeRegistry == null) {
			compositeRegistry = new CacheCompositeRegistry();
			this.cacheImpl.put(CacheCompositeRegistry.ID, compositeRegistry);
		}
		compositeRegistry = (CacheCompositeRegistry) this.cacheImpl.get(CacheCompositeRegistry.ID);
		compositeRegistry.put(cacheKey, cacheKey);
		this.cacheImpl.put(CacheCompositeRegistry.ID, compositeRegistry);
	}

	/**
	 * Removes the registry.
	 * 
	 * @param cacheKey
	 *            the cache key
	 */
	private void removeRegistry(CacheKey cacheKey) {
		CacheCompositeRegistry compositeRegistry = (CacheCompositeRegistry) this.cacheImpl.get(CacheCompositeRegistry.ID);
		if (compositeRegistry == null) {
			return;
		}
		compositeRegistry.remove(cacheKey);
		this.cacheImpl.put(CacheCompositeRegistry.ID, compositeRegistry);
	}

	/**
	 * Checks if is loading.
	 * 
	 * @param cacheId
	 *            the cache id
	 * @return true, if is loading
	 */
	public boolean isLoading(String cacheId) {
		try {
			CacheComposite cacheComposite = this.getComposite(cacheId);
			if (cacheComposite == null) {
				return false;
			}
			if (cacheComposite.isLoading()) {
				// check if loading timeout did expire
				if (cacheComposite.isLoadingExpired()) {
					// modification timeout expired, remove this composite from cache
					this.removeComposite(new CacheKey(cacheId));
					CacheLogger.warn("cache has been removed, reason: cache is loading but loading timeout exceeded, cacheId = "
							+ cacheId);
				}
				// check if last modification timeout expired
				if (cacheComposite.isModificationExpired()) {
					// modification timeout expired, remove this composite from cache
					this.removeComposite(new CacheKey(cacheId));
					CacheLogger.warn("remove composite while loading cache due timeout expiration, cacheId="
							+ cacheId);
				}
				return true;
			}
			return false;
		} catch (CacheException e) {
			CacheLogger.error("isLoading", e);
		}
		return false;
	}

	/**
	 * Checks if is loaded.
	 * 
	 * @param cacheId
	 *            the cache id
	 * @return true, if is loaded
	 */
	public boolean isLoaded(String cacheId) {
		try {
			CacheComposite cacheComposite = this.getComposite(cacheId);
			if (cacheComposite == null) {
				return false;
			}
			if (cacheComposite.isLoaded()) {
				return true;
			}
			return false;
		} catch (CacheException e) {
			CacheLogger.error("isLoaded", e);
		}
		return false;
	}

	/**
	 * Start loading.
	 * 
	 * @param cacheId
	 *            the cache id
	 */
	public void startLoading(String cacheId, int loadingTimeout) {
		try {
			CacheComposite cacheComposite = this.getComposite(cacheId);
			CacheKey cacheKey = new CacheKey(cacheId);
			if (cacheComposite != null) {
				// remove this cache composite
				this.removeComposite(cacheKey);
			}
			cacheComposite = new CacheComposite();
			cacheComposite.setSize(0);
			cacheComposite.setLoadingTimeout(loadingTimeout);
			cacheComposite.setCacheState(CACHE_STATE.LOADING);
			this.putRegistry(cacheKey);
			Statistics.getInstance().incrementCachedMessages(0);
			this.cacheImpl.put(cacheKey, cacheComposite);
			CacheLogger.debug("start loading cache, cacheId=" + cacheId);
		} catch (CacheException e) {
			CacheLogger.error("startLoading", e);
		}
		return;
	}

}
