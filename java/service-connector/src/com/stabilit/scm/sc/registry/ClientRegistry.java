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
import com.stabilit.scm.common.util.MapBean;

/**
 * The Class ClientRegistry. Registry stores entries for properly connected and attached clients.
 * 
 * @author JTraber
 */
public final class ClientRegistry extends Registry {

	/** The instance. */
	private static ClientRegistry instance = new ClientRegistry();

	/**
	 * Instantiates a new ClientRegistry.
	 */
	private ClientRegistry() {
	}

	/**
	 * Gets the current instance.
	 * 
	 * @return the current instance
	 */
	public static ClientRegistry getCurrentInstance() {
		return instance;
	}

	/**
	 * Adds an entry.
	 * 
	 * @param key
	 *            the key
	 * @param mapBean
	 *            the map bean
	 *            TODO (TRN) (DONE JOT) what is this good for? - adding a connection to the registry
	 */
	public void add(Object key, MapBean<Object> mapBean) {
		this.put(key, mapBean);
	}
}
