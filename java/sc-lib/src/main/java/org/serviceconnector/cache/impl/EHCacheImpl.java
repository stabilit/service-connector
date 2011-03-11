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
package org.serviceconnector.cache.impl;

import java.io.File;
import java.io.FileFilter;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;

import org.serviceconnector.cache.ICacheConfiguration;

/**
 * The Class EHCacheSCMPCacheImpl.
 */
public class EHCacheImpl implements ICacheImpl {

	/** The sync obj. */
	private static Object syncObj = new Object();
	/** The manager. */
	private static CacheManager manager = null;
	/** The config. */
	private static CacheConfiguration config = null;
	/** The cache. */
	private Cache cache;
	/** The Constant DEFAULT_CACHE_DISK_PERSISTENT. */
	public static final boolean DEFAULT_CACHE_DISK_PERSISTENT = true;
	/** The Constant DEFAULT_CACHE_NAME. */
	public static final String DEFAULT_CACHE_NAME = "scCache";

	/*
	 * Instantiates a new eHcache impl. 
	 *  see also http://ehcache.org/documentation/configuration.html
	 *   
	 * The following attributes are required for eHcache.
	 *    name:
	 *         Sets the name of the cache. This is used to identify the cache. It must be unique.
	 *         
	 *    maxElementsInMemory:
	 *         Sets the maximum number of objects that will be created in memory.  0 = no limit.
	 *         In practice no limit means Integer.MAX_SIZE (2147483647) unless the cache is distributed
	 *         with a Terracotta server in which case it is limited by resources.
	 *         
	 *    maxElementsOnDisk:
	 *         Sets the maximum number of objects that will be maintained in the DiskStore
	 *         The default value is zero, meaning unlimited.
	 *         
	 *    eternal:
	 *         Sets whether elements are eternal. If eternal, timeouts are ignored and the
	 *         element is never expired.
	 *         
	 *    overflowToDisk:
	 *         Sets whether elements can overflow to disk when the memory store
	 *         has reached the maxInMemory limit.
	 *         
	 * The following attributes and elements are optional.
	 *    overflowToOffHeap:
	 *         (boolean) This feature is available only in enterprise versions of Ehcache.
	 *         When set to true, enables the cache to utilize off-heap memory
	 *         storage to improve performance. Off-heap memory is not subject to Java
	 *         GC. The default value is false.
	 *         
	 *    maxMemoryOffHeap:
	 *         (string) This feature is available only in enterprise versions of Ehcache.
	 *         Sets the amount of off-heap memory available to the cache.
	 *         This attribute's values are given as <number>k|K|m|M|g|G|t|T for
	 *         kilobytes (k|K), megabytes (m|M), gigabytes (g|G), or terabytes
	 *         (t|T). For example, maxMemoryOffHeap="2g" allots 2 gigabytes to
	 *         off-heap memory.
	 *         This setting is in effect only if overflowToOffHeap is true.
	 *         Note that it is recommended to set maxElementsInMemory to at least 100 
	 *         elements when using an off-heap store, otherwise performance will be 
	 *         seriously degraded, and a warning will be logged.
	 *         The minimum amount that can be allocated is 128MB. There is no maximum.
	 *         
	 *    timeToIdleSeconds:
	 *        Sets the time to idle for an element before it expires.
	 *        i.e. The maximum amount of time between accesses before an element expires
	 *        Is only used if the element is not eternal. A value of 0 means that an 
	 *        Element can idle for infinity. The default value is 0.
	 *        
	 *    timeToLiveSeconds:
	 *        Sets the time to live for an element before it expires.
	 *         i.e. The maximum time between creation time and when an element expires.
	 *         Is only used if the element is not eternal. A value of 0 means that and 
	 *         Element can live for infinity. The default value is 0.
	 *         
	 *    diskPersistent:
	 *        Whether the disk store persists between restarts of the Virtual Machine.
	 *        The default value is false.
	 *        
	 *    diskExpiryThreadIntervalSeconds:
	 *        The number of seconds between runs of the disk expiry thread. The default value
	 *        is 120 seconds.
	 *        
	 *    diskSpoolBufferSizeMB:
	 *        This is the size to allocate the DiskStore for a spool buffer. Writes are made
	 *        to this area and then asynchronously written to disk. The default size is 30MB.
	 *        Each spool buffer is used only by its cache. If you get OutOfMemory errors consider
	 *        lowering this value. To improve DiskStore performance consider increasing it. 
	 *        Trace level logging in the DiskStore will show if put back ups are occurring.
	 *        
	 *    clearOnFlush:
	 *        whether the MemoryStore should be cleared when flush() is called on the cache.
	 *        By default, this is true i.e. the MemoryStore is cleared.
	 *        
	 *    statistics:
	 *        Whether to collect statistics. Note that this should be turned on if you are using
	 *        the Ehcache Monitor. By default statistics is turned off to favour raw performance.
	 *        To enable set statistics="true"
	 *        
	 *    memoryStoreEvictionPolicy:
	 *        Policy would be enforced upon reaching the maxElementsInMemory limit. Default
	 *        policy is Least Recently Used (specified as LRU). Other policies available -
	 *        First In First Out (specified as FIFO) and Less Frequently Used
	 *        specified as LFU)
	 *        
	 *    copyOnRead:
	 *        Whether an Element is copied when being read from a cache. By default this is false.
	 *        
	 *    copyOnWrite:
	 *        Whether an Element is copied when being added to the cache. By default this is false.
	*/
	/**
	 * 
	 * @param cacheConfiguration
	 *            the scmp cache configuration
	 * @param serviceName
	 *            the service name
	 */
	public EHCacheImpl(ICacheConfiguration cacheConfiguration, String serviceName) {
		synchronized (syncObj) {
			if (manager == null) {
				Configuration configuration = new Configuration();
				String diskPath = cacheConfiguration.getDiskPath();
				// disk store configuration is required
				DiskStoreConfiguration diskStoreConfiguration = new DiskStoreConfiguration();
				if (diskPath != null) {
					diskStoreConfiguration.setPath(diskPath);
				}
				configuration.addDiskStore(diskStoreConfiguration);
				configuration.setName(EHCacheImpl.DEFAULT_CACHE_NAME);

				// default Cache configuration is required for CacheManager
				CacheConfiguration defaultConfiguration = new CacheConfiguration(EHCacheImpl.DEFAULT_CACHE_NAME,
						cacheConfiguration.getMaxElementsInMemory());
				defaultConfiguration.setEternal(false);
				defaultConfiguration.setTimeToIdleSeconds(60);
				defaultConfiguration.setTimeToLiveSeconds(120);
				defaultConfiguration.setOverflowToDisk(true);
				defaultConfiguration.setMaxElementsInMemory(cacheConfiguration.getMaxElementsInMemory());
				defaultConfiguration.setMaxElementsOnDisk(cacheConfiguration.getMaxElementsOnDisk());
				defaultConfiguration.setDiskPersistent(EHCacheImpl.DEFAULT_CACHE_DISK_PERSISTENT);
				defaultConfiguration.setName(EHCacheImpl.DEFAULT_CACHE_NAME);
				configuration.setDefaultCacheConfiguration(defaultConfiguration);
				configuration.setUpdateCheck(false); // disable update checker
				manager = new CacheManager(configuration);
			}
		}
		EHCacheImpl.config = new CacheConfiguration(serviceName, cacheConfiguration.getMaxElementsInMemory());
		EHCacheImpl.config.setEternal(true);
		// this.config.setTimeToIdleSeconds(60);
		// this.config.setTimeToLiveSeconds(120);
		EHCacheImpl.config.setMaxElementsInMemory(cacheConfiguration.getMaxElementsInMemory());
		EHCacheImpl.config.setMaxElementsOnDisk(cacheConfiguration.getMaxElementsOnDisk());
		EHCacheImpl.config.setDiskPersistent(EHCacheImpl.DEFAULT_CACHE_DISK_PERSISTENT);
		EHCacheImpl.config.setName(EHCacheImpl.DEFAULT_CACHE_NAME + "." + serviceName);
		this.cache = new Cache(EHCacheImpl.config);
		this.cache.setName(EHCacheImpl.DEFAULT_CACHE_NAME + "." + serviceName);
		this.cache.setDiskStorePath(serviceName);
		manager.addCache(this.cache);
	}

