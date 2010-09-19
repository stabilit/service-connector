package system;

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


public class EnableServiceDisableServiceClientToSCTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger
			.getLogger(EnableServiceDisableServiceClientToSCTest.class);

	private static Process sc;
	private Process srv;

	private ISCClient client;

	private Exception ex;

	private static TestEnvironmentController ctrl;

	//TODO registering notEnabled service fails
	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
		try {
			sc = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@Before
	public void setUp() throws Exception {
		srv = ctrl.startServer(TestConstants.log4jSrvProperties, 30000, TestConstants.PORT9000, 100, new String[] {
				TestConstants.serviceName, TestConstants.serviceNameAlt });

		client = new SCClient();
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		assertEquals("1000/0", client.workload(TestConstants.serviceName));
	}

	@After
	public void tearDown() throws Exception {
		assertEquals("1000/0", client.workload(TestConstants.serviceName));
		client.detach();
		client = null;
		ctrl.stopProcess(srv, TestConstants.log4jSrvProperties);
		srv = null;
		ex = null;
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(sc, TestConstants.log4jSC0Properties);
		ctrl = null;
		sc = null;
	}

	@Test
	public void createSession_onEnabledSessionService_sessionIsCreated() throws Exception {
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));

		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));
		assertEquals(false, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_onInitiallyDisabledSessionService_clientEnablesServiceServerRegistersSessionCreated() throws Exception {
		assertEquals(false, client.isServiceEnabled(TestConstants.serviceNameNotEnabled));
		client.enableService(TestConstants.serviceNameNotEnabled);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameNotEnabled));
		
		ISessionService sessionService0 = client.newSessionService(TestConstants.serviceName);
		sessionService0.createSession("sessionInfo", 300, 60);
		sessionService0.execute(new SCMessage("register " + TestConstants.serviceNameNotEnabled));
		
		
		ISessionService sessionService1 = client.newSessionService(TestConstants.serviceNameNotEnabled);
		sessionService1.createSession("sessionInfo", 300, 60);

		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameNotEnabled));
		assertEquals(false, sessionService1.getSessionId() == null
				|| sessionService1.getSessionId().isEmpty());
		sessionService1.deleteSession();
		
		sessionService0.execute(new SCMessage("deregister " + TestConstants.serviceNameNotEnabled));
		sessionService0.deleteSession();
		client.disableService(TestConstants.serviceNameNotEnabled);
	}
	
	@Test
	public void createSession_onInitiallyEnabledService_clientDisablesServiceServerRestartsThrowsException() throws Exception {
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));
		client.disableService(TestConstants.serviceName);
		
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		
		try {
			sessionService.createSession("sessionInfo", 300, 60);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, client.isServiceEnabled(TestConstants.serviceName));
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		client.enableService(TestConstants.serviceName);
	}
	
	@Test
	public void createSession_enabledServiceDisableThenEnableAgain_createsSession() throws Exception {
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));
		client.disableService(TestConstants.serviceName);
		client.enableService(TestConstants.serviceName);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));
		
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);
		assertEquals(false, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}
}
