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

import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.test.system.api.APISystemSuperSessionClientTest;

@SuppressWarnings("unused")
public class APIExecuteCacheTest extends APISystemSuperSessionClientTest {

	/**
	 * Description: exchange message with cacheId, server reply without cacheExpirationTime<br>
	 * Expectation: no caching of the message
	 */
	@Test
	public void t01_cache() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		request.setMessageInfo(TestConstants.cacheCmd);
		request.setData("cidNoCed");
		request.setCacheId("700");
		response = sessionService.execute(request);
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
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		request.setData("cacheForOneHour");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService.execute(request);
		request.setData("cacheForTwoHour");
		response = sessionService.execute(request);
		Assert.assertEquals("cacheForOneHour", response.getData());
		sessionService.deleteSession();
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
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		request.setData(TestConstants.pangram);
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService.execute(request);
		request.setData("cacheFor2Sec");
		// wait until cache message expires
		Thread.sleep(4010);
		response = sessionService.execute(request);
		Assert.assertEquals(TestConstants.pangram, response.getData());
		sessionService.deleteSession();
	}
	
	/**
	 * Description: exchange large message with cacheId, server reply with cacheExpirationTime<br>
	 * Expectation: get large message from cache
	 */
	@Test
	public void t05_cacheLargeMessage() throws Exception {
		SCMessage request = new SCMessage();
		request.setCompressed(false);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		String largeMessage = TestUtil.getLargeString();
		request.setData(largeMessage);
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService.execute(request);
		request.setData("cacheForOneHour");
		response = sessionService.execute(request);
		request.setCacheId("700");
		Assert.assertEquals(largeMessage, response.getData());
		sessionService.deleteSession();
	}
}
