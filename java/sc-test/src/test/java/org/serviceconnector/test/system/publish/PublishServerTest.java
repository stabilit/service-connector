/*
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
 */
package org.serviceconnector.test.system.publish;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.srv.SCPublishServer;
import org.serviceconnector.api.srv.SCPublishServerCallback;
import org.serviceconnector.ctrl.util.ProcessesController;

public class PublishServerTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PublishServerTest.class);

	private SrvCallback srvCallback;
	private SCPublishServer server;

	private static Process scProcess;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		} catch (Exception e) {
			logger.error("beforeAllTests", e);
			throw e;
		}
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSCProperties);
		ctrl = null;
		scProcess = null;
	}

	@Before
	public void beforeOneTest() throws Exception {
		server = new SCPublishServer();
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		srvCallback = new SrvCallback();
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.pubServiceName1, 10, 10,
				srvCallback);

	}

	@After
	public void afterOneTest() throws Exception {
		server.deregister(TestConstants.pubServiceName1);
		server.destroy();
		server = null;
		srvCallback = null;
	}

	@Test
	public void subscribe_serviceNameValidMaskSameAsInServer_2MessagesArrive() throws Exception {
		TestPublishClient client = new TestPublishClient(
				"subscribe_serviceNameValidMaskSameAsInServer_isSubscribedSessionIdExists");
		client.start();
		client.join();

		Assert.assertEquals(2, srvCallback.messagesExchanged);
		Assert.assertEquals(true, srvCallback.subscribeMsg instanceof SCMessage);
		Assert.assertEquals(false, srvCallback.subscribeMsg.getSessionId() == null
				|| srvCallback.subscribeMsg.getSessionId().isEmpty());
		Assert.assertEquals(false, srvCallback.subscribeMsg.isFault());
		Assert.assertEquals(null, srvCallback.subscribeMsg.getData());
		Assert.assertEquals(true, srvCallback.subscribeMsg.isCompressed());
		Assert.assertEquals("sessionInfo", srvCallback.subscribeMsg.getSessionInfo());
		// TODO JOT
		// Assert.assertEquals("operation timeout", true, 0.8 * 60000 <= srvCallback.subscribeMsg.getOperationTimeout());

		Assert.assertEquals(true, srvCallback.unsubscribeMsg instanceof SCMessage);
		Assert.assertEquals(false, srvCallback.unsubscribeMsg.getSessionId() == null
				|| srvCallback.unsubscribeMsg.getSessionId().isEmpty());
		Assert.assertEquals(false, srvCallback.unsubscribeMsg.isFault());
		Assert.assertEquals(null, srvCallback.unsubscribeMsg.getData());
		Assert.assertEquals(true, srvCallback.unsubscribeMsg.isCompressed());
		Assert.assertEquals(null, srvCallback.unsubscribeMsg.getMessageInfo());
		// TODO JOT
		// Assert.assertEquals("operation timeout", true, 0.8 * 60000 <= srvCallback.unsubscribeMsg.getOperationTimeout());
	}

	@Test
	public void subscribe_withTimeOutSet_2MessagesArrive() throws Exception {
		TestPublishClient client = new TestPublishClient("subscribe_timeoutMaxAllowed_isSubscribedSessionIdExists");
		client.start();
		client.join();

		Assert.assertEquals(2, srvCallback.messagesExchanged);

		Assert.assertEquals(true, srvCallback.subscribeMsg instanceof SCMessage);
		Assert.assertEquals(false, srvCallback.subscribeMsg.getSessionId() == null
				|| srvCallback.subscribeMsg.getSessionId().isEmpty());
		Assert.assertEquals(false, srvCallback.subscribeMsg.isFault());
		Assert.assertEquals(null, srvCallback.subscribeMsg.getData());
		Assert.assertEquals(true, srvCallback.subscribeMsg.isCompressed());
		Assert.assertEquals("sessionInfo", srvCallback.subscribeMsg.getSessionInfo());
		// TODO JOT
		// Assert.assertEquals("operation timeout", true, 0.8 * 3600000 <= srvCallback.subscribeMsg.getOperationTimeout());

		Assert.assertEquals(true, srvCallback.unsubscribeMsg instanceof SCMessage);
		Assert.assertEquals(false, srvCallback.unsubscribeMsg.getSessionId() == null
				|| srvCallback.unsubscribeMsg.getSessionId().isEmpty());
		Assert.assertEquals(false, srvCallback.unsubscribeMsg.isFault());
		Assert.assertEquals(null, srvCallback.unsubscribeMsg.getData());
		Assert.assertEquals(true, srvCallback.unsubscribeMsg.isCompressed());
		Assert.assertEquals(null, srvCallback.unsubscribeMsg.getMessageInfo());
		// TODO JOT
		// Assert.assertEquals("operation timeout", true, 0.8 * 60000 <= srvCallback.unsubscribeMsg.getOperationTimeout());
	}

	@Test
	public void changeSubscription_toMaskWhiteSpace_3MessagesArrive() throws Exception {
		TestPublishClient client = new TestPublishClient("changeSubscription_toMaskWhiteSpace_passes");
		client.start();
		client.join();

		Assert.assertEquals(3, srvCallback.messagesExchanged);

		Assert.assertEquals(true, srvCallback.subscribeMsg instanceof SCMessage);
		Assert.assertEquals(false, srvCallback.subscribeMsg.getSessionId() == null
				|| srvCallback.subscribeMsg.getSessionId().isEmpty());
		Assert.assertEquals(false, srvCallback.subscribeMsg.isFault());
		Assert.assertEquals(null, srvCallback.subscribeMsg.getData());
		Assert.assertEquals(true, srvCallback.subscribeMsg.isCompressed());
		Assert.assertEquals("sessionInfo", srvCallback.subscribeMsg.getSessionInfo());
		// TODO JOT
		// Assert.assertEquals("operation timeout", true, 0.8 * 60000 <= srvCallback.subscribeMsg.getOperationTimeout());

		Assert.assertEquals(true, srvCallback.changeSubMsg instanceof SCMessage);
		Assert.assertEquals(false, srvCallback.changeSubMsg.getSessionId() == null
				|| srvCallback.changeSubMsg.getSessionId().isEmpty());
		Assert.assertEquals(false, srvCallback.changeSubMsg.isFault());
		Assert.assertEquals(null, srvCallback.changeSubMsg.getData());
		Assert.assertEquals(true, srvCallback.changeSubMsg.isCompressed());
		Assert.assertEquals(null, srvCallback.changeSubMsg.getMessageInfo());
		// TODO JOT
		// Assert.assertEquals("operation timeout", true, 0.8 * 60000 <= srvCallback.changeSubMsg.getOperationTimeout());

		Assert.assertEquals(true, srvCallback.unsubscribeMsg instanceof SCMessage);
		Assert.assertEquals(false, srvCallback.unsubscribeMsg.getSessionId() == null
				|| srvCallback.unsubscribeMsg.getSessionId().isEmpty());
		Assert.assertEquals(false, srvCallback.unsubscribeMsg.isFault());
		Assert.assertEquals(null, srvCallback.unsubscribeMsg.getData());
		Assert.assertEquals(true, srvCallback.unsubscribeMsg.isCompressed());
		Assert.assertEquals(null, srvCallback.unsubscribeMsg.getMessageInfo());
		// TODO JOT
		// Assert.assertEquals("operation timeout", true, 0.8 * 60000 <= srvCallback.unsubscribeMsg.getOperationTimeout());
	}

	// @Test
	// public void subscribeUnsubscribe_twice_4MessagesArrive() throws Exception {
	// StartPublishClient client = new StartPublishClient("subscribeUnsubscribe_twice_isSubscribedThenNot");
	// client.start();
	// client.join();
	// Assert.assertEquals(4, srvCallback.messagesExchanged);
	// }

	// @Test
	// public void changeSubscription_twice_4MessagesArrive() throws Exception {
	// StartPublishClient client = new StartPublishClient("changeSubscription_twice_passes");
	// client.start();
	// client.join();
	// Assert.assertEquals(4, srvCallback.messagesExchanged);
	// }

	@Test
	public void unsubscribe_serviceNameValid_0MesagesArrives() throws Exception {
		TestPublishClient client = new TestPublishClient("unsubscribe_serviceNameValid_notSubscribedEmptySessionId");
		client.start();
		client.join();
		Assert.assertEquals(0, srvCallback.messagesExchanged);
	}

	private class SrvCallback extends SCPublishServerCallback {

		private int messagesExchanged = 0;
		private SCMessage subscribeMsg = null;
		private SCMessage changeSubMsg = null;
		private SCMessage unsubscribeMsg = null;

		public SrvCallback() {
		}

		@Override
		public SCMessage changeSubscription(SCSubscribeMessage message, int operationTimeoutInMillis) {
			messagesExchanged++;
			changeSubMsg = message;
			return message;
		}

		@Override
		public SCMessage subscribe(SCSubscribeMessage message, int operationTimeoutInMillis) {
			messagesExchanged++;
			subscribeMsg = message;
			return message;
		}

		@Override
		public void unsubscribe(SCSubscribeMessage message, int operationTimeoutInMillis) {
			messagesExchanged++;
			unsubscribeMsg = message;
		}
	}
}
