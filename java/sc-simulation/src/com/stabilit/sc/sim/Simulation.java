package com.stabilit.sc.sim;

import java.io.IOException;
import java.util.List;

import com.stabilit.sc.cmd.factory.CommandFactory;
import com.stabilit.sc.cmd.factory.impl.SimulationServerCommandFactory;
import com.stabilit.sc.conf.ServerConfig;
import com.stabilit.sc.conf.ServerConfig.ServerConfigItem;
import com.stabilit.sc.config.ClientConfig;
import com.stabilit.sc.ctx.IServerContext;
import com.stabilit.sc.server.IServer;
import com.stabilit.sc.server.ServerFactory;
import com.stabilit.sc.sim.server.SimluationServerFactory;

public class Simulation {

	public static void main(String[] args) throws IOException {

		ServerConfig srvConfig = new ServerConfig();
		srvConfig.load("sc-sim.properties");
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.load("sc-sim.properties");

		CommandFactory.setCurrentCommandFactory(new SimulationServerCommandFactory());

		List<ServerConfigItem> serverConfigList = srvConfig.getServerConfigList();
		ServerFactory serverFactory = new SimluationServerFactory();
		for (ServerConfigItem serverConfigItem : serverConfigList) {
			IServer server = serverFactory.newInstance(serverConfigItem);
			IServerContext serverContext = server.getServerContext();
			serverContext.setAttribute(ClientConfig.class.getName(), clientConfig);
			try {
				server.create();
				server.runAsync();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
