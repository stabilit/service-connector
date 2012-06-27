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
package org.serviceconnector.cln.cc;

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCMessageCallback;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.net.ConnectionType;

/**
 * The Class DemoSessionClient.
 */
@SuppressWarnings("unused")
public class DemoCCClient {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(DemoCCClient.class);

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		DemoCCClient demoSessionClient = new DemoCCClient();
		demoSessionClient.run();
	}

	/**
	 * Run.
	 */
	public void run() {

		// Connection to SC over HTTP
		SCClient sc = new SCClient("localhost", 7000, ConnectionType.NETTY_HTTP);
		SCSessionService sessionSrv = null;

		try {
			sc.setMaxConnections(20); // can be set before attach, default 100 Connections
			sc.setKeepAliveIntervalSeconds(10); // can be set before attach, default 0 -> inactive
			sc.attach(); // attaching client to SC , communication starts

			String serviceName = "session-1";
			sessionSrv = sc.newSessionService(serviceName); // name of the service to use
			sessionSrv.setEchoIntervalSeconds(10); // can be set before create session
			sessionSrv.setEchoTimeoutSeconds(2); // can be set before create session

			SCMessage msg = new SCMessage();
			msg.setSessionInfo("session-info"); // optional
			msg.setData("certificate or what so ever"); // optional
			SCMessageCallback cbk = new DemoSessionClientCallback(sessionSrv); // callback on service!!
			SCMessage reply = sessionSrv.createSession(10, msg, cbk); // create a session within 10 seconds
			Object body = reply.getData();		
			
			String sid = sessionSrv.getSessionId();

			SCMessage requestMsg = new SCMessage();
			SCMessage responseMsg = new SCMessage();

			requestMsg.setData("cache message body");
			requestMsg.setCacheId("700");
			responseMsg = sessionSrv.execute(requestMsg); // regular synchronous call
			LOGGER.info("Message sent to put in cache=" + requestMsg.getData());
			LOGGER.info("Message received=" + responseMsg.getData());

			responseMsg = sessionSrv.execute(requestMsg); // regular synchronous call
			LOGGER.info("Message sent with cacheId=" + requestMsg.getData());
			LOGGER.info("Message received from cache=" + responseMsg.getData());			
			
			SCPublishService publishService = sc.newPublishService("updateRetriever1"); // name of the service to use

			DemoPublishClientCallback pubCbk = new DemoPublishClientCallback(publishService); // callback on service!!
			// set up subscribe message
			SCSubscribeMessage subMsg = new SCSubscribeMessage();
			String mask = "0000121ABCDEFGHIJKLMNO-----------X-----------";
			subMsg.setSessionInfo("subscription-info"); // optional
			subMsg.setData("certificate or what so ever"); // optional
			subMsg.setMask(mask); // mandatory
			subMsg.setNoDataIntervalSeconds(100); // mandatory
			SCSubscribeMessage subReply = publishService.subscribe(subMsg, pubCbk); // regular subscribe			
			
		} catch (Exception e) {
			LOGGER.error("run", e);
		} finally {
			try {
				SCMessage msg = new SCMessage();
				msg.setSessionInfo("kill server");
				sessionSrv.deleteSession(5, msg);
				sc.detach(2); // detaches from SC, stops communication
			} catch (Exception e) {
				LOGGER.error("cleanup", e);
			}
		}
	}

	/**
	 * The Class DemoSessionClientCallback.
	 */
	private class DemoSessionClientCallback extends SCMessageCallback {

		/** The reply message. */
		private SCMessage replyMessage;

		/**
		 * Instantiates a new demo session client callback.
		 * 
		 * @param service
		 *            the service
		 */
		public DemoSessionClientCallback(SCSessionService service) {
			super(service);
		}

		/** {@inheritDoc} */
		@Override
		public void receive(SCMessage reply) {
			System.out.println("DemoSessionClient.DemoSessionClientCallback.receive() async" + reply.toString());
			this.replyMessage = reply;
		}

		/** {@inheritDoc} */
		@Override
		public void receive(Exception e) {
			System.out.println("DemoSessionClient.DemoSessionClientCallback.receive() " + e.getMessage());
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
		 * @param service
		 *            the service
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