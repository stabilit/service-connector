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
package org.serviceconnector.test.system.api.cln;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestPublishServiceMessageCallback;
import org.serviceconnector.TestSessionServiceMessageCallback;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.ctrl.util.ServerDefinition;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.system.SystemSuperTest;

public class APISessionSubscriptionTest extends SystemSuperTest {

	protected SCClient client;

	public APISessionSubscriptionTest() {
		APISessionSubscriptionTest.setUpServiceConnectorAndServer();
	}

	@Override
	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_SC0_TCP, ConnectionType.NETTY_TCP);
		client.attach();
	}

	public static void setUpServiceConnectorAndServer() {
		// server definitions
		List<ServerDefinition> srvToSC0Defs = new ArrayList<ServerDefinition>();

		ServerDefinition sessSrvToSC0Def = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_SESSION, TestConstants.logbackSrv, TestConstants.sesServerName1,
				TestConstants.PORT_SES_SRV_TCP, TestConstants.PORT_SC0_TCP, 10, 5, TestConstants.sesServiceName1);
		ServerDefinition pubSrvToSC0Def = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH, TestConstants.logbackSrv, TestConstants.pubServerName1,
				TestConstants.PORT_PUB_SRV_TCP, TestConstants.PORT_SC0_TCP, 100, 10, TestConstants.pubServiceName1);
		srvToSC0Defs.add(pubSrvToSC0Def);
		srvToSC0Defs.add(sessSrvToSC0Def);
		SystemSuperTest.srvDefs = srvToSC0Defs;
	}

	/**
	 * Description: Create session (regular)<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_() throws Exception {
		SCSessionService sessService = client.newSessionService(TestConstants.sesServerName1);
		SCMessage scMessage = new SCMessage();
		sessService.createSession(scMessage, new TestSessionServiceMessageCallback(sessService));
		SCPublishService pubService = client.newPublishService(TestConstants.pubServerName1);
		SCSubscribeMessage scSubscribeMessage = new SCSubscribeMessage();
		scSubscribeMessage.setMask(TestConstants.mask);
		pubService.subscribe(scSubscribeMessage, new TestPublishServiceMessageCallback(pubService));

		SCPublishService pubService1 = client.newPublishService(TestConstants.pubServiceName1);
		scSubscribeMessage.setNoDataIntervalSeconds(40);
		pubService1.subscribe(scSubscribeMessage, new TestPublishServiceMessageCallback(pubService1));

		System.out.println("APISessionSubscriptionTest.t01_()");

		try {
			sessService.deleteSession();
			pubService.unsubscribe();
		} catch (Exception e) {
			client.detach();
		}
		client.detach();
	}

}
