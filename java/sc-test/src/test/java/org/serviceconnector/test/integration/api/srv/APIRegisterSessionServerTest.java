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
package org.serviceconnector.test.integration.api.srv;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.api.srv.SCSessionServerCallback;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.integration.api.APIIntegrationSuperServerTest;

public class APIRegisterSessionServerTest extends APIIntegrationSuperServerTest {

	/** The Constant logger. */
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(APIRegisterSessionServerTest.class);

	/**
	 * Description:	register session server on port SC is not listening<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t101_register() throws Exception {
		server = new SCServer(TestConstants.HOST, 9002, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 1, cbk);
	}

	/**
	 * Description:	register session server with no service name<br>
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t102_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(null);
	}

	/**
	 * Description:	register session server with service name = ""<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t103_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer("");
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 1, cbk);
	}
	
	/**
	 * Description:	register session server with service name = " "<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t104_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(" ");
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 1, cbk);
	}

	/**
	 * Description:	register session server with service name = "gaga"<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t105_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer("gaga");
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 1, cbk);
	}
	
	/**
	 * Description:	register session server with callback = null<br>
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t106_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = null;
		sessionServer.register(1, 1, cbk);
	}

	/**
	 * Description:	register session server with 1 session and 1 connection<br>
	 * Expectation:	passes
	 */
	@Test
	public void t107_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 1, cbk);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer.isRegistered());
	}


	/**
	 * Description:	register session server with 10 sessions and 10 connections<br>
	 * Expectation:	passes
	 */
	@Test
	public void t108_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(10, 10, cbk);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer.isRegistered());
	}

	/**
	 * Description:	register session server with 1 session and 10 connections<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t109_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 10, cbk);
	}

	/**
	 * Description:	register session server with 10 session and 20 connections<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t110_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(10, 20, cbk);
	}

	/**
	 * Description:	register session server with 0 session and 1 connection<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t111_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(0, 1, cbk);
	}
	
	/**
	 * Description:	register session server with 1 session and 0 connection<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t112_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 0, cbk);
	}

	/**
	 * Description:	register session server with 0 session and 0 connection<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t113_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(0, 0, cbk);
	}

	/**
	 * Description:	register session server with 1 session and 1 connection twice<br>
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t114_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 1, cbk);
		sessionServer.register(1, 1, cbk);
	}

	/**
	 * Description:	register session server before listener is started<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t115_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 1, cbk);
		server.startListener();
	}
	
	/**
	 * Description:	register /de-register session server with 1 session and 1 connection 1000 times<br>
	 * Expectation:	passes
	 */
	@Test
	public void t116_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		int nr = 1000;
		int sleep = 0;
		for (int i = 0; i < nr; i++) {
			if (((i+1) % 100) == 0) testLogger.info("Register/deregister nr. " + (i+1) + "...");
			sessionServer.register(1, 1, cbk);
			Assert.assertEquals("SessionServer is not registered", true, sessionServer.isRegistered());
			if (sleep > 0) 
				Thread.sleep(sleep);
			sessionServer.deregister();
			Assert.assertEquals("SessionServer is registered", false, sessionServer.isRegistered());
		}
	}

	/**
	 * Description:	register two session servers to two services with two callbacks<br>
	 * Expectation:	passes
	 */
	@Test
	public void t198_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		SCSessionServer sessionServer1 = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk1 = new SesSrvCallback(sessionServer1);
		sessionServer1.register(1, 1, cbk1);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer1.isRegistered());
		
		SCSessionServer sessionServer2 = server.newSessionServer(TestConstants.sesServiceName2);
		SCSessionServerCallback cbk2 = new SesSrvCallback(sessionServer2);
		sessionServer2.register(1, 1, cbk2);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer2.isRegistered());
		
		sessionServer1.deregister();
		sessionServer2.deregister();
	}
	
	/**
	 * Description:	register two session servers to two services with same callback<br>
	 * Expectation:	passes
	 */
	@Test
	public void t199_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		SCSessionServer sessionServer1 = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer1);
		sessionServer1.register(1, 1, cbk);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer1.isRegistered());
		
		SCSessionServer sessionServer2 = server.newSessionServer(TestConstants.sesServiceName2);
		sessionServer2.register(1, 1, cbk);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer2.isRegistered());
		
		sessionServer1.deregister();
		sessionServer2.deregister();
	}

	
	/**
	 * Description:	register session server on port  SC is not listening<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t201_register() throws Exception {
		server = new SCServer(TestConstants.HOST, 9002, TestConstants.PORT_SES_SRV_HTTP, ConnectionType.NETTY_HTTP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 1, cbk);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer.isRegistered());
	}

	/**
	 * Description:	register session server with no service name<br>
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t202_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_HTTP, TestConstants.PORT_SES_SRV_HTTP, ConnectionType.NETTY_HTTP); 
		server.startListener();
		sessionServer = server.newSessionServer(null);
	}

	/**
	 * Description:	register session server with service name = ""<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t203_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_HTTP, TestConstants.PORT_SES_SRV_HTTP, ConnectionType.NETTY_HTTP); 
		server.startListener();
		sessionServer = server.newSessionServer("");
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 1, cbk);
	}
	
	/**
	 * Description:	register session server with service name = " "<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t204_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_HTTP, TestConstants.PORT_SES_SRV_HTTP, ConnectionType.NETTY_HTTP); 
		server.startListener();
		sessionServer = server.newSessionServer(" ");
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 1, cbk);
	}

	/**
	 * Description:	register session server with service name = "gaga"<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t205_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_HTTP, TestConstants.PORT_SES_SRV_HTTP, ConnectionType.NETTY_HTTP); 
		server.startListener();
		sessionServer = server.newSessionServer("gaga");
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 1, cbk);
	}
	
	/**
	 * Description:	register session server with callback = null<br>
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t206_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_HTTP, TestConstants.PORT_SES_SRV_HTTP, ConnectionType.NETTY_HTTP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = null;
		sessionServer.register(1, 1, cbk);
	}

	/**
	 * Description:	register session server with 1 session and 1 connection<br>
	 * Expectation:	passes
	 */
	@Test
	public void t207_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_HTTP, TestConstants.PORT_SES_SRV_HTTP, ConnectionType.NETTY_HTTP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 1, cbk);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer.isRegistered());
	}


	/**
	 * Description:	register session server with 10 sessions and 10 connections<br>
	 * Expectation:	passes
	 */
	@Test
	public void t208_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_HTTP, TestConstants.PORT_SES_SRV_HTTP, ConnectionType.NETTY_HTTP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(10, 10, cbk);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer.isRegistered());
	}

	/**
	 * Description:	register session server with 1 session and 10 connections<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t209_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_HTTP, TestConstants.PORT_SES_SRV_HTTP, ConnectionType.NETTY_HTTP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 10, cbk);
	}

	/**
	 * Description:	register session server with 10 session and 20 connections<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t210_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_HTTP, TestConstants.PORT_SES_SRV_HTTP, ConnectionType.NETTY_HTTP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(10, 20, cbk);
	}

	/**
	 * Description:	register session server with 0 session and 1 connection<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t211_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_HTTP, TestConstants.PORT_SES_SRV_HTTP, ConnectionType.NETTY_HTTP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(0, 1, cbk);
	}
	
	/**
	 * Description:	register session server with 1 session and 0 connection<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t212_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_HTTP, TestConstants.PORT_SES_SRV_HTTP, ConnectionType.NETTY_HTTP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 0, cbk);
	}

	/**
	 * Description:	register session server with 0 session and 0 connection<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t213_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_HTTP, TestConstants.PORT_SES_SRV_HTTP, ConnectionType.NETTY_HTTP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(0, 0, cbk);
	}

	/**
	 * Description:	register session server with 1 session and 1 connection twice<br>
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t214_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_HTTP, TestConstants.PORT_SES_SRV_HTTP, ConnectionType.NETTY_HTTP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 1, cbk);
		sessionServer.register(1, 1, cbk);
	}

	/**
	 * Description:	register session server before listener is started<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t215_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_HTTP, TestConstants.PORT_SES_SRV_HTTP, ConnectionType.NETTY_HTTP); 
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 1, cbk);
		server.startListener();
	}
	
	
	/**
	 * Description:	register /de-register session server with 1 session and 1 connection 1000 times<br>
	 * Expectation:	passes
	 */
	@Test
	public void t216_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_HTTP, TestConstants.PORT_SES_SRV_HTTP, ConnectionType.NETTY_HTTP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		int nr = 1000;
		int sleep = 0;
		for (int i = 0; i < nr; i++) {
			if (((i+1) % 100) == 0) testLogger.info("Register/deregister nr. " + (i+1) + "...");
			sessionServer.register(1, 1, cbk);
			Assert.assertEquals("SessionServer is not registered", true, sessionServer.isRegistered());
			if (sleep > 0) 
				Thread.sleep(sleep);
			sessionServer.deregister();
			Assert.assertEquals("SessionServer is registered", false, sessionServer.isRegistered());
		}
	}

	/**
	 * Description:	register two session servers to two services with two callbacks<br>
	 * Expectation:	passes
	 */
	@Test
	public void t298_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_HTTP, TestConstants.PORT_SES_SRV_HTTP, ConnectionType.NETTY_HTTP); 
		server.startListener();
		SCSessionServer sessionServer1 = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk1 = new SesSrvCallback(sessionServer1);
		sessionServer1.register(1, 1, cbk1);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer1.isRegistered());
		
		SCSessionServer sessionServer2 = server.newSessionServer(TestConstants.sesServiceName2);
		SCSessionServerCallback cbk2 = new SesSrvCallback(sessionServer2);
		sessionServer2.register(1, 1, cbk2);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer2.isRegistered());
		
		sessionServer1.deregister();
		sessionServer2.deregister();
	}
	
	/**
	 * Description:	register two session servers to two services with same callback<br>
	 * Expectation:	passes
	 */
	@Test
	public void t299_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_HTTP, TestConstants.PORT_SES_SRV_HTTP, ConnectionType.NETTY_HTTP); 
		server.startListener();
		SCSessionServer sessionServer1 = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer1);
		sessionServer1.register(1, 1, cbk);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer1.isRegistered());
		
		SCSessionServer sessionServer2 = server.newSessionServer(TestConstants.sesServiceName2);
		sessionServer2.register(1, 1, cbk);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer2.isRegistered());
		
		sessionServer1.deregister();
		sessionServer2.deregister();
	}
}
