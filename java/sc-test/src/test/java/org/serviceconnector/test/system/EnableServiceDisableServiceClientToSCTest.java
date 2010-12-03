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
package org.serviceconnector.test.system;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.service.SCServiceException;

public class EnableServiceDisableServiceClientToSCTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(EnableServiceDisableServiceClientToSCTest.class);

	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	private static ProcessCtx scCtx;
	private ProcessCtx srvCtx;

	private SCMgmtClient client;
	private Exception ex;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
	}

	@Before
	public void beforeOneTest() throws Exception {
		srvCtx = ctrl.startServer(TestConstants.SERVER_TYPE_SESSION, TestConstants.log4jSrvProperties,
				TestConstants.sesServerName1, TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, 10,
				TestConstants.pubServiceName1 );

		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_HTTP);
		client.attach();
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			client.detach();
		} catch (Exception e) { }
		try {
			ctrl.stopServer(srvCtx);
		} catch (Exception e) {	}
		srvCtx = null;
		client = null;
		ex = null;
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		try {
			ctrl.stopSC(scCtx);
		} catch (Exception e) {	}
		scCtx = null;
		ctrl = null;
	}

	/**
	 * Description:	Create session on enabled service <br>
	 * Expectation:	Session is successfully created, returns sessionId
	 */
	@Test
	public void createSession_1() throws Exception {
		assertEquals(true, client.isServiceEnabled(TestConstants.sesServiceName1));

		// create session 
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);

		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession( 60, scMessage);

		assertEquals(true, client.isServiceEnabled(TestConstants.sesServiceName1));
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description:	Create session on initially disabled service enabled by the client<br> 
	 * Expectation:	Session is successfully created, returns sessionId
	 */
	// TODO doubt this test case is useful ?
	@Test
	public void createSession_2() throws Exception {
		assertEquals(false, client.isServiceEnabled(TestConstants.sesServiceName1));
		client.enableService(TestConstants.sesServiceName1);
		assertEquals(true, client.isServiceEnabled(TestConstants.sesServiceName1));

		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession( 60, scMessage);

		assertEquals(true, client.isServiceEnabled(TestConstants.sesServiceName1));
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

		client.disableService(TestConstants.sesServiceName1);
	}

	/**
	 * Description:	Create session on initially enabled service disabled by the client<br> 
	 * Expectation:	Throws SCServiceException exception
	 */
	@Test
	public void createSession_3() throws Exception {
		assertEquals(true, client.isServiceEnabled(TestConstants.sesServiceName1));
		client.disableService(TestConstants.sesServiceName1);
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);

		try {
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession( 60, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, client.isServiceEnabled(TestConstants.sesServiceName1));
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		client.enableService(TestConstants.sesServiceName1);
	}

	/**
	 * Description:<br> 
	 * 1. Disabled and enabled again the service.<br>
	 * 2. Create the session on enabled service.<br>
	 * <br>	
	 * Expectation:	Session is successfully created, returns sessionId
	 */
	// TODO doubt this test case is useful ? disabled & enabled first the service
	@Test
	public void createSession_4() throws Exception {
		// 1.
		assertEquals(true, client.isServiceEnabled(TestConstants.sesServiceName1));
		client.disableService(TestConstants.sesServiceName1);
		client.enableService(TestConstants.sesServiceName1);
		assertEquals(true, client.isServiceEnabled(TestConstants.sesServiceName1));

		// 2.
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession( 60, scMessage);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description:<br> 
	 * 1. Disabled and enabled the service 1000 times.<br>
	 * 2. Create the session on enabled service.<br>
	 * <br>	
	 * Expectation:	Session is successfully created, returns sessionId
	 */
	// TODO doubt this test case is useful ? disabled & enabled first the service
	@Test
	public void createSession_5() throws Exception {
		// 1.
		assertEquals(true, client.isServiceEnabled(TestConstants.sesServiceName1));

		for (int i = 0; i < 1000; i++) {
			if ((i % 100) == 0)
				testLogger.info("EnabledDisableService_1000Times cycle:\t" + i + " ...");
			client.disableService(TestConstants.sesServiceName1);
			client.enableService(TestConstants.sesServiceName1);
		}
		assertEquals(true, client.isServiceEnabled(TestConstants.sesServiceName1));
		
		// 2.
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession( 60, scMessage);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Do this steps 1000 times.<br>
	 * 	1. Disabled and enabled the service.<br>
	 * 	2. Create the session on enabled service.<br>
	 * <br>
	 * Expectation:	:	Session is successfully created, returns sessionId
	 */
	// TODO doubt this test case is useful ? disabled & enabled first the service
	@Test
	public void createSession_6() throws Exception {
		assertEquals(true, client.isServiceEnabled(TestConstants.sesServiceName1));

		for (int i = 0; i < 1000; i++) {
			if ((i % 100) == 0)
				testLogger.info("EnabledDisableService_1000Times cycle:\t" + i + " ...");
			// 1.
			client.disableService(TestConstants.sesServiceName1);
			client.enableService(TestConstants.sesServiceName1);
			
			// 2.
			SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession( 60, scMessage);
			assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
			sessionService.deleteSession();
		}
	}
}
