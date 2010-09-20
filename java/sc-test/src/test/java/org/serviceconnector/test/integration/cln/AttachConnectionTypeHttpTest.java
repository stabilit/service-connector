package org.serviceconnector.test.integration.cln;

import static org.junit.Assert.assertEquals;

import java.security.InvalidParameterException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.cln.ISCClient;
import org.serviceconnector.cln.SCClient;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.ctrl.util.TestEnvironmentController;
import org.serviceconnector.sc.service.SCServiceException;


public class AttachConnectionTypeHttpTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(AttachConnectionTypeHttpTest.class);

	private int threadCount = 0;
	private ISCClient client;
	private Exception ex;

	private static Process scProcess;
	private static TestEnvironmentController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSC0Properties);
		ctrl = null;
		scProcess = null;
	}

	@Before
	public void setUp() throws Exception {
		threadCount = Thread.activeCount();
		client = new SCClient();
	}
	
	@After
	public void tearDown() throws Exception {
		try {
			client.detach();
		} catch (Exception e) {} 
		client = null;
		assertEquals(threadCount, Thread.activeCount());
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
