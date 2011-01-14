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

import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.test.system.api.APISystemSuperSessionClientTest;

@SuppressWarnings("unused")
public class APIAfterAbortOrRestartSessionTest extends APISystemSuperSessionClientTest {

	/**
	 * Description: create session after SC was aborted<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t01_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);

		ctrl.stopServer(sesSrvCtx); // stop test server now, it cannot be stopped without SC later
		ctrl.stopSC(scCtx);
		
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: exchange message after SC was aborted<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t02_execute() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);

		ctrl.stopServer(sesSrvCtx); // stop test server now, it cannot be stopped without SC later
		ctrl.stopSC(scCtx);

		request.setMessageInfo(TestConstants.echoCmd);
		response = sessionService1.execute(request);
	}

	/**
	 * Description: send message after SC was aborted<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t03_send() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.echoCmd);
		messageReceived = false;
		MsgCallback cbk = new MsgCallback(sessionService1);

		ctrl.stopServer(sesSrvCtx); 	// stop test server now, it cannot be stopped without SC later
		ctrl.stopSC(scCtx);

		sessionService1.send(request);
	}

	/**
	 * Description: delete session after SC was aborted<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t04_deleteSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);

		ctrl.stopServer(sesSrvCtx); // stop test server now, it cannot be stopped without SC later
		ctrl.stopSC(scCtx);

		sessionService1.deleteSession();
	}

	/**
	 * Description: create session after server was aborted<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t30_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);

		ctrl.stopServer(sesSrvCtx);
		
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
	}

	/**
	 * Description: create session after server was aborted, catch the error and delete the session<br>
	 * Expectation: passes
	 */
	@Test
	public void t31_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);

		ctrl.stopServer(sesSrvCtx);
		
		msgCallback1 = new MsgCallback(sessionService1);
		Boolean passed = false;
		try {
			response = sessionService1.createSession(request, msgCallback1);
		} catch (Exception e) {
			passed = true;
		}
		Assert.assertTrue("did not throw exception", passed);
		sessionService1.deleteSession();
	}

	
	/**
	 * Description: exchange message after server was aborted<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t32_execute() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);

		ctrl.stopServer(sesSrvCtx);

		request.setMessageInfo(TestConstants.echoCmd);
		response = sessionService1.execute(request);
	}

	/**
	 * Description: exchange message after server was aborted with operation timeout = 30<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t33_execute() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);

		ctrl.stopServer(sesSrvCtx);

		request.setMessageInfo(TestConstants.echoCmd);
		response = sessionService1.execute(30, request);
	}

	/**
	 * Description: send message after server was aborted <br>
	 * Expectation: passes because exception is given to callback and handled there
	 */
	@Test
	public void t34_send() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.echoCmd);
		messageReceived = false;
		MsgCallback cbk = new MsgCallback(sessionService1);

		ctrl.stopServer(sesSrvCtx);

		sessionService1.send(request);
		cbk.waitForMessage(10); // will wait max 10 seconds for response
		response = cbk.getResponse();
		Assert.assertEquals("response is not null", null, response); //is null because exception was received 
	}

	/**
	 * Description: delete session after server was aborted<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t35_deleteSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);

		ctrl.stopServer(sesSrvCtx);

		sessionService1.deleteSession();
	}

	/**
	 * Description: exchange one message after server has been restarted<br>
	 * Expectation: throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t50_execute() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.echoAppErrorCmd);

		ctrl.stopServer(sesSrvCtx);
		sesSrvCtx = ctrl.startServer(TestConstants.COMMUNICATOR_TYPE_SESSION, TestConstants.log4jSrvProperties,
				TestConstants.sesServerName1, TestConstants.PORT_SES_SRV_TCP, TestConstants.PORT_SC_TCP, 100, 10,
				TestConstants.sesServiceName1);

		response = sessionService1.execute(request);
	}
	
	/**
	 * Description: delete session after server has been restarted<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t51_deleteSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);

		ctrl.stopServer(sesSrvCtx);
		sesSrvCtx = ctrl.startServer(TestConstants.COMMUNICATOR_TYPE_SESSION, TestConstants.log4jSrvProperties,
				TestConstants.sesServerName1, TestConstants.PORT_SES_SRV_TCP, TestConstants.PORT_SC_TCP, 100, 10,
				TestConstants.sesServiceName1);
		
		sessionService1.deleteSession();
	}


	
}
