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

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.ISCClient;
import com.stabilit.scm.cln.service.IService;
import com.stabilit.scm.cln.service.ISessionService;
import com.stabilit.scm.common.log.IExceptionLogger;
import com.stabilit.scm.common.log.impl.ExceptionLogger;
import com.stabilit.scm.common.service.ISCMessage;
import com.stabilit.scm.common.service.ISCMessageCallback;
import com.stabilit.scm.common.service.SCMessage;
import com.stabilit.scm.common.service.SCMessageCallback;
import com.stabilit.scm.unit.test.SetupTestCases;

public class ClnAPIAsyncSessionTestCase {
	
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ClnAPIAsyncSessionTestCase.class);

	@Before
	public void setUp() {
		SetupTestCases.setupSCSessionServer10Connections();
	}

	@Test
	public void testClnAPI() throws Exception {
		ISCClient sc = null;
		try {
			sc = new SCClient();
			sc.setMaxConnections(100);
			// set environment, e.g. keepAliveInterval
			// connects to SC, checks connection to SC
			sc.attach("localhost", 8080);
			ISessionService sessionServiceA = sc.newSessionService("simulation");
			sessionServiceA.createSession("sessionInfo", 60, 360);
			SCMessage requestMsg = new SCMessage();
			byte[] buffer = new byte[1024];
			requestMsg.setData(buffer);
			requestMsg.setCompressed(false);
			requestMsg.setMessageInfo("test");
			ISCMessageCallback callback = new TestCallback(sessionServiceA);
			sessionServiceA.execute(requestMsg, callback);
			Thread.sleep(100000);
			// deletes the session
			sessionServiceA.deleteSession();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// disconnects from SC
				sc.detach();
			} catch (Throwable e) {
				sc = null;
			}
		}
	}

	private class TestCallback extends SCMessageCallback {

		public TestCallback(IService service) {
			super(service);
		}

		@Override
		public void callback(ISCMessage msg) {
			System.out.println(msg);
		}

		@Override
		public void callback(Exception ex) {
			IExceptionLogger exceptionLogger = ExceptionLogger.getInstance();
			exceptionLogger.logErrorException(logger, this.getClass().getName(), "callback", ex);
		}
	}

	@After
	public void tearDown() {
		System.out.println(SetupTestCases.statisticsListener);
	}
}
