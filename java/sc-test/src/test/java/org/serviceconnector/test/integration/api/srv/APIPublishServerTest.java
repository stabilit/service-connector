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
import org.serviceconnector.api.SCPublishMessage;
import org.serviceconnector.api.srv.SCPublishServerCallback;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.test.integration.api.APIIntegrationSuperServerTest;

public class APIPublishServerTest extends APIIntegrationSuperServerTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(APIPublishServerTest.class);

	/**
	 * Description: publish 1 message to the service "publish-1"<br>
	 * Expectation: passes
	 */
	@Test
	public void t101_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP,
				ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(10, 2, cbk);
		Assert.assertEquals("PublishServer is not registered", true, publishServer.isRegistered());

		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData("something");
		publishServer.publish(publishMessage);
	}

	/**
	 * Description: publish 1 message with mask = null<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t102_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP,
				ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(10, 2, cbk);
		Assert.assertEquals("PublishServer is not registered", true, publishServer.isRegistered());

		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(null);
		publishMessage.setData("something");
		publishServer.publish(publishMessage);
	}

	/**
	 * Description: publish 1 message with mask = ""<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t103_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP,
				ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(10, 2, cbk);
		Assert.assertEquals("PublishServer is not registered", true, publishServer.isRegistered());

		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask("");
		publishMessage.setData("something");
		publishServer.publish(publishMessage);
	}

	/**
	 * Description: publish 1 message with mask = " "<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t104_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP,
				ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(10, 2, cbk);
		Assert.assertEquals("PublishServer is not registered", true, publishServer.isRegistered());

		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(" ");
		publishMessage.setData("something");
		publishServer.publish(publishMessage);
	}

	/**
	 * Description: publish 1 message with mask = 257 bytes long string<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t105_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP,
				ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(10, 2, cbk);
		Assert.assertEquals("PublishServer is not registered", true, publishServer.isRegistered());

		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.stringLength257);
		publishMessage.setData("something");
		publishServer.publish(publishMessage);
	}

	/**
	 * Description: publish 1 message with mask = "0000121%%%%%%%%%%%%%%%-----------X-----------"<br>
	 * Expectation: passes
	 */
	@Test
	public void t106_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP,
				ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(10, 2, cbk);
		Assert.assertEquals("PublishServer is not registered", true, publishServer.isRegistered());

		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask("0000121%%%%%%%%%%%%%%%-----------X-----------");
		publishMessage.setData("something");
		publishServer.publish(publishMessage);
	}

	/**
	 * Description: publish 1 message = null to the service "publish-1"<br>
	 * Expectation: passes
	 */
	@Test
	public void t107_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP,
				ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(10, 2, cbk);
		Assert.assertEquals("PublishServer is not registered", true, publishServer.isRegistered());

		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData(null);
		publishServer.publish(publishMessage);
	}

	/**
	 * Description: publish 1 message = "" to the service "publish-1"<br>
	 * Expectation: passes
	 */
	@Test
	public void t108_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP,
				ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(10, 2, cbk);
		Assert.assertEquals("PublishServer is not registered", true, publishServer.isRegistered());

		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData("");
		publishServer.publish(publishMessage);
	}

	/**
	 * Description: publish 1 message = " " to the service "publish-1"<br>
	 * Expectation: passes
	 */
	@Test
	public void t109_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP,
				ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(10, 2, cbk);
		Assert.assertEquals("PublishServer is not registered", true, publishServer.isRegistered());

		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData(" ");
		publishServer.publish(publishMessage);
	}

	/**
	 * Description: publish 1 message 10000 byte long to the service "publish-1"<br>
	 * Expectation: passes
	 */
	@Test
	public void t110_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP,
				ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(10, 2, cbk);
		Assert.assertEquals("PublishServer is not registered", true, publishServer.isRegistered());

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
	 * Description: publish 1 message 64k byte long to the service "publish-1"<br>
	 * Expectation: passes
	 */
	@Test
	public void t111_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP,
				ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(10, 2, cbk);
		Assert.assertEquals("PublishServer is not registered", true, publishServer.isRegistered());

		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData(new byte[TestConstants.dataLength60kB]);
		publishServer.publish(publishMessage);
	}

	/**
	 * Description: publish 1 message 1MB long to the service "publish-1"<br>
	 * Expectation: passes
	 */
	@Test
	public void t112_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP,
				ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(10, 2, cbk);
		Assert.assertEquals("PublishServer is not registered", true, publishServer.isRegistered());

		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData(new byte[TestConstants.dataLength1MB]);
		publishServer.publish(publishMessage);
	}

	/**
	 * Description: publish 10000 messages 128 byte long to the service "publish-1"<br>
	 * Expectation: passes
	 */
	@Test
	public void t113_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP,
				ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(10, 2, cbk);
		Assert.assertEquals("PublishServer is not registered", true, publishServer.isRegistered());

		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData(new byte[128]);
		int count = 10000;
		for (int i = 0; i < count; i++) {
			if (((i + 1) % 200) == 0)
				testLogger.info("Publish message nr. " + (i + 1));
			publishServer.publish(publishMessage);
		}
	}

	/**
	 * Description: publish message with body = new Object<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t114_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP,
				ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(10, 2, cbk);
		Assert.assertEquals("PublishServer is not registered", true, publishServer.isRegistered());

		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData(new Object());
		publishServer.publish(publishMessage);
	}

	/**
	 * Description: publish message with messageInfo = ""<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t115_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP,
				ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(10, 2, cbk);
		Assert.assertEquals("PublishServer is not registered", true, publishServer.isRegistered());

		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setMessageInfo("");
		publishServer.publish(publishMessage);
	}

	/**
	 * Description: publish message with messageInfo = ""<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t116_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP,
				ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(10, 2, cbk);
		Assert.assertEquals("PublishServer is not registered", true, publishServer.isRegistered());

		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setMessageInfo(" ");
		publishServer.publish(publishMessage);
	}

	/**
	 * Description: publish message with messageInfo = 257char<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t117_publish() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_SES_SRV_TCP,
				ConnectionType.NETTY_TCP);
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(10, 2, cbk);
		Assert.assertEquals("PublishServer is not registered", true, publishServer.isRegistered());

		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setMessageInfo(TestConstants.stringLength257);
		publishServer.publish(publishMessage);
	}

}
