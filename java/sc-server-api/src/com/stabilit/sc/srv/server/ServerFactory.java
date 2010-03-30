package com.stabilit.sc.srv.server;

import com.stabilit.sc.common.factory.Factory;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.srv.conf.ServerConfig.ServerConfigItem;

public class ServerFactory extends Factory {

	public ServerFactory() {
	}
	
	public IServer newInstance(ServerConfigItem serverConfig) {
		IFactoryable factoryInstance = this.newInstance();
		IServer server = (IServer)factoryInstance;
		server.setServerConfig(serverConfig);
		return server;
	}
}
