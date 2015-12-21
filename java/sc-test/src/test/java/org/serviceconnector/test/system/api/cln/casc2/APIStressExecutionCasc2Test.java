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

import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.system.api.cln.casc1.APIMultipleClientSubscribeCasc1Test;
import org.serviceconnector.test.system.api.cln.casc1.APIStressExecutionCasc1Test;

public class APIStressExecutionCasc2Test extends APIStressExecutionCasc1Test {

	public APIStressExecutionCasc2Test() {
		APIStressExecutionCasc2Test.setUp2CascadedServiceConnectorAndServer();
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
					TestConstants.PORT_SC2_TCP, ConnectionType.NETTY_TCP, 10, 0, TestConstants.sesServerName1, 50, 60,
					"f_execute1000MessagesAndExit");
			clientCtxs[i] = clientCtx;
		}
		APIStressExecutionCasc1Test.ctrl.waitForClientTermination(clientCtxs);
		TestUtil.checkLogFile(TestConstants.log4jClnProperties, "message.log");
		TestUtil.checkLogFile(TestConstants.log4jClnProperties, "sc.log");
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
					TestConstants.PORT_SC2_TCP, ConnectionType.NETTY_TCP, 10, 0, TestConstants.sesServerName1, 10, 60,
					"f_execute100000MessagesAndExit");
			clientCtxs[i] = clientCtx;
		}
		APIStressExecutionCasc1Test.ctrl.waitForClientTermination(clientCtxs);
		TestUtil.checkLogFile(TestConstants.log4jClnProperties, "message.log");
		TestUtil.checkLogFile(TestConstants.log4jClnProperties, "sc.log");
	}
	

	/**
	 * Description: Exchanges a 10MB message with the server<br>
	 * Expectation: passes
	 */
	@Test
	public void t160_10MBMessageExchange() throws Exception {
		ProcessCtx clientCtx = ctrl.startSessionClient(TestConstants.log4jClnProperties, "client", TestConstants.HOST,
				TestConstants.PORT_SC2_TCP, ConnectionType.NETTY_TCP, 10, 0, TestConstants.sesServerName1, 50, 60,
				"f_execute10MBMessageAndExit");

		APIMultipleClientSubscribeCasc1Test.ctrl.waitForClientTermination(clientCtx);
		TestUtil.checkLogFile(TestConstants.log4jClnProperties, "sc.log");
		TestUtil.checkLogFile(TestConstants.log4jClnProperties, "message.log");
	}
}