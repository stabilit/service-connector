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
package org.serviceconnector.test.sc.manage;

import junit.framework.Assert;

import org.junit.Test;
import org.serviceconnector.Constants;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnCreateSessionCall;
import org.serviceconnector.call.SCMPClnDeleteSessionCall;
import org.serviceconnector.call.SCMPClnExecuteCall;
import org.serviceconnector.call.SCMPInspectCall;
import org.serviceconnector.call.SCMPManageCall;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPFault;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.sc.SCTest;
import org.serviceconnector.test.sc.SetupTestCases;
import org.serviceconnector.test.sc.attach.SuperAttachTestCase;
import org.serviceconnector.util.SynchronousCallback;



public class ManageTestCase extends SuperAttachTestCase {

	private String serviceName = "enableService";

	public ManageTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void manageCommandEnableDisable() throws Exception {
		// try to create a session on service enableService - should fail
		SCMPFault fault = (SCMPFault) this.createSession();
		SCTest.verifyError(fault, SCMPError.SERVICE_DISABLED, " [Service is disabled.]",
				SCMPMsgType.CLN_CREATE_SESSION);

		// enable enableService by manage call
		SCMPManageCall manageCall = (SCMPManageCall) SCMPCallFactory.MANAGE_CALL.newInstance(req);
		TestManageCallback callback = new TestManageCallback();
		manageCall.setRequestBody("enable=" + this.serviceName);
		manageCall.invoke(callback, 1000);
		SCMPMessage result = callback.getMessageSync();
		Assert.assertNull(result.getBody());
		Assert.assertEquals(SCMPMsgType.MANAGE.getValue(), result.getHeader(SCMPHeaderAttributeKey.MSG_TYPE));

		// register a server for enableService
		SetupTestCases.registerSessionServiceEnable();

		// try to create a session on service enableService - should work
		result = (SCMPMessage) this.createSession();
		SCTest.checkReply(result);
		String sessionId = result.getSessionId();

		// disable enableService by manage call
		manageCall = (SCMPManageCall) SCMPCallFactory.MANAGE_CALL.newInstance(req);
		callback = new TestManageCallback();
		manageCall.setRequestBody("disable=" + this.serviceName);
		manageCall.invoke(callback, 1000);
		result = callback.getMessageSync();
		Assert.assertNull(result.getBody());
		Assert.assertEquals(SCMPMsgType.MANAGE.getValue(), result.getHeader(SCMPHeaderAttributeKey.MSG_TYPE));

		// try to create another session on service enableService - should fail
		fault = (SCMPFault) this.createSession();
		SCTest.verifyError(fault, SCMPError.SERVICE_DISABLED, " [service not found for enableService]",
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
		TestManageCallback callback = new TestManageCallback();
		manageCall.setRequestBody("enable=" + this.serviceName);
		manageCall.invoke(callback, 1000);
		SCMPMessage result = callback.getMessageSync();

		// state of enableService
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		callback = new TestManageCallback();
		inspectCall.setRequestBody(Constants.STATE + "=" + this.serviceName);
		inspectCall.invoke(callback, 1000);
		result = callback.getMessageSync();
		Assert.assertEquals("ENABLED", result.getBody().toString());

		// disable enableService by manage call
		manageCall = (SCMPManageCall) SCMPCallFactory.MANAGE_CALL.newInstance(req);
		callback = new TestManageCallback();
		manageCall.setRequestBody("disable=" + this.serviceName);
		manageCall.invoke(callback, 1000);
		result = callback.getMessageSync();
	}

	@Test
	public void manageCommandSessions() throws Exception {
		// enable enableService by manage call
		SCMPManageCall manageCall = (SCMPManageCall) SCMPCallFactory.MANAGE_CALL.newInstance(req);
		TestManageCallback callback = new TestManageCallback();
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
		callback = new TestManageCallback();
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
		callback = new TestManageCallback();
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

	private class TestManageCallback extends SynchronousCallback {
		// nothing to implement in this case - everything is done by super-class
	}
}
