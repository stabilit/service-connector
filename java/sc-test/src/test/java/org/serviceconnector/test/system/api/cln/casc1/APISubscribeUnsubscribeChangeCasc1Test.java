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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.ctrl.util.ServerDefinition;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.system.SystemSuperTest;
import org.serviceconnector.test.system.api.APISystemSuperPublishClientTest;

@SuppressWarnings("unused")
public class APISubscribeUnsubscribeChangeCasc1Test extends APISystemSuperPublishClientTest {

	public APISubscribeUnsubscribeChangeCasc1Test() {
		APISystemSuperPublishClientTest.setUp1CascadedServiceConnectorAndServer();
	}

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		this.setUpClientToSC();
	}
	
//	public static void setUpServiceConnectorAndServer() {
//		APISystemSuperPublishClientTest.setUpServiceConnectorAndServer();
//		// need to have a server serving 3 sessions here
//		List<ServerDefinition> srvToSC0Defs = new ArrayList<ServerDefinition>();
//		ServerDefinition srvToSC0Def = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH,
//				TestConstants.log4jSrvProperties, TestConstants.pubServerName1, TestConstants.PORT_PUB_SRV_TCP,
//				TestConstants.PORT_SC0_TCP, 3, 3, TestConstants.pubServiceName1);
//		srvToSC0Defs.add(srvToSC0Def);
//		SystemSuperTest.srvDefs = srvToSC0Defs;
//	}
//
//	public static void setUp1CascadedServiceConnectorAndServer() {
//		APISystemSuperPublishClientTest.setUp1CascadedServiceConnectorAndServer();
//
//		// need to have a server serving 3 sessions here
//		List<ServerDefinition> srvToSC0CascDefs = new ArrayList<ServerDefinition>();
//		ServerDefinition srvToSC0CascDef = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH,
//				TestConstants.log4jSrvProperties, TestConstants.pubServerName1, TestConstants.PORT_PUB_SRV_TCP,
//				TestConstants.PORT_SC0_CASC_TCP, 3, 3, TestConstants.pubServiceName1);
//		srvToSC0CascDefs.add(srvToSC0CascDef);
//		SystemSuperTest.srvDefs = srvToSC0CascDefs;
//	}
//
//	public static void setUp2CascadedServiceConnectorAndServer() {
//		APISystemSuperPublishClientTest.setUp2CascadedServiceConnectorAndServer();
//
//		// need to have a server serving 3 sessions here
//		List<ServerDefinition> srvToSC0CascDefs = new ArrayList<ServerDefinition>();
//		ServerDefinition srvToSC0CascDef = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH,
//				TestConstants.log4jSrvProperties, TestConstants.pubServerName1, TestConstants.PORT_PUB_SRV_TCP,
//				TestConstants.PORT_SC0_CASC_TCP, 3, 3, TestConstants.pubServiceName1);
//		srvToSC0CascDefs.add(srvToSC0CascDef);
//		SystemSuperTest.srvDefs = srvToSC0CascDefs;
//	}

	/**
	 * Description: subscribe (regular)<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_subscribe() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		subMsgRequest.setDataLength(TestConstants.pangram.length());
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(100);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
		Assert.assertNotNull("the session ID is null", publishService.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
		Assert.assertTrue("is not subscribed", publishService.isSubscribed());
		publishService.unsubscribe();
		Assert.assertNull("the session ID is not null", publishService.getSessionId());
	}

	/**
	 * Description: subscribe, do not reject but return APPError<br>
	 * Expectation: passes
	 */
	@Test
	public void t16_subscribeAPPError() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		subMsgRequest.setDataLength(TestConstants.pangram.length());
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.echoAppErrorCmd);
		subMsgRequest.setNoDataIntervalSeconds(100);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
		Assert.assertNotNull("the session ID is null", publishService.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
		Assert.assertEquals("appErrorCode is not the same", TestConstants.appErrorCode, subMsgResponse.getAppErrorCode());
		Assert.assertEquals("appErrorText is not the same", TestConstants.appErrorText, subMsgResponse.getAppErrorText());
		Assert.assertTrue("is not subscribed", publishService.isSubscribed());
		publishService.unsubscribe();
		Assert.assertNull("the session ID is not null", publishService.getSessionId());
	}

	/**
	 * Description: subscribe, return SessionInfo<br>
	 * Expectation: passes
	 */
	@Test
	public void t17_subscribeSessionInfo() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		subMsgRequest.setDataLength(TestConstants.pangram.length());
		subMsgRequest.setMask(TestConstants.mask);
		String sessionInfo = "sessionInfoFromServer";
		subMsgRequest.setSessionInfo(sessionInfo);
		subMsgRequest.setNoDataIntervalSeconds(100);
		SCSubscribeMessage subMsgResponse = null;
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
		Assert.assertEquals("sessionInfo is not the same", sessionInfo, subMsgResponse.getSessionInfo());
		Assert.assertTrue("is not subscribed", publishService.isSubscribed());
		publishService.unsubscribe();
	}

	/**
	 * Description: subscribe to disabled service<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t30_disabledService() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(100);
		msgCallback = new MsgCallback(publishService);

		// disable service
		SCMgmtClient clientMgmt = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC0_TCP);
		clientMgmt.attach();
		clientMgmt.disableService(TestConstants.pubServiceName1);
		clientMgmt.detach();

		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
	}

	/**
	 * Description: two subscriptions from the same client<br>
	 * Expectation: passes
	 */
	@Test
	public void t40_twoSubscriptions() throws Exception {
		SCPublishService service1 = client.newPublishService(TestConstants.pubServiceName1);
		SCPublishService service2 = client.newPublishService(TestConstants.pubServiceName1);

		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		subMsgRequest.setDataLength(TestConstants.pangram.length());
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(10);

		MsgCallback cbk1 = new MsgCallback(service1);
		MsgCallback cbk2 = new MsgCallback(service2);

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

		service1.unsubscribe();
		Assert.assertNull("the session ID is not null)", service1.getSessionId());
		service2.unsubscribe();
		Assert.assertNull("the session ID is not null)", service2.getSessionId());
	}

	/**
	 * Description: subscribe twice<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t41_subscribeTwice() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(10);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
		Assert.assertNotNull("the session ID is null", publishService.getSessionId());
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
	}

	/**
	 * Description: reject subscription by server <br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t50_reject() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.rejectCmd);
		subMsgRequest.setNoDataIntervalSeconds(100);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
	}

	/**
	 * Description: reject subscription by server, check error code<br>
	 * Expectation: passes, exception catched
	 */
	@Test
	public void t51_reject() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.rejectCmd);
		subMsgRequest.setNoDataIntervalSeconds(100);
		msgCallback = new MsgCallback(publishService);
		Boolean passed = false;
		try {
			subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
		} catch (SCServiceException e) {
			passed = true;
			Assert.assertEquals("is not appErrorCode", TestConstants.appErrorCode, e.getAppErrorCode());
			Assert.assertEquals("is not appErrorText", TestConstants.appErrorText, e.getAppErrorText());
		}
		Assert.assertTrue("did not throw exception", passed);
		Assert.assertFalse("is subscribed", publishService.isSubscribed());
		publishService.unsubscribe();
		Assert.assertNull("the session ID is not null", publishService.getSessionId());
	}

	/**
	 * Description: unsubscribe (regular)<br>
	 * Expectation: passes
	 */
	@Test
	public void t60_unsubscribe() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		subMsgRequest.setDataLength(TestConstants.pangram.length());
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(100);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
		Assert.assertNotNull("the session ID is null", publishService.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());

		publishService.unsubscribe();
		Assert.assertNull("the session ID is not null", publishService.getSessionId());
	}

	/**
	 * Description: unsubscribe to disabed service<br>
	 * Expectation: passes
	 */
	@Test
	public void t61_disabledService() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(100);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);

		// disable service
		SCMgmtClient clientMgmt = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC0_TCP);
		clientMgmt.attach();
		clientMgmt.disableService(TestConstants.pubServiceName1);
		clientMgmt.detach();

		publishService.unsubscribe();
		Assert.assertNull("the session ID is not null", publishService.getSessionId());
	}

	/**
	 * Description: subscribe twice<br>
	 * Expectation: passes
	 */
	@Test
	public void t62_unsubscribeTwice() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);

		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		subMsgRequest.setDataLength(TestConstants.pangram.length());
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(100);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
		Assert.assertNotNull("the session ID is null", publishService.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());

		publishService.unsubscribe();
		Assert.assertNull("the session ID is not null", publishService.getSessionId());

		publishService.unsubscribe();
		Assert.assertNull("the session ID is not null", publishService.getSessionId());
	}

	/**
	 * Description: change subscription (regular)<br>
	 * Expectation: passes
	 */
	@Test
	public void t70_changeSubscription() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		subMsgRequest.setDataLength(TestConstants.pangram.length());
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(10);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
		Assert.assertNotNull("the session ID is null", publishService.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());

		subMsgRequest.setMask(TestConstants.mask1);
		subMsgResponse = publishService.changeSubscription(subMsgRequest);
		Assert.assertNotNull("the session ID is null", publishService.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());

		publishService.unsubscribe();
		Assert.assertNull("the session ID is not null", publishService.getSessionId());
	}

	/**
	 * Description: change subscription without subscribing<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t72_changeSubscriptionNoSubscribe() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgResponse = publishService.changeSubscription(subMsgRequest);
	}

	/**
	 * Description: change subscription after unsubscribe<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t73_changeSubscriptionAfterUnsubscribe() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		subMsgRequest.setDataLength(TestConstants.pangram.length());
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(100);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
		publishService.unsubscribe();
		Assert.assertNull("the session ID is not null", publishService.getSessionId());

		subMsgRequest.setMask(TestConstants.mask1);
		subMsgResponse = publishService.changeSubscription(subMsgRequest);
	}

	/**
	 * Description: change subscription with the same mask<br>
	 * Expectation: passes
	 */
	@Test
	public void t74_changeSubscriptionSameMask() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		subMsgRequest.setDataLength(TestConstants.pangram.length());
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(10);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
		Assert.assertNotNull("the session ID is null", publishService.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());

		subMsgRequest.setMask(TestConstants.mask);
		subMsgResponse = publishService.changeSubscription(subMsgRequest);
		Assert.assertNotNull("the session ID is null", publishService.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());

		publishService.unsubscribe();
		Assert.assertNull("the session ID is not null", publishService.getSessionId());
	}

	/**
	 * Description: change subscription with the same mask<br>
	 * Expectation: passes
	 */
	@Test
	public void t75_changeSubscriptionAPPError() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		subMsgRequest.setDataLength(TestConstants.pangram.length());
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.echoAppErrorCmd);
		subMsgRequest.setNoDataIntervalSeconds(10);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
		Assert.assertNotNull("the session ID is null", publishService.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
		Assert.assertEquals("appErrorCode is not the same", TestConstants.appErrorCode, subMsgResponse.getAppErrorCode());
		Assert.assertEquals("appErrorText is not the same", TestConstants.appErrorText, subMsgResponse.getAppErrorText());

		subMsgRequest.setMask(TestConstants.mask);
		subMsgResponse = publishService.changeSubscription(subMsgRequest);
		Assert.assertNotNull("the session ID is null", publishService.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
		Assert.assertEquals("appErrorCode is not the same", TestConstants.appErrorCode, subMsgResponse.getAppErrorCode());
		Assert.assertEquals("appErrorText is not the same", TestConstants.appErrorText, subMsgResponse.getAppErrorText());

		publishService.unsubscribe();
		Assert.assertNull("the session ID is not null", publishService.getSessionId());
	}

	/**
	 * Description: change subscription and get sessionInfo from server<br>
	 * Expectation: passes
	 */
	@Test
	public void t76_changeSubscriptionSessionInfo() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		String sessionInfo = "sessionInfoFromServer";
		subMsgRequest.setSessionInfo(sessionInfo);
		subMsgRequest.setDataLength(TestConstants.pangram.length());
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setNoDataIntervalSeconds(10);
		SCSubscribeMessage subMsgResponse = null;
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
		subMsgRequest.setMask(TestConstants.mask);
		subMsgResponse = publishService.changeSubscription(subMsgRequest);
		Assert.assertEquals("sessionInfo is not the same", sessionInfo, subMsgResponse.getSessionInfo());
		publishService.unsubscribe();
	}

	/**
	 * Description: change subscription gets rejected from server<br>
	 * Expectation: passes
	 */
	@Test
	public void t77_changeSubscriptionReject() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setNoDataIntervalSeconds(10);
		SCSubscribeMessage subMsgResponse = null;
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
		subMsgRequest.setMask(TestConstants.mask);
		try {
			subMsgRequest.setSessionInfo(TestConstants.rejectCmd);
			subMsgRequest.setMask(TestConstants.noRecvMask);
			subMsgResponse = publishService.changeSubscription(subMsgRequest);
			Assert.fail("test should throw exception");
		} catch (SCServiceException e) {
			Assert.assertEquals("is not appErrorCode", TestConstants.appErrorCode, e.getAppErrorCode());
			Assert.assertEquals("is not appErrorText", TestConstants.appErrorText, e.getAppErrorText());
		}
		publishService.unsubscribe();
	}

	/**
	 * Description: change subscription to disabled service<br>
	 * Expectation: passes
	 */
	@Test
	public void t80_disabledService() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		subMsgRequest.setDataLength(TestConstants.pangram.length());
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(10);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
		Assert.assertNotNull("the session ID is null", publishService.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());

		// disable service
		SCMgmtClient clientMgmt = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC0_TCP);
		clientMgmt.attach();
		clientMgmt.disableService(TestConstants.pubServiceName1);
		clientMgmt.detach();

		subMsgRequest.setMask(TestConstants.mask1);
		subMsgResponse = publishService.changeSubscription(subMsgRequest);
		Assert.assertNotNull("the session ID is null", publishService.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());

		publishService.unsubscribe();
		Assert.assertNull("the session ID is not null", publishService.getSessionId());
	}

	/**
	 * Description: reject subscription by server <br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t90_reject() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		subMsgRequest.setDataLength(TestConstants.pangram.length());
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(100);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);

		subMsgRequest.setMask(TestConstants.mask1);
		subMsgRequest.setSessionInfo(TestConstants.rejectCmd);
		subMsgResponse = publishService.changeSubscription(subMsgRequest);
	}

	/**
	 * Description: reject change subscription by server, check error code<br>
	 * Expectation: passes, exception caught
	 */
	@Test
	public void t91_reject() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		subMsgRequest.setDataLength(TestConstants.pangram.length());
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(100);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
		Assert.assertNotNull("the session ID is null", publishService.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());

		subMsgRequest.setMask(TestConstants.mask1);
		subMsgRequest.setSessionInfo(TestConstants.rejectCmd);

		Boolean passed = false;
		try {
			subMsgResponse = publishService.changeSubscription(subMsgRequest);
		} catch (SCServiceException e) {
			passed = true;
			Assert.assertEquals("is not appErrorCode", TestConstants.appErrorCode, e.getAppErrorCode());
			Assert.assertEquals("is not appErrorText", TestConstants.appErrorText, e.getAppErrorText());
		}
		Assert.assertTrue("did not throw exception", passed);
		Assert.assertTrue("is nor subscribed", publishService.isSubscribed());
		publishService.unsubscribe();
		Assert.assertNull("the session ID is not null)", publishService.getSessionId());
	}

	/**
	 * Description: two clients subscribe to a message queue, the server gets destroyed<br>
	 * Expectation: clients get a not found error, passes
	 */
	@Test
	public void t95_TwoSubscribersServerGetsDestroyed() throws Exception {
		SCClient client2 = new SCClient(TestConstants.HOST, TestConstants.PORT_SC1_TCP, ConnectionType.NETTY_TCP);
		client2.attach();
		SCPublishService service1 = client.newPublishService(TestConstants.pubServiceName1);
		SCPublishService service2 = client2.newPublishService(TestConstants.pubServiceName1);

		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		subMsgRequest.setDataLength(TestConstants.pangram.length());
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(10);

		MsgCallback cbk1 = new MsgCallback(service1);
		MsgCallback cbk2 = new MsgCallback(service2);

		subMsgResponse = service1.subscribe(subMsgRequest, cbk1);
		subMsgResponse = service2.subscribe(subMsgRequest, cbk2);

		// destroy the server
		SystemSuperTest.ctrl.stopServerEnvironment(SystemSuperTest.srvCtxs);
		cbk1.waitForMessage(2);
		cbk2.waitForMessage(2);

		Assert.assertFalse("service1 is still active", service1.isActive());
		Assert.assertFalse("service2 is still active", service2.isActive());
		client2.detach();
	}
}