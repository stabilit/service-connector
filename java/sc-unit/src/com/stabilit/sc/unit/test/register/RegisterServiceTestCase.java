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
package com.stabilit.sc.unit.test.register;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.sc.cln.msg.impl.InspectMessage;
import com.stabilit.sc.cln.service.SCMPCallFactory;
import com.stabilit.sc.cln.service.SCMPDeRegisterServiceCall;
import com.stabilit.sc.cln.service.SCMPInspectCall;
import com.stabilit.sc.cln.service.SCMPRegisterServiceCall;
import com.stabilit.sc.cln.service.SCMPServiceException;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.scmp.SCMPErrorCode;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.unit.test.SCTest;
import com.stabilit.sc.unit.test.SuperTestCase;

public class RegisterServiceTestCase extends SuperTestCase {

	/**
	 * @param fileName
	 */
	public RegisterServiceTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void failRegisterServiceCall() throws Exception {
		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(client);

		/*********************** serviceName not set *******************/
		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setMultithreaded(true);
		registerServiceCall.setPortNumber(9100);

		try {
			registerServiceCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPServiceException ex) {
			SCTest.verifyError(ex.getFault(), SCMPErrorCode.VALIDATION_ERROR, SCMPMsgType.REGISTER_SERVICE);
		}
		/*********************** maxSessions 0 value *******************/
		registerServiceCall.setServiceName("P01_RTXS_RPRWS1");
		registerServiceCall.setMaxSessions(0);

		try {
			registerServiceCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPServiceException ex) {
			SCTest.verifyError(ex.getFault(), SCMPErrorCode.VALIDATION_ERROR, SCMPMsgType.REGISTER_SERVICE);
		}
		/*********************** port too high 10000 *******************/
		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setPortNumber(910000);

		try {
			registerServiceCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPServiceException ex) {
			SCTest.verifyError(ex.getFault(), SCMPErrorCode.VALIDATION_ERROR, SCMPMsgType.REGISTER_SERVICE);
		}
	}

	@Test
	public void registerServiceCall() throws Exception {
		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(client);

		registerServiceCall.setServiceName("P01_RTXS_RPRWS1");
		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setMultithreaded(true);
		registerServiceCall.setPortNumber(9100);

		registerServiceCall.invoke();
		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(client);
		SCMP inspect = inspectCall.invoke();

		/*********************************** Verify registry entries in SC ********************************/
		InspectMessage inspectMsg = (InspectMessage) inspect.getBody();
		String expectedScEntry = "P01_RTXS_RPRWS1:SCMP [header={messageID=1, portNr=9100, maxSessions=10, msgType=REGISTER_SERVICE, multiThreaded=1, serviceName=P01_RTXS_RPRWS1}]simulation:SCMP [header={messageID=1, portNr=7000, maxSessions=1, msgType=REGISTER_SERVICE, serviceName=simulation}]";
		String scEntry = (String) inspectMsg.getAttribute("serviceRegistry");
		Assert.assertEquals(expectedScEntry, scEntry);
	}

	@Test
	public void secondRegisterServiceCall() throws Exception {
		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(client);

		registerServiceCall.setServiceName("P01_RTXS_RPRWS1");
		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setMultithreaded(true);
		registerServiceCall.setPortNumber(9100);

		try {
			registerServiceCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPServiceException e) {
			SCTest.verifyError(e.getFault(), SCMPErrorCode.ALREADY_REGISTERED, SCMPMsgType.REGISTER_SERVICE);
		}

		SCMPDeRegisterServiceCall deRegisterServiceCall = (SCMPDeRegisterServiceCall) SCMPCallFactory.DEREGISTER_SERVICE_CALL
				.newInstance(client);
		deRegisterServiceCall.setServiceName("P01_RTXS_RPRWS1");
		deRegisterServiceCall.invoke();
	}
}
