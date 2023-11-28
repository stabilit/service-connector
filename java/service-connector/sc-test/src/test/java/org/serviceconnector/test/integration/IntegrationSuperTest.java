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
package org.serviceconnector.test.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.util.ResourceLeakDetector;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.serviceconnector.TestConstants;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.Loggers;

public class IntegrationSuperTest {

	/** The Constant testLogger. */
	protected static final Logger testLogger = LoggerFactory.getLogger(Loggers.TEST.getValue());

	protected static ProcessesController ctrl;
	protected static ProcessCtx scCtx;
	protected int threadCount = 0;

	@Rule
	public TestName name = new TestName();

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
	}

	@Before
	public void beforeOneTest() throws Exception {
		ResourceLeakDetector.setLevel(io.netty.util.ResourceLeakDetector.Level.PARANOID);
		System.setProperty("io.netty.leakDetection.targetRecords", String.valueOf(20));
		AppContext.init();
		testLogger.info(">> " + name.getMethodName() + " <<");
		threadCount = Thread.activeCount();
		scCtx = ctrl.startSC(TestConstants.SC0, TestConstants.logbackSC0, TestConstants.SC0Properties);
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			ctrl.stopSC(scCtx);
		} catch (Exception e) {
		}
		scCtx = null;
		testLogger.info("Number of threads=" + Thread.activeCount() + " created=" + (Thread.activeCount() - threadCount));
		AppContext.destroy();
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		ctrl = null;
	}
}
