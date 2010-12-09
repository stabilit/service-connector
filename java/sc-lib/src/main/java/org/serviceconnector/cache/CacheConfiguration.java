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
	 * Loads cache parameters from properties file.</br> Service Connector cache parameters: </br> cache.enabled=true</br>
	 * cache.name=scCache cache.diskPersistent=true </br> cache.diskPath=../../dev/cache </br> cache.timeIdleSeconds=60 </br>
	 * cache.timeToLiveSeconds=120</br> cache.maxElementsInMemory=10000 </br> cache.maxElementsOnDisk=1000000
	 * 
	 * @param fileName
	 *            the file name
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void init(CompositeConfiguration compositeConfiguration) throws Exception {

		Boolean cacheEnabled = compositeConfiguration.getBoolean(Constants.CACHE_ENABLED);
		if (cacheEnabled != null && this.cacheEnabled != cacheEnabled) {
			this.cacheEnabled = cacheEnabled;
			logger.info("cacheEnabled set to " + cacheEnabled);
		}

		String sCacheName = compositeConfiguration.getString(Constants.CACHE_NAME);
		if (sCacheName != null && sCacheName != this.cacheName) {
			this.cacheName = sCacheName;
			logger.info("cacheName set to " + this.cacheName);
		}

		Boolean diskPersistent = compositeConfiguration.getBoolean(Constants.CACHE_DISK_PERSISTENT);
		if (diskPersistent != null && diskPersistent != this.diskPersistent) {
			this.diskPersistent = diskPersistent;
			logger.info("diskPersistent set to " + this.diskPersistent);
		}

		String sDiskPath = compositeConfiguration.getString(Constants.CACHE_DISK_PATH);
		if (sDiskPath != null && sDiskPath != this.diskPath) {
			this.diskPath = sDiskPath;
			logger.info("diskPath set to " + this.diskPath);
		}

		Integer maxElementsInMemory = compositeConfiguration.getInteger(Constants.CACHE_MAX_ELEMENTS_IN_MEMORY, null);
		if (maxElementsInMemory != null && maxElementsInMemory != this.maxElementsInMemory) {
			this.maxElementsInMemory = maxElementsInMemory;
			logger.info("maxElementsInMemory set to " + this.maxElementsInMemory);
		}

		Integer maxElementsOnDisk = compositeConfiguration.getInteger(Constants.CACHE_MAX_ELEMENTS_ON_DISK, null);
		if (maxElementsOnDisk != null && maxElementsOnDisk != this.maxElementsOnDisk) {
			this.maxElementsOnDisk = maxElementsOnDisk;
			logger.info("maxElementsOnDisk set to " + this.maxElementsOnDisk);
		}

		Integer expirationThreadIntervalSeconds = compositeConfiguration.getInteger(
				Constants.CACHE_EXPIRATION_CHECK_INTERVAL_SECONDS, null);
		if (expirationThreadIntervalSeconds != null && expirationThreadIntervalSeconds != this.expirationThreadIntervalSeconds) {
			this.expirationThreadIntervalSeconds = expirationThreadIntervalSeconds;
			logger.info("expirationThreadIntervalSeconds set to " + this.expirationThreadIntervalSeconds);
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean isCacheEnabled() {
		return cacheEnabled;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isDiskPersistent() {
		return diskPersistent;
	}

	/** {@inheritDoc} */
	@Override
	public String getDiskPath() {
		return diskPath;
	}

	/** {@inheritDoc} */
	@Override
	public String getCacheName() {
		return cacheName;
	}

	/** {@inheritDoc} */
	@Override
	public int getMaxElementsInMemory() {
		return maxElementsInMemory;
	}

	/** {@inheritDoc} */
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
