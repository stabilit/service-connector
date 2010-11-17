package org.serviceconnector.cache;

import java.io.Serializable;

/**
 * The Class CacheKey.
 */
class CacheKey implements Serializable {

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