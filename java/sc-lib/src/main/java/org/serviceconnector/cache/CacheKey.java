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

import org.serviceconnector.util.XMLDumpWriter;

/**
 * The CacheKey class is a hash key identifying any instance located in the cache (e.g. Composite or Message).
 * The class the wrapper class for any given cacheId String of the format <CacheId>/<SequenceNr> (@see {@link CacheId} The only
 * purpose of this class is support for hashCode generation and equality check. The main usage for this class is
 * inside the @link Cache class.
 */
public class CacheKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4264046172213809960L;

	/** The cache id. */
	private String cacheId;

	/**
	 * Instantiates a new SCMP cache key.
	 * 
	 * @param cacheId
	 *            the cache id
	 */
	public CacheKey(String cacheId) {
		this.cacheId = cacheId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (cacheId == null) {
			result = prime * result + 0;
		} else {
			result = prime * result + cacheId.hashCode();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CacheKey other = (CacheKey) obj;
		if (cacheId == null) {
			if (other.cacheId != null) {
				return false;
			}
		} else if (!cacheId.equals(other.cacheId)) {
			return false;
		}
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

	/**
	 * Sets the cache id.
	 * 
	 * @param cacheId
	 *            the new cache id
	 */
	public void setCacheId(String cacheId) {
		this.cacheId = cacheId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SCMPCacheKey [cacheId=");
		builder.append(cacheId);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Dump the composite into the xml writer.
	 * 
	 * @param cache
	 *            the cache
	 * @param cacheKey
	 *            the cache key
	 * @throws Exception
	 *             the exception
	 */
	public void dump(XMLDumpWriter writer, Cache cache) throws Exception {
		writer.writeStartElement("message");
		String cacheId = this.getCacheId();
		writer.writeAttribute("cacheId", cacheId);
		CacheComposite cacheComposite = cache.getComposite(cacheId);
		if (cacheComposite == null) {
			writer.writeAttribute("exception", "invalid cacheId (not found) but stored in cache registry.");
		} else {
			writer.writeAttribute("state", cacheComposite.getCacheState().toString());
			writer.writeAttribute("expiration", cacheComposite.getExpiration());
			writer.writeAttribute("creation", cacheComposite.getCreationTime().toString());
			writer.writeAttribute("loadingTimeout", cacheComposite.getLoadingTimeout());
			int size = cacheComposite.getSize();
			writer.writeAttribute("size", size);
			writer.writeAttribute("isExpired", cacheComposite.isExpired());
			writer.writeAttribute("isLoaded", cacheComposite.isLoaded());
			writer.writeAttribute("isLoading", cacheComposite.isLoading());
			writer.writeAttribute("isLoadingExpired", cacheComposite.isLoadingExpired());
			// dump all messages
			writer.writeStartElement("message-parts");
			CacheId localCacheId = new CacheId(cacheId);
			for (int i = 1; i <= size; i++) {
				localCacheId.setSequenceNr(String.valueOf(i));
				CacheMessage cacheMessage = cache.getMessage(localCacheId);
				writer.writeStartElement("message-part");
				writer.writeAttribute("cacheId", localCacheId.getFullCacheId());
				if (cacheMessage == null) {
					writer.writeElement("exception", "cache message part=" + localCacheId.getFullCacheId() + " does not exists.");
				} else {
					writer.writeAttribute("messageType", cacheMessage.getMessageType());
					writer.writeAttribute("isCompressed", cacheMessage.isCompressed());
				}
				writer.writeEndElement(); // end of message-part
			}
			writer.writeEndElement(); // end of message-parts
		}
		writer.writeEndElement(); // end of message
	}


}