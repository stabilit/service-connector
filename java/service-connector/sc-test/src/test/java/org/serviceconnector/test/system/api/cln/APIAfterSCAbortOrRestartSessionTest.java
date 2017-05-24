/*-----------------------------------------------------------------------------*
 *                                                                             *
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
 *-----------------------------------------------------------------------------*/
package org.serviceconnector.test.system.api.cln;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.test.system.SystemSuperTest;
import org.serviceconnector.test.system.api.APISystemSuperSessionClientTest;

import junit.framework.Assert;

@SuppressWarnings("unused")
public class APIAfterSCAbortOrRestartSessionTest extends APISystemSuperSessionClientTest {

	public APIAfterSCAbortOrRestartSessionTest() {
		SystemSuperTest.setUpServiceConnectorAndServer();
	}

	@Override
	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		this.setUpClientToSC();
	}

	/**
	 * Description: client has session and gets notified after SC was aborted<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_notifyClientAfterSCcrash() throws Exception {
		SCMessage request = new SCMessage();
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		sessionService1.setEchoIntervalSeconds(10); // want be notified quickly!
		response = sessionService1.createSession(request, msgCallback1);
		Assert.assertNotNull("the session ID is null", sessionService1.getSessionId());

		// stop test server now, it cannot be stopped without SC later
		ctrl.stopServer(srvCtxs.get(TestConstants.sesServerName1));
		ctrl.stopSC(scCtxs.get(TestConstants.SC0));

		msgCallback1.waitForMessage(12);
		Assert.assertTrue("error code is not set", msgCallback1.getScErrorCode() > 0);
		Assert.assertNotNull("error text the not set", msgCallback1.getScErrorText());
	}

	/**
	 * Description: client crashes (server gets stopped, session destroyed) in loading cache process, another client loads same cacheId<br>
	 * Expectation: passes
	 */
	@Test
	public void t02_testCacheStateAfterStoppingLoadingClient() throws Exception {
		SCMessage request = new SCMessage();
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);

		SCMessage message = new SCMessage();
		message.setMessageInfo(TestConstants.cacheCmd);
		message.setData("cache50MBStringFor1Hour");
		message.setCacheId("700");
		sessionService1.send(message);
		// stop test server now, session on SC gets deleted
		ctrl.stopServer(srvCtxs.get(TestConstants.sesServerName1));
		ctrl.startServer(TestConstants.COMMUNICATOR_TYPE_SESSION, TestConstants.logbackSrv, TestConstants.sesServerName1, TestConstants.PORT_SES_SRV_TCP,
				TestConstants.PORT_SC0_TCP, 100, 10, TestConstants.sesServiceName1);

		SCSessionService sessionService2 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService2);
		sessionService2.createSession(request, msgCallback1);
		message = new SCMessage();
		message.setMessageInfo(TestConstants.cacheCmd);
		message.setData("cache10MBStringFor1Hour");
		message.setCacheId("700");

		response = null;
		while (response == null) {
			try {
				response = sessionService2.execute(message);
			} catch (SCServiceException e) {
				if (e.getSCErrorCode() != SCMPError.CACHE_LOADING.getErrorCode()) {
					throw e;
				}
				Thread.sleep(5000);
				continue;
			}
		}
		Assert.assertEquals("Received message has wrong length", 10485762, response.getDataLength());
	}
}
