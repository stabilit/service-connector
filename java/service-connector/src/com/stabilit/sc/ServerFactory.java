package com.stabilit.sc;

import com.stabilit.sc.conf.ServerConfig;
import com.stabilit.sc.factory.Factory;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.server.IServer;
import com.stabilit.sc.server.Server;

public class ServerFactory extends Factory {

	/**
	 * 
	 */
	public ServerFactory() {
	    Server server = new Server();
	    this.factoryMap.put("default", server);
	}
	
	public IServer newInstance(ServerConfig serverConfig) {
		IFactoryable factoryInstance = this.newInstance();
		IServer server = (IServer)factoryInstance;
		server.setServerConfig(serverConfig);
		return server;
	}
}
