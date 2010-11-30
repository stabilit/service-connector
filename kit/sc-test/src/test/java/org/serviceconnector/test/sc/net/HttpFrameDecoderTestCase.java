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
package org.serviceconnector.test.sc.net;

import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.Constants;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.FrameDecoderException;
import org.serviceconnector.net.IFrameDecoder;

/**
 * The Class HttpFrameDecoderTest.
 * 
 * @author JTraber
 */
public class HttpFrameDecoderTestCase {

	/** The decoder. */
	private IFrameDecoder decoder = AppContext.getFrameDecoderFactory().getFrameDecoder(
			Constants.HTTP);

	/**
	 * Parses the frame size test.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void parseFrameSizeTest() throws Exception {
		String httpHeader = "POST / HTTP/1.1\r\n" + "Host: www.google.com\r\n" + "Connection: close\r\n"
				+ "User-Agent: Web-sniffer/1.0.31 (+http://web-sniffer.net/)\r\n"
				+ "Accept-Charset: ISO-8859-1,UTF-8;q=0.7,*;q=0.7\r\n" + "Cache-Control: no\r\n"
				+ "Accept-Language: de,en;q=0.7,en-us;q=0.3\r\n" + "Referer: http://web-sniffer.net/\r\n"
				+ "Content-type: application/x-www-form-urlencoded\r\n" + "Content-Length: 122\r\n";

		try {
			int frameSize = decoder.parseFrameSize(httpHeader.getBytes());
			Assert.assertEquals("122", frameSize + "");
		} catch (FrameDecoderException e) {
			Assert.fail("Should not throw Exception");
		}
	}
}
