package integration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.ISCClient;
import com.stabilit.scm.common.service.SCServiceException;

public class AttachDetachClientToSCTest {

	private ISCClient client;

	private static final String host = "localhost";
	private static final int port8080 = 8080;
	private static final int port9000 = 9000;

	private static Process p = null;

	private static Runtime r;

	@BeforeClass
	public static void oneTimeSetUp() {
		r = Runtime.getRuntime();
		try {
			// p = r.exec("cmd /c start src\\test\\resources\\startSC.bat");
			String userDir = System.getProperty("user.dir");
			String command = "cmd /c start java -Dlog4j.configuration=file:"
					+ userDir
					+ "\\src\\test\\resources\\log4j.properties -jar "
					+ userDir
					+ "\\..\\service-connector\\target\\sc.jar -filename "
					+ userDir
					+ "\\src\\test\\resources\\scIntegration.properties";
			System.out.println(command);
			p = r.exec(command);
			// lets the SC load before starting communication
			try {
				Thread.sleep(3000);
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
	 * 
	 *             Create a new SCClient for each test method.
	 */
	@Before
	public void setUp() throws Exception {
		client = new SCClient();
	}

	@Test
	public void attach_changesState_initiallyNotAttachedThenAttached()
			throws Exception {
		assertEquals(false, client.isAttached());
		client.attach(host, port8080);
		assertEquals(true, client.isAttached());
	}

	@Test
	public void detach_changesState_fromAttachedToNotAttached()
			throws Exception {
		assertEquals(false, client.isAttached());
		client.attach(host, port8080);
		assertEquals(true, client.isAttached());
		client.detach();
		assertEquals(false, client.isAttached());
	}

	@Test
	public void attach_twiceSameParams_throwsExceptionAttached()
			throws Exception {
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
	public void attach_twiceDifferentParamsHttpFirst_throwsExceptionAttached()
			throws Exception {
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
	public void attach_twiceDifferentParamsTcpFirst_throwsExceptionAttached()
			throws Exception {
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

	// TODO solve the issue of running this together with the rest and failing

	@Test(timeout = 8000)
	public void attachDetach_cycle10Times_notAttached() throws Exception {
		int i = 0;
		try {
			for (; i < 10; i++) {
				System.err.println("Before attach, cycle: " + i);
				client.attach(host, port8080);
				System.err.println("After attach, cycle: " + i);
				client.detach();
			}
			assertEquals(false, client.isAttached());
		} catch (Exception e) {
			System.err.println(e.getMessage());
			printStrackStrace(e);
		}
		System.err.println("Iteration number:\t" + i);
	}

	@Test
	public void attachDetach_cycle100Times_notAttached() throws Exception {
		int i = 0;
		try {
			for (; i < 100; i++) {
				System.err.println("Before attach, cycle: " + i);
				client.attach(host, port8080);
				System.err.println("After attach, cycle: " + i);
				client.detach();
			}
			assertEquals(false, client.isAttached());
		} catch (Exception e) {
			System.err.println(e.getMessage());
			printStrackStrace(e);
		}
		System.err.println("Iteration number:\t" + i);
	}

	@Test
	public void attachDetach_cycle500Times_notAttached() throws Exception {
		int i = 0;
		try {
			for (; i < 500; i++) {
				System.err.println("Before attach, cycle: " + i);
				client.attach(host, port8080);
				System.err.println("After attach, cycle: " + i);
				client.detach();
			}
			assertEquals(false, client.isAttached());
		} catch (Exception e) {
			System.err.println(e.getMessage());
			printStrackStrace(e);
		}
		System.err.println("Iteration number:\t" + i);
	}

	public void printStrackStrace(Exception e) {
		while (e.getCause() != null) {
			System.err.println();
			System.err.println();
			e = (Exception) e.getCause();
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
