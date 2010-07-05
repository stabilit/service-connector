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
package com.stabilit.scm.unit.cln.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.stabilit.scm.cln.service.ISCMessageCallback;
import com.stabilit.scm.cln.service.ISessionContext;
import com.stabilit.scm.cln.service.ISessionService;
import com.stabilit.scm.cln.service.SCMessage;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.service.IServiceConnector;
import com.stabilit.scm.common.service.SCSessionServiceCallbackAdapter;
import com.stabilit.scm.common.service.ServiceConnector;
import com.stabilit.scm.unit.test.SetupTestCases;

public class ClnAPIAsyncSessionTestCase {

	@Before
	public void setUp() {
		// test setup
		SetupTestCases.setupAll();
	}

	@Test
	public void testClnAPI() throws Exception {
		IServiceConnector sc = null;
		try {
			sc = new ServiceConnector("localhost", 8080);
			sc.setMaxConnections(100);
			// set environment, e.g. keepAliveInterval
			// connects to SC, checks connection to SC
			sc.attach();
			ISessionService sessionServiceA = sc.newSessionService("simulation");
			sessionServiceA.createSession("sessionInfo", 360 , 60);
			SCMessage requestMsg = new SCMessage();
			byte[] buffer = new byte[1024];
			requestMsg.setData(buffer);
			requestMsg.setCompressed(false);
			requestMsg.setMessageInfo("test");
			ISCMessageCallback callback = new TestCallback(sessionServiceA);
			sessionServiceA.execute(requestMsg, callback);
			callback.join(); // wait until
			Thread.sleep(100000);
			// deletes the session
			sessionServiceA.deleteSession();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// disconnects from SC
				sc.detach();
			} catch (Exception e) {
				sc = null;
			}
		}
	}

	class TestCallback extends SCSessionServiceCallbackAdapter {

		public TestCallback(ISessionService sessionService) {
			super(sessionService);
		}

		@Override
		public void callback(SCMessage msg) {
			try {
				ISessionContext sessionContext = this.sessionService.getSessionContext();
				IServiceConnector serviceConnector = sessionContext.getServiceConnector();
				System.out.println(msg);
			} catch (Exception e) {
				ExceptionPoint.getInstance().fireException(this, e);
			}
		}

		@Override
		public void callback(Throwable th) {
			ExceptionPoint.getInstance().fireException(this, th);
		}
	}

	@After
	public void tearDown() {
		System.out.println(SetupTestCases.statisticsListener);
	}
}
