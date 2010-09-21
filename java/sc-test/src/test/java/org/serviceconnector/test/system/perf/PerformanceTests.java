package org.serviceconnector.test.system.perf;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;

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
import org.serviceconnector.cln.PerformanceSessionClient;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.ctrl.util.TestEnvironmentController;
import org.serviceconnector.log.Loggers;

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
			srvProcess = ctrl.startServer(TestConstants.sessionSrv,
					TestConstants.log4jSrvProperties, 30000, TestConstants.PORT9000, 100,
					new String[] { TestConstants.serviceName, TestConstants.serviceNameAlt });
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

		SCMessage message = new SCMessage(new byte[128]);

		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		long start = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			sessionService.execute(message);
		}
		long stop = System.currentTimeMillis();

		testLogger
				.info("Time to execute 10000 messages with 128 byte body was:\t" + (stop - start));
		assertEquals(true, stop - start < 25000);
	}

	@Test
	public void createSessionDeleteSession_10000Times_outputsTime() throws Exception {

		ISessionService sessionService = client.newSessionService(TestConstants.serviceName);

		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			for (int j = 0; j < 10; j++) {
				sessionService.createSession("sessionInfo", 300, 10);
				sessionService.deleteSession();
			}
		}
		long stop = System.currentTimeMillis();
		testLogger.info("Time to create session and delete session 10000 times was:\t"
				+ (stop - start));
		assertEquals(true, stop - start < 25000);
	}

	@Test
	public void createSessionExecuteDeleteSession_10000ExecuteMessagesDividedInto10ParallelClients_outputsTime()
			throws Exception {
		int[] messages = new int[10];

		CountDownLatch startSignal = new CountDownLatch(1);
		CountDownLatch doneSignal = new CountDownLatch(10);

		int threadCount = Thread.activeCount();
		// create and start threads
		for (int i = 0; i < 10; i++) {
			new Thread(new PerformanceSessionClient(startSignal, doneSignal, messages[i])).start();
		}

		long start = System.currentTimeMillis();
		startSignal.countDown();
		doneSignal.await();
		long stop = System.currentTimeMillis();

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300);
		SCMessage response = service.execute(new SCMessage("executed"));
		service.deleteSession();
		
		int sum = 0;
		for (int i = 0; i < 10; i++) {
			sum += messages[i];
		}
		
		testLogger.info("Messages executed successfuly (clients):\t" + sum);
		testLogger.info("Messages executed successfuly (server):\t" + response.getData().toString());
		testLogger		
				.info("Time to create session execute and delete session:\t" + (stop - start) + "ms");
		testLogger.info("Threads before initializing clients:\t" + threadCount
				+ "\nThreads after execution completed:\t" + Thread.activeCount());
		assertEquals(true, stop - start < 25000);
	}
	
	@Test
	public void createSessionExecuteDeleteSession_10000ExecuteMessagesSentByOneClient_outputsTime() throws Exception {
		int threadCount = Thread.activeCount();
		
		long start = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			ISessionService service = client.newSessionService(TestConstants.serviceName);
			service.createSession("sessionInfo", 300);
			for (int j = 0; j < 10; j++) {
				service.execute(new SCMessage(new byte[128]));
			}
			service.deleteSession();
		}
		long stop = System.currentTimeMillis();

		ISessionService service = client.newSessionService(TestConstants.serviceName);
		service.createSession("sessionInfo", 300);
		SCMessage response = service.execute(new SCMessage("executed"));
		service.deleteSession();
		
		testLogger
				.info("Time to create session execute and delete session " + response.getData().toString() + " times single client was:\t"
						+ (stop - start));
		testLogger.info("Threads before initializing clients:\t" + threadCount
				+ "\nThreads after execution completed:\t" + Thread.activeCount());
		assertEquals(true, stop - start < 25000);
	}
}
