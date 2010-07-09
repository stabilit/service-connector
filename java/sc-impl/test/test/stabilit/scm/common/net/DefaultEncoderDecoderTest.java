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
package test.stabilit.scm.common.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import test.stabilit.scm.test.SCImplTest;

import com.stabilit.scm.common.net.EncoderDecoderFactory;
import com.stabilit.scm.common.net.IEncoderDecoder;
import com.stabilit.scm.common.scmp.SCMPBodyType;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPHeadlineKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;

/**
 * The Class DefaultEncoderDecoderTest.
 */
public class DefaultEncoderDecoderTest {

	/** The coder factory. */
	private EncoderDecoderFactory coderFactory = EncoderDecoderFactory.getCurrentEncoderDecoderFactory();
	/** The head key. */
	private SCMPHeadlineKey headKey;
	/** The msg type. */
	private SCMPMsgType msgType;
	/** The body type. */
	private SCMPBodyType bodyType;
	/** The msg id. */
	private String msgID;
	/** The body length. */
	private String bodyLength;
	/** The body. */
	private String body;
	/** The encode scmp. */
	private SCMPMessage encodeScmp;

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		this.headKey = SCMPHeadlineKey.REQ;
		this.msgType = SCMPMsgType.ECHO_SC;
		this.bodyType = SCMPBodyType.binary;
		this.msgID = "1";
		this.bodyLength = "12";
		this.body = "hello world!";

