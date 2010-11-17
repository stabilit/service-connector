package org.serviceconnector.cache;

/**
 * The Class SCMPPartCacheKey.
 */
class PartCacheKey extends CacheKey {

	/** The nr. */
	private int nr;

	/**
	 * Instantiates a new sCMP part cache key.
	 * 
	 * @param cacheId
	 *            the cache id
	 * @param nr
	 *            the nr
	 */
	public PartCacheKey(String cacheId, int nr) {
		super(cacheId);
		this.nr = nr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.serviceconnector.scmp.cache.SCMPCache.SCMPCacheKey#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + nr;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.serviceconnector.scmp.cache.SCMPCache.SCMPCacheKey#equals(java
	 * .lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PartCacheKey other = (PartCacheKey) obj;
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
	 * @param nr
	 *            the new nr
	 */
	public void setNr(int nr) {
		this.nr = nr;
	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.scmp.cache.SCMPCache.SCMPCacheKey#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SCMPPartCacheKey [nr=");
		builder.append(nr);
		builder.append("]");
		return builder.toString();
	}
	
}