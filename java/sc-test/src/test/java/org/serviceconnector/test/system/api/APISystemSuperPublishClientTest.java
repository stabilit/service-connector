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

import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCMessageCallback;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.test.system.api.publish.APIReceivePublicationTest;

public class APISystemSuperPublishClientTest extends APISystemSuperTest {

	protected SCClient client;
	protected ProcessCtx srvCtx;
	protected MsgCallback cbk = null;

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		srvCtx = ctrl.startServer(TestConstants.COMMUNICATOR_TYPE_PUBLISH, TestConstants.log4jSrvProperties,
				TestConstants.pubServerName1, TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, 10,
				TestConstants.pubServiceName1);
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
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
			logger.error("receive error: " + e.getMessage());
			if (e instanceof SCServiceException) {
				SCMPError scError = ((SCServiceException) e).getSCMPError();
				logger.info("SC error received code:" + scError.getErrorCode() + " text:" + scError.getErrorText());
			}
			message = null;
			messageCounter = expectedMessages;
		}
	}

}
