package org.serviceconnector.cln;


public class DemoClient {

	public static void main(String[] args) throws Exception {

		DemoSessionClient demoSessionClient = new DemoSessionClient();
		DemoPublishClient demoPublishClient = new DemoPublishClient();
		DemoFileClient demoFileClient = new DemoFileClient();
		
		demoSessionClient.start();	
		demoPublishClient.start();
		//demoFileClient.start();
	}
}
