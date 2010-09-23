package org.serviceconnector.test.system.publish;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.cln.IPublishService;
import org.serviceconnector.api.cln.ISCClient;
import org.serviceconnector.api.cln.IService;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.ctrl.util.TestConstants;

public class PublishClientTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PublishClientTest.class);

	private static Process scProcess;
	private static Process srvProcess;

	private ISCClient client;

	private Exception ex;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
			srvProcess = ctrl.startServer(TestConstants.publishSrv,
					TestConstants.log4jSrvProperties, 30000, TestConstants.PORT9000, 100,
					new String[] { TestConstants.serviceNamePublish });
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@Before
	public void setUp() throws Exception {
		client = new SCClient();
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
	}

	@After
	public void tearDown() throws Exception {
		client.detach();
		client = null;
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSC0Properties);
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		ctrl = null;
		scProcess = null;
		srvProcess = null;
	}

	@Test
	public void publish_waitForAMessageToBePublished_incomesAMessage() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		DemoPublishClientCallback callback = new DemoPublishClientCallback(service);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, TestConstants.pangram, callback);
		for (int i = 0; i < 30; i++) {
			if (callback.lastMessage == null) {
				Thread.sleep(100);
			} else {
				i = 30;
			}
		}

		service.unsubscribe();

		assertEquals(1, callback.messageCounter);
		assertEquals(true, callback.getLastMessage().getData().toString().startsWith(
				"publish message nr "));
		assertEquals(null, callback.getLastMessage().getMessageInfo());
		assertEquals(false, callback.getLastMessage().getSessionId() == null
				|| callback.getLastMessage().getSessionId().equals(""));
	}

	@Test
	public void publish_waitFor2MessagesToBePublished_bodyEndsWithConsequentNumbers()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		DemoPublishClientCallback callback = new DemoPublishClientCallback(service);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, TestConstants.pangram, callback);

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

		assertEquals(2, callback.messageCounter);
		assertEquals(true, firstMessage.getData().toString().startsWith("publish message nr "));
		assertEquals(true, callback.getLastMessage().getData().toString().startsWith(
				"publish message nr "));
		assertEquals(Integer.parseInt(firstMessage.getData().toString().split(" ")[3]) + 1, Integer
				.parseInt(callback.getLastMessage().getData().toString().split(" ")[3]));
		assertEquals(null, firstMessage.getMessageInfo());
		assertEquals(null, callback.getLastMessage().getMessageInfo());
		assertEquals(false, firstMessage.getSessionId() == null
				|| callback.getLastMessage().getSessionId().equals(""));
		assertEquals(false, firstMessage.getSessionId() == null
				|| callback.getLastMessage().getSessionId().equals(""));
	}
	
	@Test
	public void publish_waitFor20MessagesToBePublished_bodyEndsWithConsequentNumbers()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		DemoPublishClientCallback callback = new DemoPublishClientCallback(service);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, TestConstants.pangram, callback);

		SCMessage firstMessage = null;
	}

	private class DemoPublishClientCallback extends SCMessageCallback {

		private volatile int messageCounter = 0;
		private SCMessage lastMessage = null;

		/**
		 * @return the lastMessage
		 */
		public synchronized SCMessage getLastMessage() {
			return lastMessage;
		}

		/**
		 * @param lastMessage the lastMessage to set
		 */
		public synchronized void setLastMessage(SCMessage lastMessage) {
			this.lastMessage = lastMessage;
		}

		public DemoPublishClientCallback(IService service) {
			super(service);
		}

		@Override
		public void callback(SCMessage message) {
			messageCounter++;
			setLastMessage(message);
		}

		@Override
		public void callback(Exception e) {
		}
	}
}
