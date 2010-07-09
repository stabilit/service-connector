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
package com.stabilit.scm.unit.test.attach;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.scm.cln.call.SCMPAttachCall;
import com.stabilit.scm.cln.call.SCMPCallException;
import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPDetachCall;
import com.stabilit.scm.cln.call.SCMPInspectCall;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.unit.test.SCTest;

public class DetachTestCase extends SuperAttachTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public DetachTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void secondAttach() throws Exception {
		SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(req);

		attachCall.setKeepAliveTimeout(30);
		attachCall.setKeepAliveInterval(360);

		try {
			attachCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPCallException e) {
			SCMPFault scmpFault = e.getFault();
			Assert.assertEquals("2", scmpFault.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
			SCTest.verifyError(scmpFault, SCMPError.ALREADY_ATTACHED, SCMPMsgType.ATTACH);
		}
	}

	@Test
	public void detach() throws Exception {
		SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(req);

		SCMPMessage result = null;
		try {
			result = detachCall.invoke();
		} catch (SCMPCallException e) {
			Assert.fail();
		}

		/*********************************** Verify detach response msg **********************************/
		Assert.assertNull(result.getBody());
		Assert
				.assertEquals(result.getHeader(SCMPHeaderAttributeKey.MSG_TYPE), SCMPMsgType.DETACH
						.getValue());
		Assert.assertEquals("2", result.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));

		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		SCMPMessage inspect = inspectCall.invoke();
		/*********************************** Verify registry entries in SC ***********************************/
		String inspectMsg = (String) inspect.getBody();
		Map<String, String> inspectMap = SCTest.convertInspectStringToMap(inspectMsg);
		
		String scEntry = (String) inspectMap.get("clientRegistry");
		Assert.assertEquals("", scEntry);
		Assert.assertEquals("3", inspect.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		super.clnAttachBefore();
	}

	@Test
	public void secondDetach() throws Exception {
		super.clnDetachAfter();
		SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(req);
		try {
			detachCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPCallException e) {
			SCMPFault scmpFault = e.getFault();
			Assert.assertEquals("3", scmpFault.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
			SCTest.verifyError(scmpFault, SCMPError.UNKNOWN_CLIENT, SCMPMsgType.DETACH);
		}
		super.clnAttachBefore();
	}
}
