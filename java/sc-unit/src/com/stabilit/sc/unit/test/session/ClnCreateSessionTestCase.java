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
package com.stabilit.sc.unit.test.session;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.sc.cln.call.SCMPCallException;
import com.stabilit.sc.cln.call.SCMPCallFactory;
import com.stabilit.sc.cln.call.SCMPClnCreateSessionCall;
import com.stabilit.sc.cln.call.SCMPClnDeleteSessionCall;
import com.stabilit.sc.cln.call.SCMPInspectCall;
import com.stabilit.sc.cln.msg.impl.InspectMessage;
import com.stabilit.sc.cln.scmp.SCMPSession;
import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.scmp.SCMPError;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.unit.test.SCTest;
import com.stabilit.sc.unit.test.connect.SuperConnectTestCase;

public class ClnCreateSessionTestCase extends SuperConnectTestCase {

	private SCMPSession scmpSession = null;

	/**
	 * @param fileName
	 */
	public ClnCreateSessionTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void failClnCreateSessionNotConnected() throws Exception {
		this.clnDisconnectAfter();
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(client);

		createSessionCall.setServiceName("simulation");
		createSessionCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		try {
			createSessionCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPCallException ex) {
			SCTest.verifyError(ex.getFault(), SCMPError.NOT_CONNECTED, SCMPMsgType.CLN_CREATE_SESSION);
		}
		this.clnConnectBefore();
	}

	@Test
	public void failClnCreateSessionWrongHeader() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(client);

		/*********************** serviceName not set *******************/
		createSessionCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");

		try {
			createSessionCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPCallException ex) {
			SCTest.verifyError(ex.getFault(), SCMPError.VALIDATION_ERROR, SCMPMsgType.CLN_CREATE_SESSION);
		}
	}

	@Test
	public void clnCreateSession() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(client);

		createSessionCall.setServiceName("simulation");
		createSessionCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");

		scmpSession = createSessionCall.invoke();
		/*************************** verify create session **********************************/
		Assert.assertNull(scmpSession.getBody());
		Assert.assertEquals(SCMPMsgType.CLN_CREATE_SESSION.getResponseName(), scmpSession.getMessageType());
		Assert.assertNotNull(scmpSession.getSessionId());
		Assert.assertNotNull(scmpSession.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME));

		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(client);
		SCMPMessage inspect = inspectCall.invoke();
		/*********************************** Verify registry entries in SC ********************************/
		InspectMessage inspectMsg = (InspectMessage) inspect.getBody();
		String expectedScEntry = ":com.stabilit.sc.registry.ServiceRegistryItem=messageID=1;portNr=7000;maxSessions=1;msgType=REGISTER_SERVICE;serviceName=simulation;;";
		String scEntry = (String) inspectMsg.getAttribute("sessionRegistry");
		scEntry = scEntry.substring(scEntry.indexOf(":"));
		Assert.assertEquals(expectedScEntry, scEntry);

		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(client, scmpSession);
		deleteSessionCall.invoke();
	}
}
