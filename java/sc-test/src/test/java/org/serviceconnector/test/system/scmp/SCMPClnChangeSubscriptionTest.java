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
import org.serviceconnector.call.SCMPClnChangeSubscriptionCall;
import org.serviceconnector.call.SCMPClnSubscribeCall;
import org.serviceconnector.call.SCMPClnUnsubscribeCall;
import org.serviceconnector.call.SCMPReceivePublicationCall;
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

public class SCMPClnChangeSubscriptionTest extends SystemSuperTest {

	private ProcessCtx pubSrvCtx;
	private SCRequester requester;

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		pubSrvCtx = ctrl.startServer(TestConstants.COMMUNICATOR_TYPE_PUBLISH, TestConstants.log4jSrvProperties,
				TestConstants.pubServerName1, TestConstants.PORT_PUB_SRV_TCP, TestConstants.PORT_SC_TCP, 1, 1,
				TestConstants.pubServiceName1);
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
			ctrl.stopServer(pubSrvCtx);
		} catch (Exception e) {
		}
		pubSrvCtx = null;
		super.afterOneTest();
	}

	/**
	 * Description: subscribe, receive publication no data message, change subscription, get message and unsubscribe<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_GetMessageAfterChange() throws Exception {
		SCMPClnSubscribeCall subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL.newInstance(this.requester,
				TestConstants.pubServerName1);

		subscribeCall.setSessionInfo(TestConstants.publishMsgWithDelayCmd);
		subscribeCall.setNoDataIntervalSeconds(2);
		// mask does not match
		subscribeCall.setMask(TestConstants.mask1);
		// publish 10 messages, wait 1 second after publish each message
		subscribeCall.setRequestBody("10|1000");
		TestCallback cbk = new TestCallback(true);
		subscribeCall.invoke(cbk, 1000);
		SCMPMessage reply = cbk.getMessageSync(1000);
		TestUtil.checkReply(reply);
		String sessionId = reply.getSessionId();

		// receive publication - no data
		SCMPReceivePublicationCall receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION
				.newInstance(this.requester, TestConstants.pubServerName1, sessionId);
		receivePublicationCall.invoke(cbk, 3000);
		reply = cbk.getMessageSync(3000);
		Assert.assertTrue(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));

		SCMPClnChangeSubscriptionCall changeSubscriptionCall = (SCMPClnChangeSubscriptionCall) SCMPCallFactory.CLN_CHANGE_SUBSCRIPTION
				.newInstance(this.requester, TestConstants.pubServerName1, sessionId);
		// mask matches now
		changeSubscriptionCall.setMask(TestConstants.mask);
		changeSubscriptionCall.invoke(cbk, 1000);
		TestUtil.checkReply(cbk.getMessageSync(1000));

		// receive publication first message
		receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION.newInstance(this.requester,
				TestConstants.pubServerName1, sessionId);
		receivePublicationCall.invoke(cbk, 1000);
		reply = cbk.getMessageSync(1000);
		Assert.assertFalse(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));

		SCMPClnUnsubscribeCall unSubscribeCall = (SCMPClnUnsubscribeCall) SCMPCallFactory.CLN_UNSUBSCRIBE_CALL.newInstance(
				this.requester, TestConstants.pubServerName1, sessionId);
		unSubscribeCall.invoke(cbk, 1000);
		reply = cbk.getMessageSync(1000);
		TestUtil.checkReply(reply);
	}

	/**
	 * Description: change subscription twice, second one fails because there is no free connection<br>
	 * Expectation: passes
	 */
	@Test
	public void t20_ChangeTwiceFailsNoFreeConnection() throws Exception {
		SCMPClnSubscribeCall subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL.newInstance(this.requester,
				TestConstants.pubServerName1);

		subscribeCall.setSessionInfo(TestConstants.publishCompressedMsgCmd);
		subscribeCall.setNoDataIntervalSeconds(2);
		subscribeCall.setMask(TestConstants.mask);
		subscribeCall.setRequestBody("100");
		TestCallback cbk = new TestCallback(true);
		subscribeCall.invoke(cbk, 1000);
		SCMPMessage reply = cbk.getMessageSync(1000);
		TestUtil.checkReply(reply);
		String sessionId = reply.getSessionId();

		SCMPClnChangeSubscriptionCall changeSubscriptionCall = (SCMPClnChangeSubscriptionCall) SCMPCallFactory.CLN_CHANGE_SUBSCRIPTION
				.newInstance(this.requester, TestConstants.pubServerName1, sessionId);
		// mask matches now
		changeSubscriptionCall.setMask(TestConstants.mask);
		changeSubscriptionCall.setSessionInfo(TestConstants.sleepCmd);
		changeSubscriptionCall.setRequestBody("2000");
		changeSubscriptionCall.invoke(cbk, 3000);

		changeSubscriptionCall = (SCMPClnChangeSubscriptionCall) SCMPCallFactory.CLN_CHANGE_SUBSCRIPTION.newInstance(this.requester,
				TestConstants.pubServerName1, sessionId);
		changeSubscriptionCall.setMask(TestConstants.mask);
		TestCallback cbk1 = new TestCallback(true);
		changeSubscriptionCall.invoke(cbk1, 1000);

		TestUtil.checkReply(cbk.getMessageSync(3000));
		reply = cbk1.getMessageSync(1000);
		Assert.assertTrue(reply.isFault());
		TestUtil.verifyError(reply, SCMPError.NO_FREE_CONNECTION, SCMPMsgType.CLN_CHANGE_SUBSCRIPTION);

		SCMPClnUnsubscribeCall unSubscribeCall = (SCMPClnUnsubscribeCall) SCMPCallFactory.CLN_UNSUBSCRIBE_CALL.newInstance(
				this.requester, TestConstants.pubServerName1, sessionId);
		unSubscribeCall.invoke(cbk, 1000);
		reply = cbk.getMessageSync(1000);
		TestUtil.checkReply(reply);
	}
}