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
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ServerDefinition;
import org.serviceconnector.ctrl.util.ServiceConnectorDefinition;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.system.SystemSuperTest;

public class APISystemSuperSessionClientTest extends SystemSuperTest {

	protected SCClient client;
	protected SCSessionService sessionService1 = null;
	protected static Map<String, ProcessCtx> sesSrvCtxs;
	protected MsgCallback msgCallback1 = null;
	protected static boolean messageReceived = false;
	protected static List<ServerDefinition> srvDefs;

	public APISystemSuperSessionClientTest() {
		APISystemSuperSessionClientTest.srvDefs = new ArrayList<ServerDefinition>();
		ServerDefinition srvDef = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_SESSION, TestConstants.log4jSrvProperties,
				TestConstants.sesServerName1, TestConstants.PORT_SES_SRV_TCP, TestConstants.PORT_SC_TCP, 100, 10,
				TestConstants.sesServiceName1);
		APISystemSuperSessionClientTest.srvDefs.add(srvDef);
	}

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		sesSrvCtxs = ctrl.startServerEnvironment(srvDefs);
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		messageReceived = false;
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			sessionService1.deleteSession();
		} catch (Exception e1) {
		}
		sessionService1 = null;
		try {
			client.detach();
		} catch (Exception e) {
		}
		client = null;
		try {
			ctrl.stopServerEnvironment(sesSrvCtxs);
		} catch (Exception e) {
		}
		sesSrvCtxs = null;
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
		ServerDefinition srvToSC0Def = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_SESSION,
				TestConstants.log4jSrvProperties, TestConstants.sesServerName1, TestConstants.PORT_SES_SRV_TCP,
				TestConstants.PORT_SC_TCP, 100, 10, TestConstants.sesServiceName1);
		srvToSC0Defs.add(srvToSC0Def);

		SystemSuperTest.scDefs = sc0Defs;
		APISystemSuperSessionClientTest.srvDefs = srvToSC0Defs;
	}

	public static void setUpCascadedServiceConnectorAndServer() {
		List<ServiceConnectorDefinition> scCascDefs = new ArrayList<ServiceConnectorDefinition>();
		ServiceConnectorDefinition sc0CascDef = new ServiceConnectorDefinition(TestConstants.SC0_CASC,
				TestConstants.SC0CASCProperties, TestConstants.log4jSC0CASCProperties);
		ServiceConnectorDefinition sc1CascDef = new ServiceConnectorDefinition(TestConstants.SC1_CASC,
				TestConstants.SC1CASCProperties, TestConstants.log4jSC1CASCProperties);
		scCascDefs.add(sc0CascDef);
		scCascDefs.add(sc1CascDef);

		List<ServerDefinition> srvToSC0CascDefs = new ArrayList<ServerDefinition>();
		ServerDefinition srvToSC0CascDef = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_SESSION,
				TestConstants.log4jSrvProperties, TestConstants.sesServerName1, TestConstants.PORT_SES_SRV_TCP,
				TestConstants.PORT_SC0_CASC_TCP, 100, 10, TestConstants.sesServiceName1);
		srvToSC0CascDefs.add(srvToSC0CascDef);

		SystemSuperTest.scDefs = scCascDefs;
		APISystemSuperSessionClientTest.srvDefs = srvToSC0CascDefs;
	}

	protected class MsgCallback extends SCMessageCallback {
		private SCMessage response = null;
		private int scErrorCode = 0;
		private String scErrorText = null;

		public MsgCallback(SCSessionService service) {
			super(service);
		}

		public void waitForMessage(int nrSeconds) throws Exception {
			for (int i = 0; i < (nrSeconds * 10); i++) {
				if (APISystemSuperSessionClientTest.messageReceived) {
					return;
				}
				Thread.sleep(100);
			}
			throw new TimeoutException("No message received within " + nrSeconds + " seconds timeout.");
		}

		@Override
		public void receive(SCMessage msg) {
			response = msg;
			APISystemSuperSessionClientTest.messageReceived = true;
		}

		@Override
		public void receive(Exception e) {
			testLogger.info("Error received");
			if (e instanceof SCServiceException) {
				SystemSuperTest.testLogger.info("SC error received code:" + ((SCServiceException) e).getSCErrorCode() + " text:"
						+ ((SCServiceException) e).getSCErrorText());
				scErrorCode = Integer.parseInt(((SCServiceException) e).getSCErrorCode());
				scErrorText = ((SCServiceException) e).getSCErrorText();
			} else {
				SystemSuperTest.testLogger.error("receive error: " + e.getMessage());
			}
			response = null;
			APISystemSuperSessionClientTest.messageReceived = true;
		}

		public SCMessage getResponse() {
			return response;
		}

		public int getScErrorCode() {
			return scErrorCode;
		}

		public String getScErrorText() {
			return scErrorText;
		}

	}
}
