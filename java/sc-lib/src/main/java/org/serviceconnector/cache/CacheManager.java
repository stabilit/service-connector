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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.serviceconnector.cache.impl.CacheImplFactory;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.CacheLogger;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.ServiceType;
import org.serviceconnector.util.Statistics;
import org.serviceconnector.util.TimeMillis;

public class CacheManager {
	/** The Constant logger. */
	protected final static CacheLogger cacheLogger = CacheLogger.getInstance();

	/** The scmp cache map. */
	private Map<String, Cache> cacheMap;
	private CacheConfiguration cacheConfiguration;
	private Thread expirationThread;
	private ExpirationTimeoutRun expirationTimeoutRun;

	/**
	 * Instantiates a new sCMP cache manager.
	 */
	public CacheManager() {
		// load scmp caches from configuration
		this.cacheMap = new ConcurrentHashMap<String, Cache>();
		this.cacheConfiguration = null;
		this.expirationTimeoutRun = null;
	}

	/**
	 * Inits the scmp cache map according the service registry.
	 */
	public void initialize() throws Exception {
		CacheConfiguration cacheConfiguration = AppContext.getCacheConfiguration();
		if (cacheConfiguration == null) {
			cacheLogger.debug("initialize using default configuration");
			cacheConfiguration = new CacheConfiguration();
		} else {
			cacheLogger.debug("initialize using application context cache configuration");		
		}
		initialize(cacheConfiguration);
	}

	public void initialize(CacheConfiguration cacheConfiguration) throws Exception {
		this.cacheConfiguration = cacheConfiguration;
		if (this.cacheConfiguration == null) {
			this.cacheConfiguration = new CacheConfiguration();
		}
		ServiceRegistry serviceRegistry = AppContext.getServiceRegistry();
		Service services[] = serviceRegistry.getServices();
		for (int i = 0; i < services.length; i++) {
			Service service = services[i];
			String serviceName = service.getServiceName();
			ServiceType serviceType = service.getType();
			if (serviceType == ServiceType.SESSION_SERVICE) {
				Cache cache = new Cache(this, serviceName);
				Statistics.getInstance().incrementCachedFiles(1);
				this.cacheMap.put(serviceName, cache);
			}
		}
		if (this.expirationThread != null) {
			return;
		}
		if (this.cacheConfiguration.getExpirationThreadTimeoutSeconds() > 0) {
			expirationTimeoutRun = new ExpirationTimeoutRun(this.cacheConfiguration.getExpirationThreadTimeoutSeconds());
			this.expirationThread = new Thread(expirationTimeoutRun);
			cacheLogger.debug("start cache expiration thread using timeout (s) = " + this.cacheConfiguration.getExpirationThreadTimeoutSeconds());
			expirationThread.start();
		}		
	}
	
	public void clearAll() {
		CacheImplFactory.clearAll();	
	}
	
	/**
	 * Destroy.
	 */
	public void destroy() {
		cacheLogger.debug("destroy, set expiration thread killed");
		this.expirationTimeoutRun.setKilled(true);
		try {
			cacheLogger.debug("destroy, join expiration thread");
			this.expirationThread.join(5 * TimeMillis.SECOND.getMillis());  // wait 5 seconds max to join this thread
			cacheLogger.debug("destroy, join done");
		} catch (InterruptedException e) {
			cacheLogger.debug(e.toString());
		}
		this.expirationThread = null;
		this.expirationTimeoutRun = null;
		CacheImplFactory.destroy();
	}

	/**
	 * Gets the cache.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return the cache
	 */
	public Cache getCache(String serviceName) {
		return this.cacheMap.get(serviceName);
	}

	/**
	 * Gets the all caches.
	 * 
	 * @return the all caches
	 */
	public Object[] getAllCaches() {
		return (Object[]) this.cacheMap.values().toArray();
	}

	public void removeExpiredCaches() {
		cacheLogger.debug("check for expired messages in cache");
		Object[] caches = this.getAllCaches();
		if (caches == null) {
			return;
		}
		for (Object obj : caches) {
			Cache cache = (Cache) obj;
			cache.removeExpired();
		}
	}

	/**
	 * Gets the scmp cache configuration.
	 * 
	 * @return the scmp cache configuration
	 */
	public ICacheConfiguration getScmpCacheConfiguration() {
		return cacheConfiguration;
	}

	/**
	 * The Class ExpirationTimeoutThread.
	 */
	private class ExpirationTimeoutRun implements Runnable {

		private boolean killed;
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
			cacheLogger.debug("kill cache expiration thread");
			this.killed = killed;
			synchronized (this) {
				this.notifyAll();				
			}
		}

		@Override
		public void run() {
			while (this.killed == false) {
				try {
					synchronized(this) {
					   this.wait(this.timeoutSeconds * 1000);
					}
					if (this.killed) {
						cacheLogger.debug("terminate expiration thread (killed)");
						return;
					}
					CacheManager.this.removeExpiredCaches();
				} catch (InterruptedException e) {
				}
			}
			cacheLogger.debug("terminate expiration thread (killed)");
			return;
		}

	}
}
