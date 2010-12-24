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
package org.serviceconnector.test.perf.api.cln;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestSessionServiceMessageCallback;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.perf.api.APIPerfSuperClientTest;

@SuppressWarnings("unused")
public class APISessionBenchmark extends APIPerfSuperClientTest {

	/**
	 * Description: Create and delete session x times. No message body is sent or received. Measure performance <br>
	 * Expectation: Performance better than 200 sessions/sec.
	 */
	@Test
	public void benchmark_10000_sessions_seriell() throws Exception {
		SCMessage request = new SCMessage();
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		int nr = 10000;
		long start = System.currentTimeMillis();
		for (int i = 0; i < nr; i++) {
			if (((i + 1) % 1000) == 0)
				testLogger.info("Creating Session nr. " + (i + 1) + "...");
			response = sessionService.createSession(10, request, msgCallback);
			sessionService.deleteSession(10);
		}
		long stop = System.currentTimeMillis();
		long perf = nr * 1000 / (stop - start);
		testLogger.info(nr + " Sessions created and deleted performance : " + perf + " sessions/sec.");
		Assert.assertTrue("Performence not fast enough, only" + perf + " sess/sec.", perf > 100);
	}

	@Test
	public void benchmark_1000_sessions_paralell() throws Exception {
		SCMessage request = new SCMessage();
		SCMessage response = null;
		int nr = 1000;
		SCSessionService[] sessionServices = new SCSessionService[nr];
		String[] sessionID = new String[nr];
		msgCallback = new MsgCallback(sessionServices[0]);
		
		// create services
		testLogger.info("Creating Services...");
		for (int i = 0; i < nr; i++) {
			if (((i + 1) % 100) == 0)
				testLogger.info("Creating service nr. " + (i + 1) + "...");
			sessionServices[i] = client.newSessionService(TestConstants.sesServiceName1);
		}
		long start = System.currentTimeMillis();
		
		//create sessions
		for (int i = 0; i < nr; i++) {
			if (((i + 1) % 100) == 0)
				testLogger.info("Creating session nr. " + (i + 1) + "...");
			response = sessionServices[i].createSession(10, request, msgCallback);
			sessionID[i] = sessionServices[i].getSessionId();
		}
		long stop = System.currentTimeMillis();
		long perf1 = nr * 1000 / (stop - start);

		// check duplicate sessionIDs
		Arrays.sort(sessionID);
		boolean duplicates = false;
		for (int i = 1; i < nr; i++) {
			if (sessionID[i].equals(sessionID[i - 1])) {
				duplicates = true;
				break;
			}
		}
		Assert.assertEquals("sessions not unique", false, duplicates);
		//delete sessions
		start = System.currentTimeMillis();
		for (int i = 0; i < nr; i++) {
			if (((i + 1) % 100) == 0)
				testLogger.info("Deleting session nr. " + (i + 1) + "...");
			sessionServices[i].deleteSession(10);
		}
		stop = System.currentTimeMillis();
		long perf2 = nr * 1000 / (stop - start);
		testLogger.info(nr + " Session creation performance : " + perf1 + " sessions/sec.");
		testLogger.info(nr + " Session deletion performance : " + perf2 + " sessions/sec.");
		Assert.assertTrue("Performence not fast enough, only" + perf1 + " sess/sec.", perf1 > 100);
		Assert.assertTrue("Performence not fast enough, only" + perf2 + " sess/sec.", perf2 > 500);

	}

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
	//		Assert.assertEquals(true, result < 25000);
	//	}
	//
}
