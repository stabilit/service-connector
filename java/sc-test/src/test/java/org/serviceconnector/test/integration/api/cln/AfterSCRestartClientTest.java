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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;

public class AfterSCRestartClientTest {

	/** The Constant testLogger. */
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(AfterSCRestartClientTest.class);

	private static ProcessesController ctrl;
	private static ProcessCtx scCtx;
	private SCMgmtClient client;
	private int threadCount = 0;

	
	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
	}

	@Before
	public void beforeOneTest() throws Exception {
		threadCount = Thread.activeCount();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			client.detach();
		} catch (Exception e) {}
		client = null;
		try {
			ctrl.stopSC(scCtx);
		} catch (Exception e) {}
		scCtx = null;
//		Assert.assertEquals("number of threads", threadCount, Thread.activeCount());
		testLogger.info("Number of threads :" + Thread.activeCount() + " created :"+(Thread.activeCount() - threadCount));
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		ctrl = null;
	}


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
		Assert.assertEquals("Client is attached", true, client.isAttached());
	}

	/**
	 * Description: attach after detach and SC was restarted<br> 
	 * Expectation:	will pass because the pool was been cleaned up
	 */
	@Test
	public void t102_attach() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Client is attached", true, client.isAttached());
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
		Assert.assertEquals("Client is attached", true, client.isAttached());
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		Assert.assertEquals("Client is attached", true, client.isAttached());
	}

	/**
	 * Description: detach after SC was restarted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t104_detach() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		client.detach();
	}
	
	/**
	 * Description: enable service after SC was restarted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t105_enableService() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		client.enableService(TestConstants.sesServiceName1);
	}

	/**
	 * Description: disable service after SC was restarted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t106_disableService() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		client.disableService(TestConstants.sesServiceName1);
	}

	/**
	 * Description: getWorkload after SC was restarted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t107_getWorkload() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
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
		Assert.assertEquals("Client is attached", true, client.isAttached());
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
		Assert.assertEquals("Client is attached", true, client.isAttached());
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		Assert.assertEquals("Client is attached", true, client.isAttached());
	}

	/**
	 * Description: detach after SC was restarted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t204_detach() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		client.detach();
	}
	
	/**
	 * Description: enable service after SC was restarted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t205_enableService() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		client.enableService(TestConstants.sesServiceName1);
	}

	/**
	 * Description: disable service after SC was restarted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t206_disableService() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		client.disableService(TestConstants.sesServiceName1);
	}

	/**
	 * Description: getWorkload after SC was restarted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t207_getWorkload() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		client.getWorkload(TestConstants.sesServiceName1);
	}
}
