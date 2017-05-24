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

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestCallback;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.call.SCMPClnCreateSessionCall;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.ctrl.util.ServerDefinition;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.system.SystemSuperTest;
import org.serviceconnector.test.system.scmp.casc1.SCMPClnCreateSessionCasc1Test;

import junit.framework.Assert;

/**
 * The Class ClnCreateSessionTestCase.
 */
public class SCMPClnCreateSessionTest extends SCMPClnCreateSessionCasc1Test {

	public SCMPClnCreateSessionTest() {
		SystemSuperTest.setUpServiceConnectorAndServer();
		// server definitions needs to be different
		List<ServerDefinition> srvToSC0Defs = new ArrayList<ServerDefinition>();
		ServerDefinition srvToSC0Def = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_SESSION, TestConstants.logbackSrv, TestConstants.sesServerName1,
				TestConstants.PORT_SES_SRV_TCP, TestConstants.PORT_SC0_TCP, 3, 2, TestConstants.sesServiceName1);
		srvToSC0Defs.add(srvToSC0Def);
		SystemSuperTest.srvDefs = srvToSC0Defs;
	}

	@Override
	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		this.requester = new SCRequester(
				new RemoteNodeConfiguration(TestConstants.RemoteNodeName, TestConstants.HOST, TestConstants.PORT_SC0_HTTP, ConnectionType.NETTY_HTTP.getValue(), 0, 0, 3), 0);
		AppContext.init();
	}

	/**
	 * Description: create session - echo time interval wrong<br>
	 * Expectation: passes, returns error
	 */
	@Test
	public void t01_WrongECI() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = new SCMPClnCreateSessionCall(this.requester, TestConstants.sesServerName1);
		createSessionCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		createSessionCall.setEchoIntervalSeconds(0);
		createSessionCall.getRequest().setServiceName("session-1");
		TestCallback cbk = new TestCallback();
		createSessionCall.invoke(cbk, 3000);
		SCMPMessage fault = cbk.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		TestUtil.verifyError(fault, SCMPError.HV_WRONG_ECHO_INTERVAL, SCMPMsgType.CLN_CREATE_SESSION);
	}

	/**
	 * Description: create session - serviceName not set<br>
	 * Expectation: passes, returns error
	 */
	@Test
	public void t02_ServiceNameMissing() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = new SCMPClnCreateSessionCall(this.requester, TestConstants.sesServerName1);
		createSessionCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		// set serviceName null
		createSessionCall.getRequest().setServiceName(null);
		createSessionCall.setEchoIntervalSeconds(300);
		TestCallback cbk = new TestCallback();
		createSessionCall.invoke(cbk, 1000);
		SCMPMessage fault = cbk.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		TestUtil.verifyError(fault, SCMPError.HV_WRONG_SERVICE_NAME, SCMPMsgType.CLN_CREATE_SESSION);
	}

	/**
	 * Description: create session - service name = ""<br>
	 * Expectation: passes, returns error
	 */
	@Test
	public void t03_ServiceNameEmpty() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = new SCMPClnCreateSessionCall(this.requester, TestConstants.sesServerName1);
		createSessionCall.getRequest().setServiceName("");
		createSessionCall.setEchoIntervalSeconds(300);
		TestCallback cbk = new TestCallback();
		createSessionCall.invoke(cbk, 1000);
		SCMPMessage fault = cbk.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		TestUtil.verifyError(fault, SCMPError.HV_WRONG_SERVICE_NAME, SCMPMsgType.CLN_CREATE_SESSION);
	}

	/**
	 * Description: create session - service name = " "<br>
	 * Expectation: passes, returns error
	 */
	@Test
	public void t04_ServiceNameBlank() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = new SCMPClnCreateSessionCall(this.requester, TestConstants.sesServerName1);
		createSessionCall.getRequest().setServiceName(" ");
		createSessionCall.setEchoIntervalSeconds(300);
		TestCallback cbk = new TestCallback();
		createSessionCall.invoke(cbk, 1000);
		SCMPMessage fault = cbk.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		TestUtil.verifyError(fault, SCMPError.HV_WRONG_SERVICE_NAME, SCMPMsgType.CLN_CREATE_SESSION);
	}

	/**
	 * Description: create session - serviceName too long<br>
	 * Expectation: passes, returns error
	 */
	@Test
	public void t05_ServiceNameTooLong() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = new SCMPClnCreateSessionCall(this.requester, TestConstants.sesServerName1);
		createSessionCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		// set serviceName null
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 98 << 10; i++) {
			sb.append(i);
			if (sb.length() > 10000) {
				break;
			}
		}
		createSessionCall.getRequest().setServiceName(sb.toString());
		createSessionCall.setEchoIntervalSeconds(300);
		TestCallback cbk = new TestCallback();
		createSessionCall.invoke(cbk, 1000);
		SCMPMessage fault = cbk.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		TestUtil.verifyError(fault, SCMPError.HV_WRONG_SERVICE_NAME, SCMPMsgType.CLN_CREATE_SESSION);
	}

	/**
	 * Description: create session - service name = gaga<br>
	 * Expectation: passes, returns error
	 */
	@Test
	public void t06_NonExistingServiceName() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = new SCMPClnCreateSessionCall(this.requester, TestConstants.sesServerName1);
		createSessionCall.getRequest().setServiceName("Gaga");
		createSessionCall.setEchoIntervalSeconds(300);
		TestCallback cbk = new TestCallback();
		createSessionCall.invoke(cbk, 1000);
		SCMPMessage fault = cbk.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		TestUtil.verifyError(fault, SCMPError.SERVICE_NOT_FOUND, SCMPMsgType.CLN_CREATE_SESSION);
	}
}
