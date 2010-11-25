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
package org.serviceconnector.srv;

import java.io.File;
import java.io.FileWriter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.serviceconnector.TestConstants;

public class TestServer {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(TestServer.class);

	/**
	 * start server process
	 * 
	 * @param args
	 *            [0] serverType ("session" or "publish")<br>
	 *            [1] PID file<br>
	 *            [2] listenerPort<br>
	 *            [3] SC port<br>
	 *            [4] maxSessions<br>
	 *            [5] maxConnections<br>
	 *            [6...] serviceNames<br>
	 */
	public static void main(String[] args) {
		logger.log(Level.OFF, "TestServer starting ...");
		TestServer testServer = new TestServer();
		String pidFileNameFull = args[1];
		try {
			testServer.createPIDfile(pidFileNameFull);
		} catch (Exception e) {
			e.printStackTrace();
		}
		testServer.addExitHandler(pidFileNameFull);

		// copy arguments, remove the first two arguments
		String[] arguments = new String[args.length - 1];
		System.arraycopy(args, 2, arguments, 0, args.length - 2);

		/* start server process
		 * Args:
		 *  [0] listenerPort
		 *  [1] SC port	
		 *  [2] maxSessions	
		 *  [3] maxConnections			
		 *  [4...] serviceNames
		 */
		if (args[0].equals(TestConstants.sessionSrv)) {
			TestSessionServer sessionServer = new TestSessionServer();
			sessionServer.runSessionServer(arguments);

		} else if (args[0].equals(TestConstants.publishSrv)) {
			TestPublishServer publishServer = new TestPublishServer();
			publishServer.runPublishServer(arguments);
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
			logger.info("Create PID-file: " + fileNameFull + " PID:" + pid);
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
		TestServerExitHandler exitHandler = new TestServerExitHandler(pidFileNameFull);
		Runtime.getRuntime().addShutdownHook(exitHandler);
	}

	/**
	 * The Class TestServerExitHandler.
	 */
	private static class TestServerExitHandler extends Thread {
		private String pidFileNameFull = null;

		public TestServerExitHandler(String pidFileNameFull) {
			this.pidFileNameFull = pidFileNameFull;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			File pidFile = new File(this.pidFileNameFull);
			if (pidFile.exists()) {
				pidFile.delete();
				logger.log(Level.OFF, "Delete PID-file: " + this.pidFileNameFull);
			}
			logger.log(Level.OFF, "TestServer exiting");
			logger.log(Level.OFF, "<<<");
		}
	}

}
