/*-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*/
package org.serviceconnector.cln;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCMessageCallback;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.net.ConnectionType;

/**
 * The Class DemoPublishClient.
 */
@SuppressWarnings("unused")
public class DemoPublishClient extends Thread {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(DemoPublishClient.class);

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		DemoPublishClient demoPublishClient = new DemoPublishClient();
		demoPublishClient.start();
	}

	/** {@inheritDoc} */
	@Override
	public void run() {
		// Connection to SC over HTTP
		SCClient sc = new SCClient("localhost", 7000, ConnectionType.NETTY_HTTP);
		SCPublishService service = null;

		try {
			sc.setMaxConnections(20); // can be set before attach, default 100 Connections
			sc.setKeepAliveIntervalSeconds(10); // can be set before attach, default 0 -> inactive
			sc.attach(); // attaching client to SC , communication starts

			String serviceName = "publish-1";
			service = sc.newPublishService(serviceName); // name of the service to use

			DemoPublishClientCallback cbk = new DemoPublishClientCallback(service); // callback on service!!
			// set up subscribe message
			SCSubscribeMessage msg = new SCSubscribeMessage();
			String mask = "0000121ABCDEFGHIJKLMNO-----------X-----------";
			msg.setSessionInfo("subscription-info"); // optional
			msg.setData("certificate or what so ever"); // optional
			msg.setMask(mask); // mandatory
			msg.setNoDataIntervalSeconds(100); // mandatory
			SCSubscribeMessage reply = service.subscribe(msg, cbk); // regular subscribe

			String sid = service.getSessionId();

			// wait to receive messages
			while (cbk.receivedMsg < 5) {
				Thread.sleep(1500);
			}
		} catch (Exception e) {
			LOGGER.error("run", e);
		} finally {
			try {
				SCSubscribeMessage msg = new SCSubscribeMessage();
				msg.setSessionInfo("kill server");
				service.unsubscribe(5, msg);
				sc.detach(2); // detaches from SC, stops communication
			} catch (Exception e) {
				LOGGER.info("cleanup " + e.toString());
			}
		}
	}

	/**
	 * The Class DemoPublishClientCallback.
	 */
	private class DemoPublishClientCallback extends SCMessageCallback {

		/** The received msg. */
		public int receivedMsg = 0;

		/**
		 * Instantiates a new demo publish client callback.
		 *
		 * @param service the service
		 */
		public DemoPublishClientCallback(SCPublishService service) {
			super(service);
		}

		/** {@inheritDoc} */
		@Override
		public void receive(SCMessage reply) {
			receivedMsg++;
			System.out.println("DemoPublishClient.DemoPublishClientCallback.receive() " + reply.getData());
		}

		/** {@inheritDoc} */
		@Override
		public void receive(Exception e) {
			receivedMsg++;
			System.out.println("DemoPublishClient.DemoPublishClientCallback.receive() " + e.getMessage());
		}
	}
}
