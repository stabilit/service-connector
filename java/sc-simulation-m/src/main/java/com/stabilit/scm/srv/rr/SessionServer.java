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
package com.stabilit.scm.srv.rr;

import com.stabilit.scm.cln.service.ISessionService;
import com.stabilit.scm.cln.service.SCMessage;
import com.stabilit.scm.common.service.ISCClient;
import com.stabilit.scm.common.service.ISCServer;
import com.stabilit.scm.common.service.SCClient;
import com.stabilit.scm.common.service.SCServer;

public class SessionServer {
	private ISCClient scCln = null;
	private ISCServer scSrv = null;
	private String serviceName = "simulation";

	public static void main(String[] args) throws Exception {
		SessionServer sessionServer = new SessionServer();
		sessionServer.runExample();
	}

	private void shutdown() {

		try {
			// disconnects from SC
			scCln.detach();
			scSrv.deregisterService(serviceName);
		} catch (Exception e) {
			scCln = null;
			scSrv = null;
		}
	}

	public void runExample() {

		try {
			scSrv = new SCServer("localhost", 8080, "netty.http", 10);
			scCln = new SCClient("localhost", 8080, "netty.http", 10);

			// connects to SC as client
			scCln.attach();

			// connect to SC as server
			scSrv.setMaxSessions(10);
			SRVCallback srvCallback = new SRVCallback();
			scSrv.registerService(serviceName, srvCallback);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	class SRVCallback implements ISCServerCallback {

		@Override
		public SCMessage createSession(SCMessage message) {
			return message;
		}

		@Override
		public SCMessage deleteSession(SCMessage message) {
			return message;

		}

		@Override
		public SCMessage abortSession(SCMessage message) {
			return message;
		}

		@Override
		public SCMessage execute(SCMessage request) {
			// get any message attribute
			String sessionId = request.getSessionId();

			// TODO get message body and look for kill message => initiate shutdown
			try {
				// here is the application code
				ISessionService sessionServiceA = scCln.newSessionService("simulation");
				sessionServiceA.createSession("sessionInfo", 60, 10);
				SCMessage requestMsg = new SCMessage();
				byte[] buffer = new byte[1024];
				requestMsg.setData(buffer);
				requestMsg.setCompressed(false);
				requestMsg.setMessageInfo("test");
				SCMessage responseMsg = sessionServiceA.execute(requestMsg);
				System.out.println(responseMsg);
				// deletes the session
				sessionServiceA.deleteSession();

			} catch (Exception e) {
			}
			SCMessage response = new SCMessage();
			// set response attributes (application)
			return response;
		}
	}
}