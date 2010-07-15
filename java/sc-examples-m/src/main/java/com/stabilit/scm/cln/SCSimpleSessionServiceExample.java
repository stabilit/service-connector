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

import com.stabilit.scm.cln.service.ISessionService;
import com.stabilit.scm.common.service.ISCClient;
import com.stabilit.scm.common.service.ISCMessage;
import com.stabilit.scm.common.service.SCMessage;

public class SCSimpleSessionServiceExample {

	public static void main(String[] args) {
		SCSimpleSessionServiceExample.runExample();
	}

	public static void runExample() {
		ISCClient sc = null;
		try {
			sc = new SCClient("localhost", 8000);
			sc.setMaxConnections(100);

			// connects to SC, checks connection to SC
			sc.attach();

			ISessionService sessionServiceA = sc.newSessionService("simulation");
			sessionServiceA.createSession("sessionInfo", 60, 300);

			ISCMessage requestMsg = new SCMessage();
			byte[] buffer = new byte[1024];
			requestMsg.setData(buffer);
			requestMsg.setCompressed(false);
			requestMsg.setMessageInfo("test");
			ISCMessage responseMsg = sessionServiceA.execute(requestMsg);
			System.out.println(responseMsg.getData().toString());
			
			requestMsg = new SCMessage();
			requestMsg.setData("kill server");
			requestMsg.setCompressed(false);
			requestMsg.setMessageInfo("test");
			responseMsg = sessionServiceA.execute(requestMsg);
			

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
}