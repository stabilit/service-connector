package org.serviceconnector.test.integration.cln;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.api.cln.ISCClient;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.service.SCServiceException;



public class PrematureDestroyOfSCClnTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PrematureDestroyOfSCClnTest.class);
	
	private ISCClient client;
	private Process scProcess;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
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
	public void attach_afterSCDestroy_throwsException() throws Exception {
		scProcess.destroy();
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
	}
	
	@Test
	public void detach_beforeAttachAfterSCDestroy_passes() throws Exception {
		scProcess.destroy();
		client.detach();
	}
	
	@Test
	public void detach_afterSCDestroy_passes() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		scProcess.destroy();
		client.detach();
		assertEquals(false, client.isAttached());
	}
	
	@Test(expected = SCServiceException.class)
	public void enableService_afterSCDestroy_throwsException() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		scProcess.destroy();
		client.enableService(TestConstants.serviceName);
	}
	
	@Test(expected = SCServiceException.class)
	public void disableService_afterSCDestroy_throwsException() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		scProcess.destroy();
		client.enableService(TestConstants.serviceName);
	}
	
	@Test(expected = SCServiceException.class)
	public void workload_afterSCDestroy_throwsException() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		scProcess.destroy();
		client.workload(TestConstants.serviceName);
	}
	
	@Test
	public void setMaxConnection_afterAttachAfterSCDestroy_passes() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		scProcess.destroy();
		client.setMaxConnections(10);
	}
}
