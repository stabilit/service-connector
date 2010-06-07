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
package test.stabilit.sc.net;

import org.junit.Assert;
import org.junit.Test;

import com.stabilit.scm.common.net.FrameDecoderException;
import com.stabilit.scm.common.net.FrameDecoderFactory;
import com.stabilit.scm.factory.IFactoryable;
import com.stabilit.scm.net.IFrameDecoder;

/**
 * The Class DefaultFrameDecoderTest.
 * 
 * @author JTraber
 */
public class DefaultFrameDecoderTest {

	/** The decoder. */
	private IFrameDecoder decoder = FrameDecoderFactory.getDefaultFrameDecoder();

	/**
	 * Singelton test.
	 */
	@Test
	public void singeltonTest() {
		IFactoryable decoder2 = decoder.newInstance();
		Assert.assertEquals(decoder, decoder2);
	}

	/**
	 * Parses the frame size fail test.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void parseFrameSizeFailTest() throws Exception {
		try {
			int frameSize = decoder.parseFrameSize(null);
			Assert.assertEquals("0", frameSize + "");
			frameSize = decoder.parseFrameSize(new byte[0]);
			Assert.assertEquals("0", frameSize + "");
		} catch (FrameDecoderException e) {
			Assert.fail("Should not throw Exception!");
		}

		String headline = "REQ /=87& SCMP/1.0";
		try {
			byte[] b = headline.getBytes();
			decoder.parseFrameSize(b);
			Assert.fail("Should throw Exception!");
		} catch (FrameDecoderException e) {
			Assert.assertEquals("invalid scmp header line", e.getMessage());
		}
	}

	/**
	 * Parses the frame size test.
	 */
	@Test
	public void parseFrameSizeTest() {
		byte[] b = new byte[0];
		int frameSize = 0;
		String headline = "REQ /s=87& SCMP/1.0\n";
		try {
			b = headline.getBytes();
			frameSize = decoder.parseFrameSize(b);
			Assert.assertEquals("107", frameSize + "");
		} catch (Exception e) {
			Assert.fail("Should not throw Exception!");
		}

		headline = "REQ /s=01& SCMP/1.0\n";
		try {
			b = headline.getBytes();
			frameSize = decoder.parseFrameSize(b);
			Assert.assertEquals("21", frameSize + "");
		} catch (Exception e) {
			Assert.fail("Should not throw Exception!");
		}
	}

	/**
	 * Read int fail test.
	 */
	@Test
	public void readIntFailTest() {
		byte[] b = new byte[0];
		String headline = "REQ /=87& SCMP/1.0\n";
		try {
			b = headline.getBytes();
			decoder.parseFrameSize(b);
			Assert.fail("Should throw Exception!");
		} catch (Exception e) {
			Assert.assertEquals("invalid scmp message length", e.getMessage());
		}

		headline = "REQ /s=& SCMP/1.0\n";
		try {
			b = headline.getBytes();
			decoder.parseFrameSize(b);
			Assert.fail("Should throw Exception!");
		} catch (Exception e) {
			Assert.assertEquals("invalid scmp message length", e.getMessage());
		}

		headline = "REQ /s=0& SCMP/1.0\n";
		try {
			b = headline.getBytes();
			decoder.parseFrameSize(b);
			Assert.fail("Should throw Exception!");
		} catch (Exception e) {
			Assert.assertEquals("invalid scmp message length", e.getMessage());
		}
	}
}
