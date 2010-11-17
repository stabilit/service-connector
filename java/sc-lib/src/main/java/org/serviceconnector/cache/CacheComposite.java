package org.serviceconnector.cache;

import java.io.Serializable;
import java.util.Date;

/**
 * The Class SCMPCacheRoot.
 */
class CacheComposite implements Serializable {

	/** The size. */
	private int size;

	/** The expiration. */
	private Date expiration;

	/**
	 * Instantiates a new sCMP cache root.
	 */
	public CacheComposite() {
		this(null);
	}

	/**
	 * Instantiates a new sCMP cache root.
	 * 
	 * @param expiration
	 *            the expiration
	 */
	public CacheComposite(Date expiration) {
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
	 * @param size
	 *            the new size
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
	 * @param expiration
	 *            the new expiration
	 */
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SCMPCacheRoot [size=");
		builder.append(size);
		builder.append(", expiration=");
		builder.append(expiration);
		builder.append("]");
		return builder.toString();
	}
	
}