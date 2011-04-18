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
package org.serviceconnector.cache;

import java.util.AbstractCollection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.serviceconnector.Constants;
import org.serviceconnector.cache.impl.CacheImplFactory;
import org.serviceconnector.conf.CacheConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.CacheLogger;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.ServiceType;
import org.serviceconnector.util.Statistics;
import org.serviceconnector.util.XMLDumpWriter;

/**
 * The Class CacheManager is the overall cache control class. All cache instance were controlled
 * by this cache manager.
 * The cache manager starts an internal thread controlling any cache entries for expiration or any
 * other state problems.
 */
public class CacheManager {

	/**
	 * this array is only used to run the generics method version {@link AbstractCollection#toArray(Object[])} inside
	 * {@link #getAllCaches} method.
	 */
	private static final Cache[] GENERICS_ARRAY_TEMPLATE = new Cache[0];
	/** The cache map. */
	private Map<String, Cache> cacheMap;
	/** The cache configuration. */
	private CacheConfiguration cacheConfiguration;
	/** The expiration thread. */
	private Thread expirationThread;
	/** The expiration timeout run. */
	private ExpirationTimeoutRun expirationTimeoutRun;

	/**
	 * Instantiates a new cache manager.
	 */
	public CacheManager() {
		// load scmp caches from configuration
		this.cacheMap = new ConcurrentHashMap<String, Cache>();
		this.cacheConfiguration = null;
		this.expirationTimeoutRun = null;
	}

	/**
	 * Initialize (load) the cache manager instance according given cache configuration instance.
	 * 
	 * @param cacheConfiguration
	 *            the cache configuration
	 * @throws Exception
	 *             the exception
	 */
	public void load(CacheConfiguration cacheConfiguration) throws Exception {
		this.cacheConfiguration = cacheConfiguration;
		if (this.cacheConfiguration == null) {
			this.cacheConfiguration = new CacheConfiguration();
		}
		if (this.cacheConfiguration.isCacheEnabled() == false) {
			CacheLogger.debug("cache is not enabled");
			return;
		}
		ServiceRegistry serviceRegistry = AppContext.getServiceRegistry();
		Service[] services = serviceRegistry.getServices();
		for (int i = 0; i < services.length; i++) {
			Service service = services[i];
			String serviceName = service.getName();
			ServiceType serviceType = service.getType();
			if (serviceType == ServiceType.SESSION_SERVICE || serviceType == ServiceType.CASCADED_SESSION_SERVICE) {
				Cache cache = new Cache(this, serviceName);
				Statistics.getInstance().incrementCachedFiles(1);
				this.cacheMap.put(serviceName, cache);
			}
		}
		if (this.expirationThread != null) {
			return;
		}
		if (this.cacheConfiguration.getExpirationCheckIntervalSeconds() > 0) {
			expirationTimeoutRun = new ExpirationTimeoutRun(this.cacheConfiguration.getExpirationCheckIntervalSeconds());
			this.expirationThread = new Thread(expirationTimeoutRun);
			CacheLogger.debug("start cache expiration thread using timeout (s) = "
					+ this.cacheConfiguration.getExpirationCheckIntervalSeconds());
			expirationThread.start();
		}
	}

	/**
	 * Clear all caches.
	 */
	public void clearAll() {
		CacheImplFactory.clearAll();
	}

	/**
	 * Clear loading cache composite for session
	 */
	public void clearLoading(String sessionId) {
		Cache[] cacheArray = this.getAllCaches();
		for (int i = 0; i < cacheArray.length; i++) {
			Cache cache = cacheArray[i];
			cache.removeLoadingComposite(sessionId);
		}
	}

	/**
	 * Destroy all caches controlled by this cache manager.
	 */
	public void destroy() {
		CacheLogger.debug("destroy, set expiration thread killed");
		if (this.expirationTimeoutRun != null) {
			this.expirationTimeoutRun.setKilled(true);
			try {
				CacheLogger.debug("destroy, join expiration thread");
				this.expirationThread.join(5 * Constants.SEC_TO_MILLISEC_FACTOR); // wait 5 seconds max to join this thread
				CacheLogger.debug("destroy, join done");
			} catch (InterruptedException e) {
				CacheLogger.debug(e.toString());
			}
			this.expirationThread = null;
			this.expirationTimeoutRun = null;
		}
		CacheImplFactory.destroy();
	}

