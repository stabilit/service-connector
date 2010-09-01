package integration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.IFileService;
import com.stabilit.scm.cln.service.IPublishService;
import com.stabilit.scm.cln.service.ISCClient;
import com.stabilit.scm.cln.service.ISessionService;

public class NewServicesClientToSCTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(AttachClientToSCTest.class);

	private static ISCClient client;
	private static Process p;

	private String serviceName = "simulation";

	private String host = "localhost";

	private int port8080 = 8080;

	@BeforeClass
	public static void oneTimeSetUp() {
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
	}

	@AfterClass
	public static void oneTimeTearDown() {
		p.destroy();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		client = new SCClient();
		client.attach(host, port8080);
	}

	@After
	public void tearDown() throws Exception {
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
}
