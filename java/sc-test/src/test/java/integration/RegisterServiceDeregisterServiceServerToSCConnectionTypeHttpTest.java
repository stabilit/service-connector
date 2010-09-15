package integration;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.sc.ctrl.util.TestConstants;
import com.stabilit.sc.ctrl.util.TestEnvironmentController;
import com.stabilit.sc.srv.ISCServer;
import com.stabilit.sc.srv.ISCServerCallback;
import com.stabilit.sc.srv.SCServer;

public class RegisterServiceDeregisterServiceServerToSCConnectionTypeHttpTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(RegisterServiceDeregisterServiceServerToSCConnectionTypeHttpTest.class);
	
	private ISCServer server;

	private static Process p;

	private static TestEnvironmentController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
		try {
			p = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(p, TestConstants.log4jSC0Properties);
		ctrl = null;
		p = null;
	}
	
	@Before
	public void setUp() throws Exception {
		server = new SCServer();
		((SCServer) server).setConnectionType("netty.http");
	}
	
	@After
	public void tearDown() throws Exception {
		server.destroyServer();
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
		server.startListener(TestConstants.HOST, 9001, 1);
		server.deregisterService("Name");
		assertEquals(false, server.isRegistered("Name"));
	}

	@Test
	public void deregisterService_withoutRegisteringServiceNameInSCProps_notRegistered()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 1);
		server.deregisterService(TestConstants.HOST);
		assertEquals(false, server.isRegistered(TestConstants.HOST));
	}

	@Test
	public void deregisterService_withoutRegisteringServicewithNoHost_notRegistered()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 1);
		server.deregisterService(null);
		assertEquals(false, server.isRegistered(null));
	}

	@Test
	public void deregisterService_withoutRegisteringServicewithEmptyHost_notRegistered()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 1);
		server.deregisterService("");
		assertEquals(false, server.isRegistered(""));
	}

	@Test
	public void deregisterService_withoutRegisteringServicewithWhiteSpaceHost_notRegistered()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 1);
		server.deregisterService(" ");
		assertEquals(false, server.isRegistered(" "));
	}

	@Test
	public void deregisterService_afterValidRegister_registeredThenNotRegistered() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 1);
		server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
	}

	@Test
	public void deregisterService_afterValidRegisterDifferentServiceName_registeredThenNotRegistered()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 1);
		server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceNameAlt, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceNameAlt));
		server.deregisterService(TestConstants.serviceNameAlt);
		assertEquals(false, server.isRegistered(TestConstants.serviceNameAlt));
	}
	
	@Test
	public void deregisterService_differentThanRegistered_registered() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 1);
		server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceNameAlt);
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerServiceDeregisterService_cycle500Times_registeredThenNotRegistered()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 100);
		int cycles = 500;
		for (int i = 0; i < cycles / 10; i++) {
			System.out.println("RegisterDeregister service iteration:\t" + i * 10);
			for (int j = 0; j < 10; j++) {
				server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1, 1, new CallBack());
				assertEquals(true, server.isRegistered(TestConstants.serviceName));
				server.deregisterService(TestConstants.serviceName);
				assertEquals(false, server.isRegistered(TestConstants.serviceName));
			}
		}
	}
	
	@Test
	public void registerService_500CyclesWithChangingConnectionType_registeredThenNotRegistered() throws Exception {
		int cycles = 250;
		for (int i = 0; i < cycles / 10; i++) {
			System.out.println("RegisterDeregister changing connection type iteration:\t" + i * 10);
			for (int j = 0; j < 10; j++) {
				server = new SCServer();
				((SCServer) server).setConnectionType("netty.http");
				server.startListener(TestConstants.HOST, 9001, 0);
				server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1, 1, new CallBack());
				assertEquals(true, server.isRegistered(TestConstants.serviceName));
				server.deregisterService(TestConstants.serviceName);
				assertEquals(false, server.isRegistered(TestConstants.serviceName));
				server.destroyServer();
				server = null;
				server = new SCServer();
				((SCServer) server).setConnectionType("netty.tcp");
				server.startListener(TestConstants.HOST, 9001, 0);
				server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 1, 1, new CallBack());
				assertEquals(true, server.isRegistered(TestConstants.serviceName));
				server.deregisterService(TestConstants.serviceName);
				assertEquals(false, server.isRegistered(TestConstants.serviceName));
				server.destroyServer();
			}
		}
	}
	
	// region end

	private class CallBack implements ISCServerCallback {
	}

}
