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
import org.serviceconnector.TestUtil;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnSubscribeCall;
import org.serviceconnector.call.SCMPClnUnsubscribeCall;
import org.serviceconnector.call.SCMPReceivePublicationCall;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.sc.SuperTestCase;
import org.serviceconnector.util.SynchronousCallback;

public class ClnSubscribeTestCase extends SuperTestCase {

	/**
	 * @param fileName
	 */
	public ClnSubscribeTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void subscribe() throws Exception {
		SCMPClnSubscribeCall subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL.newInstance(req, "publish-1");

		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setNoDataIntervalSeconds(3);
		subscribeCall.setMask("000012100012832102FADF-----------X-----------");
		TestSubscribeCallback callback = new TestSubscribeCallback(true);
		subscribeCall.invoke(callback, 3000);
		SCMPMessage reply = callback.getMessageSync(3000);
		TestUtil.checkReply(reply);
		String sessionId = reply.getSessionId();

		// receive publication - no data
		SCMPReceivePublicationCall receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION
				.newInstance(this.req, "publish-1", sessionId);
		callback = new TestSubscribeCallback(true);
		receivePublicationCall.invoke(callback, 30000);
		reply = callback.getMessageSync(3000);
		TestUtil.checkReply(reply);
		Assert.assertFalse(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));

		// receive publication first message
		receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION.newInstance(this.req, "publish-1",
				sessionId);
		callback = new TestSubscribeCallback(true);
		receivePublicationCall.invoke(callback, 10000);
		reply = callback.getMessageSync(3000);
		TestUtil.checkReply(reply);
		Assert.assertFalse(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));

		for (int i = 1; i < 3; i++) {
			// receive publication first message
			receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION.newInstance(this.req,
					"publish-1", sessionId);
			callback = new TestSubscribeCallback(true);
			receivePublicationCall.invoke(callback, 3000);
			reply = callback.getMessageSync(3000);
			TestUtil.checkReply(reply);
			Assert.assertFalse(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));
		}
		SCMPClnUnsubscribeCall unSubscribeCall = (SCMPClnUnsubscribeCall) SCMPCallFactory.CLN_UNSUBSCRIBE_CALL.newInstance(req,
				"publish-1", sessionId);
		callback = new TestSubscribeCallback(true);
		unSubscribeCall.invoke(callback, 3000);
		reply = callback.getMessageSync(3000);
		TestUtil.checkReply(reply);
	}

	@Test
	public void failSubscribeWrongHeader() throws Exception {
		SCMPClnSubscribeCall subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL.newInstance(req, "publish-1");

		// mask not set
		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setNoDataIntervalSeconds(50);
		TestSubscribeCallback callback = new TestSubscribeCallback(true);
		subscribeCall.invoke(callback, 3000);
		SCMPMessage fault = callback.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		TestUtil.verifyError((SCMPMessageFault) fault, SCMPError.HV_ERROR, SCMPMsgType.CLN_SUBSCRIBE);

		// noDataInterval wrong
		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setMask("ACD");
		subscribeCall.setNoDataIntervalSeconds(0);
		callback = new TestSubscribeCallback(true);
		subscribeCall.invoke(callback, 3000);
		fault = callback.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		TestUtil.verifyError((SCMPMessageFault) fault, SCMPError.HV_ERROR, SCMPMsgType.CLN_SUBSCRIBE);
	}

	private class TestSubscribeCallback extends SynchronousCallback {
		public TestSubscribeCallback(boolean synchronous) {
			this.synchronous = synchronous;
		}
	}
}
