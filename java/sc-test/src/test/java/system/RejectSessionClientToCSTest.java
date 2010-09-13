package system;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.sc.ctrl.util.TestEnvironmentController;
import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.ISCClient;
import com.stabilit.scm.cln.service.ISessionService;
import com.stabilit.scm.common.service.SCMessage;
import com.stabilit.scm.common.service.SCServiceException;


public class RejectSessionClientToCSTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(RejectSessionClientToCSTest.class);

	private static Process sc;
	private static Process srv;

	private ISCClient client;

	private static final String host = "localhost";
	private static final int port8080 = 8080;
	private static final int port9000 = 9000;
	private static final String serviceName = "simulation";
	private static final String serviceNameAlt = "P01_RTXS_sc1";

	private Exception ex;

	private static TestEnvironmentController ctrl;
	private static final String log4jSCProperties = "log4jSC0.properties";
	private static final String scProperties = "scIntegration.properties";
	private static final String log4jSrvProperties = "log4jSrv.properties";

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
		try {
			sc = ctrl.startSC(log4jSCProperties, scProperties);
			srv = ctrl.startServer(log4jSrvProperties, 30000, port9000, 100, new String[] {serviceName, serviceNameAlt});
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@Before
	public void setUp() throws Exception {
		client = new SCClient();
		client.attach(host, port8080);
		assertEquals("1000/0", client.workload(serviceName));
	}

	@After
	public void tearDown() throws Exception {
		assertEquals("1000/0", client.workload(serviceName));
		client.detach();
		client = null;
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(sc, log4jSCProperties);
		ctrl.stopProcess(srv, log4jSrvProperties);
	}

	
	@Test
	public void createSession_rejectTheSession_sessionIdIsNotSetThrowsExceptionWithAppErrorCodeAndText() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		
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
		ISessionService sessionService = client.newSessionService(serviceName);
		
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
		ISessionService sessionService = client.newSessionService(serviceName);
		
		try {
			sessionService.createSession("sessionInfo", 300, 10, "reject");
		} catch (Exception e) {
			assertEquals(true, sessionService.getSessionId() == null
					|| sessionService.getSessionId().isEmpty());
		}

		sessionService.execute(new SCMessage());
	}
	
	
	
	@Test
	public void createSession_TcpRejectTheSession_sessionIdIsNotSetThrowsExceptionWithAppErrorCodeAndText() throws Exception {
		client.detach();
		((SCClient) client).setConnectionType("netty.tcp");
		client.attach(host, port9000);
		ISessionService sessionService = client.newSessionService(serviceName);
		
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
		client.attach(host, port9000);
		ISessionService sessionService = client.newSessionService(serviceName);
		
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
		client.attach(host, port9000);
		ISessionService sessionService = client.newSessionService(serviceName);
		
		try {
			sessionService.createSession("sessionInfo", 300, 10, "reject");
		} catch (Exception e) {
			assertEquals(true, sessionService.getSessionId() == null
					|| sessionService.getSessionId().isEmpty());
		}

		sessionService.execute(new SCMessage());
	}
}
