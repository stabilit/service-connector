package org.serviceconnector.test.system.perf;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;

public class ExecuteBenchmark {

	/** The Constant testLogger. */
	protected static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ExecuteBenchmark.class);

	private static ProcessesController ctrl;
	private ProcessCtx scCtx;
	private ProcessCtx srvCtx;
	private SCClient client;
	private SCSessionService service;
	private int threadCount = 0;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
	}

	@Before
	public void beforeOneTest() throws Exception {
		threadCount = Thread.activeCount();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		srvCtx = ctrl.startServer(TestConstants.SERVER_TYPE_SESSION, TestConstants.log4jSrvProperties,
				TestConstants.sesServerName1, TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 1, 1,
				TestConstants.sesServiceName1);
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			service.deleteSession();
		} catch (Exception e1) {
		}
		service = null;
		try {
			client.detach();
		} catch (Exception e) {
		}
		client = null;
		try {
			ctrl.stopServer(srvCtx);
		} catch (Exception e) {
		}
		srvCtx = null;
		try {
			ctrl.stopSC(scCtx);
		} catch (Exception e) {
		}
		scCtx = null;
		testLogger.info("Number of threads :" + Thread.activeCount() + " created :"+(Thread.activeCount() - threadCount));
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		ctrl = null;
	}

	/**
	 * Description: Send x message à 128 bytes to the server. Receive echoed messages. Measure performance <br>
	 * Expectation: Performance better than 600 msg/sec.
	 */
	@Test
	public void benchmark_msg_compressed() throws Exception {
		SCMessage request = new SCMessage(new byte[128]);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		request.setCompressed(true);
		request.setSessionInfo("sessionInfo");
		request.setMessageInfo("echo");
		response = service.createSession(10, request);
		int nr = 10;
		long start = System.currentTimeMillis();
		long startPart = System.currentTimeMillis();
		long stopPart = 0;
		for (int i = 0; i < nr; i++) {
			if (((i+1) % 1000) == 0) {
				stopPart = System.currentTimeMillis();
				testLogger.info("Executing message nr. " + (i+1) + "... "+(1000000 / (stopPart - startPart))+ " msg/sec.");
				startPart = System.currentTimeMillis();
			}
			response = service.execute(10, request);
		}
		service.deleteSession(10);
		long stop = System.currentTimeMillis();
		long perf = nr * 1000 / (stop - start);
		testLogger.info(nr + "msg à 128 byte performance : " + perf + " msg/sec.");
		assertEquals("Performence not fast, only"+ perf + " msg/sec.", true, perf > 400);
	}

	/**
	 * Description: Send x message à 128 bytes to the server. Receive echoed messages. Measure performance <br>
	 * Expectation: Performance better than 600 msg/sec.
	 */
	@Test
	public void benchmark_msg_uncompressed() throws Exception {
		SCMessage request = new SCMessage(new byte[128]);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		request.setSessionInfo("sessionInfo");
		request.setMessageInfo("echo");
		request.setCompressed(false);
		response = service.createSession(10, request);
		int nr = 10000;
		long start = System.currentTimeMillis();
		for (int i = 0; i < nr; i++) {
			if (((i+1) % 1000) == 0)
				testLogger.info("Executing message nr. " + (i+1) + "...");
			response = service.execute(10, request);
		}
		service.deleteSession(10);
		long stop = System.currentTimeMillis();
		long perf = nr * 1000 / (stop - start);
		testLogger.info(nr + "msg à 128 byte performance : " + perf + " msg/sec.");
		assertEquals(true, perf > 600);
	}


//	/**
//	 * Description: execute_10MBDataUsingDifferentBodyLength_outputsBestTimeAndBodyLength()
//	 * 
//	 * @throws Exception
//	 */
//	@Test
//	public void t04_benchmark() throws Exception {
//		long previousResult = Long.MAX_VALUE;
//		long result = Long.MAX_VALUE - 1;
//		int dataLength = 10 * TestConstants.dataLength1MB;
//		int messages = 0;
//
//		while (result < previousResult) {
//			previousResult = result;
//			messages++;
//
//			ClientThreadController clientCtrl = new ClientThreadController(false, true, 1, 1, messages, dataLength / messages);
//
//			result = clientCtrl.perform();
//
//			scProcess = ctrl.restartSC(scProcess);
//			srvProcess = ctrl.restartServer(srvProcess);
//		}
//
//		testLogger.info("Best performance to execute roughly 10MB of data messages was " + previousResult + "ms using "
//				+ --messages + " messages of " + dataLength / messages + "B data each.");
//		assertEquals(true, previousResult < 25000);
//	}
//
//	@Test
//	public void execute_10MBDataUsingDifferentBodyLengthStartingFrom100000Messages_outputsBestTimeAndBodyLength() throws Exception {
//		long previousResult = Long.MAX_VALUE;
//		long result = Long.MAX_VALUE - 1;
//		int dataLength = 10 * TestConstants.dataLength1MB;
//		int messages = 100001;
//
//		while (result < previousResult && messages > 0) {
//			previousResult = result;
//			messages--;
//
//			ClientThreadController clientCtrl = new ClientThreadController(false, true, 1, 1, messages, dataLength / messages);
//
//			result = clientCtrl.perform();
//
//			scProcess = ctrl.restartSC(scProcess);
//			srvProcess = ctrl.restartServer(srvProcess);
//		}
//
//		testLogger.info("Best performance to execute roughly 10MB of data messages was " + previousResult + "ms using "
//				+ ++messages + " messages of " + dataLength / messages + "B data each.");
//		assertEquals(true, previousResult < 25000);
//	}

}
