/*
 * Copyright � 2010 STABILIT Informatik AG, Switzerland *
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
import org.serviceconnector.ctrl.util.ServiceConnectorDefinition;
import org.serviceconnector.log.Loggers;

public class SystemSuperTest {

	/** The Constant testLogger. */
	protected static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	protected static ProcessesController ctrl;
	protected static Map<String, ProcessCtx> scCtxs;
	protected int threadCount = 0;
	protected static List<ServiceConnectorDefinition> scDefs;

	@Rule
	public TestName name = new TestName();

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
		scDefs = new ArrayList<ServiceConnectorDefinition>();
		ServiceConnectorDefinition scDef = new ServiceConnectorDefinition(TestConstants.MAIN_SC, TestConstants.SCProperties,
				TestConstants.log4jSCProperties);
		scDefs.add(scDef);
	}

	@Before
	public void beforeOneTest() throws Exception {
		testLogger.info(">> " + name.getMethodName() + " <<");
		threadCount = Thread.activeCount();
		scCtxs = ctrl.startSCEnvironment(scDefs);
	}

	@After
	public void afterOneTest() throws Exception {
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

}
