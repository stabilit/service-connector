/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package org.serviceconnector.cln;

import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.SCService;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCPublishService;

public class SCPublishClientExample {

	private int publishedMessageCounter = 0;

	public static void main(String[] args) {
		SCPublishClientExample test = new SCPublishClientExample();
		test.runExample();
	}

	public void runExample() {
		SCClient sc = null;
		SCPublishService publishServiceA = null;
		try {
			sc = new SCClient("localhost", 7000);
			sc.setMaxConnections(100);

			// connects to SC, checks connection to SC
			sc.attach();

			publishServiceA = sc.newPublishService("publish-1");
			SCMessageCallback callback = new TestPublishCallback(publishServiceA);
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask("000012100012832102FADF-----------X-----------");
			subscibeMessage.setSessionInfo("sessionInfo");
			publishServiceA.subscribe(subscibeMessage, callback);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// disconnects from SC
				publishServiceA.unsubscribe();
				sc.detach();
			} catch (Exception e) {
				sc = null;
			}
		}
	}

	class TestPublishCallback extends SCMessageCallback {

		public TestPublishCallback(SCService service) {
			super(service);
		}

		@Override
		public void receive(SCMessage reply) {
			publishedMessageCounter++;
			System.out.println("ClnAPIPublishSubscribeTestCase.TestPublishCallback.callback() counter = "
					+ publishedMessageCounter);
		}

		@Override
		public void receive(Exception ex) {
			ex.printStackTrace();
		}
	}
}