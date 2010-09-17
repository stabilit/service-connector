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
package test.serviceconnector.group;

import junit.framework.Assert;

import org.junit.Test;
import org.serviceconnector.common.call.SCMPCallFactory;
import org.serviceconnector.common.call.SCMPClnExecuteCall;
import org.serviceconnector.common.scmp.SCMPBodyType;
import org.serviceconnector.common.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.common.scmp.SCMPMessage;
import org.serviceconnector.common.scmp.SCMPMsgType;
import org.serviceconnector.sc.cln.call.ISCMPCall;

import test.serviceconnector.session.SuperSessionTestCase;


public class GroupCallTestCase extends SuperSessionTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public GroupCallTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void groupCallTest() throws Exception {
		SCMPClnExecuteCall executeCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req,
				"simulation", this.sessionId);
		ISCMPCall groupCall = executeCall.openGroup();
		StringBuilder sb = new StringBuilder();

		sb.append("reflect: ");
		groupCall.setRequestBody(sb.toString());
		groupCall.invoke(this.sessionCallback, 1000);
		this.sessionCallback.getMessageSync();

		for (int i = 0; i < 10; i++) {
			sb.append(i);
			groupCall.setRequestBody(String.valueOf(i));
			groupCall.invoke(this.sessionCallback, 1000);
			this.sessionCallback.getMessageSync();
		}
		groupCall.closeGroup(this.sessionCallback, 1000); // send REQ (no body content)
		SCMPMessage res = this.sessionCallback.getMessageSync();

		Assert.assertEquals(sb.toString(), res.getBody());
		Assert.assertEquals(SCMPBodyType.TEXT.getValue(), res.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertEquals(sb.length() + "", res.getBodyLength() + "");
		Assert.assertEquals(SCMPMsgType.CLN_EXECUTE.getValue(), res.getMessageType());
	}

	@Test
	public void groupCallLargePartsTest() throws Exception {
		SCMPClnExecuteCall executeCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req,
				"simulation", this.sessionId);

		ISCMPCall groupCall = executeCall.openGroup();
		StringBuilder sb = new StringBuilder();
		StringBuilder expected = new StringBuilder();
		sb.append("reflect: ");
		for (int i = 0; i < 19000; i++) {
			sb.append(i);
		}

		expected.append(sb.toString());
		groupCall.setRequestBody(sb.toString());
		groupCall.invoke(this.sessionCallback, 1000);
		this.sessionCallback.getMessageSync();

		expected.append("end");
		groupCall.setRequestBody("end");
		groupCall.invoke(this.sessionCallback, 1000);
		this.sessionCallback.getMessageSync();

		groupCall.closeGroup(this.sessionCallback, 1000); // send REQ (no body content)
		SCMPMessage res = this.sessionCallback.getMessageSync();

		Assert.assertEquals(expected.length() + "", res.getBodyLength() + "");
		Assert.assertEquals(expected.toString(), res.getBody());
		Assert.assertEquals(SCMPMsgType.CLN_EXECUTE.getValue(), res.getMessageType());
	}
}
