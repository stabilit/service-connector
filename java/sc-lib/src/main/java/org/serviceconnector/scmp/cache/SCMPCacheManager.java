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
package org.serviceconnector.scmp.cache;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.ServiceType;

public class SCMPCacheManager {

	/** The scmp cache map. */
	private Map<String, SCMPCache> scmpCacheMap;

	
	/**
	 * Instantiates a new sCMP cache manager.
	 */
	public SCMPCacheManager() {
		// load scmp caches from configuration
		this.scmpCacheMap = new ConcurrentHashMap();
	}

	/**
	 * Inits the scmp cache map according the service registry.
	 */
	public void initialize() {
		ServiceRegistry serviceRegistry = AppContext.getCurrentContext().getServiceRegistry();
		Service services[] = serviceRegistry.getServices();
		for (int i = 0; i < services.length; i++) {
			Service service = services[i];
			String serviceName = service.getServiceName();
			ServiceType serviceType = service.getType();
			if (serviceType == ServiceType.SESSION_SERVICE) {
				this.scmpCacheMap.put(serviceName, new SCMPCache(serviceName));
			}
		}
	}

	public void destroy() {
		SCMPCacheImplFactory.destroy();
	}
	
	/**
	 * Gets the cache.
	 *
	 * @param serviceName the service name
	 * @return the cache
	 */
	public SCMPCache getCache(String serviceName) {
		return this.scmpCacheMap.get(serviceName);
	}
	
}
