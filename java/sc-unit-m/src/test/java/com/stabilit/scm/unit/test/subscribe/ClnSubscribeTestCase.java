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
package com.stabilit.scm.unit.test.subscribe;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPClnSubscribeCall;
import com.stabilit.scm.common.call.SCMPReceivePublicationCall;
import com.stabilit.scm.common.conf.RequesterConfigPool;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.util.SynchronousCallback;
import com.stabilit.scm.unit.TestContext;
import com.stabilit.scm.unit.test.SCTest;
import com.stabilit.scm.unit.test.SetupTestCases;
import com.stabilit.scm.unit.test.SuperTestCase;

public class ClnSubscribeTestCase extends SuperTestCase {

	private SubscribeCallback callback = new SubscribeCallback();

	/**
	 * @param fileName
	 */
	public ClnSubscribeTestCase(String fileName) {
		super(fileName);
	}

	@Before
	public void setup() throws Throwable {
		SetupTestCases.setupAll();
		try {
			this.config = new RequesterConfigPool();
			this.config.load(fileName);
			this.testContext = new TestContext(this.config.getRequesterConfig(), this.msgId);
			req = new Requester(this.testContext);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void failSubscribeWrongHeader() throws Exception {
		SCMPClnSubscribeCall subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL.newInstance(req,
				"publish-simulation");

		// mask not set
		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setNoDataIntervalSeconds(50);
		subscribeCall.invoke(this.callback);
		SCMPMessage fault = this.callback.getMessageSync();
		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.VALIDATION_ERROR, SCMPMsgType.CLN_SUBSCRIBE);

		// mask not valid
		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setNoDataIntervalSeconds(50);
		subscribeCall.setMask("%%%ACSD");
		subscribeCall.invoke(this.callback);
		fault = this.callback.getMessageSync();
		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.VALIDATION_ERROR, SCMPMsgType.CLN_SUBSCRIBE);

		// noDataInterval wrong
		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setMask("ACD");
		subscribeCall.setNoDataIntervalSeconds(0);
		subscribeCall.invoke(this.callback);
		fault = this.callback.getMessageSync();
		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.VALIDATION_ERROR, SCMPMsgType.CLN_SUBSCRIBE);
	}

	@Test
	public void subscribe() throws Throwable {
		SCMPClnSubscribeCall subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL.newInstance(req,
				"publish-simulation");

		subscribeCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		subscribeCall.setNoDataIntervalSeconds(2);
		subscribeCall.setMask("000012100012832102FADF");
		subscribeCall.invoke(this.callback);
		SCMPMessage reply = this.callback.getMessageSync();
		SCTest.checkReply(reply);
		String sessionId = reply.getSessionId();

		// receive publication - no data
		SCMPReceivePublicationCall receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION
				.newInstance(this.req, "publish-simulation", sessionId);
		receivePublicationCall.invoke(this.callback);
		reply = this.callback.getMessageSync();
		Assert.assertTrue(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));

		// receive publication first message
		receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION.newInstance(this.req,
				"publish-simulation", sessionId);
		receivePublicationCall.invoke(this.callback);
		reply = this.callback.getMessageSync();
		Assert.assertFalse(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));
		Assert.assertEquals("publish message nr 1", reply.getBody());
		Thread.sleep(3000);
		for (int i = 0; i < 2; i++) {
			// receive publication first message
			receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION.newInstance(
					this.req, "publish-simulation", sessionId);
			receivePublicationCall.invoke(this.callback);
			reply = this.callback.getMessageSync();
			Assert.assertFalse(reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA));
			Assert.assertEquals("publish message nr " + (i + 2), reply.getBody());
		}
	}

	@After
	public void tearDown() throws Exception {
		SetupTestCases.killPublishServer();
		SetupTestCases.startPublishServer();
	}

	private class SubscribeCallback extends SynchronousCallback {
	}
}
