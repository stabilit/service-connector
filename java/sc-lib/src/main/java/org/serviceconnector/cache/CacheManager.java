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
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.ServiceType;

public class CacheManager {

	/** The scmp cache map. */
	private Map<String, Cache> cacheMap;
	private CacheConfiguration cacheConfiguration;

	/**
	 * Instantiates a new sCMP cache manager.
	 */
	public CacheManager() {
		// load scmp caches from configuration
		this.cacheMap = new ConcurrentHashMap<String, Cache>();
		this.cacheConfiguration = null;
	}

	/**
	 * Inits the scmp cache map according the service registry.
	 */
	public void initialize() throws Exception {
		this.cacheConfiguration = AppContext.getCurrentContext().getConfigurationContext().getCacheConfiguration();
		if (this.cacheConfiguration == null) {
			this.cacheConfiguration = new CacheConfiguration();
		}
		ServiceRegistry serviceRegistry = AppContext.getCurrentContext().getServiceRegistry();
		Service services[] = serviceRegistry.getServices();
		for (int i = 0; i < services.length; i++) {
			Service service = services[i];
			String serviceName = service.getServiceName();
			ServiceType serviceType = service.getType();
			if (serviceType == ServiceType.SESSION_SERVICE) {
				this.cacheMap.put(serviceName, new Cache(this, serviceName));
			}
		}
	}

	public void destroy() {
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

	public ICacheConfiguration getScmpCacheConfiguration() {
		return cacheConfiguration;
	}
}
