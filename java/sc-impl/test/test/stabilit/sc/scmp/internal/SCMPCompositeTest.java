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
package test.stabilit.sc.scmp.internal;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.scm.common.scmp.SCMPBodyType;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.scmp.internal.SCMPCompositeReceiver;
import com.stabilit.scm.common.scmp.internal.SCMPPart;

/**
 * The Class SCMPCompositeTest.
 * 
 * @author JTraber
 */
public class SCMPCompositeTest {

	/**
	 * Scmp composite test.
	 */
	@Test
	public void scmpCompositeTest() {
		int bodyLength = 0;
		StringBuilder body = new StringBuilder();

		SCMPMessage request = new SCMPMessage();
		request.setBody("request");
		request.setMessageType(SCMPMsgType.CLN_DATA.getName());

		SCMPPart firstPart = new SCMPPart();
		String bodyString = "first part request";
		firstPart.setBody(bodyString);
		SCMPCompositeReceiver composite = new SCMPCompositeReceiver(request, firstPart);

		bodyLength += bodyString.length();
		body.append(bodyString);
		Assert.assertEquals(bodyLength, composite.getOffset());

		for (int i = 0; i < 10; i++) {
			SCMPPart part = new SCMPPart();
			bodyString = "part nr: " + i;
			part.setBody(bodyString);
			composite.add(part);
			bodyLength += bodyString.length();
			body.append(bodyString);
			Assert.assertEquals(bodyLength, composite.getOffset());
		}
		// needed to compare to requestPart of the composite
		// body of the requestPart is null because body is split into several parts
		// bodyType is text because split parts hold text bodies
		request.setBody(null);
		request.setHeader(SCMPHeaderAttributeKey.BODY_TYPE, SCMPBodyType.text.getName());

		Assert.assertEquals(bodyLength, composite.getBodyLength());
		Assert.assertEquals(body.toString(), composite.getBody() + "");
		Assert.assertEquals(request.toString(), composite.getPart().toString());
	}
}
