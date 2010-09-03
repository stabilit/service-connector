package integration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.scm.srv.ISCServer;
import com.stabilit.scm.srv.ISCServerCallback;
import com.stabilit.scm.srv.SCServer;

public class RegisterServiceDeregisterServiceServerToSCTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(RegisterServiceDeregisterServiceServerToSCTest.class);

	private static Process p;
	private ISCServer server;
	private String serviceName = "simulation";
	private String serviceNameAlt = "P01_RTXS_sc1";
	private String host = "localhost";
	private int port8080 = 8080;
	private int port9000 = 9000;

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

			Thread.sleep(1000);
		} catch (InterruptedException e) {
			logger.error("oneTimeSetUp", e);
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
		server = new SCServer();
	}

	@After
	public void tearDown() throws Exception {
		server = null;
	}

	@Test
	public void deregisterService_withoutListenerArbitraryServiceName_notRegistered()
			throws Exception {
		server.deregisterService("Name");
		assertEquals(false, server.isRegistered("Name"));
	}

	@Test
	public void deregisterService_withoutRegisteringArbitraryServiceName_notRegistered()
			throws Exception {
		server.startListener(host, 9001, 1);
		server.deregisterService("Name");
		assertEquals(false, server.isRegistered("Name"));
	}

	@Test
	public void deregisterService_withoutRegisteringServiceNameInSCProps_notRegistered()
			throws Exception {
		server.startListener(host, 9001, 1);
		server.deregisterService(host);
		assertEquals(false, server.isRegistered(host));
	}

	@Test
	public void deregisterService_withoutRegisteringServicewithNoHost_notRegistered()
			throws Exception {
		server.startListener(host, 9001, 1);
		server.deregisterService(null);
		assertEquals(false, server.isRegistered(null));
	}

	@Test
	public void deregisterService_withoutRegisteringServicewithEmptyHost_notRegistered()
			throws Exception {
		server.startListener(host, 9001, 1);
		server.deregisterService("");
		assertEquals(false, server.isRegistered(""));
	}

	@Test
	public void deregisterService_withoutRegisteringServicewithWhiteSpaceHost_notRegistered()
			throws Exception {
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
	public void deregisterService_afterValidRegisterDifferentServiceName_registeredThenNotRegistered()
			throws Exception {
		server.startListener(host, 9001, 1);
		server.registerService(host, port9000, serviceNameAlt, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(serviceNameAlt));
		server.deregisterService(serviceNameAlt);
		assertEquals(false, server.isRegistered(serviceNameAlt));
	}

	@Test
	public void deregisterService_differentThanRegistered_registered() throws Exception {
		server.startListener(host, 9001, 1);
		server.registerService(host, port9000, serviceName, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
		server.deregisterService(serviceNameAlt);
		assertEquals(true, server.isRegistered(serviceName));
		server.deregisterService(serviceName);
	}

	@Test
	public void registerServiceDeregisterService_cycle500Times_registeredThenNotRegistered()
			throws Exception {
		server.startListener(host, 9001, 1);
		int cycles = 500;
		for (int i = 0; i < cycles / 10; i++) {
			System.out.println("RegisterDeregister service iteration:\t" + i * 10);
			for (int j = 0; j < 10; j++) {
				server.registerService(host, port9000, serviceName, 1, 1, new CallBack());
				assertEquals(true, server.isRegistered(serviceName));
				server.deregisterService(serviceName);
				assertEquals(false, server.isRegistered(serviceName));
			}
		}
	}

	@Test
	public void registerService_500CyclesWithChangingConnectionType_registeredThenNotRegistered()
			throws Exception {
		int cycles = 250;
		for (int i = 0; i < cycles / 10; i++) {
			System.out.println("RegisterDeregister changing connection type iteration:\t" + i * 10);
			for (int j = 0; j < 10; j++) {
				server = new SCServer();
				((SCServer) server).setConnectionType("netty.tcp");
				server.startListener(host, 9001, 0);
				server.registerService(host, port9000, serviceName, 1, 1, new CallBack());
				assertEquals(true, server.isRegistered(serviceName));
				server.deregisterService(serviceName);
				assertEquals(false, server.isRegistered(serviceName));
				server = null;
				server = new SCServer();
				((SCServer) server).setConnectionType("netty.http");
				server.startListener(host, 9001, 0);
				server.registerService(host, port8080, serviceName, 1, 1, new CallBack());
				assertEquals(true, server.isRegistered(serviceName));
				server.deregisterService(serviceName);
				assertEquals(false, server.isRegistered(serviceName));
				server = null;
			}
		}
	}

	// region end

	private class CallBack implements ISCServerCallback {
	}

}
