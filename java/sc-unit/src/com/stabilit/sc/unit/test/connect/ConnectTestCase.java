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
package com.stabilit.sc.unit.test.connect;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.sc.cln.msg.impl.InspectMessage;
import com.stabilit.sc.cln.service.SCMPCallFactory;
import com.stabilit.sc.cln.service.SCMPConnectCall;
import com.stabilit.sc.cln.service.SCMPDisconnectCall;
import com.stabilit.sc.cln.service.SCMPInspectCall;
import com.stabilit.sc.cln.service.SCMPServiceException;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.scmp.SCMPErrorCode;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.unit.test.SCTest;
import com.stabilit.sc.unit.test.SuperTestCase;
import com.stabilit.sc.util.ValidatorUtility;

public class ConnectTestCase extends SuperTestCase {

	/**
	 * @param parameter
	 */
	public ConnectTestCase(String parameter) {
		super(parameter);
	}

	@Test
	public void failConnect() throws Exception {
		SCMPConnectCall connectCall = (SCMPConnectCall) SCMPCallFactory.CONNECT_CALL.newInstance(client);
		/******************* incompatible scmp version ******************/
		connectCall.setVersion("2.0-00");
		try {
			connectCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPServiceException ex) {
			SCTest.verifyError(ex.getFault(), SCMPErrorCode.VALIDATION_ERROR, SCMPMsgType.CONNECT);
//			Assert.assertEquals("1", connectCall.getCall().getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		}
	}

	@Test
	public void connect() throws Exception {
		SCMPConnectCall connectCall = (SCMPConnectCall) SCMPCallFactory.CONNECT_CALL.newInstance(client);

		connectCall.setVersion("1.0-00");
		connectCall.setCompression(false);
		connectCall.setKeepAliveTimeout(30);
		connectCall.setKeepAliveInterval(360);

		SCMP result = connectCall.invoke();

		/*********************************** Verify connect response msg **********************************/
		Assert.assertNull(result.getBody());
		Assert.assertEquals(result.getHeader(SCMPHeaderAttributeKey.MSG_TYPE), SCMPMsgType.CONNECT
				.getResponseName());
		Assert.assertNotNull(ValidatorUtility.validateLocalDateTime(result
				.getHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME)));
//		Assert.assertEquals("1", result.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(client);
		SCMP inspect = inspectCall.invoke();

		/*********************************** Verify registry entries in SC ********************************/
		InspectMessage inspectMsg = (InspectMessage) inspect.getBody();
		String expectedScEntry = ":compression=false;localDateTime="
				+ ValidatorUtility.validateLocalDateTime(connectCall.getCall().getHeader(
						SCMPHeaderAttributeKey.LOCAL_DATE_TIME))
				+ ";scVersion=1.0-00;keepAliveTimeout=30,360;";
		String scEntry = (String) inspectMsg.getAttribute("connectionRegistry");
		// truncate /127.0.0.1:3640 because port may vary.
		scEntry = scEntry.substring(scEntry.indexOf(":") + 1);
		scEntry = scEntry.substring(scEntry.indexOf(":"));

		Assert.assertEquals(expectedScEntry, scEntry);

		SCMPDisconnectCall disconnectCall = (SCMPDisconnectCall) SCMPCallFactory.DISCONNECT_CALL
				.newInstance(client);
		disconnectCall.invoke();
	}
}
