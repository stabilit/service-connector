/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 *-----------------------------------------------------------------------------*/
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

	public static List<Thread> simulationThreads;

	public static void main(String[] args) throws IOException {
		run();
	}

	private static void run() throws IOException {
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
}
