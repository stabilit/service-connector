package org.serviceconnector.test.system;

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
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.ctrl.util.TestEnvironmentController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.service.ISCMessage;
import org.serviceconnector.service.SCMessage;



public class PerformanceTests {
	
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());
	
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PerformanceTests.class);

	private static Process scProcess;
	private static Process srvProcess;

	private ISCClient client;

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
		ctrl.stopProcess(scProcess, TestConstants.log4jSC0Properties);
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		ctrl = null;
		scProcess = null;
		srvProcess = null;
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
		
		testLogger.info("Time to execute 10000 messages with 128 byte body was:\t" + (stop - start));
		assertEquals(true, stop - start < 25000);
	}
	
	@Test
	public void createSessionDeleteSession_10000Times_outputTime() throws Exception {
		
		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			for (int j = 0; j < 10; j++) {
				sessionService.createSession("sessionInfo", 300, 10);
				sessionService.deleteSession();
			}
		}
		long stop = System.currentTimeMillis();
		testLogger.info("Time to create session and delete session 10000 times was:\t" + (stop - start));
		assertEquals(true, stop - start < 25000);
	}
}
