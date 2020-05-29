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
import org.serviceconnector.ctrl.util.ServiceConnectorDefinition;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.system.SystemSuperTest;

public class APIMultipleClientSubscribeCasc1Test extends SystemSuperTest {

	public APIMultipleClientSubscribeCasc1Test() {
		APIMultipleClientSubscribeCasc1Test.setUp1CascadedServiceConnectorAndServer();
	}

	@Override
	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		TestUtil.deleteLogDir(TestConstants.logbackCln);
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

	public static void setUpServer() {
		// need two publish server here
		List<ServerDefinition> srvToSC0CascDefs = new ArrayList<ServerDefinition>();
		ServerDefinition srv1ToSC0CascDef = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH, TestConstants.logbackSrv, TestConstants.pubServerName1,
				TestConstants.PORT_PUB_SRV_TCP, TestConstants.PORT_SC1_TCP, 10, 5, TestConstants.pubServerName1);
		ServerDefinition srv2ToSC0CascDef = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH, TestConstants.logbackSrv, TestConstants.pubServiceName2, 30002,
				TestConstants.PORT_SC1_TCP, 10, 5, TestConstants.pubServiceName2);
		srvToSC0CascDefs.add(srv1ToSC0CascDef);
		srvToSC0CascDefs.add(srv2ToSC0CascDef);
		SystemSuperTest.srvDefs = srvToSC0CascDefs;
	}

	/**
	 * Description: 2 clients Subscribe, receive 10000 message and unsubscribe<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_2ClientsReceiving10000Messages() throws Exception {
		int numberOfClients = 2;
		ProcessCtx[] clientCtxs = new ProcessCtx[numberOfClients];

		for (int i = 0; i < clientCtxs.length; i++) {
			ProcessCtx clientCtx = ctrl.startPublishClient(TestConstants.logbackCln, "client" + i, TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP,
					10, 0, TestConstants.pubServerName1, 50, "f_subscribeReceive10000Unsubscribe");
			clientCtxs[i] = clientCtx;
		}
		SystemSuperTest.ctrl.waitForClientTermination(clientCtxs);
		// dont't check message.log might be an EXC because of broken CRP
		TestUtil.checkLogFile(TestConstants.logbackCln, "sc.log");
	}

	/**
	 * Description: 2 clients Subscribe, receive 500/10000 message and unsubscribe, different services<br>
	 * Expectation: passes
	 */
	@Test
	public void t02_2ClientsReceiving500_10000MessagesDiffService() throws Exception {

		ProcessCtx clientCtx = ctrl.startPublishClient(TestConstants.logbackCln, "client1", TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP, 10, 0,
				TestConstants.pubServerName1, 50, "f_subscribeReceive500Unsubscribe");

		ProcessCtx clientCtx2 = ctrl.startPublishClient(TestConstants.logbackCln, "client2", TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP, 10,
				0, TestConstants.pubServiceName2, 50, "f_subscribeReceive10000Unsubscribe");

		SystemSuperTest.ctrl.waitForClientTermination(clientCtx);
		SystemSuperTest.ctrl.waitForClientTermination(clientCtx2);
		// dont't check message.log might be an EXC because of broken CRP
		TestUtil.checkLogFile(TestConstants.logbackCln, "sc.log");
	}

	/**
	 * Description: 2 clients Subscribe, receive 20 message and unsubscribe, same service different noDataInterval which is smaller than message sending delay on server<br>
	 * Expectation: passes
	 */
	@Test
	public void t05_2ClientsReceiving20MessagesDelayed() throws Exception {
		int noDataInterval1 = 10;
		int noDataInterval2 = 11;

		ProcessCtx clientCtx = ctrl.startPublishClient(TestConstants.logbackCln, "client1", TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP, 10, 0,
				TestConstants.pubServerName1, noDataInterval1, "f_subscribeReceive20_12SecUnsubscribe");

		ProcessCtx clientCtx2 = ctrl.startPublishClient(TestConstants.logbackCln, "client2", TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP, 10,
				0, TestConstants.pubServiceName1, noDataInterval2, "f_subscribeReceive20_12SecUnsubscribe");

		SystemSuperTest.ctrl.waitForClientTermination(clientCtx);
		SystemSuperTest.ctrl.waitForClientTermination(clientCtx2);
		// dont't check message.log might be an EXC because of broken CRP
		TestUtil.checkLogFile(TestConstants.logbackCln, "sc.log");
	}

	/**
	 * Description: 10 clients Subscribe, receive 10000 message and unsubscribe<br>
	 * Expectation: passes
	 */
	@Test
	public void t10_10ClientsReceiving10000Messages() throws Exception {
		int numberOfClients = 10;
		ProcessCtx[] clientCtxs = new ProcessCtx[numberOfClients];

		for (int i = 0; i < clientCtxs.length - 1; i++) {
			ProcessCtx clientCtx = ctrl.startPublishClient(TestConstants.logbackCln, "client" + i, TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP,
					10, 0, TestConstants.pubServerName1, 50, "f_subscribeUnsubscribe");
			clientCtxs[i] = clientCtx;
		}
		ProcessCtx clientCtx10 = ctrl.startPublishClient(TestConstants.logbackCln, "client10", TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP, 10,
				0, TestConstants.pubServerName1, 50, "f_subscribeReceive10000Unsubscribe");
		clientCtxs[9] = clientCtx10;
		SystemSuperTest.ctrl.waitForClientTermination(clientCtxs);
		// dont't check message.log might be an EXC because of broken CRP
		TestUtil.checkLogFile(TestConstants.logbackCln, "sc.log");
	}

	/**
	 * Description: 3 clients Subscribe, 1 receives 10000, 2 receives 500 message and unsubscribe<br>
	 * Expectation: passes
	 */
	@Test
	public void t11_3ClientsReceivingMessages() throws Exception {
		int numberOfClients = 2;
		ProcessCtx[] clientCtxs = new ProcessCtx[numberOfClients];

		ProcessCtx clientCtx3 = ctrl.startPublishClient(TestConstants.logbackCln, "client0", TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP, 10,
				0, TestConstants.pubServerName1, 50, "f_subscribeReceive10000Unsubscribe");
		clientCtxs[0] = clientCtx3;
		for (int i = 1; i < clientCtxs.length; i++) {
			ProcessCtx clientCtx = ctrl.startPublishClient(TestConstants.logbackCln, "client" + i, TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP,
					10, 0, TestConstants.pubServerName1, 50, "f_subscribeReceive500Unsubscribe");
			clientCtxs[i] = clientCtx;
		}
		SystemSuperTest.ctrl.waitForClientTermination(clientCtxs);
		// dont't check message.log might be an EXC because of broken CRP
		TestUtil.checkLogFile(TestConstants.logbackCln, "sc.log");
	}

	/**
	 * Description: 15clients Subscribe, receives 500 message and unsubscribe, stop sc0 and server, start and try to reestablish<br>
	 * Expectation: passes
	 */
	@Test
	public void t12_15ClientsReceivingMessagesBetweenRebootOfSC() throws Exception {
		int numberOfClients = 3;
		ProcessCtx[] clientCtxs = new ProcessCtx[numberOfClients];

		ProcessCtx clientCtx3 = ctrl.startPublishClient(TestConstants.logbackCln, "client0", TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP, 10,
				0, TestConstants.pubServerName1, 50, "f_subscribeReceive500Unsubscribe");
		clientCtxs[0] = clientCtx3;
		for (int i = 1; i < clientCtxs.length; i++) {
			ProcessCtx clientCtx = ctrl.startPublishClient(TestConstants.logbackCln, "client" + i, TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP,
					10, 0, TestConstants.pubServerName1, 50, "f_subscribeReceive500Unsubscribe");
			clientCtxs[i] = clientCtx;
		}
		// dont't check message.log might be an EXC because of broken CRP
		TestUtil.checkLogFile(TestConstants.logbackCln, "sc.log");
		// dont't check message.log might be an EXC because of broken CRP
		TestUtil.checkLogFile(TestConstants.logbackSC0, "sc.log");

		ProcessCtx sc0 = SystemSuperTest.scCtxs.remove("sc0");
		SystemSuperTest.ctrl.stopSC(sc0);
		SystemSuperTest.ctrl.stopServerEnvironment(SystemSuperTest.srvCtxs);

		numberOfClients = 3;
		for (int i = 0; i < clientCtxs.length; i++) {
			ProcessCtx clientCtx = ctrl.startPublishClient(TestConstants.logbackCln, "client" + i, TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP,
					10, 0, TestConstants.pubServerName1, 50, "f_subscribeReceive500Unsubscribe");
			clientCtxs[i] = clientCtx;
		}

		List<ServiceConnectorDefinition> sc0Defs = new ArrayList<ServiceConnectorDefinition>();
		ServiceConnectorDefinition sc0Def = new ServiceConnectorDefinition(TestConstants.SC0, TestConstants.SC0Properties, TestConstants.logbackSC0);
		sc0Defs.add(sc0Def);

		SystemSuperTest.scCtxs.putAll(SystemSuperTest.ctrl.startSCEnvironment(sc0Defs));
		APIMultipleClientChangeSubscriptionCasc1Test.setUpServer();
		srvCtxs = ctrl.startServerEnvironment(srvDefs);

		for (int i = 0; i < clientCtxs.length; i++) {
			ProcessCtx clientCtx = ctrl.startPublishClient(TestConstants.logbackCln, "client" + i, TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP,
					10, 0, TestConstants.pubServerName1, 50, "f_subscribeReceive500Unsubscribe");
			clientCtxs[i] = clientCtx;
		}
		SystemSuperTest.ctrl.waitForClientTermination(clientCtxs);
	}
}
