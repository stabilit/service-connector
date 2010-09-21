package org.serviceconnector.test.system.publish;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.cln.IPublishService;
import org.serviceconnector.cln.ISCClient;
import org.serviceconnector.cln.IService;
import org.serviceconnector.cln.SCClient;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.ctrl.util.TestEnvironmentController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.sc.service.SCServiceException;
import org.serviceconnector.service.ISCMessage;
import org.serviceconnector.service.SCMessageCallback;

public class SubscribeClientToSCTest {
	
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());
	
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SubscribeClientToSCTest.class);

	private static Process scProcess;
	private static Process srvProcess;

	private ISCClient client;

	private Exception ex;

	private static TestEnvironmentController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
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
	public void getSessionId_fromNewlyCreatedPublishService_emptySessionId() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		assertEquals(null, service.getSessionId());
	}

	@Test
	public void isSubscribed_serviceNameEmpty_false() throws Exception {
		IPublishService service = client.newPublishService("");
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void isSubscribed_serviceNameWhiteSpace_false() throws Exception {
		IPublishService service = client.newPublishService(" ");
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void isSubscribed_serviceNameSingleChar_false() throws Exception {
		IPublishService service = client.newPublishService("a");
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void isSubscribed_serviceNameNotExisting_false() throws Exception {
		IPublishService service = client.newPublishService("notExistingService");
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void isSubscribed_serviceNameSessionService_false() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceName);
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void isSubscribed_serviceNameNotEnabled_false() throws Exception {
		IPublishService service = client
				.newPublishService(TestConstants.serviceNameSessionNotEnabled);
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void isSubscribed_fromNewlyCreatedPublishService_false() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameEmptyMaskNull_throwsValidatorException() throws Exception {
		IPublishService service = client.newPublishService("");
		try {
			service.subscribe(null, "sessionInfo", 300, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameEmptyMaskEmpty_throwsValidatorException() throws Exception {
		IPublishService service = client.newPublishService("");
		try {
			service.subscribe("", "sessionInfo", 300, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameEmptyMaskOneChar_throwsSCExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService("");
		try {
			service.subscribe("a", "sessionInfo", 300, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameEmptyMaskWhiteSpace_throwsSCExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService("");
		try {
			service.subscribe(" ", "sessionInfo", 300, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameEmptyMask256LongString_throwsSCExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService("");
		try {
			service.subscribe(TestConstants.stringLength256, "sessionInfo", 300,
					new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameEmptyMask257LongString_throwsValidatorExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService("");
		try {
			service.subscribe(TestConstants.stringLength257, "sessionInfo", 300,
					new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, service.isSubscribed());
	}

	// TODO FJU why returns % sign in mask InvalidParameterException instead of
	// SCMPValidator as everything other does?
	@Test
	public void subscribe_serviceNameEmptyMaskContainingPercentSign_throwsValidatorExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService("");
		try {
			service.subscribe("0000121ABCDEFGHIJKLMNO%----------X-----------", "sessionInfo", 300,
					new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameEmptyMaskSameAsInServer_throwsSCExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService("");
		try {
			service.subscribe(TestConstants.mask, "sessionInfo", 300,
					new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameNotExistingServiceMaskNull_throwsValidatorException()
			throws Exception {
		IPublishService service = client.newPublishService("notExistingService");
		try {
			service.subscribe(null, "sessionInfo", 300, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameNotExistingServiceMaskEmpty_throwsValidatorException()
			throws Exception {
		IPublishService service = client.newPublishService("notExistingService");
		try {
			service.subscribe("", "sessionInfo", 300, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameNotExistingServiceMaskOneChar_throwsSCExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService("notExistingService");
		try {
			service.subscribe("a", "sessionInfo", 300, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameNotExistingServiceMaskWhiteSpace_throwsSCExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService("notExistingService");
		try {
			service.subscribe(" ", "sessionInfo", 300, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameNotExistingServiceMask256LongString_throwsSCExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService("notExistingService");
		try {
			service.subscribe(TestConstants.stringLength256, "sessionInfo", 300,
					new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameNotExistingServiceMask257LongString_throwsValidatorExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService("notExistingService");
		try {
			service.subscribe(TestConstants.stringLength257, "sessionInfo", 300,
					new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameNotExistingServiceMaskContainingPercentSign_throwsValidatorExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService("notExistingService");
		try {
			service.subscribe("0000121ABCDEFGHIJKLMNO%----------X-----------", "sessionInfo", 300,
					new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameNotExistingServiceMaskSameAsInServer_throwsSCExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService("notExistingService");
		try {
			service.subscribe(TestConstants.mask, "sessionInfo", 300,
					new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameSessionServiceMaskNull_throwsValidatorException()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceName);
		try {
			service.subscribe(null, "sessionInfo", 300, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameSessionServiceMaskEmpty_throwsValidatorException()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceName);
		try {
			service.subscribe("", "sessionInfo", 300, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameSessionServiceMaskOneChar_throwsSCExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceName);
		try {
			service.subscribe("a", "sessionInfo", 300, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameSessionServiceMaskWhiteSpace_throwsSCExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceName);
		try {
			service.subscribe(" ", "sessionInfo", 300, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameSessionServiceMask256LongString_throwsSCExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceName);
		try {
			service.subscribe(TestConstants.stringLength256, "sessionInfo", 300,
					new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameSessionServiceMask257LongString_throwsValidatorExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceName);
		try {
			service.subscribe(TestConstants.stringLength257, "sessionInfo", 300,
					new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameSessionServiceMaskContainingPercentSign_throwsValidatorExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceName);
		try {
			service.subscribe("0000121ABCDEFGHIJKLMNO%-----------X-----------", "sessionInfo", 300,
					new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameSessionServiceMaskSameAsInServer_throwsSCExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceName);
		try {
			service.subscribe(TestConstants.mask, "sessionInfo", 300,
					new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameDisabledMaskNull_throwsValidatorException() throws Exception {
		IPublishService service = client
				.newPublishService(TestConstants.serviceNameSessionNotEnabled);
		try {
			service.subscribe(null, "sessionInfo", 300, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameDisabledMaskEmpty_throwsValidatorException() throws Exception {
		IPublishService service = client
				.newPublishService(TestConstants.serviceNameSessionNotEnabled);
		try {
			service.subscribe("", "sessionInfo", 300, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameDisabledMaskOneChar_throwsSCExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client
				.newPublishService(TestConstants.serviceNameSessionNotEnabled);
		try {
			service.subscribe("a", "sessionInfo", 300, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameDisabledMaskWhiteSpace_throwsSCExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client
				.newPublishService(TestConstants.serviceNameSessionNotEnabled);
		try {
			service.subscribe(" ", "sessionInfo", 300, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameDisabledMask256LongString_throwsSCExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client
				.newPublishService(TestConstants.serviceNameSessionNotEnabled);
		try {
			service.subscribe(TestConstants.stringLength256, "sessionInfo", 300,
					new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameDisabledMask257LongString_throwsValidatorExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client
				.newPublishService(TestConstants.serviceNameSessionNotEnabled);
		try {
			service.subscribe(TestConstants.stringLength257, "sessionInfo", 300,
					new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameDisabledMaskContainingPercentSign_throwsValidatorExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client
				.newPublishService(TestConstants.serviceNameSessionNotEnabled);
		try {
			service.subscribe("0000121ABCDEFGHIJKLMNO%-----------X-----------", "sessionInfo", 300,
					new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameDisabledMaskSameAsInServer_throwsSCExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client
				.newPublishService(TestConstants.serviceNamePublishNotEnabled);
		try {
			service.subscribe(TestConstants.mask, "sessionInfo", 300,
					new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameValidMaskNull_throwsValidatorException() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			service.subscribe(null, "sessionInfo", 300, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameValidMaskEmpty_throwsValidatorException() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			service.subscribe("", "sessionInfo", 300, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameValidMaskOneChar_isSubscribedSessionIdExists()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe("a", "sessionInfo", 300, new DemoPublishClientCallback(service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_serviceNameValidMaskWhiteSpace_isSubscribedSessionIdExists()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(" ", "sessionInfo", 300, new DemoPublishClientCallback(service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_serviceNameValidMask256LongString_isSubscribedSessionIdExists()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.stringLength256, "sessionInfo", 300,
				new DemoPublishClientCallback(service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_serviceNameValidMask257LongString_throwsValidatorExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			service.subscribe(TestConstants.stringLength257, "sessionInfo", 300,
					new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameValidMaskContainingPercentSign_throwsValidatorExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			service.subscribe("0000121ABCDEFGHIJKLMNO%-----------X-----------", "sessionInfo", 300,
					new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameValidMaskSameAsInServer_isSubscribedSessionIdExists()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(
				service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	// TODO FJU sessionInfo is supposed to be optional
	@Test
	public void subscribe_sessionInfoNull_isSubscribedSessionIdExists() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, null, 300, new DemoPublishClientCallback(service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_sessionInfoEmpty_isSubscribedSessionIdExists() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "", 300, new DemoPublishClientCallback(service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_sessionInfoOneChar_isSubscribedSessionIdExists() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "a", 300, new DemoPublishClientCallback(service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_sessionInfoPangram_isSubscribedSessionIdExists() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, TestConstants.pangram, 300,
				new DemoPublishClientCallback(service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_sessionInfo256LongString_isSubscribedSessionIdExists() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, TestConstants.stringLength256, 300,
				new DemoPublishClientCallback(service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_sessionInfo257LongString_throwsValidatorExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			service.subscribe(TestConstants.mask, TestConstants.stringLength257, 300,
					new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_noDataInterval0_throwsValidatorExceptionNotSubscribed() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			service.subscribe(TestConstants.mask, "sessionInfo", 0, new DemoPublishClientCallback(
					service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_noDataIntervalMinus1_throwsValidatorExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			service.subscribe(TestConstants.mask, "sessionInfo", -1, new DemoPublishClientCallback(
					service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_noDataInterval1_isSubscribedSessionIdExists() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 1, new DemoPublishClientCallback(
				service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_noDataIntervalMaxAllowed_isSubscribedSessionIdExists() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 3600, new DemoPublishClientCallback(
				service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_noDataIntervalMaxAllowedPlusOne_throwsValidatorExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			service.subscribe(TestConstants.mask, "sessionInfo", 3601,
					new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_noDataIntervalIntMax_throwsValidatorExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			service.subscribe(TestConstants.mask, "sessionInfo", Integer.MAX_VALUE,
					new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_callbackNull_throwsValidatorExceptionNotSubscribed() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			service.subscribe(TestConstants.mask, "sessionInfo", 300, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_authenticationIdNull_isSubscribedSessionIdExists() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, null,
				new DemoPublishClientCallback(service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_authenticationIdEmpty_isSubscribedSessionIdExists() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, "",
				new DemoPublishClientCallback(service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_authenticationIdWhiteSpace_isSubscribedSessionIdExists() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, " ",
				new DemoPublishClientCallback(service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_authenticationIdOneChar_isSubscribedSessionIdExists() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, "a",
				new DemoPublishClientCallback(service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_authenticationIdPangram_isSubscribedSessionIdExists() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, TestConstants.pangram,
				new DemoPublishClientCallback(service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_authenticationId256LongString_isSubscribedSessionIdExists()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, TestConstants.stringLength256,
				new DemoPublishClientCallback(service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_authenticationId257LongString_throwsValidatorExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			service.subscribe(TestConstants.mask, "sessionInfo", 3601,
					TestConstants.stringLength257, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_timeout0_throwsValidatorExceptionNotSubscribed() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			service.subscribe(TestConstants.mask, "sessionInfo", 300,
					new DemoPublishClientCallback(service), 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_timeoutMinus1_throwsValidatorExceptionNotSubscribed() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			service.subscribe(TestConstants.mask, "sessionInfo", 300,
					new DemoPublishClientCallback(service), -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	// this might sometimes fail with timeout
	@Test
	public void subscribe_timeout1_eitherSubscribedOrTimedOut() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			service.subscribe(TestConstants.mask, "sessionInfo", 300,
					new DemoPublishClientCallback(service), 1);
		} catch (Exception e) {
			testLogger.info(e.getMessage());
			assertEquals(true, e.getMessage().toLowerCase().contains("timeout"));
			assertEquals(false, service.isSubscribed());
			assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
			return;
		}
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_timeoutMaxAllowed_isSubscribedSessionIdExists() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(
				service), 3600);
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_timeoutMaxAllowedPlusOne_throwsValidatorExceptionNotSubscribed()
			throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			service.subscribe(TestConstants.mask, "sessionInfo", 300,
					new DemoPublishClientCallback(service), 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_timeoutIntMax_throwsValidatorExceptionNotSubscribed() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			service.subscribe(TestConstants.mask, "sessionInfo", 300,
					new DemoPublishClientCallback(service), Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_timeoutIntMin_throwsValidatorExceptionNotSubscribed() throws Exception {
		IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			service.subscribe(TestConstants.mask, "sessionInfo", 300,
					new DemoPublishClientCallback(service), Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	private class DemoPublishClientCallback extends SCMessageCallback {

		public DemoPublishClientCallback(IService service) {
			super(service);
		}

		@Override
		public void callback(ISCMessage reply) {
			testLogger.info("Publish client received: " + reply.getData());
		}

		@Override
		public void callback(Exception e) {
		}
	}
}
