package org.serviceconnector.test.unit;

import static org.junit.Assert.assertEquals;

import java.security.InvalidParameterException;

import javax.activity.InvalidActivityException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.net.SCMPCommunicationException;


public class SCServerStartListenerTest {

	private SCSessionServer server;
	private Exception ex;
	
	private static final String googleIP = "74.125.43.104"; 

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		server = new SCSessionServer();
	}
	
	@After
	public void tearDown() {
		server.destroyServer();
	}
	
	@Test
	public void startListener_hostDefaultPortHttpKeepAlive0_listening() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_HTTP, 0);
		assertEquals(true, server.isListening());
		assertEquals(TestConstants.HOST, server.getHost());
		assertEquals(TestConstants.PORT_HTTP, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostDefaultPortTcpKeepAlive0_listening() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_TCP, 0);
		assertEquals(true, server.isListening());
		assertEquals(TestConstants.HOST, server.getHost());
		assertEquals(TestConstants.PORT_TCP, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_changeConnectionTypeHostDefaultPortTcpKeepAlive0_listening()
			throws Exception {
		((SCSessionServer) server).setConnectionType("netty.tcp");
		server.startListener(TestConstants.HOST, TestConstants.PORT_TCP, 0);
		assertEquals(true, server.isListening());
		assertEquals(TestConstants.HOST, server.getHost());
		assertEquals(TestConstants.PORT_TCP, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostDefaultPort0KeepAlive0_listening() throws Exception {
		server.startListener(TestConstants.HOST, 0, 0);
		assertEquals(true, server.isListening());
		assertEquals(TestConstants.HOST, server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostDefaultPortMinus1KeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, -1, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostDefaultPortMinKeepAlive0_listening() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_MIN, 0);
		assertEquals(true, server.isListening());
		assertEquals(TestConstants.HOST, server.getHost());
		assertEquals(1, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostDefaultPortMaxAllowedKeepAlive0_listening() throws Exception {
		server.startListener(TestConstants.HOST, 0xFFFF, 0);
		assertEquals(true, server.isListening());
		assertEquals(TestConstants.HOST, server.getHost());
		assertEquals(0xFFFF, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostDefaultPortMaxAllowedPlus1KeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, 0xFFFF + 1, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostDefaultPortIntMinKeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, Integer.MIN_VALUE, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostDefaultPortIntMaxKeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, Integer.MAX_VALUE, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostNullPortHttpKeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, TestConstants.PORT_HTTP, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortTcpKeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, TestConstants.PORT_TCP, 0);
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
	public void startListener_hostNullPortMinKeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, TestConstants.PORT_MIN, 0);
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

	@Test
	public void startListener_hostEmptyPortHttpKeepAlive0_listening() throws Exception {
		server.startListener("", TestConstants.PORT_HTTP, 0);
		assertEquals(true, server.isListening());
		assertEquals("", server.getHost());
		assertEquals(TestConstants.PORT_HTTP, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostEmptyPortTcpKeepAlive0_listening() throws Exception {
		server.startListener("", TestConstants.PORT_TCP, 0);
		assertEquals(true, server.isListening());
		assertEquals("", server.getHost());
		assertEquals(TestConstants.PORT_TCP, server.getPort());
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
	public void startListener_hostEmptyPortMinKeepAlive0_listening() throws Exception {
		server.startListener("", TestConstants.PORT_MIN, 0);
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

	@Test
	public void startListener_hostAPortHttpKeepAlive0_notListeningThrowsException() throws Exception {
		try {
			server.startListener("a", TestConstants.PORT_HTTP, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostAPortTcpKeepAlive0_notListeningThrowsException() throws Exception {
		try {
			server.startListener("a", TestConstants.PORT_TCP, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostAPort0KeepAlive0_notListeningThrowsException() throws Exception {
		try {
			server.startListener("a", 0, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
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
	public void startListener_hostAPortMinKeepAlive0_notListeningThrowsException() throws Exception {
		try {
			server.startListener("a", TestConstants.PORT_MIN, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostAPortMaxAllowedKeepAlive0_notListeningThrowsException() throws Exception {
		try {
			server.startListener("a", 0xFFFF, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
		assertEquals(0, server.getPort());
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

	@Test
	public void startListener_hostArbitraryPortHttpKeepAlive0_notListeningThrowsException() throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", TestConstants.PORT_HTTP, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostArbitraryPortTcpKeepAlive0_notListeningThrowsException() throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", TestConstants.PORT_TCP, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostArbitraryPort0KeepAlive0_notListeningThrowsException() throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 0, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
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
	public void startListener_hostArbitraryPortMinKeepAlive0_notListeningThrowsException() throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", TestConstants.PORT_MIN, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostArbitraryPortMaxAllowedKeepAlive0_notListeningThrowsException() throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 0xFFFF, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
		assertEquals(0, server.getPort());
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
	
	
	@Test
	public void startListener_hostGoogleIPPortHttpKeepAlive0_notListeningThrowsException() throws Exception {
		try {
			server.startListener(googleIP, TestConstants.PORT_HTTP, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostGoogleIPPortTcpKeepAlive0_notListeningThrowsException() throws Exception {
		try {
			server.startListener(googleIP, TestConstants.PORT_TCP, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostGoogleIPPort0KeepAlive0_notListeningThrowsException() throws Exception {
		try {
			server.startListener(googleIP, 0, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostGoogleIPPortMinus1KeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(googleIP, -1, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostGoogleIPPortMinKeepAlive0_notListeningThrowsException() throws Exception {
		try {
			server.startListener(googleIP, TestConstants.PORT_MIN, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostGoogleIPPortMaxAllowedKeepAlive0_notListeningThrowsException() throws Exception {
		try {
			server.startListener(googleIP, 0xFFFF, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostGoogleIPPortMaxAllowedPlus1KeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(googleIP, 0xFFFF + 1, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hosGoogleIPPortIntMinKeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server
					.startListener(googleIP, Integer.MIN_VALUE,
							0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostGoogleIPPortIntMaxKeepAlive0_notListeningThrowsException()
			throws Exception {
		try {
			server
					.startListener(googleIP, Integer.MAX_VALUE,
							0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostDefaultPortHttpKeepAlive3600_listening() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_HTTP, 3600);
		assertEquals(true, server.isListening());
		assertEquals(TestConstants.HOST, server.getHost());
		assertEquals(TestConstants.PORT_HTTP, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostDefaultPortTcpKeepAlive3600_listening() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_TCP, 3600);
		assertEquals(true, server.isListening());
		assertEquals(TestConstants.HOST, server.getHost());
		assertEquals(TestConstants.PORT_TCP, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_changeConnectionTypeHostDefaultPortTcpKeepAlive3600_listening()
			throws Exception {
		((SCSessionServer) server).setConnectionType("netty.tcp");
		server.startListener(TestConstants.HOST, TestConstants.PORT_TCP, 3600);
		assertEquals(true, server.isListening());
		assertEquals(TestConstants.HOST, server.getHost());
		assertEquals(TestConstants.PORT_TCP, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostDefaultPort0KeepAlive3600_listening() throws Exception {
		server.startListener(TestConstants.HOST, 0, 3600);
		assertEquals(true, server.isListening());
		assertEquals(TestConstants.HOST, server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostDefaultPortMinus1KeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, -1, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostDefaultPortMinKeepAlive3600_listening() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_MIN, 3600);
		assertEquals(true, server.isListening());
		assertEquals(TestConstants.HOST, server.getHost());
		assertEquals(1, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostDefaultPortMaxAllowedKeepAlive3600_listening() throws Exception {
		server.startListener(TestConstants.HOST, 0xFFFF, 3600);
		assertEquals(true, server.isListening());
		assertEquals(TestConstants.HOST, server.getHost());
		assertEquals(0xFFFF, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostDefaultPortMaxAllowedPlus1KeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, 0xFFFF + 1, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostDefaultPortIntMinKeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, Integer.MIN_VALUE, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostDefaultPortIntMaxKeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, Integer.MAX_VALUE, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostNullPortHttpKeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, TestConstants.PORT_HTTP, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortTcpKeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, TestConstants.PORT_TCP, 3600);
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
	public void startListener_hostNullPortMinKeepAlive3600_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, TestConstants.PORT_MIN, 3600);
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

	@Test
	public void startListener_hostEmptyPortHttpKeepAlive3600_listening() throws Exception {
		server.startListener("", TestConstants.PORT_HTTP, 3600);
		assertEquals(true, server.isListening());
		assertEquals("", server.getHost());
		assertEquals(TestConstants.PORT_HTTP, server.getPort());
		assertEquals(3600, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostEmptyPortTcpKeepAlive3600_listening() throws Exception {
		server.startListener("", TestConstants.PORT_TCP, 3600);
		assertEquals(true, server.isListening());
		assertEquals("", server.getHost());
		assertEquals(TestConstants.PORT_TCP, server.getPort());
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
	public void startListener_hostEmptyPortMinKeepAlive3600_listening() throws Exception {
		server.startListener("", TestConstants.PORT_MIN, 3600);
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

	@Test
	public void startListener_hostAPortHttpKeepAlive3600_notListeningThrowsException() throws Exception {
		try {
			server.startListener("a", TestConstants.PORT_HTTP, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostAPortTcpKeepAlive3600_notListeningThrowsException() throws Exception {
		try {
			server.startListener("a", TestConstants.PORT_TCP, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostAPort0KeepAlive3600_notListeningThrowsException() throws Exception {
		try {
			server.startListener("a", 0, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
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
	public void startListener_hostAPortMinKeepAlive3600_notListeningThrowsException() throws Exception {
		try {
			server.startListener("a", TestConstants.PORT_MIN, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostAPortMaxAllowedKeepAlive3600_notListeningThrowsException() throws Exception {
		try {
			server.startListener("a", 0xFFFF, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
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

	@Test
	public void startListener_hostArbitraryPortHttpKeepAlive3600_notListeningThrowsException() throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", TestConstants.PORT_HTTP, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostArbitraryPortTcpKeepAlive3600_notListeningThrowsException() throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", TestConstants.PORT_TCP, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostArbitraryPort0KeepAlive3600_notListeningThrowsException() throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 0, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
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
	public void startListener_hostArbitraryPortMinKeepAlive3600_notListeningThrowsException() throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", TestConstants.PORT_MIN, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
	}

	@Test
	public void startListener_hostArbitraryPortMaxAllowedKeepAlive3600_notListeningThrowsException() throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 0xFFFF, 3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		assertEquals(false, server.isListening());
		assertEquals(null, server.getHost());
		assertEquals(0, server.getPort());
		assertEquals(0, server.getKeepAliveIntervalInSeconds());
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
			server
					.startListener("The quick brown fox jumps over a lazy dog.", Integer.MIN_VALUE,
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
			server
					.startListener("The quick brown fox jumps over a lazy dog.", Integer.MAX_VALUE,
							3600);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}
	
	@Test
	public void startListener_hostDefaultPortHttpKeepAliveMinus1_listening() throws Exception {
		try {
			server.startListener(TestConstants.HOST, TestConstants.PORT_HTTP, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostDefaultPortTcpKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, TestConstants.PORT_TCP, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_changeConnectionTypeHostDefaultPortTcpKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		((SCSessionServer) server).setConnectionType("netty.tcp");
		try {
			server.startListener(TestConstants.HOST, TestConstants.PORT_TCP, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostDefaultPort0KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, 0, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostDefaultPortMinus1KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, -1, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostDefaultPortMinKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, TestConstants.PORT_MIN, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostDefaultPortMaxAllowedKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, 0xFFFF, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostDefaultPortMaxAllowedPlus1KeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, 0xFFFF + 1, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostDefaultPortIntMinKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, Integer.MIN_VALUE, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostDefaultPortIntMaxKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, Integer.MAX_VALUE, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostNullPortHttpKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, TestConstants.PORT_HTTP, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortTcpKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, TestConstants.PORT_TCP, -1);
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
	public void startListener_hostNullPortMinKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, TestConstants.PORT_MIN, -1);
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

	@Test
	public void startListener_hostEmptyPortHttpKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", TestConstants.PORT_HTTP, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPortTcpKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", TestConstants.PORT_TCP, -1);
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
	public void startListener_hostEmptyPortMinKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", TestConstants.PORT_MIN, -1);
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

	@Test
	public void startListener_hostAPortHttpKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", TestConstants.PORT_HTTP, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPortTcpKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", TestConstants.PORT_TCP, -1);
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
	public void startListener_hostAPortMinKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", TestConstants.PORT_MIN, -1);
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

	@Test
	public void startListener_hostArbitraryPortHttpKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", TestConstants.PORT_HTTP, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPortTcpKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", TestConstants.PORT_TCP, -1);
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
	public void startListener_hostArbitraryPortMinKeepAliveMinus1_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", TestConstants.PORT_MIN, -1);
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

	@Test
	public void startListener_hostDefaultPortHttpKeepAlive3601_listening() throws Exception {
		try {
			server.startListener(TestConstants.HOST, TestConstants.PORT_HTTP, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostDefaultPortTcpKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, TestConstants.PORT_TCP, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_changeConnectionTypeHostDefaultPortTcpKeepAlive3601_notListeningThrowsException()
			throws Exception {
		((SCSessionServer) server).setConnectionType("netty.tcp");
		try {
			server.startListener(TestConstants.HOST, TestConstants.PORT_TCP, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostDefaultPort0KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, 0, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostDefaultPort3601KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, 3601, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostDefaultPortMinKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, TestConstants.PORT_MIN, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostDefaultPortMaxAllowedKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, 0xFFFF, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostDefaultPortMaxAllowedPlus1KeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, 0xFFFF + 1, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostDefaultPortIntMinKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, Integer.MIN_VALUE, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostDefaultPortIntMaxKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, Integer.MAX_VALUE, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostNullPortHttpKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, TestConstants.PORT_HTTP, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortTcpKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, TestConstants.PORT_TCP, 3601);
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
	public void startListener_hostNullPortMinKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, TestConstants.PORT_MIN, 3601);
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

	@Test
	public void startListener_hostEmptyPortHttpKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", TestConstants.PORT_HTTP, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPortTcpKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", TestConstants.PORT_TCP, 3601);
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
	public void startListener_hostEmptyPortMinKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", TestConstants.PORT_MIN, 3601);
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

	@Test
	public void startListener_hostAPortHttpKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", TestConstants.PORT_HTTP, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPortTcpKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", TestConstants.PORT_TCP, 3601);
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
	public void startListener_hostAPortMinKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", TestConstants.PORT_MIN, 3601);
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

	@Test
	public void startListener_hostArbitraryPortHttpKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", TestConstants.PORT_HTTP, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPortTcpKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", TestConstants.PORT_TCP, 3601);
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
	public void startListener_hostArbitraryPortMinKeepAlive3601_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", TestConstants.PORT_MIN, 3601);
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

	@Test
	public void startListener_hostDefaultPortHttpKeepAliveIntMax_listening() throws Exception {
		try {
			server.startListener(TestConstants.HOST, TestConstants.PORT_HTTP, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostDefaultPortTcpKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, TestConstants.PORT_TCP, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_changeConnectionTypeHostDefaultPortTcpKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		((SCSessionServer) server).setConnectionType("netty.tcp");
		try {
			server.startListener(TestConstants.HOST, TestConstants.PORT_TCP, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostDefaultPort0KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, 0, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostDefaultPort3601KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, 3601, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostDefaultPortMinKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, TestConstants.PORT_MIN, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostDefaultPortMaxAllowedKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, 0xFFFF, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostDefaultPortMaxAllowedPlus1KeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, 0xFFFF + 1, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostDefaultPortIntMinKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, Integer.MIN_VALUE, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostDefaultPortIntMaxKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, Integer.MAX_VALUE, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	
	// region hostName == "null", all ports

	@Test
	public void startListener_hostNullPortHttpKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, TestConstants.PORT_HTTP, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortTcpKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, TestConstants.PORT_TCP, Integer.MAX_VALUE);
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
	public void startListener_hostNullPortMinKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, TestConstants.PORT_MIN, Integer.MAX_VALUE);
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

	
	// region hostName == "", all ports

	@Test
	public void startListener_hostEmptyPortHttpKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", TestConstants.PORT_HTTP, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPortTcpKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", TestConstants.PORT_TCP, Integer.MAX_VALUE);
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
	public void startListener_hostEmptyPortMinKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", TestConstants.PORT_MIN, Integer.MAX_VALUE);
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

	@Test
	public void startListener_hostAPortHttpKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", TestConstants.PORT_HTTP, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPortTcpKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", TestConstants.PORT_TCP, Integer.MAX_VALUE);
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
	public void startListener_hostAPortMinKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", TestConstants.PORT_MIN, Integer.MAX_VALUE);
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

	@Test
	public void startListener_hostArbitraryPortHttpKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", TestConstants.PORT_HTTP,
					Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPortTcpKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", TestConstants.PORT_TCP,
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
	public void startListener_hostArbitraryPortMinKeepAliveIntMax_notListeningThrowsException()
			throws Exception {
		try {
			server
					.startListener("The quick brown fox jumps over a lazy dog.", TestConstants.PORT_MIN,
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

	@Test
	public void startListener_hostDefaultPortHttpKeepAliveIntMin_listening() throws Exception {
		try {
			server.startListener(TestConstants.HOST, TestConstants.PORT_HTTP, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostDefaultPortTcpKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, TestConstants.PORT_TCP, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_changeConnectionTypeHostDefaultPortTcpKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		((SCSessionServer) server).setConnectionType("netty.tcp");
		try {
			server.startListener(TestConstants.HOST, TestConstants.PORT_TCP, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostDefaultPort0KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, 0, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostDefaultPort3601KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, 3601, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostDefaultPortMinKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, TestConstants.PORT_MIN, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostDefaultPortMaxAllowedKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, 0xFFFF, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostDefaultPortMaxAllowedPlus1KeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, 0xFFFF + 1, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostDefaultPortIntMinKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, Integer.MIN_VALUE, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostDefaultPortIntMaxKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, Integer.MAX_VALUE, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostNullPortHttpKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, TestConstants.PORT_HTTP, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void startListener_hostNullPortTcpKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, TestConstants.PORT_TCP, Integer.MIN_VALUE);
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
	public void startListener_hostNullPortMinKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener(null, TestConstants.PORT_MIN, Integer.MIN_VALUE);
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

	@Test
	public void startListener_hostEmptyPortHttpKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", TestConstants.PORT_HTTP, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPortTcpKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", TestConstants.PORT_TCP, Integer.MIN_VALUE);
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
	public void startListener_hostEmptyPortMinKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("", TestConstants.PORT_MIN, Integer.MIN_VALUE);
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

	@Test
	public void startListener_hostAPortHttpKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", TestConstants.PORT_HTTP, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostAPortTcpKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", TestConstants.PORT_TCP, Integer.MIN_VALUE);
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
	public void startListener_hostAPortMinKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("a", TestConstants.PORT_MIN, Integer.MIN_VALUE);
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

	
	// region hostName == "The quick brown fox jumps over a lazy dog.", all
	// ports

	@Test
	public void startListener_hostArbitraryPortHttpKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", TestConstants.PORT_HTTP,
					Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(false, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPortTcpKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", TestConstants.PORT_TCP,
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
	public void startListener_hostArbitraryPortMinKeepAliveIntMin_notListeningThrowsException()
			throws Exception {
		try {
			server
					.startListener("The quick brown fox jumps over a lazy dog.", TestConstants.PORT_MIN,
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
	public void startListener_hostArbitraryPortIntMinKeepAliveIntMin_notListeningThrowsException() {
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

	

	@Test
	public void startListener_twiceWithValidParams_listeningThrowsException() throws Exception
	{
		server.startListener(TestConstants.HOST, 0, 0);
		try {
			server.startListener("another", 1, 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, server.isListening());
		assertEquals(true, ex instanceof InvalidActivityException);
	}
	
	
}
