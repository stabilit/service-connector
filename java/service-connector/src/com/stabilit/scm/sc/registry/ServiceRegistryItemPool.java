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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.stabilit.scm.scmp.IRequest;
import com.stabilit.scm.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.scmp.SCMPMessage;
import com.stabilit.scm.srv.ctx.IResponderContext;
import com.stabilit.scm.util.MapBean;

/**
 * The Class ServiceRegistryItemPool. Pools incoming request for one service. It depends on service resource and
 * type how pooling is managed.
 * 
 * @author JTraber
 */
/**
 * @author JTraber
 *
 */
public class ServiceRegistryItemPool extends MapBean<String> {

	/** The max items. */
	private int maxSessions = -1;
	
	/** The multithreaded. */
	private boolean multithreaded = false;
	
	/** The request. */
	private IRequest request;
	
	/** The responder context. */
	private IResponderContext respContext;
	
	/** The free item list. */
	private List<ServiceRegistryItem> freeItemList;
	
	/** The allocated item list. */
	private List<ServiceRegistryItem> allocatedItemList;

	/**
	 * Instantiates a new service registry item pool.
	 * 
	 * @param request the request
	 */
	public ServiceRegistryItemPool(IRequest request) {
		this.request = request;
		this.freeItemList = Collections.synchronizedList(new ArrayList<ServiceRegistryItem>());
		this.allocatedItemList = Collections.synchronizedList(new ArrayList<ServiceRegistryItem>());
		ResponderRegistry responderRegistry = ResponderRegistry.getCurrentInstance();
		this.respContext = responderRegistry.getCurrentContext();
		// init maxSessions and multithreaded attributes from given request
		SCMPMessage scmpReq = request.getMessage();
		this.maxSessions = scmpReq.getHeaderInt(SCMPHeaderAttributeKey.MAX_SESSIONS);  // required attribute
		this.multithreaded = false; // scmpReq.getHeaderBoolean(SCMPHeaderAttributeKey.MULTI_THREADED); // required attribute
		this.initFreeList();
	}

	/**
	 * Checks if is available.
	 * 
	 * @return true, if is available
	 */
	public synchronized boolean isAvailable() {
		if (isNoLimit()) {
			// there is no limit
			return true;
		}
		if (this.isMultithreaded()) {
			// we have max session limit
			int allocatedSize = this.allocatedItemList.size();
			if (allocatedSize >= this.maxSessions) {
				return false;
			}
			return true;
		}
		return this.freeItemList.isEmpty() == false;
	}

	/**
	 * Checks if is no limit.
	 * 
	 * @return true, if is no limit
	 */
	public synchronized boolean isNoLimit() {
		return this.maxSessions <= 0;
	}
	
	/**
	 * Checks if is multithreaded.
	 * 
	 * @return true, if is multithreaded
	 */
	public boolean isMultithreaded() {
		return this.multithreaded;
	}
	
	/**
	 * Gets the max sessions.
	 * 
	 * @return the maxSessions
	 */
	public int getMaxSessions() {
		return this.maxSessions;
	}

	/**
	 * Gets the available item.
	 * 
	 * @return the available item
	 */
	public synchronized ServiceRegistryItem getAvailableItem() {
		if (this.isAvailable() == false) {
			return null;
		}
		if (this.isNoLimit()) {
			ServiceRegistryItem item = new ServiceRegistryItem(this.request, this.respContext);
			item.myParentPool = this;
			this.allocatedItemList.add(item);
			return item;
		}
		ServiceRegistryItem item = null;
		if (this.freeItemList.isEmpty() == false) {
		    item = freeItemList.get(0);
		}
		if (item == null) {
			// check if we can allocate a new one
			int allocatedSize = this.allocatedItemList.size();
			if (allocatedSize >= this.maxSessions) {
				return null;
			}
			// we can allocate more
			item = new ServiceRegistryItem(this.request, this.respContext);
			item.myParentPool = this;
			this.allocatedItemList.add(item);
			return item;
		}
		this.allocatedItemList.add(item);
		freeItemList.remove(item);
		return item;
	}

	/**
	 * Free item.
	 * 
	 * @param item the item
	 */
	public synchronized void freeItem(ServiceRegistryItem item) {
		this.allocatedItemList.remove(item);
		if (this.isNoLimit() == false) {
			this.freeItemList.add(item);
		}
	}

	/**
	 * To string.
	 * 
	 * @return the string
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(request.getMessage().toString());
		for (ServiceRegistryItem serviceRegistryItem : allocatedItemList) {
			sb.append(serviceRegistryItem.toString());
		}
		return sb.toString();
	}
	
	/**
	 * Inits the free list.
	 */
	private void initFreeList() {
		if (this.isMultithreaded()) {
			if (this.isNoLimit()) {
				// nothing to do
				return;
			}
			return;
		}
		// init free list is not required for the
		for (int i = 0; i < this.maxSessions; i++) {
		    ServiceRegistryItem item = new ServiceRegistryItem(this.request, this.respContext);
		    item.myParentPool = this;
		    this.freeItemList.add(item);
		}
		return;
	}

}
