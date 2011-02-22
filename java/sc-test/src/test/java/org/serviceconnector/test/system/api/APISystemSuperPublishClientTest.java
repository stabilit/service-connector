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
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCMessageCallback;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ServerDefinition;
import org.serviceconnector.ctrl.util.ServiceConnectorDefinition;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.system.SystemSuperTest;
import org.serviceconnector.test.system.api.cln.APIReceivePublicationTest;

public class APISystemSuperPublishClientTest extends SystemSuperTest {

	protected SCClient client;
	protected SCPublishService publishService = null;
	protected static Map<String, ProcessCtx> pubSrvCtx;
	protected MsgCallback msgCallback = null;
	protected static List<ServerDefinition> srvDefs;

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
		pubSrvCtx = ctrl.startServerEnvironment(srvDefs);
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
		try {
			ctrl.stopServerEnvironment(pubSrvCtx);
		} catch (Exception e) {
		}
		pubSrvCtx = null;
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

		ServerDefinition srvToSC0Def = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH,
				TestConstants.log4jSrvProperties, TestConstants.pubServerName1, TestConstants.PORT_PUB_SRV_TCP,
				TestConstants.PORT_SC_TCP, 100, 10, TestConstants.pubServiceName1);
		srvToSC0Defs.add(srvToSC0Def);

		SystemSuperTest.scDefs = sc0Defs;
		APISystemSuperPublishClientTest.srvDefs = srvToSC0Defs;
	}

	public static void setUp1CascadedServiceConnectorAndServer() {
		List<ServiceConnectorDefinition> scCascDefs = new ArrayList<ServiceConnectorDefinition>();
		ServiceConnectorDefinition sc0CascDef = new ServiceConnectorDefinition(TestConstants.SC0_CASC,
				TestConstants.SC0CASCProperties, TestConstants.log4jSC0CASCProperties);
		ServiceConnectorDefinition sc1CascDef = new ServiceConnectorDefinition(TestConstants.SC1_CASC,
				TestConstants.SC1CASC1Properties, TestConstants.log4jSC1CASCProperties);
		scCascDefs.add(sc0CascDef);
		scCascDefs.add(sc1CascDef);

		List<ServerDefinition> srvToSC0CascDefs = new ArrayList<ServerDefinition>();
		ServerDefinition srvToSC0CascDef = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH,
				TestConstants.log4jSrvProperties, TestConstants.pubServerName1, TestConstants.PORT_PUB_SRV_TCP,
				TestConstants.PORT_SC0_CASC_TCP, 100, 10, TestConstants.pubServiceName1);
		srvToSC0CascDefs.add(srvToSC0CascDef);

		SystemSuperTest.scDefs = scCascDefs;
		APISystemSuperPublishClientTest.srvDefs = srvToSC0CascDefs;
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
