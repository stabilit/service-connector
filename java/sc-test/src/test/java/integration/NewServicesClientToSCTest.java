package integration;

import static org.junit.Assert.assertEquals;

import java.security.InvalidParameterException;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.sc.ctrl.util.TestEnvironmentController;
import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.IFileService;
import com.stabilit.scm.cln.service.IPublishService;
import com.stabilit.scm.cln.service.ISCClient;
import com.stabilit.scm.cln.service.ISessionService;

public class NewServicesClientToSCTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(NewServicesClientToSCTest.class);

	private static ISCClient client;
	private static Process p;

	private static final String serviceName = "simulation";
	private static final String serviceNameAlt = "P01_RTXS_sc1";
	private static final String serviceNameNotEnabled = "notEnabledService";
	
	private String host = "localhost";

	private int port8080 = 8080;

	private static final String log4jSC0Properties = "log4jSC0.properties";
	private static final String scProperties0 = "scIntegration.properties";
	
	private static TestEnvironmentController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
		try {
			p = ctrl.startSC(log4jSC0Properties, scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(p, log4jSC0Properties);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		client = new SCClient();
		client.attach(host, port8080);
	}

	@Test(expected = InvalidParameterException.class)
	public void newSessionService_NullParam_throwsInvalidParamException() throws Exception {
		client.newSessionService(null);
	}

	@Test
	public void newSessionService_emptyStringParam_returnsISessionService() throws Exception {
		assertEquals(true, client.newSessionService("") instanceof ISessionService);
	}

	@Test
	public void newSessionService_whiteSpaceStringParam_returnsISessionService() throws Exception {
		assertEquals(true, client.newSessionService(" ") instanceof ISessionService);
	}

	@Test
	public void newSessionService_ArbitraryStringParam_returnsISessionService() throws Exception {
		assertEquals(
				true,
				client.newSessionService("The quick brown fox jumps over a lazy dog.") instanceof ISessionService);
	}

	@Test
	public void newSessionService_validServiceName_returnsISessionService() throws Exception {
		assertEquals(true, client.newSessionService(serviceName) instanceof ISessionService);
	}
	
	@Test
	public void newSessionService_validNotEnabledServiceName_returnsISessionService() throws Exception {
		assertEquals(true, client.newSessionService(serviceNameNotEnabled) instanceof ISessionService);
	}
	
	@Test
	public void newSessionService_twice_returnsISessionService() throws Exception {
		assertEquals(true, client.newSessionService(serviceName) instanceof ISessionService);
		assertEquals(true, client.newSessionService(serviceName) instanceof ISessionService);
	}
	
	@Test
	public void newSessionService_twiceDifferentServiceName_returnsISessionService() throws Exception {
		assertEquals(true, client.newSessionService(serviceName) instanceof ISessionService);
		assertEquals(true, client.newSessionService(serviceNameAlt) instanceof ISessionService);
	}
	
	@Test
	public void newSessionService_1000TimesDifferentServiceName_returnsISessionService() throws Exception {
		for (int i = 0; i < 500; i++) {
			assertEquals(true, client.newSessionService(serviceName) instanceof ISessionService);
			assertEquals(true, client.newSessionService(serviceNameAlt) instanceof ISessionService);
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void newPublishService_NullParam_throwsInvalidParamException() throws Exception {
		client.newPublishService(null);
	}

	@Test
	public void newPublishService_emptyStringParam_returnsIPublishService() throws Exception {
		assertEquals(true, client.newPublishService("") instanceof IPublishService);
	}

	@Test
	public void newPublishService_whiteSpaceStringParam_returnsIPublishService() throws Exception {
		assertEquals(true, client.newPublishService(" ") instanceof IPublishService);
	}

	@Test
	public void newPublishService_ArbitraryStringParam_returnsIPublishService() throws Exception {
		assertEquals(
				true,
				client.newPublishService("The quick brown fox jumps over a lazy dog.") instanceof IPublishService);
	}

	@Test
	public void newPublishService_validServiceName_returnsIPublishService() throws Exception {
		assertEquals(true, client.newPublishService(serviceName) instanceof IPublishService);
	}
	
	@Test
	public void newPublishService_validNotEnabledServiceName_returnsIPublishService() throws Exception {
		assertEquals(true, client.newPublishService(serviceNameNotEnabled) instanceof IPublishService);
	}
	
	@Test
	public void newPublishService_twice_returnsIPublishService() throws Exception {
		assertEquals(true, client.newPublishService(serviceName) instanceof IPublishService);
		assertEquals(true, client.newPublishService(serviceName) instanceof IPublishService);
	}
	
	@Test
	public void newPublishService_twiceDifferentServiceName_returnsIPublishService() throws Exception {
		assertEquals(true, client.newPublishService(serviceName) instanceof IPublishService);
		assertEquals(true, client.newPublishService(serviceNameAlt) instanceof IPublishService);
	}
	
	@Test
	public void newPublishService_1000TimesDifferentServiceName_returnsIPublishService() throws Exception {
		for (int i = 0; i < 500; i++) {
			assertEquals(true, client.newPublishService(serviceName) instanceof IPublishService);
			assertEquals(true, client.newPublishService(serviceNameAlt) instanceof IPublishService);
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void newFileService_NullParam_throwsInvalidParamException() throws Exception {
		client.newFileService(null);
	}

	@Test
	public void newFileService_emptyStringParam_returnsIFileService() throws Exception {
		assertEquals(true, client.newFileService("") instanceof IFileService);
	}

	@Test
	public void newFileService_whiteSpaceStringParam_returnsIFileService() throws Exception {
		assertEquals(true, client.newFileService(" ") instanceof IFileService);
	}

	@Test
	public void newFileService_ArbitraryStringParam_returnsIFileService() throws Exception {
		assertEquals(
				true,
				client.newFileService("The quick brown fox jumps over a lazy dog.") instanceof IFileService);
	}

	@Test
	public void newFileService_validServiceName_returnsIFileService() throws Exception {
		assertEquals(true, client.newFileService(serviceName) instanceof IFileService);
	}
	
	@Test
	public void newFileService_validNotEnabledServiceName_returnsIFileService() throws Exception {
		assertEquals(true, client.newFileService(serviceNameNotEnabled) instanceof IFileService);
	}
	
	@Test
	public void newFileService_twice_returnsIFileService() throws Exception {
		assertEquals(true, client.newFileService(serviceName) instanceof IFileService);
		assertEquals(true, client.newFileService(serviceName) instanceof IFileService);
	}
	
	@Test
	public void newFileService_twiceDifferentServiceName_returnsIFileService() throws Exception {
		assertEquals(true, client.newFileService(serviceName) instanceof IFileService);
		assertEquals(true, client.newFileService(serviceNameAlt) instanceof IFileService);
	}
	
	@Test
	public void newFileService_1000TimesDifferentServiceName_returnsIFileService() throws Exception {
		for (int i = 0; i < 500; i++) {
			assertEquals(true, client.newFileService(serviceName) instanceof IFileService);
			assertEquals(true, client.newFileService(serviceNameAlt) instanceof IFileService);
		}
	}
}
