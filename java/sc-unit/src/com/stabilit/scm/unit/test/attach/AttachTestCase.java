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

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.scm.cln.call.SCMPAttachCall;
import com.stabilit.scm.cln.call.SCMPCallException;
import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPDetachCall;
import com.stabilit.scm.cln.call.SCMPInspectCall;
import com.stabilit.scm.common.msg.impl.InspectMessage;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.util.ValidatorUtility;
import com.stabilit.scm.unit.test.SCTest;
import com.stabilit.scm.unit.test.SuperTestCase;

public class AttachTestCase extends SuperTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param parameter the parameter
	 */
	public AttachTestCase(String parameter) {
		super(parameter);
	}

	@Test
	public void failAttach() throws Exception {
		SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(client);
		/******************* incompatible scmp version ******************/
		attachCall.setVersion("2.0-00");
		try {
			attachCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPCallException ex) {
			SCTest.verifyError(ex.getFault(), SCMPError.VALIDATION_ERROR, SCMPMsgType.ATTACH);
			Assert.assertEquals("1", attachCall.getRequest().getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		}
	}

	@Test
	public void attach() throws Exception {
		SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(client);

		attachCall.setCompression(false);
		attachCall.setKeepAliveTimeout(30);
		attachCall.setKeepAliveInterval(360);

		SCMPMessage result = attachCall.invoke();

		/*********************************** Verify attach response msg **********************************/
		Assert.assertNull(result.getBody());
		Assert.assertEquals(result.getHeader(SCMPHeaderAttributeKey.MSG_TYPE), SCMPMsgType.ATTACH
				.getResponseName());
		Assert.assertNotNull(ValidatorUtility.validateLocalDateTime(result
				.getHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME)));
		Assert.assertEquals("1", result.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(client);
		SCMPMessage inspect = inspectCall.invoke();

		/*********************************** Verify registry entries in SC ********************************/
		InspectMessage inspectMsg = (InspectMessage) inspect.getBody();
		String expectedScEntry = ":compression=false;localDateTime="
				+ ValidatorUtility.validateLocalDateTime(attachCall.getRequest().getHeader(
						SCMPHeaderAttributeKey.LOCAL_DATE_TIME))
				+ ";scVersion=1.0-000;keepAliveTimeout=30,360;";
		String scEntry = (String) inspectMsg.getAttribute("clientRegistry");
		// truncate /127.0.0.1:3640 because port may vary.
		scEntry = scEntry.substring(scEntry.indexOf(":") + 1);
		scEntry = scEntry.substring(scEntry.indexOf(":"));

		Assert.assertEquals(expectedScEntry, scEntry);

		SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL
				.newInstance(client);
		detachCall.invoke();
	}
}
