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

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCPublishMessage;
import org.serviceconnector.api.srv.SCPublishServer;
import org.serviceconnector.api.srv.SCPublishServerCallback;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.net.ConnectionType;

public class DemoPublishServer {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(DemoPublishServer.class);

	private SCPublishServer publishSrv = null;
	private static String serviceName = "local-publish-service";
	private static boolean killPublishServer = false;

	public static void main(String[] args) throws Exception {
		DemoPublishServer publishServer = new DemoPublishServer();
		publishServer.runPublishServer();
	}

	public void runPublishServer() {

		SCServer sc = new SCServer("localhost", 9000, 9001); // regular, defaults documented in javadoc
		sc = new SCServer("localhost", 9000, 9001, ConnectionType.NETTY_TCP); // alternative with connection type

		sc.setKeepAliveIntervalInSeconds(10); // can be set before register
		sc.setImmediateConnect(true); // can be set before register
		try {
			sc.startListener(); // regular

			// TODO TRN better solution next line
			// publishSrv = sc.newPublishServer(serviceName); // no other params possible
			publishSrv = sc.newPublishServer(); // no other params possible

			int maxSessions = 10;
			int maxConnections = 5;
			SCPublishServerCallback cbk = new SrvCallback(publishSrv);

			publishSrv.registerServer(serviceName, maxSessions, maxConnections, cbk); // regular
//			publishSrv.registerServer(10, serviceName, maxSessions, maxConnections, cbk); // alternative with operation timeout

			SCPublishMessage pubMessage = new SCPublishMessage();
			for (int i = 0; i < 10; i++) {
				pubMessage.setData("publish message nr : " + i);
				pubMessage.setMask("0000121%%%%%%%%%%%%%%%-----------X-----------");
				publishSrv.publish(serviceName, pubMessage); // regular
				publishSrv.publish(10, serviceName, pubMessage); // alternative with operation timeout

				Thread.sleep(1000);
			}
		} catch (Exception e) {
			logger.error("runPublishServer", e);
		} finally {
			try {
				publishSrv.deregisterServer(serviceName);
//				publishSrv.deregisterServer(10, serviceName);
			} catch (Exception e1) {
				logger.error("run", e1);
			}
			sc.stopListener();
		}
	}

	// public void runPublishServer() {
	// try {
	// this.publishSrv = new SCPublishServer();
	// // connect to SC as server
	// this.publishSrv.setImmediateConnect(true);
	// this.publishSrv.startListener("localhost", 9002, 0);
	// SrvCallback srvCallback = new SrvCallback(new PublishServerContext());
	// this.publishSrv.registerServer("localhost", 9000, serviceName, 10, 10, srvCallback);
	// Runnable runnable = new PublishRun(publishSrv, serviceName);
	// Thread thread = new Thread(runnable);
	// thread.start();
	// } catch (Exception ex) {
	// logger.error("runPublishServer", ex);
	// this.shutdown();
	// }
	// }
	//
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
	// while (!DemoPublishServer.killPublishServer) {
	// try {
	// if (index % 3 == 0) {
	// Thread.sleep(1000);
	// } else {
	// Thread.sleep(5000);
	// }
	// Object data = "publish message nr " + ++index;
	// SCPublishMessage publishMessage = new SCPublishMessage();
	// publishMessage.setMask("0000121%%%%%%%%%%%%%%%-----------X-----------");
	// publishMessage.setData(data);
	// server.publish(serviceName, publishMessage);
	// logger.info("Message published: " + data);
	// } catch (Exception ex) {
	// logger.error("run", ex);
	// return;
	// }
	// }
	// }
	// }
	//
	// private void shutdown() {
	// DemoPublishServer.killPublishServer = true;
	// try {
	// this.publishSrv.deregisterServer(serviceName);
	// } catch (Exception ex) {
	// logger.error("shutdown", ex);
	// this.publishSrv = null;
	// }
	// }

	private class SrvCallback extends SCPublishServerCallback {

		/** The Constant logger. */
		protected final Logger logger = Logger.getLogger(SrvCallback.class);
		private SCPublishServer publishSrv;

		public SrvCallback(SCPublishServer publishSrv) {
			this.publishSrv = publishSrv;
		}

		@Override
		public SCMessage changeSubscription(SCMessage message, int operationTimeoutInMillis) {
			logger.info("PublishServer.SrvCallback.changeSubscription()");
			return message;
		}

		@Override
		public SCMessage subscribe(SCMessage message, int operationTimeoutInMillis) {
			logger.info("PublishServer.SrvCallback.subscribe()");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException ex) {
				logger.error("subscribe", ex);
			}
			return message;
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
						this.publishSrv.deregisterServer("local-publish-service");
					} catch (Exception ex) {
						logger.error("unsubscribe", ex);
					}
				}
			}
		}

	}
}