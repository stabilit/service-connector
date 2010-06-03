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
package com.stabilit.scm.unit.test.echo.mt;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.stabilit.scm.unit.test.echo.SrvEchoTestCase;
import com.stabilit.scm.unit.test.mt.MTSuperTestCase;
import com.stabilit.scm.unit.test.mt.MTSuperTestCase.MTClientThread;

public class MTSrvEchoTestCase extends MTSuperTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public MTSrvEchoTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void invokeMultipleSrvEchoTest() throws Exception {
		Map<SrvEchoTestCase, Thread> map = new HashMap<SrvEchoTestCase, Thread>();

		for (int i = 0; i < 3; i++) {
			SrvEchoTestCase srvEchoTestCase = new SrvEchoTestCase(fileName);
			srvEchoTestCase.setClient(this.newClient());
			srvEchoTestCase.clnAttachBefore();
			srvEchoTestCase.clnCreateSessionBefore();
			Thread th = new MTClientThread(srvEchoTestCase, "invokeMultipleSrvEchoTest");
			th.start();
			map.put(srvEchoTestCase, th);
		}

		for (SrvEchoTestCase srvEchoTestCase : map.keySet()) {
			map.get(srvEchoTestCase).join();
			srvEchoTestCase.clnDeleteSessionAfter();
			srvEchoTestCase.clnDetachAfter();
		}
	}

	@Test
	public void invokeMultipleSessionSrvEchoTest() throws Exception {
		Map<SrvEchoTestCase, Thread> map = new HashMap<SrvEchoTestCase, Thread>();

		for (int i = 0; i < 10; i++) {
			SrvEchoTestCase srvEchoTestCase = new SrvEchoTestCase(fileName);
			srvEchoTestCase.setClient(this.newClient());
			srvEchoTestCase.clnAttachBefore();
			srvEchoTestCase.clnCreateSessionBefore();
			Thread th = new MTClientThread(srvEchoTestCase, "invokeMultipleSessionSrvEchoTestForMultipleClients");
			th.start();
			map.put(srvEchoTestCase, th);
		}

		for (SrvEchoTestCase srvEchoTestCase : map.keySet()) {
			map.get(srvEchoTestCase).join();
			srvEchoTestCase.clnDeleteSessionAfter();
			srvEchoTestCase.clnDetachAfter();
		}
	}
}