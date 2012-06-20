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

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;

import org.serviceconnector.cache.SCCacheMetaEntry;
import org.serviceconnector.cache.ISCCacheModule;
import org.serviceconnector.cache.SC_CACHE_MODULE_TYPE;
import org.serviceconnector.conf.SCCacheConfiguration;
import org.serviceconnector.scmp.SCMPMessage;

/**
 * A factory for creating SCCache objects. Default caches are implemented by EHCache. Factory knows how to create/configure an
 * EHCache instance. It holds the EHCache cacheManager which is necessary to take care of if you use EHCache.
 */
public final class SCCacheFactory {

	/** The Constant DEFAULT_CACHE_DISK_PERSISTENT. */
	private static final boolean DEFAULT_CACHE_DISK_PERSISTENT = true;
	/** The Constant DEFAULT_CACHE_NAME. */
	private static final String DEFAULT_CACHE_NAME = "SC_CACHE";
	/** The EHCache manager. */
	private static CacheManager ehManager;

	/**
	 * Instantiates a new factory. Private constructor to avoid instantiation.
	 */
	private SCCacheFactory() {
	}

	/*
	 * Instantiates a new eHcache impl. see also http://ehcache.org/documentation/configuration.html
	 * 
	 * The following attributes are required for eHcache. name: Sets the name of the cache. This is used to identify the cache. It must be unique.
	 * 
	 * maxElementsInMemory: Sets the maximum number of objects that will be created in memory. 0 = no limit. In practice no limit means Integer.MAX_SIZE
	 * (2147483647) unless the cache is distributed with a Terracotta server in which case it is limited by resources.
	 * 
	 * maxElementsOnDisk: Sets the maximum number of objects that will be maintained in the DiskStore The default value is zero, meaning unlimited.
	 * 
	 * eternal: Sets whether elements are eternal. If eternal, timeouts are ignored and the element is never expired.
	 * 
	 * overflowToDisk: Sets whether elements can overflow to disk when the memory store has reached the maxInMemory limit.
	 * 
	 * The following attributes and elements are optional. overflowToOffHeap: (boolean) This feature is available only in enterprise versions of Ehcache. When
	 * set to true, enables the cache to utilize off-heap memory storage to improve performance. Off-heap memory is not subject to Java GC. The default value is
	 * false.
	 * 
	 * maxMemoryOffHeap: (string) This feature is available only in enterprise versions of Ehcache. Sets the amount of off-heap memory available to the cache.
	 * This attribute's values are given as <number>k|K|m|M|g|G|t|T for kilobytes (k|K), megabytes (m|M), gigabytes (g|G), or terabytes (t|T). For example,
	 * maxMemoryOffHeap="2g" allots 2 gigabytes to off-heap memory. This setting is in effect only if overflowToOffHeap is true. Note that it is recommended to
	 * set maxElementsInMemory to at least 100 elements when using an off-heap store, otherwise performance will be seriously degraded, and a warning will be
	 * logged. The minimum amount that can be allocated is 128MB. There is no maximum.
	 * 
	 * timeToIdleSeconds: Sets the time to idle for an element before it expires. i.e. The maximum amount of time between accesses before an element expires Is
	 * only used if the element is not eternal. A value of 0 means that an Element can idle for infinity. The default value is 0.
	 * 
	 * timeToLiveSeconds: Sets the time to live for an element before it expires. i.e. The maximum time between creation time and when an element expires. Is
	 * only used if the element is not eternal. A value of 0 means that and Element can live for infinity. The default value is 0.
	 * 
	 * diskPersistent: Whether the disk store persists between restarts of the Virtual Machine. The default value is false.
	 * 
	 * diskExpiryThreadIntervalSeconds: The number of seconds between runs of the disk expiry thread. The default value is 120 seconds.
	 * 
	 * diskSpoolBufferSizeMB: This is the size to allocate the DiskStore for a spool buffer. Writes are made to this area and then asynchronously written to
	 * disk. The default size is 30MB. Each spool buffer is used only by its cache. If you get OutOfMemory errors consider lowering this value. To improve
	 * DiskStore performance consider increasing it. Trace level logging in the DiskStore will show if put back ups are occurring.
	 * 
	 * clearOnFlush: whether the MemoryStore should be cleared when flush() is called on the cache. By default, this is true i.e. the MemoryStore is cleared.
	 * 
	 * statistics: Whether to collect statistics. Note that this should be turned on if you are using the Ehcache Monitor. By default statistics is turned off
	 * to favour raw performance. To enable set statistics="true"
	 * 
	 * memoryStoreEvictionPolicy: Policy would be enforced upon reaching the maxElementsInMemory limit. Default policy is Least Recently Used (specified as
	 * LRU). Other policies available - First In First Out (specified as FIFO) and Less Frequently Used specified as LFU)
	 * 
	 * copyOnRead: Whether an Element is copied when being read from a cache. By default this is false.
	 * 
	 * copyOnWrite: Whether an Element is copied when being added to the cache. By default this is false.
	 */
	/**
	 * Creates a new SCCache object.
	 * 
	 * @param scCacheConfiguration
	 *            the SC cache configuration
	 * @param cacheType
	 *            the cache type needed
	 * @return the ISC cache
	 */
	public static ISCCacheModule<?> createDefaultSCCache(SCCacheConfiguration scCacheConfiguration, SC_CACHE_MODULE_TYPE cacheType) {
		// sets up the configuration needed for the EHCache
		String diskPath = scCacheConfiguration.getDiskPath();

		// Configuration for EHCache cache
		CacheConfiguration ehCacheConfiguration = new CacheConfiguration();
		ehCacheConfiguration.setEternal(true); // elements stay in cache until app removes them
		ehCacheConfiguration.setOverflowToDisk(true);
		ehCacheConfiguration.setMaxElementsInMemory(scCacheConfiguration.getMaxElementsInMemory());
		ehCacheConfiguration.setMaxElementsOnDisk(scCacheConfiguration.getMaxElementsOnDisk());
		ehCacheConfiguration.setDiskPersistent(SCCacheFactory.DEFAULT_CACHE_DISK_PERSISTENT);
		ehCacheConfiguration.setDiskStorePath(diskPath);
		ehCacheConfiguration.setName(cacheType.name());
		ehCacheConfiguration.setCopyOnRead(true);
		ehCacheConfiguration.setCopyOnWrite(true);
		ehCacheConfiguration.setDiskExpiryThreadIntervalSeconds(scCacheConfiguration.getExpirationCheckIntervalSeconds());
		
		// create cache
		EHCacheImpl<?> cacheData = null;
		switch (cacheType) {
		case META_DATA_CACHE_MODULE:
			cacheData = new EHCacheImpl<SCCacheMetaEntry>(ehCacheConfiguration);
			break;
		case DATA_CACHE_MODULE:
			cacheData = new EHCacheImpl<SCMPMessage>(ehCacheConfiguration);
			break;
		}

		if (ehManager == null) {
			// following code adds EHCache monitor by JOT
			// FactoryConfiguration<FactoryConfiguration> factory = new FactoryConfiguration<FactoryConfiguration>();
			// factory.className("org.terracotta.ehcachedx.monitor.probe.ProbePeerListenerFactory");
			// factory.setProperties("monitorAddress=localhost, monitorPort=9889, memoryMeasurement=true");
			// factory.setPropertySeparator(",");
			// configuration.addCacheManagerPeerListenerFactory(factory);

			// Configuration is required for EHCache CacheManager
			Configuration ehCacheManagerConfiguration = new Configuration();
			DiskStoreConfiguration diskStoreConfiguration = new DiskStoreConfiguration();
			if (diskPath != null) {
				diskStoreConfiguration.setPath(diskPath);
			}
			// use the ehCacheConfiguration as default but change the name
			ehCacheConfiguration.setName(SCCacheFactory.DEFAULT_CACHE_NAME);
			ehCacheManagerConfiguration.addDiskStore(diskStoreConfiguration);
			ehCacheManagerConfiguration.setName(SCCacheFactory.DEFAULT_CACHE_NAME);
			ehCacheManagerConfiguration.setDefaultCacheConfiguration(ehCacheConfiguration);
			ehCacheManagerConfiguration.setUpdateCheck(false); // disable update checker (checks EHCache version)
			ehManager = new CacheManager(ehCacheManagerConfiguration);
		}
		ehManager.addCache(cacheData.getEhCache()); // adds the cache to the EHCache CacheManager
		return cacheData;
	}

	/**
	 * Destroys factory and shuts down resources.
	 */
	public static void destroy() {
		if (ehManager != null) {
			// shuts down the EHCache CacheManager and related caches
			ehManager.shutdown();
			ehManager = null;
		}
	}
}