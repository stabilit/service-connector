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
package org.serviceconnector.test.integration.srv;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.api.srv.SCSessionServerCallback;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.service.SCServiceException;

public class RestartSCProcessTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(RestartSCProcessTest.class);

	private SCSessionServer server;
	private Process scProcess;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
	}

	@Before
	public void setUp() throws Exception {
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
		server = new SCSessionServer();
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 60);
	}

	@After
	public void tearDown() throws Exception {
		try {
			server.deregisterServer(TestConstants.sessionServiceName);
		} catch (Exception e) {
			// might fail - but doesn't matter
		}
		server.destroy();
		server = null;
		ctrl.stopProcess(scProcess, TestConstants.log4jSCProperties);
		scProcess = null;
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl = null;
	}

	@Test
	public void registerServer_afterSCRestartValidValues_isRegistered() throws Exception {
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSCProperties, TestConstants.SCProperties);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.sessionServiceName, 10, 10,
				new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.sessionServiceName));
		server.deregisterServer(TestConstants.sessionServiceName);
	}

	@Test(expected = SCMPValidatorException.class)
	public void registerServer_afterSCRestartInvalidMaxSessions_throwsException() throws Exception {
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSCProperties, TestConstants.SCProperties);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.sessionServiceName, -1, 10,
				new CallBack());
	}

	@Test(expected = SCServiceException.class)
	public void registerServer_afterSCRestartInvalidHost_throwsException() throws Exception {
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSCProperties, TestConstants.SCProperties);
		server.registerServer("something", TestConstants.PORT_TCP, TestConstants.sessionServiceName, 10, 10, new CallBack());
	}

	@Test(expected = SCServiceException.class)
	public void registerServer_withImmediateConnectFalseAfterSCRestartInvalidHost_throwsException() throws Exception {
		server.setImmediateConnect(false);
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSCProperties, TestConstants.SCProperties);
		server.registerServer("something", TestConstants.PORT_TCP, TestConstants.sessionServiceName, 10, 10, new CallBack());
	}

	@Test
	public void deregisterServer_afterSCRestart_passes() throws Exception {
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSCProperties, TestConstants.SCProperties);
		server.deregisterServer(TestConstants.sessionServiceName);
	}

	@Test
	public void deregisterServer_afterRegisterAfterSCRestart_isRegistered() throws Exception {
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.sessionServiceName, 10, 10,
				new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.sessionServiceName));
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSCProperties, TestConstants.SCProperties);
		try {
			server.deregisterServer(TestConstants.sessionServiceName);
		} catch (SCServiceException e) {
		}
		assertEquals(false, server.isRegistered(TestConstants.sessionServiceName));
	}

	@Test
	public void isRegistered_afterRegisterAfterSCRestart_isRegistered() throws Exception {
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.sessionServiceName, 10, 10,
				new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.sessionServiceName));
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSCProperties, TestConstants.SCProperties);
		assertEquals(true, server.isRegistered(TestConstants.sessionServiceName));
	}

	@Test
	public void registerServer_afterRegisterAfterSCRestart_isRegistered() throws Exception {
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.sessionServiceName, 10, 10,
				new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.sessionServiceName));
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSCProperties, TestConstants.SCProperties);
		try {
			server.deregisterServer(TestConstants.sessionServiceName);
		} catch (Exception e) { // ignore failing, just registering is important!
		}
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.sessionServiceName, 10, 10,
				new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.sessionServiceName));
		server.deregisterServer(TestConstants.sessionServiceName);
	}

	private class CallBack extends SCSessionServerCallback {

		@Override
		public SCMessage execute(SCMessage message, int operationTimeoutInMillis) {
			return null;
		}
	}
}
