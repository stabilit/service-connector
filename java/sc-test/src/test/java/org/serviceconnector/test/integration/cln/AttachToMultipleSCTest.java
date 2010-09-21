package org.serviceconnector.test.integration.cln;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.api.cln.ISCClient;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.ctrl.util.TestEnvironmentController;


public class AttachToMultipleSCTest {
	
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(AttachToMultipleSCTest.class);
	
	private int threadCount = 0;
	private ISCClient client1;
	private ISCClient client2;
	private static Process scProcess0;
	private static Process scProcess1;

	private static TestEnvironmentController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
		try {
			scProcess0 = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
			scProcess1 = ctrl.startSC(TestConstants.log4jSC1Properties, TestConstants.scProperties1);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(scProcess0, TestConstants.log4jSC0Properties);
		ctrl.stopProcess(scProcess1, TestConstants.log4jSC1Properties);
		ctrl = null;
		scProcess0 = null;
		scProcess1 = null;
	}

	@Before
	public void setUp() throws Exception {
		threadCount = Thread.activeCount();
		client1 = new SCClient();
		client2 = new SCClient();
		
	}
	
	@After
	public void tearDown() throws Exception {
		client1 = null;
		client2 = null;
		assertEquals("number of threads", threadCount, Thread.activeCount());
	}

	@Test
	public void attachDetach_changesState_fromNotAttachedToAttachedToBoth() throws Exception {
		assertEquals(false, client1.isAttached());
		assertEquals(false, client2.isAttached());
		client1.attach(TestConstants.LOCALHOST, TestConstants.PORT8080);
		assertEquals(true, client1.isAttached());
		assertEquals(false, client2.isAttached());
		client2.attach(TestConstants.HOST, TestConstants.PORT1);
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
		client1.attach(TestConstants.LOCALHOST, TestConstants.PORT8080);
		assertEquals(true, client1.isAttached());
		assertEquals(false, client2.isAttached());
		client2.attach(TestConstants.HOST, TestConstants.PORT65535);
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
		client1.attach(TestConstants.LOCALHOST, TestConstants.PORT9000);
		assertEquals(true, client1.isAttached());
		assertEquals(false, client2.isAttached());
		client2.attach(TestConstants.HOST, TestConstants.PORT1);
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
		client1.attach(TestConstants.LOCALHOST, TestConstants.PORT9000);
		assertEquals(true, client1.isAttached());
		assertEquals(false, client2.isAttached());
		client2.attach(TestConstants.HOST, TestConstants.PORT65535);
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
			client1.attach(TestConstants.LOCALHOST, TestConstants.PORT8080);
			client2.attach(TestConstants.HOST, TestConstants.PORT1);
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
			client1.attach(TestConstants.LOCALHOST, TestConstants.PORT9000);
			client2.attach(TestConstants.HOST, TestConstants.PORT65535);
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
			client1.attach(TestConstants.LOCALHOST, TestConstants.PORT9000);
			client2.attach(TestConstants.HOST, TestConstants.PORT1);
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
			client1.attach(TestConstants.LOCALHOST, TestConstants.PORT9000);
			client2.attach(TestConstants.HOST, TestConstants.PORT1);
			assertEquals(true, client1.isAttached());
			assertEquals(true, client2.isAttached());
			client1.detach();
			client2.detach();
			assertEquals(false, client1.isAttached());
			assertEquals(false, client2.isAttached());
			((SCClient) client1).setConnectionType("netty.http");
			((SCClient) client2).setConnectionType("netty.tcp");
			client1.attach(TestConstants.LOCALHOST, TestConstants.PORT8080);
			client2.attach(TestConstants.HOST, TestConstants.PORT65535);
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
			client1.attach(TestConstants.LOCALHOST, TestConstants.PORT8080);
			client2.attach(TestConstants.HOST, TestConstants.PORT1);
			assertEquals(true, client1.isAttached());
			assertEquals(true, client2.isAttached());
			client1.detach();
			client2.detach();
			assertEquals(false, client1.isAttached());
			assertEquals(false, client2.isAttached());
			client1.attach(TestConstants.LOCALHOST, TestConstants.PORT1);
			client2.attach(TestConstants.HOST, TestConstants.PORT8080);
			assertEquals(true, client1.isAttached());
			assertEquals(true, client2.isAttached());
			client1.detach();
			client2.detach();
			assertEquals(false, client1.isAttached());
			assertEquals(false, client2.isAttached());
		}
	}
}
