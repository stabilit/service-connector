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
package com.stabilit.scm.unit.test.session;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.scm.cln.call.SCMPCallException;
import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPClnCreateSessionCall;
import com.stabilit.scm.cln.call.SCMPInspectCall;
import com.stabilit.scm.cln.msg.impl.InspectMessage;
import com.stabilit.scm.cln.scmp.SCMPServiceSession;
import com.stabilit.scm.scmp.SCMPError;
import com.stabilit.scm.scmp.SCMPFault;
import com.stabilit.scm.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.scmp.SCMPMessage;
import com.stabilit.scm.scmp.SCMPMsgType;
import com.stabilit.scm.unit.test.SCTest;
import com.stabilit.scm.unit.test.attach.SuperAttachTestCase;

/**
 * The Class ClnCreateSessionTestCase.
 */
public class ClnCreateSessionTestCase extends SuperAttachTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public ClnCreateSessionTestCase(String fileName) {
		super(fileName);
	}

	/**
	 * Fail cln create session not connected.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void failClnCreateSessionNotConnected() throws Exception {
		this.clnDetachAfter();
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(client);

		createSessionCall.setServiceName("simulation");
		createSessionCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		try {
			createSessionCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPCallException ex) {
			SCMPFault scmpFault = ex.getFault();
			Assert.assertEquals("3", scmpFault.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
			SCTest.verifyError(ex.getFault(), SCMPError.NOT_ATTACHED, SCMPMsgType.CLN_CREATE_SESSION);
		}
		this.clnAttachBefore();
	}

	/**
	 * Fail cln create session wrong header.
	 * 
	 * @throws Exception
	 *             the exception
	 */
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
			SCMPFault scmpFault = ex.getFault();
			Assert.assertEquals("2", scmpFault.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
			SCTest.verifyError(ex.getFault(), SCMPError.VALIDATION_ERROR, SCMPMsgType.CLN_CREATE_SESSION);
		}
	}

	/**
	 * Cln create session.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void clnCreateSession() throws Exception {
		SCMPServiceSession localSession = new SCMPServiceSession(client);
		localSession.setServiceName("simulation");
		localSession.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		localSession.createSession();
		/*************************** verify create session **********************************/
		Assert.assertNotNull(localSession.getSessionId());

		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(client);
		SCMPMessage inspect = inspectCall.invoke();
		/*********************************** Verify registry entries in SC ********************************/
		InspectMessage inspectMsg = (InspectMessage) inspect.getBody();
		String expectedScEntry = ":com.stabilit.scm.registry.ServiceRegistryItem=messageID=1;portNr=7000;maxSessions=1;msgType=REGISTER_SERVICE;multiThreaded=1;serviceName=simulation;;";
		String scEntry = (String) inspectMsg.getAttribute("sessionRegistry");
		scEntry = scEntry.substring(scEntry.indexOf(":"));
		Assert.assertEquals(expectedScEntry, scEntry);
		Assert.assertEquals("3", inspect.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		
		localSession.deleteSession();
	}
}
