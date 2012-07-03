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
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCMessageCallback;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.ctrl.util.ServerDefinition;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.system.SystemSuperTest;
import org.serviceconnector.test.system.api.cln.casc1.APICacheCoherencyCasc1Test;

public class APISystemSuperCCTest extends SystemSuperTest {

	protected SCMgmtClient mgmtClient;
	protected SCClient sessionClient;
	protected SCSessionService sessionService1;
	protected SCClient updateRetrClient;
	protected UpdateRetrieverCbk updateRetrieverCbk;
	protected SessionMsgCallback sessionCbk;

	protected static int updateMsgRecvCounter = 0;

	public APISystemSuperCCTest() {
		APISystemSuperCCTest.setUp1CascadedServiceConnectorAndServer();
	}

	public void setUpClientToSC() throws Exception {
		if (cascadingLevel == 0) {
			mgmtClient = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC0_TCP, ConnectionType.NETTY_TCP);
			updateRetrClient = new SCClient(TestConstants.HOST, TestConstants.PORT_SC0_TCP, ConnectionType.NETTY_TCP);
			sessionClient = new SCClient(TestConstants.HOST, TestConstants.PORT_SC0_TCP, ConnectionType.NETTY_TCP);
		} else if (cascadingLevel == 1) {
			mgmtClient = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP);
			updateRetrClient = new SCClient(TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP);
			sessionClient = new SCClient(TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP);
		} else if (cascadingLevel == 2) {
			mgmtClient = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC2_TCP, ConnectionType.NETTY_TCP);
			updateRetrClient = new SCClient(TestConstants.HOST, TestConstants.PORT_SC2_TCP, ConnectionType.NETTY_TCP);
			sessionClient = new SCClient(TestConstants.HOST, TestConstants.PORT_SC2_TCP, ConnectionType.NETTY_TCP);
		}
		mgmtClient.attach();
		updateRetrClient.attach();
		sessionClient.attach();
		updateRetrieverCbk = new UpdateRetrieverCbk();

		sessionService1 = sessionClient.newSessionService(TestConstants.sesServiceName1);
		sessionCbk = new SessionMsgCallback(sessionService1);
		sessionService1.createSession(new SCMessage(), sessionCbk);
	}

	public static void setUpServiceConnectorAndServer() {
		SystemSuperTest.setUpServiceConnectorAndServer();
		APICacheCoherencyCasc1Test.setUpServer();
	}

	public static void setUp1CascadedServiceConnectorAndServer() {
		SystemSuperTest.setUp1CascadedServiceConnectorAndServer();
		APICacheCoherencyCasc1Test.setUpServer();
	}

	public static void setUp2CascadedServiceConnectorAndServer() {
		SystemSuperTest.setUp2CascadedServiceConnectorAndServer();
		APICacheCoherencyCasc1Test.setUpServer();
	}

	public static void setUpServer() {
		// need to have a server serving 3 sessions here
		List<ServerDefinition> srvToSC0CascDefs = new ArrayList<ServerDefinition>();
		ServerDefinition srvPublishToSC0Def = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH,
				TestConstants.log4jSrvProperties, TestConstants.pubServerName1, TestConstants.PORT_PUB_SRV_TCP,
				TestConstants.PORT_SC0_TCP, 3, 3, TestConstants.updateRetrieverName1);

		ServerDefinition srvSessionToSC0Def = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_SESSION,
				TestConstants.log4jSrvProperties, TestConstants.sesServerName1, TestConstants.PORT_SES_SRV_TCP,
				TestConstants.PORT_SC0_TCP, 100, 10, TestConstants.sesServiceName1);

		srvToSC0CascDefs.add(srvPublishToSC0Def);
		srvToSC0CascDefs.add(srvSessionToSC0Def);
		SystemSuperTest.srvDefs = srvToSC0CascDefs;
	}

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			updateRetrClient.stopCacheUpdateRetriever();
			sessionService1.deleteSession();
		} catch (Exception e1) {
			SystemSuperTest.testLogger.error("receive error=" + e1.getMessage());
		}
		try {
			mgmtClient.detach();
			updateRetrClient.detach();
			sessionClient.detach();
		} catch (Exception e) {
			SystemSuperTest.testLogger.error("receive error=" + e.getMessage());
		}
		mgmtClient = null;
		updateRetrClient = null;
		super.afterOneTest();
	}

	protected class UpdateRetrieverCbk extends SCMessageCallback {

		public void waitForMessage(int nrSeconds) throws Exception {
			this.waitForMessage(nrSeconds, 1);
		}

		public void waitForMessage(int nrSeconds, int nrMsgs) throws Exception {
			for (int i = 0; i < (nrSeconds * 10); i++) {
				if (APISystemSuperCCTest.updateMsgRecvCounter >= nrMsgs) {
					return;
				}
				Thread.sleep(100);
			}
			throw new TimeoutException("No message received within " + nrSeconds + " seconds timeout.");
		}

		@Override
		public void receive(SCMessage reply) {
			SystemSuperTest.testLogger.info("receive msg=" + reply.toString() + " data:" + reply.getData());
			APISystemSuperCCTest.updateMsgRecvCounter++;
		}

		@Override
		public void receive(Exception ex) {
			SystemSuperTest.testLogger.error("receive error=" + ex.getMessage());
			APISystemSuperCCTest.updateMsgRecvCounter++;
		}

	}

	protected class SessionMsgCallback extends SCMessageCallback {
		public SessionMsgCallback(SCSessionService service) {
			super(service);
		}

		@Override
		public void receive(SCMessage reply) {
		}

		@Override
		public void receive(Exception ex) {
		}
	}
}
