package com.stabilit.scm.cln.rr;

import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.IService;
import com.stabilit.scm.cln.service.ISessionService;
import com.stabilit.scm.common.scmp.ISCMPSynchronousCallback;
import com.stabilit.scm.common.service.ISCClient;
import com.stabilit.scm.common.service.ISCMessage;
import com.stabilit.scm.common.service.ISCMessageCallback;
import com.stabilit.scm.common.service.SCMessage;
import com.stabilit.scm.common.util.SynchronousCallback;

public class DemoSessionClient extends Thread {

	public static void main(String[] args) {
		DemoSessionClient demoSessionClient = new DemoSessionClient();
		demoSessionClient.start();
	}

	@Override
	public void run() {
		ISCClient sc = new SCClient("localhost", 8080);
		ISessionService sessionService = sc.newSessionService("simulation");

		try {
			sessionService.createSession("sessionInfo", 60, 60);

			int index = 0;
			while (true) {
				SCMessage requestMsg = new SCMessage();
				requestMsg.setData("body nr : " + index++);
				ISCMessageCallback callback = new DemoSessionClientCallback(sessionService);
				sessionService.execute(requestMsg, callback);
				ISCMPSynchronousCallback syncCallback = (ISCMPSynchronousCallback) callback;
				syncCallback.getMessageSync();
				Thread.sleep(500);
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

	private class DemoSessionClientCallback extends SynchronousCallback implements ISCMessageCallback {

		private IService service;

		public DemoSessionClientCallback(IService service) {
			this.service = service;
		}

		@Override
		public void callback(ISCMessage reply) throws Exception {
		}

		@Override
		public IService getService() {
			return this.service;
		}
	}
}