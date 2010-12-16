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
package org.serviceconnector.test.integration.scmp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.serviceconnector.Constants;
import org.serviceconnector.TestConstants;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.connection.ConnectionPool;
import org.serviceconnector.net.connection.ConnectionPoolBusyException;
import org.serviceconnector.net.connection.IConnection;

/**
 * @author JTraber
 */
@RunWith(Parameterized.class)
public class ConnectionPoolTest {

	/** The Constant testLogger. */
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCMPAttachDetachTest.class);

	private int port;
	private ConnectionType connectionType;
	private int keepAlivInSeconds = 1;

	private static ProcessesController ctrl;
	private static ProcessCtx scCtx;
	private ConnectionPool connectionPool;
	private int threadCount = 0;

	public ConnectionPoolTest(Integer port, ConnectionType connectionType) {
		this.port = port;
		this.connectionType = connectionType;
	}

	@Parameters
	public static Collection<Object[]> getParameters() {
		return Arrays.asList( //
				new Object[] { new Integer(TestConstants.PORT_TCP), ConnectionType.NETTY_TCP }, //
				new Object[] { new Integer(TestConstants.PORT_HTTP), ConnectionType.NETTY_HTTP });
	}

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
	}

	@Before
	public void beforeOneTest() throws Exception {
		threadCount = Thread.activeCount();
		connectionPool = new ConnectionPool(TestConstants.HOST, this.port, this.connectionType.getValue(), this.keepAlivInSeconds);
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			connectionPool.destroy();
		} catch (Exception e) {
		}
		connectionPool = null;
		testLogger.info("Number of threads :" + Thread.activeCount() + " created :" + (Thread.activeCount() - threadCount));
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		try {
			ctrl.stopSC(scCtx);
			scCtx = null;
		} catch (Exception e) {
		}
		ctrl = null;
	}

	/**
	 * Description: Gets a connection and frees it<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_GetAndFreeConnection() throws Exception {
		IConnection connection = connectionPool.getConnection();
		connectionPool.freeConnection(connection);
	}

	/**
	 * Description: Set max connection to 2, tries getting 3 connections<br>
	 * Expectation: throws ConnectionPoolBusyException
	 */
	@Test(expected = ConnectionPoolBusyException.class)
	public void t10_ConnectionPoolBusy() throws Exception {
		connectionPool.setMaxConnections(2);
		for (int i = 0; i < 3; i++) {
			connectionPool.getConnection();
		}
		Assert.assertFalse(connectionPool.hasFreeConnections());
		connectionPool.getConnection();
	}

	/**
	 * Description: Set max connection to 2, gets 2 connections<br>
	 * Expectation: passes
	 */
	@Test
	public void t11_ConnectionPoolHasNoFreeConnections() throws Exception {
		connectionPool.setMaxConnections(2);
		for (int i = 0; i < 2; i++) {
			connectionPool.getConnection();
		}
		Assert.assertFalse(connectionPool.hasFreeConnections());
	}

	/**
	 * Description: Send keep alive over connection<br>
	 * Expectation: passes
	 */
	@Test
	public void t20_KeepAliveActive() throws Exception {
		connectionPool.setMaxConnections(1);
		connectionPool.setMinConnections(1);
		connectionPool.initMinConnections();
		Thread.sleep((long) ((this.keepAlivInSeconds + 0.2) * Constants.SEC_TO_MILLISEC_FACTOR));
		IConnection connection = connectionPool.getConnection();
		Assert.assertTrue(connection.getNrOfIdlesInSequence() > 0);
		connectionPool.freeConnection(connection);
	}

	/**
	 * Description: Get connection assert no keep alive will be sent<br>
	 * Expectation: passes
	 */
	@Test
	public void t21_NoKeepAliveForUsedConnections() throws Exception {
		connectionPool.setMaxConnections(1);
		IConnection connection = connectionPool.getConnection();
		Thread.sleep((long) ((this.keepAlivInSeconds + 0.2) * Constants.SEC_TO_MILLISEC_FACTOR));
		Assert.assertTrue(connection.getNrOfIdlesInSequence() == 0);
		Assert.assertTrue(connection.isConnected());
	}

	/**
	 * Description: Close after free connection<br>
	 * Expectation: passes
	 */
	@Test
	public void t30_CloseAfterFreeConnection() throws Exception {
		connectionPool.setCloseOnFree(true);
		IConnection connection = connectionPool.getConnection();
		IConnection connection2 = connectionPool.getConnection();
		connectionPool.freeConnection(connection);
		Assert.assertFalse(connection.isConnected());
		connectionPool.freeConnection(connection2);
		Assert.assertTrue(connection2.isConnected());
	}

	/**
	 * Description: Test 100 connections in use<br>
	 * Expectation: passes
	 */
	@Test
	public void t40_HundredConnectionsInUse() throws Exception {
		List<IConnection> connections = new ArrayList<IConnection>();
		for (int i = 0; i < 100; i++) {
			connections.add(connectionPool.getConnection());
		}
		Assert.assertFalse(connectionPool.hasFreeConnections());
	}
}
