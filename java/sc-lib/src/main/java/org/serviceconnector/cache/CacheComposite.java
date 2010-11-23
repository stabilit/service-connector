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
import java.util.Date;

import org.serviceconnector.util.DateTimeUtility;

/**
 * The Class SCMPCacheRoot.
 */
public class CacheComposite implements Serializable {

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

	public boolean isExpired() {
		if (this.expiration == null) {
			return false;
		}
		long currentMillis = System.currentTimeMillis();
		long expirationMillis = this.expiration.getTime();
		return currentMillis > expirationMillis;
	}
	
}