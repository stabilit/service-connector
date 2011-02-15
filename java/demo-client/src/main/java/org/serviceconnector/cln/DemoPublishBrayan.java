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
package org.serviceconnector.cln;

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCMessageCallback;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.net.ConnectionType;

@SuppressWarnings("unused")
public class DemoPublishBrayan extends Thread {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(DemoPublishBrayan.class);

	public static void main(String[] args) {
		DemoPublishBrayan demoPublishClient = new DemoPublishBrayan();
		demoPublishClient.start();
	}

	@Override
	public void run() {
		// Connection to SC over HTTP
		SCClient sc = new SCClient("localhost", 7000, ConnectionType.NETTY_HTTP);
		SCPublishService service1 = null;

		try {
			sc.setKeepAliveIntervalSeconds(10);
			sc.setMaxConnections(10);
			sc.attach(); // attaching client to SC , communication starts

			String serviceName = "publish-1";
			service1 = sc.newPublishService(serviceName); // name of the service to use
			
			DemoPublishClientCallback cbk = new DemoPublishClientCallback(service1); // callback on service!!
			// set up subscribe message
			SCSubscribeMessage msg = new SCSubscribeMessage();
			String mask = "0000121ABCDEFGHIJKLMNO-----------X-----------";
			msg.setSessionInfo("subscription-info"); // optional
			msg.setData("certificate or what so ever"); // optional
			msg.setMask(mask); // mandatory
			msg.setNoDataIntervalSeconds(300);
			SCSubscribeMessage reply = service1.subscribe(msg, cbk); // regular subscribe

			String sid = service1.getSessionId();
			serviceName = "publish-2";
			SCPublishService service2 = sc.newPublishService(serviceName); // name of the service to use

			DemoPublishClientCallback cbk2 = new DemoPublishClientCallback(service2); // callback on service!!
			reply = service2.subscribe(msg, cbk2); // regular subscribe

			String sid2 = service2.getSessionId();

			SCPublishService service3 = sc.newPublishService(serviceName); // name of the service to use

			DemoPublishClientCallback cbk3 = new DemoPublishClientCallback(service3); // callback on service!!
			reply = service3.subscribe(msg, cbk3); // regular subscribe
			
			SCPublishService service4 = sc.newPublishService("publish-3");
			DemoPublishClientCallback cbk4 = new DemoPublishClientCallback(service4); // callback on service!!
			reply = service4.subscribe(msg, cbk4); // regular subscribe
			
			SCPublishService service5 = sc.newPublishService("publish-4");
			DemoPublishClientCallback cbk5 = new DemoPublishClientCallback(service5); // callback on service!!
			reply = service5.subscribe(msg, cbk5); // regular subscribe

			
			SCSessionService session1 = sc.newSessionService("session-1");
			DemoSessionClientCallback cbkSess = new DemoSessionClientCallback(session1);
			SCMessage scMessage = new SCMessage();
			session1.setEchoIntervalSeconds(60);
			session1.setEchoTimeoutSeconds(2);
			session1.createSession(scMessage, cbkSess);

			// wait to receive messages
			Thread.sleep(1500);
		} catch (Exception e) {
			logger.error("run", e);
		} finally {
			try {
				SCSubscribeMessage msg = new SCSubscribeMessage();
				msg.setSessionInfo("kill server");
				service1.unsubscribe(5, msg);
				sc.detach(2); // detaches from SC, stops communication
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

	private class DemoSessionClientCallback extends SCMessageCallback {
		private SCMessage replyMessage;

		public DemoSessionClientCallback(SCSessionService service) {
			super(service);
		}

		@Override
		public void receive(SCMessage reply) {
			System.out.println("DemoSessionClient.DemoSessionClientCallback.receive() async" + reply.toString());
			this.replyMessage = reply;
		}

		@Override
		public void receive(Exception e) {
			System.out.println("DemoSessionClient.DemoSessionClientCallback.receive() " + e.getMessage());
		}
	}
}