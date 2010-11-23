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
package org.serviceconnector.test.sc.compression;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnExecuteCall;
import org.serviceconnector.scmp.SCMPBodyType;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.test.sc.session.SuperSessionTestCase;
import org.serviceconnector.util.SynchronousCallback;
import org.serviceconnetor.TestConstants;

/**
 * @author JTraber
 */
public class MsgCompressionTestCase extends SuperSessionTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public MsgCompressionTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void msgCompressionBodyStringTest() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req, "session-1",
				this.sessionId);
		clnExecuteCall.setMessagInfo("message info");
		clnExecuteCall.setRequestBody("reflect " + TestConstants.stringLength257);
		clnExecuteCall.setCompressed(true);
		TestCompressionCallback callback = new TestCompressionCallback(true);
		clnExecuteCall.invoke(callback, 100000);
		SCMPMessage scmpReply = callback.getMessageSync(100000);
		Assert.assertEquals("reflect " + TestConstants.stringLength257, scmpReply.getBody());
		Assert.assertEquals(SCMPBodyType.TEXT.getValue(), scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
	}

	@Test
	public void largeMsgCompressionBodyStringTest() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req, "session-1",
				this.sessionId);
		clnExecuteCall.setMessagInfo("message info");
		StringBuilder sb = new StringBuilder();
		sb.append("reflect");
		while (sb.length() < (64 << 10)) {
			sb.append(TestConstants.stringLength257);
		}
		clnExecuteCall.setRequestBody(sb.toString());
		clnExecuteCall.setCompressed(true);
		TestCompressionCallback callback = new TestCompressionCallback(true);
		clnExecuteCall.invoke(callback, 100000);
		SCMPMessage scmpReply = callback.getMessageSync(100000);
		Assert.assertEquals(sb.length(), ((String) scmpReply.getBody()).length());
		Assert.assertEquals(sb.toString(), scmpReply.getBody());
		Assert.assertEquals(SCMPBodyType.TEXT.getValue(), scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
	}

	@Test
	public void msgCompressionBodyByteTest() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req, "session-1",
				this.sessionId);
		clnExecuteCall.setMessagInfo("message info");

		byte[] reflect = "reflect".getBytes();
		byte[] buffer = Arrays.copyOf(reflect, 1024);
		for (int i = reflect.length; i < 1024 - reflect.length; i++) {
			buffer[i] = (byte) i;
		}
		clnExecuteCall.setRequestBody(buffer);
		clnExecuteCall.setCompressed(true);
		TestCompressionCallback callback = new TestCompressionCallback(true);
		clnExecuteCall.invoke(callback, 100000);
		SCMPMessage scmpReply = callback.getMessageSync(100000);
		Assert.assertEquals(new String(buffer), new String((byte[]) scmpReply.getBody()));
	}

	@Test
	public void largeMsgCompressionBodyByteTest() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req, "session-1",
				this.sessionId);
		clnExecuteCall.setMessagInfo("message info");

		byte[] reflect = "reflect".getBytes();
		byte[] buffer = Arrays.copyOf(reflect, (65 << 10));
		for (int i = reflect.length; i < (65 << 10) - reflect.length; i++) {
			buffer[i] = (byte) i;
		}
		clnExecuteCall.setRequestBody(buffer);
		clnExecuteCall.setCompressed(true);
		TestCompressionCallback callback = new TestCompressionCallback(true);
		clnExecuteCall.invoke(callback, 100000);
		SCMPMessage scmpReply = callback.getMessageSync(100000);
		Assert.assertEquals(new String(buffer), new String((byte[]) scmpReply.getBody()));
	}

	@Test
	public void noMsgCompressionBodyStreamTest() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req, "session-1",
				this.sessionId);
		clnExecuteCall.setMessagInfo("message info");

		byte[] reflect = "reflect".getBytes();
		byte[] buffer = Arrays.copyOf(reflect, 1024);
		for (int i = reflect.length; i < 1024 - reflect.length; i++) {
			buffer[i] = (byte) i;
		}
		ByteArrayInputStream in = new ByteArrayInputStream(buffer);
		clnExecuteCall.setRequestBody(in);
		clnExecuteCall.setCompressed(false);
		TestCompressionCallback callback = new TestCompressionCallback(true);
		clnExecuteCall.invoke(callback, 1000);
		SCMPMessage scmpReply = callback.getMessageSync();
		Assert.assertEquals(new String(buffer), new String((byte[]) scmpReply.getBody()));
	}

	@Test
	public void msgCompressionBodyStreamTest() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req, "session-1",
				this.sessionId);
		clnExecuteCall.setMessagInfo("message info");

		byte[] reflect = "reflect".getBytes();
		byte[] buffer = Arrays.copyOf(reflect, 1024);
		for (int i = reflect.length; i < 1024 - reflect.length; i++) {
			buffer[i] = (byte) i;
		}
		ByteArrayInputStream in = new ByteArrayInputStream(buffer);
		clnExecuteCall.setRequestBody(in);
		clnExecuteCall.setCompressed(true);
		TestCompressionCallback callback = new TestCompressionCallback(true);
		clnExecuteCall.invoke(callback, 10000);
		SCMPMessage scmpReply = callback.getMessageSync(10000);
		Assert.assertEquals(buffer.length, scmpReply.getBodyLength());
		Assert.assertEquals(new String(buffer), new String((byte[]) scmpReply.getBody()));
	}

	protected class TestCompressionCallback extends SynchronousCallback {
		public TestCompressionCallback(boolean synchronous) {
			this.synchronous = synchronous;
		}
	}
}