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
package com.stabilit.scm.sim;

import java.io.IOException;
import java.util.List;

import com.stabilit.scm.common.conf.RequeserConfig;
import com.stabilit.scm.common.conf.ResponderConfig;
import com.stabilit.scm.common.conf.ResponderConfig.ResponderConfigItem;
import com.stabilit.scm.common.net.res.ResponderFactory;
import com.stabilit.scm.sim.cmd.factory.impl.SimulationServerCommandFactory;
import com.stabilit.scm.sim.server.SimluationServerFactory;
import com.stabilit.scm.srv.cmd.factory.CommandFactory;
import com.stabilit.scm.srv.config.IResponderConfigItem;
import com.stabilit.scm.srv.ctx.IResponderContext;
import com.stabilit.scm.srv.res.IResponder;

public class Simulation {

	public static void main(String[] args) throws IOException {
		run();
	}

	private static void run() throws IOException {
		ResponderConfig srvConfig = new ResponderConfig();
		srvConfig.load("sc-sim.properties");
		RequeserConfig clientConfig = new RequeserConfig();
		clientConfig.load("sc-sim.properties");

		CommandFactory commandFactory = CommandFactory.getCurrentCommandFactory();
		if (commandFactory == null) {
			CommandFactory.setCurrentCommandFactory(new SimulationServerCommandFactory());
		}
		List<ResponderConfigItem> serverConfigList = srvConfig.getResponderConfigList();
		ResponderFactory serverFactory = new SimluationServerFactory();
		for (IResponderConfigItem serverConfigItem : serverConfigList) {
			IResponder server = serverFactory.newInstance(serverConfigItem);
			IResponderContext serverContext = server.getResponderContext();
			serverContext.setAttribute(RequeserConfig.class.getName(), clientConfig);
			try {
				server.create();
				server.runAsync();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
