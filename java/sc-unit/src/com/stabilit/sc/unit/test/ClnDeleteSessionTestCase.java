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
package com.stabilit.sc.unit.test;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.sc.cln.msg.impl.InspectMessage;
import com.stabilit.sc.cln.service.SCMPCallFactory;
import com.stabilit.sc.cln.service.SCMPClnDeleteSessionCall;
import com.stabilit.sc.cln.service.SCMPInspectCall;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPMsgType;

public class ClnDeleteSessionTestCase extends SuperSessionTestCase {

	@Test
	public void clnDeleteSession() throws Exception {
		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(client, scmpSession);
		SCMP result = deleteSessionCall.invoke();

		/*************************** verify create session **********************************/
		Assert.assertNull(result.getBody());
		Assert.assertEquals(SCMPMsgType.CLN_DELETE_SESSION.getResponseName(), result.getMessageType());
		Assert.assertNotNull(result.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME));

		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(client);
		SCMP inspect = inspectCall.invoke();

		/*********************************** Verify registry entries in SC ********************************/
		InspectMessage inspectMsg = (InspectMessage) inspect.getBody();
		String scEntry = (String) inspectMsg.getAttribute("sessionRegistry");
		Assert.assertEquals("", scEntry);
		super.createSession();
	}
}
