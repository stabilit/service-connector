package com.stabilit.sc.sim.server;

import java.io.IOException;
import java.util.List;

import com.stabilit.sc.cmd.factory.CommandFactory;
import com.stabilit.sc.cmd.factory.impl.ServerApiCommandFactory;
import com.stabilit.sc.conf.ServerConfig;
import com.stabilit.sc.server.IServer;
import com.stabilit.sc.server.ServerFactory;
import com.stabilit.sc.sim.config.ConfigForServers;

public class ServerMain {

	public static void main(String[] args) throws IOException {

		ConfigForServers config = new ConfigForServers();
		config.load("sc-server-api.properties");
		
		CommandFactory.setCurrentCommandFactory(new ServerApiCommandFactory());
		
		List<ServerConfig> serverConfigList = config.getServerConfigList();
		ServerFactory serverFactory = new ServerFactory();
		for (ServerConfig serverConfig : serverConfigList) {
			IServer server = serverFactory.newInstance(serverConfig);
			server.create();
			server.runAsync();
		}
	}

}
