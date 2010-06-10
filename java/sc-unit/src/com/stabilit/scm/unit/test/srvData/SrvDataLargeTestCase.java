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
package com.stabilit.scm.unit.test.srvData;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPClnDataCall;
import com.stabilit.scm.common.scmp.SCMPBodyType;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.unit.test.session.SuperSessionTestCase;

/**
 * @author JTraber
 */
public class SrvDataLargeTestCase extends SuperSessionTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public SrvDataLargeTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void srvDataSmallRequestLargeResponseTest() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("large:");
		for (int i = 0; i < 10000; i++) {
			sb.append(i);
		}
		
		SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(req, this.scSession);
		clnDataCall.setMessagInfo("message info");
		clnDataCall.setRequestBody(sb.toString());
		SCMPMessage scmpReply = clnDataCall.invoke();

		/*********************************** Verify attach response msg **********************************/
		Assert.assertEquals(sb.toString(), scmpReply.getBody());
		Assert.assertEquals(sb.length() + "", scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_LENGTH));
		Assert.assertEquals(SCMPBodyType.text.getName(), scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertNotNull(scmpReply.getHeader(SCMPHeaderAttributeKey.SESSION_INFO));
		Assert.assertEquals(SCMPMsgType.CLN_DATA.getResponseName(), scmpReply.getMessageType());
		String serviceName = clnDataCall.getRequest().getServiceName();
		String sessionId = clnDataCall.getRequest().getSessionId();
		Assert.assertEquals(serviceName, scmpReply.getServiceName());
		Assert.assertEquals(sessionId, scmpReply.getSessionId());
		Assert.assertEquals("3", scmpReply.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
	}

	@Test
	public void srvDataLargeRequestSmallResponseTest() throws Exception {
		SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(req, this.scSession);
		clnDataCall.setMessagInfo("message info");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 19000; i++) {
			sb.append(i);
		}
		clnDataCall.setRequestBody(sb.toString());
		SCMPMessage scmpReply = clnDataCall.invoke();
		Assert.assertEquals(SCMPBodyType.text.toString(), scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertEquals("3/1", scmpReply.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		Assert.assertNotNull(scmpReply.getSessionId());
		Assert.assertEquals("16", scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_LENGTH));
		Assert.assertEquals("Session info", scmpReply.getHeader(SCMPHeaderAttributeKey.SESSION_INFO));
		Assert.assertEquals("simulation", scmpReply.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME));
		Assert.assertEquals("Message number 1", scmpReply.getBody());
	}

	@Test
	public void srvDataLargeRequestLargeResponseTest() throws Exception {
		SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(req, this.scSession);
		clnDataCall.setMessagInfo("message info");
		StringBuilder sb = new StringBuilder();
		sb.append("large:");
		for (int i = 0; i < 10000; i++) {
			if (sb.length() > 60000) {
				break;
			}
			sb.append(i);
		}
		clnDataCall.setRequestBody(sb.toString());
		SCMPMessage scmpReply = clnDataCall.invoke();
		Assert.assertEquals(SCMPBodyType.text.toString(), scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertEquals("3", scmpReply.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		Assert.assertNotNull(scmpReply.getSessionId());
		Assert.assertEquals(sb.length() + "", scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_LENGTH));
		Assert.assertEquals("Session info", scmpReply.getHeader(SCMPHeaderAttributeKey.SESSION_INFO));
		Assert.assertEquals("simulation", scmpReply.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME));
		Assert.assertEquals(sb.toString(), scmpReply.getBody());
	}
}
