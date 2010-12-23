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
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.integration.api.APIIntegrationSuperClientTest;

public class APIAfterSCRestartClientTest extends APIIntegrationSuperClientTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(APIAfterSCRestartClientTest.class);

	private SCMgmtClient client;
	
	/**
	 * Description: attach after SC was restarted<br> 
	 * Expectation:	will pass because this is the first time
	 */
	@Test
	public void t101_attach() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
	}

	/**
	 * Description: attach after detach and SC was restarted<br> 
	 * Expectation:	will pass because the pool was been cleaned up
	 */
	@Test
	public void t102_attach() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		client.detach();
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		
		client.attach();
		Assert.assertEquals("Client is attached", true, client.isAttached());
	}

	/**
	 * Description: client remains attached after SC was restarted<br> 
	 * Expectation:	will pass because the "attached" flag is set locally
	 */
	@Test
	public void t103_attach() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		
		Assert.assertEquals("Client is attached", true, client.isAttached());
	}

	/**
	 * Description: detach after SC was restarted<br> 
	 * Expectation:	passes
	 */
	@Test
	public void t104_detach() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		
		client.detach();
		Assert.assertEquals("Client is attached", false, client.isAttached());
	}
	
	/**
	 * Description: enable service after SC was restarted<br> 
	 * Expectation:	passes, because new connection is created
	 */
	@Test
	public void t105_enableService() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		
		client.enableService(TestConstants.sesServiceName1);
		Assert.assertEquals("Service is not enabled", true, client.isServiceEnabled(TestConstants.sesServiceName1));
	}

	/**
	 * Description: disable service after SC was restarted<br> 
	 * Expectation:	passes, because new connection is created
	 */
	@Test
	public void t106_disableService() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		
		client.disableService(TestConstants.sesServiceName1);
		Assert.assertEquals("Service is not disabled", false, client.isServiceEnabled(TestConstants.sesServiceName1));
	}

	/**
	 * Description: getWorkload after SC was restarted<br> 
	 * Expectation:	passes, because new connection is created
	 */
	@Test
	public void t107_getWorkload() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		
		client.getWorkload(TestConstants.sesServiceName1);
	}

	/**
	 * Description: attach after SC was restarted<br> 
	 * Expectation:	will pass because this is the first time
	 */
	@Test
	public void t201_attach() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		
		client.attach();
		Assert.assertEquals("Client is attached", true, client.isAttached());
	}

	/**
	 * Description: attach after detach and SC was restarted<br> 
	 * Expectation:	will pass because the pool was cleaned up
	 */
	@Test
	public void t202_attach() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());;
		client.detach();
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		
		client.attach();
		Assert.assertEquals("Client is attached", true, client.isAttached());
	}

	/**
	 * Description: client remains attached after SC was restarted<br> 
	 * Expectation:	will pass because the "attached" flag is set locally
	 */
	@Test
	public void t203_attach() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		
		Assert.assertEquals("Client is attached", true, client.isAttached());
	}

	/**
	 * Description: detach after SC was restarted<br> 
	 * Expectation:	passes
	 */
	@Test
	public void t204_detach() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		
		client.detach();
	}
	
	/**
	 * Description: enable service after SC was restarted<br> 
	 * Expectation:	passes, because new connection is created
	 */
	@Test
	public void t205_enableService() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		
		client.enableService(TestConstants.sesServiceName1);
		Assert.assertEquals("Service is not enabled", true, client.isServiceEnabled(TestConstants.sesServiceName1));
	}

	/**
	 * Description: disable service after SC was restarted<br> 
	 * Expectation:	passes, because new connection is created
	 */
	@Test
	public void t206_disableService() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		
		client.disableService(TestConstants.sesServiceName1);
		Assert.assertEquals("Service is not disabled", false, client.isServiceEnabled(TestConstants.sesServiceName1));
	}

	/**
	 * Description: getWorkload after SC was restarted<br> 
	 * Expectation:	passes, because new connection is created
	 */
	@Test
	public void t207_getWorkload() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		
		client.getWorkload(TestConstants.sesServiceName1);

	}
}
