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
package com.stabilit.scm.unit.test.echo;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.scm.cln.call.ISCMPCall;
import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPClnEchoCall;
import com.stabilit.scm.common.scmp.SCMPBodyType;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.unit.test.session.SuperSessionTestCase;

public class SrvEchoLargeTestCase extends SuperSessionTestCase {
	//TODO change to clndata
	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public SrvEchoLargeTestCase(String fileName) {
		super(fileName);
	}

	protected Integer index = null;

	@Test
	public void invokeTwoPartsTest() throws Exception {

		SCMPClnEchoCall echoCall = (SCMPClnEchoCall) SCMPCallFactory.CLN_ECHO_CALL.newInstance(req, "simulation",
				this.sessionId);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 19000; i++) {
			sb.append(i);
		}
		echoCall.setRequestBody(sb.toString());
		echoCall.invoke(this.sessionCallback);
		SCMPMessage result = this.sessionCallback.getMessageSync();
		/*************************** verify echo session **********************************/
		Assert.assertEquals(sb.toString(), result.getBody());
		Assert.assertEquals(SCMPBodyType.TEXT.getValue(), result.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertEquals(sb.length() + "", result.getBodyLength() + "");
		Assert.assertEquals(SCMPMsgType.CLN_ECHO.getValue(), result.getMessageType());
		Assert.assertNotNull(result.getSessionId());
	}

	@Test
	public void invokeMorePartsTest() throws Exception {
		SCMPClnEchoCall echoCall = (SCMPClnEchoCall) SCMPCallFactory.CLN_ECHO_CALL.newInstance(req, "simulation",
				this.sessionId);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 89840; i++) {
			sb.append(i);
		}
		echoCall.setRequestBody(sb.toString());
		echoCall.invoke(this.sessionCallback);
		SCMPMessage result = this.sessionCallback.getMessageSync();
		/*************************** verify echo session **********************************/
		Assert.assertEquals(sb.toString(), result.getBody());
		Assert.assertEquals(SCMPBodyType.TEXT.getValue(), result.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertEquals(sb.length() + "", result.getBodyLength() + "");
		Assert.assertEquals(SCMPMsgType.CLN_ECHO.getValue(), result.getMessageType());
		Assert.assertNotNull(result.getSessionId());
	}

	@Test
	public void groupCallTest() throws Exception {
		SCMPClnEchoCall echoCall = (SCMPClnEchoCall) SCMPCallFactory.CLN_ECHO_CALL.newInstance(req, "simulation",
				this.sessionId);
		ISCMPCall groupCall = echoCall.openGroup();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 10; i++) {
			sb.append(i);
			groupCall.setRequestBody(String.valueOf(i));
			groupCall.invoke(this.sessionCallback);
			this.sessionCallback.getMessageSync();
		}
		groupCall.closeGroup(this.sessionCallback); // send REQ (no body content)
		SCMPMessage res = this.sessionCallback.getMessageSync();

		Assert.assertEquals(sb.toString(), res.getBody());
		Assert.assertEquals(SCMPBodyType.TEXT.getValue(), res.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertEquals(sb.length() + "", res.getBodyLength() + "");
		Assert.assertEquals(SCMPMsgType.CLN_ECHO.getValue(), res.getMessageType());
		Assert.assertNotNull(res.getSessionId());
	}

	@Test
	public void groupCallLargePartsTest() throws Exception {
		SCMPClnEchoCall echoCall = (SCMPClnEchoCall) SCMPCallFactory.CLN_ECHO_CALL.newInstance(req, "simulation",
				this.sessionId);

		ISCMPCall groupCall = echoCall.openGroup();
		StringBuilder sb = new StringBuilder();
		StringBuilder expected = new StringBuilder();
		for (int i = 0; i < 19000; i++) {
			sb.append(i);
		}
		int max = 1;
		for (int i = 0; i < max; i++) {
			expected.append(sb.toString());
			groupCall.setRequestBody(sb.toString());
			groupCall.invoke(this.sessionCallback);
			this.sessionCallback.getMessageSync();
		}
		groupCall.closeGroup(this.sessionCallback); // send REQ (no body content)
		SCMPMessage res = this.sessionCallback.getMessageSync();
		Assert.assertEquals(expected.toString(), res.getBody());
		Assert.assertEquals(SCMPBodyType.TEXT.getValue(), res.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertEquals(expected.length() + "", res.getBodyLength() + "");
		Assert.assertEquals(SCMPMsgType.CLN_ECHO.getValue(), res.getMessageType());
		Assert.assertNotNull(res.getSessionId());
	}
}
