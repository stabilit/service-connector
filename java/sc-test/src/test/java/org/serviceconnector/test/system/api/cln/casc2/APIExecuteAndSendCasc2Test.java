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
package org.serviceconnector.test.system.api.cln.casc2;

import org.junit.Before;
import org.serviceconnector.test.system.api.cln.casc1.APIExecuteAndSendCasc1Test;

/**
 * The Class APIExecuteAndSendCasc2Test.
 */
public class APIExecuteAndSendCasc2Test extends APIExecuteAndSendCasc1Test {

	/**
	 * Instantiates a new aPI execute and send casc2 test.
	 */
	public APIExecuteAndSendCasc2Test() {
		APIExecuteAndSendCasc2Test.setUp2CascadedServiceConnectorAndServer();
	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.test.system.api.cln.casc1.APIExecuteAndSendCasc1Test#beforeOneTest()
	 */
	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		this.setUpClientToSC2();
		client.attach();
	}
}
