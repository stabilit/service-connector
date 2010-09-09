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


public class PrematureDestroyOfServerClientToSCTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ExecuteClientToSCTest.class);

	private static Process sc;
	private Process srv;

	private ISCClient client;

	private Exception ex;

	private static final String host = "localhost";
	private static final int port8080 = 8080;
	private static final int port9000 = 9000;
	private static final String serviceName = "simulation";
	private static final String serviceNameAlt = "P01_RTXS_sc1";

	private static TestEnvironmentController ctrl;
	private static final String log4jSCProperties = "log4jSC0.properties";
	private static final String scProperties = "scIntegration.properties";
	private static final String log4jSrvProperties = "log4jSrv.properties";

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
		try {
			sc = ctrl.startSC(log4jSCProperties, scProperties);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@Before
	public void setUp() throws Exception {
		try {
			srv = ctrl.startServer(log4jSrvProperties, 30000, port9000, 100, new String[] {serviceName, serviceNameAlt});
		} catch (Exception e) {
			logger.error("setUp", e);
		}
		client = new SCClient();
		client.attach(host, port8080);
	}

	@After
	public void tearDown() throws Exception {
		client.detach();
		ctrl.stopProcess(srv, log4jSrvProperties);
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(sc, log4jSCProperties);
	}
	
	@Test
	public void createSession_withoutServer_throwsException() throws Exception {
		ctrl.stopProcess(srv, log4jSrvProperties);
		ISessionService sessionService = client.newSessionService(serviceName);
		try {
			sessionService.createSession("sessionInfo", 300, 5);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(null, sessionService.getSessionId());
	}
	
	@Test
	public void deleteSession_withoutServer_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 5);

		ctrl.stopProcess(srv, log4jSrvProperties);

		try {
			sessionService.deleteSession();
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
	}
	
	@Test
	public void execute_withoutServer_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 5);

		ctrl.stopProcess(srv, log4jSrvProperties);

		try {
			sessionService.execute(new SCMessage());
		} catch (Exception e) {
			ex = e;
			e.printStackTrace();
		}
		assertEquals(true, ex instanceof SCServiceException);
	}
	
	@Test
	public void deleteSession_withoutServerTimeoutTakes5Seconds_passes() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 5, 5);

		ctrl.stopProcess(srv, log4jSrvProperties);
		
		Thread.sleep(5000);
		sessionService.deleteSession();
	}
	
	@Test(expected = SCServiceException.class)
	public void execute_withoutServer_timeoutTakes5SecondsThrowsException() throws Exception {
		ctrl.stopProcess(srv, log4jSrvProperties);
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 5, 5);
		
		Thread.sleep(5000);
		try {
			sessionService.execute(new SCMessage());
		} catch (Exception e) {
			ex = e;
			e.printStackTrace();
		}
		assertEquals(null, sessionService.getSessionId());
		throw ex;
	}

}
