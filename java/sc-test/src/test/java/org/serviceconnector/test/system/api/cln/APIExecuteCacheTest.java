/*
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
 */
package org.serviceconnector.test.system.api.cln;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.cache.CacheComposite.CACHE_STATE;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.system.api.APISystemSuperSessionClientTest;
import org.serviceconnector.util.URLParameterString;

@SuppressWarnings("unused")
public class APIExecuteCacheTest extends APISystemSuperSessionClientTest {

	protected SCMgmtClient mgmtClient;

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		mgmtClient = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		mgmtClient.attach();
	}
	
	@After
	public void afterOneTest() throws Exception {
		try {
			if (mgmtClient != null) {
			   mgmtClient.detach();
			}
		} catch (Exception e) {
		}
		mgmtClient = null;
		super.afterOneTest();
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
        URLParameterString inspectResponse = mgmtClient.inspectCache(TestConstants.sesServiceName1, "700");
        Assert.assertEquals(inspectResponse.getValue("return"), "notfound");
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
        URLParameterString inspectResponse = mgmtClient.inspectCache(TestConstants.sesServiceName1, "700");
        Assert.assertEquals(inspectResponse.getValue("return"), "success");
        Assert.assertEquals(inspectResponse.getValue("cacheState"), CACHE_STATE.LOADED.toString());
        Assert.assertEquals(inspectResponse.getValue("cacheId"), "700");
        
		request.setData("cacheFor2Hour");
		response = sessionService1.execute(request);
		Assert.assertEquals("cacheFor1Hour", response.getData());
		// inspect cache entry 
        inspectResponse = mgmtClient.inspectCache(TestConstants.sesServiceName1, "700");
        Assert.assertEquals(inspectResponse.getValue("return"), "success");
        Assert.assertEquals(inspectResponse.getValue("cacheState"), CACHE_STATE.LOADED.toString());
        Assert.assertEquals(inspectResponse.getValue("cacheId"), "700");
        
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
	 * Description: exchange large message with cacheId, server reply with cacheExpirationTime<br>
	 * Expectation: get large message from cache
	 */
	@Test
	public void t07_cacheLargeMessage() throws Exception {
		SCMessage request = new SCMessage();
		request.setCompressed(false);
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
        URLParameterString inspectResponse = mgmtClient.inspectCache(TestConstants.sesServiceName1, "700");
        Assert.assertEquals(inspectResponse.getValue("return"), "success");
        Assert.assertEquals(inspectResponse.getValue("cacheState"), CACHE_STATE.LOADED.toString());
        Assert.assertEquals(inspectResponse.getValue("cacheId"), "700");
        Assert.assertEquals(inspectResponse.getValue("cacheSize"), "2");

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
		Assert.assertEquals("700/1", response.getCacheId());

		// session service two gets message with cacheId 600
		request.setData(TestConstants.pangram);
		request.setCacheId("600");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService2.execute(request);
		Assert.assertEquals("cacheFor2Hour", response.getData());
		Assert.assertEquals("600/1", response.getCacheId());

		// session service one gets message with cacheId 600
		request.setData(TestConstants.pangram);
		request.setCacheId("600");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService1.execute(request);
		Assert.assertEquals("cacheFor2Hour", response.getData());
		Assert.assertEquals("600/1", response.getCacheId());

		// session service two gets message with cacheId 700
		request.setData(TestConstants.pangram);
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService2.execute(request);
		Assert.assertEquals("cacheFor1Hour", response.getData());
		Assert.assertEquals("700/1", response.getCacheId());

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
		Assert.assertEquals("700/1", response.getCacheId());

		// session service two gets message with cacheId 700
		request.setData(TestConstants.pangram);
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService2.execute(request);
		Assert.assertEquals("refreshCache700", response.getData());
		Assert.assertEquals("700/1", response.getCacheId());

		sessionService2.deleteSession();
		sessionService1.deleteSession();
	}

	/**
	 * Description: sessionService exchange a large message, sessionService1 exchange a message, with same cacheId's on two service
	 * instances, sessionService two gets message from cache<br>
	 * Expectation: get messages from cache
	 */
	@Test
	public void t12_2ClientsLargeMessage() throws Exception {
		String largeMessage = TestUtil.getLargeString();
		SCMessage request = new SCMessage();
		request.setCompressed(false);
		SCMessage response = null;
		// session service starts storing large message with cacheId 700
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setData("cacheLargeMessageFor1Hour");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionService1.send(request);

		Thread.sleep(1000);
		
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
        URLParameterString inspectResponse = mgmtClient.inspectCache(TestConstants.sesServiceName1, "700");
        Assert.assertEquals(inspectResponse.getValue("return"), "notfound");
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
		} catch(Exception e) {			
		}
        // wait other 10s, SC server timeout should expire and cache composite removed from cache
        Thread.sleep(10);
        inspectResponse = mgmtClient.inspectCache(TestConstants.sesServiceName1, "700");
        Assert.assertEquals(inspectResponse.getValue("return"), "notfound");
		sessionService1.deleteSession();
	}

	/**
	 * Description: exchange message with cacheId, server replies with different cacheId<br>
	 * Save server reply in cache under servers cache id and not under clients cache id.<br/>
	 * Expectation: server cache replies different cache id
	 */
	@Test
	public void t20_cacheServerRepliesOtherCacheId() throws Exception {
		// inspect cache		
        URLParameterString inspectResponse = mgmtClient.inspectCache(TestConstants.sesServiceName1, "700");
        Assert.assertEquals(inspectResponse.getValue("return"), "notfound");
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
        inspectResponse = mgmtClient.inspectCache(TestConstants.sesServiceName1, "700");
        Assert.assertEquals(inspectResponse.getValue("return"), "notfound");
	    // inspect cache with cacheId 800
        inspectResponse = mgmtClient.inspectCache(TestConstants.sesServiceName1, "800");
        Assert.assertEquals(inspectResponse.getValue("return"), "success");
		sessionService1.deleteSession();
	}

}
