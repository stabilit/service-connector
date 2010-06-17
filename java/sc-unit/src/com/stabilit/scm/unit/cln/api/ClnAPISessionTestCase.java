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

import com.stabilit.scm.cln.service.ISCSession;
import com.stabilit.scm.cln.service.IClientServiceConnector;
import com.stabilit.scm.cln.service.SCPublishMessage;
import com.stabilit.scm.common.service.ServiceConnectorFactory;
import com.stabilit.scm.unit.test.SetupTestCases;

public class ClnAPISessionTestCase {

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

			// connects to SC, starts observing connection
			sc.connect();

			ISCSession dataSessionA = sc.newDataSession("simulation");
			dataSessionA.setMessageInfo("sessionInfo");
			dataSessionA.setSessionInfo("messageInfo");
			// creates a session
			dataSessionA.createSession();

			SCPublishMessage message = new SCPublishMessage();
			
			byte[] buffer = new byte[1024];
			message.setData(buffer);
			message.setCompression(false);
			
			SCPublishMessage resp = dataSessionA.execute(message);
			System.out.println(resp);

			// deletes the session
			dataSessionA.deleteSession();

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
