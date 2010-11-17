package org.serviceconnector.test.integration.cln;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnetor.TestConstants;


public class EnableDisableServiceTest {
	
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());
	
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(EnableDisableServiceTest.class);

	private static Process scProcess;

	private SCMgmtClient client;

	private Exception ex;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSCProperties);
		ctrl = null;
		scProcess = null;
	}

	@Before
	public void setUp() throws Exception {
		client = new SCMgmtClient();
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
	}
	
	@After
	public void tearDown() throws Exception {
		client.detach();
		client = null;
		ex = null;
	}
	
	@Test
	public void isEnabled_enabledService_isEnabled() throws SCServiceException {
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameSession));
	}
	
	@Test
	public void isEnabled_disabledService_isNotEnabled() throws SCServiceException {
		assertEquals(false, client.isServiceEnabled(TestConstants.serviceNameSession));
	}
	
	@Test(expected = SCServiceException.class)
	public void isEnabled_notExistingService_throwsException() throws SCServiceException {
		client.isServiceEnabled("notExistingService");
	}

	@Test
	public void enableService_withoutAttach_throwsException() throws Exception {
		client.detach();
		try {
			client.enableService(TestConstants.serviceNameSession);
		} catch (SCServiceException e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void disableService_withoutAttach_throwsException() throws Exception {
		client.detach();
		try {
			client.disableService(TestConstants.serviceNameSession);
		} catch (SCServiceException e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void enableService_AlreadyEnabled_staysEnabled()
			throws Exception {
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameSession));
		client.enableService(TestConstants.serviceNameSession);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameSession));
	}
	
	@Test
	public void enableService_disabledService_fromDisabledToEnabled()
			throws Exception {
		assertEquals(false, client.isServiceEnabled(TestConstants.serviceNameSession));
		client.enableService(TestConstants.serviceNameSession);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameSession));
		client.disableService(TestConstants.serviceNameSession);
	}
	
	@Test
	public void disableService_disabledService_passes() throws Exception {
		assertEquals(false, client.isServiceEnabled(TestConstants.serviceNameSession));
		client.enableService(TestConstants.serviceNameSession);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameSession));
		client.disableService(TestConstants.serviceNameSession);
	}
	
	@Test
	public void disableService_AlreadyEnabled_disabled() throws Exception {
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameSession));
		client.disableService(TestConstants.serviceNameSession);
		assertEquals(false, client.isServiceEnabled(TestConstants.serviceNameSession));
		client.enableService(TestConstants.serviceNameSession);
	}
	
	@Test(expected = SCServiceException.class)
	public void isServiceEnabled_notExistingService_throwsSCException() throws Exception {
		client.isServiceEnabled("notExistingService");		
	}

	@Test(expected = SCServiceException.class)
	public void enableService_notExistingService_throwsSCException() throws Exception {
		client.enableService("notExistingService");
	}

	@Test(expected = SCServiceException.class)
	public void disableService_notExistingService_throwsSCException() throws Exception {
		client.disableService("notExistingService");
	}
	
	@Test
	public void enableDisableService_anotherExistingService_switchesStates() throws Exception {
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNamePublish));
		client.disableService(TestConstants.serviceNamePublish);
		assertEquals(false, client.isServiceEnabled(TestConstants.serviceNamePublish));
		client.enableService(TestConstants.serviceNamePublish);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNamePublish));
	}
	
	@Test
	public void enableDisableService_1000Times_switchesStates() throws Exception {
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameSession));
		for (int i = 0; i < 1000; i++) {
			if ((i % 500) == 0) testLogger.info("Enabling/disabling cycle nr. " + i + "...");
			client.disableService(TestConstants.serviceNameSession);
			assertEquals(false, client.isServiceEnabled(TestConstants.serviceNameSession));
			client.enableService(TestConstants.serviceNameSession);
			assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameSession));
		}
	}
	
	@Test
	public void enableDisableService_twoClients_seeChangesOfTheOther() throws Exception {
		SCMgmtClient client2 = new SCMgmtClient();
		client2.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameSession));
		assertEquals(true, client2.isServiceEnabled(TestConstants.serviceNameSession));
		client.disableService(TestConstants.serviceNameSession);
		assertEquals(false, client.isServiceEnabled(TestConstants.serviceNameSession));
		assertEquals(false, client2.isServiceEnabled(TestConstants.serviceNameSession));
		client2.enableService(TestConstants.serviceNameSession);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameSession));
		assertEquals(true, client2.isServiceEnabled(TestConstants.serviceNameSession));
		client2.detach();
	}
	
	@Test
	public void enableDisableService_twoClientsDifferentConnectionTypes_seeChangesOfTheOther() throws Exception {
		SCMgmtClient client2 = new SCMgmtClient();
		((SCMgmtClient) client2).setConnectionType("netty.tcp");
		client2.attach(TestConstants.HOST, TestConstants.PORT_TCP);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameSession));
		assertEquals(true, client2.isServiceEnabled(TestConstants.serviceNameSession));
		client.disableService(TestConstants.serviceNameSession);
		assertEquals(false, client.isServiceEnabled(TestConstants.serviceNameSession));
		assertEquals(false, client2.isServiceEnabled(TestConstants.serviceNameSession));
		client2.enableService(TestConstants.serviceNameSession);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceNameSession));
		assertEquals(true, client2.isServiceEnabled(TestConstants.serviceNameSession));
		client2.detach();
	}
}
