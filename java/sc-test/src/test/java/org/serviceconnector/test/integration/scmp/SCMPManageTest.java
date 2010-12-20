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

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.Constants;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnCreateSessionCall;
import org.serviceconnector.call.SCMPInspectCall;
import org.serviceconnector.call.SCMPManageCall;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.RequesterContext;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;

public class SCMPManageTest {
	/** The Constant testLogger. */
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCMPManageTest.class);

	private static ProcessesController ctrl;
	private static ProcessCtx scCtx;
	private SCRequester requester;
	private int threadCount = 0;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
	}

	@Before
	public void beforeOneTest() throws Exception {
		threadCount = Thread.activeCount();
		this.requester = new SCRequester(new RequesterContext(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP
				.getValue(), 0));
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			requester.destroy();
		} catch (Exception e) {
		}
		requester = null;
		testLogger.info("Number of threads :" + Thread.activeCount() + " created :" + (Thread.activeCount() - threadCount));
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		try {
			ctrl.stopSC(scCtx);
			scCtx = null;
		} catch (Exception e) {
		}
		ctrl = null;
	}

	/**
	 * Description: Manage call - disable/enable service<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_ManageCallDisableEnableService() throws Exception {
		SCMPManageCall manageCall = (SCMPManageCall) SCMPCallFactory.MANAGE_CALL.newInstance(this.requester);
		TestCallback cbk = new TestCallback();

		// disable service
		manageCall.setRequestBody(Constants.DISABLE + Constants.EQUAL_SIGN + TestConstants.sesServerName1);
		manageCall.invoke(cbk, 1000);
		SCMPMessage result = cbk.getMessageSync(3000);
		TestUtil.checkReply(result);
		// try to create a session on disabled service - should fail
		SCMPMessage fault = this.clnCreateSession();
		TestUtil.verifyError(fault, SCMPError.SERVICE_DISABLED, SCMPMsgType.CLN_CREATE_SESSION);

		// enable service
		manageCall.setRequestBody(Constants.ENABLE + Constants.EQUAL_SIGN + TestConstants.sesServerName1);
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
		SCMPManageCall manageCall = (SCMPManageCall) SCMPCallFactory.MANAGE_CALL.newInstance(this.requester);
		TestCallback cbk = new TestCallback();

		// disable service
		manageCall.setRequestBody(Constants.DISABLE + Constants.EQUAL_SIGN + TestConstants.sesServerName1);
		manageCall.invoke(cbk, 1000);
		SCMPMessage result = cbk.getMessageSync(3000);
		TestUtil.checkReply(result);

		// state of enableService
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(this.requester);
		inspectCall.setRequestBody(Constants.STATE + Constants.EQUAL_SIGN + TestConstants.sesServerName1);
		inspectCall.invoke(cbk, 1000);
		result = cbk.getMessageSync(3000);
		Assert.assertEquals("DISABLED", result.getBody().toString());
	}

	/**
	 * Cln create session.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private SCMPMessage clnCreateSession() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL.newInstance(
				this.requester, TestConstants.sesServerName1);
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoIntervalSeconds(3600);
		TestCallback cbk = new TestCallback();
		createSessionCall.invoke(cbk, 1000);
		return cbk.getMessageSync(3000);
	}
}
