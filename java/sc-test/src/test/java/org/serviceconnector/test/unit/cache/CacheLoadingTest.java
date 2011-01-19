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

import java.util.Date;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestCacheConfiguration;
import org.serviceconnector.cache.Cache;
import org.serviceconnector.cache.CacheComposite;
import org.serviceconnector.cache.CacheException;
import org.serviceconnector.cache.CacheId;
import org.serviceconnector.cache.CacheManager;
import org.serviceconnector.cache.CacheMessage;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPPart;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.SessionService;
import org.serviceconnector.test.unit.SuperUnitTest;
import org.serviceconnector.util.DateTimeUtility;
import org.serviceconnector.util.TimeMillis;

/**
 * The class CacheExpirationTest tests the cache expiration functionality.
 * 
 * @author ds
 */
public class CacheLoadingTest extends SuperUnitTest {

	private CacheManager cacheManager;

	/**
	 * Run before each test and setup the dummy environment (services and cache manager). <br/>
	 * 
	 * @throws Exception
	 * 
	 */
	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		AppContext.setSCEnvironment(true);
		ServiceRegistry serviceRegistry = AppContext.getServiceRegistry();
		Service service = new SessionService("dummy");
		serviceRegistry.addService("dummy", service);
		service = new SessionService("dummy1");
		serviceRegistry.addService("dummy1", service);
		service = new SessionService("dummy2");
		serviceRegistry.addService("dummy2", service);
		cacheManager = new CacheManager();
		cacheManager.load(new TestCacheConfiguration());
	}

	@After
	public void afterTest() {
		cacheManager.destroy();
		super.afterOneTest();
	}

	/**
	 * Description: Simple cache loading test<br>
	 * Write a message into the cache using a dummy id and nr<br>.
	 * Verify if cache has been loaded after put message.<br>
	 * 
	 * Expectation: passes
	 */
	@Test
	public void t01_cacheLoadingTest() throws CacheException {
		Cache scmpCache = this.cacheManager.getCache("dummy");
		String stringWrite = "this is the buffer";
		byte[] buffer = stringWrite.getBytes();
		SCMPMessage scmpMessageWrite = new SCMPMessage(buffer);
		scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, 1233);
		scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id");
		Date now = new Date();
		Date expirationDate = DateTimeUtility.getIncrementTimeInMillis(now, TimeMillis.HOUR.getMillis());
		scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_EXPIRATION_DATETIME,
				DateTimeUtility.getDateTimeAsString(expirationDate));
		CacheId msgCacheId = scmpCache.putMessage(scmpMessageWrite);
		Assert.assertEquals(true, scmpCache.isLoaded("dummy.cache.id"));						
	}

	/**
	 * Description: Large (part) cache loading  test<br>
	 * Write a large message into the cache using a dummy id and nr.<br>
	 * Check for each written part if the cache has loading state.<br>
	 * Check if cache is loaded when the last message has been put to the cache.<br>
	 * 
	 * Expectation: passes
	 */
	@Test
	public void t02_partCacheLoadingTest() throws CacheException {
		Cache scmpCache = this.cacheManager.getCache("dummy");
		String stringWrite = "this is the part buffer nr = ";
		for (int i = 1; i <= 10; i++) {
			String partWrite = stringWrite + i;
			byte[] buffer = partWrite.getBytes();
			SCMPMessage scmpMessageWrite = null;
			if (i < 10) {
			   scmpMessageWrite = new SCMPPart();
			} else {
			   scmpMessageWrite = new SCMPMessage();			
			}
			scmpMessageWrite.setBody(buffer);
			scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, String.valueOf(1233 + i));
			scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id");
			Date now = new Date();
			Date expirationDate = DateTimeUtility.getIncrementTimeInMillis(now, TimeMillis.HOUR.getMillis());
			scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_EXPIRATION_DATETIME,
					DateTimeUtility.getDateTimeAsString(expirationDate));			
			scmpCache.putMessage(scmpMessageWrite);
			if (i < 10) {
			   // check if cache is in loading state
			   Assert.assertEquals(true, scmpCache.isLoading("dummy.cache.id"));				
			}
		}
		// check if cache has been loaded
		Assert.assertEquals(true, scmpCache.isLoaded("dummy.cache.id"));						
	}

}
