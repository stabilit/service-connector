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
import org.serviceconnector.api.srv.SCSessionServerCallback;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;

public class RegisterServerDeregisterServerConnectionTypeHttpTest {

	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(RegisterServerDeregisterServerConnectionTypeHttpTest.class);

	private int threadCount = 0;
	private SCSessionServer server;

	private static ProcessesController ctrl;
	private static Process scProcess;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSCProperties);
		ctrl = null;
		scProcess = null;
	}

	@Before
	public void setUp() throws Exception {
//		threadCount = Thread.activeCount();
		server = new SCSessionServer();
		((SCSessionServer) server).setConnectionType("netty.http");
	}

	@After
	public void tearDown() throws Exception {
		server.destroy();
		server = null;
//		Thread.sleep(5); // little sleep because threads needs to end
//		assertEquals("number of threads", threadCount, Thread.activeCount());
	}

	@Test
	public void deregisterServer_withoutListenerArbitraryServiceName_notRegistered() throws Exception {
		server.deregister("Name");
		assertEquals(false, server.isRegistered("Name"));
	}

	@Test
	public void deregisterServer_withoutRegisteringArbitraryServiceName_notRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 1);
		server.deregister("Name");
		assertEquals(false, server.isRegistered("Name"));
	}

	@Test
	public void deregisterServer_withoutRegisteringServiceNameInSCProps_notRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 1);
		server.deregister(TestConstants.HOST);
		assertEquals(false, server.isRegistered(TestConstants.HOST));
	}

	@Test
	public void deregisterServer_withoutRegisteringServicewithNoHost_notRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 1);
		server.deregister(null);
		assertEquals(false, server.isRegistered(null));
	}

	@Test
	public void deregisterServer_withoutRegisteringServicewithEmptyHost_notRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 1);
		server.deregister("");
		assertEquals(false, server.isRegistered(""));
	}

	@Test
	public void deregisterServer_withoutRegisteringServicewithWhiteSpaceHost_notRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 1);
		server.deregister(" ");
		assertEquals(false, server.isRegistered(" "));
	}

	@Test
	public void deregisterServer_afterValidRegister_registeredThenNotRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 1);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sessionServiceNames, 1, 1,
				new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.sessionServiceNames));
		server.deregister(TestConstants.sessionServiceNames);
		assertEquals(false, server.isRegistered(TestConstants.sessionServiceNames));
	}

	@Test
	public void deregisterServer_afterValidRegisterDifferentServiceName_registeredThenNotRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 1);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.publishServiceNames, 1, 1,
				new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.publishServiceNames));
		server.deregister(TestConstants.publishServiceNames);
		assertEquals(false, server.isRegistered(TestConstants.publishServiceNames));
	}

	@Test
	public void deregisterServer_differentThanRegistered_registered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 1);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sessionServiceNames, 1, 1,
				new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.sessionServiceNames));
		server.deregister(TestConstants.publishServiceNames);
		assertEquals(true, server.isRegistered(TestConstants.sessionServiceNames));
		server.deregister(TestConstants.sessionServiceNames);
	}

	@Test
	public void registerServerDeregisterServer_cycle500Times_registeredThenNotRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 100);
		int cycles = 500;
		for (int i = 0; i < cycles; i++) {
			if ((i % 500) == 0)
				testLogger.info("Register/Deregister cycle nr. " + i + "...");
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sessionServiceNames, 1, 1, new CallBack());
			assertEquals(true, server.isRegistered(TestConstants.sessionServiceNames));
			server.deregister(TestConstants.sessionServiceNames);
			assertEquals(false, server.isRegistered(TestConstants.sessionServiceNames));
		}
	}

	// TODO out of memory direct buffer problem
	// @Test
	public void registerServer_500CyclesWithChangingConnectionType_registeredThenNotRegistered() throws Exception {
		int cycles = 500;
		for (int i = 0; i < cycles; i++) {
			if ((i % 100) == 0)
				testLogger.info("Register/Deregister cycle nr. " + i + "...");
			server = new SCSessionServer();
			((SCSessionServer) server).setConnectionType("netty.http");
			server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER + i, 0);
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sessionServiceNames, 1, 1, new CallBack());
			assertEquals(true, server.isRegistered(TestConstants.sessionServiceNames));
			server.deregister(TestConstants.sessionServiceNames);
			assertEquals(false, server.isRegistered(TestConstants.sessionServiceNames));
			server.destroy();
			server = null;
			server = new SCSessionServer();
			((SCSessionServer) server).setConnectionType("netty.tcp");
			server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER + i, 0);
			server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.sessionServiceNames, 1, 1, new CallBack());
			assertEquals(true, server.isRegistered(TestConstants.sessionServiceNames));
			server.deregister(TestConstants.sessionServiceNames);
			assertEquals(false, server.isRegistered(TestConstants.sessionServiceNames));
			server.destroy();
			System.gc();
		}
	}

	// region end
	private class CallBack extends SCSessionServerCallback {

		@Override
		public SCMessage execute(SCMessage message, int operationTimeoutInMillis) {
			return null;
		}
	}
}
