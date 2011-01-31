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

import org.junit.After;
import org.junit.Before;
import org.serviceconnector.cache.CacheManager;
import org.serviceconnector.conf.CacheConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.SessionService;
import org.serviceconnector.test.unit.SuperUnitTest;

/**
 * The Class CacheTest tests the core cache functionality.
 * 
 * @author ds
 */
public class CacheSuperUnitTest extends SuperUnitTest {

	public static final String CACHE_TEST_DEFAULT_DISK_PATH = "cache/unit";
	
	protected CacheManager cacheManager;

	/**
	 * Run before each test and setup the dummy environment (services and cache manager)<br/>
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
		CacheConfiguration cacheConfiguration = new CacheConfiguration() {
			{
				this.diskPath = CACHE_TEST_DEFAULT_DISK_PATH;
			}
		};
		cacheManager.load(cacheConfiguration);
	}

	/**
	 * Run after each test, destroy cache manager<br/>
	 */
	@After
	public void afterOneTest() {
		cacheManager.destroy();
		AppContext.setSCEnvironment(false);
	}

}
