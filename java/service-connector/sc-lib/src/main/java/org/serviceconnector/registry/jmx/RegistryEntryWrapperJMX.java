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
package org.serviceconnector.registry.jmx;

import java.beans.ConstructorProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class MapBeanWrapperJMX. Wraps registry entries. Needed for JMX access.
 *
 * @author JTraber
 */
public class RegistryEntryWrapperJMX implements IRegistryEntryWrapperMXBean {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(RegistryEntryWrapperJMX.class);
	/** The map bean. */
	private Object obj;
	/** The registry key. */
	private String registryKey;

	/**
	 * Instantiates a new RegistryEntryWrapperJMX.
	 *
	 * @param key the key
	 * @param obj the map bean
	 */
	@ConstructorProperties({ "key", "obj" })
	public RegistryEntryWrapperJMX(String key, Object obj) {
		this.registryKey = key;
		this.obj = obj;
	}

	/** {@inheritDoc} */
	@Override
	public String getEntry() {
		return obj.toString();
	}

	/** {@inheritDoc} */
	@Override
	public String getKey() {
		return registryKey;
	}
}
