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

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPClnCreateSessionCall;
import com.stabilit.scm.common.call.SCMPClnDeleteSessionCall;
import com.stabilit.scm.common.call.SCMPInspectCall;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
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
	 * Fail client create session wrong header.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void failClnCreateSessionWrongHeader() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(req, "simulation");

		// echoTimeout not set
		createSessionCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		createSessionCall.setEchoIntervalSeconds(300);
		createSessionCall.getRequest().setServiceName("simulation");
		createSessionCall.invoke(this.attachCallback);
		SCMPMessage fault = this.attachCallback.getMessageSync();
		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.HEADER_VALIDATION_ERROR, SCMPMsgType.CLN_CREATE_SESSION);

		// echoInterval not valid
		createSessionCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		createSessionCall.setEchoIntervalSeconds(0);
		createSessionCall.setEchoTimeoutSeconds(10);
		createSessionCall.getRequest().setServiceName("simulation");
		createSessionCall.invoke(this.attachCallback);
		fault = this.attachCallback.getMessageSync();
		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.HEADER_VALIDATION_ERROR, SCMPMsgType.CLN_CREATE_SESSION);

		// serviceName not set
		createSessionCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		createSessionCall.getRequest().setServiceName(null);
		createSessionCall.setEchoIntervalSeconds(300);
		createSessionCall.setEchoTimeoutSeconds(10);
		createSessionCall.invoke(this.attachCallback);
		fault = this.attachCallback.getMessageSync();
		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.HEADER_VALIDATION_ERROR, SCMPMsgType.CLN_CREATE_SESSION);
		// serviceName not set

		// sessionInfo not set
		createSessionCall.setSessionInfo(null);
		createSessionCall.setEchoIntervalSeconds(300);
		createSessionCall.getRequest().setServiceName("simulation");
		createSessionCall.setEchoTimeoutSeconds(10);
		createSessionCall.invoke(this.attachCallback);
		fault = this.attachCallback.getMessageSync();
		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.HEADER_VALIDATION_ERROR, SCMPMsgType.CLN_CREATE_SESSION);
	}

	/**
	 * Cln create session.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void clnCreateSession() throws Exception {
		// sets up a create session call
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(req, "simulation");
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoIntervalSeconds(300);
		createSessionCall.setEchoTimeoutSeconds(10);
		createSessionCall.invoke(this.attachCallback);
		SCMPMessage responseMessage = this.attachCallback.getMessageSync();
		String sessId = responseMessage.getSessionId();
		/*************************** verify create session **********************************/
		Assert.assertNotNull(sessId);

		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		inspectCall.invoke(this.attachCallback);
		SCMPMessage inspect = this.attachCallback.getMessageSync();
		/*********************************** Verify registry entries in SC ********************************/
		String inspectMsg = (String) inspect.getBody();
		Map<String, String> inspectMap = SCTest.convertInspectStringToMap(inspectMsg);
		String expectedScEntry = sessId + ":" + sessId + ":simulation_localhost/127.0.0.1: : 7000 : 10|";
		String scEntry = inspectMap.get("sessionRegistry");
		SCTest.assertEqualsUnorderedStringIgnorePorts(expectedScEntry, scEntry);

		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(this.req, responseMessage.getServiceName(), responseMessage.getSessionId());
		deleteSessionCall.invoke(this.attachCallback);
		this.attachCallback.getMessageSync();

		/*********************************** Verify registry entries in SC ********************************/
		inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		inspectCall.invoke(this.attachCallback);
		inspect = this.attachCallback.getMessageSync();
		inspectMsg = (String) inspect.getBody();
		inspectMap = SCTest.convertInspectStringToMap(inspectMsg);
		scEntry = (String) inspectMap.get("sessionRegistry");
		Assert.assertEquals("", scEntry);
	}

	@Test
	public void rejectedSession() throws Exception {
		// sets up a create session call
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(req, "simulation");
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoIntervalSeconds(300);
		createSessionCall.setEchoTimeoutSeconds(10);
		createSessionCall.setRequestBody("reject");
		createSessionCall.invoke(this.attachCallback);
		SCMPMessage responseMessage = this.attachCallback.getMessageSync();
		String sessId = responseMessage.getSessionId();
		Assert.assertNull(sessId);

		/*********************************** Verify registry entries in SC ********************************/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		inspectCall.invoke(this.attachCallback);
		SCMPMessage inspect = this.attachCallback.getMessageSync();
		String inspectMsg = (String) inspect.getBody();
		Map<String, String> inspectMap = SCTest.convertInspectStringToMap(inspectMsg);
		String scEntry = (String) inspectMap.get("sessionRegistry");
		Assert.assertEquals("", scEntry);
	}
}
