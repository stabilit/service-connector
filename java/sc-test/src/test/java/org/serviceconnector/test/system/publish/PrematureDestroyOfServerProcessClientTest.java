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
import org.serviceconnector.service.SCServiceException;

public class PrematureDestroyOfServerProcessClientTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger
			.getLogger(PrematureDestroyOfServerProcessClientTest.class);

	private Process scProcess;
	private Process srvProcess;

	private int threadCount = 0;
	private ISCClient client;

	private Exception ex;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
	}

	@Before
	public void setUp() throws Exception {
		threadCount = Thread.activeCount();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
			srvProcess = ctrl.startServer(TestConstants.publishSrv, TestConstants.log4jSrvProperties, TestConstants.PORT_LISTENER,
					TestConstants.PORT_TCP, 100, new String[] { TestConstants.serviceNamePublish });
		} catch (Exception e) {
			logger.error("setUp", e);
		}
		client = new SCClient();
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
	}

	@After
	public void tearDown() throws Exception {
		try {
			client.detach();
		} catch (Exception e) {
		} finally {
			ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
			ctrl.stopProcess(scProcess, TestConstants.log4jSC0Properties);
			client = null;
			srvProcess = null;
			scProcess = null;
		}
		assertEquals("number of threads", threadCount, Thread.activeCount());
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl = null;
	}
	
	@Test
	public void subscribe_withoutServer_throwsException() throws Exception {
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			service.subscribe(TestConstants.mask, "sessionInfo", 300,
					new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(null, service.getSessionId());
	}

	@Test
	public void unsubscribe_withoutServer_throwsException() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 300,
				new DemoPublishClientCallback(service));

		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);

		try {
			service.unsubscribe();
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void publish_withoutServer_noMessagesReceived() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		DemoPublishClientCallback callback = new DemoPublishClientCallback(service); 
		service.subscribe(TestConstants.mask, "sessionInfo", 300, callback);

		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);

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
		assertEquals(null, callback.lastMessage);
	}

	@Test
	public void publish_afterReceivingAMessageWaitFor5Seconds_noOtherMessagesReceived() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		DemoPublishClientCallback callback = new DemoPublishClientCallback(service); 
		service.subscribe(TestConstants.mask, "sessionInfo", 300, callback);


		try {
			for (int i = 0; i < 30; i++) {
				if (callback.lastMessage == null) {
					Thread.sleep(100);
				} else {
					ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
					i = 30;
				}
			}
			assertEquals(true, callback.lastMessage != null);
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
		assertEquals(null, callback.lastMessage);
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

		public DemoPublishClientCallback(IService service) {
			super(service);
		}

		@Override
		public void callback(SCMessage message) {
			setLastMessage(message);
		}

		@Override
		public void callback(Exception e) {
		}
	}
}