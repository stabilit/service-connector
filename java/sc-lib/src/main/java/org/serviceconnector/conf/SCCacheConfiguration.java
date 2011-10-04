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
package org.serviceconnector.conf;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cache.SCCacheManager;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.SCMPError;

/**
 * The Class SCCacheConfiguration.
 * This is the main SC cache configuration class, keeping all required configuration parameters.
 * This class is used to setup cache manager {@link SCCacheManager} instance.
 */
public class SCCacheConfiguration {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(SCCacheConfiguration.class);

	/** The cache enabled. */
	private boolean cacheEnabled;
	/** The disk path. */
	private String diskPath = null;
	/** The max elements in memory. */
	private int maxElementsInMemory;
	/** The max elements on disk. */
	private int maxElementsOnDisk;
	/** The expiration thread interval (timeout) in seconds. */
	private int expirationCheckIntervalSeconds;

	/**
	 * Instantiates a new cache configuration.
	 */
	public SCCacheConfiguration() {
		this.cacheEnabled = Constants.DEFAULT_CACHE_ENABLED;
		this.maxElementsInMemory = Constants.DEFAULT_CACHE_MAX_ELEMENTS_IN_MEMORY;
		this.maxElementsOnDisk = Constants.DEFAULT_CACHE_MAX_ELEMENTS_ON_DISK;
		this.expirationCheckIntervalSeconds = Constants.DEFAULT_CACHE_EXPIRATION_CHECK_INTERVAL_SECONDS;
	}

	/**
	 * Loads cache parameters from properties file.<br />
	 * Service Connector cache parameters: <br />
	 * cache.enabled=true<br />
	 * cache.diskPath=../../dev/cache <br />
	 * cache.maxElementsInMemory=10000 <br />
	 * cache.maxElementsOnDisk=1000000 <br />
	 * cache.expirationCheckIntervalSeconds=60 <br />
	 * 
	 * @param compositeConfiguration
	 *            the composite configuration
	 * @throws SCMPValidatorException
	 *             the SCMP validator exception
	 */
	public void load(CompositeConfiguration compositeConfiguration) throws SCMPValidatorException { // synchronized?
		Boolean cacheEnabledConf = compositeConfiguration.getBoolean(Constants.CACHE_ENABLED, null);
		if (cacheEnabledConf != null && this.cacheEnabled != cacheEnabledConf) {
			this.cacheEnabled = cacheEnabledConf;
		}
		LOGGER.info(Constants.CACHE_DISK_PATH + "cacheEnabled=" + this.cacheEnabled);

		// diskPath
		String sDiskPath = compositeConfiguration.getString(Constants.CACHE_DISK_PATH, null);
		if (sDiskPath == null && this.cacheEnabled) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + Constants.CACHE_DISK_PATH
					+ " is missing");
		}
		if (sDiskPath != null && sDiskPath.equals(this.diskPath) == false) {
			this.diskPath = sDiskPath;
		}
		LOGGER.info(Constants.CACHE_DISK_PATH + "=" + this.diskPath);

		// maxElementsInMemory
		Integer maxElementsInMemoryConf = compositeConfiguration.getInteger(Constants.CACHE_MAX_ELEMENTS_IN_MEMORY, null);
		if (maxElementsInMemoryConf != null && maxElementsInMemoryConf != this.maxElementsInMemory) {
			this.maxElementsInMemory = maxElementsInMemoryConf;
		}
		LOGGER.info(Constants.CACHE_MAX_ELEMENTS_IN_MEMORY + "=" + this.maxElementsInMemory);

		// maxElementsOnDisk
		Integer maxElementsOnDiskConf = compositeConfiguration.getInteger(Constants.CACHE_MAX_ELEMENTS_ON_DISK, null);
		if (maxElementsOnDiskConf != null && maxElementsOnDiskConf != this.maxElementsOnDisk) {
			this.maxElementsOnDisk = maxElementsOnDiskConf;
		}
		LOGGER.info(Constants.CACHE_MAX_ELEMENTS_ON_DISK + "=" + this.maxElementsOnDisk);

		// expirationCheckIntervalSeconds
		Integer expirationThreadIntervalSeconds = compositeConfiguration.getInteger(
				Constants.CACHE_EXPIRATION_CHECK_INTERVAL_SECONDS, null);
		if (expirationThreadIntervalSeconds != null && expirationThreadIntervalSeconds != this.expirationCheckIntervalSeconds) {
			this.expirationCheckIntervalSeconds = expirationThreadIntervalSeconds;
		}
		LOGGER.info(Constants.CACHE_EXPIRATION_CHECK_INTERVAL_SECONDS + "=" + this.expirationCheckIntervalSeconds);
	}

	/**
	 * Checks if is cache enabled.
	 * 
	 * @return true, if is cache enabled
	 */
	public boolean isCacheEnabled() {
		return cacheEnabled;
	}

	/**
	 * Gets the disk path.
	 * 
	 * @return the disk path
	 */
	public String getDiskPath() {
		return diskPath;
	}

	/**
	 * Gets the max elements in memory.
	 * 
	 * @return the max elements in memory
	 */
	public int getMaxElementsInMemory() {
		return maxElementsInMemory;
	}

	/**
	 * Gets the max elements on disk.
	 * 
	 * @return the max elements on disk
	 */
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
}
