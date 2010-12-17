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
package org.serviceconnector.test.unit.cache;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.cache.Cache;
import org.serviceconnector.cache.CacheException;
import org.serviceconnector.cache.CacheId;
import org.serviceconnector.cache.CacheManager;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.SessionService;

/**
 * The Class SCMPCacheTest.
 * 
 * @author ds
 */
public class CacheStatisticsTest {

	private CacheManager cacheManager;

	/**
	 * Scmp cache write test.
	 * 
	 * @throws Exception
	 * 
	 * @throws CacheException
	 *             the sCMP cache exception
	 */

	@Before
	public void beforeTest() throws Exception {
		AppContext.setSCEnvironment(true);
		ServiceRegistry serviceRegistry = AppContext.getServiceRegistry();
		Service service = new SessionService("dummy");
		serviceRegistry.addService("dummy", service);
		cacheManager = new CacheManager();
		cacheManager.initialize();
	}

	@After
	public void afterTest() {
		cacheManager.destroy();
	}

	@Test
	public void testElementSize() throws CacheException {
		Cache scmpCache = this.cacheManager.getCache("dummy");
		int elementSize = scmpCache.getElementSize();
		Assert.assertEquals(0, elementSize);
		long diskStoreSize = scmpCache.getDiskStoreSize();
		long memoryStoreSize = scmpCache.getMemoryStoreSize();
		String stringWrite = "this is the buffer";
		byte[] buffer = stringWrite.getBytes();
		SCMPMessage scmpMessageWrite = new SCMPMessage(buffer);

		scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, 1233);
		scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id");
		CacheId msgCacheId = scmpCache.putMessage(scmpMessageWrite);
		SCMPMessage scmpMessageRead = new SCMPMessage();
		scmpMessageRead.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, 1233);
		scmpMessageRead.setHeader(SCMPHeaderAttributeKey.CACHE_ID, msgCacheId.getFullCacheId());
		elementSize = scmpCache.getElementSize();
		Assert.assertEquals(3, elementSize);
		diskStoreSize = scmpCache.getDiskStoreSize();
		memoryStoreSize = scmpCache.getMemoryStoreSize();
	}

	@Test
	public void testPartElementSize() throws CacheException {
		Cache scmpCache = this.cacheManager.getCache("dummy");
		int elementSize = scmpCache.getElementSize();
		Assert.assertEquals(0, elementSize);
		String stringWrite = "this is the part buffer nr = ";
		for (int i = 1; i <= 10; i++) {
			String partWrite = stringWrite + i;
			byte[] buffer = partWrite.getBytes();
			SCMPMessage scmpMessageWrite = new SCMPMessage(buffer);
			scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, String.valueOf(1233 + i));
			scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id");
			scmpCache.putMessage(scmpMessageWrite);
		}
		elementSize = scmpCache.getElementSize();
		Assert.assertEquals(12, elementSize);
	}

	@Test
	public void testLargePartElementSize() throws CacheException {
		Cache scmpCache = this.cacheManager.getCache("dummy");
		int elementSize = scmpCache.getElementSize();
		Assert.assertEquals(0, elementSize);
		String stringWrite = "this is the part buffer nr = ";
		for (int i = 1; i <= 10000; i++) {
			String partWrite = stringWrite + i;
			byte[] buffer = partWrite.getBytes();
			SCMPMessage scmpMessageWrite = new SCMPMessage(buffer);
			scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, String.valueOf(1233 + i));
			scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id");
			scmpCache.putMessage(scmpMessageWrite);
		}
		elementSize = scmpCache.getElementSize();
		Assert.assertEquals(10002, elementSize);
		long diskStoreSize = scmpCache.getDiskStoreSize();
		long memoryStoreSize = scmpCache.getMemoryStoreSize();
		long memorySizeInBytes = scmpCache.getSizeInBytes();
	}
}
