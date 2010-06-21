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
package com.stabilit.scm.unit.cln.api;

import org.junit.Before;
import org.junit.Test;

import com.stabilit.scm.cln.service.IClientServiceConnector;
import com.stabilit.scm.cln.service.ISCSubscription;
import com.stabilit.scm.common.service.ServiceConnectorFactory;
import com.stabilit.scm.unit.test.SetupTestCases;

public class ClnAPIPublishTestCase {

	@Before
	public void setUp() {
		SetupTestCases.setupAll();
	}

	@Test
	public void testClnAPI() throws Exception {
		IClientServiceConnector sc = null;
		try {
			sc = ServiceConnectorFactory.newClientInstance("localhost", 8080);
			sc.setAttribute("keepAliveInterval", 60);
			sc.setAttribute("keepAliveTimeout", 10);
			sc.setAttribute("compression", false);

			// connects to SC, starts observing connection
			sc.connect();

			/* TODO (trn) Bitte umbauen auf:
			
			ISCService publServiceA = sc.newPublishingService("broadcast");
			SCExampleMessageHandler messageHandler = new SCExampleMessageHandler();
			String mask = "ABC-------"; // must not contain % sign
			publServiceA.subscribe(messageHandler,mask);
			
			mask = "AAA-------";
			publServiceA.changeSubscription(mask);

			publServiceA.unsubscribe();
			*/
			
			String mask = "ABC-------"; // must not contain % sign
			SCExampleMessageHandler messageHandler = new SCExampleMessageHandler();
			ISCSubscription subscriptionA = sc.newSubscription("publish", messageHandler, mask);

			subscriptionA.changeSubscription(mask);
			// deletes the session
			subscriptionA.unsubscribe();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// disconnects from SC
				sc.disconnect();
			} catch (Exception e) {
				sc = null;
			}
		}
	}
}