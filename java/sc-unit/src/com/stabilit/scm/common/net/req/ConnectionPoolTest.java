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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

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
	public void testPoolSuccesful() {
		IConnectionPool cp = new ConnectionPool("localhost", 8080, "netty.http", 60, 16);
		cp.setMaxConnections(10);
		cp.setMinConnections(1);

		cp.setCloseOnFree(true);
		cp.initMinConnections();
		cp.destroy();
	}

	@Test
	public void testMinMaxWrong() throws Exception {
		IConnectionPool cp = new ConnectionPool("localhost", 8080);
		cp.setMaxConnections(2);
		cp.setMinConnections(3);
		for (int i = 0; i < 2; i++) {
			cp.getConnection();
		}
		try {
			// only two connections should be created, third connection fails
			cp.getConnection();
			Assert.fail("Should throw exception");
		} catch (ConnectionPoolException es) {
		}
		cp.destroy();
	}

	@Test
	public void testGetAndFreeConnection() throws Exception {
		IConnectionPool cp = new ConnectionPool("localhost", 8080);
		IConnection connection = cp.getConnection();
		cp.freeConnection(connection);
		cp.destroy();
	}

	@Test
	public void testReachConnectionLimit() throws Exception {
		IConnectionPool cp = new ConnectionPool("localhost", 8080);
		cp.setMaxConnections(2);
		cp.getConnection();
		cp.getConnection();

		try {
			// only two connections should be created, third connection fails
			cp.getConnection();
			Assert.fail("Should throw exception");
		} catch (ConnectionPoolException es) {
		}
		cp.destroy();
	}

	@Test
	public void testKeepAliveInactive() throws Exception {
		IConnectionPool cp = new ConnectionPool("localhost", 8080, 0);
		cp.setMaxConnections(2);
		cp.setMinConnections(2);
		IConnection connection = cp.getConnection();
		// check log if no keep alive has been sent
		cp.freeConnection(connection);
		cp.destroy();
	}

	@Test
	public void testKeepAliveActive() throws Exception {
		IConnectionPool cp = new ConnectionPool("localhost", 8080, 5);
		cp.setMaxConnections(1);
		cp.setMinConnections(1);
		cp.initMinConnections();
		// check log if keep alive has been sent for connection
		IConnection connection = cp.getConnection();
		// check log if no keep alive has been sent for connection
		cp.freeConnection(connection);
		cp.destroy();
	}

	@Test
	public void testConnectionCloseAfterTenKeepAlive() throws Exception {
		IConnectionPool cp = new ConnectionPool("localhost", 8080, 1);
		cp.setMaxConnections(2);
		cp.setMinConnections(2);
		cp.initMinConnections();
		cp.destroy();
	}

	@Test
	public void testCloseAfterFreeConnection() throws Exception {
		IConnectionPool cp = new ConnectionPool("localhost", 8080);
		cp.setCloseOnFree(true);
		IConnection connection = cp.getConnection();
		cp.freeConnection(connection);
	}

	@Test
	public void testHundredConnectionsInUse() throws Exception {
		IConnectionPool cp = new ConnectionPool("localhost", 8080, 5);
		List<IConnection> connections = new ArrayList<IConnection>();
		for (int i = 0; i < 100; i++) {
			connections.add(cp.getConnection());
		}
		Thread.sleep(8000);
		for (int i = 0; i < 100; i++) {
			cp.freeConnection(connections.remove(0));
		}
		cp.destroy();
	}
}
