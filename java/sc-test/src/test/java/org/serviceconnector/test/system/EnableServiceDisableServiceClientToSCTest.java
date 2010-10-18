package org.serviceconnector.test.system;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.service.SCServiceException;

public class EnableServiceDisableServiceClientToSCTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(EnableServiceDisableServiceClientToSCTest.class);

	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	private static Process scProcess;
	private Process srvProcess;

	private SCClient client;
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
		client = new SCClient();
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		client.enableService(TestConstants.serviceNameSessionDisabled);
		srvProcess = ctrl.startServer(TestConstants.sessionSrv, TestConstants.log4jSrvProperties,
				TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, new String[] { TestConstants.serviceName,
						TestConstants.serviceNameAlt, TestConstants.serviceNameSessionDisabled });
		client.disableService(TestConstants.serviceNameSessionDisabled);
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

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, scMessage);

		assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_onInitiallyDisabledServiceThatIsEnabledByClient_sessionIsCreated() throws Exception {
		assertEquals(false, client.isServiceEnabled(TestConstants.serviceNameSessionDisabled));
		client.enableService(TestConstants.serviceNameSessionDisabled);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameSessionDisabled));

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceNameSessionDisabled);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, scMessage);

		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameSessionDisabled));
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

		client.disableService(TestConstants.serviceNameSessionDisabled);
	}

	@Test
	public void createSession_onInitiallyEnabledServiceThatIsDisabledByClient_throwsException() throws Exception {
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));

		client.disableService(TestConstants.serviceName);

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);

		try {
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 60, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, client.isServiceEnabled(TestConstants.serviceName));
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());

		client.enableService(TestConstants.serviceName);
	}

	@Test
	public void createSession_onEnabledServiceThatIsDisabledAndThenEnabledAgain_sessionIsCreated() throws Exception {
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));
		client.disableService(TestConstants.serviceName);
		client.enableService(TestConstants.serviceName);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, scMessage);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_onEnabledServiceThatIsDisabledAndThenEnabledAgainMultipleTimes_sessionIsCreated()
			throws Exception {
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));

		for (int i = 0; i < 1000; i++) {
			if ((i % 100) == 0)
				testLogger.info("EnabledDisableService_1000Times cycle:\t" + i + " ...");
			client.disableService(TestConstants.serviceName);
			client.enableService(TestConstants.serviceName);
		}
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, scMessage);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_multipleTimesOnEnabledServiceThatIsDisabledAndThenEnabledAgainMultipleTimes_sessionIsCreated()
			throws Exception {
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));

		for (int i = 0; i < 1000; i++) {
			if ((i % 100) == 0)
				testLogger.info("EnabledDisableService_1000Times cycle:\t" + i + " ...");
			client.disableService(TestConstants.serviceName);
			client.enableService(TestConstants.serviceName);

			SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 60, scMessage);
			assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
			sessionService.deleteSession();
		}
	}
}
