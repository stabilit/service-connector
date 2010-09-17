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
package test.serviceconnector.session;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnCreateSessionCall;
import org.serviceconnector.call.SCMPClnDeleteSessionCall;
import org.serviceconnector.call.SCMPInspectCall;
import org.serviceconnector.common.scmp.SCMPError;
import org.serviceconnector.common.scmp.SCMPFault;
import org.serviceconnector.common.scmp.SCMPMessage;
import org.serviceconnector.common.scmp.SCMPMsgType;

import test.serviceconnector.attach.SuperAttachTestCase;
import test.serviceconnector.unit.SCTest;


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

		// echoInterval not valid
		createSessionCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		createSessionCall.setEchoIntervalSeconds(0);
		createSessionCall.getRequest().setServiceName("simulation");
		createSessionCall.invoke(this.attachCallback, 1000);
		SCMPMessage fault = this.attachCallback.getMessageSync();
		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.HV_WRONG_ECHO_INTERVAL, " [IntValue 0 not within limits]",
				SCMPMsgType.CLN_CREATE_SESSION);

		// serviceName not set
		createSessionCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		createSessionCall.getRequest().setServiceName(null);
		createSessionCall.setEchoIntervalSeconds(300);
		createSessionCall.invoke(this.attachCallback, 1000);
		fault = this.attachCallback.getMessageSync();
		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.HV_WRONG_SERVICE_NAME, " [serviceName must be set]",
				SCMPMsgType.CLN_CREATE_SESSION);

		// sessionInfo not set
		createSessionCall.setSessionInfo(null);
		createSessionCall.setEchoIntervalSeconds(300);
		createSessionCall.getRequest().setServiceName("simulation");
		createSessionCall.invoke(this.attachCallback, 1000);
		fault = this.attachCallback.getMessageSync();
		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.HV_WRONG_SESSION_INFO, " [StringValue must be set]",
				SCMPMsgType.CLN_CREATE_SESSION);
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
		createSessionCall.invoke(this.attachCallback, 1000);
		SCMPMessage responseMessage = this.attachCallback.getMessageSync();
		String sessId = responseMessage.getSessionId();
		/*************************** verify create session **********************************/
		Assert.assertNotNull(sessId);

		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		inspectCall.invoke(this.attachCallback, 1000);
		SCMPMessage inspect = this.attachCallback.getMessageSync();
		/*********************************** Verify registry entries in SC ********************************/
		String inspectMsg = (String) inspect.getBody();
		Map<String, String> inspectMap = SCTest.convertInspectStringToMap(inspectMsg);
		String expectedScEntry = sessId + ":" + sessId + ":simulation_localhost/:7000 : 10|";
		String scEntry = inspectMap.get("sessionRegistry");
		SCTest.assertEqualsUnorderedStringIgnorePorts(expectedScEntry, scEntry);

		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(this.req, responseMessage.getServiceName(), responseMessage.getSessionId());
		deleteSessionCall.invoke(this.attachCallback, 1000);
		this.attachCallback.getMessageSync();

		/*********************************** Verify registry entries in SC ********************************/
		inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		inspectCall.invoke(this.attachCallback, 1000);
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
		createSessionCall.setRequestBody("reject");
		createSessionCall.invoke(this.attachCallback, 4000);
		SCMPMessage responseMessage = this.attachCallback.getMessageSync();
		String sessId = responseMessage.getSessionId();
		Assert.assertNull(sessId);

		/*********************************** Verify registry entries in SC ********************************/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		inspectCall.invoke(this.attachCallback, 4000);
		SCMPMessage inspect = this.attachCallback.getMessageSync();
		String inspectMsg = (String) inspect.getBody();
		Map<String, String> inspectMap = SCTest.convertInspectStringToMap(inspectMsg);
		String scEntry = (String) inspectMap.get("sessionRegistry");
		Assert.assertEquals("", scEntry);
	}
}
