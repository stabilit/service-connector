package integration;

import static org.junit.Assert.assertEquals;

import java.security.InvalidParameterException;

import org.junit.Before;
import org.junit.Test;

import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.ISCClient;
import com.stabilit.scm.common.net.req.ConnectionPoolConnectException;
import com.stabilit.scm.common.service.ISC;
import com.stabilit.scm.sc.SC;

public class AttachClientToSCTest {

	private ISC sc;
	private ISCClient client;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		SC.main(new String[] { "-filename", "scIntegration.properties" });
		client = new SCClient();
		System.out.println();
	}

	public void attach_localhost8080_attached() throws Exception {
		client.attach("localhost", 8080);
		assertEquals(true, client.isAttached());
	}

	@Test
	public void attach_localhost9000_passesWithoutException() throws Exception {
		client.attach("localhost", 9000);
		assertEquals(true, client.isAttached());
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostLocalhostPort0_throwsException() throws Exception {
		try {
			client.attach("localhost", 0);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostLocalhostPortMinus1_throwsException()
			throws Exception {
		try {
			client.attach("localhost", -1);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void attach_hostLocalhostPort1_throwsException() throws Exception {
		try {
			client.attach("localhost", 1);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void attach_hostLocalhostPortMaxAllowed_throwsException()
			throws Exception {
		try {
			client.attach("localhost", 0xFFFF);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}

	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostLocalhostPortMaxAllowedPlus1_throwsException()
			throws Exception {
		try {
			client.attach("localhost", 0xFFFF + 1);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostLocalhostPortIntMin_throwsException()
			throws Exception {
		try {
			client.attach("localhost", Integer.MIN_VALUE);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostLocalhostPortIntMax_throwsException()
			throws Exception {
		try {
			client.attach("localhost", Integer.MAX_VALUE);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostNullPort8080_throwsException() throws Exception {
		try {
			client.attach(null, 8080);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostNullPort9000_throwsException() throws Exception {
		try {
			client.attach(null, 9000);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostNullPort0_throwsException() throws Exception {
		try {
			client.attach(null, 0);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostNullPortMinus1_throwsException() throws Exception {
		try {
			client.attach(null, -1);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostNullPort1_throwsException() throws Exception {
		try {
			client.attach(null, 1);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostNullPortMaxAllowed_throwsException()
			throws Exception {
		try {
			client.attach(null, 0xFFFF);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostNullPortMaxAllowedPlus1_throwsException()
			throws Exception {
		try {
			client.attach(null, 0xFFFF + 1);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostNullPortIntMin_throwsException() throws Exception {
		try {
			client.attach(null, Integer.MIN_VALUE);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostNullPortIntMax_throwsException() throws Exception {
		try {
			client.attach(null, Integer.MAX_VALUE);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void attach_hostEmptyPort8080_throwsException() throws Exception {
		try {
			client.attach("", 8080);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void attach_hostEmptyPort9000_throwsException() throws Exception {
		try {
			client.attach("", 9000);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostEmptyPort0_throwsException() throws Exception {
		try {
			client.attach("", 0);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostEmptyPortMinus1_throwsException() throws Exception {
		try {
			client.attach("", -1);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void attach_hostEmptyPort1_throwsException() throws Exception {
		try {
			client.attach("", 1);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void attach_hostEmptyPortMaxAllowed_throwsException()
			throws Exception {
		try {
			client.attach("", 0xFFFF);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostEmptyPortMaxAllowedPlus1_throwsException()
			throws Exception {
		try {
			client.attach("", 0xFFFF + 1);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostEmptyPortIntMin_throwsException() throws Exception {
		try {
			client.attach("", Integer.MIN_VALUE);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostEmptyPortIntMax_throwsException() throws Exception {
		try {
			client.attach("", Integer.MAX_VALUE);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}
	
//	region end
//	region hostName == "a", all ports
	
	
	@Test(expected = ConnectionPoolConnectException.class)
	public void attach_hostAPort8080_throwsException() throws Exception {
		try {
			client.attach("a", 8080);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void attach_hostAPort9000_throwsException() throws Exception {
		try {
			client.attach("a", 9000);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostAPort0_throwsException() throws Exception {
		try {
			client.attach("a", 0);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostAPortMinus1_throwsException() throws Exception {
		try {
			client.attach("a", -1);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void attach_hostAPort1_throwsException() throws Exception {
		try {
			client.attach("a", 1);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void attach_hostAPortMaxAllowed_throwsException()
			throws Exception {
		try {
			client.attach("a", 0xFFFF);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostAPortMaxAllowedPlus1_throwsException()
			throws Exception {
		try {
			client.attach("a", 0xFFFF + 1);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostAPortIntMin_throwsException() throws Exception {
		try {
			client.attach("a", Integer.MIN_VALUE);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostAPortIntMax_throwsException() throws Exception {
		try {
			client.attach("a", Integer.MAX_VALUE);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}
//	region end
}
