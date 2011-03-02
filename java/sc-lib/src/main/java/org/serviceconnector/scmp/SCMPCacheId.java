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
package org.serviceconnector.scmp;

import org.apache.log4j.Logger;

/**
 * The Class SCMPCacheId. Responsible to provide correct cache id for a specific request/response. Cache id is
 * unique for every message. Format: CacheId / SequenceNr.
 * 
 * @author JTraber
 */
public class SCMPCacheId {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(SCMPCacheId.class);
	
	private String cacheId;
	private String sequenceNr;
	/** The string builder. */
	private StringBuilder fullCacheId;

	/**
	 * Instantiates a new cache id.
	 */
	public SCMPCacheId() {
		this(null,null);
	}

	public SCMPCacheId(String cacheId) {
		String[] splitted = cacheId.split("/");
		if (splitted.length == 2) {
			this.cacheId = splitted[0];
			this.sequenceNr = splitted[1];
		} else {
			this.cacheId = splitted[0];
			this.sequenceNr = null;
		}
	}
	public SCMPCacheId(String cacheId, String sequenceNr) {
		this.cacheId = cacheId;
		this.sequenceNr = sequenceNr;
		this.fullCacheId = null;
	}
	public String getCacheId() {
		return cacheId;
	}
	
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

	public String getSequenceNr() {
		return this.sequenceNr;
	}

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
	
	public void reset() {
		this.cacheId = null;
		this.sequenceNr = null;
		this.fullCacheId = null;
	}

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
