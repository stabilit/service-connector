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

import org.apache.log4j.Logger;
import org.serviceconnector.cache.ISCCacheModule;
import org.serviceconnector.cache.SCCache;
import org.serviceconnector.cache.SCCacheMetaEntry;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPPart;

/**
 * The Class EHCacheSCMPCacheImpl. This class wraps the EHCache implementation of a cache.
 * 
 * @param <T>
 */
public class EHCacheImpl<T> implements ISCCacheModule<T> {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(SCCache.class);
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
		ehCache.getCacheEventNotificationService().registerListener(new EhCacheEventListener());
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public T get(Object key) {
		Element element = this.ehCache.get(key);
		if (element == null) {
			return null;
		}
		// Any object delivered by the cache should be copied to avoid modifications done by consumers
		Object objectValue = element.getObjectValue();

		if (objectValue instanceof SCMPPart) {
			SCMPPart copiedSCMPPart = null;
			copiedSCMPPart = new SCMPPart((SCMPPart) objectValue);
			return (T) copiedSCMPPart;
		} else if (objectValue instanceof SCMPMessage) {
			SCMPMessage copiedSCMPMsg = null;
			copiedSCMPMsg = new SCMPMessage((SCMPMessage) objectValue);
			return (T) copiedSCMPMsg;
		} else if (objectValue instanceof SCCacheMetaEntry) {
			SCCacheMetaEntry copiedMetaEntry = null;
			copiedMetaEntry = new SCCacheMetaEntry((SCCacheMetaEntry) objectValue);
			return (T) copiedMetaEntry;
		} else {
			LOGGER.error("Unexpected instance copy procedure not properly done!");
			return (T) objectValue;
		}
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public void putOrUpdate(Object key, T value, int timeToLiveSeconds) {
		// Any object put to the cache should be copied to avoid modifications done by consumers
		if (value instanceof SCMPPart) {
			value = (T) new SCMPPart((SCMPPart) value);
		} else if (value instanceof SCMPMessage) {
			value = (T) new SCMPMessage((SCMPMessage) value);
		} else if (value instanceof SCCacheMetaEntry) {
			value = (T) new SCCacheMetaEntry((SCCacheMetaEntry) value);
		} else {
			LOGGER.error("Unexpected instance copy procedure not properly done!");
		}

		// key, value, eternal, timeToIdle (0 = unlimited), timeToLive
		Element element = new Element(key, value, false, 0, timeToLiveSeconds);
		this.ehCache.put(element);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void replace(Object key, T value, int timeToLiveSeconds) {
		// Any object put to the cache should be copied to avoid modifications done by consumers
		if (value instanceof SCMPPart) {
			value = (T) new SCMPPart((SCMPPart) value);
		} else if (value instanceof SCMPMessage) {
			value = (T) new SCMPMessage((SCMPMessage) value);
		} else if (value instanceof SCCacheMetaEntry) {
			value = (T) new SCCacheMetaEntry((SCCacheMetaEntry) value);
		} else {
			LOGGER.error("Unexpected instance copy procedure not properly done!");
		}
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

	@Override
	public long getOffHeapSize() {
		return this.ehCache.calculateOffHeapSize();
	}

	@Override
	public long getInMemorySize() {
		return this.ehCache.calculateInMemorySize();
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

	/** {@inheritDoc} */
	@Override
	public void destroy() {
		this.ehCache.dispose();
	}
}
