/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
	private final static Logger logger = Logger.getLogger(SCMPClnCreateSessionTest.class);

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
		scCtx = ctrl.startSC(TestConstants.SC0, TestConstants.log4jSC0Properties, TestConstants.SC0Properties);
		srvCtx = ctrl.startServer(TestConstants.COMMUNICATOR_TYPE_PUBLISH, TestConstants.log4jSrvProperties,
				TestConstants.pubServerName1, TestConstants.PORT_PUB_SRV_TCP, TestConstants.PORT_SC_TCP, 1, 1,
				TestConstants.pubServerName1);
		this.requester = new SCRequester(new RequesterContext(TestConstants.HOST, TestConstants.PORT_SC_HTTP,
				ConnectionType.NETTY_HTTP.getValue(), 0));
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
	 * Description: subscribe - mask not set<br>
	 * Expectation: passes, returns error
	 */
	@Test
	public void t01_MaskNotSet() throws Exception {
		SCMPClnSubscribeCall subscribeCall = new SCMPClnSubscribeCall(this.requester, TestConstants.pubServerName1);
		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setNoDataIntervalSeconds(50);
		TestCallback cbk = new TestCallback(true);
		subscribeCall.invoke(cbk, 3000);
		SCMPMessage fault = cbk.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		TestUtil.verifyError((SCMPMessageFault) fault, SCMPError.HV_WRONG_MASK, SCMPMsgType.CLN_SUBSCRIBE);
	}

	/**
	 * Description: subscribe - no data interval not set<br>
	 * Expectation: passes, returns error
	 */
	@Test
	public void t02_NOINotSet() throws Exception {
		SCMPClnSubscribeCall subscribeCall = new SCMPClnSubscribeCall(this.requester, TestConstants.pubServerName1);
		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setMask(TestConstants.mask);
		subscribeCall.setNoDataIntervalSeconds(0);
		TestCallback cbk = new TestCallback(true);
		subscribeCall.invoke(cbk, 3000);
		SCMPMessage fault = cbk.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		TestUtil.verifyError((SCMPMessageFault) fault, SCMPError.HV_WRONG_NODATA_INTERVAL, SCMPMsgType.CLN_SUBSCRIBE);
	}

	/**
	 * Description: subscribe - receive publication call no data received<br>
	 * Expectation: passes
	 */
	@Test
	public void t10_ReceiveNoData() throws Exception {
		SCMPClnSubscribeCall subscribeCall = new SCMPClnSubscribeCall(this.requester, TestConstants.pubServerName1);

		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setNoDataIntervalSeconds(1);
		subscribeCall.setMask(TestConstants.mask);
		TestCallback cbk = new TestCallback(true);
		subscribeCall.invoke(cbk, 3000);
		SCMPMessage reply = cbk.getMessageSync(3000);
		TestUtil.checkReply(reply);
		String sessionId = reply.getSessionId();
		// receive publication - no data
		SCMPReceivePublicationCall receivePublicationCall = new SCMPReceivePublicationCall(this.requester,
				TestConstants.pubServerName1, sessionId);
		receivePublicationCall.invoke(cbk, 30000);
		reply = cbk.getMessageSync(3000);
		TestUtil.checkReply(reply);
		Assert.assertTrue(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));

		SCMPClnUnsubscribeCall unSubscribeCall = new SCMPClnUnsubscribeCall(this.requester, TestConstants.pubServerName1, sessionId);
		unSubscribeCall.invoke(cbk, 3000);
		reply = cbk.getMessageSync(3000);
		TestUtil.checkReply(reply);
	}

	/**
	 * Description: subscribe - receive publication call message received<br>
	 * Expectation: passes
	 */
	@Test
	public void t11_ReceiveMessage() throws Exception {
		SCMPClnSubscribeCall subscribeCall = new SCMPClnSubscribeCall(this.requester, TestConstants.pubServerName1);

		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setNoDataIntervalSeconds(1);
		subscribeCall.setSessionInfo(TestConstants.publishUncompressedMsgCmd);
		subscribeCall.setMask(TestConstants.mask);
		subscribeCall.setRequestBody("5");
		TestCallback cbk = new TestCallback(true);
		subscribeCall.invoke(cbk, 1000);
		SCMPMessage reply = cbk.getMessageSync(1000);
		TestUtil.checkReply(reply);
		String sessionId = reply.getSessionId();

		// receive publication - get message
		SCMPReceivePublicationCall receivePublicationCall = new SCMPReceivePublicationCall(this.requester,
				TestConstants.pubServerName1, sessionId);
		receivePublicationCall.invoke(cbk, 1000);
		reply = cbk.getMessageSync(1000);
		TestUtil.checkReply(reply);
		Assert.assertFalse(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));

		SCMPClnUnsubscribeCall unSubscribeCall = new SCMPClnUnsubscribeCall(this.requester, TestConstants.pubServerName1, sessionId);
		unSubscribeCall.invoke(cbk, 3000);
		reply = cbk.getMessageSync(3000);
		TestUtil.checkReply(reply);
	}

	/**
	 * Description: subscribe - waits 2 seconds - another subscribe fails because no free server is available<br>
	 * Expectation: passes
	 */
	@Test
	public void t30_FailsNoFreeServer() throws Exception {
		SCMPClnSubscribeCall subscribeCall = new SCMPClnSubscribeCall(this.requester, TestConstants.pubServerName1);

		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setNoDataIntervalSeconds(2);
		subscribeCall.setMask(TestConstants.mask);
		TestCallback cbk = new TestCallback(true);
		subscribeCall.setRequestBody("2000");
		subscribeCall.invoke(cbk, 3000);

		subscribeCall = new SCMPClnSubscribeCall(this.requester, TestConstants.pubServerName1);

		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setNoDataIntervalSeconds(2);
		subscribeCall.setMask(TestConstants.mask);
		TestCallback cbk1 = new TestCallback(true);
		subscribeCall.invoke(cbk1, 1000);

		SCMPMessage reply = cbk.getMessageSync(1000);
		SCMPMessage reply1 = cbk1.getMessageSync(1000);
		String sessionId = reply.getSessionId();

		TestUtil.checkReply(reply);
		Assert.assertTrue(reply1.isFault());
		TestUtil.verifyError(reply1, SCMPError.NO_FREE_SERVER, SCMPMsgType.CLN_SUBSCRIBE);

		SCMPClnUnsubscribeCall unSubscribeCall = new SCMPClnUnsubscribeCall(this.requester, TestConstants.pubServerName1, sessionId);
		unSubscribeCall.invoke(cbk, 3000);
		TestUtil.checkReply(cbk.getMessageSync(3000));
	}

	/**
	 * Description: subscribe - receives large message<br>
	 * Expectation: passes
	 */
	@Test
	public void t40_GetLargeMessage() throws Exception {
		SCMPClnSubscribeCall subscribeCall = new SCMPClnSubscribeCall(this.requester, TestConstants.pubServerName1);

		subscribeCall.setSessionInfo(TestConstants.publishLargeMsgCmd);
		subscribeCall.setNoDataIntervalSeconds(10);
		subscribeCall.setMask(TestConstants.mask);
		subscribeCall.setRequestBody("get large message");
		TestCallback cbk = new TestCallback();
		subscribeCall.invoke(cbk, 10000);
		SCMPMessage reply = cbk.getMessageSync(3000);
		TestUtil.checkReply(reply);
		String sessionId = reply.getSessionId();

		SCMPReceivePublicationCall receivePublicationCall = new SCMPReceivePublicationCall(this.requester,
				TestConstants.pubServerName1, sessionId);
		receivePublicationCall.invoke(cbk, 2000);
		reply = cbk.getMessageSync(2000);
		Assert.assertTrue(reply.isLargeMessage());
		Assert.assertEquals(TestUtil.getLargeString(), reply.getBody());

		SCMPClnUnsubscribeCall unSubscribeCall = new SCMPClnUnsubscribeCall(this.requester, TestConstants.pubServerName1, sessionId);
		unSubscribeCall.invoke(cbk, 3000);
		TestUtil.checkReply(cbk.getMessageSync(3000));
	}
}