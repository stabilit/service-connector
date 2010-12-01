/*
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
 */
package org.serviceconnector.test.integration.cln;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.Constants;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;

public class AttachTest {

	/** The Constant testLogger. */
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(AttachTest.class);

	private static ProcessesController ctrl;
	private static ProcessCtx scCtx;
	private SCClient client;
	
	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
	}

	@Before
	public void beforeOneTest() throws Exception {
	}
	
	@After
	public void afterOneTest() throws Exception {
		try {
			client.detach();
		} catch (Exception e) {}
		client = null;
	}
	
	@AfterClass
	public static void afterAllTests() throws Exception {
		try {
			ctrl.stopSC(scCtx);
			scCtx = null;
		} catch (Exception e) {}
		ctrl = null;
	}


	/**
	 * helper for creation of a new client.
	 * @param host
	 * @param port
	 * @param connectionType
	 */
	private void newSCClient(String host, int port, ConnectionType connectionType) {
		client = new SCClient(host, port, connectionType);
		assertEquals("Host ", host, client.getHost());
		assertEquals("port ", port, client.getPort());
		assertEquals("Keep Alive Interval ", Constants.DEFAULT_KEEP_ALIVE_INTERVAL, client.getKeepAliveIntervalInSeconds());
		assertEquals("Attached ", false, client.isAttached());
		assertEquals("max Connections ", Constants.DEFAULT_MAX_CONNECTION_POOL_SIZE, client.getMaxConnections());
		assertEquals("Connection Type ", connectionType.getValue(), client.getConnectionType());
		assertNotNull("Client not created:", client);
	}
	

	/**
	 * Description: Attach client with: <br> 
	 * 		host: localhost<br>
	 * 		port: tcp<br>
	 * 		type: tcp<br>
	 * Expectation:	Client is attached.
	 */
	@Test
	public void t01_attach() throws Exception {
		this.newSCClient(TestConstants.LOCALHOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		assertEquals("Client is attached", true, client.isAttached());
	}

	/**
	 * Description: Attach client with default host and tcp-port.<br>
	 * Expectation:	Client is attached.
	 */
	@Test (expected = SCServiceException.class)
	public void t011_attach() throws Exception {
		this.newSCClient(TestConstants.LOCALHOST, TestConstants.PORT_TCP, ConnectionType.NETTY_HTTP);
		client.attach();
	}

	/**
	 * Description: Attach client with default host and http-port.<br>
	 * Expectation:	Client is not attached and throws Exception.
	 */
	@Test (expected = SCServiceException.class)
	public void t02_attach() throws Exception {
		this.newSCClient(TestConstants.LOCALHOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_TCP);
		client.attach();
	}

	/**
	 * Description: Attach client with default host and port zero.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test (expected = SCServiceException.class)
	public void t03_attach() throws Exception {
		this.newSCClient(TestConstants.LOCALHOST, 0, ConnectionType.NETTY_TCP);
		client.attach();
	}

	/**
	 * Description: Attach client with default host and port -1.<br>
	 * Expectation:	Client is not attached and throws SCMPValidatorException.
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t04_attach() throws Exception {
		this.newSCClient(TestConstants.LOCALHOST, -1, ConnectionType.NETTY_TCP);
		client.attach();
	}

	/**
	 * Description: Attach client with default host and port is set to minimum.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test (expected = SCServiceException.class)
	public void t05_attach() throws Exception {
		this.newSCClient(TestConstants.LOCALHOST, TestConstants.PORT_MIN, ConnectionType.NETTY_TCP);
		client.attach();
	}

	/**
	 * Description: Attach client with default host and port is set to minimum.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test (expected = SCServiceException.class)
	public void t06_attach() throws Exception {
		this.newSCClient(TestConstants.LOCALHOST, TestConstants.PORT_MAX, ConnectionType.NETTY_TCP);
		client.attach();
	}
	
	/**
	 * Description: Attach client with default host and port is set to maximum allowed.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test (expected = SCServiceException.class)
	public void t07_attach() throws Exception {
		this.newSCClient(TestConstants.LOCALHOST, 0xFFFF, ConnectionType.NETTY_TCP);
		client.attach();
	}

	/**
	 * Description: Attach client with default host and the port is set to maximum + 1.<br>
	 * Expectation:	Client is not attached and throws SCMPValidatorException.
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t08_attach() throws Exception {
		this.newSCClient(TestConstants.LOCALHOST, 0xFFFF + 1, ConnectionType.NETTY_TCP);
		client.attach();
	}

	/**
	 * Description: Attach client with default host and tcp-port and timeout of 10 seconds<br>
	 * Expectation:	Client is attached.
	 */
	@Test
	public void t09_attach() throws Exception {
		this.newSCClient(TestConstants.LOCALHOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach(10);
		assertEquals("Client is attached", true, client.isAttached());
	}
	
	
	/**
	 * Description: Attach one client two times with default host and tcp-port.<br>
	 * Expectation:	Client is attached but second attach throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t20_attach() throws Exception {
		this.newSCClient(TestConstants.LOCALHOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		assertEquals("Client is attached", true, client.isAttached());
		client.attach();	// second attach throws SCServiceException			
	}
}
