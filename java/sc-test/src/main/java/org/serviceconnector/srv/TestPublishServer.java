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

import java.lang.reflect.Method;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageFault;
import org.serviceconnector.api.SCPublishMessage;
import org.serviceconnector.api.srv.SCPublishServer;
import org.serviceconnector.api.srv.SCPublishServerCallback;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ThreadSafeCounter;
import org.serviceconnector.util.FileUtility;

public class TestPublishServer extends TestStatefulServer {

	static {
		TestStatefulServer.logger = Logger.getLogger(TestPublishServer.class);
	}

	/**
	 * Main method if you like to start in debug mode.
	 * 
	 * @param args
	 *            [0] serverName<br>
	 *            [1] listenerPort<br>
	 *            [2] SC port<br>
	 *            [3] maxSessions<br>
	 *            [4] maxConnections<br>
	 *            [5] connectionType<br>
	 *            ("netty.tcp" or "netty.http") [6] serviceNames (comma delimited list)<br>
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
		server.setConnectionType(args[5]);
		server.setServiceNames(args[6]);
		server.run();
	}

	@Override
	public void run() {
		// add exit handler
		this.addExitHandler(FileUtility.getPath() + fs + this.serverName + ".pid");

		ctr = new ThreadSafeCounter();
		SCServer sc = new SCServer(TestConstants.HOST, this.port, this.listenerPort, this.connectionType);
		try {
			sc.setKeepAliveIntervalSeconds(10);
			sc.setImmediateConnect(true);
			sc.startListener();

			String[] serviceNames = this.serviceNames.split(",");
			for (String serviceName : serviceNames) {
				SCPublishServer server = sc.newPublishServer(serviceName);
				SCPublishServerCallback cbk = new SrvCallback(server);
				try {
					server.register(10, this.maxSessions, this.maxConnections, cbk);
				} catch (Exception e) {
					logger.error("runSessionServer", e);
					server.deregister();
				}
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

	private class PublishThread extends Thread {
		SCPublishServer publishSrv;
		Method method;
		SCMessage request;
		int operationTimeoutInMillis = 0;

		public PublishThread(SCPublishServer publishSrv, Method method, SCMessage request, int operationTimeoutInMillis) {
			this.publishSrv = publishSrv;
			this.method = method;
		}

		/** {@inheritDoc} */
		@Override
		public void run() {

			try {
				// first sleep 2 seconds to give test client time to stay ready
				Thread.sleep(2000);
				method.invoke(this, request, operationTimeoutInMillis);
			} catch (Exception e1) {
				logger.warn("could not invoke " + method.getName() + "successfully.");
				return;
			}
			logger.log(Level.OFF, "executed method " + method.getName() + " on server");

		}

		/**
		 * This method might get invoked by reflection if client requests it in sessionInfo of a subscribe message
		 * 
		 * @param request
		 * @param operationTimeoutInMillis
		 */
		public void publish100Message(SCMessage request, int operationTimeoutInMillis) {
			SCPublishMessage pubMessage = new SCPublishMessage();
			for (int i = 0; i < 100; i++) {
				try {
					pubMessage.setMask("0000121%%%%%%%%%%%%%%%-----------X-----------");
					pubMessage.setData("publish message nr : " + i);
					this.publishSrv.publish(pubMessage); // regular
					Thread.sleep(1000);
				} catch (Exception e) {
					// quit loop in case of a publish error
					break;
				}
			}
		}
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
			SCMessage response = message;
			Object data = message.getData();
			if (data == null) {
				return response;
			}
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
					KillThread<SCPublishServer> kill = new KillThread<SCPublishServer>(this.scPublishServer);
					kill.start();
				} else {
					// watch out for method to call
					String methodName = message.getSessionInfo();
					if (methodName != null) {
						try {
							Method method = this.getClass().getMethod(methodName, SCMessage.class, int.class);
							PublishThread publishThread = new PublishThread(this.scPublishServer, method, message,
									operationTimeoutInMillis);
							publishThread.start();
							return response;
						} catch (Exception e) {
							logger.warn("method " + methodName + " not found on server");
						}
					}
				}

			}
			return response;
		}

		@Override
		public void unsubscribe(SCMessage message, int operationTimeoutInMillis) {
			logger.info("PublishServer.SrvCallback.unsubscribe()");
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
