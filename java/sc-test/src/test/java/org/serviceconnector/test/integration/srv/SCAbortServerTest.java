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

public class SCAbortServerTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCAbortServerTest.class);

	private SCSessionServer server;
	private Process scProcess;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
	}

	@Before
	public void beforeOneTest() throws Exception {
		ctrl = new ProcessesController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		} catch (Exception e) {
			logger.error("beforeAllTests", e);
		}

		server = new SCSessionServer();
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			server.deregister(TestConstants.publishServiceNames);
			server.deregister(TestConstants.sessionServiceNames);
		} catch (Exception e) {
			// might happen nothing to do
		}
		server.destroy();
		server = null;
		ctrl.stopProcess(scProcess, TestConstants.log4jSCProperties);
		scProcess = null;
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		ctrl = null;
	}

	@Test(expected = SCServiceException.class)
	public void registerServer_afterSCDestroyValidValues_throwsException() throws Exception {
		scProcess.destroy();
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.sessionServiceNames, 10, 10,
				new CallBack());
	}

	@Test(expected = SCMPValidatorException.class)
	public void registerServer_afterSCDestroyInvalidMaxSessions_throwsException() throws Exception {
		scProcess.destroy();
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.sessionServiceNames, -1, 10,
				new CallBack());
	}

	@Test(expected = SCServiceException.class)
	public void registerServer_afterSCDestroyInvalidHost_throwsException() throws Exception {
		scProcess.destroy();
		server.registerServer("something", TestConstants.PORT_TCP, TestConstants.sessionServiceNames, 10, 10, new CallBack());
	}

	@Test(expected = SCServiceException.class)
	public void registerServer_withImmediateConnectFalseAfterSCDestroyInvalidHost_throwsException() throws Exception {
		server.setImmediateConnect(false);
		scProcess.destroy();
		server.registerServer("something", TestConstants.PORT_TCP, TestConstants.sessionServiceNames, 10, 10, new CallBack());
	}

	@Test
	public void deregisterServer_afterSCDestroyWithoutPreviousRegister_passes() throws Exception {
		scProcess.destroy();
		server.deregister(TestConstants.sessionServiceNames);
	}

	@Test
	public void deregisterServer_afterRegisterAndSCDestroy_notRegistered() throws Exception {
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.sessionServiceNames, 10, 10,
				new CallBack());
		scProcess.destroy();
		scProcess.waitFor();
		try {
			server.deregister(TestConstants.sessionServiceNames);
		} catch (SCServiceException ex) {
		}
		assertEquals(false, server.isRegistered(TestConstants.sessionServiceNames));
	}

	@Test(expected = SCServiceException.class)
	public void registerServer_afterRegisterAfterSCDestroy_throwsException() throws Exception {
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.sessionServiceNames, 10, 10,
				new CallBack());
		scProcess.destroy();
		scProcess.waitFor();
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.publishServiceNames, 10, 10,
				new CallBack());
	}

	@Test
	public void isRegistered_afterRegisterAfterSCDestroy_thinksThatItIsRegistered() throws Exception {
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.sessionServiceNames, 10, 10,
				new CallBack());
		scProcess.destroy();
		scProcess.waitFor();
		assertEquals(true, server.isRegistered(TestConstants.sessionServiceNames));
	}

	@Test
	public void setImmediateConnect_afterRegisterAfterSCDestroy_passes() throws Exception {
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.sessionServiceNames, 10, 10,
				new CallBack());
		scProcess.destroy();
		scProcess.waitFor();
		server.setImmediateConnect(false);
	}

	private class CallBack extends SCSessionServerCallback {

		@Override
		public SCMessage execute(SCMessage message, int operationTimeoutInMillis) {
			return null;
		}
	}
}
