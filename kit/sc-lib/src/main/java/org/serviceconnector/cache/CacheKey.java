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

import java.io.Serializable;

/**
 * The Class CacheKey.
 */
public class CacheKey implements Serializable {

	/** The cache id. */
	private String cacheId;

	/**
	 * Instantiates a new sCMP cache key.
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
		result = prime * result
				+ ((cacheId == null) ? 0 : cacheId.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		CacheKey other = (CacheKey) obj;
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
	
	/**
	 * Sets the cache id.
	 *
	 * @param cacheId the new cache id
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

}