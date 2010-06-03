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
import com.stabilit.sc.listener.ExceptionPoint;
import com.stabilit.sc.listener.RuntimePoint;
import com.stabilit.sc.scmp.IRequest;
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
	 * @param request
	 *            the request
	 * @return the service registry item
	 * @throws Exception
	 *             the exception
	 */
	public synchronized ServiceRegistryItem allocate(IRequest request) throws Exception {
		SCMPMessage scmpMessage = request.getMessage();
		String serviceName = scmpMessage.getServiceName();
		ServiceRegistryItemPool itemPool = (ServiceRegistryItemPool) this.get(serviceName);
		if (itemPool.isAvailable() == false) {
			return null;
		}
		ServiceRegistryItem item = itemPool.getAvailableItem();
		item.srvCreateSession(scmpMessage);
		return item;
	}

	/**
	 * Deallocate session from backend server.
	 * 
	 * @param item
	 *            the item
	 * @param request
	 *            the request
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void deallocate(ServiceRegistryItem item, IRequest request) throws Exception {
		SCMPMessage scmpMessage = request.getMessage();
		if (item.isAllocated()) {
			// try catch necessary because method gets invoked in error scenario
			try {
				item.srvDeleteSession(scmpMessage);
			} catch (CommunicationException ex) {
				ExceptionPoint.getInstance().fireException(this, ex);
			}
		}
		ServiceRegistryItemPool itemPool = item.myItemPool;
		if (itemPool == null) {
			RuntimePoint.getInstance().fireRuntime(this, "ServiceRegistryItem has not item pool.");
			return;
		}
		itemPool.freeItem(item);
		return;
	}
}
