package com.stabilit.scm.cln;

import com.stabilit.scm.cln.ps.DemoPublishClient;
import com.stabilit.scm.cln.rr.DemoSessionClient;

public class SessionAndPublishClient {

	public static void main(String[] args) throws Exception {

		DemoSessionClient demoSessionClient = new DemoSessionClient();
		demoSessionClient.start();

		DemoPublishClient demoPublishClient = new DemoPublishClient();
		demoPublishClient.start();
	}
}
