/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package org.serviceconnector.test;

import java.text.DecimalFormat;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.serviceconnector.Constants;
import org.serviceconnector.scmp.SCMPHeadlineKey;
import org.serviceconnector.test.net.DefaultEncoderDecoderTestCase;
import org.serviceconnector.test.net.DefaultFrameDecoderTestCase;
import org.serviceconnector.test.net.HttpFrameDecoderTestCase;
import org.serviceconnector.test.net.KeepAliveMessageEncoderDecoderTestCase;
import org.serviceconnector.test.net.LargeMessageEncoderDecoderTestCase;
import org.serviceconnector.test.scmp.internal.SCMPCompositeTestCase;
import org.serviceconnector.test.scmp.internal.SCMPLargeRequestTestCase;
import org.serviceconnector.test.scmp.internal.SCMPLargeResponseTestCase;
import org.serviceconnector.test.util.ValidatorUtilityTestCase;



/**
 * The Class SCImplTest.
 * 
 * @author JTraber
 */
@RunWith(Suite.class)
@SuiteClasses( { DefaultFrameDecoderTestCase.class,
				 HttpFrameDecoderTestCase.class, 
				 SCMPCompositeTestCase.class,
				 SCMPLargeRequestTestCase.class, 
				 SCMPLargeResponseTestCase.class,
				 LargeMessageEncoderDecoderTestCase.class, 
				 KeepAliveMessageEncoderDecoderTestCase.class,
				 DefaultEncoderDecoderTestCase.class,
				 ValidatorUtilityTestCase.class })
public final class SCImplTest {

	private static DecimalFormat dfMsg = new DecimalFormat(Constants.FORMAT_OF_MSG_SIZE);
	private static DecimalFormat dfHeader = new DecimalFormat(Constants.FORMAT_OF_HEADER_SIZE);

	/**
	 * Instantiates a new sC impl test.
	 */
	private SCImplTest() {
	}

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

		String scmpString = headKey.name() + dfMsg.format(messageSize) + dfHeader.format(headerSize) + " 1.0\n" + msgString;
		return scmpString;
	}
}
