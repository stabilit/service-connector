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
package org.serviceconnector.test.system;

import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.SCService;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.test.system.api.publish.APIReceivePublicationTest;

public class APISystemSuperPublishClientTest extends APISystemSuperTest {

	protected SCClient client;
	protected ProcessCtx srvCtx;
	protected static boolean messageReceived = false;
	protected MsgCallback cbk = null;

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		srvCtx = ctrl.startServer(TestConstants.SERVER_TYPE_PUBLISH, TestConstants.log4jSrvProperties,
				TestConstants.pubServerName1, TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, 10,
				TestConstants.pubServiceName1);
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		messageReceived = false;
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			client.detach();
		} catch (Exception e) {
		}
		client = null;
		try {
			ctrl.stopServer(srvCtx);
		} catch (Exception e) {
		}
		srvCtx = null;
		super.afterOneTest();
	}

	
	protected void waitForMessage(int nrSeconds) throws Exception {
		for (int i = 0; i < (nrSeconds * 10); i++) {
			if (messageReceived) {
				return;
			}
			Thread.sleep(100);
		}
		throw new TimeoutException("No message received within " + nrSeconds + " seconds timeout.");
	}

	protected class MsgCallback extends SCMessageCallback {

		private SCMessage message = null;
		private int messageCounter = 0;
		private int expectedMessages = 0;

		public MsgCallback(SCService service) {
			super(service);
			APIReceivePublicationTest.messageReceived = false;
			message = null;
			messageCounter = 0;
			expectedMessages = 0;
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
			if (expectedMessages == messageCounter) {
				APIReceivePublicationTest.messageReceived = true;
			}
			if (((messageCounter + 1) % 100) == 0) {
				APIReceivePublicationTest.testLogger.info("Receiving message nr. " + (messageCounter + 1));
			}
		}

		@Override
		public void receive(Exception e) {
			logger.error("receive error: " + e.getMessage());
			if (e instanceof SCServiceException) {
				SCMPError scError = ((SCServiceException) e).getSCMPError();
				logger.info("SC error received code:" + scError.getErrorCode() + " text:" + scError.getErrorText());
			}
			message = null;
			APIReceivePublicationTest.messageReceived = true;
		}
	}

}
