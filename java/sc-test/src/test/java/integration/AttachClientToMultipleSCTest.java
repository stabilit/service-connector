package integration;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.sc.ctrl.util.TestEnvironmentController;
import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.ISCClient;

public class AttachClientToMultipleSCTest {
	
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(AttachClientToMultipleSCTest.class);
	
	private ISCClient client1;
	private ISCClient client2;
	private static Process p;
	private static Process r;

	private static final String localhost = "localhost";
	private static final String host = "localhost";
	private static final int port1 = 1;
	private static final int port8080 = 8080;
	private static final int port9000 = 9000;
	private static final int port65535 = 65535;

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
		client1 = new SCClient();
		client2 = new SCClient();
	}
	
	@After
	public void tearDown() throws Exception {
		client1 = null;
		client2 = null;
	}

	@Test
	public void attachDetach_changesState_fromNotAttachedToAttachedToBoth() throws Exception {
		assertEquals(false, client1.isAttached());
		assertEquals(false, client2.isAttached());
		client1.attach(localhost, port8080);
		assertEquals(true, client1.isAttached());
		assertEquals(false, client2.isAttached());
		client2.attach(host, port1);
		assertEquals(true, client1.isAttached());
		assertEquals(true, client2.isAttached());
		client1.detach();
		client2.detach();
		assertEquals(false, client1.isAttached());
		assertEquals(false, client2.isAttached());
	}

	@Test
	public void attach_withDifferentConnectionTypesHttpFirst_fromNotAttachedToAttached()
			throws Exception {
		((SCClient) client2).setConnectionType("netty.tcp");
		assertEquals(false, client1.isAttached());
		assertEquals(false, client2.isAttached());
		client1.attach(localhost, port8080);
		assertEquals(true, client1.isAttached());
		assertEquals(false, client2.isAttached());
		client2.attach(host, port65535);
		assertEquals(true, client1.isAttached());
		assertEquals(true, client2.isAttached());
		client1.detach();
		client2.detach();
		assertEquals(false, client1.isAttached());
		assertEquals(false, client2.isAttached());
	}

	@Test
	public void attach_withDifferentConnectionTypesTcpFirst_fromNotAttachedToAttached()
			throws Exception {
		((SCClient) client1).setConnectionType("netty.tcp");
		assertEquals(false, client1.isAttached());
		assertEquals(false, client2.isAttached());
		client1.attach(localhost, port9000);
		assertEquals(true, client1.isAttached());
		assertEquals(false, client2.isAttached());
		client2.attach(host, port1);
		assertEquals(true, client1.isAttached());
		assertEquals(true, client2.isAttached());
		client1.detach();
		client2.detach();
		assertEquals(false, client1.isAttached());
		assertEquals(false, client2.isAttached());
	}

	@Test
	public void attach_tcpConnectionType_fromNotAttachedToAttached() throws Exception {
		((SCClient) client1).setConnectionType("netty.tcp");
		((SCClient) client2).setConnectionType("netty.tcp");
		assertEquals(false, client1.isAttached());
		assertEquals(false, client2.isAttached());
		client1.attach(localhost, port9000);
		assertEquals(true, client1.isAttached());
		assertEquals(false, client2.isAttached());
		client2.attach(host, port65535);
		assertEquals(true, client1.isAttached());
		assertEquals(true, client2.isAttached());
		client1.detach();
		client2.detach();
		assertEquals(false, client1.isAttached());
		assertEquals(false, client2.isAttached());
	}

	@Test
	public void attachDetach_onTwoSCsHttp_periodicallyAttached() throws Exception {
		for (int i = 0; i < 100; i++) {
			client1.attach(localhost, port8080);
			client2.attach(host, port1);
			assertEquals(true, client1.isAttached());
			assertEquals(true, client2.isAttached());
			client1.detach();
			client2.detach();
			assertEquals(false, client1.isAttached());
			assertEquals(false, client2.isAttached());
		}
	}

	@Test
	public void attachDetach_onTwoSCsTcp_periodicallyAttached() throws Exception {
		((SCClient) client1).setConnectionType("netty.tcp");
		((SCClient) client2).setConnectionType("netty.tcp");
		for (int i = 0; i < 100; i++) {
			client1.attach(localhost, port9000);
			client2.attach(host, port65535);
			assertEquals(true, client1.isAttached());
			assertEquals(true, client2.isAttached());
			client1.detach();
			client2.detach();
			assertEquals(false, client1.isAttached());
			assertEquals(false, client2.isAttached());
		}
	}
	
	@Test
	public void attachDetach_onTwoSCsBoth_periodicallyAttached() throws Exception {
		((SCClient) client1).setConnectionType("netty.tcp");
		for (int i = 0; i < 100; i++) {
			client1.attach(localhost, port9000);
			client2.attach(host, port1);
			assertEquals(true, client1.isAttached());
			assertEquals(true, client2.isAttached());
			client1.detach();
			client2.detach();
			assertEquals(false, client1.isAttached());
			assertEquals(false, client2.isAttached());
		}
	}

	@Test
	public void attachDetach_onTwoSCsChangingTypes_periodicallyAttached() throws Exception {
		((SCClient) client1).setConnectionType("netty.tcp");
		for (int i = 0; i < 50; i++) {
			client1.attach(localhost, port9000);
			client2.attach(host, port1);
			assertEquals(true, client1.isAttached());
			assertEquals(true, client2.isAttached());
			client1.detach();
			client2.detach();
			assertEquals(false, client1.isAttached());
			assertEquals(false, client2.isAttached());
			((SCClient) client1).setConnectionType("netty.http");
			((SCClient) client2).setConnectionType("netty.tcp");
			client1.attach(localhost, port8080);
			client2.attach(host, port65535);
			assertEquals(true, client1.isAttached());
			assertEquals(true, client2.isAttached());
			client1.detach();
			client2.detach();
			assertEquals(false, client1.isAttached());
			assertEquals(false, client2.isAttached());
			((SCClient) client1).setConnectionType("netty.tcp");
			((SCClient) client2).setConnectionType("netty.http");
		}
	}

	@Test
	public void attachDetach_onTwoSCsChangingSCs_periodicallyAttached() throws Exception {
		for (int i = 0; i < 50; i++) {
			client1.attach(localhost, port8080);
			client2.attach(host, port1);
			assertEquals(true, client1.isAttached());
			assertEquals(true, client2.isAttached());
			client1.detach();
			client2.detach();
			assertEquals(false, client1.isAttached());
			assertEquals(false, client2.isAttached());
			client1.attach(localhost, port1);
			client2.attach(host, port8080);
			assertEquals(true, client1.isAttached());
			assertEquals(true, client2.isAttached());
			client1.detach();
			client2.detach();
			assertEquals(false, client1.isAttached());
			assertEquals(false, client2.isAttached());
		}
	}
}
