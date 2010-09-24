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
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.sc.service.SCServiceException;

public class SubscribeUnsubscribeTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SubscribeUnsubscribeTest.class);

	private static Process scProcess;
	private static Process srvProcess;

	private ISCClient client;

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
	public void unsubscribe_serviceNameEmpty_notSubscribedEmptySessionId() throws Exception {
		IPublishService service = client.newPublishService("");
		service.unsubscribe();
		assertEquals(null, service.getSessionId());
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void unsubscribe_serviceNameWhiteSpace_notSubscribedEmptySessionId() throws Exception {
		IPublishService service = client.newPublishService(" ");
		service.unsubscribe();
		assertEquals(null, service.getSessionId());
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void unsubscribe_serviceNameOneChar_notSubscribedEmptySessionId() throws Exception {
		IPublishService service = client.newPublishService("a");
		service.unsubscribe();
		assertEquals(null, service.getSessionId());
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void unsubscribe_serviceNameWhitePangram_notSubscribedEmptySessionId() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.pangram);
		service.unsubscribe();
		assertEquals(null, service.getSessionId());
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void unsubscribe_serviceNameDisabledService_notSubscribedEmptySessionId()
			throws Exception {
		IPublishService service = client
				.newPublishService(TestConstants.serviceNamePublishDisabled);
		service.unsubscribe();
		assertEquals(null, service.getSessionId());
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void unsubscribe_serviceNameValid_notSubscribedEmptySessionId() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.unsubscribe();
		assertEquals(null, service.getSessionId());
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void unsubscribeSubscribe_subscriptionValid_isSubscribedHasSessionId() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.unsubscribe();
		service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(
				service));
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(true, service.isSubscribed());
		service.unsubscribe();
	}

	@Test
	public void subscribeUnsubscribe_subscriptionValid_isSubscribedThenNot() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(
				service));
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(true, service.isSubscribed());
		service.unsubscribe();
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribeUnsubscribe_subscriptionThrowsValidatorException_unsubscribePasses()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			service.subscribe(null, "sessionInfo", 300, new DemoPublishClientCallback(service));
		} catch (SCMPValidatorException e) {
		}
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
		service.unsubscribe();
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribeUnsubscribe_subscriptionThrowsSCException_unsubscribePasses()
			throws Exception {
		IPublishService service = client.newPublishService("");
		try {
			service.subscribe(TestConstants.mask, "sessionInfo", 300,
					new DemoPublishClientCallback(service));
		} catch (SCServiceException e) {
		}
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
		service.unsubscribe();
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}
	
	//TODO FJU after subscrube -> unsubscribe -> subscribe, NullPointer is thrown
	@Test
	public void subscribeUnsubscribe_twice_isSubscribedThenNot() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);

			service.subscribe(TestConstants.mask, "sessionInfo", 300,
					new DemoPublishClientCallback(service));
			assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
			assertEquals(true, service.isSubscribed());
			service.unsubscribe();
			assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
			assertEquals(false, service.isSubscribed());
			
			service.subscribe(TestConstants.mask, "sessionInfo", 300,
					new DemoPublishClientCallback(service));
			assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
			assertEquals(true, service.isSubscribed());
			service.unsubscribe();
			assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
			assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribeUnsubscribe_10000Times_isSubscribedThenNot() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);

		for (int i = 0; i < 10000; i++) {
			service.subscribe(TestConstants.mask, "sessionInfo", 300,
					new DemoPublishClientCallback(service));
			assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
			assertEquals(true, service.isSubscribed());
			service.unsubscribe();
			assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
			assertEquals(false, service.isSubscribed());
		}
	}

	private class DemoPublishClientCallback extends SCMessageCallback {

		public DemoPublishClientCallback(IService service) {
			super(service);
		}

		@Override
		public void callback(SCMessage reply) {
			logger.info("Publish client received: " + reply.getData());
		}

		@Override
		public void callback(Exception e) {
		}
	}
}
