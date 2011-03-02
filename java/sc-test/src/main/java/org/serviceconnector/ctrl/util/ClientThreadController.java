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
package org.serviceconnector.ctrl.util;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestSessionServiceMessageCallback;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.cln.PerformanceSessionClient;
import org.serviceconnector.log.Loggers;

public class ClientThreadController {

	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private final static Logger LOGGER = Logger.getLogger(ClientThreadController.class);

	private CountDownLatch beforeAttachSignal;
	private CountDownLatch attachedSignal;
	private CountDownLatch afterAttachSignal;
	private CountDownLatch doneSignal;

	private boolean waitBeforeAttach;
	private boolean waitAfterAttach;
	private int clientsCount;
	private int sessionsCount;
	private int executesPerSessionCount;
	private int messageSize;

	public ClientThreadController(boolean waitBeforeAttach, boolean waitAfterAttach, int clientsCount,
			int sessionsCount, int executesPerSessionCount, int messageSize) {
		this.waitBeforeAttach = waitBeforeAttach;
		this.waitAfterAttach = waitAfterAttach;
		this.clientsCount = clientsCount;
		this.sessionsCount = sessionsCount;
		this.executesPerSessionCount = executesPerSessionCount;
		this.messageSize = messageSize;

		// prepare synchronizing tools
		if (waitBeforeAttach) {
			beforeAttachSignal = new CountDownLatch(1);
		} else {
			beforeAttachSignal = new CountDownLatch(0);
		}
		if (waitAfterAttach) {
			afterAttachSignal = new CountDownLatch(1);
		} else {
			afterAttachSignal = new CountDownLatch(0);
		}
		attachedSignal = new CountDownLatch(clientsCount);
		doneSignal = new CountDownLatch(clientsCount);
	}

	public long perform() throws Exception {
		long start;
		long stop;
		ThreadSafeCounter[] messages = new ThreadSafeCounter[clientsCount];

		for (int i = 0; i < clientsCount; i++) {
			messages[i] = new ThreadSafeCounter();
			new Thread(new PerformanceSessionClient(beforeAttachSignal, afterAttachSignal, attachedSignal, doneSignal,
					messages[i], sessionsCount, executesPerSessionCount, messageSize)).start();
		}

		start = System.currentTimeMillis();
		if (waitBeforeAttach) {
			beforeAttachSignal.countDown();
		}
		attachedSignal.await();

		if (waitAfterAttach) {
			afterAttachSignal.countDown();
		}

		doneSignal.await();

		stop = System.currentTimeMillis();

		long result = stop - start;

		logResults(messages, result);

		return result;
	}

	private void logResults(ThreadSafeCounter[] messages, long result) {
		SCMessage response = new SCMessage("0");
		int sum = 0;
		for (int i = 0; i < messages.length; i++) {
			sum += messages[i].value();
		}
		try {
			SCClient sc = new SCClient(TestConstants.HOST, TestConstants.PORT_SC_HTTP);
			sc.attach();
			SCSessionService service = sc.newSessionService(TestConstants.sesServiceName1);
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			service.createSession(300, scMessage, new TestSessionServiceMessageCallback(service));
			response = service.execute(new SCMessage("executed"));
			service.deleteSession();
			sc.detach();
		} catch (Exception e) {
		}

		testLogger.info("Messages executed successfuly (clients):\t" + sum);
		testLogger.info("Messages executed successfuly (server):\t" + response.getData().toString());
		testLogger.info("Time to create session execute and delete session:\t" + result + "ms");

	}
}
