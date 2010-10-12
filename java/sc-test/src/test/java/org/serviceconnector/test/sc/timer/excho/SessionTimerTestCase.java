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
package org.serviceconnector.test.sc.timer.excho;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.api.cln.ISCClient;
import org.serviceconnector.test.sc.SetupTestCases;



public class SessionTimerTestCase {

	private boolean sessionAborted = false;

	@Before
	public void setUp() {
		SetupTestCases.setupSCSessionServer10Connections();
	}

	@Test
	public void testClnAPI() throws Exception {
		ISCClient sc = null;
		/* TODO ISessionListener sessionListener = new ISessionListener() {

			@Override
			public void abortSessionEvent(SessionEvent sessionEvent) throws Exception {
				sessionAborted = !sessionAborted;
			}

			@Override
			public void createSessionEvent(SessionEvent sessionEvent) throws Exception {
			}

			@Override
			public void deleteSessionEvent(SessionEvent sessionEvent) throws Exception {
			}

		};
		SessionPoint.getInstance().addListener(sessionListener);
	
		try {
			sc = new SCClient();
			sc.setMaxConnections(100);

			// connects to SC, checks connection to SC
			sc.attach(TestConstants.HOST, TestConstants.PORT_HTTP);

			ISessionService sessionServiceA = sc.newSessionService("simulation");
			sessionServiceA.createSession("sessionInfo", 60, 1);

			Thread.sleep(8000);

			Assert.assertEquals(true, sessionAborted);

			// deletes the session
			sessionServiceA.deleteSession();
			Assert.fail("Should throw exception!");
		} catch (Exception e) {
		} finally {
			SessionPoint.getInstance().removeListener(sessionListener);
			try {
				// disconnects from SC
				sc.detach();
			} catch (Exception e) {
				sc = null;
			}
		}
		*/
	}
}
