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
	 * 
	 * @throws SCMPCacheException
	 *             the sCMP cache exception
	 */

	@Before
	public void beforeTest() {		
       ServiceRegistry serviceRegistry = AppContext.getCurrentContext().getServiceRegistry();
	   Service service = new SessionService("dummy");
       serviceRegistry.addService("dummy", service);
	   scmpCacheManager = new SCMPCacheManager();
	   scmpCacheManager.initialize();
	}

	@After
	public void afterTest() {

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

}