	/** {@inheritDoc} */
	@Override
	public Object get(Object key) {
		Element element = this.cache.get(key);
		if (element == null) {
			return null;
		}
		return element.getObjectValue();
	}

	/** {@inheritDoc} */
	@Override
	public void put(Object key, Object value) {
		Element element = new Element(key, value);
		this.cache.put(element);
	}

	/** {@inheritDoc} */
	@Override
	public boolean remove(Object key) {
		boolean ret = this.cache.remove(key);
		return ret;
	}

	/**
	 * Destroy.
	 */
	public static void clearAll() {
		synchronized (syncObj) {
			if (manager != null) {
				String[] cacheNames = manager.getCacheNames();
				for (String cacheName : cacheNames) {
					Ehcache ehCache = manager.getEhcache(cacheName);
					if (ehCache instanceof Cache) {
						Cache cache = (Cache) ehCache;
						cache.removeAll();
					}
				}
			}
		}
	}

	/**
	 * Destroy.
	 */
	public static void destroy() {
		synchronized (syncObj) {
			if (manager != null) {
				manager.clearAll();
				String[] cacheNames = manager.getCacheNames();
				for (String cacheName : cacheNames) {
					Ehcache ehCache = manager.getEhcache(cacheName);
					if (ehCache instanceof Cache) {
						Cache cache = (Cache) ehCache;
						cache.dispose();
					}
				}
				manager.removalAll();
				String diskStorePath = manager.getDiskStorePath();
				File diskStorePathFile = new File(diskStorePath);
				if (diskStorePathFile.exists()) {
					File[] files = diskStorePathFile.listFiles(new FileFilter() {
						String cacheName = manager.getName();

						@Override
						public boolean accept(File pathname) {
							String fileName = pathname.getName();
							if (fileName.startsWith(cacheName + ".") == false) {
								return false;
							}
							if (fileName.endsWith(".data")) {
								return true;
							}
							if (fileName.endsWith(".index")) {
								return true;
							}
							return false;
						}

					});

					for (int i = 0; i < files.length; i++) {
						if (files[i].isFile()) {
							files[i].delete();
						}
					}
				}
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public int getElementSize() {
		return this.cache.getSize();
	}

	/** {@inheritDoc} */
	@Override
	public long getSizeInBytes() {
		return this.cache.calculateInMemorySize();
	}

	/** {@inheritDoc} */
	@Override
	public String getCacheName() {
		return this.cache.getName();
	}

	/** {@inheritDoc} */
	@Override
	public long getMemoryStoreSize() {
		return this.cache.getMemoryStoreSize();
	}

	/** {@inheritDoc} */
	@Override
	public long getDiskStoreSize() {
		return this.cache.getDiskStoreSize();
	}

}
