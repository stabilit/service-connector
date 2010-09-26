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
package org.serviceconector.test.sc.connectionPool;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.serviceconector.test.sc.SetupTestCases;
import org.serviceconnector.net.connection.ConnectionPool;
import org.serviceconnector.net.connection.ConnectionPoolBusyException;
import org.serviceconnector.net.connection.IConnection;



/**
 * @author JTraber
 */
public class ConnectionPoolTestCase {

	@Before
	public void setUp() {
		SetupTestCases.setupSC();
	}

	@Test
	public void testPoolSuccesful() {
		ConnectionPool connectionPool = new ConnectionPool("localhost", 8080, "netty.http", 60);
		connectionPool.setMaxConnections(10);
		connectionPool.setMinConnections(1);

		connectionPool.setCloseOnFree(true);
		connectionPool.initMinConnections();
		connectionPool.destroy();
	}

	@Test
	public void testMinMaxWrong() throws Exception {
		ConnectionPool connectionPool = new ConnectionPool("localhost", 8080);
		connectionPool.setMaxConnections(2);
		connectionPool.setMinConnections(3);
		for (int i = 0; i < 2; i++) {
			connectionPool.getConnection();
		}
		try {
			// only two connections should be created, third connection fails
			connectionPool.getConnection();
			Assert.fail("Should throw exception");
		} catch (ConnectionPoolBusyException es) {
		}
		connectionPool.destroy();
	}

	@Test
	public void testGetAndFreeConnection() throws Exception {
		ConnectionPool connectionPool = new ConnectionPool("localhost", 8080);
		IConnection connection = connectionPool.getConnection();
		connectionPool.freeConnection(connection);
		connectionPool.destroy();
	}

	@Test
	public void testReachConnectionLimit() throws Exception {
		ConnectionPool connectionPool = new ConnectionPool("localhost", 8080);
		int maxConnections = 2;
		connectionPool.setMaxConnections(maxConnections);
		connectionPool.getConnection();
		connectionPool.getConnection();

		try {
			// only two connections should be created, third connection fails
			connectionPool.getConnection();
			Assert.fail("Should throw exception");
		} catch (ConnectionPoolBusyException es) {
		}
		Assert.assertFalse(connectionPool.hasFreeConnections());
		connectionPool.destroy();
	}

	@Test
	public void testKeepAliveInactive() throws Exception {
		ConnectionPool connectionPool = new ConnectionPool("localhost", 8080, 0);
		connectionPool.setMaxConnections(2);
		connectionPool.setMinConnections(2);
		IConnection connection = connectionPool.getConnection();
		// check log if no keep alive has been sent
		connectionPool.freeConnection(connection);
		connectionPool.destroy();
	}

	@Test
	public void testKeepAliveActive() throws Exception {
		ConnectionPool connectionPool = new ConnectionPool("localhost", 8080, 5);
		connectionPool.setMaxConnections(1);
		connectionPool.setMinConnections(1);
		connectionPool.initMinConnections();
		// check log if keep alive has been sent for connection
		IConnection connection = connectionPool.getConnection();
		// check log if no keep alive has been sent for connection
		connectionPool.freeConnection(connection);
		connectionPool.destroy();
	}

	@Test
	public void testConnectionCloseAfterTenKeepAlive() throws Exception {
		ConnectionPool connectionPool = new ConnectionPool("localhost", 8080, 1);
		connectionPool.setMaxConnections(2);
		connectionPool.setMinConnections(2);
		connectionPool.initMinConnections();
		Thread.sleep(10000);
		connectionPool.destroy();
	}

	@Test
	public void testCloseAfterFreeConnection() throws Exception {
		ConnectionPool connectionPool = new ConnectionPool("localhost", 8080);
		connectionPool.setCloseOnFree(true);
		IConnection connection = connectionPool.getConnection();
		connectionPool.freeConnection(connection);
	}

	@Test
	public void testHundredConnectionsInUse() throws Exception {
		ConnectionPool connectionPool = new ConnectionPool("localhost", 8080, 5);
		List<IConnection> connections = new ArrayList<IConnection>();
		for (int i = 0; i < 100; i++) {
			connections.add(connectionPool.getConnection());
		}
		Assert.assertFalse(connectionPool.hasFreeConnections());
		Thread.sleep(8000);
		for (int i = 0; i < 100; i++) {
			connectionPool.freeConnection(connections.remove(0));
		}
		connectionPool.destroy();
	}
}
