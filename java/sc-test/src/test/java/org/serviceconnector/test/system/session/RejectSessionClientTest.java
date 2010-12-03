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
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;

public class RejectSessionClientTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(RejectSessionClientTest.class);
	private static ProcessesController ctrl;

	private static ProcessCtx scCtx;
	private static ProcessCtx srvCtx;

	private SCMgmtClient client;
	Exception ex;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		srvCtx = ctrl.startServer(TestConstants.SERVER_TYPE_SESSION, TestConstants.log4jSrvProperties,
				TestConstants.sesServerName1, TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, 10,
				TestConstants.pubServiceName1 );
	}

	@Before
	public void beforeOneTest() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_HTTP);
		client.attach();
		assertEquals("available/allocated sessions", "1000/0", client.getWorkload(TestConstants.sesServiceName1));
		ex = null;
	}

	@After
	public void afterOneTest() throws Exception {
		assertEquals("available/allocated sessions", "1000/0", client.getWorkload(TestConstants.sesServiceName1));
		client.detach();
		client = null;
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		try {
			ctrl.stopServer(srvCtx);
		} catch (Exception e) {	}
		try {
			ctrl.stopSC(scCtx);
		} catch (Exception e) {	}
		srvCtx = null;
		scCtx = null;
		ctrl = null;
	}

	@Test
	public void createSession_rejectTheSession_sessionIdIsNotSetThrowsExceptionWithAppErrorCodeAndText()
			throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);

		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());

		try {
			// message "reject" translates on the server to reject the session
			SCMessage scMessage = new SCMessage("reject");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession( 10, scMessage);
		} catch (SCServiceException e) {
			ex = e;
		}

		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		// TODO FJU appErrorCode & appErrorText should be maybe in the exception message rather than attributes of
		// exception
		assertEquals(0, Integer.parseInt(((SCServiceException) ex).getAppErrorCode()));
		assertEquals("\"This is the app error text\"", ((SCServiceException) ex).getAppErrorText());
	}

	@Test
	public void createSession_rejectTheSessionAndTryToDeleteSession_sessionIdIsNotSetPasses() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);

		try {
			SCMessage scMessage = new SCMessage("reject");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession( 10, scMessage);
		} catch (Exception e) {
		}
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	// TODO FJU throws NullPointerException
	@Test(expected = SCServiceException.class)
	public void createSession_rejectTheSessionAndTryToExecuteAMessage_sessionIdIsNotSetExecuteThrowsException()
			throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);

		try {
			SCMessage scMessage = new SCMessage("reject");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession( 10, scMessage);
		} catch (Exception e) {
			assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		}

		sessionService.execute(new SCMessage());
	}

	@Test
	public void createSession_rejectTheSessionThenCreateValidSessionThenExecuteAMessage_passes() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);

		try {
			SCMessage scMessage = new SCMessage("reject");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession( 10, scMessage);
		} catch (Exception e) {
			assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		}
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession( 10, scMessage);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		SCMessage response = sessionService.execute(new SCMessage());
		assertEquals(sessionService.getSessionId(), response.getSessionId());
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_TcpRejectTheSession_sessionIdIsNotSetThrowsExceptionWithAppErrorCodeAndText()
			throws Exception {
		client.detach();
		client = null;
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);

		try {
			// message "reject" translates on the server to reject the session
			SCMessage scMessage = new SCMessage("reject");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession( 10, scMessage);
		} catch (SCServiceException e) {
			ex = e;
		}

		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		assertEquals(0, Integer.parseInt(((SCServiceException) ex).getAppErrorCode()));
		assertEquals("\"This is the app error text\"", ((SCServiceException) ex).getAppErrorText());
	}

	@Test
	public void createSession_TcpRejectTheSessionAndTryToDeleteSession_sessionIdIsNotSetPasses() throws Exception {
		client.detach();
		client = null;
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);

		try {
			SCMessage scMessage = new SCMessage("reject");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession( 10, scMessage);
		} catch (Exception e) {
		}
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test(expected = SCServiceException.class)
	public void createSession_TcpRejectTheSessionAndTryToExecuteAMessage_sessionIdIsNotSetThrowsException()
			throws Exception {
		client.detach();
		client = null;
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);

		try {
			SCMessage scMessage = new SCMessage("reject");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession( 10, scMessage);
		} catch (Exception e) {
			assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		}

		sessionService.execute(new SCMessage());
	}

	@Test
	public void createSession_TcpRejectTheSessionThenCreateValidSessionThenExecuteAMessage_passes() throws Exception {
		client.detach();
		client = null;
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();

		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);

		try {
			SCMessage scMessage = new SCMessage("reject");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession( 10, scMessage);
		} catch (Exception e) {
			assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		}
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession( 10, scMessage);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		SCMessage response = sessionService.execute(new SCMessage());
		assertEquals(sessionService.getSessionId(), response.getSessionId());
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}
}
