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
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnetor.TestConstants;

public class RejectSessionClientTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(RejectSessionClientTest.class);

	private static Process scProcess;
	private static Process srvProcess;

	private SCMgmtClient client;
	private Exception ex;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
			srvProcess = ctrl.startServer(TestConstants.sessionSrv, TestConstants.log4jSrvProperties,
					TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, new String[] { TestConstants.serviceNameSession,
							TestConstants.serviceNamePublish });
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@Before
	public void setUp() throws Exception {
		client = new SCMgmtClient();
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		assertEquals("available/allocated sessions", "1000/0", client.getWorkload(TestConstants.serviceNameSession));
	}

	@After
	public void tearDown() throws Exception {
		assertEquals("available/allocated sessions", "1000/0", client.getWorkload(TestConstants.serviceNameSession));
		client.detach();
		client = null;
		ex = null;
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSCProperties);
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		ctrl = null;
		scProcess = null;
		srvProcess = null;
	}

	@Test
	public void createSession_rejectTheSession_sessionIdIsNotSetThrowsExceptionWithAppErrorCodeAndText()
			throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceNameSession);

		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());

		try {
			// message "reject" translates on the server to reject the session
			SCMessage scMessage = new SCMessage("reject");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
		} catch (SCServiceException e) {
			ex = e;
		}

		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		// TODO FJU appErrorCode & appErrorText should be maybe in the exception message rather than attributes of
		// exception
		assertEquals(0, Integer.parseInt(((SCServiceException) ex).getAppErrorCode()));
		assertEquals("\"This is the app error text\"", ((SCServiceException) ex).getAppErrorText());
	}

	@Test
	public void createSession_rejectTheSessionAndTryToDeleteSession_sessionIdIsNotSetPasses() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceNameSession);

		try {
			SCMessage scMessage = new SCMessage("reject");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
		} catch (Exception e) {
		}
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	// TODO FJU throws NullPointerException
	@Test(expected = SCServiceException.class)
	public void createSession_rejectTheSessionAndTryToExecuteAMessage_sessionIdIsNotSetExecuteThrowsException()
			throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceNameSession);

		try {
			SCMessage scMessage = new SCMessage("reject");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
		} catch (Exception e) {
			assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		}

		sessionService.execute(new SCMessage());
	}

	@Test
	public void createSession_rejectTheSessionThenCreateValidSessionThenExecuteAMessage_passes() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceNameSession);

		try {
			SCMessage scMessage = new SCMessage("reject");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
		} catch (Exception e) {
			assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		}
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 10, scMessage);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		SCMessage response = sessionService.execute(new SCMessage());
		assertEquals(sessionService.getSessionId(), response.getSessionId());
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_TcpRejectTheSession_sessionIdIsNotSetThrowsExceptionWithAppErrorCodeAndText()
			throws Exception {
		client.detach();
		((SCClient) client).setConnectionType("netty.tcp");
		client.attach(TestConstants.HOST, TestConstants.PORT_TCP);
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceNameSession);

		try {
			// message "reject" translates on the server to reject the session
			SCMessage scMessage = new SCMessage("reject");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
		} catch (SCServiceException e) {
			ex = e;
		}

		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		assertEquals(0, Integer.parseInt(((SCServiceException) ex).getAppErrorCode()));
		assertEquals("\"This is the app error text\"", ((SCServiceException) ex).getAppErrorText());
	}

	@Test
	public void createSession_TcpRejectTheSessionAndTryToDeleteSession_sessionIdIsNotSetPasses() throws Exception {
		client.detach();
		((SCClient) client).setConnectionType("netty.tcp");
		client.attach(TestConstants.HOST, TestConstants.PORT_TCP);
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceNameSession);

		try {
			SCMessage scMessage = new SCMessage("reject");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
		} catch (Exception e) {
		}
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test(expected = SCServiceException.class)
	public void createSession_TcpRejectTheSessionAndTryToExecuteAMessage_sessionIdIsNotSetThrowsException()
			throws Exception {
		client.detach();
		((SCClient) client).setConnectionType("netty.tcp");
		client.attach(TestConstants.HOST, TestConstants.PORT_TCP);
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceNameSession);

		try {
			SCMessage scMessage = new SCMessage("reject");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
		} catch (Exception e) {
			assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		}

		sessionService.execute(new SCMessage());
	}

	@Test
	public void createSession_TcpRejectTheSessionThenCreateValidSessionThenExecuteAMessage_passes() throws Exception {
		client.detach();
		((SCClient) client).setConnectionType("netty.tcp");
		client.attach(TestConstants.HOST, TestConstants.PORT_TCP);

		SCSessionService sessionService = client.newSessionService(TestConstants.serviceNameSession);

		try {
			SCMessage scMessage = new SCMessage("reject");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 10, scMessage);
		} catch (Exception e) {
			assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		}
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 10, scMessage);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		SCMessage response = sessionService.execute(new SCMessage());
		assertEquals(sessionService.getSessionId(), response.getSessionId());
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}
}
