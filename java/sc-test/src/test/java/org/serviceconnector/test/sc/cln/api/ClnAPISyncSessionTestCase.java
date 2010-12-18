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
package org.serviceconnector.test.sc.cln.api;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.SCService;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.sc.SetupTestCases;

public class ClnAPISyncSessionTestCase {

	@Before
	public void beforeOneTest() {
		SetupTestCases.setupSCSessionServer10Connections();
	}

	@Test
	public void testClnAPI() throws Exception {
		SCClient sc = null;
		try {
			sc = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
			sc.setMaxConnections(100);
			sc.attach();

			SCSessionService sessionServiceA = sc.newSessionService("session-1");
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			SCMessageCallback callback = new TestCallback(sessionServiceA);
			sessionServiceA.createSession(10, scMessage, callback);

			SCMessage requestMsg = new SCMessage();
			byte[] buffer = new byte[1024];
			requestMsg.setData(buffer);
			requestMsg.setCompressed(false);
			requestMsg.setMessageInfo("test");
			SCMessage responseMsg = sessionServiceA.execute(requestMsg);

			System.out.println(responseMsg);

			// deletes the session
			sessionServiceA.deleteSession();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// disconnects from SC
				sc.detach();
			} catch (Throwable e) {
				sc = null;
			}
		}
	}

	private class TestCallback extends SCMessageCallback {

		public TestCallback(SCService service) {
			super(service);
		}

		@Override
		public void receive(SCMessage msg) {
			System.out.println(msg);
		}

		@Override
		public void receive(Exception ex) {
			logger.error("callback", ex);
		}
	}
}
