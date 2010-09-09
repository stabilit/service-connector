package integration;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.sc.ctrl.util.TestEnvironmentController;
import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.ISCClient;
import com.stabilit.scm.common.service.SCServiceException;

public class AttachDetachClientToSCTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(AttachDetachClientToSCTest.class);
	
	private static Process p;

	private ISCClient client;

	private static final String host = "localhost";
	private static final int port8080 = 8080;
	private static final int port9000 = 9000;

	private static final String log4jSC0Properties = "log4jSC0.properties";
	private static final String scProperties0 = "scIntegration.properties";
	
	private static TestEnvironmentController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
		try {
			p = ctrl.startSC(log4jSC0Properties, scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(p, log4jSC0Properties);
	}
	
	/**
	 * @throws java.lang.Exception
	 * 
	 *             Create a new SCClient for each test method.
	 */
	@Before
	public void setUp() throws Exception {
		client = new SCClient();
	}

	@Test
	public void attach_changesState_initiallyNotAttachedThenAttached() throws Exception {
		assertEquals(false, client.isAttached());
		client.attach(host, port8080);
		assertEquals(true, client.isAttached());
		client.detach();
	}

	@Test
	public void detach_changesState_fromAttachedToNotAttached() throws Exception {
		assertEquals(false, client.isAttached());
		client.attach(host, port8080);
		assertEquals(true, client.isAttached());
		client.detach();
		assertEquals(false, client.isAttached());
	}

	@Test
	public void attach_twiceSameParams_throwsExceptionAttached() throws Exception {
		Exception ex = null;
		client.attach(host, port8080);
		try {
			client.attach(host, port8080);
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
		client.attach(host, port8080);
		((SCClient) client).setConnectionType("netty.tcp");
		try {
			client.attach(host, port9000);
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
		client.attach(host, port9000);
		((SCClient) client).setConnectionType("netty.http");
		try {
			client.attach(host, port8080);
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
			client.attach(host, port8080);
		} finally {
			client.detach();
			assertEquals(false, client.isAttached());
		}
	}

	@Test
	public void detach_validAttachPort9000_notAttached() throws Exception {
		((SCClient) client).setConnectionType("netty.tcp");
		try {
			client.attach(host, port9000);
		} finally {
			client.detach();
			assertEquals(false, client.isAttached());
		}
	}

	@Test
	public void detach_afterDoubleAttemptedAttachDetach_throwsExceptionNotAttached()
			throws Exception {
		Exception ex = null;
		client.attach(host, port8080);
		assertEquals(true, client.isAttached());
		try {
			((SCClient) client).setConnectionType("netty.tcp");
			client.attach(host, port9000);
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
			client.attach(host, port8080);
			client.detach();
		}
		assertEquals(false, client.isAttached());
	}

	@Test
	public void attachDetach_cycle100Times_notAttached() throws Exception {
		for (int i = 0; i < 99; i++) {
			client.attach(host, port8080);
			client.detach();
		}
		client.attach(host, port8080);
		assertEquals(true, client.isAttached());
		client.detach();
		assertEquals(false, client.isAttached());
	}

	@Test
	public void attachDetach_cycle500Times_notAttached() throws Exception {
		for (int i = 0; i < 499; i++) {
			client.attach(host, port8080);
			client.detach();
		}
		client.attach(host, port8080);
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
			System.out.println("Attaching client " + i*10);
			for (int j = 0; j < 10; j++) {
				clients[j + (10 * i)] = new SCClient();
				clients[j + (10 * i)].attach(host, port8080);
			}
		}
		i = 0;
		for (; i < clientsCount; i++) {
			assertEquals(true, clients[i].isAttached());
		}
		i = 0;
		for (; i < clientsCount / 10; i++) {
			System.out.println("Detaching client " + i*10);
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
