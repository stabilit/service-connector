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
package com.stabilit.sc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.stabilit.sc.registry.jmx.IRegistryMXBean;
import com.stabilit.sc.registry.jmx.MapBeanWrapperJMX;
import com.stabilit.sc.util.MapBean;

/**
 * The Class Registry. Provides functionality for general registries.
 * 
 * @author JTraber
 */
public abstract class Registry implements IRegistryMXBean {

	/** The registry map. */
	private Map<Object, MapBean<?>> registryMap;

	/**
	 * Instantiates a new registry.
	 */
	public Registry() {
		registryMap = new ConcurrentHashMap<Object, MapBean<?>>();
	}

	/**
	 * Put an entry into map.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	protected void put(Object key, MapBean<?> value) {
		registryMap.put(key, value);
	}

	/**
	 * Gets an entry by key.
	 * 
	 * @param key
	 *            the key
	 * @return the map bean<?>
	 */
	public MapBean<?> get(Object key) {
		return registryMap.get(key);
	}

	/**
	 * Removes an entry by key.
	 * 
	 * @param key
	 *            the key
	 */
	public void remove(Object key) {
		this.registryMap.remove(key);
	}

	/**
	 * Looks up a key.
	 * 
	 * @param key
	 *            the key
	 * @return true, if successful
	 */
	public boolean containsKey(Object key) {
		return registryMap.containsKey(key);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuffer dump = new StringBuffer();
		for (Object key : registryMap.keySet()) {
			dump.append(key);
			dump.append(":");
			dump.append(registryMap.get(key).toString());
		}
		return dump.toString();
	}

	/** {@inheritDoc} */
	@Override
	public MapBeanWrapperJMX[] getEntries() {
		MapBeanWrapperJMX[] mapBeanStringJMX = new MapBeanWrapperJMX[registryMap.size()];
		int i = 0;
		for (Object key : registryMap.keySet()) {
			mapBeanStringJMX[i] = new MapBeanWrapperJMX(key.toString(), (MapBean<?>) registryMap.get(key));
			i++;
		}
		return mapBeanStringJMX;
	}
}
