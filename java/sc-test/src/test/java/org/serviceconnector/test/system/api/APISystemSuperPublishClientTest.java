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
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.cln.SCMessageCallback;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.ctrl.util.ServerDefinition;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.system.SystemSuperTest;
import org.serviceconnector.test.system.api.cln.APIReceivePublicationTest;
import org.serviceconnector.test.system.api.cln.casc1.APIReceivePublicationCasc1Test;

public class APISystemSuperPublishClientTest extends SystemSuperTest {

	protected SCMgmtClient client;
	protected SCPublishService publishService = null;
	protected MsgCallback msgCallback = null;

	public APISystemSuperPublishClientTest() {
		APISystemSuperPublishClientTest.setUp1CascadedServiceConnectorAndServer();
	}
	
	public void setUpClientToSC() throws Exception {
		if (client == null) {	// client may be already created and attached because the of the class hierarchy
			if (cascadingLevel == 0) {
				client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC0_TCP, ConnectionType.NETTY_TCP);
				client.attach();
			} else if (cascadingLevel == 1) {
				client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP);
				client.attach();
			} else if (cascadingLevel == 2) {
				client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC2_TCP, ConnectionType.NETTY_TCP);
				client.attach();
			}
		}
	}
	
	public static void setUpServiceConnectorAndServer() {
		SystemSuperTest.setUpServiceConnectorAndServer();
		APIReceivePublicationCasc1Test.setUpServer();
	}

	public static void setUp1CascadedServiceConnectorAndServer() {
		SystemSuperTest.setUp1CascadedServiceConnectorAndServer();
		APIReceivePublicationCasc1Test.setUpServer();
	}

	public static void setUp2CascadedServiceConnectorAndServer() {
		SystemSuperTest.setUp2CascadedServiceConnectorAndServer();
		APIReceivePublicationCasc1Test.setUpServer();
	}

	public static void setUpServer() {
		// need to have a server serving 3 sessions here
		List<ServerDefinition> srvToSC0CascDefs = new ArrayList<ServerDefinition>();
		ServerDefinition srvToSC0CascDef = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH,
				TestConstants.log4jSrvProperties, TestConstants.pubServerName1, TestConstants.PORT_PUB_SRV_TCP,
				TestConstants.PORT_SC0_TCP, 3, 3, TestConstants.pubServiceName1);
		srvToSC0CascDefs.add(srvToSC0CascDef);
		SystemSuperTest.srvDefs = srvToSC0CascDefs;
	}

	
//	public static void setUpServiceConnectorAndServer() {
//		// SC definitions
//		List<ServiceConnectorDefinition> sc0Defs = new ArrayList<ServiceConnectorDefinition>();
//		ServiceConnectorDefinition sc0Def = new ServiceConnectorDefinition(TestConstants.SC0, TestConstants.SC0Properties,
//				TestConstants.log4jSC0Properties);
//		sc0Defs.add(sc0Def);
//
//		// server definitions
//		List<ServerDefinition> srvToSC0Defs = new ArrayList<ServerDefinition>();
//		ServerDefinition srvToSC0Def = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH,
//				TestConstants.log4jSrvProperties, TestConstants.pubServerName1, TestConstants.PORT_PUB_SRV_TCP,
//				TestConstants.PORT_SC0_TCP, 1, 1, TestConstants.pubServiceName1);
//		srvToSC0Defs.add(srvToSC0Def);
//
//		SystemSuperTest.scDefs = sc0Defs;
//		SystemSuperTest.srvDefs = srvToSC0Defs;
//	}
//
//	public static void setUp1CascadedServiceConnectorAndServer() {
//		List<ServiceConnectorDefinition> scCascDefs = new ArrayList<ServiceConnectorDefinition>();
//		ServiceConnectorDefinition sc0CascDef = new ServiceConnectorDefinition(TestConstants.SC0_CASC,
//				TestConstants.SC0CASCProperties, TestConstants.log4jSC0CASCProperties);
//		ServiceConnectorDefinition sc1CascDef = new ServiceConnectorDefinition(TestConstants.SC1_CASC,
//				TestConstants.SC1CASC1Properties, TestConstants.log4jSC1CASCProperties);
//		scCascDefs.add(sc0CascDef);
//		scCascDefs.add(sc1CascDef);
//
//		List<ServerDefinition> srvToSC0CascDefs = new ArrayList<ServerDefinition>();
//		ServerDefinition srvToSC0CascDef = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH,
//				TestConstants.log4jSrvProperties, TestConstants.pubServerName1, TestConstants.PORT_PUB_SRV_TCP,
//				TestConstants.PORT_SC0_CASC_TCP, 1, 1, TestConstants.pubServiceName1);
//		srvToSC0CascDefs.add(srvToSC0CascDef);
//
//		SystemSuperTest.scDefs = scCascDefs;
//		SystemSuperTest.srvDefs = srvToSC0CascDefs;
//	}

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
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
			if (((messageCounter + 1) % 1000) == 0) {
				APIReceivePublicationTest.testLogger.info("Receiving message nr. " + (messageCounter + 1));
			}
		}

		@Override
		public void receive(Exception e) {
			SystemSuperTest.testLogger.error("receive error=" + e.getMessage());
			if (e instanceof SCServiceException) {
				SystemSuperTest.testLogger.info("SC error received code=" + ((SCServiceException) e).getSCErrorCode() + " text="
						+ ((SCServiceException) e).getSCErrorText());
			}
			message = null;
			messageCounter = expectedMessages;
		}
	}

}
