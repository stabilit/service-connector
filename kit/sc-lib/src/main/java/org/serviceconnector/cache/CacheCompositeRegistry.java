package org.serviceconnector.cache;

import java.io.Serializable;

import org.serviceconnector.registry.Registry;

public class CacheCompositeRegistry extends Registry<CacheKey, CacheKey> implements Serializable {

	public static final String ID = "org.serviceconnector.cache.CacheCompositeRegistry";
	
	public CacheCompositeRegistry() {
	}
	
	/**
	 * Put an entry into map.
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
	 */
	protected CacheKey remove(CacheKey key) {
		if (key == null) {
			return null;
		}
		return this.registryMap.remove(key);
	}

}
