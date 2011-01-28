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
package org.serviceconnector.test.integration.api.srv;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.srv.SCPublishServerCallback;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.api.srv.SCSessionServerCallback;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.integration.api.APIIntegrationSuperServerTest;

public class APICheckRegistrationTest extends APIIntegrationSuperServerTest {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(APICheckRegistrationTest.class);

	/**
	 * Description:	check session server registration<br>
	 * Expectation:	passes
	 */
	@Test
	public void t01_checkSessionServer() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 1, cbk);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer.isRegistered());
		sessionServer.checkRegistration();
		Assert.assertEquals("SessionServer is not registered", true, sessionServer.isRegistered());
	}

	/**
	 * Description:	check publish server registration<br>
	 * Expectation:	passes
	 */
	@Test
	public void t02_checkPublishServer() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();

		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(1, 1, cbk);
		Assert.assertEquals("PublishServer is not registered", true, publishServer.isRegistered());
		publishServer.checkRegistration();
		Assert.assertEquals("PublishServer is not registered", true, publishServer.isRegistered());
	}

	
}
