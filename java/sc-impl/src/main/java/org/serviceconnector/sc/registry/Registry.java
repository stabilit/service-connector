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
package org.serviceconnector.sc.registry;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.serviceconnector.sc.registry.jmx.IRegistryMXBean;
import org.serviceconnector.sc.registry.jmx.RegistryEntryWrapperJMX;


/**
 * The Class Registry. Provides functionality for general registries.
 * 
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 * @author JTraber
 */
public abstract class Registry<K, V> implements IRegistryMXBean {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(Registry.class);

	/** The registry map. */
	private Map<K, V> registryMap;

	/**
	 * Instantiates a new registry.
	 */
	public Registry() {
		this.registryMap = new ConcurrentHashMap<K, V>();
	}

	/**
	 * Put an entry into map.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	protected void put(K key, V value) {
		this.registryMap.put(key, value);
	}

	/**
	 * Gets an entry by key. If key is null - null will be returned.
	 * 
	 * @param key
	 *            the key
	 * @return the map bean
	 */
	protected V get(K key) {
		if (key == null) {
			return null;
		}
		return registryMap.get(key);
	}

	/**
	 * Removes an entry by key.
	 * 
	 * @param key
	 *            the key
	 */
	protected V remove(K key) {
		if (key == null) {
			return null;
		}
		return this.registryMap.remove(key);
	}

	/**
	 * Gets the size.
	 * 
	 * @return the size
	 */
	public int getSize() {
		return this.registryMap.size();
	}

	/**
	 * Looks up a key.
	 * 
	 * @param key
	 *            the key
	 * @return true, if successful
	 */
	public boolean containsKey(K key) {
		if (key == null) {
			return false;
		}
		return registryMap.containsKey(key);
	}

	/**
	 * Key set. The view's iterator is a "weakly consistent" iterator that will never throw
	 * ConcurrentModificationException, and guarantees to traverse elements as they existed upon construction of the
	 * iterator, and may (but is not guaranteed to) reflect any modifications subsequent to construction.
	 * 
	 * @return the set
	 */
	public Set<K> keySet() {
		return this.registryMap.keySet();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuffer dump = new StringBuffer();
		for (Object key : registryMap.keySet()) {
			dump.append(key);
			dump.append(":");
			dump.append(registryMap.get(key).toString());
			dump.append("|");
		}
		if (getSize() > 0) {
			dump.append("@");
		}
		return dump.toString();
	}

	/** {@inheritDoc} */
	@Override
	public RegistryEntryWrapperJMX[] getEntries() {
		RegistryEntryWrapperJMX[] mapBeanStringJMX = new RegistryEntryWrapperJMX[registryMap.size()];
		int i = 0;
		for (Object key : registryMap.keySet()) {
			mapBeanStringJMX[i] = new RegistryEntryWrapperJMX(key.toString(), registryMap.get(key));
			i++;
		}
		return mapBeanStringJMX;
	}
}
