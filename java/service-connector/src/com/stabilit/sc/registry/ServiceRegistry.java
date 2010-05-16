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

import com.stabilit.sc.cln.net.CommunicationException;
import com.stabilit.sc.listener.ExceptionListenerSupport;
import com.stabilit.sc.listener.WarningListenerSupport;
import com.stabilit.sc.scmp.SCMPMessage;

/**
 * The Class ServiceRegistry. Registry stores entries for properly registered services (backend servers).
 * 
 * @author JTraber
 */
public final class ServiceRegistry extends Registry {

	/** The instance. */
	private static ServiceRegistry instance = new ServiceRegistry();

	/**
	 * Instantiates a new service registry.
	 */
	private ServiceRegistry() {
	}

	/**
	 * Gets the current instance.
	 * 
	 * @return the current instance
	 */
	public static ServiceRegistry getCurrentInstance() {
		return instance;
	}

	/**
	 * Adds an entry.
	 * 
	 * @param key
	 *            the key
	 * @param itemPool
	 *            the item pool
	 */
	public void add(Object key, ServiceRegistryItemPool itemPool) {
		this.put(key, itemPool);
	}

	/**
	 * Allocate a session on a backend server.
	 * 
	 * @param key
	 *            the key
	 * @param scmp
	 *            the scmp
	 * @return the service registry item
	 * @throws Exception
	 *             the exception
	 */
	public synchronized ServiceRegistryItem allocate(Object key, SCMPMessage scmp) throws Exception {
		ServiceRegistryItemPool itemPool = (ServiceRegistryItemPool) this.get(key); // is this a list, TODO
		if (itemPool.isAvailable() == false) {
			return null;
		}
		ServiceRegistryItem item = itemPool.getAvailableItem();
		item.srvCreateSession(scmp);
		return item;
	}

	/**
	 * Deallocate session from backend server.
	 * 
	 * @param item
	 *            the item
	 * @param scmp
	 *            the scmp
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void deallocate(ServiceRegistryItem item, SCMPMessage scmp) throws Exception {
		if (item.isAllocated()) {
			// try catch necessary because method gets invoked in error scenario
			try {
				item.srvDeleteSession(scmp);
			} catch (CommunicationException ex) {
				ExceptionListenerSupport.getInstance().fireException(this, ex);
			}
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
