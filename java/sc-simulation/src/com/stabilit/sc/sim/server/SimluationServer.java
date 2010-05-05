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
package com.stabilit.sc.sim.server;

import com.stabilit.sc.cln.client.ClientFactory;
import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.cln.config.ClientConfig;
import com.stabilit.sc.cln.service.SCMPCallFactory;
import com.stabilit.sc.cln.service.SCMPRegisterServiceCall;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.srv.config.IServerConfigItem;
import com.stabilit.sc.srv.server.Server;

/**
 * @author JTraber
 * 
 */
public class SimluationServer extends Server {
	private ClientFactory clientFactory;
	private IClient client;

	public SimluationServer() {
		clientFactory = new ClientFactory();
		client = null;
	}

	@Override
	public void create() throws Exception {
		super.create();
		ClientConfig clientConfig = (ClientConfig) this.getServerContext().getAttribute(ClientConfig.class.getName());
		IServerConfigItem serverConfigItem = (IServerConfigItem) this.getServerContext().getServer().getServerConfig();
		client = clientFactory.newInstance(clientConfig.getClientConfig());
		client.connect(); // physical connect
		// scmp registerService		
		SCMPRegisterServiceCall registerService = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL.newInstance(client);
		registerService.setServiceName("simulation");
		registerService.setMaxSessions(1);
		registerService.setPortNumber(serverConfigItem.getPort());
		registerService.invoke();
	}

	@Override
	public IFactoryable newInstance() {
		return new SimluationServer();
	}
}
