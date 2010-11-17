package org.serviceconnector.test.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.Constants;
import org.serviceconnector.api.srv.SCPublishServerCallback;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.net.ConnectionType;


public class SCServerTest {

	private SCServer server;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void init() {
		server = null;
	}
	
	/**
	 * Description:	Check initial values set by constructor<br>
	 * Expectation:	Initial values are properly set.
	 */
	public void constructor_1(){
		/*
		assertEquals(false, server.isListening());
		assertEquals(ConnectionType.DEFAULT_SERVER_CONNECTION_TYPE, server.getConnectionType());
		assertEquals(true, server.isImmediateConnect());
		assertEquals(null, server.getHost());
		assertEquals(Constants.DEFAULT_KEEP_ALIVE_INTERVAL, server.getKeepAliveIntervalInSeconds());
		assertEquals(-1, server.getPort());
		*/		
	}

	/*
		SCServer sc = new SCServer("localhost", 9000, 9001); // regular, defaults documented in javadoc
		sc = new SCServer("localhost", 9000, 9001, ConnectionType.NETTY_TCP); // alternative with connection type

		sc.setKeepAliveIntervalInSeconds(10); // can be set before register
		sc.setImmediateConnect(true); // can be set before register
		try {
			sc.startListener(); // regular
			publishSrv = sc.newPublishServer(serviceName); // no other params possible

			int maxSessions = 10;
			int maxConnections = 5;
			SCPublishServerCallback cbk = new SrvCallback(publishSrv);

			publishSrv.registerServer(maxSessions, maxConnections, cbk); // regular

	 */
	
	/**
	 * Description:	Invoke SCServer constructor with host, port and listener port. <br>
	 * Expectation: Host, Port and listener Port was set
	 */
	@Test
	public void t01_construtor() {
		server = new SCServer("localhost", 9000, 9001);
		assertEquals("Host not equal", "localhost", server.getSCHost());
		assertEquals("Port not equal", 9000, server.getSCPort());
		assertEquals("Listener Port not equal", 9001, server.getListenerPort());
	}

	/**
	 * Description:	Invoke SCServer constructor with host=null, port and listener port. <br>
	 * Expectation: Host, Port and listener Port was set
	 */
	@Test
	public void t02_construtor() {
		server = new SCServer(null, 9000, 9001);
		assertEquals("Host not equal", null, server.getSCHost());
		assertEquals("Port not equal", 9000, server.getSCPort());
		assertEquals("Listener Port not equal", 9001, server.getListenerPort());
	}

	/**
	 * Description:	Invoke SCServer constructor with host, port=Integer.MIN_VALUE and listener port. <br>
	 * Expectation: Host, Port and listener Port was set
	 */
	@Test
	public void t03_construtor() {
		server = new SCServer("localhost", Integer.MIN_VALUE, 9001);
		assertEquals("Host not equal", "localhost", server.getSCHost());
		assertEquals("Port not equal", Integer.MIN_VALUE, server.getSCPort());
		assertEquals("Listener Port not equal", 9001, server.getListenerPort());
	}

	/**
	 * Description:	Invoke SCServer constructor with host, port and listener port=Integer.MIN_VALUE. <br>
	 * Expectation: Host, Port and listener Port was set
	 */
	@Test
	public void t04_construtor() {
		server = new SCServer("localhost", 9000, Integer.MIN_VALUE);
		assertEquals("Host not equal", "localhost", server.getSCHost());
		assertEquals("Port not equal", 9000, server.getSCPort());
		assertEquals("Listener Port not equal", Integer.MIN_VALUE, server.getListenerPort());
	}
	
	/**
	 * Description:	Invoke SCServer constructor with host, port, listener port and connection type. <br>
	 * Expectation: Host, Port, listener Port and connection type was set
	 */
	@Test
	public void t05_construtor() {
		server = new SCServer("localhost", 9000, 9001, ConnectionType.NETTY_TCP);
		assertEquals("Connection Type not equal", ConnectionType.NETTY_TCP, server.getConnectionType());
		assertNotNull(server);
	}
	
	/**
	 * Description:	Invoke SCServer constructor with host, port, listener port and connection type=null. <br>
	 * Expectation: Host, Port, listener Port and connection type was set
	 */
	@Test
	public void t06_construtor() {
		server = new SCServer("localhost", 9000, 9001, null);
		assertEquals("Connection Type not equal", null, server.getConnectionType());
		assertNotNull(server);
	}

	/**
	 * Description:	Set KeepAliveInterval with valid value. <br>
	 * Expectation: KeepAliveInterval was set
	 */
	@Test
	public void t10_KeepAliveInterval() {
		server = new SCServer("localhost", 9000, 9001);
		server.setKeepAliveIntervalInSeconds(10);
		assertEquals("KeepAliveInterval not equal", 10, server.getKeepAliveIntervalSeconds());
		assertNotNull(server);
	}

	/**
	 * Description:	Set KeepAliveInterval with invalid value. <br>
	 * Expectation: KeepAliveInterval was set
	 */
	@Test
	public void t11_KeepAliveInterval() {
		server = new SCServer("localhost", 9000, 9001);
		server.setKeepAliveIntervalInSeconds(-1);
		assertEquals("KeepAliveInterval not equal", -1, server.getKeepAliveIntervalSeconds());
		assertNotNull(server);
	}

	/**
	 * Description:	Set KeepAliveInterval with valid value. <br>
	 * Expectation: KeepAliveInterval was set
	 */
	@Test
	public void t20_ImmediateConnect() {
		server = new SCServer("localhost", 9000, 9001);
		//assertEquals("ImmediateConnect not equal", false, server.isImmediateConnect());
		server.setImmediateConnect(true);
		//assertEquals("ImmediateConnect not equal", true, server.isImmediateConnect());
		assertNotNull(server);
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
