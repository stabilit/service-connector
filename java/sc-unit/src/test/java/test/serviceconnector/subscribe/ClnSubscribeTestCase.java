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
package test.serviceconnector.subscribe;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnSubscribeCall;
import org.serviceconnector.call.SCMPReceivePublicationCall;
import org.serviceconnector.conf.RequesterConfigPool;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPFault;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.util.SynchronousCallback;

import test.serviceconnector.pool.TestContext;
import test.serviceconnector.unit.SCTest;
import test.serviceconnector.unit.SetupTestCases;
import test.serviceconnector.unit.SuperTestCase;


public class ClnSubscribeTestCase extends SuperTestCase {

	private SubscribeCallback callback = new SubscribeCallback();
	private static int index = 0;

	/**
	 * @param fileName
	 */
	public ClnSubscribeTestCase(String fileName) {
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
			if (ClnSubscribeTestCase.index != 0) {
				ClnSubscribeTestCase.index = 4;
			} else {
				ClnSubscribeTestCase.index = 1;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Test
	public void subscribe() throws Exception {
		SCMPClnSubscribeCall subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL.newInstance(req,
				"publish-simulation");

		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setNoDataIntervalSeconds(2);
		subscribeCall.setMask("000012100012832102FADF-----------X-----------");
		subscribeCall.invoke(this.callback, 3000);
		SCMPMessage reply = this.callback.getMessageSync();
		SCTest.checkReply(reply);
		String sessionId = reply.getSessionId();

		// receive publication - no data
		SCMPReceivePublicationCall receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION
				.newInstance(this.req, "publish-simulation", sessionId);
		receivePublicationCall.invoke(this.callback, 3000);
		reply = this.callback.getMessageSync();
		Assert.assertTrue(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));

		// receive publication first message
		receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION.newInstance(this.req,
				"publish-simulation", sessionId);
		receivePublicationCall.invoke(this.callback, 3000);
		reply = this.callback.getMessageSync();
		Assert.assertFalse(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));
		Assert.assertEquals("publish message nr " + ClnSubscribeTestCase.index, reply.getBody());
		Thread.sleep(3000);
		for (int i = 1; i < 3; i++) {
			// receive publication first message
			receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION.newInstance(
					this.req, "publish-simulation", sessionId);
			receivePublicationCall.invoke(this.callback, 3000);
			reply = this.callback.getMessageSync();
			Assert.assertFalse(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));
			Assert.assertEquals("publish message nr " + (ClnSubscribeTestCase.index + i), reply.getBody());
		}
	}

	@Test
	public void failSubscribeWrongHeader() throws Exception {
		SCMPClnSubscribeCall subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL.newInstance(req,
				"publish-simulation");

		// mask not set
		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setNoDataIntervalSeconds(50);
		subscribeCall.invoke(this.callback, 3000);
		SCMPMessage fault = this.callback.getMessageSync();
		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.HV_ERROR, " [IntValue must be set]", SCMPMsgType.CLN_SUBSCRIBE);

		// mask not valid
		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setNoDataIntervalSeconds(50);
		subscribeCall.setMask("%%%ACSD");
		subscribeCall.invoke(this.callback, 3000);
		fault = this.callback.getMessageSync();
		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.HV_ERROR, " [IntValue must be set]", SCMPMsgType.CLN_SUBSCRIBE);

		// noDataInterval wrong
		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setMask("ACD");
		subscribeCall.setNoDataIntervalSeconds(0);
		subscribeCall.invoke(this.callback, 3000);
		fault = this.callback.getMessageSync();
		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.HV_ERROR, " [IntValue must be set]", SCMPMsgType.CLN_SUBSCRIBE);
	}

	private class SubscribeCallback extends SynchronousCallback {
	}
}
