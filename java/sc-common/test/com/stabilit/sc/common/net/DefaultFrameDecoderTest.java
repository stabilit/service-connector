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
package com.stabilit.sc.common.net;

import org.junit.Assert;
import org.junit.Test;

import com.stabilit.sc.common.factory.IFactoryable;

/**
 * @author JTraber
 * 
 */
public class DefaultFrameDecoderTest {

	private DefaultFrameDecoder decoder = new DefaultFrameDecoder();

	@Test
	public void singeltonTest() {
		IFactoryable decoder2 = decoder.newInstance();
		Assert.assertEquals(decoder, decoder2);
	}

	@Test
	public void parseFrameSizeFailTest() {
		try {
			decoder.parseFrameSize(null);
			Assert.fail("Should throw Exception!");
		} catch (FrameDecoderException e) {
		}

		byte[] b = new byte[0];
		try {
			decoder.parseFrameSize(b);
			Assert.fail("Should throw Exception!");
		} catch (FrameDecoderException e) {
		}

		String headline = "REQ /=87& SCMP/1.0";
		try {
			b = headline.getBytes();
			decoder.parseFrameSize(b);
			Assert.fail("Should throw Exception!");
		} catch (FrameDecoderException e) {
		}
	}

	@Test
	public void parseFrameSizeTest() {
		byte[] b = new byte[0];
		int frameSize = 0;
		String headline = "REQ /s=87& SCMP/1.0\n";
		try {
			b = headline.getBytes();
			frameSize = decoder.parseFrameSize(b);
			Assert.assertEquals("107", frameSize + "");
		} catch (FrameDecoderException e) {
			Assert.fail("Should not throw Exception!");
		}

		headline = "REQ /s=01& SCMP/1.0\n";
		try {
			b = headline.getBytes();
			frameSize = decoder.parseFrameSize(b);
			Assert.assertEquals("21", frameSize + "");
		} catch (FrameDecoderException e) {
			Assert.fail("Should not throw Exception!");
		}
	}

	@Test
	public void readIntFailTest() {
		byte[] b = new byte[0];
		String headline = "REQ /=87& SCMP/1.0\n";
		try {
			b = headline.getBytes();
			decoder.parseFrameSize(b);
			Assert.fail("Should throw Exception!");
		} catch (FrameDecoderException e) {
		}

		headline = "REQ /s=& SCMP/1.0\n";
		try {
			b = headline.getBytes();
			decoder.parseFrameSize(b);
			Assert.fail("Should throw Exception!");
		} catch (FrameDecoderException e) {
		}

		headline = "REQ /s=0& SCMP/1.0\n";
		try {
			b = headline.getBytes();
			decoder.parseFrameSize(b);
			Assert.fail("Should throw Exception!");
		} catch (FrameDecoderException e) {
		}
	}
}
