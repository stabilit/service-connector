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
import com.stabilit.scm.common.service.SCServiceException;

public class EnableSessionDisableSessionClientToSCTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger
			.getLogger(EnableSessionDisableSessionClientToSCTest.class);

	private static Process sc;
	private static Process srv;

	private ISCClient client;

	private static final String host = "localhost";
	private static final int port8080 = 8080;
	private static final int port9000 = 9000;
	private static final String serviceName = "simulation";
	private static final String serviceNameAlt = "P01_RTXS_sc1";
	private static final String serviceNameNotEnabled = "notEnabledService";

	private Exception ex;

	private static TestEnvironmentController ctrl;
	private static final String log4jSCProperties = "log4jSC0.properties";
	private static final String scProperties = "scIntegration.properties";
	private static final String log4jSrvProperties = "log4jSrv.properties";

	//TODO registering notEnabled service fails
	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
		try {
			sc = ctrl.startSC(log4jSCProperties, scProperties);
			srv = ctrl.startServer(log4jSrvProperties, 30000, port9000, 100, new String[] {
					serviceName, serviceNameAlt, serviceNameNotEnabled });
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
	public void createSession_onEnabledSessionService_sessionIsCreated() throws Exception {
		assertEquals(true, client.isServiceEnabled(serviceName));

		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		assertEquals(true, client.isServiceEnabled(serviceName));
		assertEquals(false, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
		assertEquals(true, client.isServiceEnabled(serviceName));
	}

	@Test
	public void createSession_onDisabledSessionService_throwsException() throws Exception {
		assertEquals(false, client.isServiceEnabled(serviceNameNotEnabled));

		ISessionService sessionService = client.newSessionService(serviceNameNotEnabled);
		sessionService.createSession("sessionInfo", 300, 60);

		assertEquals(false, client.isServiceEnabled(serviceName));
		assertEquals(false, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
		assertEquals(false, client.isServiceEnabled(serviceName));
	}
}
