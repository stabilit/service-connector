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

import org.junit.Before;
import org.junit.Test;

import com.stabilit.sc.cln.client.ClientFactory;
import com.stabilit.sc.cln.config.ClientConfig;
import com.stabilit.sc.cln.service.SCMPCallFactory;
import com.stabilit.sc.cln.service.SCMPEchoSCCall;
import com.stabilit.sc.common.scmp.SCMP;
import com.stabilit.sc.common.scmp.SCMPBodyType;
import com.stabilit.sc.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.scmp.SCMPMsgType;
import com.stabilit.sc.unit.test.SetupTestCases;
import com.stabilit.sc.unit.test.SuperTestCase;

public class EchoSCTestCase extends SuperTestCase {

	/**
	 * @param fileName
	 */
	
	public EchoSCTestCase(String fileName) {
		super(fileName);
	}

	@Before
	@Override
	public void setup() throws Exception {
		SetupTestCases.setupSC();
		try {
			config = new ClientConfig();
			config.load(fileName);
			ClientFactory clientFactory = new ClientFactory();
			client = clientFactory.newInstance(config.getClientConfig());
			client.connect(); // physical connect
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Test
	public void invokeSingleEchoSCTest() throws Exception {
		SCMPEchoSCCall echoCall = (SCMPEchoSCCall) SCMPCallFactory.ECHO_SC_CALL.newInstance(client);

		SCMP result = null;
		Map<String, String> header = null;

		echoCall.setBody("hello world!" + client.toHashCodeString());
		result = echoCall.invoke();
		System.out.println("result = " + result.getBody());
		header = result.getHeader();
		Assert.assertEquals("hello world!" + client.toHashCodeString(), result.getBody());
		Assert.assertEquals("1", result.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		Assert.assertNotNull(header.get(SCMPHeaderAttributeKey.BODY_LENGTH.getName()));

		/*************************** verify echo session **********************************/
		Assert.assertEquals(SCMPBodyType.text.getName(), header.get(SCMPHeaderAttributeKey.BODY_TYPE
				.getName()));
		Assert.assertEquals(SCMPMsgType.ECHO_SC.getResponseName(), result.getMessageType());
	}

	@Test
	public void invokeMultipleEchoSCTest() throws Exception {
		SCMPEchoSCCall echoCall = (SCMPEchoSCCall) SCMPCallFactory.ECHO_SC_CALL.newInstance(client);

		SCMP result = null;
		int i = 0;
		String echoString = null;
		for (i = 0; i < 10000; i++) {
			echoString = "hello world " + i + client.toHashCodeString();
			echoCall.setBody(echoString);
			result = echoCall.invoke();
			Assert.assertEquals("hello world " + i + client.toHashCodeString(), result.getBody());
			Assert.assertEquals(echoString.length() + "", result
					.getHeader(SCMPHeaderAttributeKey.BODY_LENGTH));
			Assert.assertEquals(SCMPBodyType.text.getName(), result
					.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
			Assert.assertEquals(SCMPMsgType.ECHO_SC.getResponseName(), result.getMessageType());
			Assert.assertEquals(i + 1 + "", result.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		}
		Assert.assertEquals("hello world " + (i - 1) + client.toHashCodeString(), result.getBody());
		Assert.assertEquals(echoString.length() + "", result.getHeader(SCMPHeaderAttributeKey.BODY_LENGTH));
		Assert.assertEquals(SCMPBodyType.text.getName(), result.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertEquals(SCMPMsgType.ECHO_SC.getResponseName(), result.getMessageType());
	}

}