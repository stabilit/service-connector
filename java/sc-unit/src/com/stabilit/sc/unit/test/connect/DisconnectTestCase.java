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
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.unit.test.SCTest;

public class DisconnectTestCase extends SuperConnectTestCase {

	/**
	 * @param fileName
	 */
	public DisconnectTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void secondConnect() throws Exception {
		SCMPConnectCall connectCall = (SCMPConnectCall) SCMPCallFactory.CONNECT_CALL.newInstance(client);

		connectCall.setVersion("1.0-00");
		connectCall.setCompression(false);
		connectCall.setKeepAliveTimeout(30);
		connectCall.setKeepAliveInterval(360);

		try {
			connectCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPServiceException e) {
			SCTest.verifyError(e.getFault(), SCMPErrorCode.ALREADY_CONNECTED, SCMPMsgType.CONNECT);
		}
	}

	@Test
	public void disconnect() throws Exception {
		SCMPDisconnectCall disconnectCall = (SCMPDisconnectCall) SCMPCallFactory.DISCONNECT_CALL
				.newInstance(client);

		SCMP result = null;
		try {
			result = disconnectCall.invoke();
		} catch (SCMPServiceException e) {
			Assert.fail();
		}

		/*********************************** Verify disconnect response msg **********************************/
		Assert.assertNull(result.getBody());
		Assert.assertEquals(result.getHeader(SCMPHeaderAttributeKey.MSG_TYPE), SCMPMsgType.DISCONNECT
				.getResponseName());

		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(client);
		SCMP inspect = inspectCall.invoke();
		/*********************************** Verify registry entries in SC ***********************************/
		InspectMessage inspectMsg = (InspectMessage) inspect.getBody();
		String scEntry = (String) inspectMsg.getAttribute("connectionRegistry");
		Assert.assertEquals("", scEntry);
		super.clnConnectBefore();
	}

	@Test
	public void secondDisconnect() throws Exception {
		super.clnDisconnectAfter();
		SCMPDisconnectCall disconnectCall = (SCMPDisconnectCall) SCMPCallFactory.DISCONNECT_CALL
				.newInstance(client);
		try {
			disconnectCall.invoke();
		} catch (SCMPServiceException e) {
			SCTest.verifyError(e.getFault(), SCMPErrorCode.NOT_CONNECTED, SCMPMsgType.DISCONNECT);
		}
		super.clnConnectBefore();
	}
}
