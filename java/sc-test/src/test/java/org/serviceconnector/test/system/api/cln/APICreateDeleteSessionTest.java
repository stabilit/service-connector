/*
 * Copyright © 2010 STABILIT Informatik AG, Switzerland *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License"); *
 * you may not use this file except in compliance with the License. *
 * You may obtain a copy of the License at *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0 *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS, *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and *
 * limitations under the License. *
 */
package org.serviceconnector.test.system.api.cln;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ServerDefinition;
import org.serviceconnector.ctrl.util.ServiceConnectorDefinition;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.test.system.SystemSuperTest;
import org.serviceconnector.test.system.api.APISystemSuperSessionClientTest;

@SuppressWarnings("unused")
@RunWith(Parameterized.class)
public class APICreateDeleteSessionTest extends APISystemSuperSessionClientTest {

	public APICreateDeleteSessionTest(List<ServiceConnectorDefinition> scDefinitions, List<ServerDefinition> srvDefinitions) {
		SystemSuperTest.scDefs = scDefinitions;
		this.srvDefs = srvDefinitions;
	}

	@Parameters
	public static Collection<Object[]> getParameters() {
		List<ServiceConnectorDefinition> sc0Defs = new ArrayList<ServiceConnectorDefinition>();
		ServiceConnectorDefinition sc0Def = new ServiceConnectorDefinition(TestConstants.SC0, TestConstants.SC0Properties,
				TestConstants.log4jSC0Properties);
		sc0Defs.add(sc0Def);

		List<ServiceConnectorDefinition> scCascDefs = new ArrayList<ServiceConnectorDefinition>();
		ServiceConnectorDefinition sc0CascDef = new ServiceConnectorDefinition(TestConstants.SC0_CASC,
				TestConstants.SC0CASCProperties, TestConstants.log4jSC0CASCProperties);
		ServiceConnectorDefinition sc1CascDef = new ServiceConnectorDefinition(TestConstants.SC1_CASC,
				TestConstants.SC1CASCProperties, TestConstants.log4jSC1CASCProperties);
		scCascDefs.add(sc0CascDef);
		scCascDefs.add(sc1CascDef);

		List<ServerDefinition> srvToSC0Defs = new ArrayList<ServerDefinition>();
		ServerDefinition srvToSC0Def = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_SESSION,
				TestConstants.log4jSrvProperties, TestConstants.sesServerName1, TestConstants.PORT_SES_SRV_TCP,
				TestConstants.PORT_SC_TCP, 100, 10, TestConstants.sesServiceName1);
		srvToSC0Defs.add(srvToSC0Def);

