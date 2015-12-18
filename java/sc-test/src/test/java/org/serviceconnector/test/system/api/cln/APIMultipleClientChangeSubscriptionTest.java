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

import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.system.api.cln.casc1.APIMultipleClientChangeSubscriptionCasc1Test;

public class APIMultipleClientChangeSubscriptionTest extends APIMultipleClientChangeSubscriptionCasc1Test {

	public APIMultipleClientChangeSubscriptionTest() {
		APIMultipleClientChangeSubscriptionCasc1Test.setUpServiceConnectorAndServer();
	}

	/**
	 * Description: 3 clients Subscribe, 1 receives 10000, 2 receives 500 message and unsubscribe<br>
	 * Expectation: passes
	 */
	@Test
	public void t11_3ClientsReceivingMessages() throws Exception {
		int numberOfClients = 3;
		ProcessCtx[] clientCtxs = new ProcessCtx[numberOfClients];

		ProcessCtx clientCtx3 = ctrl.startPublishClient(TestConstants.log4jClnProperties, "client0", TestConstants.HOST,
				TestConstants.PORT_SC0_TCP, ConnectionType.NETTY_TCP, 10, 0, TestConstants.pubServerName1, 50,
				"f_subscribeReceive10000Unsubscribe");
		clientCtxs[0] = clientCtx3;
		for (int i = 1; i < clientCtxs.length; i++) {
			ProcessCtx clientCtx = ctrl.startPublishClient(TestConstants.log4jClnProperties, "client" + i, TestConstants.HOST,
					TestConstants.PORT_SC0_TCP, ConnectionType.NETTY_TCP, 10, 0, TestConstants.pubServerName1, 50,
					"f_subscribeReceive500ChangeSubscriptionUnsubscribe");
			clientCtxs[i] = clientCtx;
		}
		APIMultipleClientChangeSubscriptionCasc1Test.ctrl.waitForClientTermination(clientCtxs);
		// dont't check message.log might be an EXC because of broken CRP
		TestUtil.checkLogFile(TestConstants.log4jClnProperties, "sc.log");
	}

	/**
	 * Description: 3 clients changeSubscription 10000<br>
	 * Expectation: passes
	 */
	@Test
	public void t15_3ClientsChangeSubscription10000() throws Exception {
		int numberOfClients = 10;
		ProcessCtx[] clientCtxs = new ProcessCtx[numberOfClients];

		ProcessCtx clientCtx3 = ctrl.startPublishClient(TestConstants.log4jClnProperties, "client0", TestConstants.HOST,
				TestConstants.PORT_SC0_TCP, ConnectionType.NETTY_TCP, 10, 0, TestConstants.pubServerName1, 50,
				"f_subscribeReceive10000Unsubscribe");
		clientCtxs[0] = clientCtx3;
		for (int i = 1; i < clientCtxs.length; i++) {
			ProcessCtx clientCtx = ctrl.startPublishClient(TestConstants.log4jClnProperties, "client" + i, TestConstants.HOST,
					TestConstants.PORT_SC0_TCP, ConnectionType.NETTY_TCP, 10, 0, TestConstants.pubServerName1, 50,
					"f_10000ChangeSubscription");
			clientCtxs[i] = clientCtx;
		}
		APIMultipleClientChangeSubscriptionCasc1Test.ctrl.waitForClientTermination(clientCtxs);
		// dont't check message.log might be an EXC because of broken CRP
		TestUtil.checkLogFile(TestConstants.log4jClnProperties, "sc.log");
	}
}