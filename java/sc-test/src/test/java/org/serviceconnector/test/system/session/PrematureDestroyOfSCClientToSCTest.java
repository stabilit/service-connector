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


public class PrematureDestroyOfSCClientToSCTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger
			.getLogger(PrematureDestroyOfSCClientToSCTest.class);

	private Process scProcess;
	private Process srvProcess;

	private int threadCount = 0;
	private ISCClient client;

	private static TestEnvironmentController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
	}

	@Before
	public void setUp() throws Exception {
		threadCount = Thread.activeCount();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
			srvProcess = ctrl.startServer(TestConstants.sessionSrv, TestConstants.log4jSrvProperties, 30000,
					TestConstants.PORT9000, 100, new String[] { TestConstants.serviceName, TestConstants.serviceNameAlt });
		} catch (Exception e) {
			logger.error("setUp", e);
		}
		client = new SCClient();
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
	}

	@After
	public void tearDown() throws Exception {
		try {
			client.detach();
		} finally {
			ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
			ctrl.stopProcess(scProcess, TestConstants.log4jSC0Properties);
			client = null;
			srvProcess = null;
			scProcess = null;
		}
		assertEquals("number of threads", threadCount, Thread.activeCount());
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl = null;
	}
	
	@Test
	public void detach_afterSCRestart_notAttached() throws Exception {
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		srvProcess = ctrl.startServer(TestConstants.sessionSrv, TestConstants.log4jSrvProperties, 30000,
				TestConstants.PORT9000, 100, new String[] { TestConstants.serviceName, TestConstants.serviceNameAlt });

		// client thinks he is attached
		client.detach();
	}

	@Test
	public void createSession_afterSCRestart_createsSessionService() throws Exception {
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		srvProcess = ctrl.startServer(TestConstants.sessionSrv, TestConstants.log4jSrvProperties, 30000,
				TestConstants.PORT9000, 100, new String[] { TestConstants.serviceName, TestConstants.serviceNameAlt });

		// client thinks he is attached
		assertEquals(true, client.newSessionService(TestConstants.serviceName) instanceof ISessionService);
	}
	
	@Test(expected = SCServiceException.class)
	public void newSessionService_afterSCRestart_ThrowsSCServiceException() throws Exception {
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		srvProcess = ctrl.startServer(TestConstants.sessionSrv, TestConstants.log4jSrvProperties, 30000,
				TestConstants.PORT9000, 100, new String[] { TestConstants.serviceName, TestConstants.serviceNameAlt });

		// client thinks he is attached
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 60);
	}
}
