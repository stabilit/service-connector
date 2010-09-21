package org.serviceconnector.cln;

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.cln.ISCClient;
import org.serviceconnector.api.cln.IService;
import org.serviceconnector.api.cln.ISessionService;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.sc.service.ISCMessageCallback;


public class DemoSessionClient extends Thread {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(DemoSessionClient.class);
	
	private static boolean pendingRequest = false;

	public static void main(String[] args) {
		DemoSessionClient demoSessionClient = new DemoSessionClient();
		demoSessionClient.start();
	}

	@Override
	public void run() {
		ISCClient sc = new SCClient();
		ISessionService sessionService = null;

		try {
			sc.attach("localhost", 8000);
			sessionService = sc.newSessionService("simulation");
			sessionService.createSession("sessionInfo", 300, 60);

			int index = 0;
			while (true) {
				SCMessage requestMsg = new SCMessage();
				requestMsg.setData("body nr : " + index++);
				ISCMessageCallback callback = new DemoSessionClientCallback(sessionService);
				DemoSessionClient.pendingRequest = true;
				sessionService.execute(requestMsg, callback);
				while (DemoSessionClient.pendingRequest) {
					Thread.sleep(500);
				}
			}
		} catch (Exception e) {
			logger.error("run", e);
		} finally {
			try {
				sessionService.deleteSession();
				sc.detach();
			} catch (Exception e) {
				logger.error("run", e);
			}
		}
	}

	private class DemoSessionClientCallback extends SCMessageCallback {

		private IService service;

		public DemoSessionClientCallback(IService service) {
			super(service);
		}

		@Override
		public void callback(SCMessage reply) {
			System.out.println("Session client received: " + reply.getData());
			DemoSessionClient.pendingRequest = false;
		}

		@Override
		public IService getService() {
			return this.service;
		}

		@Override
		public void callback(Exception e) {
		}
	}
}