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
package com.stabilit.scm.common.net.req;

import org.junit.Before;
import org.junit.Test;

import com.stabilit.scm.unit.test.SetupTestCases;

/**
 * @author JTraber
 */
public class ConnectionPoolTest {

	@Before
	public void setUp() {
		SetupTestCases.setupSC();
	}

	@Test
	public void testSuccesfulPool() {
		IConnectionPool cp = new ConnectionPool("localhost", 8080, "netty.http");
		cp.setMaxConnections(10);
		cp.setMinConnections(1);
		cp.setKeepAliveInterval(60);

		cp.setCloseOnFree(true); // default = false
		cp.start();
		cp.destroy();
	}

	@Test
	public void test2() {

		// IConnectionPool cp = new ConnectionPool("localhost", 8080, "xyt");
		// cp1 == null
	}

	@Test
	public void test3() {

		// IConnectionPool cp = new ConnectionPool("localhost", 8080, "xyt");
		// IConnection connection = cp.getConnection();
		//		
		// cp.freeConnection(connection);
	}

}
