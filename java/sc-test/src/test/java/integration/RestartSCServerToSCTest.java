package integration;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.sc.common.cmd.SCMPValidatorException;
import com.stabilit.sc.common.service.SCServiceException;
import com.stabilit.sc.ctrl.util.TestConstants;
import com.stabilit.sc.ctrl.util.TestEnvironmentController;
import com.stabilit.sc.srv.ISCServer;
import com.stabilit.sc.srv.ISCServerCallback;
import com.stabilit.sc.srv.SCServer;


public class RestartSCServerToSCTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(RestartSCServerToSCTest.class);
	
	private ISCServer server;
	private Process p;

	private static TestEnvironmentController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
	}

	@Before
	public void setUp() throws Exception {
		try {
			p = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
		server = new SCServer();
		server.startListener(TestConstants.HOST, 30000, 60);
	}

	@After
	public void tearDown() throws Exception {
		server.destroyServer();
		server = null;
		ctrl.stopProcess(p, TestConstants.log4jSC0Properties);
		p = null;
	}
	
	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl = null;
	}

	@Test
	public void registerService_afterSCRestartValidValues_isRegistered() throws Exception {
		p = ctrl.restartSC(p, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
	}

	@Test(expected = SCMPValidatorException.class)
	public void registerService_afterSCRestartInvalidMaxSessions_throwsException() throws Exception {
		p = ctrl.restartSC(p, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, -1, 10, new CallBack());
	}

	@Test(expected = SCServiceException.class)
	public void registerService_afterSCRestartInvalidHost_throwsException() throws Exception {
		p = ctrl.restartSC(p, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		server.registerService("something", TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
	}

	@Test(expected = SCServiceException.class)
	public void registerService_withImmediateConnectFalseAfterSCRestartInvalidHost_throwsException()
			throws Exception {
		server.setImmediateConnect(false);
		p = ctrl.restartSC(p, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		server.registerService("something", TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
	}

	@Test
	public void deregisterService_afterSCRestart_passes() throws Exception {
		p = ctrl.restartSC(p, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void deregisterService_afterRegisterAfterSCRestart_isRegistered() throws Exception {
		server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		p = ctrl.restartSC(p, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		try { 
			server.deregisterService(TestConstants.serviceName);
		} catch (SCServiceException e) {
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
	}
	
	@Test
	public void isRegistered_afterRegisterAfterSCRestart_isRegistered() throws Exception {
		server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		p = ctrl.restartSC(p, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
	}
	
	@Test
	public void registerService_afterRegisterAfterSCRestart_isRegistered() throws Exception {
		server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		p = ctrl.restartSC(p, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
	}
	
	private class CallBack implements ISCServerCallback {
	}
}
