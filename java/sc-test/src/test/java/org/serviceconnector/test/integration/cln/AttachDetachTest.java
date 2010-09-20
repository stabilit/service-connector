package org.serviceconnector.test.integration.cln;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.cln.ISCClient;
import org.serviceconnector.cln.SCClient;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.ctrl.util.TestEnvironmentController;
import org.serviceconnector.sc.service.SCServiceException;


public class AttachDetachTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(AttachDetachTest.class);
	
	private int threadCount = 0;

	private ISCClient client;

	private static TestEnvironmentController ctrl;
	private static Process scProcess;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSC0Properties);
		ctrl = null;
		scProcess = null;
	}
	
	@Before
	public void setUp() {
		threadCount = Thread.activeCount();
		client = new SCClient();
	}
	
	@After
	public void tearDown() {
		client = null;
		assertEquals(threadCount, Thread.activeCount());
	}

	@Test
	public void attach_changesState_initiallyNotAttachedThenAttached() throws Exception {
		assertEquals(false, client.isAttached());
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		assertEquals(true, client.isAttached());
		client.detach();
	}

	@Test
	public void detach_changesState_fromAttachedToNotAttached() throws Exception {
		assertEquals(false, client.isAttached());
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		assertEquals(true, client.isAttached());
		client.detach();
		assertEquals(false, client.isAttached());
	}

	@Test
	public void attach_twiceSameParams_throwsExceptionAttached() throws Exception {
		Exception ex = null;
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		try {
			client.attach(TestConstants.HOST, TestConstants.PORT8080);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, client.isAttached());
		client.detach();
	}

	@Test
	public void attach_twiceDifferentParamsHttpFirst_throwsExceptionAttached() throws Exception {
		Exception ex = null;
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		((SCClient) client).setConnectionType("netty.tcp");
		try {
			client.attach(TestConstants.HOST, TestConstants.PORT9000);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, client.isAttached());
		client.detach();
	}

	@Test
	public void attach_twiceDifferentParamsTcpFirst_throwsExceptionAttached() throws Exception {
		Exception ex = null;
		((SCClient) client).setConnectionType("netty.tcp");
		client.attach(TestConstants.HOST, TestConstants.PORT9000);
		((SCClient) client).setConnectionType("netty.http");
		try {
			client.attach(TestConstants.HOST, TestConstants.PORT8080);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, client.isAttached());
		client.detach();
	}

	@Test
	public void detach_withoutAttach_notAttached() throws Exception {
		client.detach();
		assertEquals(false, client.isAttached());
	}

	@Test
	public void detach_validAttachPort8080_notAttached() throws Exception {
		try {
			client.attach(TestConstants.HOST, TestConstants.PORT8080);
		} finally {
			client.detach();
			assertEquals(false, client.isAttached());
		}
	}

	@Test
	public void detach_validAttachPort9000_notAttached() throws Exception {
		((SCClient) client).setConnectionType("netty.tcp");
		try {
			client.attach(TestConstants.HOST, TestConstants.PORT9000);
		} finally {
			client.detach();
			assertEquals(false, client.isAttached());
		}
	}

	@Test
	public void detach_afterDoubleAttemptedAttachDetach_throwsExceptionNotAttached()
			throws Exception {
		Exception ex = null;
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		assertEquals(true, client.isAttached());
		try {
			((SCClient) client).setConnectionType("netty.tcp");
			client.attach(TestConstants.HOST, TestConstants.PORT9000);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, client.isAttached());
		client.detach();
		assertEquals(false, client.isAttached());
	}

	@Test
	public void attachDetach_cycle10Times_notAttached() throws Exception {
		for (int i = 0; i < 10; i++) {
			client.attach(TestConstants.HOST, TestConstants.PORT8080);
			Thread.sleep(1000);
			client.detach();
			Thread.sleep(1000);
		}
		assertEquals(false, client.isAttached());
	}

	@Test
	public void attachDetach_cycle100Times_notAttached() throws Exception {
		for (int i = 0; i < 99; i++) {
			client.attach(TestConstants.HOST, TestConstants.PORT8080);
			client.detach();
		}
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		assertEquals(true, client.isAttached());
		client.detach();
		assertEquals(false, client.isAttached());
	}

	@Test
	public void attachDetach_cycle500Times_notAttached() throws Exception {
		for (int i = 0; i < 499; i++) {
			client.attach(TestConstants.HOST, TestConstants.PORT8080);
			client.detach();
		}
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
		assertEquals(true, client.isAttached());
		client.detach();
		assertEquals(false, client.isAttached());
	}

	@Test
	public void attach_1000ClientsAttachedBeforeDetach_allAttached() throws Exception {
		int clientsCount = 1000;
		ISCClient[] clients = new SCClient[clientsCount];
		int i = 0;
		for (; i < clientsCount / 10; i++) {
//			TODO FJU either delete or comment out all outputs to console from sc-test 
//			System.out.println("Attaching client " + i*10);
			for (int j = 0; j < 10; j++) {
				clients[j + (10 * i)] = new SCClient();
				clients[j + (10 * i)].attach(TestConstants.HOST, TestConstants.PORT8080);
			}
		}
		i = 0;
		for (; i < clientsCount; i++) {
			assertEquals(true, clients[i].isAttached());
		}
		i = 0;
		for (; i < clientsCount / 10; i++) {
//			System.out.println("Detaching client " + i*10);
			for (int j = 0; j < 10; j++) {
				clients[j + (10 * i)].detach();
			}
		}
		i = 0;
		for (; i < clientsCount; i++) {
			assertEquals(false, clients[i].isAttached());
		}
	}
}
