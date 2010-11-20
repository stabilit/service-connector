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

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnetor.TestConstants;


public class RestartOfSCProcessClientTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger
			.getLogger(RestartOfSCProcessClientTest.class);

	private SCMgmtClient client;
	private Process scProcess;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
	}

	@Before
	public void setUp() throws Exception {
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
		client = new SCMgmtClient();
	}

	@After
	public void tearDown() throws Exception {
		ctrl.stopSC(scProcess, TestConstants.log4jSCProperties);
		client = null;
		scProcess = null;
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl = null;
	}

	@Test(expected = SCServiceException.class)
	public void attach_againAfterSCRestart_throwsException() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);

		// restart SC
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSCProperties, TestConstants.SCProperties);
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
	}

	@Test(expected = SCServiceException.class)
	public void detach_afterSCRestart_throwsException() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSCProperties, TestConstants.SCProperties);
		client.detach();
	}

	@Test(expected = SCServiceException.class)
	public void enableService_afterSCRestart_throwsException() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSCProperties, TestConstants.SCProperties);
		client.enableService(TestConstants.sessionServiceName);
	}

	@Test(expected = SCServiceException.class)
	public void disableService_afterSCRestart_throwsException() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSCProperties, TestConstants.SCProperties);
		client.enableService(TestConstants.sessionServiceName);
	}

	@Test(expected = SCServiceException.class)
	public void workload_afterSCRestart_throwsException() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSCProperties, TestConstants.SCProperties);
		client.getWorkload(TestConstants.sessionServiceName);
	}

	@Test
	public void setMaxConnection_afterAttachAfterSCRestart_passes() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSCProperties, TestConstants.SCProperties);
		client.setMaxConnections(10);
		assertEquals(10, client.getMaxConnections());
	}
	
	@Test
	public void isAttached_afterAttachAfterSCRestart_true() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		assertEquals(true, client.isAttached());
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSCProperties, TestConstants.SCProperties);
		assertEquals(true, client.isAttached());
	}
	
	@Test
	public void attach_afterAttachAndSCRestartAndDetach_attached() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		assertEquals(true, client.isAttached());
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSCProperties, TestConstants.SCProperties);
		try {
			client.detach();
		} catch (SCServiceException e) {
		}
		assertEquals(false, client.isAttached());
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		assertEquals(true, client.isAttached());
	}
	
	@Test
	public void isServiceDisabled_afterDisablingItBeforeSCRestart_enabled() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		assertEquals(true, client.isServiceEnabled(TestConstants.sessionServiceName));
		client.disableService(TestConstants.sessionServiceName);
		assertEquals(false, client.isServiceEnabled(TestConstants.sessionServiceName));
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSCProperties, TestConstants.SCProperties);
		try {
			client.detach();
		} catch (SCServiceException e) {
		}
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		assertEquals(true, client.isServiceEnabled(TestConstants.sessionServiceName));
	}
}
