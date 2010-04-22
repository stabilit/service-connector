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

import org.apache.log4j.Logger;

import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.registry.Registry;

/**
 * @author JTraber
 * 
 */
public final class ServiceRegistry extends Registry {

	private static ServiceRegistry instance = new ServiceRegistry();

	private ServiceRegistry() {
		log = Logger.getLogger("serviceRegistry." + ServiceRegistry.class.getName());
	}

	public static ServiceRegistry getCurrentInstance() {
		return instance;
	}

	public void add(Object key, ServiceRegistryItem item) {
		this.put(key, item);
	}

	public synchronized ServiceRegistryItem allocate(Object key, SCMP scmp) throws Exception {
		ServiceRegistryItem item = (ServiceRegistryItem) this.get(key); // is this a list, TODO
		if (item.isAllocated()) {
			return null;
		}
		item.allocate(scmp);
		return item;
	}

	public synchronized void deallocate(ServiceRegistryItem item, SCMP scmp) throws Exception {
		if (item.isAllocated()) {
			item.deallocate(scmp);
		}
		return;
	}

}
