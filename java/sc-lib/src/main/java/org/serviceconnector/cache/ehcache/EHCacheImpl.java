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
package org.serviceconnector.cache.ehcache;

import java.util.Date;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

import org.serviceconnector.cache.ISCCacheModule;

/**
 * The Class EHCacheSCMPCacheImpl. This class wraps the EHCache implementation of a cache.
 * 
 * @param <T>
 */
public class EHCacheImpl<T> implements ISCCacheModule<T> {
	/** The cache. */
	private Cache ehCache;

	/**
	 * Instantiates a new EHCache.
	 * 
	 * @param cacheConfiguration
	 *            the cache configuration
	 */
	EHCacheImpl(CacheConfiguration cacheConfiguration) {
		this.ehCache = new Cache(cacheConfiguration);
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public T get(Object key) {
		Element element = this.ehCache.get(key);
		if (element == null) {
			return null;
		}
		return (T) element.getObjectValue();
	}

	/** {@inheritDoc} */
	@Override
	public void putOrUpdate(Object key, T value, int timeToLiveSeconds) {
		// key, value, eternal, timeToIdle (0 = unlimited), timeToLive
		Element element = new Element(key, value, false, 0, timeToLiveSeconds);
		this.ehCache.put(element);
	}

	@Override
	public void replace(Object key, T value, int timeToLiveSeconds) {
		// key, value, eternal, timeToIdle (0 = unlimited), timeToLive
		Element element = new Element(key, value, false, 0, timeToLiveSeconds);
		this.ehCache.replace(element);
	}

	Cache getEhCache() {
		return this.ehCache;
	}

	@Override
	public Date getExpirationTime(String key) {
		Element element = this.ehCache.get(key);
		return new Date(element.getExpirationTime());
	}

	@Override
	public Date getCreationTime(String key) {
		Element element = this.ehCache.get(key);
		return new Date(element.getCreationTime());
	}

	@Override
	public Date getLastAccessTime(String key) {
		Element element = this.ehCache.get(key);
		return new Date(element.getLastAccessTime());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getKeyList() {
		// EHCache returns list of keys whis did not expire yet
		return this.ehCache.getKeysWithExpiryCheck();
	}

	/** {@inheritDoc} */
	@Override
	public String getCacheModuleName() {
		return this.ehCache.getName();
	}

	/** {@inheritDoc} */
	@Override
	public long getNumberOfMessagesInStore() {
		return this.ehCache.getMemoryStoreSize();
	}

	/** {@inheritDoc} */
	@Override
	public long getNumberOfMessagesInDiskStore() {
		return this.ehCache.getDiskStoreSize();
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public T remove(Object key) {
		Element element = this.ehCache.get(key);
		this.ehCache.remove(key);
		if (element == null) {
			return null;
		}
		return (T) element.getObjectValue();
	}

	/** {@inheritDoc} */
	@Override
	public void removeAll() {
		this.ehCache.removeAll();
	}

	@Override
	public void destroy() {
		this.ehCache.dispose();
	}
}
