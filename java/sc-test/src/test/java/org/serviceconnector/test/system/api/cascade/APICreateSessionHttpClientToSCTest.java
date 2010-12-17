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
package org.serviceconnector.test.system.api.cascade;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;

public class APICreateSessionHttpClientToSCTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(APICreateSessionHttpClientToSCTest.class);
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	private static ProcessesController ctrl;
	private static ProcessCtx scCtx;
	private static ProcessCtx srvCtx;
	private int threadCount = 0;
	private SCClient client;
	private Exception ex;

	

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		srvCtx = ctrl.startServer(TestConstants.SERVER_TYPE_SESSION, TestConstants.log4jSrvProperties,
				TestConstants.sesServerName1, TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, 10,
				TestConstants.sesServiceName1);
	}

	@Before
	public void beforeOneTest() throws Exception {
		threadCount = Thread.activeCount();
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
	}

	@After
	public void afterOneTest() throws Exception {
		client.detach();
		client = null;
		ex = null;
		Assert.assertEquals("number of threads", threadCount, Thread.activeCount());
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		try {
			ctrl.stopServer(scCtx);
		} catch (Exception e) {}
		try {
			ctrl.stopSC(srvCtx);
		} catch (Exception e) {}

		srvCtx = null;
		scCtx = null;
		ctrl = null;
	}

	/**
	 * Description: Create new session with empty session name and delete the session.<br>
	 * Expectation:	Session is deleted
	 */
	@Test
	public void t01_deleteSession() throws Exception {
		SCSessionService sessionService = client.newSessionService("");
		sessionService.deleteSession();
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create new session with blank string as session name and delete the session.<br>
	 * Expectation:	Session is deleted
	 */
	@Test
	public void t02_deleteSession() throws Exception {
		SCSessionService sessionService = client.newSessionService(" ");
		sessionService.deleteSession();
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create new session with session name "a" (SingleChar) and delete the session.<br>
	 * Expectation:	Session is deleted
	 */
	@Test
	public void t03_deleteSession() throws Exception {
		SCSessionService sessionService = client.newSessionService("a");
		sessionService.deleteSession();
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create new session with invalid name and delete the session.<br>
	 * Expectation:	Session is deleted
	 */
	@Test
	public void t04_deleteSession() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.pangram);
		sessionService.deleteSession();
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create new session with service name and delete the session.<br>
	 * Expectation:	Session is deleted
	 */
	@Test
	public void t05_deleteSession() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		sessionService.deleteSession();
		Assert.assertEquals("Session is not deleted ", true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}
	
	/**
	 * Description: Create new session with empty session service name.<br>
	 * Expectation:	throws service exception
	 */
	@Test
	public void t10_createSession() throws Exception {
		
		String data = null; 
		String sessionInfo = "sessionInfo"; 
		int timeoutInSeconds = 10;

		SCSessionService sessionService = client.newSessionService("");
		try {
			SCMessage scMessage = new SCMessage(data);
			scMessage.setSessionInfo(sessionInfo);
			sessionService.createSession( timeoutInSeconds, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	
	/**
	 * Description: Create new session with empty session service name and message (SCMessage) with white space.<br>
	 * Expectation:	throws service exception
	 */
	@Test
	public void t11_createSession() throws Exception {

		String data = " "; 
		String sessionInfo = "sessionInfo"; 
		int timeoutInSeconds = 10;

		SCSessionService sessionService = client.newSessionService("");
		try {
			SCMessage scMessage = new SCMessage(data);
			scMessage.setSessionInfo(sessionInfo);
			sessionService.createSession( timeoutInSeconds, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}
	
	/**
	 * Description: Create new session with white space session service name and message (SCMessage) with white space.<br>
	 * Expectation:	throws service exception
	 */
	@Test
	public void t12_createSession() throws Exception {
		String data = " "; 
		String sessionInfo = "sessionInfo"; 
		int timeoutInSeconds = 10;

		SCSessionService sessionService = client.newSessionService(" ");
		try {
			SCMessage scMessage = new SCMessage(data);
			scMessage.setSessionInfo(sessionInfo);
			sessionService.createSession( timeoutInSeconds, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}
	
	/**
	 * Description: Create new session with arbitrary session service name and message (SCMessage) with white space.<br>
	 * Expectation:	throws service exception
	 */
	@Test
	public void t13_createSession() throws Exception {
		String data = " "; 
		String sessionInfo = "sessionInfo"; 
		int timeoutInSeconds = 10;

		SCSessionService sessionService = client.newSessionService(TestConstants.pangram);
		try {
			SCMessage scMessage = new SCMessage(data);
			scMessage.setSessionInfo(sessionInfo);
			sessionService.createSession( timeoutInSeconds, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}
	
	
	/**
	 * Description: Create new session service with blank string (session name) and new session.<br>
	 * Expectation:	throws service exception
	 */
	@Test
	public void t14_createSession() throws Exception { 
		String data = null; 
		String sessionInfo = "sessionInfo"; 
		int timeoutInSeconds = 10;

		SCSessionService sessionService = client.newSessionService(" ");
		try {
			SCMessage scMessage = new SCMessage(data);
			scMessage.setSessionInfo(sessionInfo);
			sessionService.createSession( timeoutInSeconds, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

	}

	/**
	 * Description: Create new session service with invalid name and new session.<br>
	 * Expectation:	throws service exception
	 */
	@Test
	public void t15_createSession() throws Exception {
		String data = null; 
		String sessionInfo = "sessionInfo"; 
		int timeoutInSeconds = 10;

		SCSessionService sessionService = client.newSessionService(TestConstants.pangram);
		try {
			SCMessage scMessage = new SCMessage(data);
			scMessage.setSessionInfo(sessionInfo);
			sessionService.createSession( timeoutInSeconds, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create new session service with valid name and new session.<br>
	 * Expectation:	throws service exception
	 */
	@Test
	public void t16_createSession() throws Exception {
		String data = null; 
		String sessionInfo = "sessionInfo"; 
		int timeoutInSeconds = 10;

		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		try {
			SCMessage scMessage = new SCMessage(data);
			scMessage.setSessionInfo(sessionInfo);
			sessionService.createSession( timeoutInSeconds, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals("Expected SCServiceException", true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create new session service with valid name and new session with empty session info.<br>
	 * Expectation:	throws SCMPValidator exception
	 */
	@Test
	public void t20_createSessionInfo() throws Exception { 
		String data = null; 
		String sessionInfo = ""; 
		int timeoutInSeconds = 10;

		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		try {
			SCMessage scMessage = new SCMessage(data);
			scMessage.setSessionInfo(sessionInfo);
			sessionService.createSession( timeoutInSeconds, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}



	/**
	 * Description: Create new session service with valid name and new session with blank string as session info.<br>
	 * Expectation:	Session service isn't created.
	 */
	@Test
	public void t21_createSessionInfo() throws Exception {
		String messageData = null;
		String sessionInfo = " ";
		int timeoutInSeconds = 10;
		
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage(messageData);
		scMessage.setSessionInfo(sessionInfo);
		sessionService.createSession( timeoutInSeconds, scMessage);
		Assert.assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create new session service with valid name and new session with arbitrary string as session info.<br>
	 * Expectation:	Session service isn't created.
	 */
	@Test
	public void t22_createSessionInfo() throws Exception {
		String messageData = null;
		String sessionInfo = TestConstants.pangram;
		int timeoutInSeconds = 10;
		
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage(messageData);
		scMessage.setSessionInfo(sessionInfo);
		sessionService.createSession( timeoutInSeconds, scMessage);
		Assert.assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create new session service with valid name and new session with long (256 Bytes) session info.<br>
	 * Expectation:	Session service is created.
	 */
	@Test
	public void t23_createSessionInfo() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 256; i++) {
			sb.append('a');
		}

		String messageData = null;
		String sessionInfo = sb.toString();
		int timeoutInSeconds = 10;
		
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage(messageData);
		scMessage.setSessionInfo(sessionInfo);
		sessionService.createSession( timeoutInSeconds, scMessage);
		Assert.assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create new session service with valid name and new session with long (257 Bytes) session info.<br>
	 * Expectation:	throws SCMPValidator exception
	 */
	@Test
	public void t24_createSessionInfo() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 257; i++) {
			sb.append('a');
		}
		String data = null; 
		String sessionInfo = sb.toString(); 
		int timeoutInSeconds = 10;

		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		try {
			SCMessage scMessage = new SCMessage(data);
			scMessage.setSessionInfo(sessionInfo);
			sessionService.createSession( timeoutInSeconds, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

	}

	/**
	 * Description: Create new session service with white space name and new session with empty session info.<br>
	 * Expectation:	throws SCMPValidator exception
	 */
	@Test
	public void t25_createSessionInfo() throws Exception {
		String data = " "; 
		String sessionInfo = ""; 
		int timeoutInSeconds = 10;

		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		try {
			SCMessage scMessage = new SCMessage(data);
			scMessage.setSessionInfo(sessionInfo);
			sessionService.createSession( timeoutInSeconds, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create new session service with white space name and new session with white space session info.<br>
	 * Expectation:	throws SCMPValidator exception
	 */
	@Test
	public void t26_createSessionInfo() throws Exception {
		String messageData = " ";
		String sessionInfo = " ";
		int timeoutInSeconds = 10;
		
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage(messageData);
		scMessage.setSessionInfo(sessionInfo);
		sessionService.createSession( timeoutInSeconds, scMessage);
		Assert.assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

	}

	/**
	 * Description: Create new session service with white spaces name and new session with arbitrary string as session info.<br>
	 * Expectation:	Session service isn't created.
	 */
	@Test
	public void t27_createSessionInfo() throws Exception {
		String messageData = " ";
		String sessionInfo = TestConstants.pangram;
		int timeoutInSeconds = 10;
		
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage(messageData);
		scMessage.setSessionInfo(sessionInfo);
		sessionService.createSession( timeoutInSeconds, scMessage);
		Assert.assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

	}

	/**
	 * Description: Create new session service with white spaces name and new session with long (256 Bytes) session info.<br>
	 * Expectation:	Session service isn't created.
	 */
	@Test
	public void t28_createSessionInfo() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 256; i++) {
			sb.append('a');
		}
		String messageData = " ";
		String sessionInfo = sb.toString();
		int timeoutInSeconds = 10;
		
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage(messageData);
		scMessage.setSessionInfo(sessionInfo);
		sessionService.createSession( timeoutInSeconds, scMessage);
		Assert.assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

	}
	
	/**
	 * Description: Create new session service with SC-message and delete the session.<br>
	 * Expectation:	Session is deleted
	 */
	@Test
	public void t30_deleteSessionService() throws Exception {
		String messageData = null;
		String sessionInfo = "sessionInfo";
		
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage(messageData);
		scMessage.setSessionInfo(sessionInfo);
		sessionService.createSession( 10, scMessage);
		sessionService.deleteSession();
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());	
	}

	/**
	 * Description: Create new session service with SC-message (blank string as session info) and delete the session.<br>
	 * Expectation:	Session is deleted
	 */
	@Test
	public void t31_deleteSessionService() throws Exception {
		String messageData = null;
		String sessionInfo = " ";
		
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage(messageData);
		scMessage.setSessionInfo(sessionInfo);
		sessionService.createSession( 10, scMessage);
		sessionService.deleteSession();
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());	
	}

	/**
	 * Description: Create new session service with SC-message (white space as message name) and delete the session.<br>
	 * Expectation:	Session is deleted
	 */
@Test
	public void t32_deleteSessionService() throws Exception {
		String messageData = " ";
		String sessionInfo = "sessionInfo";
		
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage(messageData);
		scMessage.setSessionInfo(sessionInfo);
		sessionService.createSession( 10, scMessage);
		sessionService.deleteSession();
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());	
	}

	/**
	 * Description: Create new session service with SC-message (white space as message name and as Info-Data) and delete the session.<br>
	 * Expectation:	Session is deleted
	 */
	@Test
	public void t33_deleteSessionService() throws Exception {
		String messageData = " ";
		String sessionInfo = " ";
		
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage(messageData);
		scMessage.setSessionInfo(sessionInfo);
		sessionService.createSession( 10, scMessage);
		sessionService.deleteSession();
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());	
	}

	/**
	 * Description: Create new session service with valid name and two sessions with the same SCMessage.<br>
	 * Expectation:	throws SC-Service exception
	 */
	@Test
	public void t400_createSession() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage();
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession( 10, scMessage);
		try {
			sessionService.createSession( 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create new session service with valid name and two sessions with the same SessionInfo.<br>
	 * Expectation:	throws SC-Service exception
	 */
	@Test
	public void t401_createSession() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage0 = new SCMessage();
		scMessage0.setSessionInfo("sessionInfo");
		sessionService.createSession( 10, scMessage0);
		try {
			SCMessage scMessage1 = new SCMessage();
			scMessage1.setSessionInfo("sessionInfo");
			sessionService.createSession( 10, scMessage1);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	
	/**
	 * Description: Create two new session service with any one session (different session id's).<br>
	 * Expectation:	two sessions are created
	 */
	@Test
	public void t402_createSession() throws Exception {
		SCSessionService sessionService0 = client.newSessionService(TestConstants.sesServiceName1);
		SCSessionService sessionService1 = client.newSessionService(TestConstants.pubServiceName1);

		Assert.assertEquals(true, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		Assert.assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		SCMessage scMessage0 = new SCMessage();
		scMessage0.setSessionInfo("sessionInfo");
		sessionService0.createSession( 10, scMessage0);

		Assert.assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		Assert.assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		SCMessage scMessage1 = new SCMessage();
		scMessage1.setSessionInfo("sessionInfo");
		sessionService1.createSession( 10, scMessage1);

		Assert.assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		Assert.assertEquals(false, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		Assert.assertEquals(false, sessionService0.getSessionId().equals(sessionService1.getSessionId()));

		sessionService0.deleteSession();
		sessionService1.deleteSession();
	}

	/**
	 * Description: Create two new session service with any one session (different session id's). The name of the messages are  message are white space. <br>
	 * Expectation:	two sessions are created
	 */
	@Test
	public void t403_createSession() throws Exception {
		SCSessionService sessionService0 = client.newSessionService(TestConstants.sesServiceName1);
		SCSessionService sessionService1 = client.newSessionService(TestConstants.pubServiceName1);

		Assert.assertEquals(true, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		Assert.assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		SCMessage scMessage0 = new SCMessage(" ");
		scMessage0.setSessionInfo("sessionInfo");
		sessionService0.createSession( 10, scMessage0);

		Assert.assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		Assert.assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		SCMessage scMessage1 = new SCMessage(" ");
		scMessage1.setSessionInfo("sessionInfo");
		sessionService1.createSession( 10, scMessage1);

		Assert.assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		Assert.assertEquals(false, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		Assert.assertEquals(false, sessionService0.getSessionId().equals(sessionService1.getSessionId()));

		sessionService0.deleteSession();
		sessionService1.deleteSession();
	}


	/**
	 * Description: Create and delete 10'000 times one session service with one session.<br>
	 * Expectation:	all session service are created and deleted.
	 */	
	@Test
	public void t404_createSession() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		for (int i = 0; i < 1000; i++) {
			if ((i % 100) == 0)
				testLogger.info("createSession_12 cycle:\t" + i + " ...");
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession( 10, scMessage);
			Assert.assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
			sessionService.deleteSession();
			Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		}
	}

	/**
	 * Description: Create one session service with echoInterval 0.<br>
	 * Expectation:	throws SCMP-Validator exception
	 */	
	@Test
	public void t50_echoInterval() throws Exception {
		String data = null; 
		String sessionInfo = "sessionInfo"; 
		int timeoutInSeconds = 10;

		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		try {
			SCMessage scMessage = new SCMessage(data);
			scMessage.setSessionInfo(sessionInfo);
			sessionService.createSession( timeoutInSeconds, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

	}

	/**
	 * Description: Create one session service with negative echoInterval (-1).<br>
	 * Expectation:	throws SCMP-Validator exception
	 */	
	@Test
	public void t51_echoInterval() throws Exception {
		//this.testCreateSessionWithException(TestConstants.sessionServiceNames, null, "sessionInfo", -1, 10);
		String data = null; 
		String sessionInfo = "sessionInfo"; 
		int timeoutInSeconds = 10;

		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		try {
			SCMessage scMessage = new SCMessage(data);
			scMessage.setSessionInfo(sessionInfo);
			sessionService.createSession( timeoutInSeconds, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create one session service with echoInterval 1.<br>
	 * Expectation:	Session service was created and delete.
	 */	
	@Test
	public void t52_echoInterval() throws Exception {
		//this.testCreateSessionWithException(TestConstants.sessionServiceNames, null, "sessionInfo", 1, 10);
		String data = null; 
		String sessionInfo = "sessionInfo"; 
		int timeoutInSeconds = 10;
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		try {
			SCMessage scMessage = new SCMessage(data);
			scMessage.setSessionInfo(sessionInfo);
			sessionService.createSession( timeoutInSeconds, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

	}

	/**
	 * Description: Create one session service with to small negative echoInterval.<br>
	 * Expectation:	throws SCMP-Validator exception
	 */	
	@Test
	public void t53_echoInterval() throws Exception {
		//this.testCreateSessionWithException(TestConstants.sessionServiceNames, null, "sessionInfo", Integer.MIN_VALUE, 10);
		String data = null; 
		String sessionInfo = "sessionInfo"; 
		int timeoutInSeconds = 10;
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		try {
			SCMessage scMessage = new SCMessage(data);
			scMessage.setSessionInfo(sessionInfo);
			sessionService.createSession( timeoutInSeconds, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

	}

	/**
	 * Description: Create one session service with to big echoInterval.<br>
	 * Expectation:	throws SCMP-Validator exception
	 */	
	@Test
	public void t54_echoInterval() throws Exception {
		//this.testCreateSessionWithException(TestConstants.sessionServiceNames, null, "sessionInfo", Integer.MAX_VALUE, 10);

		String data = null; 
		String sessionInfo = "sessionInfo"; 
		int timeoutInSeconds = 10;

		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		try {
			SCMessage scMessage = new SCMessage(data);
			scMessage.setSessionInfo(sessionInfo);
			sessionService.createSession( timeoutInSeconds, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

	}

	/**
	 * Description: Create one session service with echoInterval 1 hour.<br>
	 * Expectation:	Session service was created and delete.
	 */	
	@Test
	public void t55_echoInterval() throws Exception {
		String messageData = null;
		String sessionInfo = "sessionInfo";
		int timeoutInSeconds = 10;
		
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage(messageData);
		scMessage.setSessionInfo(sessionInfo);
		sessionService.createSession( timeoutInSeconds, scMessage);
		Assert.assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

	}

	/**
	 * Description: Create one session service with echoInterval 1 hour and 1 second.<br>
	 * Expectation:	throws SCMP-Validator exception
	 */	
	@Test
	public void t56_echoInterval() throws Exception {
		//this.testCreateSessionWithException(TestConstants.sessionServiceNames, null, "sessionInfo", 3601, 10);

		String data = null; 
		String sessionInfo = "sessionInfo"; 
		int timeoutInSeconds = 10;

		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		try {
			SCMessage scMessage = new SCMessage(data);
			scMessage.setSessionInfo(sessionInfo);
			sessionService.createSession( timeoutInSeconds, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

	}

	/**
	 * Description: Create one session service with timeout 0 second.<br>
	 * Expectation:	throws SCMP-Validator exception
	 */	
	@Test
	public void t60_timeout() throws Exception {
		//this.testCreateSessionWithException(TestConstants.sessionServiceNames, null, "sessionInfo", 3600, 0);

		String data = null; 
		String sessionInfo = "sessionInfo"; 
		int timeoutInSeconds = 0;

		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		try {
			SCMessage scMessage = new SCMessage(data);
			scMessage.setSessionInfo(sessionInfo);
			sessionService.createSession( timeoutInSeconds, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

	}

	/**
	 * Description: Create one session service with timeout -1 second.<br>
	 * Expectation:	throws SCMP-Validator exception
	 */	
	@Test
	public void t61_timeout() throws Exception {
		//this.testCreateSessionWithException(TestConstants.sessionServiceNames, null, "sessionInfo", 300, -1);
		
		String data = null; 
		String sessionInfo = "sessionInfo"; 
		int timeoutInSeconds = -1;

		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		try {
			SCMessage scMessage = new SCMessage(data);
			scMessage.setSessionInfo(sessionInfo);
			sessionService.createSession( timeoutInSeconds, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

	}

	/**
	 * Description: Create one session service with timeout 1 second.<br>
	 * Expectation:	Session service was created and delete.
	 */	
	@Test
	public void t62_timeout() throws Exception {
		String serviceName = TestConstants.sesServiceName1;
		String messageData = null;
		String sessionInfo = "sessionInfo";
		int timeoutInSeconds = 10;
		
		SCSessionService sessionService = client.newSessionService(serviceName);
		SCMessage scMessage = new SCMessage(messageData);
		scMessage.setSessionInfo(sessionInfo);
		sessionService.createSession( timeoutInSeconds, scMessage);
		Assert.assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

	}

	/**
	 * Description: Create one session service with to small negative timeout.<br>
	 * Expectation:	throws SCMP-Validator exception
	 */	
	@Test
	public void t63_timeout() throws Exception {
		//this.testCreateSessionWithException(TestConstants.sessionServiceNames, null, "sessionInfo", 300, Integer.MIN_VALUE);

		String data = null; 
		String sessionInfo = "sessionInfo"; 
		int timeoutInSeconds = Integer.MIN_VALUE;

		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		try {
			SCMessage scMessage = new SCMessage(data);
			scMessage.setSessionInfo(sessionInfo);
			sessionService.createSession( timeoutInSeconds, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

	}

		
	/**
	 * Description: Create one session service with to big negative timeout.<br>
	 * Expectation:	throws SCMP-Validator exception
	 */	
	@Test
	public void t64_timeout() throws Exception {
		//this.testCreateSessionWithException(TestConstants.sessionServiceNames, null, "sessionInfo", 300, Integer.MAX_VALUE);

		String data = null; 
		String sessionInfo = "sessionInfo"; 
		int timeoutInSeconds = Integer.MAX_VALUE;

		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		try {
			SCMessage scMessage = new SCMessage(data);
			scMessage.setSessionInfo(sessionInfo);
			sessionService.createSession( timeoutInSeconds, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

	}

	/**
	 * Description: Create one session service with timeout 1 hour.<br>
	 * Expectation:	Session service was created and delete.
	 */		
	@Test
	public void t65_timeout() throws Exception {
		String messageData = null;
		String sessionInfo = "sessionInfo";
		int timeoutInSeconds = 10;
		
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage(messageData);
		scMessage.setSessionInfo(sessionInfo);
		sessionService.createSession( timeoutInSeconds, scMessage);
		Assert.assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

	}

	/**
	 * Description: Create one session service with timeout 1 hour and 1 second.<br>
	 * Expectation:	throws SCServiceException 
	 */	
	@Test
	public void t66_timeout() throws Exception {
		//this.testCreateSessionWithException(TestConstants.sessionServiceNames, null, "sessionInfo", 300, 3601);

		String data = null; 
		String sessionInfo = "sessionInfo"; 
		int timeoutInSeconds = 3601;

		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		try {
			SCMessage scMessage = new SCMessage(data);
			scMessage.setSessionInfo(sessionInfo);
			sessionService.createSession( timeoutInSeconds, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create one session service with invalid parameters.<br>
	 * Expectation:	throws SCMPValidatorException
	 */	
	@Test (expected = SCMPValidatorException.class)
	public void t405_createSession() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		sessionService.createSession( -1, null);
	}



	/**
	 * Description: Create one session service with invalid parameters (WhiteSpace).<br>
	 * Expectation:	throws Exception
	 */	
	@Test (expected = SCServiceException.class)
	public void t406_createSession() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage(" ");
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession( 10, scMessage);


		SCMessage scMessage1 = new SCMessage();
		scMessage1.setSessionInfo("sessionInfo");
		sessionService.createSession( 10, scMessage1);
	}


	/**
	 * Description: Create 1000 times session service with invalid parameters (WhiteSpace).<br>
	 * Expectation:	All sessions was created and deleted
	 */	
	@Test
	public void t407_createSession() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		for (int i = 0; i < 1000; i++) {
			if ((i % 100) == 0)
				testLogger.info("createSession_1000times cycle:\t" + i + " ...");
			SCMessage scMessage = new SCMessage(" ");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession( 10, scMessage);
			Assert.assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
			sessionService.deleteSession();
			Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		}
	}

	/**
	 * Description: Create one session service with invalid parameters (empty ServiceName).<br>
	 * Expectation:	throws SCServiceException
	 */	
	@Test (expected = SCServiceException.class)
	public void t408_createSession() throws Exception {
		SCSessionService sessionService = client.newSessionService("");

		SCMessage scMessage = new SCMessage("a");
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession( 10, scMessage);
	}

	/**
	 * Description: Create one session service with invalid parameters (WhiteSpace).<br>
	 * Expectation:	throws Exception
	 */	
	@Test
	public void t409_createSession_whiteSpaceSessionServiceNameDataOneChar_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(" ");
		try {
			SCMessage scMessage = new SCMessage("a");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession( 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create one session service with invalid parameters (WhiteSpace).<br>
	 * Expectation:	throws Exception
	 */	
	@Test
	public void t410_createSession_arbitrarySessionServiceNameNotInSCPropsDataOneChar_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.pangram);
		try {
			SCMessage scMessage = new SCMessage("a");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession( 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create one session service with invalid parameters (WhiteSpace).<br>
	 * Expectation:	throws Exception
	 */	
	@Test(expected = SCMPValidatorException.class)
	public void t411_createSession_emptySessionInfoDataOneChar_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage("a");
		scMessage.setSessionInfo("");
		sessionService.createSession( 10, scMessage);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create one session service with invalid parameters (WhiteSpace).<br>
	 * Expectation:	throws Exception
	 */	
	@Test
	public void t412_createSession_whiteSpaceSessionInfoDataOneChar_sessionIdIsNotEmpty() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage("a");
		scMessage.setSessionInfo(" ");
		sessionService.createSession( 10, scMessage);
		Assert.assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create one session service with invalid parameters (WhiteSpace).<br>
	 * Expectation:	throws Exception
	 */	
	@Test
	public void t413_createSession_arbitrarySpaceSessionInfoDataOneChar_sessionIdIsNotEmpty() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage("a");
		scMessage.setSessionInfo(TestConstants.pangram);
		sessionService.createSession( 10, scMessage);
		Assert.assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create one session service with invalid parameters (WhiteSpace).<br>
	 * Expectation:	throws Exception
	 */	
	@Test
	public void t414_createSession_256LongSessionInfoDataOneChar_sessionIdIsNotEmpty() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 256; i++) {
			sb.append('a');
		}
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage("a");
		scMessage.setSessionInfo(sb.toString());
		sessionService.createSession( 10, scMessage);
		Assert.assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	/**
	 * Description: Create one session service with invalid parameters (WhiteSpace).<br>
	 * Expectation:	throws Exception
	 */	
	@Test
	public void t415_createSession_257LongSessionInfoDataOneChar_throwsException() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 257; i++) {
			sb.append('a');
		}
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		try {
			SCMessage scMessage = new SCMessage("a");
			scMessage.setSessionInfo(sb.toString());
			sessionService.createSession( 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCMPValidatorException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	/**
	 * Description: Create one session service with invalid parameters (WhiteSpace).<br>
	 * Expectation:	throws Exception
	 */	
	@Test
	public void deleteSession_afterValidCreateSessionDataOneChar_noSessionId() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage("a");
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession( 10, scMessage);
		sessionService.deleteSession();
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_whiteSpaceSessionInfoDataOneChar_noSessionId() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage("a");
		scMessage.setSessionInfo(" ");
		sessionService.createSession( 10, scMessage);
		sessionService.deleteSession();
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_twiceDataOneChar_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage("a");
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession( 10, scMessage);
		try {
			SCMessage scMessage1 = new SCMessage();
			scMessage1.setSessionInfo("sessionInfo");
			sessionService.createSession( 10, scMessage1);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_twiceWithDifferentSessionServicesDataOneChar_differentSessionIds() throws Exception {
		SCSessionService sessionService0 = client.newSessionService(TestConstants.sesServiceName1);
		SCSessionService sessionService1 = client.newSessionService(TestConstants.pubServiceName1);

		Assert.assertEquals(true, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		Assert.assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		SCMessage scMessage0 = new SCMessage("a");
		scMessage0.setSessionInfo("sessionInfo");
		sessionService0.createSession( 10, scMessage0);

		Assert.assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		Assert.assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		SCMessage scMessage1 = new SCMessage("a");
		scMessage1.setSessionInfo("sessionInfo");
		sessionService1.createSession( 10, scMessage1);

		Assert.assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		Assert.assertEquals(false, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		Assert.assertEquals(false, sessionService0.getSessionId().equals(sessionService1.getSessionId()));

		sessionService0.deleteSession();
		sessionService1.deleteSession();
	}

	@Test
	public void createSession_1000times_passes() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		for (int i = 0; i < 1000; i++) {
			if ((i % 100) == 0)
				testLogger.info("createSession_1000times cycle:\t" + i + " ...");
			SCMessage scMessage = new SCMessage("a");
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession( 10, scMessage);
			Assert.assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
			sessionService.deleteSession();
			Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		}
	}

	@Test
	public void createSession_emptySessionServiceNameData60kBByteArray_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService("");
		try {
			SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession( 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_whiteSpaceSessionServiceNameData60kBByteArray_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(" ");
		try {
			SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession( 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_arbitrarySessionServiceNameNotInSCPropsData60kBByteArray_throwsException()
			throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.pangram);
		try {
			SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession( 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test(expected = SCMPValidatorException.class)
	public void createSession_emptySessionInfoData60kBByteArray_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
		scMessage.setSessionInfo("");
		sessionService.createSession( 10, scMessage);
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_whiteSpaceSessionInfoData60kBByteArray_sessionIdIsNotEmpty() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
		scMessage.setSessionInfo(" ");
		sessionService.createSession( 10, scMessage);
		Assert.assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_arbitrarySpaceSessionInfoData60kBByteArray_sessionIdIsNotEmpty() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
		scMessage.setSessionInfo(TestConstants.pangram);
		sessionService.createSession( 10, scMessage);
		Assert.assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_256LongSessionInfoData60kBByteArray_sessionIdIsNotEmpty() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
		scMessage.setSessionInfo(TestConstants.stringLength256);
		sessionService.createSession( 10, scMessage);
		Assert.assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void deleteSession_afterValidCreateSessionData60kBByteArray_noSessionId() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession( 10, scMessage);
		sessionService.deleteSession();
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_whiteSpaceSessionInfoData60kBByteArray_noSessionId() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
		scMessage.setSessionInfo(" ");
		sessionService.createSession( 10, scMessage);
		sessionService.deleteSession();
		Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_twiceData60kBByteArray_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
		scMessage.setSessionInfo("sessionInfo");
		sessionService.createSession( 10, scMessage);
		try {
			sessionService.createSession( 10, scMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_twiceWithDifferentSessionServicesData60kBByteArray_differentSessionIds() throws Exception {
		SCSessionService sessionService0 = client.newSessionService(TestConstants.sesServiceName1);
		SCSessionService sessionService1 = client.newSessionService(TestConstants.pubServiceName1);

		Assert.assertEquals(true, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		Assert.assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
		scMessage.setSessionInfo("sessionInfo");
		sessionService0.createSession( 10, scMessage);

		Assert.assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		Assert.assertEquals(true, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		SCMessage scMessage1 = new SCMessage(new byte[TestConstants.dataLength60kB]);
		scMessage1.setSessionInfo("sessionInfo");
		sessionService1.createSession( 10, scMessage1);

		Assert.assertEquals(false, sessionService0.getSessionId() == null || sessionService0.getSessionId().isEmpty());
		Assert.assertEquals(false, sessionService1.getSessionId() == null || sessionService1.getSessionId().isEmpty());

		Assert.assertEquals(false, sessionService0.getSessionId().equals(sessionService1.getSessionId()));

		sessionService0.deleteSession();
		sessionService1.deleteSession();
	}

	@Test
	public void createSession_1000timesData60kBByteArray_passes() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		for (int i = 0; i < 1000; i++) {
			if ((i % 100) == 0)
				testLogger.info("createSession_1000times cycle:\t" + i + " ...");
			SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession( 10, scMessage);
			Assert.assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
			sessionService.deleteSession();
			Assert.assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		}
	}

	@Test
	public void createSession_1000SessionsAtOnce_acceptAllOfThem() throws Exception {
		int sessionsCount = 1000;
		String[] sessions = new String[sessionsCount];
		SCSessionService[] sessionServices = new SCSessionService[sessionsCount];
		for (int i = 0; i < sessionsCount; i++) {
			if ((i % 100) == 0)
				testLogger.info("createSession_1000times cycle:\t" + i + " ...");
			sessionServices[i] = client.newSessionService(TestConstants.sesServiceName1);
			sessionServices[i].createSession( 10, new SCMessage());
			sessions[i] = sessionServices[i].getSessionId();
		}
		for (int i = 0; i < sessionsCount; i++) {
			sessionServices[i].deleteSession();
			sessionServices[i] = null;
		}
		sessionServices = null;

		Arrays.sort(sessions);
		boolean duplicates = false;

		for (int i = 1; i < sessionsCount; i++) {
			if (sessions[i].equals(sessions[i - 1])) {
				duplicates = true;
				break;
			}
		}
		Assert.assertEquals(false, duplicates);
	}

	@Test
	public void createSession_1001SessionsAtOnce_exceedsConnectionsLimitThrowsException() throws Exception {
		int sessionsCount = 1001;
		int ctr = 0;
		String[] sessions = new String[sessionsCount];
		SCSessionService[] sessionServices = new SCSessionService[sessionsCount];
		try {
			for (int i = 0; i < sessionsCount; i++) {
				if ((i % 100) == 0)
					testLogger.info("createSession_1001times cycle:\t" + i + " ...");
				sessionServices[i] = client.newSessionService(TestConstants.sesServiceName1);
				sessionServices[i].createSession( 10, new SCMessage());
				sessions[i] = sessionServices[i].getSessionId();
				ctr++;
			}
		} catch (Exception e) {
			ex = e;
		}

		for (int i = 0; i < ctr; i++) {
			sessionServices[i].deleteSession();
			sessionServices[i] = null;
		}
		sessionServices = null;

		String[] successfulSessions = new String[ctr];
		System.arraycopy(sessions, 0, successfulSessions, 0, ctr);

		Arrays.sort(successfulSessions);
		boolean duplicates = false;

		for (int i = 1; i < ctr; i++) {
			if (successfulSessions[i].equals(successfulSessions[i - 1])) {
				duplicates = true;
				break;
			}
		}
		Assert.assertEquals(true, ex instanceof SCServiceException);
		Assert.assertEquals(sessionsCount - 1, ctr);
		Assert.assertEquals(false, duplicates);
	}

//	@Test
//	public void createSession_overBothConnectionTypes_passes() throws Exception {
//		SCClient client2 = new SCClient();
//		((SCClient) client2).setConnectionType("netty.tcp");
//		client2.attach(TestConstants.HOST, TestConstants.PORT_TCP);
//
//		SCSessionService session1 = client.newSessionService(TestConstants.sessionServiceNames);
//		SCSessionService session2 = client2.newSessionService(TestConstants.sessionServiceNames);
//
//		session1.createSession( 10, new SCMessage());
//		session2.createSession( 10, new SCMessage());
//
//		Assert.assertEquals(false, session1.getSessionId().equals(session2.getSessionId()));
//
//		session1.deleteSession();
//		session2.deleteSession();
//
//		Assert.assertEquals(session1.getSessionId(), session2.getSessionId());
//		client2.detach();
//		client2 = null;
//	}

//	@Test
//	public void createSession_overBothConnectionTypesDifferentServices_passes() throws Exception {
//		SCClient client2 = new SCClient();
//		((SCClient) client2).setConnectionType("netty.tcp");
//		client2.attach(TestConstants.HOST, TestConstants.PORT_TCP);
//
//		SCSessionService session1 = client.newSessionService(TestConstants.sessionServiceNames);
//		SCSessionService session2 = client2.newSessionService(TestConstants.publishServiceNames);
//
//		session1.createSession( 10, new SCMessage());
//		session2.createSession( 10, new SCMessage());
//
//		Assert.assertEquals(false, session1.getSessionId().equals(session2.getSessionId()));
//
//		session1.deleteSession();
//		session2.deleteSession();
//
//		Assert.assertEquals(session1.getSessionId(), session2.getSessionId());
//		client2.detach();
//		client2 = null;
//	}

	@Test
	public void sessionId_uniqueCheckFor10000IdsByOneClient_allSessionIdsAreUnique() throws Exception {
		int clientsCount = 10000;

		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
		String[] sessions = new String[clientsCount];

		for (int i = 0; i < clientsCount; i++) {
			if ((i % 500) == 0)
				testLogger.info("createSession_10000times cycle:\t" + i + " ...");
			sessionService.createSession( 10, new SCMessage());
			sessions[i] = sessionService.getSessionId();
			sessionService.deleteSession();
		}

		Arrays.sort(sessions);
		boolean duplicates = false;

		for (int i = 1; i < clientsCount; i++) {
			if (sessions[i].equals(sessions[i - 1])) {
				duplicates = true;
				break;
			}
		}
		Assert.assertEquals(false, duplicates);
	}
}
