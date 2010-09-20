package integration.cln;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.cln.ISCClient;
import org.serviceconnector.cln.SCClient;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.ctrl.util.TestEnvironmentController;
import org.serviceconnector.sc.service.SCServiceException;


public class EnableDisableServiceTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(EnableDisableServiceTest.class);

	private static Process scProcess;

	private ISCClient client;

	private Exception ex;

	private static TestEnvironmentController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
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
		client = new SCClient();
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
	}
	
	@After
	public void tearDown() throws Exception {
		client.detach();
		client = null;
		ex = null;
	}
	
	@Test
	public void isEnabled_enabledService_isEnabled() throws SCServiceException {
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));
	}
	
	@Test
	public void isEnabled_disabledService_isNotEnabled() throws SCServiceException {
		assertEquals(false, client.isServiceEnabled(TestConstants.serviceNameSessionNotEnabled));
	}
	
	@Test(expected = SCServiceException.class)
	public void isEnabled_notExistingService_throwsException() throws SCServiceException {
		client.isServiceEnabled("notExistingService");
	}

	@Test
	public void enableService_withoutAttach_throwsException() throws Exception {
		client.detach();
		try {
			client.enableService(TestConstants.serviceName);
		} catch (SCServiceException e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void disableService_withoutAttach_throwsException() throws Exception {
		client.detach();
		try {
			client.disableService(TestConstants.serviceName);
		} catch (SCServiceException e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void enableService_simulationAlreadyEnabled_staysEnabled()
			throws Exception {
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));
		client.enableService(TestConstants.serviceName);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));
	}
	
	@Test
	public void enableService_unabledService_fromNotEnabledToEnabled()
			throws Exception {
		assertEquals(false, client.isServiceEnabled(TestConstants.serviceNameSessionNotEnabled));
		client.enableService(TestConstants.serviceNameSessionNotEnabled);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameSessionNotEnabled));
		client.disableService(TestConstants.serviceNameSessionNotEnabled);
	}
	
	@Test
	public void disableService_unabledService_passes() throws Exception {
		assertEquals(false, client.isServiceEnabled(TestConstants.serviceNameSessionNotEnabled));
		client.enableService(TestConstants.serviceNameSessionNotEnabled);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameSessionNotEnabled));
		client.disableService(TestConstants.serviceNameSessionNotEnabled);
	}
	
	@Test
	public void disableService_simulationAlreadyEnabled_disabled() throws Exception {
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));
		client.disableService(TestConstants.serviceName);
		assertEquals(false, client.isServiceEnabled(TestConstants.serviceName));
		client.enableService(TestConstants.serviceName);
	}
	
	//TODO allows enable to pass without exception but not isServiceEnabled
	@Test
	public void enableService_notExistingService_notEnabled() throws Exception {
//		assertEquals(false, client.isServiceEnabled("notExistingService"));
		client.enableService("notExistingService");
		assertEquals(false, client.isServiceEnabled("notExistingService"));
	}
	
	@Test
	public void disableService_notExistingService_notEnabled() throws Exception {
//		assertEquals(false, client.isServiceEnabled("notExistingService"));
		client.disableService("notExistingService");
		assertEquals(false, client.isServiceEnabled("notExistingService"));
	}
	
	@Test
	public void enableDisableService_anotherExistingService_switchesStates() throws Exception {
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameAlt));
		client.disableService(TestConstants.serviceNameAlt);
		assertEquals(false, client.isServiceEnabled(TestConstants.serviceNameAlt));
		client.enableService(TestConstants.serviceNameAlt);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameAlt));
	}
	
	@Test
	public void enableDisableService_1000Times_switchesStates() throws Exception {
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));
		for (int i = 0; i < 100; i++) {
//			System.out.println("Enabling/disabling service, iteration:\t" + i*10);
			for (int j = 0; j < 10; j++) {
				client.disableService(TestConstants.serviceName);
				assertEquals(false, client.isServiceEnabled(TestConstants.serviceName));
				client.enableService(TestConstants.serviceName);
				assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));
			}
		}
	}
	
	@Test
	public void enableDisableService_twoClients_seeChangesOfTheOther() throws Exception {
		ISCClient client2 = new SCClient();
		client2.attach(TestConstants.HOST, TestConstants.PORT8080);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));
		assertEquals(true, client2.isServiceEnabled(TestConstants.serviceName));
		client.disableService(TestConstants.serviceName);
		assertEquals(false, client.isServiceEnabled(TestConstants.serviceName));
		assertEquals(false, client2.isServiceEnabled(TestConstants.serviceName));
		client2.enableService(TestConstants.serviceName);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));
		assertEquals(true, client2.isServiceEnabled(TestConstants.serviceName));
		client2.detach();
	}
	
	@Test
	public void enableDisableService_twoClientsDifferentConnectionTypes_seeChangesOfTheOther() throws Exception {
		ISCClient client2 = new SCClient();
		((SCClient) client2).setConnectionType("netty.tcp");
		client2.attach(TestConstants.HOST, TestConstants.PORT9000);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));
		assertEquals(true, client2.isServiceEnabled(TestConstants.serviceName));
		client.disableService(TestConstants.serviceName);
		assertEquals(false, client.isServiceEnabled(TestConstants.serviceName));
		assertEquals(false, client2.isServiceEnabled(TestConstants.serviceName));
		client2.enableService(TestConstants.serviceName);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));
		assertEquals(true, client2.isServiceEnabled(TestConstants.serviceName));
		client2.detach();
	}
}
