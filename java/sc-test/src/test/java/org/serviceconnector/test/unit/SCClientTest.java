/**
 * 
 */
package org.serviceconnector.test.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.Constants;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.net.ConnectionType;

/**
 * @author FJurnecka
 * 
 */
public class SCClientTest {

	private SCClient client;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		client = new SCClient();
	}

	/**
	 * Description:	Check initial values set by constructor<br>
	 * Expectation:	Initial values are properly set.
	 */
	@Test
	public void construtor_1() {
		assertEquals(null, client.getHost());
		assertEquals(-1, client.getPort());
		assertEquals(ConnectionType.DEFAULT_CLIENT_CONNECTION_TYPE, client.getConnectionType());
		assertEquals(Constants.DEFAULT_KEEP_ALIVE_INTERVAL, client.getKeepAliveIntervalInSeconds());
		assertEquals(Constants.DEFAULT_MAX_CONNECTIONS, client.getMaxConnections());
		assertEquals(false, client.isAttached());
		assertNotNull(client.getSCContext());
	}

	/**
	 * Description:	Invoke setConnectionType with null parameter <br>
	 * Expectation:	connectionType was set to null
	 */
	@Test
	public void setConnectionType_1() {
		client.setConnectionType(null);
		assertEquals(null, client.getConnectionType());
	}

	/**
	 * Description:	Invoke setConnectionType with empty string<br>
	 * Expectation:	connectionType was set to empty string
	 */
	@Test
	public void setConnectionType_2() {
		client.setConnectionType(new String());
		assertEquals("", client.getConnectionType());
	}

	/**
	 * Description:	Invoke setConnectionType with blank string<br>
	 * Expectation:	connectionType was set to blank
	 */
	@Test
	public void setConnectionType_3() {
		client.setConnectionType(" ");
		assertEquals(" ", client.getConnectionType());
	}

	/**
	 * Description:	Invoke setConnectionType with "a" string<br>
	 * Expectation:	connectionType was set to "a"
	 */
	@Test
	public void setConnectionType_4() {
		client.setConnectionType("a");
		assertEquals("a", client.getConnectionType());
	}

	/**
	 * Description:	Invoke setConnectionType with some string<br>
	 * Expectation:	connectionType was set to some value
	 */
	@Test
	public void setConnectionType_5() {
		client.setConnectionType(TestConstants.HOST);
		assertEquals(TestConstants.HOST, client.getConnectionType());
	}

	/**
	 * Description:	Invoke setMaxConnections with 0 value<br>
	 * Expectation:	throws validation exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void setMaxConnections_1() throws SCMPValidatorException {
		client.setMaxConnections(0);
	}

	/**
	 * Description:	Invoke setMaxConnections with value = MIN<br>
	 * Expectation:	throws validation exception
	 */
	@Test
	public void setMaxConnections_2() throws SCMPValidatorException {
		Throwable e = null;

		/*
		 * Description:	Invoke setMaxConnections with value = MIN<br>
		 * Expectation:	throws validation exception
		 */
		e = null;
		try {
			client.setMaxConnections(Integer.MIN_VALUE);
		} catch (Throwable ex) {
			e = ex;
		}
		assertEquals("MIN_VALUE", true, e instanceof SCMPValidatorException);

		/*
		 * Description:	Invoke setMaxConnections with value = MAX<br>
		 * Expectation:	value = MAX was properly set
		 */
		assertEquals(Integer.MAX_VALUE, client.getMaxConnections());
	}

	/**
	 * Description:	Invoke setMaxConnections with value = MAX<br>
	 * Expectation:	value = MAX was properly set
	 */
	@Test
	public void setMaxConnections_3() throws SCMPValidatorException {
		client.setMaxConnections(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, client.getMaxConnections());
	}

	/**
	 * Description:	Invoke setMaxConnections with value = -1<br>
	 * Expectation:	throws validation exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void setMaxConnections_4() throws SCMPValidatorException {
		client.setMaxConnections(-1);
	}

	/**
	 * Description: Invoke setMaxConnections with value = 1<br>
	 * Expectation: value = 1 was properly set
	 */
	@Test
	public void setMaxConnections_5() throws SCMPValidatorException {
		client.setMaxConnections(1);
		assertEquals(1, client.getMaxConnections());
	}
}
