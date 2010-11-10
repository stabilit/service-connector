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

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.cache.SCMPCache;
import org.serviceconnector.scmp.cache.SCMPCacheException;
import org.serviceconnector.scmp.cache.SCMPCacheManager;
import org.serviceconnector.scmp.cache.SCMPCacheMessage;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.SessionService;

/**
 * The Class SCMPCacheTest.
 * 
 * @author ds
 */
public class SCMPCacheTestCase {

	private SCMPCacheManager scmpCacheManager;
	/**
	 * Scmp cache write test.
	 * @throws Exception 
	 * 
	 * @throws SCMPCacheException
	 *             the sCMP cache exception
	 */

	@Before
	public void beforeTest() throws Exception {		
       ServiceRegistry serviceRegistry = AppContext.getCurrentContext().getServiceRegistry();
	   Service service = new SessionService("dummy");
       serviceRegistry.addService("dummy", service);
	   service = new SessionService("dummy1");
       serviceRegistry.addService("dummy1", service);
	   service = new SessionService("dummy2");
       serviceRegistry.addService("dummy2", service);
	   scmpCacheManager = new SCMPCacheManager();
	   scmpCacheManager.initialize(null);
	}

	@After
	public void afterTest() {
		scmpCacheManager.destroy();
	}

	@Test
	public void simpleSCMPCacheWriteTest() throws SCMPCacheException {
		SCMPCache scmpCache = this.scmpCacheManager.getCache("dummy");
		String stringWrite = "this is the buffer";
		byte[] buffer = stringWrite.getBytes();
		SCMPMessage scmpMessageWrite = new SCMPMessage(buffer);
		
		scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, 1233);
		scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id");
		scmpCache.putSCMP(scmpMessageWrite);
		SCMPMessage scmpMessageRead = new SCMPMessage();
		scmpMessageRead.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, 1233);
		scmpMessageRead.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id");
		SCMPCacheMessage cacheMessage = scmpCache.getSCMP(scmpMessageRead);
		byte[] bufferRead = (byte[]) cacheMessage.getBody();
		String stringRead = new String(bufferRead);
		Assert.assertEquals(stringWrite, stringRead);
	}

	@Test
	public void duplicateSCMPCacheWriteTest() throws SCMPCacheException {
		SCMPCache scmpCache1 = this.scmpCacheManager.getCache("dummy1");
		SCMPCache scmpCache2 = this.scmpCacheManager.getCache("dummy2");
		String stringWrite = "this is the buffer";
		byte[] buffer = stringWrite.getBytes();
		SCMPMessage scmpMessageWrite = new SCMPMessage(buffer);
		
		scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, 1233);
		scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id");
		scmpCache1.putSCMP(scmpMessageWrite);
		scmpCache2.putSCMP(scmpMessageWrite);
		SCMPMessage scmpMessageRead = new SCMPMessage();
		scmpMessageRead.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, 1233);
		scmpMessageRead.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id");
		SCMPCacheMessage cacheMessage1 = scmpCache1.getSCMP(scmpMessageRead);
		byte[] bufferRead1 = (byte[]) cacheMessage1.getBody();
		String stringRead1 = new String(bufferRead1);
		Assert.assertEquals(stringWrite, stringRead1);
		SCMPCacheMessage cacheMessage2 = scmpCache2.getSCMP(scmpMessageRead);
		byte[] bufferRead2 = (byte[]) cacheMessage2.getBody();
		String stringRead2 = new String(bufferRead2);
		Assert.assertEquals(stringWrite, stringRead2);
	}

}
