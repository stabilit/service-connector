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

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.cln.ISCClient;
import org.serviceconnector.api.cln.IService;
import org.serviceconnector.api.cln.ISessionService;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.service.ISCMessageCallback;
import org.serviceconnector.test.sc.SetupTestCases;



public class ClnAPIAsyncSessionTestCase {
	
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ClnAPIAsyncSessionTestCase.class);

	@Before
	public void setUp() {
		SetupTestCases.setupSCSessionServer10Connections();
	}

	@Test
	public void testClnAPI() throws Exception {
		ISCClient sc = null;
		try {
			sc = new SCClient();
			sc.setMaxConnections(100);
			// set environment, e.g. keepAliveInterval
			// connects to SC, checks connection to SC
			sc.attach("localhost", TestConstants.PORT_HTTP);
			ISessionService sessionServiceA = sc.newSessionService("simulation");
			sessionServiceA.createSession("sessionInfo", 60, 360);
			SCMessage requestMsg = new SCMessage();
			byte[] buffer = new byte[1024];
			requestMsg.setData(buffer);
			requestMsg.setCompressed(false);
			requestMsg.setMessageInfo("test");
			ISCMessageCallback callback = new TestCallback(sessionServiceA);
			sessionServiceA.execute(requestMsg, callback);
			Thread.sleep(100000);
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

		public TestCallback(IService service) {
			super(service);
		}

		@Override
		public void callback(SCMessage msg) {
			System.out.println(msg);
		}

		@Override
		public void callback(Exception ex) {
			logger.error("callback", ex);
		}
	}

	@After
	public void tearDown() {
	}
}
