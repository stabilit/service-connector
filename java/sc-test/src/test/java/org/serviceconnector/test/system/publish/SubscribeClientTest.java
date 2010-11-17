package org.serviceconnector.test.system.publish;

import static org.junit.Assert.assertEquals;

import java.security.InvalidParameterException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.SCService;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.service.SCServiceException;

public class SubscribeClientTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SubscribeClientTest.class);

	private static Process scProcess;
	private static Process srvProcess;

	private SCClient client;

	private Exception ex;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.scProperties0);
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
		ctrl.stopProcess(scProcess, TestConstants.log4jSCProperties);
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		ctrl = null;
		scProcess = null;
		srvProcess = null;
	}

	@Test
	public void getSessionId_fromNewlyCreatedPublishService_emptySessionId() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		assertEquals(null, service.getSessionId());
	}

	@Test
	public void isSubscribed_serviceNameEmpty_false() throws Exception {
		SCPublishService service = client.newPublishService("");
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void isSubscribed_serviceNameWhiteSpace_false() throws Exception {
		SCPublishService service = client.newPublishService(" ");
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void isSubscribed_serviceNameSingleChar_false() throws Exception {
		SCPublishService service = client.newPublishService("a");
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void isSubscribed_serviceNameNotExisting_false() throws Exception {
		SCPublishService service = client.newPublishService("notExistingService");
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void isSubscribed_serviceNameSessionService_false() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNameSession);
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void isSubscribed_serviceNameDisabled_false() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNameSessionDisabled);
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void isSubscribed_fromNewlyCreatedPublishService_false() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameEmptyMaskNull_throwsValidatorException() throws Exception {
		SCPublishService service = client.newPublishService("");
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(null);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameEmptyMaskEmpty_throwsValidatorException() throws Exception {
		SCPublishService service = client.newPublishService("");
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask("");
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameEmptyMaskOneChar_throwsSCExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService("");
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask("a");
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameEmptyMaskWhiteSpace_throwsSCExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService("");
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(" ");
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameEmptyMask256LongString_throwsSCExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService("");
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.stringLength256);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameEmptyMask257LongString_throwsValidatorExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService("");
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.stringLength257);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
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
		SCPublishService service = client.newPublishService("");
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask("0000121ABCDEFGHIJKLMNO%----------X-----------");
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameEmptyMaskSameAsInServer_throwsSCExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService("");
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.mask);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameNotExistingServiceMaskNull_throwsValidatorException() throws Exception {
		SCPublishService service = client.newPublishService("notExistingService");
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(null);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameNotExistingServiceMaskEmpty_throwsValidatorException() throws Exception {
		SCPublishService service = client.newPublishService("notExistingService");
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask("");
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameNotExistingServiceMaskOneChar_throwsSCExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService("notExistingService");
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask("a");
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameNotExistingServiceMaskWhiteSpace_throwsSCExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService("notExistingService");
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(" ");
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
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
		SCPublishService service = client.newPublishService("notExistingService");
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.stringLength256);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
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
		SCPublishService service = client.newPublishService("notExistingService");
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.stringLength257);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
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
		SCPublishService service = client.newPublishService("notExistingService");
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask("0000121ABCDEFGHIJKLMNO%----------X-----------");
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
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
		SCPublishService service = client.newPublishService("notExistingService");
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.mask);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameSessionServiceMaskNull_throwsValidatorException() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNameSession);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(null);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameSessionServiceMaskEmpty_throwsValidatorException() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNameSession);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask("");
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameSessionServiceMaskOneChar_throwsSCExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNameSession);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask("a");
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameSessionServiceMaskWhiteSpace_throwsSCExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNameSession);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.mask);
			subscibeMessage.setSessionInfo(" ");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameSessionServiceMask256LongString_throwsSCExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNameSession);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.stringLength256);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
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
		SCPublishService service = client.newPublishService(TestConstants.serviceNameSession);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.stringLength257);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
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
		SCPublishService service = client.newPublishService(TestConstants.serviceNameSession);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask("0000121ABCDEFGHIJKLMNO%----------X-----------");
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameSessionServiceMaskSameAsInServer_throwsSCExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNameSession);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.mask);
			subscibeMessage.setSessionInfo(" ");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameDisabledMaskNull_throwsValidatorException() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNameSessionDisabled);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(null);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameDisabledMaskEmpty_throwsValidatorException() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNameSessionDisabled);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask("");
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameDisabledMaskOneChar_throwsSCExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNameSessionDisabled);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask("a");
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameDisabledMaskWhiteSpace_throwsSCExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNameSessionDisabled);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(" ");
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameDisabledMask256LongString_throwsSCExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNameSessionDisabled);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.stringLength256);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameDisabledMask257LongString_throwsValidatorExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNameSessionDisabled);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.stringLength257);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
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
		SCPublishService service = client.newPublishService(TestConstants.serviceNameSessionDisabled);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask("0000121ABCDEFGHIJKLMNO%----------X-----------");
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameDisabledMaskSameAsInServer_throwsSCExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublishDisabled);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.mask);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameValidMaskNull_throwsValidatorException() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(null);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameValidMaskEmpty_throwsValidatorException() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask("");
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameValidMaskOneChar_isSubscribedSessionIdExists() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask("a");
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_serviceNameValidMaskWhiteSpace_isSubscribedSessionIdExists() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo(" ");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_serviceNameValidMask256LongString_isSubscribedSessionIdExists() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.stringLength256);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_serviceNameValidMask257LongString_throwsValidatorExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.stringLength257);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
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
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask("0000121ABCDEFGHIJKLMNO%----------X-----------");
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_serviceNameValidMaskSameAsInServer_isSubscribedSessionIdExists() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_sessionInfoOneChar_isSubscribedSessionIdExists() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("a");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_sessionInfoPangram_isSubscribedSessionIdExists() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo(TestConstants.pangram);
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_sessionInfo256LongString_isSubscribedSessionIdExists() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.stringLength256);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_sessionInfo257LongString_throwsValidatorExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.mask);
			subscibeMessage.setSessionInfo(TestConstants.stringLength257);
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_noDataInterval0_throwsValidatorExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.mask);
			subscibeMessage.setSessionInfo("sessionInfo");
			subscibeMessage.setNoDataIntervalInSeconds(0);
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_noDataIntervalMinus1_throwsValidatorExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.mask);
			subscibeMessage.setSessionInfo("sessionInfo");
			subscibeMessage.setNoDataIntervalInSeconds(-1);
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_noDataInterval1_isSubscribedSessionIdExists() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		subscibeMessage.setNoDataIntervalInSeconds(1);
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_noDataIntervalMaxAllowed_isSubscribedSessionIdExists() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		subscibeMessage.setNoDataIntervalInSeconds(3600);
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_noDataIntervalMaxAllowedPlusOne_throwsValidatorExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.mask);
			subscibeMessage.setSessionInfo("sessionInfo");
			subscibeMessage.setNoDataIntervalInSeconds(3601);
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_noDataIntervalIntMax_throwsValidatorExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.mask);
			subscibeMessage.setSessionInfo("sessionInfo");
			subscibeMessage.setNoDataIntervalInSeconds(Integer.MAX_VALUE);
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_callbackNull_throwsValidatorExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.mask);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof InvalidParameterException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_timeout0_throwsValidatorExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.mask);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service), 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_timeoutMinus1_throwsValidatorExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.mask);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service), -1);
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
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.mask);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service), 1);
		} catch (Exception e) {
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
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service), 3600);
		assertEquals(true, service.isSubscribed());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		service.unsubscribe();
	}

	@Test
	public void subscribe_timeoutMaxAllowedPlusOne_throwsValidatorExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.mask);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service), 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_timeoutIntMax_throwsValidatorExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.mask);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service), Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_timeoutIntMin_throwsValidatorExceptionNotSubscribed() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.mask);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service), Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribe_twiceInARow_throwsSCExceptionRemainsSubscribed() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		try {
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex.getMessage().equals("already subscribed"));
		assertEquals(SCServiceException.class, ex.getClass());
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(true, service.isSubscribed());
	}

	private class DemoPublishClientCallback extends SCMessageCallback {

		public DemoPublishClientCallback(SCService service) {
			super(service);
		}

		@Override
		public void receive(SCMessage reply) {
			logger.info("Publish client received: " + reply.getData());
		}

		@Override
		public void receive(Exception e) {
		}
	}
}
