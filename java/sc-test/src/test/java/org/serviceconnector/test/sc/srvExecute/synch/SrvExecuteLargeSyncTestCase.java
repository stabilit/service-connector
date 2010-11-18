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
import org.serviceconnector.Constants;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnExecuteCall;
import org.serviceconnector.scmp.SCMPBodyType;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.sc.session.SuperSessionTestCase;



/**
 * @author JTraber
 */
public class SrvExecuteLargeSyncTestCase extends SuperSessionTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public SrvExecuteLargeSyncTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void srvExecuteApplicationError() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req, "session-1",
				this.sessionId);
		clnExecuteCall.setMessagInfo("message info");
		clnExecuteCall.setRequestBody("appError");
		clnExecuteCall.invoke(this.sessionCallback, 2000);
		SCMPMessage scmpReply = this.sessionCallback.getMessageSync();
		Assert.assertEquals("500", scmpReply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_CODE));
		Assert.assertEquals("appErrorText", scmpReply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
	}

	@Test
	public void srvExecuteSmallRequestLargeResponseTest() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("large:");
		for (int i = 0; i < 10000; i++) {
			sb.append(i);
		}

		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req, "session-1",
				this.sessionId);
		clnExecuteCall.setMessagInfo("message info");
		clnExecuteCall.setRequestBody(sb.toString());
		clnExecuteCall.invoke(this.sessionCallback, 1000);
		SCMPMessage scmpReply = this.sessionCallback.getMessageSync();

		// create expected result
		StringBuilder sbRes = new StringBuilder();
		sbRes.append("large:");
		for (int i = 0; i < 100000; i++) {
			if (sbRes.length() > Constants.MAX_MESSAGE_SIZE + 10000) {
				break;
			}
			sbRes.append(i);
		}

		/*********************************** Verify attach response msg **********************************/
		Assert.assertEquals(sbRes.length() + "", scmpReply.getBodyLength() + "");
		Assert.assertEquals(sbRes.toString(), scmpReply.getBody());
		Assert.assertEquals(SCMPBodyType.TEXT.getValue(), scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertEquals(SCMPMsgType.CLN_EXECUTE.getValue(), scmpReply.getMessageType());
		String serviceName = clnExecuteCall.getRequest().getServiceName();
		String sessionId = clnExecuteCall.getRequest().getSessionId();
		Assert.assertEquals(serviceName, scmpReply.getServiceName());
		Assert.assertEquals(sessionId, scmpReply.getSessionId());
	}

	@Test
	public void srvExecuteLargeRequestSmallResponseTest() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req, "session-1",
				this.sessionId);
		clnExecuteCall.setMessagInfo("message info");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 19000; i++) {
			sb.append(i);
		}
		String expectedBody = "message data test case";
		clnExecuteCall.setRequestBody(sb.toString());
		clnExecuteCall.invoke(this.sessionCallback, 1000);
		SCMPMessage scmpReply = this.sessionCallback.getMessageSync();
		Assert.assertEquals(SCMPBodyType.TEXT.getValue(), scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertNotNull(scmpReply.getSessionId());
		Assert.assertEquals(expectedBody.length(), scmpReply.getBodyLength());
		Assert.assertEquals("session-1", scmpReply.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME));
		Assert.assertEquals(expectedBody, scmpReply.getBody());
	}

	@Test
	public void srvExecuteLargeRequestLargeResponseTest() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req, "session-1",
				this.sessionId);
		clnExecuteCall.setMessagInfo("message info");
		StringBuilder sb = new StringBuilder();
		sb.append("large:");
		for (int i = 0; i < 100000; i++) {
			if (sb.length() > Constants.MAX_MESSAGE_SIZE + 10000) {
				break;
			}
			sb.append(i);
		}
		clnExecuteCall.setRequestBody(sb.toString());
		clnExecuteCall.invoke(this.sessionCallback, 1000);
		SCMPMessage scmpReply = this.sessionCallback.getMessageSync();
		Assert.assertEquals(SCMPBodyType.TEXT.getValue(), scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertNotNull(scmpReply.getSessionId());
		Assert.assertEquals(sb.length() + "", scmpReply.getBodyLength() + "");
		Assert.assertEquals("session-1", scmpReply.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME));
		Assert.assertEquals(sb.toString(), scmpReply.getBody());
	}
}
