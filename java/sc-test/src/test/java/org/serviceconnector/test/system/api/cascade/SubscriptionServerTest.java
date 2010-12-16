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
package org.serviceconnector.test.system.api.cascade;

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
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;

public class SubscriptionServerTest {
	
//	/** The Constant logger. */
//	protected final static Logger logger = Logger.getLogger(SubscriptionServerTest.class);
//
//	private static ProcessesController ctrl;
//	private static ProcessCtx scCtx;
//	private static ProcessCtx scCasCtx; 	// Cascaded
//	private int threadCount = 0;
//
//	private SCServer server;
//	private SCPublishServer publishServer;
//	private SrvCallback srvCallback;
//
//	@BeforeClass
//	public static void beforeAllTests() throws Exception {
//		ctrl = new ProcessesController();
//		try {
//			scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
//			scCasCtx = ctrl.startSC(TestConstants.log4jSCcascadedProperties, TestConstants.SCcascadedProperties);
//		} catch (Exception e) {
//			logger.error("beforeAllTests", e);
//			throw e;
//		}
//	}
//
//	@AfterClass
//	public static void afterAllTests() throws Exception {
//		try {
//			ctrl.stopServer(scCtx);
//		} catch (Exception e) {}
//		try {
//			ctrl.stopSC(scCasCtx);
//		} catch (Exception e) {}
//		scCasCtx = null;
//		scCtx = null;
//		ctrl = null;
//	}
//
//	@Before
//	public void beforeOneTest() throws Exception {
//		threadCount = Thread.activeCount();
////		publishServer = new SCPublishServer();
////		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
////		srvCallback = new SrvCallback();
////		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.publishServiceNames, 10, 10,
////				srvCallback);
//		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER);
//		server.startListener();
//		
//		publishServer = server.newPublishServer(TestConstants.sesServerName1);
//		srvCallback = new SrvCallback(publishServer);
//		publishServer.register(10, 10, srvCallback);
//
//		
//	}
//
//	@After
//	public void afterOneTest() throws Exception {
//		server.stopListener();
//		publishServer.deregister();
//		publishServer.destroy();
//		srvCallback = null;
//		Assert.assertEquals("number of threads", threadCount, Thread.activeCount());
//	}
//
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
//		Assert.assertEquals("sessionInfo", srvCallback.subscribeMsg.getMessageInfo());
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
//		Assert.assertEquals("sessionInfo", srvCallback.subscribeMsg.getMessageInfo());
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
//		Assert.assertEquals("sessionInfo", srvCallback.subscribeMsg.getMessageInfo());
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
//	@Test
//	public void subscribeUnsubscribe_twice_4MessagesArrive() throws Exception {
//		TestPublishClient client = new TestPublishClient("subscribeUnsubscribe_twice_isSubscribedThenNot");
//		client.start();
//		client.join();
//
//		Assert.assertEquals(4, srvCallback.messagesExchanged);
//	}
//
//	@Test
//	public void changeSubscription_twice_4MessagesArrive() throws Exception {
//		TestPublishClient client = new TestPublishClient("changeSubscription_twice_passes");
//		client.start();
//		client.join();
//
//		Assert.assertEquals(4, srvCallback.messagesExchanged);
//	}
//
//	// TODO FJU if client thinks he is not subscribed(has session), delete does not go through to server. is that ok?
//	@Test
//	public void unsubscribe_serviceNameValid_0MesagesArrives() throws Exception {
//		TestPublishClient client = new TestPublishClient("unsubscribe_serviceNameValid_notSubscribedEmptySessionId");
//		client.start();
//		client.join();
//
//		Assert.assertEquals(0, srvCallback.messagesExchanged);
//	}
//
//	private class SrvCallback extends SCPublishServerCallback {
//
//		public SrvCallback(SCPublishServer scPublishServer) {
//			super(scPublishServer);
//			// TODO Auto-generated constructor stub
//		}
//
//		private int messagesExchanged = 0;
//		private SCMessage subscribeMsg = null;
//		private SCMessage changeSubMsg = null;
//		private SCMessage unsubscribeMsg = null;
//
//		@Override
//		public SCMessage changeSubscription(SCSubscribeMessage message, int operationTimeoutInMillis) {
//			messagesExchanged++;
//			changeSubMsg = message;
//			return message;
//		}
//
//		@Override
//		public SCMessage subscribe(SCSubscribeMessage message, int operationTimeoutInMillis) {
//			messagesExchanged++;
//			subscribeMsg = message;
//			return message;
//		}
//
//		@Override
//		public void unsubscribe(SCSubscribeMessage message, int operationTimeoutInMillis) {
//			messagesExchanged++;
//			unsubscribeMsg = message;
//		}
//	}
}
