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
package com.stabilit.scm.unit.cln.api;

import org.junit.Before;
import org.junit.Test;

import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.ISessionService;
import com.stabilit.scm.common.service.ISCClient;
import com.stabilit.scm.common.service.SCMessage;
import com.stabilit.scm.unit.test.SetupTestCases;

public class PerformanceSessionServiceTestCase {

	@Before
	public void setUp() {
		SetupTestCases.setupSCSessionServer10Connections();
	}

	@Test
	public void performanceSessionService() throws Exception {
		ISCClient sc = null;
		try {
			sc = new SCClient("localhost", 8080);
			sc.setMaxConnections(100);

			// connects to SC, checks connection to SC
			sc.attach();

			ISessionService sessionServiceA = sc.newSessionService("simulation");
			sessionServiceA.createSession("sessionInfo", 60, 10);

			SCMessage requestMsg = new SCMessage();
			byte[] buffer = new byte[1024];
			requestMsg.setData(buffer);
			requestMsg.setCompressed(false);
			requestMsg.setMessageInfo("test");

			int total = 100000;
			long startTime = System.currentTimeMillis();
			for (int i = 0; i < total; i++) {
				requestMsg.setData("Performance : " + i);
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
			} catch (Exception e) {
				sc = null;
			}
		}
	}
}
