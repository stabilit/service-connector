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
package com.stabilit.scm.unit.test.msgid;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPClnDataCall;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.unit.test.session.SuperSessionTestCase;

/**
 * @author JTraber
 */
public class MessageIdTestCase extends SuperSessionTestCase {

	public MessageIdTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void messageIdTest() throws Exception {
		SCMPClnDataCall clnDataCall = null;
		// normal communication
		for (int i = 0; i < 20; i++) {
			clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL
					.newInstance(req, "simulation", this.sessionId);

			clnDataCall.setMessagInfo("message info");
			clnDataCall.setRequestBody("get Data (query)");

			clnDataCall.invoke(this.sessionCallback);
			SCMPMessage scmpReply = this.sessionCallback.getMessageSync();
			String sessionId = clnDataCall.getRequest().getSessionId();
			Assert.assertEquals(sessionId, scmpReply.getSessionId());
			Assert.assertEquals(i + "", scmpReply.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		}
		// small request - large response communication
		StringBuilder reqData = new StringBuilder();
		reqData.append("large:");
		for (int i = 0; i < 10000; i++) {
			reqData.append(i);
		}
		clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(req, "simulation", this.sessionId);
		clnDataCall.setMessagInfo("message info");
		clnDataCall.setRequestBody(reqData.toString());
		clnDataCall.invoke(this.sessionCallback);
		SCMPMessage scmpReply = this.sessionCallback.getMessageSync();
		Assert.assertEquals("3/2", scmpReply.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));

		// large request - large response communication
		reqData = new StringBuilder();
		reqData.append("large:");
		for (int i = 0; i < 100000; i++) {
			if (reqData.length() > SCMPMessage.LARGE_MESSAGE_LIMIT + 10000) {
				break;
			}
			reqData.append(i);
		}
		clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(req, "simulation", this.sessionId);
		clnDataCall.setMessagInfo("message info");
		clnDataCall.setRequestBody(reqData.toString());
		clnDataCall.invoke(this.sessionCallback);
		scmpReply = this.sessionCallback.getMessageSync();
		Assert.assertEquals("3/2", scmpReply.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
	}
}