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
package org.serviceconnector.test.integration.srv;

import static org.junit.Assert.assertEquals;

import java.security.InvalidParameterException;

import javax.activity.InvalidActivityException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.srv.SCPublishServer;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.api.srv.SCSessionServerCallback;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;

public class RegisterServerTest {

	/** The Constant testLogger. */
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(RegisterServerTest.class);

	private static ProcessesController ctrl;
	private static ProcessCtx scCtx;
	private SCServer server;
	private SCSessionServer sessionServer;
	private SCPublishServer publishServer;
	private int threadCount = 0;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
	}

	@Before
	public void beforeOneTest() throws Exception {
		threadCount = Thread.activeCount();
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			sessionServer.deregister();
		} catch (Exception e) {}
		sessionServer = null;
		try {
			server.stopListener();
		} catch (Exception e) {}
		server = null;
//		assertEquals("number of threads", threadCount, Thread.activeCount());
		testLogger.info("Number of threads :" + Thread.activeCount() + " created :"+(Thread.activeCount() - threadCount));
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		try {
			ctrl.stopSC(scCtx);
			scCtx = null;
		} catch (Exception e) {}
		ctrl = null;
	}	

	/**
	 * Description:	register session server on port  SC is not listening<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t101_register() throws Exception {
		server = new SCServer(TestConstants.HOST, 9002, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new CallBack(sessionServer);
		sessionServer.register(1, 1, cbk);
		assertEquals("SessionServer is not registered", true, sessionServer.isRegistered());
	}

	/**
	 * Description:	register session server with no service name<br>
	 * Expectation:	throws InvalidParameterException
	 */
	@Test (expected = InvalidParameterException.class)
	public void t102_register() throws Exception {
		server = new SCServer(TestConstants.HOST, 9002, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(null);
	}

	/**
	 * Description:	register session server with service name = ""<br>
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t103_register() throws Exception {
		server = new SCServer(TestConstants.HOST, 9002, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer("");
	}
	
	/**
	 * Description:	register session server with service name = " "<br>
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t104_register() throws Exception {
		server = new SCServer(TestConstants.HOST, 9002, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(" ");
	}

	/**
	 * Description:	register session server with callback = null<br>
	 * Expectation:	throws InvalidParameterException
	 */
	@Test (expected = InvalidParameterException.class)
	public void t105_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP); 
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
	public void t106_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new CallBack(sessionServer);
		sessionServer.register(1, 1, cbk);
		assertEquals("SessionServer is not registered", true, sessionServer.isRegistered());
	}


	/**
	 * Description:	register session server with 10 sessions and 10 connections<br>
	 * Expectation:	passes
	 */
	@Test
	public void t107_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new CallBack(sessionServer);
		sessionServer.register(10, 10, cbk);
		assertEquals("SessionServer is not registered", true, sessionServer.isRegistered());
	}

	/**
	 * Description:	register session server with 1 session and 10 connections<br>
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t108_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new CallBack(sessionServer);
		sessionServer.register(1, 10, cbk);
	}

	/**
	 * Description:	register session server with 10 session and 20 connections<br>
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t109_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new CallBack(sessionServer);
		sessionServer.register(10, 20, cbk);
	}

	/**
	 * Description:	register session server with 0 session and 1 connection<br>
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t110_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new CallBack(sessionServer);
		sessionServer.register(0, 1, cbk);
	}
	
	/**
	 * Description:	register session server with 1 session and 0 connection<br>
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t111_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new CallBack(sessionServer);
		sessionServer.register(1, 0, cbk);
	}

	/**
	 * Description:	register session server with 0 session and 0 connection<br>
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t112_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new CallBack(sessionServer);
		sessionServer.register(0, 0, cbk);
	}

	/**
	 * Description:	register session server with 1 session and 1 connection twice<br>
	 * Expectation:	throws InvalidActivityException
	 */
	@Test (expected = InvalidActivityException.class)
	public void t113_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new CallBack(sessionServer);
		sessionServer.register(1, 1, cbk);
		sessionServer.register(1, 1, cbk);
	}

	/**
	 * Description:	register session server before listener is started<br>
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t114_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new CallBack(sessionServer);
		sessionServer.register(1, 1, cbk);
		server.startListener();
	}
	
	
	/**
	 * Description:	register two session servers to two services with two callbacks<br>
	 * Expectation:	passes
	 */
	@Test
	public void t198_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		SCSessionServer sessionServer1 = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk1 = new CallBack(sessionServer1);
		sessionServer1.register(1, 1, cbk1);
		assertEquals("SessionServer is not registered", true, sessionServer1.isRegistered());
		
		SCSessionServer sessionServer2 = server.newSessionServer(TestConstants.sesServiceName2);
		SCSessionServerCallback cbk2 = new CallBack(sessionServer2);
		sessionServer2.register(1, 1, cbk2);
		assertEquals("SessionServer is not registered", true, sessionServer2.isRegistered());
		
		sessionServer1.deregister();
		sessionServer2.deregister();
	}
	
	/**
	 * Description:	register two session servers to two services with same callback<br>
	 * Expectation:	passes
	 */
	@Test
	public void t199_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		SCSessionServer sessionServer1 = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new CallBack(sessionServer1);
		sessionServer1.register(1, 1, cbk);
		assertEquals("SessionServer is not registered", true, sessionServer1.isRegistered());
		
		SCSessionServer sessionServer2 = server.newSessionServer(TestConstants.sesServiceName2);
		sessionServer2.register(1, 1, cbk);
		assertEquals("SessionServer is not registered", true, sessionServer2.isRegistered());
		
		sessionServer1.deregister();
		sessionServer2.deregister();
	}


	
	// ----------------------------
	@Test
	public void registerServer_validServiceNameInSCProps_registered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, "sc1-session-service", 1, 1, new CallBack());
		assertEquals(true, server.isRegistered("sc1-session-service"));
		server.deregister("sc1-session-service");
	}

	@Test
	public void registerServer_emptyServiceName_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, "", 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(""));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_whiteSpaceServiceName_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, " ", 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(" "));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerServer_arbitraryServiceNameNotInSCProps_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, "Name", 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered("Name"));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerServer_serviceNameLength32NotInSCProps_notRegisteredThrowsException() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 32; i++) {
			sb.append("a");
		}
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, sb.toString(), 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(sb.toString()));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerServer_serviceNameLength33TooLong_notRegisteredThrowsException() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 33; i++) {
			sb.append("a");
		}
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, sb.toString(), 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(sb.toString()));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_maxSessions0_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sesServiceName1, 0, 1,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.sesServiceName1));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_maxSessionsMinus1_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sesServiceName1, -1, 1,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.sesServiceName1));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_maxSessionsIntMax_registered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sesServiceName1,
				Integer.MAX_VALUE, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.sesServiceName1));
		server.deregister(TestConstants.sesServiceName1);
	}

	@Test
	public void registerServer_maxSessionsIntMin_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sesServiceName1,
					Integer.MIN_VALUE, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.sesServiceName1));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_maxConnections0_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sesServiceName1, 1, 0,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.sesServiceName1));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_maxConnectionsMinus1_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sesServiceName1, 1, -1,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.sesServiceName1));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_maxConnectionsIntMin_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sesServiceName1, 1,
					Integer.MIN_VALUE, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.sesServiceName1));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_maxConnectionsIntMaxSessions1_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sesServiceName1, 1,
					Integer.MAX_VALUE, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.sesServiceName1));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_maxConnections2Sessions1_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sesServiceName1, 1, 2,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.sesServiceName1));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// @Test TODO to much
	public void registerServer_maxConnectionsMAX1024SessionsIntMax_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sesServiceName1,
				Integer.MAX_VALUE, 1024, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.sesServiceName1));
		server.deregister(TestConstants.sesServiceName1);
	}

	// @Test TODO to much
	public void registerServer_maxConnectionsSameAsSessions1024_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sesServiceName1, 1024, 1024,
				new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.sesServiceName1));
		server.deregister(TestConstants.sesServiceName1);
	}

	@Test
	public void registerServer_maxConnectionsSameAsSessions2_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sesServiceName1, 2, 2,
				new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.sesServiceName1));
		server.deregister(TestConstants.sesServiceName1);
	}

	@Test
	public void registerServer_maxConnections1023LessThanSessionsIntMax_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sesServiceName1,
				Integer.MAX_VALUE, 1023, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.sesServiceName1));
		server.deregister(TestConstants.sesServiceName1);
	}

	@Test
	public void registerServer_maxConnections1023LessThanSessions1024_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sesServiceName1, 1024, 1023,
				new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.sesServiceName1));
		server.deregister(TestConstants.sesServiceName1);
	}

	@Test
	public void registerServer_maxConnections1024LessThanSessions1025_isRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sesServiceName1, 1025, 1024,
				new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.sesServiceName1));
		server.deregister(TestConstants.sesServiceName1);
	}

	@Test
	public void registerServer_maxConnections1025OverAllowedMaximum_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sesServiceName1, 1025, 1025,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.sesServiceName1));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_maxConnectionsLessThanSessions2_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sesServiceName1, 2, 1,
				new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.sesServiceName1));
		server.deregister(TestConstants.sesServiceName1);
	}

	@Test
	public void registerServer_maxConnectionsMoreThanSessionsIntMax_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sesServiceName1,
					Integer.MAX_VALUE - 1, Integer.MAX_VALUE, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.sesServiceName1));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_maxConnectionsMoreThanSessions2_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sesServiceName1, 1, 2,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.sesServiceName1));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_allParamsWrong_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer("host", TestConstants.PORT_LISTENER, "Name", -1, -1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered("Name"));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_allParamsWrongExceptHost_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_LISTENER, "Name", -1, -1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered("Name"));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_allParamsWrongExceptHostAndPort_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, "Name", -1, -1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered("Name"));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_allParamsWrongExceptHostAndPortAndServiceName_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sesServiceName1, -1, -1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.sesServiceName1));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_allParamsWrongExcepHostPortServiceNameMaxSessions_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sesServiceName1, 1, -1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.sesServiceName1));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_allParamsWrongExceptHostPortServiceNameMaxSessionsMaxConnections_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sesServiceName1, 1, 1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.sesServiceName1));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_allParamsWrongExceptCallBack_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer("host", TestConstants.PORT_LISTENER, "Name", -1, -1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered("Name"));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_allParamsWrongExceptCallBackMaxConnectionsMaxSessions_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer("host", TestConstants.PORT_LISTENER, "Name", 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered("Name"));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerServer_allParamsWrongExceptCallBackMaxConnectionsMaxSessionsServiceName_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer("host", TestConstants.PORT_LISTENER, TestConstants.sesServiceName1, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.sesServiceName1));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void multipleRegisterServer_differentServiceNames() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.sesServiceName1, 1, 1,
				new CallBack());
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.pubServiceName1, 1, 1,
				new CallBack());
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.pubServiceName1, 1, 1,
				new CallBack());

		assertEquals(true, server.isRegistered(TestConstants.sesServiceName1));
		assertEquals(true, server.isRegistered(TestConstants.pubServiceName1));
		assertEquals(true, server.isRegistered(TestConstants.pubServiceName1));
		server.deregister(TestConstants.sesServiceName1);
		server.deregister(TestConstants.pubServiceName1);
		server.deregister(TestConstants.pubServiceName1);
		assertEquals(false, server.isRegistered(TestConstants.sesServiceName1));
		assertEquals(false, server.isRegistered(TestConstants.pubServiceName1));
		assertEquals(false, server.isRegistered(TestConstants.pubServiceName1));
	}

	private class CallBack extends SCSessionServerCallback {

		public CallBack(SCSessionServer server) {
			super(server);
		}
		@Override
		public SCMessage createSession(SCMessage request, int operationTimeoutInMillis) {
			return request;
		}

		@Override
		public void deleteSession(SCMessage request, int operationTimeoutInMillis) {
		}

		@Override
		public void abortSession(SCMessage request, int operationTimeoutInMillis) {
		}

		@Override
		public SCMessage execute(SCMessage request, int operationTimeoutInMillis) {
			return request;
		}
	}
}
