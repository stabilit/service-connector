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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.cache.CacheComposite.CACHE_STATE;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.test.system.SystemSuperTest;
import org.serviceconnector.test.system.api.APISystemSuperSessionClientTest;

@SuppressWarnings("unused")
public class APIExecuteCacheCasc1Test extends APISystemSuperSessionClientTest {

	public APIExecuteCacheCasc1Test() {
		SystemSuperTest.setUp1CascadedServiceConnectorAndServer();
	}

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		this.setUpClientToSC();
	}

	/**
	 * Description: exchange message with cacheId, server reply without cacheExpirationTime<br>
	 * Expectation: no caching of the message
	 */
	@Test
	public void t01_cache() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.cacheCmd);
		request.setData("cidNoCed");
		request.setCacheId("700");
		response = sessionService1.execute(request);
		// inspect cache entry
		Map<String, String> inspectResponse = client.inspectCache(TestConstants.sesServiceName1, "700");
		Assert.assertEquals(inspectResponse.get("return"), "notfound");
	}

	/**
	 * Description: exchange message with cacheId, server reply with cacheExpirationTime + 1 hour, get message from cache<br>
	 * Expectation: catching a cached message
	 */
	@Test
	public void t02_cache() throws Exception {
		SCMessage request = new SCMessage();
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setData("cacheFor1Hour");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService1.execute(request);
		// inspect cache entry
		Map<String, String> inspectResponse = client.inspectCache(TestConstants.sesServiceName1, "700");
		Assert.assertEquals("success", inspectResponse.get("return"));
		Assert.assertEquals(CACHE_STATE.LOADED.toString(), inspectResponse.get("cacheState"));
		Assert.assertEquals("700", inspectResponse.get("cacheId"));

		request.setData("cacheFor2Hour");
		response = sessionService1.execute(request);
		Assert.assertEquals("cacheFor1Hour", response.getData());
		// inspect cache entry
		inspectResponse = client.inspectCache(TestConstants.sesServiceName1, "700");
		Assert.assertEquals("success", inspectResponse.get("return"));
		Assert.assertEquals(CACHE_STATE.LOADED.toString(), inspectResponse.get("cacheState"));
		Assert.assertEquals("700", inspectResponse.get("cacheId"));

		sessionService1.deleteSession();
	}

	/**
	 * Description: exchange message with cacheId, server reply with cacheExpirationTime + 2 seconds, let cached message expire<br>
	 * Expectation: cached message expired
	 */
	@Test
	public void t05_cache() throws Exception {
		SCMessage request = new SCMessage();
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setData("cacheFor2Sec");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService1.execute(request);
		Assert.assertEquals("cacheFor2Sec", response.getData());
		// wait until cache message expires
		Thread.sleep(4010);
		request.setData(TestConstants.pangram);
		response = sessionService1.execute(request);
		Assert.assertEquals(TestConstants.pangram, response.getData());
		sessionService1.deleteSession();
	}

	/**
	 * Description: exchange message with cacheId, server replies with cacheExpirationTime - 1 Hour<br>
	 * In a second step insert a normal not expired cache message<br/>
	 * Expectation: server cache message expired
	 */
	@Test
	public void t06_cache() throws Exception {
		SCMessage request = new SCMessage();
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		// request expired server message, cache should still be empty
		request.setData("cacheExpired1Hour");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService1.execute(request);
		Assert.assertEquals("cacheExpired1Hour", response.getData());
		// request valid server cache message, will be stored in cache
		request.setData("cacheFor1Hour");
		response = sessionService1.execute(request);
		Assert.assertEquals("cacheFor1Hour", response.getData());
		// request expired server message again, previous message should still remain in cache
		request.setData("cacheExpired1Hour");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService1.execute(request);

		sessionService1.deleteSession();
	}

	/**
	 * Description: exchange large message with cacheId, server reply with cacheExpirationTime, part size 64KB<br>
	 * Expectation: get large message from cache
	 */
	@Test
	public void t07_cacheLargeMessage() throws Exception {
		SCMessage request = new SCMessage();
		request.setCompressed(false);
		request.setPartSize(1 << 16); // 64KB
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		String largeMessage = TestUtil.getLargeString();
		request.setData(largeMessage); // internal cache timeout on server one hour
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService1.execute(request);
		request.setData("cacheFor1Hour");
		response = sessionService1.execute(request);
		Assert.assertEquals(largeMessage, response.getData());
		// inspect cache entry
		Map<String, String> inspectResponse = client.inspectCache(TestConstants.sesServiceName1, "700");
		Assert.assertEquals("success", inspectResponse.get("return"));
		Assert.assertEquals(CACHE_STATE.LOADED.toString(), inspectResponse.get("cacheState"));
		Assert.assertEquals("700", inspectResponse.get("cacheId"));
		Assert.assertEquals("2", inspectResponse.get("cacheSize"));
		sessionService1.deleteSession();
	}

	/**
	 * Description: exchange large message with cacheId repeating times, part size 64KB<br>
	 * The first large client call loads the cache and accesses the server.<br>
	 * The following client calls (also large message) MUST read the reply from the cache, NO ACCESS
	 * to the server is allowed.
	 * Expectation: get large message from cache
	 */
	@Test
	public void t07_2_cacheLargeMessage() throws Exception {
		SCMessage request = new SCMessage();
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setPartSize(1 << 16); // 64KB
		Thread.sleep(1000);
		{ // first time
			String largeMessage = TestUtil.getLargeString();
			request.setData(largeMessage); // internal cache timeout on server one hour
			request.setCacheId("700");
			request.setMessageInfo(TestConstants.cacheCmd);
			response = sessionService1.execute(request);
			Assert.assertEquals(largeMessage, response.getData());
			// inspect cache entry
			Map<String, String> inspectResponse = client.inspectCache(TestConstants.sesServiceName1, "700");
			Assert.assertEquals("success", inspectResponse.get("return"));
			Assert.assertEquals(CACHE_STATE.LOADED.toString(), inspectResponse.get("cacheState"));
			Assert.assertEquals("700", inspectResponse.get("cacheId"));
			Assert.assertEquals("2", inspectResponse.get("cacheSize"));
		}
		Thread.sleep(1000);
		// next client calls, do not access srv, read from cache
		for (int i = 0; i < 10; i++) {
			String largeMessage = TestUtil.getLargeString();
			request.setData(largeMessage); // internal cache timeout on server one hour
			request.setCacheId("700");
			request.setMessageInfo(TestConstants.cacheCmd);
			response = sessionService1.execute(request);
			Assert.assertEquals(largeMessage, response.getData());
			Thread.sleep(1000);
		}
		sessionService1.deleteSession();
	}

	/**
	 * Description: exchange 10MB large message with cacheId, server reply with cacheExpirationTime, part size 64KB<br>
	 * Expectation: get large message from cache
	 */
	@Test
	public void t08_cache10MBLargeMessage() throws Exception {
		SCMessage request = new SCMessage();
		request.setCompressed(false);
		SCMessage response = null;
		request.setPartSize(1 << 16); // 64KB
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		String largeMessage = TestUtil.get10MBString();
		request.setData(largeMessage); // internal cache timeout on server one hour
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService1.execute(request);
		// inspect cache entry
		Map<String, String> inspectResponse = client.inspectCache(TestConstants.sesServiceName1, "700");
		Assert.assertEquals("success", inspectResponse.get("return"));
		Assert.assertEquals(CACHE_STATE.LOADED.toString(), inspectResponse.get("cacheState"));
		Assert.assertEquals("700", inspectResponse.get("cacheId"));
		Assert.assertEquals("52", inspectResponse.get("cacheSize"));
		request.setData("cacheFor1Hour");
		response = sessionService1.execute(request);
		Assert.assertEquals(largeMessage, response.getData());
		// inspect cache entry
		inspectResponse = client.inspectCache(TestConstants.sesServiceName1, "700");
		Assert.assertEquals("success", inspectResponse.get("return"));
		Assert.assertEquals(CACHE_STATE.LOADED.toString(), inspectResponse.get("cacheState"));
		Assert.assertEquals("700", inspectResponse.get("cacheId"));
		Assert.assertEquals("52", inspectResponse.get("cacheSize"));
		sessionService1.deleteSession();
	}

	/**
	 * Description: sessionService exchange a message, sessionService1 exchange a message, with different cacheId's on two service
	 * instances<br>
	 * Expectation: get messages from cache
	 */
	@Test
	public void t10_2ClientsGetDifferentCacheMessage() throws Exception {
		SCMessage request = new SCMessage();
		request.setCompressed(false);
		SCMessage response = null;
		// session service one stores "cacheFor1Hour" with cacheId 700
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setData("cacheFor1Hour");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService1.execute(request);
		Assert.assertEquals("cacheFor1Hour", response.getData());

		// session service two stores "cacheFor2Hour" with cacheId 600
		SCSessionService sessionService2 = client.newSessionService(TestConstants.sesServiceName1);
		MsgCallback msgCallback2 = new MsgCallback(sessionService1);
		response = sessionService2.createSession(request, msgCallback2);
		request.setData("cacheFor2Hour");
		request.setCacheId("600");
		response = sessionService2.execute(request);
		Assert.assertEquals("cacheFor2Hour", response.getData());

		// session service one gets message with cacheId 700
		request.setData(TestConstants.pangram);
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService1.execute(request);
		Assert.assertEquals("cacheFor1Hour", response.getData());
		Assert.assertEquals("700", response.getCacheId());
		Assert.assertEquals("1", response.getCachePartNr());

		// session service two gets message with cacheId 600
		request.setData(TestConstants.pangram);
		request.setCacheId("600");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService2.execute(request);
		Assert.assertEquals("cacheFor2Hour", response.getData());
		Assert.assertEquals("600", response.getCacheId());
		Assert.assertEquals("1", response.getCachePartNr());

		// session service one gets message with cacheId 600
		request.setData(TestConstants.pangram);
		request.setCacheId("600");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService1.execute(request);
		Assert.assertEquals("cacheFor2Hour", response.getData());
		Assert.assertEquals("600", response.getCacheId());
		Assert.assertEquals("1", response.getCachePartNr());

		// session service two gets message with cacheId 700
		request.setData(TestConstants.pangram);
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService2.execute(request);
		Assert.assertEquals("cacheFor1Hour", response.getData());
		Assert.assertEquals("700", response.getCacheId());
		Assert.assertEquals("1", response.getCachePartNr());

		sessionService2.deleteSession();
		sessionService1.deleteSession();
	}

	/**
	 * Description: sessionService exchange a message, sessionService1 exchange a message, with same cacheId's on two service
	 * instances, sessionService two refreshes cache<br>
	 * Expectation: get messages from cache
	 */
	@Test
	public void t11_2ClientsStoresSameMessage() throws Exception {
		SCMessage request = new SCMessage();
		request.setCompressed(false);
		SCMessage response = null;
		// session service one stores "cacheFor1Hour" with cacheId 700
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setData("cacheFor1Hour");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService1.execute(request);
		Assert.assertEquals("cacheFor1Hour", response.getData());

		Thread.sleep(2000);

		// session service two stores "refreshCache700" with no cacheId, server refreshes cacheId 700
		SCSessionService sessionService2 = client.newSessionService(TestConstants.sesServiceName1);
		MsgCallback msgCallback2 = new MsgCallback(sessionService1);
		response = sessionService2.createSession(request, msgCallback2);
		request.setData("refreshCache700");
		request.setCacheId(null);
		response = sessionService2.execute(request);
		Assert.assertEquals("refreshCache700", response.getData());

		// session service one gets message with cacheId 700
		request.setData(TestConstants.pangram);
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService1.execute(request);
		Assert.assertEquals("refreshCache700", response.getData());
		Assert.assertEquals("700", response.getCacheId());
		Assert.assertEquals("1", response.getCachePartNr());

		// session service two gets message with cacheId 700
		request.setData(TestConstants.pangram);
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService2.execute(request);
		Assert.assertEquals("refreshCache700", response.getData());
		Assert.assertEquals("700", response.getCacheId());
		Assert.assertEquals("1", response.getCachePartNr());

		sessionService2.deleteSession();
		sessionService1.deleteSession();
	}

	/**
	 * Description: sessionService1 exchange a large message into cache, sessionService2 gets same message on same service
	 * instances, sessionService2 gets an exception<br>
	 * Expectation: cache loading exception
	 */
	@Test
	public void t12_2ClientsCacheAndGetSameMessage() throws Exception {
		SCMessage request = new SCMessage();
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);

		SCSessionService sessionService2 = client.newSessionService(TestConstants.sesServiceName1);
		MsgCallback msgCallback2 = new MsgCallback(sessionService1);
		response = sessionService2.createSession(request, msgCallback2);

		// session service starts storing large message with cacheId 700
		// request.setData(largeMessage);
		request.setData("cache10MBStringFor1Hour");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.send(request);
		// to assure service1 started loading cache
		Thread.sleep(10);
		// session service2 starts getting large message from cache with cacheId 700
		request.setData("randomContent");
		request.setMessageInfo(null);
		request.setCacheId("700");
		try {
			response = sessionService2.execute(request);
			Thread.sleep(500);
			Assert.fail("should throw exception");
		} catch (SCServiceException e) {
			Assert.assertEquals(SCMPError.CACHE_LOADING.getErrorCode(), e.getSCErrorCode());
		}
	}

	/**
	 * Description: sessionService1 exchange a large message into cache, sessionService2 gets same message on same service
	 * The second small client request will be faster than the first one, beacause cache loading will start when the last
	 * client part did arrive on sc.
	 * instances, sessionService2 gets an exception<br>
	 * Expectation: cache loading exception
	 */
	@Test
	public void t13_2ClientsOneLargeOneSmallRequestCacheAndGetSameMessage() throws Exception {
		SCMessage request = new SCMessage();
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);

		SCSessionService sessionService2 = client.newSessionService(TestConstants.sesServiceName1);
		MsgCallback msgCallback2 = new MsgCallback(sessionService1);
		response = sessionService2.createSession(request, msgCallback2);

		// session service starts storing large message with cacheId 700
		String largeMessage = TestUtil.get10MBString();
		request.setData(largeMessage);
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		// System.out.println("sessionService1 sessionId=" + sessionService1.getSessionId());
		sessionService1.send(request);
		// to assure service1 started loading cache
		Thread.sleep(100);
		// session service2 starts getting large message from cache with cacheId 700
		request.setData(TestConstants.pangram);
		request.setMessageInfo(null);
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		try {
			response = sessionService2.execute(request);
			Assert.fail("Should throw an exception but did not.");
		} catch (SCServiceException e) {
			Assert.assertEquals(SCMPError.CACHE_LOADING.getErrorCode(), e.getSCErrorCode());
		}
		// get response from sessionService1 request
		msgCallback1.waitForMessage(60);
		response = msgCallback1.getResponse();
		SCMessage response2 = sessionService2.execute(request);
		Assert.assertEquals(response.getData(), response2.getData());
	}

	/**
	 * Description: sessionService exchange a large message, sessionService1 exchange a message, with same cacheId's on two service
	 * instances, sessionService two gets message from cache, part size 64KB<br>
	 * Expectation: get messages from cache
	 */
	@Test
	public void t14_2ClientsLargeMessage() throws Exception {
		String largeMessage = TestUtil.get10MBString();
		SCMessage request = new SCMessage();
		request.setPartSize(1 << 16); // 64KB
		request.setCompressed(false);
		SCMessage response = null;
		// session service starts storing large message with cacheId 700
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setData("cache10MBStringFor1Hour");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.send(request);

		Thread.sleep(10000);

		// session service2 starts getting large message from cache with cacheId 700
		SCSessionService sessionService2 = client.newSessionService(TestConstants.sesServiceName1);
		MsgCallback msgCallback2 = new MsgCallback(sessionService1);
		response = sessionService2.createSession(request, msgCallback2);
		request.setData("randomContent");
		request.setCacheId("700");
		response = sessionService2.execute(request);
		Assert.assertEquals(largeMessage, response.getData());

		// session service1 get message
		msgCallback1.waitForMessage(60);
		response = msgCallback1.getResponse();
		Assert.assertEquals(largeMessage, response.getData());
	}

	/**
	 * Description: exchange message with cacheId, server does not reply within given timeout<br>
	 * Inspect the cache and verify that no cache entry is available for given cacheId.<br/>
	 * Expectation: server cache reply timeout, no cache entry
	 */
	@Test
	public void t20_cacheServerTimeoutReply() throws Exception {
		// inspect cache
		Map<String, String> inspectResponse = client.inspectCache(TestConstants.sesServiceName1, "700");
		Assert.assertEquals(inspectResponse.get("return"), "notfound");
		SCMessage request = new SCMessage();
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		// ask for message from cache, the server does reply after 10 seconds
		// but this is too late
		request.setData("cacheTimeoutReply");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		try {
			response = sessionService1.execute(5, request);
			Assert.fail("execution timeout not thrown");
		} catch (Exception e) {
		}
		// wait other 10s, SC server timeout should expire and cache composite removed from cache
		Thread.sleep(10000);
		inspectResponse = client.inspectCache(TestConstants.sesServiceName1, "700");
		Assert.assertEquals(inspectResponse.get("return"), "notfound");
		sessionService1.deleteSession();
	}

	/**
	 * Description: exchange message with cacheId, server replies with different cacheId<br>
	 * Save server reply in cache under servers cache id and not under clients cache id.<br/>
	 * Expectation: server cache replies different cache id
	 */
	@Test
	public void t21_cacheServerRepliesOtherCacheId() throws Exception {
		// inspect cache
		Map<String, String> inspectResponse = client.inspectCache(TestConstants.sesServiceName1, "700");
		Assert.assertEquals(inspectResponse.get("return"), "notfound");
		SCMessage request = new SCMessage();
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		// ask for message from cache for cacheId 700, the server replies with cacheId 800 (700 + 100)
		request.setData("cacheServerReplyOther");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService1.execute(request);
		Assert.assertEquals("cacheServerReplyOther", response.getData());
		// inspect cache with cacheId 700
		inspectResponse = client.inspectCache(TestConstants.sesServiceName1, "700");
		Assert.assertEquals(inspectResponse.get("return"), "notfound");
		// inspect cache with cacheId 800
		inspectResponse = client.inspectCache(TestConstants.sesServiceName1, "800");
		Assert.assertEquals(inspectResponse.get("return"), "success");
		sessionService1.deleteSession();
	}

	/**
	 * Description: exchange message with cacheId, server replies with cacheExpirationTime - 1 Hour<br>
	 * In a second step clear the cache and get the message. In a cascaded mode the message will come from a cascaded SC cache<br/>
	 * Expectation: passes
	 */
	@Test
	public void t25_cacheAMessageClearCacheAndGetMessage() throws Exception {
		SCMessage request = new SCMessage();
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		// request expired server message, cache should still be empty
		request.setData("cacheFor1Hour");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService1.execute(request);
		Assert.assertEquals("cacheFor1Hour", response.getData());
		client.clearCache();

		response = sessionService1.execute(request);
		Assert.assertEquals("cacheFor1Hour", response.getData());
		sessionService1.deleteSession();
	}

	/**
	 * Description: exchange message with cacheId, cache expires but is still in cache, exchange message with same cacheId - same
	 * session<br>
	 * Expectation: passes
	 */
	@Test
	public void t26_cacheAMessageExpiresCacheAgain() throws Exception {
		SCMessage request = new SCMessage();
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		// request expired server message, cache should still be empty
		request.setData("cacheFor2Sec");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService1.execute(request);
		Thread.sleep(3000);
		request.setData("cacheFor2Hour");
		response = sessionService1.execute(request);
		Assert.assertEquals("cacheFor2Hour", response.getData());
		sessionService1.deleteSession();
	}
}
