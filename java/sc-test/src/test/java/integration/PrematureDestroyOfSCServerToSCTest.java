package integration;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.stabilit.sc.ctrl.util.TestEnvironmentController;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.service.SCServiceException;
import com.stabilit.scm.srv.ISCServer;
import com.stabilit.scm.srv.ISCServerCallback;
import com.stabilit.scm.srv.SCServer;

public class PrematureDestroyOfSCServerToSCTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger
			.getLogger(PrematureDestroyOfSCServerToSCTest.class);

	private static ISCServer server;
	private static Process p;

	private String host = "localhost";
	private int port9000 = 9000;

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

		server = new SCServer();
		server.startListener(host, 30000, 0);
	}

	@After
	public void tearDown() throws Exception {
		server.destroyServer();
		ctrl.stopProcess(p, log4jSC0Properties);
	}

	@Test(expected = SCServiceException.class)
	public void registerService_afterSCDestroyValidValues_throwsException() throws Exception {
		p.destroy();
		server.registerService(host, port9000, serviceName, 10, 10, new CallBack());
	}

	@Test(expected = SCMPValidatorException.class)
	public void registerService_afterSCDestroyInvalidMaxSessions_throwsException() throws Exception {
		p.destroy();
		server.registerService(host, port9000, serviceName, -1, 10, new CallBack());
	}

	@Test(expected = SCServiceException.class)
	public void registerService_afterSCDestroyInvalidHost_throwsException() throws Exception {
		p.destroy();
		server.registerService("something", port9000, serviceName, 10, 10, new CallBack());
	}

	@Test(expected = SCServiceException.class)
	public void registerService_withImmediateConnectFalseAfterSCDestroyInvalidHost_throwsException()
			throws Exception {
		server.setImmediateConnect(false);
		p.destroy();
		server.registerService("something", port9000, serviceName, 10, 10, new CallBack());
	}

	@Test
	public void deregisterService_afterSCDestroy_passes() throws Exception {
		p.destroy();
		server.deregisterService(serviceName);
	}

	@Test(expected = SCServiceException.class)
	public void deregisterService_afterRegisterAfterSCDestroy_throwsException() throws Exception {
		server.registerService(host, port9000, serviceName, 10, 10, new CallBack());
		p.destroy();
		server.deregisterService(serviceName);
	}

	private class CallBack implements ISCServerCallback {
	}
}
