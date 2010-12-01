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
package org.serviceconnector.test.system.session;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.service.SCServiceException;

public class PrematureDestroyOfSCProcessClientTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PrematureDestroyOfSCProcessClientTest.class);

	private Process scProcess;
	private Process srvProcess;

	private SCClient client;

	private Exception ex;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
	}

	@Before
	public void beforeOneTest() throws Exception {
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
			srvProcess = ctrl.startServer(TestConstants.SERVER_TYPE_SESSION, TestConstants.log4jSrvProperties,
					TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, new String[] { TestConstants.sessionServiceNames });
		} catch (Exception e) {
			logger.error("setUp", e);
		}
		client = new SCClient();
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			client.detach();
		} catch (Exception e) {
		} finally {
			ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
			ctrl.stopProcess(scProcess, TestConstants.log4jSCProperties);
			client = null;
			srvProcess = null;
			scProcess = null;
		}
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		ctrl = null;
	}

	@Test
	public void createSession_withoutSC_throwsException() throws Exception {
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		SCSessionService sessionService = client.newSessionService(TestConstants.sessionServiceNames);
		try {
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 5, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(null, sessionService.getSessionId());
	}

	@Test
	public void deleteSession_withoutSC_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sessionServiceNames);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 5, scMessage);
		ctrl.stopProcess(scProcess, TestConstants.log4jSrvProperties);
		try {
			sessionService.deleteSession();
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void execute_withoutSC_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sessionServiceNames);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 5, scMessage);
		ctrl.stopProcess(scProcess, TestConstants.log4jSrvProperties);
		try {
			sessionService.execute(new SCMessage());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void deleteSession_withoutSCTimeoutTakes5Seconds_passes() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sessionServiceNames);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(5, 5, scMessage);
		ctrl.stopProcess(scProcess, TestConstants.log4jSrvProperties);
		Thread.sleep(5000);
		sessionService.deleteSession();
	}

	@Test(expected = SCServiceException.class)
	public void execute_withoutSC_timeoutTakes5SecondsThrowsException() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSrvProperties);
		SCSessionService sessionService = client.newSessionService(TestConstants.sessionServiceNames);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(5, 5, scMessage);
		Thread.sleep(5000);
		try {
			sessionService.execute(new SCMessage());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(null, sessionService.getSessionId());
		throw ex;
	}
}
