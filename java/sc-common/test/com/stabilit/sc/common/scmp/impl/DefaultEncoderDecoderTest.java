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
package com.stabilit.sc.common.scmp.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.stabilit.sc.common.scmp.SCMP;
import com.stabilit.sc.common.scmp.SCMPBodyType;
import com.stabilit.sc.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.scmp.SCMPHeadlineKey;
import com.stabilit.sc.common.scmp.SCMPMsgType;

public class DefaultEncoderDecoderTest {

	private EncoderDecoderFactory coderFactory = EncoderDecoderFactory.getCurrentEncoderDecoderFactory();
	private SCMPHeadlineKey headKey;
	private SCMPMsgType msgType;
	private SCMPBodyType bodyType;
	private String msgID;
	private String bodyLength;
	private String body;

	@Before
	public void setUp() {
		this.headKey = SCMPHeadlineKey.REQ;
		this.msgType = SCMPMsgType.ECHO_SC;
		this.bodyType = SCMPBodyType.text;
		this.msgID = "1";
		this.bodyLength = "12";
		this.body = "hello world!";
	}

	@Test
	public void decodeREQTest() {
		String requestString = headKey.name() + " /s=69& SCMP/1.0\n" + "bodyType=" + bodyType.getName()
				+ "\n" + "messageID=" + msgID + "\n" + "bodyLength=" + bodyLength + "\n" + "msgType="
				+ msgType.getRequestName() + "\n\n" + body + "\n";

		byte[] buffer = requestString.getBytes();
		InputStream is = new ByteArrayInputStream(buffer);
		IEncoderDecoder coder = coderFactory.newInstance(buffer);

		SCMP scmp = null;
		try {
			scmp = (SCMP) coder.decode(is);
		} catch (EncodingDecodingException e) {
			Assert.fail("Should not throw exception");
		}
		verifySCMP(scmp);
	}

	@Test
	public void decodeRESTest() {
		headKey = SCMPHeadlineKey.RES;
		String requestString = headKey.name() + " /s=69& SCMP/1.0\n" + "bodyType=" + bodyType.getName()
				+ "\n" + "messageID=" + msgID + "\n" + "bodyLength=" + bodyLength + "\n" + "msgType="
				+ msgType.getRequestName() + "\n\n" + body + "\n";

		byte[] buffer = requestString.getBytes();
		InputStream is = new ByteArrayInputStream(buffer);
		IEncoderDecoder coder = coderFactory.newInstance(buffer);

		SCMP scmp = null;
		try {
			scmp = (SCMP) coder.decode(is);
		} catch (EncodingDecodingException e) {
			Assert.fail("Should not throw exception");
		}
		verifySCMP(scmp);
	}

	@Test
	public void decodeEXCTest() {
		headKey = SCMPHeadlineKey.EXC;
		String requestString = headKey.name() + " /s=69& SCMP/1.0\n" + "bodyType=" + bodyType.getName()
				+ "\n" + "messageID=" + msgID + "\n" + "bodyLength=" + bodyLength + "\n" + "msgType="
				+ msgType.getRequestName() + "\n\n" + body + "\n";

		byte[] buffer = requestString.getBytes();
		InputStream is = new ByteArrayInputStream(buffer);
		IEncoderDecoder coder = coderFactory.newInstance(buffer);

		SCMP scmp = null;
		try {
			scmp = (SCMP) coder.decode(is);
		} catch (EncodingDecodingException e) {
			Assert.fail("Should not throw exception");
		}
		if (scmp.isFault() == false)
			Assert.fail("scmp should be of type fault");
		verifySCMP(scmp);
	}

	@Test
	public void decodeUNDEFTest() {
		String requestString = "garbage /s=69& SCMP/1.0\n" + "bodyType=" + bodyType.getName() + "\n"
				+ "messageID=" + msgID + "\n" + "bodyLength=" + bodyLength + "\n" + "msgType="
				+ msgType.getRequestName() + "\n\n" + body + "\n";

		byte[] buffer = requestString.getBytes();
		InputStream is = new ByteArrayInputStream(buffer);
		IEncoderDecoder coder = coderFactory.newInstance(buffer);

		try {
			coder.decode(is);
			Assert.fail("Should throw exception");
		} catch (EncodingDecodingException e) {
		}
	}

	@Test
	public void decodeBodyTypesTest() {
		String requestString = headKey.name() + " /s=69& SCMP/1.0\n" + "bodyType=" + bodyType.getName()
				+ "\n" + "messageID=" + msgID + "\n" + "bodyLength=" + bodyLength + "\n" + "msgType="
				+ msgType.getRequestName() + "\n\n" + body + "\n";

		byte[] buffer = requestString.getBytes();
		InputStream is = new ByteArrayInputStream(buffer);
		IEncoderDecoder coder = coderFactory.newInstance(buffer);

		SCMP scmp = null;
		try {
			scmp = (SCMP) coder.decode(is);
		} catch (EncodingDecodingException e) {
			Assert.fail("Should not throw exception");
		}
		verifySCMP(scmp);

		bodyType = SCMPBodyType.binary;
		requestString = headKey.name() + " /s=69& SCMP/1.0\n" + "bodyType=" + bodyType.getName() + "\n"
				+ "messageID=" + msgID + "\n" + "bodyLength=" + bodyLength + "\n" + "msgType="
				+ msgType.getRequestName() + "\n\n" + body + "\n";

		buffer = requestString.getBytes();
		is = new ByteArrayInputStream(buffer);
		coder = coderFactory.newInstance(buffer);

		scmp = null;
		try {
			scmp = (SCMP) coder.decode(is);
		} catch (EncodingDecodingException e) {
			Assert.fail("Should not throw exception");
		}
		verifySCMPBinaryBody(scmp);
	}

	private void verifySCMP(SCMP scmp) {
		Assert.assertEquals(bodyType.getName(), scmp.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertEquals(msgID, scmp.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		Assert.assertEquals(bodyLength, scmp.getHeader(SCMPHeaderAttributeKey.BODY_LENGTH));
		Assert.assertEquals(msgType.getRequestName(), scmp.getHeader(SCMPHeaderAttributeKey.MSG_TYPE));
		Assert.assertEquals(body, scmp.getBody());
	}

	private void verifySCMPBinaryBody(SCMP scmp) {
		Assert.assertEquals(bodyType.getName(), scmp.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertEquals(msgID, scmp.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		Assert.assertEquals(bodyLength, scmp.getHeader(SCMPHeaderAttributeKey.BODY_LENGTH));
		Assert.assertEquals(msgType.getRequestName(), scmp.getHeader(SCMPHeaderAttributeKey.MSG_TYPE));
		Assert.assertEquals(body, new String((byte[]) scmp.getBody()));
	}
}
