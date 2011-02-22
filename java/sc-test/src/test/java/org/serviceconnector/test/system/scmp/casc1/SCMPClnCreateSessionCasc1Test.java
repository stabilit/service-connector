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
package org.serviceconnector.test.system.scmp.casc1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestCallback;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.call.SCMPClnCreateSessionCall;
import org.serviceconnector.call.SCMPClnDeleteSessionCall;
import org.serviceconnector.call.SCMPClnExecuteCall;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ServerDefinition;
import org.serviceconnector.ctrl.util.ServiceConnectorDefinition;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.system.SystemSuperTest;

/**
 * The Class ClnCreateSessionTestCase.
 */
public class SCMPClnCreateSessionCasc1Test extends SystemSuperTest {

	protected SCRequester requester;
	protected static List<ServerDefinition> srvDefs;
	protected static Map<String, ProcessCtx> sesSrvCtxs;

	public SCMPClnCreateSessionCasc1Test() {
		SCMPClnCreateSessionCasc1Test.setUpCascadedServiceConnectorAndServer();
	}

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		sesSrvCtxs = ctrl.startServerEnvironment(SCMPClnCreateSessionCasc1Test.srvDefs);
		this.requester = new SCRequester(new RemoteNodeConfiguration(TestConstants.RemoteNodeName, TestConstants.HOST,
				TestConstants.PORT_SC_HTTP, ConnectionType.NETTY_HTTP.getValue(), 0, 1));
		AppContext.init();
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			this.requester.destroy();
		} catch (Exception e) {
		}
		this.requester = null;
		try {
			ctrl.stopServerEnvironment(sesSrvCtxs);
		} catch (Exception e) {
		}
		super.afterOneTest();
	}

	public static void setUpCascadedServiceConnectorAndServer() {
		List<ServiceConnectorDefinition> scCascDefs = new ArrayList<ServiceConnectorDefinition>();
		ServiceConnectorDefinition sc0CascDef = new ServiceConnectorDefinition(TestConstants.SC0_CASC,
				TestConstants.SC0CASCProperties, TestConstants.log4jSC0CASCProperties);
		ServiceConnectorDefinition sc1CascDef = new ServiceConnectorDefinition(TestConstants.SC1_CASC,
				TestConstants.SC1CASCProperties, TestConstants.log4jSC1CASCProperties);
		scCascDefs.add(sc0CascDef);
		scCascDefs.add(sc1CascDef);

		List<ServerDefinition> srvToSC0CascDefs = new ArrayList<ServerDefinition>();
		ServerDefinition srvToSC0CascDef = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_SESSION,
				TestConstants.log4jSrvProperties, TestConstants.sesServerName1, TestConstants.PORT_SES_SRV_TCP,
				TestConstants.PORT_SC0_CASC_TCP, 100, 10, TestConstants.sesServiceName1);
		srvToSC0CascDefs.add(srvToSC0CascDef);

		SystemSuperTest.scDefs = scCascDefs;
		SCMPClnCreateSessionCasc1Test.srvDefs = srvToSC0CascDefs;
	}

	/**
	 * Description: create session - delete session<br>
	 * Expectation: passes
	 */
	@Test
	public void t10_CreateSessionDeleteSession() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = new SCMPClnCreateSessionCall(this.requester, TestConstants.sesServerName1);
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoIntervalSeconds(3000);
		TestCallback cbk = new TestCallback();
		createSessionCall.invoke(cbk, 2000);
		SCMPMessage responseMessage = cbk.getMessageSync(4000);
		String sessId = responseMessage.getSessionId();
		TestUtil.checkReply(responseMessage);

		SCMPClnDeleteSessionCall deleteSessionCall = new SCMPClnDeleteSessionCall(this.requester, responseMessage.getServiceName(),
				sessId);
		deleteSessionCall.invoke(cbk, 2000);
		responseMessage = cbk.getMessageSync(4000);
		TestUtil.checkReply(responseMessage);
	}

	/**
	 * Description: create session - session gets rejected<br>
	 * Expectation: passes, returns rejection
	 */
	@Test
	public void t20_SessionRejected() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = new SCMPClnCreateSessionCall(this.requester, TestConstants.sesServerName1);
		createSessionCall.setSessionInfo(TestConstants.rejectCmd);
		createSessionCall.setEchoIntervalSeconds(300);
		TestCallback cbk = new TestCallback();
		createSessionCall.invoke(cbk, 4000);
		SCMPMessage responseMessage = cbk.getMessageSync(3000);
		String sessId = responseMessage.getSessionId();
		Assert.assertNull(sessId);
		Assert.assertFalse(responseMessage.isFault());
		Assert.assertTrue(responseMessage.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION));
	}

	/**
	 * Description: create session - wait until session times out<br>
	 * Expectation: passes, returns error
	 */
	@Test
	public void t30_SessionTimesOut() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = new SCMPClnCreateSessionCall(this.requester, TestConstants.sesServerName1);
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoIntervalSeconds(10);
		TestCallback cbk = new TestCallback();
		createSessionCall.invoke(cbk, 4000);
		SCMPMessage responseMessage = cbk.getMessageSync(3000);
		TestUtil.checkReply(responseMessage);

		String sessionId = responseMessage.getSessionId();
		// wait until session times out and get cleaned up
		Thread.sleep(13000);
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.echoCmd);
		clnExecuteCall.setRequestBody(TestConstants.pangram);
		clnExecuteCall.invoke(cbk, 2000);
		SCMPMessage msg = cbk.getMessageSync(3000);
		TestUtil.verifyError(msg, SCMPError.SESSION_NOT_FOUND, SCMPMsgType.CLN_EXECUTE);
	}
}