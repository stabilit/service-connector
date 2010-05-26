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

import com.stabilit.sc.cln.call.SCMPCallFactory;
import com.stabilit.sc.cln.call.SCMPClnEchoCall;
import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.scmp.SCMPBodyType;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.unit.test.session.SuperSessionTestCase;

/**
 * The Class SrvEchoTestCase.
 */
public class SrvEchoTestCase extends SuperSessionTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public SrvEchoTestCase(String fileName) {
		super(fileName);
	}

	/**
	 * Invoke single srv echo test.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void invokeSingleSrvEchoTest() throws Exception {
		SCMPMessage result = null;
		SCMPClnEchoCall clnEchoCall = (SCMPClnEchoCall) SCMPCallFactory.CLN_ECHO_CALL.newInstance(client);
		clnEchoCall.setMaxNodes(2);
		clnEchoCall.setServiceName("simulation");
		clnEchoCall.setRequestBody("hello world");
		result = clnEchoCall.invoke();

		Assert.assertEquals("hello world", result.getBody());
		Assert.assertEquals(SCMPBodyType.text.getName(), result.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertNotNull(result.getSessionId());
		Assert.assertEquals(SCMPMsgType.CLN_ECHO.getResponseName(), result.getMessageType());
		Assert.assertEquals("3", result.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
	}

	/**
	 * Invoke multiple srv echo test.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void invokeMultipleSrvEchoTest() throws Exception {

		long startTime = System.currentTimeMillis();
		int anzMsg = 1000;
		SCMPMessage result = null;

		SCMPClnEchoCall clnEchoCall = (SCMPClnEchoCall) SCMPCallFactory.CLN_ECHO_CALL.newInstance(client);
		clnEchoCall.setMaxNodes(2);
		clnEchoCall.setServiceName("simulation");

		for (int i = 0; i < anzMsg; i++) {
			clnEchoCall.setRequestBody("hello world, index = " + i + client.toHashCodeString());
			result = clnEchoCall.invoke();
			Assert.assertEquals("hello world, index = " + i + client.toHashCodeString(), result.getBody());
			Assert.assertEquals((i + 3) + "", result.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		}
		System.out.println(anzMsg / ((System.currentTimeMillis() - startTime) / 1000D) + " msg pro sec");
	}

	/**
	 * Invoke multiple session srv echo test. Out of memory test. If threads are not stopped and destroyed properly an
	 * out of memory exception on SC will occur. If number of sockets on the operating system is delimited test will end
	 * before regular end is reached.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void invokeMultipleSessionSrvEchoTest() throws Exception {
		super.clnDeleteSessionAfter();
		long startTime = System.currentTimeMillis();
		int anzMsg = 1000;
		SCMPMessage result = null;

		for (int i = 0; i < anzMsg; i++) {
			super.clnCreateSessionBefore();
			SCMPClnEchoCall clnEchoCall = (SCMPClnEchoCall) SCMPCallFactory.CLN_ECHO_CALL.newInstance(client);
			clnEchoCall.setMaxNodes(2);
			clnEchoCall.setServiceName("simulation");
			clnEchoCall.setRequestBody("hello world, index = " + i + client.toHashCodeString());
			result = clnEchoCall.invoke();
			Assert.assertEquals("hello world, index = " + i + client.toHashCodeString(), result.getBody());
			Assert.assertEquals((i*3)+5 + "", result.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
			super.clnDeleteSessionAfter();
		}
		System.out.println(anzMsg / ((System.currentTimeMillis() - startTime) / 1000D) + " msg pro sec");
		super.clnCreateSessionBefore();
	}

	/**
	 * Invoke multiple session srv echo test for multiple clients. Gets invoked from MT tests.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void invokeMultipleSessionSrvEchoTestForMultipleClients() throws Exception {
		super.clnDeleteSessionAfter();
		long startTime = System.currentTimeMillis();
		int anzMsg = 100;
		SCMPMessage result = null;

		for (int i = 0; i < anzMsg; i++) {
			super.clnCreateSessionBefore();
			SCMPClnEchoCall clnEchoCall = (SCMPClnEchoCall) SCMPCallFactory.CLN_ECHO_CALL.newInstance(client);
			clnEchoCall.setMaxNodes(2);
			clnEchoCall.setServiceName("simulation");
			clnEchoCall.setRequestBody("hello world, index = " + i + client.toHashCodeString());
			result = clnEchoCall.invoke();
			Assert.assertEquals("hello world, index = " + i + client.toHashCodeString(), result.getBody());
			Assert.assertEquals((i*3)+5 + "", result.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
			super.clnDeleteSessionAfter();
		}
		System.out.println(anzMsg / ((System.currentTimeMillis() - startTime) / 1000D) + " msg pro sec");
		super.clnCreateSessionBefore();
	}
}