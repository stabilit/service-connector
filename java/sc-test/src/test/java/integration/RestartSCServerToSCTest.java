package integration;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.sc.ctrl.util.TestEnvironmentController;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.service.SCServiceException;
import com.stabilit.scm.srv.ISCServer;
import com.stabilit.scm.srv.ISCServerCallback;
import com.stabilit.scm.srv.SCServer;


public class RestartSCServerToSCTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PrematureDestroyOfSCClientToSCTest.class);
	
	private static ISCServer server;
	private static Process p;

	private String host = "localhost";
	private int port9000 = 9000;

	private String serviceName = "simulation";
	private static final String log4jSCProperties = "log4jSC0.properties";
	private static final String scProperties = "scIntegration.properties";
	
	private static TestEnvironmentController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
	}

	@Before
	public void setUp() throws Exception {
		try {
			p = ctrl.startSC(log4jSCProperties, scProperties);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
		server = new SCServer();
		server.startListener(host, 30000, 0);
	}

	@After
	public void tearDown() throws Exception {
		server.destroyServer();
		p.destroy();
		ctrl.deleteFile(ctrl.getPidLogPath(log4jSCProperties));
	}
	
	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		p.destroy();
		ctrl.deleteFile(ctrl.getPidLogPath(log4jSCProperties));
	}

	@Test
	public void registerService_afterSCRestartValidValues_isRegistered() throws Exception {
		p = ctrl.restartSC(p, log4jSCProperties, scProperties);
		server.registerService(host, port9000, serviceName, 10, 10, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
		server.deregisterService(serviceName);
	}

	@Test(expected = SCMPValidatorException.class)
	public void registerService_afterSCRestartInvalidMaxSessions_throwsException() throws Exception {
		p = ctrl.restartSC(p, log4jSCProperties, scProperties);
		server.registerService(host, port9000, serviceName, -1, 10, new CallBack());
	}

	@Test(expected = SCServiceException.class)
	public void registerService_afterSCRestartInvalidHost_throwsException() throws Exception {
		p = ctrl.restartSC(p, log4jSCProperties, scProperties);
		server.registerService("something", port9000, serviceName, 10, 10, new CallBack());
	}

	@Test(expected = SCServiceException.class)
	public void registerService_withImmediateConnectFalseAfterSCRestartInvalidHost_throwsException()
			throws Exception {
		server.setImmediateConnect(false);
		p = ctrl.restartSC(p, log4jSCProperties, scProperties);
		server.registerService("something", port9000, serviceName, 10, 10, new CallBack());
	}

	@Test
	public void deregisterService_afterSCRestart_passes() throws Exception {
		p = ctrl.restartSC(p, log4jSCProperties, scProperties);
		server.deregisterService(serviceName);
	}

	@Test
	public void deregisterService_afterRegisterAfterSCRestart_isRegistered() throws Exception {
		server.registerService(host, port9000, serviceName, 10, 10, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
		p = ctrl.restartSC(p, log4jSCProperties, scProperties);
		try { 
			server.deregisterService(serviceName);
		} catch (SCServiceException e) {
		}
		assertEquals(false, server.isRegistered(serviceName));
	}
	
	@Test
	public void isRegistered_afterRegisterAfterSCRestart_isRegistered() throws Exception {
		server.registerService(host, port9000, serviceName, 10, 10, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
		p = ctrl.restartSC(p, log4jSCProperties, scProperties);
		assertEquals(true, server.isRegistered(serviceName));
	}
	
	@Test
	public void registerService_afterRegisterAfterSCRestart_isRegistered() throws Exception {
		server.registerService(host, port9000, serviceName, 10, 10, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
		p = ctrl.restartSC(p, log4jSCProperties, scProperties);
		server.registerService(host, port9000, serviceName, 10, 10, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
		server.deregisterService(serviceName);
	}

	private class CallBack implements ISCServerCallback {
	}
}
