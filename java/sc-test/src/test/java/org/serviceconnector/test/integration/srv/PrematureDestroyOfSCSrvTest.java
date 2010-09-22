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
import org.serviceconnector.sc.service.SCServiceException;


public class PrematureDestroyOfSCSrvTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger
			.getLogger(PrematureDestroyOfSCSrvTest.class);

	private ISCSessionServer server;
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
			scProcess = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}

		server = new SCSessionServer();
		server.startListener(TestConstants.HOST, 30000, 0);
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

	@Test(expected = SCServiceException.class)
	public void registerServer_afterSCDestroyValidValues_throwsException() throws Exception {
		scProcess.destroy();
		server.registerServer(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
	}

	@Test(expected = SCMPValidatorException.class)
	public void registerServer_afterSCDestroyInvalidMaxSessions_throwsException() throws Exception {
		scProcess.destroy();
		server.registerServer(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, -1, 10, new CallBack());
	}

	@Test(expected = SCServiceException.class)
	public void registerServer_afterSCDestroyInvalidHost_throwsException() throws Exception {
		scProcess.destroy();
		server.registerServer("something", TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
	}

	@Test(expected = SCServiceException.class)
	public void registerServer_withImmediateConnectFalseAfterSCDestroyInvalidHost_throwsException()
			throws Exception {
		server.setImmediateConnect(false);
		scProcess.destroy();
		server.registerServer("something", TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
	}

	@Test
	public void deregisterServer_afterSCDestroyWithoutPreviousRegister_passes() throws Exception {
		scProcess.destroy();
		server.deregisterServer(TestConstants.serviceName);
	}

	@Test
	public void deregisterServer_afterRegisterAfterSCDestroy_notRegistered() throws Exception {
		server.registerServer(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
		scProcess.destroy();
		server.deregisterServer(TestConstants.serviceName);
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
	}
	
	@Test(expected = SCServiceException.class)
	public void registerServer_afterRegisterAfterSCDestroy_throwsException() throws Exception {
		server.registerServer(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
		scProcess.destroy();
		server.registerServer(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceNameAlt, 10, 10, new CallBack());
	}
	
	@Test
	public void isRegistered_afterRegisterAfterSCDestroy_thinksThatItIsRegistered() throws Exception {
		server.registerServer(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
		scProcess.destroy();
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
	}
	
	@Test
	public void setImmediateConnect_afterRegisterAfterSCDestroy_passes() throws Exception {
		server.registerServer(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
		scProcess.destroy();
		server.setImmediateConnect(false);
	}

	private class CallBack implements ISCServerCallback {
	}
}
