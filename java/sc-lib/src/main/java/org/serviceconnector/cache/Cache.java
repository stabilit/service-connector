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

import org.serviceconnector.cache.impl.ICacheImpl;
import org.serviceconnector.cache.impl.CacheImplFactory;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageSequenceNr;
import org.serviceconnector.util.DateTimeUtility;

// TODO: Auto-generated Javadoc
/**
 * The Class Cache.
 */
public class Cache {

	/** The manager. */
	private CacheManager manager;

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
		this.cacheImpl = CacheImplFactory.getDefaultCacheImpl(
				manager.getScmpCacheConfiguration(), serviceName);
	}

	/**
	 * Gets the sCMP.
	 * 
	 * @param msg
	 *            the msg
	 * @return the sCMP
	 * @throws CacheException
	 *             the sCMP cache exception
	 */
	public synchronized CacheMessage getSCMP(SCMPMessage msg)
			throws CacheException {
		String cacheId = msg.getCacheId();
		if (cacheId == null) {
			throw new CacheException("no cache id");
		}
		String seqNr = msg.getMessageSequenceNr();
		if (seqNr == null) {
			throw new CacheException("no message id");
		}
		SCMPMessageSequenceNr msgSequenceNr = SCMPMessage.parseMsgSequenceNr(seqNr);
		CacheKey cacheKey = null;
		CacheComposite cacheRoot = null;
		// check if this message is part of cache
		cacheKey = new CacheKey(cacheId);
		Object value = this.cacheImpl.get(cacheKey);
		if (value == null) {
			return null;
		}
		if (value != null && value instanceof CacheComposite) {
			cacheRoot = (CacheComposite) value;
		}
		//TODO DANI gibt nimmer!
		int partSequenceNr = 0;
//		int partSequenceNr = msgSequenceNr.getPartSequenceNr();
		PartCacheKey cachePartKey = new PartCacheKey(cacheId,
				partSequenceNr);
		CacheMessage scmpCacheMessage = (CacheMessage) this.cacheImpl
				.get(cachePartKey);
		return scmpCacheMessage;
	}

	/**
	 * Put scmp.
	 * 
	 * @param scmpReply
	 *            the scmp reply
	 * @throws CacheException
	 *             the sCMP cache exception
	 */
	public synchronized void putSCMP(SCMPMessage scmpReply)
			throws CacheException {
		try {
			String cacheId = scmpReply.getCacheId();
			if (cacheId == null) {
				throw new CacheException("no cache id");
			}
			String seqNr = scmpReply.getMessageSequenceNr();
			if (seqNr == null) {
				throw new CacheException("no message id");
			}
			SCMPMessageSequenceNr msgSequenceNr = SCMPMessage.parseMsgSequenceNr(seqNr);
			CacheKey cacheKey = null;
			CacheComposite cacheRoot = null;
			// check if this message is part of cache
			cacheKey = new CacheKey(cacheId);
			Object value = this.cacheImpl.get(cacheKey);
			if (value != null && value instanceof CacheComposite) {
				cacheRoot = (CacheComposite) value;
			}
			String cacheExpirationDateTime = scmpReply
					.getHeader(SCMPHeaderAttributeKey.CACHE_EXPIRATION_DATETIME);
			Date expirationDateTime = null;
			if (cacheExpirationDateTime != null) {
				expirationDateTime = DateTimeUtility
						.parseDateString(cacheExpirationDateTime);
			}
			//TODO DANI gibt nimmer!
			int partSequenceNr = 0;
//			int partSequenceNr = msgSequenceNr.getPartSequenceNr();
			if (partSequenceNr > 0) {
				// this is a multi part message (large message)
				// cache root must exist
				if (cacheRoot == null && partSequenceNr > 1) {
					throw new CacheException("no cache root");
				}
			}
			if (partSequenceNr == 0) {
				// this is a simple scmp message (not large)
				if ((cacheRoot != null)) {
					// cache root exists, remove it, included all associated
					// scmp parts
					this.removeRoot(cacheKey);
				} else {
					cacheRoot = new CacheComposite();
				}
				// insert cache root
				cacheRoot.setSize(0);
				cacheRoot.setExpiration(expirationDateTime);
				this.cacheImpl.put(cacheKey, cacheRoot);
				// insert cache part
				PartCacheKey cachePartKey = new PartCacheKey(cacheId, 0);
				CacheMessage scmpCacheMessage = new CacheMessage(
						scmpReply.getBody());
				this.cacheImpl.put(cachePartKey, scmpCacheMessage);
				return;
			}
			if (cacheRoot == null) {
				cacheRoot = new CacheComposite();
			    cacheRoot.setExpiration(expirationDateTime);
			    this.cacheImpl.put(cacheKey, cacheRoot);
			}
			// multi part message
			// check if part sequence nr is valid
			int size = cacheRoot.getSize();
 			if (partSequenceNr != size+1) {
				throw new CacheException("invalid part sequence nr = "
						+ partSequenceNr + ", cache root size = " + size);
			}
			cacheRoot.setSize(partSequenceNr);
		    this.cacheImpl.put(cacheKey, cacheRoot);
			PartCacheKey cachePartKey = new PartCacheKey(cacheId,
					partSequenceNr);
			CacheMessage scmpCacheMessage = new CacheMessage(
					scmpReply.getBody());
			this.cacheImpl.put(cachePartKey, scmpCacheMessage);
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
	public synchronized void removeRoot(CacheKey cacheKey) {
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
		PartCacheKey partCacheKey = new PartCacheKey(cacheId, 0);
		boolean ret = this.cacheImpl.remove(partCacheKey);
		if (ret == false) {
			return;
		}
		for (int i = 1; i < size; i++) {
			partCacheKey.setNr(i);
			ret = this.cacheImpl.remove(partCacheKey);
			if (ret == false) {
				return;
			}
		}
		return;
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

	public String getCacheName() {
		return this.cacheImpl.getCacheName();
	}

	public int getElementSize() {
		return this.cacheImpl.getElementSize();
	}

	public long getMemoryStoreSize() {
		return this.cacheImpl.getMemoryStoreSize();
	}

	public int getDiskStoreSize() {
		return this.cacheImpl.getDiskStoreSize();
	}

}
