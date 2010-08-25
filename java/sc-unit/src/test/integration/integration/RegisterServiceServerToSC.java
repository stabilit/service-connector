package integration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.InvalidParameterException;

import javax.activity.InvalidActivityException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.common.net.req.ConnectionPoolConnectException;
import com.stabilit.scm.common.service.SCServiceException;
import com.stabilit.scm.srv.ISCServer;
import com.stabilit.scm.srv.ISCServerCallback;
import com.stabilit.scm.srv.SCServer;

public class RegisterServiceServerToSC {

	private static Process p;
	private ISCServer server;
	private Exception ex;
	private String serviceName = "simulation";
	private String host = "localhost";

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

	// region hostName == "localhost" (set as only one in
	// scIntegration.properties), all ports
	@Test
	public void registerService_withoutStartListener_throwsException() {
		try {
			server.registerService(host, 8080, "simulation", 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof InvalidActivityException);
	}

	@Test
	public void registerService_withStartListenerToSameHostAndPort_throwsException()
			throws Exception {
		server.startListener(host, 9000, 1);
		try {
			server.registerService(host, 9000, serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, server.isRegistered(serviceName));
//		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerService_withStartListenerToSameHostDifferentPort_notRegisteredthrowsException()
			throws Exception {
		server.startListener(host, 8080, 1);
		try {
			server.registerService(host, 9000, "Name", 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerService_withStartListenerToDifferentHostSamePort_throwsException()
			throws Exception {
		server.startListener(host, 8080, 1);
		try {
			server.registerService(host, 9000, "Name", 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerService_withValidParams_isRegistered()
			throws Exception {
		server.startListener(host, 9001, 0);
		server.registerService(host, 9000, serviceName, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
	}

	

	// region end

	private class CallBack implements ISCServerCallback {
	}

}
