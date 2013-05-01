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
package org.serviceconnector.test.system.scmp.casc2;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.serviceconnector.TestConstants;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.ctrl.util.ServerDefinition;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.test.system.SystemSuperTest;
import org.serviceconnector.test.system.scmp.casc1.SCMPClnCreateSessionCasc1Test;

/**
 * The Class ClnCreateSessionTestCase.
 */
public class SCMPClnCreateSessionCasc2Test extends SCMPClnCreateSessionCasc1Test {

	public SCMPClnCreateSessionCasc2Test() {
		SCMPClnCreateSessionCasc2Test.setUp2CascadedServiceConnectorAndServer();
		// server definitions
		List<ServerDefinition> srvToSC0Defs = new ArrayList<ServerDefinition>();
		ServerDefinition srvToSC0Def = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_SESSION,
				TestConstants.log4jSrvProperties, TestConstants.sesServerName1, TestConstants.PORT_SES_SRV_TCP,
				TestConstants.PORT_SC0_TCP, 3, 2, TestConstants.sesServiceName1);
		srvToSC0Defs.add(srvToSC0Def);
		SystemSuperTest.srvDefs = srvToSC0Defs;
	}

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		this.requester = new SCRequester(new RemoteNodeConfiguration(TestConstants.RemoteNodeName, TestConstants.HOST,
				TestConstants.PORT_SC2_HTTP, ConnectionType.NETTY_HTTP.getValue(), 0, 0, 3), 0);
		AppContext.init();
	}
}