package org.serviceconnector.cln;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.ISCClient;
import org.serviceconnector.api.cln.ISessionService;
import org.serviceconnector.api.cln.SCClient;
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
			
			for (int i = 0; i < 100; i++) {
				ISessionService service = client.newSessionService(TestConstants.serviceName);
				service.createSession("sessionInfo", 300);
				for (int j = 0; j < 10; j++) {
					service.execute(new SCMessage(new byte[128]));
				}
				service.deleteSession();
			}
			
			//signal that this worker is done
			doneSignal.countDown();

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
