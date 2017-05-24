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

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestCallback;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.call.SCMPClnSubscribeCall;
import org.serviceconnector.call.SCMPClnUnsubscribeCall;
import org.serviceconnector.call.SCMPReceivePublicationCall;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.system.scmp.casc1.SCMPClnSubscribeCasc1Test;

import junit.framework.Assert;

public class SCMPClnSubscribeTest extends SCMPClnSubscribeCasc1Test {

	public SCMPClnSubscribeTest() {
		SCMPClnSubscribeCasc1Test.setUpServiceConnectorAndServer();
	}

	@Override
	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		this.requester = new SCRequester(
				new RemoteNodeConfiguration(TestConstants.RemoteNodeName, TestConstants.HOST, TestConstants.PORT_SC0_HTTP, ConnectionType.NETTY_HTTP.getValue(), 0, 0, 10), 0);
		AppContext.init();
	}

	/**
	 * Description: subscribe - waits 2 seconds - another subscribe fails because no free server is available<br>
	 * Expectation: passes
	 */
	@Override
	public void t30_FailsNoFreeServer() throws Exception {
		SCMPClnSubscribeCall subscribeCall = new SCMPClnSubscribeCall(this.requester, TestConstants.pubServerName1);

		subscribeCall.setSessionInfo(TestConstants.sleepCmd);
		subscribeCall.setNoDataIntervalSeconds(10);
		subscribeCall.setMask(TestConstants.mask);
		TestCallback cbk = new TestCallback();
		TestCallback cbk1 = new TestCallback();
		subscribeCall.setRequestBody("3000");
		subscribeCall.invoke(cbk, 5000);
		subscribeCall.invoke(cbk1, 5000);
		Thread.sleep(100);
		subscribeCall = new SCMPClnSubscribeCall(this.requester, TestConstants.pubServerName1);

		subscribeCall.setNoDataIntervalSeconds(10);
		subscribeCall.setMask(TestConstants.mask);
		TestCallback cbk3 = new TestCallback();
		subscribeCall.invoke(cbk3, 2000);

		SCMPMessage reply = cbk.getMessageSync(5000);
		SCMPMessage reply1 = cbk1.getMessageSync(4000);
		SCMPMessage reply3 = cbk3.getMessageSync(4000);
		String sessionId = reply.getSessionId();

		TestUtil.checkReply(reply);
		TestUtil.checkReply(reply1);
		Assert.assertTrue(reply3.isFault());
		TestUtil.verifyError(reply3, SCMPError.NO_FREE_SERVER, SCMPMsgType.CLN_SUBSCRIBE);

		SCMPClnUnsubscribeCall unSubscribeCall = new SCMPClnUnsubscribeCall(this.requester, TestConstants.pubServerName1, sessionId);
		cbk = new TestCallback();
		unSubscribeCall.invoke(cbk, 4000);
		TestUtil.checkReply(cbk.getMessageSync(4000));
	}

	/**
	 * Description: This test fills the messageQueue with 100'000 messages<br>
	 * Expectation: passes, SC should work properly with 100'000 messages
	 */
	@Test
	public void t50_FillMessageQueueWith100000Messages() throws Exception {
		SCMPClnSubscribeCall subscribeCall = new SCMPClnSubscribeCall(this.requester, TestConstants.pubServerName1);

		subscribeCall.setNoDataIntervalSeconds(10);
		subscribeCall.setSessionInfo(TestConstants.publishMsgWithDelayCmd);
		subscribeCall.setMask(TestConstants.mask);
		subscribeCall.setRequestBody("100003|1");
		TestCallback cbk = new TestCallback(true);
		subscribeCall.invoke(cbk, 2000);
		SCMPMessage reply = cbk.getMessageSync(2000);
		TestUtil.checkReply(reply);
		String sessionId = reply.getSessionId();

		Thread.sleep(55000);
		// receive publication - get message
		SCMPReceivePublicationCall receivePublicationCall = new SCMPReceivePublicationCall(this.requester, TestConstants.pubServerName1, sessionId);
		cbk = new TestCallback();
		receivePublicationCall.invoke(cbk, 20000);
		reply = cbk.getMessageSync(20000);
		TestUtil.checkReply(reply);
		Assert.assertFalse(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));
		Thread.sleep(55000);
		cbk = new TestCallback();
		receivePublicationCall.invoke(cbk, 20000);
		reply = cbk.getMessageSync(20000);
		TestUtil.checkReply(reply);
		Assert.assertFalse(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));
		Thread.sleep(55000);
		cbk = new TestCallback();
		receivePublicationCall.invoke(cbk, 20000);
		reply = cbk.getMessageSync(20000);
		TestUtil.checkReply(reply);
		Assert.assertFalse(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));
		Thread.sleep(55000);
		SCMPClnUnsubscribeCall unSubscribeCall = new SCMPClnUnsubscribeCall(this.requester, TestConstants.pubServerName1, sessionId);
		cbk = new TestCallback();
		unSubscribeCall.invoke(cbk, 3000);
		reply = cbk.getMessageSync(3000);
		TestUtil.checkReply(reply);
	}
}
