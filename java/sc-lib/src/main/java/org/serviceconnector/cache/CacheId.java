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

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * The Class SCMPCacheId. Responsible to provide correct cache id for a specific request/response. Cache id is
 * unique for every message. Format: CacheId / SequenceNr.
 * 
 * @author JTraber
 */
public class CacheId implements Serializable {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(CacheId.class);
	
	private String cacheId;
	private String sequenceNr;
	/** The string builder. */
	private StringBuilder fullCacheId;

	/**
	 * Instantiates a new cache id.
	 */
	public CacheId() {
		this(null,null);
	}

	public CacheId(String cacheId) {
		String[] splitted = cacheId.split("/");
		if (splitted.length == 2) {
			this.cacheId = splitted[0];
			this.sequenceNr = splitted[1];
		} else {
			this.cacheId = splitted[0];
			this.sequenceNr = null;
		}
	}
	
	/**
	 * Instantiates a new sCMP cache id.
	 *
	 * @param cacheId the cache id
	 * @param sequenceNr the sequence nr
	 */
	public CacheId(String cacheId, String sequenceNr) {
		this.cacheId = cacheId;
		this.sequenceNr = sequenceNr;
		this.fullCacheId = null;
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
	 * Gets the full cache id.
	 *
	 * @return the full cache id
	 */
	public String getFullCacheId() {
		if (this.fullCacheId != null) {
			return this.fullCacheId.toString();
		}
		this.fullCacheId = new StringBuilder();
		if (this.sequenceNr == null) {
			// no part SCMP has been sent, partSequenceNr irrelevant
			this.fullCacheId.append(this.cacheId);
			return this.fullCacheId.toString();
		}
		this.fullCacheId.append(this.cacheId);
		this.fullCacheId.append("/");
		this.fullCacheId.append(this.sequenceNr);
		return this.fullCacheId.toString();
	}

	/**
	 * Gets the sequence nr.
	 *
	 * @return the sequence nr
	 */
	public String getSequenceNr() {
		return this.sequenceNr;
	}

	/**
	 * Sets the sequence nr.
	 *
	 * @param sequenceNr the new sequence nr
	 */
	public void setSequenceNr(String sequenceNr) {
		this.sequenceNr = sequenceNr;
		if (this.fullCacheId == null) {
			this.fullCacheId = new StringBuilder();
		}
		this.fullCacheId.setLength(0);
		this.fullCacheId.append(this.cacheId);
		this.fullCacheId.append("/");
		this.fullCacheId.append(this.sequenceNr);
	}

	/**
	 * Checks if is composite id.
	 *
	 * @return true, if is composite id
	 * @throws CacheException the cache exception
	 */
	public boolean isCompositeId() throws CacheException {
		String cacheId = this.getCacheId();
		String fullCacheId = this.getFullCacheId();
		if (cacheId == null) {
			throw new CacheException("invalid cacheId (null)");
		}
		return cacheId.equals(fullCacheId);
	}

	/**
	 * Reset.
	 */
	public void reset() {
		this.cacheId = null;
		this.sequenceNr = null;
		this.fullCacheId = null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SCMPCacheId [cacheId=");
		builder.append(cacheId);
		builder.append(", sequenceNr=");
		builder.append(sequenceNr);
		builder.append(", fullCacheId=");
		builder.append(fullCacheId);
		builder.append("]");
		return builder.toString();
	}

	
}
