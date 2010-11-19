package org.serviceconnector.test;

import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnetor.TestConstants;

public class ThreadTest {

	private static ProcessesController ctrl;
	private static Process scProcess;
	private static Process srvProcess;

	public static void main(String[] args) throws Exception {
		try {
			ThreadTest test = new ThreadTest();
			test.run();
		} finally {
			scProcess.destroy();
			srvProcess.destroy();
		}
	}

	public void run() throws Exception {
		ctrl = new ProcessesController();
		scProcess = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		srvProcess = ctrl.startServer(TestConstants.sessionSrv, TestConstants.log4jSrvProperties, 9001, TestConstants.PORT_TCP, 100,
				new String[] { TestConstants.sessionServiceName });
	}
}
