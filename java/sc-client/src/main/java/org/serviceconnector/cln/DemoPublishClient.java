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
			msg.setNoDataIntervalInSeconds(100); // mandatory
			SCSubscribeMessage reply = service.subscribe(msg, cbk); // regular subscribe

			String sid = service.getSessionId();

			// wait to receive messages
			while (cbk.receivedMsg < 10) {
				Thread.sleep(1500);
			}
		} catch (Exception e) {
			logger.error("run", e);
		} finally {
			try {
				SCSubscribeMessage msg = new SCSubscribeMessage();
				msg.setSessionInfo("kill server"); // optional
				service.unsubscribe(msg); // regular
				sc.detach(); // detaches from SC, stops communication
			} catch (Exception e) {
				logger.info("cleanup " + e.toString());
			}
		}
	}

	private class DemoPublishClientCallback extends SCMessageCallback {

		public int receivedMsg = 0;

		public DemoPublishClientCallback(SCPublishService service) {
			super(service);
		}

		@Override
		public void receive(SCMessage reply) {
			receivedMsg++;
			System.out.println("DemoPublishClient.DemoPublishClientCallback.receive() " + reply.getData());
		}

		@Override
		public void receive(Exception e) {
			receivedMsg++;
			System.out.println("DemoPublishClient.DemoPublishClientCallback.receive() " + e.getMessage());
		}
	}
}