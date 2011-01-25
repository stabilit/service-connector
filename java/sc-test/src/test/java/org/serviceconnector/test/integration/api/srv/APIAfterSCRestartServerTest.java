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
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.api.srv.SCSessionServerCallback;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.test.integration.api.APIIntegrationSuperServerTest;

public class APIAfterSCRestartServerTest extends APIIntegrationSuperServerTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(APIAfterSCRestartServerTest.class);


	/**
	 * Description: start listener after SC was restarted<br> 
	 * Expectation:	passes because SC is not involved
	 */
	@Test
	public void t101_startListener() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.SC0, TestConstants.log4jSC0Properties, TestConstants.SC0Properties);
		
		server.startListener();
		Assert.assertEquals("SessionServer is not registered", true, server.isListening());
	}

	/**
	 * Description: stop listener after SC was restarted<br> 
	 * Expectation:	passes because SC is not involved
	 */
	@Test
	public void t102_stopListener() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		Assert.assertEquals("SessionServer is not registered", true, server.isListening());
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.SC0, TestConstants.log4jSC0Properties, TestConstants.SC0Properties);
		
		server.stopListener();
		Assert.assertEquals("SessionServer is registered", false, server.isListening());
	}

	/**
	 * Description: register after SC was restarted<br> 
	 * Expectation:	passes because SC is not involved
	 */
	@Test
	public void t103_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		server.setImmediateConnect(true);
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.SC0, TestConstants.log4jSC0Properties, TestConstants.SC0Properties);
		
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
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		server.setImmediateConnect(false);
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.SC0, TestConstants.log4jSC0Properties, TestConstants.SC0Properties);
		
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
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		Assert.assertEquals("SessionServer is not registered", true, server.isListening());
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 1, cbk);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer.isRegistered());
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.SC0, TestConstants.log4jSC0Properties, TestConstants.SC0Properties);
		
		sessionServer.deregister();
		Assert.assertEquals("SessionServer is registered", false, sessionServer.isRegistered());
	}

	/**
	 * Description: start listener after SC was restarted<br> 
	 * Expectation:	passes because SC is not involved
	 */
	@Test
	public void t201_startListener() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_HTTP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_HTTP); 
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.SC0, TestConstants.log4jSC0Properties, TestConstants.SC0Properties);
		
		server.startListener();
		Assert.assertEquals("SessionServer is not registered", true, server.isListening());
	}

	/**
	 * Description: stop listener after SC was restarted<br> 
	 * Expectation:	passes because SC is not involved
	 */
	@Test
	public void t202_stopListener() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_HTTP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_HTTP); 
		server.startListener();
		Assert.assertEquals("SessionServer is not registered", true, server.isListening());
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.SC0, TestConstants.log4jSC0Properties, TestConstants.SC0Properties);
		
		server.stopListener();
		Assert.assertEquals("SessionServer is registered", false, server.isListening());
	}

	/**
	 * Description: register after SC was restarted<br> 
	 * Expectation:	passes because SC is not involved
	 */
	@Test
	public void t203_register() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_HTTP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_HTTP); 
		server.startListener();
		server.setImmediateConnect(true);
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.SC0, TestConstants.log4jSC0Properties, TestConstants.SC0Properties);
		
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
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_HTTP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_HTTP); 
		server.startListener();
		server.setImmediateConnect(false);
		
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.SC0, TestConstants.log4jSC0Properties, TestConstants.SC0Properties);
		
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
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_HTTP, TestConstants.PORT_SES_SRV_TCP, ConnectionType.NETTY_HTTP); 
		server.startListener();
		Assert.assertEquals("SessionServer is not registered", true, server.isListening());
		sessionServer = server.newSessionServer(TestConstants.sesServiceName1);
		SCSessionServerCallback cbk = new SesSrvCallback(sessionServer);
		sessionServer.register(1, 1, cbk);
		Assert.assertEquals("SessionServer is not registered", true, sessionServer.isRegistered());
		ctrl.stopSC(scCtx);
		scCtx = ctrl.startSC(TestConstants.SC0, TestConstants.log4jSC0Properties, TestConstants.SC0Properties);
		sessionServer.deregister();
		Assert.assertEquals("SessionServer is registered", false, sessionServer.isRegistered());
	}
	
}
