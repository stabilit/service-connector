package org.serviceconnector.test.integration.cln;

import static org.junit.Assert.assertEquals;

import java.security.InvalidParameterException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.service.SCServiceException;

public class AttachConnectionTypeTcpTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(AttachConnectionTypeTcpTest.class);

	private SCClient client;

	private static ProcessesController ctrl;
	private static Process scProcess;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSCProperties);
		ctrl = null;
		scProcess = null;
	}

	@Before
	public void setUp() throws Exception {
		// threadCount = Thread.activeCount();
		client = new SCClient();
		((SCClient) client).setConnectionType("netty.tcp");
	}

	@After
	public void tearDown() throws Exception {
		try {
			client.detach();
		} catch (Exception e) {
		}
		client = null;
		// assertEquals("number of threads", threadCount, Thread.activeCount());
	}

	/**
	 * Run the method "client.atach" and check the exeptions.
	 * @param host
	 * @param port
	 * @param keepAlive = the keep alive interval in seconds, integer value or can be null
	 * @param isClientAttached = true or false
	 * @param expectedException = need the expected exception as string.
	 */
	private void testAttach(String host, int port, Integer keepAlive, boolean isClientAttached, String expectedException) {
		try {
			if (keepAlive == null) {
				client.attach(host, port);
			}
			else {
				client.attach(host, port, keepAlive.intValue() );
			}
		} catch (SCServiceException ex) {
			assertEquals("Host:"+host+"  port:"+port, expectedException, "SCServiceException");
		} catch (SCMPValidatorException ex) {
			assertEquals("Host:"+host+"  port:"+port, expectedException, "SCMPValidatorException");
		} catch (InvalidParameterException ex) {
			assertEquals("Host:"+host+"  port:"+port, expectedException, "InvalidParameterException");
		} catch (Exception ex){
			assertEquals("Host:"+host+"  port:"+port+"  Exception:"+ex.getMessage(), expectedException, "Exception");
		}
		assertEquals("Is client attached:", isClientAttached, client.isAttached());
	}

	/**
	 * Description: Attach client with default host and tcp-port.<br>
	 * Expectation:	Client is attached.
	 */
	@Test
	public void attach_1() throws Exception {
		this.testAttach(TestConstants.HOST, TestConstants.PORT_TCP, null, true, null);
	}

	/**
	 * Description: Attach client with default host and http-port.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test
	public void attach_2() throws Exception {
		this.testAttach(TestConstants.HOST, TestConstants.PORT_HTTP, null, false, "SCServiceException");
	}

	/**
	 * Description: Attach client with default host and port zero.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test
	public void attach_4() throws Exception {
		this.testAttach(TestConstants.HOST, 0, null, false, "SCServiceException");
	}

	/**
	 * Description: Attach client with default host and port -1.<br>
	 * Expectation:	Client is not attached and throws SCMPValidatorException.
	 */
	@Test
	public void attach_5() throws Exception {
		this.testAttach(TestConstants.HOST, -1, null, false, "SCMPValidatorException");
	}

	/**
	 * Description: Attach client with default host and port is set to minimum.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test
	public void attach_6() throws Exception {
		this.testAttach(TestConstants.HOST, TestConstants.PORT_MIN, null, false, "SCServiceException");
	}

	/**
	 * Description: Attach client with default host and port is set to maximum allowed.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test
	public void attach_7() throws Exception {
		this.testAttach(TestConstants.HOST, 0xFFFF, null, false, "SCServiceException");
	}

	/**
	 * Description: Attach client with default host and the port is set to maximum + 1.<br>
	 * Expectation:	Client is not attached and throws SCMPValidatorException.
	 */
	@Test
	public void attach_8() throws Exception {
		this.testAttach(TestConstants.HOST, 0xFFFF + 1, null, false, "SCMPValidatorException");
	}

	/**
	 * Description: Attach client with default host and minimum port number.<br>
	 * Expectation:	Client is not attached and throws SCMPValidatorException.
	 */
	@Test
	public void attach_9() throws Exception {
		this.testAttach(TestConstants.HOST, Integer.MIN_VALUE, null, false, "SCMPValidatorException");
	}

	/**
	 * Description: Attach client with default host and maximum port number.<br>
	 * Expectation:	Client is not attached and throws SCMPValidatorException.
	 */
	@Test
	public void attach_10() throws Exception {
		this.testAttach(TestConstants.HOST, Integer.MAX_VALUE, null, false, "SCMPValidatorException");
	}

	/**
	 * Description: Attach client without a host and default tcp-port.<br>
	 * Expectation:	Client is not attached and throws InvalidParameterException.
	 */
	@Test
	public void attach_11() throws Exception {
		this.testAttach(null, TestConstants.PORT_TCP, null, false, "InvalidParameterException");
	}

	/**
	 * Description: Attach client without a host and http-port.<br>
	 * Expectation:	Client is not attached and throws InvalidParameterException.
	 */
	@Test
	public void attach_12() throws Exception {
		this.testAttach(null, TestConstants.PORT_HTTP, null, false, "InvalidParameterException");
	}

	/**
	 * Description: Attach client without a host and port zero.<br>
	 * Expectation:	Client is not attached and throws InvalidParameterException.
	 */
	@Test
	public void attach_13() throws Exception {
		this.testAttach(null, 0, null, false, "InvalidParameterException");
	}

	/**
	 * Description: Attach client without a host and port -1.<br>
	 * Expectation:	Client is not attached and throws InvalidParameterException.
	 */
	@Test
	public void attach_14() throws Exception {
		this.testAttach(null, -1, null, false, "InvalidParameterException");
	}

	/**
	 * Description: Attach client without a host and port is min value.<br>
	 * Expectation:	Client is not attached and throws InvalidParameterException.
	 */
	@Test
	public void attach_15() throws Exception {
		this.testAttach(null, TestConstants.PORT_MIN, null, false, "InvalidParameterException");
	}

	/**
	 * Description: Attach client without a host and set the port to max allowed value.<br>
	 * Expectation:	Client is not attached and throws InvalidParameterException.
	 */
	@Test
	public void attach_16() throws Exception {
		this.testAttach(null, 0xFFFF, null, false, "InvalidParameterException");
	}

	/**
	 * Description: Attach client without a host and set the port to max allowed value + 1.<br>
	 * Expectation:	Client is not attached and throws InvalidParameterException.
	 */
	@Test
	public void attach_17() throws Exception {
		this.testAttach(null, 0xFFFF + 1, null, false, "InvalidParameterException");
	}

	/**
	 * Description: Attach client without a host and use port number "Integer.MIN_VALUE".<br>
	 * Expectation:	Client is not attached and throws InvalidParameterException.
	 */
	@Test
	public void attach_18() throws Exception {
		this.testAttach(null, Integer.MIN_VALUE, null, false, "InvalidParameterException");
	}

	/**
	 * Description: Attach client without a host and use port number "Integer.MAX_VALUE".<br>
	 * Expectation:	Client is not attached and throws InvalidParameterException.
	 */
	@Test
	public void attach_19() throws Exception {
		this.testAttach(null, Integer.MAX_VALUE, null, false, "InvalidParameterException");
	}

	/**
	 * Description: Attach client with empty host name and use default tcp-port number.<br>
	 * Expectation:	Client is attached.
	 */
	@Test
	public void attach_20() throws Exception {
		this.testAttach("",  TestConstants.PORT_TCP, null, true, null);
	}

	/**
	 * Description: Attach client with empty host name and http-port.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test
	public void attach_21() throws Exception {
		this.testAttach("", TestConstants.PORT_HTTP, null, false, "SCServiceException");
	}

	/**
	 * Description: Attach client with empty host name and port zero.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test
	public void attach_22() throws Exception {
		this.testAttach("", 0, null, false, "SCServiceException");
	}

	/**
	 * Description: Attach client with empty host name and port -1.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test
	public void attach_23() throws Exception {
		this.testAttach("", -1, null, false, "SCMPValidatorException");
	}

	/**
	 * Description: Attach client with empty host name and minimum port number.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test
	public void attach_24() throws Exception {
		this.testAttach("", TestConstants.PORT_MIN, null, false, "SCServiceException");
	}

	/**
	 * Description: Attach client with empty host name and maximum allowed port number.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test
	public void attach_25() throws Exception {
		this.testAttach("", 0xFFFF, null, false, "SCServiceException");
	}

	/**
	 * Description: Attach client with empty host name and maximum allowed port number +1.<br>
	 * Expectation:	Client is not attached and throws SCMPValidatorException.
	 */
	@Test
	public void attach_26() throws Exception {
		this.testAttach("", 0xFFFF + 1, null, false, "SCMPValidatorException");
	}

	/**
	 * Description: Attach client with empty host name and use port number "Integer.MIN_VALUE".<br>
	 * Expectation:	Client is not attached and throws SCMPValidatorException.
	 */
	@Test
	public void attach_27() throws Exception {
		this.testAttach("", Integer.MIN_VALUE, null, false, "SCMPValidatorException");
	}

	/**
	 * Description: Attach client with empty host name and use port number "Integer.MAX_VALUE".<br>
	 * Expectation:	Client is not attached and throws SCMPValidatorException.
	 */
	@Test
	public void attach_28() throws Exception {
		this.testAttach("", Integer.MAX_VALUE, null, false, "SCMPValidatorException");
	}

	/**
	 * Description: Attach client with host name "a" and use default tcp-port.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test
	public void attach_29() throws Exception {
		this.testAttach("a", TestConstants.PORT_TCP, null, false, "SCServiceException");
	}

	/**
	 * Description: Attach client with host name "a" and use default http-port.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test
	public void attach_30() throws Exception {
		this.testAttach("a", TestConstants.PORT_HTTP, null, false, "SCServiceException");
	}

	/**
	 * Description: Attach client with host name "a" and port zero.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test
	public void attach_31() throws Exception {
		this.testAttach("a", 0, null, false, "SCServiceException");
	}

	/**
	 * Description: Attach client with host name "a" and port -1.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test
	public void attach_32() throws Exception {
		this.testAttach("a", -1, null, false, "SCMPValidatorException");
	}

	/**
	 * Description: Attach client with host name "a" and minimum port number.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test
	public void attach_33() throws Exception {
		this.testAttach("a", TestConstants.PORT_MIN, null, false, "SCServiceException");
	}

	/**
	 * Description: Attach client with host name "a" and maximum allowed port number.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test
	public void attach_34() throws Exception {
		this.testAttach("a", 0xFFFF, null, false, "SCServiceException");
	}

	/**
	 * Description: Attach client with host name "a" and maximum allowed port number +1.<br>
	 * Expectation:	Client is not attached and throws SCMPValidatorException.
	 */
	@Test
	public void attach_35() throws Exception {
		this.testAttach("a", 0xFFFF + 1, null, false, "SCMPValidatorException");
	}

	/**
	 * Description: Attach client with host name "a" and use port number "Integer.MIN_VALUE".<br>
	 * Expectation:	Client is not attached and throws SCMPValidatorException.
	 */
	@Test
	public void attach_36() throws Exception {
		this.testAttach("a", Integer.MIN_VALUE, null, false, "SCMPValidatorException");
	}

	/**
	 * Description: Attach client with host name "a" and use port number "Integer.MAX_VALUE".<br>
	 * Expectation:	Client is not attached and throws SCMPValidatorException.
	 */
	@Test
	public void attach_37() throws Exception {
		this.testAttach("a", Integer.MAX_VALUE, null, false, "SCMPValidatorException");
	}

	/**
	 * Description: Attach client with arbitrary host name and use default tcp-port.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test
	public void attach_38() throws Exception {
		this.testAttach(TestConstants.pangram, TestConstants.PORT_TCP, null, false, "SCServiceException");
	}

	/**
	 * Description: Attach client with arbitrary host name and use default http-port.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test
	public void attach_39() throws Exception {
		this.testAttach(TestConstants.pangram, TestConstants.PORT_HTTP, null, false, "SCServiceException");
	}

	/**
	 * Description: Attach client with arbitrary host name and port zero.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test
	public void attach_40() throws Exception {
		this.testAttach(TestConstants.pangram, 0, null, false, "SCServiceException");
	}

	/**
	 * Description: Attach client with arbitrary host name and port -1.<br>
	 * Expectation:	Client is not attached and throws SCMPValidatorException.
	 */
	@Test
	public void attach_41() throws Exception {
		this.testAttach(TestConstants.pangram, -1, null, false, "SCMPValidatorException");
	}

	/**
	 * Description: Attach client with arbitrary host name and minimum port number.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test
	public void attach_42() throws Exception {
		this.testAttach(TestConstants.pangram, TestConstants.PORT_MIN, null, false, "SCServiceException");
	}

	/**
	 * Description: Attach client with arbitrary host name and maximum allowed port number.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test
	public void attach_43() throws Exception {
		this.testAttach(TestConstants.pangram, 0xFFFF, null, false, "SCServiceException");
	}

	/**
	 * Description: Attach client with arbitrary host name and maximum allowed port number + 1.<br>
	 * Expectation:	Client is not attached and throws SCMPValidatorException.
	 */
	@Test
	public void attach_44()
			throws Exception {
		this.testAttach(TestConstants.pangram, 0xFFFF + 1, null, false, "SCMPValidatorException");
	}

	/**
	 * Description: Attach client with arbitrary host name and use port number "Integer.MIN_VALUE".<br>
	 * Expectation:	Client is not attached and throws SCMPValidatorException.
	 */
	@Test
	public void attach_45() throws Exception {
		this.testAttach(TestConstants.pangram, Integer.MIN_VALUE, null, false, "SCMPValidatorException");
	}

	/**
	 * Description: Attach client with arbitrary host name and use port number "Integer.MAX_VALUE".<br>
	 * Expectation:	Client is not attached and throws SCMPValidatorException.
	 */
	@Test
	public void attach_46() throws Exception {
		this.testAttach(TestConstants.pangram, Integer.MAX_VALUE, null, false, "SCMPValidatorException");
	}

	/**
	 * Description: Attach client with default host name, default tcp-port and set keepAlive to 1 .<br>
	 * Expectation:	Client is attached.
	 */
	@Test
	public void attach_47() throws Exception {
		this.testAttach(TestConstants.HOST, TestConstants.PORT_TCP, new Integer(1), true, "");
	}

	/**
	 * Description: Attach client with default host name, default tcp-port and set keepAlive to 3600 .<br>
	 * Expectation:	Client is attached.
	 */
	@Test
	public void attach_48() throws Exception {
		this.testAttach(TestConstants.HOST, TestConstants.PORT_TCP, new Integer(3600), true, "");
	}

	/**
	 * Description: Attach client with default host name, default tcp-port and set keepAlive to 0 .<br>
	 * Expectation:	Client is attached.
	 */
	@Test
	public void attach_49() throws Exception {
		this.testAttach(TestConstants.HOST, TestConstants.PORT_TCP, new Integer(0), true, "");
	}

	/**
	 * Description: Attach client with default host name, default tcp-port and set keepAlive to -1 .<br>
	 * Expectation:	Client is not attached and throws SCMPValidatorException.
	 */
	@Test
	public void attach_50() throws Exception {
		this.testAttach(TestConstants.HOST, TestConstants.PORT_TCP, new Integer(-1), false, "SCMPValidatorException");
	}

	/**
	 * Description: Attach client with default host name, default tcp-port and set keepAlive to 1 .<br>
	 * Expectation:	Client is attached.
	 */
	@Test
	public void attach_51() throws Exception {
		this.testAttach(TestConstants.HOST, TestConstants.PORT_TCP, new Integer(1), true, "");
	}

	/**
	 * Description: Attach client with default host name, default tcp-port and set keepAlive to 3601 .<br>
	 * Expectation:	Client is not attached and throws SCMPValidatorException.
	 */
	@Test
	public void attach_52() throws Exception {
		this.testAttach(TestConstants.HOST, TestConstants.PORT_TCP, new Integer(3601), false, "SCMPValidatorException");
	}

	/**
	 * Description: Attach client with default host name, default tcp-port and set keepAlive to "Integer.MIN_VALUE" .<br>
	 * Expectation:	Client is not attached and throws SCMPValidatorException.
	 */
	@Test
	public void attach_53() throws Exception {
		this.testAttach(TestConstants.HOST, TestConstants.PORT_TCP, Integer.MIN_VALUE, false, "SCMPValidatorException");
	}

	/**
	 * Description: Attach client with default host name, default tcp-port and set keepAlive to "Integer.MAX_VALUE" .<br>
	 * Expectation:	Client is not attached and throws SCMPValidatorException.
	 */
	@Test
	public void attach_54() throws Exception {
		this.testAttach(TestConstants.HOST, TestConstants.PORT_TCP, Integer.MAX_VALUE, false, "SCMPValidatorException");
	}
	
}
