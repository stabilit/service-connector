package system;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.cln.SCClient;
import org.serviceconnector.cln.service.ISCClient;
import org.serviceconnector.cln.service.ISessionService;
import org.serviceconnector.common.service.SCServiceException;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.ctrl.util.TestEnvironmentController;


public class PrematureDestroyOfSCClientToSCTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger
			.getLogger(PrematureDestroyOfSCClientToSCTest.class);

	private Process sc;
	private Process srv;

	private ISCClient client;

	private static TestEnvironmentController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
	}

	@Before
	public void setUp() throws Exception {
		try {
			sc = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
			srv = ctrl.startServer(TestConstants.log4jSrvProperties, 30000, TestConstants.PORT9000,
					100, new String[] { TestConstants.serviceName, TestConstants.serviceNameAlt });
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
			ctrl.stopProcess(srv, TestConstants.log4jSrvProperties);
			ctrl.stopProcess(sc, TestConstants.log4jSC0Properties);
			client = null;
			srv = null;
			sc = null;
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl = null;
	}
	
	@Test
	public void detach_afterSCRestart_notAttached() throws Exception {
		sc = ctrl.restartSC(sc, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		ctrl.stopProcess(srv, TestConstants.log4jSrvProperties);
		srv = ctrl.startServer(TestConstants.log4jSrvProperties, 30000, TestConstants.PORT9000,
				100, new String[] { TestConstants.serviceName, TestConstants.serviceNameAlt });

		// client thinks he is attached
		client.detach();
	}

	@Test
	public void createSession_afterSCRestart_createsSessionService() throws Exception {
		sc = ctrl.restartSC(sc, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		ctrl.stopProcess(srv, TestConstants.log4jSrvProperties);
		srv = ctrl.startServer(TestConstants.log4jSrvProperties, 30000, TestConstants.PORT9000,
				100, new String[] { TestConstants.serviceName, TestConstants.serviceNameAlt });

		// client thinks he is attached
		assertEquals(true, client.newSessionService(TestConstants.serviceName) instanceof ISessionService);
	}
	
	@Test(expected = SCServiceException.class)
	public void newSessionService_afterSCRestart_ThrowsSCServiceException() throws Exception {
		sc = ctrl.restartSC(sc, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		ctrl.stopProcess(srv, TestConstants.log4jSrvProperties);
		srv = ctrl.startServer(TestConstants.log4jSrvProperties, 30000, TestConstants.PORT9000,
				100, new String[] { TestConstants.serviceName, TestConstants.serviceNameAlt });

		// client thinks he is attached
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 60);
	}
}
