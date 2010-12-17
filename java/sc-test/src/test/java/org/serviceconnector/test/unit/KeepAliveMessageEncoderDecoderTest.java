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
package org.serviceconnector.test.unit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestUtil;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.FlyweightEncoderDecoderFactory;
import org.serviceconnector.net.IEncoderDecoder;
import org.serviceconnector.scmp.SCMPHeadlineKey;
import org.serviceconnector.scmp.SCMPKeepAlive;
import org.serviceconnector.scmp.SCMPMessage;

/**
 * The Class LargeMessageEncoderDecoderTest.
 */
public class KeepAliveMessageEncoderDecoderTest {

	/** The coder factory. */
	private FlyweightEncoderDecoderFactory coderFactory = AppContext.getEncoderDecoderFactory();
	/** The head key. */
	private SCMPHeadlineKey headKey;
	/** The encode scmp. */
	private SCMPMessage encodeScmp;

	/**
	 * Description: Decode KRQ test<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_DecodeKRQTest() {
		this.headKey = SCMPHeadlineKey.KRQ;

		String requestString = TestUtil.getSCMPString(headKey, null, null);

		byte[] buffer = requestString.getBytes();
		InputStream is = new ByteArrayInputStream(buffer);
		IEncoderDecoder coder = coderFactory.createEncoderDecoder(new SCMPKeepAlive());

		SCMPMessage message = null;
		try {
			message = (SCMPMessage) coder.decode(is);
		} catch (Exception e) {
			Assert.fail("Should not throw exception");
		}
		verifySCMP(message);
	}

	/**
	 * Description: Decode KRS test<br>
	 * Expectation: passes
	 */
	@Test
	public void t02_DecodeKRSTest() {
		this.headKey = SCMPHeadlineKey.KRS;
		String requestString = headKey.name() + " 0000000 00000 1.0\n";

		byte[] buffer = requestString.getBytes();
		InputStream is = new ByteArrayInputStream(buffer);
		IEncoderDecoder coder = coderFactory.createEncoderDecoder(new SCMPKeepAlive());

		SCMPMessage message = null;
		try {
			message = (SCMPMessage) coder.decode(is);
		} catch (Exception e) {
			Assert.fail("Should not throw exception");
		}
		verifySCMP(message);
	}

	/**
	 * Description: Encode KRQ test<br>
	 * Expectation: passes
	 */
	@Test
	public void t10_EncodeKRQTest() {
		this.headKey = SCMPHeadlineKey.KRQ;
		this.encodeScmp = new SCMPKeepAlive();
		IEncoderDecoder coder = coderFactory.createEncoderDecoder(new SCMPKeepAlive());

		String expectedString = this.headKey.name() + " 0000000 00000 1.0\n";

		OutputStream os = new ByteArrayOutputStream();
		try {
			coder.encode(os, encodeScmp);
		} catch (Exception e) {
			Assert.fail("Should not throw exception");
		}
		Assert.assertEquals(expectedString, os.toString());
	}

	/**
	 * Description: Encode KRS test<br>
	 * Expectation: passes
	 */
	@Test
	public void t11_EncodeKRSTest() {
		this.headKey = SCMPHeadlineKey.KRS;
		this.encodeScmp = new SCMPKeepAlive();
		IEncoderDecoder coder = coderFactory.createEncoderDecoder(new SCMPKeepAlive());

		String expectedString = this.headKey.name() + " 0000000 00000 1.0\n";

		OutputStream os = new ByteArrayOutputStream();
		try {
			encodeScmp.setIsReply(true);
			coder.encode(os, encodeScmp);
		} catch (Exception e) {
			Assert.fail("Should not throw exception");
		}
		Assert.assertEquals(expectedString, os.toString());
	}

	private void verifySCMP(SCMPMessage scmp) {
		Assert.assertNull(scmp.getBody());
		Assert.assertEquals(SCMPKeepAlive.class, scmp.getClass());
	}
}
