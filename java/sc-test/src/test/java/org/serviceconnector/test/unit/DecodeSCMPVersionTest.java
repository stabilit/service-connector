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
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.FlyweightEncoderDecoderFactory;
import org.serviceconnector.net.IEncoderDecoder;
import org.serviceconnector.scmp.SCMPPart;
import org.serviceconnector.scmp.SCMPVersion;

public class DecodeSCMPVersionTest extends SuperUnitTest {

	/** The coder factory. */
	private FlyweightEncoderDecoderFactory coderFactory = AppContext.getEncoderDecoderFactory();

	/**
	 * Description: Decode invalid SCMP version format test<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_InvalidSCMPVersionFormatTest() {
		String requestString = "REQ 0000053 00053 xxx\nldt=2010-08-02T11:24:52.093+0200\nver=1.0-000\nmty=ATT";

		byte[] buffer = requestString.getBytes();
		InputStream is = new ByteArrayInputStream(buffer);
		IEncoderDecoder coder = coderFactory.createEncoderDecoder(new SCMPPart(SCMPVersion.CURRENT));

		try {
			coder.decode(is);
			Assert.fail("Should throw exception");
		} catch (Exception e) {
			Assert.assertEquals("Incompatible SCMP release nr. [xxx]", e.getMessage());
		}
	}

	/**
	 * Description: Decode invalid SCMP version format test<br>
	 * Expectation: passes
	 */
	@Test
	public void t02_InvalidSCMPVersion9_9Test() {
		String requestString = "REQ 0000053 00053 9.9\nldt=2010-08-02T11:24:52.093+0200\nver=1.0-000\nmty=ATT";

		byte[] buffer = requestString.getBytes();
		InputStream is = new ByteArrayInputStream(buffer);
		IEncoderDecoder coder = coderFactory.createEncoderDecoder(new SCMPPart(SCMPVersion.CURRENT));

		try {
			coder.decode(is);
			Assert.fail("Should throw exception");
		} catch (Exception e) {
			Assert.assertEquals("Incompatible SCMP release nr. [9.9]", e.getMessage());
		}
	}

	/**
	 * Description: Decode invalid SCMP version format test<br>
	 * Expectation: passes
	 */
	@Test
	public void t03_InvalidSCMPVersion0_9Test() {
		String requestString = "REQ 0000053 00053 0.9\nldt=2010-08-02T11:24:52.093+0200\nver=1.0-000\nmty=ATT";

		byte[] buffer = requestString.getBytes();
		InputStream is = new ByteArrayInputStream(buffer);
		IEncoderDecoder coder = coderFactory.createEncoderDecoder(new SCMPPart(SCMPVersion.CURRENT));

		try {
			coder.decode(is);
			Assert.fail("Should throw exception");
		} catch (Exception e) {
			Assert.assertEquals("Incompatible SCMP release nr. [0.9]", e.getMessage());
		}
	}

	/**
	 * Description: Decode valid SCMP version format test<br>
	 * Expectation: passes
	 */
	@Test
	public void t03_ValidSCMPVersionTest() {
		String requestString = "REQ 0000052 00052 1.0\nldt=2010-08-02T11:24:52.093+0200\nver=1.0-000\nmty=ATT";

		byte[] buffer = requestString.getBytes();
		InputStream is = new ByteArrayInputStream(buffer);
		IEncoderDecoder coder = coderFactory.createEncoderDecoder(new SCMPPart(SCMPVersion.CURRENT));

		try {
			coder.decode(is);
		} catch (Exception e) {
			Assert.fail("Should not throw exception");
		}
	}

	/**
	 * Description: Decode invalid release number test<br>
	 * Expectation: passes
	 */
	@Test
	public void t10_InvalidReleaseNumberTest() {
		String requestString = "REQ 0000053 00053 2.0\nldt=2010-08-02T11:24:52.093+0200\nver=1.0-000\nmty=ATT";
		byte[] buffer = requestString.getBytes();
		InputStream is = new ByteArrayInputStream(buffer);
		IEncoderDecoder coder = coderFactory.createEncoderDecoder(new SCMPPart(SCMPVersion.CURRENT));

		try {
			coder.decode(is);
			Assert.fail("Should throw exception");
		} catch (Exception e) {
			Assert.assertEquals("Incompatible SCMP release nr. [2.0]", e.getMessage());
		}
	}
}