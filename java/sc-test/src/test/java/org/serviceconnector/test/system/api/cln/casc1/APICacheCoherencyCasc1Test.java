/*
 * Copyright © 2010 STABILIT Informatik AG, Switzerland *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License"); *
 * you may not use this file except in compliance with the License. *
 * You may obtain a copy of the License at *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0 *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS, *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and *
 * limitations under the License. *
 */
package org.serviceconnector.test.system.api.cln.casc1;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.api.SCAppendMessage;
import org.serviceconnector.api.SCManagedMessage;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.cache.SC_CACHE_ENTRY_STATE;
import org.serviceconnector.cache.SC_CACHING_METHOD;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.test.system.api.APISystemSuperCCTest;

public class APICacheCoherencyCasc1Test extends APISystemSuperCCTest {

	public APICacheCoherencyCasc1Test() {
		APICacheCoherencyCasc1Test.setUp1CascadedServiceConnectorAndServer();
	}

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		this.setUpClientToSC();
	}

	/**
	 * Description
	 * 1: load 10MB to cache (cid=700)<br>
	 * 2: start cache guardian - publish 3 appendix<br>
	 * 3: verify callback retrieval
	 * Expectation: passes
	 */
	@Test
	public void t01_cc_receive3Appendix() throws Exception {
		// 1: load 10MB to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setData("cacheFor1Hour_managedData");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 2: start cache guardian - publish 3 Appendix
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish3AppendixMsgCmd);
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);
		// 3: verify callback retrieval - 3 appendix within 10sec
		cacheGuardianCbk.waitForMessage(10, 3);
	}

	/**
	 * Description: read managed data from cache, 3 appendices<br>
	 * Expectation: passes
	 */
	@Test
	public void t02_cc_readManagedData() throws Exception {
		// 1: load 10MB to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("cacheFor1Hour_managedData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// start cache guardian
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish3AppendixMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);
		// assure 3 messages arrive within 10 seconds!
		cacheGuardianCbk.waitForMessage(10, 3);

		// read managed data with session client from cache
		SCMessage response = sessionService1.execute(request);
		this.checkAppendices(response, 3);
		// verify that message is in cache on sc1 and not only in sc0
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "3",
				"700/0|0=0&700/1|0=0&700/2|0=0&700/3|0=0&", TestConstants.cacheGuardian1);
	}

	/**
	 * Description: large initial, read managed data from cache, 3 appendices<br>
	 * Expectation: passes
	 */
	@Test
	public void t03_cc_readManagedData10MBInitial() throws Exception {
		// 1: load 10MB to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("cache10MBStringFor1Hour_managedData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// start cache guardian
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish3AppendixMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);
		// assure 3 messages arrive within 10 seconds!
		cacheGuardianCbk.waitForMessage(10, 3);

		// read managed data with session client from cache
		SCMessage response = sessionService1.execute(request);
		this.checkAppendices(response, 3);
		// verify that message is in cache on sc1 and not only in sc0
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "3",
				"700/0|0=51&700/1|0=0&700/2|0=0&700/3|0=0&", TestConstants.cacheGuardian1);
	}

	/**
	 * Description: large initial, read managed data from cache, 3 large appendices<br>
	 * Expectation: passes
	 */
	@Test
	public void t04_cc_readManagedData10MBInitialLargeAppendix() throws Exception {
		// 1: load 10MB to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("cache10MBStringFor1Hour_managedData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// start cache guardian
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish3LargeAppendixMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);
		// assure 3 messages arrive within 10 seconds!
		cacheGuardianCbk.waitForMessage(10, 3);

		// read managed data with session client from cache
		SCMessage response = sessionService1.execute(request);
		this.checkAppendices(response, 3);
		// verify that message is in cache on sc1 and not only in sc0
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "3",
				"700/0|0=51&700/1|0=1&700/2|0=1&700/3|0=1&", TestConstants.cacheGuardian1);
	}

	/**
	 * Description: large initial, read managed data from cache, 1 10BM appendices<br>
	 * Expectation: passes
	 */
	@Test
	public void t05_cc_readManagedData10MBInitial10MBAppendix() throws Exception {
		// 1: load 10MB to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("cache10MBStringFor1Hour_managedData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// start cache guardian
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish10MBAppendixMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);
		// assure 3 messages arrive within 10 seconds!
		cacheGuardianCbk.waitForMessage(100, 1);

		// read managed data with session client from cache
		SCMessage response = sessionService1.execute(request);
		this.checkAppendices(response, 1);
		// verify that message is in cache on sc1 and not only in sc0
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "1", "700/0|0=51&700/1|0=51&",
				TestConstants.cacheGuardian1);
	}

	/**
	 * Description: cache initial message, 3 appendix, stop cache guardian, check cache state is empty<br>
	 * Expectation: passes
	 */
	@Test
	public void t06_cc_InitialMsg3AppendixStopCacheGuardian() throws Exception {
		// 1: load 10MB to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setData("cacheFor1Hour_managedData");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// start cache guardian
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish3LargeAppendixMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);
		guardianClient.stopCacheGuardian();

		// verify that message is not in cascaded SC cache anymore
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "notfound", SC_CACHE_ENTRY_STATE.UNDEFINDED, "", "", "", "");

		// read managed data with session client from cache
		request.setMessageInfo(TestConstants.echoCmd);
		request.setData("test");
		request.setCacheId("700");
		SCMessage response = sessionService1.execute(request);
		Assert.assertEquals("test", response.getData());
	}

	/**
	 * Description: cache initial message, stop cache guardian, check cache state is empty<br>
	 * Expectation: passes
	 */
	@Test
	public void t07_cc_InitialMsgStopCacheGuardian() throws Exception {
		// gets message cached (cacheid=700)
		SCMessage request = new SCMessage();
		request.setData("cacheFor1Hour_managedData");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// start cache guardian
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);
		guardianClient.stopCacheGuardian();

		// verify that message is not in cache anymore
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "notfound", SC_CACHE_ENTRY_STATE.UNDEFINDED, "", "", "", "");
	}

	@Test
	public void t20_cc_RetrievingAppendixDuringLoadOfInitialMsg() throws Exception {
		// start cache guardian publish every second a message
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publishMsgWithDelayCmd);
		subMsg.setData("100003|1000|700");
		subMsg.setCachingMethod(SC_CACHING_METHOD.APPEND);
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 1: load 10MB to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("cache10MBStringFor1Hour_managedData");
		request.setMessageInfo(TestConstants.cacheCmd);

		SCMessage response = sessionService1.execute(request);
		String expectedResponse = TestUtil.get10MBString();
		Assert.assertEquals("unequal length", expectedResponse.length(), response.getDataLength());
		Assert.assertEquals(expectedResponse, response.getData());

		guardianClient.stopCacheGuardian();
		// assure 1 messages arrived within 5 seconds!
		cacheGuardianCbk.waitForMessage(5, 1);

		// verify that message is not in cascaded SC cache anymore
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "notfound", SC_CACHE_ENTRY_STATE.UNDEFINDED, "", "", "", "");
	}

	@Test
	public void t21_cc_CacheLoadingExceptionLoadingAppendix() throws Exception {
		// 1: load 10MB to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("cache10MBStringFor1Hour_managedData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// verify that message is in cache on cascaded SC
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "0", "700/0|0=51&", "unset");

		// start cache guardian publish 10MB Appendix
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish50MBAppendixMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);
		Thread.sleep(2000);
		try {
			SCMessage response = sessionService1.execute(request);
			String expectedResponse = TestUtil.get10MBString();
			Assert.assertEquals("unequal length", expectedResponse.length(), response.getDataLength());
			Assert.assertEquals(expectedResponse, response.getData());
			Assert.fail("Cache loading exception expected.");
		} catch (SCServiceException e) {
			Assert.assertEquals(SCMPError.CACHE_LOADING.getErrorCode(), e.getSCErrorCode());
		}
		guardianClient.stopCacheGuardian();

		// verify that message is not in cascaded SC cache anymore
		inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "notfound", SC_CACHE_ENTRY_STATE.UNDEFINDED, "", "", "", "");
	}

	/**
	 * Description: managed data to SC0, start client to SC1 and load from cache of SC0<br>
	 * Expectation: passes
	 */
	@Test
	public void t40_cc_ManagedDataToSC0PublishAppendixClientToCascSC() throws Exception {
		// disconnect session client
		this.sessionService1.deleteSession();
		this.sessionClient.detach();
		// connect new client to SC0
		SCClient clientToSc0 = new SCClient(TestConstants.HOST, TestConstants.PORT_SC0_TCP, ConnectionType.NETTY_TCP);
		clientToSc0.attach();
		SCSessionService sessionSrvToSC0 = clientToSc0.newSessionService(TestConstants.sesServiceName1);

		// 1: load 10MB to cache (cid=700) SC0
		SCMessage request = new SCMessage();
		request.setData("cacheFor1Hour_managedData");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionSrvToSC0.createSession(new SCMessage(), new SessionMsgCallback(sessionSrvToSC0));
		sessionSrvToSC0.execute(request);

		// start cache guardian and cache 3 appendix
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish3LargeAppendixMsgCmd);
		subMsg.setData("700");
		clientToSc0.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);
		// assure 3 messages arrive within 10 seconds!
		cacheGuardianCbk.waitForMessage(10, 3);

		// verify that message is in cache on sc0
		Map<String, String> inspectResponse = clientToSc0.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "3",
				"700/0|0=0&700/1|0=1&700/2|0=1&700/3|0=1&", TestConstants.cacheGuardian1);
		// start client to cascaded sc, no appendix will be published
		subMsg.setSessionInfo(TestConstants.doNothingCmd);
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);
		// stop client to sc0- cache SC0 should stay
		clientToSc0.stopCacheGuardian();

		// verify that message is still in cache on sc0
		inspectResponse = clientToSc0.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "3",
				"700/0|0=0&700/1|0=1&700/2|0=1&700/3|0=1&", TestConstants.cacheGuardian1);

		this.sessionClient.attach();
		sessionService1 = sessionClient.newSessionService(TestConstants.sesServiceName1);
		sessionService1.createSession(new SCMessage(), new SessionMsgCallback(sessionService1));
		// read managed data with session client from cache SC0 and load cascaded caches
		SCMessage response = sessionService1.execute(request);
		this.checkAppendices(response, 3);

		// verify that message is still in cache on sc0
		inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "3",
				"700/0|0=0&700/1|0=1&700/2|0=1&700/3|0=1&", "unset");

	}

	/**
	 * Description: 2 client 2 publish server one cache, 2 cache guardian<br>
	 * Expectation: passes
	 */
	@Test
	public void t41_cc_2Clients2Server1Cache() throws Exception {
		// connect new client to cascaded SC
		SCClient client2 = new SCClient(TestConstants.HOST, sessionClient.getPort(), ConnectionType.NETTY_TCP);
		client2.attach();
		SCSessionService sessionService2 = client2.newSessionService(TestConstants.sesServiceName1);

		// gets message cached (cacheid=700) to SC0
		SCMessage request = new SCMessage();
		request.setData("cacheFor1Hour_managedData");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);
		// verify that message is still in cache on sc0
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "0", "700/0|0=0&", "unset");

		// start cache guardian no publish instruction
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setData("700");
		subMsg.setSessionInfo(TestConstants.doNothingCmd);
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		sessionService2.createSession(new SCMessage(), new SessionMsgCallback(sessionService2));
		// start cache guardian and cache 3 appendix
		subMsg.setSessionInfo(TestConstants.publish3LargeAppendixMsgCmd);
		GuardianCbk cacheGuardianCbk2 = new GuardianCbk();
		client2.startCacheGuardian(TestConstants.cacheGuardian2, subMsg, cacheGuardianCbk2);
		// assure 3 messages arrive within 10 seconds!
		cacheGuardianCbk2.waitForMessage(10, 3);

		Assert.assertEquals("unexpected append received!", 0, cacheGuardianCbk.getUpdateMsgCounter());

		// verify that message is in cache on sc0
		inspectResponse = guardianClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "3",
				"700/0|0=0&700/1|0=1&700/2|0=1&700/3|0=1&", TestConstants.cacheGuardian2);
	}

	/**
	 * Description: 2 client 2 publish server one cache, 2 cache guardian<br>
	 * Expectation: passes
	 */
	@Test
	public void t42_cc_2Clients2Server1Cache() throws Exception {
		// connect new client to cascaded SC
		SCClient client2 = new SCClient(TestConstants.HOST, sessionClient.getPort(), ConnectionType.NETTY_TCP);
		client2.attach();
		SCSessionService sessionService2 = client2.newSessionService(TestConstants.sesServiceName1);

		// gets message cached (cacheid=700) to SC0
		SCMessage request = new SCMessage();
		request.setData("cacheFor1Hour_managedData");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);
		// verify that message is still in cache on sc0
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "0", "700/0|0=0&", "unset");

		sessionService2.createSession(new SCMessage(), new SessionMsgCallback(sessionService2));
		// start cache guardian and cache 3 appendix
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setData("700");
		subMsg.setSessionInfo(TestConstants.publish3LargeAppendixMsgCmd);
		GuardianCbk cacheGuardianCbk2 = new GuardianCbk();
		client2.startCacheGuardian(TestConstants.cacheGuardian2, subMsg, cacheGuardianCbk2);
		// assure 3 messages arrive within 10 seconds!
		cacheGuardianCbk2.waitForMessage(10, 3);

		// start cache guardian no publish instruction
		subMsg.setSessionInfo(TestConstants.doNothingCmd);
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		Assert.assertEquals("unexpected append received!", 0, cacheGuardianCbk.getUpdateMsgCounter());
		guardianClient.stopCacheGuardian();

		// verify that message is in cache on sc0
		inspectResponse = guardianClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "3",
				"700/0|0=0&700/1|0=1&700/2|0=1&700/3|0=1&", TestConstants.cacheGuardian2);

		subMsg.setSessionInfo(TestConstants.publish3LargeAppendixMsgCmd);
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);
		// assure 3 messages arrive within 10 seconds!
		cacheGuardianCbk.waitForMessage(10, 3);

		// verify that message is in cache on sc0
		inspectResponse = guardianClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "3",
				"700/0|0=0&700/1|0=1&700/2|0=1&700/3|0=1&", TestConstants.cacheGuardian2);
	}

	private void checkCacheInspectString(Map<String, String> inspectResponse, String returnStr, SC_CACHE_ENTRY_STATE msgState,
			String cid, String nrOfApp, String partInfo, String cacheGuardian) {
		Assert.assertEquals(returnStr, inspectResponse.get("return"));

		if (inspectResponse.get("return").equals("notfound")) {
			return;
		}
		Assert.assertEquals(msgState.toString(), inspectResponse.get("cacheMessageState"));
		Assert.assertEquals(cid, inspectResponse.get("cacheId"));
		Assert.assertEquals(nrOfApp, inspectResponse.get("cacheMessageNrOfAppendix"));
		Assert.assertEquals(partInfo, inspectResponse.get("cacheMessagePartInfo")); // cacheNrOfPartsOfInitialMsg=0
		Assert.assertEquals(cacheGuardian, inspectResponse.get("cacheMessageAssignedUpdateGuardian"));
	}

	private void checkAppendices(SCMessage scMessage, int expectedNrOfApp) {
		Assert.assertTrue("response not of type managed message", scMessage.isManaged());
		Assert.assertEquals(SC_CACHING_METHOD.INITIAL, scMessage.getCachingMethod());
		SCManagedMessage managedMessage = (SCManagedMessage) scMessage;
		Assert.assertEquals("unexpected number of appendices found in managed data", expectedNrOfApp,
				managedMessage.getNrOfAppendixes());
		int i = 0;
		for (SCAppendMessage scAppendMessage : managedMessage.getAppendixes()) {
			String body = (String) scAppendMessage.getData();
			if (body.startsWith(i + "") == false) {
				Assert.fail("unexpected appnendix body: " + body);
			}
			i++;
		}
	}
}
