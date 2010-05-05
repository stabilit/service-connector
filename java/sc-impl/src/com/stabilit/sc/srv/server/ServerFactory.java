package com.stabilit.sc.srv.server;

import com.stabilit.sc.factory.Factory;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.srv.config.IServerConfigItem;

public class ServerFactory extends Factory {

	public ServerFactory() {
	}
	
	public IServer newInstance(IServerConfigItem serverConfig) {
		IFactoryable factoryInstance = this.newInstance();
		IServer server = (IServer)factoryInstance;
		server.setServerConfig(serverConfig);
		return server;
	}
}
