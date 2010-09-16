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
package com.stabilit.sc.unit.test.manage;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.sc.common.call.SCMPCallFactory;
import com.stabilit.sc.common.call.SCMPClnCreateSessionCall;
import com.stabilit.sc.common.call.SCMPClnDeleteSessionCall;
import com.stabilit.sc.common.call.SCMPClnExecuteCall;
import com.stabilit.sc.common.call.SCMPInspectCall;
import com.stabilit.sc.common.call.SCMPManageCall;
import com.stabilit.sc.common.conf.Constants;
import com.stabilit.sc.common.scmp.SCMPError;
import com.stabilit.sc.common.scmp.SCMPFault;
import com.stabilit.sc.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.scmp.SCMPMessage;
import com.stabilit.sc.common.scmp.SCMPMsgType;
import com.stabilit.sc.common.util.SynchronousCallback;
import com.stabilit.sc.unit.SCTest;
import com.stabilit.sc.unit.SetupTestCases;
import com.stabilit.sc.unit.test.attach.SuperAttachTestCase;

public class ManageTestCase extends SuperAttachTestCase {

	private String serviceName = "enableService";

	public ManageTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void manageCommandEnableDisable() throws Exception {
		// try to create a session on service enableService - should fail
		SCMPFault fault = (SCMPFault) this.createSession();
		SCTest.verifyError(fault, SCMPError.NOT_FOUND, " [service not found for enableService]",
				SCMPMsgType.CLN_CREATE_SESSION);

		// enable enableService by manage call
		SCMPManageCall manageCall = (SCMPManageCall) SCMPCallFactory.MANAGE_CALL.newInstance(req);
		ManageTestCallback callback = new ManageTestCallback();
		manageCall.setRequestBody("enable=" + this.serviceName);
		manageCall.invoke(callback, 1000);
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
		manageCall.invoke(callback, 1000);
		result = callback.getMessageSync();
		Assert.assertNull(result.getBody());
		Assert.assertEquals(result.getHeader(SCMPHeaderAttributeKey.MSG_TYPE), SCMPMsgType.MANAGE.getValue());

		// try to create another session on service enableService - should fail
		fault = (SCMPFault) this.createSession();
		SCTest.verifyError(fault, SCMPError.NOT_FOUND, " [service not found for enableService]",
				SCMPMsgType.CLN_CREATE_SESSION);

		// try to send data over first created session - should work
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(req,
				this.serviceName, sessionId);
		clnExecuteCall.setMessagInfo("message info");
		clnExecuteCall.setRequestBody("get Data (query)");
		clnExecuteCall.invoke(callback, 1000);
		result = callback.getMessageSync();
		SCTest.checkReply(result);

		// delete session one
		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(req, this.serviceName, sessionId);
		deleteSessionCall.invoke(callback, 1000);
		result = callback.getMessageSync();
		SCTest.checkReply(result);

		// deregister a server for enableService
		SetupTestCases.deregisterSessionServiceEnable();
	}

	@Test
	public void manageCommandState() throws Exception {
		// enable enableService by manage call
		SCMPManageCall manageCall = (SCMPManageCall) SCMPCallFactory.MANAGE_CALL.newInstance(req);
		ManageTestCallback callback = new ManageTestCallback();
		manageCall.setRequestBody("enable=" + this.serviceName);
		manageCall.invoke(callback, 1000);
		SCMPMessage result = callback.getMessageSync();

		// state of enableService
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		callback = new ManageTestCallback();
		inspectCall.setRequestBody(Constants.STATE + "=" + this.serviceName);
		inspectCall.invoke(callback, 1000);
		result = callback.getMessageSync();
		Assert.assertEquals("true", result.getBody().toString());

		// disable enableService by manage call
		manageCall = (SCMPManageCall) SCMPCallFactory.MANAGE_CALL.newInstance(req);
		callback = new ManageTestCallback();
		manageCall.setRequestBody("disable=" + this.serviceName);
		manageCall.invoke(callback, 1000);
		result = callback.getMessageSync();
	}

	@Test
	public void manageCommandSessions() throws Exception {
		// enable enableService by manage call
		SCMPManageCall manageCall = (SCMPManageCall) SCMPCallFactory.MANAGE_CALL.newInstance(req);
		ManageTestCallback callback = new ManageTestCallback();
		manageCall.setRequestBody("enable=" + this.serviceName);
		manageCall.invoke(callback, 1000);
		SCMPMessage result = callback.getMessageSync();

		// register a server for enableService
		SetupTestCases.registerSessionServiceEnable();

		// try to create a session on service enableService - should work
		result = (SCMPMessage) this.createSession();
		String sessionId = result.getSessionId();

		// sessions of enableService
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		callback = new ManageTestCallback();
		inspectCall.setRequestBody(Constants.SESSIONS + "=" + this.serviceName);
		inspectCall.invoke(callback, 1000);
		result = callback.getMessageSync();
		Assert.assertEquals("10/1", result.getBody().toString());

		// delete session one
		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(req, this.serviceName, sessionId);
		deleteSessionCall.invoke(callback, 1000);
		result = callback.getMessageSync();

		// deregister a server for enableService
		SetupTestCases.deregisterSessionServiceEnable();

		// disable enableService by manage call
		manageCall = (SCMPManageCall) SCMPCallFactory.MANAGE_CALL.newInstance(req);
		callback = new ManageTestCallback();
		manageCall.setRequestBody("disable=" + this.serviceName);
		manageCall.invoke(callback, 1000);
		result = callback.getMessageSync();
	}

	// @Test
	// public void shutdownSCByManageCMD() throws Exception {
	// // enable enableService by manage call
	// SCMPManageCall manageCall = (SCMPManageCall) SCMPCallFactory.MANAGE_CALL.newInstance(req);
	// ManageTestCallback callback = new ManageTestCallback();
	// manageCall.setRequestBody("kill");
	// manageCall.invoke(callback);
	// }

	private Object createSession() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(req, serviceName);

		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoIntervalSeconds(300);
		createSessionCall.invoke(this.attachCallback, 1000);
		return this.attachCallback.getMessageSync();
	}

	private class ManageTestCallback extends SynchronousCallback {
		// nothing to implement in this case - everything is done by super-class
	}
}
