package integration;


import static org.junit.Assert.assertEquals;

import java.security.InvalidParameterException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.common.net.req.ConnectionPoolConnectException;
import com.stabilit.scm.common.service.SCServiceException;
import com.stabilit.scm.srv.SCServer;

public class StarListenerServerToSCTest {

	private Process p;
	private SCServer server;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// SC.main(new String[] { "-filename", "scIntegration.properties" });
		try {
			p = Runtime.getRuntime().exec(
					"cmd /c start src\\test\\resources\\startSC.bat");

		} catch (Exception err) {
			err.printStackTrace();
		}
		server = new SCServer();
	}

	@After
	public void tearDown() throws Exception {
		p.destroy();
	}

//	region hostName == "localhost" (set as only one in 
//	scIntegration.properties), all ports
	@Test
	public void startListener_localhost8080_startListenered() throws Exception {
		server.startListener("localhost", 8080);
		assertEquals(true, server.isListening());
	}
	
	@Test(expected = SCServiceException.class)
	public void startListener_hostLocalhostPort9000_startListenered() throws Exception {
		try {
			server.startListener("localhost", 9000);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}
	
	@Test
	public void startListener_changeConnectionTypeHostLocalhostPort9000_startListenered() throws Exception {
		((SCClient)server).setConnectionType("netty.tcp");
		server.startListener("localhost", 9000);
		assertEquals(true, server.isListening());
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostLocalhostPort0_notAttachedThrowsException() throws Exception {
		try {
			server.startListener("localhost", 0);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostLocalhostPortMinus1_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", -1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void startListener_hostLocalhostPort1_notAttachedThrowsException() throws Exception {
		try {
			server.startListener("localhost", 1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void startListener_hostLocalhostPortMaxAllowed_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 0xFFFF);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}

	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostLocalhostPortMaxAllowedPlus1_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", 0xFFFF + 1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostLocalhostPortIntMin_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", Integer.MIN_VALUE);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostLocalhostPortIntMax_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("localhost", Integer.MAX_VALUE);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	
//	region end
//	region hostName == "null", all ports
	
	
	@Test(expected = InvalidParameterException.class)
	public void startListener_hostNullPort8080_notAttachedThrowsException() throws Exception {
		try {
			server.startListener(null, 8080);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostNullPort9000_notAttachedThrowsException() throws Exception {
		try {
			server.startListener(null, 9000);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostNullPort0_notAttachedThrowsException() throws Exception {
		try {
			server.startListener(null, 0);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostNullPortMinus1_notAttachedThrowsException() throws Exception {
		try {
			server.startListener(null, -1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostNullPort1_notAttachedThrowsException() throws Exception {
		try {
			server.startListener(null, 1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostNullPortMaxAllowed_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener(null, 0xFFFF);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostNullPortMaxAllowedPlus1_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener(null, 0xFFFF + 1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostNullPortIntMin_notAttachedThrowsException() throws Exception {
		try {
			server.startListener(null, Integer.MIN_VALUE);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostNullPortIntMax_notAttachedThrowsException() throws Exception {
		try {
			server.startListener(null, Integer.MAX_VALUE);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

//	region end
//	region hostName == "", all ports
	
	
	@Test(expected = ConnectionPoolConnectException.class)
	public void startListener_hostEmptyPort8080_notAttachedThrowsException() throws Exception {
		try {
			server.startListener("", 8080);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void startListener_hostEmptyPort9000_notAttachedThrowsException() throws Exception {
		try {
			server.startListener("", 9000);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostEmptyPort0_notAttachedThrowsException() throws Exception {
		try {
			server.startListener("", 0);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostEmptyPortMinus1_notAttachedThrowsException() throws Exception {
		try {
			server.startListener("", -1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void startListener_hostEmptyPort1_notAttachedThrowsException() throws Exception {
		try {
			server.startListener("", 1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void startListener_hostEmptyPortMaxAllowed_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("", 0xFFFF);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostEmptyPortMaxAllowedPlus1_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("", 0xFFFF + 1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostEmptyPortIntMin_notAttachedThrowsException() throws Exception {
		try {
			server.startListener("", Integer.MIN_VALUE);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostEmptyPortIntMax_notAttachedThrowsException() throws Exception {
		try {
			server.startListener("", Integer.MAX_VALUE);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}
	
//	region end
//	region hostName == "a", all ports
	
	
	@Test(expected = ConnectionPoolConnectException.class)
	public void startListener_hostAPort8080_notAttachedThrowsException() throws Exception {
		try {
			server.startListener("a", 8080);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void startListener_hostAPort9000_notAttachedThrowsException() throws Exception {
		try {
			server.startListener("a", 9000);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostAPort0_notAttachedThrowsException() throws Exception {
		try {
			server.startListener("a", 0);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostAPortMinus1_notAttachedThrowsException() throws Exception {
		try {
			server.startListener("a", -1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void startListener_hostAPort1_notAttachedThrowsException() throws Exception {
		try {
			server.startListener("a", 1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void startListener_hostAPortMaxAllowed_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("a", 0xFFFF);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostAPortMaxAllowedPlus1_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("a", 0xFFFF + 1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostAPortIntMin_notAttachedThrowsException() throws Exception {
		try {
			server.startListener("a", Integer.MIN_VALUE);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostAPortIntMax_notAttachedThrowsException() throws Exception {
		try {
			server.startListener("a", Integer.MAX_VALUE);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}
	
	
//	region end
//	region hostName == "The quick brown fox jumps over a lazy dog.", all ports

	
	@Test(expected = ConnectionPoolConnectException.class)
	public void startListener_hostArbitraryPort8080_notAttachedThrowsException() throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 8080);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void startListener_hostArbitraryPort9000_notAttachedThrowsException() throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 9000);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostArbitraryPort0_notAttachedThrowsException() throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 0);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostArbitraryPortMinus1_notAttachedThrowsException() throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", -1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void startListener_hostArbitraryPort1_notAttachedThrowsException() throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void startListener_hostArbitraryPortMaxAllowed_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 0xFFFF);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostArbitraryPortMaxAllowedPlus1_notAttachedThrowsException()
			throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", 0xFFFF + 1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostArbitraryPortIntMin_notAttachedThrowsException() throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", Integer.MIN_VALUE);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void startListener_hostArbitraryPortIntMax_notAttachedThrowsException() throws Exception {
		try {
			server.startListener("The quick brown fox jumps over a lazy dog.", Integer.MAX_VALUE);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}
	
	
//	region end
//	region startListener with 3 parameters
	
//	since all mandatory combinations are tested, we can omit all combinations
//	and test only those the attribute keepAlive could have some effect on
//	other combinations are useless. Really
	
	public void startListener_hostLocalhostPort8080KeepAlive1_startListenered() throws Exception {
		server.startListener("localhost", 8080, 1);
		assertEquals(true, server.isListening());
	}
	
	public void startListener_hostLocalhostPort8080KeepAlive3600_startListenered() throws Exception {
		server.startListener("localhost", 8080, 3600);
		assertEquals(true, server.isListening());
	}
	
	@Test
	public void startListener_hostLocalhostPort8080KeepAlive0_notAttached() throws Exception {
		try {
			server.startListener("localhost", 8080, 0);
		} catch (Exception e) {
			assertEquals(true, server.isListening());
			throw e;
		}
	}
	
	@Test(expected = InvalidParameterException.class)
	public void startListener_hostLocalhostPort8080KeepAliveMinus1_notAttached() throws Exception {
		try {
			server.startListener("localhost", 8080, -1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}
	
	@Test(expected = InvalidParameterException.class)
	public void startListener_hostLocalhostPort8080KeepAlive1_notAttached() throws Exception {
		try {
			server.startListener("localhost", 8080, 1);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}
	
	@Test(expected = InvalidParameterException.class)
	public void startListener_hostLocalhostPort8080KeepAlive3601_notAttached() throws Exception {
		try {
			server.startListener("localhost", 8080, 3601);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}
	
	@Test(expected = InvalidParameterException.class)
	public void startListener_hostLocalhostPort8080KeepAliveIntMin_notAttached() throws Exception {
		try {
			server.startListener("localhost", 8080, Integer.MIN_VALUE);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}
	
	@Test(expected = InvalidParameterException.class)
	public void startListener_hostLocalhostPort8080KeepAliveIntMax_notAttached() throws Exception {
		try {
			server.startListener("localhost", 8080, Integer.MAX_VALUE);
		} catch (Exception e) {
			assertEquals(false, server.isListening());
			throw e;
		}
	}
	
//	region end
}
