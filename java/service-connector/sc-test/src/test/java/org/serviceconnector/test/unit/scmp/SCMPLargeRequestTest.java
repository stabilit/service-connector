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
package org.serviceconnector.test.unit.scmp;

import org.junit.Test;
import org.serviceconnector.scmp.SCMPCompositeSender;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPOffsetPart;
import org.serviceconnector.scmp.SCMPVersion;

import junit.framework.Assert;

/**
 * The Class SCMPLargeRequestTest.
 *
 * @author JTraber
 */
public class SCMPLargeRequestTest {

	/** The MAX_ANZ. */
	private static final int MAX_ANZ = 100000;

	/**
	 * Description: SCMP large request test<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_SCMPLargeRequestTest() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < MAX_ANZ; i++) {
			sb.append(i);
		}

		SCMPMessage largeScmp = new SCMPMessage(SCMPVersion.CURRENT);
		largeScmp.setBody(sb.toString());

		SCMPCompositeSender largeRequest = new SCMPCompositeSender(largeScmp);

		int offset = 0;
		while (largeRequest.hasNext()) {
			SCMPMessage message = largeRequest.getNext();

			SCMPOffsetPart responsePart = new SCMPOffsetPart(largeScmp, offset, sb.length());
			offset += responsePart.getBodyLength();
			Assert.assertEquals(responsePart.getBody().toString(), message.getBody().toString());
			Assert.assertEquals(responsePart.getBodyLength(), message.getBodyLength());
			Assert.assertEquals(responsePart.getBodyOffset(), message.getBodyOffset());
			Assert.assertEquals(responsePart.getBodyType(), message.getBodyType());
		}

		SCMPOffsetPart firstPart = new SCMPOffsetPart(largeScmp, 0, sb.length());
		SCMPMessage message = largeRequest.getFirst();
		Assert.assertEquals(firstPart.getBody().toString(), message.getBody().toString());
		Assert.assertEquals(firstPart.getBodyLength(), message.getBodyLength());
		Assert.assertEquals(firstPart.getBodyOffset(), message.getBodyOffset());
		Assert.assertEquals(firstPart.getBodyType(), message.getBodyType());
	}
}
