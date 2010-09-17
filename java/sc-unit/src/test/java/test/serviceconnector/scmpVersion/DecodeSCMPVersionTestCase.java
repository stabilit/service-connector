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
package test.serviceconnector.scmpVersion;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.common.net.EncoderDecoderFactory;
import org.serviceconnector.common.net.IEncoderDecoder;
import org.serviceconnector.common.scmp.internal.SCMPPart;


public class DecodeSCMPVersionTestCase {

	/** The coder factory. */
	private EncoderDecoderFactory coderFactory = EncoderDecoderFactory.getCurrentEncoderDecoderFactory();

	@Test
	public void invalidSCMPVersionFormatTest() {
		String requestString = "REQ 0000053 00053 xxx\nldt=2010-08-02T11:24:52.093+0200\nver=1.0-000\nmty=ATT";

		byte[] buffer = requestString.getBytes();
		InputStream is = new ByteArrayInputStream(buffer);
		IEncoderDecoder coder = coderFactory.newInstance(new SCMPPart());

		try {
			coder.decode(is);
			Assert.fail("Should throw exception");
		} catch (Exception e) {
			Assert.assertEquals("Invalid scmp release nr. [xxx]", e.getMessage());
		}
	}

	@Test
	public void invalidReleaseNumberTest() {
		String requestString = "REQ 0000053 00053 2.0\nldt=2010-08-02T11:24:52.093+0200\nver=1.0-000\nmty=ATT";
		byte[] buffer = requestString.getBytes();
		InputStream is = new ByteArrayInputStream(buffer);
		IEncoderDecoder coder = coderFactory.newInstance(new SCMPPart());

		try {
			coder.decode(is);
			Assert.fail("Should throw exception");
		} catch (Exception e) {
			Assert.assertEquals("Invalid scmp release nr. [2.0]", e.getMessage());
		}
	}

	@Test
	public void invalidSCMPVersion1_1Test() {
		String requestString = "REQ 0000053 00053 1.1\nldt=2010-08-02T11:24:52.093+0200\nver=1.0-000\nmty=ATT";

		byte[] buffer = requestString.getBytes();
		InputStream is = new ByteArrayInputStream(buffer);
		IEncoderDecoder coder = coderFactory.newInstance(new SCMPPart());

		try {
			coder.decode(is);
			Assert.fail("Should throw exception");
		} catch (Exception e) {
			Assert.assertEquals("Invalid scmp version nr. [1.1]", e.getMessage());
		}
	}

	@Test
	public void invalidSCMPVersion0_9Test() {
		String requestString = "REQ 0000053 00053 0.9\nldt=2010-08-02T11:24:52.093+0200\nver=1.0-000\nmty=ATT";

		byte[] buffer = requestString.getBytes();
		InputStream is = new ByteArrayInputStream(buffer);
		IEncoderDecoder coder = coderFactory.newInstance(new SCMPPart());

		try {
			coder.decode(is);
			Assert.fail("Should throw exception");
		} catch (Exception e) {
			Assert.assertEquals("Invalid scmp release nr. [0.9]", e.getMessage());
		}
	}

	@Test
	public void validSCMPVersionTest() {
		String requestString = "REQ 0000053 00053 1.0\nldt=2010-08-02T11:24:52.093+0200\nver=1.0-000\nmty=ATT";

		byte[] buffer = requestString.getBytes();
		InputStream is = new ByteArrayInputStream(buffer);
		IEncoderDecoder coder = coderFactory.newInstance(new SCMPPart());

		try {
			coder.decode(is);
		} catch (Exception e) {
			Assert.fail("Should not throw exception");
		}
	}
}
