package org.serviceconnector.cln;

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.cln.IPublishService;
import org.serviceconnector.api.cln.ISCClient;
import org.serviceconnector.api.cln.IService;
import org.serviceconnector.api.cln.SCClient;


public class DemoPublishClient extends Thread {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(DemoPublishClient.class);
	
	public static void main(String[] args) {
		DemoPublishClient demoPublishClient = new DemoPublishClient();
		demoPublishClient.start();
	}

	@Override
	public void run() {
		ISCClient sc = new SCClient();
		IPublishService publishService = null;
		try {
			((SCClient) sc).setConnectionType("netty.tcp");
			sc.attach("localhost", 7000);
			publishService = sc.newPublishService("publish-simulation");
			publishService.subscribe("0000121ABCDEFGHIJKLMNO-----------X-----------", "sessionInfo", 300,
					new DemoSessionClientCallback(publishService));

			while (true)
				;
		} catch (Exception e) {
			logger.error("run", e);
		} finally {
			try {
				publishService.unsubscribe();
				sc.detach();
			} catch (Exception e) {
				logger.info("run "+e.getMessage());
			}
		}
	}

	private class DemoSessionClientCallback extends SCMessageCallback {

		public DemoSessionClientCallback(IService service) {
			super(service);
		}

		@Override
		public void callback(SCMessage reply) {
			System.out.println("Publish client received: " + reply.getData());
		}

		@Override
		public void callback(Exception e) {
		}
	}
}