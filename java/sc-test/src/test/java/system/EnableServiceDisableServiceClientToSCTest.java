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

public class EnableServiceDisableServiceClientToSCTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger
			.getLogger(EnableServiceDisableServiceClientToSCTest.class);

	private static Process sc;
	private Process srv;

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
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@Before
	public void setUp() throws Exception {
		srv = ctrl.startServer(log4jSrvProperties, 30000, port9000, 100, new String[] {
				serviceName, serviceNameAlt });

		client = new SCClient();
		client.attach(host, port8080);
		assertEquals("1000/0", client.workload(serviceName));
	}

	@After
	public void tearDown() throws Exception {
		assertEquals("1000/0", client.workload(serviceName));
		client.detach();
		client = null;
		ctrl.stopProcess(srv, log4jSrvProperties);
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(sc, log4jSCProperties);
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
	}

	@Test
	public void createSession_onInitiallyDisabledSessionService_clientEnablesServiceServerRegistersSessionCreated() throws Exception {
		assertEquals(false, client.isServiceEnabled(serviceNameNotEnabled));
		client.enableService(serviceNameNotEnabled);
		assertEquals(true, client.isServiceEnabled(serviceNameNotEnabled));
		
		ISessionService sessionService0 = client.newSessionService(serviceName);
		sessionService0.createSession("sessionInfo", 300, 60);
		sessionService0.execute(new SCMessage("register " + serviceNameNotEnabled));
		
		
		ISessionService sessionService1 = client.newSessionService(serviceNameNotEnabled);
		sessionService1.createSession("sessionInfo", 300, 60);

		assertEquals(true, client.isServiceEnabled(serviceNameNotEnabled));
		assertEquals(false, sessionService1.getSessionId() == null
				|| sessionService1.getSessionId().isEmpty());
		sessionService1.deleteSession();
		
		sessionService0.execute(new SCMessage("deregister " + serviceNameNotEnabled));
		sessionService0.deleteSession();
		client.disableService(serviceNameNotEnabled);
	}
	
	@Test
	public void createSession_onInitiallyEnabledService_clientDisablesServiceServerRestartsThrowsException() throws Exception {
		assertEquals(true, client.isServiceEnabled(serviceName));
		client.disableService(serviceName);
		
		ISessionService sessionService = client.newSessionService(serviceName);
		
		try {
			sessionService.createSession("sessionInfo", 300, 60);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, client.isServiceEnabled(serviceName));
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		client.enableService(serviceName);
	}
	
	@Test
	public void createSession_enabledServiceDisableThenEnableAgain_createsSession() throws Exception {
		assertEquals(true, client.isServiceEnabled(serviceName));
		client.disableService(serviceName);
		client.enableService(serviceName);
		assertEquals(true, client.isServiceEnabled(serviceName));
		
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60);
		assertEquals(false, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}
}
