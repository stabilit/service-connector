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
package org.serviceconnector.test.integration.api.cln;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.Constants;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.test.integration.api.APIIntegrationSuperClientTest;

public class APIAttachDetachClientTest extends APIIntegrationSuperClientTest {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(APIAttachDetachClientTest.class);

	/**
	 * helper create the client with different parameters
	 * @param host
	 * @param port
	 * @param connectionType
	 * @return the created client
	 */
	private SCClient newSCClient(String host, int port, ConnectionType connectionType) {
		SCClient client = new SCClient(host, port, connectionType);
		Assert.assertEquals("Host ", host, client.getHost());
		Assert.assertEquals("port ", port, client.getPort());
		Assert.assertEquals("Keep Alive Interval ", Constants.DEFAULT_KEEP_ALIVE_INTERVAL_SECONDS, client.getKeepAliveIntervalSeconds());
		Assert.assertEquals("Attached ", false, client.isAttached());
		Assert.assertEquals("max Connections ", Constants.DEFAULT_MAX_CONNECTION_POOL_SIZE, client.getMaxConnections());
		Assert.assertEquals("Connection Type ", connectionType, client.getConnectionType());
		Assert.assertNotNull("Client not created:", client);
		return client;
	}
	
	/**
	 * Description: Attach client to SC on localhost, tcp-port and tcp-connection type<br> 
	 * Expectation:	Client is attached.
	 */
	@Test
	public void t010_attach() throws Exception {
		client = newSCClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
	}

	/**
	 * Description: Attach client to SC on localhost, http-port and http-connection type<br> 
	 * Expectation:	Client is attached.
	 */
	@Test
	public void t020_attach() throws Exception {
		client = newSCClient(TestConstants.HOST, TestConstants.PORT_SC_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
	}

	
	/**
	 * Description: Attach client to SC on localhost, tcp-port and http-connection type<br> 
	 * Expectation:	Client is attached.
	 */
	@Test (expected = SCServiceException.class)
	public void t030_attach() throws Exception {
		client = newSCClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_HTTP);
		client.attach();
	}

	/**
	 * Description: attach client to SC on localhost, http-port and tcp-connection type<br>
	 * Expectation:	throws SCServiceException.
	 */
	@Test (expected = SCServiceException.class)
	public void t040_attach() throws Exception {
		client = newSCClient(TestConstants.HOST, TestConstants.PORT_SC_HTTP, ConnectionType.NETTY_TCP);
		client.attach();
	}

	/**
	 * Description: Attach client with default host and port zero.<br>
	 * Expectation:	throws SCMPValidatorException.
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t050_attachPortZero() throws Exception {
		client = newSCClient(TestConstants.HOST, 0, ConnectionType.NETTY_TCP);
		client.attach();
	}

	/**
	 * Description: Attach client with default host and port -1.<br>
	 * Expectation:	throws SCMPValidatorException.
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t060_attachPortNegative() throws Exception {
		client = newSCClient(TestConstants.HOST, -1, ConnectionType.NETTY_TCP);
		client.attach();
	}

	/**
	 * Description: Attach client with default host and port is set to minimum.<br>
	 * Expectation:	throws SCServiceException because SC is not listing to this port
	 */
	@Test (expected = SCServiceException.class)
	public void t070_attachPortMinimum() throws Exception {
		client = newSCClient(TestConstants.HOST, TestConstants.PORT_MIN, ConnectionType.NETTY_TCP);
		client.attach();
	}

	/**
	 * Description: Attach client with default host and port is set to minimum - 1.<br>
	 * Expectation:	throws SCMPValidatorException.
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t080_attachPortMinimum() throws Exception {
		client = newSCClient(TestConstants.HOST, TestConstants.PORT_MIN - 1, ConnectionType.NETTY_TCP);
		client.attach();
	}
	
	/**
	 * Description: Attach client with default host and the port is set to maximum + 1.<br>
	 * Expectation:	throws SCMPValidatorException.
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t100_attachPortOverMaximum() throws Exception {
		client = newSCClient(TestConstants.HOST, TestConstants.PORT_MAX + 1, ConnectionType.NETTY_TCP);
		client.attach();
	}

	/**
	 * Description: Attach client with default host and tcp-port and timeout of 10 seconds<br>
	 * Expectation:	passes
	 */
	@Test
	public void t110_attachTimeout() throws Exception {
		client = newSCClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		client.attach(10);
		Assert.assertEquals("Client is not attached", true, client.isAttached());
	}
	
	
	/**
	 * Description: Attach one client two times with default host and tcp-port.<br>
	 * Expectation:	throws Exception
	 */
	@Test (expected = SCServiceException.class)
	public void t120_attachTwoTimes() throws Exception {
		client = newSCClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		client.attach();	// second attach throws SCServiceException			
	}

	/**
	 * Description: setKeepAliveIntervalSeconds after attach.<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t130_setKeepAliveIntervalSeconds() throws Exception {
		client = newSCClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		client.setKeepAliveIntervalSeconds(10);	// too late => throws SCServiceException			
	}
	
	/**
	 * Description: setMaxConnections after attach<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t140_setMaxConnections() throws Exception {
		client = newSCClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		client.setMaxConnections(10);	// too late => throws SCServiceException			
	}

	/**
	 * Description: Attach and detach one time to SC on localhost, http-port and http-connection.<br>
	 * Expectation:	Client is detached.
	 */
	@Test
	public void t01_attachDetach() throws Exception {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_SC_HTTP, ConnectionType.NETTY_HTTP);
		Assert.assertEquals("Client is attached", false, client.isAttached());
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		client.detach();
		Assert.assertEquals("Client is attached", false, client.isAttached());
	}

	
	/**
	 * Description: Attach two times the same client to SC on localhost  http-connection type.<br>
	 * Expectation:	Throws SCServiceException on the second attach.
	 */
	@Test (expected = SCServiceException.class)
	public void t02_attachTwoTimes() throws Exception {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_SC_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		client.attach();
	}

	/**
	 * Description: Detach the client without attach.<br>
	 * Expectation:	Client is detached.
	 */
	@Test
	public void t03_detachNoAttach() throws Exception {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_SC_HTTP, ConnectionType.NETTY_HTTP);
		Assert.assertEquals("Client is attached", false, client.isAttached());
		client.detach();
		Assert.assertEquals("Client is attached", false, client.isAttached());
	}

	/**
	 * Description: first attach, then detach 100 times.<br>
	 * Expectation:	Client is detached.
	 */
	@Test
	public void t04_attachDetach100times() throws Exception {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_SC_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		int nr = 100;
		for (int i = 0; i < nr; i++) {
			client.detach();
		}
		Assert.assertEquals("Client is attached", false, client.isAttached());
	}	

}
