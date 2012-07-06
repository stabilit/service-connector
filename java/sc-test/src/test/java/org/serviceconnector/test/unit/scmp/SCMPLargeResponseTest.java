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

import junit.framework.Assert;

import org.junit.Test;
import org.serviceconnector.scmp.SCMPBodyType;
import org.serviceconnector.scmp.SCMPCompositeReceiver;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.scmp.SCMPPart;

/**
 * The Class SCMPCompositeTest.
 * 
 * @author JTraber
 */
public class SCMPLargeResponseTest {

	/**
	 * Description: SCMP large response test<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_SCMPLargeResponseTest() {
		int bodyLength = 0;
		StringBuilder body = new StringBuilder();

		SCMPMessage request = new SCMPMessage();
		request.setBody("request");
		request.setMessageType(SCMPMsgType.CLN_EXECUTE);

		SCMPPart firstPart = new SCMPPart(false);
		String bodyString = "first part request";
		firstPart.setBody(bodyString);
		SCMPCompositeReceiver largeResponse = new SCMPCompositeReceiver(request, firstPart);

		bodyLength += bodyString.length();
		body.append(bodyString);
		Assert.assertEquals(bodyLength, largeResponse.getOffset());

		for (int i = 0; i < 10; i++) {
			SCMPPart part = new SCMPPart();
			bodyString = "part nr: " + i;
			part.setBody(bodyString);
			largeResponse.add(part);
			bodyLength += bodyString.length();
			body.append(bodyString);
			Assert.assertEquals(bodyLength, largeResponse.getOffset());
		}
		// needed to compare to requestPart of the composite
		// body of the requestPart is null because body is split into several parts
		// bodyType is text because split parts hold text bodies
		request.setBody(null);
		request.setHeader(SCMPHeaderAttributeKey.BODY_TYPE, SCMPBodyType.TEXT.getValue());

		Assert.assertEquals(bodyLength, largeResponse.getBodyLength());
		Assert.assertEquals(body.toString(), largeResponse.getBody() + "");
		Assert.assertEquals(request.toString(), largeResponse.getPollMessage().toString());
	}
}
