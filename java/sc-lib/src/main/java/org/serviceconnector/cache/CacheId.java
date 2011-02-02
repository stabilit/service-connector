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
 * The Class SCMPCacheId. Responsible to provide correct cache id for a specific request/response. Cache id is unique for every
 * message. Format: CacheId / SequenceNr.
 * 
 * Any Cache entry belongs to a composite instance and assigned messages. Each composite will be identified by a unique 
 * CacheId without any sequence nr.
 * 
 * Every message is identified by CacheId and Sequence Nr (<CacheId>/<SequenceNr>). The CacheId of each message links
 * to the composite instance, the sequence nr identifies the message starting at point 1. The sequence nr will be 
 * incremented for each additional message with same CacheId.
 *  
 * 
 * @author JTraber
 */
public class CacheId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1147590394007400404L;

	/** The Constant logger. */
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(CacheId.class);

	private String cacheId = null;
	private String sequenceNr = null;
	private StringBuilder fullCacheId = null;

	/**
	 * Instantiates a new cache id.
	 */
	public CacheId() {
		this(null, null);
	}

	/**
	 * Instantiates a new cache id parsing given string of the format <CacheId>/<SequenceNr>
	 * If the string does not contains the / separator then no sequence nr is parsed and its content
	 * belongs to the cache id.
	 * The sequence number must be a number >= 1.
	 *
	 * @param cacheId the cache id
	 */
	public CacheId(String cacheId) {
		if (cacheId == null) {
			return;
		}		
		String[] splitted = cacheId.split("/");
		if (splitted == null) {
			return;
		}
		if (splitted.length <= 0 || splitted.length > 2) {
			return;
		}
		if (splitted.length == 2) {
			this.cacheId = splitted[0];
			this.sequenceNr = splitted[1];
		} else {
			this.cacheId = splitted[0];
			this.sequenceNr = null;
		}
	}

	/**
	 * Instantiates a new SCMP cache id.
	 * 
	 * @param cacheId
	 *            the cache id
	 * @param sequenceNr
	 *            the sequence nr
	 */
	public CacheId(String cacheId, String sequenceNr) {
		this.cacheId = cacheId;
		this.sequenceNr = sequenceNr;
		this.fullCacheId = null;
	}

	/**
	 * Checks if both cache ids were equal, only the cache id is checked and any sequence nr 
	 * will be ignored.
	 * 
	 * @param cacheId
	 *            the cache id
	 * @return true, if successful
	 */
	public boolean equalsCacheId(CacheId cacheId) {
		if (this.cacheId == null || cacheId == null) {
			return false;
		}
		return this.cacheId.equals(cacheId.cacheId);
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
	 * Gets the full cache id. The full cacheid has the format <CacheId>/<SequenceNr>. If no
	 * sequence nr exists, then the CacheId without any / separator will be returned.
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
	 * Returns the sequence number or null.
	 * 
	 * @return the sequence nr
	 */
	public String getSequenceNr() {
		return this.sequenceNr;
	}

	/**
	 * Gets the sequence nr as an Integer (int). 
	 * If no sequence number is specified, then 0 will be returned. 
	 * A valid sequence number is >= 1.
	 * 
	 * @return the sequence nr int
	 */
	public int getSequenceNrInt() {
		int nr;
		try {
			nr = Integer.parseInt(this.sequenceNr);
		} catch (NumberFormatException e) {
			return 0;
		}
		return nr;
	}

	/**
	 * Sets the sequence nr.
	 * This method replaces any existing sequence nr and recomputes the full cache id. 
	 * 
	 * @param sequenceNr
	 *            the new sequence nr
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
	 * This method increments any existing sequence number or starts at position 1
	 * returning the same CacheId instance (this).
	 * 
	 * @return the cache id
	 */
	public CacheId nextSequence() {
		if (this.sequenceNr == null) {
			this.setSequenceNr(String.valueOf(1));
			return this;
		}
		this.setSequenceNr(String.valueOf(this.getSequenceNrInt() + 1));
		return this;
	}

	/**
	 * Check if this CacheId belongs to a composite id. A composite id does not contain any
	 * sequence number.
	 * 
	 * @return true, if is composite id
	 * @throws CacheException
	 *             the cache exception
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
	 * Reset any attributes to null.
	 */
	public void reset() {
		this.cacheId = null;
		this.sequenceNr = null;
		this.fullCacheId = null;
	}

	/*
	 * (non-Javadoc)
	 * 
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
