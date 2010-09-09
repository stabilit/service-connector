package integration;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.stabilit.sc.ctrl.util.TestEnvironmentController;
import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.ISCClient;
import com.stabilit.scm.common.service.SCServiceException;


public class PrematureDestroyOfSCClientToSCTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PrematureDestroyOfSCClientToSCTest.class);
	
	private static ISCClient client;
	private static Process p;

	private String host = "localhost";
	private int port8080 = 8080;

	private String serviceName = "simulation";

	private static final String log4jSC0Properties = "log4jSC0.properties";
	private static final String scProperties0 = "scIntegration.properties";
	
	private static TestEnvironmentController ctrl;

	@Before
	public void setUp() throws Exception {
		ctrl = new TestEnvironmentController();
		try {
			p = ctrl.startSC(log4jSC0Properties, scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
		client = new SCClient();
	}

	@After
	public void tearDown() throws Exception {
		ctrl.stopProcess(p, log4jSC0Properties);
	}

	@Test(expected = SCServiceException.class)
	public void attach_afterSCDestroy_throwsException() throws Exception {
		p.destroy();
		client.attach(host, port8080);
	}
	
	@Test
	public void detach_beforeAttachAfterSCDestroy_passes() throws Exception {
		p.destroy();
		client.detach();
	}
	
	@Test(expected = SCServiceException.class)
	public void detach_afterSCDestroy_throwsException() throws Exception {
		client.attach(host, port8080);
		p.destroy();
		client.detach();
	}
	
	@Test(expected = SCServiceException.class)
	public void enableService_afterSCDestroy_throwsException() throws Exception {
		client.attach(host, port8080);
		p.destroy();
		client.enableService(serviceName);
	}
	
	@Test(expected = SCServiceException.class)
	public void disableService_afterSCDestroy_throwsException() throws Exception {
		client.attach(host, port8080);
		p.destroy();
		client.enableService(serviceName);
	}
	
	@Test(expected = SCServiceException.class)
	public void workload_afterSCDestroy_throwsException() throws Exception {
		client.attach(host, port8080);
		p.destroy();
		client.workload(serviceName);
	}
	
	@Test
	public void setMaxConnection_afterAttachAfterSCDestroy_passes() throws Exception {
		client.attach(host, port8080);
		p.destroy();
		client.setMaxConnections(10);
	}
}
