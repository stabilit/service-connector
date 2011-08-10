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
package org.serviceconnector.test.system.scmp.casc1;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestCallback;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.call.SCMPClnSubscribeCall;
import org.serviceconnector.call.SCMPClnUnsubscribeCall;
import org.serviceconnector.call.SCMPReceivePublicationCall;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.ctrl.util.ServerDefinition;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.system.SystemSuperTest;

public class SCMPClnSubscribeCasc1Test extends SystemSuperTest {

	protected SCRequester requester;

	public SCMPClnSubscribeCasc1Test() {
		SCMPClnSubscribeCasc1Test.setUp1CascadedServiceConnectorAndServer();
	}

	public static void setUpServiceConnectorAndServer() {
		SystemSuperTest.setUpServiceConnectorAndServer();
		SCMPClnSubscribeCasc1Test.setUpServer();
	}

	public static void setUp1CascadedServiceConnectorAndServer() {
		SystemSuperTest.setUp1CascadedServiceConnectorAndServer();
		SCMPClnSubscribeCasc1Test.setUpServer();
	}

	public static void setUp2CascadedServiceConnectorAndServer() {
		SystemSuperTest.setUp2CascadedServiceConnectorAndServer();
		SCMPClnSubscribeCasc1Test.setUpServer();
	}

