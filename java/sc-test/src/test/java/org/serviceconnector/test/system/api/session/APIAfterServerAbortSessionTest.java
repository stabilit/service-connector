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

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.test.system.APISystemSuperSessionClientTest;

@SuppressWarnings("unused")
public class APIAfterServerAbortSessionTest extends APISystemSuperSessionClientTest {
	
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
	 * Description: create session after server was aborted<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t01_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);

		ctrl.stopServer(srvCtx);
		
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
	}

	/**
	 * Description: create session after server was aborted, catch the error and delete the session<br>
	 * Expectation: passes
	 */
	@Test
	public void t02_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);

		ctrl.stopServer(srvCtx);
		
		cbk = new MsgCallback(service);
		Boolean passed = false;
		try {
			response = service.createSession(request, cbk);
		} catch (Exception e) {
			passed = true;
		}
		Assert.assertTrue("did not throw exception", passed);
		service.deleteSession();
	}

	
	/**
	 * Description: exchange message after server was aborted<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t10_execute() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);

		ctrl.stopServer(srvCtx);

		request.setMessageInfo(TestConstants.echoCmd);
		response = service.execute(request);
	}

	/**
	 * Description: exchange message after server was aborted with operation timeout = 30<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t11_execute() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);

		ctrl.stopServer(srvCtx);

		request.setMessageInfo(TestConstants.echoCmd);
		response = service.execute(30, request);
	}

	/**
	 * Description: send message after server was aborted <br>
	 * Expectation: passes because exception is given to callback and handled there
	 */
	@Test
	public void t12_send() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
		request.setMessageInfo(TestConstants.echoCmd);
		messageReceived = false;
		MsgCallback cbk = new MsgCallback(service);

		ctrl.stopServer(srvCtx);

		service.send(request);
		cbk.waitForMessage(10); // will wait max 10 seconds for response
		response = cbk.getResponse();
		Assert.assertEquals("response is not null", null, response); //is null because exception was received 
	}

	/**
	 * Description: delete session after server was aborted<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t20_deleteSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);

		ctrl.stopServer(srvCtx);

		service.deleteSession();
	}

}
