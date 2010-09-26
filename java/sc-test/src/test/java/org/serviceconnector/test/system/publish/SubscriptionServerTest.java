package org.serviceconnector.test.system.publish;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.srv.ISCPublishServer;
import org.serviceconnector.api.srv.ISCPublishServerCallback;
import org.serviceconnector.api.srv.SCPublishServer;
import org.serviceconnector.cln.StartPublishClient;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.ctrl.util.TestConstants;


public class SubscriptionServerTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger
			.getLogger(SubscriptionServerTest.class);

	private int threadCount = 0;
	private SrvCallback srvCallback;
	private ISCPublishServer server;

	private static Process scProcess;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
			throw e;
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSC0Properties);
		ctrl = null;
		scProcess = null;
	}

	@Before
	public void setUp() throws Exception {
		threadCount = Thread.activeCount();
		server = new SCPublishServer();
		server.startListener(TestConstants.HOST, 9001, 0);
		srvCallback = new SrvCallback();
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP,
				TestConstants.serviceNamePublish, 10, 10, srvCallback);

	}

	@After
	public void tearDown() throws Exception {
		server.deregisterServer(TestConstants.serviceName);
		server.destroyServer();
		server = null;
		srvCallback = null;
		assertEquals("number of threads", threadCount, Thread.activeCount());
	}

	@Test
	public void subscribe_serviceNameValidMaskSameAsInServer_2MessagesArrive() throws Exception {
		StartPublishClient client = new StartPublishClient(
				"subscribe_serviceNameValidMaskSameAsInServer_isSubscribedSessionIdExists");
		client.start();
		client.join();

		assertEquals(2, srvCallback.messagesExchanged);
		assertEquals(true, srvCallback.subscribeMsg instanceof SCMessage);
		assertEquals(false, srvCallback.subscribeMsg.getSessionId() == null
				|| srvCallback.subscribeMsg.getSessionId().isEmpty());
		assertEquals(false, srvCallback.subscribeMsg.isFault());
		assertEquals(null, srvCallback.subscribeMsg.getData());
		assertEquals(true, srvCallback.subscribeMsg.isCompressed());
		assertEquals("sessionInfo", srvCallback.subscribeMsg.getMessageInfo());
		assertEquals("operation timeout", true, 0.8 * 60000 <= srvCallback.subscribeMsg.getOperationTimeout());

		assertEquals(true, srvCallback.unsubscribeMsg instanceof SCMessage);
		assertEquals(false, srvCallback.unsubscribeMsg.getSessionId() == null
				|| srvCallback.unsubscribeMsg.getSessionId().isEmpty());
		assertEquals(false, srvCallback.unsubscribeMsg.isFault());
		assertEquals(null, srvCallback.unsubscribeMsg.getData());
		assertEquals(true, srvCallback.unsubscribeMsg.isCompressed());
		assertEquals(null, srvCallback.unsubscribeMsg.getMessageInfo());
		assertEquals("operation timeout", true, 0.8 * 60000 <= srvCallback.unsubscribeMsg.getOperationTimeout());
	}
	
	@Test
	public void subscribe_withTimeOutSet_2MessagesArrive() throws Exception {
		StartPublishClient client = new StartPublishClient(
				"subscribe_timeoutMaxAllowed_isSubscribedSessionIdExists");
		client.start();
		client.join();

		assertEquals(2, srvCallback.messagesExchanged);
		
		assertEquals(true, srvCallback.subscribeMsg instanceof SCMessage);
		assertEquals(false, srvCallback.subscribeMsg.getSessionId() == null
				|| srvCallback.subscribeMsg.getSessionId().isEmpty());
		assertEquals(false, srvCallback.subscribeMsg.isFault());
		assertEquals(null, srvCallback.subscribeMsg.getData());
		assertEquals(true, srvCallback.subscribeMsg.isCompressed());
		assertEquals("sessionInfo", srvCallback.subscribeMsg.getMessageInfo());
		assertEquals("operation timeout", true, 0.8 * 3600000 <= srvCallback.subscribeMsg.getOperationTimeout());
		
		assertEquals(true, srvCallback.unsubscribeMsg instanceof SCMessage);
		assertEquals(false, srvCallback.unsubscribeMsg.getSessionId() == null
				|| srvCallback.unsubscribeMsg.getSessionId().isEmpty());
		assertEquals(false, srvCallback.unsubscribeMsg.isFault());
		assertEquals(null, srvCallback.unsubscribeMsg.getData());
		assertEquals(true, srvCallback.unsubscribeMsg.isCompressed());
		assertEquals(null, srvCallback.unsubscribeMsg.getMessageInfo());
		assertEquals("operation timeout", true, 0.8 * 60000 <= srvCallback.unsubscribeMsg.getOperationTimeout());
	}
	
	@Test
	public void changeSubscription_toMaskWhiteSpace_3MessagesArrive() throws Exception {
		StartPublishClient client = new StartPublishClient(
				"changeSubscription_toMaskWhiteSpace_passes");
		client.start();
		client.join();

		assertEquals(3, srvCallback.messagesExchanged);
		
		assertEquals(true, srvCallback.subscribeMsg instanceof SCMessage);
		assertEquals(false, srvCallback.subscribeMsg.getSessionId() == null
				|| srvCallback.subscribeMsg.getSessionId().isEmpty());
		assertEquals(false, srvCallback.subscribeMsg.isFault());
		assertEquals(null, srvCallback.subscribeMsg.getData());
		assertEquals(true, srvCallback.subscribeMsg.isCompressed());
		assertEquals("sessionInfo", srvCallback.subscribeMsg.getMessageInfo());
		assertEquals("operation timeout", true, 0.8 * 60000 <= srvCallback.subscribeMsg.getOperationTimeout());
		
		assertEquals(true, srvCallback.changeSubMsg instanceof SCMessage);
		assertEquals(false, srvCallback.changeSubMsg.getSessionId() == null
				|| srvCallback.changeSubMsg.getSessionId().isEmpty());
		assertEquals(false, srvCallback.changeSubMsg.isFault());
		assertEquals(null, srvCallback.changeSubMsg.getData());
		assertEquals(true, srvCallback.changeSubMsg.isCompressed());
		assertEquals(null, srvCallback.changeSubMsg.getMessageInfo());
		assertEquals("operation timeout", true, 0.8 * 60000 <= srvCallback.changeSubMsg.getOperationTimeout());
		
		
		assertEquals(true, srvCallback.unsubscribeMsg instanceof SCMessage);
		assertEquals(false, srvCallback.unsubscribeMsg.getSessionId() == null
				|| srvCallback.unsubscribeMsg.getSessionId().isEmpty());
		assertEquals(false, srvCallback.unsubscribeMsg.isFault());
		assertEquals(null, srvCallback.unsubscribeMsg.getData());
		assertEquals(true, srvCallback.unsubscribeMsg.isCompressed());
		assertEquals(null, srvCallback.unsubscribeMsg.getMessageInfo());
		assertEquals("operation timeout", true, 0.8 * 60000 <= srvCallback.unsubscribeMsg.getOperationTimeout());
	}
	
	@Test
	public void subscribeUnsubscribe_twice_4MessagesArrive() throws Exception {
		StartPublishClient client = new StartPublishClient(
				"subscribeUnsubscribe_twice_isSubscribedThenNot");
		client.start();
		client.join();

		assertEquals(4, srvCallback.messagesExchanged);
	}
	
	@Test
	public void changeSubscription_twice_4MessagesArrive() throws Exception {
		StartPublishClient client = new StartPublishClient(
				"changeSubscription_twice_passes");
		client.start();
		client.join();

		assertEquals(4, srvCallback.messagesExchanged);
	}
	
	//TODO FJU if client thinks he is not subscribed(has session), delete does not go through to server. is that ok?
	@Test
	public void unsubscribe_serviceNameValid_0MesagesArrives() throws Exception {
		StartPublishClient client = new StartPublishClient(
				"unsubscribe_serviceNameValid_notSubscribedEmptySessionId");
		client.start();
		client.join();

		assertEquals(0, srvCallback.messagesExchanged);
	}
	

	private class SrvCallback implements ISCPublishServerCallback {

		private int messagesExchanged = 0;
		private SCMessage subscribeMsg = null;
		private SCMessage changeSubMsg = null;
		private SCMessage unsubscribeMsg = null;

		public SrvCallback() {
		}

		@Override
		public SCMessage changeSubscription(SCMessage message) {
			messagesExchanged++;
			changeSubMsg = message;
			return message;
		}

		@Override
		public SCMessage subscribe(SCMessage message) {
			messagesExchanged++;
			subscribeMsg = message;
			return message;
		}

		@Override
		public void unsubscribe(SCMessage message) {
			messagesExchanged++;
			unsubscribeMsg = message;
		}
	}
}
