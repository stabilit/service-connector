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
import org.serviceconnector.api.SCSubscribeMessage;
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

		SCServer sc = new SCServer("localhost", 9000, 9001); // regular, defaults documented in java doc

		try {
			sc.setKeepAliveIntervalSeconds(10); // can be set before register
			sc.setImmediateConnect(true); // can be set before register
			sc.startListener(); // regular

			String serviceName = "publish-1";
			SCPublishServer publishSrv = sc.newPublishServer(serviceName);
			try {

				int maxSessions = 10;
				int maxConnections = 5;
				SCPublishServerCallback cbk = new SrvCallback(publishSrv);

				publishSrv.register(maxSessions, maxConnections, cbk);
			} catch (Exception e) {
				publishSrv.deregister();
				throw e;
			}
		} catch (Exception e) {
			logger.error("runPublishServer", e);
			sc.stopListener();
			sc.destroy();
		}
	}

	private class SrvCallback extends SCPublishServerCallback {

		/** The Constant logger. */
		protected final Logger logger = Logger.getLogger(SrvCallback.class);

		public SrvCallback(SCPublishServer publishSrv) {
			super(publishSrv);
		}

		@Override
		public SCMessage changeSubscription(SCSubscribeMessage message, int operationTimeoutInMillis) {
			logger.info("PublishServer.SrvCallback.changeSubscription()");
			return message;
		}

		@Override
		public SCMessage subscribe(SCSubscribeMessage message, int operationTimeoutInMillis) {
			logger.info("PublishServer.SrvCallback.subscribe()");
			PublishThread publish = new PublishThread(this.scPublishServer);
			publish.start();
			return message;
		}

		@Override
		public void unsubscribe(SCSubscribeMessage message, int operationTimeoutInMillis) {
			logger.info("PublishServer.SrvCallback.unsubscribe()");
			String sessionInfo = message.getSessionInfo();
			// watch out for kill server message
			if (sessionInfo != null) {
				if (sessionInfo.equals("kill server")) {
					try {
						KillThread kill = new KillThread(this.scPublishServer);
						kill.start();
					} catch (Exception ex) {
						logger.error("unsubscribe", ex);
					}
				}
			}
		}
	}

	private class PublishThread extends Thread {

		private SCPublishServer publishSrv;

		public PublishThread(SCPublishServer publishSrv) {
			super();
			this.publishSrv = publishSrv;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(2000);
				SCPublishMessage pubMessage = new SCPublishMessage();
				for (int i = 0; i < 10; i++) {
					pubMessage.setData("publish message nr : " + i);
					pubMessage.setMask("0000121%%%%%%%%%%%%%%%-----------X-----------");
					publishSrv.publish(pubMessage);
					Thread.sleep(2000);
				}
			} catch (Exception e) {
				logger.warn("publish failed");
			}
		}
	}

	protected class KillThread extends Thread {
		private SCPublishServer scPublishServer;

		public KillThread(SCPublishServer scPublishServer) {
			this.scPublishServer = scPublishServer;
		}

		@Override
		public void run() {
			// sleep for 2 seconds before killing the server
			try {
				Thread.sleep(500);
				this.scPublishServer.deregister();
				SCServer scServer = this.scPublishServer.getSCServer();
				scServer.stopListener();
				scServer.destroy();
			} catch (Exception e) {
				logger.error("run", e);
			}
		}
	}
}