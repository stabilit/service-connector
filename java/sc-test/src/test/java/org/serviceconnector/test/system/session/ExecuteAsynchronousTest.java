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

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.SCService;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.service.SCServiceException;

public class ExecuteAsynchronousTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ExecuteAsynchronousTest.class);

	private static ProcessCtx scCtx;
	private static ProcessCtx srvCtx;
	private static boolean messageReceived;

	private SCMgmtClient client;
	private Exception ex;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		srvCtx = ctrl.startServer(TestConstants.SERVER_TYPE_SESSION, TestConstants.log4jSrvProperties,
				TestConstants.sesServerName1, TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, 10,
				TestConstants.pubServiceName1 );
	}

	@Before
	public void beforeOneTest() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_HTTP);
		client.attach();
		assertEquals("available/allocated sessions", "1000/0", client.getWorkload(TestConstants.sesServiceName1));
	}

	@After
	public void afterOneTest() throws Exception {
		assertEquals("available/allocated sessions", "1000/0", client.getWorkload(TestConstants.sesServiceName1));
		client.detach();
		client = null;
		ex = null;
		messageReceived = false;
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		try {
			ctrl.stopServer(srvCtx);
		} catch (Exception e) {	}
		try {
			ctrl.stopSC(scCtx);
		} catch (Exception e) {	}
		srvCtx = null;
		scCtx = null;
		ctrl = null;
	}

	/**
	 * Description:  <br>
	 * Expectation: SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void execute_beforeCreateSession_throwsException() throws Exception {
		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		service.send(new SCMessage(), new MsgCallback(service));
	}

	/**
	 * Description: Send message with empty string. <br>
	 * Expectation: Server return the same message data.
	 */
	@Test
	public void t100_sendMessage() throws Exception {
		SCMessage message = new SCMessage("");
		message.setSessionInfo("sessionInfo");

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		service.createSession( 10, message);

		MsgCallback callback = new MsgCallback(service);
		service.send(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		service.deleteSession();

		assertEquals(null, response.getData());
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	/**
	 * Description: Send message with single character as data. <br>
	 * Expectation: Server return the same message data.
	 */
	@Test
	public void t101_sendMessage() throws Exception {
		SCMessage message = new SCMessage("a");
		message.setSessionInfo("sessionInfo");

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		service.createSession( 10, message);

		MsgCallback callback = new MsgCallback(service);
		service.send(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		service.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	/**
	 * Description: Send message with arbitrary data. <br>
	 * Expectation: Server return the same message data.
	 */
	@Test
	public void t102_sendMessage() throws Exception {
		SCMessage message = new SCMessage(TestConstants.pangram);
		message.setSessionInfo("sessionInfo");

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		service.createSession( 10, message);

		MsgCallback callback = new MsgCallback(service);
		service.send(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		service.deleteSession();

		assertEquals(message.getData(), response.getData());
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	/**
	 * Description: Send message with 1MB array data. <br>
	 * Expectation: Server return the same message data.
	 */
	@Test
	public void t103_sendMessage() throws Exception {
		SCMessage message = new SCMessage(new byte[TestConstants.dataLength1MB]);
		message.setSessionInfo("sessionInfo");

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		service.createSession( 10, message);

		MsgCallback callback = new MsgCallback(service);
		service.send(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		service.deleteSession();

		assertEquals(((byte[]) message.getData()).length, ((byte[]) response.getData()).length);
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	/**
	 * Description: Send message with white space into message info. <br>
	 * Expectation: Server return the same message data.
	 */
	@Test
	public void t104_sendMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setSessionInfo("sessionInfo");
		message.setMessageInfo(" ");

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		service.createSession( 10, message);

		MsgCallback callback = new MsgCallback(service);
		service.send(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		service.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	/**
	 * Description: Send message with single character message info. <br>
	 * Expectation: Server return the same message data.
	 */
	@Test
	public void t105_snedMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setSessionInfo("sessionInfo");
		message.setMessageInfo("a");
	
		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		service.createSession( 10, message);

		MsgCallback callback = new MsgCallback(service);
		service.send(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		service.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	/**
	 * Description: Send message with arbitrary message info. <br>
	 * Expectation: Server return the same message data.
	 */
	@Test
	public void t106_sendMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setSessionInfo("sessionInfo");
		message.setMessageInfo(TestConstants.pangram);

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		service.createSession( 10, message);

		MsgCallback callback = new MsgCallback(service);
		service.send(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		service.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	/**
	 * Description: Send message with arbitrary message info and compress. <br>
	 * Expectation: Server return the same message data.
	 */
	@Test
	public void t107_sendMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setSessionInfo("sessionInfo");
		message.setMessageInfo(TestConstants.pangram);
		message.setCompressed(true);

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		service.createSession( 10, message);
		
		MsgCallback callback = new MsgCallback(service);
		service.send(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		service.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}


	/**
	 * Description: Send message with arbitrary message info and compress is false. <br>
	 * Expectation: Server return the same message data.
	 */
	@Test
	public void t108_sendMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setSessionInfo("sessionInfo");
		message.setMessageInfo(TestConstants.pangram);
		message.setCompressed(false);

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		service.createSession( 10, message);

		MsgCallback callback = new MsgCallback(service);
		service.send(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		service.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
	}

	/**
	 * Description: Send message with empty session ID. <br>
	 * Expectation: Server return the same message data.
	 */	
	@Test
	public void t109_sendMessage() throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setSessionInfo("sessionInfo");
		message.setMessageInfo(TestConstants.pangram);
		message.setSessionId("");

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		service.createSession( 10, message);

		MsgCallback callback = new MsgCallback(service);
		service.send(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		String sessionId = service.getSessionId();
		service.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionId, response.getSessionId());
		assertEquals(message.isFault(), response.isFault());
	}

	/**
	 * Description: Send message with white space as session ID. <br>
	 * Expectation: Server return the same message data.
	 */	
	@Test
	public void execute_messageSessionIdWhiteSpaceString_returnsTheSameMessage() throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setSessionInfo("sessionInfo");
		message.setMessageInfo(TestConstants.pangram);
		message.setSessionId(" ");

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		service.createSession( 10, message);

		MsgCallback callback = new MsgCallback(service);
		service.send(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		String sessionId = service.getSessionId();
		service.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionId, response.getSessionId());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSessionIdSingleChar_returnsTheSameMessage() throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(TestConstants.pangram);
		message.setSessionId("a");

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		message.setSessionInfo("sessionInfo");
		service.createSession( 10, message);

		MsgCallback callback = new MsgCallback(service);
		service.send(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		String sessionId = service.getSessionId();
		service.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionId, response.getSessionId());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSessionIdArbitraryString_returnsTheSameMessage() throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(TestConstants.pangram);
		((SCMessage) message).setSessionId(TestConstants.pangram);

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		message.setSessionInfo("sessionInfo");
		service.createSession( 10, message);

		MsgCallback callback = new MsgCallback(service);
		service.send(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		String sessionId = service.getSessionId();
		service.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionId, response.getSessionId());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSessionIdLikeSessionIdString_returnsCorrectSessionId() throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(TestConstants.pangram);
		((SCMessage) message).setSessionId("aaaa0000-bb11-cc22-dd33-eeeeee444444");

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		message.setSessionInfo("sessionInfo");
		service.createSession( 10, message);

		MsgCallback callback = new MsgCallback(service);
		service.send(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		String sessionId = service.getSessionId();
		service.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionId, response.getSessionId());
		assertEquals(false, message.getSessionId().equals(response.getSessionId()));
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSessionIdSetManually_returnsTheSameMessage() throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(TestConstants.pangram);

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		message.setSessionInfo("sessionInfo");
		service.createSession( 10, message);

		((SCMessage) message).setSessionId(service.getSessionId());

		MsgCallback callback = new MsgCallback(service);
		service.send(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		String sessionId = service.getSessionId();
		service.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionId, response.getSessionId());
		assertEquals(message.getSessionId(), response.getSessionId());
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSessionIdSetToIdOfDifferentSessionSameServiceThanExecuting_returnsCorrectSessionId()
			throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(TestConstants.pangram);

		SCSessionService service0 = client.newSessionService(TestConstants.sesServiceName1);
		service0.createSession( 10, message);

		SCSessionService service1 = client.newSessionService(TestConstants.sesServiceName1);
		service1.createSession( 10, message);

		((SCMessage) message).setSessionId(service1.getSessionId());

		MsgCallback callback = new MsgCallback(service0);
		service0.send(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		String sessionId0 = service0.getSessionId();
		service0.deleteSession();
		service1.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionId0, response.getSessionId());
		assertEquals(false, message.getSessionId().equals(response.getSessionId()));
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_messageSessionIdSetToIdOfDifferentSessionServiceThanExecuting_returnsCorrectSessionId()
			throws Exception {

		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(TestConstants.pangram);

		SCSessionService service0 = client.newSessionService(TestConstants.sesServiceName1);
		service0.createSession( 10, message);

		SCSessionService service1 = client.newSessionService(TestConstants.pubServiceName1);
		service1.createSession( 10, message);

		((SCMessage) message).setSessionId(service1.getSessionId());

		MsgCallback callback = new MsgCallback(service0);
		service0.send(message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;
		String sessionId0 = service0.getSessionId();
		service0.deleteSession();
		service1.deleteSession();

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(sessionId0, response.getSessionId());
		assertEquals(false, message.getSessionId().equals(response.getSessionId()));
		assertEquals(message.isFault(), response.isFault());
	}

	@Test
	public void execute_timeout1_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(TestConstants.pangram);

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		message.setSessionInfo("sessionInfo");
		service.createSession( 10, message);

		MsgCallback callback = new MsgCallback(service);
		service.send(1, message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(service.getSessionId(), response.getSessionId());
		assertEquals(message.isFault(), response.isFault());

		service.deleteSession();
	}

	@Test
	public void execute_timeout2_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(TestConstants.pangram);

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		message.setSessionInfo("sessionInfo");
		service.createSession( 10, message);

		MsgCallback callback = new MsgCallback(service);
		service.send(2, message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(service.getSessionId(), response.getSessionId());
		assertEquals(message.isFault(), response.isFault());

		service.deleteSession();
	}

	@Test
	public void execute_timeout0_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(TestConstants.pangram);

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		message.setSessionInfo("sessionInfo");
		service.createSession( 10, message);

		SCMessage response = null;
		try {
			MsgCallback callback = new MsgCallback(service);
			service.send(0, message, callback);
			// wait until message received
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(null, response);

		service.deleteSession();
	}

	@Test
	public void execute_timeoutMinus1_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(TestConstants.pangram);

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		message.setSessionInfo("sessionInfo");
		service.createSession( 10, message);

		SCMessage response = null;
		try {
			MsgCallback callback = new MsgCallback(service);
			service.send(-1, message, callback);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(null, response);

		service.deleteSession();
	}

	@Test
	public void execute_timeoutIntMin_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(TestConstants.pangram);

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		message.setSessionInfo("sessionInfo");
		service.createSession( 10, message);

		SCMessage response = null;
		try {
			MsgCallback callback = new MsgCallback(service);
			service.send(Integer.MIN_VALUE, message, callback);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(null, response);

		service.deleteSession();
	}

	@Test
	public void execute_timeoutIntMax_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(TestConstants.pangram);

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		message.setSessionInfo("sessionInfo");
		service.createSession( 10, message);

		SCMessage response = null;
		try {
			MsgCallback callback = new MsgCallback(service);
			service.send(Integer.MAX_VALUE, message, callback);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(null, response);

		service.deleteSession();
	}

	@Test
	public void execute_timeoutAllowedMax_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(TestConstants.pangram);

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		message.setSessionInfo("sessionInfo");
		service.createSession( 10, message);

		MsgCallback callback = new MsgCallback(service);
		service.send(3600, message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
		service.deleteSession();
	}

	@Test
	public void execute_timeoutAllowedMaxPlus1_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(TestConstants.pangram);

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		message.setSessionInfo("sessionInfo");
		service.createSession( 10, message);

		SCMessage response = null;
		try {
			MsgCallback callback = new MsgCallback(service);
			service.send(3601, message, callback);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(null, response);

		service.deleteSession();
	}

	@Test
	public void execute_timeout1_passes() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(TestConstants.pangram);

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		message.setSessionInfo("sessionInfo");
		service.createSession( 10, message);

		MsgCallback callback = new MsgCallback(service);
		service.send(1, message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
		service.deleteSession();
	}

	@Test
	public void execute_timeout2_passes() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(TestConstants.pangram);

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		message.setSessionInfo("sessionInfo");
		service.createSession( 10, message);

		MsgCallback callback = new MsgCallback(service);
		service.send(2, message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
		service.deleteSession();
	}

	@Test
	public void execute_timeoutMaxAllowed_passes() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(TestConstants.pangram);

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		message.setSessionInfo("sessionInfo");
		service.createSession( 10, message);

		MsgCallback callback = new MsgCallback(service);
		service.send(3600, message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
		service.deleteSession();
	}

	@Test
	public void execute_timeoutMaxAllowedPlus1_throwsSCMPValidatorException() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(TestConstants.pangram);

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		message.setSessionInfo("sessionInfo");
		service.createSession( 10, message);

		try {
			MsgCallback callback = new MsgCallback(service);
			service.send(3601, message, callback);
		} catch (Exception e) {
			ex = e;
		}

		service.deleteSession();
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void execute_timeoutIntMax_throwsSCMPValidatorException() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(TestConstants.pangram);

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		message.setSessionInfo("sessionInfo");
		service.createSession( 10, message);

		try {
			MsgCallback callback = new MsgCallback(service);
			service.send(Integer.MAX_VALUE, message, callback);
		} catch (Exception e) {
			ex = e;
		}

		service.deleteSession();
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void execute_timeoutIntMin_throwsSCMPValidatorException() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(TestConstants.pangram);

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		message.setSessionInfo("sessionInfo");
		service.createSession( 10, message);

		try {
			MsgCallback callback = new MsgCallback(service);
			service.send(Integer.MIN_VALUE, message, callback);
		} catch (Exception e) {
			ex = e;
		}

		service.deleteSession();
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void execute_timeout0_throwsSCMPValidatorException() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(TestConstants.pangram);

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		message.setSessionInfo("sessionInfo");
		service.createSession( 10, message);

		try {
			MsgCallback callback = new MsgCallback(service);
			service.send(0, message, callback);
		} catch (Exception e) {
			ex = e;
		}

		service.deleteSession();
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void execute_timeoutMinus1_throwsSCMPValidatorException() throws Exception {
		SCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(TestConstants.pangram);

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		message.setSessionInfo("sessionInfo");
		service.createSession( 10, message);

		try {
			MsgCallback callback = new MsgCallback(service);
			service.send(-1, message, callback);
		} catch (Exception e) {
			ex = e;
		}

		service.deleteSession();
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void execute_timeoutExpiresOnServer_throwsException() throws Exception {
		SCMessage message = new SCMessage();
		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		message.setSessionInfo("sessionInfo");
		service.createSession( 10, message);

		MsgCallback callback = new MsgCallback(service);
		service.send(2, new SCMessage("timeout 4000"), callback);
		// wait until message received
		while (messageReceived == false)
			;
		ex = callback.exc;

		service.deleteSession();
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void execute_timeoutCloselyExpires_throwsException() throws Exception {
		SCMessage message = new SCMessage();
		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		message.setSessionInfo("sessionInfo");
		service.createSession( 10, message);

		MsgCallback callback = new MsgCallback(service);
		service.send(2, new SCMessage("timeout 2000"), callback);
		// wait until message received
		while (messageReceived == false)
			;
		ex = callback.exc;
		service.deleteSession();
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void execute_timeoutIsEnough_returnsSameMessage() throws Exception {
		SCMessage message = new SCMessage("timeout 1500");

		SCSessionService service = client.newSessionService(TestConstants.sesServiceName1);
		message.setSessionInfo("sessionInfo");
		service.createSession( 10, message);

		MsgCallback callback = new MsgCallback(service);
		service.send(2, message, callback);
		// wait until message received
		while (messageReceived == false)
			;
		SCMessage response = callback.response;

		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.isFault(), response.isFault());
		service.deleteSession();
	}

	private class MsgCallback extends SCMessageCallback {
		private SCMessage response = null;
		private volatile Exception exc = null;

		public MsgCallback(SCService service) {
			super(service);
		}

		@Override
		public void receive(SCMessage msg) {
			response = msg;
			ExecuteAsynchronousTest.messageReceived = true;
		}

		@Override
		public void receive(Exception e) {
			logger.error("callback", e);
			exc = e;
			ExecuteAsynchronousTest.messageReceived = true;
		}

	}

}
