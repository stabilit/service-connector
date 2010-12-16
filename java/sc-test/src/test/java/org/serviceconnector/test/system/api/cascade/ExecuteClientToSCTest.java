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

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;

public class ExecuteClientToSCTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ExecuteClientToSCTest.class);

//	private static ProcessCtx scCtx;
//	private static ProcessCtx scCasCtx;
//	private static ProcessCtx srvCtx;
//
//	//private int threadCount = 0;
//	private SCMgmtClient client;
//	private Exception ex;
//
//	private static ProcessesController ctrl;
//
//	@BeforeClass
//	public static void beforeAllTests() throws Exception {
//		ctrl = new ProcessesController();
//	}
//
//	@Before
//	public void beforeOneTest() throws Exception {
//		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
//		scCasCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCcascadedProperties);
//		srvCtx = ctrl.startServer(TestConstants.SERVER_TYPE_SESSION, TestConstants.log4jSrvProperties, 
//				TestConstants.sesServerName1, TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, 10, 
//				TestConstants.sesServiceName1);
//
//		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
//		client.attach(5);
//	}
//	
//
//	@After
//	public void afterOneTest() throws Exception {
//		try {
//			client.detach();
//		} catch (Exception e) {}
//		try {
//			ctrl.stopServer(srvCtx);
//		} catch (Exception e) {}
//		try {
//			ctrl.stopServer(scCasCtx);
//		} catch (Exception e) {}
//		try {
//			ctrl.stopSC(scCtx);
//		} catch (Exception e) {}
//		srvCtx = null;
//		scCtx = null;
//
//		client = null;
//	}
//
//	@AfterClass
//	public static void afterAllTests() throws Exception {
//		ctrl = null;
//	}
//
//
//	@Test(expected = SCServiceException.class)
//	public void execute_beforeCreateSession_throwsException() throws Exception {
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		sessionService.execute(new SCMessage());
//	}
//
//	@Test
//	public void execute_messageDataEmptyString_returnsTheSameMessageData() throws Exception {
//
//		SCMessage message = new SCMessage("");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession(10, message);
//
//		SCMessage response = sessionService.execute(message);
//		sessionService.deleteSession();
//
//		Assert.assertEquals(null, response.getData());
//		Assert.assertEquals(message.getMessageInfo(), response.getMessageInfo());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(message.isFault(), response.isFault());
//	}
//
//	@Test
//	public void execute_messageDataSingleChar_returnsTheSameMessageData() throws Exception {
//
//		SCMessage message = new SCMessage("a");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = sessionService.execute(message);
//		sessionService.deleteSession();
//
//		Assert.assertEquals(message.getData().toString(), response.getData().toString());
//		Assert.assertEquals(message.getMessageInfo(), response.getMessageInfo());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(message.isFault(), response.isFault());
//	}
//
//	@Test
//	public void execute_messageDataArbitrary_returnsTheSameMessageData() throws Exception {
//
//		SCMessage message = new SCMessage("The quick brown fox jumps over a lazy dog.");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = sessionService.execute(message);
//		sessionService.deleteSession();
//
//		Assert.assertEquals(message.getData(), response.getData());
//		Assert.assertEquals(message.getMessageInfo(), response.getMessageInfo());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(message.isFault(), response.isFault());
//	}
//
//	@Test
//	public void execute_messageData1MBArray_returnsTheSameMessageData() throws Exception {
//
//		SCMessage message = new SCMessage(new byte[TestConstants.dataLength1MB]);
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = sessionService.execute(message);
//		sessionService.deleteSession();
//
//		Assert.assertEquals(((byte[]) message.getData()).length, ((byte[]) response.getData()).length);
//		Assert.assertEquals(message.getMessageInfo(), response.getMessageInfo());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(message.isFault(), response.isFault());
//	}
//
//	@Test
//	public void execute_messageWhiteSpaceMessageInfo_returnsTheSameMessage() throws Exception {
//
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo(" ");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = sessionService.execute(message);
//		sessionService.deleteSession();
//
//		Assert.assertEquals(message.getData().toString(), response.getData().toString());
//		Assert.assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(message.isFault(), response.isFault());
//	}
//
//	@Test
//	public void execute_messageSingleCharMessageInfo_returnsTheSameMessage() throws Exception {
//
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("a");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = sessionService.execute(message);
//		sessionService.deleteSession();
//
//		Assert.assertEquals(message.getData().toString(), response.getData().toString());
//		Assert.assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(message.isFault(), response.isFault());
//	}
//
//	@Test
//	public void execute_messageArbitraryMessageInfo_returnsTheSameMessage() throws Exception {
//
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = sessionService.execute(message);
//		sessionService.deleteSession();
//
//		Assert.assertEquals(message.getData().toString(), response.getData().toString());
//		Assert.assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(message.isFault(), response.isFault());
//	}
//
//	@Test
//	public void execute_messageCompressedTrue_returnsTheSameMessage() throws Exception {
//
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//		message.setCompressed(true);
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = sessionService.execute(message);
//		sessionService.deleteSession();
//
//		Assert.assertEquals(message.getData().toString(), response.getData().toString());
//		Assert.assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(message.isFault(), response.isFault());
//	}
//
//	@Test
//	public void execute_messageCompressedFalse_returnsTheSameMessage() throws Exception {
//
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//		message.setCompressed(false);
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = sessionService.execute(message);
//		sessionService.deleteSession();
//
//		Assert.assertEquals(message.getData().toString(), response.getData().toString());
//		Assert.assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(message.isFault(), response.isFault());
//	}
//
//	@Test
//	public void execute_messageSessionIdEmptyString_returnsTheSameMessage() throws Exception {
//
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//		((SCMessage) message).setSessionId("");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = sessionService.execute(message);
//		sessionService.deleteSession();
//
//		Assert.assertEquals(message.getData().toString(), response.getData().toString());
//		Assert.assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(sessionService.getSessionId(), response.getSessionId());
//		Assert.assertEquals(message.isFault(), response.isFault());
//	}
//
//	@Test
//	public void execute_messageSessionIdWhiteSpaceString_returnsTheSameMessage() throws Exception {
//
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//		((SCMessage) message).setSessionId(" ");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = sessionService.execute(message);
//		sessionService.deleteSession();
//
//		Assert.assertEquals(message.getData().toString(), response.getData().toString());
//		Assert.assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(sessionService.getSessionId(), response.getSessionId());
//		Assert.assertEquals(message.isFault(), response.isFault());
//	}
//
//	@Test
//	public void execute_messageSessionIdSingleChar_returnsTheSameMessage() throws Exception {
//
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//		((SCMessage) message).setSessionId("a");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = sessionService.execute(message);
//		sessionService.deleteSession();
//
//		Assert.assertEquals(message.getData().toString(), response.getData().toString());
//		Assert.assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(sessionService.getSessionId(), response.getSessionId());
//		Assert.assertEquals(message.isFault(), response.isFault());
//	}
//
//	@Test
//	public void execute_messageSessionIdArbitraryString_returnsTheSameMessage() throws Exception {
//
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//		((SCMessage) message).setSessionId("The quick brown fox jumps over a lazy dog.");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = sessionService.execute(message);
//		sessionService.deleteSession();
//
//		Assert.assertEquals(message.getData().toString(), response.getData().toString());
//		Assert.assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(sessionService.getSessionId(), response.getSessionId());
//		Assert.assertEquals(message.isFault(), response.isFault());
//	}
//
//	@Test
//	public void execute_messageSessionIdLikeSessionIdString_returnsCorrectSessionId() throws Exception {
//
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//		((SCMessage) message).setSessionId("aaaa0000-bb11-cc22-dd33-eeeeee444444");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = sessionService.execute(message);
//		sessionService.deleteSession();
//
//		Assert.assertEquals(message.getData().toString(), response.getData().toString());
//		Assert.assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(sessionService.getSessionId(), response.getSessionId());
//		Assert.assertEquals(false, message.getSessionId().equals(response.getSessionId()));
//		Assert.assertEquals(message.isFault(), response.isFault());
//	}
//
//	@Test
//	public void execute_messageSessionIdSetManually_returnsTheSameMessage() throws Exception {
//
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		((SCMessage) message).setSessionId(sessionService.getSessionId());
//
//		SCMessage response = sessionService.execute(message);
//		sessionService.deleteSession();
//
//		Assert.assertEquals(message.getData().toString(), response.getData().toString());
//		Assert.assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(sessionService.getSessionId(), response.getSessionId());
//		Assert.assertEquals(message.getSessionId(), response.getSessionId());
//		Assert.assertEquals(message.isFault(), response.isFault());
//	}
//
//	@Test
//	public void execute_messageSessionIdSetToIdOfDifferentSessionSameServiceThanExecuting_returnsCorrectSessionId()
//			throws Exception {
//
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//
//		SCSessionService sessionService0 = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService0.createSession( 10, message);
//
//		SCSessionService sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService1.createSession( 10, message);
//
//		((SCMessage) message).setSessionId(sessionService1.getSessionId());
//
//		SCMessage response = sessionService0.execute(message);
//		sessionService0.deleteSession();
//		sessionService1.deleteSession();
//
//		Assert.assertEquals(message.getData().toString(), response.getData().toString());
//		Assert.assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(sessionService0.getSessionId(), response.getSessionId());
//		Assert.assertEquals(false, message.getSessionId().equals(response.getSessionId()));
//		Assert.assertEquals(message.isFault(), response.isFault());
//	}
//
//	@Test
//	public void execute_messageSessionIdSetToIdOfDifferentSessionServiceThanExecuting_returnsCorrectSessionId()
//			throws Exception {
//
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//
//		SCSessionService sessionService0 = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService0.createSession( 10, message);
//
//		SCSessionService sessionService1 = client.newSessionService(TestConstants.pubServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService1.createSession( 10, message);
//
//		((SCMessage) message).setSessionId(sessionService1.getSessionId());
//
//		SCMessage response = sessionService0.execute(message);
//		sessionService0.deleteSession();
//		sessionService1.deleteSession();
//
//		Assert.assertEquals(message.getData().toString(), response.getData().toString());
//		Assert.assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(sessionService0.getSessionId(), response.getSessionId());
//		Assert.assertEquals(false, message.getSessionId().equals(response.getSessionId()));
//		Assert.assertEquals(message.isFault(), response.isFault());
//	}
//
//	@Test
//	public void execute_timeout1_returnsSameMessage() throws Exception {
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = sessionService.execute(1, message);
//
//		Assert.assertEquals(message.getData().toString(), response.getData().toString());
//		Assert.assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(sessionService.getSessionId(), response.getSessionId());
//		Assert.assertEquals(message.isFault(), response.isFault());
//
//		sessionService.deleteSession();
//	}
//
//	@Test
//	public void execute_timeout2_returnsSameMessage() throws Exception {
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = sessionService.execute(2, message);
//
//		Assert.assertEquals(message.getData().toString(), response.getData().toString());
//		Assert.assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(sessionService.getSessionId(), response.getSessionId());
//		Assert.assertEquals(message.isFault(), response.isFault());
//
//		sessionService.deleteSession();
//	}
//
//	@Test
//	public void execute_timeout0_returnsSameMessage() throws Exception {
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = null;
//		try {
//			response = sessionService.execute(0, message);
//		} catch (Exception e) {
//			ex = e;
//		}
//		Assert.assertEquals(true, ex instanceof SCMPValidatorException);
//		Assert.assertEquals(null, response);
//
//		sessionService.deleteSession();
//	}
//
//	@Test
//	public void execute_timeoutMinus1_returnsSameMessage() throws Exception {
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = null;
//		try {
//			response = sessionService.execute(-1, message);
//		} catch (Exception e) {
//			ex = e;
//		}
//		Assert.assertEquals(true, ex instanceof SCMPValidatorException);
//		Assert.assertEquals(null, response);
//
//		sessionService.deleteSession();
//	}
//
//	@Test
//	public void execute_timeoutIntMin_returnsSameMessage() throws Exception {
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = null;
//		try {
//			response = sessionService.execute(Integer.MIN_VALUE, message);
//		} catch (Exception e) {
//			ex = e;
//		}
//		Assert.assertEquals(true, ex instanceof SCMPValidatorException);
//		Assert.assertEquals(null, response);
//
//		sessionService.deleteSession();
//	}
//
//	@Test
//	public void execute_timeoutIntMax_returnsSameMessage() throws Exception {
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = null;
//		try {
//			response = sessionService.execute(Integer.MAX_VALUE, message);
//		} catch (Exception e) {
//			ex = e;
//		}
//		Assert.assertEquals(true, ex instanceof SCMPValidatorException);
//		Assert.assertEquals(null, response);
//
//		sessionService.deleteSession();
//	}
//
//	@Test
//	public void execute_timeoutAllowedMax_returnsSameMessage() throws Exception {
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = sessionService.execute(3600, message);
//
//		Assert.assertEquals(message.getData().toString(), response.getData().toString());
//		Assert.assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(message.isFault(), response.isFault());
//		sessionService.deleteSession();
//	}
//
//	@Test
//	public void execute_timeoutAllowedMaxPlus1_returnsSameMessage() throws Exception {
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = null;
//		try {
//			response = sessionService.execute(3601, message);
//		} catch (Exception e) {
//			ex = e;
//		}
//		Assert.assertEquals(true, ex instanceof SCMPValidatorException);
//		Assert.assertEquals(null, response);
//
//		sessionService.deleteSession();
//	}
//
//	@Test
//	public void execute_timeout1_passes() throws Exception {
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = sessionService.execute(1, message);
//
//		Assert.assertEquals(message.getData().toString(), response.getData().toString());
//		Assert.assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(message.isFault(), response.isFault());
//		sessionService.deleteSession();
//	}
//
//	@Test
//	public void execute_timeout2_passes() throws Exception {
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = sessionService.execute(2, message);
//
//		Assert.assertEquals(message.getData().toString(), response.getData().toString());
//		Assert.assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(message.isFault(), response.isFault());
//		sessionService.deleteSession();
//	}
//
//	@Test
//	public void execute_timeoutMaxAllowed_passes() throws Exception {
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = sessionService.execute(3600, message);
//
//		Assert.assertEquals(message.getData().toString(), response.getData().toString());
//		Assert.assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(message.isFault(), response.isFault());
//		sessionService.deleteSession();
//	}
//
//	@Test
//	public void execute_timeoutMaxAllowedPlus1_throwsSCMPValidatorException() throws Exception {
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		try {
//			sessionService.execute(3601, message);
//		} catch (Exception e) {
//			ex = e;
//		}
//
//		Assert.assertEquals(true, ex instanceof SCMPValidatorException);
//		sessionService.deleteSession();
//	}
//
//	@Test
//	public void execute_timeoutIntMax_throwsSCMPValidatorException() throws Exception {
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		try {
//			sessionService.execute(Integer.MAX_VALUE, message);
//		} catch (Exception e) {
//			ex = e;
//		}
//
//		Assert.assertEquals(true, ex instanceof SCMPValidatorException);
//		sessionService.deleteSession();
//	}
//
//	@Test
//	public void execute_timeoutIntMin_throwsSCMPValidatorException() throws Exception {
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		try {
//			sessionService.execute(Integer.MIN_VALUE, message);
//		} catch (Exception e) {
//			ex = e;
//		}
//
//		Assert.assertEquals(true, ex instanceof SCMPValidatorException);
//		sessionService.deleteSession();
//	}
//
//	@Test
//	public void execute_timeout0_throwsSCMPValidatorException() throws Exception {
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		try {
//			sessionService.execute(0, message);
//		} catch (Exception e) {
//			ex = e;
//		}
//
//		Assert.assertEquals(true, ex instanceof SCMPValidatorException);
//		sessionService.deleteSession();
//	}
//
//	@Test
//	public void execute_timeoutMinus1_throwsSCMPValidatorException() throws Exception {
//		SCMessage message = new SCMessage("Ahoj");
//		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		try {
//			sessionService.execute(-1, message);
//		} catch (Exception e) {
//			ex = e;
//		}
//
//		Assert.assertEquals(true, ex instanceof SCMPValidatorException);
//		sessionService.deleteSession();
//	}
//
//	@Test
//	public void execute_timeoutExpiresOnServer_throwsException() throws Exception {
//		SCMessage message = new SCMessage();
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		try {
//			sessionService.execute(2, new SCMessage("timeout 4000"));
//		} catch (Exception e) {
//			ex = e;
//		}
//		Assert.assertEquals(true, ex instanceof SCServiceException);
//		sessionService.deleteSession();
//	}
//
//	@Test
//	public void execute_timeoutCloselyExpires_throwsException() throws Exception {
//		SCMessage message = new SCMessage();
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		try {
//			sessionService.execute(2, new SCMessage("timeout 2000"));
//		} catch (Exception e) {
//			ex = e;
//		}
//		Assert.assertEquals(true, ex instanceof SCServiceException);
//		sessionService.deleteSession();
//	}
//
//	@Test
//	public void execute_timeoutIsEnough_returnsSameMessage() throws Exception {
//		SCMessage message = new SCMessage("timeout 1500");
//
//		SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//		message.setSessionInfo("sessionInfo");
//		sessionService.createSession( 10, message);
//
//		SCMessage response = sessionService.execute(2, message);
//		Assert.assertEquals(message.getData().toString(), response.getData().toString());
//		Assert.assertEquals(message.getMessageInfo(), response.getMessageInfo());
//		Assert.assertEquals(message.isCompressed(), response.isCompressed());
//		Assert.assertEquals(message.isFault(), response.isFault());
//		sessionService.deleteSession();
//	}
}
