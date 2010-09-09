package integration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.sc.ctrl.util.TestEnvironmentController;
import com.stabilit.scm.srv.ISCServer;
import com.stabilit.scm.srv.ISCServerCallback;
import com.stabilit.scm.srv.SCServer;

public class RegisterServiceServerToMultipleSCTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(RegisterServiceServerToMultipleSCTest.class);
	
	private static Process p;
	private static Process r;
	private ISCServer server;
	private String serviceName = "simulation";
	private String serviceNameAlt = "P01_RTXS_sc1";
	private String host = "localhost";
	private int port65535 = 65535;
	private int port9000 = 9000;
	private int port8080 = 8080;
	private int port1 = 1;

	private static final String log4jSC0Properties = "log4jSC0.properties";
	private static final String log4jSC1Properties = "log4jSC1.properties";
	private static final String scProperties0 = "scIntegration.properties";
	private static final String scProperties1 = "scIntegrationChanged.properties";

	private static TestEnvironmentController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
		try {
			p = ctrl.startSC(log4jSC0Properties, scProperties0);
			r = ctrl.startSC(log4jSC1Properties, scProperties1);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(p, log4jSC0Properties);
		ctrl.stopProcess(r, log4jSC1Properties);
	}

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

	@Test
	public void registerService_withDifferentConnectionTypesHttpFirst_registeredThenNot()
			throws Exception {
		server.startListener(host, 9001, 0);
		((SCServer) server).setConnectionType("netty.http");
		server.registerService(host, port8080, serviceName, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
		assertEquals(false, server.isRegistered(serviceNameAlt));
		((SCServer) server).setConnectionType("netty.tcp");
		server.registerService(host, port65535, serviceNameAlt, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
		assertEquals(true, server.isRegistered(serviceNameAlt));
		server.deregisterService(serviceName);
		server.deregisterService(serviceNameAlt);
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(false, server.isRegistered(serviceNameAlt));
	}

	@Test
	public void registerService_withDifferentConnectionTypesTcpFirst_registeredThenNot()
			throws Exception {
		server.startListener(host, 9001, 0);
		server.registerService(host, port9000, serviceName, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
		assertEquals(false, server.isRegistered(serviceNameAlt));
		((SCServer) server).setConnectionType("netty.http");
		server.registerService(host, port1, serviceNameAlt, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
		assertEquals(true, server.isRegistered(serviceNameAlt));
		server.deregisterService(serviceName);
		server.deregisterService(serviceNameAlt);
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(false, server.isRegistered(serviceNameAlt));
	}

	@Test
	public void registerService_httpConnectionType_registeredThenNot() throws Exception {
		server.startListener(host, 9001, 0);
		((SCServer) server).setConnectionType("netty.http");
		server.registerService(host, port8080, serviceName, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
		assertEquals(false, server.isRegistered(serviceNameAlt));
		server.registerService(host, port1, serviceNameAlt, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
		assertEquals(true, server.isRegistered(serviceNameAlt));
		server.deregisterService(serviceName);
		server.deregisterService(serviceNameAlt);
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(false, server.isRegistered(serviceNameAlt));
	}

	@Test
	public void registerServiceDeregisterService_onTwoSCsHttp_periodicallyRegistered() throws Exception {
		server.startListener(host, 9001, 0);
		((SCServer) server).setConnectionType("netty.http");
		for (int i = 0; i < 100; i++) {
			server.registerService(host, port8080, serviceName, 1, 1, new CallBack());
			server.registerService(host, port1, serviceNameAlt, 1, 1, new CallBack());
			assertEquals(true, server.isRegistered(serviceName));
			assertEquals(true, server.isRegistered(serviceNameAlt));
			server.deregisterService(serviceName);
			server.deregisterService(serviceNameAlt);
			assertEquals(false, server.isRegistered(serviceName));
			assertEquals(false, server.isRegistered(serviceNameAlt));
		}
	}

	@Test
	public void registerServiceDeregisterService_onTwoSCsTcp_periodicallyRegistered() throws Exception {
		server.startListener(host, 9001, 0);
		for (int i = 0; i < 100; i++) {
			server.registerService(host, port9000, serviceName, 1, 1, new CallBack());
			server.registerService(host, port65535, serviceNameAlt, 1, 1, new CallBack());
			assertEquals(true, server.isRegistered(serviceName));
			assertEquals(true, server.isRegistered(serviceNameAlt));
			server.deregisterService(serviceName);
			server.deregisterService(serviceNameAlt);
			assertEquals(false, server.isRegistered(serviceName));
			assertEquals(false, server.isRegistered(serviceNameAlt));
		}
	}
	
	@Test
	public void registerServiceDeregisterService_onTwoSCsBoth_periodicallyRegistered() throws Exception {
		server.startListener(host, 9001, 0);
		for (int i = 0; i < 100; i++) {
			((SCServer) server).setConnectionType("netty.tcp");
			server.registerService(host, port9000, serviceName, 1, 1, new CallBack());
			((SCServer) server).setConnectionType("netty.http");
			server.registerService(host, port1, serviceName, 1, 1, new CallBack());
			assertEquals(true, server.isRegistered(serviceName));
			assertEquals(true, server.isRegistered(serviceName));
			server.deregisterService(serviceName);
			server.deregisterService(serviceName);
			assertEquals(false, server.isRegistered(serviceName));
			assertEquals(false, server.isRegistered(serviceName));
		}
	}

	@Test
	public void registerServiceDeregisterService_onTwoSCsChangingConnectionTypes_periodicallyRegistered() throws Exception {
		server.startListener(host, 9001, 0);
		for (int i = 0; i < 50; i++) {
			server.registerService(host, port9000, serviceName, 1, 1, new CallBack());
			((SCServer) server).setConnectionType("netty.http");
			server.registerService(host, port1, serviceNameAlt, 1, 1, new CallBack());
			assertEquals(true, server.isRegistered(serviceName));
			assertEquals(true, server.isRegistered(serviceNameAlt));
			server.deregisterService(serviceName);
			server.deregisterService(serviceNameAlt);
			assertEquals(false, server.isRegistered(serviceName));
			assertEquals(false, server.isRegistered(serviceNameAlt));
			server.registerService(host, port8080, serviceName, 1, 1, new CallBack());
			((SCServer) server).setConnectionType("netty.tcp");
			server.registerService(host, port65535, serviceNameAlt, 1, 1, new CallBack());
			assertEquals(true, server.isRegistered(serviceName));
			assertEquals(true, server.isRegistered(serviceNameAlt));
			server.deregisterService(serviceName);
			server.deregisterService(serviceNameAlt);
			assertEquals(false, server.isRegistered(serviceName));
			assertEquals(false, server.isRegistered(serviceNameAlt));
			((SCServer) server).setConnectionType("netty.tcp");
		}
	}

	@Test
	public void registerServiceDeregisterService_onTwoSCsChangingServices_periodicallyRegistered() throws Exception {
		server.startListener(host, 9001, 0);
		for (int i = 0; i < 50; i++) {
			server.registerService(host, port9000, serviceName, 1, 1, new CallBack());
			server.registerService(host, port65535, serviceNameAlt, 1, 1, new CallBack());
			assertEquals(true, server.isRegistered(serviceName));
			assertEquals(true, server.isRegistered(serviceNameAlt));
			server.deregisterService(serviceName);
			server.deregisterService(serviceNameAlt);
			assertEquals(false, server.isRegistered(serviceName));
			assertEquals(false, server.isRegistered(serviceNameAlt));
			server.registerService(host, port65535, serviceName, 1, 1, new CallBack());
			server.registerService(host, port9000, serviceNameAlt, 1, 1, new CallBack());
			assertEquals(true, server.isRegistered(serviceName));
			assertEquals(true, server.isRegistered(serviceNameAlt));
			server.deregisterService(serviceName);
			server.deregisterService(serviceNameAlt);
			assertEquals(false, server.isRegistered(serviceName));
			assertEquals(false, server.isRegistered(serviceNameAlt));
		}
	}
	
	private class CallBack implements ISCServerCallback {
	}
}
