package org.serviceconnector.test.integration.srv;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.api.srv.SCSessionServerCallback;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.service.SCServiceException;

public class PrematureDestroyOfSCProcessServerTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PrematureDestroyOfSCProcessServerTest.class);

	private SCSessionServer server;
	private Process scProcess;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
	}

	@Before
	public void setUp() throws Exception {
		ctrl = new ProcessesController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}

		server = new SCSessionServer();
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
	}

	@After
	public void tearDown() throws Exception {
		try {
			server.deregisterServer(TestConstants.serviceNameAlt);
			server.deregisterServer(TestConstants.serviceNameSession);
		} catch (Exception e) {
			// might happen nothing to do
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

	@Test(expected = SCServiceException.class)
	public void registerServer_afterSCDestroyValidValues_throwsException() throws Exception {
		scProcess.destroy();
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.serviceNameSession, 10, 10,
				new CallBack());
	}

	@Test(expected = SCMPValidatorException.class)
	public void registerServer_afterSCDestroyInvalidMaxSessions_throwsException() throws Exception {
		scProcess.destroy();
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.serviceNameSession, -1, 10,
				new CallBack());
	}

	@Test(expected = SCServiceException.class)
	public void registerServer_afterSCDestroyInvalidHost_throwsException() throws Exception {
		scProcess.destroy();
		server.registerServer("something", TestConstants.PORT_TCP, TestConstants.serviceNameSession, 10, 10, new CallBack());
	}

	@Test(expected = SCServiceException.class)
	public void registerServer_withImmediateConnectFalseAfterSCDestroyInvalidHost_throwsException() throws Exception {
		server.setImmediateConnect(false);
		scProcess.destroy();
		server.registerServer("something", TestConstants.PORT_TCP, TestConstants.serviceNameSession, 10, 10, new CallBack());
	}

	@Test
	public void deregisterServer_afterSCDestroyWithoutPreviousRegister_passes() throws Exception {
		scProcess.destroy();
		server.deregisterServer(TestConstants.serviceNameSession);
	}

	@Test
	public void deregisterServer_afterRegisterAndSCDestroy_notRegistered() throws Exception {
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.serviceNameSession, 10, 10,
				new CallBack());
		scProcess.destroy();
		scProcess.waitFor();
		try {
			server.deregisterServer(TestConstants.serviceNameSession);
		} catch (SCServiceException ex) {
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
	}

	@Test(expected = SCServiceException.class)
	public void registerServer_afterRegisterAfterSCDestroy_throwsException() throws Exception {
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.serviceNameSession, 10, 10,
				new CallBack());
		scProcess.destroy();
		scProcess.waitFor();
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.serviceNameAlt, 10, 10,
				new CallBack());
	}

	@Test
	public void isRegistered_afterRegisterAfterSCDestroy_thinksThatItIsRegistered() throws Exception {
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.serviceNameSession, 10, 10,
				new CallBack());
		scProcess.destroy();
		scProcess.waitFor();
		assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
	}

	@Test
	public void setImmediateConnect_afterRegisterAfterSCDestroy_passes() throws Exception {
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.serviceNameSession, 10, 10,
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
