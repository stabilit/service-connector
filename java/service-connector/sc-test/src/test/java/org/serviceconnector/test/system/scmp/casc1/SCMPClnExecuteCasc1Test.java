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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

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
import org.serviceconnector.ctrl.util.ServerDefinition;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPBodyType;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.system.SystemSuperTest;

import junit.framework.Assert;

/**
 * @author JTraber
 */
public class SCMPClnExecuteCasc1Test extends SystemSuperTest {

	protected SCRequester requester;
	protected String sessionId;

	public SCMPClnExecuteCasc1Test() {
		SCMPClnExecuteCasc1Test.setUp1CascadedServiceConnectorAndServer();
	}

	@Override
	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		if (cascadingLevel == 1) {
			this.requester = new SCRequester(
					new RemoteNodeConfiguration(TestConstants.RemoteNodeName, TestConstants.HOST, TestConstants.PORT_SC1_HTTP, ConnectionType.NETTY_HTTP.getValue(), 0, 0, 10), 0);
			AppContext.init();
			this.createSession();
		}
	}

	public static void setUpServiceConnectorAndServer() {
		SystemSuperTest.setUpServiceConnectorAndServer();
		SCMPClnExecuteCasc1Test.setUpServer();
	}

	public static void setUp1CascadedServiceConnectorAndServer() {
		SystemSuperTest.setUp1CascadedServiceConnectorAndServer();
		SCMPClnExecuteCasc1Test.setUpServer();
	}

	public static void setUp2CascadedServiceConnectorAndServer() {
		SystemSuperTest.setUp2CascadedServiceConnectorAndServer();
		SCMPClnExecuteCasc1Test.setUpServer();
	}

	public static void setUpServer() {
		// needs a server with 1 session/connection
		List<ServerDefinition> srvToSC0CascDefs = new ArrayList<ServerDefinition>();
		ServerDefinition srvToSC0CascDef = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_SESSION, TestConstants.logbackSrv, TestConstants.sesServerName1,
				TestConstants.PORT_SES_SRV_TCP, TestConstants.PORT_SC0_TCP, 3, 2, TestConstants.sesServiceName1);
		srvToSC0CascDefs.add(srvToSC0CascDef);
		SystemSuperTest.srvDefs = srvToSC0CascDefs;
	}

	@Override
	@After
	public void afterOneTest() throws Exception {
		this.deleteSession();
		this.sessionId = null;
		try {
			this.requester.destroy();
		} catch (Exception e) {
		}
		this.requester = null;
		super.afterOneTest();
	}

	/**
	 * Description: execute - compressed message of type string is exchanged<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_StringMessageCompressed() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.echoCmd);
		clnExecuteCall.setRequestBody(TestConstants.stringLength257);
		clnExecuteCall.setCompressed(true);
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 3000);
		SCMPMessage scmpReply = cbk.getMessageSync(3000);
		Assert.assertEquals(TestConstants.stringLength257, scmpReply.getBody());
		Assert.assertEquals(SCMPBodyType.TEXT.getValue(), scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
	}

	/**
	 * Description: execute - compressed message of type byte is exchanged<br>
	 * Expectation: passes
	 */
	@Test
	public void t02_ByteMessageCompressed() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.echoCmd);
		String largeString = TestUtil.getLargeString();
		clnExecuteCall.setRequestBody(largeString.getBytes());
		clnExecuteCall.setCompressed(true);
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 3000);
		SCMPMessage scmpReply = cbk.getMessageSync(3000);
		Assert.assertEquals(largeString, new String((byte[]) scmpReply.getBody()));
	}

	/**
	 * Description: execute - message of type stream is always exchanged uncompressed, ignores compression<br>
	 * Expectation: passes
	 */
	@Test
	public void t10_StreamMessage() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.echoCmd);
		String largeString = TestUtil.getLargeString();
		ByteArrayInputStream in = new ByteArrayInputStream(largeString.getBytes());
		clnExecuteCall.setRequestBody(in);
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 3000000);
		SCMPMessage scmpReply = cbk.getMessageSync(300000);
		Assert.assertEquals(new String(largeString.getBytes()), new String((byte[]) scmpReply.getBody()));
	}

	/**
	 * Description: execute 100 times - message received by callback<br>
	 * Expectation: passes
	 */
	@Test
	public void t15_100AsynchronousMessages() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.echoCmd);
		clnExecuteCall.setRequestBody(TestConstants.pangram);
		ExecuteCallback callback = new ExecuteCallback();

		for (int i = 0; i < 100; i++) {
			callback.messageReceived = false;
			clnExecuteCall.invoke(callback, 3000);
			while (callback.messageReceived == false) {
				;
			}
			Assert.assertEquals(TestConstants.pangram, callback.reply.getBody());
			Assert.assertEquals(SCMPBodyType.TEXT.getValue(), callback.reply.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
			Assert.assertEquals(SCMPMsgType.CLN_EXECUTE.getValue(), callback.reply.getMessageType());
		}
	}

	/**
	 * Description: execute 100 times large messages - message received by callback<br>
	 * Expectation: passes
	 */
	@Test
	public void t16_100LargeMessagesAsynchronous() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.echoCmd);
		String largeString = TestUtil.getLargeString();
		clnExecuteCall.setRequestBody(largeString);
		ExecuteCallback callback = new ExecuteCallback();

		for (int i = 0; i < 100; i++) {
			callback.messageReceived = false;
			clnExecuteCall.invoke(callback, 3000);
			while (callback.messageReceived == false) {
				;
			}
			Assert.assertEquals(largeString, callback.reply.getBody());
			Assert.assertEquals(SCMPBodyType.TEXT.getValue(), callback.reply.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
			Assert.assertEquals(SCMPMsgType.CLN_EXECUTE.getValue(), callback.reply.getMessageType());
		}
	}

	/**
	 * Description: execute - exception on server - hand over to client<br>
	 * Expectation: passes
	 */
	@Test
	public void t20_EXCOnServer() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.raiseExceptionCmd);
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 3000);
		SCMPMessage scmpReply = cbk.getMessageSync(3000);
		Assert.assertTrue(scmpReply.isFault());
		Assert.assertEquals(SCMPError.SERVER_ERROR.getErrorCode(), scmpReply.getHeaderInt(SCMPHeaderAttributeKey.SC_ERROR_CODE).intValue());
	}

	/**
	 * Description: execute - application error code and application error text returned by server<br>
	 * Expectation: passes
	 */
	@Test
	public void t30_AppErrorCodeText() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.echoAppErrorCmd);
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 3000);
		SCMPMessage scmpReply = cbk.getMessageSync(3000);
		TestUtil.checkReply(scmpReply);
		Assert.assertEquals(TestConstants.appErrorCode + "", scmpReply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_CODE));
		Assert.assertEquals(TestConstants.appErrorText, scmpReply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
	}

	/**
	 * Description: execute small request - large response both uncompressed<br>
	 * Expectation: passes
	 */
	@Test
	public void t40_SmallRequestLargeResponseUncompressed() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.largeResponseCmd);
		clnExecuteCall.setRequestBody("test body");
		clnExecuteCall.setCompressed(false);
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 3000);
		SCMPMessage scmpReply = cbk.getMessageSync(3000);

		String expectedResponse = TestUtil.getLargeString();
		Assert.assertEquals(expectedResponse.length() + "", scmpReply.getBodyLength() + "");
		Assert.assertEquals(expectedResponse, scmpReply.getBody());
		Assert.assertEquals(SCMPBodyType.TEXT.getValue(), scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertEquals(SCMPMsgType.CLN_EXECUTE.getValue(), scmpReply.getMessageType());
		String serviceName = clnExecuteCall.getRequest().getServiceName();
		String sessionId = clnExecuteCall.getRequest().getSessionId();
		Assert.assertEquals(serviceName, scmpReply.getServiceName());
		Assert.assertEquals(sessionId, scmpReply.getSessionId());
	}

	/**
	 * Description: execute large request - small response both uncompressed<br>
	 * Expectation: passes
	 */
	@Test
	public void t41_LargeRequestSmallResponseUncompressed() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		String largeString = TestUtil.getLargeString();
		clnExecuteCall.setRequestBody(largeString);
		clnExecuteCall.setCompressed(false);
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 3000);
		SCMPMessage scmpReply = cbk.getMessageSync(3000);
		Assert.assertNotNull(scmpReply.getSessionId());
		Assert.assertEquals(0, scmpReply.getBodyLength());
		Assert.assertEquals(null, scmpReply.getBody());
	}

	/**
	 * Description: execute large request - large response both uncompressed<br>
	 * Expectation: passes
	 */
	@Test
	public void t42_LargeRequestLargeResponseUncompressed() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		String largeString = TestUtil.getLargeString();
		clnExecuteCall.setMessageInfo(TestConstants.largeResponseCmd);
		clnExecuteCall.setRequestBody(largeString);
		clnExecuteCall.setCompressed(false);
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 3000);
		SCMPMessage scmpReply = cbk.getMessageSync(3000);
		Assert.assertEquals(largeString.length() + "", scmpReply.getBodyLength() + "");
		Assert.assertEquals(largeString, scmpReply.getBody());
		Assert.assertEquals(SCMPBodyType.TEXT.getValue(), scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertEquals(SCMPMsgType.CLN_EXECUTE.getValue(), scmpReply.getMessageType());
		String serviceName = clnExecuteCall.getRequest().getServiceName();
		String sessionId = clnExecuteCall.getRequest().getSessionId();
		Assert.assertEquals(serviceName, scmpReply.getServiceName());
		Assert.assertEquals(sessionId, scmpReply.getSessionId());
	}

	/**
	 * Description: execute small request - large response both compressed<br>
	 * Expectation: passes
	 */
	@Test
	public void t50_SmallRequestLargeResponseCompressed() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.largeResponseCmd);
		clnExecuteCall.setRequestBody("test body");
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 3000);
		SCMPMessage scmpReply = cbk.getMessageSync(3000);

		String expectedResponse = TestUtil.getLargeString();
		Assert.assertEquals(expectedResponse.length(), scmpReply.getBodyLength());
		Assert.assertEquals(expectedResponse, scmpReply.getBody());
		Assert.assertEquals(SCMPBodyType.TEXT.getValue(), scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertEquals(SCMPMsgType.CLN_EXECUTE.getValue(), scmpReply.getMessageType());
		String serviceName = clnExecuteCall.getRequest().getServiceName();
		String sessionId = clnExecuteCall.getRequest().getSessionId();
		Assert.assertEquals(serviceName, scmpReply.getServiceName());
		Assert.assertEquals(sessionId, scmpReply.getSessionId());
	}

	/**
	 * Description: execute small request - larger response both compressed<br>
	 * Expectation: passes
	 */
	@Test
	public void t51_SmallRequest10MBLargeResponseCompressed() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.largeResponse10MBCmd);
		clnExecuteCall.setRequestBody("test body");
		clnExecuteCall.setCompressed(true);
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 300000);
		SCMPMessage scmpReply = cbk.getMessageSync(300000);

		String expectedResponse = TestUtil.get10MBString();
		Assert.assertEquals("unequal length", expectedResponse.length(), scmpReply.getBodyLength());
		Assert.assertEquals(expectedResponse, scmpReply.getBody());
		Assert.assertEquals(SCMPBodyType.TEXT.getValue(), scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertEquals(SCMPMsgType.CLN_EXECUTE.getValue(), scmpReply.getMessageType());
		String serviceName = clnExecuteCall.getRequest().getServiceName();
		String sessionId = clnExecuteCall.getRequest().getSessionId();
		Assert.assertEquals(serviceName, scmpReply.getServiceName());
		Assert.assertEquals(sessionId, scmpReply.getSessionId());
	}

	/**
	 * Description: execute large request - small response both compressed<br>
	 * Expectation: passes
	 */
	@Test
	public void t52_LargeRequestSmallResponseCompressed() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		String largeString = TestUtil.getLargeString();
		clnExecuteCall.setRequestBody(largeString);
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 3000);
		SCMPMessage scmpReply = cbk.getMessageSync(3000);
		Assert.assertNotNull(scmpReply.getSessionId());
		Assert.assertEquals(0, scmpReply.getBodyLength());
		Assert.assertEquals(null, scmpReply.getBody());
	}

	/**
	 * Description: execute large request - large response both compressed<br>
	 * Expectation: passes
	 */
	@Test
	public void t53_LargeRequestLargeResponseCompressed() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		String largeString = TestUtil.getLargeString();
		clnExecuteCall.setMessageInfo(TestConstants.largeResponseCmd);
		clnExecuteCall.setRequestBody(largeString);
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 3000);
		SCMPMessage scmpReply = cbk.getMessageSync(3000);
		Assert.assertEquals(largeString.length() + "", scmpReply.getBodyLength() + "");
		Assert.assertEquals(largeString, scmpReply.getBody());
		Assert.assertEquals(SCMPBodyType.TEXT.getValue(), scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_TYPE));
		Assert.assertEquals(SCMPMsgType.CLN_EXECUTE.getValue(), scmpReply.getMessageType());
		String serviceName = clnExecuteCall.getRequest().getServiceName();
		String sessionId = clnExecuteCall.getRequest().getSessionId();
		Assert.assertEquals(serviceName, scmpReply.getServiceName());
		Assert.assertEquals(sessionId, scmpReply.getSessionId());
	}

	/**
	 * Description: execute - waits 2 seconds - another execute times out when waiting for free connection<br>
	 * Expectation: passes
	 */
	@Test
	public void t70_WaitsForConnectionTimeout() throws Exception {

		// reserve connection 1 with standard session
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.sleepCmd);
		clnExecuteCall.setRequestBody("3000");
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 10000);

		// create another session2
		SCMPClnCreateSessionCall createSessionCall = new SCMPClnCreateSessionCall(this.requester, TestConstants.sesServerName1);
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoIntervalSeconds(3600);
		TestCallback cbk2 = new TestCallback();
		createSessionCall.invoke(cbk2, 3000);
		SCMPMessage resp = cbk2.getMessageSync(3000);
		String sessionId2 = resp.getSessionId();

		// create another session3
		createSessionCall = new SCMPClnCreateSessionCall(this.requester, TestConstants.sesServerName1);
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoIntervalSeconds(3600);
		TestCallback cbk3 = new TestCallback();
		createSessionCall.invoke(cbk3, 3000);
		resp = cbk3.getMessageSync(3000);
		String sessionId3 = resp.getSessionId();

		// reserve connection 2 with session2
		clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, sessionId3);
		clnExecuteCall.setMessageInfo(TestConstants.sleepCmd);
		clnExecuteCall.setRequestBody("3000");
		TestCallback cbk4 = new TestCallback();
		clnExecuteCall.invoke(cbk4, 10000);

		// to assure second create is not faster
		Thread.sleep(20);
		// try to get a third connection with a third session - should not work
		clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, sessionId2);
		TestCallback cbk5 = new TestCallback();
		clnExecuteCall.invoke(cbk5, 2000);

		SCMPMessage reply = cbk4.getMessageSync(4000);
		SCMPMessage reply1 = cbk5.getMessageSync(4000);

		TestUtil.checkReply(reply);
		Assert.assertTrue(reply1.isFault());
		TestUtil.verifyError(reply1, SCMPError.NO_FREE_CONNECTION, SCMPMsgType.CLN_EXECUTE);
	}

	/**
	 * Description: execute - waits 2 seconds - another execute times out when waiting for free connection<br>
	 * Expectation: passes
	 */
	@Test
	public void t71_LoopsForFreeConnectionAndWorksFineAfter() throws Exception {
		// reserve connection 1 with standard session
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.sleepCmd);
		clnExecuteCall.setRequestBody("3000");
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 10000);

		// create another session2
		SCMPClnCreateSessionCall createSessionCall = new SCMPClnCreateSessionCall(this.requester, TestConstants.sesServerName1);
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoIntervalSeconds(3600);
		TestCallback cbk2 = new TestCallback();
		createSessionCall.invoke(cbk2, 3000);
		SCMPMessage resp = cbk2.getMessageSync(3000);
		String sessionId2 = resp.getSessionId();

		// create another session3
		createSessionCall = new SCMPClnCreateSessionCall(this.requester, TestConstants.sesServerName1);
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoIntervalSeconds(3600);
		TestCallback cbk3 = new TestCallback();
		createSessionCall.invoke(cbk3, 3000);
		resp = cbk3.getMessageSync(3000);
		String sessionId3 = resp.getSessionId();

		// reserve connection 2 with session2
		clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, sessionId3);
		clnExecuteCall.setMessageInfo(TestConstants.sleepCmd);
		clnExecuteCall.setRequestBody("3000");
		TestCallback cbk4 = new TestCallback();
		clnExecuteCall.invoke(cbk4, 10000);

		// to assure second create is not faster
		Thread.sleep(20);
		// try to get a third connection with a third session - should work with the wait mec for free connections
		clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, sessionId2);
		TestCallback cbk5 = new TestCallback();
		clnExecuteCall.invoke(cbk5, 7000);

		SCMPMessage reply = cbk4.getMessageSync(4000);
		SCMPMessage reply1 = cbk5.getMessageSync(7000);

		TestUtil.checkReply(reply);
		TestUtil.checkReply(reply1);
		Assert.assertEquals(SCMPMsgType.CLN_EXECUTE.getValue(), reply1.getMessageType());
		Assert.assertEquals(SCMPMsgType.CLN_EXECUTE.getValue(), reply.getMessageType());
	}

	/**
	 * Description: execute - waits 2 seconds, OTI runs out on SC<br>
	 * Expectation: passes
	 */
	@Test
	public void t80_OTITimesOut() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.sleepCmd);
		clnExecuteCall.setRequestBody("2000");
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 2000);
		SCMPMessage responseMessage = cbk.getMessageSync(2000);
		TestUtil.verifyError(responseMessage, SCMPError.OPERATION_TIMEOUT, SCMPMsgType.CLN_EXECUTE);
	}

	/**
	 * Description: execute 2 times on same session<br>
	 * Expectation: throws parallel request error
	 */
	@Test
	public void t81_PendingRequestError() throws Exception {

		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.sleepCmd);
		clnExecuteCall.setRequestBody("3000");
		TestCallback cbk2 = new TestCallback();
		clnExecuteCall.invoke(cbk2, 10000);

		// to assure second create is not faster
		Thread.sleep(20);
		clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		TestCallback cbk1 = new TestCallback();
		clnExecuteCall.invoke(cbk1, 2000);

		SCMPMessage reply = cbk2.getMessageSync(4000);
		SCMPMessage reply1 = cbk1.getMessageSync(4000);

		TestUtil.checkReply(reply);
		Assert.assertTrue(reply1.isFault());
		TestUtil.verifyError(reply1, SCMPError.PARALLEL_REQUEST, SCMPMsgType.CLN_EXECUTE);
	}

	/**
	 * create session.
	 *
	 * @throws Exception the exception
	 */
	protected void createSession() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = new SCMPClnCreateSessionCall(this.requester, TestConstants.sesServerName1);
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoIntervalSeconds(3600);
		TestCallback cbk = new TestCallback();
		createSessionCall.invoke(cbk, 3000);
		SCMPMessage resp = cbk.getMessageSync(3000);
		this.sessionId = resp.getSessionId();
	}

	/**
	 * delete session.
	 *
	 * @throws Exception the exception
	 */
	private void deleteSession() throws Exception {
		SCMPClnDeleteSessionCall deleteSessionCall = new SCMPClnDeleteSessionCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		TestCallback cbk = new TestCallback();
		deleteSessionCall.invoke(cbk, 3000);
		cbk.getMessageSync(3000);
	}

	/**
	 * The Class ExecuteCallback.
	 */
	private class ExecuteCallback implements ISCMPMessageCallback {

		public volatile boolean messageReceived = false;
		public SCMPMessage reply;

		@Override
		public void receive(SCMPMessage scmpReply) throws Exception {
			this.reply = scmpReply;
			this.messageReceived = true;
		}

		@Override
		public void receive(Exception ex) {
			this.messageReceived = true;
		}
	}
}
