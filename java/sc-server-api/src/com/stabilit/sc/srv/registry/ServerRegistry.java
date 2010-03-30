package com.stabilit.sc.srv.registry;

import com.stabilit.sc.common.registry.Registry;
import com.stabilit.sc.common.util.MapBean;
import com.stabilit.sc.srv.ctx.IServerContext;
import com.stabilit.sc.srv.server.IServer;

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
