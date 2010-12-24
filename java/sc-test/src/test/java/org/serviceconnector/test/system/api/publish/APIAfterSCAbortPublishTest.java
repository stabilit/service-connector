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

import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.test.system.api.APISystemSuperPublishClientTest;

public class APIAfterSCAbortPublishTest extends APISystemSuperPublishClientTest {

	/**
	 * Description: receive after SC abort <br>
	 * Expectation: passes
	 */
	@Test
	public void t01_receive() throws Exception {
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
		
		ctrl.stopSC(scCtx);
	
		cbk.waitForMessage(10);

		Assert.assertTrue("Test is not implemented", false);
	}	
}
