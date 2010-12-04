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
/**
 * 
 */
package org.serviceconnector.test.unit.cln;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;

/**
 * @author FJurnecka
 * 
 */
public class SCClientTest {

	/** The Constant testLogger. */
	protected static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCClientTest.class);
	
	private SCClient client;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void beforeOneTest() {
		client = null;
	}
	@After
	public void afterOneTest(){
		client = null;
	}

	/**
	 * Description: Invoke Constructor with Host and Port<br>
	 * Expectation: Host and Port was set
	 */
	@Test
	public void t01_construtor() {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP);
		assertEquals("Host not equal", TestConstants.HOST, client.getHost());
		assertEquals("Port not equal", TestConstants.PORT_TCP, client.getPort());
		assertNotNull(client);
	}

	/**
	 * Description: Invoke Constructor with Host, Port and connection Type<br>
	 * Expectation: Host, Port and connection Type was set
	 */
	@Test
	public void t02_construtor() {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP );
		assertEquals("Host not equal", TestConstants.HOST, client.getHost());
		assertEquals("Port not equal", TestConstants.PORT_TCP, client.getPort());
		assertEquals("Connection Type not equal", ConnectionType.NETTY_TCP, client.getConnectionType());
		assertNotNull(client);
	}

	
	/**
	 * Description: Invoke Constructor with Host, Port and connection Type<br>
	 * Expectation: Host, Port and connection Type was set
	 */
	@Test
	public void t03_construtor() {
		client = new SCClient(null, TestConstants.PORT_TCP);
		assertEquals("Host not equal", null, client.getHost());
		assertEquals("Port not equal", TestConstants.PORT_TCP, client.getPort());
		assertNotNull(client);
	}

	/**
	 * Description: Invoke Constructor with Host, Port and connection Type<br>
	 * Expectation: Host, Port and connection Type was set
	 */
	@Test
	public void t04_construtor() {
		client = new SCClient(null, -1);
		assertEquals("Host not equal", null, client.getHost());
		assertEquals("Port not equal", -1, client.getPort());
		assertNotNull(client);
	}

	/**
	 * Description: Invoke setMaxConnections with 0 value<br>
	 * Expectation: throws validation exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t10_maxConnections() throws Exception {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP );
		client.setMaxConnections(0);
	}
	
	/**
	 * Description: Invoke setMaxConnections with value = Integer.MIN_VALUE<br>
	 * Expectation: throws validation exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t11_maxConnections() throws Exception {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP );
		client.setMaxConnections(Integer.MIN_VALUE);
	}

	/**
	 * Description: Invoke setMaxConnections with value = Integer.MAX_VALUE<br>
	 * Expectation: value = MAX was properly set
	 */
	@Test
	public void t12_maxConnections() throws Exception {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP );
		client.setMaxConnections(Integer.MAX_VALUE);
		assertEquals("MaxConnections not equal", Integer.MAX_VALUE, client.getMaxConnections());
	}

	/**
	 * Description: Invoke setMaxConnections with value = -1<br>
	 * Expectation: throws validation exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t13_maxConnections() throws Exception {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP );
		client.setMaxConnections(-1);
	}

	/**
	 * Description: Invoke setMaxConnections with value = 1<br>
	 * Expectation: value = 1 was properly set
	 */
	@Test
	public void t14_maxConnections() throws Exception {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP );
		client.setMaxConnections(1);
		assertEquals("MaxConnections not equal", 1, client.getMaxConnections());
	}
	
	/**
	 * Description: Invoke keep alive Interval with value = 0<br>
	 * Expectation: value = 0 was properly set
	 */
	@Test
	public void t20_keepAliveInterval() throws Exception {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP );
		client.setKeepAliveIntervalSeconds(0); // can be set before attach
		assertEquals("MaxConnections not equal", 0, client.getKeepAliveIntervalSeconds());
	}
	
	/**
	 * Description: Invoke keep alive Interval with value = Integer.MAX_VALUE<br>
	 * Expectation: value = Integer.MAX_VALUE was properly set
	 */
	@Test
	public void t21_keepAliveInterval() throws Exception {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP );
		client.setKeepAliveIntervalSeconds(Integer.MAX_VALUE); // can be set before attach
		assertEquals("MaxConnections not equal", Integer.MAX_VALUE, client.getKeepAliveIntervalSeconds());
	}

	/**
	 * Description: Invoke keep alive Interval with value = Integer.MIN_VALUE<br>
	 * Expectation: value = Integer.MMIN_VALUE was properly set
	 */
	@Test
	public void t22_keepAliveInterval() throws Exception {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP );
		client.setKeepAliveIntervalSeconds(Integer.MIN_VALUE); // can be set before attach
		assertEquals("MaxConnections not equal", Integer.MIN_VALUE, client.getKeepAliveIntervalSeconds());
	}
}
	
