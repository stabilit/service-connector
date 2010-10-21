package org.serviceconnector.test.unit;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.Constants;
import org.serviceconnector.api.srv.SCSessionServer;


public class SCServerTest {

	private SCSessionServer server;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		server = new SCSessionServer();
	}
	
	/**
	 * Description:	Check initial values set by constructor<br>
	 * Expectation:	Initial values are properly set.
	 */
	@Test
	public void constructor_1(){
		assertEquals(false, server.isListening());
		assertEquals(Constants.DEFAULT_SERVER_CON, server.getConnectionType());
		assertEquals(true, server.isImmediateConnect());
		assertEquals(null, server.getHost());
		assertEquals(Constants.DEFAULT_KEEP_ALIVE_INTERVAL, server.getKeepAliveIntervalInSeconds());
		assertEquals(-1, server.getPort());		
	}

	/**
	 * Description:	Invoke setConnectionType with null parameter <br>
	 * Expectation:	connectionType was set to null
	 */
	@Test
	public void setConnectionType_1() {
		((SCSessionServer) server).setConnectionType(null);
		assertEquals(null, server.getConnectionType());
	}

	/**
	 * Description:	Invoke setConnectionType with empty string<br>
	 * Expectation:	connectionType was set to empty string
	 */
	@Test
	public void setConnectionType_2() {
		((SCSessionServer) server).setConnectionType("");
		assertEquals("", server.getConnectionType());
	}

	/**
	 * Description:	Invoke setConnectionType with blank string<br>
	 * Expectation:	connectionType was set to blank
	 */
	@Test
	public void setConnectionType_3() {
		((SCSessionServer) server).setConnectionType(" ");
		assertEquals(" ", server.getConnectionType());
	}

	/**
	 * Description:	Invoke setConnectionType with "a" string<br>
	 * Expectation:	connectionType was set to "a"
	 */
	@Test
	public void setConnectionType_5() {
		((SCSessionServer) server).setConnectionType("a");
		assertEquals("a", server.getConnectionType());
	}

	/**
	 * Description:	Invoke setConnectionType with "aaa" string<br>
	 * Expectation:	connectionType was set to "aaa"
	 */
	@Test
	public void setConnectionType_6() {
		((SCSessionServer) server).setConnectionType("aaa");
		assertEquals("aaa", server.getConnectionType());
	}

	/**
	 * Description:	Invoke setConnectionType with characters "a" and lengths "Short.MAX_VALUE"<br>
	 * Expectation:	connectionType was set
	 */
	@Test
	public void setConnectionType_7() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Short.MAX_VALUE; i++) {
			sb.append('a');
		}
		((SCSessionServer) server).setConnectionType(sb.toString());
		assertEquals(sb.toString(), server.getConnectionType());
	}

	/**
	 * Description:	Invoke setImmediateConnect with "true"<br>
	 * Expectation:	ImmediateConnect was set to "true"
	 */
	@Test
	public void setImmediateConnect_1() {
		server.setImmediateConnect(true);
		assertEquals(true, server.isImmediateConnect());
	}

	/**
	 * Description:	Invoke setImmediateConnect with "false"<br>
	 * Expectation:	ImmediateConnect was set to "false"
	 */
	@Test
	public void setImmediateConnect_2() {
		server.setImmediateConnect(false);
		assertEquals(false, server.isImmediateConnect());
	}

}
