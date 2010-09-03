package integration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.ISCClient;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.service.SCServiceException;

public class AttachClientToSCTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(AttachClientToSCTest.class);
	
	private static ISCClient client;
	private static Process p;
	private Exception ex;

	@BeforeClass
	public static void oneTimeSetUp() {
		try {
			String userDir = System.getProperty("user.dir");
			String command = "java -Dlog4j.configuration=file:" + userDir
					+ "\\src\\main\\resources\\log4jSC0.properties -jar " + userDir
					+ "\\..\\service-connector\\target\\sc.jar -filename " + userDir
					+ "\\src\\main\\resources\\scIntegration.properties";

			p = Runtime.getRuntime().exec(command);

			// lets the SC load before starting communication
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error("oneTimeSetUp", e);
			}
		} catch (IOException e) {
			logger.error("oneTimeSetUp", e);
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
		client = new SCClient();
	}

	// region hostName == "localhost" (set as only one in
	// scIntegration.properties), all ports
	@Test
	public void attach_localhost8080_attached() throws Exception {
		client.attach("localhost", 8080);
		assertEquals(true, client.isAttached());
	}

	@Test
	public void attach_hostLocalhostPort9000_attached() throws Exception {
		try {
			client.attach("localhost", 9000);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void attach_changeConnectionTypeHostLocalhostPort9000_attached() throws Exception {
		((SCClient) client).setConnectionType("netty.tcp");
		client.attach("localhost", 9000);
		assertEquals(true, client.isAttached());
	}

	@Test
	public void attach_hostLocalhostPort0_notAttachedThrowsException() throws Exception {
		try {
			client.attach("localhost", 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void attach_hostLocalhostPortMinus1_notAttachedThrowsException() throws Exception {
		try {
			client.attach("localhost", -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void attach_hostLocalhostPort1_notAttachedThrowsException() throws Exception {
		try {
			client.attach("localhost", 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void attach_hostLocalhostPortMaxAllowed_notAttachedThrowsException() throws Exception {
		try {
			client.attach("localhost", 0xFFFF);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void attach_hostLocalhostPortMaxAllowedPlus1_notAttachedThrowsException()
			throws Exception {
		try {
			client.attach("localhost", 0xFFFF + 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void attach_hostLocalhostPortIntMin_notAttachedThrowsException() throws Exception {
		try {
			client.attach("localhost", Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void attach_hostLocalhostPortIntMax_notAttachedThrowsException() throws Exception {
		try {
			client.attach("localhost", Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region hostName == "null", all ports

	@Test
	public void attach_hostNullPort8080_notAttachedThrowsException() throws Exception {
		try {
			client.attach(null, 8080);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void attach_hostNullPort9000_notAttachedThrowsException() throws Exception {
		try {
			client.attach(null, 9000);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostNullPort0_notAttachedThrowsException() throws Exception {
		try {
			client.attach(null, 0);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostNullPortMinus1_notAttachedThrowsException() throws Exception {
		try {
			client.attach(null, -1);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostNullPort1_notAttachedThrowsException() throws Exception {
		try {
			client.attach(null, 1);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostNullPortMaxAllowed_notAttachedThrowsException() throws Exception {
		try {
			client.attach(null, 0xFFFF);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostNullPortMaxAllowedPlus1_notAttachedThrowsException() throws Exception {
		try {
			client.attach(null, 0xFFFF + 1);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostNullPortIntMin_notAttachedThrowsException() throws Exception {
		try {
			client.attach(null, Integer.MIN_VALUE);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void attach_hostNullPortIntMax_notAttachedThrowsException() throws Exception {
		try {
			client.attach(null, Integer.MAX_VALUE);
		} catch (Exception e) {
			assertEquals(false, client.isAttached());
			throw e;
		}
	}

	// region end
	// region hostName == "", all ports

	@Test
	public void attach_hostEmptyPort8080_hostIsInterpretedAsLocalhostIsAttached() throws Exception {
		client.attach("", 8080);
		assertEquals(true, client.isAttached());
	}

	@Test
	public void attach_hostEmptyPort9000_notAttachedThrowsException() throws Exception {
		try {
			client.attach("", 9000);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void attach_hostEmptyPort0_notAttachedThrowsException() throws Exception {
		try {
			client.attach("", 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void attach_hostEmptyPortMinus1_notAttachedThrowsException() throws Exception {
		try {
			client.attach("", -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void attach_hostEmptyPort1_notAttachedThrowsException() throws Exception {
		try {
			client.attach("", 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void attach_hostEmptyPortMaxAllowed_notAttachedThrowsException() throws Exception {
		try {
			client.attach("", 0xFFFF);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void attach_hostEmptyPortMaxAllowedPlus1_notAttachedThrowsException() throws Exception {
		try {
			client.attach("", 0xFFFF + 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void attach_hostEmptyPortIntMin_notAttachedThrowsException() throws Exception {
		try {
			client.attach("", Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void attach_hostEmptyPortIntMax_notAttachedThrowsException() throws Exception {
		try {
			client.attach("", Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region hostName == "a", all ports

	@Test
	public void attach_hostAPort8080_notAttachedThrowsException() throws Exception {
		try {
			client.attach("a", 8080);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void attach_hostAPort9000_notAttachedThrowsException() throws Exception {
		try {
			client.attach("a", 9000);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void attach_hostAPort0_notAttachedThrowsException() throws Exception {
		try {
			client.attach("a", 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void attach_hostAPortMinus1_notAttachedThrowsException() throws Exception {
		try {
			client.attach("a", -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void attach_hostAPort1_notAttachedThrowsException() throws Exception {
		try {
			client.attach("a", 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void attach_hostAPortMaxAllowed_notAttachedThrowsException() throws Exception {
		try {
			client.attach("a", 0xFFFF);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void attach_hostAPortMaxAllowedPlus1_notAttachedThrowsException() throws Exception {
		try {
			client.attach("a", 0xFFFF + 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void attach_hostAPortIntMin_notAttachedThrowsException() throws Exception {
		try {
			client.attach("a", Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void attach_hostAPortIntMax_notAttachedThrowsException() throws Exception {
		try {
			client.attach("a", Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region hostName == "The quick brown fox jumps over a lazy dog.", all
	// ports

	@Test
	public void attach_hostArbitraryPort8080_notAttachedThrowsException() throws Exception {
		try {
			client.attach("The quick brown fox jumps over a lazy dog.", 8080);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void attach_hostArbitraryPort9000_notAttachedThrowsException() throws Exception {
		try {
			client.attach("The quick brown fox jumps over a lazy dog.", 9000);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void attach_hostArbitraryPort0_notAttachedThrowsException() throws Exception {
		try {
			client.attach("The quick brown fox jumps over a lazy dog.", 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void attach_hostArbitraryPortMinus1_notAttachedThrowsException() throws Exception {
		try {
			client.attach("The quick brown fox jumps over a lazy dog.", -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void attach_hostArbitraryPort1_notAttachedThrowsException() throws Exception {
		try {
			client.attach("The quick brown fox jumps over a lazy dog.", 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void attach_hostArbitraryPortMaxAllowed_notAttachedThrowsException() throws Exception {
		try {
			client.attach("The quick brown fox jumps over a lazy dog.", 0xFFFF);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void attach_hostArbitraryPortMaxAllowedPlus1_notAttachedThrowsException()
			throws Exception {
		try {
			client.attach("The quick brown fox jumps over a lazy dog.", 0xFFFF + 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void attach_hostArbitraryPortIntMin_notAttachedThrowsException() throws Exception {
		try {
			client.attach("The quick brown fox jumps over a lazy dog.", Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void attach_hostArbitraryPortIntMax_notAttachedThrowsException() throws Exception {
		try {
			client.attach("The quick brown fox jumps over a lazy dog.", Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
	// region attach with 3 parameters

	// since all mandatory combinations are tested, we can omit all combinations
	// and test only those the attribute keepAlive could have some effect on
	// other combinations are useless. Really

	public void attach_hostLocalhostPort8080KeepAlive1_attached() throws Exception {
		client.attach("localhost", 8080, 1);
		assertEquals(true, client.isAttached());
	}

	public void attach_hostLocalhostPort8080KeepAlive3600_attached() throws Exception {
		client.attach("localhost", 8080, 3600);
		assertEquals(true, client.isAttached());
	}

	@Test
	public void attach_KeepAlive0_notAttached() throws Exception {
		client.attach("localhost", 8080, 0);
		assertEquals(true, client.isAttached());
	}

	@Test
	public void attach_hostLocalhostPort8080KeepAliveMinus1_notAttached() throws Exception {
		try {
			client.attach("localhost", 8080, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void attach_hostLocalhostPort8080KeepAlive1_isAttached() throws Exception {
		client.attach("localhost", 8080, 1);
		assertEquals(true, client.isAttached());
	}

	@Test
	public void attach_hostLocalhostPort8080KeepAlive3601_notAttached() throws Exception {
		try {
			client.attach("localhost", 8080, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void attach_hostLocalhostPort8080KeepAliveIntMin_notAttached() throws Exception {
		try {
			client.attach("localhost", 8080, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void attach_hostLocalhostPort8080KeepAliveIntMax_notAttached() throws Exception {
		try {
			client.attach("localhost", 8080, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, client.isAttached());
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// region end
}
