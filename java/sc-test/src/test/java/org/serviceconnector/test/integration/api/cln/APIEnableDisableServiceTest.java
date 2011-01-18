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

import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.test.integration.api.APIIntegrationSuperClientTest;

public class APIEnableDisableServiceTest extends APIIntegrationSuperClientTest {

	private SCMgmtClient client;
			
	/**
	 * Description: check non-existing service<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t01_disable_enable() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Enabled ", true, client.isServiceEnabled("notExistingService"));
	}

	/**
	 * Description: enable non-existing service<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t02_disable_enable() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		client.enableService("notExistingService");
	}
	
	/**
	 * Description: disable non-existing service<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t03_disable_enable() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		client.disableService("notExistingService");
	}

	/**
	 * Description: check service without attach<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t04_disable_enable() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		Assert.assertEquals("Enabled ", true, client.isServiceEnabled(TestConstants.sesServiceName1));
	}
	
	/**
	 * Description: check default service<br> 
	 * Expectation:	service is enabled.
	 */
	@Test
	public void t05_default() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Enabled ", true, client.isServiceEnabled(TestConstants.sesServiceName1));
		client.detach();
	}
	
	/**
	 * Description: disable service without attach<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t06_disable_enable() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		client.disableService(TestConstants.sesServiceName1);
	}

	/**
	 * Description: disable and enable service<br> 
	 * Expectation:	service is enabled.
	 */
	@Test
	public void t07_disable_enable() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Enabled ", true, client.isServiceEnabled(TestConstants.sesServiceName1));
		client.disableService(TestConstants.sesServiceName1);
		Assert.assertEquals("Disabled ", false, client.isServiceEnabled(TestConstants.sesServiceName1));
		client.enableService(TestConstants.sesServiceName1);
		Assert.assertEquals("Enabled ", true, client.isServiceEnabled(TestConstants.sesServiceName1));
		client.detach();
	}
	
	/**
	 * Description: enable / disable service twice<br> 
	 * Expectation:	stays enabled / disabled
	 */
	@Test
	public void t08_disable_enable() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Enabled ", true, client.isServiceEnabled(TestConstants.sesServiceName1));
		client.disableService(TestConstants.sesServiceName1);
		client.disableService(TestConstants.sesServiceName1);
		Assert.assertEquals("Disabled ", false, client.isServiceEnabled(TestConstants.sesServiceName1));
		client.enableService(TestConstants.sesServiceName1);
		client.enableService(TestConstants.sesServiceName1);
		Assert.assertEquals("Enabled ", true, client.isServiceEnabled(TestConstants.sesServiceName1));
		client.detach();
	}

	/**
	 * Description: enable / disable service 1000 times<br> 
	 * Expectation:	stays enabled / disabled
	 */
	@Test
	public void t09_disable_enable() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		int nr = 1000;
		for (int i = 0; i < nr; i++) {
			if (((i+1) % 100) == 0) testLogger.info("Enable/disable nr. " + (i+1) + "...");
			client.disableService(TestConstants.sesServiceName1);
			Assert.assertEquals("Disabled ", false, client.isServiceEnabled(TestConstants.sesServiceName1));
			client.enableService(TestConstants.sesServiceName1);
			Assert.assertEquals("Enabled ", true, client.isServiceEnabled(TestConstants.sesServiceName1));
		}
		client.detach();
	}
}
