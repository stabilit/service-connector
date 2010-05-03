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

import org.junit.Before;
import org.junit.Test;

import com.stabilit.sc.unit.test.SetupTestCases;
import com.stabilit.sc.unit.test.echo.EchoSCTestCase;
import com.stabilit.sc.unit.test.mt.MTSuperTestCase;

public class MTEchoSCTestCase extends MTSuperTestCase {

	/**
	 * @param fileName
	 */
	public MTEchoSCTestCase(String fileName) {
		super(fileName);
	}

	@Before
	@Override
	public void setup() throws Exception {
		SetupTestCases.setupSC();
	}

	@Test
	public void invokeMultipleEchoSCTest() throws Exception {
		EchoSCTestCase echoSCTestCase = new EchoSCTestCase(fileName);
		echoSCTestCase.setClient(this.newClient());
		Thread th1 = new MTClientThread(echoSCTestCase, "invokeMultipleEchoSCTest");
		th1.start();
		echoSCTestCase = new EchoSCTestCase(fileName);
		echoSCTestCase.setClient(this.newClient());
		Thread th2 = new MTClientThread(echoSCTestCase, "invokeMultipleEchoSCTest");
		th2.start();
		echoSCTestCase = new EchoSCTestCase(fileName);
		echoSCTestCase.setClient(this.newClient());
		Thread th3 = new MTClientThread(echoSCTestCase, "invokeMultipleEchoSCTest");
		th3.start();
		th1.join();
		th2.join();
		th3.join();
	}
}