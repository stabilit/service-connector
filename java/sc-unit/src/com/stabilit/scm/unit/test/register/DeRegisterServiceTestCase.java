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
package com.stabilit.scm.unit.test.register;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.scm.cln.call.SCMPCallException;
import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPDeRegisterServiceCall;
import com.stabilit.scm.cln.call.SCMPInspectCall;
import com.stabilit.scm.common.msg.impl.InspectMessage;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.unit.test.SCTest;

public class DeRegisterServiceTestCase extends SuperRegisterTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public DeRegisterServiceTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void deRegisterServiceCall() throws Exception {
		SCMPDeRegisterServiceCall deRegisterServiceCall = (SCMPDeRegisterServiceCall) SCMPCallFactory.DEREGISTER_SERVICE_CALL
				.newInstance(client);

		deRegisterServiceCall.setServiceName("P01_RTXS_RPRWS1");
		deRegisterServiceCall.invoke();

		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(client);
		SCMPMessage inspect = inspectCall.invoke();

		/*********************************** Verify registry entries in SC ********************************/
		InspectMessage inspectMsg = (InspectMessage) inspect.getBody();
		String scEntry = (String) inspectMsg.getAttribute("serviceRegistry");
		String expectedEnty = "simulation:SCMP [header={messageID=1, portNr=7000, maxSessions=1, msgType=REGISTER_SERVICE, multiThreaded=1, serviceName=simulation}]";
		Assert.assertEquals(expectedEnty, scEntry);
		Assert.assertEquals("4", inspect.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		super.registerServiceBefore();
	}

	@Test
	public void secondDeRegisterServiceCall() throws Exception {
		super.deRegisterServiceAfter();
		SCMPDeRegisterServiceCall deRegisterServiceCall = (SCMPDeRegisterServiceCall) SCMPCallFactory.DEREGISTER_SERVICE_CALL
				.newInstance(client);

		deRegisterServiceCall.setServiceName("P01_RTXS_RPRWS1");

		try {
			deRegisterServiceCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPCallException e) {
			SCMPFault scmpFault = e.getFault();
			Assert.assertEquals("4", scmpFault.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
			SCTest.verifyError(e.getFault(), SCMPError.NOT_REGISTERED,
					SCMPMsgType.DEREGISTER_SERVICE);
		}
		super.registerServiceBefore();
	}
}
