package com.stabilit.scm.cln.rr;

import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.ISCClient;
import com.stabilit.scm.cln.service.IService;
import com.stabilit.scm.cln.service.ISessionService;
import com.stabilit.scm.common.service.ISCMessage;
import com.stabilit.scm.common.service.ISCMessageCallback;
import com.stabilit.scm.common.service.SCMessage;
import com.stabilit.scm.common.service.SCMessageCallback;

public class DemoSessionClient extends Thread {

	private static boolean pendingRequest = false;

	public static void main(String[] args) {
		DemoSessionClient demoSessionClient = new DemoSessionClient();
		demoSessionClient.start();
	}

	@Override
	public void run() {
		ISCClient sc = new SCClient("localhost", 8000);
		ISessionService sessionService = sc.newSessionService("simulation");

		try {
			sessionService.createSession("sessionInfo", 60, 60);

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
			e.printStackTrace();
		} finally {
			try {
				sessionService.deleteSession();
				sc.detach();
			} catch (Exception e) {
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
		public void callback(Throwable th) {
		}
	}
}