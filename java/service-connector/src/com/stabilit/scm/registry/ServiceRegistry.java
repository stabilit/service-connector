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
package com.stabilit.scm.registry;

import com.stabilit.scm.cln.net.CommunicationException;
import com.stabilit.scm.listener.ExceptionPoint;
import com.stabilit.scm.listener.RuntimePoint;
import com.stabilit.scm.scmp.IRequest;
import com.stabilit.scm.scmp.SCMPMessage;
import com.stabilit.scm.util.MapBean;

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
		// try to get service list
		ServicePoolList servicePoolList = (ServicePoolList)this.getServicePoolList(key);
		servicePoolList.add(itemPool);
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
		ServiceRegistryItemPool itemPool = (ServiceRegistryItemPool) this.getServiceItemPool(serviceName);
		if (itemPool == null) {
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
		ServiceRegistryItemPool itemPool = item.myParentPool;
		if (itemPool == null) {
			RuntimePoint.getInstance().fireRuntime(this, "ServiceRegistryItem has not item pool.");
			return;
		}
		itemPool.freeItem(item);
		return;
	}

	private synchronized ServiceRegistryItemPool getServiceItemPool(Object key) {
		ServicePoolList servicePoolList = (ServicePoolList)this.getServicePoolList(key);
		if (servicePoolList == null) {
			return null;
		}
		ServiceRegistryItemPool itemPool = servicePoolList.getAvailable(); 
		return itemPool;
	}


	/**
	 * Gets the service pool list.
	 * 
	 * @param key the key
	 * 
	 * @return the service pool list
	 */
	private ServicePoolList getServicePoolList(Object key) {
		// try to get service list
		ServicePoolList servicePoolList = (ServicePoolList)this.get(key);
		if (servicePoolList == null) {
			this.put(key, new ServicePoolList((String) key));
		}
		return (ServicePoolList)this.get(key);
	}

	// member class ServicePoolList
	private class ServicePoolList extends MapBean<Object> {
		private String serviceName;
		private int capacity = 16;
		private int nextIndex = 0;
		private int activeSize = 0;
		private ServiceRegistryItemPool[] poolArray;
		
		public ServicePoolList(String serviceName) {
			this.serviceName = serviceName;
			poolArray = new ServiceRegistryItemPool[this.capacity];
			this.activeSize = 0;
		}

		public synchronized void add(ServiceRegistryItemPool itemPool) {
			if (activeSize == this.poolArray.length) {
				// reorganize array
				this.reorganize();
			}
			for (int i = 0; i < this.poolArray.length; i++) {
				if (this.poolArray[i] == null) {
					this.activeSize++;
					this.poolArray[i] = itemPool;
					break;
				}
			}			
		}
		
		public synchronized void remove(ServiceRegistryItemPool itemPool) {
    	    for (int i = 0; i < poolArray.length; i++) {
				if (poolArray[i] == itemPool) {
					poolArray[i] = null;
					this.activeSize--;
					break;
				}
			}		
		}
		
		public synchronized ServiceRegistryItemPool getAvailable() {
			if (this.activeSize <= 0) {
				return null;
			}
    	    for (int i = nextIndex; i < poolArray.length; i++) {
    	    	ServiceRegistryItemPool itemPool = this.poolArray[i];
    	    	if (itemPool == null) {
    	    		continue;
    	    	}
    	    	if (itemPool.isAvailable()) {
    	    		this.nextIndex = i+1;
    	    		if (this.nextIndex == this.poolArray.length) {
    	    			this.nextIndex = 0;
    	    		}
    	    		return itemPool;
    	    	}
    	    }
    	    for (int i = 0; i <= nextIndex; i++) {
    	    	ServiceRegistryItemPool itemPool = this.poolArray[i];
    	    	if (itemPool == null) {
    	    		continue;
    	    	}
    	    	if (itemPool.isAvailable()) {
    	    		this.nextIndex = i;
    	    		return itemPool;
    	    	}
    	    }    	   
    	    return null;
		}

		/**
		 * @return the serviceName
		 */
		public String getServiceName() {
			return serviceName;
		}
				
		
		private void reorganize() {
			ServiceRegistryItemPool[] newItemPool = new ServiceRegistryItemPool[this.poolArray.length << 1];
			for (int i = 0; i < this.poolArray.length; i++) {
				newItemPool[i] = this.poolArray[i];
			}
			this.poolArray = newItemPool;
		}
	}
}
