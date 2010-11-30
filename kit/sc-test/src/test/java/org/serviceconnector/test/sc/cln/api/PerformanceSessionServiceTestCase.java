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
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.sc.SetupTestCases;

public class PerformanceSessionServiceTestCase {

	/**
	 * Last results without logging - 05.07.2010<br>
	 * Throughput : 1666 msg per sec.
	 */

	@Before
	public void setUp() {
		SetupTestCases.setupSCSessionServer10ConnectionsOverFile(TestConstants.SCProperties);
	}

	@Test
	public void performanceSessionService() throws Exception {
		SCClient sc = null;
		try {
			sc = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP,ConnectionType.NETTY_HTTP);
			sc.setMaxConnections(100);

			// connects to SC, checks connection to SC
			sc.attach();

			SCSessionService sessionServiceA = sc.newSessionService("session-1");
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			sessionServiceA.createSession(10, scMessage);

			SCMessage requestMsg = new SCMessage();
			byte[] buffer = new byte[1024];
			requestMsg.setData(buffer);
			requestMsg.setCompressed(false);
			requestMsg.setMessageInfo("test");

			// prepare body
			StringBuilder sb = new StringBuilder();
			int dataSize = 128; // 128 Byte
			for (int i = 0; i < 100000; i++) {
				sb.append(i);
				if (sb.length() > dataSize)
					break;
			}

			int total = 5000;
			long startTime = System.currentTimeMillis();
			for (int i = 0; i < total; i++) {
				requestMsg.setData("Performance : " + i + sb.toString());
				sessionServiceA.execute(requestMsg);
			}
			long endTime = System.currentTimeMillis();
			long neededTimeInSec = (endTime - startTime) / 1000;
			System.out.println("Throughput : " + total / neededTimeInSec + " msg per sec.");
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
}
