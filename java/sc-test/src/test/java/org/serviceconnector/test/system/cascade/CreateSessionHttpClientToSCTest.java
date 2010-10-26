package org.serviceconnector.test.system.cascade;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.service.SCServiceException;

public class CreateSessionHttpClientToSCTest {

	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(CreateSessionHttpClientToSCTest.class);

	private static Process sc0Process;
	private static Process scCascadedProcess;
	private static Process srvProcess;

	private int threadCount = 0;
	private SCClient client;
	private Exception ex;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
		try {
			sc0Process = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
			scCascadedProcess = ctrl.startSC(TestConstants.log4jSC1Properties, TestConstants.scPropertiesCascaded);
			srvProcess = ctrl.startServer(TestConstants.sessionSrv, TestConstants.log4jSrvProperties,
					TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, new String[] { TestConstants.serviceName,
							TestConstants.serviceNameAlt });
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@Before
	public void setUp() throws Exception {
		threadCount = Thread.activeCount();
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
		assertEquals("number of threads", threadCount, Thread.activeCount());
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		ctrl.stopProcess(sc0Process, TestConstants.log4jSC0Properties);
		ctrl.stopProcess(scCascadedProcess, TestConstants.log4jSC1Properties);
		srvProcess = null;
		sc0Process = null;
		scCascadedProcess = null;
		ctrl = null;
	}

	/**
	 * Description: Create new session with empty session name and delete the session.<br>
	 * Expectation:	Session is deleted
	 */
	@Test
	public void deleteSession_1() throws Exception {
		SCSessionService sessionService = client.newSessionService("");
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create new session with blank string as session name and delete the session.<br>
	 * Expectation:	Session is deleted
	 */
	@Test
	public void deleteSession_2() throws Exception {
		SCSessionService sessionService = client.newSessionService(" ");
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create new session with session name "a" (SingleChar) and delete the session.<br>
	 * Expectation:	Session is deleted
	 */
	@Test
	public void deleteSession_3() throws Exception {
		SCSessionService sessionService = client.newSessionService("a");
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create new session with invalid name and delete the session.<br>
	 * Expectation:	Session is deleted
	 */
	@Test
	public void deleteSession_4() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.pangram);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create new session with service name and delete the session.<br>
	 * Expectation:	Session is deleted
	 */
	@Test
	public void deleteSession_5() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceNameSessionDisabled);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create new session with empty session service name.<br>
	 * Expectation:	throws service exception
	 */
	@Test
	public void createSession_1() throws Exception {
		SCSessionService sessionService = client.newSessionService("");
		try {
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	
	/**
	 * Description: Create new session with empty session service name and message (SCMessage) with white space.<br>
	 * Expectation:	throws service exception
	 */
	@Test
	public void createSession_1_1() throws Exception {
		SCSessionService sessionService = client.newSessionService("");
		try {
			SCMessage scMessage = new SCMessage(" ");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}
	
	/**
	 * Description: Create new session with white space session service name and message (SCMessage) with white space.<br>
	 * Expectation:	throws service exception
	 */
	@Test
	public void createSession_1_2() throws Exception {
		SCSessionService sessionService = client.newSessionService(" ");
		try {
			SCMessage scMessage = new SCMessage(" ");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}
	
	/**
	 * Description: Create new session with arbitrary session service name and message (SCMessage) with white space.<br>
	 * Expectation:	throws service exception
	 */
	@Test
	public void createSession_1_3() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.pangram);
		try {
			SCMessage scMessage = new SCMessage(" ");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}
	
	
	/**
	 * Description: Create new session service with blank string (session name) and new session.<br>
	 * Expectation:	throws service exception
	 */
	@Test
	public void createSession_2() throws Exception {
		SCSessionService sessionService = client.newSessionService(" ");
		try {
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create new session service with invalid name and new session.<br>
	 * Expectation:	throws service exception
	 */
	@Test
	public void createSession_3() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.pangram);
		try {
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create new session service with valid name and new session.<br>
	 * Expectation:	throws service exception
	 */
	@Test
	public void createSession_4() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceNameSessionDisabled);
		try {
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("something");
			sessionService.createSession(300, 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		assertEquals(true, ex instanceof SCServiceException);
		sessionService.deleteSession();
	}

	/**
	 * Description: Create new session service with valid name and new session with empty session info.<br>
	 * Expectation:	throws SCMPValidator exception
	 */
	@Test
	public void createSessionInfo_1() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("");
			sessionService.createSession(300, 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		assertEquals(true, ex instanceof SCMPValidatorException);
		sessionService.deleteSession();
	}

	/**
	 * Description: Create new session service with valid name and new session with blank string as session info.<br>
	 * Expectation:	Session service isn't created.
	 */
	@Test
	public void createSessionInfo_1_1() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo(" ");
		sessionService.createSession(300, 10, scMessage);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create new session service with valid name and new session with arbitrary string as session info.<br>
	 * Expectation:	Session service isn't created.
	 */
	@Test
	public void createSessionInfo_1_3() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo(TestConstants.pangram);
		sessionService.createSession(300, 10, scMessage);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create new session service with valid name and new session with long (256 Bytes) session info.<br>
	 * Expectation:	Session service isn't created.
	 */
	@Test
	public void createSessionInfo_1_4() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 256; i++) {
			sb.append('a');
		}
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo(sb.toString());
		sessionService.createSession(300, 10, scMessage);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create new session service with valid name and new session with long (257 Bytes) session info.<br>
	 * Expectation:	throws SCMPValidator exception
	 */
	@Test
	public void createSessionInfo_1_5() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 257; i++) {
			sb.append('a');
		}
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo(sb.toString());
			sessionService.createSession(300, 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create new session service with white space name and new session with empty session info.<br>
	 * Expectation:	throws SCMPValidator exception
	 */
	@Test
	public void createSessionInfo_2() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			SCMessage scMessage = new SCMessage(" ");
			scMessage.setSessionInfo("");
			sessionService.createSession(300, 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create new session service with white space name and new session with white space session info.<br>
	 * Expectation:	throws SCMPValidator exception
	 */
	@Test
	public void createSessionInfo_2_1() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage(" ");
		scMessage.setSessionInfo(" ");
		sessionService.createSession(300, 10, scMessage);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create new session service with white spaces name and new session with arbitrary string as session info.<br>
	 * Expectation:	Session service isn't created.
	 */
	@Test
	public void createSessionInfo_2_2() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage(" ");
		scMessage.setSessionInfo(TestConstants.pangram);
		sessionService.createSession(300, 10, scMessage);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create new session service with white spaces name and new session with long (256 Bytes) session info.<br>
	 * Expectation:	Session service isn't created.
	 */
	@Test
	public void createSessionInfo_2_3() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 256; i++) {
			sb.append('a');
		}
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage(" ");
		scMessage.setSessionInfo(sb.toString());
		sessionService.createSession(300, 10, scMessage);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}
	
	/**
	 * Description: Create new session service with SC-message and delete the session.<br>
	 * Expectation:	Session is deleted
	 */
	@Test
	public void deleteSessionService_1() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 10, scMessage);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create new session service with SC-message (blank string as session info) and delete the session.<br>
	 * Expectation:	Session is deleted
	 */
	@Test
	public void deleteSessionService_1_1() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo(" ");
		sessionService.createSession(300, 10, scMessage);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create new session service with SC-message (white space as message name) and delete the session.<br>
	 * Expectation:	Session is deleted
	 */
@Test
	public void deleteSessionService_2() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage(" ");
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 10, scMessage);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create new session service with SC-message (white space as message name and as Info-Data) and delete the session.<br>
	 * Expectation:	Session is deleted
	 */
	@Test
	public void deleteSessionService_2_1() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage(" ");
		scMessage.setSessionInfo(" ");
		sessionService.createSession(300, 10, scMessage);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create new session service with valid name and two sessions with the same SCMessage.<br>
	 * Expectation:	throws SC-Service exception
	 */
	@Test
	public void createSession_10() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 10, scMessage);
		try {
			sessionService.createSession(300, 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create new session service with valid name and two sessions with the same SessionInfo.<br>
	 * Expectation:	throws SC-Service exception
	 */
	@Test
	public void createSession_10_1() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage0 = new SCMessage();
		scMessage0.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 10, scMessage0);
		try {
			SCMessage scMessage1 = new SCMessage();
			scMessage1.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	
	/**
	 * Description: Create two new session service with any one session (different session id's).<br>
	 * Expectation:	two sessions are created
	 */
	@Test
	public void createSession_11() throws Exception {
		SCSessionService sessionService0 = client.newSessionService(TestConstants.serviceName);
		SCSessionService sessionService1 = client.newSessionService(TestConstants.serviceNameAlt);

		assertEquals(true, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		SCMessage scMessage0 = new SCMessage();
		scMessage0.setSessionInfo("sessionInfo");
		sessionService0.createSession(300, 10, scMessage0);

		assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		SCMessage scMessage1 = new SCMessage();
		scMessage1.setSessionInfo("sessionInfo");
		sessionService1.createSession(300, 10, scMessage1);

		assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		assertEquals(false, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		assertEquals(false, sessionService0.getSessionId().equals(sessionService1.getSessionId()));

		sessionService0.deleteSession();
		sessionService1.deleteSession();
	}

	/**
	 * Description: Create two new session service with any one session (different session id's). The name of the messages are  message are white space. <br>
	 * Expectation:	two sessions are created
	 */
	@Test
	public void createSession_11_1() throws Exception {
		SCSessionService sessionService0 = client.newSessionService(TestConstants.serviceName);
		SCSessionService sessionService1 = client.newSessionService(TestConstants.serviceNameAlt);

		assertEquals(true, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		SCMessage scMessage0 = new SCMessage(" ");
		scMessage0.setSessionInfo("sessionInfo");
		sessionService0.createSession(300, 10, scMessage0);

		assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		SCMessage scMessage1 = new SCMessage(" ");
		scMessage1.setSessionInfo("sessionInfo");
		sessionService1.createSession(300, 10, scMessage1);

		assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		assertEquals(false, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		assertEquals(false, sessionService0.getSessionId().equals(sessionService1.getSessionId()));

		sessionService0.deleteSession();
		sessionService1.deleteSession();
	}


	/**
	 * Description: Create and delete 10'000 times one session service with one session.<br>
	 * Expectation:	all session service are created and deleted.
	 */	
	@Test
	public void createSession_12() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		for (int i = 0; i < 1000; i++) {
			if ((i % 100) == 0)
				testLogger.info("createSession_12 cycle:\t" + i + " ...");
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
			assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
			sessionService.deleteSession();
			assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		}
	}

	/**
	 * Description: Create one session service with echoInterval 0.<br>
	 * Expectation:	throws SCMP-Validator exception
	 */	
	@Test
	public void echoInterval_1() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(0, 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create one session service with negative echoInterval (-1).<br>
	 * Expectation:	throws SCMP-Validator exception
	 */	
	@Test
	public void echoInterval_2() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(-1, 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create one session service with echoInterval 1.<br>
	 * Expectation:	Session service was created and delete.
	 */	
	@Test
	public void echoInterval_3() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(1, 10, scMessage);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create one session service with to small negative echoInterval.<br>
	 * Expectation:	throws SCMP-Validator exception
	 */	
	@Test
	public void echoInterval_4() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(Integer.MIN_VALUE, 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create one session service with to big echoInterval.<br>
	 * Expectation:	throws SCMP-Validator exception
	 */	
	@Test
	public void echoInterval_5() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(Integer.MAX_VALUE, 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create one session service with echoInterval 1 hour.<br>
	 * Expectation:	Session service was created and delete.
	 */	
	@Test
	public void echoInterval_6() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(3600, 10, scMessage);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

	}

	/**
	 * Description: Create one session service with echoInterval 1 hour and 1 second.<br>
	 * Expectation:	throws SCMP-Validator exception
	 */	
	@Test
	public void echoInterval_7() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(3601, 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create one session service with timeout 0 second.<br>
	 * Expectation:	throws SCMP-Validator exception
	 */	
	@Test
	public void timeout_1() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 0, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create one session service with timeout -1 second.<br>
	 * Expectation:	throws SCMP-Validator exception
	 */	
	@Test
	public void timeout_2() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, -1, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create one session service with timeout 1 second.<br>
	 * Expectation:	Session service was created and delete.
	 */	
	@Test
	public void timeout_3() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 1, scMessage);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create one session service with to small negative timeout.<br>
	 * Expectation:	throws SCMP-Validator exception
	 */	
	@Test
	public void timeout_4() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, Integer.MIN_VALUE, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

		
	/**
	 * Description: Create one session service with to big negative timeout.<br>
	 * Expectation:	throws SCMP-Validator exception
	 */	
	@Test
	public void timeout_5() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, Integer.MAX_VALUE, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create one session service with timeout 1 hour.<br>
	 * Expectation:	Session service was created and delete.
	 */		@Test
	public void timeout_6() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 3600, scMessage);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

	}

	/**
	 * Description: Create one session service with timeout 1 hour and 1 second.<br>
	 * Expectation:	throws SCMP-Validator exception
	 */	
	@Test
	public void timeout_7() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 3601, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create one session service with invalid parameters.<br>
	 * Expectation:	throws SCMP-Validator exception
	 */	
	@Test
	public void createSession_13() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			sessionService.createSession(-1, -1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}






	





	@Test
	public void createSession_twiceDataWhiteSpace_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage(" ");
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 10, scMessage);
		try {
			SCMessage scMessage1 = new SCMessage();
			scMessage1.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}


	@Test
	public void createSession_1000timesDataWhiteSpace_passes() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		for (int i = 0; i < 1000; i++) {
			if ((i % 100) == 0)
				testLogger.info("createSession_1000times cycle:\t" + i + " ...");
			SCMessage scMessage = new SCMessage(" ");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
			assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
			sessionService.deleteSession();
			assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		}
	}

	@Test
	public void createSession_emptySessionServiceNameDataOneChar_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService("");
		try {
			SCMessage scMessage = new SCMessage("a");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
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
			SCMessage scMessage = new SCMessage("a");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_arbitrarySessionServiceNameNotInSCPropsDataOneChar_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.pangram);
		try {
			SCMessage scMessage = new SCMessage("a");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test(expected = SCMPValidatorException.class)
	public void createSession_emptySessionInfoDataOneChar_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage("a");
		scMessage.setSessionInfo("");
		sessionService.createSession(300, 10, scMessage);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_whiteSpaceSessionInfoDataOneChar_sessionIdIsNotEmpty() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage("a");
		scMessage.setSessionInfo(" ");
		sessionService.createSession(300, 10, scMessage);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_arbitrarySpaceSessionInfoDataOneChar_sessionIdIsNotEmpty() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage("a");
		scMessage.setSessionInfo(TestConstants.pangram);
		sessionService.createSession(300, 10, scMessage);
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
		SCMessage scMessage = new SCMessage("a");
		scMessage.setSessionInfo(sb.toString());
		sessionService.createSession(300, 10, scMessage);
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
			SCMessage scMessage = new SCMessage("a");
			scMessage.setSessionInfo(sb.toString());
			sessionService.createSession(300, 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_afterValidCreateSessionDataOneChar_noSessionId() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage("a");
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 10, scMessage);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_whiteSpaceSessionInfoDataOneChar_noSessionId() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage("a");
		scMessage.setSessionInfo(" ");
		sessionService.createSession(300, 10, scMessage);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_twiceDataOneChar_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage("a");
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 10, scMessage);
		try {
			SCMessage scMessage1 = new SCMessage();
			scMessage1.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage1);
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

		SCMessage scMessage0 = new SCMessage("a");
		scMessage0.setSessionInfo("sessionInfo");
		sessionService0.createSession(300, 10, scMessage0);

		assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		SCMessage scMessage1 = new SCMessage("a");
		scMessage1.setSessionInfo("sessionInfo");
		sessionService1.createSession(300, 10, scMessage1);

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
			if ((i % 100) == 0)
				testLogger.info("createSession_1000times cycle:\t" + i + " ...");
			SCMessage scMessage = new SCMessage("a");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
			assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
			sessionService.deleteSession();
			assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		}
	}

	@Test
	public void createSession_emptySessionServiceNameData60kBByteArray_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService("");
		try {
			SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
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
			SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
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
		SCSessionService sessionService = client.newSessionService(TestConstants.pangram);
		try {
			SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test(expected = SCMPValidatorException.class)
	public void createSession_emptySessionInfoData60kBByteArray_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
		scMessage.setSessionInfo("");
		sessionService.createSession(300, 10, scMessage);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_whiteSpaceSessionInfoData60kBByteArray_sessionIdIsNotEmpty() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
		scMessage.setSessionInfo(" ");
		sessionService.createSession(300, 10, scMessage);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_arbitrarySpaceSessionInfoData60kBByteArray_sessionIdIsNotEmpty() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
		scMessage.setSessionInfo(TestConstants.pangram);
		sessionService.createSession(300, 10, scMessage);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_256LongSessionInfoData60kBByteArray_sessionIdIsNotEmpty() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
		scMessage.setSessionInfo(TestConstants.stringLength256);
		sessionService.createSession(300, 10, scMessage);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void deleteSession_afterValidCreateSessionData60kBByteArray_noSessionId() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 10, scMessage);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_whiteSpaceSessionInfoData60kBByteArray_noSessionId() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
		scMessage.setSessionInfo(" ");
		sessionService.createSession(300, 10, scMessage);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_twiceData60kBByteArray_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 10, scMessage);
		try {
			sessionService.createSession(300, 10, scMessage);
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

		SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
		scMessage.setSessionInfo("sessionInfo");
		sessionService0.createSession(300, 10, scMessage);

		assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		SCMessage scMessage1 = new SCMessage(new byte[TestConstants.dataLength60kB]);
		scMessage1.setSessionInfo("sessionInfo");
		sessionService1.createSession(300, 10, scMessage1);

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
			SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
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
			sessionServices[i].createSession(60, 10, new SCMessage());
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
				sessionServices[i].createSession(300, 10, new SCMessage());
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

		session1.createSession(60, 10, new SCMessage());
		session2.createSession(60, 10, new SCMessage());

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

		session1.createSession(60, 10, new SCMessage());
		session2.createSession(60, 10, new SCMessage());

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
			sessionService.createSession(300, 60, new SCMessage());
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
