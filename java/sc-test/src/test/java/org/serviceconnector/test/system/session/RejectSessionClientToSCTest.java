package org.serviceconnector.test.system.session;

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
import org.serviceconnector.service.SCServiceException;



public class RejectSessionClientToSCTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(RejectSessionClientToSCTest.class);

	private static Process scProcess;
	private static Process srvProcess;

	private int threadCount = 0;
	private ISCClient client;
	private Exception ex;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
			srvProcess = ctrl.startServer(TestConstants.sessionSrv, TestConstants.log4jSrvProperties, TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, new String[] {TestConstants.serviceName, TestConstants.serviceNameAlt});
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
		ctrl.stopProcess(scProcess, TestConstants.log4jSC0Properties);
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		ctrl = null;
		scProcess = null;
		srvProcess = null;
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
		//TODO FJU appErrorCode & appErrorText should be maybe in the exception message rather than attributes of exception
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
	
	//TODO FJU throws NullPointerException
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
		SCMessage response = sessionService.execute(new SCMessage());
		assertEquals(sessionService.getSessionId(), response.getSessionId());
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}
	
	@Test
	public void createSession_TcpRejectTheSession_sessionIdIsNotSetThrowsExceptionWithAppErrorCodeAndText() throws Exception {
		client.detach();
		((SCClient) client).setConnectionType("netty.tcp");
		client.attach(TestConstants.HOST, TestConstants.PORT_TCP);
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
		client.attach(TestConstants.HOST, TestConstants.PORT_TCP);
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
		client.attach(TestConstants.HOST, TestConstants.PORT_TCP);
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
		client.attach(TestConstants.HOST, TestConstants.PORT_TCP);
		
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
		SCMessage response = sessionService.execute(new SCMessage());
		assertEquals(sessionService.getSessionId(), response.getSessionId());
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}
}
