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
import org.junit.Before;
import org.junit.BeforeClass;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.srv.SCPublishServer;
import org.serviceconnector.api.srv.SCPublishServerCallback;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;

public class PublishTest {
	
	/** The Constant testLogger. */
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PublishTest.class);

	private static ProcessesController ctrl;
	private static ProcessCtx scCtx;
	private SCServer server;
	private SCPublishServer publishServer;
	private int threadCount = 0;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
	}

	@Before
	public void beforeOneTest() throws Exception {
		threadCount = Thread.activeCount();
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			publishServer.deregister();
		} catch (Exception e) {}
		publishServer = null;
		try {
			server.stopListener();
		} catch (Exception e) {}
		try {
			server.destroy();
		} catch (Exception e) {}

		server = null;
//		Assert.assertEquals("number of threads", threadCount, Thread.activeCount());
		testLogger.info("Number of threads :" + Thread.activeCount() + " created :"+(Thread.activeCount() - threadCount));
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		try {
			ctrl.stopSC(scCtx);
			scCtx = null;
		} catch (Exception e) {}
		ctrl = null;
	}	

	
//	@Test
//	public void subscribe_serviceNameValidMaskSameAsInServer_2MessagesArrive() throws Exception {
//		TestPublishClient client = new TestPublishClient(
//				"subscribe_serviceNameValidMaskSameAsInServer_isSubscribedSessionIdExists");
//		client.start();
//		client.join();
//
//		Assert.assertEquals(2, srvCallback.messagesExchanged);
//		Assert.assertEquals(true, srvCallback.subscribeMsg instanceof SCMessage);
//		Assert.assertEquals(false, srvCallback.subscribeMsg.getSessionId() == null
//				|| srvCallback.subscribeMsg.getSessionId().isEmpty());
//		Assert.assertEquals(false, srvCallback.subscribeMsg.isFault());
//		Assert.assertEquals(null, srvCallback.subscribeMsg.getData());
//		Assert.assertEquals(true, srvCallback.subscribeMsg.isCompressed());
//		Assert.assertEquals("sessionInfo", srvCallback.subscribeMsg.getSessionInfo());
//		// TODO JOT
//		// Assert.assertEquals("operation timeout", true, 0.8 * 60000 <= srvCallback.subscribeMsg.getOperationTimeout());
//
//		Assert.assertEquals(true, srvCallback.unsubscribeMsg instanceof SCMessage);
//		Assert.assertEquals(false, srvCallback.unsubscribeMsg.getSessionId() == null
//				|| srvCallback.unsubscribeMsg.getSessionId().isEmpty());
//		Assert.assertEquals(false, srvCallback.unsubscribeMsg.isFault());
//		Assert.assertEquals(null, srvCallback.unsubscribeMsg.getData());
//		Assert.assertEquals(true, srvCallback.unsubscribeMsg.isCompressed());
//		Assert.assertEquals(null, srvCallback.unsubscribeMsg.getMessageInfo());
//		// TODO JOT
//		// Assert.assertEquals("operation timeout", true, 0.8 * 60000 <= srvCallback.unsubscribeMsg.getOperationTimeout());
//	}
//
//	@Test
//	public void subscribe_withTimeOutSet_2MessagesArrive() throws Exception {
//		TestPublishClient client = new TestPublishClient("subscribe_timeoutMaxAllowed_isSubscribedSessionIdExists");
//		client.start();
//		client.join();
//
//		Assert.assertEquals(2, srvCallback.messagesExchanged);
//
//		Assert.assertEquals(true, srvCallback.subscribeMsg instanceof SCMessage);
//		Assert.assertEquals(false, srvCallback.subscribeMsg.getSessionId() == null
//				|| srvCallback.subscribeMsg.getSessionId().isEmpty());
//		Assert.assertEquals(false, srvCallback.subscribeMsg.isFault());
//		Assert.assertEquals(null, srvCallback.subscribeMsg.getData());
//		Assert.assertEquals(true, srvCallback.subscribeMsg.isCompressed());
//		Assert.assertEquals("sessionInfo", srvCallback.subscribeMsg.getSessionInfo());
//		// TODO JOT
//		// Assert.assertEquals("operation timeout", true, 0.8 * 3600000 <= srvCallback.subscribeMsg.getOperationTimeout());
//
//		Assert.assertEquals(true, srvCallback.unsubscribeMsg instanceof SCMessage);
//		Assert.assertEquals(false, srvCallback.unsubscribeMsg.getSessionId() == null
//				|| srvCallback.unsubscribeMsg.getSessionId().isEmpty());
//		Assert.assertEquals(false, srvCallback.unsubscribeMsg.isFault());
//		Assert.assertEquals(null, srvCallback.unsubscribeMsg.getData());
//		Assert.assertEquals(true, srvCallback.unsubscribeMsg.isCompressed());
//		Assert.assertEquals(null, srvCallback.unsubscribeMsg.getMessageInfo());
//		// TODO JOT
//		// Assert.assertEquals("operation timeout", true, 0.8 * 60000 <= srvCallback.unsubscribeMsg.getOperationTimeout());
//	}
//
//	@Test
//	public void changeSubscription_toMaskWhiteSpace_3MessagesArrive() throws Exception {
//		TestPublishClient client = new TestPublishClient("changeSubscription_toMaskWhiteSpace_passes");
//		client.start();
//		client.join();
//
//		Assert.assertEquals(3, srvCallback.messagesExchanged);
//
//		Assert.assertEquals(true, srvCallback.subscribeMsg instanceof SCMessage);
//		Assert.assertEquals(false, srvCallback.subscribeMsg.getSessionId() == null
//				|| srvCallback.subscribeMsg.getSessionId().isEmpty());
//		Assert.assertEquals(false, srvCallback.subscribeMsg.isFault());
//		Assert.assertEquals(null, srvCallback.subscribeMsg.getData());
//		Assert.assertEquals(true, srvCallback.subscribeMsg.isCompressed());
//		Assert.assertEquals("sessionInfo", srvCallback.subscribeMsg.getSessionInfo());
//		// TODO JOT
//		// Assert.assertEquals("operation timeout", true, 0.8 * 60000 <= srvCallback.subscribeMsg.getOperationTimeout());
//
//		Assert.assertEquals(true, srvCallback.changeSubMsg instanceof SCMessage);
//		Assert.assertEquals(false, srvCallback.changeSubMsg.getSessionId() == null
//				|| srvCallback.changeSubMsg.getSessionId().isEmpty());
//		Assert.assertEquals(false, srvCallback.changeSubMsg.isFault());
//		Assert.assertEquals(null, srvCallback.changeSubMsg.getData());
//		Assert.assertEquals(true, srvCallback.changeSubMsg.isCompressed());
//		Assert.assertEquals(null, srvCallback.changeSubMsg.getMessageInfo());
//		// TODO JOT
//		// Assert.assertEquals("operation timeout", true, 0.8 * 60000 <= srvCallback.changeSubMsg.getOperationTimeout());
//
//		Assert.assertEquals(true, srvCallback.unsubscribeMsg instanceof SCMessage);
//		Assert.assertEquals(false, srvCallback.unsubscribeMsg.getSessionId() == null
//				|| srvCallback.unsubscribeMsg.getSessionId().isEmpty());
//		Assert.assertEquals(false, srvCallback.unsubscribeMsg.isFault());
//		Assert.assertEquals(null, srvCallback.unsubscribeMsg.getData());
//		Assert.assertEquals(true, srvCallback.unsubscribeMsg.isCompressed());
//		Assert.assertEquals(null, srvCallback.unsubscribeMsg.getMessageInfo());
//		// TODO JOT
//		// Assert.assertEquals("operation timeout", true, 0.8 * 60000 <= srvCallback.unsubscribeMsg.getOperationTimeout());
//	}
//
//	// @Test
//	// public void subscribeUnsubscribe_twice_4MessagesArrive() throws Exception {
//	// StartPublishClient client = new StartPublishClient("subscribeUnsubscribe_twice_isSubscribedThenNot");
//	// client.start();
//	// client.join();
//	// Assert.assertEquals(4, srvCallback.messagesExchanged);
//	// }
//
//	// @Test
//	// public void changeSubscription_twice_4MessagesArrive() throws Exception {
//	// StartPublishClient client = new StartPublishClient("changeSubscription_twice_passes");
//	// client.start();
//	// client.join();
//	// Assert.assertEquals(4, srvCallback.messagesExchanged);
//	// }
//
//	@Test
//	public void unsubscribe_serviceNameValid_0MesagesArrives() throws Exception {
//		TestPublishClient client = new TestPublishClient("unsubscribe_serviceNameValid_notSubscribedEmptySessionId");
//		client.start();
//		client.join();
//		Assert.assertEquals(0, srvCallback.messagesExchanged);
//	}

	
	private class CallBack extends SCPublishServerCallback {

		public CallBack(SCPublishServer server) {
			super(server);
		}
		@Override
		public SCMessage changeSubscription(SCSubscribeMessage message, int operationTimeoutInMillis) {
			return message;
		}

		@Override
		public SCMessage subscribe(SCSubscribeMessage message, int operationTimeoutInMillis) {
			return message;
		}

		@Override
		public void unsubscribe(SCSubscribeMessage message, int operationTimeoutInMillis) {
		}
	}
}
