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
import org.serviceconnector.log.Loggers;
import org.serviceconnector.service.SCServiceException;

//TODO FJU missing method to get current subscription to verify correct changes

public class ChangeSubscriptionClientTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ChangeSubscriptionClientTest.class);

	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());
	
	private static Process scProcess;
	private static Process srvProcess;

	private SCClient client;

	private Exception ex;

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

	// TODO FJU in most of these tests is needed assertion of the changed mask
	@Test
	public void changeSubscription_serviceNameEmptyNotEstablishedPreviousSubscription_throwsSCException()
			throws Exception {
		SCPublishService service = client.newPublishService("");
		try {
			service.changeSubscription(TestConstants.mask);
		} catch (Exception e) {
			ex = e;
		} finally {
			assertEquals(SCServiceException.class, ex.getClass());
			assertEquals(null, service.getSessionId());
			assertEquals(false, service.isSubscribed());
		}
	}

	@Test
	public void changeSubscription_serviceNameValidNotEstablishedPreviousSubscription_throwsSCException()
			throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			service.changeSubscription(TestConstants.mask);
		} catch (Exception e) {
			ex = e;
		} finally {
			assertEquals(SCServiceException.class, ex.getClass());
			assertEquals(null, service.getSessionId());
			assertEquals(false, service.isSubscribed());
		}
	}

	@Test
	public void changeSubscription_afterUnsubscribed_throwsSCException() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(service));
		service.unsubscribe();
		try {
			service.changeSubscription(TestConstants.mask);
		} catch (Exception e) {
			ex = e;
		} finally {
			assertEquals(SCServiceException.class, ex.getClass());
			assertEquals(null, service.getSessionId());
			assertEquals(false, service.isSubscribed());
		}
	}

	@Test
	public void changeSubscription_toTheSameMask_passes() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(service));
		service.changeSubscription(TestConstants.mask);
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(true, service.isSubscribed());
		service.unsubscribe();
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void changeSubscription_toMaskNull_throwsValidatorException() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(service));
		try {
			service.changeSubscription(null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(SCMPValidatorException.class, ex.getClass());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(true, service.isSubscribed());
		service.unsubscribe();
	}

	@Test
	public void changeSubscription_toMaskEmpty_throwsValidatorException() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(service));
		try {
			service.changeSubscription("");
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(SCMPValidatorException.class, ex.getClass());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(true, service.isSubscribed());
		service.unsubscribe();
	}

	@Test
	public void changeSubscription_toMaskWhiteSpace_passes() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(service));
		service.changeSubscription(" ");
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(true, service.isSubscribed());
		service.unsubscribe();
	}

	@Test
	public void changeSubscription_toMaskOneChar_passes() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(service));
		service.changeSubscription("a");
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(true, service.isSubscribed());
		service.unsubscribe();
	}

	@Test
	public void changeSubscription_toMaskPangram_passes() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(service));
		service.changeSubscription(TestConstants.pangram);
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(true, service.isSubscribed());
		service.unsubscribe();
	}

	@Test
	public void changeSubscription_toMask256LongString_passes() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(service));
		service.changeSubscription(TestConstants.stringLength256);
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(true, service.isSubscribed());
		service.unsubscribe();
	}

	@Test
	public void changeSubscription_toMask257LongString_throwsValidatorException() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(service));
		try {
			service.changeSubscription(TestConstants.stringLength257);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(SCMPValidatorException.class, ex.getClass());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(true, service.isSubscribed());
		service.unsubscribe();
	}

	@Test
	public void changeSubscription_twice_passes() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(service));

		service.changeSubscription(TestConstants.mask);
		service.changeSubscription(TestConstants.mask);

		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(true, service.isSubscribed());
		service.unsubscribe();
	}

	@Test
	public void changeSubscription_10000Times_passes() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(service));
		int loop = 10000;
		for (int i = 0; i < loop; i++) {
			if ((i % 500) == 0)
				testLogger.info("changeSubscription_10000Times cycle:\t" + i + " ...");
			service.changeSubscription(TestConstants.mask);
			Thread.sleep(5); // TODO little sleep required in this Netty version. Known bug, will be fixed soon

		}
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(true, service.isSubscribed());
		service.unsubscribe();
	}

	@Test
	public void subscribeChangeSubscriptionUnsubscribe_10000Times_passes() throws Exception {
		int loop = 10000;
		for (int i = 0; i < loop; i++) {
			if ((i % 500) == 0)
				testLogger.info("ubscribeChangeSubscriptionUnsubscribe_10000Times cycle:\t" + i + " ...");
			SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
			service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(service));
			service.changeSubscription(TestConstants.mask);
			Thread.sleep(5); // TODO little sleep required in this Netty version. Known bug, will be fixed soon
			assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
			assertEquals(true, service.isSubscribed());
			service.unsubscribe();
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
			System.err.println(e);
		}
	}
}
