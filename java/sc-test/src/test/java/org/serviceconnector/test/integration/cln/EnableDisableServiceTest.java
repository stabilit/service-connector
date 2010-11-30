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
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;


public class EnableDisableServiceTest {
	
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());
	
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(EnableDisableServiceTest.class);

	private static Process scProcess;

	private SCMgmtClient client;

	private Exception ex;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopSC(scProcess, TestConstants.log4jSCProperties);
		ctrl = null;
		scProcess = null;
	}

	@Before
	public void setUp() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_HTTP);
		client.attach();
	}
	
	@After
	public void tearDown() throws Exception {
		client.detach();
		client = null;
		ex = null;
	}
	
	@Test
	public void isEnabled_enabledService_isEnabled() throws SCServiceException {
		assertEquals(true, client.isServiceEnabled(TestConstants.sessionServiceName));
	}
	
	@Test
	public void isEnabled_disabledService_isNotEnabled() throws SCServiceException {
		assertEquals(false, client.isServiceEnabled(TestConstants.sessionServiceName));
	}
	
	@Test(expected = SCServiceException.class)
	public void isEnabled_notExistingService_throwsException() throws SCServiceException {
		client.isServiceEnabled("notExistingService");
	}

	@Test
	public void enableService_withoutAttach_throwsException() throws Exception {
		client.detach();
		try {
			client.enableService(TestConstants.sessionServiceName);
		} catch (SCServiceException e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void disableService_withoutAttach_throwsException() throws Exception {
		client.detach();
		try {
			client.disableService(TestConstants.sessionServiceName);
		} catch (SCServiceException e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void enableService_AlreadyEnabled_staysEnabled()
			throws Exception {
		assertEquals(true, client.isServiceEnabled(TestConstants.sessionServiceName));
		client.enableService(TestConstants.sessionServiceName);
		assertEquals(true, client.isServiceEnabled(TestConstants.sessionServiceName));
	}
	
	@Test
	public void enableService_disabledService_fromDisabledToEnabled()
			throws Exception {
		assertEquals(false, client.isServiceEnabled(TestConstants.sessionServiceName));
		client.enableService(TestConstants.sessionServiceName);
		assertEquals(true, client.isServiceEnabled(TestConstants.sessionServiceName));
		client.disableService(TestConstants.sessionServiceName);
	}
	
	@Test
	public void disableService_disabledService_passes() throws Exception {
		assertEquals(false, client.isServiceEnabled(TestConstants.sessionServiceName));
		client.enableService(TestConstants.sessionServiceName);
		assertEquals(true, client.isServiceEnabled(TestConstants.sessionServiceName));
		client.disableService(TestConstants.sessionServiceName);
	}
	
	@Test
	public void disableService_AlreadyEnabled_disabled() throws Exception {
		assertEquals(true, client.isServiceEnabled(TestConstants.sessionServiceName));
		client.disableService(TestConstants.sessionServiceName);
		assertEquals(false, client.isServiceEnabled(TestConstants.sessionServiceName));
		client.enableService(TestConstants.sessionServiceName);
	}
	
	@Test(expected = SCServiceException.class)
	public void isServiceEnabled_notExistingService_throwsSCException() throws Exception {
		client.isServiceEnabled("notExistingService");		
	}

	@Test(expected = SCServiceException.class)
	public void enableService_notExistingService_throwsSCException() throws Exception {
		client.enableService("notExistingService");
	}

	@Test(expected = SCServiceException.class)
	public void disableService_notExistingService_throwsSCException() throws Exception {
		client.disableService("notExistingService");
	}
	
	@Test
	public void enableDisableService_anotherExistingService_switchesStates() throws Exception {
		assertEquals(true, client.isServiceEnabled(TestConstants.publishServiceName));
		client.disableService(TestConstants.publishServiceName);
		assertEquals(false, client.isServiceEnabled(TestConstants.publishServiceName));
		client.enableService(TestConstants.publishServiceName);
		assertEquals(true, client.isServiceEnabled(TestConstants.publishServiceName));
	}
	
	@Test
	public void enableDisableService_1000Times_switchesStates() throws Exception {
		assertEquals(true, client.isServiceEnabled(TestConstants.sessionServiceName));
		for (int i = 0; i < 1000; i++) {
			if ((i % 500) == 0) testLogger.info("Enabling/disabling cycle nr. " + i + "...");
			client.disableService(TestConstants.sessionServiceName);
			assertEquals(false, client.isServiceEnabled(TestConstants.sessionServiceName));
			client.enableService(TestConstants.sessionServiceName);
			assertEquals(true, client.isServiceEnabled(TestConstants.sessionServiceName));
		}
	}
	
	@Test
	public void enableDisableService_twoClients_seeChangesOfTheOther() throws Exception {
		SCMgmtClient client2 = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_HTTP);
		client2.attach();
		assertEquals(true, client.isServiceEnabled(TestConstants.sessionServiceName));
		assertEquals(true, client2.isServiceEnabled(TestConstants.sessionServiceName));
		client.disableService(TestConstants.sessionServiceName);
		assertEquals(false, client.isServiceEnabled(TestConstants.sessionServiceName));
		assertEquals(false, client2.isServiceEnabled(TestConstants.sessionServiceName));
		client2.enableService(TestConstants.sessionServiceName);
		assertEquals(true, client.isServiceEnabled(TestConstants.sessionServiceName));
		assertEquals(true, client2.isServiceEnabled(TestConstants.sessionServiceName));
		client2.detach();
	}
	
	@Test
	public void enableDisableService_twoClientsDifferentConnectionTypes_seeChangesOfTheOther() throws Exception {
		SCMgmtClient client2 = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client2.attach();
		assertEquals(true, client.isServiceEnabled(TestConstants.sessionServiceName));
		assertEquals(true, client2.isServiceEnabled(TestConstants.sessionServiceName));
		client.disableService(TestConstants.sessionServiceName);
		assertEquals(false, client.isServiceEnabled(TestConstants.sessionServiceName));
		assertEquals(false, client2.isServiceEnabled(TestConstants.sessionServiceName));
		client2.enableService(TestConstants.sessionServiceName);
		assertEquals(true, client.isServiceEnabled(TestConstants.sessionServiceName));
		assertEquals(true, client2.isServiceEnabled(TestConstants.sessionServiceName));
		client2.detach();
	}
}
