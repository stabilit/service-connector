package org.serviceconnector.cln;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.serviceconnector.ctrl.util.TestConstants;


public class PerformanceSessionClient implements Runnable {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PerformanceSessionClient.class);
	private final CountDownLatch startSignal;
	private final CountDownLatch doneSignal;

	public PerformanceSessionClient(CountDownLatch startSignal, CountDownLatch doneSignal) {
		this.startSignal = startSignal;
		this.doneSignal = doneSignal;
	}

	@Override
	public void run() {
		ISCClient client = new SCClient();

		try {
			client.attach(TestConstants.HOST, TestConstants.PORT8080);
			//wait for signal to start cycle
			startSignal.await();
			
			for(int i = 0; i < 100; i++) {
				ISessionService service = client.newSessionService(TestConstants.serviceName);
				service.createSession("sessionInfo", 300);
			}
			

		} catch (Exception e) {
			logger.error("run", e);
		} finally {
			try {
				client.detach();
			} catch (Exception e) {
				logger.error("run", e);
			}
		}
	}

}
