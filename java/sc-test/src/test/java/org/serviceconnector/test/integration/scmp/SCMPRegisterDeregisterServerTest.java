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
package org.serviceconnector.test.integration.scmp;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestCallback;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.call.SCMPDeRegisterServerCall;
import org.serviceconnector.call.SCMPRegisterServerCall;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.integration.IntegrationSuperTest;
import org.serviceconnector.util.DateTimeUtility;

public class SCMPRegisterDeregisterServerTest extends IntegrationSuperTest {

	private SCRequester requester;

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		this.requester = new SCRequester(new RemoteNodeConfiguration(TestConstants.RemoteNodeName, TestConstants.HOST,
				TestConstants.PORT_SC_HTTP, ConnectionType.NETTY_HTTP.getValue(), 0, 1));
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			requester.destroy();
		} catch (Exception e) {
		}
		requester = null;
		super.afterOneTest();
	}

	/**
	 * Description: register server call - keep alive interval not set<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_RegisterServerCallKeepAliveIntervalNotSet() throws Exception {
		SCMPRegisterServerCall registerServerCall = new SCMPRegisterServerCall(this.requester, TestConstants.sesServerName1);
		TestCallback cbk = new TestCallback();

		registerServerCall.setMaxSessions(10);
		registerServerCall.setMaxConnections(10);
		registerServerCall.setPortNumber(9100);
		registerServerCall.setImmediateConnect(true);
		registerServerCall.setVersion(SCMPMessage.SC_VERSION.toString());
		registerServerCall.setLocalDateTime(DateTimeUtility.getCurrentTimeZoneMillis());
		registerServerCall.invoke(cbk, 1000);
		SCMPMessage reply = cbk.getMessageSync(3000);
		Assert.assertTrue(reply.isFault());
		TestUtil.verifyError(reply, SCMPError.HV_WRONG_KEEPALIVE_INTERVAL, SCMPMsgType.REGISTER_SERVER);
	}

	/**
	 * Description: register server call - invalid max sessions<br>
	 * Expectation: passes
	 */
	@Test
	public void t02_RegisterServerCallInvalidMaxSessions() throws Exception {
		SCMPRegisterServerCall registerServerCall = new SCMPRegisterServerCall(this.requester, TestConstants.sesServerName1);
		TestCallback cbk = new TestCallback();

		registerServerCall.setPortNumber(9100);
		registerServerCall.setMaxSessions(0);
		registerServerCall.setMaxConnections(10);
		registerServerCall.setImmediateConnect(true);
		registerServerCall.setKeepAliveInterval(360);
		registerServerCall.setVersion(SCMPMessage.SC_VERSION.toString());
		registerServerCall.setLocalDateTime(DateTimeUtility.getCurrentTimeZoneMillis());
		registerServerCall.invoke(cbk, 1000);
		SCMPMessage reply = cbk.getMessageSync(3000);
		Assert.assertTrue(reply.isFault());
		TestUtil.verifyError(reply, SCMPError.HV_WRONG_MAX_SESSIONS, SCMPMsgType.REGISTER_SERVER);
	}

	/**
	 * Description: register server call - invalid max connections<br>
	 * Expectation: passes
	 */
	@Test
	public void t03_RegisterServerCallInvalidMaxConnections() throws Exception {
		SCMPRegisterServerCall registerServerCall = new SCMPRegisterServerCall(this.requester, TestConstants.sesServerName1);
		TestCallback cbk = new TestCallback();

		registerServerCall.setPortNumber(9100);
		registerServerCall.setMaxSessions(10);
		registerServerCall.setMaxConnections(0);
		registerServerCall.setImmediateConnect(true);
		registerServerCall.setKeepAliveInterval(360);
		registerServerCall.setVersion(SCMPMessage.SC_VERSION.toString());
		registerServerCall.setLocalDateTime(DateTimeUtility.getCurrentTimeZoneMillis());
		registerServerCall.invoke(cbk, 1000);
		SCMPMessage reply = cbk.getMessageSync(3000);
		Assert.assertTrue(reply.isFault());
		TestUtil.verifyError(reply, SCMPError.HV_WRONG_MAX_CONNECTIONS, SCMPMsgType.REGISTER_SERVER);
	}

	/**
	 * Description: register server call - invalid port number<br>
	 * Expectation: passes
	 */
	@Test
	public void t04_RegisterServerCallInvalidPortNumber() throws Exception {
		SCMPRegisterServerCall registerServerCall = new SCMPRegisterServerCall(this.requester, TestConstants.sesServerName1);
		TestCallback cbk = new TestCallback();

		registerServerCall.setPortNumber(910000);
		registerServerCall.setMaxSessions(10);
		registerServerCall.setMaxConnections(10);
		registerServerCall.setImmediateConnect(true);
		registerServerCall.setKeepAliveInterval(360);
		registerServerCall.setVersion(SCMPMessage.SC_VERSION.toString());
		registerServerCall.setLocalDateTime(DateTimeUtility.getCurrentTimeZoneMillis());
		registerServerCall.invoke(cbk, 1000);
		SCMPMessage reply = cbk.getMessageSync(3000);
		Assert.assertTrue(reply.isFault());
		TestUtil.verifyError(reply, SCMPError.HV_WRONG_PORTNR, SCMPMsgType.REGISTER_SERVER);
	}

	/**
	 * Description: register server call - unknown service name<br>
	 * Expectation: passes
	 */
	@Test
	public void t05_RegisterServerCallForUnknownService() throws Exception {
		SCMPRegisterServerCall registerServerCall = new SCMPRegisterServerCall(this.requester, "notConfiguredServiceName");
		TestCallback cbk = new TestCallback();

		registerServerCall.setMaxSessions(10);
		registerServerCall.setMaxConnections(10);
		registerServerCall.setPortNumber(TestConstants.PORT_SES_SRV_TCP);
		registerServerCall.setImmediateConnect(true);
		registerServerCall.setKeepAliveInterval(360);
		registerServerCall.setVersion(SCMPMessage.SC_VERSION.toString());
		registerServerCall.setLocalDateTime(DateTimeUtility.getCurrentTimeZoneMillis());

		registerServerCall.invoke(cbk, 1000);
		SCMPMessage reply = cbk.getMessageSync(3000);
		Assert.assertTrue(reply.isFault());
		TestUtil.verifyError(reply, SCMPError.SERVICE_NOT_FOUND, SCMPMsgType.REGISTER_SERVER);
	}

	/**
	 * Description: register server call - deregister server call<br>
	 * Expectation: passes
	 */
	@Test
	public void t20_RegisterServerCallDeregisterServerCall() throws Exception {
		SCMPRegisterServerCall registerServerCall = new SCMPRegisterServerCall(this.requester, TestConstants.pubServerName1);
		TestCallback cbk = new TestCallback();

		registerServerCall.setMaxSessions(10);
		registerServerCall.setMaxConnections(10);
		registerServerCall.setPortNumber(51000);
		registerServerCall.setImmediateConnect(true);
		registerServerCall.setKeepAliveInterval(360);
		registerServerCall.setVersion(SCMPMessage.SC_VERSION.toString());
		registerServerCall.setLocalDateTime(DateTimeUtility.getCurrentTimeZoneMillis());

		registerServerCall.invoke(cbk, 8000);
		TestUtil.checkReply(cbk.getMessageSync(10000));

		SCMPDeRegisterServerCall deRegisterServerCall = new SCMPDeRegisterServerCall(this.requester, TestConstants.pubServerName1);
		deRegisterServerCall.invoke(cbk, 8000);
		TestUtil.checkReply(cbk.getMessageSync(10000));
	}

	/**
	 * Description: register server twice<br>
	 * Expectation: passes
	 */
	@Test
	public void t30_DeRegisterServerCallTwice() throws Exception {
		SCMPRegisterServerCall registerServerCall = new SCMPRegisterServerCall(this.requester, TestConstants.pubServerName1);
		TestCallback cbk = new TestCallback();

		registerServerCall.setMaxSessions(10);
		registerServerCall.setMaxConnections(10);
		registerServerCall.setPortNumber(51000);
		registerServerCall.setImmediateConnect(true);
		registerServerCall.setKeepAliveInterval(360);
		registerServerCall.setVersion(SCMPMessage.SC_VERSION.toString());
		registerServerCall.setLocalDateTime(DateTimeUtility.getCurrentTimeZoneMillis());

		registerServerCall.invoke(cbk, 8000);
		TestUtil.checkReply(cbk.getMessageSync(10000));
		// first deregister server call
		SCMPDeRegisterServerCall deRegisterServerCall = new SCMPDeRegisterServerCall(this.requester, TestConstants.pubServerName1);
		deRegisterServerCall.invoke(cbk, 8000);
		TestUtil.checkReply(cbk.getMessageSync(10000));
		// second deregister server call
		deRegisterServerCall.invoke(cbk, 8000);
		SCMPMessage reply = cbk.getMessageSync(10000);
		Assert.assertTrue(reply.isFault());
		Assert.assertEquals(SCMPMsgType.DEREGISTER_SERVER.getValue(), reply.getHeader(SCMPHeaderAttributeKey.MSG_TYPE));
		Assert.assertEquals(SCMPError.SERVER_NOT_FOUND.getErrorCode(), reply.getHeaderInt(SCMPHeaderAttributeKey.SC_ERROR_CODE).intValue());
	}
}