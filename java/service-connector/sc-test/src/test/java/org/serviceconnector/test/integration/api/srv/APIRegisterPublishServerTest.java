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
package org.serviceconnector.test.integration.api.srv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.srv.SCPublishServer;
import org.serviceconnector.api.srv.SCPublishServerCallback;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.integration.api.APIIntegrationSuperServerTest;

public class APIRegisterPublishServerTest extends APIIntegrationSuperServerTest {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(APIRegisterPublishServerTest.class);

	/**
	 * Description: register session server on port SC is not listening<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t101_register() throws Exception {
		server = new SCServer(TestConstants.HOST, 9002, TestConstants.PORT_PUB_SRV_TCP, ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(1, 1, cbk);
	}

	/**
	 * Description: register session server with no service name<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t102_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_TCP, TestConstants.PORT_PUB_SRV_TCP, ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(null);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(1, 1, cbk);
	}

	/**
	 * Description: register session server with service name = ""<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t103_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_TCP, TestConstants.PORT_PUB_SRV_TCP, ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer("");
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(1, 1, cbk);
	}

	/**
	 * Description: register session server with service name = " "<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t104_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_TCP, TestConstants.PORT_PUB_SRV_TCP, ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(" ");
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(1, 1, cbk);
	}

	/**
	 * Description: register session server with service name = "gaga"<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t105_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_TCP, TestConstants.PORT_PUB_SRV_TCP, ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer("gaga");
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(1, 1, cbk);
	}

	/**
	 * Description: register session server with callback = null<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t106_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_TCP, TestConstants.PORT_PUB_SRV_TCP, ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = null;
		publishServer.register(1, 1, cbk);
	}

	/**
	 * Description: register session server with 1 session and 1 connection<br>
	 * Expectation: passes
	 */
	@Test
	public void t107_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_TCP, TestConstants.PORT_PUB_SRV_TCP, ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(1, 1, cbk);
		Assert.assertEquals("SessionServer is not registered", true, publishServer.isRegistered());
	}

	/**
	 * Description: register session server with 10 sessions and 10 connections<br>
	 * Expectation: passes
	 */
	@Test
	public void t108_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_TCP, TestConstants.PORT_PUB_SRV_TCP, ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(10, 10, cbk);
		Assert.assertEquals("SessionServer is not registered", true, publishServer.isRegistered());
	}

	/**
	 * Description: register session server with 1 session and 10 connections<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t109_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_TCP, TestConstants.PORT_PUB_SRV_TCP, ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(1, 10, cbk);
	}

	/**
	 * Description: register session server with 10 session and 20 connections<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t110_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_TCP, TestConstants.PORT_PUB_SRV_TCP, ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(10, 20, cbk);
	}

	/**
	 * Description: register session server with 0 session and 1 connection<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t111_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_TCP, TestConstants.PORT_PUB_SRV_TCP, ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(0, 1, cbk);
	}

	/**
	 * Description: register session server with 1 session and 0 connection<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t112_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_TCP, TestConstants.PORT_PUB_SRV_TCP, ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(1, 0, cbk);
	}

	/**
	 * Description: register session server with 0 session and 0 connection<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t113_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_TCP, TestConstants.PORT_PUB_SRV_TCP, ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(0, 0, cbk);
	}

	/**
	 * Description: register session server with 1 session and 1 connection twice<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t114_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_TCP, TestConstants.PORT_PUB_SRV_TCP, ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(1, 1, cbk);
		publishServer.register(1, 1, cbk);
	}

	/**
	 * Description: register session server before listener is started<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t115_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_TCP, TestConstants.PORT_PUB_SRV_TCP, ConnectionType.NETTY_TCP);
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(1, 1, cbk);
		server.startListener();
	}

	/**
	 * Description: register two session servers to two services with two callbacks<br>
	 * Expectation: passes
	 */
	@Test
	public void t198_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_TCP, TestConstants.PORT_PUB_SRV_TCP, ConnectionType.NETTY_TCP);
		server.startListener();
		SCPublishServer publishServer1 = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk1 = new PubSrvCallback(publishServer1);
		publishServer1.register(1, 1, cbk1);
		Assert.assertEquals("SessionServer is not registered", true, publishServer1.isRegistered());

		SCPublishServer publishServer2 = server.newPublishServer(TestConstants.sesServiceName2);
		SCPublishServerCallback cbk2 = new PubSrvCallback(publishServer2);
		publishServer2.register(1, 1, cbk2);
		Assert.assertEquals("SessionServer is not registered", true, publishServer2.isRegistered());

		publishServer1.deregister();
		publishServer2.deregister();
	}

	/**
	 * Description: register two session servers to two services with same callback<br>
	 * Expectation: passes
	 */
	@Test
	public void t199_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_TCP, TestConstants.PORT_PUB_SRV_TCP, ConnectionType.NETTY_TCP);
		server.startListener();
		SCPublishServer publishServer1 = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer1);
		publishServer1.register(1, 1, cbk);
		Assert.assertEquals("SessionServer is not registered", true, publishServer1.isRegistered());

		SCPublishServer publishServer2 = server.newPublishServer(TestConstants.sesServiceName2);
		publishServer2.register(1, 1, cbk);
		Assert.assertEquals("SessionServer is not registered", true, publishServer2.isRegistered());

		publishServer1.deregister();
		publishServer2.deregister();
	}

	/**
	 * Description: register session server on port SC is not listening<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t201_register() throws Exception {
		server = new SCServer(TestConstants.HOST, 9002, TestConstants.PORT_PUB_SRV_HTTP, ConnectionType.NETTY_HTTP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(1, 1, cbk);
	}

	/**
	 * Description: register session server with no service name<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t202_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_HTTP, TestConstants.PORT_PUB_SRV_HTTP, ConnectionType.NETTY_HTTP);
		server.startListener();
		publishServer = server.newPublishServer(null);
	}

	/**
	 * Description: register session server with service name = ""<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t203_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_HTTP, TestConstants.PORT_PUB_SRV_HTTP, ConnectionType.NETTY_HTTP);
		server.startListener();
		publishServer = server.newPublishServer("");
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(1, 1, cbk);
	}

	/**
	 * Description: register session server with service name = " "<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t204_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_HTTP, TestConstants.PORT_PUB_SRV_HTTP, ConnectionType.NETTY_HTTP);
		server.startListener();
		publishServer = server.newPublishServer(" ");
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(1, 1, cbk);
	}

	/**
	 * Description: register session server with service name = "gaga"<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t205_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_HTTP, TestConstants.PORT_PUB_SRV_HTTP, ConnectionType.NETTY_HTTP);
		server.startListener();
		publishServer = server.newPublishServer("gaga");
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(1, 1, cbk);
	}

	/**
	 * Description: register session server with callback = null<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t206_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_HTTP, TestConstants.PORT_PUB_SRV_HTTP, ConnectionType.NETTY_HTTP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = null;
		publishServer.register(1, 1, cbk);
	}

	/**
	 * Description: register session server with 1 session and 1 connection<br>
	 * Expectation: passes
	 */
	@Test
	public void t207_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_HTTP, TestConstants.PORT_PUB_SRV_HTTP, ConnectionType.NETTY_HTTP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(1, 1, cbk);
		Assert.assertEquals("SessionServer is not registered", true, publishServer.isRegistered());
	}

	/**
	 * Description: register session server with 10 sessions and 10 connections<br>
	 * Expectation: passes
	 */
	@Test
	public void t208_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_HTTP, TestConstants.PORT_PUB_SRV_HTTP, ConnectionType.NETTY_HTTP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(10, 10, cbk);
		Assert.assertEquals("SessionServer is not registered", true, publishServer.isRegistered());
	}

	/**
	 * Description: register session server with 1 session and 10 connections<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t209_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_HTTP, TestConstants.PORT_PUB_SRV_HTTP, ConnectionType.NETTY_HTTP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(1, 10, cbk);
	}

	/**
	 * Description: register session server with 10 session and 20 connections<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t210_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_HTTP, TestConstants.PORT_PUB_SRV_HTTP, ConnectionType.NETTY_HTTP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(10, 20, cbk);
	}

	/**
	 * Description: register session server with 0 session and 1 connection<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t211_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_HTTP, TestConstants.PORT_PUB_SRV_HTTP, ConnectionType.NETTY_HTTP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(0, 1, cbk);
	}

	/**
	 * Description: register session server with 1 session and 0 connection<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t212_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_HTTP, TestConstants.PORT_PUB_SRV_HTTP, ConnectionType.NETTY_HTTP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(1, 0, cbk);
	}

	/**
	 * Description: register session server with 0 session and 0 connection<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t213_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_HTTP, TestConstants.PORT_PUB_SRV_HTTP, ConnectionType.NETTY_HTTP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(0, 0, cbk);
	}

	/**
	 * Description: register session server with 1 session and 1 connection twice<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t214_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_HTTP, TestConstants.PORT_PUB_SRV_HTTP, ConnectionType.NETTY_HTTP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(1, 1, cbk);
		publishServer.register(1, 1, cbk);
	}

	/**
	 * Description: register session server before listener is started<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t215_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_HTTP, TestConstants.PORT_PUB_SRV_HTTP, ConnectionType.NETTY_HTTP);
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(1, 1, cbk);
		server.startListener();
	}

	/**
	 * Description: register two session servers to two services with two callbacks<br>
	 * Expectation: passes
	 */
	@Test
	public void t298_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_HTTP, TestConstants.PORT_PUB_SRV_HTTP, ConnectionType.NETTY_HTTP);
		server.startListener();
		SCPublishServer publishServer1 = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk1 = new PubSrvCallback(publishServer1);
		publishServer1.register(1, 1, cbk1);
		Assert.assertEquals("SessionServer is not registered", true, publishServer1.isRegistered());

		SCPublishServer publishServer2 = server.newPublishServer(TestConstants.pubServiceName2);
		SCPublishServerCallback cbk2 = new PubSrvCallback(publishServer2);
		publishServer2.register(1, 1, cbk2);
		Assert.assertEquals("SessionServer is not registered", true, publishServer2.isRegistered());

		publishServer1.deregister();
		publishServer2.deregister();
	}

	/**
	 * Description: register two session servers to two services with same callback<br>
	 * Expectation: passes
	 */
	@Test
	public void t299_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC0_HTTP, TestConstants.PORT_PUB_SRV_HTTP, ConnectionType.NETTY_HTTP);
		server.startListener();
		SCPublishServer publishServer1 = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer1);
		publishServer1.register(1, 1, cbk);
		Assert.assertEquals("SessionServer is not registered", true, publishServer1.isRegistered());

		SCPublishServer publishServer2 = server.newPublishServer(TestConstants.pubServiceName2);
		publishServer2.register(1, 1, cbk);
		Assert.assertEquals("SessionServer is not registered", true, publishServer2.isRegistered());

		publishServer1.deregister();
		publishServer2.deregister();
	}
}
