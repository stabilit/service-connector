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
package org.serviceconnector.test.system.scmp;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestCallback;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnCreateSessionCall;
import org.serviceconnector.call.SCMPClnDeleteSessionCall;
import org.serviceconnector.call.SCMPClnExecuteCall;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.RequesterContext;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.system.SystemSuperTest;

/**
 * The Class ClnCreateSessionTestCase.
 */
public class SCMPClnCreateSessionTest extends SystemSuperTest {

	private ProcessCtx sesSrvCtx;
	private SCRequester requester;

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		sesSrvCtx = ctrl.startServer(TestConstants.COMMUNICATOR_TYPE_SESSION, TestConstants.log4jSrvProperties, TestConstants.sesServerName1,
				TestConstants.PORT_SES_SRV_TCP, TestConstants.PORT_SC_TCP, 1, 1, TestConstants.sesServiceName1);
		this.requester = new SCRequester(new RequesterContext(TestConstants.HOST, TestConstants.PORT_SC_HTTP, ConnectionType.NETTY_HTTP
				.getValue(), 0));
		AppContext.init();
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			this.requester.destroy();
		} catch (Exception e) {
		}
		this.requester = null;
		try {
			ctrl.stopServer(sesSrvCtx);
		} catch (Exception e) {
		}
		sesSrvCtx = null;
		super.afterOneTest();
	}


	/**
	 * Description: create session - echo time interval wrong<br>
	 * Expectation: passes, returns error
	 */
	@Test
	public void t01_WrongECI() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL.newInstance(
				this.requester, TestConstants.sesServerName1);
		createSessionCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		createSessionCall.setEchoIntervalSeconds(0);
		createSessionCall.getRequest().setServiceName("session-1");
		TestCallback cbk = new TestCallback();
		createSessionCall.invoke(cbk, 1000);
		SCMPMessage fault = cbk.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		TestUtil.verifyError(fault, SCMPError.HV_WRONG_ECHO_INTERVAL, SCMPMsgType.CLN_CREATE_SESSION);
	}

	/**
	 * Description: create session - serviceName not set<br>
	 * Expectation: passes, returns error
	 */
	@Test
	public void t02_ServiceNameMissing() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL.newInstance(
				this.requester, TestConstants.sesServerName1);
		createSessionCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		// set serviceName null
		createSessionCall.getRequest().setServiceName(null);
		createSessionCall.setEchoIntervalSeconds(300);
		TestCallback cbk = new TestCallback();
		createSessionCall.invoke(cbk, 1000);
		SCMPMessage fault = cbk.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		TestUtil.verifyError(fault, SCMPError.HV_WRONG_SERVICE_NAME, SCMPMsgType.CLN_CREATE_SESSION);
	}
	
	/**
	 * Description: create session - service name = ""<br>
	 * Expectation: passes, returns error
	 */
	@Test
	public void t03_ServiceNameEmpty() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL.newInstance(
				this.requester, TestConstants.sesServerName1);
		createSessionCall.getRequest().setServiceName("");
		createSessionCall.setEchoIntervalSeconds(300);
		TestCallback cbk = new TestCallback();
		createSessionCall.invoke(cbk, 1000);
		SCMPMessage fault = cbk.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		TestUtil.verifyError(fault, SCMPError.HV_WRONG_SERVICE_NAME, SCMPMsgType.CLN_CREATE_SESSION);
	}

	/**
	 * Description: create session - service name = " "<br>
	 * Expectation: passes, returns error
	 */
	@Test
	public void t04_ServiceNameBlank() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL.newInstance(
				this.requester, TestConstants.sesServerName1);
		createSessionCall.getRequest().setServiceName(" ");
		createSessionCall.setEchoIntervalSeconds(300);
		TestCallback cbk = new TestCallback();
		createSessionCall.invoke(cbk, 1000);
		SCMPMessage fault = cbk.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		TestUtil.verifyError(fault, SCMPError.HV_WRONG_SERVICE_NAME, SCMPMsgType.CLN_CREATE_SESSION);
	}

	
	/**
	 * Description: create session - serviceName too long<br>
	 * Expectation: passes, returns error
	 */
	@Test
	public void t05_ServiceNameTooLong() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL.newInstance(
				this.requester, TestConstants.sesServerName1);
		createSessionCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		// set serviceName null
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 98<<10; i++) {
			sb.append(i);
			if(sb.length() > 100000) break;
		}
		createSessionCall.getRequest().setServiceName(sb.toString());
		createSessionCall.setEchoIntervalSeconds(300);
		TestCallback cbk = new TestCallback();
		createSessionCall.invoke(cbk, 1000);
		SCMPMessage fault = cbk.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		TestUtil.verifyError(fault, SCMPError.SERVER_ERROR, SCMPMsgType.UNDEFINED);
	}

	/**
	 * Description: create session - service name = gaga<br>
	 * Expectation: passes, returns error
	 */
	@Test
	public void t06_NonExistingServiceName() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL.newInstance(
				this.requester, TestConstants.sesServerName1);
		createSessionCall.getRequest().setServiceName("Gaga");
		createSessionCall.setEchoIntervalSeconds(300);
		TestCallback cbk = new TestCallback();
		createSessionCall.invoke(cbk, 1000);
		SCMPMessage fault = cbk.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		TestUtil.verifyError(fault, SCMPError.SERVICE_NOT_FOUND, SCMPMsgType.CLN_CREATE_SESSION);
	}

	
	/**
	 * Description: create session - delete session<br>
	 * Expectation: passes
	 */
	@Test
	public void t10_CreateSessionDeleteSession() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL.newInstance(
				this.requester, TestConstants.sesServerName1);
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoIntervalSeconds(3000);
		TestCallback cbk = new TestCallback();
		createSessionCall.invoke(cbk, 2000);
		SCMPMessage responseMessage = cbk.getMessageSync(4000);
		String sessId = responseMessage.getSessionId();
		TestUtil.checkReply(responseMessage);

		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL.newInstance(
				this.requester, responseMessage.getServiceName(), sessId);
		deleteSessionCall.invoke(cbk, 2000);
		responseMessage = cbk.getMessageSync(4000);
		TestUtil.checkReply(responseMessage);
	}

	/**
	 * Description: create session - session gets rejected<br>
	 * Expectation: passes, returns rejection
	 */
	@Test
	public void t20_SessionRejected() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL.newInstance(
				this.requester, TestConstants.sesServerName1);
		createSessionCall.setSessionInfo(TestConstants.rejectSessionCmd);
		createSessionCall.setEchoIntervalSeconds(300);
		TestCallback cbk = new TestCallback();
		createSessionCall.invoke(cbk, 4000);
		SCMPMessage responseMessage = cbk.getMessageSync(3000);
		String sessId = responseMessage.getSessionId();
		Assert.assertNull(sessId);
		Assert.assertFalse(responseMessage.isFault());
		Assert.assertTrue(responseMessage.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION));
	}

	/**
	 * Description: create session - wait until session times out<br>
	 * Expectation: passes, returns error
	 */
	@Test
	public void t30_SessionTimesOut() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL.newInstance(
				this.requester, TestConstants.sesServerName1);
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoIntervalSeconds(10);
		TestCallback cbk = new TestCallback();
		createSessionCall.invoke(cbk, 4000);
		SCMPMessage responseMessage = cbk.getMessageSync(3000);
		TestUtil.checkReply(responseMessage);

		String sessionId = responseMessage.getSessionId();
		// wait until session times out and get cleaned up
		Thread.sleep(13000);
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(this.requester,
				TestConstants.sesServerName1, sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.echoCmd);
		clnExecuteCall.setRequestBody(TestConstants.pangram);
		clnExecuteCall.invoke(cbk, 1000);
		SCMPMessage msg = cbk.getMessageSync(3000);
		TestUtil.verifyError(msg, SCMPError.SESSION_NOT_FOUND, SCMPMsgType.CLN_EXECUTE);
	}
}