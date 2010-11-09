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


public class AttachConnectionTypeHttpTest {
	
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(AttachConnectionTypeHttpTest.class);

	private int threadCount = 0;
	private SCClient client;

	private static Process scProcess;
	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
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
//		threadCount = Thread.activeCount();
		client = new SCClient();
	}
	
	@After
	public void tearDown() throws Exception {
		try {
			client.detach();
		} catch (Exception e) {} 
		client = null;
//		assertEquals("number of threads", threadCount, Thread.activeCount());
	}

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
		assertEquals(isClientAttached, client.isAttached());
	}

	/**
	 * Description: Attach client with default host and Http-port.<br>
	 * Expectation:	Client is attached.
	 */
	@Test
	public void attach_1() throws Exception {
		this.testAttach(TestConstants.HOST, TestConstants.PORT_HTTP, null, true, null);
	}

	/**
	 * Description: Attach client with default host and Tcp-port.<br>
	 * Expectation:	Client is not attached and throws SCServiceException.
	 */
	@Test
	public void attach_2() throws Exception {
		this.testAttach(TestConstants.HOST, TestConstants.PORT_TCP, null, false, "SCServiceException");
	}

	/**
	 * Description: Change "connection type" to "netty.tcp" and attach client with default host and Tcp-port.<br>
	 * Expectation:	Client is attached.
	 */
	@Test
	public void attach_3() throws Exception {
		((SCClient) client).setConnectionType("netty.tcp");
		this.testAttach(TestConstants.HOST, TestConstants.PORT_TCP, null, true, null);
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
	 * Description: Attach client without a host and default port.<br>
	 * Expectation:	Client is not attached and throws InvalidParameterException.
	 */
	@Test
	public void attach_11() throws Exception {
		this.testAttach(null, TestConstants.PORT_HTTP, null, false, "InvalidParameterException");
	}

	/**
	 * Description: Attach client without a host and TCP-port.<br>
	 * Expectation:	Client is not attached and throws InvalidParameterException.
	 */
	@Test
	public void attach_12() throws Exception {
		this.testAttach(null, TestConstants.PORT_TCP, null, false, "InvalidParameterException");
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
	 * Description: Attach client with empty host name and use default port number.<br>
	 * Expectation:	Client is attached.
	 */
	@Test
	public void attach_20() throws Exception {
		this.testAttach("",  TestConstants.PORT_HTTP, null, true, null);
	}

	@Test
	public void attach_hostEmptyPortTcp_notAttachedThrowsException() throws Exception {
		this.testAttach("", TestConstants.PORT_TCP, null, false, "SCServiceException");
	}

	@Test
	public void attach_hostEmptyPort0_notAttachedThrowsException() throws Exception {
		this.testAttach("", 0, null, false, "SCServiceException");
	}

	@Test
	public void attach_hostEmptyPortMinus1_notAttachedThrowsException() throws Exception {
		this.testAttach("", -1, null, false, "SCMPValidatorException");
	}

	@Test
	public void attach_hostEmptyPortMin_notAttachedThrowsException() throws Exception {
		this.testAttach("", TestConstants.PORT_MIN, null, false, "SCServiceException");
	}

	@Test
	public void attach_hostEmptyPortMaxAllowed_notAttachedThrowsException() throws Exception {
		this.testAttach("", 0xFFFF, null, false, "SCServiceException");
	}

	@Test
	public void attach_hostEmptyPortMaxAllowedPlus1_notAttachedThrowsException() throws Exception {
		this.testAttach("", 0xFFFF + 1, null, false, "SCMPValidatorException");
	}

	@Test
	public void attach_hostEmptyPortIntMin_notAttachedThrowsException() throws Exception {
		this.testAttach("", Integer.MIN_VALUE, null, false, "SCMPValidatorException");
	}

	@Test
	public void attach_hostEmptyPortIntMax_notAttachedThrowsException() throws Exception {
		this.testAttach("", Integer.MAX_VALUE, null, false, "SCMPValidatorException");
	}

	@Test
	public void attach_hostAPortHttp_notAttachedThrowsException() throws Exception {
		this.testAttach("a", TestConstants.PORT_HTTP, null, false, "SCServiceException");
	}

	@Test
	public void attach_hostAPortTcp_notAttachedThrowsException() throws Exception {
		this.testAttach("a", TestConstants.PORT_TCP, null, false, "SCServiceException");
	}

	@Test
	public void attach_hostAPort0_notAttachedThrowsException() throws Exception {
		this.testAttach("a", 0, null, false, "SCServiceException");
	}

	@Test
	public void attach_hostAPortMinus1_notAttachedThrowsException() throws Exception {
		this.testAttach("a", -1, null, false, "SCMPValidatorException");
	}

	@Test
	public void attach_hostAPortMin_notAttachedThrowsException() throws Exception {
		this.testAttach("a", TestConstants.PORT_MIN, null, false, "SCServiceException");
	}

	@Test
	public void attach_hostAPortMaxAllowed_notAttachedThrowsException() throws Exception {
		this.testAttach("a", 0xFFFF, null, false, "SCServiceException");
	}

	@Test
	public void attach_hostAPortMaxAllowedPlus1_notAttachedThrowsException() throws Exception {
		this.testAttach("a", 0xFFFF + 1, null, false, "SCMPValidatorException");
	}

	@Test
	public void attach_hostAPortIntMin_notAttachedThrowsException() throws Exception {
		this.testAttach("a", Integer.MIN_VALUE, null, false, "SCMPValidatorException");
	}

	@Test
	public void attach_hostAPortIntMax_notAttachedThrowsException() throws Exception {
		this.testAttach("a", Integer.MAX_VALUE, null, false, "SCMPValidatorException");
	}

	@Test
	public void attach_hostArbitraryPortHttp_notAttachedThrowsException() throws Exception {
		this.testAttach(TestConstants.pangram, TestConstants.PORT_HTTP, null, false, "SCServiceException");
	}

	@Test
	public void attach_hostArbitraryPortTcp_notAttachedThrowsException() throws Exception {
		this.testAttach(TestConstants.pangram, TestConstants.PORT_TCP, null, false, "SCServiceException");
	}

	@Test
	public void attach_hostArbitraryPort0_notAttachedThrowsException() throws Exception {
		this.testAttach(TestConstants.pangram, 0, null, false, "SCServiceException");
	}

	@Test
	public void attach_hostArbitraryPortMinus1_notAttachedThrowsException() throws Exception {
		this.testAttach(TestConstants.pangram, -1, null, false, "SCMPValidatorException");
	}

	@Test
	public void attach_hostArbitraryPortMin_notAttachedThrowsException() throws Exception {
		this.testAttach(TestConstants.pangram, TestConstants.PORT_MIN, null, false, "SCServiceException");
	}

	@Test
	public void attach_hostArbitraryPortMaxAllowed_notAttachedThrowsException() throws Exception {
		this.testAttach(TestConstants.pangram, 0xFFFF, null, false, "SCServiceException");
	}

	@Test
	public void attach_hostArbitraryPortMaxAllowedPlus1_notAttachedThrowsException()
			throws Exception {
		this.testAttach(TestConstants.pangram, 0xFFFF + 1, null, false, "SCMPValidatorException");
	}

	@Test
	public void attach_hostArbitraryPortMinValue_notAttachedThrowsException() throws Exception {
		this.testAttach(TestConstants.pangram, Integer.MIN_VALUE, null, false, "SCMPValidatorException");
	}

	@Test
	public void attach_hostArbitraryPortIntMax_notAttachedThrowsException() throws Exception {
		this.testAttach(TestConstants.pangram, Integer.MAX_VALUE, null, false, "SCMPValidatorException");
	}

	public void attach_hostDefaultPortHttpKeepAlive1_attached() throws Exception {
		this.testAttach(TestConstants.HOST, TestConstants.PORT_HTTP, new Integer(1), true, "");
	}

	public void attach_hostDefaultPortHttpKeepAlive3600_attached() throws Exception {
		this.testAttach(TestConstants.HOST, TestConstants.PORT_HTTP, new Integer(3600), true, "");
	}

	@Test
	public void attach_KeepAlive0_notAttached() throws Exception {
		this.testAttach(TestConstants.HOST, TestConstants.PORT_HTTP, new Integer(0), true, "");
	}

	@Test
	public void attach_hostDefaultPortHttpKeepAliveMinus1_notAttached() throws Exception {
		this.testAttach(TestConstants.HOST, TestConstants.PORT_HTTP, new Integer(-1), false, "SCMPValidatorException");
	}

	@Test
	public void attach_hostDefaultPortHttpKeepAlive1_isAttached() throws Exception {
		this.testAttach(TestConstants.HOST, TestConstants.PORT_HTTP, new Integer(1), true, "");
	}

	@Test
	public void attach_hostDefaultPortHttpKeepAlive3601_notAttached() throws Exception {
		this.testAttach(TestConstants.HOST, TestConstants.PORT_HTTP, new Integer(3601), false, "SCMPValidatorException");
	}

	@Test
	public void attach_hostDefaultPortHttpKeepAliveIntMin_notAttached() throws Exception {
		this.testAttach(TestConstants.HOST, TestConstants.PORT_HTTP, Integer.MIN_VALUE, false, "SCMPValidatorException");
	}

	@Test
	public void attach_hostDefaultPortHttpKeepAliveIntMax_notAttached() throws Exception {
		this.testAttach(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.PORT_HTTP, false, "SCMPValidatorException");
	}
}
