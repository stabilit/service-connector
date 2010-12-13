/*
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
 */
package org.serviceconnector.test.unit;

import java.text.DecimalFormat;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.serviceconnector.Constants;
import org.serviceconnector.scmp.SCMPHeadlineKey;
import org.serviceconnector.test.unit.api.SCMessageTest;
import org.serviceconnector.test.unit.api.SCSubscribeMessageTest;
import org.serviceconnector.test.unit.api.cln.SCClientTest;
import org.serviceconnector.test.unit.api.srv.NewServerTest;
import org.serviceconnector.test.unit.api.srv.SCServerTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { SCMessageTest.class, //
		SCSubscribeMessageTest.class, //
		SCClientTest.class, //
		SCServerTest.class, //
		NewServerTest.class, //
		DefaultFrameDecoderTest.class, //
		HttpFrameDecoderTest.class, //
		SCMPCompositeTest.class, //
		SCMPLargeRequestTest.class, //
		SCMPLargeResponseTest.class, //
		LargeMessageEncoderDecoderTest.class, //
		KeepAliveMessageEncoderDecoderTest.class,//
		DefaultEncoderDecoderTest.class,//
		ValidatorUtilityTest.class })
public class AllUnitTests {

	private static DecimalFormat dfMsg = new DecimalFormat(Constants.SCMP_FORMAT_OF_MSG_SIZE);
	private static DecimalFormat dfHeader = new DecimalFormat(Constants.SCMP_FORMAT_OF_HEADER_SIZE);

	public static String getSCMPString(SCMPHeadlineKey headKey, String header, String body) {
		int headerSize = 0;
		int bodySize = 0;
		String msgString = "";

		if (header != null) {
			headerSize = header.length();
			msgString += header;
		}
		if (body != null) {
			bodySize = body.length();
			msgString += body;
		}
		int messageSize = headerSize + bodySize;

		String scmpString = headKey.name() + AllUnitTests.dfMsg.format(messageSize) + AllUnitTests.dfHeader.format(headerSize)
				+ " 1.0\n" + msgString;
		return scmpString;
	}
}