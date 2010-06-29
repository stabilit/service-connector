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

import com.stabilit.scm.cln.call.ISCMPCall;
import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPEchoSCCall;
import com.stabilit.scm.common.conf.RequesterConfigPool;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.scmp.SCMPBodyType;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.unit.TestContext;
import com.stabilit.scm.unit.test.SetupTestCases;
import com.stabilit.scm.unit.test.SuperTestCase;

public class EchoSCLargeTestCase extends SuperTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 * @throws Exception 
	 */
	public EchoSCLargeTestCase(String fileName) throws Exception {
		super(fileName);
	}

	@Before
	@Override
	public void setup() throws Exception {
		SetupTestCases.setupSC();
		try {
			config = new RequesterConfigPool();
			config.load(fileName);
			this.testContext = new TestContext(this.config.getRequesterConfig());
			req = new Requester(this.testContext);
			req.connect(); // physical connect
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Test
	public void invokeTwoPartsTest() throws Exception {
		SCMPEchoSCCall echoCall = (SCMPEchoSCCall) SCMPCallFactory.ECHO_SC_CALL.newInstance(req);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 19000; i++) {
			sb.append(i);
		}
		echoCall.setRequestBody(sb.toString());
		SCMPMessage result = echoCall.invoke();
		/*************************** verify echo session **********************************/
		Map<String, String> header = result.getHeader();
		Assert.assertEquals(sb.toString(), result.getBody());
		Assert.assertEquals(SCMPBodyType.text.getName(), header.get(SCMPHeaderAttributeKey.BODY_TYPE.getName()));
		Assert.assertEquals("2/2", result.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		Assert.assertEquals(sb.length() + "", header.get(SCMPHeaderAttributeKey.BODY_LENGTH.getName()));
		Assert.assertEquals(SCMPMsgType.ECHO_SC.getName(), result.getMessageType());
	}

	@Test
	public void groupCallTest() throws Exception {
		SCMPEchoSCCall echoCall = (SCMPEchoSCCall) SCMPCallFactory.ECHO_SC_CALL.newInstance(req);
		ISCMPCall groupCall = echoCall.openGroup();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 10; i++) {
			sb.append(i);
			groupCall.setRequestBody(String.valueOf(i));
			groupCall.invoke();
		}
		SCMPMessage res = groupCall.closeGroup(); // send REQ (no body content)
		Assert.assertEquals(sb.toString(), res.getBody());
		Assert.assertEquals(SCMPBodyType.text.getName(), res.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertEquals("1/10", res.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		Assert.assertEquals(sb.length() + "", res.getHeader(SCMPHeaderAttributeKey.BODY_LENGTH.getName()));
		Assert.assertEquals(SCMPMsgType.ECHO_SC.getName(), res.getMessageType());
	}

	@Test
	public void groupCallLargePartsTest() throws Exception {
		SCMPEchoSCCall echoCall = (SCMPEchoSCCall) SCMPCallFactory.ECHO_SC_CALL.newInstance(req);
		ISCMPCall groupCall = echoCall.openGroup();
		StringBuilder sb = new StringBuilder();
		StringBuilder expected = new StringBuilder();
		for (int i = 0; i < 19000; i++) {
			sb.append(i);
		}
		int max = 2;
		for (int i = 0; i < max; i++) {
			expected.append(sb.toString());
			groupCall.setRequestBody(sb.toString());
			groupCall.invoke();
		}
		SCMPMessage res = groupCall.closeGroup(); // send REQ (no body content)
		Assert.assertEquals(expected.toString(), res.getBody());
		Assert.assertEquals(SCMPBodyType.text.getName(), res.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertEquals("2/3", res.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		Assert.assertEquals(expected.length() + "", res.getHeader(SCMPHeaderAttributeKey.BODY_LENGTH.getName()));
		Assert.assertEquals(SCMPMsgType.ECHO_SC.getName(), res.getMessageType());
	}
}
