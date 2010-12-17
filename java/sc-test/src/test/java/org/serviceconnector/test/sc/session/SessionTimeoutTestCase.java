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
package org.serviceconnector.test.sc.session;

import org.junit.Test;
import org.serviceconnector.TestUtil;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnCreateSessionCall;
import org.serviceconnector.call.SCMPClnDeleteSessionCall;
import org.serviceconnector.call.SCMPClnExecuteCall;
import org.serviceconnector.call.SCMPEchoCall;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.sc.attach.SuperAttachTestCase;



public class SessionTimeoutTestCase extends SuperAttachTestCase {

	/**
	 * @param fileName
	 */
	public SessionTimeoutTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void sessionTimeout() throws Exception {
		// sets up a create session call
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(req, "session-1");
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoIntervalSeconds(1);
		createSessionCall.invoke(this.attachCallback, 1000);
		SCMPMessage responseMessage = this.attachCallback.getMessageSync(3000);
		TestUtil.checkReply(responseMessage);
		String sessionId = responseMessage.getSessionId();
		Thread.sleep(1200);
		// session should not exist
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req, "session-1",
				sessionId);
		clnExecuteCall.setMessagInfo("message info");
		clnExecuteCall.setRequestBody("get Data (query)");
		clnExecuteCall.invoke(this.attachCallback, 1000);
		SCMPMessage msg = this.attachCallback.getMessageSync(3000);
		TestUtil.verifyError(msg, SCMPError.NOT_FOUND, SCMPMsgType.CLN_EXECUTE);

		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(req, "session-1", sessionId);
		deleteSessionCall.invoke(this.attachCallback, 1000);
	}

	@Test
	public void noSessionTimeout() throws Exception {
		// sets up a create session call
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(req, "session-1");
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoIntervalSeconds(1);
		createSessionCall.invoke(this.attachCallback, 1000);
		SCMPMessage responseMessage = this.attachCallback.getMessageSync(3000);
		TestUtil.checkReply(responseMessage);
		String sessionId = responseMessage.getSessionId();

		for (int i = 0; i < 5; i++) {
			SCMPEchoCall clnEchoCall = (SCMPEchoCall) SCMPCallFactory.ECHO_CALL.newInstance(req,
					"session-1", sessionId);
			clnEchoCall.invoke(this.attachCallback, 1000);
			TestUtil.checkReply(this.attachCallback.getMessageSync(3000));
			Thread.sleep(400);
		}
		// session should still exist
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req, "session-1",
				sessionId);
		clnExecuteCall.setMessagInfo("message info");
		clnExecuteCall.setRequestBody("get Data (query)");
		clnExecuteCall.invoke(this.attachCallback, 1000);
		TestUtil.checkReply(this.attachCallback.getMessageSync(3000));

		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(req, "session-1", sessionId);
		deleteSessionCall.invoke(this.attachCallback, 1000);
	}
}
