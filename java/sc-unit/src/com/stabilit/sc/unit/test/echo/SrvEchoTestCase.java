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
import com.stabilit.sc.unit.test.session.SuperSessionTestCase;

public class SrvEchoTestCase extends SuperSessionTestCase {

	@Test
	public void invokeSingleSrvEchoTest() throws Exception {
		SCMP result = null;
		SCMPClnEchoCall clnEchoCall = (SCMPClnEchoCall) SCMPCallFactory.CLN_ECHO_CALL.newInstance(client,
				scmpSession);
		clnEchoCall.setMaxNodes(2);
		clnEchoCall.setServiceName("simulation");
		clnEchoCall.setBody("hello world");
		result = clnEchoCall.invoke();
		
		Map<String, String> header = result.getHeader();
		Assert.assertEquals("hello world", result.getBody());
		Assert.assertEquals(SCMPBodyType.text.getName(), header.get(SCMPHeaderAttributeKey.SCMP_BODY_TYPE.getName()));
		Assert.assertNotNull(result.getSessionId());
		Assert.assertEquals(SCMPMsgType.CLN_ECHO.getResponseName(), result.getMessageType());
	}

	@Test
	public void invokeMultipleSrvEchoTest() throws Exception {

		long startTime = System.currentTimeMillis();
		int anzMsg = 1000;
		SCMP result = null;

		SCMPClnEchoCall ClnEchoCall = (SCMPClnEchoCall) SCMPCallFactory.CLN_ECHO_CALL.newInstance(client,
				scmpSession);
		ClnEchoCall.setMaxNodes(2);
		ClnEchoCall.setServiceName("simulation");

		for (int i = 0; i < anzMsg; i++) {
			ClnEchoCall.setBody("hello world, index = " + i);
			result = ClnEchoCall.invoke();
			Assert.assertEquals("hello world, index = " + i, result.getBody());
		}
		System.out.println(anzMsg / ((System.currentTimeMillis() - startTime) / 1000D) + " msg pro sec");
	}

	@Test
	public void invokeMultipleSessionSrvEchoTest() throws Exception {
		clnDeleteSession();
		long startTime = System.currentTimeMillis();
		int anzMsg = 1000;
		SCMP result = null;	

		for (int i = 0; i < anzMsg; i++) {
			createSession();
			SCMPClnEchoCall clnEchoCall = (SCMPClnEchoCall) SCMPCallFactory.CLN_ECHO_CALL.newInstance(client,
					scmpSession);
			clnEchoCall.setMaxNodes(2);
			clnEchoCall.setServiceName("simulation");
			clnEchoCall.setBody("hello world, index = " + i);
			result = clnEchoCall.invoke();
			Assert.assertEquals("hello world, index = " + i, result.getBody());			
			clnDeleteSession();
		}
		System.out.println(anzMsg / ((System.currentTimeMillis() - startTime) / 1000D) + " msg pro sec");
		createSession();
	}
}