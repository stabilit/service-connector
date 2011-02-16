/*-----------------------------------------------------------------------------*
 *                                                                             *
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
 *-----------------------------------------------------------------------------*/
package org.serviceconnector.test.system.scmp;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.serviceconnector.TestCallback;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.call.SCMPClnSubscribeCall;
import org.serviceconnector.call.SCMPClnUnsubscribeCall;
import org.serviceconnector.ctrl.util.ServerDefinition;
import org.serviceconnector.ctrl.util.ServiceConnectorDefinition;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.system.SystemSuperTest;

public class SCMPClnSubscribeTest extends org.serviceconnector.test.system.scmp.casc.SCMPClnSubscribeTest {

	public SCMPClnSubscribeTest() {
		SCMPClnSubscribeTest.setUpServiceConnectorAndServer();
	}

	public static void setUpServiceConnectorAndServer() {
		// SC definitions
		List<ServiceConnectorDefinition> sc0Defs = new ArrayList<ServiceConnectorDefinition>();
		ServiceConnectorDefinition sc0Def = new ServiceConnectorDefinition(TestConstants.SC0, TestConstants.SC0Properties,
				TestConstants.log4jSC0Properties);
		sc0Defs.add(sc0Def);

		// server definitions
		List<ServerDefinition> srvToSC0Defs = new ArrayList<ServerDefinition>();

		ServerDefinition srvToSC0Def = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH,
				TestConstants.log4jSrvProperties, TestConstants.pubServerName1, TestConstants.PORT_PUB_SRV_TCP,
				TestConstants.PORT_SC_TCP, 1, 1, TestConstants.pubServiceName1);
		srvToSC0Defs.add(srvToSC0Def);

		SystemSuperTest.scDefs = sc0Defs;
		SCMPClnSubscribeTest.srvDefs = srvToSC0Defs;
	}
	
	/**
	 * Description: subscribe - waits 2 seconds - another subscribe fails because no free server is available<br>
	 * Expectation: passes
	 */
	@Override
	public void t30_FailsNoFreeServer() throws Exception {
		SCMPClnSubscribeCall subscribeCall = new SCMPClnSubscribeCall(this.requester, TestConstants.pubServerName1);

		subscribeCall.setSessionInfo(TestConstants.sleepCmd);
		subscribeCall.setNoDataIntervalSeconds(10);
		subscribeCall.setMask(TestConstants.mask);
		TestCallback cbk = new TestCallback(true);
		subscribeCall.setRequestBody("3000");
		subscribeCall.invoke(cbk, 5000);

		Thread.sleep(100);
		subscribeCall = new SCMPClnSubscribeCall(this.requester, TestConstants.pubServerName1);

		subscribeCall.setNoDataIntervalSeconds(10);
		subscribeCall.setMask(TestConstants.mask);
		TestCallback cbk1 = new TestCallback(true);
		subscribeCall.invoke(cbk1, 2000);

		SCMPMessage reply = cbk.getMessageSync(5000);
		SCMPMessage reply1 = cbk1.getMessageSync(4000);
		String sessionId = reply.getSessionId();

		TestUtil.checkReply(reply);
		Assert.assertTrue(reply1.isFault());
		TestUtil.verifyError(reply1, SCMPError.NO_FREE_SERVER, SCMPMsgType.CLN_SUBSCRIBE);

		SCMPClnUnsubscribeCall unSubscribeCall = new SCMPClnUnsubscribeCall(this.requester, TestConstants.pubServerName1, sessionId);
		unSubscribeCall.invoke(cbk, 4000);
		TestUtil.checkReply(cbk.getMessageSync(4000));
	}
}