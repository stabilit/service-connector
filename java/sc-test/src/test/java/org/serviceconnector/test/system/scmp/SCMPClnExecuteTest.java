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

import java.io.ByteArrayInputStream;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestCallback;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnCreateSessionCall;
import org.serviceconnector.call.SCMPClnDeleteSessionCall;
import org.serviceconnector.call.SCMPClnExecuteCall;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.RequesterContext;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPBodyType;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.system.SystemSuperTest;

/**
 * @author JTraber
 */
public class SCMPClnExecuteTest extends SystemSuperTest {

	private ProcessCtx sesSrvCtx;
	private SCRequester requester;
	private String sessionId;

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		sesSrvCtx = ctrl.startServer(TestConstants.COMMUNICATOR_TYPE_SESSION, TestConstants.log4jSrvProperties, TestConstants.sesServerName1,
				TestConstants.PORT_SES_SRV_TCP, TestConstants.PORT_SC_TCP, 1, 1, TestConstants.sesServiceName1);
		this.requester = new SCRequester(new RequesterContext(TestConstants.HOST, TestConstants.PORT_SC_HTTP, ConnectionType.NETTY_HTTP
				.getValue(), 0));
		AppContext.init();
		this.createSession();
	}

	@After
	public void afterOneTest() throws Exception {
		this.deleteSession();
		this.sessionId = null;
		try {
			this.requester.destroy();
		} catch (Exception e) {
		}
		this.requester = null;
		try {
			ctrl.stopServer(sesSrvCtx);
		} catch (Exception e) {
		}
		sesSrvCtx = null;
		super.afterOneTest();
	}

	/**
	 * Description: execute - compressed message of type string is exchanged<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_StringMessageCompressed() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(this.requester,
				TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.echoCmd);
		clnExecuteCall.setRequestBody(TestConstants.stringLength257);
		clnExecuteCall.setCompressed(true);
		TestCallback cbk = new TestCallback(false);
		clnExecuteCall.invoke(cbk, 1000);
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
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(this.requester,
				TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.echoCmd);
		String largeString = TestUtil.getLargeString();
		clnExecuteCall.setRequestBody(largeString.getBytes());
		clnExecuteCall.setCompressed(true);
		TestCallback cbk = new TestCallback(false);
		clnExecuteCall.invoke(cbk, 1000);
		SCMPMessage scmpReply = cbk.getMessageSync(3000);
		Assert.assertEquals(largeString, new String((byte[]) scmpReply.getBody()));
	}

	/**
	 * Description: execute - message of type stream is always exchanged uncompressed, ignores compression<br>
	 * Expectation: passes
	 */
	@Test
	public void t10_StreamMessage() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(this.requester,
				TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.echoCmd);
		String largeString = TestUtil.getLargeString();
		ByteArrayInputStream in = new ByteArrayInputStream(largeString.getBytes());
		clnExecuteCall.setRequestBody(in);
		TestCallback cbk = new TestCallback(false);
		clnExecuteCall.invoke(cbk, 1000);
		SCMPMessage scmpReply = cbk.getMessageSync(3000);
		Assert.assertEquals(new String(largeString.getBytes()), new String((byte[]) scmpReply.getBody()));
	}

	/**
	 * Description: execute 100 times - message received by callback<br>
	 * Expectation: passes
	 */
	@Test
	public void t15_100AsynchronousMessages() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(this.requester,
				TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.echoCmd);
		clnExecuteCall.setRequestBody(TestConstants.pangram);
		ExecuteCallback callback = new ExecuteCallback();

		for (int i = 0; i < 100; i++) {
			callback.messageReceived = false;
			clnExecuteCall.invoke(callback, 1000);
			while (callback.messageReceived == false)
				;
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
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(this.requester,
				TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.echoCmd);
		String largeString = TestUtil.getLargeString();
		clnExecuteCall.setRequestBody(largeString);
		ExecuteCallback callback = new ExecuteCallback();

		for (int i = 0; i < 100; i++) {
			callback.messageReceived = false;
			clnExecuteCall.invoke(callback, 1000);
			while (callback.messageReceived == false)
				;
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
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(this.requester,
				TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.raiseExceptionCmd);
		TestCallback cbk = new TestCallback(false);
		clnExecuteCall.invoke(cbk, 1000);
		SCMPMessage scmpReply = cbk.getMessageSync(3000);
		Assert.assertTrue(scmpReply.isFault());
		Assert.assertEquals(SCMPError.SERVER_ERROR.getErrorCode(), scmpReply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
	}

	/**
	 * Description: execute - application error code and application error text returned by server<br>
	 * Expectation: passes
	 */
	@Test
	public void t30_AppErrorCodeText() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(this.requester,
				TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.echoAppErrorCmd);
		TestCallback cbk = new TestCallback(false);
		clnExecuteCall.invoke(cbk, 1000);
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
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(this.requester,
				TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.largeResponseCmd);
		clnExecuteCall.setRequestBody("test body");
		clnExecuteCall.setCompressed(false);
		TestCallback cbk = new TestCallback(false);
		clnExecuteCall.invoke(cbk, 1000);
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
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(this.requester,
				TestConstants.sesServerName1, this.sessionId);
		String largeString = TestUtil.getLargeString();
		clnExecuteCall.setRequestBody(largeString);
		clnExecuteCall.setCompressed(false);
		TestCallback cbk = new TestCallback(false);
		clnExecuteCall.invoke(cbk, 1000);
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
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(this.requester,
				TestConstants.sesServerName1, this.sessionId);
		String largeString = TestUtil.getLargeString();
		clnExecuteCall.setMessageInfo(TestConstants.largeResponseCmd);
		clnExecuteCall.setRequestBody(largeString);
		clnExecuteCall.setCompressed(false);
		TestCallback cbk = new TestCallback(false);
		clnExecuteCall.invoke(cbk, 1000);
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
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(this.requester,
				TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.largeResponseCmd);
		clnExecuteCall.setRequestBody("test body");
		TestCallback cbk = new TestCallback(false);
		clnExecuteCall.invoke(cbk, 1000);
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
	 * Description: execute large request - small response both compressed<br>
	 * Expectation: passes
	 */
	@Test
	public void t51_LargeRequestSmallResponseCompressed() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(this.requester,
				TestConstants.sesServerName1, this.sessionId);
		String largeString = TestUtil.getLargeString();
		clnExecuteCall.setRequestBody(largeString);
		TestCallback cbk = new TestCallback(false);
		clnExecuteCall.invoke(cbk, 1000);
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
	public void t52_LargeRequestLargeResponseCompressed() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(this.requester,
				TestConstants.sesServerName1, this.sessionId);
		String largeString = TestUtil.getLargeString();
		clnExecuteCall.setMessageInfo(TestConstants.largeResponseCmd);
		clnExecuteCall.setRequestBody(largeString);
		TestCallback cbk = new TestCallback(false);
		clnExecuteCall.invoke(cbk, 1000);
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
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(this.requester,
				TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.sleepCmd);
		clnExecuteCall.setRequestBody("2000");
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 10000);

		// to assure second create is not faster
		Thread.sleep(20);
		clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(this.requester,
				TestConstants.sesServerName1, this.sessionId);
		TestCallback cbk1 = new TestCallback();
		clnExecuteCall.invoke(cbk1, 1000);

		SCMPMessage reply = cbk.getMessageSync(3000);
		SCMPMessage reply1 = cbk1.getMessageSync(3000);

		TestUtil.checkReply(reply);
		Assert.assertTrue(reply1.isFault());
		TestUtil.verifyError(reply1, SCMPError.NO_FREE_CONNECTION, SCMPMsgType.CLN_EXECUTE);
	}

	/**
	 * Description: execute - waits 2 seconds, OTI runs out on SC<br>
	 * Expectation: passes
	 */
	@Test
	public void t80_OTITimesOut() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(this.requester,
				TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.sleepCmd);
		clnExecuteCall.setRequestBody("2000");
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 1000);
		SCMPMessage responseMessage = cbk.getMessageSync(2000);
		TestUtil.verifyError(responseMessage, SCMPError.OPERATION_TIMEOUT_EXPIRED, SCMPMsgType.CLN_EXECUTE);
	}

	/**
	 * create session.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private void createSession() throws Exception {
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL.newInstance(
				this.requester, TestConstants.sesServerName1);
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoIntervalSeconds(3600);
		TestCallback cbk = new TestCallback();
		createSessionCall.invoke(cbk, 1000);
		SCMPMessage resp = cbk.getMessageSync(3000);
		this.sessionId = resp.getSessionId();
	}

	/**
	 * delete session.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private void deleteSession() throws Exception {
		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL.newInstance(
				this.requester, TestConstants.sesServerName1, this.sessionId);
		TestCallback cbk = new TestCallback();
		deleteSessionCall.invoke(cbk, 1000);
		cbk.getMessageSync(3000);
	}

	/**
	 * The Class ExecuteCallback.
	 */
	private class ExecuteCallback implements ISCMPMessageCallback {

		public boolean messageReceived = false;
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