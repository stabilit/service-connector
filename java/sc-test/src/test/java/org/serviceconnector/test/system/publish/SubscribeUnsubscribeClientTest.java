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
import org.serviceconnector.api.SCService;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.service.SCServiceException;

public class SubscribeUnsubscribeClientTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SubscribeUnsubscribeClientTest.class);

	private static Process scProcess;
	private static Process srvProcess;

	private SCClient client;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
			srvProcess = ctrl.startServer(TestConstants.publishSrv, TestConstants.log4jSrvProperties,
					TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100,
					new String[] { TestConstants.serviceNamePublish });
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
		ctrl.stopProcess(scProcess, TestConstants.log4jSC0Properties);
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		ctrl = null;
		scProcess = null;
		srvProcess = null;
	}

	@Test
	public void unsubscribe_serviceNameEmpty_notSubscribedEmptySessionId() throws Exception {
		SCPublishService service = client.newPublishService("");
		service.unsubscribe();
		assertEquals(null, service.getSessionId());
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void unsubscribe_serviceNameWhiteSpace_notSubscribedEmptySessionId() throws Exception {
		SCPublishService service = client.newPublishService(" ");
		service.unsubscribe();
		assertEquals(null, service.getSessionId());
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void unsubscribe_serviceNameOneChar_notSubscribedEmptySessionId() throws Exception {
		SCPublishService service = client.newPublishService("a");
		service.unsubscribe();
		assertEquals(null, service.getSessionId());
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void unsubscribe_serviceNameWhitePangram_notSubscribedEmptySessionId() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.pangram);
		service.unsubscribe();
		assertEquals(null, service.getSessionId());
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void unsubscribe_serviceNameDisabledService_notSubscribedEmptySessionId() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublishDisabled);
		service.unsubscribe();
		assertEquals(null, service.getSessionId());
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void unsubscribe_serviceNameValid_notSubscribedEmptySessionId() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.unsubscribe();
		assertEquals(null, service.getSessionId());
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void unsubscribeSubscribe_subscriptionValid_isSubscribedHasSessionId() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.unsubscribe();
		service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(service));
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(true, service.isSubscribed());
		service.unsubscribe();
	}

	@Test
	public void subscribeUnsubscribe_subscriptionValid_isSubscribedThenNot() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(service));
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(true, service.isSubscribed());
		service.unsubscribe();
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribeUnsubscribe_subscriptionThrowsValidatorException_unsubscribePasses() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
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
	public void subscribeUnsubscribe_subscriptionThrowsSCException_unsubscribePasses() throws Exception {
		SCPublishService service = client.newPublishService("");
		try {
			service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(service));
		} catch (SCServiceException e) {
		}
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
		service.unsubscribe();
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	// TODO FJU after subscrube -> unsubscribe -> subscribe, NullPointer is thrown
	@Test
	public void subscribeUnsubscribe_twice_isSubscribedThenNot() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);

		service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(service));
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(true, service.isSubscribed());
		service.unsubscribe();
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());

		service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(service));
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(true, service.isSubscribed());
		service.unsubscribe();
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribeUnsubscribe_10000Times_isSubscribedThenNot() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);

		int loop = 10000;
		for (int i = 0; i < loop / 10; i++) {
			System.out.println("subscribeUnsubscribe.loop " + i * 10);
			for (int j = 0; j < 10; j++) {
				service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(service));
				assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
				assertEquals(true, service.isSubscribed());
				service.unsubscribe();
				Thread.sleep(5); // little sleep, Netty has problems sending very fast will be done next version!
				assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
				assertEquals(false, service.isSubscribed());
			}
		}
	}

	private class DemoPublishClientCallback extends SCMessageCallback {

		public DemoPublishClientCallback(SCService service) {
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
