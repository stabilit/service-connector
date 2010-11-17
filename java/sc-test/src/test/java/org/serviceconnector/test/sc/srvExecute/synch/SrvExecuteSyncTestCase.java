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
package org.serviceconnector.test.sc.srvExecute.synch;

import junit.framework.Assert;

import org.junit.Test;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnCreateSessionCall;
import org.serviceconnector.call.SCMPClnDeleteSessionCall;
import org.serviceconnector.call.SCMPClnExecuteCall;
import org.serviceconnector.scmp.SCMPBodyType;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.sc.SCTest;
import org.serviceconnector.test.sc.session.SuperSessionTestCase;
import org.serviceconnector.util.SynchronousCallback;

/**
 * @author JTraber
 */
public class SrvExecuteSyncTestCase extends SuperSessionTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public SrvExecuteSyncTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void multipleSrvExecuteTest() throws Exception {

		for (int i = 0; i < 100; i++) {
			SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req,
					"local-session-service", this.sessionId);
			clnExecuteCall.setMessagInfo("message info");
			clnExecuteCall.setRequestBody("get Data (query)");
			clnExecuteCall.invoke(this.sessionCallback, 1000);
			SCMPMessage scmpReply = this.sessionCallback.getMessageSync();

			Assert.assertEquals("message data test case", scmpReply.getBody());
			Assert.assertEquals(SCMPBodyType.TEXT.getValue(), scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
			int bodyLength = "message data test case".length();
			Assert.assertEquals(bodyLength + "", scmpReply.getBodyLength() + "");
			Assert.assertEquals(SCMPMsgType.CLN_EXECUTE.getValue(), scmpReply.getMessageType());
			String serviceName = clnExecuteCall.getRequest().getServiceName();
			String sessionId = clnExecuteCall.getRequest().getSessionId();
			Assert.assertEquals(serviceName, scmpReply.getServiceName());
			Assert.assertEquals(sessionId, scmpReply.getSessionId());
		}
	}

	@Test
	public void executeWaitsForConnection_TimesOutTest() throws Exception {
		this.clnCreateSession1Conn();
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req,
				"local-session-service", this.sessionId);
		clnExecuteCall.setMessagInfo("message info");
		clnExecuteCall.setRequestBody("wait:2000");
		TestWaitMechanismCallback callback = new TestWaitMechanismCallback(true);

		clnExecuteCall.invoke(callback, 10000);

		// to assure second create is not faster
		Thread.sleep(20);
		clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL
				.newInstance(req, "local-session-service", this.sessionId);
		clnExecuteCall.setMessagInfo("message info");
		TestWaitMechanismCallback callback1 = new TestWaitMechanismCallback(true);
		clnExecuteCall.invoke(callback1, 1000);

		SCMPMessage responseMessage = callback.getMessageSync();
		SCMPMessage responseMessage1 = callback1.getMessageSync();

		SCTest.checkReply(responseMessage);
		Assert.assertFalse(responseMessage.isFault());
		Assert.assertTrue(responseMessage1.isFault());
		SCTest.verifyError(responseMessage1, SCMPError.SC_ERROR, "[no free connection on server for service local-session-service]",
				SCMPMsgType.CLN_EXECUTE);
		this.clnDeleteSession1Conn();
	}

	@Test
	public void executeWaitsForConnection_WaitsTest() throws Exception {
		this.clnCreateSession1Conn();
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req,
				"local-session-service", this.sessionId);
		clnExecuteCall.setMessagInfo("message info");
		clnExecuteCall.setRequestBody("wait:2000");
		TestWaitMechanismCallback callback = new TestWaitMechanismCallback(true);

		clnExecuteCall.invoke(callback, 10000);

		// to assure second create is not faster
		Thread.sleep(20);
		clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL
				.newInstance(req, "local-session-service", this.sessionId);
		clnExecuteCall.setMessagInfo("message info");
		TestWaitMechanismCallback callback1 = new TestWaitMechanismCallback(true);
		clnExecuteCall.invoke(callback1, 10000);

		SCMPMessage responseMessage = callback.getMessageSync();
		SCMPMessage responseMessage1 = callback1.getMessageSync();

		SCTest.checkReply(responseMessage);
		SCTest.checkReply(responseMessage1);
		this.clnDeleteSession1Conn();
	}

	@Test
	public void ExcOnServerExecuteTest() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req,
				"local-session-service", this.sessionId);
		clnExecuteCall.setMessagInfo("message info");
		clnExecuteCall.setRequestBody("excOnServer");
		clnExecuteCall.invoke(this.sessionCallback, 1000);
		SCMPMessage scmpReply = this.sessionCallback.getMessageSync();
		System.out.println(scmpReply.getBody());
		Assert.assertTrue(scmpReply.isFault());
		Assert.assertEquals(SCMPError.SERVER_ERROR.getErrorCode(), scmpReply
				.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));

	}

	private void clnCreateSession1Conn() throws Exception {
		// sets up a create session call
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(req, "1conn");
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoIntervalSeconds(3600);
		// create session and keep sessionId
		createSessionCall.invoke(this.sessionCallback, 1000);
		SCMPMessage resp = this.sessionCallback.getMessageSync();
		this.sessionId = resp.getSessionId();
	}

	private void clnDeleteSession1Conn() throws Exception {
		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(this.req, "1conn", this.sessionId);
		deleteSessionCall.invoke(this.sessionCallback, 1000);
		this.sessionCallback.getMessageSync();
	}

	protected class TestWaitMechanismCallback extends SynchronousCallback {

		public TestWaitMechanismCallback(boolean synchronous) {
			this.synchronous = synchronous;
		}
	}
}