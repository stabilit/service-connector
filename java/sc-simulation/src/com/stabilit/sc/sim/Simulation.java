package com.stabilit.sc.sim;

import java.io.IOException;
import java.util.List;

import com.stabilit.sc.cln.config.ClientConfig;
import com.stabilit.sc.sim.cmd.factory.impl.SimulationServerCommandFactory;
import com.stabilit.sc.sim.server.SimluationServerFactory;
import com.stabilit.sc.srv.cmd.factory.CommandFactory;
import com.stabilit.sc.srv.conf.ServerConfig;
import com.stabilit.sc.srv.conf.ServerConfig.ServerConfigItem;
import com.stabilit.sc.srv.ctx.IServerContext;
import com.stabilit.sc.srv.server.IServer;
import com.stabilit.sc.srv.server.ServerFactory;

public class Simulation {

	public static void main(String[] args) throws IOException {

		ServerConfig srvConfig = new ServerConfig();
		srvConfig.load("sc-sim.properties");
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.load("sc-sim.properties");

		CommandFactory commandFactory = CommandFactory.getCurrentCommandFactory();
		if (commandFactory == null) {
			CommandFactory.setCurrentCommandFactory(new SimulationServerCommandFactory());
		}
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

	/**
	 * @throws IOException
	 * 
	 */
	public static Process startup() throws IOException {
		// TODO Auto-generated method stub
		Process process = Runtime.getRuntime().exec("java ch.stabilit.sc.sim.Simluation");
		return process;
	}
}
