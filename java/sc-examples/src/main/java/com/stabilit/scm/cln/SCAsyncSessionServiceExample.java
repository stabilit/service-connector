/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.cln;

import com.stabilit.scm.cln.service.ISCClient;
import com.stabilit.scm.cln.service.IService;
import com.stabilit.scm.cln.service.IServiceContext;
import com.stabilit.scm.cln.service.ISessionService;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.service.ISC;
import com.stabilit.scm.common.service.ISCMessage;
import com.stabilit.scm.common.service.ISCMessageCallback;
import com.stabilit.scm.common.service.SCMessage;
import com.stabilit.scm.common.service.SCMessageCallback;

/**
 * The Class SCAsyncSessionServiceExample. Demonstrates use of session service in asynchronous mode.
 */
public class SCAsyncSessionServiceExample {

	private static boolean messageReceived = false;

	public static void main(String[] args) {
		SCAsyncSessionServiceExample example = new SCAsyncSessionServiceExample();
		example.runExample();
	}

	public void runExample() {
		ISCClient sc = null;
		try {
			sc = new SCClient();
			sc.setMaxConnections(100);

			// connects to SC, checks connection to SC
			sc.attach("localhost", 8080);

			ISessionService sessionServiceA = sc.newSessionService("simulation");
			// creates a session
			sessionServiceA.createSession("sessionInfo", 60, 300);

			ISCMessage requestMsg = new SCMessage();
			requestMsg.setData("Hello World");
			requestMsg.setCompressed(false);
			ISCMessageCallback callback = new ExampleCallback(sessionServiceA);
			sessionServiceA.execute(requestMsg, callback);

			// wait until message received
			while (SCAsyncSessionServiceExample.messageReceived == false);
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

	/**
	 * The Class ExampleCallback. Callback used for asynchronously execution.
	 */
	private class ExampleCallback extends SCMessageCallback {

		public ExampleCallback(IService service) {
			super(service);
		}

		@Override
		public void callback(ISCMessage msg) {
			IServiceContext sessionContext = (IServiceContext) this.getService().getContext();
			ISC serviceConnector = sessionContext.getServiceConnector();
			System.out.println(msg);
			SCAsyncSessionServiceExample.messageReceived = true;
		}

		@Override
		public void callback(Exception ex) {
			ExceptionPoint.getInstance().fireException(this, ex);
		}
	}
}