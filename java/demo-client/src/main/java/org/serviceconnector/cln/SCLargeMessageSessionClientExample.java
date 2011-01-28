/*
 * -----------------------------------------------------------------------------*
 * *
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
 * -----------------------------------------------------------------------------*
 * /*
 * /**
 */
package org.serviceconnector.cln;

import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCMessageCallback;
import org.serviceconnector.api.cln.SCSessionService;

public class SCLargeMessageSessionClientExample {

	public static void main(String[] args) {
		SCLargeMessageSessionClientExample.runExample();
	}

	public static void runExample() {
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
			// set up large buffer
			byte[] buffer = new byte[100000];
			for (int i = 0; i < buffer.length; i++) {
				buffer[i] = (byte) i;
			}
			requestMsg.setData(buffer);
			requestMsg.setCompressed(false);
			SCMessage responseMsg = sessionServiceA.execute(requestMsg);

			System.out.println(responseMsg.getData().toString());
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

	/**
	 * The Class ExampleCallback. Callback used for asynchronously execution.
	 */
	private static class ExampleCallback extends SCMessageCallback {

		public ExampleCallback(SCSessionService service) {
			super(service);
		}

		@Override
		public void receive(SCMessage msg) {
			@SuppressWarnings("unused")
			SCClient client = this.getService().getSCClient();
			System.out.println(msg);
		}

		@Override
		public void receive(Exception ex) {
			System.out.println(ex);
		}
	}
}