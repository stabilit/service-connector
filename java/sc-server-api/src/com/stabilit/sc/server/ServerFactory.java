package com.stabilit.sc.server;

import com.stabilit.sc.conf.ServerConfig.ServerConfigItem;
import com.stabilit.sc.factory.Factory;
import com.stabilit.sc.factory.IFactoryable;

public class ServerFactory extends Factory {

	public ServerFactory() {
	    Server server = new Server();
	    this.factoryMap.put("default", server);
	}
	
	public IServer newInstance(ServerConfigItem serverConfig) {
		IFactoryable factoryInstance = this.newInstance();
		IServer server = (IServer)factoryInstance;
		server.setServerConfig(serverConfig);
		return server;
	}
}
