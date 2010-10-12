package org.serviceconnector.srv;

import org.serviceconnector.ctrl.util.TestConstants;

public class SessionAndPublishServer {

	public static void main(String[] args) {
		String[] arguments = new String[args.length - 1];
		System.arraycopy(args, 1, arguments, 0, args.length - 1);
		if (args[0].equals(TestConstants.sessionSrv)) {
			StartSessionServer sessionServer = new StartSessionServer();
			sessionServer.runSessionServer(arguments);
		} else if (args[0].equals(TestConstants.publishSrv)) {
			StartPublishServer publishServer = new StartPublishServer();
			publishServer.runPublishServer(arguments);
		}
	}
}
