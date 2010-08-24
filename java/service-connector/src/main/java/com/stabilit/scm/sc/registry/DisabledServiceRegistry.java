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
package com.stabilit.scm.sc.registry;

import com.stabilit.scm.common.registry.Registry;
import com.stabilit.scm.sc.service.Service;

/**
 * The Class DisabledServiceRegistry.
 */
public final class DisabledServiceRegistry extends Registry<String, Service> {

	/** The instance. */
	private static DisabledServiceRegistry instance = new DisabledServiceRegistry();

	/**
	 * Instantiates a new disabled service registry.
	 */
	private DisabledServiceRegistry() {
	}

	/**
	 * Gets the current instance.
	 * 
	 * @return the current instance
	 */
	public static DisabledServiceRegistry getCurrentInstance() {
		return instance;
	}

	/**
	 * Adds the service.
	 * 
	 * @param key
	 *            the key
	 * @param service
	 *            the service
	 */
	public void addService(String key, Service service) {
		super.put(key, service);
	}

	/**
	 * Gets the service.
	 * 
	 * @param key
	 *            the key
	 * @return the service
	 */
	public Service getService(String key) {
		return this.get(key);
	}

	/**
	 * Removes the service.
	 * 
	 * @param service
	 *            the service
	 */
	public void removeService(Service service) {
		this.removeService(service.getServiceName());
	}

	/**
	 * Removes the service.
	 * 
	 * @param key
	 *            the key
	 * @return the service
	 */
	public Service removeService(String key) {
		return super.remove(key);
	}
}
