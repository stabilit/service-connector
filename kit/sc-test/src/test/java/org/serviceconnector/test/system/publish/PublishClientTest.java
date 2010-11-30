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

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
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
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;

public class PublishClientTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PublishClientTest.class);

	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	private static Process scProcess;
	private static Process srvProcess;

	private SCClient client;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
			srvProcess = ctrl.startServer(TestConstants.publishSrv, TestConstants.log4jSrvProperties,
					TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100,
					new String[] { TestConstants.publishServiceName });
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@Before
	public void setUp() throws Exception {
		client = new SCClient();
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
	}

	@After
	public void tearDown() throws Exception {
		client.detach();
		client = null;
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSCProperties);
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		ctrl = null;
		scProcess = null;
		srvProcess = null;
	}

	@Test
	public void publish_waitForAMessageToBePublished_incomesAMessage() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.publishServiceName);
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

		assertEquals(1, callback.getMessageCounter());
		assertEquals(true, callback.getLastMessage().getData().toString().startsWith("publish message nr "));
		assertEquals(null, callback.getLastMessage().getMessageInfo());
		assertEquals(false, callback.getLastMessage().getSessionId() == null
				|| callback.getLastMessage().getSessionId().equals(""));
	}

	@Test
	public void publish_waitFor2MessagesToBePublished_bodyEndsWithConsequentNumbers() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.publishServiceName);
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

		assertEquals(2, callback.getMessageCounter());
		assertEquals(true, firstMessage.getData().toString().startsWith("publish message nr "));
		assertEquals(true, callback.getLastMessage().getData().toString().startsWith("publish message nr "));
		assertEquals(Integer.parseInt(firstMessage.getData().toString().split(" ")[3]) + 1, Integer.parseInt(callback
				.getLastMessage().getData().toString().split(" ")[3]));
		assertEquals(null, firstMessage.getMessageInfo());
		assertEquals(null, callback.getLastMessage().getMessageInfo());
		assertEquals(false, firstMessage.getSessionId() == null || callback.getLastMessage().getSessionId().equals(""));
		assertEquals(false, firstMessage.getSessionId() == null || callback.getLastMessage().getSessionId().equals(""));
	}

	@Test
	public void publish_waitFor20MessagesToBePublished_bodysEndWithConsequentNumbers() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.publishServiceName);
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
					assertEquals(Integer.parseInt(previousMessage.getData().toString().split(" ")[3]) + 1, Integer
							.parseInt(newMessage.getData().toString().split(" ")[3]));
				}
			}
		}
		assertEquals("recieved messages", 20, counter);
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
