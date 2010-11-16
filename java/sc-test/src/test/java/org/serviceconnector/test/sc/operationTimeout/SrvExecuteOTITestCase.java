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
package org.serviceconnector.test.sc.operationTimeout;

import org.junit.Test;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnExecuteCall;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.sc.SCTest;
import org.serviceconnector.test.sc.session.SuperSessionTestCase;
import org.serviceconnector.util.SynchronousCallback;

/**
 * @author JTraber
 */
public class SrvExecuteOTITestCase extends SuperSessionTestCase {

	public SrvExecuteOTITestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void executeOTIRunsOutSessionCleanedUp() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req,
				"simulation", this.sessionId);
		clnExecuteCall.setMessagInfo("message info");
		clnExecuteCall.setRequestBody("wait:3000");
		TestWaitMechanismCallback callback = new TestWaitMechanismCallback(true);

		clnExecuteCall.invoke(callback, 1000);
		SCMPMessage responseMessage = callback.getMessageSync();
		SCTest.verifyError(responseMessage, SCMPError.PROXY_TIMEOUT, "executing command timed out",
				SCMPMsgType.CLN_EXECUTE);

		clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req, "simulation",
				this.sessionId);
		clnExecuteCall.setMessagInfo("message info");
		TestWaitMechanismCallback callback1 = new TestWaitMechanismCallback(true);
		clnExecuteCall.invoke(callback1, 1000);
		SCMPMessage responseMessage1 = callback1.getMessageSync();
		SCTest.checkReply(responseMessage1);
	}

	protected class TestWaitMechanismCallback extends SynchronousCallback {

		public TestWaitMechanismCallback(boolean synchronous) {
			this.synchronous = synchronous;
		}
	}
}