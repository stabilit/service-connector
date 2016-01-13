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
package org.serviceconnector.test.integration.api.cln;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.integration.api.APIIntegrationSuperClientTest;

public class APIAfterSCAbortClientTest extends APIIntegrationSuperClientTest  {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(APIAfterSCAbortClientTest.class);

	private SCMgmtClient client;
	
	/**
	 * Description: attach after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t101_attachAfterAbort() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC0_TCP, ConnectionType.NETTY_TCP);
		
		ctrl.stopSC(scCtx);
		
		client.attach();
	}

	/**
	 * Description: detach after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t102_detachAfterAbort() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC0_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		
		client.detach();
	}

	/**
	 * Description: enable service after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t103_enableServiceAfterAbort() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC0_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		
		client.enableService(TestConstants.sesServiceName1);
	}

	/**
	 * Description: disable service after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t104_disableServiceAfterAbort() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC0_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		
		client.disableService(TestConstants.sesServiceName1);
	}

	/**
	 * Description: getWorkload after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t105_getWorkloadAfterAbort() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC0_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		
		client.getWorkload(TestConstants.sesServiceName1);
	}

	/**
	 * Description: attach after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t201_attachAfterAbort() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC0_HTTP, ConnectionType.NETTY_HTTP);
		
		ctrl.stopSC(scCtx);
		
		client.attach();
	}

	/**
	 * Description: detach after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t202_detachAfterAbort() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC0_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		
		client.detach();
	}

	/**
	 * Description: enable service after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t203_enableServiceAfterAbort() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC0_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		
		client.enableService(TestConstants.sesServiceName1);
	}

	/**
	 * Description: disable service after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t204_disableServiceAfterAbort() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC0_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		
		client.disableService(TestConstants.sesServiceName1);
	}

	/**
	 * Description: getWorkload after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t205_getWorkloadAfterAbort() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC0_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		
		client.getWorkload(TestConstants.sesServiceName1);
	}

}
