package system;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.sc.ctrl.util.TestConstants;
import com.stabilit.sc.ctrl.util.TestEnvironmentController;
import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.ISCClient;
import com.stabilit.scm.cln.service.ISessionService;
import com.stabilit.scm.common.service.ISCMessage;
import com.stabilit.scm.common.service.SCMessage;


public class PerformanceTests {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PerformanceTests.class);

	private static Process sc;
	private static Process srv;

	private ISCClient client;

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
		((SCClient) client).setConnectionType("netty.tcp");
		client.attach(TestConstants.HOST, TestConstants.PORT9000);
	}

	@After
	public void tearDown() throws Exception {
		client.detach();
		client = null;
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(sc, TestConstants.log4jSC0Properties);
		ctrl.stopProcess(srv, TestConstants.log4jSrvProperties);
		ctrl = null;
		sc = null;
		srv = null;
		System.gc();
	}


	@Test
	public void execute_10000MessagesWith128BytesLongBody_outputTime() throws Exception {

		ISCMessage message = new SCMessage(new byte[128]);

		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		long start = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			sessionService.execute(message);
		}
		long stop = System.currentTimeMillis();
		
		System.out.println("Time to execute 10000 messages with 128 byte body was:\t" + (stop - start));
		assertEquals(true, stop - start < 25000);
	}
}
