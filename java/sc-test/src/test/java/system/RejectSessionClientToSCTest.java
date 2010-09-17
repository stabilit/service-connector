package system;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.cln.SCClient;
import org.serviceconnector.cln.service.ISCClient;
import org.serviceconnector.cln.service.ISessionService;
import org.serviceconnector.common.service.ISCMessage;
import org.serviceconnector.common.service.SCServiceException;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.ctrl.util.TestEnvironmentController;
import org.serviceconnector.service.SCMessage;



public class RejectSessionClientToSCTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(RejectSessionClientToSCTest.class);

	private static Process sc;
	private static Process srv;

	private ISCClient client;

	private Exception ex;

	private static TestEnvironmentController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
		try {
			sc = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
			srv = ctrl.startServer(TestConstants.log4jSrvProperties, 30000, TestConstants.PORT9000, 100, new String[] {TestConstants.serviceName, TestConstants.serviceNameAlt});
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@Before
	public void setUp() throws Exception {
		client = new SCClient();
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		assertEquals("1000/0", client.workload(TestConstants.serviceName));
	}

	@After
	public void tearDown() throws Exception {
		assertEquals("1000/0", client.workload(TestConstants.serviceName));
		client.detach();
		client = null;
		ex = null;
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(sc, TestConstants.log4jSC0Properties);
		ctrl.stopProcess(srv, TestConstants.log4jSrvProperties);
		ctrl = null;
		sc = null;
		srv = null;
	}

	
	@Test
	public void createSession_rejectTheSession_sessionIdIsNotSetThrowsExceptionWithAppErrorCodeAndText() throws Exception {
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		
		try {
			// message "reject" translates on the server to reject the session
			sessionService.createSession("sessionInfo", 300, 10, "reject");
		} catch (SCServiceException e) {
			ex = e;
		}
		
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		//TODO appErrorCode & appErrorText should be maybe in the exception message rather than attributes of exception
		assertEquals(0, Integer.parseInt(((SCServiceException)ex).getAppErrorCode()));
		assertEquals("\"This is the app error text\"", ((SCServiceException)ex).getAppErrorText());
	}
	
	@Test
	public void createSession_rejectTheSessionAndTryToDeleteSession_sessionIdIsNotSetPasses() throws Exception {
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		
		try {
			sessionService.createSession("sessionInfo", 300, 10, "reject");
		} catch (Exception e) {
		}
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}
	
	//TODO throws NullPointerException
	@Test(expected = SCServiceException.class)
	public void createSession_rejectTheSessionAndTryToExecuteAMessage_sessionIdIsNotSetExecuteThrowsException() throws Exception {
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		
		try {
			sessionService.createSession("sessionInfo", 300, 10, "reject");
		} catch (Exception e) {
			assertEquals(true, sessionService.getSessionId() == null
					|| sessionService.getSessionId().isEmpty());
		}

		sessionService.execute(new SCMessage());
	}
	
	@Test
	public void createSession_rejectTheSessionThenCreateValidSessionThenExecuteAMessage_passes() throws Exception {
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		
		try {
			sessionService.createSession("sessionInfo", 300, 10, "reject");
		} catch (Exception e) {
			assertEquals(true, sessionService.getSessionId() == null
					|| sessionService.getSessionId().isEmpty());
		}
		sessionService.createSession("sessionInfo", 300, 10);
		assertEquals(false, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		ISCMessage response = sessionService.execute(new SCMessage());
		assertEquals(sessionService.getSessionId(), response.getSessionId());
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}
	
	@Test
	public void createSession_TcpRejectTheSession_sessionIdIsNotSetThrowsExceptionWithAppErrorCodeAndText() throws Exception {
		client.detach();
		((SCClient) client).setConnectionType("netty.tcp");
		client.attach(TestConstants.HOST, TestConstants.PORT9000);
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		
		try {
			// message "reject" translates on the server to reject the session
			sessionService.createSession("sessionInfo", 300, 10, "reject");
		} catch (SCServiceException e) {
			ex = e;
		}
		
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		assertEquals(0, Integer.parseInt(((SCServiceException)ex).getAppErrorCode()));
		assertEquals("\"This is the app error text\"", ((SCServiceException)ex).getAppErrorText());
	}
	
	@Test
	public void createSession_TcpRejectTheSessionAndTryToDeleteSession_sessionIdIsNotSetPasses() throws Exception {
		client.detach();
		((SCClient) client).setConnectionType("netty.tcp");
		client.attach(TestConstants.HOST, TestConstants.PORT9000);
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		
		try {
			sessionService.createSession("sessionInfo", 300, 10, "reject");
		} catch (Exception e) {
		}
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}
	
	@Test(expected = SCServiceException.class)
	public void createSession_TcpRejectTheSessionAndTryToExecuteAMessage_sessionIdIsNotSetThrowsException() throws Exception {
		client.detach();
		((SCClient) client).setConnectionType("netty.tcp");
		client.attach(TestConstants.HOST, TestConstants.PORT9000);
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		
		try {
			sessionService.createSession("sessionInfo", 300, 10, "reject");
		} catch (Exception e) {
			assertEquals(true, sessionService.getSessionId() == null
					|| sessionService.getSessionId().isEmpty());
		}

		sessionService.execute(new SCMessage());
	}
	
	@Test
	public void createSession_TcpRejectTheSessionThenCreateValidSessionThenExecuteAMessage_passes() throws Exception {
		client.detach();
		((SCClient) client).setConnectionType("netty.tcp");
		client.attach(TestConstants.HOST, TestConstants.PORT9000);
		
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		
		try {
			sessionService.createSession("sessionInfo", 300, 10, "reject");
		} catch (Exception e) {
			assertEquals(true, sessionService.getSessionId() == null
					|| sessionService.getSessionId().isEmpty());
		}
		sessionService.createSession("sessionInfo", 300, 10);
		assertEquals(false, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		ISCMessage response = sessionService.execute(new SCMessage());
		assertEquals(sessionService.getSessionId(), response.getSessionId());
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}
}
