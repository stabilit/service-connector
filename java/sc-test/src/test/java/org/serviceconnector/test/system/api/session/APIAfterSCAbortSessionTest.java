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
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.test.system.api.APISystemSuperSessionClientTest;

@SuppressWarnings("unused")
public class APIAfterSCAbortSessionTest extends APISystemSuperSessionClientTest {

	/**
	 * Description: create session after SC was aborted<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t01_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);

		ctrl.stopServer(sesSrvCtx); // stop test server now, it cannot be stopped without SC later
		ctrl.stopSC(scCtx);
		
		cbk = new MsgCallback(sessionService);
		response = sessionService.createSession(request, cbk);
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
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(sessionService);
		response = sessionService.createSession(request, cbk);

		ctrl.stopServer(sesSrvCtx); // stop test server now, it cannot be stopped without SC later
		ctrl.stopSC(scCtx);

		request.setMessageInfo(TestConstants.echoCmd);
		response = sessionService.execute(request);
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
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(sessionService);
		response = sessionService.createSession(request, cbk);
		request.setMessageInfo(TestConstants.echoCmd);
		messageReceived = false;
		MsgCallback cbk = new MsgCallback(sessionService);

		ctrl.stopServer(sesSrvCtx); 	// stop test server now, it cannot be stopped without SC later
		ctrl.stopSC(scCtx);

		sessionService.send(request);
	}

	/**
	 * Description: delete session after SC was aborted<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t04_deleteSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(sessionService);
		response = sessionService.createSession(request, cbk);

		ctrl.stopServer(sesSrvCtx); // stop test server now, it cannot be stopped without SC later
		ctrl.stopSC(scCtx);

		sessionService.deleteSession();
	}
}
