package integration;

import static org.junit.Assert.*;


import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.scm.srv.ISCServer;
import com.stabilit.scm.srv.ISCServerCallback;
import com.stabilit.scm.srv.SCServer;


public class RegisterServiceDeregisterServiceServerToSCTest {

	private static Process p;
	private ISCServer server;
	private Exception ex;
	private String serviceName = "simulation";
	private String serviceNameAlt = "P01_RTXS_sc1";
	private String host = "localhost";
	private int port9000 = 9000;

	@BeforeClass
	public static void oneTimeSetUp() {
		try {
			String userDir = System.getProperty("user.dir");
			String command = "java -Dlog4j.configuration=file:" + userDir
					+ "\\src\\test\\resources\\log4j.properties -jar " + userDir
					+ "\\..\\service-connector\\target\\sc.jar -filename " + userDir
					+ "\\src\\test\\resources\\scIntegration.properties";

			p = Runtime.getRuntime().exec(command);

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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		server = new SCServer();
	}


	@Test
	public void deregisterService_withoutListenerArbitraryServiceName_notRegistered() throws Exception {
		server.deregisterService("Name");
		assertEquals(false, server.isRegistered("Name"));
	}
	
	@Test
	public void deregisterService_withoutRegisteringArbitraryServiceName_notRegistered() throws Exception {
		server.startListener(host, 9001, 1);
		server.deregisterService("Name");
		assertEquals(false, server.isRegistered("Name"));
	}
	
	@Test
	public void deregisterService_withoutRegisteringServiceNameInSCProps_notRegistered() throws Exception {
		server.startListener(host, 9001, 1);
		server.deregisterService(host);
		assertEquals(false, server.isRegistered(host));
	}
	
	@Test
	public void deregisterService_withoutRegisteringServicewithNoHost_notRegistered() throws Exception {
		server.startListener(host, 9001, 1);
		server.deregisterService(null);
		assertEquals(false, server.isRegistered(null));
	}
	
	@Test
	public void deregisterService_withoutRegisteringServicewithEmptyHost_notRegistered() throws Exception {
		server.startListener(host, 9001, 1);
		server.deregisterService("");
		assertEquals(false, server.isRegistered(""));
	}
	
	@Test
	public void deregisterService_withoutRegisteringServicewithWhiteSpaceHost_notRegistered() throws Exception {
		server.startListener(host, 9001, 1);
		server.deregisterService(" ");
		assertEquals(false, server.isRegistered(" "));
	}
	
	@Test
	public void deregisterService_afterValidRegister_registeredThenNotRegistered() throws Exception {
		server.startListener(host, 9001, 1);
		server.registerService(host, port9000, serviceName, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
		server.deregisterService(serviceName);
		assertEquals(false, server.isRegistered(serviceName));
	}
	
	@Test
	public void deregisterService_afterValidRegisterDifferentServiceName_registeredThenNotRegistered() throws Exception {
		server.startListener(host, 9001, 1);
		server.registerService(host, port9000, serviceNameAlt, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(serviceNameAlt));
		server.deregisterService(serviceNameAlt);
		assertEquals(false, server.isRegistered(serviceNameAlt));
	}
	
	

	// region end

	private class CallBack implements ISCServerCallback {
	}

}
