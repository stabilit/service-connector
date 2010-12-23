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
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.api.srv.SCSessionServerCallback;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.test.integration.api.APIIntegrationSuperServerTest;

public class APIAfterSCRestartServerTest extends APIIntegrationSuperServerTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(APIAfterSCRestartServerTest.class);

	@BeforeClass
	public static void beforeAllTests() throws Exception {
	}

	@Before
	public void beforeOneTest() throws Exception {
		ctrl = new ProcessesController();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		threadCount = Thread.activeCount();
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			if (publishServer != null) publishServer.deregister();
		} catch (Exception e) {}
		publishServer = null;
		try {
			if (sessionServer != null) sessionServer.deregister();
		} catch (Exception e) {}
		sessionServer = null;
		try {
			server.stopListener();
		} catch (Exception e) {}
		try {
			server.destroy();
		} catch (Exception e) {}
		server = null;
		try {
			ctrl.stopSC(scCtx);
			scCtx = null;
		} catch (Exception e) {}
		ctrl = null;
		testLogger.info("Number of threads :" + Thread.activeCount() + " created :"+(Thread.activeCount() - threadCount));
	}
	
	@AfterClass
	public static void afterAllTests() throws Exception {
	}

	
	/**
	 * Description: start listener after SC was restarted<br> 
	 * Expectation:	passes because SC is not involved
	 */
	@Test
	public void t101_startListener() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		
		server.startListener();
		Assert.assertEquals("SessionServer is not registered", true, server.isListening());
	}

	/**
	 * Description: stop listener after SC was restarted<br> 
	 * Expectation:	passes because SC is not involved
	 */
	@Test
	public void t102_stopListener() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		Assert.assertEquals("SessionServer is not registered", true, server.isListening());
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		
		server.stopListener();
		Assert.assertEquals("SessionServer is registered", false, server.isListening());
	}

	/**
	 * Description: register after SC was restarted<br> 
	 * Expectation:	passes because SC is not involved
	 */
	@Test
	public void t103_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		server.setImmediateConnect(true);
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		
		Assert.assertEquals("SessionServer is not registered", true, server.isListening());
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 1, cbk);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer.isRegistered());
	}

	/**
	 * Description: register after SC was restarted and ImmediateConnect = false<br> 
	 * Expectation:	passes because SC is not involved
	 */
	@Test
	public void t104_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		server.setImmediateConnect(false);
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		
		Assert.assertEquals("SessionServer is not registered", true, server.isListening());
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 1, cbk);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer.isRegistered());
	}

	/**
	 * Description: de-register after SC was restarted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t105_deregister() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		Assert.assertEquals("SessionServer is not registered", true, server.isListening());
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 1, cbk);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer.isRegistered());
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		
		sessionServer.deregister();
		Assert.assertEquals("SessionServer is registered", false, sessionServer.isRegistered());
	}

	/**
	 * Description: start listener after SC was restarted<br> 
	 * Expectation:	passes because SC is not involved
	 */
	@Test
	public void t201_startListener() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_HTTP); 
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		
		server.startListener();
		Assert.assertEquals("SessionServer is not registered", true, server.isListening());
	}

	/**
	 * Description: stop listener after SC was restarted<br> 
	 * Expectation:	passes because SC is not involved
	 */
	@Test
	public void t202_stopListener() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_HTTP); 
		server.startListener();
		Assert.assertEquals("SessionServer is not registered", true, server.isListening());
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		
		server.stopListener();
		Assert.assertEquals("SessionServer is registered", false, server.isListening());
	}

	/**
	 * Description: register after SC was restarted<br> 
	 * Expectation:	passes because SC is not involved
	 */
	@Test
	public void t203_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_HTTP); 
		server.startListener();
		server.setImmediateConnect(true);
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		
		Assert.assertEquals("SessionServer is not registered", true, server.isListening());
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 1, cbk);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer.isRegistered());
	}

	/**
	 * Description: register after SC was restarted and ImmediateConnect = false<br> 
	 * Expectation:	passes because SC is not involved
	 */
	@Test
	public void t204_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_HTTP); 
		server.startListener();
		server.setImmediateConnect(false);
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		
		Assert.assertEquals("SessionServer is not registered", true, server.isListening());
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 1, cbk);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer.isRegistered());
	}

	/**
	 * Description: de-register after SC was restarted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t205_deregister() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_HTTP); 
		server.startListener();
		Assert.assertEquals("SessionServer is not registered", true, server.isListening());
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 1, cbk);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer.isRegistered());
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		sessionServer.deregister();
		Assert.assertEquals("SessionServer is registered", false, sessionServer.isRegistered());
	}
	
}
