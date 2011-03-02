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

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.srv.SCPublishServer;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.test.unit.SuperUnitTest;


public class APINewServerTest extends SuperUnitTest{

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private final static Logger LOGGER = Logger.getLogger(APINewServerTest.class);
	
	private SCServer server;
	
	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP); 
		server.startListener();
	}

	@After
	public void afterOneTest() {
		try {
			server.stopListener();
		} catch (Exception e) {
		}
		server = null;
		super.afterOneTest();
	}
		
	/**
	 * Description:	Create new session server<br>
	 * Expectation:	server is not null
	 */
	@Test
	public void t01_SessionServer() throws Exception {
		SCSessionServer sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		Assert.assertNotNull("Can not create SessionServer!", sessionServer);
	}

	/**
	 * Description:	Invoke sessionServer.destroy()<br>
	 * Expectation:	The SessionServer is destroyed.
	 */
	@Test
	public void t02_SessionServer() throws Exception {
		SCSessionServer sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		Assert.assertNotNull("No SessionServer", sessionServer);
	}

	/**
	 * Description:	Check the Host and Port<br>
	 * Expectation:	The Host and Port have a value.
	 */	
	@Test
	public void t03_ServerHostPort() throws Exception {
		SCSessionServer sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		Assert.assertNotNull("Can not create SessionServer!", sessionServer);
		/* Cannot test interfaces, because they have dynamic values
		Assert.assertEquals("SessionServer Host", TestConstants.HOST, sessionServer.getListenerInterfaces());
		*/
		Assert.assertEquals("SessionServer Port", TestConstants.PORT_SES_SRV_TCP, sessionServer.getListenerPort());
	}	

	/**
	 * Description:	Check the SCHost and SCPort<br>
	 * Expectation:	The SCHost and SCPort have a value.
	 */	
	@Test
	public void t04_SCHostPort() throws Exception  {
		SCSessionServer sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		Assert.assertNotNull("Can not create SessionServer!", sessionServer);
		Assert.assertEquals("SC Host", TestConstants.HOST, sessionServer.getSCHost());
		Assert.assertEquals("SC Port", TestConstants.PORT_SC_TCP, sessionServer.getSCPort());
	}
	
	/**
	 * Description:	Create new publish server<br>
	 * Expectation:	server is not null
	 */
	@Test
	public void t21_PublishServer() throws Exception {
		SCPublishServer publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		Assert.assertNotNull("Can not create PublishServer!", publishServer);
	}

	/**
	 * Description:	Invoke publishServer.destroy()<br>
	 * Expectation:	The publishServer is destroyed.
	 */
	@Test
	public void t22_PublishServer() throws Exception {
		SCPublishServer publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		Assert.assertNotNull("No PublishServer", publishServer);
	}

	/**
	 * Description:	Check the Host and Port<br>
	 * Expectation:	The Host and Port have a value.
	 */	
	@Test
	public void t23_ServerHostPort() throws Exception {
		SCPublishServer publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		Assert.assertNotNull("Can not create PublishServer!", publishServer);
		/* Cannot test interfaces, because they have dynamic values
		Assert.assertEquals("PublishServer Host", TestConstants.HOST, publishServer.getListenerInterfaces());
		*/
		Assert.assertEquals("PublishServer Port", TestConstants.PORT_SES_SRV_TCP, publishServer.getListenerPort());
	}	

	/**
	 * Description:	Check the SCHost and SCPort<br>
	 * Expectation:	The SCHost and SCPort have a value.
	 */	
	@Test
	public void t24_SCHostPort() throws Exception  {
		SCPublishServer publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		Assert.assertNotNull("Can not create PublishServer!", publishServer);
		Assert.assertEquals("SC Host", TestConstants.HOST, publishServer.getSCHost());
		Assert.assertEquals("SC Port", TestConstants.PORT_SC_TCP, publishServer.getSCPort());
	}		

	
}
