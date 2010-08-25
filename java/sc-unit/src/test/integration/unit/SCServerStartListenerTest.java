package unit;

import static org.junit.Assert.assertEquals;

import java.security.InvalidParameterException;

import javax.activity.InvalidActivityException;

import org.junit.Before;
import org.junit.Test;

import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.srv.ISCServer;
import com.stabilit.scm.srv.SCServer;

public class SCServerStartListenerTest {

	private ISCServer server;
	private Exception ex;

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
	public void startListener_localhost8080KeepAlive0_startListenered() throws Exception {
		server.startListener("localhost", 8080, 0);
		assertEquals(true, server.isListening());
		assertEquals("localhost", server.getHost());
		assertEquals(8080, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostLocalhostPort9000KeepAlive0_listening() throws Exception {
		server.startListener("localhost", 9000, 0);
		assertEquals(true, server.isListening());
		assertEquals("localhost", server.getHost());
		assertEquals(9000, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_changeConnectionTypeHostLocalhostPort9000KeepAlive0_listening()
			throws Exception {
		((SCServer) server).setConnectionType("netty.tcp");
		server.startListener("localhost", 9000, 0);
		assertEquals(true, server.isListening());
		assertEquals("localhost", server.getHost());
		assertEquals(9000, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostLocalhostPort0KeepAlive0_listening() throws Exception {
		server.startListener("localhost", 0, 0);
		assertEquals(true, server.isListening());
		assertEquals("localhost", server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostLocalhostPortMinus1KeepAlive0_notListeningThrowsException()
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
	public void startListener_hostLocalhostPort1KeepAlive0_listening() throws Exception {
		server.startListener("localhost", 1, 0);
		assertEquals(true, server.isListening());
		assertEquals("localhost", server.getHost());
		assertEquals(1, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostLocalhostPortMaxAllowedKeepAlive0_listening() throws Exception {
		server.startListener("localhost", 0xFFFF, 0);
		assertEquals(true, server.isListening());
		assertEquals("localhost", server.getHost());
		assertEquals(0xFFFF, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostLocalhostPortMaxAllowedPlus1KeepAlive0_notListeningThrowsException()
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
	public void startListener_hostLocalhostPortIntMinKeepAlive0_notListeningThrowsException()
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
	public void startListener_hostLocalhostPortIntMaxKeepAlive0_notListeningThrowsException()
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
	public void startListener_hostNullPort8080KeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 8080, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort9000KeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 9000, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort0KeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 0, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortMinus1KeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, -1, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort1KeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 1, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortMaxAllowedKeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 0xFFFF, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortMaxAllowedPlus1KeepAlive0_notListeningThrowsException()
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
	public void startListener_hostNullPortIntMinKeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, Integer.MIN_VALUE, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortIntMaxKeepAlive0_notListeningThrowsException()
			throws Exception {
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
	public void startListener_hostEmptyPort8080KeepAlive0_listening() throws Exception {
		server.startListener("", 8080, 0);
		assertEquals(true, server.isListening());
		assertEquals("", server.getHost());
		assertEquals(8080, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostEmptyPort9000KeepAlive0_listening() throws Exception {
		server.startListener("", 9000, 0);
		assertEquals(true, server.isListening());
		assertEquals("", server.getHost());
		assertEquals(9000, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostEmptyPort0KeepAlive0_listening() throws Exception {
		server.startListener("", 0, 0);
		assertEquals(true, server.isListening());
		assertEquals("", server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostEmptyPortMinus1KeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", -1, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostEmptyPort1KeepAlive0_listening() throws Exception {
		server.startListener("", 1, 0);
		assertEquals(true, server.isListening());
		assertEquals("", server.getHost());
		assertEquals(1, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostEmptyPortMaxAllowedKeepAlive0_listening() throws Exception {
		server.startListener("", 0xFFFF, 0);
		assertEquals(true, server.isListening());
		assertEquals("", server.getHost());
		assertEquals(0xFFFF, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostEmptyPortMaxAllowedPlus1KeepAlive0_notListeningThrowsException()
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
	public void startListener_hostEmptyPortIntMinKeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", Integer.MIN_VALUE, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostEmptyPortIntMaxKeepAlive0_notListeningThrowsException()
			throws Exception {
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
	public void startListener_hostAPort8080KeepAlive0_listening() throws Exception {
		server.startListener("a", 8080, 0);
		assertEquals(true, server.isListening());
		assertEquals("a", server.getHost());
		assertEquals(8080, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostAPort9000KeepAlive0_listening() throws Exception {
		server.startListener("a", 9000, 0);
		assertEquals(true, server.isListening());
		assertEquals("a", server.getHost());
		assertEquals(9000, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostAPort0KeepAlive0_listening() throws Exception {
		server.startListener("a", 0, 0);
		assertEquals(true, server.isListening());
		assertEquals("a", server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostAPortMinus1KeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", -1, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostAPort1KeepAlive0_listening() throws Exception {
		server.startListener("a", 1, 0);
		assertEquals(true, server.isListening());
		assertEquals("a", server.getHost());
		assertEquals(1, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostAPortMaxAllowedKeepAlive0_listening() throws Exception {
		server.startListener("a", 0xFFFF, 0);
		assertEquals(true, server.isListening());
		assertEquals("a", server.getHost());
		assertEquals(0xFFFF, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostAPortMaxAllowedPlus1KeepAlive0_notListeningThrowsException()
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
	public void startListener_hostAPortIntMinKeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", Integer.MIN_VALUE, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostAPortIntMaxKeepAlive0_notListeningThrowsException()
			throws Exception {
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
	public void startListener_hostArbitraryPort8080KeepAlive0_listening() throws Exception {
		server.startListener("The quick brown fox jumps over a lazy dog.", 8080, 0);
		assertEquals(true, server.isListening());
		assertEquals("The quick brown fox jumps over a lazy dog.", server.getHost());
		assertEquals(8080, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostArbitraryPort9000KeepAlive0_listening() throws Exception {
		server.startListener("The quick brown fox jumps over a lazy dog.", 9000, 0);
		assertEquals(true, server.isListening());
		assertEquals("The quick brown fox jumps over a lazy dog.", server.getHost());
		assertEquals(9000, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostArbitraryPort0KeepAlive0_listening() throws Exception {
		server.startListener("The quick brown fox jumps over a lazy dog.", 0, 0);
		assertEquals(true, server.isListening());
		assertEquals("The quick brown fox jumps over a lazy dog.", server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostArbitraryPortMinus1KeepAlive0_notListeningThrowsException()
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
	public void startListener_hostArbitraryPort1KeepAlive0_listening() throws Exception {
		server.startListener("The quick brown fox jumps over a lazy dog.", 1, 0);
		assertEquals(true, server.isListening());
		assertEquals("The quick brown fox jumps over a lazy dog.", server.getHost());
		assertEquals(1, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostArbitraryPortMaxAllowedKeepAlive0_listening() throws Exception {
		server.startListener("The quick brown fox jumps over a lazy dog.", 0xFFFF, 0);
		assertEquals(true, server.isListening());
		assertEquals("The quick brown fox jumps over a lazy dog.", server.getHost());
		assertEquals(0xFFFF, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostArbitraryPortMaxAllowedPlus1KeepAlive0_notListeningThrowsException()
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
	public void startListener_hostArbitraryPortIntMinKeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server
					.startListener("The quick brown fox jumps over a lazy dog.", Integer.MIN_VALUE,
							0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostArbitraryPortIntMaxKeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server
					.startListener("The quick brown fox jumps over a lazy dog.", Integer.MAX_VALUE,
							0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region hostName == "localhost" (set as only one in
	// scIntegration.properties), all ports
	@Test
	public void startListener_localhost8080KeepAlive3600_startListenered() throws Exception {
		server.startListener("localhost", 8080, 3600);
		assertEquals(true, server.isListening());
		assertEquals("localhost", server.getHost());
		assertEquals(8080, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostLocalhostPort9000KeepAlive3600_listening() throws Exception {
		server.startListener("localhost", 9000, 3600);
		assertEquals(true, server.isListening());
		assertEquals("localhost", server.getHost());
		assertEquals(9000, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_changeConnectionTypeHostLocalhostPort9000KeepAlive3600_listening()
			throws Exception {
		((SCServer) server).setConnectionType("netty.tcp");
		server.startListener("localhost", 9000, 3600);
		assertEquals(true, server.isListening());
		assertEquals("localhost", server.getHost());
		assertEquals(9000, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostLocalhostPort0KeepAlive3600_listening() throws Exception {
		server.startListener("localhost", 0, 3600);
		assertEquals(true, server.isListening());
		assertEquals("localhost", server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostLocalhostPortMinus1KeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", -1, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostLocalhostPort1KeepAlive3600_listening() throws Exception {
		server.startListener("localhost", 1, 3600);
		assertEquals(true, server.isListening());
		assertEquals("localhost", server.getHost());
		assertEquals(1, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostLocalhostPortMaxAllowedKeepAlive3600_listening() throws Exception {
		server.startListener("localhost", 0xFFFF, 3600);
		assertEquals(true, server.isListening());
		assertEquals("localhost", server.getHost());
		assertEquals(0xFFFF, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostLocalhostPortMaxAllowedPlus1KeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 0xFFFF + 1, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostLocalhostPortIntMinKeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", Integer.MIN_VALUE, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostLocalhostPortIntMaxKeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", Integer.MAX_VALUE, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region hostName == "null", all ports

	@Test
	public void startListener_hostNullPort8080KeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 8080, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort9000KeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 9000, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort0KeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 0, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortMinus1KeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, -1, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort1KeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 1, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortMaxAllowedKeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 0xFFFF, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortMaxAllowedPlus1KeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 0xFFFF + 1, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortIntMinKeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, Integer.MIN_VALUE, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortIntMaxKeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, Integer.MAX_VALUE, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	// region end
	// region hostName == "", all ports

	@Test
	public void startListener_hostEmptyPort8080KeepAlive3600_listening() throws Exception {
		server.startListener("", 8080, 3600);
		assertEquals(true, server.isListening());
		assertEquals("", server.getHost());
		assertEquals(8080, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostEmptyPort9000KeepAlive3600_listening() throws Exception {
		server.startListener("", 9000, 3600);
		assertEquals(true, server.isListening());
		assertEquals("", server.getHost());
		assertEquals(9000, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostEmptyPort0KeepAlive3600_listening() throws Exception {
		server.startListener("", 0, 3600);
		assertEquals(true, server.isListening());
		assertEquals("", server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostEmptyPortMinus1KeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", -1, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostEmptyPort1KeepAlive3600_listening() throws Exception {
		server.startListener("", 1, 3600);
		assertEquals(true, server.isListening());
		assertEquals("", server.getHost());
		assertEquals(1, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostEmptyPortMaxAllowedKeepAlive3600_listening() throws Exception {
		server.startListener("", 0xFFFF, 3600);
		assertEquals(true, server.isListening());
		assertEquals("", server.getHost());
		assertEquals(0xFFFF, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostEmptyPortMaxAllowedPlus1KeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 0xFFFF + 1, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostEmptyPortIntMinKeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", Integer.MIN_VALUE, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostEmptyPortIntMaxKeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", Integer.MAX_VALUE, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region hostName == "a", all ports

	@Test
	public void startListener_hostAPort8080KeepAlive3600_listening() throws Exception {
		server.startListener("a", 8080, 3600);
		assertEquals(true, server.isListening());
		assertEquals("a", server.getHost());
		assertEquals(8080, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostAPort9000KeepAlive3600_listening() throws Exception {
		server.startListener("a", 9000, 3600);
		assertEquals(true, server.isListening());
		assertEquals("a", server.getHost());
		assertEquals(9000, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostAPort0KeepAlive3600_listening() throws Exception {
		server.startListener("a", 0, 3600);
		assertEquals(true, server.isListening());
		assertEquals("a", server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostAPortMinus1KeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", -1, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostAPort1KeepAlive3600_listening() throws Exception {
		server.startListener("a", 1, 3600);
		assertEquals(true, server.isListening());
		assertEquals("a", server.getHost());
		assertEquals(1, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostAPortMaxAllowedKeepAlive3600_listening() throws Exception {
		server.startListener("a", 0xFFFF, 3600);
		assertEquals(true, server.isListening());
		assertEquals("a", server.getHost());
		assertEquals(0xFFFF, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostAPortMaxAllowedPlus1KeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 0xFFFF + 1, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostAPortIntMinKeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", Integer.MIN_VALUE, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostAPortIntMaxKeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", Integer.MAX_VALUE, 3600);
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
	public void startListener_hostArbitraryPort8080KeepAlive3600_listening() throws Exception {
		server.startListener("The quick brown fox jumps over a lazy dog.", 8080, 3600);
		assertEquals(true, server.isListening());
		assertEquals("The quick brown fox jumps over a lazy dog.", server.getHost());
		assertEquals(8080, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostArbitraryPort9000KeepAlive3600_listening() throws Exception {
		server.startListener("The quick brown fox jumps over a lazy dog.", 9000, 3600);
		assertEquals(true, server.isListening());
		assertEquals("The quick brown fox jumps over a lazy dog.", server.getHost());
		assertEquals(9000, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostArbitraryPort0KeepAlive3600_listening() throws Exception {
		server.startListener("The quick brown fox jumps over a lazy dog.", 0, 3600);
		assertEquals(true, server.isListening());
		assertEquals("The quick brown fox jumps over a lazy dog.", server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostArbitraryPortMinus1KeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", -1, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostArbitraryPort1KeepAlive3600_listening() throws Exception {
		server.startListener("The quick brown fox jumps over a lazy dog.", 1, 3600);
		assertEquals(true, server.isListening());
		assertEquals("The quick brown fox jumps over a lazy dog.", server.getHost());
		assertEquals(1, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostArbitraryPortMaxAllowedKeepAlive3600_listening() throws Exception {
		server.startListener("The quick brown fox jumps over a lazy dog.", 0xFFFF, 3600);
		assertEquals(true, server.isListening());
		assertEquals("The quick brown fox jumps over a lazy dog.", server.getHost());
		assertEquals(0xFFFF, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostArbitraryPortMaxAllowedPlus1KeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 0xFFFF + 1, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostArbitraryPortIntMinKeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", Integer.MIN_VALUE,
					3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostArbitraryPortIntMaxKeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", Integer.MAX_VALUE,
					3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region hostName == "localhost" (set as only one in
	// scIntegration.properties), all ports
	@Test
	public void startListener_localhost8080KeepAliveMinus1_startListenered() throws Exception {
		try {
			server.startListener("localhost", 8080, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPort9000KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 9000, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_changeConnectionTypeHostLocalhostPort9000KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		((SCServer) server).setConnectionType("netty.tcp");
		try {
			server.startListener("localhost", 9000, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPort0KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 0, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPortMinus1KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", -1, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostLocalhostPort1KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 1, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPortMaxAllowedKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 0xFFFF, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPortMaxAllowedPlus1KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 0xFFFF + 1, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostLocalhostPortIntMinKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", Integer.MIN_VALUE, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostLocalhostPortIntMaxKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", Integer.MAX_VALUE, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region hostName == "null", all ports

	@Test
	public void startListener_hostNullPort8080KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 8080, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort9000KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 9000, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort0KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 0, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortMinus1KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, -1, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort1KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 1, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortMaxAllowedKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 0xFFFF, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortMaxAllowedPlus1KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 0xFFFF + 1, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortIntMinKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, Integer.MIN_VALUE, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortIntMaxKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, Integer.MAX_VALUE, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	// region end
	// region hostName == "", all ports

	@Test
	public void startListener_hostEmptyPort8080KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 8080, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPort9000KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 9000, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPort0KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 0, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPortMinus1KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", -1, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostEmptyPort1KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 1, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPortMaxAllowedKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 0xFFFF, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPortMaxAllowedPlus1KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 0xFFFF + 1, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostEmptyPortIntMinKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", Integer.MIN_VALUE, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostEmptyPortIntMaxKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", Integer.MAX_VALUE, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region hostName == "a", all ports

	@Test
	public void startListener_hostAPort8080KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 8080, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPort9000KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 9000, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPort0KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 0, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPortMinus1KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", -1, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostAPort1KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 1, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPortMaxAllowedKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 0xFFFF, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPortMaxAllowedPlus1KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 0xFFFF + 1, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostAPortIntMinKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", Integer.MIN_VALUE, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostAPortIntMaxKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", Integer.MAX_VALUE, -1);
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
	public void startListener_hostArbitraryPort8080KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 8080, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPort9000KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 9000, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPort0KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 0, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPortMinus1KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", -1, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostArbitraryPort1KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 1, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPortMaxAllowedKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 0xFFFF, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPortMaxAllowedPlus1KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 0xFFFF + 1, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostArbitraryPortIntMinKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", Integer.MIN_VALUE,
					-1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostArbitraryPortIntMaxKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", Integer.MAX_VALUE,
					-1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region hostName == "localhost" (set as only one in
	// scIntegration.properties), all ports
	@Test
	public void startListener_localhost8080KeepAlive3601_startListenered() throws Exception {
		try {
			server.startListener("localhost", 8080, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPort9000KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 9000, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_changeConnectionTypeHostLocalhostPort9000KeepAlive3601_notListeningThrowsException()
			throws Exception {
		((SCServer) server).setConnectionType("netty.tcp");
		try {
			server.startListener("localhost", 9000, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPort0KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 0, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPort3601KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 3601, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostLocalhostPort1KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 1, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPortMaxAllowedKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 0xFFFF, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPortMaxAllowedPlus1KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 0xFFFF + 1, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostLocalhostPortIntMinKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", Integer.MIN_VALUE, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostLocalhostPortIntMaxKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", Integer.MAX_VALUE, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region hostName == "null", all ports

	@Test
	public void startListener_hostNullPort8080KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 8080, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort9000KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 9000, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort0KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 0, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort3601KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 3601, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort1KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 1, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortMaxAllowedKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 0xFFFF, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortMaxAllowedPlus1KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 0xFFFF + 1, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortIntMinKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, Integer.MIN_VALUE, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortIntMaxKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, Integer.MAX_VALUE, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	// region end
	// region hostName == "", all ports

	@Test
	public void startListener_hostEmptyPort8080KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 8080, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPort9000KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 9000, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPort0KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 0, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPort3601KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 3601, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostEmptyPort1KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 1, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPortMaxAllowedKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 0xFFFF, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPortMaxAllowedPlus1KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 0xFFFF + 1, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostEmptyPortIntMinKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", Integer.MIN_VALUE, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostEmptyPortIntMaxKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", Integer.MAX_VALUE, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region hostName == "a", all ports

	@Test
	public void startListener_hostAPort8080KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 8080, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPort9000KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 9000, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPort0KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 0, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPort3601KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 3601, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostAPort1KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 1, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPortMaxAllowedKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 0xFFFF, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPortMaxAllowedPlus1KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 0xFFFF + 1, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostAPortIntMinKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", Integer.MIN_VALUE, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostAPortIntMaxKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", Integer.MAX_VALUE, 3601);
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
	public void startListener_hostArbitraryPort8080KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 8080, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPort9000KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 9000, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPort0KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 0, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPort3601KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 3601, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostArbitraryPort1KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 1, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPortMaxAllowedKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 0xFFFF, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPortMaxAllowedPlus1KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 0xFFFF + 1, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostArbitraryPortIntMinKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", Integer.MIN_VALUE,
					3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostArbitraryPortIntMaxKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", Integer.MAX_VALUE,
					3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region hostName == "localhost" (set as only one in
	// scIntegration.properties), all ports
	@Test
	public void startListener_localhost8080KeepAliveIntMax_startListenered() throws Exception {
		try {
			server.startListener("localhost", 8080, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPort9000KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 9000, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_changeConnectionTypeHostLocalhostPort9000KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		((SCServer) server).setConnectionType("netty.tcp");
		try {
			server.startListener("localhost", 9000, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPort0KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 0, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPort3601KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 3601, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostLocalhostPort1KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 1, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPortMaxAllowedKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 0xFFFF, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPortMaxAllowedPlus1KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 0xFFFF + 1, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostLocalhostPortIntMinKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", Integer.MIN_VALUE, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostLocalhostPortIntMaxKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", Integer.MAX_VALUE, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region hostName == "null", all ports

	@Test
	public void startListener_hostNullPort8080KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 8080, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort9000KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 9000, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort0KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 0, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort3601KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 3601, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort1KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 1, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortMaxAllowedKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 0xFFFF, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortMaxAllowedPlus1KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 0xFFFF + 1, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortIntMinKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, Integer.MIN_VALUE, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortIntMaxKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, Integer.MAX_VALUE, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	// region end
	// region hostName == "", all ports

	@Test
	public void startListener_hostEmptyPort8080KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 8080, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPort9000KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 9000, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPort0KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 0, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPort3601KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 3601, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostEmptyPort1KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 1, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPortMaxAllowedKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 0xFFFF, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPortMaxAllowedPlus1KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 0xFFFF + 1, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostEmptyPortIntMinKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", Integer.MIN_VALUE, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostEmptyPortIntMaxKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", Integer.MAX_VALUE, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region hostName == "a", all ports

	@Test
	public void startListener_hostAPort8080KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 8080, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPort9000KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 9000, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPort0KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 0, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPort3601KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 3601, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostAPort1KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 1, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPortMaxAllowedKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 0xFFFF, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPortMaxAllowedPlus1KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 0xFFFF + 1, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostAPortIntMinKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", Integer.MIN_VALUE, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostAPortIntMaxKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", Integer.MAX_VALUE, Integer.MAX_VALUE);
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
	public void startListener_hostArbitraryPort8080KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 8080,
					Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPort9000KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 9000,
					Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPort0KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server
					.startListener("The quick brown fox jumps over a lazy dog.", 0,
							Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPort3601KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 3601,
					Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostArbitraryPort1KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server
					.startListener("The quick brown fox jumps over a lazy dog.", 1,
							Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPortMaxAllowedKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 0xFFFF,
					Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPortMaxAllowedPlus1KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 0xFFFF + 1,
					Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostArbitraryPortIntMinKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", Integer.MIN_VALUE,
					Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostArbitraryPortIntMaxKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", Integer.MAX_VALUE,
					Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region hostName == "localhost" (set as only one in
	// scIntegration.properties), all ports
	@Test
	public void startListener_localhost8080KeepAliveIntMin_startListenered() throws Exception {
		try {
			server.startListener("localhost", 8080, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPort9000KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 9000, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_changeConnectionTypeHostLocalhostPort9000KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		((SCServer) server).setConnectionType("netty.tcp");
		try {
			server.startListener("localhost", 9000, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPort0KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 0, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPort3601KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 3601, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostLocalhostPort1KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 1, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPortMaxAllowedKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 0xFFFF, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPortMaxAllowedPlus1KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 0xFFFF + 1, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostLocalhostPortIntMinKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", Integer.MIN_VALUE, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostLocalhostPortIntMaxKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", Integer.MAX_VALUE, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region hostName == "null", all ports

	@Test
	public void startListener_hostNullPort8080KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 8080, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort9000KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 9000, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort0KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 0, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort3601KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 3601, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPort1KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 1, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortMaxAllowedKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 0xFFFF, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortMaxAllowedPlus1KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, 0xFFFF + 1, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortIntMinKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, Integer.MIN_VALUE, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortIntMaxKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, Integer.MAX_VALUE, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	// region end
	// region hostName == "", all ports

	@Test
	public void startListener_hostEmptyPort8080KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 8080, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPort9000KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 9000, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPort0KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 0, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPort3601KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 3601, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostEmptyPort1KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 1, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPortMaxAllowedKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 0xFFFF, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPortMaxAllowedPlus1KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", 0xFFFF + 1, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostEmptyPortIntMinKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", Integer.MIN_VALUE, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostEmptyPortIntMaxKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", Integer.MAX_VALUE, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region hostName == "a", all ports

	@Test
	public void startListener_hostAPort8080KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 8080, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPort9000KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 9000, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPort0KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 0, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPort3601KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 3601, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostAPort1KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 1, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPortMaxAllowedKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 0xFFFF, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPortMaxAllowedPlus1KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", 0xFFFF + 1, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostAPortIntMinKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", Integer.MIN_VALUE, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostAPortIntMaxKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", Integer.MAX_VALUE, Integer.MIN_VALUE);
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
	public void startListener_hostArbitraryPort8080KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 8080,
					Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPort9000KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 9000,
					Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPort0KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server
					.startListener("The quick brown fox jumps over a lazy dog.", 0,
							Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPort3601KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 3601,
					Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostArbitraryPort1KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server
					.startListener("The quick brown fox jumps over a lazy dog.", 1,
							Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPortMaxAllowedKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 0xFFFF,
					Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPortMaxAllowedPlus1KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 0xFFFF + 1,
					Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostArbitraryPortIntMinKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", Integer.MIN_VALUE,
					Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostArbitraryPortIntMaxKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", Integer.MAX_VALUE,
					Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end

	@Test
	public void startListener_twiceWithValidParams_listeningThrowsException() throws Exception
	{
		server.startListener("localhost", 0, 0);
		try {
			server.startListener("another", 1, 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, server.isListening());
		assertEquals(true, ex instanceof InvalidActivityException);
	}
}
