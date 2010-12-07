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
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.net.ConnectionType;

@SuppressWarnings("unused")
public class DemoSessionClient {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(DemoSessionClient.class);

	public static void main(String[] args) {
		DemoSessionClient demoSessionClient = new DemoSessionClient();
		demoSessionClient.run();
	}

	public void run() {

		// Connection to SC over HTTP
		SCClient sc = new SCClient("localhost", 7000, ConnectionType.NETTY_HTTP);
		SCSessionService service = null;

		try {
			sc.setMaxConnections(20); // can be set before attach, default 100 Connections
			sc.setKeepAliveIntervalSeconds(10); // can be set before attach, default 0 -> inactive
			sc.attach(); // attaching client to SC , communication starts

			String serviceName = "session-1";
			service = sc.newSessionService(serviceName); // name of the service to use
			service.setEchoIntervalInSeconds(10); // can be set before create session
			service.setEchoTimeoutInSeconds(2); // can be set before create session

			SCMessage msg = new SCMessage();
			msg.setSessionInfo("session-info"); // optional
			msg.setData("certificate or what so ever"); // optional
			SCMessage reply = service.createSession(10, msg); // create a session within 10 seconds
			Object body = reply.getData();

			String sid = service.getSessionId();

			SCMessage requestMsg = new SCMessage();
			SCMessage responseMsg = new SCMessage();
			SCMessageCallback cbk = new DemoSessionClientCallback(service); // callback on service!!
			for (int i = 0; i < 5; i++) {
				requestMsg.setData("body nr : " + i);
				responseMsg = service.execute(requestMsg); // regular synchronous call
				logger.info("Message sent sync: " + requestMsg.getData());
				logger.info("Message received sync: " + responseMsg.getData());
				Thread.sleep(2000);
			}
			for (int i = 0; i < 5; i++) {
				requestMsg.setData("body nr : " + i);
				service.send(requestMsg, cbk); // regular asynchronous call
				logger.info("Message sent async: " + requestMsg.getData());
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			logger.error("run", e);
		} finally {
			try {
				SCMessage msg = new SCMessage();
				msg.setSessionInfo("kill server"); // optional
				service.deleteSession(msg); // regular
				sc.detach(); // detaches from SC, stops communication
			} catch (Exception e) {
				logger.error("cleanup", e);
			}
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