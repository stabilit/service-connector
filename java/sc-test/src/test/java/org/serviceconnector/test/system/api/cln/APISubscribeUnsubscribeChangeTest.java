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
package org.serviceconnector.test.system.api.cln;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ServerDefinition;
import org.serviceconnector.test.system.SystemSuperTest;
import org.serviceconnector.test.system.api.APISystemSuperPublishClientTest;
import org.serviceconnector.test.system.api.cln.casc1.APISubscribeUnsubscribeChangeCasc1Test;

@SuppressWarnings("unused")
public class APISubscribeUnsubscribeChangeTest extends APISubscribeUnsubscribeChangeCasc1Test {

	public APISubscribeUnsubscribeChangeTest() {
		APISubscribeUnsubscribeChangeTest.setUpServiceConnectorAndServer();
	}

	/**
	 * Description: create publish service with name = "service = gaga"<br>
	 * Expectation: throws SCMPValidatorException (contains "=")
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t06_subscribeWrongservice() throws Exception {
		publishService = client.newPublishService("service = gaga");
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		subMsgRequest.setMask(TestConstants.mask);
		SCSubscribeMessage subMsgResponse = null;
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
	}

	/**
	 * Description: subscribe with service name = null<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t08_subscribeNullService() throws Exception {
		publishService = client.newPublishService(null);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(" ");
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(100);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
	}

	/**
	 * Description: subscribe with file service name<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t10_subscribeWrongServiceType() throws Exception {
		publishService = client.newPublishService(TestConstants.filServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(" ");
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(100);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
	}

	/**
	 * Description: subscribe with non-existing service name<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t12_subscribeNonExistingService() throws Exception {
		publishService = client.newPublishService("gaga");
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(" ");
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(100);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
	}

	/**
	 * Description: subscribe with session service name<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t13_subscribeWrongServiceType() throws Exception {
		publishService = client.newPublishService(TestConstants.sesServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(" ");
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(100);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
	}

	/**
	 * Description: subscribe with callback = null<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t15_subscribeInvalidCallback() throws Exception {
		publishService = client.newPublishService(TestConstants.filServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(" ");
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(100);
		subMsgResponse = publishService.subscribe(subMsgRequest, null);
	}

	/**
	 * Description: subscribe with sessionInfo = ""<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t17_subscribeEmptySessionInfo() throws Exception {
		publishService = client.newPublishService(TestConstants.filServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo("");
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
	}

	/**
	 * Description: subscribe with sessionInfo = " "<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t18_subscribeBlankSessionInfo() throws Exception {
		publishService = client.newPublishService(TestConstants.filServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(" ");
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
	}

	/**
	 * Description: subscribe with sessionInfo = 257char<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t19_subscribeSessionInfoTooLong() throws Exception {
		publishService = client.newPublishService(TestConstants.filServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.stringLength257);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
	}

	/**
	 * Description: subscribe with mask = null<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t20_subscribeNullMask() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(null);
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(100);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
	}

	/**
	 * Description: subscribe with no mask<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t21_subscribeNoMask() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(100);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
	}

	/**
	 * Description: subscribe with mask = " "<br>
	 * Expectation: passes
	 */
	@Test
	public void t22_subscribeBlankMask() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(" ");
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(100);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
	}

	/**
	 * Description: subscribe with mask = string[257]<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t23_subscribeMaskTooLong() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.stringLength257);
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(100);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
	}

	/**
	 * Description: subscribe with mask = abc%xy<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t23_subscribeMaskIllegalCharacter() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask("abc%xy");
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(100);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
	}

	/**
	 * Description: subscribe service with noDataInterval = 1 sec<br>
	 * Expectation: passes
	 */
	@Test
	public void t25_noDataInterval() throws Exception {
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
		Assert.assertTrue("is not subscribed", publishService.isSubscribed());
		publishService.unsubscribe();
		Assert.assertNull("the session ID is not null", publishService.getSessionId());
	}

	/**
	 * Description: subscribe with noDataInterval = -1<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t26_noDataIntervalNegative() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(-1);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
	}

	/**
	 * Description: subscribe with noDataInterval = 0<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t27_noDataIntervalZero() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(0);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
	}

	/**
	 * Description: subscribe with noDataInterval = 67000<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t28_noDataInterval() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(67000);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
	}

	/**
	 * Description: change subscription with mask = null<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t71_changeSubscriptionNullMask() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		subMsgRequest.setDataLength(TestConstants.pangram.length());
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(100);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);

		subMsgRequest.setMask(null);
		subMsgResponse = publishService.changeSubscription(subMsgRequest);
	}

	/**
	 * Description: change subscription with mask = abc%xy<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t76_changeSubscriptionMaskIllegalCharacter() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		subMsgRequest.setDataLength(TestConstants.pangram.length());
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.doNothingCmd);
		subMsgRequest.setNoDataIntervalSeconds(100);
		msgCallback = new MsgCallback(publishService);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);

		subMsgRequest.setMask("abc%xy");
		subMsgResponse = publishService.changeSubscription(subMsgRequest);
	}
}