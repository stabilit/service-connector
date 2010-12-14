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
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.SCService;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;

public class PrematureDestroyOfServerProcessClientTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PrematureDestroyOfServerProcessClientTest.class);

	private ProcessCtx scCtx;
	private ProcessCtx srvCtx;

	private int threadCount = 0;
	private SCClient client;

	private Exception ex;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
	}

	@Before
	public void beforeOneTest() throws Exception {
		threadCount = Thread.activeCount();
		try {
			scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
			srvCtx = ctrl.startServer(TestConstants.SERVER_TYPE_PUBLISH, TestConstants.log4jSrvProperties,
					TestConstants.sesServerName1, TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, 10,
					TestConstants.pubServiceName1 );
			
		} catch (Exception e) {
			logger.error("setUp", e);
		}
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_TCP);
		client.attach(5);
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			client.detach();
		} catch (Exception e) { }
		try {
			ctrl.stopServer(srvCtx);
		} catch (Exception e) {	}
		try {
			ctrl.stopSC(scCtx);
		} catch (Exception e) {	}
		srvCtx = null;
		scCtx = null;
		client = null;

		Assert.assertEquals("number of threads", threadCount, Thread.activeCount());
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		ctrl = null;
	}

	@Test
	public void subscribe_withoutServer_throwsException() throws Exception {
		ctrl.stopServer(srvCtx);
		SCPublishService service = client.newPublishService(TestConstants.pubServiceName1);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.mask);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(null, service.getSessionId());
	}

	@Test
	public void unsubscribe_withoutServer_throwsException() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));

		ctrl.stopServer(srvCtx);

		try {
			service.unsubscribe();
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void publish_withoutServer_noMessagesReceived() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.pubServiceName1);
		DemoPublishClientCallback callback = new DemoPublishClientCallback(service);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));

		ctrl.stopServer(srvCtx);

		try {
			for (int i = 0; i < 30; i++) {
				if (callback.lastMessage == null) {
					Thread.sleep(100);
				} else {
					i = 30;
				}
			}
		} catch (Exception e) {
		}
		Assert.assertEquals(null, callback.lastMessage);
	}

	@Test
	public void publish_afterReceivingAMessageWaitFor5Seconds_noOtherMessagesReceived() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.pubServiceName1);
		DemoPublishClientCallback callback = new DemoPublishClientCallback(service);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));

		try {
			for (int i = 0; i < 30; i++) {
				if (callback.lastMessage == null) {
					Thread.sleep(100);
				} else {
					ctrl.stopServer(srvCtx);
					i = 30;
				}
			}
			Assert.assertEquals(true, callback.lastMessage != null);
			callback.lastMessage = null;
			for (int i = 0; i < 30; i++) {
				if (callback.lastMessage == null) {
					Thread.sleep(100);
				} else {
					i = 30;
				}
			}
		} catch (Exception e) {
		}
		Assert.assertEquals(null, callback.lastMessage);
	}

	private class DemoPublishClientCallback extends SCMessageCallback {

		private volatile SCMessage lastMessage = null;

		/**
		 * @param lastMessage
		 *            the lastMessage to set
		 */
		public synchronized void setLastMessage(SCMessage lastMessage) {
			this.lastMessage = lastMessage;
		}

		public DemoPublishClientCallback(SCService service) {
			super(service);
		}

		@Override
		public void receive(SCMessage message) {
			setLastMessage(message);
		}

		@Override
		public void receive(Exception e) {
		}
	}
}
