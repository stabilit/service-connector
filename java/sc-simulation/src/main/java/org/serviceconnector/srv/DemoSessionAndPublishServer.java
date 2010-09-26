package org.serviceconnector.srv;


public class DemoSessionAndPublishServer {

	public static void main(String[] args) {
		DemoSessionServer sessionServer = new DemoSessionServer();
		DemoPublishServer publishServer = new DemoPublishServer();
			
		sessionServer.runSessionServer();
		publishServer.runPublishServer();
	}
}
