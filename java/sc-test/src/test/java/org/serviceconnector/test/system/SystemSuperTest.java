/*
 * Copyright © 2010 STABILIT Informatik AG, Switzerland *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License"); *
 * you may not use this file except in compliance with the License. *
 * You may obtain a copy of the License at *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0 *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS, *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and *
 * limitations under the License. *
 */
package org.serviceconnector.test.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.serviceconnector.TestConstants;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.ctrl.util.ServerDefinition;
import org.serviceconnector.ctrl.util.ServiceConnectorDefinition;
import org.serviceconnector.log.Loggers;

public class SystemSuperTest {

	/** The Constant testLogger. */
	protected static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	protected static ProcessesController ctrl;
	protected static Map<String, ProcessCtx> scCtxs;
	protected int threadCount = 0;
	protected static List<ServiceConnectorDefinition> scDefs;
	protected static int cascadingLevel = 0;

	protected static List<ServerDefinition> srvDefs;
	protected static Map<String, ProcessCtx> srvCtxs;

	public SystemSuperTest() {
		SystemSuperTest.setUpServiceConnectorAndServer();
	}

	@Rule
	public TestName name = new TestName();

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
	}

	@Before
	public void beforeOneTest() throws Exception {
		testLogger.info(">> " + name.getMethodName() + " <<");
		threadCount = Thread.activeCount();
		scCtxs = ctrl.startSCEnvironment(scDefs);
		srvCtxs = ctrl.startServerEnvironment(srvDefs);
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			ctrl.stopServerEnvironment(srvCtxs);
		} catch (Exception e) {
		}
		srvCtxs = null;
		try {
			ctrl.stopSCEnvironment(scCtxs);
		} catch (Exception e) {
		}
		scCtxs = null;
		testLogger.info("Number of threads :" + Thread.activeCount() + " created :" + (Thread.activeCount() - threadCount));
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		ctrl = null;
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
				TestConstants.PORT_SC0_TCP, 100, 10, TestConstants.sesServiceName1);
		srvToSC0Defs.add(srvToSC0Def);

		SystemSuperTest.scDefs = sc0Defs;
		SystemSuperTest.srvDefs = srvToSC0Defs;
		cascadingLevel = 0;
	}

	public static void setUp1CascadedServiceConnectorAndServer() {
		List<ServiceConnectorDefinition> scDefs = new ArrayList<ServiceConnectorDefinition>();
		ServiceConnectorDefinition sc0Def = new ServiceConnectorDefinition(TestConstants.SC0, TestConstants.SC0Properties,
				TestConstants.log4jSC0Properties);
		scDefs.add(sc0Def);
		ServiceConnectorDefinition sc1 = new ServiceConnectorDefinition(TestConstants.SC1, TestConstants.SC1Properties,
				TestConstants.log4jSC1Properties);
		scDefs.add(sc1);

		// server definitions
		List<ServerDefinition> srvToSC0Defs = new ArrayList<ServerDefinition>();
		ServerDefinition srvToSC0Def = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_SESSION,
				TestConstants.log4jSrvProperties, TestConstants.sesServerName1, TestConstants.PORT_SES_SRV_TCP,
				TestConstants.PORT_SC0_TCP, 100, 10, TestConstants.sesServiceName1);
		srvToSC0Defs.add(srvToSC0Def);

		SystemSuperTest.scDefs = scDefs;
		SystemSuperTest.srvDefs = srvToSC0Defs;
		cascadingLevel = 1;
	}

	public static void setUp2CascadedServiceConnectorAndServer() {
		List<ServiceConnectorDefinition> scDefs = new ArrayList<ServiceConnectorDefinition>();
		ServiceConnectorDefinition sc0Def = new ServiceConnectorDefinition(TestConstants.SC0, TestConstants.SC0Properties,
				TestConstants.log4jSC0Properties);
		scDefs.add(sc0Def);
		ServiceConnectorDefinition sc1 = new ServiceConnectorDefinition(TestConstants.SC1, TestConstants.SC1Properties,
				TestConstants.log4jSC1Properties);
		scDefs.add(sc1);
		ServiceConnectorDefinition sc2 = new ServiceConnectorDefinition(TestConstants.SC2, TestConstants.SC2Properties,
				TestConstants.log4jSC2Properties);
		scDefs.add(sc2);

		// server definitions
		List<ServerDefinition> srvToSC0Defs = new ArrayList<ServerDefinition>();
		ServerDefinition srvToSC0Def = new ServerDefinition(TestConstants.COMMUNICATOR_TYPE_SESSION,
				TestConstants.log4jSrvProperties, TestConstants.sesServerName1, TestConstants.PORT_SES_SRV_TCP,
				TestConstants.PORT_SC0_TCP, 100, 10, TestConstants.sesServiceName1);
		srvToSC0Defs.add(srvToSC0Def);

		SystemSuperTest.scDefs = scDefs;
		SystemSuperTest.srvDefs = srvToSC0Defs;
		cascadingLevel = 2;
	}
}
