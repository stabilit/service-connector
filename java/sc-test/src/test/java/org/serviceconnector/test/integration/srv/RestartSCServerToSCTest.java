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
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.service.SCServiceException;



public class RestartSCServerToSCTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(RestartSCServerToSCTest.class);
	
	private ISCSessionServer server;
	private Process scProcess;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
	}

	@Before
	public void setUp() throws Exception {
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
		server = new SCSessionServer();
		server.startListener(TestConstants.HOST, 30000, 60);
	}

	@After
	public void tearDown() throws Exception {
		server.destroyServer();
		server = null;
		ctrl.stopProcess(scProcess, TestConstants.log4jSC0Properties);
		scProcess = null;
	}
	
	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl = null;
	}

	@Test
	public void registerServer_afterSCRestartValidValues_isRegistered() throws Exception {
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterServer(TestConstants.serviceName);
	}

	@Test(expected = SCMPValidatorException.class)
	public void registerServer_afterSCRestartInvalidMaxSessions_throwsException() throws Exception {
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, -1, 10, new CallBack());
	}

	@Test(expected = SCServiceException.class)
	public void registerServer_afterSCRestartInvalidHost_throwsException() throws Exception {
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		server.registerServer("something", TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
	}

	@Test(expected = SCServiceException.class)
	public void registerServer_withImmediateConnectFalseAfterSCRestartInvalidHost_throwsException()
			throws Exception {
		server.setImmediateConnect(false);
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		server.registerServer("something", TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
	}

	@Test
	public void deregisterServer_afterSCRestart_passes() throws Exception {
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		server.deregisterServer(TestConstants.serviceName);
	}

	@Test
	public void deregisterServer_afterRegisterAfterSCRestart_isRegistered() throws Exception {
		server.registerServer(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		try { 
			server.deregisterServer(TestConstants.serviceName);
		} catch (SCServiceException e) {
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
	}
	
	@Test
	public void isRegistered_afterRegisterAfterSCRestart_isRegistered() throws Exception {
		server.registerServer(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
	}
	
	@Test
	public void registerServer_afterRegisterAfterSCRestart_isRegistered() throws Exception {
		server.registerServer(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterServer(TestConstants.serviceName);
	}
	
	private class CallBack implements ISCServerCallback {
	}
}
