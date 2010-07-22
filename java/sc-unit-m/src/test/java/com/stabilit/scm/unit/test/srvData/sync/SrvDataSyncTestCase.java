/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
package com.stabilit.scm.unit.test.srvData.sync;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPClnDataCall;
import com.stabilit.scm.common.scmp.SCMPBodyType;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.unit.test.session.SuperSessionTestCase;

/**
 * @author JTraber
 */
public class SrvDataSyncTestCase extends SuperSessionTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public SrvDataSyncTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void multipleSrvDataTest() throws Exception {

		for (int i = 0; i < 100; i++) {
			SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(req,
					"simulation", this.sessionId);
			clnDataCall.setMessagInfo("message info");
			clnDataCall.setRequestBody("get Data (query)");
			clnDataCall.invoke(this.sessionCallback);
			SCMPMessage scmpReply = this.sessionCallback.getMessageSync();

			Assert.assertEquals("message data test case", scmpReply.getBody());
			Assert.assertEquals(SCMPBodyType.TEXT.getValue(), scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
			int bodyLength = "message data test case".length();
			Assert.assertEquals(bodyLength + "", scmpReply.getBodyLength() + "");
			Assert.assertEquals(SCMPMsgType.CLN_DATA.getValue(), scmpReply.getMessageType());
			String serviceName = clnDataCall.getRequest().getServiceName();
			String sessionId = clnDataCall.getRequest().getSessionId();
			Assert.assertEquals(serviceName, scmpReply.getServiceName());
			Assert.assertEquals(sessionId, scmpReply.getSessionId());
			Assert.assertEquals((i + 3) + "", scmpReply.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		}
	}
}