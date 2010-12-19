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
package org.serviceconnector.test.system.api.publish;

import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.test.system.APISystemSuperPublishClientTest;

public class APIReceivePublicationTest extends APISystemSuperPublishClientTest {

	private SCPublishService service;

	@After
	public void afterOneTest() throws Exception {
		try {
			service.unsubscribe();
		} catch (Exception e1) {
		}
		service = null;
		super.afterOneTest();
	}

	/**
	 * Description: receive one message (regular)<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_receive() throws Exception {
		service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		cbk = new MsgCallback(service);
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.publishCompressedMsgCmd);
		int nrMessages = 1;
		subMsgRequest.setData(Integer.toString(nrMessages));
		cbk.setExpectedMessages(nrMessages);
		subMsgResponse = service.subscribe(subMsgRequest, cbk);
		Assert.assertNotNull("the session ID is null", service.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
		Assert.assertTrue("is not subscribed", service.isSubscribed());

		cbk.waitForMessage(10);
		Assert.assertEquals("Nr messages does not match", nrMessages, cbk.getMessageCount());
		SCMessage response = cbk.getMessage();
		Assert.assertEquals("message body is empty", true, response.getDataLength() > 0);

		service.unsubscribe();
		Assert.assertNull("the session ID is not null", service.getSessionId());
	}

	/**
	 * Description: receive 1000 messages<br>
	 * Expectation: passes
	 */
	@Test
	public void t02_receive() throws Exception {
		service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		cbk = new MsgCallback(service);
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.publishCompressedMsgCmd);
		int nrMessages = 1000;
		subMsgRequest.setData(Integer.toString(nrMessages));
		cbk.setExpectedMessages(nrMessages);
		subMsgResponse = service.subscribe(subMsgRequest, cbk);
		Assert.assertNotNull("the session ID is null", service.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
		Assert.assertTrue("is not subscribed", service.isSubscribed());

		cbk.waitForMessage(10);
		Assert.assertEquals("Nr messages does not match", nrMessages, cbk.getMessageCount());
		SCMessage response = cbk.getMessage();
		Assert.assertEquals("message body is empty", true, response.getDataLength() > 0);

		service.unsubscribe();
		Assert.assertNull("the session ID is not null", service.getSessionId());
	}

	/**
	 * Description: receive message after noDataInterval has expired<br>
	 * Expectation: passes
	 */
	@Test
	public void t03_receive() throws Exception {
		service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		cbk = new MsgCallback(service);
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.publishMessagesWithDelayCmd);
		int nrMessages = 5;
		String waitInMillis = "1000";
		subMsgRequest.setData(nrMessages + "|" + waitInMillis);
		cbk.setExpectedMessages(nrMessages);
		subMsgResponse = service.subscribe(subMsgRequest, cbk);
		Assert.assertNotNull("the session ID is null", service.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
		Assert.assertTrue("is not subscribed", service.isSubscribed());

		cbk.waitForMessage(5);
		Assert.assertEquals("Nr messages does not match", nrMessages, cbk.getMessageCount());
		SCMessage response = cbk.getMessage();
		Assert.assertEquals("message body is empty", true, response.getDataLength() > 0);

		service.unsubscribe();
		Assert.assertNull("the session ID is not null", service.getSessionId());
	}

	/**
	 * Description: do not receive message because subscription does not match<br>
	 * Expectation: passes (catch exception while waiting for message)
	 */
	@Test
	public void t04_receiveNoMatch() throws Exception {
		service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		cbk = new MsgCallback(service);
		// wrong mask
		subMsgRequest.setMask(TestConstants.mask1);
		subMsgRequest.setSessionInfo(TestConstants.publishCompressedMsgCmd);
		int nrMessages = 200;
		subMsgRequest.setData(Integer.toString(nrMessages));
		cbk.setExpectedMessages(nrMessages);
		subMsgResponse = service.subscribe(subMsgRequest, cbk);
		Assert.assertNotNull("the session ID is null", service.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
		Assert.assertTrue("is not subscribed", service.isSubscribed());

		try {
			cbk.waitForMessage(2);
			Assert.fail("TimeoutException should have been thrown!");
		} catch (TimeoutException e) {
			Assert.assertEquals("Nr messages does not match", 0, cbk.getMessageCount());
		}
		service.unsubscribe();
		Assert.assertNull("the session ID is not null", service.getSessionId());
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
		subMsgRequest.setSessionInfo(TestConstants.publishCompressedMsgCmd);
		subMsgRequest.setData("certificate or what so ever");
		subMsgRequest.setData(Integer.toString(nrMessages));
		
		subMsgResponse = service1.subscribe(subMsgRequest, cbk1);
		Assert.assertNotNull("the session ID is null", service1.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
		Assert.assertTrue("is not subscribed", service1.isSubscribed());
		
		subMsgResponse = service2.subscribe(subMsgRequest, cbk2);
		Assert.assertNotNull("the session ID is null", service2.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
		Assert.assertTrue("is not subscribed", service2.isSubscribed());
		
		cbk1.waitForMessage(20);
		Assert.assertEquals("Nr messages does not match", nrMessages, cbk1.getMessageCount());
		SCMessage response = cbk1.getMessage();
		Assert.assertEquals("message body is empty", true, response.getDataLength() > 0);
		cbk2.waitForMessage(20);
		Assert.assertEquals("Nr messages does not match", nrMessages, cbk2.getMessageCount());
		response = cbk2.getMessage();
		Assert.assertEquals("message body is empty", true, response.getDataLength() > 0);
		
		service1.unsubscribe(2);
		Assert.assertNull("the session ID is not null)", service1.getSessionId());
		service2.unsubscribe(2);
		Assert.assertNull("the session ID is not null)", service2.getSessionId());
	}


}
