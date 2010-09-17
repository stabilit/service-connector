package org.serviceconnector.cln;


public class SessionAndPublishClient {

	public static void main(String[] args) throws Exception {

		DemoSessionClient demoSessionClient = new DemoSessionClient();
		demoSessionClient.start();

		DemoPublishClient demoPublishClient = new DemoPublishClient();
		demoPublishClient.start();
	}
}
