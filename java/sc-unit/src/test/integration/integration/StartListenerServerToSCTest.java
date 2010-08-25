package integration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.net.req.ConnectionPoolConnectException;
import com.stabilit.scm.common.service.SCServiceException;
import com.stabilit.scm.srv.SCServer;

public class StartListenerServerToSCTest {

	private static Process p;
	private SCServer server;
	private Exception ex;

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
	public void startListener_localhost8080_startListenered() throws Exception {
		server.startListener("localhost", 8080, 0);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPort9000_listening() throws Exception {
		server.startListener("localhost", 9000, 0);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_changeConnectionTypeHostLocalhostPort9000_listening()
			throws Exception {
		((SCServer) server).setConnectionType("netty.tcp");
		server.startListener("localhost", 9000, 0);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPort0_listening() throws Exception {
		server.startListener("localhost", 0, 0);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPortMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", -1, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostLocalhostPort1_listening() throws Exception {
		server.startListener("localhost", 1, 0);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPortMaxAllowed_listening() throws Exception {
		server.startListener("localhost", 0xFFFF, 0);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPortMaxAllowedPlus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 0xFFFF + 1, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostLocalhostPortIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", Integer.MIN_VALUE, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostLocalhostPortIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", Integer.MAX_VALUE, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region hostName == "null", all ports

	@Test
	public void startListener_hostNullPort8080_notListeningThrowsException() throws Exception {
		try {
			server.startListener(null, 8080, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort9000_notListeningThrowsException() throws Exception {
		try {
			server.startListener(null, 9000, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort0_notListeningThrowsException() throws Exception {
		try {
			server.startListener(null, 0, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortMinus1_notListeningThrowsException() throws Exception {
		try {
			server.startListener(null, -1, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort1_notListeningThrowsException() throws Exception {
		try {
			server.startListener(null, 1, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortMaxAllowed_notListeningThrowsException() throws Exception {
		try {
			server.startListener(null, 0xFFFF, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortMaxAllowedPlus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 0xFFFF + 1, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortIntMin_notListeningThrowsException() throws Exception {
		try {
			server.startListener(null, Integer.MIN_VALUE, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortIntMax_notListeningThrowsException() throws Exception {
		try {
			server.startListener(null, Integer.MAX_VALUE, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	// region end
	// region hostName == "", all ports

	@Test
	public void startListener_hostEmptyPort8080_listening() throws Exception {
		server.startListener("", 8080, 0);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPort9000_listening() throws Exception {
		server.startListener("", 9000, 0);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPort0_listening() throws Exception {
		server.startListener("", 0, 0);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPortMinus1_notListeningThrowsException() throws Exception {
		try {
			server.startListener("", -1, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostEmptyPort1_listening() throws Exception {
		server.startListener("", 1, 0);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPortMaxAllowed_listening() throws Exception {
		server.startListener("", 0xFFFF, 0);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPortMaxAllowedPlus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 0xFFFF + 1, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostEmptyPortIntMin_notListeningThrowsException() throws Exception {
		try {
			server.startListener("", Integer.MIN_VALUE, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostEmptyPortIntMax_notListeningThrowsException() throws Exception {
		try {
			server.startListener("", Integer.MAX_VALUE, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region hostName == "a", all ports

	@Test
	public void startListener_hostAPort8080_listening() throws Exception {
		server.startListener("a", 8080, 0);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostAPort9000_listening() throws Exception {
		server.startListener("a", 9000, 0);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostAPort0_listening() throws Exception {
		server.startListener("a", 0, 0);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostAPortMinus1_notListeningThrowsException() throws Exception {
		try {
			server.startListener("a", -1, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostAPort1_listening() throws Exception {
		server.startListener("a", 1, 0);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostAPortMaxAllowed_listening() throws Exception {
		server.startListener("a", 0xFFFF, 0);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostAPortMaxAllowedPlus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 0xFFFF + 1, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostAPortIntMin_notListeningThrowsException() throws Exception {
		try {
			server.startListener("a", Integer.MIN_VALUE, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostAPortIntMax_notListeningThrowsException() throws Exception {
		try {
			server.startListener("a", Integer.MAX_VALUE, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region hostName == "The quick brown fox jumps over a lazy dog.", all
	// ports

	@Test
	public void startListener_hostArbitraryPort8080_listening() throws Exception {
		server.startListener("The quick brown fox jumps over a lazy dog.", 8080, 0);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPort9000_listening() throws Exception {
		server.startListener("The quick brown fox jumps over a lazy dog.", 9000, 0);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPort0_listening() throws Exception {
		server.startListener("The quick brown fox jumps over a lazy dog.", 0, 0);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPortMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", -1, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostArbitraryPort1_listening() throws Exception {
		server.startListener("The quick brown fox jumps over a lazy dog.", 1, 0);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPortMaxAllowed_listening() throws Exception {
		server.startListener("The quick brown fox jumps over a lazy dog.", 0xFFFF, 0);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPortMaxAllowedPlus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 0xFFFF + 1, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostArbitraryPortIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", Integer.MIN_VALUE,
							0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostArbitraryPortIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", Integer.MAX_VALUE,
							0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region startListener with 3 parameters

}
