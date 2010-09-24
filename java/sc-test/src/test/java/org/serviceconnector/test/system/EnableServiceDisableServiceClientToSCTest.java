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
		srvProcess = ctrl.startServer(TestConstants.sessionSrv, TestConstants.log4jSrvProperties,
				30000, TestConstants.PORT9000, 100, new String[] { TestConstants.serviceName,
						TestConstants.serviceNameAlt, TestConstants.serviceNameSessionDisabled });

		client = new SCClient();
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
	}

	@After
	public void tearDown() throws Exception {
		try {
			client.detach();
		} catch (Exception e) {
		}
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
	public void createSession_onInitiallyDisabledService_clientEnablesServiceServerRegisters_SessionCreated()
			throws Exception {
		assertEquals(false, client.isServiceEnabled(TestConstants.serviceNameSessionDisabled));
		client.enableService(TestConstants.serviceNameSessionDisabled);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameSessionDisabled));
		
		ISessionService sessionService = client
				.newSessionService(TestConstants.serviceNameSessionDisabled);
		sessionService.createSession("sessionInfo", 300, 60);

		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameSessionDisabled));
		assertEquals(false, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

		client.disableService(TestConstants.serviceNameSessionDisabled);
	}

	@Test
	public void createSession_onInitiallyEnabledService_clientDisablesService_ThrowsException()
			throws Exception {
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
	public void createSession_onEnabledService_disableThenEnableAgain_createsSession()
			throws Exception {
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
