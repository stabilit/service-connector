package com.stabilit.sc.srv.ctx;

import com.stabilit.sc.ctx.ContextAdapter;
import com.stabilit.sc.srv.registry.ServerRegistry;
import com.stabilit.sc.srv.server.IServer;


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
