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
package org.serviceconnector.test.sc.group;

import junit.framework.Assert;

import org.junit.Test;
import org.serviceconnector.call.ISCMPCall;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnExecuteCall;
import org.serviceconnector.scmp.SCMPBodyType;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.sc.session.SuperSessionTestCase;
import org.serviceconnector.util.SynchronousCallback;

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
		SCMPClnExecuteCall executeCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req, "session-1",
				this.sessionId);
		ISCMPCall groupCall = executeCall.openGroup();
		StringBuilder sb = new StringBuilder();

		sb.append("reflect: ");
		groupCall.setRequestBody(sb.toString());
		TestGroupCallCallback callback = new TestGroupCallCallback(true);
		groupCall.invoke(callback, 1000);
		SCMPMessage message = callback.getMessageSync(100);
		Assert.assertNotNull(message.getMessageSequenceNr());
		int currentMsgSequenceNr = Integer.parseInt(message.getMessageSequenceNr());

		for (int i = 0; i < 10; i++) {
			sb.append(i);
			groupCall.setRequestBody(String.valueOf(i));
			callback = new TestGroupCallCallback(true);
			groupCall.invoke(callback, 1000);
			message = callback.getMessageSync(100);
			Assert.assertEquals(++currentMsgSequenceNr + "", message.getMessageSequenceNr());
		}
		callback = new TestGroupCallCallback(true);
		groupCall.closeGroup(callback, 1000); // send REQ (no body content)
		SCMPMessage res = callback.getMessageSync(100);

		Assert.assertEquals(++currentMsgSequenceNr + "", res.getMessageSequenceNr());
		Assert.assertEquals(sb.toString(), res.getBody());
		Assert.assertNotNull(res.getMessageSequenceNr());
		Assert.assertEquals(SCMPBodyType.TEXT.getValue(), res.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertEquals(sb.length() + "", res.getBodyLength() + "");
		Assert.assertEquals(SCMPMsgType.CLN_EXECUTE.getValue(), res.getMessageType());
	}

	@Test
	public void groupCallLargePartsTest() throws Exception {
		SCMPClnExecuteCall executeCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req, "session-1",
				this.sessionId);

		ISCMPCall groupCall = executeCall.openGroup();
		StringBuilder sb = new StringBuilder();
		StringBuilder expected = new StringBuilder();
		sb.append("reflect: ");
		for (int i = 0; i < 19000; i++) {
			sb.append(i);
		}

		expected.append(sb.toString());
		groupCall.setRequestBody(sb.toString());
		TestGroupCallCallback callback = new TestGroupCallCallback(true);
		groupCall.invoke(callback, 1000);
		SCMPMessage message = callback.getMessageSync(100);
		Assert.assertNotNull(message.getMessageSequenceNr());
		int currentMsgSequenceNr = Integer.parseInt(message.getMessageSequenceNr());

		expected.append("end");
		groupCall.setRequestBody("end");
		callback = new TestGroupCallCallback(true);
		groupCall.invoke(callback, 1000);
		message = callback.getMessageSync(3000);
		Assert.assertEquals(++currentMsgSequenceNr + "", message.getMessageSequenceNr());

		callback = new TestGroupCallCallback(true);
		groupCall.closeGroup(callback, 1000); // send REQ (no body content)
		SCMPMessage res = callback.getMessageSync(3000);

		// currentMsgSequenceNr+2 because there is a PAC sent - large response
		Assert.assertEquals(currentMsgSequenceNr + 3 + "", res.getMessageSequenceNr());
		Assert.assertEquals(expected.length() + "", res.getBodyLength() + "");
		Assert.assertEquals(expected.toString(), res.getBody());
		Assert.assertEquals(SCMPMsgType.CLN_EXECUTE.getValue(), res.getMessageType());
	}

	private class TestGroupCallCallback extends SynchronousCallback {
		public TestGroupCallCallback(boolean synchronous) {
			this.synchronous = synchronous;
		}
	}
}