		List<ServerDefinition> srvToSC0CascDefs = new ArrayList<ServerDefinition>();
		ServerDefinition srvToSC0CascDef = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_SESSION,
				TestConstants.log4jSrvProperties, TestConstants.sesServerName1, TestConstants.PORT_SES_SRV_TCP,
				TestConstants.PORT_SC0_CASC_TCP, 100, 10, TestConstants.sesServiceName1);
		srvToSC0CascDefs.add(srvToSC0CascDef);

		Collection<Object[]> col = Arrays.asList(new Object[] { sc0Defs, srvToSC0Defs }, //
				new Object[] { scCascDefs, srvToSC0CascDefs });
		// Collection<Object[]> col = Arrays.asList(new Object[] { scCascDefs, srvToSC0CascDefs }, new Object[] {
		// scCascDefs, srvToSC0CascDefs });
		return col;
	}

	/**
	 * Description: Create session (regular)<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_createSession() throws Exception {
		SCMessage request = new SCMessage();
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		Assert.assertNotNull("the session ID is null", sessionService1.getSessionId());
		sessionService1.deleteSession();
		Assert.assertNull("the session ID is NOT null after deleteSession()", sessionService1.getSessionId());
	}

	/**
	 * Description: create session service with service name = "service = gaga"<br>
	 * Expectation: throws SCMPValidatorException (contains "=")
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t05_createSession() throws Exception {
		SCMessage request = new SCMessage();
		SCMessage response = null;
		sessionService1 = client.newSessionService("service = gaga");
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create session to publish service<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t06_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.pubServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create session to file service<br>
	 * Expectation: throws SCServiceException (unfortunately this passes because file services uses sessions) file service accepts
	 * create session (sessionService) TODO JOT/TRN how do we distinguish between session for file services??
	 */
	@Test(expected = SCServiceException.class)
	public void t07_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.filServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create session to service which does not exist<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t08_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService1 = client.newSessionService("gaga");
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create session to service not served by a server<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t09_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName2);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create session with operationTimeout = 0<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t10_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService1.createSession(0, request, msgCallback1);
	}

	/**
	 * Description: Create session with operationTimeout = -1<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t11_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService1.createSession(-1, request, msgCallback1);
	}

	/**
	 * Description: Create session with operationTimeout = 3601<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t12_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService1.createSession(3601, request, msgCallback1);
	}

	/**
	 * Description: Create session with message = new Object<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t13_createSession() throws Exception {
		SCMessage request = new SCMessage();
		request.setData(new Object());
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create session with messageInfo = ""<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t14_createSession() throws Exception {
		SCMessage request = new SCMessage();
		request.setMessageInfo("");
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create session with messageInfo = " "<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t15_createSession() throws Exception {
		SCMessage request = new SCMessage();
		request.setMessageInfo(" ");
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create session with messageInfo = 257chars<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t16_createSession() throws Exception {
		SCMessage request = new SCMessage();
		request.setMessageInfo(TestConstants.stringLength257);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create session with sessionInfo = ""<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t17_createSession() throws Exception {
		SCMessage request = new SCMessage();
		request.setSessionInfo("");
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create session with sessionInfo = " "<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t18_createSession() throws Exception {
		SCMessage request = new SCMessage();
		request.setSessionInfo(" ");
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create session with sessionInfo = 257char<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t19_createSession() throws Exception {
		SCMessage request = new SCMessage();
		request.setSessionInfo(TestConstants.stringLength257);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create session with 60kB message<br>
	 * Expectation: passes
	 */
	@Test
	public void t50_createSession60kBmsg() throws Exception {
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength60kB]);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		Assert.assertNotNull("the session ID is null", sessionService1.getSessionId());
		sessionService1.deleteSession();
	}

	/**
	 * Description: Create session with large message<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t51_createSession1MBmsg() throws Exception {
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength1MB]);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create session twice<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t60_createSessionTwice() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		Assert.assertNotNull("the session ID is null", sessionService1.getSessionId());

		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create two sessions to the same service<br>
	 * Expectation: passes
	 */
	@Test
	public void t70_createTwoSessions() throws Exception {
		SCMessage request = new SCMessage();
		SCMessage response = null;
		SCSessionService service1 = client.newSessionService(TestConstants.sesServiceName1);
		SCSessionService service2 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = service1.createSession(request, msgCallback1);
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
	public void t80_sessionId() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		String sessionId = "aaaa0000-bb11-cc22-dd33-eeeeee444444";
		request.setSessionId(sessionId);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		Assert.assertEquals("sessionId is the same", false, sessionId == response.getSessionId());
		sessionService1.deleteSession();
	}

	/**
	 * Description: Create session with service which has been disabled<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t81_disabledService() throws Exception {
		// disable service
		SCMgmtClient clientMgmt = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP);
		clientMgmt.attach();
		clientMgmt.disableService(TestConstants.sesServiceName1);
		clientMgmt.detach();

		SCMessage request = null;
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create session with echo interval = 1<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t82_echoInterval() throws Exception {
		SCMessage request = new SCMessage(new byte[128]);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		sessionService1.setEchoIntervalInSeconds(1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create session with echo interval = 0<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t83_echoInterval() throws Exception {
		SCMessage request = new SCMessage(new byte[128]);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		sessionService1.setEchoIntervalInSeconds(0);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create session with echo interval = 10<br>
	 * Expectation: throws SCServiceException
	 */
	@Test
	public void t84_echoInterval() throws Exception {
		SCMessage request = new SCMessage(new byte[128]);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		sessionService1.setEchoIntervalInSeconds(10);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		Assert.assertNotNull("the session ID is null", sessionService1.getSessionId());
		sessionService1.deleteSession();
	}

	/**
	 * Description: Reject session by server, check error code<br>
	 * Expectation: passes, exception catched
	 */
	@Test
	public void t85_rejectSession() throws Exception {
		SCMessage request = new SCMessage();
		SCMessage response = new SCMessage();
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		request.setSessionInfo(TestConstants.rejectSessionCmd);
		Boolean passed = false;
		try {
			msgCallback1 = new MsgCallback(sessionService1);
			response = sessionService1.createSession(request, msgCallback1);
		} catch (SCServiceException e) {
			passed = true;
			Assert.assertNull("the session ID is NOT null", sessionService1.getSessionId());
			Assert.assertEquals("is not appErrorCode", TestConstants.appErrorCode, e.getAppErrorCode());
			Assert.assertEquals("is not appErrorText", TestConstants.appErrorText, e.getAppErrorText());
		}
		Assert.assertTrue("did not throw exception", passed);
		sessionService1.deleteSession();
	}

	/**
	 * Description: Reject session by server<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t86_rejectSession() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		request.setSessionInfo(TestConstants.rejectSessionCmd);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Delete session before create session<br>
	 * Expectation: passes
	 */
	@Test
	public void t90_deleteSession() throws Exception {
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		Assert.assertNull("the session ID is NOT null before deleteSession()", sessionService1.getSessionId());
		sessionService1.deleteSession();
		Assert.assertNull("the session ID is NOT null after deleteSession()", sessionService1.getSessionId());
	}

	/**
	 * Description: Delete session on service which has been disabled<br>
	 * Expectation: passes
	 */
	@Test
	public void t91_disabledService() throws Exception {
		SCMessage request = new SCMessage();
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		Assert.assertNotNull("the session ID is null", sessionService1.getSessionId());

		// disable service
		SCMgmtClient clientMgmt = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP);
		clientMgmt.attach();
		clientMgmt.disableService(TestConstants.sesServiceName1);
		clientMgmt.detach();

		// delete session
		sessionService1.deleteSession();
		Assert.assertNull("the session ID is NOT null after deleteSession()", sessionService1.getSessionId());
	}

	/**
	 * Description: Delete session twice<br>
	 * Expectation: passes
	 */
	@Test
	public void t92_deleteSessionTwice() throws Exception {
		SCMessage request = new SCMessage();
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		Assert.assertNotNull("the session ID is null", sessionService1.getSessionId());
		sessionService1.deleteSession();
		Assert.assertNull("the session ID is NOT null after deleteSession()", sessionService1.getSessionId());
		sessionService1.deleteSession();
		Assert.assertNull("the session ID is NOT null after deleteSession()", sessionService1.getSessionId());
	}

}