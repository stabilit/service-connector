package com.stabilit.sc.ctx;

import com.stabilit.sc.server.IServer;


public class ServerContext extends ContextAdapter implements IServerContext {
		
	private IServer server;
	
	public ServerContext(IServer server) {
		this.server = server;
	}

	public IServer getServer() {
		return server;
	}
}
