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
package com.stabilit.sc.unit.test.worse;

import org.junit.Test;

import com.stabilit.sc.unit.test.session.SuperSessionRegisterTestCase;

/**
 * @author JTraber
 */
public class WorseSCServerToServiceTestCase extends SuperSessionRegisterTestCase {

	/**
	 * @param fileName
	 */
	public WorseSCServerToServiceTestCase(String fileName) {
		super(fileName);
	}
//	
//	@Before
//	@Override
//	public void setup() {
//		try {
//			SetupTestCases.init();
//			CommandFactory.setCurrentCommandFactory(new UnitCommandFactory());
//			ServiceConnector.main(null);
//			Simulation.main(null);
//			config = new ClientConfig();
//			config.load(fileName);
//			ClientFactory clientFactory = new ClientFactory();
//			client = clientFactory.newInstance(config.getClientConfig());
//			client.connect(); // physical connect
//			clnConnectBefore();
//			registerServiceBefore();
//			clnCreateSessionBefore();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	@Test
	public void clnDataSCServerToServiceDisconnect() throws Exception {
	}

	private void tearDownSCServerToClient() throws Exception {
	}
}