		encodeScmp = new SCMPMessage();
		encodeScmp.setHeader(SCMPHeaderAttributeKey.MSG_TYPE, msgType.getName());
		encodeScmp.setHeader(SCMPHeaderAttributeKey.BODY_TYPE, bodyType.getName());
		encodeScmp.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID);
		encodeScmp.setBody(body.getBytes());
	}

	/**
	 * Decode REQ test.
	 */
	@Test
	public void decodeREQTest() {
		String header = "bty=" + bodyType.getName() + "\n" + "mid=" + msgID + "\n" + "bodyLength=" + bodyLength + "\n"
				+ "mty=" + msgType.getName() + "\n";

		String requestString = SCImplTest.getSCMPString(headKey, header, body);
		byte[] buffer = requestString.getBytes();
		InputStream is = new ByteArrayInputStream(buffer);
		IEncoderDecoder coder = coderFactory.newInstance(buffer);

		SCMPMessage message = null;
		try {
			message = (SCMPMessage) coder.decode(is);
		} catch (Exception e) {
			Assert.fail("Should not throw exception");
		}
		verifySCMP(message);
	}

	/**
	 * Decode RES test.
	 */
	@Test
	public void decodeRESTest() {
		headKey = SCMPHeadlineKey.RES;

		String header = "bty=" + bodyType.getName() + "\n" + "mid=" + msgID + "\n" + "bodyLength=" + bodyLength + "\n"
				+ "mty=" + msgType.getName() + "\n";

		String requestString = SCImplTest.getSCMPString(headKey, header, body);

		byte[] buffer = requestString.getBytes();
		InputStream is = new ByteArrayInputStream(buffer);
		IEncoderDecoder coder = coderFactory.newInstance(buffer);

		SCMPMessage message = null;
		try {
			message = (SCMPMessage) coder.decode(is);
		} catch (Exception e) {
			Assert.fail("Should not throw exception");
		}
		verifySCMP(message);
	}

	/**
	 * Decode exc test.
	 */
	@Test
	public void decodeEXCTest() {
		headKey = SCMPHeadlineKey.EXC;

		String header = "bty=" + bodyType.getName() + "\n" + "mid=" + msgID + "\n" + "bodyLength=" + bodyLength + "\n"
				+ "mty=" + msgType.getName() + "\n";

		String requestString = SCImplTest.getSCMPString(headKey, header, body);

		byte[] buffer = requestString.getBytes();
		InputStream is = new ByteArrayInputStream(buffer);
		IEncoderDecoder coder = coderFactory.newInstance(buffer);

		SCMPMessage message = null;
		try {
			message = (SCMPMessage) coder.decode(is);
		} catch (Exception e) {
			Assert.fail("Should not throw exception");
		}
		if (message.isFault() == false) {
			Assert.fail("scmp should be of type fault");
		}
		verifySCMP(message);
	}

	/**
	 * Decode undef test.
	 */
	@Test
	public void decodeUNDEFTest() {
		String requestString = "garbage /s=69& SCMP/1.0\n" + "bty=" + bodyType.getName() + "\n" + "mid=" + msgID + "\n"
				+ "bodyLength=" + bodyLength + "\n" + "mty=" + msgType.getName() + "\n\n" + body + "\n";

		byte[] buffer = requestString.getBytes();
		InputStream is = new ByteArrayInputStream(buffer);
		IEncoderDecoder coder = coderFactory.newInstance(buffer);

		try {
			coder.decode(is);
			Assert.fail("Should throw exception");
		} catch (Exception e) {
			Assert.assertEquals("wrong protocol in message not possible to decode", e.getMessage());
		}
	}

	/**
	 * Decode body types test.
	 */
	@Test
	public void decodeBodyTypesTest() {
		String header = "bty=" + bodyType.getName() + "\n" + "mid=" + msgID + "\n" + "bodyLength=" + bodyLength + "\n"
				+ "mty=" + msgType.getName() + "\n";

		String requestString = SCImplTest.getSCMPString(headKey, header, body);

		byte[] buffer = requestString.getBytes();
		InputStream is = new ByteArrayInputStream(buffer);
		IEncoderDecoder coder = coderFactory.newInstance(buffer);

		SCMPMessage message = null;
		try {
			message = (SCMPMessage) coder.decode(is);
		} catch (Exception e) {
			Assert.fail("Should not throw exception");
		}
		verifySCMP(message);

		bodyType = SCMPBodyType.text;
		header = "bty=" + bodyType.getName() + "\n" + "mid=" + msgID + "\n" + "bodyLength=" + bodyLength + "\n"
				+ "mty=" + msgType.getName() + "\n";

		requestString = SCImplTest.getSCMPString(headKey, header, body);

		buffer = requestString.getBytes();
		is = new ByteArrayInputStream(buffer);
		coder = coderFactory.newInstance(buffer);

		message = null;
		try {
			message = (SCMPMessage) coder.decode(is);
		} catch (Exception e) {
			Assert.fail("Should not throw exception");
		}
		verifySCMPStringBody(message);
	}

	/**
	 * Encode REQ test.
	 */
	@Test
	public void encodeREQTest() {
		IEncoderDecoder coder = coderFactory.newInstance(encodeScmp);

		String header = "bodyLength=" + bodyLength + "\n" + "mid=" + msgID + "\n" + "bty=" + bodyType.getName() + "\n"
				+ "mty=" + msgType.getName() + "\n";

		String expectedString = SCImplTest.getSCMPString(headKey, header, body);

		OutputStream os = new ByteArrayOutputStream();
		try {
			coder.encode(os, encodeScmp);
		} catch (Exception e) {
			Assert.fail("Should not throw exception");
		}
		Assert.assertEquals(expectedString, os.toString());
	}

	/**
	 * Encode RES test.
	 */
	@Test
	public void encodeRESTest() {
		IEncoderDecoder coder = coderFactory.newInstance(encodeScmp);

		this.headKey = SCMPHeadlineKey.RES;
		String header = "bodyLength=" + bodyLength + "\n" + "mid=" + msgID + "\n" + "bty=" + bodyType.getName() + "\n"
				+ "mty=" + msgType.getName() + "\n";

		String expectedString = SCImplTest.getSCMPString(headKey, header, body);

		SCMPMessage encodeRes = new SCMPMessage();
		encodeRes.setIsReply(true);
		encodeRes.setHeader(encodeScmp);
		encodeRes.setBody(body.getBytes());

		OutputStream os = new ByteArrayOutputStream();
		try {
			coder.encode(os, encodeRes);
		} catch (Exception e) {
			Assert.fail("Should not throw exception");
		}
		Assert.assertEquals(expectedString, os.toString());
	}

	/**
	 * Encode EXC test.
	 */
	@Test
	public void encodeEXCTest() {
		IEncoderDecoder coder = coderFactory.newInstance(encodeScmp);

		this.headKey = SCMPHeadlineKey.EXC;
		String header = "bodyLength=" + bodyLength + "\n" + "mid=" + msgID + "\n" + "bty=" + bodyType.getName() + "\n"
				+ "mty=" + msgType.getName() + "\n";

		String expectedString = SCImplTest.getSCMPString(headKey, header, body);

		SCMPMessage encodeExc = new SCMPFault();
		encodeExc.setHeader(encodeScmp);
		encodeExc.setBody(body.getBytes());

		OutputStream os = new ByteArrayOutputStream();
		try {
			coder.encode(os, encodeExc);
		} catch (Exception e) {
			Assert.fail("Should not throw exception");
		}
		Assert.assertEquals(expectedString, os.toString());
	}

	/**
	 * Encode body types test.
	 */
	@Test
	public void encodeBodyTypesTest() {
		IEncoderDecoder coder = coderFactory.newInstance(encodeScmp);

		String header = "bodyLength=" + bodyLength + "\n" + "mid=" + msgID + "\n" + "bty=" + bodyType.getName() + "\n"
				+ "mty=" + msgType.getName() + "\n";

		String expectedString = SCImplTest.getSCMPString(headKey, header, body);

		OutputStream os = new ByteArrayOutputStream();
		try {
			coder.encode(os, encodeScmp);
		} catch (Exception e) {
			Assert.fail("Should not throw exception");
		}
		Assert.assertEquals(expectedString, os.toString());

		coder = coderFactory.newInstance(encodeScmp);
		bodyType = SCMPBodyType.text;
		encodeScmp.setHeader(SCMPHeaderAttributeKey.BODY_TYPE, bodyType.getName());

		header = "bodyLength=" + bodyLength + "\n" + "mid=" + msgID + "\n" + "bty=" + bodyType.getName() + "\n"
				+ "mty=" + msgType.getName() + "\n";

		expectedString = SCImplTest.getSCMPString(headKey, header, body);

		os = new ByteArrayOutputStream();
		try {
			coder.encode(os, encodeScmp);
		} catch (Exception e) {
			Assert.fail("Should not throw exception");
		}
		Assert.assertEquals(expectedString, os.toString());
	}

	/**
	 * Verify scmp string body.
	 * 
	 * @param scmp
	 *            the scmp
	 */
	private void verifySCMPStringBody(SCMPMessage scmp) {
		Assert.assertEquals(bodyType.getName(), scmp.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		// Assert.assertEquals(msgID, scmp.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		Assert.assertEquals(bodyLength, scmp.getHeader(SCMPHeaderAttributeKey.BODY_LENGTH));
		Assert.assertEquals(msgType.getName(), scmp.getHeader(SCMPHeaderAttributeKey.MSG_TYPE));
		Assert.assertEquals(body, scmp.getBody());
	}

	/**
	 * Verify scmp.
	 * 
	 * @param scmp
	 *            the scmp
	 */
	private void verifySCMP(SCMPMessage scmp) {
		Assert.assertEquals(bodyType.getName(), scmp.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		// Assert.assertEquals(msgID, scmp.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		Assert.assertEquals(bodyLength, scmp.getHeader(SCMPHeaderAttributeKey.BODY_LENGTH));
		Assert.assertEquals(msgType.getName(), scmp.getHeader(SCMPHeaderAttributeKey.MSG_TYPE));
		Assert.assertEquals(body, new String((byte[]) scmp.getBody()));
	}
}
