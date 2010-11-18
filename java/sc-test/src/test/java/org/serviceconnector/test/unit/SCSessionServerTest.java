package org.serviceconnector.test.unit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.api.srv.SCSessionServer;


public class SCSessionServerTest {

	private static String hostname = "localhost";
	private static int port = 9000;
	private static int listenerPort = 9001;
	
	private static String serviceName = "local-session-service";
	
	private SCServer server;
	
	@Before
	public void setUp() throws Exception {
		server = null;
		server = new SCServer(hostname, port, listenerPort); 
		server.setKeepAliveIntervalInSeconds(10); // can be set before register
		server.setImmediateConnect(true); // can be set before register
		//server.startListener();
	}

	@After
	public void tearDown() throws Exception {
		//server.stopListener();
		server = null;
	}

	
	private String convertStackTraceToString(Exception ex) {
		String result = "";
		if (ex == null) return result;
		
		StackTraceElement[] traceElements = ex.getStackTrace();
		for(int i=0; i<traceElements.length; i++) {
			result = traceElements[i].toString();
			if (result.indexOf(".test")==-1)	break;
		}
		return result;
	}	
	
	
	/**
	 * Description:	Invoke setConnectionType with empty string<br>
	 * Expectation:	connectionType was set to empty string
	 */
	@Test
	public void temp01_newSessionServer(){
		try {
			server.startListener(); // regular
			sessionServer = server.newSessionServer(serviceName);
			assertNotNull("No SessionServer", sessionServer);
			server.stopListener();
		}
		catch (Exception ex){
			assertFalse("Exception on: " + convertStackTraceToString(ex), true);
		}
			
	}

	/**
	 * Description:	Invoke setConnectionType with empty string<br>
	 * Expectation:	connectionType was set to empty string
	 */
	@Test
	public void t01_newSessionServer() {
		SCSessionServer sessionServer = server.newSessionServer(serviceName);
		assertNotNull("No SessionServer", sessionServer);
	}

	@Test
	public void t02_newSessionServer() {
		sessionServer = server.newSessionServer(serviceName);
		assertNotNull("No SessionServer", sessionServer);
		try {
			sessionServer.destroy();
		}
		catch (Exception ex){
			assertFalse("Exception on: " + convertStackTraceToString(ex), true);
		}
	}

	@Test
	public void t10_HostPort() {
		try {
			sessionServer = server.newSessionServer(serviceName);
			assertEquals("SessionServer Host", "localhost", sessionServer.getHost());
			assertEquals("SessionServer Port", 9000, sessionServer.getPort());
		}
		catch (Exception ex){
			assertFalse("Exception on: " + convertStackTraceToString(ex), true);
		}
	}	

	@Test
	public void t11_HostPort() {
		try {
			sessionServer = server.newSessionServer(serviceName);
			assertEquals("SC Host", "localhost", sessionServer.getSCHost());
			assertEquals("SC Port", 9000, sessionServer.getSCPort());
		}
		catch (Exception ex){
			assertFalse("Exception on: " + convertStackTraceToString(ex), true);
		}
	}	

	/**
	 * Description:	Invoke setConnectionType with blank string<br>
	 * Expectation:	connectionType was set to blank
	 */
	@Test
	public void t20_ConnectionType() {
		sessionServer = server.newSessionServer(serviceName);
		sessionServer.setConnectionType(" ");
		assertEquals("ConnectionType not equal,", " ", server.getConnectionType());
	}

	/**
	 * Description:	Invoke setConnectionType with "a" string<br>
	 * Expectation:	connectionType was set to "a"
	 */
	@Test
	public void t21_ConnectionType() {
		sessionServer = server.newSessionServer(serviceName);
		sessionServer.setConnectionType("a");
		assertEquals("ConnectionType not equal,", "a", server.getConnectionType());
	}

	/**
	 * Description:	Invoke setConnectionType with "aaa" string<br>
	 * Expectation:	connectionType was set to "aaa"
	 */
	@Test
	public void t22_ConnectionType() {
		sessionServer = server.newSessionServer(serviceName);
		sessionServer.setConnectionType("aaa");
		assertEquals("ConnectionType not equal,", "aaa", server.getConnectionType());
	}

	/**
	 * Description:	Invoke setConnectionType with characters "a" and lengths "Short.MAX_VALUE"<br>
	 * Expectation:	connectionType was set
	 */
	@Test
	public void t23_ConnectionType() {
		sessionServer = server.newSessionServer(serviceName);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Short.MAX_VALUE; i++) {
			sb.append('a');
		}
		sessionServer.setConnectionType(sb.toString());
		assertEquals("ConnectionType not equal,", sb.toString(), server.getConnectionType());
	}

	/**
	 * Description:	Invoke setImmediateConnect with "true"<br>
	 * Expectation:	ImmediateConnect was set to "true"
	 */
	@Test
	public void t30_ImmediateConnec() {
		sessionServer = server.newSessionServer(serviceName);
		sessionServer.setImmediateConnect(true);
		assertEquals("ImmediateConnect", true, sessionServer.isImmediateConnect());
	}

	/**
	 * Description:	Invoke setImmediateConnect with "false"<br>
	 * Expectation:	ImmediateConnect was set to "false"
	 */
	@Test
	public void t31_ImmediateConnect() {
		sessionServer = server.newSessionServer(serviceName);
		sessionServer.setImmediateConnect(false);
		assertEquals("ImmediateConnect", false, sessionServer.isImmediateConnect());
	}

	
}
