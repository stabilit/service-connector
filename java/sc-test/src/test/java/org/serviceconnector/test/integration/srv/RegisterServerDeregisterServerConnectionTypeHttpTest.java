package org.serviceconnector.test.integration.srv;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.api.srv.ISCServerCallback;
import org.serviceconnector.api.srv.ISCSessionServer;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.ctrl.util.TestEnvironmentController;
import org.serviceconnector.log.Loggers;


public class RegisterServerDeregisterServerConnectionTypeHttpTest {

	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());
	
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(RegisterServerDeregisterServerConnectionTypeHttpTest.class);
	
	private int threadCount = 0;
	private ISCSessionServer server;

	private static TestEnvironmentController ctrl;
	private static Process scProcess;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSC0Properties);
		ctrl = null;
		scProcess = null;
	}
	
	@Before
	public void setUp() throws Exception {
		threadCount = Thread.activeCount();
		server = new SCSessionServer();
		((SCSessionServer) server).setConnectionType("netty.http");
	}
	
	@After
	public void tearDown() throws Exception {
		server.destroyServer();
		server = null;
		assertEquals("number of threads", threadCount, Thread.activeCount());
	}

	@Test
	public void deregisterServer_withoutListenerArbitraryServiceName_notRegistered()
			throws Exception {
		server.deregisterServer("Name");
		assertEquals(false, server.isRegistered("Name"));
	}

	@Test
	public void deregisterServer_withoutRegisteringArbitraryServiceName_notRegistered()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 1);
		server.deregisterServer("Name");
		assertEquals(false, server.isRegistered("Name"));
	}

	@Test
	public void deregisterServer_withoutRegisteringServiceNameInSCProps_notRegistered()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 1);
		server.deregisterServer(TestConstants.HOST);
		assertEquals(false, server.isRegistered(TestConstants.HOST));
	}

	@Test
	public void deregisterServer_withoutRegisteringServicewithNoHost_notRegistered()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 1);
		server.deregisterServer(null);
		assertEquals(false, server.isRegistered(null));
	}

	@Test
	public void deregisterServer_withoutRegisteringServicewithEmptyHost_notRegistered()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 1);
		server.deregisterServer("");
		assertEquals(false, server.isRegistered(""));
	}

	@Test
	public void deregisterServer_withoutRegisteringServicewithWhiteSpaceHost_notRegistered()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 1);
		server.deregisterServer(" ");
		assertEquals(false, server.isRegistered(" "));
	}

	@Test
	public void deregisterServer_afterValidRegister_registeredThenNotRegistered() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 1);
		server.registerServer(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterServer(TestConstants.serviceName);
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
	}

	@Test
	public void deregisterServer_afterValidRegisterDifferentServiceName_registeredThenNotRegistered()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 1);
		server.registerServer(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceNameAlt, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceNameAlt));
		server.deregisterServer(TestConstants.serviceNameAlt);
		assertEquals(false, server.isRegistered(TestConstants.serviceNameAlt));
	}
	
	@Test
	public void deregisterServer_differentThanRegistered_registered() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 1);
		server.registerServer(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterServer(TestConstants.serviceNameAlt);
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterServer(TestConstants.serviceName);
	}

	@Test
	public void registerServerDeregisterServer_cycle500Times_registeredThenNotRegistered()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 100);
		int cycles = 500;
		for (int i = 0; i < cycles / 10; i++) {
			testLogger.info("RegisterDeregister server iteration:\t" + i * 10);
			for (int j = 0; j < 10; j++) {
				server.registerServer(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1, 1, new CallBack());
				assertEquals(true, server.isRegistered(TestConstants.serviceName));
				server.deregisterServer(TestConstants.serviceName);
				assertEquals(false, server.isRegistered(TestConstants.serviceName));
			}
		}
	}
	
	@Test
	public void registerServer_500CyclesWithChangingConnectionType_registeredThenNotRegistered() throws Exception {
		int cycles = 250;
		for (int i = 0; i < cycles / 10; i++) {
			testLogger.info("RegisterDeregister changing connection type iteration:\t" + i * 10);
			for (int j = 0; j < 10; j++) {
				server = new SCSessionServer();
				((SCSessionServer) server).setConnectionType("netty.http");
				server.startListener(TestConstants.HOST, 9001, 0);
				server.registerServer(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1, 1, new CallBack());
				assertEquals(true, server.isRegistered(TestConstants.serviceName));
				server.deregisterServer(TestConstants.serviceName);
				assertEquals(false, server.isRegistered(TestConstants.serviceName));
				server.destroyServer();
				server = null;
				server = new SCSessionServer();
				((SCSessionServer) server).setConnectionType("netty.tcp");
				server.startListener(TestConstants.HOST, 9001, 0);
				server.registerServer(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 1, 1, new CallBack());
				assertEquals(true, server.isRegistered(TestConstants.serviceName));
				server.deregisterServer(TestConstants.serviceName);
				assertEquals(false, server.isRegistered(TestConstants.serviceName));
				server.destroyServer();
			}
		}
	}
	
	// region end

	private class CallBack implements ISCServerCallback {
	}

}