	/**
	 * Gets the cache for given service name.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return the cache
	 */
	public Cache getCache(String serviceName) {
		if (serviceName == null) {
			return null;
		}
		return this.cacheMap.get(serviceName);
	}

	/**
	 * Gets all caches registerred by this cache manager.
	 * 
	 * @return the all caches
	 */
	public Cache[] getAllCaches() {
		return (Cache[]) this.cacheMap.values().toArray(CacheManager.GENERICS_ARRAY_TEMPLATE);
	}

	/**
	 * Checks if is cache enabled.
	 * 
	 * @return true, if is cache enabled
	 */
	public boolean isCacheEnabled() {
		if (this.cacheConfiguration == null) {
			return false;
		}
		return this.cacheConfiguration.isCacheEnabled();
	}

	/**
	 * Removes the expired caches.
	 */
	public void removeExpiredCaches() {
		CacheLogger.debug("check for expired messages in cache");
		Cache[] caches = this.getAllCaches();
		if (caches == null) {
			return;
		}
		for (Cache cache : caches) {
			cache.removeExpired();
		}
	}

	/**
	 * Gets the cache configuration.
	 * 
	 * @return the cache configuration
	 */
	public ICacheConfiguration getCacheConfiguration() {
		return cacheConfiguration;
	}

	/**
	 * Dump the cache manager into the xml writer.
	 * 
	 * @param writer
	 *            the writer
	 * @throws Exception
	 *             the exception
	 */
	public void dump(XMLDumpWriter writer) throws Exception {
		writer.writeStartElement("cache-manager");
		writer.writeAttribute("enabled", this.isCacheEnabled());
		writer.writeAttribute("diskPath", this.getCacheConfiguration().getDiskPath());
		writer.writeAttribute("maxElementsInMemory", this.getCacheConfiguration().getMaxElementsInMemory());
		writer.writeAttribute("maxElementsOnDisk", this.getCacheConfiguration().getMaxElementsOnDisk());
		writer.writeStartElement("cache-list");
		Cache[] caches = this.getAllCaches();
		if (caches == null) {
			writer.writeAttribute("information", "no caches found");
		} else {
			for (Cache cache : caches) {
				cache.dump(writer);
			}
		}
		writer.writeEndElement(); // end of cache-list
		writer.writeEndElement(); // end of cache-manager

	}

	/**
	 * The Class ExpirationTimeoutThread.
	 * This class control within a thread any cache instance for expiration or other state failures.
	 */
	private class ExpirationTimeoutRun implements Runnable {

		/** The killed. */
		private boolean killed;
		/** The timeout seconds. */
		private int timeoutSeconds;

		/**
		 * Instantiates a new expiration timeout thread.
		 * 
		 * @param timeoutSeconds
		 *            the timeout seconds
		 */
		public ExpirationTimeoutRun(int timeoutSeconds) {
			this.killed = false;
			this.timeoutSeconds = timeoutSeconds;
		}

		/**
		 * Sets the killed.
		 * 
		 * @param killed
		 *            the new killed
		 */
		public void setKilled(boolean killed) {
			CacheLogger.debug("kill cache expiration thread");
			this.killed = killed;
			synchronized (this) {
				this.notifyAll();
			}
		}

		/**
		 * cache expiration thread run method, checks withing given interval if any cache elements were expired and removes them
		 * from cache.
		 */
		@Override
		public void run() {
			while (this.killed == false) {
				try {
					synchronized (this) {
						this.wait(this.timeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
					}
					if (this.killed) {
						CacheLogger.debug("terminate expiration thread (killed)");
						return;
					}
					CacheManager.this.removeExpiredCaches();
				} catch (InterruptedException e) {
					CacheLogger.debug(e.getMessage());
				}
			}
			CacheLogger.debug("terminate expiration thread (killed)");
			return;
		}
	}
}
