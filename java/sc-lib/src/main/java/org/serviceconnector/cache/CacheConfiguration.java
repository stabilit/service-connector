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

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;
import org.serviceconnector.Constants;

/**
 * The Class SCMPCacheConfiguration.
 */
public class CacheConfiguration implements ICacheConfiguration {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(CacheConfiguration.class);
	
	/** The cache enabled. */
	private boolean cacheEnabled;

	/** The cache name. */
	private String cacheName;

	/** The disk path. */
	private String diskPath;

	/** The disk persistent. */
	private boolean diskPersistent;

	/** The max elements in memory. */
	private int maxElementsInMemory;

	/** The max elements on disk. */
	private int maxElementsOnDisk;

	/** The expiration thread interval (timeout) in seconds. */
	private int expirationThreadIntervalSeconds;

	/**
	 * Instantiates a new sCMP cache configuration.
	 */
	public CacheConfiguration() {
		this.cacheEnabled = Constants.DEFAULT_CACHE_ENABLED;
		this.cacheName = Constants.DEFAULT_CACHE_NAME;
		this.diskPath = Constants.DEFAULT_CACHE_DISK_PATH;
		this.diskPersistent = Constants.DEFAULT_CACHE_DISK_PERSISTENT;
		this.maxElementsInMemory = Constants.DEFAULT_CACHE_MAX_ELEMENTS_IN_MEMORY;
		this.maxElementsOnDisk = Constants.DEFAULT_CACHE_MAX_ELEMENTS_ON_DISK;
		this.expirationThreadIntervalSeconds = Constants.DEFAULT_CACHE_EXPIRATION_CHECK_INTERVAL_SECONDS;
	}

	/**
	 * Loads cache parameters from properties file.</br> 
	 * Service Connector cache parameters: </br>
	 * cache.enabled=true</br> 
	 * cache.name=scCache
	 * cache.diskPersistent=true </br>
	 * cache.diskPath=../../dev/cache </br>
	 * cache.timeIdleSeconds=60 </br>
	 * cache.timeToLiveSeconds=120</br>
	 * cache.maxElementsInMemory=10000 </br>
	 * cache.maxElementsOnDisk=1000000
	 * 
	 * @param fileName
	 *            the file name
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void init(CompositeConfiguration compositeConfiguration) throws Exception {
		try {
			this.cacheEnabled = compositeConfiguration.getBoolean(Constants.CACHE_ENABLED);
			logger.info("cache configuration: cache enabled is " + this.cacheEnabled);
		} catch (Exception e) {
			logger.warn("default CACHE_ENABLED = " + e.toString());
		}
		try {
			String sCacheName = compositeConfiguration.getString(Constants.CACHE_NAME);
			if (sCacheName != null) {
				this.cacheName = sCacheName;
			}
			logger.info("cache configuration: cache name = " + this.cacheName);
		} catch (Exception e) {
			logger.warn("default CACHE_NAME = " + e.toString());
		}
		try {
			this.diskPersistent = compositeConfiguration.getBoolean(Constants.CACHE_DISK_PERSISTENT);
			logger.info("cache configuration: cache disk persistent is " + this.diskPersistent);
		} catch (Exception e) {
			logger.warn("default CACHE_DISK_PERSISTENT = " + e.toString());
		}
		try {
			String sDiskPath = compositeConfiguration.getString(Constants.CACHE_DISK_PATH);
			if (sDiskPath != null) {
				this.diskPath = sDiskPath;
			}
			logger.info("cache configuration: disk path = " + this.diskPath);
		} catch (Exception e) {
			logger.warn("default CACHE_DISK_PATH = " + e.toString());
		}
		try {
			int maxElementsInMemory = compositeConfiguration.getInt(Constants.CACHE_MAX_ELEMENTS_IN_MEMORY);
			if (maxElementsInMemory > 0) {
				this.maxElementsInMemory = maxElementsInMemory;
			}
			logger.info("cache configuration: max elements in memory = " + this.maxElementsInMemory);
		} catch (Exception e) {
			logger.warn("default CACHE_MAX_ELEMENTS_IN_MEMORY = " + e.toString());
		}
		try {
			int maxElementsOnDisk = compositeConfiguration.getInt(Constants.CACHE_MAX_ELEMENTS_ON_DISK);
			if (maxElementsOnDisk > 0) {
				this.maxElementsOnDisk = maxElementsOnDisk;
			}
			logger.info("cache configuration: max elements on disk = " + this.maxElementsOnDisk);
		} catch (Exception e) {
			logger.warn("default CACHE_MAX_ELEMENTS_ON_DISK = " + e.toString());
		}
		try {
			int expirationThreadTimeoutSeconds = compositeConfiguration.getInt(Constants.CACHE_EXPIRATION_CHECK_INTERVAL_SECONDS);
			if (expirationThreadTimeoutSeconds >= 0) {
				this.expirationThreadIntervalSeconds = expirationThreadTimeoutSeconds;
			}
			logger.info("cache configuration: expirationThreadTimeoutSeconds = " + this.expirationThreadIntervalSeconds);
		} catch (Exception e) {
			logger.warn("default CACHE_EXPIRATION_CHECK_INTERVAL_SECONDS = " + e.toString());
		}
	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.cache.ICacheConfiguration#isCacheEnabled()
	 */
	@Override
	public boolean isCacheEnabled() {
		return cacheEnabled;
	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.cache.ICacheConfiguration#isDiskPersistent()
	 */
	@Override
	public boolean isDiskPersistent() {
		return diskPersistent;
	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.cache.ICacheConfiguration#getDiskPath()
	 */
	@Override
	public String getDiskPath() {
		return diskPath;
	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.cache.ICacheConfiguration#getCacheName()
	 */
	@Override
	public String getCacheName() {
		return cacheName;
	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.cache.ICacheConfiguration#getMaxElementsInMemory()
	 */
	@Override
	public int getMaxElementsInMemory() {
		return maxElementsInMemory;
	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.cache.ICacheConfiguration#getMaxElementsOnDisk()
	 */
	@Override
	public int getMaxElementsOnDisk() {
		return maxElementsOnDisk;
	}

	/**
	 * Gets the expiration thread interval seconds.
	 * 
	 * @return the expiration thread interval seconds
	 */
	public int getExpirationThreadIntervalSeconds() {
		return expirationThreadIntervalSeconds;
	}

	/**
	 * Sets the expiration thread interval seconds.
	 * 
	 * @param expirationThreadIntervalSeconds
	 *            the new expiration thread interval seconds
	 */
	public void setExpirationThreadIntervalSeconds(int expirationThreadIntervalSeconds) {
		this.expirationThreadIntervalSeconds = expirationThreadIntervalSeconds;
	}
}
