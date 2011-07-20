/*
 * Copyright © 2010 STABILIT Informatik AG, Switzerland *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License"); *
 * you may not use this file except in compliance with the License. *
 * You may obtain a copy of the License at *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0 *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS, *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and *
 * limitations under the License. *
 */
package org.serviceconnector.srv;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCPublishMessage;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.srv.SCPublishServer;
import org.serviceconnector.api.srv.SCPublishServerCallback;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.net.ConnectionType;

public class DemoPublishServer extends Thread {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(DemoPublishServer.class);

	/**
	 * Main method if you like to start in debug mode.
	 */
	public static void main(String[] args) throws Exception {
		DemoPublishServer publishServer = new DemoPublishServer();
		publishServer.run();
	}

	@Override
	public void run() {

		List<String> nics = new ArrayList<String>();
		nics.add("localhost");

		SCServer sc = new SCServer("localhost", 9000, nics, 9001, ConnectionType.DEFAULT_SERVER_CONNECTION_TYPE);

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
			LOGGER.error("runPublishServer", e);
			sc.stopListener();
			sc.destroy();
		}
	}

	private class SrvCallback extends SCPublishServerCallback {

		/** The Constant LOGGER. */
		private final Logger LOGGER = Logger.getLogger(SrvCallback.class);

		public SrvCallback(SCPublishServer publishSrv) {
			super(publishSrv);
		}

		@Override
		public SCMessage changeSubscription(SCSubscribeMessage message, int operationTimeoutMillis) {
			LOGGER.info("PublishServer.SrvCallback.changeSubscription()");
			return message;
		}

		@Override
		public SCMessage subscribe(SCSubscribeMessage message, int operationTimeoutMillis) {
			LOGGER.info("PublishServer.SrvCallback.subscribe()");
			PublishThread publish = new PublishThread(this.scPublishServer);
			publish.start();
			return message;
		}

		@Override
		public void unsubscribe(SCSubscribeMessage message, int operationTimeoutMillis) {
			LOGGER.info("PublishServer.SrvCallback.unsubscribe()");
			String sessionInfo = message.getSessionInfo();
			// watch out for kill server message
			if (sessionInfo != null) {
				if (sessionInfo.equals("kill server")) {
					try {
						KillThread kill = new KillThread(this.scPublishServer);
						kill.start();
					} catch (Exception ex) {
						LOGGER.error("unsubscribe", ex);
					}
				}
			}
		}

		@Override
		public void exceptionCaught(SCServiceException ex) {
			LOGGER.error("exception caught");
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
				for (int i = 0; i < 5; i++) {
					pubMessage.setData("publish message nr : " + i);
					pubMessage.setMask("0000121%%%%%%%%%%%%%%%-----------X-----------");
					publishSrv.publish(pubMessage);
					Thread.sleep(2000);
				}
			} catch (Exception e) {
				LOGGER.warn("publish failed");
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
			// sleep before killing the server
			try {
				Thread.sleep(200);
				this.scPublishServer.deregister();
				;
			} catch (Exception e) {
				LOGGER.error("run", e);
			} finally {
				SCServer sc = this.scPublishServer.getSCServer();
				sc.stopListener();
				sc.destroy();
			}
		}
	}
}