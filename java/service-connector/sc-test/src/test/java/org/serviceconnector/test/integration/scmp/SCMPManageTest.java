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
package org.serviceconnector.test.integration.scmp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.Constants;
import org.serviceconnector.TestCallback;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.call.SCMPClnCreateSessionCall;
import org.serviceconnector.call.SCMPInspectCall;
import org.serviceconnector.call.SCMPManageCall;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.integration.IntegrationSuperTest;

import junit.framework.Assert;

public class SCMPManageTest extends IntegrationSuperTest {

	private SCRequester requester;

	@Override
	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		AppContext.init();
		this.requester = new SCRequester(
				new RemoteNodeConfiguration(TestConstants.RemoteNodeName, TestConstants.HOST, TestConstants.PORT_SC0_HTTP, ConnectionType.NETTY_HTTP.getValue(), 0, 0, 1), 0);
	}

	@Override
	@After
	public void afterOneTest() throws Exception {
		try {
			requester.destroy();
		} catch (Exception e) {
		}
		requester = null;
		super.afterOneTest();
	}

	/**
	 * Description: Manage call - disable/enable service<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_ManageCallDisableEnableService() throws Exception {
		SCMPManageCall manageCall = new SCMPManageCall(this.requester);
		TestCallback cbk = new TestCallback();

		// disable service
		manageCall.setRequestBody(Constants.CC_CMD_DISABLE + Constants.QUESTION_MARK + Constants.SERVICE_NAME + Constants.EQUAL_SIGN + TestConstants.sesServerName1);
		manageCall.invoke(cbk, 1000);
		SCMPMessage result = cbk.getMessageSync(3000);
		TestUtil.checkReply(result);
		// try to create a session on disabled service - should fail
		SCMPMessage fault = this.clnCreateSession();
		TestUtil.verifyError(fault, SCMPError.SERVICE_DISABLED, SCMPMsgType.CLN_CREATE_SESSION);

		// enable service
		manageCall.setRequestBody(Constants.CC_CMD_ENABLE + Constants.QUESTION_MARK + Constants.SERVICE_NAME + Constants.EQUAL_SIGN + TestConstants.sesServerName1);
		manageCall.invoke(cbk, 1000);
		result = cbk.getMessageSync(3000);
		TestUtil.checkReply(result);
	}

	/**
	 * Description: Manage call - disable service verify by inspect call<br>
	 * Expectation: passes
	 */
	@Test
	public void t02_ManageCallDisableServiceVerifyByInspect() throws Exception {
		SCMPManageCall manageCall = new SCMPManageCall(this.requester);
		TestCallback cbk = new TestCallback();

		// disable service
		manageCall.setRequestBody(Constants.CC_CMD_DISABLE + Constants.QUESTION_MARK + Constants.SERVICE_NAME + Constants.EQUAL_SIGN + TestConstants.sesServerName1);
		manageCall.invoke(cbk, 1000);
		SCMPMessage result = cbk.getMessageSync(3000);
		TestUtil.checkReply(result);

		// state of enableService
		SCMPInspectCall inspectCall = new SCMPInspectCall(this.requester);
		inspectCall.setRequestBody(Constants.CC_CMD_STATE + Constants.QUESTION_MARK + Constants.SERVICE_NAME + Constants.EQUAL_SIGN + TestConstants.sesServerName1);
		inspectCall.invoke(cbk, 1000);
		result = cbk.getMessageSync(3000);
		Assert.assertEquals(TestConstants.sesServerName1 + Constants.EQUAL_SIGN + "disabled", result.getBody().toString());
	}

	/**
	 * Cln create session.
	 *
	 * @throws Exception the exception
	 */
	private SCMPMessage clnCreateSession() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = new SCMPClnCreateSessionCall(this.requester, TestConstants.sesServerName1);
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoIntervalSeconds(3600);
		TestCallback cbk = new TestCallback();
		createSessionCall.invoke(cbk, 1000);
		return cbk.getMessageSync(3000);
	}
}
