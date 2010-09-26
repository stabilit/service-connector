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
package org.serviceconnector.test.sc.subscriptionChange;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnChangeSubscriptionCall;
import org.serviceconnector.call.SCMPClnSubscribeCall;
import org.serviceconnector.call.SCMPReceivePublicationCall;
import org.serviceconnector.conf.RequesterConfigPool;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.test.sc.SCTest;
import org.serviceconnector.test.sc.SetupTestCases;
import org.serviceconnector.test.sc.SuperTestCase;
import org.serviceconnector.test.sc.connectionPool.TestContext;
import org.serviceconnector.util.SynchronousCallback;



public class ClnChangeSubscriptionTestCase extends SuperTestCase {

	private ChangeSubscriptionCallback callback = new ChangeSubscriptionCallback();
	private static int run = -1;

	/**
	 * @param fileName
	 */
	public ClnChangeSubscriptionTestCase(String fileName) {
		super(fileName);
	}

	@Before
	public void setup() throws Exception {
		SetupTestCases.setupAll();
		try {
			this.config = new RequesterConfigPool();
			this.config.load(fileName);
			this.testContext = new TestContext(this.config.getRequesterConfig(), this.msgId);
			req = new SCRequester(this.testContext);
			if (run != -1) {
				run = 5;
			} else {
				run = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void subscribe() throws Exception {
		SCMPClnSubscribeCall subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL.newInstance(req,
				"publish-simulation");

		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setNoDataIntervalSeconds(2);
		subscribeCall.setMask("000012100012832102FADF-----------------------");
		subscribeCall.invoke(this.callback, 3000);
		SCMPMessage reply = this.callback.getMessageSync();
		SCTest.checkReply(reply);
		String sessionId = reply.getSessionId();

		for (int i = 0; i < 2; i++) {

			// receive publication - no data
			SCMPReceivePublicationCall receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION
					.newInstance(this.req, "publish-simulation", sessionId);
			receivePublicationCall.invoke(this.callback, 3000);
			reply = this.callback.getMessageSync();
			Assert.assertTrue(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));
		}

		SCMPClnChangeSubscriptionCall changeSubscriptionCall = (SCMPClnChangeSubscriptionCall) SCMPCallFactory.CLN_CHANGE_SUBSCRIPTION
				.newInstance(this.req, "publish-simulation", sessionId);
		changeSubscriptionCall.setMask("000012100012832102FADF-----------X-----------");
		changeSubscriptionCall.invoke(this.callback, 3000);
		SCTest.checkReply(this.callback.getMessageSync());

		// receive publication first message
		SCMPReceivePublicationCall receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION
				.newInstance(this.req, "publish-simulation", sessionId);
		receivePublicationCall.invoke(this.callback, 3000);
		reply = this.callback.getMessageSync();
		Assert.assertFalse(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));
		Assert.assertEquals("publish message nr " + (2 + run), reply.getBody());
		Thread.sleep(3000);
		for (int i = 1; i < 3; i++) {
			// receive publication first message
			receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION.newInstance(
					this.req, "publish-simulation", sessionId);
			receivePublicationCall.invoke(this.callback, 3000);
			reply = this.callback.getMessageSync();
			Assert.assertFalse(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));
			Assert.assertEquals("publish message nr " + (2 + i + run), reply.getBody());
		}
	}

	private class ChangeSubscriptionCallback extends SynchronousCallback {
	}
}
