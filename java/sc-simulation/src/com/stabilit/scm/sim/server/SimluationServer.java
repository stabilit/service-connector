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
package com.stabilit.scm.sim.server;

import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPRegisterServiceCall;
import com.stabilit.scm.common.conf.RequeserConfig;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.RequesterFactory;
import com.stabilit.scm.common.net.res.Responder;
import com.stabilit.scm.factory.IFactoryable;
import com.stabilit.scm.srv.config.IResponderConfigItem;

/**
 * @author JTraber
 * 
 */
public class SimluationServer extends Responder {
	private RequesterFactory clientFactory;
	private IRequester client;

	public SimluationServer() {
		clientFactory = new RequesterFactory();
		client = null;
	}

	@Override
	public void create() throws Exception {
		super.create();
		RequeserConfig clientConfig = (RequeserConfig) this.getResponderContext().getAttribute(RequeserConfig.class.getName());
		IResponderConfigItem serverConfigItem = (IResponderConfigItem) this.getResponderContext().getResponder().getResponderConfig();
		client = clientFactory.newInstance(clientConfig.getClientConfig());
		client.connect(); // physical connect
		// scmp registerService		
		SCMPRegisterServiceCall registerService = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL.newInstance(client);
		registerService.setServiceName("simulation");
		registerService.setMaxSessions(1);
		registerService.setMultithreaded(true);
		registerService.setPortNumber(serverConfigItem.getPort());
		registerService.invoke();
	}

	@Override
	public IFactoryable newInstance() {
		return new SimluationServer();
	}
}
