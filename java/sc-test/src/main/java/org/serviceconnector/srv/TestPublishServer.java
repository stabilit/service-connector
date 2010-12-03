/*
 *-----------------------------------------------------------------------------*
 *                                                                             *
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package org.serviceconnector.srv;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageFault;
import org.serviceconnector.api.SCPublishMessage;
import org.serviceconnector.api.srv.SCPublishServer;
import org.serviceconnector.api.srv.SCPublishServerCallback;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ThreadSafeCounter;
import org.serviceconnector.util.FileUtility;

public class TestPublishServer extends Thread {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(TestPublishServer.class);

	private int listenerPort;
	private int port;
	private int maxSessions;
	private int maxConnections;
	private String serviceNames;
	private String pidFile;
	private String serverName;
	private static final String fs = System.getProperty("file.separator");
	private ThreadSafeCounter ctr;

	/**
	 * Main method if you like to start in debug mode.
	 * 
	 * @param args
	 *            [0] serverName<br>
	 *            [1] listenerPort<br>
	 *            [2] SC port<br>
	 *            [3] maxSessions<br>
	 *            [4] maxConnections<br>
	 *            [5] serviceNames (comma delimited list)<br>
	 */
	public static void main(String[] args) throws Exception {
		logger.log(Level.OFF, "TestPublishServer is starting ...");
		for (int i = 0; i < 7; i++) {
			logger.log(Level.OFF, "args[" + i + "]:" + args[i]);
		}
		TestPublishServer server = new TestPublishServer();
		server.setServerName(args[0]);
		server.setListenerPort(Integer.parseInt(args[1]));
		server.setPort(Integer.parseInt(args[2]));
		server.setMaxSessions(Integer.parseInt(args[3]));
		server.setMaxConnections(Integer.parseInt(args[4]));
		server.setServiceNames(args[5]);
		server.run();
	}

	@Override
	public void run() {
		// add exit handler
		this.addExitHandler(FileUtility.getPath() + fs + this.serverName + ".pid");

		ctr = new ThreadSafeCounter();
		SCServer sc = new SCServer(TestConstants.HOST, this.port, this.listenerPort);
		try {
			sc.setKeepAliveIntervalSeconds(10);
			sc.setImmediateConnect(true);
			sc.startListener();
			String serviceName = this.serviceNames; // TODO TRN handle multiple services
			// for (int i = 0; i < serviceNames.length; i++) {
			// }

			SCPublishServer server = sc.newPublishServer(serviceName); // no other params possible
			SCPublishServerCallback cbk = new SrvCallback(server);
			try {
				server.register(10, this.maxSessions, this.maxConnections, cbk);
			} catch (Exception e) {
				logger.error("runPublishServer", e);
				server.deregister();
			}

			SCPublishMessage pubMessage = new SCPublishMessage();
			for (int i = 0; i < 10; i++) {
				pubMessage.setData("publish message nr : " + i);
				pubMessage.setMask("0000121%%%%%%%%%%%%%%%-----------X-----------");
				server.publish(pubMessage); // regular
				server.publish(10, pubMessage); // alternative with operation timeout
				Thread.sleep(1000);
			}
			FileUtility.createPIDfile(FileUtility.getPath() + fs + this.serverName + ".pid");
			logger.log(Level.OFF, "TestPublishServer is running ...");
		} catch (Exception e) {
			logger.error("runPublishServer", e);
		} finally {
			try {
				// publishSrv.deregisterServer();
				// publishSrv.deregisterServer(10, serviceName);
			} catch (Exception e1) {
				logger.error("run", e1);
			}
			// sc.stopListener();
		}
	}

	public void setListenerPort(int listenerPort) {
		this.listenerPort = listenerPort;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setMaxSessions(int maxSessions) {
		this.maxSessions = maxSessions;
	}

	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	public void setServiceNames(String serviceNames) {
		this.serviceNames = serviceNames;
	}

	private class SrvCallback extends SCPublishServerCallback {

		/** The Constant logger. */
		protected final Logger logger = Logger.getLogger(SrvCallback.class);

		public SrvCallback(SCPublishServer publishSrv) {
			super(publishSrv);
		}

		@Override
		public SCMessage changeSubscription(SCMessage message, int operationTimeoutInMillis) {
			logger.info("PublishServer.SrvCallback.changeSubscription()");
			return message;
		}

		@Override
		public SCMessage subscribe(SCMessage message, int operationTimeoutInMillis) {
			logger.info("PublishServer.SrvCallback.subscribe()");
			Object data = message.getData();
			if (data == null) {
				return message;
			}
			SCMessage response = message;
			// watch out for kill server message
			if (data.getClass() == String.class) {
				String dataString = (String) data;

				if (dataString.equals(TestConstants.killServerCmd)) {
					response = new SCMessageFault();
					try {
						((SCMessageFault) response).setAppErrorCode(1050);
						((SCMessageFault) response).setAppErrorText("subscribe rejected - kill server requested!");
					} catch (SCMPValidatorException e) {
					}
					KillThread kill = new KillThread(this.scPublishServer);
					kill.start();
				}
			}
			return response;
		}

		@Override
		public void unsubscribe(SCMessage message, int operationTimeoutInMillis) {
			logger.info("PublishServer.SrvCallback.unsubscribe()");
			Object data = message.getData();
			// watch out for kill server message
			if (data != null && data.getClass() == String.class) {
				String dataString = (String) data;
				if (dataString.equals("kill server")) {
					try {
						this.scPublishServer.deregister();
						this.scPublishServer.getSCServer().stopListener();
					} catch (Exception ex) {
						logger.error("unsubscribe", ex);
					}
				}
			}
		}
	}

	private class KillThread extends Thread {
		private SCSessionServer server;

		public KillThread(SCSessionServer server) {
			this.server = server;
		}

		@Override
		public void run() {
			// sleep for 2 seconds before killing the server
			try {
				this.server.deregister();
			} catch (Exception e) {
				logger.error("run", e);
			} finally {
				try {
					this.server.getSCServer().stopListener();
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					System.exit(0);
				}
			}
		}
	}

	public String getPidFile() {
		return pidFile;
	}

	public void setPidFile(String pidFile) {
		this.pidFile = pidFile;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
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

		@Override
		public void run() {
			FileUtility.deletePIDfile(this.pidFileNameFull);
			logger.log(Level.OFF, "Delete PID-file: " + this.pidFileNameFull);
			logger.log(Level.OFF, "TestServer exiting");
			logger.log(Level.OFF, "<<<");
		}
	}
}

