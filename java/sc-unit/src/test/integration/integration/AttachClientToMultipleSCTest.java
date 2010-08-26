package integration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.ISCClient;

public class AttachClientToMultipleSCTest {
	private static ISCClient client1;
	private static ISCClient client2;
	private static Process p;
	private static Process r;

	private static final String localhost = "localhost";
	private static final String host = "localhost";
	private static final int port1 = 1;
	private static final int port8080 = 8080;
	private static final int port9000 = 9000;
	private static final int port65535 = 65535;

	@BeforeClass
	public static void oneTimeSetUp() {
		try {
			String userDir = System.getProperty("user.dir");
			String command = "java -Dlog4j.configuration=file:" + userDir
					+ "\\src\\test\\resources\\log4j.properties -jar " + userDir
					+ "\\..\\service-connector\\target\\sc.jar -filename " + userDir
					+ "\\src\\test\\resources\\";

			p = Runtime.getRuntime().exec(command + "scIntegration.properties");
			r = Runtime.getRuntime().exec(command + "scIntegrationChanged.properties");
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
		/*SCClient endClient1 = new SCClient();
		SCClient endClient2 = new SCClient();
		try {
			endClient1.attach(localhost, port8080);
			endClient2.attach(host, port1);
			((SCClient) endClient1).killSC();
			((SCClient) endClient2).killSC();
		} catch (SCServiceException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		p.destroy();
		r.destroy();
	}

	/**
	 * @throws java.lang.Exception
	 * 
	 *             Create a new SCClient for each test method.
	 */
	@Before
	public void setUp() throws Exception {
		client1 = new SCClient();
		client2 = new SCClient();
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
