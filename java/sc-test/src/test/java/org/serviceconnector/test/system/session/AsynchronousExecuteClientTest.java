package org.serviceconnector.test.system.session;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.cln.ISCClient;
import org.serviceconnector.api.cln.IService;
import org.serviceconnector.api.cln.ISessionService;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.service.SCServiceException;

public class AsynchronousExecuteClientTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger
			.getLogger(AsynchronousExecuteClientTest.class);

	private static Process scProcess;
	private static Process srvProcess;
	private static boolean messageReceived;
	
	private ISCClient client;
	private Exception ex;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
			srvProcess = ctrl.startServer(TestConstants.sessionSrv,
					TestConstants.log4jSrvProperties, TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100,
					new String[] { TestConstants.serviceName, TestConstants.serviceNameAlt });
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@Before
	public void setUp() throws Exception {
		client = new SCClient();
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		assertEquals("available/allocated sessions", "1000/0", client
				.workload(TestConstants.serviceName));
	}

	@After
	public void tearDown() throws Exception {
		assertEquals("available/allocated sessions", "1000/0", client
				.workload(TestConstants.serviceName));
		client.detach();
		client = null;
		ex = null;
		messageReceived = false;
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		ctrl.stopProcess(scProcess, TestConstants.log4jSC0Properties);
		ctrl = null;
		srvProcess = null;
		scProcess = null;
	}

	@Test(expected = SCServiceException.class)
	public void execute_beforeCreateSession_throwsException() throws Exception {
		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.execute(new SCMessage(), new CallBack(service));
	}

	@Test
	public void execute_messageDataNull_returnsTheSameMessageData() throws Exception {

		SCMessage message = new SCMessage(null);

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		service.deleteSession();

		assertEquals(message.getData(), response.getData());
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageDataEmptyString_returnsTheSameMessageData() throws Exception {

		SCMessage message = new SCMessage("");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		service.deleteSession();

		assertEquals(null, response.getData());
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageDataSingleChar_returnsTheSameMessageData() throws Exception {

		SCMessage message = new SCMessage("a");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		service.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageDataArbitrary_returnsTheSameMessageData() throws Exception {

		SCMessage message = new SCMessage("The quick brown fox jumps over a lazy dog.");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		service.deleteSession();

		assertEquals(message.getData(), response.getData());
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageData1MBArray_returnsTheSameMessageData() throws Exception {

		SCMessage message = new SCMessage(new byte[TestConstants.dataLength1MB]);

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		service.deleteSession();

		assertEquals(((byte[]) message.getData()).length, ((byte[]) response.getData()).length);
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageWhiteSpaceMessageInfo_returnsTheSameMessage() throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(" ");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		service.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSingleCharMessageInfo_returnsTheSameMessage() throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("a");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		service.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageArbitraryMessageInfo_returnsTheSameMessage() throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		service.deleteSession();

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

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		service.deleteSession();

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

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		service.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	//TODO FJU when is the messages session id supposed to be modified?
	@Test
	public void execute_messageSessionIdEmptyString_returnsTheSameMessage() throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		message.setSessionId("");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		String sessionId = service.getSessionId();
		service.deleteSession();

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
		message.setSessionId(" ");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		String sessionId = service.getSessionId();
		service.deleteSession();

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
		message.setSessionId("a");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		String sessionId = service.getSessionId();
		service.deleteSession();

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

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		String sessionId = service.getSessionId();
		service.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionId, response.getSessionId());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSessionIdLikeSessionIdString_returnsCorrectSessionId()
			throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		((SCMessage) message).setSessionId("aaaa0000-bb11-cc22-dd33-eeeeee444444");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		String sessionId = service.getSessionId();
		service.deleteSession();

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

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		((SCMessage) message).setSessionId(service.getSessionId());

		CallBack callback = new CallBack(service);
		service.execute(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		String sessionId = service.getSessionId();
		service.deleteSession();

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

		ISessionService service0 = client.newSessionService(TestConstants.serviceName);
		service0.createSession("sessionInfo", 300, 60);

		ISessionService service1 = client.newSessionService(TestConstants.serviceName);
		service1.createSession("sessionInfo", 300, 60);

		((SCMessage) message).setSessionId(service1.getSessionId());

		CallBack callback = new CallBack(service0);
		service0.execute(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		String sessionId0 = service0.getSessionId();
		service0.deleteSession();
		service1.deleteSession();

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

		ISessionService service0 = client.newSessionService(TestConstants.serviceName);
		service0.createSession("sessionInfo", 300, 60);

		ISessionService service1 = client.newSessionService(TestConstants.serviceNameAlt);
		service1.createSession("sessionInfo", 300, 60);

		((SCMessage) message).setSessionId(service1.getSessionId());

		CallBack callback = new CallBack(service0);
		service0.execute(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		String sessionId0 = service0.getSessionId();
		service0.deleteSession();
		service1.deleteSession();

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

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(message, callback, 1);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(service.getSessionId(), response.getSessionId());
		assertEquals(message.isFault(), response.isFault());

		service.deleteSession();
	}

	@Test
	public void execute_timeout2_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(message, callback, 2);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(service.getSessionId(), response.getSessionId());
		assertEquals(message.isFault(), response.isFault());

		service.deleteSession();
	}

	@Test
	public void execute_timeout0_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		SCMessage response = null;
		try {
			CallBack callback = new CallBack(service);
			service.execute(message, callback, 0);
			// wait until message received
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(null, response);

		service.deleteSession();
	}

	@Test
	public void execute_timeoutMinus1_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		SCMessage response = null;
		try {
			CallBack callback = new CallBack(service);
			service.execute(message, callback, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(null, response);

		service.deleteSession();
	}

	@Test
	public void execute_timeoutIntMin_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		SCMessage response = null;
		try {
			CallBack callback = new CallBack(service);
			service.execute(message, callback, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(null, response);

		service.deleteSession();
	}

	@Test
	public void execute_timeoutIntMax_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		SCMessage response = null;
		try {
			CallBack callback = new CallBack(service);
			service.execute(message, callback, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(null, response);

		service.deleteSession();
	}

	@Test
	public void execute_timeoutAllowedMax_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(message, callback, 3600);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
		service.deleteSession();
	}

	@Test
	public void execute_timeoutAllowedMaxPlus1_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		SCMessage response = null;
		try {
			CallBack callback = new CallBack(service);
			service.execute(message, callback, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(null, response);

		service.deleteSession();
	}

	@Test
	public void execute_timeout1_passes() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(message, callback, 1);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
		service.deleteSession();
	}

	@Test
	public void execute_timeout2_passes() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(message, callback, 2);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
		service.deleteSession();
	}

	@Test
	public void execute_timeoutMaxAllowed_passes() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(message, callback, 3600);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
		service.deleteSession();
	}

	@Test
	public void execute_timeoutMaxAllowedPlus1_throwsSCMPValidatorException() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		try {
			CallBack callback = new CallBack(service);
			service.execute(message, callback, 3601);
		} catch (Exception e) {
			ex = e;
		}

		service.deleteSession();
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void execute_timeoutIntMax_throwsSCMPValidatorException() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		try {
			CallBack callback = new CallBack(service);
			service.execute(message, callback, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}

		service.deleteSession();
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void execute_timeoutIntMin_throwsSCMPValidatorException() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		try {
			CallBack callback = new CallBack(service);
			service.execute(message, callback, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}

		service.deleteSession();
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void execute_timeout0_throwsSCMPValidatorException() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		try {
			CallBack callback = new CallBack(service);
			service.execute(message, callback, 0);
		} catch (Exception e) {
			ex = e;
		}

		service.deleteSession();
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void execute_timeoutMinus1_throwsSCMPValidatorException() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		try {
			CallBack callback = new CallBack(service);
			service.execute(message, callback, -1);
		} catch (Exception e) {
			ex = e;
		}

		service.deleteSession();
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void execute_timeoutExpiresOnServer_throwsException() throws Exception {

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(new SCMessage("timeout 4000"), callback, 2);
		// wait until message received
		while (messageReceived == false)
			;
		ex = callback.exc;

		service.deleteSession();
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void execute_timeoutCloselyExpires_throwsException() throws Exception {

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(new SCMessage("timeout 2000"), callback, 2);
		// wait until message received
		while (messageReceived == false)
			;
		ex = callback.exc;
		service.deleteSession();
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void execute_timeoutIsEnough_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("timeout 1500");

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300, 60);

		CallBack callback = new CallBack(service);
		service.execute(message, callback, 2);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
		service.deleteSession();
	}

	private class CallBack extends SCMessageCallback {
		private SCMessage response = null;
		private volatile Exception exc = null;

		public CallBack(IService service) {
			super(service);
		}

		@Override
		public void callback(SCMessage msg) {
			response = msg;
			AsynchronousExecuteClientTest.messageReceived = true;
		}

		@Override
		public void callback(Exception e) {
			logger.error("callback", e);
			exc = e;
			AsynchronousExecuteClientTest.messageReceived = true;
		}

	}

}