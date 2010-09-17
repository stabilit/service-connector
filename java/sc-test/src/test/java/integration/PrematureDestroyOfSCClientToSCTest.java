package integration;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.sc.cln.SCClient;
import com.stabilit.sc.cln.service.ISCClient;
import com.stabilit.sc.common.service.SCServiceException;
import com.stabilit.sc.ctrl.util.TestConstants;
import com.stabilit.sc.ctrl.util.TestEnvironmentController;


public class PrematureDestroyOfSCClientToSCTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PrematureDestroyOfSCClientToSCTest.class);
	
	private ISCClient client;
	private Process p;

	private static TestEnvironmentController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
	}
	
	@Before
	public void setUp() throws Exception {
		try {
			p = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
		client = new SCClient();
	}

	@After
	public void tearDown() throws Exception {
		ctrl.stopProcess(p, TestConstants.log4jSC0Properties);
		client = null;
		p = null;
	}
	
	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl = null;
	}

	@Test(expected = SCServiceException.class)
	public void attach_afterSCDestroy_throwsException() throws Exception {
		p.destroy();
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
	}
	
	@Test
	public void detach_beforeAttachAfterSCDestroy_passes() throws Exception {
		p.destroy();
		client.detach();
	}
	
	@Test
	public void detach_afterSCDestroy_passes() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		p.destroy();
		client.detach();
		assertEquals(false, client.isAttached());
	}
	
	@Test(expected = SCServiceException.class)
	public void enableService_afterSCDestroy_throwsException() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		p.destroy();
		client.enableService(TestConstants.serviceName);
	}
	
	@Test(expected = SCServiceException.class)
	public void disableService_afterSCDestroy_throwsException() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		p.destroy();
		client.enableService(TestConstants.serviceName);
	}
	
	@Test(expected = SCServiceException.class)
	public void workload_afterSCDestroy_throwsException() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		p.destroy();
		client.workload(TestConstants.serviceName);
	}
	
	@Test
	public void setMaxConnection_afterAttachAfterSCDestroy_passes() throws Exception {
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		p.destroy();
		client.setMaxConnections(10);
	}
}
