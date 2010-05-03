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
		SrvEchoTestCase srvEchoTestCase = new SrvEchoTestCase(fileName);
		srvEchoTestCase.setClient(this.newClient());
		srvEchoTestCase.clnConnectBefore();
		srvEchoTestCase.clnCreateSessionBefore();
		Thread th1 = new MTClientThread(srvEchoTestCase, "invokeMultipleSrvEchoTest");
		th1.start();
		srvEchoTestCase = new SrvEchoTestCase(fileName);
		srvEchoTestCase.setClient(this.newClient());
		srvEchoTestCase.clnConnectBefore();
		srvEchoTestCase.clnCreateSessionBefore();
		Thread th2 = new MTClientThread(srvEchoTestCase, "invokeMultipleSrvEchoTest");
		th2.start();
		srvEchoTestCase = new SrvEchoTestCase(fileName);
		srvEchoTestCase.setClient(this.newClient());
		Thread th3 = new MTClientThread(srvEchoTestCase, "invokeMultipleSrvEchoTest");
//		th3.start();
		th1.join();
		th2.join();
//		th3.join();
	}
}