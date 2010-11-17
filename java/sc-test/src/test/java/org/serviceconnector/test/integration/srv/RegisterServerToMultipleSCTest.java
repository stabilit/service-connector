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
import org.serviceconnetor.TestConstants;


public class RegisterServerToMultipleSCTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(RegisterServerToMultipleSCTest.class);
	
	private int threadCount = 0;
	private SCSessionServer server;

	private static ProcessesController ctrl;
	private static Process scProcess;
	private static Process r;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
			r = ctrl.startSC(TestConstants.log4jSCcascadedProperties, TestConstants.SCcascadedProperties);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSCProperties);
		ctrl.stopProcess(r, TestConstants.log4jSCcascadedProperties);
		ctrl = null;
		scProcess = null;
		r = null;
	}

	@Before
	public void setUp() throws Exception {
//		threadCount = Thread.activeCount();
		server = new SCSessionServer();
	}

	@After
	public void tearDown() throws Exception {
		server.destroy();
		server = null;
//		assertEquals("number of threads", threadCount, Thread.activeCount());
	}

	@Test
	public void registerServer_onMultipleSCs_registeredOnBoth() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.serviceNameSession, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(false, server.isRegistered(TestConstants.serviceNamePublish));
		server.registerServer(TestConstants.HOST, TestConstants.PORT_MAX, TestConstants.serviceNamePublish, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, server.isRegistered(TestConstants.serviceNamePublish));
		server.deregisterServer(TestConstants.serviceNameSession);
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, server.isRegistered(TestConstants.serviceNamePublish));
		server.deregisterServer(TestConstants.serviceNamePublish);
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(false, server.isRegistered(TestConstants.serviceNamePublish));
	}

	@Test
	public void registerServer_withDifferentConnectionTypesHttpFirst_registeredThenNot()
			throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		((SCSessionServer) server).setConnectionType("netty.http");
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(false, server.isRegistered(TestConstants.serviceNamePublish));
		((SCSessionServer) server).setConnectionType("netty.tcp");
		server.registerServer(TestConstants.HOST, TestConstants.PORT_MAX, TestConstants.serviceNamePublish, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, server.isRegistered(TestConstants.serviceNamePublish));
		server.deregisterServer(TestConstants.serviceNameSession);
		server.deregisterServer(TestConstants.serviceNamePublish);
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(false, server.isRegistered(TestConstants.serviceNamePublish));
	}

	@Test
	public void registerServer_withDifferentConnectionTypesTcpFirst_registeredThenNot()
			throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.serviceNameSession, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(false, server.isRegistered(TestConstants.serviceNamePublish));
		((SCSessionServer) server).setConnectionType("netty.http");
		server.registerServer(TestConstants.HOST, TestConstants.PORT_MIN, TestConstants.serviceNamePublish, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, server.isRegistered(TestConstants.serviceNamePublish));
		server.deregisterServer(TestConstants.serviceNameSession);
		server.deregisterServer(TestConstants.serviceNamePublish);
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(false, server.isRegistered(TestConstants.serviceNamePublish));
	}

	@Test
	public void registerServer_httpConnectionType_registeredThenNot() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		((SCSessionServer) server).setConnectionType("netty.http");
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(false, server.isRegistered(TestConstants.serviceNamePublish));
		server.registerServer(TestConstants.HOST, TestConstants.PORT_MIN, TestConstants.serviceNamePublish, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, server.isRegistered(TestConstants.serviceNamePublish));
		server.deregisterServer(TestConstants.serviceNameSession);
		server.deregisterServer(TestConstants.serviceNamePublish);
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(false, server.isRegistered(TestConstants.serviceNamePublish));
	}

	@Test
	public void registerServerDeregisterServer_onTwoSCsHttp_periodicallyRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		((SCSessionServer) server).setConnectionType("netty.http");
		for (int i = 0; i < 100; i++) {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1, 1, new CallBack());
			server.registerServer(TestConstants.HOST, TestConstants.PORT_MIN, TestConstants.serviceNamePublish, 1, 1, new CallBack());
			assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
			assertEquals(true, server.isRegistered(TestConstants.serviceNamePublish));
			server.deregisterServer(TestConstants.serviceNameSession);
			server.deregisterServer(TestConstants.serviceNamePublish);
			assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
			assertEquals(false, server.isRegistered(TestConstants.serviceNamePublish));
		}
	}

	@Test
	public void registerServerDeregisterServer_onTwoSCsTcp_periodicallyRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		for (int i = 0; i < 100; i++) {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.serviceNameSession, 1, 1, new CallBack());
			server.registerServer(TestConstants.HOST, TestConstants.PORT_MAX, TestConstants.serviceNamePublish, 1, 1, new CallBack());
			assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
			assertEquals(true, server.isRegistered(TestConstants.serviceNamePublish));
			server.deregisterServer(TestConstants.serviceNameSession);
			server.deregisterServer(TestConstants.serviceNamePublish);
			assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
			assertEquals(false, server.isRegistered(TestConstants.serviceNamePublish));
		}
	}
	
	//TODO verify with jan - service name must be unique!!!
