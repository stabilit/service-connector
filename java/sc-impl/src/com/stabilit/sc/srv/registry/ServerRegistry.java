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
package com.stabilit.sc.srv.registry;

import com.stabilit.sc.registry.Registry;
import com.stabilit.sc.srv.ctx.IServerContext;
import com.stabilit.sc.srv.server.IServer;
import com.stabilit.sc.util.MapBean;

/**
 * @author JTraber
 * 
 */
public final class ServerRegistry extends Registry {

	private static ServerRegistry instance = new ServerRegistry();
	private ThreadLocal<Object> threadLocal;

	private ServerRegistry() {
		threadLocal = new ThreadLocal<Object>();
	}
	
	public void setThreadLocal(Object obj) {
        this.threadLocal.set(obj);		
	}

	public static ServerRegistry getCurrentInstance() {
		return instance;
	}

	public void add(Object key, ServerRegistryItem item) {
		this.put(key, item);
	}
	
	public IServerContext getCurrentContext() {
		Object key = this.threadLocal.get();
		ServerRegistryItem serverRegistryItem = (ServerRegistryItem) this.get(key);
		return serverRegistryItem.getServerContext();
	}
		
	public static class ServerRegistryItem extends MapBean<Object> {

		public ServerRegistryItem(IServer server) {
			this.setAttribute(IServer.class.getName(), server);
		}
		
		public IServerContext getServerContext() {
			IServer server = (IServer) this.getAttribute(IServer.class.getName());
			if (server == null) {
				return null;
			}
			return server.getServerContext();
		}
	}
}
