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
package org.serviceconnector.test.integration.cln;

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
import org.serviceconnector.service.SCServiceException;


public class EnableDisableServiceTest {
	
	/** The Constant testLogger. */
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(EnableDisableServiceTest.class);

	private static ProcessesController ctrl;
	private static ProcessCtx scCtx;
	private SCMgmtClient client;
	private int threadCount = 0;
	
	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
	}

	@Before
	public void beforeOneTest() throws Exception {
		threadCount = Thread.activeCount();
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			client.detach();
		} catch (Exception e) {}
		client = null;
//		Assert.assertEquals("number of threads", threadCount, Thread.activeCount());
		testLogger.info("Number of threads :" + Thread.activeCount() + " created :"+(Thread.activeCount() - threadCount));
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		try {
			ctrl.stopSC(scCtx);
			scCtx = null;
		} catch (Exception e) {}
		ctrl = null;
		ctrl = null;
	}

		
	/**
	 * Description: check non-existing service<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t01_disable_enable() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP);
		client.attach(2);
		Assert.assertEquals("Enabled ", true, client.isServiceEnabled("notExistingService"));
	}

	/**
	 * Description: enable non-existing service<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t02_disable_enable() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP);
		client.attach(2);
		client.enableService("notExistingService");
	}
	
	/**
	 * Description: disable non-existing service<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t03_disable_enable() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP);
		client.attach(2);
		client.disableService("notExistingService");
	}

	/**
	 * Description: check service without attach<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t04_disable_enable() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP);
		Assert.assertEquals("Enabled ", true, client.isServiceEnabled(TestConstants.sesServiceName1));
	}
	
	/**
	 * Description: check default service<br> 
	 * Expectation:	service is enabled.
	 */
	@Test
	public void t05_default() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP);
		client.attach(2);
		Assert.assertEquals("Enabled ", true, client.isServiceEnabled(TestConstants.sesServiceName1));
	}
	
	/**
	 * Description: disable service without attach<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t06_disable_enable() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP);
		client.disableService(TestConstants.sesServiceName1);
	}

	/**
	 * Description: disable and enable service<br> 
	 * Expectation:	service is enabled.
	 */
	@Test
	public void t07_disable_enable() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP);
		client.attach(2);
		Assert.assertEquals("Enabled ", true, client.isServiceEnabled(TestConstants.sesServiceName1));
		client.disableService(TestConstants.sesServiceName1);
		Assert.assertEquals("Disabled ", false, client.isServiceEnabled(TestConstants.sesServiceName1));
		client.enableService(TestConstants.sesServiceName1);
		Assert.assertEquals("Enabled ", true, client.isServiceEnabled(TestConstants.sesServiceName1));
	}
	
	/**
	 * Description: enable / disable service twice<br> 
	 * Expectation:	stays enabled / disabled
	 */
	@Test
	public void t08_disable_enable() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP);
		client.attach(2);
		Assert.assertEquals("Enabled ", true, client.isServiceEnabled(TestConstants.sesServiceName1));
		client.disableService(TestConstants.sesServiceName1);
		client.disableService(TestConstants.sesServiceName1);
		Assert.assertEquals("Disabled ", false, client.isServiceEnabled(TestConstants.sesServiceName1));
		client.enableService(TestConstants.sesServiceName1);
		client.enableService(TestConstants.sesServiceName1);
		Assert.assertEquals("Enabled ", true, client.isServiceEnabled(TestConstants.sesServiceName1));
	}

	/**
	 * Description: enable / disable service 1000 times<br> 
	 * Expectation:	stays enabled / disabled
	 */
	@Test
	public void t09_disable_enable() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP);
		client.attach(2);
		int nr = 1000;
		for (int i = 0; i < nr; i++) {
			if (((i+1) % 100) == 0) testLogger.info("Enable/disable nr. " + (i+1) + "...");
			client.disableService(TestConstants.sesServiceName1);
			Assert.assertEquals("Disabled ", false, client.isServiceEnabled(TestConstants.sesServiceName1));
			client.enableService(TestConstants.sesServiceName1);
			Assert.assertEquals("Enabled ", true, client.isServiceEnabled(TestConstants.sesServiceName1));
		}
	}
}
