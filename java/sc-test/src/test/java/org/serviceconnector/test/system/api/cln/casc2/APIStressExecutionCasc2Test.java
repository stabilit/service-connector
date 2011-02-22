/*
 * Copyright © 2010 STABILIT Informatik AG, Switzerland *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License"); *
 * you may not use this file except in compliance with the License. *
 * You may obtain a copy of the License at *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0 *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS, *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and *
 * limitations under the License. *
 */
package org.serviceconnector.test.system.api.cln.casc2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ServerDefinition;
import org.serviceconnector.ctrl.util.ServiceConnectorDefinition;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.system.SystemSuperTest;

public class APIStressExecutionCasc2Test extends SystemSuperTest {

	protected Map<String, ProcessCtx> srvCtxs;
	protected static List<ServerDefinition> srvDefs;
	
	public APIStressExecutionCasc2Test() {
		APIStressExecutionCasc2Test.setUp1CascadedServiceConnectorAndServer();
	}

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		srvCtxs = ctrl.startServerEnvironment(srvDefs);
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			ctrl.stopServerEnvironment(srvCtxs);
		} catch (Exception e) {
		}
		srvCtxs = null;
		super.afterOneTest();
	}

	public static void setUpServiceConnectorAndServer() {
		// SC definitions
		List<ServiceConnectorDefinition> sc0Defs = new ArrayList<ServiceConnectorDefinition>();
		ServiceConnectorDefinition sc0Def = new ServiceConnectorDefinition(TestConstants.SC0, TestConstants.SC0Properties,
				TestConstants.log4jSC0Properties);
		sc0Defs.add(sc0Def);

		// server definitions
		List<ServerDefinition> srvToSC0Defs = new ArrayList<ServerDefinition>();
		ServerDefinition srv1ToSC0Def = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_SESSION,
				TestConstants.log4jSrvProperties, TestConstants.sesServerName1, TestConstants.PORT_SES_SRV_TCP,
				TestConstants.PORT_SC_TCP, 10, 5, TestConstants.sesServiceName1);
		srvToSC0Defs.add(srv1ToSC0Def);

		SystemSuperTest.scDefs = sc0Defs;
		APIStressExecutionCasc2Test.srvDefs = srvToSC0Defs;
	}

	public static void setUp1CascadedServiceConnectorAndServer() {
		List<ServiceConnectorDefinition> scCascDefs = new ArrayList<ServiceConnectorDefinition>();
		ServiceConnectorDefinition sc0CascDef = new ServiceConnectorDefinition(TestConstants.SC0_CASC,
				TestConstants.SC0CASCProperties, TestConstants.log4jSC0CASCProperties);
		ServiceConnectorDefinition sc1CascDef = new ServiceConnectorDefinition(TestConstants.SC1_CASC,
				TestConstants.SC1CASC1Properties, TestConstants.log4jSC1CASCProperties);
		scCascDefs.add(sc0CascDef);
		scCascDefs.add(sc1CascDef);

		// server definitions
		List<ServerDefinition> srvToSC0CascDefs = new ArrayList<ServerDefinition>();
		ServerDefinition srv1ToSC0CascDef = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH,
				TestConstants.log4jSrvProperties, TestConstants.pubServerName1, TestConstants.PORT_PUB_SRV_TCP,
				TestConstants.PORT_SC0_CASC_TCP, 10, 5, TestConstants.pubServerName1);
		srvToSC0CascDefs.add(srv1ToSC0CascDef);

		SystemSuperTest.scDefs = scCascDefs;
		APIStressExecutionCasc2Test.srvDefs = srvToSC0CascDefs;
	}

	/**
	 * Description: Create session (regular)<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_2Clients10000Messages() throws Exception {
		int numberOfClients = 2;
		ProcessCtx[] clientCtxs = new ProcessCtx[numberOfClients];

		for (int i = 0; i < clientCtxs.length; i++) {
			ProcessCtx clientCtx = ctrl.startSessionClient(TestConstants.log4jClnProperties, "client" + i, TestConstants.HOST,
					TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP, 10, 0, TestConstants.sesServerName1, 50, 60,
					"f_execute1000MessagesAndExit");
			clientCtxs[i] = clientCtx;
		}
		APIStressExecutionCasc2Test.ctrl.waitForClientTermination(clientCtxs);
		TestUtil.checkLogFile(TestConstants.log4jClnProperties, "message.log");
		TestUtil.checkLogFile(TestConstants.log4jClnProperties, "client.log");
	}

	/**
	 * Description: Create session (regular)<br>
	 * Expectation: passes
	 */
	@Test
	public void t05_10Clients100000Messages() throws Exception {
		int numberOfClients = 10;
		ProcessCtx[] clientCtxs = new ProcessCtx[numberOfClients];

		for (int i = 0; i < clientCtxs.length; i++) {
			ProcessCtx clientCtx = ctrl.startSessionClient(TestConstants.log4jClnProperties, "client" + i, TestConstants.HOST,
					TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP, 10, 0, TestConstants.sesServerName1, 10, 60,
					"f_execute100000MessagesAndExit");
			clientCtxs[i] = clientCtx;
		}
		APIStressExecutionCasc2Test.ctrl.waitForClientTermination(clientCtxs);
		TestUtil.checkLogFile(TestConstants.log4jClnProperties, "message.log");
		TestUtil.checkLogFile(TestConstants.log4jClnProperties, "client.log");
	}
}