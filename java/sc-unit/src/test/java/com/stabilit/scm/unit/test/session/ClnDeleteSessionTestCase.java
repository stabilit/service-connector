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
import com.stabilit.scm.common.call.SCMPClnDeleteSessionCall;
import com.stabilit.scm.common.call.SCMPInspectCall;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.unit.test.SCTest;

public class ClnDeleteSessionTestCase extends SuperSessionTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public ClnDeleteSessionTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void clnDeleteSession() throws Exception {
		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(req, "simulation", this.sessionId);
		deleteSessionCall.invoke(this.sessionCallback);
		SCMPMessage result = this.sessionCallback.getMessageSync();

		/*************************** verify delete session **********************************/
		Assert.assertNull(result.getBody());
		Assert.assertEquals(SCMPMsgType.CLN_DELETE_SESSION.getValue(), result.getMessageType());
		Assert.assertNotNull(result.getServiceName());

		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		inspectCall.invoke(this.sessionCallback);
		SCMPMessage inspect = this.sessionCallback.getMessageSync();

		/*********************************** Verify registry entries in SC ********************************/
		String inspectMsg = (String) inspect.getBody();
		Map<String, String> inspectMap = SCTest.convertInspectStringToMap(inspectMsg);
		String scEntry = (String) inspectMap.get("sessionRegistry");
		Assert.assertEquals("", scEntry);
		super.clnCreateSessionBefore();
	}
}
