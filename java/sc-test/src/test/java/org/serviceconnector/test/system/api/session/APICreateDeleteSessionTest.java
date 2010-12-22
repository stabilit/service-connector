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
package org.serviceconnector.test.system.api.session;

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

	private SCSessionService service;

	@After
	public void afterOneTest() throws Exception {
		try {
			service.deleteSession();
		} catch (Exception e1) {
		}
		service = null;
		super.afterOneTest();
	}

	/**
	 * Description: Create session (regular)<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_createSession() throws Exception {
		SCMessage request = new SCMessage();
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
		Assert.assertNotNull("the session ID is null", service.getSessionId());
		service.deleteSession();
		Assert.assertNull("the session ID is NOT null after deleteSession()", service.getSessionId());
	}

	/**
	 * Description: Create session to publish service<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t02_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService(TestConstants.pubServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
	}

	/**
	 * Description: Create session to file service<br>
	 * Expectation: throws SCServiceException (unfortunatelly this passes because file services uses sessions)
	 * file service accepts create session (sessionService)
	 * TODO JOT/TRN how do we distinguish between session for file services??
	 */
	@Test(expected = SCServiceException.class)
	public void t03_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService(TestConstants.filServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
	}

	/**
	 * Description: Create session to service which does not exist<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t04_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService("gaga");
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
	}

	/**
	 * Description: Create session to service not served by a server<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t05_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName2);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
	}

	/**
	 * Description: Create session with operationTimeout = 0<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t06_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(0, request, cbk);
	}

	/**
	 * Description: Create session with operationTimeout = -1<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t07_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(-1, request, cbk);
	}

	/**
	 * Description: Create session with operationTimeout = 3601<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t08_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(3601, request, cbk);
	}

	/**
	 * Description: Create session with 60kB message<br>
	 * Expectation: passes
	 */
	@Test
	public void t09_createSession60kBmsg() throws Exception {
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength60kB]);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
		Assert.assertNotNull("the session ID is null", service.getSessionId());
		service.deleteSession();
	}

	/**
	 * Description: Create session with large message<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t10_createSession1MBmsg() throws Exception {
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength1MB]);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
	}

	/**
	 * Description: Create session twice<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t11_createSessionTwice() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
		Assert.assertNotNull("the session ID is null", service.getSessionId());

		response = service.createSession(request, cbk);
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
		cbk = new MsgCallback(service);
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
		service = client.newSessionService(TestConstants.sesServiceName1);
		String sessionId = "aaaa0000-bb11-cc22-dd33-eeeeee444444";
		request.setSessionId(sessionId);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
		Assert.assertEquals("sessionId is the same", false, sessionId == response.getSessionId());
		service.deleteSession();
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
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
	}

	/**
	 * Description: Create session with echo interval = 1<br>
	 * Expectation: passes
	 */
	@Test
	public void t30_echoInterval() throws Exception {
		SCMessage request = new SCMessage(new byte[128]);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		service.setEchoIntervalInSeconds(1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
		Assert.assertNotNull("the session ID is null", service.getSessionId());
		service.deleteSession();
	}

	/**
	 * Description: Create session with echo interval = 0<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t31_echoInterval() throws Exception {
		SCMessage request = new SCMessage(new byte[128]);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		service.setEchoIntervalInSeconds(0);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
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
		service = client.newSessionService(TestConstants.sesServiceName1);
		request.setSessionInfo(TestConstants.rejectSessionCmd);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
	}

	/**
	 * Description: Reject session by server, check error code<br>
	 * Expectation: passes, exception catched
	 */
	@Test
	public void t33_rejectSession() throws Exception {
		SCMessage request = new SCMessage();
		SCMessage response = new SCMessage();
		service = client.newSessionService(TestConstants.sesServiceName1);
		request.setSessionInfo(TestConstants.rejectSessionCmd);
		Boolean passed = false;
		try {
			cbk = new MsgCallback(service);
			response = service.createSession(request, cbk);
		} catch (SCServiceException e) {
			passed = true;
			Assert.assertNull("the session ID is NOT null", service.getSessionId());
			Assert.assertEquals("is not appErrorCode", TestConstants.appErrorCode, e.getAppErrorCode());
			Assert.assertEquals("is not appErrorText", TestConstants.appErrorText, e.getAppErrorText());
		}
		Assert.assertTrue("did not throw exception", passed);
		service.deleteSession();
	}
	
	/**
	 * Description: Delete session before create session<br>
	 * Expectation: passes
	 */
	@Test
	public void t40_deleteSession() throws Exception {
		service = client.newSessionService(TestConstants.sesServiceName1);
		Assert.assertNull("the session ID is NOT null before deleteSession()", service.getSessionId());
		service.deleteSession();
		Assert.assertNull("the session ID is NOT null after deleteSession()", service.getSessionId());
	}

	/**
	 * Description: Delete session on service which has been disabled<br>
	 * Expectation: passes
	 */
	@Test
	public void t41_disabledService() throws Exception {
		SCMessage request = new SCMessage();
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
		Assert.assertNotNull("the session ID is null", service.getSessionId());

		// disable service
		SCMgmtClient clientMgmt = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP);
		clientMgmt.attach();
		clientMgmt.disableService(TestConstants.sesServiceName1);
		clientMgmt.detach();
		
		// delete session
		service.deleteSession();
		Assert.assertNull("the session ID is NOT null after deleteSession()", service.getSessionId());
	}

	/**
	 * Description: Delete session twice<br>
	 * Expectation: passes
	 */
	@Test
	public void t42_deleteSessionTwice() throws Exception {
		SCMessage request = new SCMessage();
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
		Assert.assertNotNull("the session ID is null", service.getSessionId());
		service.deleteSession();
		Assert.assertNull("the session ID is NOT null after deleteSession()", service.getSessionId());
		service.deleteSession();
		Assert.assertNull("the session ID is NOT null after deleteSession()", service.getSessionId());
	}

}