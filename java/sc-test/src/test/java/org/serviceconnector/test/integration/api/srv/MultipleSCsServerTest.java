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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.api.srv.SCSessionServerCallback;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;

public class MultipleSCsServerTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(MultipleSCsServerTest.class);
	
	/** The Constant testLogger. */
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());
	
	private int threadCount = 0;
	private static ProcessesController ctrl;
	private static ProcessCtx scCtx2;
	private static ProcessCtx scCtx1;
	private SCServer server1;
	private SCServer server2;
	private SCSessionServer sessionServer1;
	private SCSessionServer sessionServer2;
	
	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
		scCtx1 = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		scCtx2 = ctrl.startSC(TestConstants.log4jSCcascadedProperties, TestConstants.SCcascadedProperties);
	}

	@Before
	public void beforeOneTest() throws Exception {
		threadCount = Thread.activeCount();
	}
	
	@After
	public void afterOneTest() throws Exception {
		try {
			sessionServer1.deregister();
		} catch (Exception e) {
		}
		try {
			server1.destroy();
		} catch (Exception e) {
		}
		server1 = null;
		try {
			sessionServer2.deregister();
		} catch (Exception e) {
		}
		try {
			server2.destroy();
		} catch (Exception e) {
		}
		server2 = null;
//		Assert.assertEquals("number of threads", threadCount, Thread.activeCount());
		testLogger.info("Number of threads :" + Thread.activeCount() + " created :"+(Thread.activeCount() - threadCount));
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		try {
			ctrl.stopSC(scCtx2);
			scCtx2 = null;
		} catch (Exception e) {
		}
		try {
			ctrl.stopSC(scCtx1);
			scCtx2 = null;
		} catch (Exception e) {
		}
		ctrl = null;
	}

	/**
	 * Description: register and deregister two servers to the same service with:<br>
	 * <table>
	 * <tr><td></td><td>Server 1</td><td>Server 2</td></tr>
	 * <tr><td>connectionType</td><td>netty.http</td><td>netty.http</td></tr>
	 * <tr><td>host</td><td>TestConstants.HOST</td><td>TestConstants.HOST</td></tr>
	 * <tr><td>port</td><td>TestConstants.PORT_TCP</td><td>TestConstants.PORT_TCP</td></tr>
	 * </table>
	 * </br>
	 * Expectation:	Both servers are registered.
	 */
	@Test
	public void t01_registerDeregister() throws Exception {
		server1 = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server1.startListener();
		Assert.assertEquals("SessionServer is not listening", true, server1.isListening());
		sessionServer1 = server1.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk1 = new SrvCallback(sessionServer1);
		sessionServer1.register(1, 1, cbk1);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer1.isRegistered());
		
		server2 = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER+1, ConnectionType.NETTY_TCP); 
		server2.startListener();
		Assert.assertEquals("SessionServer is not listening", true, server2.isListening());
		sessionServer2 = server2.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk2 = new SrvCallback(sessionServer2);
		sessionServer2.register(1, 1, cbk2);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer2.isRegistered());
		
		sessionServer1.deregister();
		Assert.assertEquals("SessionServer is registered", false, sessionServer1.isRegistered());
		server1.stopListener();
		Assert.assertEquals("SessionServer is listening", false, server1.isListening());
		server1.destroy();
		
		sessionServer2.deregister();
		Assert.assertEquals("SessionServer is registered", false, sessionServer2.isRegistered());
		server2.stopListener();
		Assert.assertEquals("SessionServer is listening", false, server2.isListening());
		server2.destroy();
	}


	/**
	 * Description: register and de-register two servers to the same service with:<br>
	 * <table>
	 * <tr><td></td><td>Server 1</td><td>Server 2</td></tr>
	 * <tr><td>connectionType</td><td>netty.http</td><td>netty.http</td></tr>
	 * <tr><td>host</td><td>TestConstants.HOST</td><td>TestConstants.HOST</td></tr>
	 * <tr><td>port</td><td>TestConstants.PORT_HTTP</td><td>TestConstants.PORT_HTTP</td></tr>
	 * </table>
	 * </br>
	 * Expectation:	Both servers are registered.
	 */
	@Test
	public void t02_registerDeregister() throws Exception {
		server1 = new SCServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_HTTP); 
		server1.startListener();
		Assert.assertEquals("SessionServer is not listening", true, server1.isListening());
		sessionServer1 = server1.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk1 = new SrvCallback(sessionServer1);
		sessionServer1.register(1, 1, cbk1);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer1.isRegistered());
		
		server2 = new SCServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.PORT_LISTENER+1, ConnectionType.NETTY_HTTP); 
		server2.startListener();
		Assert.assertEquals("SessionServer is not listening", true, server2.isListening());
		sessionServer2 = server2.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk2 = new SrvCallback(sessionServer2);
		sessionServer2.register(1, 1, cbk2);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer2.isRegistered());
		
		sessionServer1.deregister();
		Assert.assertEquals("SessionServer is registered", false, sessionServer1.isRegistered());
		server1.stopListener();
		Assert.assertEquals("SessionServer is listening", false, server1.isListening());
		server1.destroy();
		
		sessionServer2.deregister();
		Assert.assertEquals("SessionServer is registered", false, sessionServer2.isRegistered());
		server2.stopListener();
		Assert.assertEquals("SessionServer is listening", false, server2.isListening());
		server2.destroy();
	}

	
	/**
	 * Description: register and de-register two servers to the same service with:<br>
	 * <table>
	 * <tr><td></td><td>Server 1</td><td>Server 2</td></tr>
	 * <tr><td>connectionType</td><td>netty.http</td><td>netty.http</td></tr>
	 * <tr><td>host</td><td>TestConstants.HOST</td><td>TestConstants.HOST</td></tr>
	 * <tr><td>port</td><td>TestConstants.PORT_TCP</td><td>TestConstants.PORT_HTTP</td></tr>
	 * </table>
	 * </br>
	 * Expectation:	Both servers are registered.
	 */
	@Test
	public void t03_registerDeregister() throws Exception {
		server1 = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server1.startListener();
		Assert.assertEquals("SessionServer is not listening", true, server1.isListening());
		sessionServer1 = server1.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk1 = new SrvCallback(sessionServer1);
		sessionServer1.register(1, 1, cbk1);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer1.isRegistered());
		
		server2 = new SCServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.PORT_LISTENER+1, ConnectionType.NETTY_HTTP); 
		server2.startListener();
		Assert.assertEquals("SessionServer is not listening", true, server2.isListening());
		sessionServer2 = server2.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk2 = new SrvCallback(sessionServer2);
		sessionServer2.register(1, 1, cbk2);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer2.isRegistered());
		
		sessionServer1.deregister();
		Assert.assertEquals("SessionServer is registered", false, sessionServer1.isRegistered());
		server1.stopListener();
		Assert.assertEquals("SessionServer is listening", false, server1.isListening());
		server1.destroy();
		
		sessionServer2.deregister();
		Assert.assertEquals("SessionServer is registered", false, sessionServer2.isRegistered());
		server2.stopListener();
		Assert.assertEquals("SessionServer is listening", false, server2.isListening());
		server2.destroy();
	}

	
	private class SrvCallback extends SCSessionServerCallback {

		public SrvCallback(SCSessionServer server) {
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
