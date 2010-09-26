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
package org.serviceconector.test.sc.publish;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.serviceconector.test.sc.SCTest;
import org.serviceconector.test.sc.SetupTestCases;
import org.serviceconector.test.sc.SuperTestCase;
import org.serviceconector.test.sc.connectionPool.TestContext;
import org.serviceconnector.Constants;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnSubscribeCall;
import org.serviceconnector.call.SCMPReceivePublicationCall;
import org.serviceconnector.conf.RequesterConfigPool;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.util.SynchronousCallback;



public class PublishLargeMessagesTestCase extends SuperTestCase {

	private PublishCallback callback = new PublishCallback();;

	/**
	 * @param fileName
	 */
	public PublishLargeMessagesTestCase(String fileName) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void receiveLargeMessage() throws Exception {
		SCMPClnSubscribeCall subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL.newInstance(req,
				"publish-simulation");

		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setNoDataIntervalSeconds(2);
		subscribeCall.setMask("000012100012832102FADF-----------X-----------");
		subscribeCall.setRequestBody("large");
		subscribeCall.invoke(this.callback, 3000);
		SCMPMessage reply = this.callback.getMessageSync();
		SCTest.checkReply(reply);
		String sessionId = reply.getSessionId();

		StringBuilder sb = new StringBuilder();
		sb.append("large:");
		for (int i = 0; i < 100000; i++) {
			if (sb.length() > Constants.LARGE_MESSAGE_LIMIT + 10000) {
				break;
			}
			sb.append(i);
		}
		Thread.sleep(4000);
		for (int i = 1; i < 3; i++) {
			// receive publication first message
			SCMPReceivePublicationCall receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION
					.newInstance(this.req, "publish-simulation", sessionId);
			receivePublicationCall.invoke(this.callback, 3000);
			reply = this.callback.getMessageSync();
			Assert.assertTrue(reply.isLargeMessage());
			Assert.assertEquals(sb.toString(), reply.getBody());
			Thread.sleep(2000);
		}
	}

	private class PublishCallback extends SynchronousCallback {
	}
}
