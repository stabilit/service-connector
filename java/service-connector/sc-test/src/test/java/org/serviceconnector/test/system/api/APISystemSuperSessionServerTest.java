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
package org.serviceconnector.test.system.api;

import org.junit.After;
import org.junit.Before;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.test.system.SystemSuperTest;

public class APISystemSuperSessionServerTest extends SystemSuperTest {

	protected SCServer server;
	protected ProcessCtx sesClnCtx;

	@Override
	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
	}

	@Override
	@After
	public void afterOneTest() throws Exception {
		super.afterOneTest();
	}

}
