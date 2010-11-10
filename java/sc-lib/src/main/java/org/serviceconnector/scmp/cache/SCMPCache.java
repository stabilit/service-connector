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
package org.serviceconnector.scmp.cache;

import java.io.Serializable;
import java.util.Date;

import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageId;
import org.serviceconnector.util.DateTimeUtility;

// TODO: Auto-generated Javadoc
/**
 * The Class SCMPCache.
 */
public class SCMPCache {

	/** The manager. */
	private SCMPCacheManager manager;
	
	/** The cache impl. */
	private ISCMPCacheImpl cacheImpl;

	/**
	 * Instantiates a new sCMP cache.
	 *
	 * @param manager the manager
	 * @param serviceName the service name
	 */
	public SCMPCache(SCMPCacheManager manager, String serviceName) {
		this.manager = manager;
		this.cacheImpl = SCMPCacheImplFactory.getDefaultCacheImpl(manager.getScmpCacheConfiguration(), serviceName);
	}
		
	/**
	 * Gets the sCMP.
	 *
	 * @param msg the msg
	 * @return the sCMP
	 * @throws SCMPCacheException the sCMP cache exception
	 */
	public synchronized SCMPCacheMessage getSCMP(SCMPMessage msg) throws SCMPCacheException {
		String cacheId = msg.getCacheId();
		if (cacheId == null) {
			throw new SCMPCacheException("no cache id");
		}
		String messageId = msg.getMessageId();
		if (messageId == null) {
			throw new SCMPCacheException("no message id");
		}
		SCMPMessageId msgId = SCMPMessage.parseMessageId(messageId);
		SCMPCacheKey cacheKey = null;
		SCMPCacheRoot cacheRoot = null;
		// check if this message is part of cache
		cacheKey = new SCMPCacheKey(cacheId);		
		Object value = this.cacheImpl.get(cacheKey);
		if (value == null) {
			return null;
		}
		if (value != null && value instanceof SCMPCacheRoot) {
			cacheRoot = (SCMPCacheRoot) value;
		}				
		int partSequenceNr = msgId.getPartSequenceNr();
		SCMPPartCacheKey cachePartKey = new SCMPPartCacheKey(cacheId, partSequenceNr);
		SCMPCacheMessage scmpCacheMessage = (SCMPCacheMessage) this.cacheImpl.get(cachePartKey);
		return scmpCacheMessage;
	}

	/**
	 * Put scmp.
	 *
	 * @param scmpReply the scmp reply
	 * @throws SCMPCacheException the sCMP cache exception
	 */
	public synchronized void putSCMP(SCMPMessage scmpReply) throws SCMPCacheException {
		try {
			String cacheId = scmpReply.getCacheId();
			if (cacheId == null) {
				throw new SCMPCacheException("no cache id");
			}
			String messageId = scmpReply.getMessageId();
			if (messageId == null) {
				throw new SCMPCacheException("no message id");
			}
			SCMPMessageId msgId = SCMPMessage.parseMessageId(messageId);
			SCMPCacheKey cacheKey = null;
			SCMPCacheRoot cacheRoot = null;
			// check if this message is part of cache
			cacheKey = new SCMPCacheKey(cacheId);
			Object value = this.cacheImpl.get(cacheKey);
			if (value != null && value instanceof SCMPCacheRoot) {
				cacheRoot = (SCMPCacheRoot) value;
			}				
			String cacheExpirationDateTime = scmpReply.getHeader(SCMPHeaderAttributeKey.CACHE_EXPIRATION_DATETIME);
			Date expirationDateTime = null;
			if (cacheExpirationDateTime != null) {
               expirationDateTime = DateTimeUtility.parseDateString(cacheExpirationDateTime);				
			}
			int partSequenceNr = msgId.getPartSequenceNr();
			if (partSequenceNr > 0) {
				// this is a multi part message (large message)
				// cache root must exist
				if (cacheRoot == null) {
					throw new SCMPCacheException("no cache root");
				}
			}
			if (partSequenceNr == 0) {
				// this is a simple scmp message (not large)
				if ((cacheRoot != null)) {
					// cache root exists, remove it, included all associated scmp parts 
					this.removeRoot(cacheKey);
				} else {
					cacheRoot = new SCMPCacheRoot();
				}
				// insert cache root
				cacheRoot.setSize(0);
				cacheRoot.setExpiration(expirationDateTime);
				this.cacheImpl.put(cacheKey, cacheRoot);
				// insert cache part
				SCMPPartCacheKey cachePartKey = new SCMPPartCacheKey(cacheId, 0);
				SCMPCacheMessage scmpCacheMessage = new SCMPCacheMessage(scmpReply.getBody());
				this.cacheImpl.put(cachePartKey, scmpCacheMessage);
				return;
			}
			// multi part message
			// check if part sequence nr is valid
			int size = cacheRoot.getSize();
			if (partSequenceNr != size) {
				throw new SCMPCacheException("invalid part sequence nr = " + partSequenceNr + ", cache root size = " + size);
			}
			cacheRoot.setSize(partSequenceNr);
			SCMPPartCacheKey cachePartKey = new SCMPPartCacheKey(cacheId, partSequenceNr);
			SCMPCacheMessage scmpCacheMessage = new SCMPCacheMessage(scmpReply.getBody());
			this.cacheImpl.put(cachePartKey, scmpCacheMessage);
		} catch (SCMPCacheException e) {
			throw e;
		} catch (Exception e) {
			throw new SCMPCacheException(e.toString());
		}
	}
	