//	@Test
	public void registerServerDeregisterServer_onTwoSCsBoth_periodicallyRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		for (int i = 0; i < 100; i++) {
			((SCSessionServer) server).setConnectionType("netty.tcp");
			server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.serviceNameSession, 1, 1, new CallBack());
			((SCSessionServer) server).setConnectionType("netty.http");
			server.registerServer(TestConstants.HOST, TestConstants.PORT_MIN, TestConstants.serviceNameSession, 1, 1, new CallBack());
			assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
			assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
			server.deregisterServer(TestConstants.serviceNameSession);
			server.deregisterServer(TestConstants.serviceNameSession);
			assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
			assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
			System.out.println(i);
		}
	}

	@Test
	public void registerServerDeregisterServer_onTwoSCsChangingConnectionTypes_periodicallyRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		for (int i = 0; i < 50; i++) {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.serviceNameSession, 1, 1, new CallBack());
			((SCSessionServer) server).setConnectionType("netty.http");
			server.registerServer(TestConstants.HOST, TestConstants.PORT_MIN, TestConstants.serviceNamePublish, 1, 1, new CallBack());
			assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
			assertEquals(true, server.isRegistered(TestConstants.serviceNamePublish));
			server.deregisterServer(TestConstants.serviceNameSession);
			server.deregisterServer(TestConstants.serviceNamePublish);
			assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
			assertEquals(false, server.isRegistered(TestConstants.serviceNamePublish));
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1, 1, new CallBack());
			((SCSessionServer) server).setConnectionType("netty.tcp");
			server.registerServer(TestConstants.HOST, TestConstants.PORT_MAX, TestConstants.serviceNamePublish, 1, 1, new CallBack());
			assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
			assertEquals(true, server.isRegistered(TestConstants.serviceNamePublish));
			server.deregisterServer(TestConstants.serviceNameSession);
			server.deregisterServer(TestConstants.serviceNamePublish);
			assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
			assertEquals(false, server.isRegistered(TestConstants.serviceNamePublish));
			((SCSessionServer) server).setConnectionType("netty.tcp");
		}
	}

	@Test
	public void registerServerDeregisterServer_onTwoSCsChangingServices_periodicallyRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		for (int i = 0; i < 50; i++) {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.serviceNameSession, 1, 1, new CallBack());
			server.registerServer(TestConstants.HOST, TestConstants.PORT_MAX, TestConstants.serviceNamePublish, 1, 1, new CallBack());
			assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
			assertEquals(true, server.isRegistered(TestConstants.serviceNamePublish));
			server.deregisterServer(TestConstants.serviceNameSession);
			server.deregisterServer(TestConstants.serviceNamePublish);
			assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
			assertEquals(false, server.isRegistered(TestConstants.serviceNamePublish));
			server.registerServer(TestConstants.HOST, TestConstants.PORT_MAX, TestConstants.serviceNameSession, 1, 1, new CallBack());
			server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.serviceNamePublish, 1, 1, new CallBack());
			assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
			assertEquals(true, server.isRegistered(TestConstants.serviceNamePublish));
			server.deregisterServer(TestConstants.serviceNameSession);
			server.deregisterServer(TestConstants.serviceNamePublish);
			assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
			assertEquals(false, server.isRegistered(TestConstants.serviceNamePublish));
		}
	}

	private class CallBack extends SCSessionServerCallback {

		@Override
		public SCMessage execute(SCMessage message, int operationTimeoutInMillis) {
			return null;
		}
	}
}
