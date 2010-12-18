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
package org.serviceconnector.cln;

import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.SCService;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCSessionService;

/**
 * The Class SCAsyncSessionServiceExample. Demonstrates use of session service in asynchronous mode.
 */
public class SCAsyncSessionClientExample {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCAsyncSessionClientExample.class);

	private static boolean messageReceived = false;

	public static void main(String[] args) {
		SCAsyncSessionClientExample example = new SCAsyncSessionClientExample();
		example.runExample();
	}

	public void runExample() {
		SCClient sc = null;
		try {
			sc = new SCClient("localhost", 7000);
			sc.setMaxConnections(100);

			// connects to SC, checks connection to SC
			sc.attach();

			SCSessionService sessionServiceA = sc.newSessionService("session-1");
			// creates a session
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			sessionServiceA.setEchoTimeoutInSeconds(300);
			SCMessageCallback cbk = new ExampleCallback(sessionServiceA);
			sessionServiceA.createSession(60, scMessage, cbk);

			SCMessage requestMsg = new SCMessage();
			requestMsg.setData("Hello World");
			requestMsg.setCompressed(false);
			
			sessionServiceA.send(requestMsg);

			// wait until message received
			waitForMessage(10);
			// deletes the session
			sessionServiceA.deleteSession();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// disconnects from SC
				sc.detach();
			} catch (Exception e) {
				sc = null;
			}
		}
	}

	private void waitForMessage(int nrSeconds) throws Exception {
		for (int i = 0; i < (nrSeconds*10); i++) {
			if (messageReceived) {
				return;
			}
			Thread.sleep(100);
		}
		throw new TimeoutException("No message received within " + nrSeconds + " seconds timeout.");
	}

	/**
	 * The Class ExampleCallback. Callback used for asynchronously execution.
	 */
	private class ExampleCallback extends SCMessageCallback {

		public ExampleCallback(SCService service) {
			super(service);
		}

		@Override
		public void receive(SCMessage msg) {
			@SuppressWarnings("unused")
			SCClient client = this.getService().getScClient();
			System.out.println(msg);
			SCAsyncSessionClientExample.messageReceived = true;
		}

		@Override
		public void receive(Exception ex) {
			logger.error("callback", ex);
		}
	}
}