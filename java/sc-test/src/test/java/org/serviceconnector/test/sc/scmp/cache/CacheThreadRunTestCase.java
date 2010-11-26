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
import org.serviceconnector.cache.CacheConfiguration;
import org.serviceconnector.cache.CacheException;
import org.serviceconnector.cache.CacheManager;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.SessionService;

/**
 * The Class SCMPCacheTest.
 * 
 * @author ds
 */
public class CacheThreadRunTestCase {

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
	}

	@After
	public void afterTest() {
	}

	@Test
	public void testInitDestroy() throws CacheException {
		AppContext.setSCEnvironment(true);
		ServiceRegistry serviceRegistry = AppContext.getServiceRegistry();
		Service service = new SessionService("dummy");
		serviceRegistry.addService("dummy", service);
		CacheManager cacheManager = new CacheManager();
		CacheConfiguration cacheConfiguration = new CacheConfiguration();
		cacheConfiguration.setExpirationThreadTimeoutSeconds(2);
		try {
			cacheManager.initialize(cacheConfiguration);
			Thread.sleep(5000);
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
		cacheManager.destroy();
	}

}
