package org.serviceconnector.scmp.cache;

import java.io.Serializable;
import java.util.Date;

import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageId;
import org.serviceconnector.util.DateTimeUtility;

public class SCMPCache {

	private ISCMPCacheImpl cacheImpl;

	public SCMPCache(String serviceName) {
		this.cacheImpl = SCMPCacheImplFactory.getDefaultCacheImpl(serviceName);
	}
	
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

	// member class SCMPCacheKey
	static class SCMPCacheKey implements Serializable {	
		private String cacheId;
		
		public SCMPCacheKey(String cacheId) {
			this.cacheId = cacheId;
		}
		
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((cacheId == null) ? 0 : cacheId.hashCode());
			return result;
		}


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
		
		public String getCacheId() {
			return cacheId;
		}
	}
	
	// member class SCMPCacheHeader
	static class SCMPCacheRoot implements Serializable {
		private int size;
		private Date expiration;
		
		public SCMPCacheRoot() {
			this(null);
		}
		
		public SCMPCacheRoot(Date expiration) {
			this.expiration = expiration;
			this.size = 0;
		}
		
		public int getSize() {
			return size;
		}
		
		public void setSize(int size) {
			this.size = size;
		}
		
		public Date getExpiration() {
			return expiration;
		}
		
		public void setExpiration(Date expiration) {
			this.expiration = expiration;
		}
	}
	
	// member class SCMPPartCacheKey
	static class SCMPPartCacheKey extends SCMPCacheKey {
		private int nr;
		
		public SCMPPartCacheKey(String cacheId, int nr) {
			super(cacheId);
			this.nr = nr;
		}
		
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + nr;
			return result;
		}


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


		public int getNr() {
			return nr;
		}
		
		public void setNr(int nr) {
			this.nr = nr;
		}
	}
}
