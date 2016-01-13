/*-----------------------------------------------------------------------------*
 *                                                                             *
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
 *-----------------------------------------------------------------------------*/
package org.serviceconnector.test.perf.api.cln;

import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.test.perf.api.APIPerfSuperClientTest;

@SuppressWarnings("unused")
public class APIExecuteBenchmark extends APIPerfSuperClientTest{


	/**
	 * Description: Send 10000 message � 128 bytes to the server. Receive echoed messages. Measure performance <br>
	 * Expectation: Performance better than 600 msg/sec.
	 */
	@Test
	public void t_10000_msg_compressed() throws Exception {
		SCMessage request = new SCMessage(new byte[128]);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		request.setCompressed(true);
		request.setSessionInfo("sessionInfo");
		request.setMessageInfo(TestConstants.echoCmd);
		MsgCallback cbk = new MsgCallback(sessionService);
		response = sessionService.createSession(10, request, cbk);
		int nrMessages = 10000;
		long start = System.currentTimeMillis();
		long startPart = System.currentTimeMillis();
		long stopPart = 0;
		for (int i = 0; i < nrMessages; i++) {
			if (((i + 1) % 1000) == 0) {
				stopPart = System.currentTimeMillis();
				testLogger.info("Executing message nr. " + (i + 1) + "... " + (1000000 / (stopPart - startPart))
						+ " msg/sec.");
				startPart = System.currentTimeMillis();
			}
			response = sessionService.execute(10, request);
		}
		sessionService.deleteSession(10);
		long stop = System.currentTimeMillis();
		long perf = nrMessages * 1000 / (stop - start);
		testLogger.info(nrMessages + "msg � 128 byte performance=" + perf + " msg/sec.");
		Assert.assertTrue("Performence not fast enough, only" + perf + " msg/sec.", perf > 400);
	}

	/**
	 * Description: Send 10000 message � 128 bytes to the server. Receive echoed messages. Measure performance <br>
	 * Expectation: Performance better than 600 msg/sec.
	 */
	@Test
	public void t_10000_msg_uncompressed() throws Exception {
		SCMessage request = new SCMessage(new byte[128]);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		request.setSessionInfo("sessionInfo");
		request.setMessageInfo(TestConstants.echoCmd);
		request.setCompressed(false);
		MsgCallback cbk = new MsgCallback(sessionService);
		response = sessionService.createSession(10, request, cbk);
		int nrMessages = 10000;
		long start = System.currentTimeMillis();
		long startPart = System.currentTimeMillis();
		long stopPart = 0;
		for (int i = 0; i < nrMessages; i++) {
			if (((i + 1) % 1000) == 0) {
				stopPart = System.currentTimeMillis();
				testLogger.info("Executing message nr. " + (i + 1) + "... " + (1000000 / (stopPart - startPart))
						+ " msg/sec.");
				startPart = System.currentTimeMillis();
			}

			response = sessionService.execute(10, request);
		}
		sessionService.deleteSession(10);
		long stop = System.currentTimeMillis();
		long perf = nrMessages * 1000 / (stop - start);
		testLogger.info(nrMessages + "msg � 128 byte performance=" + perf + " msg/sec.");
		Assert.assertTrue("Performence not fast enough, only" + perf + " msg/sec.", perf > 600);
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
	//		Assert.assertEquals(true, previousResult < 25000);
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
	//		Assert.assertEquals(true, previousResult < 25000);
	//	}

}
