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

import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.system.SystemSuperTest;
import org.serviceconnector.test.system.api.cln.casc1.APISubscribeUnsubscribeChangeCasc1Test;

public class APISubscribeUnsubscribeChangeCasc2Test extends APISubscribeUnsubscribeChangeCasc1Test {

	public APISubscribeUnsubscribeChangeCasc2Test() {
		APISubscribeUnsubscribeChangeCasc2Test.setUp2CascadedServiceConnectorAndServer();
	}
	
	/**
	 * Description: two clients subscribe to a message queue, the server gets destroyed<br>
	 * Expectation: clients get a not found error, passes
	 */
	@Test
	public void t95_TwoSubscribersServerGetsDestroyed() throws Exception {
		SCClient client2 = new SCClient(TestConstants.HOST, TestConstants.PORT_SC2_TCP, ConnectionType.NETTY_TCP);
		client2.attach();
		SCPublishService service1 = client.newPublishService(TestConstants.pubServiceName1);
		SCPublishService service2 = client2.newPublishService(TestConstants.pubServiceName1);

		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		subMsgRequest.setDataLength(TestConstants.pangram.length());
		@SuppressWarnings("unused")
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(10);

		MsgCallback cbk1 = new MsgCallback(service1);
		MsgCallback cbk2 = new MsgCallback(service2);

		subMsgResponse = service1.subscribe(subMsgRequest, cbk1);
		subMsgResponse = service2.subscribe(subMsgRequest, cbk2);

		// destroy the server
		SystemSuperTest.ctrl.stopServerEnvironment(SystemSuperTest.srvCtxs);
		cbk1.waitForMessage(2);
		cbk2.waitForMessage(2);

		Assert.assertFalse("service1 is still active", service1.isActive());
		Assert.assertFalse("service2 is still active", service2.isActive());
		client2.detach();
	}
}