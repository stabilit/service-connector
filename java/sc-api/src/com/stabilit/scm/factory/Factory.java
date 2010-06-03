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
package com.stabilit.sc.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Class Factory. SuperClass for factories.
 * 
 * *** ATTENTION ***
 * Subclasses must not have any properties other then private static.
 * Otherwise these properties will not be thread-safe!
 * 
 * @author JTraber
 */
public class Factory {

	/** The Constant DEFAULT. Key for default instance. */
	protected static final String DEFAULT = "default";
	/** The factory map stores instances created by factory. */
	protected Map<Object, IFactoryable> factoryMap = new ConcurrentHashMap<Object, IFactoryable>();

	/**
	 * Gets the single instance of Factory.
	 * 
	 * @return single instance of Factory
	 */
	public IFactoryable getInstance() {
		return getInstance(DEFAULT);
	}

	/**
	 * Adds the.
	 * 
	 * @param key
	 *            the key
	 * @param factoryInstance
	 *            the factory instance
	 */
	public void add(Object key, IFactoryable factoryInstance) {
		factoryMap.put(key, factoryInstance);
	}

	/**
	 * Removes the.
	 * 
	 * @param key
	 *            the key
	 */
	public void remove(Object key) {
		factoryMap.remove(key);
	}

	/**
	 * Gets the single instance of Factory.
	 * 
	 * @param key
	 *            the key
	 * @return single instance of Factory
	 */
	public IFactoryable getInstance(Object key) {
		IFactoryable factoryInstance = factoryMap.get(key);
		return factoryInstance;
	}

	/**
	 * New instance.
	 * 
	 * @return an instance
	 */
	public IFactoryable newInstance() {
		return newInstance(DEFAULT);
	}

	/**
	 * New instance.
	 * 
	 * @param key
	 *            the key
	 * @return an instance
	 */
	public IFactoryable newInstance(Object key) {
		IFactoryable factoryInstance = this.getInstance(key);
		if (factoryInstance == null) {
			return null;
		}
		return factoryInstance.newInstance();
	}
}
