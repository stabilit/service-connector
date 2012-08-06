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
	 * 1: load initial data to cache (cid=700) 2 seconds expiration
	 * 2: wait until expiration time runs out
	 * 3: verify data is NOT in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t01_cc_expirationOfInitialData() throws Exception {
		// 1: load initial data to cache (cid=700) 2 seconds expiration
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("cacheFor2Sec_managedData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 2: wait until expiration time runs out
		Thread.sleep(2000);

		// 3: verify data is NOT in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "notfound", SC_CACHE_ENTRY_STATE.UNDEFINDED, "", "", "", "");
	}

	/**
	 * Description
	 * 1: load initial data to cache (cid=700) 4 seconds expiration
	 * 2: start cache guardian - publish 3 Appendix
	 * 3: verify callback retrieval - 3 appendix within 10sec
	 * 4: verify data is in top level cache
	 * 5: wait until expiration time runs out
	 * 6: verify data is NOT in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t02_cc_expirationOfInitialDataAndAppendix() throws Exception {
		// 1: load initial data to cache (cid=700) 4 seconds expiration
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("cacheFor4Sec_managedData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 2: start cache guardian - publish 3 Appendix
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish3AppendixMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 3: verify callback retrieval - 3 appendix within 10sec
		cacheGuardianCbk.waitForAppendMessage(3, 10);

		// 4: verify data is in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "3",
				"700/0/0=0&700/1/0=0&700/2/0=0&700/3/0=0&", TestConstants.cacheGuardian1);

		// 5: wait until expiration time runs out
		Thread.sleep(3000);

		// 6: verify data is NOT in top level cache
		inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "notfound", SC_CACHE_ENTRY_STATE.UNDEFINDED, "", "", "", "");
	}

	/**
	 * Description
	 * 1: load initial data to cache (cid=700)
	 * 2: verify data is in top level cache
	 * 3: start cache guardian - publish 1 Remove
	 * 4: verify callback retrieval - 1 appendix within 5sec
	 * 5: verify data is NOT in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t03_cc_initialDataRemove() throws Exception {
		// 1: load initial data to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("managedData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 2: verify data is in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "0", "700/0/0=0&", "unset");

		// 3: start cache guardian - publish 1 Remove
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish1RemoveMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 4: verify callback retrieval - 1 remove within 5sec
		cacheGuardianCbk.waitForRemoveMessage(1, 5);

		// 5: verify data is NOT in top level cache
		inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "notfound", SC_CACHE_ENTRY_STATE.UNDEFINDED, "", "", "", "");
	}

	/**
	 * Description
	 * 1: load initial data to cache (cid=700) 2 seconds expiration
	 * 2: verify data is in top level cache
	 * 3: start cache guardian - publish 3 Appendix 1 Remove
	 * 4: verify callback retrieval - 3 appendix within 10sec
	 * 5: verify callback retrieval - 1 remove within 10sec
	 * 6: verify data is NOT in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t04_cc_initialData3LargeAppendixRemove() throws Exception {
		// 1: load initial data to cache (cid=700) 2 seconds expiration
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("managedData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 2: verify data is in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "0", "700/0/0=0&", "unset");

		// 3: start cache guardian - publish 3 Appendix 1 Remove
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish3LargeAppendix1RemoveMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 4: verify callback retrieval - 3 appendix within 10sec
		cacheGuardianCbk.waitForAppendMessage(3, 10);

		// 5: verify callback retrieval - 1 remove within 10sec
		cacheGuardianCbk.waitForRemoveMessage(1, 10);

		// 6: verify data is NOT in top level cache
		inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "notfound", SC_CACHE_ENTRY_STATE.UNDEFINDED, "", "", "", "");
	}

	/**
	 * Description
	 * 1: load initial data to cache (cid=700) for 1 hour
	 * 2: verify data is in top level cache
	 * 3: start cache guardian - publish 1 initial message
	 * 4: verify callback retrieval - 1 initial within 10sec
	 * 5: verify data is in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t05_cc_initialLargeDataReplaceByInitialData() throws Exception {
		// 1: load initial data to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("cache10MBStringFor1Hour_managedData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 2: verify data is in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "0", "700/0/0=51&", "unset");

		// 3: start cache guardian - publish 1 initial message
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish1InitialMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 4: verify callback retrieval - 1 initial within 10sec
		cacheGuardianCbk.waitForMessage(1, 10);

		// 5: verify data is in top level cache
		inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "0", "700/0/0=0&",
				TestConstants.cacheGuardian1);
	}

	/**
	 * Description
	 * 1: load initial data to cache (cid=700) for 1 hour
	 * 2: verify data is in top level cache
	 * 3: start cache guardian - publish 3 Appendix 1 initial message
	 * 4: verify callback retrieval - 3 appendix within 10sec
	 * 5: verify callback retrieval - 1 initial within 10sec
	 * 6: verify data is in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t05_cc_initialLargeData3AppendixReplaceByInitialData() throws Exception {
		// 1: load initial data to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("cache10MBStringFor1Hour_managedData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 2: verify data is in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "0", "700/0/0=51&", "unset");

		// 3: start cache guardian - publish 3 Appendix 1 initial message
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish3LargeAppendix1InitialMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 4: verify callback retrieval - 3 appendix within 10sec
		cacheGuardianCbk.waitForAppendMessage(3, 10);

		// 5: verify callback retrieval - 1 initial within 10sec
		cacheGuardianCbk.waitForMessage(1, 10);

		// 6: verify data is in top level cache
		inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "0", "700/0/0=0&",
				TestConstants.cacheGuardian1);
	}

	/**
	 * Description
	 * 1: load initial large data to cache (cid=700), static - no expiration time
	 * 2: verify data is in top level cache
	 * 3: start cache guardian - publish 1 Remove
	 * 4: verify callback retrieval - 1 remove within 5sec
	 * 5: verify data is NOT in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t06_cc_initialLargeDataNoExpirationRemove() throws Exception {
		// 1: load initial data to cache (cid=700), static - no expiration time
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("staticLargeData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 2: verify data is in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "0", "700/0/0=1&", "unset");

		// 3: start cache guardian - publish 1 Remove
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish1RemoveMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 4: verify callback retrieval - 1 remove within 5sec
		cacheGuardianCbk.waitForRemoveMessage(1, 5);

		// 5: verify data is NOT in top level cache
		inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "notfound", SC_CACHE_ENTRY_STATE.UNDEFINDED, "", "", "", "");
	}

	/**
	 * Description
	 * 1: load initial large data to cache (cid=700), static - no expiration time
	 * 2: verify data is in top level cache
	 * 3: start cache guardian - publish 3 Appendix
	 * 4: verify callback retrieval - 3 appendix within 10sec
	 * 5: verify data is in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t07_cc_initialLargeDataNoExpiration3LargeAppendix() throws Exception {
		// 1: load initial data to cache (cid=700), static - no expiration time
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("staticLargeData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 2: verify data is in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "0", "700/0/0=1&", "unset");

		// 3: start cache guardian - publish 3 Appendix
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish3AppendixMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 4: verify callback retrieval - 3 appendix within 10sec
		cacheGuardianCbk.waitForAppendMessage(3, 10);

		// 5: verify data is in top level cache
		inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "0", "700/0/0=1&", "unset");
	}

	/**
	 * Description
	 * 1: load initial large data to cache (cid=700), static - no expiration time
	 * 2: verify data is in top level cache
	 * 3: start cache guardian - publish 1 initial
	 * 4: verify callback retrieval - 1 initial within 10sec
	 * 5: verify data is NOT in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t08_cc_initialLargeDataNoExpirationInitial() throws Exception {
		// 1: load initial data to cache (cid=700), static - no expiration time
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("staticLargeData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 2: verify data is in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "0", "700/0/0=1&", "unset");

		// 3: start cache guardian - publish 1 initial
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish1InitialMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 4: verify callback retrieval - 1 initial within 10sec
		cacheGuardianCbk.waitForMessage(1, 10);

		// 5: verify data is NOT in top level cache
		inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "0", "700/0/0=1&", "unset");
	}

	/**
	 * Description
	 * 1: start cache guardian - publish 1 remove
	 * 2: verify callback retrieval - 1 remove within 10sec
	 * 3: verify data is NOT in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t09_cc_publishRemoveNothingInCache() throws Exception {
		// 1: start cache guardian - publish 1 remove
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish1RemoveMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 2: verify callback retrieval - 1 remove within 10sec
		cacheGuardianCbk.waitForRemoveMessage(1, 10);

		// 3: verify data is NOT in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "notfound", SC_CACHE_ENTRY_STATE.UNDEFINDED, "", "", "", "");
	}

	/**
	 * Description
	 * 1: start cache guardian - publish 1 initial
	 * 2: verify callback retrieval - 1 initial within 10sec
	 * 3: verify data is NOT in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t10_cc_publishInitialDataNothingInCache() throws Exception {
		// 1: start cache guardian - publish 1 initial
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish1InitialMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 2: verify callback retrieval - 1 initial within 10sec
		cacheGuardianCbk.waitForMessage(1, 10);

		// 3: verify data is NOT in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "notfound", SC_CACHE_ENTRY_STATE.UNDEFINDED, "", "", "", "");
	}

	/**
	 * Description
	 * 1: start cache guardian - publish 3 Appendix
	 * 2: verify callback retrieval - 3 appendix within 10sec
	 * 3: verify data is NOT in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t11_cc_Publish3LargeAppendixNothingInCache() throws Exception {
		// 1: start cache guardian - publish 3 Appendix
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish3AppendixMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 2: verify callback retrieval - 3 appendix within 10sec
		cacheGuardianCbk.waitForAppendMessage(3, 10);

		// 3: verify data is NOT in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "notfound", SC_CACHE_ENTRY_STATE.UNDEFINDED, "", "", "", "");
	}

	/**
	 * Description
	 * 1: start cache guardian - publish 1 initial
	 * 2: verify callback retrieval - 1 initial within 10sec
	 * 3: verify data is NOT in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t12_cc_Publish1InitialNothingInCache() throws Exception {
		// 1: start cache guardian - publish 1 initial
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish1InitialMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 2: verify callback retrieval - 1 initial within 10sec
		cacheGuardianCbk.waitForMessage(1, 10);

		// 3: verify data is NOT in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "notfound", SC_CACHE_ENTRY_STATE.UNDEFINDED, "", "", "", "");
	}

	/**
	 * Description
	 * 1: load small initial data to cache (cid=700)
	 * 2: verify data is in top level cache
	 * 3: start cache guardian - publish 1 small initial
	 * 4: verify callback retrieval - 1 small within 10sec
	 * 5: verify data is in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t13_cc_smallInitialSmallInitial() throws Exception {
		// 1: load initial data to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("managedData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 2: verify data is in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "0", "700/0/0=0&", "unset");

		// 3: start cache guardian - publish 1 small initial
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish1InitialMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 4: verify callback retrieval - 1 initial within 10sec
		cacheGuardianCbk.waitForMessage(1, 10);

		// 5: verify data is in top level cache
		inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "0", "700/0/0=0&",
				TestConstants.cacheGuardian1);
	}

	/**
	 * Description
	 * 1: load small initial data to cache (cid=700)
	 * 2: verify data is in top level cache
	 * 3: start cache guardian - publish 1 large initial
	 * 4: verify callback retrieval - 1 initial within 10sec
	 * 5: verify data is in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t14_cc_smallInitialLargeInitial() throws Exception {
		// 1: load initial data to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("managedData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 2: verify data is in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "0", "700/0/0=0&", "unset");

		// 3: start cache guardian - publish 1 large initial
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish1LargeInitialMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 4: verify callback retrieval - 1 initial within 10sec
		cacheGuardianCbk.waitForMessage(1, 10);

		// 5: verify data is in top level cache
		inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "0", "700/0/0=1&",
				TestConstants.cacheGuardian1);
	}

	/**
	 * Description
	 * 1: load small initial data to cache (cid=700)
	 * 2: verify data is in top level cache
	 * 3: start cache guardian - publish 10MB initial
	 * 4: verify callback retrieval - 1 initial within 10sec
	 * 5: verify data is in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t15_cc_smallInitial10BMInitial() throws Exception {
		// 1: load initial data to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("managedData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 2: verify data is in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "0", "700/0/0=0&", "unset");

		// 3: start cache guardian - publish 10MB initial
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish10MBInitialMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 4: verify callback retrieval - 1 initial within 10sec
		cacheGuardianCbk.waitForMessage(1, 10);

		// 5: verify data is in top level cache
		inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "0", "700/0/0=51&",
				TestConstants.cacheGuardian1);
	}

	/**
	 * Description
	 * 1: load large data (10MB) to cache (cid=700)
	 * 2: start cache guardian - publish 3 large Appendix (2 parts each) & 1 large initial
	 * 3: verify callback retrieval - 3 appendix within 10sec
	 * 4: verify callback retrieval - 1 initial within 10sec
	 * 5: read data from cache and verify
	 * 6: verify data is in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t16_cc_10MBInitial3LargeAppendix1LargeInitial() throws Exception {
		// 1: load large data (10MB) to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("cache10MBStringFor1Hour_managedData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 2: start cache guardian - publish 3 large Appendix (2 parts each) & 1 large initial
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish3LargeAppendix1LargeInitialMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 3: verify callback retrieval - 3 appendix within 10sec
		cacheGuardianCbk.waitForAppendMessage(3, 10);

		// 4: verify callback retrieval - 1 initial within 10sec
		cacheGuardianCbk.waitForMessage(1, 10);

		// 5: read data from cache and verify
		SCMessage response = sessionService1.execute(request);
		this.checkAppendices(response, 0);

		// 6: verify data is in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "0", "700/0/0=1&",
				TestConstants.cacheGuardian1);
	}

	/**
	 * Description
	 * 1: load large data (10MB) to cache (cid=700)
	 * 2: start cache guardian - publish 1 small initial & 3 large Appendix (2 parts each)
	 * 3: verify callback retrieval - 1 initial within 10sec
	 * 4: verify callback retrieval - 3 appendix within 10sec
	 * 5: read data from cache and verify
	 * 6: verify data is in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t17_cc_10MBInitial1SmallInitial3LargeAppendix() throws Exception {
		// 1: load large data (10MB) to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("cache10MBStringFor1Hour_managedData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 2: start cache guardian - publish 1 small initial 3 large Appendix (2 parts each)
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish1Initial3LargeAppendixMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 4: verify callback retrieval - 1 initial within 10sec
		cacheGuardianCbk.waitForMessage(1, 10);

		// 3: verify callback retrieval - 3 appendix within 10sec
		cacheGuardianCbk.waitForAppendMessage(3, 10);

		// 5: read data from cache and verify
		SCMessage response = sessionService1.execute(request);
		this.checkAppendices(response, 3);

		// 6: verify data is in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "3",
				"700/0/0=0&700/1/0=1&700/2/0=1&700/3/0=1&", TestConstants.cacheGuardian1);
	}

	/**
	 * Description
	 * 1: start cache guardian - publish Initials (10000 with 1sec delay, cid=700)
	 * 2: load large data (10MB) to cache (cid=700)
	 * 3: verify data is in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t18_cc_PublishInitialsLoad10MBInitialNotPossible() throws Exception {
		// 1: start cache guardian - publish Initials (10000 with 1sec delay, cid=700)
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publishMsgWithDelayCmd);
		subMsg.setData("10000|100|700|" + SC_CACHING_METHOD.INITIAL.getValue());
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 2: load large data (10MB) to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("cache10MBStringFor1Hour_managedData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 3: verify data is in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "notfound", SC_CACHE_ENTRY_STATE.UNDEFINDED, "", "", "", "");
	}

	/**
	 * Description
	 * 1: load data to cache (cid=700)
	 * 2: start cache guardian - publish 3 Appendix
	 * 3: verify callback retrieval - 3 appendix within 10sec
	 * 4: read data from cache and verify
	 * 5: verify data is in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t19_cc_readManagedData() throws Exception {
		// 1: load data to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("cacheFor1Hour_managedData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 2: start cache guardian - publish 3 Appendix
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish3AppendixMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 3: verify callback retrieval - 3 appendix within 10sec
		cacheGuardianCbk.waitForAppendMessage(3, 10);

		// 4: read data from cache and verify
		SCMessage response = sessionService1.execute(request);
		this.checkAppendices(response, 3);

		// 5: verify data is in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "3",
				"700/0/0=0&700/1/0=0&700/2/0=0&700/3/0=0&", TestConstants.cacheGuardian1);
	}

	/**
	 * Description
	 * 1: load large data (10MB) to cache (cid=700)
	 * 2: start cache guardian - publish 3 Appendix
	 * 3: verify callback retrieval - 3 appendix within 10sec
	 * 4: read data from cache and verify
	 * 5: verify data is in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t20_cc_readManagedData10MBInitial() throws Exception {
		// 1: load large data (10MB) to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("cache10MBStringFor1Hour_managedData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 2: start cache guardian - publish 3 Appendix
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish3AppendixMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 3: verify callback retrieval - 3 appendix within 10sec
		cacheGuardianCbk.waitForAppendMessage(3, 10);

		// 4: read data from cache and verify
		SCMessage response = sessionService1.execute(request);
		this.checkAppendices(response, 3);

		// 5: verify data is in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "3",
				"700/0/0=51&700/1/0=0&700/2/0=0&700/3/0=0&", TestConstants.cacheGuardian1);
	}

	/**
	 * Description
	 * 1: load large data (10MB) to cache (cid=700)
	 * 2: start cache guardian - publish 3 large Appendix (2 parts each)
	 * 3: verify callback retrieval - 3 appendix within 10sec
	 * 4: read data from cache and verify
	 * 5: verify data is in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t21_cc_readManagedData10MBInitialLargeAppendix() throws Exception {
		// 1: load large data (10MB) to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("cache10MBStringFor1Hour_managedData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 2: start cache guardian - publish 3 large Appendix (2 parts each)
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish3LargeAppendixMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 3: verify callback retrieval - 3 appendix within 10sec
		cacheGuardianCbk.waitForAppendMessage(3, 10);

		// 4: read data from cache and verify
		SCMessage response = sessionService1.execute(request);
		this.checkAppendices(response, 3);

		// 5: verify data is in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "3",
				"700/0/0=51&700/1/0=1&700/2/0=1&700/3/0=1&", TestConstants.cacheGuardian1);
	}

	/**
	 * Description:
	 * 1: load large data (10MB) to cache (cid=700)
	 * 2: start cache guardian - publish 1 large (10MB) Appendix
	 * 3: verify callback retrieval - 1 appendix within 100sec
	 * 4: read data from cache and verify
	 * 5: verify data is in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t22_cc_readManagedData10MBInitial10MBAppendix() throws Exception {
		// 1: load large data (10MB) to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("cache10MBStringFor1Hour_managedData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 2: start cache guardian - publish 1 large (10MB) Appendix
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish10MBAppendixMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 3: verify callback retrieval - 1 appendix within 100sec
		cacheGuardianCbk.waitForAppendMessage(1, 100);

		// 4: read data from cache and verify
		SCMessage response = sessionService1.execute(request);
		this.checkAppendices(response, 1);

		// 5: verify data is in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "1", "700/0/0=51&700/1/0=51&",
				TestConstants.cacheGuardian1);
	}

	/**
	 * Description
	 * 1: load data to cache (cid=700)
	 * 2: start cache guardian - doing nothing
	 * 3: stop cache guardian - cache will clean up managed data
	 * 4: verify data is NOT in top level cache
	 * 5: verify data is NOT in any cache
	 * Expectation: passes
	 */
	@Test
	public void t23_cc_InitialMsgStopCacheGuardian() throws Exception {
		// 1: load data to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setData("cacheFor1Hour_managedData");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 2: start cache guardian - doing nothing
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 3: stop cache guardian - cache will clean up managed data
		guardianClient.stopCacheGuardian();

		// 4: verify data is NOT in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "notfound", SC_CACHE_ENTRY_STATE.UNDEFINDED, "", "", "", "");

		// 5: verify data is NOT in any cache
		request.setMessageInfo(TestConstants.echoCmd);
		request.setData("test");
		request.setCacheId("700");
		SCMessage response = sessionService1.execute(request);
		Assert.assertEquals("test", response.getData());
	}

	/**
	 * Description:
	 * 1: load data to cache (cid=700)
	 * 2: start cache guardian - publish 3 large Appendix (2 parts each)
	 * 3: stop cache guardian - cache will clean up managed data
	 * 4: verify data is NOT in top level cache
	 * 5: verify data is NOT in any cache
	 * Expectation: passes
	 */
	@Test
	public void t24_cc_InitialMsg3AppendixStopCacheGuardian() throws Exception {
		// 1: load data to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setData("cacheFor1Hour_managedData");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 2: start cache guardian - publish 3 large Appendix (2 parts each)
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish3LargeAppendixMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 3: stop cache guardian - cache will clean up managed data
		guardianClient.stopCacheGuardian();

		// 4: verify data is NOT in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "notfound", SC_CACHE_ENTRY_STATE.UNDEFINDED, "", "", "", "");

		// 5: verify data is NOT in any cache
		request.setMessageInfo(TestConstants.echoCmd);
		request.setData("test");
		request.setCacheId("700");
		SCMessage response = sessionService1.execute(request);
		Assert.assertEquals("test", response.getData());
	}

	/**
	 * Description:
	 * 1: start cache guardian - publish Appendix (10000 with 1sec delay, cid=700)
	 * 2: load 10MB to cache (cid=700)
	 * 3: verify response is correct
	 * 4: verify data is NOT in top level cache
	 * 5: stop cache guardian
	 * Expectation: passes
	 */
	@Test
	public void t25_cc_RetrievingAppendixDuringLoadOfInitialMsg() throws Exception {
		// 1: start cache guardian - publish Appendix (10000 with 1sec delay, cid=700)
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publishMsgWithDelayCmd);
		subMsg.setData("10000|100|700");
		subMsg.setCachingMethod(SC_CACHING_METHOD.APPEND);
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 2: load 10MB to cache (cid=700) - no caching possible because appendix are retrieved
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("cache10MBStringFor1Hour_managedData");
		request.setMessageInfo(TestConstants.cacheCmd);
		SCMessage response = sessionService1.execute(request);

		// 3: verify response is correct
		String expectedResponse = TestUtil.get10MBString();
		Assert.assertEquals("unequal length", expectedResponse.length(), response.getDataLength());
		Assert.assertEquals(expectedResponse, response.getData());

		// 4: verify data is NOT in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "notfound", SC_CACHE_ENTRY_STATE.UNDEFINDED, "", "", "", "");

		// 5: stop cache guardian
		guardianClient.stopCacheGuardian();
	}

	/**
	 * Description
	 * 1: load large data (10MB) to cache (cid=700)
	 * 2: verify data is in top level cache
	 * 3: start cache guardian - publish 1 large (50MB) Appendix
	 * 4: try reading data from cache - cache loading exception (appendix loading)
	 * 5: wait until appendix is loaded
	 * 6: verify data is in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t26_cc_CacheLoadingExceptionLoadingAppendix() throws Exception {
		// 1: load large data (10MB) to cache (cid=700)
		SCMessage request = new SCMessage();
		request.setCacheId("700");
		request.setData("cache10MBStringFor1Hour_managedData");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 2: verify data is in top level cache
		Map<String, String> inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "0", "700/0/0=51&", "unset");

		// 3: start cache guardian - publish 1 large (50MB) Appendix
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish50MBAppendixMsgCmd);
		subMsg.setData("700");
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 4: try reading data from cache - cache loading exception (appendix loading)
		Thread.sleep(2000); // assure publish appendix started
		try {
			sessionService1.execute(request);
			Assert.fail("Cache loading exception expected.");
		} catch (SCServiceException e) {
			Assert.assertEquals(SCMPError.CACHE_LOADING.getErrorCode(), e.getSCErrorCode());
		}

		// 5: wait until appendix is loaded
		boolean finish = false;
		while (finish == false) {
			inspectResponse = mgmtClient.inspectCache("700");
			String state = inspectResponse.get("cacheMessageState");
			if (state.equals(SC_CACHE_ENTRY_STATE.LOADING_APPENDIX.name()) == false) {
				finish = true;
			}
			Thread.sleep(1000);
		}

		// 6: verify data is in top level cache
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "1",
				"700/0/0=51&700/1/0=256&", TestConstants.cacheGuardian1);

		// 7: stop cache guardian
		guardianClient.stopCacheGuardian();
	}

	/**
	 * Description:
	 * 1: connect new client2 to top level (cascaded) SC
	 * 2: load data to cache (cid=700) by client1
	 * 3: start cache guardian1 - do nothing
	 * 4: start cache guardian2 - publish 3 large appendix
	 * 5: verify callback of guardian2 retrieval - 3 appendix within 100sec
	 * 6: verify callback of guardian1 retrieval - nothing retrieved
	 * 7: verify data is now in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t30_cc_2Clients2Server1Cache() throws Exception {
		// 1: connect new client2 to top level (cascaded) SC
		SCClient client2 = new SCClient(TestConstants.HOST, sessionClient.getPort(), ConnectionType.NETTY_TCP);
		client2.attach();
		SCSessionService sessionService2 = client2.newSessionService(TestConstants.sesServiceName1);
		sessionService2.createSession(new SCMessage(), new SessionMsgCallback(sessionService2));

		// 2: load data to cache (cid=700) by client1
		SCMessage request = new SCMessage();
		request.setData("cacheFor1Hour_managedData");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 3: start cache guardian1 - do nothing
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setData("700");
		subMsg.setSessionInfo(TestConstants.doNothingCmd);
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 4: start cache guardian2 - publish 3 large appendix
		subMsg.setSessionInfo(TestConstants.publish3LargeAppendixMsgCmd);
		GuardianCbk cacheGuardianCbk2 = new GuardianCbk();
		client2.startCacheGuardian(TestConstants.cacheGuardian2, subMsg, cacheGuardianCbk2);

		// 5: verify callback of guardian2 retrieval - 3 appendix within 100sec
		cacheGuardianCbk2.waitForAppendMessage(3, 10);

		// 6: verify callback of guardian1 retrieval - nothing retrieved
		Assert.assertEquals("unexpected append received!", 0, cacheGuardianCbk.getUpdateMsgCounter());

		// 7: verify data is now in top level cache
		Map<String, String> inspectResponse = guardianClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "3",
				"700/0/0=0&700/1/0=1&700/2/0=1&700/3/0=1&", TestConstants.cacheGuardian2);
	}

	/**
	 * Description:
	 * 1: connect new client2 to top level (cascaded) SC
	 * 2: load data to cache (cid=700) by client1
	 * 3: start cache guardian1 - publish 3 large appendix
	 * 4: start cache guardian2 - publish 3 large appendix
	 * 5: verify callback of guardian1 retrieval - 3 appendix within 100sec
	 * 6: verify callback of guardian2 retrieval - 3 appendix within 100sec
	 * 7: verify data is now in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t31_cc_2Clients2Server1Cache() throws Exception {
		// 1: connect new client2 to top level (cascaded) SC
		SCClient client2 = new SCClient(TestConstants.HOST, sessionClient.getPort(), ConnectionType.NETTY_TCP);
		client2.attach();
		SCSessionService sessionService2 = client2.newSessionService(TestConstants.sesServiceName1);
		sessionService2.createSession(new SCMessage(), new SessionMsgCallback(sessionService2));

		// 2: load data to cache (cid=700) by client1
		SCMessage request = new SCMessage();
		request.setData("cacheFor1Hour_managedData");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.execute(request);

		// 3: start cache guardian1 - publish 3 large appendix
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setData("700");
		subMsg.setSessionInfo(TestConstants.publish3LargeAppendixMsgCmd);
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 4: start cache guardian2 - publish 3 large appendix
		subMsg.setSessionInfo(TestConstants.publish3LargeAppendixMsgCmd);
		GuardianCbk cacheGuardianCbk2 = new GuardianCbk();
		client2.startCacheGuardian(TestConstants.cacheGuardian2, subMsg, cacheGuardianCbk2);

		// 5: verify callback of guardian1 retrieval - 3 appendix within 100sec
		cacheGuardianCbk.waitForAppendMessage(3, 10);

		// 6: verify callback of guardian2 retrieval - 3 appendix within 100sec
		cacheGuardianCbk2.waitForAppendMessage(3, 10);

		// 7: verify data is now in top level cache
		Map<String, String> inspectResponse = guardianClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "3",
				"700/0/0=0&700/1/0=1&700/2/0=1&700/3/0=1&");
	}

	protected void checkCacheInspectString(Map<String, String> inspectResponse, String returnStr, SC_CACHE_ENTRY_STATE msgState,
			String cid, String nrOfApp, String partInfo, String cacheGuardian) {
		this.checkCacheInspectString(inspectResponse, returnStr, msgState, cid, nrOfApp, partInfo);
		if (inspectResponse.get("return").equals("notfound")) {
			return;
		}
		Assert.assertEquals(cacheGuardian, inspectResponse.get("cacheMessageAssignedUpdateGuardian"));
	}

	protected void checkCacheInspectString(Map<String, String> inspectResponse, String returnStr, SC_CACHE_ENTRY_STATE msgState,
			String cid, String nrOfApp, String partInfo) {
		Assert.assertEquals(returnStr, inspectResponse.get("return"));

		if (inspectResponse.get("return").equals("notfound")) {
			return;
		}
		Assert.assertEquals(msgState.toString(), inspectResponse.get("cacheMessageState"));
		Assert.assertEquals(cid, inspectResponse.get("cacheId"));
		Assert.assertEquals(nrOfApp, inspectResponse.get("cacheMessageNrOfAppendix"));
		Assert.assertEquals(partInfo, inspectResponse.get("cacheMessagePartInfo")); // cacheNrOfPartsOfInitialMsg=0
	}

	protected void checkAppendices(SCMessage scMessage, int expectedNrOfApp) {
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
