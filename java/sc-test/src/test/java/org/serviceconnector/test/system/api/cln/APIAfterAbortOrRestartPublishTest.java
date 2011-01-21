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
package org.serviceconnector.test.system.api.cln;

import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.test.system.api.APISystemSuperPublishClientTest;

public class APIAfterAbortOrRestartPublishTest extends APISystemSuperPublishClientTest {

	/**
	 * Description: receive after SC abort <br>
	 * Expectation: ?
	 */
	@Test
	public void t01_receivePublication() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		MsgCallback cbk = new MsgCallback(publishService);
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.publishCompressedMsgCmd);
		int nrMessages = 1;
		subMsgRequest.setData(Integer.toString(nrMessages));
		cbk.setExpectedMessages(nrMessages);
		subMsgResponse = publishService.subscribe(subMsgRequest, cbk);
		
		ctrl.stopSC(scCtxs.get("SC1"));
	
		cbk.waitForMessage(10);

		Assert.fail("Test is not implemented");
	}
	
	/**
	 * Description: receive after server abort <br>
	 * Expectation: ?
	 */
	@Test
	public void t02_receivePublication() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		MsgCallback cbk = new MsgCallback(publishService);
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.publishCompressedMsgCmd);
		int nrMessages = 1;
		subMsgRequest.setData(Integer.toString(nrMessages));
		cbk.setExpectedMessages(nrMessages);
		subMsgResponse = publishService.subscribe(subMsgRequest, cbk);
		
		ctrl.stopServer(pubSrvCtx);
		
		cbk.waitForMessage(10);
		
		Assert.fail("Test is not implemented");
	}

	/**
	 * Description: receive after server restart <br>
	 * Expectation: ?
	 */
	@Test
	public void t03_receivePublication() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		msgCallback = new MsgCallback(publishService);
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.publishCompressedMsgCmd);
		int nrMessages = 1;
		subMsgRequest.setData(Integer.toString(nrMessages));
		msgCallback.setExpectedMessages(nrMessages);
		subMsgResponse = publishService.subscribe(subMsgRequest, msgCallback);
		
		ctrl.stopServer(pubSrvCtx);
		pubSrvCtx = ctrl.startServer(TestConstants.COMMUNICATOR_TYPE_PUBLISH, TestConstants.log4jSrvProperties,
				TestConstants.pubServerName1, TestConstants.PORT_PUB_SRV_TCP, TestConstants.PORT_SC_TCP, 100, 10,
				TestConstants.pubServiceName1);
		
		msgCallback.waitForMessage(10);
		
		Assert.fail("Test is not implemented");
	}

}
