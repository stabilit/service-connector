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

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.srv.ctx.IServerContext;
import com.stabilit.sc.srv.registry.ServerRegistry;
import com.stabilit.sc.util.MapBean;

/**
 * The Class ServiceRegistryItemPool. Pools incoming request for one service. It depends on service resource and
 * type how pooling is managed.
 * 
 * @author JTraber
 */
public class ServiceRegistryItemPool extends MapBean<String> {

	/** The max items. */
	private int maxItems = -1;
	/** The scmp. */
	private SCMPMessage scmp;
	/** The socket address. */
	private SocketAddress socketAddress;
	/** The server context. */
	private IServerContext serverContext;
	/** The free item list. */
	private List<ServiceRegistryItem> freeItemList;
	/** The allocated item list. */
	private List<ServiceRegistryItem> allocatedItemList;

	/**
	 * Instantiates a new service registry item pool.
	 * 
	 * @param scmp
	 *            the scmp
	 * @param socketAddress
	 *            the socket address
	 */
	public ServiceRegistryItemPool(SCMPMessage scmp, SocketAddress socketAddress) {
		this.scmp = scmp;
		this.socketAddress = socketAddress;
		this.freeItemList = Collections.synchronizedList(new ArrayList<ServiceRegistryItem>());
		this.allocatedItemList = Collections.synchronizedList(new ArrayList<ServiceRegistryItem>());
		ServerRegistry serverRegistry = ServerRegistry.getCurrentInstance();
		this.serverContext = serverRegistry.getCurrentContext();
	}

	/**
	 * Gets the server context.
	 * 
	 * @return the server context
	 */
	public IServerContext getServerContext() {
		return this.serverContext;
	}

	/**
	 * Gets the scmp.
	 * 
	 * @return the scmp
	 */
	public SCMPMessage getScmp() {
		return scmp;
	}

	/**
	 * Gets the socket address.
	 * 
	 * @return the socket address
	 */
	public SocketAddress getSocketAddress() {
		return socketAddress;
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
		return this.freeItemList.isEmpty() == false;
	}

	/**
	 * Checks if is no limit.
	 * 
	 * @return true, if is no limit
	 */
	public synchronized boolean isNoLimit() {
		return this.maxItems < 0;
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
			ServiceRegistryItem item = new ServiceRegistryItem(this.scmp, this.socketAddress, this.serverContext);
			item.myItemPool = this;
			this.allocatedItemList.add(item);
			return item;
		}
		ServiceRegistryItem item = freeItemList.get(0);
		this.allocatedItemList.add(item);
		freeItemList.remove(item);
		return item;
	}

	/**
	 * Free item.
	 * 
	 * @param item
	 *            the item
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
		sb.append(scmp.toString());
		for (ServiceRegistryItem serviceRegistryItem : allocatedItemList) {
			sb.append(serviceRegistryItem.toString());
		}
		return sb.toString();
	}
}
