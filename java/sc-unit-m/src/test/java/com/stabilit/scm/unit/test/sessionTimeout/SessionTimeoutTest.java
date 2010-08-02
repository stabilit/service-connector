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
package com.stabilit.scm.unit.test.sessionTimeout;

import org.junit.Test;

import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPClnCreateSessionCall;
import com.stabilit.scm.common.call.SCMPClnDataCall;
import com.stabilit.scm.common.call.SCMPClnDeleteSessionCall;
import com.stabilit.scm.common.call.SCMPClnEchoCall;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.unit.test.SCTest;
import com.stabilit.scm.unit.test.attach.SuperAttachTestCase;

public class SessionTimeoutTest extends SuperAttachTestCase {

	/**
	 * @param fileName
	 */
	public SessionTimeoutTest(String fileName) {
		super(fileName);
	}

	@Test
	public void sessionTimeout() throws Throwable {
		// sets up a create session call
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(req, "simulation");
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoIntervalSeconds(1);
		createSessionCall.setEchoTimeoutSeconds(10);
		createSessionCall.invoke(this.attachCallback);
		SCMPMessage responseMessage = this.attachCallback.getMessageSync();
		SCTest.checkReply(responseMessage);
		String sessionId = responseMessage.getSessionId();
		Thread.sleep(1000);
		// session should not exist
		SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(req, "simulation",
				sessionId);
		clnDataCall.setMessagInfo("message info");
		clnDataCall.setRequestBody("get Data (query)");
		clnDataCall.invoke(this.attachCallback);
		SCMPMessage msg = this.attachCallback.getMessageSync();
		SCTest.verifyError(msg, SCMPError.NO_SESSION_FOUND, SCMPMsgType.CLN_DATA);

		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(req, "simulation", sessionId);
		deleteSessionCall.invoke(this.attachCallback);
	}

	@Test
	public void noSessionTimeout() throws Throwable {
		// sets up a create session call
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(req, "simulation");
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoIntervalSeconds(1);
		createSessionCall.setEchoTimeoutSeconds(10);
		createSessionCall.invoke(this.attachCallback);
		SCMPMessage responseMessage = this.attachCallback.getMessageSync();
		SCTest.checkReply(responseMessage);
		String sessionId = responseMessage.getSessionId();

		for (int i = 0; i < 5; i++) {
			SCMPClnEchoCall clnEchoCall = (SCMPClnEchoCall) SCMPCallFactory.CLN_ECHO_CALL.newInstance(req,
					"simulation", sessionId);
			clnEchoCall.invoke(this.attachCallback);
			SCTest.checkReply(this.attachCallback.getMessageSync());
			Thread.sleep(500);
		}
		// session should still exist
		SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(req, "simulation",
				sessionId);
		clnDataCall.setMessagInfo("message info");
		clnDataCall.setRequestBody("get Data (query)");
		clnDataCall.invoke(this.attachCallback);
		SCTest.checkReply(this.attachCallback.getMessageSync());

		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(req, "simulation", sessionId);
		deleteSessionCall.invoke(this.attachCallback);
	}
}
