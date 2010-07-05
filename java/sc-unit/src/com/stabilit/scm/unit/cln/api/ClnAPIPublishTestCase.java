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

import com.stabilit.scm.common.service.ISCPublishServer;
import com.stabilit.scm.common.service.SCPublishServer;
import com.stabilit.scm.unit.test.SetupTestCases;

public class ClnAPIPublishTestCase {

	@Before
	public void setUp() {
		SetupTestCases.setupAll();
	}

	@Test
	public void testClnAPI() throws Exception {
		ISCPublishServer sc = null;
		try {

			sc = new SCPublishServer("localhost", 9000, "netty.tcp");

			// connects to SC, starts observing connection
			sc.register();
			Object data = null;
			String mask = "AVSD-----";
			sc.publish(mask, data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// disconnects from SC
				sc.deregister();
			} catch (Exception e) {
				sc = null;
			}
		}

	}
}