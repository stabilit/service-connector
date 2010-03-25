package com.stabilit.sc;

import java.io.IOException;
import java.util.List;

import com.stabilit.sc.cmd.factory.CommandFactory;
import com.stabilit.sc.cmd.factory.impl.ServiceConnectorCommandFactory;
import com.stabilit.sc.conf.ServerConfig;
import com.stabilit.sc.conf.ServerConfig.ServerConfigItem;
import com.stabilit.sc.server.IServer;
import com.stabilit.sc.server.SCServerFactory;

public final class ServiceConnector {

	private ServiceConnector() {
	}

	public static void main(String[] args) throws IOException {

		ServerConfig config = new ServerConfig();
		config.load("sc.properties");

		CommandFactory.setCurrentCommandFactory(new ServiceConnectorCommandFactory());

		List<ServerConfigItem> serverConfigList = config.getServerConfigList();
		SCServerFactory serverFactory = new SCServerFactory();
		for (ServerConfigItem serverConfig : serverConfigList) {
			IServer server = serverFactory.newInstance(serverConfig);
			try {
				server.create();
				server.runAsync();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
