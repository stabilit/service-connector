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
package org.serviceconnector.test.system.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCAppendMessage;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCRemovedMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCGuardianMessageCallback;
import org.serviceconnector.api.cln.SCMessageCallback;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ServerDefinition;
import org.serviceconnector.ctrl.util.ServiceConnectorDefinition;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.system.SystemSuperTest;
import org.serviceconnector.test.system.api.cln.casc1.APICacheCoherencyCasc1Test;

public class APISystemSuperCCTest extends SystemSuperTest {

	protected SCMgmtClient mgmtClient;
	protected SCClient sessionClient;
	protected SCSessionService sessionService1;
	protected SCClient guardianClient;
	protected GuardianCbk cacheGuardianCbk;
	protected SessionMsgCallback sessionCbk;

	public APISystemSuperCCTest() {
		APISystemSuperCCTest.setUp1CascadedServiceConnectorAndServer();
	}

	public void setUpClientToSC() throws Exception {
		if (cascadingLevel == 0) {
			mgmtClient = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC0_TCP, ConnectionType.NETTY_TCP);
			guardianClient = new SCClient(TestConstants.HOST, TestConstants.PORT_SC0_TCP, ConnectionType.NETTY_TCP);
			sessionClient = new SCClient(TestConstants.HOST, TestConstants.PORT_SC0_TCP, ConnectionType.NETTY_TCP);
		} else if (cascadingLevel == 1) {
			mgmtClient = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP);
			guardianClient = new SCClient(TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP);
			sessionClient = new SCClient(TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP);
		} else if (cascadingLevel == 2) {
			mgmtClient = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC2_TCP, ConnectionType.NETTY_TCP);
			guardianClient = new SCClient(TestConstants.HOST, TestConstants.PORT_SC2_TCP, ConnectionType.NETTY_TCP);
			sessionClient = new SCClient(TestConstants.HOST, TestConstants.PORT_SC2_TCP, ConnectionType.NETTY_TCP);
		}
		mgmtClient.attach();
		guardianClient.attach();
		sessionClient.attach();
		cacheGuardianCbk = new GuardianCbk();

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
				TestConstants.PORT_SC0_TCP, 3, 3, TestConstants.cacheGuardian1);

		ServerDefinition srvPublish2ToSC0Def = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH,
				TestConstants.log4jSrvProperties, TestConstants.pubServerName2, TestConstants.PORT_PUB_SRV2_TCP,
				TestConstants.PORT_SC0_TCP, 3, 3, TestConstants.cacheGuardian2);

		ServerDefinition srvSessionToSC0Def = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_SESSION,
				TestConstants.log4jSrvProperties, TestConstants.sesServerName1, TestConstants.PORT_SES_SRV_TCP,
				TestConstants.PORT_SC0_TCP, 100, 10, TestConstants.sesServiceName1);

		srvToSC0CascDefs.add(srvPublishToSC0Def);
		srvToSC0CascDefs.add(srvPublish2ToSC0Def);
		srvToSC0CascDefs.add(srvSessionToSC0Def);
		SystemSuperTest.srvDefs = srvToSC0CascDefs;
	}

	protected void startSC0AAndServers() throws Exception {
		List<ServiceConnectorDefinition> sc0ADefs = new ArrayList<ServiceConnectorDefinition>();
		ServiceConnectorDefinition sc0Def = new ServiceConnectorDefinition(TestConstants.SC0A, TestConstants.SC0AProperties,
				TestConstants.log4jSC0AProperties);
		sc0ADefs.add(sc0Def);

		Map<String, ProcessCtx> sc0AProcess = ctrl.startSCEnvironment(sc0ADefs);
		SystemSuperTest.scCtxs.putAll(sc0AProcess);

		List<ServerDefinition> srvToSC0ACascDefs = new ArrayList<ServerDefinition>();
		ServerDefinition srvPublishToSC0Def = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH,
				TestConstants.log4jSrvProperties, TestConstants.pubServerName1A, TestConstants.PORT_PUB_SRVA_TCP,
				TestConstants.PORT_SC0A_TCP, 3, 3, TestConstants.cacheGuardian1A);

		ServerDefinition srvSessionToSC0Def = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_SESSION,
				TestConstants.log4jSrvProperties, TestConstants.sesServerName1A, TestConstants.PORT_SES_SRVA_TCP,
				TestConstants.PORT_SC0A_TCP, 100, 10, TestConstants.sesServiceName1);

		srvToSC0ACascDefs.add(srvPublishToSC0Def);
		srvToSC0ACascDefs.add(srvSessionToSC0Def);

		Map<String, ProcessCtx> sc0ASeverProcess = ctrl.startServerEnvironment(srvToSC0ACascDefs);
		SystemSuperTest.srvCtxs.putAll(sc0ASeverProcess);
	}

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			guardianClient.stopCacheGuardian();
			sessionService1.deleteSession();
		} catch (Exception e1) {
			SystemSuperTest.testLogger.error("receive error=" + e1.getMessage());
		}
		try {
			mgmtClient.detach();
			guardianClient.detach();
			sessionClient.detach();
		} catch (Exception e) {
			SystemSuperTest.testLogger.error("receive error=" + e.getMessage());
		}
		mgmtClient = null;
		guardianClient = null;
		super.afterOneTest();
	}

	protected class GuardianCbk extends SCGuardianMessageCallback {

		private int appendMsgRecvCounter;
		private int removeMsgRecvCounger;
		private int initialMsgRecvCounter;

		public GuardianCbk() {
			this.appendMsgRecvCounter = 0;
		}

		public void waitForMessage(int nrSeconds) throws Exception {
			this.waitForMessage(1, nrSeconds);
		}

		public void waitForMessage(int nrMsgs, int nrSeconds) throws Exception {
			try {
				for (int i = 0; i < (nrSeconds * 10); i++) {
					if (initialMsgRecvCounter >= nrMsgs) {
						return;
					}
					Thread.sleep(100);
				}
				throw new TimeoutException("No message received within " + nrSeconds + " seconds timeout.");
			} finally {
				this.initialMsgRecvCounter = 0;
			}
		}

		public void waitForAppendMessage(int nrMsgs, int nrSeconds) throws Exception {
			try {
				if (nrMsgs == 0) {
					Thread.sleep(nrSeconds * 1000);
					if (this.appendMsgRecvCounter == 0) {
						return;
					}
					throw new TimeoutException(this.appendMsgRecvCounter + " Message(s) received within " + nrSeconds
							+ " seconds, fault zero expected.");
				}

				for (int i = 0; i < (nrSeconds * 10); i++) {
					if (appendMsgRecvCounter >= nrMsgs) {
						return;
					}
					Thread.sleep(100);
				}

				throw new TimeoutException("No message received within " + nrSeconds + " seconds timeout.");
			} finally {
				this.appendMsgRecvCounter = 0;
			}
		}

		public void waitForRemoveMessage(int nrMsgs, int nrSeconds) throws Exception {
			try {
				for (int i = 0; i < (nrSeconds * 10); i++) {
					if (removeMsgRecvCounger >= nrMsgs) {
						return;
					}
					Thread.sleep(100);
				}
				throw new TimeoutException("No remove message received within " + nrSeconds + " seconds timeout.");
			} finally {
				this.removeMsgRecvCounger = 0;
			}
		}

		@Override
		public void receive(SCMessage initial) {
			SystemSuperTest.testLogger.info("receive initial msg=" + initial.toString());
			initialMsgRecvCounter++;
		}

		@Override
		public void receiveAppendix(SCAppendMessage appendix) {
			// SystemSuperTest.testLogger.info("receive append msg=" + appendix.toString());
			appendMsgRecvCounter++;
			if (appendMsgRecvCounter == 1) {
				SystemSuperTest.testLogger.info("Receiving message nr. " + appendMsgRecvCounter);
			}
			if (((appendMsgRecvCounter + 1) % 5000) == 0) {
				SystemSuperTest.testLogger.info("Receiving message nr. " + (appendMsgRecvCounter + 1));
			}
		}

		@Override
		public void receiveRemove(SCRemovedMessage remove) {
			SystemSuperTest.testLogger.info("receive remove msg=" + remove.toString());
			removeMsgRecvCounger++;
		}

		@Override
		public void receive(Exception ex) {
			SystemSuperTest.testLogger.error("receive error=" + ex.getMessage());
			appendMsgRecvCounter++;
			removeMsgRecvCounger++;
			initialMsgRecvCounter++;
		}

		public int getUpdateMsgCounter() {
			return this.appendMsgRecvCounter;
		}
	}

	protected class SessionMsgCallback extends SCMessageCallback {

		private Exception receivedException;

		public SessionMsgCallback(SCSessionService service) {
			super(service);
		}

		@Override
		public void receive(SCMessage reply) {
		}

		@Override
		public void receive(Exception ex) {
			this.receivedException = ex;
		}

		public Exception getReceivedException() {
			return receivedException;
		}
	}
}
