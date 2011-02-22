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
package org.serviceconnector.test.system.api;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCMessageCallback;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.ctrl.util.ServerDefinition;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.system.SystemSuperTest;
import org.serviceconnector.test.system.api.cln.APIReceivePublicationTest;

public class APISystemSuperPublishClientTest extends SystemSuperTest {

	protected SCClient client;
	protected SCPublishService publishService = null;
	protected MsgCallback msgCallback = null;

	public APISystemSuperPublishClientTest() {
		APISystemSuperPublishClientTest.srvDefs = new ArrayList<ServerDefinition>();
		ServerDefinition srvDef = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH, TestConstants.log4jSrvProperties,
				TestConstants.pubServerName1, TestConstants.PORT_PUB_SRV_TCP, TestConstants.PORT_SC_TCP, 100, 10,
				TestConstants.pubServiceName1);
		APISystemSuperPublishClientTest.srvDefs.add(srvDef);
	}

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		client.attach();
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			publishService.unsubscribe();
		} catch (Exception e1) {
		}
		publishService = null;
		try {
			client.detach();
		} catch (Exception e) {
		}
		client = null;
		super.afterOneTest();
	}

	protected class MsgCallback extends SCMessageCallback {

		private SCMessage message;
		private int messageCounter;
		private int expectedMessages;

		public MsgCallback(SCPublishService service) {
			super(service);
			message = null;
			messageCounter = 0;
			expectedMessages = 1;
		}

		public void waitForMessage(int nrSeconds) throws Exception {
			for (int i = 0; i < (nrSeconds * 10); i++) {
				if (messageCounter == expectedMessages) {
					return;
				}
				Thread.sleep(100);
			}
			throw new TimeoutException("No message received within " + nrSeconds + " seconds timeout.");
		}

		public SCMessage getMessage() {
			return message;
		}

		public void setExpectedMessages(int msgCount) {
			expectedMessages = msgCount;
		}

		public int getMessageCount() {
			return messageCounter;
		}

		@Override
		public void receive(SCMessage msg) {
			message = msg;
			messageCounter++;
			if (((messageCounter + 1) % 100) == 0) {
				APIReceivePublicationTest.testLogger.info("Receiving message nr. " + (messageCounter + 1));
			}
		}

		@Override
		public void receive(Exception e) {
			SystemSuperTest.testLogger.error("receive error: " + e.getMessage());
			if (e instanceof SCServiceException) {
				SystemSuperTest.testLogger.info("SC error received code:" + ((SCServiceException) e).getSCErrorCode() + " text:"
						+ ((SCServiceException) e).getSCErrorText());
			}
			message = null;
			messageCounter = expectedMessages;
		}
	}

}
