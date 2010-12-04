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
package org.serviceconnector.test.system.session;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;

public class CreateSessionTest {

	/** The Constant testLogger. */
	protected static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(CreateSessionTest.class);

	private static ProcessesController ctrl;
	private ProcessCtx scCtx;
	private ProcessCtx srvCtx;
	private SCClient client;
	private SCSessionService service;
	private int threadCount = 0;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
	}

	@Before
	public void beforeOneTest() throws Exception {
		threadCount = Thread.activeCount();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		srvCtx = ctrl.startServer(TestConstants.SERVER_TYPE_SESSION, TestConstants.log4jSrvProperties,
				TestConstants.sesServerName1, TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, 10,
				TestConstants.sesServiceName1);
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			service.deleteSession();
		} catch (Exception e1) {
		}
		service = null;
		try {
			client.detach();
		} catch (Exception e) {
		}
		client = null;
		try {
			ctrl.stopServer(srvCtx);
		} catch (Exception e) {
		}
		srvCtx = null;
		try {
			ctrl.stopSC(scCtx);
		} catch (Exception e) {
		}
		scCtx = null;
		testLogger.info("Number of threads :" + Thread.activeCount() + " created :"+(Thread.activeCount() - threadCount));
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
	public void t01_regular() throws Exception {	
		SCMessage request = null;
		@SuppressWarnings("unused")
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(request);
		assertNotNull("the session ID is null", service.getSessionId());
		service.deleteSession();
		assertNull("the session ID is NOT null after deleteSession()", service.getSessionId());
	}
	
	/**
	 * Description: Create session with service which has been disabled<br>
	 * Expectation: throws SCServiceException
	 */
	@Test (expected = SCServiceException.class )
	public void t02_disabledService() throws Exception {
		// disable service
		SCMgmtClient clientMgmt = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP);
		clientMgmt.attach();
		clientMgmt.disableService(TestConstants.sesServiceName1);
		clientMgmt.detach();
		
		SCMessage request = null;
		@SuppressWarnings("unused")
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(request);
		service.deleteSession();
	}

	/**
	 * Description: Delete session before create session<br>
	 * Expectation: passes
	 */
	@Test
	public void t03_deleteSession() throws Exception {	
		service = client.newSessionService(TestConstants.sesServiceName1);
		service.deleteSession();
		assertNull("the session ID is NOT null after deleteSession()", service.getSessionId());
	}

	/**
	 * Description: Create session twice<br>
	 * Expectation: throws SCServiceException
	 */
	@Test (expected = SCServiceException.class )
	public void t04_createSession() throws Exception {	
		SCMessage request = null;
		@SuppressWarnings("unused")
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(request);
		assertNotNull("the session ID is null", service.getSessionId());
		response = service.createSession(request);
		service.deleteSession();
		assertNull("the session ID is NOT null after deleteSession()", service.getSessionId());
	}

	/**
	 * Description: Create two sessions to the same service<br>
	 * Expectation: passes
	 */
	@Test
	public void t05_createSession() throws Exception {	
		SCMessage request = null;
		@SuppressWarnings("unused")
		SCMessage response = null;
		SCSessionService service1 = client.newSessionService(TestConstants.sesServiceName1);
		SCSessionService service2 = client.newSessionService(TestConstants.sesServiceName1);
		
		response = service1.createSession(request);
		assertNotNull("the session ID is null", service1.getSessionId());
		response = service2.createSession(request);
		assertNotNull("the session ID is null", service2.getSessionId());

		service1.deleteSession();
		assertNull("the session ID is NOT null after deleteSession()", service1.getSessionId());
		service2.deleteSession();
		assertNull("the session ID is NOT null after deleteSession()", service2.getSessionId());
	}

	/**
	 * Description: Create session to service which does not exist<br>
	 * Expectation: throws SCServiceException
	 */
	@Test (expected = SCServiceException.class )
	public void t06_createSession() throws Exception {	
		SCMessage request = null;
		@SuppressWarnings("unused")
		SCMessage response = null;
		service = client.newSessionService("gaga");
		response = service.createSession(request);
		service.deleteSession();
	}

	/**
	 * Description: Create session to service not served by a server<br>
	 * Expectation: throws SCServiceException
	 */
	@Test (expected = SCServiceException.class )
	public void t07_createSession() throws Exception {	
		SCMessage request = null;
		@SuppressWarnings("unused")
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName2);
		response = service.createSession(request);
		service.deleteSession();
	}

	/**
	 * Description: Create session with operationTimeout = 0<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class )
	public void t08_createSession() throws Exception {	
		SCMessage request = null;
		@SuppressWarnings("unused")
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(0,request);
		service.deleteSession();
	}

	/**
	 * Description: Create session with operationTimeout = -1<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class )
	public void t09_createSession() throws Exception {	
		SCMessage request = null;
		@SuppressWarnings("unused")
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(-1,request);
		service.deleteSession();
	}

	/**
	 * Description: Create session with operationTimeout = 3601<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class )
	public void t10_createSession() throws Exception {	
		SCMessage request = null;
		@SuppressWarnings("unused")
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(3601,request);
		service.deleteSession();
	}

	/**
	 * Description: Create session with 60kB message<br>
	 * Expectation: passes
	 */
	@Test
	public void t11_createSession() throws Exception {	
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength60kB]);
		@SuppressWarnings("unused")
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(request);
		service.deleteSession();
	}

	/**
	 * Description: Create session with large message<br>
	 * Expectation: throws SCServiceException
	 */
	@Test (expected = SCServiceException.class )
	public void t12_createSession() throws Exception {	
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength1MB]);
		@SuppressWarnings("unused")
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(request);
		service.deleteSession();
	}
	
	/**
	 * Description: Reject session by server<br>
	 * Expectation: throws SCServiceException
	 */
	@Test (expected = SCServiceException.class )
	public void t13_rejectSession() throws Exception {	
		SCMessage request = new SCMessage();
		@SuppressWarnings("unused")
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		request.setSessionInfo(TestConstants.rejectSessionCmd);
		response = service.createSession(request);
		service.deleteSession();
	}

	
}