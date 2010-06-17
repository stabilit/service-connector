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
package com.stabilit.scm.srv;

import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPRegisterServiceCall;
import com.stabilit.scm.common.conf.IResponderConfigItem;
import com.stabilit.scm.common.conf.RequesterConfig;
import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.net.res.Responder;

/**
 * @author JTraber
 */
public class SessionServerResponder extends Responder {

	private IRequester req;

	public SessionServerResponder() {
		req = null;
	}

	@Override
	public void runAsync() throws Exception {
		super.runAsync();
		this.makeRegisterService();
	}

	@Override
	public void runSync() throws Exception {
		super.runSync();
		this.makeRegisterService();
	}

	private void makeRegisterService() throws Exception {
		// needs to register service in SC
		RequesterConfig clientConfig = (RequesterConfig) this.getResponderContext().getAttribute(
				RequesterConfig.class.getName());
		IResponderConfigItem serverConfigItem = (IResponderConfigItem) this.getResponderContext().getResponder()
				.getResponderConfig();
		req = new Requester();
		req.setRequesterConfig(clientConfig.getRequesterConfig());
		req.connect(); // physical connect
		// scmp registerService
		SCMPRegisterServiceCall registerService = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(req, "simulation");
		registerService.setMaxSessions(1);
		registerService.setPortNumber(serverConfigItem.getPort());
		registerService.setImmediateConnect(true);
		registerService.setKeepAliveTimeout(30);
		registerService.setKeepAliveInterval(360);
		registerService.invoke();
	}

	@Override
	public IFactoryable newInstance() {
		return new SessionServerResponder();
	}
}
