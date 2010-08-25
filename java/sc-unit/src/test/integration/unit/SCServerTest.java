package unit;

import static org.junit.Assert.assertEquals;

import java.security.InvalidParameterException;

import org.junit.Before;
import org.junit.Test;

import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.srv.ISCServer;
import com.stabilit.scm.srv.SCServer;

public class SCServerTest {

	private ISCServer server;
	private Exception ex;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		server = new SCServer();
	}

	@Test
	public void setConnectionType_nullParam_null() {
		((SCServer) server).setConnectionType(null);
		assertEquals(null, server.getConnectionType());
	}

	@Test
	public void setConnectionType_emptyString_emptyString() {
		((SCServer) server).setConnectionType("");
		assertEquals("", server.getConnectionType());
	}

	@Test
	public void setConnectionType_whiteSpaceString_emptyString() {
		((SCServer) server).setConnectionType(" ");
		assertEquals(" ", server.getConnectionType());
	}

	@Test
	public void setConnectionType_oneCharString_givenString() {
		((SCServer) server).setConnectionType("a");
		assertEquals("a", server.getConnectionType());
	}

	@Test
	public void setConnectionType_arbitraryString_givenString() {
		((SCServer) server).setConnectionType("aaa");
		assertEquals("aaa", server.getConnectionType());
	}

	@Test
	public void setConnectionType_shortMaxLengthString_givenString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Short.MAX_VALUE; i++) {
			sb.append('a');
		}
		((SCServer) server).setConnectionType(sb.toString());
		assertEquals(sb.toString(), server.getConnectionType());
	}

	@Test
	public void setImmediateConnect_true_true() {
		server.setImmediateConnect(true);
		assertEquals(true, server.isImmediateConnect());
	}

	@Test
	public void setImmediateConnect_false_false() {
		server.setImmediateConnect(false);
		assertEquals(false, server.isImmediateConnect());
	}

	// region hostName == "localhost" (set as only one in
	// scIntegration.properties), all ports

	@Test
	public void startListener_localhost8080_startListenered() throws Exception {
		server.startListener("localhost", 8080, 1);
		assertEquals(true, server.isListening());
		server.destroyServer();
	}

	@Test
	public void startListener_hostLocalhostPort9000_startListenered()
			throws Exception {
		server.startListener("localhost", 9000, 1);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPort0_notAttachedThrowsException()
			throws Exception {
		server.startListener("localhost", 0, 1);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPortMinus1_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", -1, 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostLocalhostPort1_notAttachedThrowsException()
			throws Exception {
		server.startListener("localhost", 1, 1);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPortMaxAllowed_notAttachedThrowsException()
			throws Exception {
		server.startListener("localhost", 0xFFFF, 1);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostLocalhostPortMaxAllowedPlus1_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 0xFFFF + 1, 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostLocalhostPortIntMin_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", Integer.MIN_VALUE, 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostLocalhostPortIntMax_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", Integer.MAX_VALUE, 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region hostName == "null", all ports

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostNullPort8080_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener(null, 8080, 1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostNullPort9000_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener(null, 9000, 1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostNullPort0_notAttachedThrowsException()
			throws Exception {
		server.startListener(null, 0, 1);
		assertEquals(true, server.isListening());
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostNullPortMinus1_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener(null, -1, 1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostNullPort1_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener(null, 1, 1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostNullPortMaxAllowed_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener(null, 0xFFFF, 1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostNullPortMaxAllowedPlus1_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener(null, 0xFFFF + 1, 1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostNullPortIntMin_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener(null, Integer.MIN_VALUE, 1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostNullPortIntMax_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener(null, Integer.MAX_VALUE, 1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	// region end
	// region hostName == "", all ports

	@Test
	public void startListener_hostEmptyPort8080_notAttachedThrowsException()
			throws Exception {
		server.startListener("", 8080, 1);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPort9000_notAttachedThrowsException()
			throws Exception {
		server.startListener("", 9000, 1);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPort0_notAttachedThrowsException()
			throws Exception {
		server.startListener("", 0, 1);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPortMinus1_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("", -1, 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostEmptyPort1_notAttachedThrowsException()
			throws Exception {
		server.startListener("", 1, 1);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPortMaxAllowed_notAttachedThrowsException()
			throws Exception {
		server.startListener("", 0xFFFF, 1);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostEmptyPortMaxAllowedPlus1_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("", 0xFFFF + 1, 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostEmptyPortIntMin_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("", Integer.MIN_VALUE, 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostEmptyPortIntMax_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("", Integer.MAX_VALUE, 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region hostName == "a", all ports

	@Test
	public void startListener_hostAPort8080_notAttachedThrowsException()
			throws Exception {
		server.startListener("a", 8080, 1);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostAPort9000_notAttachedThrowsException()
			throws Exception {
		server.startListener("a", 9000, 1);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostAPort0_notAttachedThrowsException()
			throws Exception {
		server.startListener("a", 0, 1);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostAPortMinus1_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("a", -1, 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostAPort1_notAttachedThrowsException()
			throws Exception {
		server.startListener("a", 1, 1);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostAPortMaxAllowed_notAttachedThrowsException()
			throws Exception {
		server.startListener("a", 0xFFFF, 1);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostAPortMaxAllowedPlus1_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("a", 0xFFFF + 1, 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostAPortIntMin_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("a", Integer.MIN_VALUE, 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostAPortIntMax_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("a", Integer.MAX_VALUE, 1);
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
	public void startListener_hostArbitraryPort8080_notAttachedThrowsException()
			throws Exception {
		server.startListener("The quick brown fox jumps over a lazy dog.",
				8080, 1);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPort9000_notAttachedThrowsException()
			throws Exception {
		server.startListener("The quick brown fox jumps over a lazy dog.",
				9000, 1);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPort0_notAttachedThrowsException()
			throws Exception {
		server
				.startListener("The quick brown fox jumps over a lazy dog.", 0,
						1);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPortMinus1_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", -1, 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostArbitraryPort1_notAttachedThrowsException()
			throws Exception {
		server.startListener("The quick brown fox jumps over a lazy dog.", 1, 1);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPortMaxAllowed_notAttachedThrowsException()
			throws Exception {
		server.startListener("The quick brown fox jumps over a lazy dog.",
				0xFFFF, 1);
		assertEquals(true, server.isListening());
	}

	@Test
	public void startListener_hostArbitraryPortMaxAllowedPlus1_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.",
					0xFFFF + 1, 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostArbitraryPortIntMin_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", Integer.MIN_VALUE, 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void startListener_hostArbitraryPortIntMax_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", Integer.MAX_VALUE, 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isListening());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	
	@Test
	public void destroyServer_withoutPreviousisListening_notisListening()
	{
		server.destroyServer();
		assertEquals(false, server.isListening());
	}
	
	@Test
	public void destroyServer_withValidisListening_notisListening() throws Exception
	{
		server.startListener("localhost", 8080, 1);
		assertEquals(true, server.isListening());
		server.destroyServer();
		assertEquals(false, server.isListening());
	}
	
	@Test
	public void startListeningdestroyServer_500Times_notisListening() throws Exception
	{
		for (int i = 0; i < 500; i++) {
			server.startListener("localhost", 8080, 1);
			assertEquals(true, server.isListening());
			server.destroyServer();
			assertEquals(false, server.isListening());
		}
	}
}
