package org.serviceconnector;

import org.serviceconnector.Constants;
import org.serviceconnector.cache.CacheConfiguration;

public class TestCacheConfiguration extends CacheConfiguration {
	
	public TestCacheConfiguration() {
		this.cacheEnabled = Constants.DEFAULT_CACHE_ENABLED;
		this.cacheName = Constants.DEFAULT_CACHE_NAME;
		this.diskPersistent = Constants.DEFAULT_CACHE_DISK_PERSISTENT;
		this.diskPath = "cache";
		this.maxElementsInMemory = Constants.DEFAULT_CACHE_MAX_ELEMENTS_IN_MEMORY;
		this.maxElementsOnDisk = Constants.DEFAULT_CACHE_MAX_ELEMENTS_ON_DISK;
		this.expirationCheckIntervalSeconds = Constants.DEFAULT_CACHE_EXPIRATION_CHECK_INTERVAL_SECONDS;
	}
}
