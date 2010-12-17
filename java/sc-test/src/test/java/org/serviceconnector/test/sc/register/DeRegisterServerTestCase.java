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
package org.serviceconnector.test.sc.register;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.serviceconnector.TestUtil;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPDeRegisterServerCall;
import org.serviceconnector.call.SCMPInspectCall;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.sc.SCTest;

public class DeRegisterServerTestCase extends SuperRegisterTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public DeRegisterServerTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void deRegisterServerCall() throws Exception {
		SCMPDeRegisterServerCall deRegisterServerCall = (SCMPDeRegisterServerCall) SCMPCallFactory.DEREGISTER_SERVER_CALL
				.newInstance(this.registerRequester, "publish-1");

		deRegisterServerCall.invoke(this.attachCallback, 1000);
		TestUtil.checkReply(this.attachCallback.getMessageSync(3000));
		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		inspectCall.invoke(this.attachCallback, 1000);
		SCMPMessage inspect = this.attachCallback.getMessageSync(3000);

		/*********************************** Verify registry entries in SC ********************************/
		String inspectMsg = (String) inspect.getBody();
		Map<String, String> inspectMap = SCTest.convertInspectStringToMap(inspectMsg);

		String scEntry = (String) inspectMap.get("serviceRegistry"); // TODO TRN this will not work if config is changed!
		String expectedEntry = "sc2-session-2:0|sc1-session-1:0|session-1:1 - session-1_localhost/:30000 : 10 - session-1_localhost/:41000 : 1 - session-1_localhost/:42000 : 1|sc1-publish-1:0|publish-2:0|sc2-publish-2:0|file-1:file-1:ENABLED:file|session-2:0|publish-1:0 - publish-1_localhost/:51000 : 1|";
		SCTest.assertEqualsUnorderedStringIgnorePorts(expectedEntry, scEntry);
		super.registerServerBefore();
	}

	@Test
	public void secondDeRegisterServerCall() throws Exception {
		super.deRegisterServerAfter();
		SCMPDeRegisterServerCall deRegisterServerCall = (SCMPDeRegisterServerCall) SCMPCallFactory.DEREGISTER_SERVER_CALL
				.newInstance(this.registerRequester, "publish-1");

		deRegisterServerCall.invoke(this.attachCallback, 1000);
		SCMPMessage fault = this.attachCallback.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		Assert.assertEquals(SCMPMsgType.DEREGISTER_SERVER.getValue(), fault.getHeader(SCMPHeaderAttributeKey.MSG_TYPE));
		Assert.assertEquals(SCMPError.NOT_FOUND.getErrorCode(), fault.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
		super.registerServerBefore();
	}
}
