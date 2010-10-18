package org.serviceconnector.test.system.session;

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
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.service.SCServiceException;

public class ExecuteClientTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ExecuteClientTest.class);

	private static Process scProcess;
	private static Process srvProcess;

	private SCClient client;
	private Exception ex;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
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
		ctrl = null;
		scProcess = null;
		srvProcess = null;
	}

	@Test(expected = SCServiceException.class)
	public void execute_beforeCreateSession_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.execute(new SCMessage());
	}

	@Test
	public void execute_messageDataEmptyString_returnsTheSameMessageData() throws Exception {

		SCMessage message = new SCMessage("");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = sessionService.execute(message);
		sessionService.deleteSession();

		assertEquals(null, response.getData());
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageDataSingleChar_returnsTheSameMessageData() throws Exception {

		SCMessage message = new SCMessage("a");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = sessionService.execute(message);
		sessionService.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageDataArbitrary_returnsTheSameMessageData() throws Exception {

		SCMessage message = new SCMessage("The quick brown fox jumps over a lazy dog.");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = sessionService.execute(message);
		sessionService.deleteSession();

		assertEquals(message.getData(), response.getData());
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageData1MBArray_returnsTheSameMessageData() throws Exception {

		SCMessage message = new SCMessage(new byte[TestConstants.dataLength1MB]);

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = sessionService.execute(message);
		sessionService.deleteSession();

		assertEquals(((byte[]) message.getData()).length, ((byte[]) response.getData()).length);
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageWhiteSpaceMessageInfo_returnsTheSameMessage() throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(" ");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = sessionService.execute(message);
		sessionService.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSingleCharMessageInfo_returnsTheSameMessage() throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("a");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = sessionService.execute(message);
		sessionService.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageArbitraryMessageInfo_returnsTheSameMessage() throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = sessionService.execute(message);
		sessionService.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageCompressedTrue_returnsTheSameMessage() throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		message.setCompressed(true);

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = sessionService.execute(message);
		sessionService.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageCompressedFalse_returnsTheSameMessage() throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		message.setCompressed(false);

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = sessionService.execute(message);
		sessionService.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSessionIdEmptyString_returnsTheSameMessage() throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		((SCMessage) message).setSessionId("");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = sessionService.execute(message);
		String sessionId = sessionService.getSessionId();
		sessionService.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionId, response.getSessionId());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSessionIdWhiteSpaceString_returnsTheSameMessage() throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		((SCMessage) message).setSessionId(" ");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = sessionService.execute(message);
		String sessionId = sessionService.getSessionId();
		sessionService.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionId, response.getSessionId());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSessionIdSingleChar_returnsTheSameMessage() throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		((SCMessage) message).setSessionId("a");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = sessionService.execute(message);
		String sessionId = sessionService.getSessionId();
		sessionService.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionId, response.getSessionId());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSessionIdArbitraryString_returnsTheSameMessage() throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		((SCMessage) message).setSessionId("The quick brown fox jumps over a lazy dog.");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = sessionService.execute(message);
		String sessionId = sessionService.getSessionId();
		sessionService.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionId, response.getSessionId());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSessionIdLikeSessionIdString_returnsCorrectSessionId() throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		((SCMessage) message).setSessionId("aaaa0000-bb11-cc22-dd33-eeeeee444444");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = sessionService.execute(message);
		String sessionId = sessionService.getSessionId();
		sessionService.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionId, response.getSessionId());
		assertEquals(false, message.getSessionId().equals(response.getSessionId()));
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSessionIdSetManually_returnsTheSameMessage() throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		((SCMessage) message).setSessionId(sessionService.getSessionId());

		SCMessage response = sessionService.execute(message);
		String sessionId = sessionService.getSessionId();
		sessionService.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionId, response.getSessionId());
		assertEquals(message.getSessionId(), response.getSessionId());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSessionIdSetToIdOfDifferentSessionSameServiceThanExecuting_returnsCorrectSessionId()
			throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		SCSessionService sessionService0 = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService0.createSession(300, 60, message);

		SCSessionService sessionService1 = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService1.createSession(300, 60, message);

		((SCMessage) message).setSessionId(sessionService1.getSessionId());

		SCMessage response = sessionService0.execute(message);
		String sessionId0 = sessionService0.getSessionId();
		sessionService0.deleteSession();
		sessionService1.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionId0, response.getSessionId());
		assertEquals(false, message.getSessionId().equals(response.getSessionId()));
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSessionIdSetToIdOfDifferentSessionServiceThanExecuting_returnsCorrectSessionId()
			throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		SCSessionService sessionService0 = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService0.createSession(300, 60, message);

		SCSessionService sessionService1 = client.newSessionService(TestConstants.serviceNameAlt);
		message.setSessionInfo("sessionInfo");
		sessionService1.createSession(300, 60, message);

		((SCMessage) message).setSessionId(sessionService1.getSessionId());

		SCMessage response = sessionService0.execute(message);
		String sessionId0 = sessionService0.getSessionId();
		sessionService0.deleteSession();
		sessionService1.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionId0, response.getSessionId());
		assertEquals(false, message.getSessionId().equals(response.getSessionId()));
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_timeout1_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = sessionService.execute(message, 1);

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionService.getSessionId(), response.getSessionId());
		assertEquals(message.isFault(), response.isFault());

		sessionService.deleteSession();
	}

	@Test
	public void execute_timeout2_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = sessionService.execute(message, 2);

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionService.getSessionId(), response.getSessionId());
		assertEquals(message.isFault(), response.isFault());

		sessionService.deleteSession();
	}

	@Test
	public void execute_timeout0_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = null;
		try {
			response = sessionService.execute(message, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(null, response);

		sessionService.deleteSession();
	}

	@Test
	public void execute_timeoutMinus1_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = null;
		try {
			response = sessionService.execute(message, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(null, response);

		sessionService.deleteSession();
	}

	@Test
	public void execute_timeoutIntMin_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = null;
		try {
			response = sessionService.execute(message, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(null, response);

		sessionService.deleteSession();
	}

	@Test
	public void execute_timeoutIntMax_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = null;
		try {
			response = sessionService.execute(message, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(null, response);

		sessionService.deleteSession();
	}

	@Test
	public void execute_timeoutAllowedMax_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = sessionService.execute(message, 3600);

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
		sessionService.deleteSession();
	}

	@Test
	public void execute_timeoutAllowedMaxPlus1_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = null;
		try {
			response = sessionService.execute(message, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(null, response);

		sessionService.deleteSession();
	}

	@Test
	public void execute_timeout1_passes() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = sessionService.execute(message, 1);

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
		sessionService.deleteSession();
	}

	@Test
	public void execute_timeout2_passes() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = sessionService.execute(message, 2);

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
		sessionService.deleteSession();
	}

	@Test
	public void execute_timeoutMaxAllowed_passes() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = sessionService.execute(message, 3600);

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
		sessionService.deleteSession();
	}

	@Test
	public void execute_timeoutMaxAllowedPlus1_throwsSCMPValidatorException() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		try {
			sessionService.execute(message, 3601);
		} catch (Exception e) {
			ex = e;
		}

		assertEquals(true, ex instanceof SCMPValidatorException);
		sessionService.deleteSession();
	}

	@Test
	public void execute_timeoutIntMax_throwsSCMPValidatorException() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		try {
			sessionService.execute(message, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}

		assertEquals(true, ex instanceof SCMPValidatorException);
		sessionService.deleteSession();
	}

	@Test
	public void execute_timeoutIntMin_throwsSCMPValidatorException() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		try {
			sessionService.execute(message, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}

		assertEquals(true, ex instanceof SCMPValidatorException);
		sessionService.deleteSession();
	}

	@Test
	public void execute_timeout0_throwsSCMPValidatorException() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		try {
			sessionService.execute(message, 0);
		} catch (Exception e) {
			ex = e;
		}

		assertEquals(true, ex instanceof SCMPValidatorException);
		sessionService.deleteSession();
	}

	@Test
	public void execute_timeoutMinus1_throwsSCMPValidatorException() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		try {
			sessionService.execute(message, -1);
		} catch (Exception e) {
			ex = e;
		}

		assertEquals(true, ex instanceof SCMPValidatorException);
		sessionService.deleteSession();
	}

	@Test
	public void execute_timeoutExpiresOnServer_throwsException() throws Exception {
		SCMessage message = new SCMessage();
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		try {
			sessionService.execute(new SCMessage("timeout 4000"), 2);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		sessionService.deleteSession();
	}

	@Test
	public void execute_timeoutCloselyExpires_throwsException() throws Exception {
		SCMessage message = new SCMessage();
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		try {
			sessionService.execute(new SCMessage("timeout 2000"), 2);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		sessionService.deleteSession();
	}

	@Test
	public void execute_timeoutIsEnough_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("timeout 1500");

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		message.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 60, message);

		SCMessage response = sessionService.execute(message, 2);
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
		sessionService.deleteSession();
	}
}
