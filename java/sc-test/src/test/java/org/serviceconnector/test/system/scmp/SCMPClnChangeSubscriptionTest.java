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

import org.junit.Test;
import org.serviceconnector.TestCallback;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.call.SCMPClnChangeSubscriptionCall;
import org.serviceconnector.call.SCMPClnSubscribeCall;
import org.serviceconnector.call.SCMPClnUnsubscribeCall;
import org.serviceconnector.ctrl.util.ServerDefinition;
import org.serviceconnector.ctrl.util.ServiceConnectorDefinition;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.system.SystemSuperTest;
import org.serviceconnector.test.system.scmp.casc1.SCMPClnChangeSubscriptionCasc1Test;

public class SCMPClnChangeSubscriptionTest extends SCMPClnChangeSubscriptionCasc1Test {

	public SCMPClnChangeSubscriptionTest() {
		SCMPClnChangeSubscriptionTest.setUpServiceConnectorAndServer();
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
		SystemSuperTest.srvDefs = srvToSC0Defs;
	}

	/**
	 * Description: change subscription twice, second one fails because there is no free connection<br>
	 * Expectation: passes
	 */
	@Test
	public void t20_ChangeTwiceFailsNoFreeConnection() throws Exception {
		SCMPClnSubscribeCall subscribeCall = new SCMPClnSubscribeCall(this.requester, TestConstants.pubServerName1);

		subscribeCall.setSessionInfo(TestConstants.publishCompressedMsgCmd);
		subscribeCall.setNoDataIntervalSeconds(10);
		subscribeCall.setMask(TestConstants.mask);
		subscribeCall.setRequestBody("100");
		TestCallback cbk = new TestCallback(true);
		subscribeCall.invoke(cbk, 1300);
		SCMPMessage reply = cbk.getMessageSync(1300);
		TestUtil.checkReply(reply);
		String sessionId = reply.getSessionId();

		SCMPClnChangeSubscriptionCall changeSubscriptionCall = new SCMPClnChangeSubscriptionCall(this.requester,
				TestConstants.pubServerName1, sessionId);
		// mask matches now
		changeSubscriptionCall.setMask(TestConstants.mask);
		changeSubscriptionCall.setSessionInfo(TestConstants.sleepCmd);
		changeSubscriptionCall.setRequestBody("2000");
		cbk = new TestCallback(true);
		changeSubscriptionCall.invoke(cbk, 4000);

		changeSubscriptionCall = new SCMPClnChangeSubscriptionCall(this.requester, TestConstants.pubServerName1, sessionId);
		changeSubscriptionCall.setMask(TestConstants.mask);
		TestCallback cbk1 = new TestCallback(true);
		changeSubscriptionCall.invoke(cbk1, 1300);

		TestUtil.checkReply(cbk.getMessageSync(4000));
		reply = cbk1.getMessageSync(1300);
		Assert.assertTrue(reply.isFault());
		TestUtil.verifyError(reply, SCMPError.NO_FREE_CONNECTION, SCMPMsgType.CLN_CHANGE_SUBSCRIPTION); // TODO JOT ##testing läuft

		SCMPClnUnsubscribeCall unSubscribeCall = new SCMPClnUnsubscribeCall(this.requester, TestConstants.pubServerName1, sessionId);
		cbk = new TestCallback(true);
		unSubscribeCall.invoke(cbk, 1300);
		reply = cbk.getMessageSync(1300);
		TestUtil.checkReply(reply);
	}
}