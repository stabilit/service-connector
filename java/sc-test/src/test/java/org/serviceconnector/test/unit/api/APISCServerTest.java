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
package org.serviceconnector.test.unit.api;

import java.security.InvalidParameterException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;


public class APISCServerTest {

	/** The Constant testLogger. */
	protected static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(APISCServerTest.class);
	
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
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP);
		Assert.assertEquals("Host not equal", TestConstants.HOST, server.getSCHost());
		Assert.assertEquals("Port not equal", TestConstants.PORT_SC_TCP, server.getSCPort());
		Assert.assertEquals("Listener Port not equal", TestConstants.PORT_SES_SRV_TCP, server.getListenerPort());
		Assert.assertEquals("Default ConnectionType not set", ConnectionType.DEFAULT_SERVER_CONNECTION_TYPE, server.getConnectionType());
		Assert.assertNotNull(server);
		server.startListener();
		Assert.assertEquals("Listener is not running", true, server.isListening());
	}

	/**
	 * Description:	Invoke SCServer constructor with host=null, port and listener port. <br>
	 * Expectation: throws InvalidParameterException
	 */
	@Test (expected = InvalidParameterException.class)
	public void t02_constructor() throws Exception {
		server = new SCServer(null, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP);
		Assert.assertEquals("Host not equal", null, server.getSCHost());
		Assert.assertEquals("Port not equal", TestConstants.PORT_SC_TCP, server.getSCPort());
		Assert.assertEquals("Listener Port not equal", TestConstants.PORT_SES_SRV_TCP, server.getListenerPort());
		Assert.assertEquals("Default ConnectionType not set", ConnectionType.DEFAULT_SERVER_CONNECTION_TYPE, server.getConnectionType());
		Assert.assertNotNull(server);
		server.startListener();
	}

	/**
	 * Description:	Invoke SCServer constructor with host, port=Integer.MIN_VALUE and listener port. <br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t03_constructor() throws Exception {
		server = new SCServer(TestConstants.HOST, Integer.MIN_VALUE, TestConstants.PORT_SES_SRV_TCP);
		Assert.assertEquals("Host not equal", TestConstants.HOST, server.getSCHost());
		Assert.assertEquals("Port not equal", Integer.MIN_VALUE, server.getSCPort());
		Assert.assertEquals("Listener Port not equal", TestConstants.PORT_SES_SRV_TCP, server.getListenerPort());
		Assert.assertEquals("Default ConnectionType not set", ConnectionType.DEFAULT_SERVER_CONNECTION_TYPE, server.getConnectionType());
		Assert.assertNotNull(server);
		server.startListener();
	}

	/**
	 * Description:	Invoke SCServer constructor with host, port and listener port=Integer.MIN_VALUE. <br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t04_constructor() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, Integer.MIN_VALUE);
		Assert.assertEquals("Host not equal", TestConstants.HOST, server.getSCHost());
		Assert.assertEquals("Port not equal", TestConstants.PORT_SC_TCP, server.getSCPort());
		Assert.assertEquals("Listener Port not equal", Integer.MIN_VALUE, server.getListenerPort());
		Assert.assertEquals("Default ConnectionType not set", ConnectionType.DEFAULT_SERVER_CONNECTION_TYPE, server.getConnectionType());
		Assert.assertNotNull(server);
		server.startListener();
	}
	
	/**
	 * Description:	Invoke SCServer constructor with host, port, listener port and connection type. <br>
	 * Expectation: throws SCMPCommunicationException
	 */
	@Test
	public void t05_constructor() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP);
		Assert.assertEquals("Host not equal", TestConstants.HOST, server.getSCHost());
		Assert.assertEquals("Port not equal", TestConstants.PORT_SC_TCP, server.getSCPort());
		Assert.assertEquals("Listener Port not equal", TestConstants.PORT_SES_SRV_TCP, server.getListenerPort());
		Assert.assertEquals("Connection Type not equal", ConnectionType.NETTY_TCP, server.getConnectionType());
		Assert.assertNotNull(server);
		server.startListener();
	}
	
	/**
	 * Description:	SCServer with host, port, listener port and connection type=null. <br>
	 * Expectation: throws InvalidParameterException
	 */
	@Test (expected = InvalidParameterException.class)
	public void t06_constructor() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, null);
		Assert.assertEquals("Host not equal", TestConstants.HOST, server.getSCHost());
		Assert.assertEquals("Port not equal", TestConstants.PORT_SC_TCP, server.getSCPort());
		Assert.assertEquals("Listener Port not equal", TestConstants.PORT_SES_SRV_TCP, server.getListenerPort());
		Assert.assertEquals("Connection Type not equal", null, server.getConnectionType());
		Assert.assertNotNull(server);
		server.startListener();
	}

	/**
	 * Description:	start listener to the same port <br>
	 * Expectation: throws InvalidParameterException
	 */
	@Test (expected = InvalidParameterException.class)
	public void t07_constructor() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		Assert.assertEquals("Host not equal", TestConstants.HOST, server.getSCHost());
		Assert.assertEquals("Port not equal", TestConstants.PORT_SC_TCP, server.getSCPort());
		Assert.assertEquals("Listener Port not equal", TestConstants.PORT_SC_TCP, server.getListenerPort());
		Assert.assertEquals("Connection Type not equal", ConnectionType.NETTY_TCP, server.getConnectionType());
		Assert.assertNotNull(server);
		server.startListener();
	}

	/**
	 * Description:	SCServer with non-existing host <br>
	 * Expectation: throws InvalidParameterException
	 */
	@Test (expected = InvalidParameterException.class)
	public void t08_constructor() throws Exception {
		server = new SCServer("gaga", TestConstants.PORT_SC_TCP, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		Assert.assertEquals("Host not equal", "gaga", server.getSCHost());
		Assert.assertEquals("Port not equal", TestConstants.PORT_SC_TCP, server.getSCPort());
		Assert.assertEquals("Listener Port not equal", TestConstants.PORT_SC_TCP, server.getListenerPort());
		Assert.assertEquals("Connection Type not equal", ConnectionType.NETTY_TCP, server.getConnectionType());
		Assert.assertNotNull(server);
		server.startListener();
	}
	
	/**
	 * Description:	SCServer with empty host <br>
	 * Expectation: throws InvalidParameterException
	 */
	@Test (expected = InvalidParameterException.class)
	public void t09_constructor() throws Exception {
		server = new SCServer(" ", TestConstants.PORT_SC_TCP, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		Assert.assertEquals("Host not equal", " ", server.getSCHost());
		Assert.assertEquals("Port not equal", TestConstants.PORT_SC_TCP, server.getSCPort());
		Assert.assertEquals("Listener Port not equal", TestConstants.PORT_SC_TCP, server.getListenerPort());
		Assert.assertEquals("Connection Type not equal", ConnectionType.NETTY_TCP, server.getConnectionType());
		Assert.assertNotNull(server);
		server.startListener();
	}

	/**
	 * Description:	SCServer with host = null <br>
	 * Expectation: throws InvalidParameterException
	 */
	@Test (expected = InvalidParameterException.class)
	public void t10_constructor() throws Exception {
		server = new SCServer(null, TestConstants.PORT_SC_TCP, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		Assert.assertEquals("Host not equal", null, server.getSCHost());
		Assert.assertEquals("Port not equal", TestConstants.PORT_SC_TCP, server.getSCPort());
		Assert.assertEquals("Listener Port not equal", TestConstants.PORT_SC_TCP, server.getListenerPort());
		Assert.assertEquals("Connection Type not equal", ConnectionType.NETTY_TCP, server.getConnectionType());
		Assert.assertNotNull(server);
		server.startListener();
	}	

	/**
	 * Description:	SCServer with port = 0 <br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t11_constructor() throws Exception {
		server = new SCServer(TestConstants.HOST, 0, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		Assert.assertEquals("Host not equal", TestConstants.HOST, server.getSCHost());
		Assert.assertEquals("Port not equal", 0, server.getSCPort());
		Assert.assertEquals("Listener Port not equal", TestConstants.PORT_SC_TCP, server.getListenerPort());
		Assert.assertEquals("Connection Type not equal", ConnectionType.NETTY_TCP, server.getConnectionType());
		Assert.assertNotNull(server);
		server.startListener();
	}	

	/**
	 * Description:	SCServer with port = MIN <br>
	 * Expectation: passes
	 */
	@Test
	public void t12_constructor() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_MIN, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		Assert.assertEquals("Host not equal", TestConstants.HOST, server.getSCHost());
		Assert.assertEquals("Port not equal", TestConstants.PORT_MIN, server.getSCPort());
		Assert.assertEquals("Listener Port not equal", TestConstants.PORT_SC_TCP, server.getListenerPort());
		Assert.assertEquals("Connection Type not equal", ConnectionType.NETTY_TCP, server.getConnectionType());
		Assert.assertNotNull(server);
		server.startListener();
	}	

	/**
	 * Description:	SCServer with port = MAX <br>
	 * Expectation: passes
	 */
	@Test
	public void t13_constructor() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_MAX, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		Assert.assertEquals("Host not equal", TestConstants.HOST, server.getSCHost());
		Assert.assertEquals("Port not equal", TestConstants.PORT_MAX, server.getSCPort());
		Assert.assertEquals("Listener Port not equal", TestConstants.PORT_SC_TCP, server.getListenerPort());
		Assert.assertEquals("Connection Type not equal", ConnectionType.NETTY_TCP, server.getConnectionType());
		Assert.assertNotNull(server);
		server.startListener();
	}	

	/**
	 * Description:	SCServer with port = MAX + 1 <br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t14_constructor() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_MAX + 1, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		Assert.assertEquals("Host not equal", TestConstants.HOST, server.getSCHost());
		Assert.assertEquals("Port not equal", TestConstants.PORT_MAX + 1, server.getSCPort());
		Assert.assertEquals("Listener Port not equal", TestConstants.PORT_SC_TCP, server.getListenerPort());
		Assert.assertEquals("Connection Type not equal", ConnectionType.NETTY_TCP, server.getConnectionType());
		Assert.assertNotNull(server);
		server.startListener();
	}	

	
	
	/**
	 * Description:	Set KeepAliveInterval with valid value. <br>
	 * Expectation: KeepAliveInterval was set
	 */
	@Test
	public void t60_KeepAliveInterval() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP);
		server.setKeepAliveIntervalSeconds(10);
		Assert.assertEquals("KeepAliveInterval not equal", 10, server.getKeepAliveIntervalSeconds());
		Assert.assertNotNull(server);
		server.startListener();
	}

	/**
	 * Description:	Set KeepAliveInterval with invalid value. <br>
	 * Expectation: throws SCMPCommunicationException
	 */
	@Test
	public void t61_KeepAliveInterval() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP);
		server.setKeepAliveIntervalSeconds(-1);
		Assert.assertEquals("KeepAliveInterval not equal", -1, server.getKeepAliveIntervalSeconds());
		Assert.assertNotNull(server);
		server.startListener();
	}

	/**
	 * Description:	Set ImmediateConnect with valid value. <br>
	 * Expectation: ImmediateConnect was set
	 */
	@Test
	public void t70_ImmediateConnect() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP);
		Assert.assertEquals("ImmediateConnect not equal", false, server.isImmediateConnect());
		server.setImmediateConnect(true);
		Assert.assertEquals("ImmediateConnect not equal", true, server.isImmediateConnect());
		server.setImmediateConnect(false);
		Assert.assertEquals("ImmediateConnect not equal", false, server.isImmediateConnect());
		Assert.assertNotNull(server);
		server.startListener();
	}

	/**
	 * Description:	Start and stop Listener. <br>
	 * Expectation: Listener is stopped
	 */
	@Test
	public void t80_Listener() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP);
		Assert.assertNotNull("server exists", server);
		server.startListener();
		Assert.assertEquals("Listener is not running", true, server.isListening());
		server.stopListener();
		Assert.assertEquals("Listener is running", false, server.isListening());
	}
	
	/**
	 * Description:	Start and stop Listener 100 times. <br>
	 * Expectation: Listener is stopped
	 */
	@Test
	public void t81_Listener() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP);
		Assert.assertNotNull("server exists", server);
		int nr = 100;
		int sleep = 0;
		for (int i = 0; i < nr; i++) {
			if (((i+1) % 10) == 0) testLogger.info("Start/stop listener nr. " + (i+1) + "...");
			server.startListener();
			Assert.assertEquals("Listener is running", true, server.isListening());
			if (sleep > 0) 
				Thread.sleep(sleep);
			server.stopListener();
			Assert.assertEquals("Listener is not running", false, server.isListening());
		}


	}

}
