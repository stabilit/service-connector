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
package org.serviceconnector.registry;

import org.serviceconnector.cache.ISCCache;

/**
 * The Class CacheRegistry. Registries stores caches available in SC.
 */
public class CacheRegistry extends Registry<String, ISCCache<?>> {

	/**
	 * Adds the cache.
	 * 
	 * @param key
	 *            the key
	 * @param cache
	 *            the cache
	 */
	public void addCache(String key, ISCCache<?> cache) {
		super.put(key, cache);
	}

	/**
	 * Gets the cache.
	 * 
	 * @param key
	 *            the key
	 * @return the cache
	 */
	public ISCCache<?> getCache(String key) {
		return this.get(key);
	}

	/**
	 * Removes the cache.
	 * 
	 * @param key
	 *            the key
	 */
	public void removeCache(String key) {
		this.remove(key);
	}
}
