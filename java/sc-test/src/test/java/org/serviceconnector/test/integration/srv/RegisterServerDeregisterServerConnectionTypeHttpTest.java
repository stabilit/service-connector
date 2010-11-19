package org.serviceconnector.test.integration.srv;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.srv.SCSessionServerCallback;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnetor.TestConstants;

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
		server.deregisterServer("Name");
		assertEquals(false, server.isRegistered("Name"));
	}

	@Test
	public void deregisterServer_withoutRegisteringArbitraryServiceName_notRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 1);
		server.deregisterServer("Name");
		assertEquals(false, server.isRegistered("Name"));
	}

	@Test
	public void deregisterServer_withoutRegisteringServiceNameInSCProps_notRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 1);
		server.deregisterServer(TestConstants.HOST);
		assertEquals(false, server.isRegistered(TestConstants.HOST));
	}

	@Test
	public void deregisterServer_withoutRegisteringServicewithNoHost_notRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 1);
		server.deregisterServer(null);
		assertEquals(false, server.isRegistered(null));
	}

	@Test
	public void deregisterServer_withoutRegisteringServicewithEmptyHost_notRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 1);
		server.deregisterServer("");
		assertEquals(false, server.isRegistered(""));
	}

	@Test
	public void deregisterServer_withoutRegisteringServicewithWhiteSpaceHost_notRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 1);
		server.deregisterServer(" ");
		assertEquals(false, server.isRegistered(" "));
	}

	@Test
	public void deregisterServer_afterValidRegister_registeredThenNotRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 1);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sessionServiceName, 1, 1,
				new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.sessionServiceName));
		server.deregisterServer(TestConstants.sessionServiceName);
		assertEquals(false, server.isRegistered(TestConstants.sessionServiceName));
	}

	@Test
	public void deregisterServer_afterValidRegisterDifferentServiceName_registeredThenNotRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 1);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.publishServiceName, 1, 1,
				new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.publishServiceName));
		server.deregisterServer(TestConstants.publishServiceName);
		assertEquals(false, server.isRegistered(TestConstants.publishServiceName));
	}

	@Test
	public void deregisterServer_differentThanRegistered_registered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 1);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sessionServiceName, 1, 1,
				new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.sessionServiceName));
		server.deregisterServer(TestConstants.publishServiceName);
		assertEquals(true, server.isRegistered(TestConstants.sessionServiceName));
		server.deregisterServer(TestConstants.sessionServiceName);
	}

	@Test
	public void registerServerDeregisterServer_cycle500Times_registeredThenNotRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 100);
		int cycles = 500;
		for (int i = 0; i < cycles; i++) {
			if ((i % 500) == 0)
				testLogger.info("Register/Deregister cycle nr. " + i + "...");
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sessionServiceName, 1, 1, new CallBack());
			assertEquals(true, server.isRegistered(TestConstants.sessionServiceName));
			server.deregisterServer(TestConstants.sessionServiceName);
			assertEquals(false, server.isRegistered(TestConstants.sessionServiceName));
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
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sessionServiceName, 1, 1, new CallBack());
			assertEquals(true, server.isRegistered(TestConstants.sessionServiceName));
			server.deregisterServer(TestConstants.sessionServiceName);
			assertEquals(false, server.isRegistered(TestConstants.sessionServiceName));
			server.destroy();
			server = null;
			server = new SCSessionServer();
			((SCSessionServer) server).setConnectionType("netty.tcp");
			server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER + i, 0);
			server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.sessionServiceName, 1, 1, new CallBack());
			assertEquals(true, server.isRegistered(TestConstants.sessionServiceName));
			server.deregisterServer(TestConstants.sessionServiceName);
			assertEquals(false, server.isRegistered(TestConstants.sessionServiceName));
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
