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
package org.serviceconnector.test.system.api.cln.casc1;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ServerDefinition;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.system.SystemSuperTest;

public class APIMultipleClientChangeSubscriptionCasc1Test extends SystemSuperTest {

	public APIMultipleClientChangeSubscriptionCasc1Test() {
		APIMultipleClientChangeSubscriptionCasc1Test.setUp1CascadedServiceConnectorAndServer();
	}

	public static void setUpServiceConnectorAndServer() {
		SystemSuperTest.setUpServiceConnectorAndServer();
		APIMultipleClientChangeSubscriptionCasc1Test.setUpServer();
	}

	public static void setUp1CascadedServiceConnectorAndServer() {
		SystemSuperTest.setUp1CascadedServiceConnectorAndServer();
		APIMultipleClientChangeSubscriptionCasc1Test.setUpServer();
	}

	public static void setUp2CascadedServiceConnectorAndServer() {
		SystemSuperTest.setUp2CascadedServiceConnectorAndServer();
		APIMultipleClientChangeSubscriptionCasc1Test.setUpServer();
	}

	@Override
	@Before
	public void beforeOneTest() throws Exception {
		TestUtil.deleteLogDir(TestConstants.logbackCln);
		super.beforeOneTest();
		TestUtil.deleteLogDir(TestConstants.logbackCln);
	}

	public static void setUpServer() {
		// need two publish server here
		List<ServerDefinition> srvToSC0CascDefs = new ArrayList<ServerDefinition>();
		ServerDefinition srv1ToSC0CascDef = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH, TestConstants.logbackSrv, TestConstants.pubServerName1,
				TestConstants.PORT_PUB_SRV_TCP, TestConstants.PORT_SC0_TCP, 10, 5, TestConstants.pubServerName1);
		ServerDefinition srv2ToSC0CascDef = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH, TestConstants.logbackSrv, TestConstants.pubServiceName2, 30002,
				TestConstants.PORT_SC0_TCP, 10, 5, TestConstants.pubServiceName2);
		srvToSC0CascDefs.add(srv1ToSC0CascDef);
		srvToSC0CascDefs.add(srv2ToSC0CascDef);
		SystemSuperTest.srvDefs = srvToSC0CascDefs;
	}

	/**
	 * Description: 3 clients Subscribe, 1 receives 10000, 2 receives 500 message and unsubscribe<br>
	 * Expectation: passes
	 */
	@Test
	public void t11_3ClientsReceivingMessages() throws Exception {
		int numberOfClients = 3;
		ProcessCtx[] clientCtxs = new ProcessCtx[numberOfClients];

		ProcessCtx clientCtx3 = ctrl.startPublishClient(TestConstants.logbackCln, "client0", TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP, 10,
				0, TestConstants.pubServerName1, 50, "f_subscribeReceive10000Unsubscribe");
		clientCtxs[0] = clientCtx3;
		for (int i = 1; i < clientCtxs.length; i++) {
			ProcessCtx clientCtx = ctrl.startPublishClient(TestConstants.logbackCln, "client" + i, TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP,
					10, 0, TestConstants.pubServerName1, 50, "f_subscribeReceive500ChangeSubscriptionUnsubscribe");
			clientCtxs[i] = clientCtx;
		}
		SystemSuperTest.ctrl.waitForClientTermination(clientCtxs);
		// dont't check message.log might be an EXC because of broken CRP
		TestUtil.checkLogFile(TestConstants.logbackCln, "sc.log");
	}

	/**
	 * Description: 3 clients changeSubscription 10000<br>
	 * Expectation: passes
	 */
	@Test
	public void t15_3ClientsChangeSubscription10000() throws Exception {
		int numberOfClients = 10;
		ProcessCtx[] clientCtxs = new ProcessCtx[numberOfClients];

		ProcessCtx clientCtx3 = ctrl.startPublishClient(TestConstants.logbackCln, "client0", TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP, 10,
				0, TestConstants.pubServerName1, 50, "f_subscribeReceive10000Unsubscribe");
		clientCtxs[0] = clientCtx3;
		for (int i = 1; i < clientCtxs.length; i++) {
			ProcessCtx clientCtx = ctrl.startPublishClient(TestConstants.logbackCln, "client" + i, TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP,
					10, 0, TestConstants.pubServerName1, 50, "f_10000ChangeSubscription");
			clientCtxs[i] = clientCtx;
		}
		SystemSuperTest.ctrl.waitForClientTermination(clientCtxs);
		// dont't check message.log might be an EXC because of broken CRP
		TestUtil.checkLogFile(TestConstants.logbackCln, "sc.log");
	}

}
