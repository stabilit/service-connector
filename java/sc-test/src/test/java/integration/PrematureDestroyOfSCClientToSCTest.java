package integration;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		try {
			String userDir = System.getProperty("user.dir");
			String command = "java -Dlog4j.configuration=file:" + userDir
					+ "\\src\\main\\resources\\log4jSC0.properties -jar " + userDir
					+ "\\..\\service-connector\\target\\sc.jar -filename " + userDir
					+ "\\src\\main\\resources\\scIntegration.properties";

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
