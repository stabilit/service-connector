/*
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
 */
package org.serviceconnector.test.perf.api;

import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCMessageCallback;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.api.cln.SCService;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.test.perf.api.cln.APIReceivePublicationBenchmark;

public class APIPerfSuperClientTest extends APIPerfSuperTest {

	protected SCClient client;
	protected SCSessionService sessionService = null;
	protected SCPublishService publishService = null;
	protected ProcessCtx sesSrvCtx;
	protected ProcessCtx pubSrvCtx;

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		sesSrvCtx = ctrl.startServer(TestConstants.COMMUNICATOR_TYPE_SESSION, TestConstants.log4jSrvProperties,
				TestConstants.sesServerName1, TestConstants.PORT_SES_SRV_TCP, TestConstants.PORT_SC_TCP, 1000, 10,
				TestConstants.sesServiceName1);
		pubSrvCtx = ctrl.startServer(TestConstants.COMMUNICATOR_TYPE_PUBLISH, TestConstants.log4jSrvProperties,
				TestConstants.pubServerName1, TestConstants.PORT_PUB_SRV_TCP, TestConstants.PORT_SC_TCP, 1000, 10,
				TestConstants.pubServiceName1);
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
			sessionService.deleteSession();
		} catch (Exception e1) {
		}
		sessionService = null;
		try {
			client.detach();
		} catch (Exception e) {
		}
		client = null;
		try {
			ctrl.stopServer(sesSrvCtx);
		} catch (Exception e) {
		}
		sesSrvCtx = null;
		try {
			ctrl.stopServer(pubSrvCtx);
		} catch (Exception e) {
		}
		pubSrvCtx = null;
		super.afterOneTest();
	}

	protected class MsgCallback extends SCMessageCallback {

		private SCMessage message;
		private int messageCounter;
		private int expectedMessages;
		long start = System.currentTimeMillis();
		long stop = 0;
		long startPart = System.currentTimeMillis();
		long stopPart = 0;

		public MsgCallback(SCService service) {
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

		public long getDifference() {
			return (stop - start);
		}

		@Override
		public void receive(SCMessage msg) {
			message = msg;
			messageCounter++;

			if (((messageCounter + 1) % 1000) == 0) {
				stopPart = System.currentTimeMillis();
				APIReceivePublicationBenchmark.testLogger.info("Receiving message nr. " + (messageCounter + 1) + "... "
						+ (1000000 / (stopPart - startPart)) + " msg/sec.");
				startPart = System.currentTimeMillis();
			}
			if (expectedMessages == messageCounter) {
				stop = System.currentTimeMillis();
			}
		}

		@Override
		public void receive(Exception e) {
			logger.error("receive error: " + e.getMessage());
			if (e instanceof SCServiceException) {
				logger.info("SC error received code:" + ((SCServiceException) e).getSCErrorCode() + " text:"
						+ ((SCServiceException) e).getSCErrorText());
			}
			message = null;
			messageCounter = expectedMessages;
		}
	}
	
}
