package org.serviceconnector.test.system.session;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.cln.ISCClient;
import org.serviceconnector.cln.ISessionService;
import org.serviceconnector.cln.SCClient;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.ctrl.util.TestEnvironmentController;
import org.serviceconnector.sc.service.SCServiceException;
import org.serviceconnector.service.SCMessage;



public class PrematureDestroyOfServerClientToSCTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PrematureDestroyOfServerClientToSCTest.class);

	private static Process scProcess;
	private Process srvProcess;

	private ISCClient client;

	private Exception ex;

	private static TestEnvironmentController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@Before
	public void setUp() throws Exception {
		try {
			srvProcess = ctrl.startServer(TestConstants.sessionSrv, TestConstants.log4jSrvProperties, 30000, TestConstants.PORT9000, 100, new String[] {TestConstants.serviceName, TestConstants.serviceNameAlt});
		} catch (Exception e) {
			logger.error("setUp", e);
		}
		client = new SCClient();
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
	}

	@After
	public void tearDown() throws Exception {
		client.detach();
		client = null;
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		srvProcess = null;
		ex = null;
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSC0Properties);
		ctrl = null;
		scProcess = null;
	}
	
	@Test
	public void createSession_withoutServer_throwsException() throws Exception {
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			sessionService.createSession("sessionInfo", 300, 5);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(null, sessionService.getSessionId());
	}
	
	@Test
	public void deleteSession_withoutServer_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 5);

		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);

		try {
			sessionService.deleteSession();
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
	}
	
	@Test
	public void execute_withoutServer_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 5);

		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);

		try {
			sessionService.execute(new SCMessage());
		} catch (Exception e) {
			ex = e;
			e.printStackTrace();
		}
		assertEquals(true, ex instanceof SCServiceException);
	}
	
	@Test
	public void deleteSession_withoutServerTimeoutTakes5Seconds_passes() throws Exception {
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 5, 5);

		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		
		Thread.sleep(5000);
		sessionService.deleteSession();
	}
	
	@Test(expected = SCServiceException.class)
	public void execute_withoutServer_timeoutTakes5SecondsThrowsException() throws Exception {
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 5, 5);
		
		Thread.sleep(5000);
		try {
			sessionService.execute(new SCMessage());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(null, sessionService.getSessionId());
		throw ex;
	}
	
}
