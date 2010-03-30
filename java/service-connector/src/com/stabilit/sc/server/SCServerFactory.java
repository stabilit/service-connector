package com.stabilit.sc.server;

import com.stabilit.sc.common.factory.Factory;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.srv.conf.ServerConfig.ServerConfigItem;
import com.stabilit.sc.srv.server.IServer;
import com.stabilit.sc.srv.server.Server;

public class SCServerFactory extends Factory {

	public SCServerFactory() {
	    Server server = new SCServer();
	    this.factoryMap.put("default", server);
	}
	
	public IServer newInstance(ServerConfigItem serverConfig) {
		IFactoryable factoryInstance = this.newInstance();
		IServer server = (IServer)factoryInstance;
		server.setServerConfig(serverConfig);
		return server;
	}
}
