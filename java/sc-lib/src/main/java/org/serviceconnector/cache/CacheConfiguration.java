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

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.SCMPError;

/**
 * The Class CacheConfiguration.
 * 
 * This is the main cache configuration class, keeping all required configuration parameters.
 * 
 * This class is used to setup cache manager {@link CacheManager} instances.
 */
public class CacheConfiguration implements ICacheConfiguration {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(CacheConfiguration.class);

	/** The cache enabled. */
	protected boolean cacheEnabled;
	/** The disk path. */
	protected String diskPath = null;
	/** The max elements in memory. */
	protected int maxElementsInMemory;
	/** The max elements on disk. */
	protected int maxElementsOnDisk;
	/** The expiration thread interval (timeout) in seconds. */
	protected int expirationCheckIntervalSeconds;

	/**
	 * Instantiates a new cache configuration.
	 */
	public CacheConfiguration() {
		this.cacheEnabled = Constants.DEFAULT_CACHE_ENABLED;
		this.maxElementsInMemory = Constants.DEFAULT_CACHE_MAX_ELEMENTS_IN_MEMORY;
		this.maxElementsOnDisk = Constants.DEFAULT_CACHE_MAX_ELEMENTS_ON_DISK;
		this.expirationCheckIntervalSeconds = Constants.DEFAULT_CACHE_EXPIRATION_CHECK_INTERVAL_SECONDS;
	}

	/**
	 * Loads cache parameters from properties file.</br>
	 * Service Connector cache parameters: </br>
	 * cache.enabled=true</br>
	 * cache.name=scCache</br>
	 * cache.diskPersistent=true </br>
	 * cache.diskPath=../../dev/cache </br>
	 * cache.timeIdleSeconds=60 </br>
	 * cache.timeToLiveSeconds=120</br>
	 * cache.maxElementsInMemory=10000 </br>
	 * cache.maxElementsOnDisk=1000000
	 * 
	 * @param compositeConfiguration
	 *            the composite configuration
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	public synchronized void load(CompositeConfiguration compositeConfiguration) throws SCMPValidatorException {
		// enabled
		Boolean cacheEnabled = compositeConfiguration.getBoolean(Constants.CACHE_ENABLED, null);
		if (cacheEnabled != null && this.cacheEnabled != cacheEnabled) {
			this.cacheEnabled = cacheEnabled;
		}
		logger.log(Level.OFF, Constants.CACHE_DISK_PATH + "cacheEnabled=" + this.cacheEnabled);

		// diskPath
		String sDiskPath = compositeConfiguration.getString(Constants.CACHE_DISK_PATH, null);
		if (sDiskPath == null && this.cacheEnabled) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + Constants.CACHE_DISK_PATH
					+ " is missing");
		}
		if (sDiskPath != null && sDiskPath != this.diskPath) {
			this.diskPath = sDiskPath;
		}
		logger.log(Level.OFF, Constants.CACHE_DISK_PATH + "diskPath=" + this.diskPath);

		// maxElementsInMemory
		Integer maxElementsInMemory = compositeConfiguration.getInteger(Constants.CACHE_MAX_ELEMENTS_IN_MEMORY, null);
		if (maxElementsInMemory != null && maxElementsInMemory != this.maxElementsInMemory) {
			this.maxElementsInMemory = maxElementsInMemory;
		}
		logger.log(Level.OFF, Constants.CACHE_DISK_PATH + "maxElementsInMemory=" + this.maxElementsInMemory);

		// maxElementsOnDisk
		Integer maxElementsOnDisk = compositeConfiguration.getInteger(Constants.CACHE_MAX_ELEMENTS_ON_DISK, null);
		if (maxElementsOnDisk != null && maxElementsOnDisk != this.maxElementsOnDisk) {
			this.maxElementsOnDisk = maxElementsOnDisk;
		}
		logger.log(Level.OFF, Constants.CACHE_DISK_PATH + "maxElementsOnDisk=" + this.maxElementsOnDisk);

		// expirationCheckIntervalSeconds
		Integer expirationThreadIntervalSeconds = compositeConfiguration.getInteger(
				Constants.CACHE_EXPIRATION_CHECK_INTERVAL_SECONDS, null);
		if (expirationThreadIntervalSeconds != null && expirationThreadIntervalSeconds != this.expirationCheckIntervalSeconds) {
			this.expirationCheckIntervalSeconds = expirationThreadIntervalSeconds;
		}
		logger.log(Level.OFF, Constants.CACHE_DISK_PATH + "expirationCheckIntervalSeconds=" + this.expirationCheckIntervalSeconds);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isCacheEnabled() {
		return cacheEnabled;
	}

	/** {@inheritDoc} */
	@Override
	public String getDiskPath() {
		return diskPath;
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
	 * Gets the expiration check interval seconds.
	 * 
	 * @return the expiration check interval seconds
	 */
	public int getExpirationCheckIntervalSeconds() {
		return expirationCheckIntervalSeconds;
	}

	/**
	 * Sets the expiration check interval seconds.
	 * 
	 * @param expirationCheckIntervalSeconds
	 *            the new expiration check interval seconds
	 */
	public void setExpirationCheckIntervalSeconds(int expirationCheckIntervalSeconds) {
		this.expirationCheckIntervalSeconds = expirationCheckIntervalSeconds;
	}
}
