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
import org.serviceconnector.ctrl.util.ClientThreadController;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.ctrl.util.ThreadSafeCounter;
import org.serviceconnector.log.Loggers;

public class PerformanceTests {

	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PerformanceTests.class);

	private static Process scProcess;
	private static Process srvProcess;

	private ISCClient client;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
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
	public void execute_10000MessagesWith128BytesLongBody_outputsTime() throws Exception {

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
	public void execute_10MBDataUsingDifferentBodyLength_outputsBestTimeAndBodyLength() throws Exception {
		long previousResult = Long.MAX_VALUE;
		long result = Long.MAX_VALUE - 1;
		int dataLength = 10 * TestConstants.dataLength1MB;
		int messages = 0;

		while (result < previousResult) {
			previousResult = result;
			messages++;

			ClientThreadController clientCtrl = new ClientThreadController(false, true,
					1, 1, messages, dataLength / messages);

			result = clientCtrl.perform();
		}

		testLogger.info("Best performance to execute roughly 10MB of data messages was " + previousResult + " using " + --messages
				+ " messages of " + dataLength/messages + "B data each.");
		assertEquals(true, previousResult < 25000);
	}


	@Test
	public void createSessionDeleteSession_10000Times_outputsTime() throws Exception {

		ClientThreadController clientCtrl = new ClientThreadController(false, true, 1, 10000, 0, 0);
		long result = clientCtrl.perform();
		assertEquals(true, result < 25000);
	}

	@Test
	public void createSessionExecuteDeleteSession_10000ExecuteMessagesDividedInto10ParallelClients_outputsTime()
			throws Exception {
		int threadCount = Thread.activeCount();

		ClientThreadController clientCtrl = new ClientThreadController(false, true, 10, 10, 100,
				128);
		long result = clientCtrl.perform();

		testLogger.info("Threads before initializing clients:\t" + threadCount);
		testLogger.info("Threads after execution completed:\t" + Thread.activeCount());
		assertEquals(true, result < 25000);
	}

	@Test
	public void createSessionExecuteDeleteSession_10000ExecuteMessagesSentByOneClient_outputsTime()
			throws Exception {
		int threadCount = Thread.activeCount();

		ClientThreadController clientCtrl = new ClientThreadController(false, false, 1, 100, 100,
				128);
		long result = clientCtrl.perform();

		testLogger.info("Threads before initializing clients:\t" + threadCount);
		testLogger.info("Threads after execution completed:\t" + Thread.activeCount());
		assertEquals(true, result < 25000);
	}

	@Test
	public void createSessionExecuteDeleteSession_roughly10000ExecuteMessagesByParallelClients_outputsBestTimeAndNumberOfClients()
			throws Exception {
		long previousResult = Long.MAX_VALUE;
		long result = Long.MAX_VALUE - 1;
		int clientsCount = 0;

		while (result < previousResult) {
			previousResult = result;
			clientsCount++;

			ClientThreadController clientCtrl = new ClientThreadController(false, true,
					clientsCount, 10000 / (100 * clientsCount), 100, 128);

			result = clientCtrl.perform();
		}

		testLogger.info("Best performance to execute roughly 10000 messages was " + previousResult + " using " + --clientsCount
				+ " parallel clients");
		assertEquals(true, previousResult < 25000);
	}
}
