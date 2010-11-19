package org.serviceconnector.srv;

import org.serviceconnetor.TestConstants;

public class TestServer {

	public static void main(String[] args) {
		String[] arguments = new String[args.length - 1];
		System.arraycopy(args, 1, arguments, 0, args.length - 1);
		
		if (args[0].equals(TestConstants.sessionSrv)) {
			TestSessionServer sessionServer = new TestSessionServer();
			sessionServer.runSessionServer(arguments);
		
		} else if (args[0].equals(TestConstants.publishSrv)) {
			TestPublishServer publishServer = new TestPublishServer();
			publishServer.runPublishServer(arguments);
		}
	}
}
