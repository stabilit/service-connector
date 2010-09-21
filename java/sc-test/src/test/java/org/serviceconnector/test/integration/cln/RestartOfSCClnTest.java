package org.serviceconnector.test.integration.cln;

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


public class RestartOfSCClnTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger
			.getLogger(RestartOfSCClnTest.class);

	private ISCClient client;
	private Process scProcess;

	private static TestEnvironmentController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
	}

	@Before
	public void setUp() throws Exception {
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
		client = new SCClient();
	}

	@After
	public void tearDown() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSC0Properties);
		client = null;
		scProcess = null;
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl = null;
	}

	@Test(expected = SCServiceException.class)
	public void attach_againAfterSCRestart_throwsException() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT8080);

		// restart SC
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
	}

	@Test(expected = SCServiceException.class)
	public void detach_afterSCRestart_throwsException() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		client.detach();
	}

	@Test(expected = SCServiceException.class)
	public void enableService_afterSCRestart_throwsException() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		client.enableService(TestConstants.serviceName);
	}

	@Test(expected = SCServiceException.class)
	public void disableService_afterSCRestart_throwsException() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		client.enableService(TestConstants.serviceName);
	}

	@Test(expected = SCServiceException.class)
	public void workload_afterSCRestart_throwsException() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		client.workload(TestConstants.serviceName);
	}

	@Test
	public void setMaxConnection_afterAttachAfterSCRestart_passes() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		client.setMaxConnections(10);
		assertEquals(10, client.getMaxConnections());
	}
	
	@Test
	public void isAttached_afterAttachAfterSCRestart_true() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		assertEquals(true, client.isAttached());
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		assertEquals(true, client.isAttached());
	}
	
	@Test
	public void attach_afterAttachAndSCRestartAndDetach_attached() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		assertEquals(true, client.isAttached());
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		try {
			client.detach();
		} catch (SCServiceException e) {
		}
		assertEquals(false, client.isAttached());
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		assertEquals(true, client.isAttached());
	}
	
	@Test
	public void isServiceDisabled_afterDisablingItBeforeSCRestart_enabled() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));
		client.disableService(TestConstants.serviceName);
		assertEquals(false, client.isServiceEnabled(TestConstants.serviceName));
		scProcess = ctrl.restartSC(scProcess, TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		try {
			client.detach();
		} catch (SCServiceException e) {
		}
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		assertEquals(true, client.isServiceEnabled(TestConstants.serviceName));
	}
}
