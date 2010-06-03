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
package com.stabilit.scm.srv.registry;

import com.stabilit.scm.registry.Registry;
import com.stabilit.scm.srv.ctx.IServerContext;
import com.stabilit.scm.srv.res.IResponder;
import com.stabilit.scm.util.MapBean;

/**
 * The Class ServerRegistry. Server registry stores every server which completed register process correctly.
 * 
 * @author JTraber
 */
public final class ServerRegistry extends Registry {

	/** The instance. */
	private static ServerRegistry instance = new ServerRegistry();
	/** The thread local. Space to store any data for a single thread. */
	private ThreadLocal<Object> threadLocal;

	/**
	 * Instantiates a new server registry.
	 */
	private ServerRegistry() {
		threadLocal = new ThreadLocal<Object>();
	}

	/**
	 * Sets an object in thread local attached to incoming thread. This object can be used later from the same
	 * thread. Other threads can not access earlier set object.
	 * 
	 * @param obj
	 *            the new thread local
	 */
	public void setThreadLocal(Object obj) {
		this.threadLocal.set(obj);
	}

	/**
	 * Gets the current instance of server registry.
	 * 
	 * @return the current instance
	 */
	public static ServerRegistry getCurrentInstance() {
		return instance;
	}

	/**
	 * Adds an entry of a server.
	 * 
	 * @param key
	 *            the key
	 * @param item
	 *            the item
	 */
	public void add(Object key, ServerRegistryItem item) {
		this.put(key, item);
	}

	/**
	 * Gets the current context.
	 * 
	 * @return the current context
	 */
	public IServerContext getCurrentContext() {
		// gets the key from thread local, key has been set before of the same thread by calling method
		// setThreadLocal(object obj), very useful to identify current context
		Object key = this.threadLocal.get();
		ServerRegistryItem serverRegistryItem = (ServerRegistryItem) this.get(key);
		return serverRegistryItem.getServerContext();
	}

	/**
	 * The Class ServerRegistryItem. Represents an entry in registry of a server. Holds server context and an
	 * attribute map to store any data related to the server.
	 */
	public static class ServerRegistryItem extends MapBean<Object> {

		/**
		 * Instantiates a new server registry item.
		 * 
		 * @param server
		 *            the server
		 */
		public ServerRegistryItem(IResponder server) {
			this.setAttribute(IResponder.class.getName(), server);
		}

		/**
		 * Gets the server context.
		 * 
		 * @return the server context
		 */
		public IServerContext getServerContext() {
			IResponder server = (IResponder) this.getAttribute(IResponder.class.getName());
			if (server == null) {
				return null;
			}
			return server.getServerContext();
		}
	}
}
