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

import junit.framework.Assert;

import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.test.system.api.APISystemSuperSessionClientTest;
import org.serviceconnector.test.system.api.cln.casc1.APICreateDeleteSessionCasc1Test;

@SuppressWarnings("unused")
public class APICreateDeleteSessionTest extends APICreateDeleteSessionCasc1Test {

	public APICreateDeleteSessionTest() {
		APISystemSuperSessionClientTest.setUpServiceConnectorAndServer();
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
	 * Description: Create session to service which does not exist<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t08_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService1 = client.newSessionService("gaga");
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create session with operationTimeout = 0<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t10_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService1.createSession(0, request, msgCallback1);
	}

	/**
	 * Description: Create session with operationTimeout = -1<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t11_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService1.createSession(-1, request, msgCallback1);
	}

	/**
	 * Description: Create session with operationTimeout = 3601<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t12_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService1.createSession(3601, request, msgCallback1);
	}

	/**
	 * Description: Create session with message = new Object<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t13_createSession() throws Exception {
		SCMessage request = new SCMessage();
		request.setData(new Object());
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create session with messageInfo = ""<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t14_createSession() throws Exception {
		SCMessage request = new SCMessage();
		request.setMessageInfo("");
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create session with messageInfo = " "<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t15_createSession() throws Exception {
		SCMessage request = new SCMessage();
		request.setMessageInfo(" ");
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create session with messageInfo = 257chars<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t16_createSession() throws Exception {
		SCMessage request = new SCMessage();
		request.setMessageInfo(TestConstants.stringLength257);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create session with sessionInfo = ""<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t17_createSession() throws Exception {
		SCMessage request = new SCMessage();
		request.setSessionInfo("");
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create session with sessionInfo = " "<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t18_createSession() throws Exception {
		SCMessage request = new SCMessage();
		request.setSessionInfo(" ");
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: Create session with sessionInfo = 257char<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t19_createSession() throws Exception {
		SCMessage request = new SCMessage();
		request.setSessionInfo(TestConstants.stringLength257);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
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
		sessionService1.setEchoIntervalSeconds(1);
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
		sessionService1.setEchoIntervalSeconds(0);
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
		sessionService1.setEchoIntervalSeconds(10);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		Assert.assertNotNull("the session ID is null", sessionService1.getSessionId());
		sessionService1.deleteSession();
	}

}