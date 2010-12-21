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
import org.serviceconnector.TestCallback;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnSubscribeCall;
import org.serviceconnector.call.SCMPClnUnsubscribeCall;
import org.serviceconnector.call.SCMPReceivePublicationCall;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.RequesterContext;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;

public class SCMPClnSubscribeTest {

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
		srvCtx = ctrl.startServer(TestConstants.SERVER_TYPE_PUBLISH, TestConstants.log4jSrvProperties, TestConstants.pubServerName1,
				TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 1, 1, TestConstants.pubServerName1);
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
	 * Description: subscribe call - mask not set<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_ClnSubscribeCallMaskNotSet() throws Exception {
		SCMPClnSubscribeCall subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL.newInstance(this.requester,
				TestConstants.pubServerName1);
		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setNoDataIntervalSeconds(50);
		TestCallback cbk = new TestCallback(true);
		subscribeCall.invoke(cbk, 3000);
		SCMPMessage fault = cbk.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		TestUtil.verifyError((SCMPMessageFault) fault, SCMPError.HV_ERROR, SCMPMsgType.CLN_SUBSCRIBE);
	}

	/**
	 * Description: subscribe call - no data interval not set<br>
	 * Expectation: passes
	 */
	@Test
	public void t02_ClnSubscribeCallNoiNotSet() throws Exception {
		SCMPClnSubscribeCall subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL.newInstance(this.requester,
				TestConstants.pubServerName1);
		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setMask("ACD");
		subscribeCall.setNoDataIntervalSeconds(0);
		TestCallback cbk = new TestCallback(true);
		subscribeCall.invoke(cbk, 3000);
		SCMPMessage fault = cbk.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		TestUtil.verifyError((SCMPMessageFault) fault, SCMPError.HV_ERROR, SCMPMsgType.CLN_SUBSCRIBE);
	}

	/**
	 * Description: subscribe call - receive publication call no data received<br>
	 * Expectation: passes
	 */
	@Test
	public void t10_ClnSubscribeCallReceivePublicationCallNoData() throws Exception {
		SCMPClnSubscribeCall subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL.newInstance(this.requester,
				TestConstants.pubServerName1);

		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setNoDataIntervalSeconds(1);
		subscribeCall.setMask(TestConstants.mask);
		TestCallback cbk = new TestCallback(true);
		subscribeCall.invoke(cbk, 3000);
		SCMPMessage reply = cbk.getMessageSync(3000);
		TestUtil.checkReply(reply);
		String sessionId = reply.getSessionId();
		// receive publication - no data
		SCMPReceivePublicationCall receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION
				.newInstance(this.requester, TestConstants.pubServerName1, sessionId);
		receivePublicationCall.invoke(cbk, 30000);
		reply = cbk.getMessageSync(3000);
		TestUtil.checkReply(reply);
		Assert.assertTrue(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));

		SCMPClnUnsubscribeCall unSubscribeCall = (SCMPClnUnsubscribeCall) SCMPCallFactory.CLN_UNSUBSCRIBE_CALL.newInstance(
				this.requester, TestConstants.pubServerName1, sessionId);
		unSubscribeCall.invoke(cbk, 3000);
		reply = cbk.getMessageSync(3000);
		TestUtil.checkReply(reply);
	}

	/**
	 * Description: subscribe call - receive publication call message received<br>
	 * Expectation: passes
	 */
	@Test
	public void t11_ClnSubscribeCallReceivePublicationCallMessage() throws Exception {
		SCMPClnSubscribeCall subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL.newInstance(this.requester,
				TestConstants.pubServerName1);

		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setNoDataIntervalSeconds(1);
		subscribeCall.setSessionInfo(TestConstants.publishMsgUncompressedCmd);
		subscribeCall.setMask(TestConstants.mask);
		subscribeCall.setRequestBody("5");
		TestCallback cbk = new TestCallback(true);
		subscribeCall.invoke(cbk, 1000);
		SCMPMessage reply = cbk.getMessageSync(1000);
		TestUtil.checkReply(reply);
		String sessionId = reply.getSessionId();

		// receive publication - get message
		SCMPReceivePublicationCall receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION
				.newInstance(this.requester, TestConstants.pubServerName1, sessionId);
		receivePublicationCall.invoke(cbk, 1000);
		reply = cbk.getMessageSync(1000);
		TestUtil.checkReply(reply);
		Assert.assertFalse(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));

		SCMPClnUnsubscribeCall unSubscribeCall = (SCMPClnUnsubscribeCall) SCMPCallFactory.CLN_UNSUBSCRIBE_CALL.newInstance(
				this.requester, TestConstants.pubServerName1, sessionId);
		unSubscribeCall.invoke(cbk, 3000);
		reply = cbk.getMessageSync(3000);
		TestUtil.checkReply(reply);
	}
}