	public static void setUpServer() {
		// need to have a publish service here
		List<ServerDefinition> srvToSC0CascDefs = new ArrayList<ServerDefinition>();
		ServerDefinition srvToSC0CascDef = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH,
				TestConstants.log4jSrvProperties, TestConstants.pubServerName1, TestConstants.PORT_PUB_SRV_TCP,
				TestConstants.PORT_SC0_TCP, 2, 2, TestConstants.pubServiceName1);
		srvToSC0CascDefs.add(srvToSC0CascDef);
		SystemSuperTest.srvDefs = srvToSC0CascDefs;
	}

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		this.requester = new SCRequester(new RemoteNodeConfiguration(TestConstants.RemoteNodeName, TestConstants.HOST,
				TestConstants.PORT_SC1_HTTP, ConnectionType.NETTY_HTTP.getValue(), 0, 0, 10), 0);
		AppContext.init();
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			this.requester.destroy();
		} catch (Exception e) {
		}
		this.requester = null;
		super.afterOneTest();
	}

	/**
	 * Description: subscribe - receive publication call no data received<br>
	 * Expectation: passes
	 */
	@Test
	public void t10_ReceiveNoData() throws Exception {
		SCMPClnSubscribeCall subscribeCall = new SCMPClnSubscribeCall(this.requester, TestConstants.pubServerName1);

		subscribeCall.setNoDataIntervalSeconds(10);
		subscribeCall.setMask(TestConstants.mask);
		TestCallback cbk = new TestCallback(true);
		subscribeCall.invoke(cbk, 3000);
		SCMPMessage reply = cbk.getMessageSync(3000);
		TestUtil.checkReply(reply);
		String sessionId = reply.getSessionId();
		// receive publication - no data
		SCMPReceivePublicationCall receivePublicationCall = new SCMPReceivePublicationCall(this.requester,
				TestConstants.pubServerName1, sessionId);
		cbk = new TestCallback(true);
		receivePublicationCall.invoke(cbk, 30000);
		reply = cbk.getMessageSync(30000);
		TestUtil.checkReply(reply);
		Assert.assertTrue(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));

		SCMPClnUnsubscribeCall unSubscribeCall = new SCMPClnUnsubscribeCall(this.requester, TestConstants.pubServerName1, sessionId);
		cbk = new TestCallback(true);
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

		subscribeCall.setNoDataIntervalSeconds(10);
		subscribeCall.setSessionInfo(TestConstants.publishUncompressedMsgCmd);
		subscribeCall.setMask(TestConstants.mask);
		subscribeCall.setRequestBody("5");
		TestCallback cbk = new TestCallback(true);
		subscribeCall.invoke(cbk, 2000);
		SCMPMessage reply = cbk.getMessageSync(2000);
		TestUtil.checkReply(reply);
		String sessionId = reply.getSessionId();

		// receive publication - get message
		SCMPReceivePublicationCall receivePublicationCall = new SCMPReceivePublicationCall(this.requester,
				TestConstants.pubServerName1, sessionId);
		receivePublicationCall.invoke(cbk, 2000);
		reply = cbk.getMessageSync(20000);
		TestUtil.checkReply(reply);
		Assert.assertFalse(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));

		SCMPClnUnsubscribeCall unSubscribeCall = new SCMPClnUnsubscribeCall(this.requester, TestConstants.pubServerName1, sessionId);
		unSubscribeCall.invoke(cbk, 3000);
		reply = cbk.getMessageSync(3000);
		TestUtil.checkReply(reply);
	}

	/**
	 * Description: subscribe - waits 2 seconds - another subscribe fails because operation times out. We are in cascaded mode means
	 * only one client request can reach the server. The other client requests wait on proxy's and an get back with operation time
	 * out. It differs from direct test.<br>
	 * Expectation: passes
	 */
	@Test
	public void t30_FailsNoFreeServer() throws Exception {
		SCMPClnSubscribeCall subscribeCall = new SCMPClnSubscribeCall(this.requester, TestConstants.pubServerName1);

		subscribeCall.setSessionInfo(TestConstants.sleepCmd);
		subscribeCall.setNoDataIntervalSeconds(10);
		subscribeCall.setMask(TestConstants.mask);
		TestCallback cbk = new TestCallback(true);
		subscribeCall.setRequestBody("3000");
		subscribeCall.invoke(cbk, 6000);

		Thread.sleep(100);
		subscribeCall = new SCMPClnSubscribeCall(this.requester, TestConstants.pubServerName1);

		subscribeCall.setNoDataIntervalSeconds(10);
		subscribeCall.setMask(TestConstants.mask);
		TestCallback cbk1 = new TestCallback(true);
		subscribeCall.invoke(cbk1, 2000);

		SCMPMessage reply = cbk.getMessageSync(6000);
		SCMPMessage reply1 = cbk1.getMessageSync(4000);
		String sessionId = reply.getSessionId();

		TestUtil.checkReply(reply);
		Assert.assertTrue(reply1.isFault());
		TestUtil.verifyError(reply1, SCMPError.OPERATION_TIMEOUT, SCMPMsgType.CLN_SUBSCRIBE);

		SCMPClnUnsubscribeCall unSubscribeCall = new SCMPClnUnsubscribeCall(this.requester, TestConstants.pubServerName1, sessionId);
		unSubscribeCall.invoke(cbk, 4000);
		TestUtil.checkReply(cbk.getMessageSync(4000));
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
		receivePublicationCall.invoke(cbk, 20000);
		reply = cbk.getMessageSync(20000);
		Assert.assertTrue(reply.isLargeMessage());
		Assert.assertEquals(TestUtil.getLargeString(), reply.getBody());

		SCMPClnUnsubscribeCall unSubscribeCall = new SCMPClnUnsubscribeCall(this.requester, TestConstants.pubServerName1, sessionId);
		unSubscribeCall.invoke(cbk, 3000);
		TestUtil.checkReply(cbk.getMessageSync(3000));
	}

	/**
	 * Description: subscribe - receive publication call message received and wait to long, subscription times out<br>
	 * Expectation: passes
	 */
	@Test
	public void t43_SlowClient_SubscriptionTimesOut() throws Exception {
		SCMPClnSubscribeCall subscribeCall = new SCMPClnSubscribeCall(this.requester, TestConstants.pubServerName1);

		subscribeCall.setNoDataIntervalSeconds(10);
		subscribeCall.setSessionInfo(TestConstants.publishUncompressedMsgCmd);
		subscribeCall.setMask(TestConstants.mask);
		subscribeCall.setRequestBody("5");
		TestCallback cbk = new TestCallback(true);
		subscribeCall.invoke(cbk, 2000);
		SCMPMessage reply = cbk.getMessageSync(1000);
		TestUtil.checkReply(reply);
		String sessionId = reply.getSessionId();

		// receive publication - get message
		SCMPReceivePublicationCall receivePublicationCall = new SCMPReceivePublicationCall(this.requester,
				TestConstants.pubServerName1, sessionId);
		receivePublicationCall.invoke(cbk, 20000);
		reply = cbk.getMessageSync(15000);
		TestUtil.checkReply(reply);
		Assert.assertFalse(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));

		Thread.sleep(65000);
		receivePublicationCall = new SCMPReceivePublicationCall(this.requester, TestConstants.pubServerName1, sessionId);
		receivePublicationCall.invoke(cbk, 2000);
		reply = cbk.getMessageSync(1000);
		TestUtil.verifyError(reply, SCMPError.SUBSCRIPTION_NOT_FOUND, SCMPMsgType.RECEIVE_PUBLICATION);
	}

	/**
	 * Description: 2 subscribes - one client waits to long and times out<br>
	 * Expectation: passes
	 */
	@Test
	public void t44_OneSlowClientOfTwo() throws Exception {
		SCMPClnSubscribeCall subscribeCall1 = new SCMPClnSubscribeCall(this.requester, TestConstants.pubServerName1);

		subscribeCall1.setNoDataIntervalSeconds(10);
		subscribeCall1.setSessionInfo(TestConstants.publishUncompressedMsgCmd);
		subscribeCall1.setMask(TestConstants.mask);
		subscribeCall1.setRequestBody("5");
		TestCallback cbk1 = new TestCallback(true);
		subscribeCall1.invoke(cbk1, 2000);
		SCMPMessage reply1 = cbk1.getMessageSync(1000);
		TestUtil.checkReply(reply1);
		String sessionId1 = reply1.getSessionId();

		SCMPClnSubscribeCall subscribeCall2 = new SCMPClnSubscribeCall(this.requester, TestConstants.pubServerName1);

		subscribeCall2.setNoDataIntervalSeconds(10);
		subscribeCall2.setSessionInfo(TestConstants.publishUncompressedMsgCmd);
		subscribeCall2.setMask(TestConstants.mask);
		subscribeCall2.setRequestBody("5");
		TestCallback cbk2 = new TestCallback(true);
		subscribeCall2.invoke(cbk2, 2000);
		SCMPMessage reply2 = cbk2.getMessageSync(2000);
		TestUtil.checkReply(reply2);
		String sessionId2 = reply2.getSessionId();

		// sleep 50 seconds - then send RCP for client1
		Thread.sleep(50000);
		// receive publication - get message
		SCMPReceivePublicationCall receivePublicationCall1 = new SCMPReceivePublicationCall(this.requester,
				TestConstants.pubServerName1, sessionId1);
		receivePublicationCall1.invoke(cbk1, 20000);
		reply1 = cbk1.getMessageSync(15000);
		TestUtil.checkReply(reply1);
		Assert.assertFalse(reply1.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));

		// sleep 15 seconds - client2 timed out
		Thread.sleep(15000);
		SCMPReceivePublicationCall receivePublicationCall2 = new SCMPReceivePublicationCall(this.requester,
				TestConstants.pubServerName1, sessionId2);
		receivePublicationCall2.invoke(cbk1, 2000);
		reply1 = cbk1.getMessageSync(1000);
		TestUtil.verifyError(reply1, SCMPError.SUBSCRIPTION_NOT_FOUND, SCMPMsgType.RECEIVE_PUBLICATION);

		SCMPClnUnsubscribeCall unSubscribeCall = new SCMPClnUnsubscribeCall(this.requester, TestConstants.pubServerName1,
				sessionId1);
		unSubscribeCall.invoke(new TestCallback(false), 3000);
		unSubscribeCall = new SCMPClnUnsubscribeCall(this.requester, TestConstants.pubServerName1, sessionId2);
		unSubscribeCall.invoke(new TestCallback(false), 3000);
	}
}