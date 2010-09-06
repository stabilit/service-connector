package integration;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.sc.ctrl.util.TestEnvironmentController;
import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.ISCClient;
import com.stabilit.scm.common.service.SCServiceException;

public class RestartSCClientToSCTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger
			.getLogger(PrematureDestroyOfSCClientToSCTest.class);

	private static ISCClient client;
	private static Process p;

	private String host = "localhost";
	private int port8080 = 8080;

	private String serviceName = "simulation";
	private static final String log4jSCProperties = "log4jSC0.properties";
	private static final String scProperties = "scIntegration.properties";

	private static TestEnvironmentController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
	}

	@Before
	public void setUp() throws Exception {
		try {
			p = ctrl.startSC(log4jSCProperties, scProperties);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
		client = new SCClient();
	}

	@After
	public void tearDown() throws Exception {
		p.destroy();
		ctrl.deleteFile(ctrl.getPidLogPath(log4jSCProperties));
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		p.destroy();
		ctrl.deleteFile(ctrl.getPidLogPath(log4jSCProperties));
	}

	@Test(expected = SCServiceException.class)
	public void attach_againAfterSCRestart_throwsException() throws Exception {
		client.attach(host, port8080);

		// restart SC
		p = ctrl.restartSC(p, log4jSCProperties, scProperties);
		client.attach(host, port8080);
	}

	@Test(expected = SCServiceException.class)
	public void detach_afterSCRestart_throwsException() throws Exception {
		client.attach(host, port8080);
		p = ctrl.restartSC(p, log4jSCProperties, scProperties);
		client.detach();
	}

	@Test(expected = SCServiceException.class)
	public void enableService_afterSCRestart_throwsException() throws Exception {
		client.attach(host, port8080);
		p = ctrl.restartSC(p, log4jSCProperties, scProperties);
		client.enableService(serviceName);
	}

	@Test(expected = SCServiceException.class)
	public void disableService_afterSCRestart_throwsException() throws Exception {
		client.attach(host, port8080);
		p = ctrl.restartSC(p, log4jSCProperties, scProperties);
		client.enableService(serviceName);
	}

	@Test(expected = SCServiceException.class)
	public void workload_afterSCRestart_throwsException() throws Exception {
		client.attach(host, port8080);
		p = ctrl.restartSC(p, log4jSCProperties, scProperties);
		client.workload(serviceName);
	}

	@Test
	public void setMaxConnection_afterAttachAfterSCRestart_passes() throws Exception {
		client.attach(host, port8080);
		p = ctrl.restartSC(p, log4jSCProperties, scProperties);
		client.setMaxConnections(10);
	}
	
	@Test
	public void attach_afterAttachAndSCRestartAndDetach_attached() throws Exception {
		client.attach(host, port8080);
		assertEquals(true, client.isAttached());
		p = ctrl.restartSC(p, log4jSCProperties, scProperties);
		try {
			client.detach();
		} catch (SCServiceException e) {
		}
		assertEquals(false, client.isAttached());
		client.attach(host, port8080);
		assertEquals(true, client.isAttached());
	}
}
