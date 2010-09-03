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
	protected final static Logger logger = Logger.getLogger(EnableDisableServiceClientToSCTest.class);

	private static Process p;

	private ISCClient client;

	private String serviceName = "simulation";
	private String serviceNameAlt = "P01_RTXS_sc1";
	private String newServiceName = "notEnabledService";

	private Exception ex;

	private static final String host = "localhost";
	private static final int port8080 = 8080;
	private static final int port9000 = 9000;

	@BeforeClass
	public static void oneTimeSetUp() {

		String userDir = System.getProperty("user.dir");
		String command = "java -Dlog4j.configuration=file:" + userDir
				+ "\\src\\main\\resources\\log4jSC0.properties -jar " + userDir
				+ "\\..\\service-connector\\target\\sc.jar -filename " + userDir
				+ "\\src\\main\\resources\\scIntegration.properties";

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
		assertEquals(true, client.isServiceEnabled(newServiceName));
		client.disableService(newServiceName);
	}
	
	@Test
	public void disableService_simulationAlreadyEnabled_disabled() throws Exception {
		assertEquals(true, client.isServiceEnabled(serviceName));
		client.disableService(serviceName);
		assertEquals(false, client.isServiceEnabled(serviceName));
		client.enableService(serviceName);
	}
	
	@Test
	public void enableService_notExistingService_notEnabled() throws Exception {
		assertEquals(false, client.isServiceEnabled("notExistingService"));
		client.enableService("notExistingService");
		assertEquals(false, client.isServiceEnabled("notExistingService"));
	}
	
	@Test
	public void disableService_notExistingService_notEnabled() throws Exception {
		assertEquals(false, client.isServiceEnabled("notExistingService"));
		client.disableService("notExistingService");
		assertEquals(false, client.isServiceEnabled("notExistingService"));
	}
	
	@Test
	public void enableDisableService_anotherExistingService_switchesStates() throws Exception {
		assertEquals(true, client.isServiceEnabled(serviceNameAlt));
		client.disableService(serviceNameAlt);
		assertEquals(false, client.isServiceEnabled(serviceNameAlt));
		client.enableService(serviceNameAlt);
		assertEquals(true, client.isServiceEnabled(serviceNameAlt));
	}
	
	@Test
	public void enableDisableService_1000Times_switchesStates() throws Exception {
		assertEquals(true, client.isServiceEnabled(serviceName));
		for (int i = 0; i < 100; i++) {
			System.out.println("Enabling/disabling service, iteration:\t" + i*10);
			for (int j = 0; j < 10; j++) {
				client.disableService(serviceName);
				assertEquals(false, client.isServiceEnabled(serviceName));
				client.enableService(serviceName);
				assertEquals(true, client.isServiceEnabled(serviceName));
			}
		}
	}
	
	@Test
	public void enableDisableService_twoClients_seeChangesOfTheOther() throws Exception {
		ISCClient client2 = new SCClient();
		client2.attach(host, port8080);
		assertEquals(true, client.isServiceEnabled(serviceName));
		assertEquals(true, client2.isServiceEnabled(serviceName));
		client.disableService(serviceName);
		assertEquals(false, client.isServiceEnabled(serviceName));
		assertEquals(false, client2.isServiceEnabled(serviceName));
		client2.enableService(serviceName);
		assertEquals(true, client.isServiceEnabled(serviceName));
		assertEquals(true, client2.isServiceEnabled(serviceName));
		client2.detach();
	}
	
	@Test
	public void enableDisableService_twoClientsDifferentConnectionTypes_seeChangesOfTheOther() throws Exception {
		ISCClient client2 = new SCClient();
		((SCClient) client2).setConnectionType("netty.tcp");
		client2.attach(host, port9000);
		assertEquals(true, client.isServiceEnabled(serviceName));
		assertEquals(true, client2.isServiceEnabled(serviceName));
		client.disableService(serviceName);
		assertEquals(false, client.isServiceEnabled(serviceName));
		assertEquals(false, client2.isServiceEnabled(serviceName));
		client2.enableService(serviceName);
		assertEquals(true, client.isServiceEnabled(serviceName));
		assertEquals(true, client2.isServiceEnabled(serviceName));
		client2.detach();
	}
}
