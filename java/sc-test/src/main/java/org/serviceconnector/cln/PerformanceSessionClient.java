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
package org.serviceconnector.cln;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.ctrl.util.ThreadSafeCounter;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;

public class PerformanceSessionClient implements Runnable {

	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PerformanceSessionClient.class);
	private final CountDownLatch beforeAttachSignal;
	private final CountDownLatch afterAttachSignal;
	private final CountDownLatch attachedSignal;
	private final CountDownLatch doneSignal;

	private ThreadSafeCounter counter;

	private final int sessionCycles;
	private final int executeCycles;
	private final int messageSize;

	public PerformanceSessionClient(CountDownLatch beforeAttachSignal,
			CountDownLatch afterAttachSignal, CountDownLatch attachedSignal,
			CountDownLatch doneSignal, ThreadSafeCounter counter, int sessionCycles,
			int executeCycles, int messageSize) {
		this.beforeAttachSignal = beforeAttachSignal;
		this.afterAttachSignal = afterAttachSignal;
		this.attachedSignal = attachedSignal;
		this.doneSignal = doneSignal;
		this.counter = counter;
		this.sessionCycles = sessionCycles;
		this.executeCycles = executeCycles;
		this.messageSize = messageSize;
	}

	@Override
	public void run() {
		SCClient sc = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		long start = System.currentTimeMillis();
		try {
			// wait for signal to start cycle
			beforeAttachSignal.await();
			try {
				sc.attach();
			} catch (Exception e) {
				testLogger.info("attach failed");
			} finally {
				attachedSignal.countDown();
			}

			afterAttachSignal.await();

			for (int i = 0; i < sessionCycles; i++) {
				SCSessionService service = sc.newSessionService(TestConstants.sessionServiceName);
				SCMessage scMessage = new SCMessage();
				scMessage.setSessionInfo("sessionInfo");
				service.createSession(300, scMessage);
				for (int j = 0; j < executeCycles; j++) {
					service.execute(new SCMessage(new byte[messageSize]));
					counter.increment();
				}
				service.deleteSession();
			}

		} catch (Exception e) {
			logger.fatal("run", e);
		} finally {
			try {
				sc.detach();
			} catch (Exception e) {
				testLogger.info("detach failed");
			}
			long stop = System.currentTimeMillis();
			// signal that this worker is done
			doneSignal.countDown();

			testLogger.info("Performance client has taken " + (stop - start) + "ms to execute "
					+ counter + " messages of " + messageSize + "B.\nCurrent number on the latch:\t"
					+ doneSignal.getCount());
		}
	}
}
