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
package org.serviceconnector.test.system.scmp.casc2;

import java.util.ArrayList;
import java.util.List;

import org.serviceconnector.TestConstants;
import org.serviceconnector.ctrl.util.ServerDefinition;
import org.serviceconnector.ctrl.util.ServiceConnectorDefinition;
import org.serviceconnector.test.system.SystemSuperTest;
import org.serviceconnector.test.system.scmp.casc1.SCMPClnChangeSubscriptionCasc1Test;

public class SCMPClnChangeSubscriptionCasc2Test extends SCMPClnChangeSubscriptionCasc1Test {

	public SCMPClnChangeSubscriptionCasc2Test() {
		SCMPClnChangeSubscriptionCasc2Test.setUp2CascadedServiceConnectorAndServer();
	}

	public static void setUp2CascadedServiceConnectorAndServer() {
		List<ServiceConnectorDefinition> scCascDefs = new ArrayList<ServiceConnectorDefinition>();
		ServiceConnectorDefinition sc0CascDef = new ServiceConnectorDefinition(TestConstants.SC0_CASC,
				TestConstants.SC0CASCProperties, TestConstants.log4jSC0CASCProperties);
		ServiceConnectorDefinition sc1CascDef = new ServiceConnectorDefinition(TestConstants.SC1_CASC,
				TestConstants.SC1CASC1Properties, TestConstants.log4jSC1CASCProperties);
		ServiceConnectorDefinition sc2CascDef = new ServiceConnectorDefinition(TestConstants.SC2_CASC,
				TestConstants.SC2CASC2Properties, TestConstants.log4jSC2CASCProperties);
		scCascDefs.add(sc0CascDef);
		scCascDefs.add(sc1CascDef);
		scCascDefs.add(sc2CascDef);

		List<ServerDefinition> srvToSC0CascDefs = new ArrayList<ServerDefinition>();
		ServerDefinition srvToSC0CascDef = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_PUBLISH,
				TestConstants.log4jSrvProperties, TestConstants.pubServerName1, TestConstants.PORT_PUB_SRV_TCP,
				TestConstants.PORT_SC0_CASC_TCP, 1, 1, TestConstants.pubServiceName1);
		srvToSC0CascDefs.add(srvToSC0CascDef);

		SystemSuperTest.scDefs = scCascDefs;
		SCMPClnChangeSubscriptionCasc2Test.srvDefs = srvToSC0CascDefs;
	}
}