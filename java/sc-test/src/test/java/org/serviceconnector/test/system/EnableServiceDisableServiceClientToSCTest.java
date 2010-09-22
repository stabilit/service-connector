package org.serviceconnector.test.system;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.ISCClient;
import org.serviceconnector.api.cln.ISessionService;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.sc.service.SCServiceException;


public class EnableServiceDisableServiceClientToSCTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger
			.getLogger(EnableServiceDisableServiceClientToSCTest.class);

	private static Process scProcess;
	private Process srvProcess;
	
	private int threadCount = 0;
	private ISCClient client;
	private Exception ex;

	private static ProcessesController ctrl;

	//TODO registering notEnabled service fails
	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@Before
	public void setUp() throws Exception {
		threadCount = Thread.activeCount();
		srvProcess = ctrl.startServer(TestConstants.sessionSrv, TestConstants.log4jSrvProperties, 30000, TestConstants.PORT9000, 100, new String[] {
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
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		srvProcess = null;
		ex = null;
		assertEquals("number of threads", threadCount, Thread.activeCount());
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSC0Properties);
		ctrl = null;
		scProcess = null;
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
		assertEquals(false, client.isServiceEnabled(TestConstants.serviceNameSessionNotEnabled));
		client.enableService(TestConstants.serviceNameSessionNotEnabled);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameSessionNotEnabled));
		
		ISessionService sessionService0 = client.newSessionService(TestConstants.serviceName);
		sessionService0.createSession("sessionInfo", 300, 60);
		sessionService0.execute(new SCMessage("register " + TestConstants.serviceNameSessionNotEnabled));
		
		
		ISessionService sessionService1 = client.newSessionService(TestConstants.serviceNameSessionNotEnabled);
		sessionService1.createSession("sessionInfo", 300, 60);

		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameSessionNotEnabled));
		assertEquals(false, sessionService1.getSessionId() == null
				|| sessionService1.getSessionId().isEmpty());
		sessionService1.deleteSession();
		
		sessionService0.execute(new SCMessage("deregister " + TestConstants.serviceNameSessionNotEnabled));
		sessionService0.deleteSession();
		client.disableService(TestConstants.serviceNameSessionNotEnabled);
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