// try {
// // start publishing
// for (int i = 0; i < serviceNames.length; i++) {
// Runnable run = new PublishRun(publishSrv, serviceNames[i]);
// Thread thread = new Thread(run);
// thread.start();
// }
// } catch (Exception ex) {
// logger.error("runPublishServer", ex);
// this.shutdown();
// }

// private static class PublishRun implements Runnable {
// SCPublishServer server;
// String serviceName;
//
// public PublishRun(SCPublishServer server, String serviceName) {
// this.server = server;
// this.serviceName = serviceName;
// }
//
// @Override
// public void run() {
// int index = 0;
// while (!TestPublishServer.killPublishServer) {
// try {
// if (index % 3 == 0) {
// Thread.sleep(1000);
// } else {
// Thread.sleep(2000);
// }
// Object data = "publish message nr " + ++index;
// SCPublishMessage publishMessage = new SCPublishMessage();
// publishMessage.setMask("0000121%%%%%%%%%%%%%%%-----------X-----------");
// publishMessage.setData(data);
// server.publish(serviceName, publishMessage);
// logger.info("message nr " + index + " sent.");
// } catch (Exception ex) {
// logger.error("run", ex);
// return;
// }
// }
// }
// }
//
// private void shutdown() {
// TestPublishServer.killPublishServer = true;
// try {
// for (int i = 0; i < serviceNames.length; i++) {
// this.publishSrv.deregisterServer(serviceNames[i]);
// }
// } catch (Exception ex) {
// logger.error("shutdown", ex);
// this.publishSrv = null;
// }
// }
