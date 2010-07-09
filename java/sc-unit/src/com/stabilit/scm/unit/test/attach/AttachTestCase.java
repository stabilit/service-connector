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

import java.util.Date;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.scm.cln.call.SCMPAttachCall;
import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPDetachCall;
import com.stabilit.scm.cln.call.SCMPInspectCall;
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
	 * @param parameter
	 *            the parameter
	 */
	public AttachTestCase(String parameter) {
		super(parameter);
	}

	public void failAttach() throws Exception {
		// TODO
	}

	@Test
	public void attach() throws Exception {
		SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(req);
		attachCall.setKeepAliveInterval(360);
		SCMPMessage result = attachCall.invoke();

		/*********************************** Verify attach response msg **********************************/
		Assert.assertNull(result.getBody());
		Assert.assertEquals(result.getHeader(SCMPHeaderAttributeKey.MSG_TYPE), SCMPMsgType.ATTACH.getValue());
		Assert.assertNotNull(ValidatorUtility.validateLocalDateTime(result
				.getHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME)));
		Assert.assertEquals("1", result.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		SCMPMessage inspectString = inspectCall.invoke();

		/*********************************** Verify registry entries in SC ********************************/
		String inspectMsg = (String) inspectString.getBody();
		Map<String, String> inspectMap = SCTest.convertInspectStringToMap(inspectMsg);
		String localDateTimeString = attachCall.getRequest().getHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME);
		Date localDateTime = ValidatorUtility.validateLocalDateTime(localDateTimeString);
		String expectedScEntry = "/127.0.0.1::/127.0.0.1::SCMP [header={bodyLength=0, kpi=360, ver=1.0-000, ldt="
				+ localDateTimeString + ", mid=1, mty=ATT}] MapBean: ldt=" + localDateTime + ";|";
		String scEntry = inspectMap.get("clientRegistry");
		// truncate /127.0.0.1:3640 because port may vary.
		scEntry = scEntry.replaceAll("/127.0.0.1:\\d*", "/127.0.0.1:");
		Assert.assertEquals(expectedScEntry, scEntry);

		SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(req);
		detachCall.invoke();

		inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		inspectString = inspectCall.invoke();

		/*********************************** Verify registry entries in SC ********************************/
		inspectMsg = (String) inspectString.getBody();
		inspectMap = SCTest.convertInspectStringToMap(inspectMsg);
		scEntry = inspectMap.get("clientRegistry");
		Assert.assertEquals("", scEntry);
	}
}
