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
package com.stabilit.sc.unit.test.srvData;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.sc.cln.call.SCMPCallFactory;
import com.stabilit.sc.cln.call.SCMPClnDataCall;
import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.scmp.SCMPBodyType;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.unit.test.session.SuperSessionTestCase;

/**
 * @author JTraber
 */
public class SrvDataLargeTestCase extends SuperSessionTestCase {

	/**
	 * @param fileName
	 */
	public SrvDataLargeTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void srvDataSmallRequestLargeResponseTest() throws Exception {
		SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(client,
				scmpSession);
		clnDataCall.setMessagInfo("message info");
		clnDataCall.setBody("large");
		SCMPMessage scmpReply = clnDataCall.invoke();

		/*********************************** Verify connect response msg **********************************/
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 10000; i++) {
			sb.append(i);
		}
		Assert.assertEquals(sb.toString(), scmpReply.getBody());
		Assert.assertEquals(sb.length() + "", scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_LENGTH));
		Assert.assertEquals(SCMPBodyType.text.getName(), scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertNotNull(scmpReply.getHeader(SCMPHeaderAttributeKey.SESSION_INFO));
		Assert.assertEquals(SCMPMsgType.CLN_DATA.getResponseName(), scmpReply.getMessageType());
		String serviceName = clnDataCall.getRequest().getHeader(SCMPHeaderAttributeKey.SERVICE_NAME);
		String sessionId = clnDataCall.getRequest().getSessionId();
		Assert.assertEquals(serviceName, scmpReply.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME));
		Assert.assertEquals(sessionId, scmpReply.getSessionId());
	}

	@Test
	public void srvDataLargeRequestSmallResponseTest() throws Exception {
		SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(client,
				scmpSession);
		clnDataCall.setMessagInfo("message info");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 19000; i++) {
			sb.append(i);
		}
		clnDataCall.setBody(sb.toString());
		SCMPMessage scmpReply = clnDataCall.invoke();
		System.out.println(scmpReply);
		// TODO assertion
	}

	public void srvDataLargeRequestLargeResponseTest() throws Exception {
		// TODO
	}

}
