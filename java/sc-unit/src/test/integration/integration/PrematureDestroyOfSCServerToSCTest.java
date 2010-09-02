package integration;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.ISCClient;
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
	private int port8080 = 8080;
	private int port9000 = 9000;

	private String serviceName = "simulation";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		try {
			String userDir = System.getProperty("user.dir");
			String command = "java -Dlog4j.configuration=file:" + userDir
					+ "\\src\\test\\resources\\log4jSC0.properties -jar " + userDir
					+ "\\..\\service-connector\\target\\sc.jar -filename " + userDir
					+ "\\src\\test\\resources\\scIntegration.properties";

			p = Runtime.getRuntime().exec(command);

			// lets the SC load before starting communication
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error("oneTimeSetUp", e);
			}
		} catch (IOException e) {
			logger.error("oneTimeSetUp", e);
		}

		server = new SCServer();
		server.startListener(host, 30000, 0);
	}

	@After
	public void tearDown() throws Exception {
		p.destroy();
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
