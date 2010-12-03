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
package org.serviceconnector.test.unit.srv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.security.InvalidParameterException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;


public class SCServerTest {

	/** The Constant testLogger. */
	protected static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCServerTest.class);
	
	private SCServer server;

	@Before
	public void beforeOneTest() {
		server = null;
	}
	
	@After
	public void afterOneTest() {
		try {
			server.stopListener();
		} catch (Exception e) {
		}
		server = null;
	}
	
	/**
	 * Description:	Invoke SCServer constructor with host, port and listener port. <br>
	 * Expectation: Host, Port and listener Port was set
	 */
	@Test
	public void t01_constructor() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER);
		assertEquals("Host not equal", TestConstants.HOST, server.getSCHost());
		assertEquals("Port not equal", TestConstants.PORT_TCP, server.getSCPort());
		assertEquals("Listener Port not equal", TestConstants.PORT_LISTENER, server.getListenerPort());
		assertEquals("Default ConnectionType not set", ConnectionType.DEFAULT_SERVER_CONNECTION_TYPE, server.getConnectionType());
		assertNotNull(server);
		server.startListener();
		assertEquals("Listener is not running", true, server.isListening());
	}

	/**
	 * Description:	Invoke SCServer constructor with host=null, port and listener port. <br>
	 * Expectation: throws InvalidParameterException
	 */
	@Test (expected = InvalidParameterException.class)
	public void t02_constructor() throws Exception {
		server = new SCServer(null, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER);
		assertEquals("Host not equal", null, server.getSCHost());
		assertEquals("Port not equal", TestConstants.PORT_TCP, server.getSCPort());
		assertEquals("Listener Port not equal", TestConstants.PORT_LISTENER, server.getListenerPort());
		assertEquals("Default ConnectionType not set", ConnectionType.DEFAULT_SERVER_CONNECTION_TYPE, server.getConnectionType());
		assertNotNull(server);
		server.startListener();
	}

	/**
	 * Description:	Invoke SCServer constructor with host, port=Integer.MIN_VALUE and listener port. <br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t03_constructor() throws Exception {
		server = new SCServer(TestConstants.HOST, Integer.MIN_VALUE, TestConstants.PORT_LISTENER);
		assertEquals("Host not equal", TestConstants.HOST, server.getSCHost());
		assertEquals("Port not equal", Integer.MIN_VALUE, server.getSCPort());
		assertEquals("Listener Port not equal", TestConstants.PORT_LISTENER, server.getListenerPort());
		assertEquals("Default ConnectionType not set", ConnectionType.DEFAULT_SERVER_CONNECTION_TYPE, server.getConnectionType());
		assertNotNull(server);
		server.startListener();
	}

	/**
	 * Description:	Invoke SCServer constructor with host, port and listener port=Integer.MIN_VALUE. <br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t04_constructor() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, Integer.MIN_VALUE);
		assertEquals("Host not equal", TestConstants.HOST, server.getSCHost());
		assertEquals("Port not equal", TestConstants.PORT_TCP, server.getSCPort());
		assertEquals("Listener Port not equal", Integer.MIN_VALUE, server.getListenerPort());
		assertEquals("Default ConnectionType not set", ConnectionType.DEFAULT_SERVER_CONNECTION_TYPE, server.getConnectionType());
		assertNotNull(server);
		server.startListener();
	}
	
	/**
	 * Description:	Invoke SCServer constructor with host, port, listener port and connection type. <br>
	 * Expectation: throws SCMPCommunicationException
	 */
	@Test
	public void t05_constructor() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP);
		assertEquals("Host not equal", TestConstants.HOST, server.getSCHost());
		assertEquals("Port not equal", TestConstants.PORT_TCP, server.getSCPort());
		assertEquals("Listener Port not equal", TestConstants.PORT_LISTENER, server.getListenerPort());
		assertEquals("Connection Type not equal", ConnectionType.NETTY_TCP, server.getConnectionType());
		assertNotNull(server);
		server.startListener();
	}
	
	/**
	 * Description:	SCServer with host, port, listener port and connection type=null. <br>
	 * Expectation: throws InvalidParameterException
	 */
	@Test (expected = InvalidParameterException.class)
	public void t06_constructor() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, null);
		assertEquals("Host not equal", TestConstants.HOST, server.getSCHost());
		assertEquals("Port not equal", TestConstants.PORT_TCP, server.getSCPort());
		assertEquals("Listener Port not equal", TestConstants.PORT_LISTENER, server.getListenerPort());
		assertEquals("Connection Type not equal", null, server.getConnectionType());
		assertNotNull(server);
		server.startListener();
	}

	/**
	 * Description:	start listener to the same port <br>
	 * Expectation: throws InvalidParameterException
	 */
	@Test (expected = InvalidParameterException.class)
	public void t07_constructor() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		assertEquals("Host not equal", TestConstants.HOST, server.getSCHost());
		assertEquals("Port not equal", TestConstants.PORT_TCP, server.getSCPort());
		assertEquals("Listener Port not equal", TestConstants.PORT_TCP, server.getListenerPort());
		assertEquals("Connection Type not equal", ConnectionType.NETTY_TCP, server.getConnectionType());
		assertNotNull(server);
		server.startListener();
	}

	/**
	 * Description:	SCServer with nonexisting host <br>
	 * Expectation: throws InvalidParameterException
	 */
	@Test (expected = InvalidParameterException.class)
	public void t08_constructor() throws Exception {
		server = new SCServer("gaga", TestConstants.PORT_TCP, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		assertEquals("Host not equal", "gaga", server.getSCHost());
		assertEquals("Port not equal", TestConstants.PORT_TCP, server.getSCPort());
		assertEquals("Listener Port not equal", TestConstants.PORT_TCP, server.getListenerPort());
		assertEquals("Connection Type not equal", ConnectionType.NETTY_TCP, server.getConnectionType());
		assertNotNull(server);
		server.startListener();
	}
	
	/**
	 * Description:	Set KeepAliveInterval with valid value. <br>
	 * Expectation: KeepAliveInterval was set
	 */
	@Test
	public void t10_KeepAliveInterval() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP);
		server.setKeepAliveIntervalSeconds(10);
		assertEquals("KeepAliveInterval not equal", 10, server.getKeepAliveIntervalSeconds());
		assertNotNull(server);
		server.startListener();
	}

	/**
	 * Description:	Set KeepAliveInterval with invalid value. <br>
	 * Expectation: throws SCMPCommunicationException
	 */
	@Test
	public void t11_KeepAliveInterval() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP);
		server.setKeepAliveIntervalSeconds(-1);
		assertEquals("KeepAliveInterval not equal", -1, server.getKeepAliveIntervalSeconds());
		assertNotNull(server);
		server.startListener();
	}

	/**
	 * Description:	Set ImmediateConnect with valid value. <br>
	 * Expectation: ImmediateConnect was set
	 */
	@Test
	public void t20_ImmediateConnect() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP);
		assertEquals("ImmediateConnect not equal", false, server.isImmediateConnect());
		server.setImmediateConnect(true);
		assertEquals("ImmediateConnect not equal", true, server.isImmediateConnect());
		server.setImmediateConnect(false);
		assertEquals("ImmediateConnect not equal", false, server.isImmediateConnect());
		assertNotNull(server);
		server.startListener();
	}

	/**
	 * Description:	Start and stop Listener. <br>
	 * Expectation: Listener is stopped
	 */
	@Test
	public void t30_Listener() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP);
		assertNotNull(server);
		server.startListener();
		assertEquals("Listener is not running", true, server.isListening());
		server.stopListener();
		assertEquals("Listener is running", false, server.isListening());
	}
	

}
