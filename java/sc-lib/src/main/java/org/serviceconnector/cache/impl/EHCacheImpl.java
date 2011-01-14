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

	/**
	 * Instantiates a new eH cache scmp cache impl. <cache name="scCache" maxElementsInMemory="10000" eternal="false"
	 * timeToIdleSeconds="120" timeToLiveSeconds="120" overflowToDisk="true" maxElementsOnDisk="10000000" diskPersistent="true"
	 * diskExpiryThreadIntervalSeconds="120" memoryStoreEvictionPolicy="LRU"/>
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
				DiskStoreConfiguration diskStoreConfiguration = new DiskStoreConfiguration();
				diskStoreConfiguration.setPath(cacheConfiguration.getDiskPath());
				configuration.addDiskStore(diskStoreConfiguration);
				configuration.setName(cacheConfiguration.getCacheName());
				CacheConfiguration defaultCacheConfiguration = new CacheConfiguration(cacheConfiguration.getCacheName(),
						cacheConfiguration.getMaxElementsInMemory());
				defaultCacheConfiguration.setEternal(false); // ignore any
				// timeouts
				defaultCacheConfiguration.setTimeToIdleSeconds(60);
				defaultCacheConfiguration.setTimeToLiveSeconds(120);
				defaultCacheConfiguration.setOverflowToDisk(true);
				defaultCacheConfiguration.setMaxElementsInMemory(cacheConfiguration.getMaxElementsInMemory());
				defaultCacheConfiguration.setMaxElementsOnDisk(cacheConfiguration.getMaxElementsOnDisk());
				defaultCacheConfiguration.setDiskPersistent(cacheConfiguration.isDiskPersistent());
				defaultCacheConfiguration.setName(cacheConfiguration.getCacheName());
				configuration.setDefaultCacheConfiguration(defaultCacheConfiguration);
				configuration.setUpdateCheck(false); // disable update checker
				manager = new CacheManager(configuration);
			}
		}
		this.config = new CacheConfiguration(serviceName, cacheConfiguration.getMaxElementsInMemory());
		this.config.setEternal(true);
		// this.config.setTimeToIdleSeconds(60);
		// this.config.setTimeToLiveSeconds(120);
		this.config.setMaxElementsInMemory(cacheConfiguration.getMaxElementsInMemory());
		this.config.setMaxElementsOnDisk(cacheConfiguration.getMaxElementsOnDisk());
		this.config.setDiskPersistent(cacheConfiguration.isDiskPersistent());
		this.config.setName(cacheConfiguration.getCacheName() + "." + serviceName);
		this.cache = new Cache(this.config);
		this.cache.setName(cacheConfiguration.getCacheName() + "." + serviceName);
		this.cache.setDiskStorePath(serviceName);
		manager.addCache(this.cache);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.serviceconnector.scmp.cache.ISCMPCacheImpl#get(java.lang.Object)
	 */
	public Object get(Object key) {
		Element element = this.cache.get(key);
		if (element == null) {
			return null;
		}
		return element.getObjectValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.serviceconnector.scmp.cache.ISCMPCacheImpl#put(java.lang.Object, java.lang.Object)
	 */
	public void put(Object key, Object value) {
		Element element = new Element(key, value);
		this.cache.put(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.serviceconnector.scmp.cache.ISCMPCacheImpl#remove(java.lang.Object)
	 */
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

	@Override
	public int getElementSize() {
		return this.cache.getSize();
	}

	@Override
	public long getSizeInBytes() {
		return this.cache.calculateInMemorySize();
	}

	@Override
	public String getCacheName() {
		return this.cache.getName();
	}

	@Override
	public long getMemoryStoreSize() {
		return this.cache.getMemoryStoreSize();
	}

	@Override
	public long getDiskStoreSize() {
		return this.cache.getDiskStoreSize();
	}

}
