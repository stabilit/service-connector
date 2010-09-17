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
package test.serviceconnector.messageId;

import junit.framework.Assert;

import org.junit.Test;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnExecuteCall;
import org.serviceconnector.conf.Constants;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;

import test.serviceconnector.session.SuperSessionTestCase;


/**
 * @author JTraber
 */
public class MessageIdTestCase extends SuperSessionTestCase {

	public MessageIdTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void messageIdTest() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = null;
		// normal communication
		int index = 0;
		for (index = 0; index < 20; index++) {
			this.msgId.incrementMsgSequenceNr();
			clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req, "simulation",
					this.sessionId);

			clnExecuteCall.setMessagInfo("message info");
			clnExecuteCall.setRequestBody("get Data (query)");
			clnExecuteCall.getRequest().setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, this.msgId.getCurrentMessageID());
			clnExecuteCall.invoke(this.sessionCallback, 1000);
			SCMPMessage scmpReply = this.sessionCallback.getMessageSync();
			String sessionId = clnExecuteCall.getRequest().getSessionId();
			Assert.assertEquals(sessionId, scmpReply.getSessionId());
			Assert.assertEquals((index + 2) + "", scmpReply.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		}
		// small request - large response communication
		StringBuilder reqData = new StringBuilder();
		reqData.append("large:");
		for (int i = 0; i < 10000; i++) {
			reqData.append(i);
		}
		this.msgId.incrementMsgSequenceNr();
		clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req, "simulation",
				this.sessionId);
		clnExecuteCall.getRequest().setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, this.msgId.getCurrentMessageID());
		clnExecuteCall.setMessagInfo("message info");
		clnExecuteCall.setRequestBody(reqData.toString());
		clnExecuteCall.invoke(this.sessionCallback, 1000);
		SCMPMessage scmpReply = this.sessionCallback.getMessageSync();
		Assert.assertEquals((index + 3) + "", scmpReply.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));

		// large request - large response communication
		reqData = new StringBuilder();
		reqData.append("large:");
		for (int i = 0; i < 100000; i++) {
			if (reqData.length() > Constants.LARGE_MESSAGE_LIMIT + 10000) {
				break;
			}
			reqData.append(i);
		}
		this.msgId.incrementMsgSequenceNr();
		clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req, "simulation",
				this.sessionId);
		clnExecuteCall.setMessagInfo("message info");
		clnExecuteCall.setRequestBody(reqData.toString());
		clnExecuteCall.invoke(this.sessionCallback, 1000);
		scmpReply = this.sessionCallback.getMessageSync();
		Assert.assertEquals((index + 6) + "", scmpReply.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
	}
}