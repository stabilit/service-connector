package integration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.ISCClient;
import com.stabilit.scm.common.service.SCServiceException;

public class EnableDisableServiceClientToSCTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(AttachDetachClientToSCTest.class);

	private static Process p;

	private ISCClient client;

	private String serviceName = "simulation";
	private String newServiceName = "notExistingService";

	private Exception ex;

	private static final String host = "localhost";
	private static final int port8080 = 8080;
	private static final int port9000 = 9000;

	@BeforeClass
	public static void oneTimeSetUp() {

		String userDir = System.getProperty("user.dir");
		String command = "java -Dlog4j.configuration=file:" + userDir
				+ "\\src\\test\\resources\\log4jSC0.properties -jar " + userDir
				+ "\\..\\service-connector\\target\\sc.jar -filename " + userDir
				+ "\\src\\test\\resources\\scIntegration.properties";

		try {
			p = Runtime.getRuntime().exec(command);

			// lets the SC load before starting communication
			Thread.sleep(1000);
		} catch (IOException e) {
			logger.error("oneTimeSetUp", e);
		} catch (InterruptedException e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() {
		p.destroy();
	}

	/**
	 * @throws java.lang.Exception
	 * 
	 *             Create a new SCClient for each test method.
	 */
	@Before
	public void setUp() throws Exception {
		client = new SCClient();
		client.attach(host, port8080);
	}
	
	@After
	public void tearDown() throws Exception {
		client.detach();
	}

	@Test
	public void enableService_withoutAttach_throwsException() throws Exception {
		client.detach();
		try {
			client.enableService(serviceName);
		} catch (SCServiceException e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void disableService_withoutAttach_throwsException() throws Exception {
		client.detach();
		try {
			client.disableService(serviceName);
		} catch (SCServiceException e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void enableService_simulationAlreadyEnabled_staysEnabled()
			throws Exception {
		assertEquals(true, client.isServiceEnabled(serviceName));
		client.enableService(serviceName);
		assertEquals(true, client.isServiceEnabled(serviceName));
	}
	
	@Test
	public void enableService_unabledService_fromNotEnabledToEnabled()
			throws Exception {
		assertEquals(false, client.isServiceEnabled(newServiceName));
		client.enableService(newServiceName);
		assertEquals(true, client.isServiceEnabled(newServiceName));
		client.disableService(newServiceName);
	}
	
	@Test
	public void disableService_unabledService_passes() throws Exception {
		assertEquals(false, client.isServiceEnabled(newServiceName));
		client.enableService(newServiceName);
		assertEquals(false, client.isServiceEnabled(newServiceName));
	}
	
	@Test
	public void disableService_simulationAlreadyEnabled_disabled() throws Exception {
		assertEquals(true, client.isServiceEnabled(serviceName));
		client.disableService(serviceName);
		assertEquals(false, client.isServiceEnabled(serviceName));
		client.enableService(serviceName);
	}
	

}
