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
import org.serviceconnector.util.XMLDumpWriter;

/**
 * The is the main Cache class which represents an active cache instance. For each service instance
 * we have a cache instance assigned.
 * Inside each cache we have composite and message instances.
 * Each composite is identified by its unique {@link CacheId}.
 * Each message is identified by <CacheId>/<SequenceNr> and MUST belong to a cache composite instance.
 * The central cache methods are {@link Cache#putMessage(SCMPMessage)}, {@link Cache#getMessage(CacheId)},
 * {@link Cache#getComposite(CacheId)} and {@link Cache#removeComposite(CacheKey)}.
 * This class is synchronized and thread safe for each instance.
 * This class uses the Bridge Design Pattern delegating the Cache implementation, see {@link ICacheImpl}.
 * The default cache implementation uses the EHCache <a href="http://www.ehcache.org">http://www.ehcache.org</a> library.
 * Inside each cache a {@link CacheCompositeRegistry} instance keeps control over all cache keys. This registry
 * instance is for internal use only.
 */

public class Cache {
	/** The cache manager. */
	private CacheManager manager;

	/** The service name. */
	private String serviceName;

	/** The cache implementation instance (default EHCache <a href="http://www.ehcache.org">http://www.ehcache.org</a>). */
	private ICacheImpl cacheImpl;

	/**
	 * Instantiates a new cache and its bridged implementation.
	 * 
	 * @param manager
	 *            the cache manager
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
	 * This methods returns all composite keys stored in this cache instance.
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
	 * The size means how composites are stored in this cache instance.
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
	 * Gets the composite for given cacheId String. The String will be converted
	 * into an {@link CacheId} instance
	 * 
	 * @param cacheId
	 *            the cache id
	 * @return the composite instance or null
	 * @throws CacheException
	 *             the cache exception
	 */
	public CacheComposite getComposite(String cacheId) throws CacheException {
		if (cacheId == null) {
			throw new CacheException("no cacheId");
		}
		return getComposite(new CacheId(cacheId));
	}

