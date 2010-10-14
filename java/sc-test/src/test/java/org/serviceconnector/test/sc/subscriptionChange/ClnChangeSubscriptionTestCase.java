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

import org.junit.Test;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnChangeSubscriptionCall;
import org.serviceconnector.call.SCMPClnSubscribeCall;
import org.serviceconnector.call.SCMPClnUnsubscribeCall;
import org.serviceconnector.call.SCMPReceivePublicationCall;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.sc.SCTest;
import org.serviceconnector.test.sc.SuperTestCase;
import org.serviceconnector.util.SynchronousCallback;

public class ClnChangeSubscriptionTestCase extends SuperTestCase {

	/**
	 * @param fileName
	 */
	public ClnChangeSubscriptionTestCase(String fileName) {
		super(fileName);
	}


	public void changeSubscription() throws Exception {
		SCMPClnSubscribeCall subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL.newInstance(req,
				"publish-simulation");

		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setNoDataIntervalSeconds(2);
		subscribeCall.setMask("000012100012832102FADF-----------------------");
		ChangeSubscriptionCallback callback = new ChangeSubscriptionCallback(true);
		subscribeCall.invoke(callback, 3000);
		SCMPMessage reply = callback.getMessageSync();
		SCTest.checkReply(reply);
		String sessionId = reply.getSessionId();

		for (int i = 0; i < 2; i++) {

			// receive publication - no data
			SCMPReceivePublicationCall receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION
					.newInstance(this.req, "publish-simulation", sessionId);
			callback = new ChangeSubscriptionCallback(true);
			receivePublicationCall.invoke(callback, 3000);
			reply = callback.getMessageSync();
			Assert.assertTrue(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));
		}

		SCMPClnChangeSubscriptionCall changeSubscriptionCall = (SCMPClnChangeSubscriptionCall) SCMPCallFactory.CLN_CHANGE_SUBSCRIPTION
				.newInstance(this.req, "publish-simulation", sessionId);
		changeSubscriptionCall.setMask("000012100012832102FADF-----------X-----------");
		callback = new ChangeSubscriptionCallback(true);
		changeSubscriptionCall.invoke(callback, 3000);
		SCTest.checkReply(callback.getMessageSync());

		// receive publication first message
		SCMPReceivePublicationCall receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION
				.newInstance(this.req, "publish-simulation", sessionId);
		callback = new ChangeSubscriptionCallback(true);
		receivePublicationCall.invoke(callback, 3000);
		reply = callback.getMessageSync();
		Assert.assertFalse(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));

		for (int i = 1; i < 3; i++) {
			// receive publication first message
			receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION.newInstance(
					this.req, "publish-simulation", sessionId);
			callback = new ChangeSubscriptionCallback(true);
			receivePublicationCall.invoke(callback, 3000);
			reply = callback.getMessageSync();
			Assert.assertFalse(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));
		}
		SCMPClnUnsubscribeCall unSubscribeCall = (SCMPClnUnsubscribeCall) SCMPCallFactory.CLN_UNSUBSCRIBE_CALL
				.newInstance(req, "publish-simulation", sessionId);
		callback = new ChangeSubscriptionCallback(true);
		unSubscribeCall.invoke(callback, 3000);
		reply = callback.getMessageSync();
		SCTest.checkReply(reply);
	}

	@Test
	public void changeSubscriptionWaitForConnection() throws Exception {
		SCMPClnSubscribeCall subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL.newInstance(req,
				"publish-simulation");

		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setNoDataIntervalSeconds(2);
		subscribeCall.setMask("000012100012832102FADF-----------X-----------");
		ChangeSubscriptionCallback callback = new ChangeSubscriptionCallback(true);
		subscribeCall.invoke(callback, 3000);
		SCMPMessage reply = callback.getMessageSync();
		SCTest.checkReply(reply);
		String sessionId = reply.getSessionId();

		SCMPClnChangeSubscriptionCall changeSubscriptionCall = (SCMPClnChangeSubscriptionCall) SCMPCallFactory.CLN_CHANGE_SUBSCRIPTION
				.newInstance(this.req, "publish-simulation", sessionId);
		changeSubscriptionCall.setMask("000012100012832102FADF-----------X-----------");
		changeSubscriptionCall.setRequestBody("wait:2000");
		callback = new ChangeSubscriptionCallback(true);
		changeSubscriptionCall.invoke(callback, 3000);

		changeSubscriptionCall = (SCMPClnChangeSubscriptionCall) SCMPCallFactory.CLN_CHANGE_SUBSCRIPTION.newInstance(
				this.req, "publish-simulation", sessionId);
		changeSubscriptionCall.setMask("000012100012832102FADF-----------X-----------");
		ChangeSubscriptionCallback callback1 = new ChangeSubscriptionCallback(true);
		changeSubscriptionCall.invoke(callback1, 1000);

		reply = callback.getMessageSync();
		SCMPMessage reply1 = callback1.getMessageSync();
		SCTest.checkReply(reply);
		Assert.assertFalse(reply.isFault());
		Assert.assertTrue(reply1.isFault());
		SCTest.verifyError(reply1, SCMPError.SC_ERROR, "[no free connection on server for service publish-simulation]",
				SCMPMsgType.CLN_CHANGE_SUBSCRIPTION);

		SCMPClnUnsubscribeCall unSubscribeCall = (SCMPClnUnsubscribeCall) SCMPCallFactory.CLN_UNSUBSCRIBE_CALL
				.newInstance(req, "publish-simulation", sessionId);
		callback = new ChangeSubscriptionCallback(true);
		unSubscribeCall.invoke(callback, 3000);
		reply = callback.getMessageSync();
		SCTest.checkReply(reply);
	}

	private class ChangeSubscriptionCallback extends SynchronousCallback {
		public ChangeSubscriptionCallback(boolean synchronous) {
			this.synchronous = synchronous;
		}
	}
}
