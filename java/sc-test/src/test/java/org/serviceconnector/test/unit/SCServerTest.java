package org.serviceconnector.test.unit;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.web.InvalidParameterException;


public class SCServerTest {

	private SCServer server;

	@Before
	public void init() {
		server = null;
	}
	
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
	 * Description:	Set ImmediateConnect with valid value. <br>
	 * Expectation: ImmediateConnect was set
	 */
	@Test
	public void t20_ImmediateConnect() {
		server = new SCServer("localhost", 9000, 9001);
		// TODO method server.isImmediateConnect() is missing
		//assertEquals("ImmediateConnect not equal", false, server.isImmediateConnect());
		server.setImmediateConnect(true);
		//assertEquals("ImmediateConnect not equal", true, server.isImmediateConnect());
		server.setImmediateConnect(false);
		//assertEquals("ImmediateConnect not equal", true, server.isImmediateConnect());
		assertNotNull(server);
	}

	/**
	 * Description:	Start and stop Listener. <br>
	 * Expectation: Listener is stopped
	 */
	@Test
	public void t30_Listener() {
		server = new SCServer("localhost", 9000, 9001);
		assertNotNull(server);
		
		try {
			server.startListener();
			assertEquals("Listener is not running", true, server.isListening());

		} catch (InvalidParameterException ex) {
			assertFalse("InvalidParameterException:" + ex.getMessage(), false);
		} catch (Exception ex) {
			assertFalse("Exception:" + ex.getMessage(), false);
		}
		server.stopListener();
		assertEquals("Listener is running", false, server.isListening());
	}
	

}
