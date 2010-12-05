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

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCPublishMessage;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.srv.SCPublishServer;
import org.serviceconnector.api.srv.SCPublishServerCallback;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;

public class PublishServerTest {
	
	/** The Constant testLogger. */
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PublishServerTest.class);

	private static ProcessesController ctrl;
	private static ProcessCtx scCtx;
	private SCServer server;
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
			publishServer.deregister();
		} catch (Exception e) {}
		publishServer = null;
		try {
			server.stopListener();
		} catch (Exception e) {}
		try {
			server.destroy();
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
	 * Description:	publish 1 message to the service "publish-1"<br>
	 * Expectation:	passes
	 */
	@Test
	public void t101_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new CallBack(publishServer);
		publishServer.register(10, 2, cbk);
		assertEquals("SessionServer is not registered", true, publishServer.isRegistered());
		
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData("something");
		publishServer.publish(publishMessage);
	}
	
	/**
	 * Description:	publish 1 message with mask = null<br>
	 * Expectation: throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t102_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new CallBack(publishServer);
		publishServer.register(10, 2, cbk);
		assertEquals("SessionServer is not registered", true, publishServer.isRegistered());
		
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(null);
		publishMessage.setData("something");
		publishServer.publish(publishMessage);
	}

	/**
	 * Description:	publish 1 message with mask = ""<br>
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t103_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new CallBack(publishServer);
		publishServer.register(10, 2, cbk);
		assertEquals("SessionServer is not registered", true, publishServer.isRegistered());
		
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask("");
		publishMessage.setData("something");
		publishServer.publish(publishMessage);
	}

	/**
	 * Description:	publish 1 message with mask = " "<br>
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t104_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new CallBack(publishServer);
		publishServer.register(10, 2, cbk);
		assertEquals("SessionServer is not registered", true, publishServer.isRegistered());
		
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(" ");
		publishMessage.setData("something");
		publishServer.publish(publishMessage);
	}

	/**
	 * Description:	publish 1 message with mask = 257 bytes long string<br>
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t105_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new CallBack(publishServer);
		publishServer.register(10, 2, cbk);
		assertEquals("SessionServer is not registered", true, publishServer.isRegistered());
		
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.stringLength257);
		publishMessage.setData("something");
		publishServer.publish(publishMessage);
	}

	/**
	 * Description:	publish 1 message with mask = "0000121%%%%%%%%%%%%%%%-----------X-----------"<br>
	 * Expectation:	passes
	 */
	@Test
	public void t106_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new CallBack(publishServer);
		publishServer.register(10, 2, cbk);
		assertEquals("SessionServer is not registered", true, publishServer.isRegistered());
		
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask("0000121%%%%%%%%%%%%%%%-----------X-----------");
		publishMessage.setData("something");
		publishServer.publish(publishMessage);
	}

	/**
	 * Description:	publish 1 message = null to the service "publish-1"<br>
	 * Expectation:	passes
	 */
	@Test
	public void t107_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new CallBack(publishServer);
		publishServer.register(10, 2, cbk);
		assertEquals("SessionServer is not registered", true, publishServer.isRegistered());
		
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData(null);
		publishServer.publish(publishMessage);
	}

	/**
	 * Description:	publish 1 message = "" to the service "publish-1"<br>
	 * Expectation:	passes
	 */
	@Test
	public void t108_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new CallBack(publishServer);
		publishServer.register(10, 2, cbk);
		assertEquals("SessionServer is not registered", true, publishServer.isRegistered());
		
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData("");
		publishServer.publish(publishMessage);
	}

	/**
	 * Description:	publish 1 message = " " to the service "publish-1"<br>
	 * Expectation:	passes
	 */
	@Test
	public void t109_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new CallBack(publishServer);
		publishServer.register(10, 2, cbk);
		assertEquals("SessionServer is not registered", true, publishServer.isRegistered());
		
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData(" ");
		publishServer.publish(publishMessage);
	}
	
	/**
	 * Description:	publish 1 message 10000 byte long to the service "publish-1"<br>
	 * Expectation:	passes
	 */
	@Test
	public void t110_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new CallBack(publishServer);
		publishServer.register(10, 2, cbk);
		assertEquals("SessionServer is not registered", true, publishServer.isRegistered());
		
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 10000; i++) {
			sb.append("a");
		}
		publishMessage.setData(sb.toString());
		publishServer.publish(publishMessage);
	}

	/**
	 * Description:	publish 1 message 64k byte long to the service "publish-1"<br>
	 * Expectation:	passes
	 */
	@Test
	public void t111_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new CallBack(publishServer);
		publishServer.register(10, 2, cbk);
		assertEquals("SessionServer is not registered", true, publishServer.isRegistered());
		
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		StringBuilder sb = new StringBuilder();
		publishMessage.setData(new byte[TestConstants.dataLength60kB]);
		publishServer.publish(publishMessage);
	}

	/**
	 * Description:	publish 1 message 1MB long to the service "publish-1"<br>
	 * Expectation:	passes
	 */
	@Test
	public void t112_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new CallBack(publishServer);
		publishServer.register(10, 2, cbk);
		assertEquals("SessionServer is not registered", true, publishServer.isRegistered());
		
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		StringBuilder sb = new StringBuilder();
		publishMessage.setData(new byte[TestConstants.dataLength1MB]);
		publishServer.publish(publishMessage);
	}
	
	/**
	 * Description:	publish 10000 messages 128 byte long to the service "publish-1"<br>
	 * Expectation:	passes
	 */
	@Test
	public void t113_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new CallBack(publishServer);
		publishServer.register(10, 2, cbk);
		assertEquals("SessionServer is not registered", true, publishServer.isRegistered());
		
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		StringBuilder sb = new StringBuilder();
		publishMessage.setData(new byte[128]);
		int count = 10000;
		for (int i= 0; i < count; i++) {
			if (((i+1) % 200) == 0) testLogger.info("Publish message nr. " + (i+1) );
			publishServer.publish(publishMessage);
		}
	}

	private class CallBack extends SCPublishServerCallback {

		public CallBack(SCPublishServer server) {
			super(server);
		}
		@Override
		public SCMessage changeSubscription(SCSubscribeMessage message, int operationTimeoutInMillis) {
			return message;
		}

		@Override
		public SCMessage subscribe(SCSubscribeMessage message, int operationTimeoutInMillis) {
			return message;
		}

		@Override
		public void unsubscribe(SCSubscribeMessage message, int operationTimeoutInMillis) {
		}
	}
	
}
