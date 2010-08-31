package com.stabilit.scm.cln;

import org.apache.log4j.Logger;

import com.stabilit.scm.cln.service.ISCClient;
import com.stabilit.scm.cln.service.IService;
import com.stabilit.scm.cln.service.ISessionService;
import com.stabilit.scm.common.service.ISCMessage;
import com.stabilit.scm.common.service.ISCMessageCallback;
import com.stabilit.scm.common.service.SCMessage;
import com.stabilit.scm.common.service.SCMessageCallback;

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
		public void callback(ISCMessage reply) {
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