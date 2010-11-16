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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.ServiceType;

public class SCCacheManager {

	/** The scmp cache map. */
	private Map<String, SCCache> scmpCacheMap;
	private SCCacheConfiguration scmpCacheConfiguration;

	
	/**
	 * Instantiates a new sCMP cache manager.
	 */
	public SCCacheManager() {
		// load scmp caches from configuration
		this.scmpCacheMap = new ConcurrentHashMap<String, SCCache>();
		this.scmpCacheConfiguration = null;
	}

	/**
	 * Inits the scmp cache map according the service registry.
	 */
	public void initialize(String configFile) throws Exception {
		this.scmpCacheConfiguration = SCCacheConfiguration.getInstance();
		this.scmpCacheConfiguration.load(configFile);
		
		ServiceRegistry serviceRegistry = AppContext.getCurrentContext().getServiceRegistry();
		Service services[] = serviceRegistry.getServices();
		for (int i = 0; i < services.length; i++) {
			Service service = services[i];
			String serviceName = service.getServiceName();
			ServiceType serviceType = service.getType();
			if (serviceType == ServiceType.SESSION_SERVICE) {
				this.scmpCacheMap.put(serviceName, new SCCache(this, serviceName));
			}
		}
	}

	public void destroy() {
		SCCacheImplFactory.destroy();
	}
	
	/**
	 * Gets the cache.
	 *
	 * @param serviceName the service name
	 * @return the cache
	 */
	public SCCache getCache(String serviceName) {
		return this.scmpCacheMap.get(serviceName);
	}

	/**
	 * Gets the all caches.
	 *
	 * @return the all caches
	 */
	public Object[] getAllCaches() {
		return (Object[]) this.scmpCacheMap.values().toArray();
	}

	public SCCacheConfiguration getScmpCacheConfiguration() {
		return scmpCacheConfiguration;
	}
}
