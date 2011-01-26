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

import org.serviceconnector.registry.Registry;

/**
 * The Class CacheCompositeRegistry
 * 
 * Each Cache has a single instance of this class to control the stored cache keys within the cache.
 * 
 * The registry is kept in the cache for performance purposes only.
 * 
 * This class is for internal use only.
 * 
 *  
 */
class CacheCompositeRegistry extends Registry<CacheKey, CacheKey> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3813494695829470740L;
	
	/** The Constant ID. */
	public static final String ID = "org.serviceconnector.cache.CacheCompositeRegistry";

	/**
	 * Instantiates a new cache composite registry.
	 */
	public CacheCompositeRegistry() {
	}

	/**
	 * Put an entry into the registry map.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	protected void put(CacheKey key, CacheKey value) {
		this.registryMap.put(key, value);
	}

	/**
	 * Gets an entry by key. If key is null - null will be returned.
	 * 
	 * @param key
	 *            the key
	 * @return the map bean
	 */
	protected CacheKey get(CacheKey key) {
		if (key == null) {
			return null;
		}
		return registryMap.get(key);
	}

	/**
	 * Removes an entry by key.
	 * 
	 * @param key
	 *            the key
	 * @return the cache key
	 */
	protected CacheKey remove(CacheKey key) {
		if (key == null) {
			return null;
		}
		return this.registryMap.remove(key);
	}

}
