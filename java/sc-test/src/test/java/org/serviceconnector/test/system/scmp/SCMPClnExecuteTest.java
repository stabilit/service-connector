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
package org.serviceconnector.test.system.scmp;

import java.util.ArrayList;
import java.util.List;

import org.serviceconnector.TestConstants;
import org.serviceconnector.ctrl.util.ServerDefinition;
import org.serviceconnector.ctrl.util.ServiceConnectorDefinition;
import org.serviceconnector.test.system.SystemSuperTest;

/**
 * @author JTraber
 */
public class SCMPClnExecuteTest extends org.serviceconnector.test.system.scmp.casc.SCMPClnExecuteTest {

	public SCMPClnExecuteTest() {
		SCMPClnExecuteTest.setUpServiceConnectorAndServer();
	}

	public static void setUpServiceConnectorAndServer() {
		// SC definitions
		List<ServiceConnectorDefinition> sc0Defs = new ArrayList<ServiceConnectorDefinition>();
		ServiceConnectorDefinition sc0Def = new ServiceConnectorDefinition(TestConstants.SC0, TestConstants.SC0Properties,
				TestConstants.log4jSC0Properties);
		sc0Defs.add(sc0Def);

		// server definitions
		List<ServerDefinition> srvToSC0Defs = new ArrayList<ServerDefinition>();

		ServerDefinition srvToSC0Def = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_SESSION,
				TestConstants.log4jSrvProperties, TestConstants.sesServerName1, TestConstants.PORT_SES_SRV_TCP,
				TestConstants.PORT_SC_TCP, 1, 1, TestConstants.sesServiceName1);
		srvToSC0Defs.add(srvToSC0Def);

		SystemSuperTest.scDefs = sc0Defs;
		SCMPClnExecuteTest.srvDefs = srvToSC0Defs;
	}
}