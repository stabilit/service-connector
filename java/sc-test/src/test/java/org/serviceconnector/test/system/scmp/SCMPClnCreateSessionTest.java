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

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnCreateSessionCall;
import org.serviceconnector.call.SCMPClnDeleteSessionCall;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.RequesterContext;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.integration.scmp.TestCallback;

/**
 * The Class ClnCreateSessionTestCase.
 */
public class SCMPClnCreateSessionTest {

	/** The Constant testLogger. */
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCMPClnCreateSessionTest.class);

	private static ProcessesController ctrl;
	private ProcessCtx scCtx;
	private ProcessCtx srvCtx;
	private SCRequester requester;
	private int threadCount = 0;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
	}

	@Before
	public void beforeOneTest() throws Exception {
		threadCount = Thread.activeCount();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		srvCtx = ctrl.startServer(TestConstants.SERVER_TYPE_SESSION, TestConstants.log4jSrvProperties, TestConstants.sesServerName1,
				TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, 10, TestConstants.sesServiceName1);
		this.requester = new SCRequester(new RequesterContext(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP
				.getValue(), 0));
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			this.requester.destroy();
		} catch (Exception e) {
		}
		this.requester = null;
		try {
			ctrl.stopServer(srvCtx);
		} catch (Exception e) {
		}
		srvCtx = null;
		try {
			ctrl.stopSC(scCtx);
		} catch (Exception e) {
		}
		scCtx = null;
		testLogger.info("Number of threads :" + Thread.activeCount() + " created :" + (Thread.activeCount() - threadCount));
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		ctrl = null;
	}

	/**
	 * Description: create session call - echo time interval wrong<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_ClnCreateSessionEciWrong() throws Exception {
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
	 * Description: create session call - serviceName not set<br>
	 * Expectation: passes
	 */
	@Test
	public void t02_ClnCreateSessionServiceNameWrong() throws Exception {
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
	 * Description: create session call - delete session call<br>
	 * Expectation: passes
	 */
	@Test
	public void t10_ClnCreateSessionClnDeleteSession() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL.newInstance(
				this.requester, TestConstants.sesServerName1);
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoIntervalSeconds(3000);
		TestCallback cbk = new TestCallback();
		createSessionCall.invoke(cbk, 1000);
		SCMPMessage responseMessage = cbk.getMessageSync(3000);
		String sessId = responseMessage.getSessionId();
		TestUtil.checkReply(responseMessage);

		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL.newInstance(
				this.requester, responseMessage.getServiceName(), sessId);
		deleteSessionCall.invoke(cbk, 1000);
		responseMessage = cbk.getMessageSync(3000);
		TestUtil.checkReply(responseMessage);
	}

	/**
	 * Description: create session call - session gets rejected<br>
	 * Expectation: passes
	 */
	@Test
	public void t20_ClnCreateSessionRejectedSession() throws Exception {
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
}
