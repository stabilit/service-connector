/*
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 */
package org.serviceconnector.test.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.api.srv.SCSessionServer;


public class SCSessionServerTest {
	
	private SCServer server;
	
	@Before
	public void beforeOneTest() throws Exception {
		server = null;
		server = new SCServer(TestConstants.LOCALHOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER); 
		server.setKeepAliveIntervalInSeconds(10); // can be set before register
		server.setImmediateConnect(true); // can be set before register
		server.startListener();
	}

	@After
	public void afterOneTest() throws Exception {
		server.stopListener(); 
		server = null;
	}

	
	/**
	 * Return the first TraceElement from the Exception, without ".test" into the package name.
	 * @param ex
	 * @return String
	 */
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
	public void t01_newSessionServer() {
		SCSessionServer sessionServer = server.newSessionServer(TestConstants.sessionServiceNames);
		assertNotNull("No SessionServer", sessionServer);
	}

	/**
	 * Description:	Invoke sessionServer.destroy()<br>
	 * Expectation:	The SessionServer is destroyed.
	 */
	@Test
	public void t02_newSessionServer() {
		SCSessionServer sessionServer = server.newSessionServer(TestConstants.sessionServiceNames);
		assertNotNull("No SessionServer", sessionServer);
		try {
			sessionServer.destroy();
		}
		catch (Exception ex){
			assertFalse("Exception on: " + convertStackTraceToString(ex), true);
		}
	}

	/**
	 * Description:	Check the Host and Port<br>
	 * Expectation:	The Host and Port have a value.
	 */	
	@Test
	public void t10_HostPort() {
		try {
			SCSessionServer sessionServer = server.newSessionServer(TestConstants.sessionServiceNames);
			assertNotNull("Can not create a new SessionServer!", sessionServer);
			// TODO check function getHost on SCSessionServer.java
			assertEquals("SessionServer Host", TestConstants.LOCALHOST, sessionServer.getHost());
			// TODO check function getPort on SCSessionServer.java
			assertEquals("SessionServer Port", TestConstants.PORT_TCP, sessionServer.getPort());
		}
		catch (Exception ex){
			assertFalse("Exception on: " + convertStackTraceToString(ex), true);
		}
	}	

	/**
	 * Description:	Check the SCHost and SCPort<br>
	 * Expectation:	The SCHost and SCPort have a value.
	 */	
	@Test
	public void t11_HostPort() {
		try {
			SCSessionServer sessionServer = server.newSessionServer(TestConstants.sessionServiceNames);
			assertNotNull("Can not create a new SessionServer!", sessionServer);

			// TODO check getSCHost getHost on SCSessionServer.java
			assertEquals("SC Host", TestConstants.LOCALHOST, sessionServer.getSCHost());
			// TODO check getSCPort getHost on SCSessionServer.java
			assertEquals("SC Port", TestConstants.PORT_TCP, sessionServer.getSCPort());
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
		SCSessionServer sessionServer = server.newSessionServer(TestConstants.sessionServiceNames);
		sessionServer.setConnectionType(" ");
		assertEquals("ConnectionType not equal,", " ", sessionServer.getConnectionType());
	}

	/**
	 * Description:	Invoke setConnectionType with "a" string<br>
	 * Expectation:	connectionType was set to "a"
	 */
	@Test
	public void t21_ConnectionType() {
		SCSessionServer sessionServer = server.newSessionServer(TestConstants.sessionServiceNames);
		sessionServer.setConnectionType("a");
		assertEquals("ConnectionType not equal,", "a", sessionServer.getConnectionType());
	}

	/**
	 * Description:	Invoke setConnectionType with "aaa" string<br>
	 * Expectation:	connectionType was set to "aaa"
	 */
	@Test
	public void t22_ConnectionType() {
		SCSessionServer sessionServer = server.newSessionServer(TestConstants.sessionServiceNames);
		sessionServer.setConnectionType("aaa");
		assertEquals("ConnectionType not equal,", "aaa", sessionServer.getConnectionType());
	}

	/**
	 * Description:	Invoke setConnectionType with characters "a" and lengths "Short.MAX_VALUE"<br>
	 * Expectation:	connectionType was set
	 */
	@Test
	public void t23_ConnectionType() {
		SCSessionServer sessionServer = server.newSessionServer(TestConstants.sessionServiceNames);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Short.MAX_VALUE; i++) {
			sb.append('a');
		}
		sessionServer.setConnectionType(sb.toString());
		assertEquals("ConnectionType not equal,", sb.toString(), sessionServer.getConnectionType());
	}

	/**
	 * Description:	Invoke setImmediateConnect with "true"<br>
	 * Expectation:	ImmediateConnect was set to "true"
	 */
	@Test
	public void t30_ImmediateConnec() {
		SCSessionServer sessionServer = server.newSessionServer(TestConstants.sessionServiceNames);
		sessionServer.setImmediateConnect(true);
		assertEquals("ImmediateConnect", true, sessionServer.isImmediateConnect());
	}

	/**
	 * Description:	Invoke setImmediateConnect with "false"<br>
	 * Expectation:	ImmediateConnect was set to "false"
	 */
	@Test
	public void t31_ImmediateConnect() {
		SCSessionServer sessionServer = server.newSessionServer(TestConstants.sessionServiceNames);
		sessionServer.setImmediateConnect(false);
		assertEquals("ImmediateConnect", false, sessionServer.isImmediateConnect());
	}

	
}
