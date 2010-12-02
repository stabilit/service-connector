/*
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 */
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

public class SessionBenchmarks {

	/** The Constant testLogger. */
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SessionBenchmarks.class);

	private ProcessCtx scCtx;
	private ProcessCtx srvCtx;
	private SCClient client;
	private static ProcessesController ctrl;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
	}

	@Before
	public void beforeOneTest() throws Exception {
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		srvCtx = ctrl.startServer(TestConstants.SERVER_TYPE_SESSION, TestConstants.log4jSrvProperties, TestConstants.sessionServerName,
				TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, 10, TestConstants.sessionServiceNames);
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach(5);
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			client.detach();
		} catch (Exception e) {
		}
		try {
			ctrl.stopServer(srvCtx);
		} catch (Exception e) {
		}
		try {
			ctrl.stopSC(scCtx);
		} catch (Exception e) {
		}
		client = null;
		srvCtx = null;
		scCtx = null;
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		ctrl = null;
	}

	/**
	 * Description: Send 1000 message à 128 bytes to the server. Receive echoed messages. Measure performance <br>
	 * Expectation: Performance better than 600 msg/sec.
	 */
	@Test
	public void benchmark_1000_msg_compressed() throws Exception {
		SCMessage request = new SCMessage(new byte[128]);
		SCMessage response = null;
		SCSessionService service = client.newSessionService(TestConstants.sessionServiceNames);
		request.setCompressed(true);
		request.setSessionInfo("sessionInfo");
		response = service.createSession(10, request);
		int nr = 10000;
		long start = System.currentTimeMillis();
		for (int i = 0; i < nr; i++) {
			if ((i % 1000) == 0)
				testLogger.info("Executing message nr. " + i + "...");
			response = service.execute(10, request);
		}
		service.deleteSession(10);
		long stop = System.currentTimeMillis();
		long perf = nr * 1000 / (stop - start);
		testLogger.info(nr + "msg à 128 byte performance : " + perf + " msg/sec.");
		assertEquals(true, perf > 400);
	}

	/**
	 * Description: Send 1000 message à 128 bytes to the server. Receive echoed messages. Measure performance <br>
	 * Expectation: Performance better than 600 msg/sec.
	 */
	@Test
	public void benchmark_1000_msg_uncompressed() throws Exception {
		SCMessage request = new SCMessage(new byte[128]);
		SCMessage response = null;
		SCSessionService service = client.newSessionService(TestConstants.sessionServiceNames);
		request.setSessionInfo("sessionInfo");
		request.setCompressed(false);
		response = service.createSession(10, request);
		int nr = 10000;
		long start = System.currentTimeMillis();
		for (int i = 0; i < nr; i++) {
			if ((i % 1000) == 0)
				testLogger.info("Executing message nr. " + i + "...");
			response = service.execute(10, request);
		}
		service.deleteSession(10);
		long stop = System.currentTimeMillis();
		long perf = nr * 1000 / (stop - start);
		testLogger.info(nr + "msg à 128 byte performance : " + perf + " msg/sec.");
		assertEquals(true, perf > 600);
	}

	/**
	 * Description: Send 100000 message à 128 bytes to the server. Receive echoed messages. Measure performance <br>
	 * Expectation: Performance better than 600 msg/sec.
	 */
	@Test
	public void benchmark_100000_msg() throws Exception {
		SCMessage request = new SCMessage(new byte[128]);
		SCMessage response = null;
		SCSessionService service = client.newSessionService(TestConstants.sessionServiceNames);
		request.setSessionInfo("sessionInfo");
		response = service.createSession(10, request);

		int nr = 100000;
		long start = System.currentTimeMillis();
		for (int i = 0; i < nr; i++) {
			if ((i % 2000) == 0)
				testLogger.info("Executing message nr. " + i + "...");
			response = service.execute(request);
		}
		service.deleteSession(10);
		long stop = System.currentTimeMillis();
		long perf = nr * 1000 / (stop - start);
		testLogger.info(nr + "msg à 128 byte performance : " + perf + " msg/sec.");
		assertEquals(true, perf > 600);
	}

	/**
	 * Description: Create and delete session 10000 times. No message body is sent or received. Measure performance <br>
	 * Expectation: Performance better than 200 sessions/sec.
	 */
	@Test
	public void benchmark_10000_sessions() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		SCSessionService service = client.newSessionService(TestConstants.sessionServiceNames);
	
		int nr = 10000;
		long start = System.currentTimeMillis();
		for (int i = 0; i < nr; i++) {
			if ((i % 1000) == 0)
				testLogger.info("Creating Session nr. " + i + "...");
			response = service.createSession(10, request);
			service.deleteSession(10);
		}
		long stop = System.currentTimeMillis();
		long perf = nr * 1000 / (stop - start);
		testLogger.info(nr + " Sessions created and deleted performance : " + perf + " sessions/sec.");
		assertEquals(true, perf > 200);
	}


//	/**
//	 * Description: execute_10MBDataUsingDifferentBodyLength_outputsBestTimeAndBodyLength()
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
//			ClientThreadController clientCtrl = new ClientThreadController( false, true, 1, 1, messages, dataLength / messages);
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
//
//	@Test
//	public void createSessionDeleteSession_10000Times_outputsTime() throws Exception {
//
//		ClientThreadController clientCtrl = new ClientThreadController(false, true, 1, 10000, 0, 0);
//		long result = clientCtrl.perform();
//		assertEquals(true, result < 25000);
//	}
//
//	@Test
//	public void createSessionExecuteDeleteSession_10000ExecuteMessagesDividedInto10ParallelClients_outputsTime() throws Exception {
//		int threadCount = Thread.activeCount();
//
//		ClientThreadController clientCtrl = new ClientThreadController(false, true, 10, 10, 100, 128);
//		long result = clientCtrl.perform();
//
//		testLogger.info("Threads before initializing clients:\t" + threadCount);
//		testLogger.info("Threads after execution completed:\t" + Thread.activeCount());
//		assertEquals(true, result < 25000);
//	}
//
//	@Test
//	public void createSessionExecuteDeleteSession_10000ExecuteMessagesSentByOneClient_outputsTime() throws Exception {
//		int threadCount = Thread.activeCount();
//
//		ClientThreadController clientCtrl = new ClientThreadController(false, false, 1, 100, 100, 128);
//		long result = clientCtrl.perform();
//
//		testLogger.info("Threads before initializing clients:\t" + threadCount);
//		testLogger.info("Threads after execution completed:\t" + Thread.activeCount());
//		assertEquals(true, result < 25000);
//	}
//
//	@Test
//	public void createSessionExecuteDeleteSession_roughly100000ExecuteMessagesByParallelClients_outputsBestTimeAndNumberOfClients()
//			throws Exception {
//		long previousResult = Long.MAX_VALUE;
//		long result = Long.MAX_VALUE - 1;
//		int clientsCount = 0;
//
//		while (result < previousResult) {
//			previousResult = result;
//			clientsCount++;
//
//			ClientThreadController clientCtrl = new ClientThreadController(false, true, clientsCount,
//					100000 / (1000 * clientsCount), 1000, 128);
//
//			result = clientCtrl.perform();
//
//			scProcess = ctrl.restartSC(scProcess);
//			srvProcess = ctrl.restartServer(srvProcess);
//		}
//
//		testLogger.info("Best performance to execute roughly 100000 messages was " + previousResult + "ms using " + --clientsCount
//				+ " parallel clients");
//		assertEquals(true, previousResult < 25000);
//	}
}
