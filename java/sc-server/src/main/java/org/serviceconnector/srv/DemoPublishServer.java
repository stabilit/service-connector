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

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCPublishMessage;
import org.serviceconnector.api.srv.SCPublishServer;
import org.serviceconnector.api.srv.SCPublishServerCallback;
import org.serviceconnector.api.srv.SCServer;

public class DemoPublishServer extends Thread {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(DemoPublishServer.class);

	/**
	 * Main method if you like to start in debug mode.
	 */
	public static void main(String[] args) throws Exception {
		DemoPublishServer publishServer = new DemoPublishServer();
		publishServer.run();
	}

	@Override
	public void run() {

		SCServer sc = new SCServer("localhost", 9000, 9001); // regular, defaults documented in javadoc
		//SCServer sc = new SCServer("localhost", 9000, 9001, ConnectionType.NETTY_TCP); // alternative with connection type

		sc.setKeepAliveIntervalInSeconds(10); // can be set before register
		sc.setImmediateConnect(true); // can be set before register
		try {
			sc.startListener(); // regular
			
			String serviceName = "publish-1";
			SCPublishServer publishSrv = sc.newPublishServer(serviceName); // no other params possible

			int maxSessions = 10;
			int maxConnections = 5;
			SCPublishServerCallback cbk = new SrvCallback(publishSrv);

			publishSrv.register(maxSessions, maxConnections, cbk); // regular
			// publishSrv.registerServer(10, maxSessions, maxConnections, cbk); // alternative with operation timeout

			SCPublishMessage pubMessage = new SCPublishMessage();
			for (int i = 0; i < 10; i++) {
				pubMessage.setData("publish message nr : " + i);
				pubMessage.setMask("0000121%%%%%%%%%%%%%%%-----------X-----------");
				//publishSrv.publish(pubMessage); // regular
				publishSrv.publish(10, pubMessage); // alternative with operation timeout
				Thread.sleep(1000);
			}
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

	private class SrvCallback extends SCPublishServerCallback {

		/** The Constant logger. */
		protected final Logger logger = Logger.getLogger(SrvCallback.class);
		private SCPublishServer server;

		public SrvCallback(SCPublishServer publishSrv) {
			this.server = publishSrv;
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
						this.server.deregister();
						//SCServer sc = server.getSCServer().stopListener();
					} catch (Exception ex) {
						logger.error("unsubscribe", ex);
					}
				}
			}
		}
	}
}