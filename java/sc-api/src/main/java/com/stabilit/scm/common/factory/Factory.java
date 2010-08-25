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
package com.stabilit.scm.common.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.listener.LoggerPoint;

/**
 * The Class Factory. SuperClass for factories.<br>
 * <br>
 * *** ATTENTION ***<br>
 * Subclasses must not have any properties other then private static. Otherwise these properties will not be
 * thread-safe! This construct is an extended factory pattern. The factory stores base instances of classes in a map. To
 * create an other instance the factory takes the base instance and calls there newInstance method. So only the base
 * instance knows how to create himself and factory does not to know more details. The base instance need to be created
 * by concrete subclasses of the <code>Factory</code>
 * 
 * @author JTraber
 */
public abstract class Factory {
	
	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(Factory.class);

	/** The Constant DEFAULT. Key for default instance. */
	protected static final String DEFAULT = "default";
	/** The map stores base instances by a key. */
	protected Map<Object, IFactoryable> baseInstances = new ConcurrentHashMap<Object, IFactoryable>();

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
	protected void add(Object key, IFactoryable factoryInstance) {
		baseInstances.put(key, factoryInstance);
	}

	/**
	 * Removes the.
	 * 
	 * @param key
	 *            the key
	 */
	protected void remove(Object key) {
		baseInstances.remove(key);
	}

	/**
	 * Gets the single instance of Factory.
	 * 
	 * @param key
	 *            the key
	 * @return single instance of Factory
	 */
	public IFactoryable getInstance(Object key) {
		IFactoryable factoryInstance = baseInstances.get(key);
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
			// if key is not found return default
			LoggerPoint.getInstance().fireWarn(this,
					"key : " + key + " not found in baseInstances of factory, returned default instance");
			return this.getInstance(DEFAULT);
		}
		// invoke the base instance constructor
		return factoryInstance.newInstance();
	}
}
