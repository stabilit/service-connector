package integration;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.sc.ctrl.util.TestConstants;
import com.stabilit.sc.ctrl.util.TestEnvironmentController;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.service.SCServiceException;
import com.stabilit.scm.srv.ISCServer;
import com.stabilit.scm.srv.ISCServerCallback;
import com.stabilit.scm.srv.SCServer;

public class PrematureDestroyOfSCServerToSCTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger
			.getLogger(PrematureDestroyOfSCServerToSCTest.class);

	private ISCServer server;
	private Process p;

	private static TestEnvironmentController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
	}
	
	@Before
	public void setUp() throws Exception {
		ctrl = new TestEnvironmentController();
		try {
			p = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}

		server = new SCServer();
		server.startListener(TestConstants.HOST, 30000, 0);
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

	@Test(expected = SCServiceException.class)
	public void registerService_afterSCDestroyValidValues_throwsException() throws Exception {
		p.destroy();
		server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
	}

	@Test(expected = SCMPValidatorException.class)
	public void registerService_afterSCDestroyInvalidMaxSessions_throwsException() throws Exception {
		p.destroy();
		server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, -1, 10, new CallBack());
	}

	@Test(expected = SCServiceException.class)
	public void registerService_afterSCDestroyInvalidHost_throwsException() throws Exception {
		p.destroy();
		server.registerService("something", TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
	}

	@Test(expected = SCServiceException.class)
	public void registerService_withImmediateConnectFalseAfterSCDestroyInvalidHost_throwsException()
			throws Exception {
		server.setImmediateConnect(false);
		p.destroy();
		server.registerService("something", TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
	}

	@Test
	public void deregisterService_afterSCDestroyWithoutPreviousRegister_passes() throws Exception {
		p.destroy();
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void deregisterService_afterRegisterAfterSCDestroy_notRegistered() throws Exception {
		server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
		p.destroy();
		server.deregisterService(TestConstants.serviceName);
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
	}
	
	@Test(expected = SCServiceException.class)
	public void registerService_afterRegisterAfterSCDestroy_throwsException() throws Exception {
		server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
		p.destroy();
		server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceNameAlt, 10, 10, new CallBack());
	}
	
	@Test
	public void isRegistered_afterRegisterAfterSCDestroy_thinksThatItIsRegistered() throws Exception {
		server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
		p.destroy();
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
	}
	
	@Test
	public void setImmediateConnect_afterRegisterAfterSCDestroy_passes() throws Exception {
		server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 10, 10, new CallBack());
		p.destroy();
		server.setImmediateConnect(false);
	}

	private class CallBack implements ISCServerCallback {
	}
}
