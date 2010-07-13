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

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.scm.cln.call.SCMPCallException;
import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPDeRegisterServiceCall;
import com.stabilit.scm.cln.call.SCMPInspectCall;
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
				.newInstance(this.registerRequester, "publish-simulation");

		deRegisterServiceCall.invoke();

		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		SCMPMessage inspect = inspectCall.invoke();

		/*********************************** Verify registry entries in SC ********************************/
		String inspectMsg = (String) inspect.getBody();
		Map<String, String> inspectMap = SCTest.convertInspectStringToMap(inspectMsg);

		String scEntry = (String) inspectMap.get("serviceRegistry");
		String expectedEntry = "P01_logging:0|publish-simulation:0|P01_RTXS_sc1:0|simulation:0 - simulation_localhost/127.0.0.1: : 7000 : 1|P01_BCST_CH_sc1:0|";
		SCTest.assertEqualsUnorderedStringIgnorePorts(expectedEntry, scEntry);
		Assert.assertEquals("2", inspect.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		super.registerServiceBefore();
	}

	@Test
	public void secondDeRegisterServiceCall() throws Exception {
		super.deRegisterServiceAfter();
		SCMPDeRegisterServiceCall deRegisterServiceCall = (SCMPDeRegisterServiceCall) SCMPCallFactory.DEREGISTER_SERVICE_CALL
				.newInstance(this.registerRequester, "publish-simulation");

		try {
			deRegisterServiceCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPCallException e) {
			SCMPFault scmpFault = e.getFault();
			Assert.assertEquals("3", scmpFault.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
			SCTest.verifyError(e.getFault(), SCMPError.NOT_REGISTERED, SCMPMsgType.DEREGISTER_SERVICE);
		}
		super.registerServiceBefore();
	}
}
