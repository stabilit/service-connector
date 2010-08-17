package com.stabilit.scm.cln.ps;

import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.IService;
import com.stabilit.scm.common.service.IPublishService;
import com.stabilit.scm.common.service.ISCClient;
import com.stabilit.scm.common.service.ISCMessage;
import com.stabilit.scm.common.service.SCMessageCallback;

public class DemoPublishClient extends Thread {

	public static void main(String[] args) {
		DemoPublishClient demoPublishClient = new DemoPublishClient();
		demoPublishClient.start();
	}

	@Override
	public void run() {
		ISCClient sc = new SCClient("localhost", 8000);
		IPublishService publishService = null;
		try {
			sc.attach();
			publishService = sc.newPublishService("publish-simulation");
			publishService.subscribe("0000121ABCDEFGHIJKLMNO-----------X-----------", "sessionInfo", 300,
					new DemoSessionClientCallback(publishService));

			while (true)
				;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				publishService.unsubscribe();
				sc.detach();
			} catch (Exception e) {
			}
		}
	}

	private class DemoSessionClientCallback extends SCMessageCallback {

		public DemoSessionClientCallback(IService service) {
			super(service);
		}

		@Override
		public void callback(ISCMessage reply) {
			System.out.println("Publish client received: " + reply.getData());
		}

		@Override
		public void callback(Throwable th) {
		}
	}
}