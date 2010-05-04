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
package com.stabilit.sc.unit.test.echo.mt;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.stabilit.sc.unit.test.echo.SrvEchoTestCase;
import com.stabilit.sc.unit.test.mt.MTSuperTestCase;

public class MTSrvEchoTestCase extends MTSuperTestCase {

	/**
	 * @param fileName
	 */
	public MTSrvEchoTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void invokeMultipleSrvEchoTest() throws Exception {
		SrvEchoTestCase srvEchoTestCase1 = new SrvEchoTestCase(fileName);
		srvEchoTestCase1.setClient(this.newClient());
		srvEchoTestCase1.clnConnectBefore();
		srvEchoTestCase1.clnCreateSessionBefore();
		Thread th1 = new MTClientThread(srvEchoTestCase1, "invokeMultipleSrvEchoTest");
		th1.start();
		SrvEchoTestCase srvEchoTestCase2 = new SrvEchoTestCase(fileName);
		srvEchoTestCase2.setClient(this.newClient());
		srvEchoTestCase2.clnConnectBefore();
		srvEchoTestCase2.clnCreateSessionBefore();
		Thread th2 = new MTClientThread(srvEchoTestCase2, "invokeMultipleSrvEchoTest");
		th2.start();
		SrvEchoTestCase srvEchoTestCase3 = new SrvEchoTestCase(fileName);
		srvEchoTestCase3.setClient(this.newClient());
		srvEchoTestCase3.clnConnectBefore();
		srvEchoTestCase3.clnCreateSessionBefore();
		Thread th3 = new MTClientThread(srvEchoTestCase3, "invokeMultipleSrvEchoTest");
		th3.start();
		th1.join();
		srvEchoTestCase1.clnDeleteSessionAfter();
		srvEchoTestCase1.clnDisconnectAfter();
		th2.join();
		srvEchoTestCase2.clnDeleteSessionAfter();
		srvEchoTestCase2.clnDisconnectAfter();
		th3.join();
		srvEchoTestCase3.clnDeleteSessionAfter();
		srvEchoTestCase3.clnDisconnectAfter();
	}

	@Test
	public void invokeMultipleSessionSrvEchoTest() throws Exception {
		Map<SrvEchoTestCase, Thread> map = new HashMap<SrvEchoTestCase, Thread>();

		for (int i = 0; i < 15; i++) {
			SrvEchoTestCase srvEchoTestCase = new SrvEchoTestCase(fileName);
			srvEchoTestCase.setClient(this.newClient());
			srvEchoTestCase.clnConnectBefore();
			srvEchoTestCase.clnCreateSessionBefore();
			Thread th = new MTClientThread(srvEchoTestCase, "invokeMultipleSessionSrvEchoTest");
			th.start();
			map.put(srvEchoTestCase, th);
		}

		for (SrvEchoTestCase srvEchoTestCase : map.keySet()) {
			map.get(srvEchoTestCase).join();
			srvEchoTestCase.clnDeleteSessionAfter();
			srvEchoTestCase.clnDisconnectAfter();
		}
	}
}