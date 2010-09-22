package org.serviceconnector.cln;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.ISCClient;
import org.serviceconnector.api.cln.ISessionService;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.ctrl.util.ThreadSafeCounter;
import org.serviceconnector.log.Loggers;

public class PerformanceSessionClient implements Runnable {

	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PerformanceSessionClient.class);
	private final CountDownLatch startSignal;
	private final CountDownLatch doneSignal;
	
	private ThreadSafeCounter counter;

	public PerformanceSessionClient(CountDownLatch startSignal, CountDownLatch doneSignal, ThreadSafeCounter counter) {
		this.startSignal = startSignal;
		this.doneSignal = doneSignal;
		this.counter = counter;
	}

	@Override
	public void run() {
		ISCClient client = new SCClient();
		((SCClient) client).setConnectionType("netty.tcp");
		long start = System.currentTimeMillis();

		try {
			client.attach(TestConstants.HOST, TestConstants.PORT9000);
			// wait for signal to start cycle
			startSignal.await();

			for (int i = 0; i < 100; i++) {
				ISessionService service = client.newSessionService(TestConstants.serviceName);
				service.createSession("sessionInfo", 300);
				for (int j = 0; j < 10; j++) {
					service.execute(new SCMessage(new byte[128]));
					counter.increment();
				}
				service.deleteSession();
			}
			// signal that this worker is done

		} catch (Exception e) {
			logger.fatal("run", e);
		} finally {
			try {
				client.detach();
			} catch (Exception e) {
				logger.error("run", e);
			}
			long stop = System.currentTimeMillis();
			testLogger.info("Performance client threadId: " + Thread.currentThread().getId()
					+ " has taken " + (stop - start)
					+ "ms to execute " + counter + "messages.\nCurrent number on the latch:\t" + doneSignal.getCount());
			doneSignal.countDown();
		}
	}
}
