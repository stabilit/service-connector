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
package org.serviceconnector.test.sc.scmp.cache;

import java.util.Iterator;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.cache.Cache;
import org.serviceconnector.cache.CacheComposite;
import org.serviceconnector.cache.CacheException;
import org.serviceconnector.cache.CacheManager;
import org.serviceconnector.cache.CacheMessage;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.scmp.SCMPCacheId;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.SessionService;

/**
 * The Class SCMPCacheTest.
 * 
 * @author ds
 */
public class SCMPCacheTestCase {

	private CacheManager cacheManager;
	/**
	 * Scmp cache write test.
	 * @throws Exception 
	 * 
	 * @throws CacheException
	 *             the sCMP cache exception
	 */

	@Before
	public void beforeTest() throws Exception {		
       ServiceRegistry serviceRegistry = AppContext.getServiceRegistry();
	   Service service = new SessionService("dummy");
       serviceRegistry.addService("dummy", service);
	   service = new SessionService("dummy1");
       serviceRegistry.addService("dummy1", service);
	   service = new SessionService("dummy2");
       serviceRegistry.addService("dummy2", service);
	   cacheManager = new CacheManager();
	   cacheManager.initialize();
	}

	@After
	public void afterTest() {
		cacheManager.destroy();
	}

	@Test
	public void testSimpleCacheWrite() throws CacheException {
		Cache scmpCache = this.cacheManager.getCache("dummy");
		String stringWrite = "this is the buffer";
		byte[] buffer = stringWrite.getBytes();
		SCMPMessage scmpMessageWrite = new SCMPMessage(buffer);
		
		scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, 1233);
		scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id");
		SCMPCacheId msgCacheId = scmpCache.putMessage(scmpMessageWrite);
		SCMPMessage scmpMessageRead = new SCMPMessage();
		scmpMessageRead.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, 1233);
		scmpMessageRead.setHeader(SCMPHeaderAttributeKey.CACHE_ID, msgCacheId.getFullCacheId());
		CacheMessage cacheMessage = scmpCache.getMessage(scmpMessageRead);
		byte[] bufferRead = (byte[]) cacheMessage.getBody();
		String stringRead = new String(bufferRead);
		Assert.assertEquals(stringWrite, stringRead);
		// get composite cache of given id
		CacheComposite cacheComposite = scmpCache.getComposite(msgCacheId.getCacheId());
		int size = cacheComposite.getSize();
		Assert.assertEquals(1, size);
	}

	@Test
	public void testDuplicateCacheWrite() throws CacheException {
		Cache scmpCache1 = this.cacheManager.getCache("dummy1");
		Cache scmpCache2 = this.cacheManager.getCache("dummy2");
		String stringWrite = "this is the buffer";
		byte[] buffer = stringWrite.getBytes();
		SCMPMessage scmpMessageWrite = new SCMPMessage(buffer);
		
		scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, 1233);
		scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id");
		SCMPCacheId msgCacheId1 = scmpCache1.putMessage(scmpMessageWrite);
		SCMPCacheId msgCacheId2 = scmpCache2.putMessage(scmpMessageWrite);
		SCMPMessage scmpMessageRead = new SCMPMessage();
		scmpMessageRead.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, 1233);
		scmpMessageRead.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id");
		CacheMessage cacheMessage1 = scmpCache1.getMessage(msgCacheId1);
		byte[] bufferRead1 = (byte[]) cacheMessage1.getBody();
		String stringRead1 = new String(bufferRead1);
		Assert.assertEquals(stringWrite, stringRead1);
		CacheMessage cacheMessage2 = scmpCache2.getMessage(msgCacheId2);
		byte[] bufferRead2 = (byte[]) cacheMessage2.getBody();
		String stringRead2 = new String(bufferRead2);
		Assert.assertEquals(stringWrite, stringRead2);
	}

	@Test
	public void testPartSCMPCacheWrite() throws CacheException {
		Cache scmpCache = this.cacheManager.getCache("dummy");
		String stringWrite = "this is the part buffer nr = ";
		for (int i = 1; i <= 10; i++) {
			String partWrite = stringWrite + i;		    
		    byte[] buffer = partWrite.getBytes();
		    SCMPMessage scmpMessageWrite = new SCMPMessage(buffer);		
		    scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, String.valueOf(1233+i));
		    scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id");
		    scmpCache.putMessage(scmpMessageWrite);
		}
		// get composite cache of given id
		CacheComposite cacheComposite = scmpCache.getComposite("dummy.cache.id");
		int size = cacheComposite.getSize();
		Assert.assertEquals(10, size);
		for (int i = 1; i <= 11; i++) {
			String partWrite = stringWrite + i;		    
		    SCMPMessage scmpMessageRead = new SCMPMessage();
		    scmpMessageRead.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id/" + i);
		    CacheMessage cacheMessage = scmpCache.getMessage(scmpMessageRead);
		    if (cacheMessage == null) {
		    	if (i < 11) {
		    	   Assert.fail("cacheMessage is null but should not");
		    	   continue;
		    	}
		    	break;
		    }
		    byte[] bufferRead = (byte[]) cacheMessage.getBody();
		    String stringRead = new String(bufferRead);
		    Assert.assertEquals(partWrite, stringRead);
		}
	}

	@Test
	public void testPartSCMPCacheWriteUsingIterator() throws CacheException {
		Cache scmpCache = this.cacheManager.getCache("dummy");
		String stringWrite = "this is the part buffer nr = ";
		for (int i = 1; i <= 10; i++) {
			String partWrite = stringWrite + i;		    
		    byte[] buffer = partWrite.getBytes();
		    SCMPMessage scmpMessageWrite = new SCMPMessage(buffer);		
		    scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, String.valueOf(1233+i));
		    scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id");
		    scmpCache.putMessage(scmpMessageWrite);
		}
		// get composite cache of given id
		CacheComposite cacheComposite = scmpCache.getComposite("dummy.cache.id");
		int size = cacheComposite.getSize();
		Assert.assertEquals(10, size);
		Iterator<CacheMessage> cacheIterator = scmpCache.iterator("dummy.cache.id");
		int index = 0;
		while(cacheIterator.hasNext()) {
			index++;
			String partWrite = stringWrite + index;		    
		    byte[] buffer = partWrite.getBytes();
		    CacheMessage cacheMessage = cacheIterator.next();
		    if (cacheMessage == null) {
		    	if (index < 11) {
		    	   Assert.fail("cacheMessage is null but should not");
		    	   continue;
		    	}
		    	break;
		    }
		    byte[] bufferRead = (byte[]) cacheMessage.getBody();
		    String stringRead = new String(bufferRead);
		    Assert.assertEquals(partWrite, stringRead);
		}
	}

}
