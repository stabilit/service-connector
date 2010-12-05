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

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.net.ConnectionType;

@SuppressWarnings("unused")
public class DemoPublishClient extends Thread {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(DemoPublishClient.class);

	public static void main(String[] args) {
		DemoPublishClient demoPublishClient = new DemoPublishClient();
		demoPublishClient.start();
	}

	@Override
	public void run() {

		// SCClient sc = new SCClient("localhost", 7000); // regular, defaults documented in javadoc
		SCClient sc = new SCClient("localhost", 7000, ConnectionType.NETTY_HTTP); // alternative with connection type
		SCPublishService service = null;
		try {
			sc.setMaxConnections(20); // can be set before attach
			sc.setKeepAliveIntervalSeconds(10); // can be set before attach
			sc.attach(); // regular
			// sc.attach(10); // alternative with operation timeout

			String serviceName = "publish-1";
			service = sc.newPublishService(serviceName); // no other params possible

			SCMessageCallback cbk = new DemoPublishClientCallback(service); // callback on service!!
			String mask = "0000121ABCDEFGHIJKLMNO-----------X-----------";
			SCSubscribeMessage msg = new SCSubscribeMessage();
			msg.setSessionInfo("subscription-info"); // optional
			msg.setData("certificate or what so ever"); // optional
			msg.setMask(mask); // mandatory
			msg.setNoDataIntervalInSeconds(100); // mandatory
			SCSubscribeMessage reply = service.subscribe(msg, cbk); // regular
			reply = service.subscribe(10, msg, cbk); // alternative with operation timeout

			String sid = service.getSessionId();

			// while (true) {
			// service.receive(cbk); // wait for response
			// cbk.receive(); // wait for response ?
			// responseMsg = cbk.getMessage(); // response message
			// }
		} catch (Exception e) {
			logger.error("run", e);
		} finally {
			try {
				service.unsubscribe(); // regular
				// service.unsubscribe(10); // alternative with operation timeout
				SCMessage msg = new SCMessage();
				msg.setSessionInfo("subscription-info");
				// service.unsubscribe(10, msg); // alternative with operation timeout and session info
				sc.detach();
			} catch (Exception e) {
				logger.info("cleanup " + e.toString());
			}
		}
	}

	private class DemoPublishClientCallback extends SCMessageCallback {

		private SCMessage pubMessage;

		public DemoPublishClientCallback(SCPublishService service) {
			super(service);
		}

		@Override
		public void receive(SCMessage reply) {
			this.pubMessage = reply;
			System.out.println("Publish client received: " + reply.getData());
		}

		public SCMessage getMessage() {
			return this.pubMessage;
		}

		@Override
		public void receive(Exception e) {
		}
	}
}