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
package com.stabilit.scm.unit.test.manage;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPClnCreateSessionCall;
import com.stabilit.scm.common.call.SCMPClnDataCall;
import com.stabilit.scm.common.call.SCMPClnDeleteSessionCall;
import com.stabilit.scm.common.call.SCMPManageCall;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.util.SynchronousCallback;
import com.stabilit.scm.unit.test.SCTest;
import com.stabilit.scm.unit.test.SetupTestCases;
import com.stabilit.scm.unit.test.attach.SuperAttachTestCase;

public class ManageTestCase extends SuperAttachTestCase {

	private String serviceName = "enableService";

	public ManageTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void manageCommand() throws Exception {
		// try to create a session on service enableService - should fail
		SCMPFault fault = (SCMPFault) this.createSession();
		SCTest.verifyError(fault, SCMPError.NOT_FOUND, " [service not found for enableService]",
				SCMPMsgType.CLN_CREATE_SESSION);

		// enable enableService by manage call
		SCMPManageCall manageCall = (SCMPManageCall) SCMPCallFactory.MANAGE_CALL.newInstance(req);
		ManageTestCallback callback = new ManageTestCallback();
		manageCall.setRequestBody("enable=" + this.serviceName);
		manageCall.invoke(callback);
		SCMPMessage result = callback.getMessageSync();
		Assert.assertNull(result.getBody());
		Assert.assertEquals(result.getHeader(SCMPHeaderAttributeKey.MSG_TYPE), SCMPMsgType.MANAGE.getValue());

		// register a server for enableService
		SetupTestCases.registerSessionServiceEnable();

		// try to create a session on service enableService - should work
		result = (SCMPMessage) this.createSession();
		SCTest.checkReply(result);
		String sessionId = result.getSessionId();

		// disable enableService by manage call
		manageCall = (SCMPManageCall) SCMPCallFactory.MANAGE_CALL.newInstance(req);
		callback = new ManageTestCallback();
		manageCall.setRequestBody("disable=" + this.serviceName);
		manageCall.invoke(callback);
		result = callback.getMessageSync();
		Assert.assertNull(result.getBody());
		Assert.assertEquals(result.getHeader(SCMPHeaderAttributeKey.MSG_TYPE), SCMPMsgType.MANAGE.getValue());

		// try to create another session on service enableService - should fail
		fault = (SCMPFault) this.createSession();
		SCTest.verifyError(fault, SCMPError.NOT_FOUND, " [service not found for enableService]",
				SCMPMsgType.CLN_CREATE_SESSION);

		// try to send data over first created session - should work
		SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(req,
				this.serviceName, sessionId);
		clnDataCall.setMessagInfo("message info");
		clnDataCall.setRequestBody("get Data (query)");
		clnDataCall.invoke(callback);
		result = callback.getMessageSync();
		SCTest.checkReply(result);

		// delete session one
		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(req, this.serviceName, sessionId);
		deleteSessionCall.invoke(callback);
		result = callback.getMessageSync();
		SCTest.checkReply(result);

		// deregister a server for enableService
		SetupTestCases.deregisterSessionServiceEnable();
	}
	
//	@Test
//	public void shutdownSCByManageCMD() throws Exception {
//		// enable enableService by manage call
//		SCMPManageCall manageCall = (SCMPManageCall) SCMPCallFactory.MANAGE_CALL.newInstance(req);
//		ManageTestCallback callback = new ManageTestCallback();
//		manageCall.setRequestBody("kill");
//		manageCall.invoke(callback);
//	}

	private Object createSession() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(req, serviceName);

		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoIntervalSeconds(300);
		createSessionCall.setEchoTimeoutSeconds(10);
		createSessionCall.invoke(this.attachCallback);
		return this.attachCallback.getMessageSync();
	}

	private class ManageTestCallback extends SynchronousCallback {
		// nothing to implement in this case - everything is done by super-class
	}
}
