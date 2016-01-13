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
package org.serviceconnector.test.system.api.cln;

import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.test.system.SystemSuperTest;
import org.serviceconnector.test.system.api.cln.casc1.APIExecuteCacheCasc1Test;

public class APIExecuteCacheTest extends APIExecuteCacheCasc1Test {
	
	public APIExecuteCacheTest() {
		SystemSuperTest.setUpServiceConnectorAndServer();
	}
	
	@Test
	public void t25_cacheAMessageClearCacheAndGetMessage() throws Exception {
		SCMessage request = new SCMessage();
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		// request expired server message, cache should still be empty
		request.setData("cacheFor1Hour");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		response = sessionService1.execute(request);
		Assert.assertEquals("cacheFor1Hour", response.getData());
		client.clearCache();

		request.setData("blabla");
		response = sessionService1.execute(request);
		Assert.assertEquals("blabla", response.getData());
		sessionService1.deleteSession();
	}
}
