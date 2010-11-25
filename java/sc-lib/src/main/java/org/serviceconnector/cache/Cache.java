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

import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.serviceconnector.cache.impl.CacheImplFactory;
import org.serviceconnector.cache.impl.ICacheImpl;
import org.serviceconnector.scmp.SCMPCacheId;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.util.DateTimeUtility;

// TODO: Auto-generated Javadoc
/**
 * The Class Cache.
 */
public class Cache {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(Cache.class);

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
		this.cacheImpl = CacheImplFactory.getDefaultCacheImpl(manager.getScmpCacheConfiguration(), serviceName);
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
	 * Gets the composite.
	 * 
	 * @param cacheId
	 *            the cache id
	 * @return the composite
	 * @throws CacheException
	 *             the cache exception
	 */
	public synchronized CacheComposite getComposite(String cacheId) throws CacheException {
		if (cacheId == null) {
			throw new CacheException("no cache id");
		}
		CacheKey rootCacheKey = null;
		CacheComposite cacheRoot = null;
		// check if this message is part of cache
		rootCacheKey = new CacheKey(cacheId);
		Object value = this.cacheImpl.get(rootCacheKey);
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
	public synchronized CacheMessage getMessage(SCMPMessage msg) throws CacheException {
		String cacheId = msg.getCacheId();
		if (cacheId == null) {
			throw new CacheException("no cache id");
		}
		SCMPCacheId scmpCacheId = new SCMPCacheId(cacheId);
		return getMessage(scmpCacheId);
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
	public synchronized CacheMessage getMessage(SCMPCacheId scmpCacheId) throws CacheException {
		CacheKey rootCacheKey = null;
		CacheComposite cacheRoot = null;
		// check if this message is part of cache
		rootCacheKey = new CacheKey(scmpCacheId.getCacheId());
		Object value = this.cacheImpl.get(rootCacheKey);
		if (value == null) {
			return null;
		}
		if (value != null && value instanceof CacheComposite) {
			cacheRoot = (CacheComposite) value;
		}
		// check if cache is expired or not
		if (cacheRoot.isExpired()) {
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
	 * @param scmpReply
	 *            the scmp reply
	 * @return the sCMP cache id
	 * @throws CacheException
	 *             the sCMP cache exception
	 */
	public synchronized SCMPCacheId putMessage(SCMPMessage scmpReply) throws CacheException {
		try {
			String cacheId = scmpReply.getCacheId();
			if (cacheId == null) {
				throw new CacheException("no cache id");
			}
			SCMPCacheId scmpCacheId = new SCMPCacheId(cacheId);
			String messageSequenceNr = scmpReply.getMessageSequenceNr();
			if (messageSequenceNr == null) {
				throw new CacheException("no message id");
			}
			CacheKey cacheKey = null;
			CacheComposite cacheRoot = null;
			// check if this message is part of cache
			cacheKey = new CacheKey(cacheId);
			Object value = this.cacheImpl.get(cacheKey);
			if (value != null && value instanceof CacheComposite) {
				cacheRoot = (CacheComposite) value;
			}
			String cacheExpirationDateTime = scmpReply.getHeader(SCMPHeaderAttributeKey.CACHE_EXPIRATION_DATETIME);
			Date expirationDateTime = null;
			if (cacheExpirationDateTime != null) {
				expirationDateTime = DateTimeUtility.parseDateString(cacheExpirationDateTime);
			}
			if ((cacheRoot == null)) {
				cacheRoot = new CacheComposite();
				// insert cache root
				cacheRoot.setSize(0);
				cacheRoot.setExpiration(expirationDateTime);
				this.putRegistry(cacheKey);
				this.cacheImpl.put(cacheKey, cacheRoot);
			}
			value = this.cacheImpl.get(cacheKey);
			if (value == null) {
				throw new CacheException("no cache root");
			}
			int newSize = cacheRoot.getSize() + 1;
			cacheRoot.setSize(newSize); // increment size
			SCMPCacheId msgCacheId = new SCMPCacheId(scmpCacheId.getCacheId(), String.valueOf(newSize));
			CacheKey msgCacheKey = new CacheKey(msgCacheId.getFullCacheId());
			CacheMessage scmpCacheMessage = new CacheMessage(messageSequenceNr, scmpReply.getBody());
			this.cacheImpl.put(msgCacheKey, scmpCacheMessage);
			return msgCacheId;
		} catch (CacheException e) {
			throw e;
		} catch (Exception e) {
			throw new CacheException(e.toString());
		}
	}

	/**
	 * Removes the root.
	 * 
	 * @param cacheKey
	 *            the cache key
	 */
	public synchronized void removeComposite(CacheKey cacheKey) {
		// remove all parts
		CacheComposite cacheRoot = null;
		Object value = this.cacheImpl.get(cacheKey);
		if (value != null && value instanceof CacheComposite) {
			cacheRoot = (CacheComposite) value;
		}
		if (cacheRoot == null) {
			return;
		}
		String cacheId = cacheKey.getCacheId();
		int size = cacheRoot.getSize();
		CacheKey localCacheKey = new CacheKey(cacheId);
		this.removeRegistry(cacheKey);
		boolean ret = this.cacheImpl.remove(cacheKey);
		if (ret == false) {
			return;
		}
		SCMPCacheId scmpCacheId = new SCMPCacheId(cacheId);
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
	 * Removes the root if expired.
	 * 
	 * @param cacheKey
	 *            the cache key
	 */
	public synchronized void removeExpiredComposite(CacheKey cacheKey) {
		// remove all parts
		CacheComposite cacheRoot = null;
		Object value = this.cacheImpl.get(cacheKey);
		if (value != null && value instanceof CacheComposite) {
			cacheRoot = (CacheComposite) value;
		}
		if (cacheRoot == null) {
			return;
		}
		if (cacheRoot.isExpired() == false) {
			return;
		}
		String cacheId = cacheKey.getCacheId();
		int size = cacheRoot.getSize();
		CacheKey localCacheKey = new CacheKey(cacheId);
		this.removeRegistry(cacheKey);
		boolean ret = this.cacheImpl.remove(cacheKey);
		if (ret == false) {
			return;
		}
		SCMPCacheId scmpCacheId = new SCMPCacheId(cacheId);
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
	 * Gets the element size. 
	 * Each stored instance in the cache belong to a key and represents 
	 * an element. This is not the size in bytes.
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
	 * @param cacheId the cache id
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
		private SCMPCacheId scmpCacheId;

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
			this.scmpCacheId = new SCMPCacheId(cacheId);
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
}
