package org.serviceconnector.test.system.session;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.service.SCServiceException;

public class CreateSessionHttpClientTest {

	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(CreateSessionHttpClientTest.class);

	private static Process scProcess;
	private static Process srvProcess;

	private SCClient client;
	private Exception ex;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		// needed to init AppContext
		new SCSessionServer();
		ctrl = new ProcessesController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
			srvProcess = ctrl.startServer(TestConstants.sessionSrv, TestConstants.log4jSrvProperties,
					TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, new String[] { TestConstants.serviceName,
							TestConstants.serviceNameAlt });
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@Before
	public void setUp() throws Exception {
		client = new SCClient();
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		assertEquals("available/allocated sessions", "1000/0", client.workload(TestConstants.serviceName));
	}

	@After
	public void tearDown() throws Exception {
		assertEquals("available/allocated sessions", "1000/0", client.workload(TestConstants.serviceName));
		client.detach();
		client = null;
		ex = null;
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSC0Properties);
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		scProcess = null;
		srvProcess = null;
		ctrl = null;
	}

	@Test
	public void deleteSession_sessionServiceNameEmpty_passes() throws Exception {
		SCSessionService sessionService = client.newSessionService("");
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_sessionServiceNameWhiteSpace_passes() throws Exception {
		SCSessionService sessionService = client.newSessionService(" ");
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_sessionServiceNameSingleChar_passes() throws Exception {
		SCSessionService sessionService = client.newSessionService("a");
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_sessionServiceNamePangram_passes() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.pangram);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_sessionServiceNameDisabled_passes() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceNameSessionDisabled);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_emptySessionServiceName_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService("");
		try {
			sessionService.createSession("sessionInfo", 300, 60);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_whiteSpaceSessionServiceName_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(" ");
		try {
			sessionService.createSession("sessionInfo", 300, 60);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_arbitrarySessionServiceNameNotInSCProps_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService("The quick brown fox jumps over a lazy dog.");
		try {
			sessionService.createSession("sessionInfo", 300, 60);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_disabledService_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceNameSessionDisabled);
		try {
			sessionService.createSession("something", 300, 60);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		assertEquals(true, ex instanceof SCServiceException);
		sessionService.deleteSession();
	}

	@Test
	public void createSession_nullSessionInfo_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			sessionService.createSession(null, 300, 60);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		assertEquals(true, ex instanceof SCMPValidatorException);
		sessionService.deleteSession();
	}

	@Test
	public void createSession_emptySessionInfo_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			sessionService.createSession("", 300, 60);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		assertEquals(true, ex instanceof SCMPValidatorException);
		sessionService.deleteSession();
	}

	@Test
	public void createSession_whiteSpaceSessionInfo_sessionIdIsNotEmpty() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession(" ", 300, 60);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_arbitrarySpaceSessionInfo_sessionIdIsNotEmpty() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("The quick brown fox jumps over a lazy dog.", 300, 60);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_256LongSessionInfo_sessionIdIsNotEmpty() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 256; i++) {
			sb.append('a');
		}
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession(sb.toString(), 300, 60);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_257LongSessionInfo_throwsException() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 257; i++) {
			sb.append('a');
		}
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			sessionService.createSession(sb.toString(), 300, 60);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_beforeCreateSession_noSessionId() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_afterValidNewSessionService_noSessionId() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_whiteSpaceSessionInfo_noSessionId() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession(" ", 300, 60);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_twice_throwsExceptioin() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);
		try {
			sessionService.createSession("sessionInfo", 300, 60);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_twiceWithDifferentSessionServices_differentSessionIds() throws Exception {
		SCSessionService sessionService0 = client.newSessionService(TestConstants.serviceName);
		SCSessionService sessionService1 = client.newSessionService(TestConstants.serviceNameAlt);

		assertEquals(true, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		sessionService0.createSession("sessionInfo", 300, 60);

		assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		sessionService1.createSession("sessionInfo", 300, 60);

		assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		assertEquals(false, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		assertEquals(false, sessionService0.getSessionId().equals(sessionService1.getSessionId()));

		sessionService0.deleteSession();
		sessionService1.deleteSession();
	}

	@Test
	public void createSession_10000times_passes() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		for (int i = 0; i < 10000; i++) {
			if ((i % 500) == 0)
				testLogger.info("createSession_10000times cycle:\t" + i + " ...");
			sessionService.createSession("sessionInfo", 300, 10);
			assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
			sessionService.deleteSession();
			assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		}
	}

	@Test
	public void createSession_echoInterval0_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			sessionService.createSession("sessionInfo", 0, 10);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_echoIntervalMinus1_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			sessionService.createSession("sessionInfo", -1, 10);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_echoInterval1_sessionIdCreated() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 1, 10);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_echoIntervalIntMin_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			sessionService.createSession("sessionInfo", Integer.MIN_VALUE, 10);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_echoIntervalIntMax_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			sessionService.createSession("sessionInfo", Integer.MAX_VALUE, 10);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_echoInterval3600_sessionIdCreated() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 3600, 10);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

	}

	@Test
	public void createSession_echoInterval3601_sessionIdCreated() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			sessionService.createSession("sessionInfo", 3601, 10);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_timeout0_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			sessionService.createSession("sessionInfo", 300, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_timeoutMinus1_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			sessionService.createSession("sessionInfo", 300, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_timeout1_sessionIdCreated() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 1);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_timeoutIntMin_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			sessionService.createSession("sessionInfo", 300, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_timeoutIntMax_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			sessionService.createSession("sessionInfo", 300, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_timeout3600_sessionIdCreated() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 3600);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

	}

	@Test
	public void createSession_timeout3601_sessionIdCreated() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			sessionService.createSession("sessionInfo", 300, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_allInvalidParams_throwsSCMPValidatorException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			sessionService.createSession(null, -1, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_emptySessionServiceNameDataNull_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService("");
		try {
			sessionService.createSession("sessionInfo", 300, 10, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_whiteSpaceSessionServiceNameDataNull_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(" ");
		try {
			sessionService.createSession("sessionInfo", 300, 10, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_arbitrarySessionServiceNameNotInSCPropsDataNull_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService("The quick brown fox jumps over a lazy dog.");
		try {
			sessionService.createSession("sessionInfo", 300, 10, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test(expected = SCMPValidatorException.class)
	public void createSession_nullSessionInfoDataNull_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession(null, 300, 10, null);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test(expected = SCMPValidatorException.class)
	public void createSession_emptySessionInfoDataNull_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("", 300, 10, null);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_whiteSpaceSessionInfoDataNull_sessionIdIsNotEmpty() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession(" ", 300, 10, null);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_arbitrarySpaceSessionInfoDataNull_sessionIdIsNotEmpty() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("The quick brown fox jumps over a lazy dog.", 300, 10, null);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_256LongSessionInfoDataNull_sessionIdIsNotEmpty() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 256; i++) {
			sb.append('a');
		}
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession(sb.toString(), 300, 10, null);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_257LongSessionInfoDataNull_throwsException() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 257; i++) {
			sb.append('a');
		}
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			sessionService.createSession(sb.toString(), 300, 10, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_afterValidCreateSessionDataNull_noSessionId() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 10, null);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_whiteSpaceSessionInfoDataNull_noSessionId() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession(" ", 300, 10, null);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_twiceDataNull_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 10, null);
		try {
			sessionService.createSession("sessionInfo", 300, 60);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_twiceWithDifferentSessionServicesDataNull_differentSessionIds() throws Exception {
		SCSessionService sessionService0 = client.newSessionService(TestConstants.serviceName);
		SCSessionService sessionService1 = client.newSessionService(TestConstants.serviceNameAlt);

		assertEquals(true, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		sessionService0.createSession("sessionInfo", 300, 10, null);

		assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		sessionService1.createSession("sessionInfo", 300, 10, null);

		assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		assertEquals(false, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		assertEquals(false, sessionService0.getSessionId().equals(sessionService1.getSessionId()));

		sessionService0.deleteSession();
		sessionService1.deleteSession();
	}

	@Test
	public void createSession_1000timesDataNull_passes() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		for (int i = 0; i < 1000; i++) {
			if ((i % 500) == 0)
				testLogger.info("createSession_1000times cycle:\t" + i + " ...");
			sessionService.createSession("sessionInfo", 300, 10, null);
			assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
			sessionService.deleteSession();
			assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		}
	}

	@Test
	public void createSession_emptySessionServiceNameDataWhiteSpace_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService("");
		try {
			sessionService.createSession("sessionInfo", 300, 10, " ");
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_whiteSpaceSessionServiceNameDataWhiteSpace_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(" ");
		try {
			sessionService.createSession("sessionInfo", 300, 10, " ");
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_arbitrarySessionServiceNameNotInSCPropsDataWhiteSpace_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService("The quick brown fox jumps over a lazy dog.");
		try {
			sessionService.createSession("sessionInfo", 300, 10, " ");
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test(expected = SCMPValidatorException.class)
	public void createSession_nullSessionInfoDataWhiteSpace_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession(null, 300, 10, " ");
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test(expected = SCMPValidatorException.class)
	public void createSession_emptySessionInfoDataWhiteSpace_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("", 300, 10, " ");
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_whiteSpaceSessionInfoDataWhiteSpace_sessionIdIsNotEmpty() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession(" ", 300, 10, " ");
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_arbitrarySpaceSessionInfoDataWhiteSpace_sessionIdIsNotEmpty() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("The quick brown fox jumps over a lazy dog.", 300, 10, " ");
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_256LongSessionInfoDataWhiteSpace_sessionIdIsNotEmpty() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 256; i++) {
			sb.append('a');
		}
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession(sb.toString(), 300, 10, " ");
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_257LongSessionInfoDataWhiteSpace_throwsException() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 257; i++) {
			sb.append('a');
		}
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			sessionService.createSession(sb.toString(), 300, 10, " ");
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_afterValidCreateSessionDataWhiteSpace_noSessionId() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 10, " ");
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_whiteSpaceSessionInfoDataWhiteSpace_noSessionId() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession(" ", 300, 10, " ");
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_twiceDataWhiteSpace_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 10, " ");
		try {
			sessionService.createSession("sessionInfo", 300, 60);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_twiceWithDifferentSessionServicesDataWhiteSpace_differentSessionIds() throws Exception {
		SCSessionService sessionService0 = client.newSessionService(TestConstants.serviceName);
		SCSessionService sessionService1 = client.newSessionService(TestConstants.serviceNameAlt);

		assertEquals(true, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		sessionService0.createSession("sessionInfo", 300, 10, " ");

		assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		sessionService1.createSession("sessionInfo", 300, 10, " ");

		assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		assertEquals(false, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		assertEquals(false, sessionService0.getSessionId().equals(sessionService1.getSessionId()));

		sessionService0.deleteSession();
		sessionService1.deleteSession();
	}

	@Test
	public void createSession_1000timesDataWhiteSpace_passes() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		for (int i = 0; i < 1000; i++) {
			if ((i % 500) == 0)
				testLogger.info("createSession_1000times cycle:\t" + i + " ...");
			sessionService.createSession("sessionInfo", 300, 10, " ");
			assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
			sessionService.deleteSession();
			assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		}
	}

	@Test
	public void createSession_emptySessionServiceNameDataOneChar_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService("");
		try {
			sessionService.createSession("sessionInfo", 300, 10, "a");
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_whiteSpaceSessionServiceNameDataOneChar_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(" ");
		try {
			sessionService.createSession("sessionInfo", 300, 10, "a");
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_arbitrarySessionServiceNameNotInSCPropsDataOneChar_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService("The quick brown fox jumps over a lazy dog.");
		try {
			sessionService.createSession("sessionInfo", 300, 10, "a");
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test(expected = SCMPValidatorException.class)
	public void createSession_nullSessionInfoDataOneChar_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession(null, 300, 10, "a");
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test(expected = SCMPValidatorException.class)
	public void createSession_emptySessionInfoDataOneChar_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("", 300, 10, "a");
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_whiteSpaceSessionInfoDataOneChar_sessionIdIsNotEmpty() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession(" ", 300, 10, "a");
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_arbitrarySpaceSessionInfoDataOneChar_sessionIdIsNotEmpty() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("The quick brown fox jumps over a lazy dog.", 300, 10, "a");
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_256LongSessionInfoDataOneChar_sessionIdIsNotEmpty() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 256; i++) {
			sb.append('a');
		}
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession(sb.toString(), 300, 10, "a");
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_257LongSessionInfoDataOneChar_throwsException() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 257; i++) {
			sb.append('a');
		}
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			sessionService.createSession(sb.toString(), 300, 10, "a");
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_afterValidCreateSessionDataOneChar_noSessionId() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 10, "a");
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_whiteSpaceSessionInfoDataOneChar_noSessionId() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession(" ", 300, 10, "a");
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_twiceDataOneChar_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 10, "a");
		try {
			sessionService.createSession("sessionInfo", 300, 60);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_twiceWithDifferentSessionServicesDataOneChar_differentSessionIds() throws Exception {
		SCSessionService sessionService0 = client.newSessionService(TestConstants.serviceName);
		SCSessionService sessionService1 = client.newSessionService(TestConstants.serviceNameAlt);

		assertEquals(true, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		sessionService0.createSession("sessionInfo", 300, 10, "a");

		assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		sessionService1.createSession("sessionInfo", 300, 10, "a");

		assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		assertEquals(false, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		assertEquals(false, sessionService0.getSessionId().equals(sessionService1.getSessionId()));

		sessionService0.deleteSession();
		sessionService1.deleteSession();
	}

	@Test
	public void createSession_1000times_passes() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		for (int i = 0; i < 1000; i++) {
			if ((i % 500) == 0)
				testLogger.info("createSession_1000times cycle:\t" + i + " ...");
			sessionService.createSession("sessionInfo", 300, 10, "a");
			assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
			sessionService.deleteSession();
			assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		}
	}

	@Test
	public void createSession_emptySessionServiceNameData60kBByteArray_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService("");
		try {
			sessionService.createSession("sessionInfo", 300, 10, new byte[TestConstants.dataLength60kB]);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_whiteSpaceSessionServiceNameData60kBByteArray_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(" ");
		try {
			sessionService.createSession("sessionInfo", 300, 10, new byte[TestConstants.dataLength60kB]);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_arbitrarySessionServiceNameNotInSCPropsData60kBByteArray_throwsException()
			throws Exception {
		SCSessionService sessionService = client.newSessionService("The quick brown fox jumps over a lazy dog.");
		try {
			sessionService.createSession("sessionInfo", 300, 10, new byte[TestConstants.dataLength60kB]);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test(expected = SCMPValidatorException.class)
	public void createSession_nullSessionInfoData60kBByteArray_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession(null, 300, 10, new byte[TestConstants.dataLength60kB]);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test(expected = SCMPValidatorException.class)
	public void createSession_emptySessionInfoData60kBByteArray_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("", 300, 10, new byte[TestConstants.dataLength60kB]);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_whiteSpaceSessionInfoData60kBByteArray_sessionIdIsNotEmpty() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession(" ", 300, 10, new byte[TestConstants.dataLength60kB]);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_arbitrarySpaceSessionInfoData60kBByteArray_sessionIdIsNotEmpty() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("The quick brown fox jumps over a lazy dog.", 300, 10,
				new byte[TestConstants.dataLength60kB]);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_256LongSessionInfoData60kBByteArray_sessionIdIsNotEmpty() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession(TestConstants.stringLength256, 300, 10, new byte[TestConstants.dataLength60kB]);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_257LongSessionInfoData60kBByteArray_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			sessionService
					.createSession(TestConstants.stringLength257, 300, 10, new byte[TestConstants.dataLength60kB]);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_afterValidCreateSessionData60kBByteArray_noSessionId() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 10, new byte[TestConstants.dataLength60kB]);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_whiteSpaceSessionInfoData60kBByteArray_noSessionId() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession(" ", 300, 10, new byte[TestConstants.dataLength60kB]);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_twiceData60kBByteArray_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 10, new byte[TestConstants.dataLength60kB]);
		try {
			sessionService.createSession("sessionInfo", 300, 10, new byte[TestConstants.dataLength60kB]);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_twiceWithDifferentSessionServicesData60kBByteArray_differentSessionIds() throws Exception {
		SCSessionService sessionService0 = client.newSessionService(TestConstants.serviceName);
		SCSessionService sessionService1 = client.newSessionService(TestConstants.serviceNameAlt);

		assertEquals(true, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		sessionService0.createSession("sessionInfo", 300, 10, new byte[TestConstants.dataLength60kB]);

		assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		sessionService1.createSession("sessionInfo", 300, 10, new byte[TestConstants.dataLength60kB]);

		assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		assertEquals(false, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		assertEquals(false, sessionService0.getSessionId().equals(sessionService1.getSessionId()));

		sessionService0.deleteSession();
		sessionService1.deleteSession();
	}

	@Test
	public void createSession_1000timesData60kBByteArray_passes() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		for (int i = 0; i < 1000; i++) {
			if ((i % 100) == 0)
				testLogger.info("createSession_1000times cycle:\t" + i + " ...");
			sessionService.createSession("sessionInfo", 300, 10, new byte[TestConstants.dataLength60kB]);
			assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
			sessionService.deleteSession();
			assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		}
	}

	@Test
	public void createSession_1000SessionsAtOnce_acceptAllOfThem() throws Exception {
		int sessionsCount = 1000;
		String[] sessions = new String[sessionsCount];
		SCSessionService[] sessionServices = new SCSessionService[sessionsCount];
		for (int i = 0; i < sessionsCount; i++) {
			if ((i % 100) == 0)
				testLogger.info("createSession_1000times cycle:\t" + i + " ...");
			sessionServices[i] = client.newSessionService(TestConstants.serviceName);
			sessionServices[i].createSession("sessionInfo", 300, 10);
			sessions[i] = sessionServices[i].getSessionId();
		}
		for (int i = 0; i < sessionsCount; i++) {
			sessionServices[i].deleteSession();
			sessionServices[i] = null;
		}
		sessionServices = null;

		Arrays.sort(sessions);
		boolean duplicates = false;

		for (int i = 1; i < sessionsCount; i++) {
			if (sessions[i].equals(sessions[i - 1])) {
				duplicates = true;
				break;
			}
		}
		assertEquals(false, duplicates);
	}

	@Test
	public void createSession_1001SessionsAtOnce_exceedsConnectionsLimitThrowsException() throws Exception {
		int sessionsCount = 1001;
		int ctr = 0;
		String[] sessions = new String[sessionsCount];
		SCSessionService[] sessionServices = new SCSessionService[sessionsCount];
		try {
			for (int i = 0; i < sessionsCount; i++) {
				if ((i % 100) == 0)
					testLogger.info("createSession_1001times cycle:\t" + i + " ...");
				sessionServices[i] = client.newSessionService(TestConstants.serviceName);
				sessionServices[i].createSession("sessionInfo", 300, 10);
				sessions[i] = sessionServices[i].getSessionId();
				ctr++;
			}
		} catch (Exception e) {
			ex = e;
		}

		for (int i = 0; i < ctr; i++) {
			sessionServices[i].deleteSession();
			sessionServices[i] = null;
		}
		sessionServices = null;

		String[] successfulSessions = new String[ctr];
		System.arraycopy(sessions, 0, successfulSessions, 0, ctr);

		Arrays.sort(successfulSessions);
		boolean duplicates = false;

		for (int i = 1; i < ctr; i++) {
			if (successfulSessions[i].equals(successfulSessions[i - 1])) {
				duplicates = true;
				break;
			}
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(sessionsCount - 1, ctr);
		assertEquals(false, duplicates);
	}

	@Test
	public void createSession_overBothConnectionTypes_passes() throws Exception {
		SCClient client2 = new SCClient();
		((SCClient) client2).setConnectionType("netty.tcp");
		client2.attach(TestConstants.HOST, TestConstants.PORT_TCP);

		SCSessionService session1 = client.newSessionService(TestConstants.serviceName);
		SCSessionService session2 = client2.newSessionService(TestConstants.serviceName);

		session1.createSession("sessionInfo", 60, 10);
		session2.createSession("sessionInfo", 60, 10);

		assertEquals(false, session1.getSessionId().equals(session2.getSessionId()));

		session1.deleteSession();
		session2.deleteSession();

		assertEquals(session1.getSessionId(), session2.getSessionId());
		client2.detach();
		client2 = null;
	}

	@Test
	public void createSession_overBothConnectionTypesDifferentServices_passes() throws Exception {
		SCClient client2 = new SCClient();
		((SCClient) client2).setConnectionType("netty.tcp");
		client2.attach(TestConstants.HOST, TestConstants.PORT_TCP);

		SCSessionService session1 = client.newSessionService(TestConstants.serviceName);
		SCSessionService session2 = client2.newSessionService(TestConstants.serviceNameAlt);

		session1.createSession("sessionInfo", 60, 10);
		session2.createSession("sessionInfo", 60, 10);

		assertEquals(false, session1.getSessionId().equals(session2.getSessionId()));

		session1.deleteSession();
		session2.deleteSession();

		assertEquals(session1.getSessionId(), session2.getSessionId());
		client2.detach();
		client2 = null;
	}

	@Test
	public void sessionId_uniqueCheckFor10000IdsByOneClient_allSessionIdsAreUnique() throws Exception {
		int clientsCount = 10000;

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		String[] sessions = new String[clientsCount];

		for (int i = 0; i < clientsCount; i++) {
			if ((i % 500) == 0)
				testLogger.info("createSession_10000times cycle:\t" + i + " ...");
			sessionService.createSession("sessionInfo", 300, 60);
			sessions[i] = sessionService.getSessionId();
			sessionService.deleteSession();
		}

		Arrays.sort(sessions);
		boolean duplicates = false;

		for (int i = 1; i < clientsCount; i++) {
			if (sessions[i].equals(sessions[i - 1])) {
				duplicates = true;
				break;
			}
		}
		assertEquals(false, duplicates);
	}
}
