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
package com.stabilit.scm.unit.test.srvData.async;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPClnDataCall;
import com.stabilit.scm.common.scmp.SCMPBodyType;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.util.SynchronousCallback;
import com.stabilit.scm.unit.test.session.SuperSessionTestCase;

/**
 * @author JTraber
 */
public class SrvDataLargeAsyncTestCase extends SuperSessionTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public SrvDataLargeAsyncTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void srvDataSmallRequestLargeResponseTest() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("large:");
		for (int i = 0; i < 10000; i++) {
			sb.append(i);
		}

		SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(req, "simulation",
				this.sessionId);
		clnDataCall.setMessagInfo("message info");
		clnDataCall.setRequestBody(sb.toString());
		SynchronousCallback callback = new SrvDataTestCaseCallback();
		clnDataCall.invoke(callback);
		SCMPMessage scmpReply = callback.getMessageSync();

		// create expected result
		StringBuilder sbRes = new StringBuilder();
		sbRes.append("large:");
		for (int i = 0; i < 100000; i++) {
			if (sbRes.length() > SCMPMessage.LARGE_MESSAGE_LIMIT + 10000) {
				break;
			}
			sbRes.append(i);
		}
		Assert.assertEquals(sbRes.length() + "", scmpReply.getBodyLength() + "");
		Assert.assertEquals(sbRes.toString(), scmpReply.getBody());
		Assert.assertEquals(SCMPBodyType.TEXT.getValue(), scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertEquals(SCMPMsgType.CLN_DATA.getValue(), scmpReply.getMessageType());
		String serviceName = clnDataCall.getRequest().getServiceName();
		String sessionId = clnDataCall.getRequest().getSessionId();
		Assert.assertEquals(serviceName, scmpReply.getServiceName());
		Assert.assertEquals(sessionId, scmpReply.getSessionId());
		Assert.assertEquals("3/2", scmpReply.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
	}

	private class SrvDataTestCaseCallback extends SynchronousCallback {
		// necessary because SynchronousCallback is abstract
	}

	@Test
	public void srvDataLargeRequestSmallResponseTest() throws Exception {
		SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(req, "simulation",
				this.sessionId);
		clnDataCall.setMessagInfo("message info");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 19000; i++) {
			sb.append(i);
		}
		String expectedBody = "message data test case";
		clnDataCall.setRequestBody(sb.toString());
		SrvDataTestCaseCallback callback = new SrvDataTestCaseCallback();
		clnDataCall.invoke(callback);
		SCMPMessage scmpReply = callback.getMessageSync();

		Assert.assertEquals(SCMPBodyType.TEXT.getValue(), scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertEquals("3/1", scmpReply.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		Assert.assertNotNull(scmpReply.getSessionId());
		Assert.assertEquals(expectedBody.length(), scmpReply.getBodyLength());
		Assert.assertEquals("simulation", scmpReply.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME));
		Assert.assertEquals(expectedBody, scmpReply.getBody());
	}

	@Test
	public void srvDataLargeRequestLargeResponseTest() throws Exception {
		SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(req, "simulation",
				this.sessionId);
		clnDataCall.setMessagInfo("message info");
		StringBuilder sb = new StringBuilder();
		sb.append("large:");
		for (int i = 0; i < 100000; i++) {
			if (sb.length() > SCMPMessage.LARGE_MESSAGE_LIMIT + 10000) {
				break;
			}
			sb.append(i);
		}
		clnDataCall.setRequestBody(sb.toString());
		SrvDataTestCaseCallback callback = new SrvDataTestCaseCallback();
		clnDataCall.invoke(callback);
		SCMPMessage scmpReply = callback.getMessageSync();
		
		Assert.assertEquals(SCMPBodyType.TEXT.getValue(), scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertEquals("3/3", scmpReply.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		Assert.assertNotNull(scmpReply.getSessionId());
		Assert.assertEquals(sb.length() + "", scmpReply.getBodyLength() + "");
		Assert.assertEquals("simulation", scmpReply.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME));
		Assert.assertEquals(sb.toString(), scmpReply.getBody());
	}
}
