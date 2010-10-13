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
package org.serviceconnector.test.sc.subscribe;

import junit.framework.Assert;

import org.junit.Test;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnSubscribeCall;
import org.serviceconnector.call.SCMPClnUnsubscribeCall;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.sc.SCTest;
import org.serviceconnector.test.sc.SuperTestCase;
import org.serviceconnector.util.SynchronousCallback;

public class ClnSubscribeWaitMechanismTestCase extends SuperTestCase {

	/**
	 * @param fileName
	 */
	public ClnSubscribeWaitMechanismTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void waitForSessionInSubscribe_secondCCSTimesOut() throws Exception {
		SCMPClnSubscribeCall subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL.newInstance(req,
				"publish-simulation");

		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setNoDataIntervalSeconds(20);
		subscribeCall.setMask("000012100012832102FADF-----------X-----------");
		SubscribeCallback callback = new SubscribeCallback(true);
		subscribeCall.setRequestBody("wait:2000");
		subscribeCall.invoke(callback, 10000);

		// to assure second create is not faster
		Thread.sleep(20);
		subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL
				.newInstance(req, "publish-simulation");

		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setNoDataIntervalSeconds(20);
		subscribeCall.setMask("000012100012832102FADF-----------X-----------");
		SubscribeCallback callback1 = new SubscribeCallback(true);
		subscribeCall.invoke(callback1, 1000);

		SCMPMessage reply = callback.getMessageSync();
		SCMPMessage reply1 = callback1.getMessageSync();

		SCTest.checkReply(reply);
		Assert.assertFalse(reply.isFault());
		Assert.assertTrue(reply1.isFault());
		SCTest.verifyError(reply1, SCMPError.NO_FREE_SESSION, "[for service publish-simulation]",
				SCMPMsgType.CLN_SUBSCRIBE);

		SCMPClnUnsubscribeCall unSubscribeCall = (SCMPClnUnsubscribeCall) SCMPCallFactory.CLN_UNSUBSCRIBE_CALL
				.newInstance(req, "publish-simulation", reply.getSessionId());
		unSubscribeCall.invoke(callback, 3000);
		reply = callback.getMessageSync();
		SCTest.checkReply(reply);
	}

	private class SubscribeCallback extends SynchronousCallback {

		public SubscribeCallback(boolean synchronous) {
			this.synchronous = synchronous;
		}
	}
}
