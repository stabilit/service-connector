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
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnetor.TestConstants;

public class PrematureDestroyOfServerProcessClientTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PrematureDestroyOfServerProcessClientTest.class);

	private static Process scProcess;
	private Process srvProcess;

	private SCClient client;
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

	@Before
	public void setUp() throws Exception {
		try {
			srvProcess = ctrl.startServer(TestConstants.sessionSrv, TestConstants.log4jSrvProperties,
					TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, new String[] { TestConstants.sessionServiceName,
							TestConstants.publishServiceName });
		} catch (Exception e) {
			logger.error("setUp", e);
		}
		client = new SCClient();
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
	}

	@After
	public void tearDown() throws Exception {
		try {
			client.detach();
		} catch (Exception e) {
		} finally {
			client = null;
			ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
			srvProcess = null;
			ex = null;
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSCProperties);
		ctrl = null;
		scProcess = null;
	}

	@Test
	public void createSession_withoutServer_throwsException() throws Exception {
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		SCSessionService sessionService = client.newSessionService(TestConstants.sessionServiceName);
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
	public void deleteSession_withoutServer_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sessionServiceName);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 5, scMessage);

		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		Thread.sleep(5000); // wait until server has been cleaned up on SC
		try {
			sessionService.deleteSession();
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void execute_withoutServer_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sessionServiceName);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(300, 5, scMessage);

		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		Thread.sleep(5000); // wait until server has been cleaned up on SC
		try {
			sessionService.execute(new SCMessage());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void deleteSession_withoutServerTimeoutTakes5Seconds_passes() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sessionServiceName);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession(5, 5, scMessage);

		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);

		Thread.sleep(5000);
		sessionService.deleteSession();
	}

	@Test(expected = SCServiceException.class)
	public void execute_withoutServer_timeoutTakes5SecondsThrowsException() throws Exception {
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		SCSessionService sessionService = client.newSessionService(TestConstants.sessionServiceName);
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
