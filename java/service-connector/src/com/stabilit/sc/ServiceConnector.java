package com.stabilit.sc;

import java.io.IOException;
import java.util.List;

import com.stabilit.sc.cmd.factory.CommandFactory;
import com.stabilit.sc.cmd.factory.impl.ServiceConnectorCommandFactory;
import com.stabilit.sc.conf.ServerConfig;
import com.stabilit.sc.conf.ServerConfig.ServerConfigItem;
import com.stabilit.sc.server.IServer;
import com.stabilit.sc.server.ServerFactory;

public class ServiceConnector {

	public static void main(String[] args) throws IOException {

		ServerConfig config = new ServerConfig();
		config.load("sc.properties");
		
		CommandFactory.setCurrentCommandFactory(new ServiceConnectorCommandFactory());
		
		List<ServerConfigItem> serverConfigList = config.getServerConfigList();
		ServerFactory serverFactory = new ServerFactory();
		for (ServerConfigItem serverConfig : serverConfigList) {
			IServer server = serverFactory.newInstance(serverConfig);
			server.create();
			server.runAsync();
		}
	}
}
