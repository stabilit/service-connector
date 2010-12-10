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
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;

public class PublishClientTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PublishClientTest.class);
	
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	private static ProcessCtx scCtx;
	private static ProcessCtx srvCtx;

	private SCClient client;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		srvCtx = ctrl.startServer(TestConstants.SERVER_TYPE_PUBLISH, TestConstants.log4jSrvProperties,
					TestConstants.sesServerName1, TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, 10,
					TestConstants.pubServiceName1 );
	}

	@Before
	public void beforeOneTest() throws Exception {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_TCP);
		client.attach(5);
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			client.detach();
		} catch (Exception e) { }
		client = null;
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		try {
			ctrl.stopServer(srvCtx);
		} catch (Exception e) {	}
		try {
			ctrl.stopSC(scCtx);
		} catch (Exception e) {	}
		srvCtx = null;
		scCtx = null;
		ctrl = null;
	}

	@Test
	public void publish_waitForAMessageToBePublished_incomesAMessage() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.pubServiceName1);
		DemoPublishClientCallback callback = new DemoPublishClientCallback(service);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, callback);
		for (int i = 0; i < 30; i++) {
			if (callback.lastMessage == null) {
				Thread.sleep(100);
			} else {
				i = 30;
			}
		}

		service.unsubscribe();

		Assert.assertEquals(1, callback.getMessageCounter());
		Assert.assertEquals(true, callback.getLastMessage().getData().toString().startsWith("publish message nr "));
		Assert.assertEquals(null, callback.getLastMessage().getMessageInfo());
		Assert.assertEquals(false, callback.getLastMessage().getSessionId() == null
				|| callback.getLastMessage().getSessionId().equals(""));
	}

	@Test
	public void publish_waitFor2MessagesToBePublished_bodyEndsWithConsequentNumbers() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.pubServiceName1);
		DemoPublishClientCallback callback = new DemoPublishClientCallback(service);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, callback);

		SCMessage firstMessage = null;

		for (int i = 0; i < 60; i++) {
			if (firstMessage == null && callback.getLastMessage() == null) {
				Thread.sleep(100);
			} else if (firstMessage == null) {
				firstMessage = callback.getLastMessage();
				callback.setLastMessage(null);
				Thread.sleep(100);
			} else if (callback.getLastMessage() == null) {
				Thread.sleep(100);
			} else {
				i = 60;
			}
		}

		service.unsubscribe();

		Assert.assertEquals(2, callback.getMessageCounter());
		Assert.assertEquals(true, firstMessage.getData().toString().startsWith("publish message nr "));
		Assert.assertEquals(true, callback.getLastMessage().getData().toString().startsWith("publish message nr "));
		Assert.assertEquals(Integer.parseInt(firstMessage.getData().toString().split(" ")[3]) + 1, Integer.parseInt(callback
				.getLastMessage().getData().toString().split(" ")[3]));
		Assert.assertEquals(null, firstMessage.getMessageInfo());
		Assert.assertEquals(null, callback.getLastMessage().getMessageInfo());
		Assert.assertEquals(false, firstMessage.getSessionId() == null || callback.getLastMessage().getSessionId().equals(""));
		Assert.assertEquals(false, firstMessage.getSessionId() == null || callback.getLastMessage().getSessionId().equals(""));
	}

	@Test
	public void publish_waitFor20MessagesToBePublished_bodysEndWithConsequentNumbers() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.pubServiceName1);
		DemoPublishClientCallback callback = new DemoPublishClientCallback(service);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, callback);

		SCMessage previousMessage = null;
		SCMessage newMessage = null;
		int counter = 0;

		for (int i = 0; i < 600 && counter < 20; i++) {
			if ((i % 10) == 0)
				testLogger.info("wait for message cycle:\t" + i + " ...");
			if (counter == callback.getMessageCounter()) {
				Thread.sleep(100);
			} else if (counter < callback.getMessageCounter()) {
				previousMessage = newMessage;
				newMessage = callback.getLastMessage();
				counter++;
				if (counter > 1) {
					Assert.assertEquals(Integer.parseInt(previousMessage.getData().toString().split(" ")[3]) + 1, Integer
							.parseInt(newMessage.getData().toString().split(" ")[3]));
				}
			}
		}
		Assert.assertEquals("recieved messages", 20, counter);
	}

	private class DemoPublishClientCallback extends SCMessageCallback {

		private int messageCounter = 0;

		/**
		 * @return the messageCounter
		 */
		public synchronized int getMessageCounter() {
			return messageCounter;
		}

		public synchronized void increment() {
			messageCounter++;
		}

		private SCMessage lastMessage = null;

		/**
		 * @return the lastMessage
		 */
		public synchronized SCMessage getLastMessage() {
			return lastMessage;
		}

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
			increment();
			setLastMessage(message);
		}

		@Override
		public void receive(Exception e) {
		}
	}
}
