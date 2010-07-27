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
package com.stabilit.scm.unit.test.echo;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPClnEchoCall;
import com.stabilit.scm.common.scmp.SCMPBodyType;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.unit.test.session.SuperSessionTestCase;

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
	 * Invoke single server echo test.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void invokeSingleSrvEchoTest() throws Exception {
		SCMPMessage result = null;
		SCMPClnEchoCall clnEchoCall = (SCMPClnEchoCall) SCMPCallFactory.CLN_ECHO_CALL.newInstance(req, "simulation",
				this.sessionId);
		clnEchoCall.setRequestBody("hello world");

		double startTime = System.currentTimeMillis();
		double anzMsg = 1000;
		for (int i = 0; i < anzMsg; i++) {
			clnEchoCall.setRequestBody("hello world");
			clnEchoCall.invoke(this.sessionCallback);
			result = this.sessionCallback.getMessageSync();
		}
		double endTime = System.currentTimeMillis();
		System.out.println("Needed Time in sec: " + (endTime - startTime) / 1000L);
		System.out.println("Number of msg: " + anzMsg);
		System.out.println("Msg in sec: " + (anzMsg / ((endTime - startTime) / 1000L)));

		Assert.assertEquals("hello world", result.getBody());
		Assert.assertEquals(SCMPBodyType.TEXT.getValue(), result.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertNotNull(result.getSessionId());
		Assert.assertEquals(SCMPMsgType.CLN_ECHO.getValue(), result.getMessageType());
		anzMsg += 2;
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

		SCMPClnEchoCall clnEchoCall = (SCMPClnEchoCall) SCMPCallFactory.CLN_ECHO_CALL.newInstance(req, "simulation",
				this.sessionId);

		for (int i = 0; i < anzMsg; i++) {
			clnEchoCall.setRequestBody("hello world, index = " + i + req.toHashCodeString());
			clnEchoCall.invoke(this.sessionCallback);
			result = this.sessionCallback.getMessageSync();
			Assert.assertEquals("hello world, index = " + i + req.toHashCodeString(), result.getBody());
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
			SCMPClnEchoCall clnEchoCall = (SCMPClnEchoCall) SCMPCallFactory.CLN_ECHO_CALL.newInstance(req,
					"simulation", this.sessionId);
			clnEchoCall.setRequestBody("hello world, index = " + i + req.toHashCodeString());
			clnEchoCall.invoke(this.sessionCallback);
			result = this.sessionCallback.getMessageSync();
			Assert.assertEquals("hello world, index = " + i + req.toHashCodeString(), result.getBody());
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
			SCMPClnEchoCall clnEchoCall = (SCMPClnEchoCall) SCMPCallFactory.CLN_ECHO_CALL.newInstance(req,
					"simulation", this.sessionId);
			clnEchoCall.setRequestBody("hello world, index = " + i + req.toHashCodeString());
			clnEchoCall.invoke(this.sessionCallback);
			result = this.sessionCallback.getMessageSync();
			Assert.assertEquals("hello world, index = " + i + req.toHashCodeString(), result.getBody());
			super.clnDeleteSessionAfter();
		}
		System.out.println(anzMsg / ((System.currentTimeMillis() - startTime) / 1000D) + " msg pro sec");
		super.clnCreateSessionBefore();
	}

	public void invokeMultipleSrvEchoTestForMultipleClients() throws Exception {

		long startTime = System.currentTimeMillis();
		int anzMsg = 100;
		SCMPMessage result = null;

		SCMPClnEchoCall clnEchoCall = (SCMPClnEchoCall) SCMPCallFactory.CLN_ECHO_CALL.newInstance(req, "simulation",
				this.sessionId);

		for (int i = 0; i < anzMsg; i++) {
			clnEchoCall.setRequestBody("hello world, index = " + i + req.toHashCodeString());
			clnEchoCall.invoke(this.sessionCallback);
			result = this.sessionCallback.getMessageSync();
			Assert.assertEquals("hello world, index = " + i + req.toHashCodeString(), result.getBody());
		}
		System.out.println(anzMsg / ((System.currentTimeMillis() - startTime) / 1000D) + " msg pro sec");
	}
}