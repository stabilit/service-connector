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
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.ctrl.util.TestEnvironmentController;
import org.serviceconnector.sc.service.SCServiceException;
import org.serviceconnector.service.ISCMessage;
import org.serviceconnector.service.SCMessage;



public class ExecuteClientToSCTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ExecuteClientToSCTest.class);

	private static Process scProcess;
	private static Process srvProcess;

	private int threadCount = 0;
	private ISCClient client;
	private Exception ex;

	private static TestEnvironmentController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
			srvProcess = ctrl.startServer(TestConstants.sessionSrv, TestConstants.log4jSrvProperties, 30000, TestConstants.PORT9000, 100, new String[] {TestConstants.serviceName, TestConstants.serviceNameAlt});
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@Before
	public void setUp() throws Exception {
		threadCount = Thread.activeCount();
		client = new SCClient();
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
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
		ctrl.stopProcess(scProcess, TestConstants.log4jSC0Properties);
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		ctrl = null;
		scProcess = null;
		srvProcess = null;
	}

	@Test(expected = SCServiceException.class)
	public void execute_beforeCreateSession_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.execute(new SCMessage());
	}

	@Test
	public void execute_messageDataNull_returnsTheSameMessageData() throws Exception {

		ISCMessage message = new SCMessage(null);

		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		sessionService.deleteSession();
		
		assertEquals(message.getData(), response.getData());
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageDataEmptyString_returnsTheSameMessageData() throws Exception {

		ISCMessage message = new SCMessage("");

		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		sessionService.deleteSession();
		
		assertEquals(null, response.getData());
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageDataSingleChar_returnsTheSameMessageData() throws Exception {

		ISCMessage message = new SCMessage("a");

		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		sessionService.deleteSession();
		
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageDataArbitrary_returnsTheSameMessageData() throws Exception {

		ISCMessage message = new SCMessage("The quick brown fox jumps over a lazy dog.");

		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		sessionService.deleteSession();
		
		assertEquals(message.getData(), response.getData());
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageData1MBArray_returnsTheSameMessageData() throws Exception {

		ISCMessage message = new SCMessage(new byte[TestConstants.dataLength1MB]);

		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		sessionService.deleteSession();
		
		assertEquals(((byte[])message.getData()).length, ((byte[])response.getData()).length);
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageWhiteSpaceMessageInfo_returnsTheSameMessage() throws Exception {

		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(" ");

		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		sessionService.deleteSession();
		
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSingleCharMessageInfo_returnsTheSameMessage() throws Exception {

		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("a");

		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		sessionService.deleteSession();
		
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageArbitraryMessageInfo_returnsTheSameMessage() throws Exception {

		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		sessionService.deleteSession();
		
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageCompressedTrue_returnsTheSameMessage() throws Exception {

		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		message.setCompressed(true);

		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		sessionService.deleteSession();
		
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageCompressedFalse_returnsTheSameMessage() throws Exception {

		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		message.setCompressed(false);

		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		sessionService.deleteSession();
		
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSessionIdEmptyString_returnsTheSameMessage() throws Exception {

		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		((SCMessage) message).setSessionId("");

		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		sessionService.deleteSession();
		
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionService.getSessionId(), response.getSessionId());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSessionIdWhiteSpaceString_returnsTheSameMessage() throws Exception {

		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		((SCMessage) message).setSessionId(" ");

		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		sessionService.deleteSession();
		
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionService.getSessionId(), response.getSessionId());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSessionIdSingleChar_returnsTheSameMessage() throws Exception {

		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		((SCMessage) message).setSessionId("a");

		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		sessionService.deleteSession();
		
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionService.getSessionId(), response.getSessionId());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSessionIdArbitraryString_returnsTheSameMessage() throws Exception {

		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		((SCMessage) message).setSessionId("The quick brown fox jumps over a lazy dog.");

		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		sessionService.deleteSession();
		
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionService.getSessionId(), response.getSessionId());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSessionIdLikeSessionIdString_returnsCorrectSessionId()
			throws Exception {

		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		((SCMessage) message).setSessionId("aaaa0000-bb11-cc22-dd33-eeeeee444444");

		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		sessionService.deleteSession();
		
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionService.getSessionId(), response.getSessionId());
		assertEquals(false, message.getSessionId().equals(response.getSessionId()));
		assertEquals(message.isFault(), response.isFault());
	}
	
	@Test
	public void execute_messageSessionIdSetManually_returnsTheSameMessage()
			throws Exception {

		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		((SCMessage) message).setSessionId(sessionService.getSessionId());

		ISCMessage response = sessionService.execute(message);
		sessionService.deleteSession();
		
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionService.getSessionId(), response.getSessionId());
		assertEquals(message.getSessionId(), response.getSessionId());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSessionIdSetToIdOfDifferentSessionSameServiceThanExecuting_returnsCorrectSessionId()
			throws Exception {

		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		ISessionService sessionService0 = client.newSessionService(TestConstants.serviceName);
		sessionService0.createSession("sessionInfo", 300, 60);

		ISessionService sessionService1 = client.newSessionService(TestConstants.serviceName);
		sessionService1.createSession("sessionInfo", 300, 60);
		
		((SCMessage) message).setSessionId(sessionService1.getSessionId());

		ISCMessage response = sessionService0.execute(message);
		sessionService0.deleteSession();
		sessionService1.deleteSession();
		
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionService0.getSessionId(), response.getSessionId());
		assertEquals(false, message.getSessionId().equals(response.getSessionId()));
		assertEquals(message.isFault(), response.isFault());
	}
	
	@Test
	public void execute_messageSessionIdSetToIdOfDifferentSessionServiceThanExecuting_returnsCorrectSessionId()
			throws Exception {

		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		ISessionService sessionService0 = client.newSessionService(TestConstants.serviceName);
		sessionService0.createSession("sessionInfo", 300, 60);

		ISessionService sessionService1 = client.newSessionService(TestConstants.serviceNameAlt);
		sessionService1.createSession("sessionInfo", 300, 60);
		
		((SCMessage) message).setSessionId(sessionService1.getSessionId());

		ISCMessage response = sessionService0.execute(message);
		sessionService0.deleteSession();
		sessionService1.deleteSession();
		
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionService0.getSessionId(), response.getSessionId());
		assertEquals(false, message.getSessionId().equals(response.getSessionId()));
		assertEquals(message.isFault(), response.isFault());
	}
	
	@Test
	public void execute_timeout1_returnsSameMessage() throws Exception {
		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message, 1);
		
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionService.getSessionId(), response.getSessionId());
		assertEquals(message.isFault(), response.isFault());
		
		sessionService.deleteSession();
	}
	
	@Test
	public void execute_timeout2_returnsSameMessage() throws Exception {
		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message, 2);
		
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionService.getSessionId(), response.getSessionId());
		assertEquals(message.isFault(), response.isFault());
		
		sessionService.deleteSession();
	}
	
	@Test
	public void execute_timeout0_returnsSameMessage() throws Exception {
		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = null;
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
		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = null;
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
		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = null;
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
		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = null;
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
		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message, 3600);
		
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
		sessionService.deleteSession();
	}
	
	@Test
	public void execute_timeoutAllowedMaxPlus1_returnsSameMessage() throws Exception {
		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = null;
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
		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message, 1);
		
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
		sessionService.deleteSession();
	}
	
	@Test
	public void execute_timeout2_passes() throws Exception {
		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message, 2);
		
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
		sessionService.deleteSession();
	}
	
	@Test
	public void execute_timeoutMaxAllowed_passes() throws Exception {
		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message, 3600);
		
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
		sessionService.deleteSession();
	}
	
	@Test
	public void execute_timeoutMaxAllowedPlus1_throwsSCMPValidatorException() throws Exception {
		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

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
		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

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
		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

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
		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

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
		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

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

		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

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

		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

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
		ISCMessage message = new SCMessage("timeout 1500");
		
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message, 2);
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
		sessionService.deleteSession();
	}
}
