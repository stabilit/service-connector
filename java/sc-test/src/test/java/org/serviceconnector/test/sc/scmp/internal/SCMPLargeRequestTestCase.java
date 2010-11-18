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
package org.serviceconnector.test.sc.scmp.internal;

import junit.framework.Assert;

import org.junit.Test;
import org.serviceconnector.scmp.SCMPLargeRequest;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPSendPart;


/**
 * The Class SCMPLargeRequestTest.
 * 
 * @author JTraber
 */
public class SCMPLargeRequestTestCase extends SCMPMessage {

	/** The MAX_ANZ. */
	private static final int MAX_ANZ = 100000;

	/**
	 * Scmp large request test.
	 */
	@Test
	public void scmpLargeRequestTest() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < MAX_ANZ; i++) {
			sb.append(i);
		}

		SCMPMessage largeScmp = new SCMPMessage();
		largeScmp.setBody(sb.toString());

		SCMPLargeRequest largeRequest = new SCMPLargeRequest(largeScmp);

		int offset = 0;
		while (largeRequest.hasNext()) {

			SCMPSendPart responsePart = new SCMPSendPart(largeScmp, offset, sb.length());
			offset += responsePart.getBodyLength();

			SCMPMessage message = largeRequest.getNext();
			Assert.assertEquals(responsePart.getBody().toString(), message.getBody().toString());
			Assert.assertEquals(responsePart.getBodyLength(), message.getBodyLength());
			Assert.assertEquals(responsePart.getBodyOffset(), message.getBodyOffset());
			Assert.assertEquals(responsePart.getBodyType(), message.getBodyType());
		}

		SCMPSendPart firstPart = new SCMPSendPart(largeScmp, 0, sb.length());
		SCMPMessage message = largeRequest.getFirst();
		Assert.assertEquals(firstPart.getBody().toString(), message.getBody().toString());
		Assert.assertEquals(firstPart.getBodyLength(), message.getBodyLength());
		Assert.assertEquals(firstPart.getBodyOffset(), message.getBodyOffset());
		Assert.assertEquals(firstPart.getBodyType(), message.getBodyType());
	}
}
