/*
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 */
package org.serviceconnector.cln;

import java.io.File;
import java.io.FileWriter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.serviceconnector.TestConstants;
import org.serviceconnector.srv.TestPublishServer;
import org.serviceconnector.srv.TestSessionServer;

public class TestClient {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(TestClient.class);

	/**
	 * start test process
	 * 
	 * @param args
	 *            [0] clientType ("session" or "publish")<br>
	 *            [1] PID file<br>
	 *            [2] SC host<br>
	 *            [3] SC port<br>
	 *            [4] connectionType<br>
	 *            [5] maxConnections<br>
	 *            [6] keepAliveIntervalSeconds (0 = disabled)<br>
	 *            [7] serviceName
	 */
	public static void main(String[] args) {
		logger.log(Level.OFF, "TestClient starting ...");

		for (int i = 0; i < 7; i++) {
			logger.log(Level.OFF, "args[" + i + "]:" + args[i]);
		}
		TestClient testclient = new TestClient();
		String pidFileNameFull = args[1];
		try {
			testclient.createPIDfile(pidFileNameFull);
		} catch (Exception e) {
			e.printStackTrace();
		}
		testclient.addExitHandler(pidFileNameFull);

		if (args[0].equals(TestConstants.SERVER_TYPE_SESSION)) {
			TestSessionServer server = new TestSessionServer();
			server.setListenerPort(Integer.parseInt(args[2]));
			server.setPort(Integer.parseInt(args[3]));
			server.setMaxSessions(Integer.parseInt(args[4]));
			server.setMaxConnections(Integer.parseInt(args[5]));
			server.setServiceNames(args[6]);
			server.start();

		} else if (args[0].equals(TestConstants.SERVER_TYPE_PUBLISH)) {
			TestPublishServer server = new TestPublishServer();
			server.setListenerPort(Integer.parseInt(args[2]));
			server.setPort(Integer.parseInt(args[3]));
			server.setMaxSessions(Integer.parseInt(args[4]));
			server.setMaxConnections(Integer.parseInt(args[5]));
			server.setServiceNames(args[6]);
			server.start();
		}
	}

	/**
	 * Create file containing the PID of the SC process. Is used for testing purpose to verify that SC is running properly.
	 */
	private void createPIDfile(String fileNameFull) throws Exception {
		FileWriter fw = null;
		try {
			String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
			long pid = Long.parseLong(processName.split("@")[0]);

			// create the pid file
			File pidFile = new File(fileNameFull);
			fw = new FileWriter(pidFile);
			fw.write("pid: " + pid);
			fw.flush();
			logger.log(Level.OFF, "Create PID-file: " + fileNameFull + " PID:" + pid);
		} finally {
			if (fw != null) {
				fw.close();
			}
		}
	}

	/**
	 * Adds the shutdown hook.
	 */
	private void addExitHandler(String pidFileNameFull) {
		TestClientExitHandler exitHandler = new TestClientExitHandler(pidFileNameFull);
		Runtime.getRuntime().addShutdownHook(exitHandler);
	}

	/**
	 * The Class TestClientExitHandler.
	 */
	private static class TestClientExitHandler extends Thread {
		private String pidFileNameFull = null;

		public TestClientExitHandler(String pidFileNameFull) {
			this.pidFileNameFull = pidFileNameFull;
		}

		@Override
		public void run() {
			File pidFile = new File(this.pidFileNameFull);
			if (pidFile.exists()) {
				pidFile.delete();
				logger.log(Level.OFF, "Delete PID-file: " + this.pidFileNameFull);
			}
			logger.log(Level.OFF, "TestClient exiting");
			logger.log(Level.OFF, "<<<");
		}
	}
}
