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
package org.serviceconnector.cache.impl;

import org.serviceconnector.cache.ICacheConfiguration;

/**
 * A factory for creating CacheImpl objects.
 */
public final class CacheImplFactory {

	/**
	 * Instantiates a new cache impl factory.
	 */
	private CacheImplFactory() {
	}
	
	/**
	 * Gets the default cache impl.
	 * 
	 * @param scmpCacheConfiguration
	 *            the scmp cache configuration
	 * @param serviceName
	 *            the service name
	 * @return the default cache impl
	 */
	public static ICacheImpl getDefaultCacheImpl(ICacheConfiguration scmpCacheConfiguration, String serviceName) {
		return new EHCacheImpl(scmpCacheConfiguration, serviceName);
	}
	
	/**
	 * Destroy.
	 */
	public static void destroy() {
		EHCacheImpl.destroy();
	}

	/**
	 * Clear all.
	 */
	public static void clearAll() {
		EHCacheImpl.clearAll();
	}
}
