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

import java.security.InvalidParameterException;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.net.res.netty.http.NettyHttpEndpoint;

// TODO: Auto-generated Javadoc
/**
 * The Class SCMPCacheConfiguration.
 */
public class SCCacheConfiguration {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCCacheConfiguration.class);

	/** The scmp cache configuration. */
	private static SCCacheConfiguration scmpCacheConfiguration = new SCCacheConfiguration();

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

	/**
	 * Instantiates a new sCMP cache configuration.
	 */
	private SCCacheConfiguration() {
		this.cacheEnabled = false;
		this.cacheName = "scmpCache";
		this.diskPath = "";
		this.diskPersistent = false;
		this.maxElementsInMemory = 10000;
		this.maxElementsOnDisk = 1000000;
	}

	/**
	 * Gets the single instance of SCMPCacheConfiguration.
	 * 
	 * @return single instance of SCMPCacheConfiguration
	 */
	public static SCCacheConfiguration getInstance() {
		return scmpCacheConfiguration;
	}

	/**
	 * Loads cache parameters from properties file.
	 * 
	 * # Service Connector cache parameters cache.enabled=true cache.name=scCache cache.diskPersistent=true
	 * cache.diskPath=../../dev/ cache cache.timeIdleSeconds=60 cache.timeToLiveSeconds=120 cache.maxElementsInMemory=10000
	 * cache.maxElementsOnDisk=1000000
	 * 
	 * @param fileName
	 *            the file name
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void load(String fileName) throws Exception {
		CompositeConfiguration config = new CompositeConfiguration();
		try {
			PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(fileName);
			config.addConfiguration(propertiesConfiguration);
		} catch (Exception e) {
			logger.error("could not find property file : " + fileName);
			logger.info("cache uses default configuration");
			return;
		}
		try {
			this.cacheEnabled = config.getBoolean(Constants.CACHE_ENABLED);
			logger.info("cache configuration: cache enabled is " + this.cacheEnabled);
		} catch (Exception e) {
			logger.error("CACHE_ENABLED = " + e.toString());
		}
		try {
			String sCacheName = config.getString(Constants.CACHE_NAME);
			if (sCacheName != null) {
				this.cacheName = sCacheName;
			}
			logger.info("cache configuration: cache name = " + this.cacheName);
		} catch (Exception e) {
			logger.error("CACHE_NAME = " + e.toString());
		}
		try {
			this.diskPersistent = config.getBoolean(Constants.CACHE_DISK_PERSISTENT);
			logger.info("cache configuration: cache disk persistent is " + this.diskPersistent);
		} catch (Exception e) {
			logger.error("CACHE_DISK_PERSISTENT = " + e.toString());
		}
		try {
			String sDiskPath = config.getString(Constants.CACHE_DISK_PATH);
			if (sDiskPath != null) {
				this.diskPath = sDiskPath;
			}
			logger.info("cache configuration: disk path = " + this.diskPath);
		} catch (Exception e) {
			logger.error("CACHE_DISK_PATH = " + e.toString());
		}
		try {
			int maxElementsInMemory = config.getInt(Constants.CACHE_MAX_ELEMENTS_IN_MEMORY);
			if (maxElementsInMemory > 0) {
				this.maxElementsInMemory = maxElementsInMemory;
			}
			logger.info("cache configuration: max elements in memory = " + this.maxElementsInMemory);
		} catch (Exception e) {
			logger.error("CACHE_MAX_ELEMENTS_IN_MEMORY = " + e.toString());
		}
		try {
			int maxElementsOnDisk = config.getInt(Constants.CACHE_MAX_ELEMENTS_ON_DISK);
			if (maxElementsOnDisk > 0) {
				this.maxElementsOnDisk = maxElementsOnDisk;
			}
			logger.info("cache configuration: max elements on disk = " + this.maxElementsOnDisk);
		} catch (Exception e) {
			logger.error("CACHE_MAX_ELEMENTS_ON_DISK = " + e.toString());
		}
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
	 * Checks if is disk persistent.
	 * 
	 * @return true, if is disk persistent
	 */
	public boolean isDiskPersistent() {
		return diskPersistent;
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
	 * Gets the cache name.
	 * 
	 * @return the cache name
	 */
	public String getCacheName() {
		return cacheName;
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
}
