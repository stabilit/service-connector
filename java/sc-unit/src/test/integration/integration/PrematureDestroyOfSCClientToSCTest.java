package integration;

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


public class PrematureDestroyOfSCClientToSCTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PrematureDestroyOfSCClientToSCTest.class);
	
	private static ISCClient client;
	private static Process p;
	private Exception ex;

	private String host = "localhost";
	private int port8080 = 8080;

	@BeforeClass
	public static void oneTimeSetUp() {
	}

	@AfterClass
	public static void oneTimeTearDown() {
	}

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
		
		client = new SCClient();
	}

	@After
	public void tearDown() throws Exception {
		p.destroy();
	}

	@Test(expected = SCServiceException.class)
	public void attach_AfterSCDestroy_throwsException() throws Exception {
		p.destroy();
		client.attach(host, port8080);
	}
	
	@Test
	public void detach_BeforeAttachAfterSCDestroy_passes() throws Exception {
		p.destroy();
		client.detach();
	}
	
	@Test(expected = SCServiceException.class)
	public void detach_AfterSCDestroy_throwsException() throws Exception {
		client.attach(host, port8080);
		p.destroy();
		client.detach();
	}
}
