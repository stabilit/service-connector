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

import com.stabilit.scm.unit.test.echo.EchoSCTestCase;
import com.stabilit.scm.unit.test.mt.MTSuperTestCase;

public class MTEchoSCTestCase extends MTSuperTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public MTEchoSCTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void invokeMultipleEchoSCTest() throws Exception {
		Map<EchoSCTestCase, Thread> map = new HashMap<EchoSCTestCase, Thread>();

		for (int i = 0; i < 20; i++) {
			EchoSCTestCase echoSCTestCase = new EchoSCTestCase(fileName);
			echoSCTestCase.setReq(this.newReq());
			Thread th = new MTClientThread(echoSCTestCase, "invokeMultipleEchoSCTest");
			th.start();
			map.put(echoSCTestCase, th);
		}

		for (EchoSCTestCase echoSCTestCase : map.keySet()) {
			map.get(echoSCTestCase).join();
		}
	}
}