	/**
	 * Removes the root.
	 *
	 * @param cacheKey the cache key
	 */
	public synchronized void removeRoot(SCMPCacheKey cacheKey) {
		// remove all parts
		SCMPCacheRoot cacheRoot = null;
		Object value = this.cacheImpl.get(cacheKey);
		if (value != null && value instanceof SCMPCacheRoot) {
			cacheRoot = (SCMPCacheRoot) value;
		}
		if (cacheRoot == null) {
			return;
		}
		String cacheId = cacheKey.getCacheId();
		int size = cacheRoot.getSize();
		SCMPPartCacheKey partCacheKey = new SCMPPartCacheKey(cacheId, 0);
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
	public SCMPCacheManager getManager() {
		return manager;
	}

	// member class SCMPCacheKey
	/**
	 * The Class SCMPCacheKey.
	 */
	static class SCMPCacheKey implements Serializable {	
		
		/** The cache id. */
		private String cacheId;
		
		/**
		 * Instantiates a new sCMP cache key.
		 *
		 * @param cacheId the cache id
		 */
		public SCMPCacheKey(String cacheId) {
			this.cacheId = cacheId;
		}
		
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((cacheId == null) ? 0 : cacheId.hashCode());
			return result;
		}


		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SCMPCacheKey other = (SCMPCacheKey) obj;
			if (cacheId == null) {
				if (other.cacheId != null)
					return false;
			} else if (!cacheId.equals(other.cacheId))
				return false;
			return true;
		}
		
		/**
		 * Gets the cache id.
		 *
		 * @return the cache id
		 */
		public String getCacheId() {
			return cacheId;
		}
	}
	
	// member class SCMPCacheHeader
	/**
	 * The Class SCMPCacheRoot.
	 */
	static class SCMPCacheRoot implements Serializable {
		
		/** The size. */
		private int size;
		
		/** The expiration. */
		private Date expiration;
		
		/**
		 * Instantiates a new sCMP cache root.
		 */
		public SCMPCacheRoot() {
			this(null);
		}
		
		/**
		 * Instantiates a new sCMP cache root.
		 *
		 * @param expiration the expiration
		 */
		public SCMPCacheRoot(Date expiration) {
			this.expiration = expiration;
			this.size = 0;
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
		 * @param size the new size
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
		 * @param expiration the new expiration
		 */
		public void setExpiration(Date expiration) {
			this.expiration = expiration;
		}
	}
	
	// member class SCMPPartCacheKey
	/**
	 * The Class SCMPPartCacheKey.
	 */
	static class SCMPPartCacheKey extends SCMPCacheKey {
		
		/** The nr. */
		private int nr;
		
		/**
		 * Instantiates a new sCMP part cache key.
		 *
		 * @param cacheId the cache id
		 * @param nr the nr
		 */
		public SCMPPartCacheKey(String cacheId, int nr) {
			super(cacheId);
			this.nr = nr;
		}
		
		
		/* (non-Javadoc)
		 * @see org.serviceconnector.scmp.cache.SCMPCache.SCMPCacheKey#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + nr;
			return result;
		}


		/* (non-Javadoc)
		 * @see org.serviceconnector.scmp.cache.SCMPCache.SCMPCacheKey#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			SCMPPartCacheKey other = (SCMPPartCacheKey) obj;
			if (nr != other.nr)
				return false;
			return true;
		}


		/**
		 * Gets the nr.
		 *
		 * @return the nr
		 */
		public int getNr() {
			return nr;
		}
		
		/**
		 * Sets the nr.
		 *
		 * @param nr the new nr
		 */
		public void setNr(int nr) {
			this.nr = nr;
		}
	}
}
