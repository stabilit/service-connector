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

import java.util.logging.Logger;

import com.stabilit.sc.listener.WarningListenerSupport;
import com.stabilit.sc.scmp.SCMP;

/**
 * @author JTraber
 * 
 */
public final class ServiceRegistry extends Registry {

	private static ServiceRegistry instance = new ServiceRegistry();

	private ServiceRegistry() {
	}

	public static ServiceRegistry getCurrentInstance() {
		return instance;
	}

	public void add(Object key, ServiceRegistryItemPool itemPool) {
		this.put(key, itemPool);
	}

	public synchronized ServiceRegistryItem allocate(Object key, SCMP scmp) throws Exception {
		ServiceRegistryItemPool itemPool = (ServiceRegistryItemPool) this.get(key); // is this a list, TODO
		if (itemPool.isAvailable() == false) {
			return null;
		}
		ServiceRegistryItem item = itemPool.getAvailableItem();
		item.srvCreateSession(scmp);
		return item;
	}

	public synchronized void deallocate(ServiceRegistryItem item, SCMP scmp) throws Exception {
		if (item.isAllocated()) {
			item.srvDeleteSession(scmp);
		}
		ServiceRegistryItemPool itemPool = item.myItemPool;
		if (itemPool == null) {
			WarningListenerSupport.getInstance().fireWarning(this, "ServiceRegistryItem has not item pool.");
			return;
		}
		itemPool.freeItem(item);
		return;
	}

}
