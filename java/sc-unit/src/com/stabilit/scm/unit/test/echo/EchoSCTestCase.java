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

import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPEchoSCCall;
import com.stabilit.scm.common.conf.RequesterConfigPool;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.scmp.SCMPBodyType;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.unit.test.SetupTestCases;
import com.stabilit.scm.unit.test.SuperTestCase;

public class EchoSCTestCase extends SuperTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param fileName the file name
	 * @throws Exception 
	 */	
	public EchoSCTestCase(String fileName) throws Exception {
		super(fileName);
	}

	@Before
	@Override
	public void setup() throws Exception {
		SetupTestCases.setupSC();
		try {
			config = new RequesterConfigPool();
			config.load(fileName);
			req = new Requester(this.testContext);
			req.connect(); // physical connect
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Test
	public void invokeSingleEchoSCTest() throws Exception {
		SCMPEchoSCCall echoCall = (SCMPEchoSCCall) SCMPCallFactory.ECHO_SC_CALL.newInstance(req);

		SCMPMessage result = null;
		Map<String, String> header = null;

		echoCall.setRequestBody("hello world!" + req.toHashCodeString());
		result = echoCall.invoke();
		System.out.println("result = " + result.getBody());
		header = result.getHeader();
		Assert.assertEquals("hello world!" + req.toHashCodeString(), result.getBody());
		Assert.assertEquals("1", result.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		Assert.assertNotNull(header.get(SCMPHeaderAttributeKey.BODY_LENGTH.getName()));

		/*************************** verify echo session **********************************/
		Assert.assertEquals(SCMPBodyType.text.getName(), header.get(SCMPHeaderAttributeKey.BODY_TYPE
				.getName()));
		Assert.assertEquals(SCMPMsgType.ECHO_SC.getName(), result.getMessageType());
	}

	@Test
	public void invokeMultipleEchoSCTest() throws Exception {
		SCMPEchoSCCall echoCall = (SCMPEchoSCCall) SCMPCallFactory.ECHO_SC_CALL.newInstance(req);

		SCMPMessage result = null;
		int i = 0;
		String echoString = null;
		for (i = 0; i < 1000; i++) {
			echoString = "hello world " + i + req.toHashCodeString();
			echoCall.setRequestBody(echoString);
			result = echoCall.invoke();
			Assert.assertEquals("hello world " + i + req.toHashCodeString(), result.getBody());
			Assert.assertEquals(echoString.length() + "", result
					.getHeader(SCMPHeaderAttributeKey.BODY_LENGTH));
			Assert.assertEquals(SCMPBodyType.text.getName(), result
					.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
			Assert.assertEquals(SCMPMsgType.ECHO_SC.getName(), result.getMessageType());
			Assert.assertEquals(i + 1 + "", result.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		}
	}
}