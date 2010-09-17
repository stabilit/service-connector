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
package test.serviceconnector.pool;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.common.net.req.ConnectionPool;
import org.serviceconnector.common.net.req.ConnectionPoolBusyException;
import org.serviceconnector.common.net.req.IConnection;
import org.serviceconnector.common.net.req.IConnectionPool;

import test.serviceconnector.unit.SetupTestCases;


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
		IConnectionPool localCp = new ConnectionPool("localhost", 8080, "netty.http", 60);
		localCp.setMaxConnections(10);
		localCp.setMinConnections(1);

		localCp.setCloseOnFree(true);
		localCp.initMinConnections();
		localCp.destroy();
	}

	@Test
	public void testMinMaxWrong() throws Exception {
		IConnectionPool localCp = new ConnectionPool("localhost", 8080);
		localCp.setMaxConnections(2);
		localCp.setMinConnections(3);
		for (int i = 0; i < 2; i++) {
			localCp.getConnection();
		}
		try {
			// only two connections should be created, third connection fails
			localCp.getConnection();
			Assert.fail("Should throw exception");
		} catch (ConnectionPoolBusyException es) {
		}
		localCp.destroy();
	}

	@Test
	public void testGetAndFreeConnection() throws Exception {
		IConnectionPool localCp = new ConnectionPool("localhost", 8080);
		IConnection connection = localCp.getConnection();
		localCp.freeConnection(connection);
		localCp.destroy();
	}

	@Test
	public void testReachConnectionLimit() throws Exception {
		IConnectionPool localCp = new ConnectionPool("localhost", 8080);
		int maxConnections = 2;
		localCp.setMaxConnections(maxConnections);
		localCp.getConnection();
		localCp.getConnection();

		try {
			// only two connections should be created, third connection fails
			localCp.getConnection();
			Assert.fail("Should throw exception");
		} catch (ConnectionPoolBusyException es) {
		}
		Assert.assertFalse(localCp.hasFreeConnections());
		localCp.destroy();
	}

	@Test
	public void testKeepAliveInactive() throws Exception {
		IConnectionPool localCp = new ConnectionPool("localhost", 8080, 0);
		localCp.setMaxConnections(2);
		localCp.setMinConnections(2);
		IConnection connection = localCp.getConnection();
		// check log if no keep alive has been sent
		localCp.freeConnection(connection);
		localCp.destroy();
	}

	@Test
	public void testKeepAliveActive() throws Exception {
		IConnectionPool localCp = new ConnectionPool("localhost", 8080, 5);
		localCp.setMaxConnections(1);
		localCp.setMinConnections(1);
		localCp.initMinConnections();
		// check log if keep alive has been sent for connection
		IConnection connection = localCp.getConnection();
		// check log if no keep alive has been sent for connection
		localCp.freeConnection(connection);
		localCp.destroy();
	}

	@Test
	public void testConnectionCloseAfterTenKeepAlive() throws Exception {
		IConnectionPool localCp = new ConnectionPool("localhost", 8080, 1);
		localCp.setMaxConnections(2);
		localCp.setMinConnections(2);
		localCp.initMinConnections();
		Thread.sleep(10000);
		localCp.destroy();
	}

	@Test
	public void testCloseAfterFreeConnection() throws Exception {
		IConnectionPool localCp = new ConnectionPool("localhost", 8080);
		localCp.setCloseOnFree(true);
		IConnection connection = localCp.getConnection();
		localCp.freeConnection(connection);
	}

	@Test
	public void testHundredConnectionsInUse() throws Exception {
		IConnectionPool localCp = new ConnectionPool("localhost", 8080, 5);
		List<IConnection> connections = new ArrayList<IConnection>();
		for (int i = 0; i < 100; i++) {
			connections.add(localCp.getConnection());
		}
		Assert.assertFalse(localCp.hasFreeConnections());
		Thread.sleep(8000);
		for (int i = 0; i < 100; i++) {
			localCp.freeConnection(connections.remove(0));
		}
		localCp.destroy();
	}
}
