package com.stabilit.scm.srv;


public class SessionAndPublishServer {

	public static void main(String[] args) {
		DemoSessionServer sessionServer = new DemoSessionServer();
		DemoPublishServer publishServer = new DemoPublishServer();
			
		sessionServer.runSessionServer();
		publishServer.runPublishServer();
	}
}
