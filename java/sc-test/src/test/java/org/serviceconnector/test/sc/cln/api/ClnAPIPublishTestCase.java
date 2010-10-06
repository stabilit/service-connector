/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
package org.serviceconnector.test.sc.cln.api;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.cln.IPublishService;
import org.serviceconnector.api.cln.ISCClient;
import org.serviceconnector.api.cln.IService;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.service.ISCMessageCallback;
import org.serviceconnector.test.sc.SetupTestCases;



public class ClnAPIPublishTestCase {

	private int publishedMessageCounter = 0;

	@Before
	public void setUp() {
		SetupTestCases.setupAll();
	}

	@Test
	public void testSubscribeUnsubscribe() throws Exception {
		ISCClient sc = null;
		IPublishService publishServiceA = null;
		try {
			sc = new SCClient();
			sc.setMaxConnections(100);

			// connects to SC, checks connection to SC
			sc.attach(TestConstants.HOST, TestConstants.PORT_HTTP);

			publishServiceA = sc.newPublishService("publish-simulation");
			ISCMessageCallback callback = new TestPublishCallback(publishServiceA);
			publishServiceA.subscribe("000012100012832102FADF-----------X-----------", "sessionInfo", 300, callback);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// disconnects from SC
				publishServiceA.unsubscribe();
				sc.detach();
			} catch (Throwable e) {
				sc = null;
			}
		}
	}

	@Test
	public void testSubscribePublishUnsubscribe() throws Exception {
		ISCClient sc = null;
		IPublishService publishServiceA = null;
		try {
			sc = new SCClient();
			sc.setMaxConnections(100);

			// connects to SC, checks connection to SC
			sc.attach(TestConstants.HOST, TestConstants.PORT_HTTP);

			publishServiceA = sc.newPublishService("publish-simulation");
			ISCMessageCallback callback = new TestPublishCallback(publishServiceA);
			publishServiceA.subscribe("AEC----", "sessionInfo", 300, callback);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// disconnects from SC
				publishServiceA.unsubscribe();
				sc.detach();
			} catch (Throwable e) {
				sc = null;
			}
		}
	}

	class TestPublishCallback extends SCMessageCallback {

		public TestPublishCallback(IService service) {
			super(service);
		}

		@Override
		public void callback(SCMessage reply) {
			publishedMessageCounter++;
			System.out.println("ClnAPIPublishSubscribeTestCase.TestPublishCallback.callback() counter = "
					+ publishedMessageCounter);
		}

		@Override
		public void callback(Exception ex) {
			Assert.fail(ex.toString());
		}
	}
}