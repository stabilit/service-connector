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
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.system.SystemSuperTest;

/**
 * @author JTraber
 */
public class SCMPClnExecuteCacheCasc1Test extends SystemSuperTest {

	protected SCRequester requester;
	protected String sessionId;

	public SCMPClnExecuteCacheCasc1Test() {
		SCMPClnExecuteCacheCasc1Test.setUp1CascadedServiceConnectorAndServer();
	}

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		if (cascadingLevel == 1) {
			this.requester = new SCRequester(new RemoteNodeConfiguration(TestConstants.RemoteNodeName, TestConstants.HOST,
					TestConstants.PORT_SC1_HTTP, ConnectionType.NETTY_HTTP.getValue(), 0, 0, 10), 0);
			AppContext.init();
			this.createSession();
		}
	}

	public static void setUpServiceConnectorAndServer() {
		SystemSuperTest.setUpServiceConnectorAndServer();
		SCMPClnExecuteCacheCasc1Test.setUpServer();
	}

	public static void setUp1CascadedServiceConnectorAndServer() {
		SystemSuperTest.setUp1CascadedServiceConnectorAndServer();
		SCMPClnExecuteCacheCasc1Test.setUpServer();
	}

	public static void setUp2CascadedServiceConnectorAndServer() {
		SystemSuperTest.setUp2CascadedServiceConnectorAndServer();
		SCMPClnExecuteCacheCasc1Test.setUpServer();
	}

	public static void setUpServer() {
		// needs a server with 1 session/connection
		List<ServerDefinition> srvToSC0CascDefs = new ArrayList<ServerDefinition>();
		ServerDefinition srvToSC0CascDef = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_SESSION,
				TestConstants.log4jSrvProperties, TestConstants.sesServerName1, TestConstants.PORT_SES_SRV_TCP,
				TestConstants.PORT_SC0_TCP, 3, 2, TestConstants.sesServiceName1);
		srvToSC0CascDefs.add(srvToSC0CascDef);
		SystemSuperTest.srvDefs = srvToSC0CascDefs;
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
		super.afterOneTest();
	}

	/**
	 * Description: execute - uncompressed message of type string is exchanged and cached<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_StringMessageUnCompressed() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.cacheCmd);
		clnExecuteCall.setRequestBody(TestConstants.stringLength257);
		clnExecuteCall.setCompressed(false);
		clnExecuteCall.setCacheId("700");
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 3000);
		cbk.getMessageSync(3000);
		this.checkCacheContent("700", TestConstants.stringLength257);
	}

	/**
	 * Description: execute - exception on server - hand over to client<br>
	 * Expectation: passes
	 */
	@Test
	public void t20_EXCOnServer() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.raiseExceptionCmd);
		clnExecuteCall.setCacheId("700");
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 3000);
		cbk.getMessageSync(3000);
		this.checkCacheContent("700", "checkCacheContent");
	}

	/**
	 * Description: execute small request - large response both uncompressed, cached<br>
	 * Expectation: passes
	 */
	@Test
	public void t40_SmallRequestLargeResponseUncompressed() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.cacheCmd);
		clnExecuteCall.setCacheId("700");
		clnExecuteCall.setRequestBody("cacheLargeMessageFor1Hour");
		clnExecuteCall.setCompressed(false);
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 3000);
		cbk.getMessageSync(3000);
		this.checkCacheContent("700", TestUtil.getLargeString());
	}

	/**
	 * Description: execute large request - small response both uncompressed, cached<br>
	 * Expectation: passes
	 */
	@Test
	public void t41_LargeRequestSmallResponseUncompressed() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		String largeString = TestUtil.getLargeString();
		clnExecuteCall.setRequestBody(largeString);
		clnExecuteCall.setMessageInfo(TestConstants.cacheCmd);
		clnExecuteCall.setCacheId("999");
		clnExecuteCall.setCompressed(false);
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 3000);
		cbk.getMessageSync(3000);
		this.checkCacheContent("999", "large request small response - 999 is a key for that!");
	}

	/**
	 * Description: execute large request - large response both uncompressed, cached<br>
	 * Expectation: passes
	 */
	@Test
	public void t42_LargeRequestLargeResponseUncompressed() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		String largeString = TestUtil.getLargeString();
		clnExecuteCall.setMessageInfo(TestConstants.cacheCmd);
		clnExecuteCall.setCacheId("666");
		clnExecuteCall.setRequestBody(largeString);
		clnExecuteCall.setCompressed(false);
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 3000);
		cbk.getMessageSync(3000);
		this.checkCacheContent("666", largeString);
	}

	/**
	 * Description: execute small request - 10MB response both uncompressed, cached<br>
	 * Expectation: passes
	 */
	@Test
	public void t43_SmallRequest10MBResponseUncompressed() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.cacheCmd);
		clnExecuteCall.setCacheId("700");
		clnExecuteCall.setRequestBody("cache10MBStringFor1Hour");
		clnExecuteCall.setCompressed(false);
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 30000);
		cbk.getMessageSync(30000);
		this.checkCacheContent("700", TestUtil.get10MBString());
	}

	/**
	 * Description: execute small request - no cache id set by server, no caching of message<br>
	 * Expectation: passes
	 */
	@Test
	public void t45_NoCacheIdSetByServer() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.cacheCmd);
		clnExecuteCall.setCacheId("700");
		clnExecuteCall.setRequestBody("noCid");
		clnExecuteCall.setCompressed(false);
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 3000);
		cbk.getMessageSync(3000);
		this.checkCacheContent("700", "checkCacheContent");
	}

	/**
	 * Description: execute small request - different cache id set by server, no caching of message<br>
	 * Expectation: passes
	 */
	@Test
	public void t46_DifferentCacheIdSetByServer() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.cacheCmd);
		clnExecuteCall.setCacheId("700");
		clnExecuteCall.setRequestBody("cacheServerReplyOther");
		clnExecuteCall.setCompressed(false);
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 3000);
		cbk.getMessageSync(3000);
		this.checkCacheContent("700", "checkCacheContent");
	}

	/**
	 * Description: execute small request - no cache expiration date set by server, caching of message<br>
	 * Expectation: passes
	 */
	@Test
	public void t47_NOCacheExpirationDateSetByServer() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.cacheCmd);
		clnExecuteCall.setCacheId("700");
		clnExecuteCall.setRequestBody("cidNoCed");
		clnExecuteCall.setCompressed(false);
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 3000);
		cbk.getMessageSync(3000);
		this.checkCacheContent("700", "cidNoCed");
	}

	/**
	 * Description: execute small request - cache for 2 seconds wait until expiration than try to load<br>
	 * Expectation: passes
	 */
	@Test
	public void t48_TryLoadingExpiredMessage() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.cacheCmd);
		clnExecuteCall.setCacheId("700");
		clnExecuteCall.setRequestBody("cacheFor2Sec");
		clnExecuteCall.setCompressed(false);
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 3000);
		cbk.getMessageSync(3000);
		Thread.sleep(2000);
		this.checkCacheContent("700", "checkCacheContent");
	}

	/**
	 * Description: execute small request - cache for 2 seconds wait until expiration than try to load, load cachid again for 1 hour<br>
	 * Expectation: passes
	 */
	@Test
	public void t49_TryLoadingExpiredMessageLoadAgain() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.cacheCmd);
		clnExecuteCall.setCacheId("700");
		clnExecuteCall.setRequestBody("cacheFor2Sec");
		clnExecuteCall.setCompressed(false);
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 3000);
		cbk.getMessageSync(3000);
		Thread.sleep(3000);
		this.checkCacheContent("700", "checkCacheContent");
		clnExecuteCall.setRequestBody("cacheFor1Hour");
		clnExecuteCall.invoke(cbk, 3000);
		cbk.getMessageSync(3000);
		this.checkCacheContent("700", "cacheFor1Hour");
	}

	/**
	 * Description: execute - two clients try to load same cache, second gets cache loading error<br>
	 * Expectation: passes
	 */
	@Test
	public void t70_2ClientsLoadingCacheError() throws Exception {

		// reserve connection 1 with standard session
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setMessageInfo(TestConstants.sleepCmd);
		clnExecuteCall.setRequestBody("3000");
		clnExecuteCall.setCacheId("700");
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

		// reserve connection 2 with session2
		clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, sessionId2);
		clnExecuteCall.setMessageInfo(TestConstants.sleepCmd);
		clnExecuteCall.setRequestBody("3000");
		clnExecuteCall.setCacheId("700");
		TestCallback cbk4 = new TestCallback();
		clnExecuteCall.invoke(cbk4, 10000);
		SCMPMessage reply = cbk4.getMessageSync(4000);
		TestUtil.verifyError(reply, SCMPError.CACHE_LOADING, SCMPMsgType.CLN_EXECUTE);
		TestUtil.checkReply(cbk.getMessageSync(4000));
	}

	/**
	 * Description: execute - waits 2 seconds, OTI runs out on SC<br>
	 * Expectation: passes
	 */
	@Test
	public void t80_OTITimesOut() throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setRequestBody("cacheTimeoutReply");
		clnExecuteCall.setMessageInfo(TestConstants.cacheCmd);
		clnExecuteCall.setCacheId("700");
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 1000);
		SCMPMessage responseMessage = cbk.getMessageSync(1000);
		TestUtil.verifyError(responseMessage, SCMPError.OPERATION_TIMEOUT, SCMPMsgType.CLN_EXECUTE);
		this.checkCacheContent("700", "checkCacheContent");
	}

	/**
	 * create session.
	 * 
	 * @throws Exception
	 *             the exception
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
	 * @throws Exception
	 *             the exception
	 */
	private void deleteSession() throws Exception {
		SCMPClnDeleteSessionCall deleteSessionCall = new SCMPClnDeleteSessionCall(this.requester, TestConstants.sesServerName1,
				this.sessionId);
		TestCallback cbk = new TestCallback();
		deleteSessionCall.invoke(cbk, 3000);
		cbk.getMessageSync(3000);
	}

	private void checkCacheContent(String cacheId, Object body) throws Exception {
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, TestConstants.sesServerName1, this.sessionId);
		clnExecuteCall.setCacheId(cacheId);
		clnExecuteCall.setMessageInfo(TestConstants.echoCmd);
		clnExecuteCall.setRequestBody("checkCacheContent");
		TestCallback cbk = new TestCallback();
		clnExecuteCall.invoke(cbk, 30000);
		SCMPMessage scmpReply = cbk.getMessageSync(30000);

		String expected = "expected";
		String received = "received";
		if (body instanceof String) {
			expected = (String) body;
			received = (String) scmpReply.getBody();
		}

		if (expected.equals(received) == false) {
			throw new Exception("unexpected body received: expected=" + expected + " received=" + received);
		}
	}
}