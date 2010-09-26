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
package org.serviceconector.test.sc.cln.api;

import org.junit.Before;
import org.junit.Test;
import org.serviceconector.test.sc.SetupTestCases;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.ISCClient;
import org.serviceconnector.api.cln.ISessionService;
import org.serviceconnector.api.cln.SCClient;



public class PerformanceSessionServiceTestCase {

	/**
	 * Last results without logging - 05.07.2010<br>
	 * Throughput : 1666 msg per sec.
	 */

	@Before
	public void setUp() {
		SetupTestCases.setupSCSessionServer10ConnectionsOverFile("scPerf.properties");
	}

	@Test
	public void performanceSessionService() throws Exception {
		ISCClient sc = null;
		try {
			sc = new SCClient();
			sc.setMaxConnections(100);

			// connects to SC, checks connection to SC
			sc.attach("localhost", 8080);

			ISessionService sessionServiceA = sc.newSessionService("simulation");
			sessionServiceA.createSession("sessionInfo", 10, 60);

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
