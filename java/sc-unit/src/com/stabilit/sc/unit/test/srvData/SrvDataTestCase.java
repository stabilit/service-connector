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
package com.stabilit.sc.unit.test.srvData;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.sc.cln.call.SCMPCallFactory;
import com.stabilit.sc.cln.call.SCMPClnCreateSessionCall;
import com.stabilit.sc.cln.call.SCMPClnDataCall;
import com.stabilit.sc.cln.call.SCMPClnDeleteSessionCall;
import com.stabilit.sc.cln.call.SCMPClnSystemCall;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.scmp.SCMPBodyType;
import com.stabilit.sc.scmp.SCMPErrorCode;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.unit.test.SCTest;
import com.stabilit.sc.unit.test.session.SuperSessionTestCase;

/**
 * @author JTraber
 * 
 */
public class SrvDataTestCase extends SuperSessionTestCase {

	/**
	 * @param fileName
	 */
	public SrvDataTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void multipleSrvDataTest() throws Exception {

		for (int i = 0; i < 100; i++) {
			SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(client,
					scmpSession);
			clnDataCall.setMessagInfo("message info");
			clnDataCall.setBody("get Data (query)");
			SCMP scmpReply = clnDataCall.invoke();

			Assert.assertEquals("Message number " + i, scmpReply.getBody());
			Assert.assertEquals(SCMPBodyType.text.getName(), scmpReply
					.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
			int bodyLength = (i + "").length() + 15;
			Assert.assertEquals(bodyLength + "", scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_LENGTH));
			Assert.assertNotNull(scmpReply.getHeader(SCMPHeaderAttributeKey.SESSION_INFO));
			Assert.assertEquals(SCMPMsgType.CLN_DATA.getResponseName(), scmpReply.getMessageType());
			String serviceName = clnDataCall.getCall().getHeader(SCMPHeaderAttributeKey.SERVICE_NAME);
			String sessionId = clnDataCall.getCall().getSessionId();
			Assert.assertEquals(serviceName, scmpReply.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME));
			Assert.assertEquals(sessionId, scmpReply.getSessionId());
		}
	}
	
	public void clnDataSimulationServerDisconnectAfterCreateSession() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(client);

		createSessionCall.setServiceName("simulation");
		createSessionCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		scmpSession = createSessionCall.invoke();

		// disconnects simulation server from SC after sending response
		SCMPClnSystemCall systemCall = (SCMPClnSystemCall) SCMPCallFactory.CLN_SYSTEM_CALL.newInstance(
				client, scmpSession);
		systemCall.setMaxNodes(2);
		systemCall.invoke();

		// data call should fail because connection lost to simulation server
		SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(client,
				scmpSession);
		clnDataCall.setServiceName("simulation");
		clnDataCall.setMessagInfo("asdasd");
		clnDataCall.setBody("hello");
		clnDataCall.invoke();
		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(client, scmpSession);

		SCMP result = null;
		try {
			result = deleteSessionCall.invoke();
		} catch (Exception e) {
			SCTest.verifyError(result, SCMPErrorCode.SERVER_ERROR, SCMPMsgType.CLN_DATA);
		}
		//TODO verify that session is not longer available on SC with an inspect
	}
}