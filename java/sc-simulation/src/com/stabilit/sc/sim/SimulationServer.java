package com.stabilit.sc.sim;

import java.io.IOException;
import java.util.List;

import com.stabilit.sc.cmd.factory.CommandFactory;
import com.stabilit.sc.cmd.factory.impl.SimulationServerCommandFactory;
import com.stabilit.sc.conf.ServerConfig;
import com.stabilit.sc.conf.ServerConfig.ServerConfigItem;
import com.stabilit.sc.server.IServer;
import com.stabilit.sc.server.ServerFactory;

public class SimulationServer {

	public static void main(String[] args) throws IOException {

		ServerConfig config = new ServerConfig();
		config.load("sc-sim.properties");
		
		CommandFactory.setCurrentCommandFactory(new SimulationServerCommandFactory());
		
		List<ServerConfigItem> serverConfigList = config.getServerConfigList();
		ServerFactory serverFactory = new ServerFactory();
		for (ServerConfigItem serverConfigItem : serverConfigList) {
			IServer server = serverFactory.newInstance(serverConfigItem);
			server.create();
			server.runAsync();
		}
	}

}
