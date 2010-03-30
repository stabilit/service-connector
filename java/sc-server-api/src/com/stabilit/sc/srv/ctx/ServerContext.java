package com.stabilit.sc.ctx;

import com.stabilit.sc.common.ctx.ContextAdapter;
import com.stabilit.sc.registry.ServerRegistry;
import com.stabilit.sc.server.IServer;


public class ServerContext extends ContextAdapter implements IServerContext {
		
	private IServer server;
	
	public ServerContext(IServer server) {
		this.server = server;
	}

	public IServer getServer() {
		return server;
	}

	public static IServerContext getCurrentInstance() {
		ServerRegistry serverRegistry = ServerRegistry.getCurrentInstance();
		return (IServerContext) serverRegistry.getCurrentContext();
	}
}
