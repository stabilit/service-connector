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

import com.stabilit.sc.cln.call.ISCMPCall;
import com.stabilit.sc.cln.call.SCMPCallFactory;
import com.stabilit.sc.cln.call.SCMPEchoSCCall;
import com.stabilit.sc.cln.client.ClientFactory;
import com.stabilit.sc.cln.config.ClientConfig;
import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.scmp.SCMPBodyType;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.unit.test.SetupTestCases;
import com.stabilit.sc.unit.test.SuperTestCase;

public class EchoSCLargeTestCase extends SuperTestCase {

	/**
	 * @param fileName
	 */
	public EchoSCLargeTestCase(String fileName) {
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
	public void invokeTwoPartsTest() throws Exception {
		SCMPEchoSCCall echoCall = (SCMPEchoSCCall) SCMPCallFactory.ECHO_SC_CALL.newInstance(client);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 19000; i++) {
			sb.append(i);
		}
		echoCall.setBody(sb.toString());
		SCMPMessage result = echoCall.invoke();
		/*************************** verify echo session **********************************/
		Map<String, String> header = result.getHeader();
		Assert.assertEquals(sb.toString(), result.getBody());
		Assert.assertEquals(SCMPBodyType.text.getName(), header.get(SCMPHeaderAttributeKey.BODY_TYPE
				.getName()));
//		Assert.assertEquals("1/3", result.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		Assert.assertEquals(sb.length() + "", header.get(SCMPHeaderAttributeKey.BODY_LENGTH.getName()));
		Assert.assertEquals(SCMPMsgType.ECHO_SC.getResponseName(), result.getMessageType());
	}

	@Test
	public void groupCallTest() throws Exception {
		SCMPEchoSCCall echoCall = (SCMPEchoSCCall) SCMPCallFactory.ECHO_SC_CALL.newInstance(client);
		ISCMPCall groupCall = echoCall.openGroup();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 10; i++) {
			sb.append(i);
			groupCall.setBody(String.valueOf(i));
			groupCall.invoke();
		}
		SCMPMessage res = groupCall.closeGroup(); // send REQ (no body content)
		Assert.assertEquals(sb.toString(), res.getBody());
		Assert.assertEquals(SCMPBodyType.text.getName(), res.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
//		Assert.assertEquals("1/10", res.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		Assert.assertEquals(sb.length() + "", res.getHeader(SCMPHeaderAttributeKey.BODY_LENGTH.getName()));
		Assert.assertEquals(SCMPMsgType.ECHO_SC.getResponseName(), res.getMessageType());

	}
	
	@Test
	public void groupCallLargePartsTest() throws Exception {
		SCMPEchoSCCall echoCall = (SCMPEchoSCCall) SCMPCallFactory.ECHO_SC_CALL.newInstance(client);
		ISCMPCall groupCall = echoCall.openGroup();
		StringBuilder sb = new StringBuilder();
		StringBuilder expected = new StringBuilder();
		for(int i = 0; i < 19000; i++) {
			sb.append(i);
		}
		int max = 2;
		for (int i = 0; i < max; i++) {
			expected.append(sb.toString());
			groupCall.setBody(sb.toString());
			groupCall.invoke();
		}
		SCMPMessage res = groupCall.closeGroup(); // send REQ (no body content)
		Assert.assertEquals(expected.toString(), res.getBody());
		Assert.assertEquals(SCMPBodyType.text.getName(), res.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		//Assert.assertEquals("1/" + (max * 4), res.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		Assert.assertEquals(expected.length() + "", res.getHeader(SCMPHeaderAttributeKey.BODY_LENGTH.getName()));
		Assert.assertEquals(SCMPMsgType.ECHO_SC.getResponseName(), res.getMessageType());

	}
}
