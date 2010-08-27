package integration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.scm.srv.ISCServer;
import com.stabilit.scm.srv.ISCServerCallback;
import com.stabilit.scm.srv.SCServer;

public class RegisterServiceServerToMultipleSCTest {

	private static Process p;
	private static Process r;
	private ISCServer server;
	private String serviceName = "simulation";
	private String serviceNameAlt = "P01_RTXS_sc1";
	private String host = "localhost";
	private int port9000 = 9000;
	private int port65535 = 1;

	@BeforeClass
	public static void oneTimeSetUp() {
		try {
			String userDir = System.getProperty("user.dir");
			String cmdP0 = "java -Dlog4j.configuration=file:" + userDir
					+ "\\src\\test\\resources\\";
			String cmdP1 = " -jar " + userDir
					+ "\\..\\service-connector\\target\\sc.jar -filename " + userDir
					+ "\\src\\test\\resources\\";

			p = Runtime.getRuntime().exec(
					cmdP0 + "log4jSC0.properties" + cmdP1 + "scIntegration.properties");
			r = Runtime.getRuntime().exec(
					cmdP0 + "log4jSC1.properties" + cmdP1 + "scIntegrationChanged.properties");

			// lets the SC load before starting communication
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void oneTimeTearDown() {
		p.destroy();
		r.destroy();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		server = new SCServer();
	}

	@After
	public void tearDown() throws Exception {
		server = null;
	}

	@Test
	public void registerService_onMultipleSCs_registeredOnBoth() throws Exception {
		server.startListener(host, 9001, 0);
		server.registerService(host, port9000, serviceName, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
		assertEquals(false, server.isRegistered(serviceNameAlt));
		server.registerService(host, port65535, serviceNameAlt, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
		assertEquals(true, server.isRegistered(serviceNameAlt));
		server.deregisterService(serviceName);
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, server.isRegistered(serviceNameAlt));
		server.deregisterService(serviceNameAlt);
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(false, server.isRegistered(serviceNameAlt));
	}

	private class CallBack implements ISCServerCallback {
	}
}
