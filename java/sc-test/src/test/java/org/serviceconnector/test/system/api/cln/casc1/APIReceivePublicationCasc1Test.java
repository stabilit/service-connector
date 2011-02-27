/*
 * Copyright © 2010 STABILIT Informatik AG, Switzerland *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License"); *
 * you may not use this file except in compliance with the License. *
 * You may obtain a copy of the License at *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0 *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS, *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and *
 * limitations under the License. *
 */
package org.serviceconnector.test.system.api.cln.casc1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.ctrl.util.ServerDefinition;
import org.serviceconnector.test.system.SystemSuperTest;
import org.serviceconnector.test.system.api.APISystemSuperPublishClientTest;

public class APIReceivePublicationCasc1Test extends APISystemSuperPublishClientTest {

	public APIReceivePublicationCasc1Test() {
		APIReceivePublicationCasc1Test.setUp1CascadedServiceConnectorAndServer();
	}

	public static void setUpServiceConnectorAndServer() {
		APISystemSuperPublishClientTest.setUpServiceConnectorAndServer();
		// need to have a server serving 3 sessions here
		List<ServerDefinition> srvToSC0Defs = new ArrayList<ServerDefinition>();
		ServerDefinition srvToSC0Def = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH,
				TestConstants.log4jSrvProperties, TestConstants.pubServerName1, TestConstants.PORT_PUB_SRV_TCP,
				TestConstants.PORT_SC_TCP, 3, 3, TestConstants.pubServiceName1);
		srvToSC0Defs.add(srvToSC0Def);
		SystemSuperTest.srvDefs = srvToSC0Defs;
	}

	public static void setUp1CascadedServiceConnectorAndServer() {
		APISystemSuperPublishClientTest.setUp1CascadedServiceConnectorAndServer();
	
		// need to have a server serving 3 sessions here
		List<ServerDefinition> srvToSC0CascDefs = new ArrayList<ServerDefinition>();
		ServerDefinition srvToSC0CascDef = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH,
				TestConstants.log4jSrvProperties, TestConstants.pubServerName1, TestConstants.PORT_PUB_SRV_TCP,
				TestConstants.PORT_SC0_CASC_TCP, 3, 3, TestConstants.pubServiceName1);
		srvToSC0CascDefs.add(srvToSC0CascDef);
		SystemSuperTest.srvDefs = srvToSC0CascDefs;
	}

	public static void setUp2CascadedServiceConnectorAndServer() {
		APISystemSuperPublishClientTest.setUp2CascadedServiceConnectorAndServer();

		// need to have a server serving 3 sessions here
		List<ServerDefinition> srvToSC0CascDefs = new ArrayList<ServerDefinition>();
		ServerDefinition srvToSC0CascDef = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH,
				TestConstants.log4jSrvProperties, TestConstants.pubServerName1, TestConstants.PORT_PUB_SRV_TCP,
				TestConstants.PORT_SC0_CASC_TCP, 3, 3, TestConstants.pubServiceName1);
		srvToSC0CascDefs.add(srvToSC0CascDef);
		SystemSuperTest.srvDefs = srvToSC0CascDefs;
	}
	
	/**
	 * Description: receive one message (regular)<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_receive() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		msgCallback = new MsgCallback(publishService);
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.publishCompressedMsgCmd);
		int nrMessages = 1;
		subMsgRequest.setData(Integer.toString(nrMessages));
		subMsgRequest.setDataLength(((String) subMsgRequest.getData()).length());
		msgCallback.setExpectedMessages(nrMessages);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
		Assert.assertNotNull("the session ID is null", publishService.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
		Assert.assertTrue("is not subscribed", publishService.isSubscribed());

		msgCallback.waitForMessage(10);
		Assert.assertEquals("Nr messages does not match", nrMessages, msgCallback.getMessageCount());
		SCMessage response = msgCallback.getMessage();
		Assert.assertEquals("message body is empty", true, response.getDataLength() > 0);

		publishService.unsubscribe();
		Assert.assertNull("the session ID is not null", publishService.getSessionId());
	}

	/**
	 * Description: receive 1000 messages<br>
	 * Expectation: passes
	 */
	@Test
	public void t02_receive() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		msgCallback = new MsgCallback(publishService);
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.publishCompressedMsgCmd);
		int nrMessages = 1000;
		subMsgRequest.setData(Integer.toString(nrMessages));
		subMsgRequest.setDataLength(((String) subMsgRequest.getData()).length());
		msgCallback.setExpectedMessages(nrMessages);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
		Assert.assertNotNull("the session ID is null", publishService.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
		Assert.assertTrue("is not subscribed", publishService.isSubscribed());

		msgCallback.waitForMessage(20); // TODO JOT ##testing -> TRN zeit erhöhen!
		Assert.assertEquals("Nr messages does not match", nrMessages, msgCallback.getMessageCount());
		SCMessage response = msgCallback.getMessage();
		Assert.assertEquals("message body is empty", true, response.getDataLength() > 0);

		publishService.unsubscribe();
		Assert.assertNull("the session ID is not null", publishService.getSessionId());
	}

	/**
	 * Description: receive message after noDataInterval has expired<br>
	 * Expectation: passes
	 */
	@Test
	public void t03_receive() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		msgCallback = new MsgCallback(publishService);
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.publishMsgWithDelayCmd);
		int nrMessages = 5;
		String waitMillis = "1000";
		subMsgRequest.setData(nrMessages + "|" + waitMillis);
		subMsgRequest.setDataLength(((String) subMsgRequest.getData()).length());
		msgCallback.setExpectedMessages(nrMessages);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
		Assert.assertNotNull("the session ID is null", publishService.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
		Assert.assertTrue("is not subscribed", publishService.isSubscribed());

		msgCallback.waitForMessage(5);
		Assert.assertEquals("Nr messages does not match", nrMessages, msgCallback.getMessageCount());
		SCMessage response = msgCallback.getMessage();
		Assert.assertEquals("message body is empty", true, response.getDataLength() > 0);

		publishService.unsubscribe();
		Assert.assertNull("the session ID is not null", publishService.getSessionId());
	}

	/**
	 * Description: do not receive message because subscription does not match<br>
	 * Expectation: passes (catch exception while waiting for message)
	 */
	@Test
	public void t04_receiveNoMatch() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		msgCallback = new MsgCallback(publishService);
		// wrong mask
		subMsgRequest.setMask(TestConstants.mask1);
		subMsgRequest.setSessionInfo(TestConstants.publishCompressedMsgCmd);
		int nrMessages = 200;
		subMsgRequest.setData(Integer.toString(nrMessages));
		subMsgRequest.setDataLength(((String) subMsgRequest.getData()).length());
		msgCallback.setExpectedMessages(nrMessages);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
		Assert.assertNotNull("the session ID is null", publishService.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
		Assert.assertTrue("is not subscribed", publishService.isSubscribed());

		try {
			msgCallback.waitForMessage(2);
			Assert.fail("TimeoutException should have been thrown!");
		} catch (TimeoutException e) {
			Assert.assertEquals("Nr messages does not match", 0, msgCallback.getMessageCount());
		}
		publishService.unsubscribe();
		Assert.assertNull("the session ID is not null", publishService.getSessionId());
	}

	/**
	 * Description: receive 2x100 messages in two subscriptions of the same client<br>
	 * Expectation: passes
	 */
	@Test
	public void t10_receiveTwoSubscriptions() throws Exception {
		SCPublishService service1 = client.newPublishService(TestConstants.pubServiceName1);
		SCPublishService service2 = client.newPublishService(TestConstants.pubServiceName1);

		int nrMessages = 100;
		MsgCallback cbk1 = new MsgCallback(service1);
		cbk1.setExpectedMessages(nrMessages);
		MsgCallback cbk2 = new MsgCallback(service2);
		cbk2.setExpectedMessages(nrMessages);

		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setData(Integer.toString(nrMessages));
		subMsgRequest.setDataLength(((String) subMsgRequest.getData()).length());

		subMsgResponse = service1.subscribe(subMsgRequest, cbk1);
		Assert.assertNotNull("the session ID is null", service1.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
		Assert.assertTrue("is not subscribed", service1.isSubscribed());

		subMsgRequest.setSessionInfo(TestConstants.publishCompressedMsgCmd);
		subMsgResponse = service2.subscribe(subMsgRequest, cbk2);
		Assert.assertNotNull("the session ID is null", service2.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
		Assert.assertTrue("is not subscribed", service2.isSubscribed());

		cbk1.waitForMessage(200);
		Assert.assertEquals("Nr messages does not match", nrMessages, cbk1.getMessageCount());
		SCMessage response = cbk1.getMessage();
		Assert.assertEquals("message body is empty", true, response.getDataLength() > 0);
		cbk2.waitForMessage(200);
		Assert.assertEquals("Nr messages does not match", nrMessages, cbk2.getMessageCount());
		response = cbk2.getMessage();
		Assert.assertEquals("message body is empty", true, response.getDataLength() > 0);

		service1.unsubscribe(4);
		Assert.assertNull("the session ID is not null)", service1.getSessionId());
		service2.unsubscribe(4);
		Assert.assertNull("the session ID is not null)", service2.getSessionId());
	}
}
