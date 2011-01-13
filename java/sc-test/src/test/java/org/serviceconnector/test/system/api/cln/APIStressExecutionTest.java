/*
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
 */
package org.serviceconnector.test.system.api.cln;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;

public class APIStressExecutionTest {

	/** The Constant testLogger. */
	protected static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	protected static ProcessesController ctrl;
	protected static ProcessCtx scCtx;
	protected ProcessCtx sesSrvCtx;
	protected int threadCount = 0;

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
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		sesSrvCtx = ctrl.startServer(TestConstants.COMMUNICATOR_TYPE_SESSION, TestConstants.log4jSrvProperties,
				TestConstants.sesServerName1, TestConstants.PORT_SES_SRV_TCP, TestConstants.PORT_SC_TCP, 10, 5,
				TestConstants.sesServiceName1);
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			ctrl.stopServer(sesSrvCtx);
		} catch (Exception e) {
		}
		sesSrvCtx = null;
		try {
			ctrl.stopSC(scCtx);
		} catch (Exception e) {
		}
		scCtx = null;
		testLogger.info("Number of threads :" + Thread.activeCount() + " created :" + (Thread.activeCount() - threadCount));
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		ctrl = null;
	}

	/**
	 * Description: Create session (regular)<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_1Clients10000Messages() throws Exception {
		int numberOfClients = 10;
		ProcessCtx[] clientCtxs = new ProcessCtx[numberOfClients];

		for (int i = 0; i < clientCtxs.length; i++) {
			ProcessCtx clientCtx = ctrl.startClient(TestConstants.COMMUNICATOR_TYPE_SESSION, TestConstants.log4jClnProperties,
					"client" + i, TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP, 10, 0,
					TestConstants.sesServerName1, 10, 60, "f_execute10000MessagesAndExit");
			clientCtxs[i] = clientCtx;
		}
		APIStressExecutionTest.ctrl.waitForClientTermination(clientCtxs);
		TestUtil.checkLogFile(TestConstants.log4jClnProperties, "message.log");
		TestUtil.checkLogFile(TestConstants.log4jClnProperties, "client.log");
	}

	/**
	 * Description: Create session (regular)<br>
	 * Expectation: passes
	 */
	@Test
	public void t02_2Clients10000Messages() throws Exception {
		int numberOfClients = 3;
		ProcessCtx[] clientCtxs = new ProcessCtx[numberOfClients];

		for (int i = 0; i < clientCtxs.length; i++) {
			ProcessCtx clientCtx = ctrl.startClient(TestConstants.COMMUNICATOR_TYPE_SESSION, TestConstants.log4jClnProperties,
					"client" + i, TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP, 10, 0,
					TestConstants.sesServerName1, 50, 60, "f_execute10000MessagesAndExit");
			clientCtxs[i] = clientCtx;
		}
		APIStressExecutionTest.ctrl.waitForClientTermination(clientCtxs);
		TestUtil.checkLogFile(TestConstants.log4jClnProperties, "message.log");
		TestUtil.checkLogFile(TestConstants.log4jClnProperties, "client.log");
	}

}