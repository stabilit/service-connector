/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
package com.stabilit.sc.unit.test;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.sc.cln.io.SCMPSession;
import com.stabilit.sc.cln.msg.impl.InspectMessage;
import com.stabilit.sc.cln.service.SCMPCallFactory;
import com.stabilit.sc.cln.service.SCMPCreateSessionCall;
import com.stabilit.sc.cln.service.SCMPDeleteSessionCall;
import com.stabilit.sc.cln.service.SCMPInspectCall;
import com.stabilit.sc.cln.service.SCMPServiceException;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPMsgType;


public class CreateSessionTestCase extends SuperConnectTestCase{

	private SCMPSession scmpSession = null;

	@Test
	public void failCreateSession() throws Exception {
		SCMPCreateSessionCall createSessionCall = (SCMPCreateSessionCall) SCMPCallFactory.CREATE_SESSION_CALL
				.newInstance(client);

		/*********************** serviceName not set *******************/
		createSessionCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");

		try {
			createSessionCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPServiceException ex) {
			SCTest.verifyError(ex.getFault(), SCMPErrorCode.VALIDATION_ERROR, SCMPMsgType.CREATE_SESSION);
		}
	}

	@Test
	public void createSession() throws Exception {
		SCMPCreateSessionCall createSessionCall = (SCMPCreateSessionCall) SCMPCallFactory.CREATE_SESSION_CALL
				.newInstance(client);

		createSessionCall.setServiceName("simulation");
		createSessionCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");

		scmpSession = createSessionCall.invoke();
		/*************************** verify create session **********************************/
		Assert.assertNull(scmpSession.getBody());
		Assert.assertEquals(SCMPMsgType.CREATE_SESSION.getResponseName(), scmpSession.getMessageType());
		Assert.assertNotNull(scmpSession.getSessionId());
		Assert.assertNotNull(scmpSession.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME));

		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL
				.newInstance(client);
		SCMP inspect = inspectCall.invoke();
		/*********************************** Verify registry entries in SC ********************************/
		InspectMessage inspectMsg = (InspectMessage) inspect.getBody();
		String expectedScEntry = ":com.stabilit.sc.registry.ServiceRegistryItem=portNr=7000;maxSessions=1;msgType=REGISTER_SERVICE;serviceName=simulation;;";
		String scEntry = (String) inspectMsg.getAttribute("sessionRegistry");
		scEntry = scEntry.substring(scEntry.indexOf(":"));
		Assert.assertEquals(expectedScEntry, scEntry);
		
		SCMPDeleteSessionCall deleteSessionCall = (SCMPDeleteSessionCall) SCMPCallFactory.DELETE_SESSION_CALL.newInstance(client, scmpSession);
		deleteSessionCall.invoke();
	}
}
