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

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCAppendMessage;
import org.serviceconnector.api.SCManagedMessage;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.cache.SC_CACHE_ENTRY_STATE;
import org.serviceconnector.test.system.api.APISystemSuperCCTest;

public class APICacheCoherencyCasc1Test extends APISystemSuperCCTest {

	public APICacheCoherencyCasc1Test() {
		APICacheCoherencyCasc1Test.setUp1CascadedServiceConnectorAndServer();
	}

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		this.setUpClientToSC();
	}

	/**
	 * Description: receive 3 appendices<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_cc_receive3Appendix() throws Exception {
		// gets message cached (cacheid=700)
		SCMessage request = new SCMessage();
		request.setData("managedData");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// start update retriever
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish3AppendixMsgCmd);
		updateRetrClient.startCacheUpdateRetriever(TestConstants.updateRetrieverName1, subMsg, updateRetrieverCbk);
		// assure 3 messages arrive within 10 seconds!
		updateRetrieverCbk.waitForMessage(10, 3);
	}

	/**
	 * Description: read managed data from cache, 3 appendices<br>
	 * Expectation: passes
	 */
	@Test
	public void t02_cc_readManagedData() throws Exception {
		// gets message cached (cacheid=700)
		SCMessage request = new SCMessage();
		request.setData("managedData");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// start update retriever
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish3AppendixMsgCmd);
		subMsg.setData("700");
		updateRetrClient.startCacheUpdateRetriever(TestConstants.updateRetrieverName1, subMsg, updateRetrieverCbk);
		// assure 3 messages arrive within 10 seconds!
		updateRetrieverCbk.waitForMessage(10, 3);

		// read managed data with session client from cache
		SCMessage response = sessionService1.execute(request);

		Assert.assertTrue("response not of type managed message", response instanceof SCManagedMessage);
		SCManagedMessage managedData = (SCManagedMessage) response;
		Assert.assertEquals("response not of type managed message", managedData.getNrOfAppendices(), 3);
		List<SCAppendMessage> appendices = managedData.getAppendices();

		int i = 0;
		for (SCAppendMessage scAppendMessage : appendices) {
			Assert.assertEquals(i + "", (String) scAppendMessage.getData());
			i++;
		}

		// verify that message is in cache on sc1 and not only in sc0
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		Assert.assertEquals("success", inspectResponse.get("return"));
		Assert.assertEquals(SC_CACHE_ENTRY_STATE.LOADED.toString(), inspectResponse.get("cacheMessageState"));
		Assert.assertEquals("700", inspectResponse.get("cacheId"));
		Assert.assertEquals("3", inspectResponse.get("cacheMessageNrOfAppendix"));
		Assert.assertEquals("700/3|0=0&700/0=0&700/2|0=0&700/1|0=0&", inspectResponse.get("cacheMessagePartInfo")); // cacheNrOfPartsOfInitialMsg=0
		Assert.assertEquals("updateRetriever1", inspectResponse.get("cacheMessageAssignedUpdateGuardian"));
	}

	/**
	 * Description:
	 * Expectation: passes
	 */
	@Test
	public void t10_cc_StopUpdateRetrieverReadManagedData() throws Exception {

	}

	@Test
	public void t10_cc_RetrievingAppendixWithoutCacheId() throws Exception {

	}

	@Test
	public void t10_cc_RetrievingAppendixOfDifferentCacheGuardian() throws Exception {

	}

	@Test
	public void t10_cc_RetrievingAppendixDuringLoadOfInitialMsg() throws Exception {

	}

	// /**
	// * Description: receive 1000 messages<br>
	// * Expectation: passes
	// */
	// @Test
	// public void t02_receive() throws Exception {
	// publishService = mgmtClient.newPublishService(TestConstants.pubServiceName1);
	// SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
	// SCSubscribeMessage subMsgResponse = null;
	// msgCallback = new MsgCallback(publishService);
	// subMsgRequest.setMask(TestConstants.mask);
	// subMsgRequest.setSessionInfo(TestConstants.publishCompressedMsgCmd);
	// int nrMessages = 1000;
	// subMsgRequest.setData(Integer.toString(nrMessages));
	// subMsgRequest.setDataLength(((String) subMsgRequest.getData()).length());
	// msgCallback.setExpectedMessages(nrMessages);
	// subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
	// Assert.assertNotNull("the session ID is null", publishService.getSessionId());
	// Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
	// Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
	// Assert.assertTrue("is not subscribed", publishService.isSubscribed());
	//
	// msgCallback.waitForMessage(20);
	// Assert.assertEquals("Nr messages does not match", nrMessages, msgCallback.getMessageCount());
	// SCMessage response = msgCallback.getMessage();
	// Assert.assertEquals("message body is empty", true, response.getDataLength() > 0);
	//
	// publishService.unsubscribe();
	// Assert.assertNull("the session ID is not null", publishService.getSessionId());
	// }
	//
	// /**
	// * Description: receive message after noDataInterval has expired<br>
	// * Expectation: passes
	// */
	// @Test
	// public void t03_receive() throws Exception {
	// publishService = mgmtClient.newPublishService(TestConstants.pubServiceName1);
	// SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
	// SCSubscribeMessage subMsgResponse = null;
	// msgCallback = new MsgCallback(publishService);
	// subMsgRequest.setMask(TestConstants.mask);
	// subMsgRequest.setSessionInfo(TestConstants.publishMsgWithDelayCmd);
	// int nrMessages = 5;
	// String waitMillis = "1000";
	// subMsgRequest.setData(nrMessages + "|" + waitMillis);
	// subMsgRequest.setDataLength(((String) subMsgRequest.getData()).length());
	// msgCallback.setExpectedMessages(nrMessages);
	// subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
	// Assert.assertNotNull("the session ID is null", publishService.getSessionId());
	// Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
	// Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
	// Assert.assertTrue("is not subscribed", publishService.isSubscribed());
	//
	// msgCallback.waitForMessage(5);
	// Assert.assertEquals("Nr messages does not match", nrMessages, msgCallback.getMessageCount());
	// SCMessage response = msgCallback.getMessage();
	// Assert.assertEquals("message body is empty", true, response.getDataLength() > 0);
	//
	// publishService.unsubscribe();
	// Assert.assertNull("the session ID is not null", publishService.getSessionId());
	// }
	//
	// /**
	// * Description: do not receive message because subscription does not match<br>
	// * Expectation: passes (catch exception while waiting for message)
	// */
	// @Test
	// public void t04_receiveNoMatch() throws Exception {
	// publishService = mgmtClient.newPublishService(TestConstants.pubServiceName1);
	// SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
	// SCSubscribeMessage subMsgResponse = null;
	// msgCallback = new MsgCallback(publishService);
	// // wrong mask
	// subMsgRequest.setMask(TestConstants.mask1);
	// subMsgRequest.setSessionInfo(TestConstants.publishCompressedMsgCmd);
	// int nrMessages = 200;
	// subMsgRequest.setData(Integer.toString(nrMessages));
	// subMsgRequest.setDataLength(((String) subMsgRequest.getData()).length());
	// msgCallback.setExpectedMessages(nrMessages);
	// subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
	// Assert.assertNotNull("the session ID is null", publishService.getSessionId());
	// Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
	// Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
	// Assert.assertTrue("is not subscribed", publishService.isSubscribed());
	//
	// try {
	// msgCallback.waitForMessage(2);
	// Assert.fail("TimeoutException should have been thrown!");
	// } catch (TimeoutException e) {
	// Assert.assertEquals("Nr messages does not match", 0, msgCallback.getMessageCount());
	// }
	// publishService.unsubscribe();
	// Assert.assertNull("the session ID is not null", publishService.getSessionId());
	// }
	//
	// /**
	// * Description: receive 2x100 messages in two subscriptions of the same client<br>
	// * Expectation: passes
	// */
	// @Test
	// public void t10_receiveTwoSubscriptions() throws Exception {
	// SCPublishService service1 = mgmtClient.newPublishService(TestConstants.pubServiceName1);
	// SCPublishService service2 = mgmtClient.newPublishService(TestConstants.pubServiceName1);
	//
	// int nrMessages = 100;
	// MsgCallback cbk1 = new MsgCallback(service1);
	// cbk1.setExpectedMessages(nrMessages);
	// MsgCallback cbk2 = new MsgCallback(service2);
	// cbk2.setExpectedMessages(nrMessages);
	//
	// SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
	// SCSubscribeMessage subMsgResponse = null;
	// subMsgRequest.setMask(TestConstants.mask);
	// subMsgRequest.setData(Integer.toString(nrMessages));
	// subMsgRequest.setDataLength(((String) subMsgRequest.getData()).length());
	//
	// subMsgResponse = service1.subscribe(subMsgRequest, cbk1);
	// Assert.assertNotNull("the session ID is null", service1.getSessionId());
	// Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
	// Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
	// Assert.assertTrue("is not subscribed", service1.isSubscribed());
	//
	// subMsgRequest.setSessionInfo(TestConstants.publishCompressedMsgCmd);
	// subMsgResponse = service2.subscribe(subMsgRequest, cbk2);
	// Assert.assertNotNull("the session ID is null", service2.getSessionId());
	// Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
	// Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
	// Assert.assertTrue("is not subscribed", service2.isSubscribed());
	//
	// cbk1.waitForMessage(200);
	// Assert.assertEquals("Nr messages does not match", nrMessages, cbk1.getMessageCount());
	// SCMessage response = cbk1.getMessage();
	// Assert.assertEquals("message body is empty", true, response.getDataLength() > 0);
	// cbk2.waitForMessage(200);
	// Assert.assertEquals("Nr messages does not match", nrMessages, cbk2.getMessageCount());
	// response = cbk2.getMessage();
	// Assert.assertEquals("message body is empty", true, response.getDataLength() > 0);
	//
	// service1.unsubscribe(4);
	// Assert.assertNull("the session ID is not null)", service1.getSessionId());
	// service2.unsubscribe(4);
	// Assert.assertNull("the session ID is not null)", service2.getSessionId());
	// }
	//
	// /**
	// * Description: two message receives - waitTime on server is longer than subscriptionTimeout, it verifies timer are scheduled
	// * correctly<br>
	// * Expectation: passes
	// */
	// @Test
	// public void t11_receive() throws Exception {
	// publishService = mgmtClient.newPublishService(TestConstants.pubServiceName1);
	// SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
	// msgCallback = new MsgCallback(publishService);
	// subMsgRequest.setMask(TestConstants.mask);
	// subMsgRequest.setSessionInfo(TestConstants.publishMsgWithDelayCmd);
	// int nrMessages = 2;
	// String waitMillis = "124000";
	// subMsgRequest.setData(nrMessages + "|" + waitMillis);
	// subMsgRequest.setDataLength(((String) subMsgRequest.getData()).length());
	// subMsgRequest.setNoDataIntervalSeconds(63);
	// msgCallback.setExpectedMessages(nrMessages);
	// publishService.subscribe(subMsgRequest, msgCallback);
	// msgCallback.waitForMessage(124000);
	// Assert.assertEquals("Nr messages does not match", nrMessages, msgCallback.getMessageCount());
	// msgCallback.getMessage();
	// publishService.unsubscribe();
	// }
}
