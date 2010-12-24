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
package org.serviceconnector.test.system.api.cln;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.test.system.api.APISystemSuperSessionClientTest;

@SuppressWarnings("unused")
public class APICreateDeleteSessionTest extends APISystemSuperSessionClientTest {

	/**
	 * Description: Create session (regular)<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_createSession() throws Exception {
		SCMessage request = new SCMessage();
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(sessionService);
		response = sessionService.createSession(request, cbk);
		Assert.assertNotNull("the session ID is null", sessionService.getSessionId());
		sessionService.deleteSession();
		Assert.assertNull("the session ID is NOT null after deleteSession()", sessionService.getSessionId());
	}

	/**
	 * Description: Create session to publish service<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t02_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.pubServiceName1);
		cbk = new MsgCallback(sessionService);
		response = sessionService.createSession(request, cbk);
	}

	/**
	 * Description: Create session to file service<br>
	 * Expectation: throws SCServiceException (unfortunately this passes because file services uses sessions)
	 * file service accepts create session (sessionService)
	 * TODO JOT/TRN how do we distinguish between session for file services??
	 */
	@Test(expected = SCServiceException.class)
	public void t03_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.filServiceName1);
		cbk = new MsgCallback(sessionService);
		response = sessionService.createSession(request, cbk);
	}

	/**
	 * Description: Create session to service which does not exist<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t04_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService = client.newSessionService("gaga");
		cbk = new MsgCallback(sessionService);
		response = sessionService.createSession(request, cbk);
	}

	/**
	 * Description: Create session to service not served by a server<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t05_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName2);
		cbk = new MsgCallback(sessionService);
		response = sessionService.createSession(request, cbk);
	}

	/**
	 * Description: Create session with operationTimeout = 0<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t06_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService.createSession(0, request, cbk);
	}

	/**
	 * Description: Create session with operationTimeout = -1<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t07_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService.createSession(-1, request, cbk);
	}

	/**
	 * Description: Create session with operationTimeout = 3601<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t08_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService.createSession(3601, request, cbk);
	}

	/**
	 * Description: Create session with 60kB message<br>
	 * Expectation: passes
	 */
	@Test
	public void t09_createSession60kBmsg() throws Exception {
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength60kB]);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(sessionService);
		response = sessionService.createSession(request, cbk);
		Assert.assertNotNull("the session ID is null", sessionService.getSessionId());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create session with large message<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t10_createSession1MBmsg() throws Exception {
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength1MB]);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(sessionService);
		response = sessionService.createSession(request, cbk);
	}

	/**
	 * Description: Create session twice<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t11_createSessionTwice() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(sessionService);
		response = sessionService.createSession(request, cbk);
		Assert.assertNotNull("the session ID is null", sessionService.getSessionId());

		response = sessionService.createSession(request, cbk);
	}

	/**
	 * Description: Create two sessions to the same service<br>
	 * Expectation: passes
	 */
	@Test
	public void t12_createTwoSessions() throws Exception {
		SCMessage request = new SCMessage();
		SCMessage response = null;
		SCSessionService service1 = client.newSessionService(TestConstants.sesServiceName1);
		SCSessionService service2 = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(sessionService);
		response = service1.createSession(request, cbk);
		Assert.assertNotNull("the session ID is null", service1.getSessionId());
		MsgCallback cbk2 = new MsgCallback(service2);
		response = service2.createSession(request, cbk2);
		Assert.assertNotNull("the session ID is null", service2.getSessionId());

		service1.deleteSession();
		Assert.assertNull("the session ID is NOT null after deleteSession()", service1.getSessionId());
		service2.deleteSession();
		Assert.assertNull("the session ID is NOT null after deleteSession()", service2.getSessionId());
	}

	/**
	 * Description: screw up sessionId before create session<br>
	 * Expectation: passes because sessionId is set internally.
	 */
	@Test
	public void t13_sessionId() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		String sessionId = "aaaa0000-bb11-cc22-dd33-eeeeee444444";
		request.setSessionId(sessionId);
		cbk = new MsgCallback(sessionService);
		response = sessionService.createSession(request, cbk);
		Assert.assertEquals("sessionId is the same", false, sessionId == response.getSessionId());
		sessionService.deleteSession();
	}
	
	/**
	 * Description: Create session with service which has been disabled<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t20_disabledService() throws Exception {
		// disable service
		SCMgmtClient clientMgmt = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP);
		clientMgmt.attach();
		clientMgmt.disableService(TestConstants.sesServiceName1);
		clientMgmt.detach();

		SCMessage request = null;
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(sessionService);
		response = sessionService.createSession(request, cbk);
	}

	/**
	 * Description: Create session with echo interval = 1<br>
	 * Expectation: passes
	 */
	@Test
	public void t30_echoInterval() throws Exception {
		SCMessage request = new SCMessage(new byte[128]);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		sessionService.setEchoIntervalInSeconds(1);
		cbk = new MsgCallback(sessionService);
		response = sessionService.createSession(request, cbk);
		Assert.assertNotNull("the session ID is null", sessionService.getSessionId());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create session with echo interval = 0<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t31_echoInterval() throws Exception {
		SCMessage request = new SCMessage(new byte[128]);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		sessionService.setEchoIntervalInSeconds(0);
		cbk = new MsgCallback(sessionService);
		response = sessionService.createSession(request, cbk);
	}

	/**
	 * Description: Reject session by server<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t32_rejectSession() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		request.setSessionInfo(TestConstants.rejectSessionCmd);
		cbk = new MsgCallback(sessionService);
		response = sessionService.createSession(request, cbk);
	}

	/**
	 * Description: Reject session by server, check error code<br>
	 * Expectation: passes, exception catched
	 */
	@Test
	public void t33_rejectSession() throws Exception {
		SCMessage request = new SCMessage();
		SCMessage response = new SCMessage();
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		request.setSessionInfo(TestConstants.rejectSessionCmd);
		Boolean passed = false;
		try {
			cbk = new MsgCallback(sessionService);
			response = sessionService.createSession(request, cbk);
		} catch (SCServiceException e) {
			passed = true;
			Assert.assertNull("the session ID is NOT null", sessionService.getSessionId());
			Assert.assertEquals("is not appErrorCode", TestConstants.appErrorCode, e.getAppErrorCode());
			Assert.assertEquals("is not appErrorText", TestConstants.appErrorText, e.getAppErrorText());
		}
		Assert.assertTrue("did not throw exception", passed);
		sessionService.deleteSession();
	}
	
	/**
	 * Description: Delete session before create session<br>
	 * Expectation: passes
	 */
	@Test
	public void t40_deleteSession() throws Exception {
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		Assert.assertNull("the session ID is NOT null before deleteSession()", sessionService.getSessionId());
		sessionService.deleteSession();
		Assert.assertNull("the session ID is NOT null after deleteSession()", sessionService.getSessionId());
	}

	/**
	 * Description: Delete session on service which has been disabled<br>
	 * Expectation: passes
	 */
	@Test
	public void t41_disabledService() throws Exception {
		SCMessage request = new SCMessage();
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(sessionService);
		response = sessionService.createSession(request, cbk);
		Assert.assertNotNull("the session ID is null", sessionService.getSessionId());

		// disable service
		SCMgmtClient clientMgmt = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP);
		clientMgmt.attach();
		clientMgmt.disableService(TestConstants.sesServiceName1);
		clientMgmt.detach();
		
		// delete session
		sessionService.deleteSession();
		Assert.assertNull("the session ID is NOT null after deleteSession()", sessionService.getSessionId());
	}

	/**
	 * Description: Delete session twice<br>
	 * Expectation: passes
	 */
	@Test
	public void t42_deleteSessionTwice() throws Exception {
		SCMessage request = new SCMessage();
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(sessionService);
		response = sessionService.createSession(request, cbk);
		Assert.assertNotNull("the session ID is null", sessionService.getSessionId());
		sessionService.deleteSession();
		Assert.assertNull("the session ID is NOT null after deleteSession()", sessionService.getSessionId());
		sessionService.deleteSession();
		Assert.assertNull("the session ID is NOT null after deleteSession()", sessionService.getSessionId());
	}

}