	/**
	 * Gets the composite, see {@link Cache#getComposite(String)}.
	 * 
	 * @param cacheId
	 *            the cache id
	 * @return the composite instance of null
	 * @throws CacheException
	 *             the cache exception
	 */
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
	 * Gets the message for given cacheId String or null if not found. If no sequence nr is specified then
	 * we try to get the first message (sequenceNr is 1).
	 * 
	 * @param cacheId
	 *            the cache id
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
	 * Gets the message for given cacheId or null if not found. If no sequence nr is specified then
	 * an exception is thrown.
	 * 
	 * @param cacheId
	 *            the cache id <CacheId>/<SequenceNr>
	 * @return the message for given cacheId
	 * @throws CacheException
	 *             the cache exception
	 */
	public synchronized CacheMessage getMessage(CacheId cacheId) throws CacheException {
		if (cacheId == null) {
			return null;
		}
		if (cacheId.isCompositeId()) {
			return null;
		}
		CacheKey compositeCacheKey = null;
		CacheComposite cacheComposite = null;
		// check if this message is part of cache
		compositeCacheKey = new CacheKey(cacheId.getCacheId());
		Object value = this.cacheImpl.get(compositeCacheKey);
		if (value == null) {
			return null;
		}
		if (value != null && value instanceof CacheComposite) {
			cacheComposite = (CacheComposite) value;
		}
		// check if cache is expired or not
		if (cacheComposite.isExpired()) {
			CacheLogger.debug("expired composite=" + compositeCacheKey + " found in cache, expiration time="
					+ cacheComposite.getExpiration());
			return null;
		}
		CacheKey msgCacheKey = new CacheKey(cacheId.getFullCacheId());
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
	 * This method puts a new scmp message instance into the cache. The scmp message will be wrapped
	 * by a {@link CacheMessage} instance. Only the body and the {@link SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR} are
	 * kept in the cache message.
	 * Each message requires a composite instance. If no composite instance exists, then a composite instance will be
	 * created and stored in the cache. In an second step the message instance will be added to the cache setting sequence nr to 1.
	 * If a composite instance already exists, the message is stored in the cache setting next free sequence nr.
	 * If the cache composite is expired, an exception is thrown.
	 * If the cache has been loaded an exception is thrown.
	 * 
	 * @param message
	 *            the scmp message instance
	 * @return the cache id <CacheId>/<SequenceNr>
	 * @throws CacheException
	 *             the cache exception
	 */
	public synchronized CacheId putMessage(SCMPMessage message) throws CacheException {
		try {
			String sessionId = message.getSessionId();
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
				// check if cache composite loading session id belongs to message sessionId
				if (cacheComposite.isLoadingSessionId(sessionId) == false) {
					// put message with wrong session id
					CacheLogger.debug("cache composite=" + cacheKey
							+ " message put failed, wrong session id, cache loadingSessionId="
							+ cacheComposite.getLoadingSessionId() + ", message sessionId=" + message.getSessionId());
					throw new CacheExpiredException("cache composite=" + cacheKey
							+ " message put failed, wrong session id, cache loadingSessionId="
							+ cacheComposite.getLoadingSessionId() + ", message sessionId=" + message.getSessionId());
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
				cacheComposite.setHeader(message.getHeader());  // save all header attributes
				cacheComposite.setLoadingSessionId(message.getSessionId());
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
			if (cacheComposite.isPartLoading()) {
				CacheLogger.debug("cache composite=" + cacheKey + " PART_LOADING state changed to LOADING, loadingSessionId="
						+ cacheComposite.getLoadingSessionId() + ", message sessionId=" + message.getSessionId());
				cacheComposite.setCacheState(CACHE_STATE.LOADING);
			}
			int newSize = cacheComposite.getSize() + 1;
			cacheComposite.setSize(newSize); // increment size
			String cacheExpirationDateTime = message.getHeader(SCMPHeaderAttributeKey.CACHE_EXPIRATION_DATETIME);
			if (newSize == 1 && cacheExpirationDateTime == null) {
				this.removeComposite(sessionId, cacheKey);
				throw new CacheException("cacheExpirationDateTime is missing, composite has been removed");
			}
			// check for expiration date time, but only if this is the first message part
			if (newSize == 1 && cacheExpirationDateTime != null) {
				// validate expiration date time format
				ValidatorUtility.validateDateTime(cacheExpirationDateTime, SCMPError.HV_WRONG_CED);
				cacheComposite.setExpiration(cacheExpirationDateTime);
				if (cacheComposite.isExpired()) {
					CacheLogger.info("composite=" + scmpCacheId + " is expired, expiration=" + cacheExpirationDateTime);
					this.removeComposite(sessionId, cacheKey);
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
	 * Removes the cache composite and all its children for given cache id.
	 * 
	 * @param sessionId
	 *            the session id
	 * @param cacheId
	 *            the cache id
	 */
	public void removeComposite(String sessionId, String cacheId) {
		this.removeComposite(sessionId, new CacheKey(cacheId));
	}

	/**
	 * Removes the cache composite and all its children for given cache key.
	 * 
	 * @param sessionId
	 *            the session id
	 * @param cacheKey
	 *            the cache key as String
	 */
	public synchronized void removeComposite(String sessionId, CacheKey cacheKey) {
		// remove all parts
		CacheComposite cacheComposite = null;
		Object value = this.cacheImpl.get(cacheKey);
		if (value != null && value instanceof CacheComposite) {
			cacheComposite = (CacheComposite) value;
		}
		if (cacheComposite == null) {
			return;
		}
		if (cacheComposite.isLoadingSessionId(sessionId) == false) {
			return;
		}
		removeCompositeImmediate(cacheKey, cacheComposite);
		return;
	}

	/**
	 * Removes the composite immediate.
	 * 
	 * @param cacheKey
	 *            the cache key
	 * @param cacheComposite
	 *            the cache composite
	 */
	private void removeCompositeImmediate(CacheKey cacheKey, CacheComposite cacheComposite) {
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
			// don't stop in case of a failure, try to remove all valid sequence nr, if
			// there is one missed, we won't remove all others
			// if (ret == false) {
			// return;
			// }
		}
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
	 * Removes all expired composite instances and all its children.
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
	public Iterator<CacheMessage> iterator(String cacheId) {
		return new CacheIterator(cacheId);
	}

	/**
	 * The Class CacheIterator represents an iterator instance.
	 * This class is for internal use only.
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
				throw new IllegalStateException("invalid iterator");
			}
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
	 * Put given CacheKey into the cache composite registry.
	 * If no registry is part of this cache, then create a new one.
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
	 * Removes given CacheKey from cache composite registry.
	 * If no registry exists for cache key then no action required.
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
	 * Checks if this cache composite has loading state.
	 * If no composite exists in the cache for given cacheId, then false is returned.
	 * 
	 * @param cacheId
	 *            the cache id
	 * @return true, if cache composite is loaded
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
					this.removeExpiredComposite(new CacheKey(cacheId));
					CacheLogger.warn("cache has been removed, reason: cache is loading but loading timeout exceeded, cacheId = "
							+ cacheId);
				}
				// check if last modification timeout expired
				if (cacheComposite.isModificationExpired()) {
					// modification timeout expired, remove this composite from cache
					this.removeExpiredComposite(new CacheKey(cacheId));
					CacheLogger.warn("remove composite while loading cache due timeout expiration, cacheId=" + cacheId);
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
	 * Checks if this cache composite has loaded state.
	 * If no composite exists in the cache for given cacheId, then false is returned.
	 * 
	 * @param cacheId
	 *            the cache id
	 * @return true, if cache composite is loaded
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
	 * This start loading method create a new composite instance and stores them into the cache
	 * setting {@link CACHE_STATE.LOADING} state.
	 * If another composite instance exists for given cacheId, those instance will be replaced.
	 * 
	 * @param message
	 *            the message
	 * @param loadingTimeout
	 *            the loading timeout
	 */
	public void startLoading(SCMPMessage message, int loadingTimeout) {
		try {
			String sessionId = message.getSessionId();
			String cacheId = message.getCacheId();
			CacheComposite cacheComposite = this.getComposite(cacheId);
			CacheKey cacheKey = new CacheKey(cacheId);
			if (cacheComposite != null) {
				// remove this cache composite
				this.removeCompositeImmediate(cacheKey, cacheComposite);
			}
			cacheComposite = new CacheComposite();
			cacheComposite.setHeader(message.getHeader());  // save all header attributes
			cacheComposite.setLoadingSessionId(sessionId);
			cacheComposite.setSize(0);
			cacheComposite.setLoadingTimeout(loadingTimeout);
			if (message.isPollRequest() == true || message.isPart() == false) {
				CacheLogger.debug("start loading cache, cacheId=" + cacheId + ", state is " + CACHE_STATE.LOADING);
				cacheComposite.setCacheState(CACHE_STATE.LOADING);
			} else {
				CacheLogger.debug("start loading cache, cacheId=" + cacheId + ", state is " + CACHE_STATE.PART_LOADING);
				cacheComposite.setCacheState(CACHE_STATE.PART_LOADING);
			}
			this.putRegistry(cacheKey);
			Statistics.getInstance().incrementCachedMessages(0);
			this.cacheImpl.put(cacheKey, cacheComposite);
		} catch (CacheException e) {
			CacheLogger.error("startLoading", e);
		}
		return;
	}
	
	/**
	 * Dump the cache into the xml writer.
	 * 
	 * @param cache
	 *            the cache
	 * @throws Exception
	 *             the exception
	 */
	public void dump(XMLDumpWriter writer) throws Exception {
		writer.writeStartElement("cache");
		writer.writeAttribute("service", this.getServiceName());
		writer.writeAttribute("name", this.getCacheName());
		writer.writeAttribute("elementSize", this.getElementSize());
		writer.writeAttribute("diskStoreSize", this.getDiskStoreSize());
		writer.writeAttribute("memoryStoreSize", this.getMemoryStoreSize());
		Object[] compositeKeys = this.getCompositeKeys();
		if (compositeKeys != null) {
			writer.writeStartElement("messages");
			for (Object key : compositeKeys) {
				CacheKey cacheKey = (CacheKey) key;
				cacheKey.dump(writer, this);
			}
			writer.writeEndElement(); // end of messages
		}
		writer.writeEndElement(); // end of cache
	}

}
