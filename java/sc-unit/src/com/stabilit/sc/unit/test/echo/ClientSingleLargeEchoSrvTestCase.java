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
package com.stabilit.sc.unit.test.echo;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.sc.cln.service.SCMPCallFactory;
import com.stabilit.sc.cln.service.SCMPClnEchoCall;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPBodyType;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.unit.test.SuperSessionTestCase;

public class ClientSingleLargeEchoSrvTestCase extends SuperSessionTestCase {

	protected Integer index = null;

	@Test
	public void invokeTest() throws Exception {

		SCMPClnEchoCall echoCall = (SCMPClnEchoCall) SCMPCallFactory.CLN_ECHO_CALL.newInstance(client,
				scmpSession);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 100000; i++) {
			sb.append(i);
		}
		echoCall.setBody(sb.toString());
		echoCall.setMaxNodes(2);
		SCMP result = echoCall.invoke();
		/*************************** verify echo session **********************************/
		int start = (sb.length() / SCMP.LARGE_MESSAGE_LIMIT) * SCMP.LARGE_MESSAGE_LIMIT;
		int bodyLength = sb.length() - start;
		String lastPartBody = sb.substring(start);
		Map<String, String> header = result.getHeader();
		Assert.assertEquals(lastPartBody, result.getBody());
		Assert.assertEquals(SCMPBodyType.text.getName(), header.get(SCMPHeaderAttributeKey.SCMP_BODY_TYPE
				.getName()));
		Assert.assertNull(header.get(SCMPHeaderAttributeKey.PART_ID.getName()));
		Assert.assertEquals(bodyLength + "", header.get(SCMPHeaderAttributeKey.BODY_LENGTH.getName()));
		Assert.assertEquals(SCMPMsgType.CLN_ECHO.getResponseName(), result.getMessageType());
		Assert.assertNotNull(result.getSessionId());
	}

	@Test
	public void invokeTestTransitive() throws Exception {
		SCMPClnEchoCall echoCall = (SCMPClnEchoCall) SCMPCallFactory.CLN_ECHO_CALL.newInstance(client,
				scmpSession);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 89840; i++) {
			sb.append(i);
		}
		echoCall.setBody(sb.toString());
		echoCall.setMaxNodes(2);
		SCMP result = echoCall.invoke();
		/*************************** verify echo session **********************************/
		int start = (sb.length() / SCMP.LARGE_MESSAGE_LIMIT) * SCMP.LARGE_MESSAGE_LIMIT;
		int bodyLength = sb.length() - start;
		String lastPartBody = sb.substring(start);
		Map<String, String> header = result.getHeader();
		Assert.assertEquals(lastPartBody, result.getBody());
		Assert.assertEquals(SCMPBodyType.text.getName(), header.get(SCMPHeaderAttributeKey.SCMP_BODY_TYPE
				.getName()));
		Assert.assertNull(header.get(SCMPHeaderAttributeKey.PART_ID.getName()));
		Assert.assertEquals(bodyLength + "", header.get(SCMPHeaderAttributeKey.BODY_LENGTH.getName()));
		Assert.assertEquals(SCMPMsgType.CLN_ECHO.getResponseName(), result.getMessageType());
		Assert.assertNotNull(result.getSessionId());
	}
}
