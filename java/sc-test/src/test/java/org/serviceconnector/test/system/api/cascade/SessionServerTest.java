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
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.api.srv.SCSessionServerCallback;
import org.serviceconnector.cln.TestSessionClient;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;


public class SessionServerTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SessionServerTest.class);
	/** The Constant logger. */
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	private static ProcessesController ctrl;
	private static ProcessCtx scCtx;
	private static ProcessCtx scCasCtx;		// Cascaded
	private int threadCount = 0;

	private SCServer server;
	private SCSessionServer sessionServer;
	private SrvCallback srvCallback;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
		try {
			scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
			scCasCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCcascadedProperties);
		} catch (Exception e) {
			testLogger.error("beforeAllTests", e);
			throw e;
		}
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		try {
			ctrl.stopServer(scCtx);
		} catch (Exception e) {}
		try {
			ctrl.stopSC(scCasCtx);
		} catch (Exception e) {}
		scCasCtx = null;
		scCtx = null;
		ctrl = null;
	}

	@Before
	public void beforeOneTest() throws Exception {
		threadCount = Thread.activeCount();
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER);
		server.startListener();
//		srvCallback = new SrvCallback();
		
		sessionServer = server.newSessionServer(TestConstants.sesServerName1);
		srvCallback = new SrvCallback(sessionServer);
		sessionServer.register(10, 10, srvCallback);
	}

	@After
	public void afterOneTest() throws Exception {
		server.stopListener();
		sessionServer.deregister();
		server.destroy();
		srvCallback = null;
		Assert.assertEquals("number of threads", threadCount, Thread.activeCount());
	}

	
	@Test
	public void createSession_whiteSpaceSessionInfo_createSessionMessageArrived() throws Exception {
		TestSessionClient client = new TestSessionClient("createSession_whiteSpaceSessionInfo_sessionIdIsNotEmpty");
		client.start();
		client.join();

		Assert.assertEquals(2, srvCallback.messagesExchanged);
		Assert.assertEquals(true, srvCallback.createSessionMsg instanceof SCMessage);
		Assert.assertEquals(false, srvCallback.createSessionMsg.getSessionId() == null
				|| srvCallback.createSessionMsg.getSessionId().isEmpty());
		Assert.assertEquals(false, srvCallback.createSessionMsg.isFault());
		Assert.assertEquals(null, srvCallback.createSessionMsg.getData());
		Assert.assertEquals(true, srvCallback.createSessionMsg.isCompressed());
		Assert.assertEquals(" ", srvCallback.createSessionMsg.getMessageInfo());
	}

	@Test
	public void createSession_arbitrarySpaceSessionInfo_createSessionMessageArrived() throws Exception {
		TestSessionClient client = new TestSessionClient("createSession_whiteSpaceSessionInfo_sessionIdIsNotEmpty");
		client.start();
		client.join();

		Assert.assertEquals(2, srvCallback.messagesExchanged);
		Assert.assertEquals(true, srvCallback.createSessionMsg instanceof SCMessage);
		Assert.assertEquals(false, srvCallback.createSessionMsg.getSessionId() == null
				|| srvCallback.createSessionMsg.getSessionId().isEmpty());
		Assert.assertEquals(false, srvCallback.createSessionMsg.isFault());
		Assert.assertEquals(null, srvCallback.createSessionMsg.getData());
		Assert.assertEquals(true, srvCallback.createSessionMsg.isCompressed());
		Assert.assertEquals(TestConstants.pangram, srvCallback.createSessionMsg.getMessageInfo());
	}

	@Test
	public void createSession_arbitrarySpaceSessionInfoDataOneChar_createSessionMessageArrived() throws Exception {
		TestSessionClient client = new TestSessionClient(
				"createSession_arbitrarySpaceSessionInfoDataOneChar_sessionIdIsNotEmpty");
		client.start();
		client.join();

		Assert.assertEquals(2, srvCallback.messagesExchanged);
		Assert.assertEquals(true, srvCallback.createSessionMsg instanceof SCMessage);
		Assert.assertEquals(false, srvCallback.createSessionMsg.getSessionId() == null
				|| srvCallback.createSessionMsg.getSessionId().isEmpty());
		Assert.assertEquals("a", srvCallback.createSessionMsg.getData().toString());
		Assert.assertEquals(false, srvCallback.createSessionMsg.isFault());
		Assert.assertEquals(true, srvCallback.createSessionMsg.isCompressed());
		Assert.assertEquals(TestConstants.pangram, srvCallback.createSessionMsg.getMessageInfo());
	}

	@Test
	public void createSession_256LongSessionInfoData60kBByteArray_createSessionMessageArrived() throws Exception {
		TestSessionClient client = new TestSessionClient(
				"createSession_256LongSessionInfoData60kBByteArray_sessionIdIsNotEmpty");
		client.start();
		client.join();

		Assert.assertEquals(2, srvCallback.messagesExchanged);
		Assert.assertEquals(true, srvCallback.createSessionMsg instanceof SCMessage);
		Assert.assertEquals(false, srvCallback.createSessionMsg.getSessionId() == null
				|| srvCallback.createSessionMsg.getSessionId().isEmpty());
		Assert.assertEquals(byte[].class, srvCallback.createSessionMsg.getData().getClass());
		Assert.assertEquals(TestConstants.dataLength60kB, ((byte[]) srvCallback.createSessionMsg.getData()).length);
		Assert.assertEquals(false, srvCallback.createSessionMsg.isFault());
		Assert.assertEquals(true, srvCallback.createSessionMsg.isCompressed());
		Assert.assertEquals(TestConstants.stringLength256, srvCallback.createSessionMsg.getMessageInfo());
	}

	@Test
	public void deleteSession_beforeCreateSession_noDeleteSessionArrives() throws Exception {
		TestSessionClient client = new TestSessionClient("deleteSession_beforeCreateSession_noSessionId");
		client.start();
		client.join();

		Assert.assertEquals(0, srvCallback.messagesExchanged);
		Assert.assertEquals(null, srvCallback.createSessionMsg);
	}

	@Test
	public void deleteSession_afterValidNewSessionService_deleteSessionMessageArrives() throws Exception {
		TestSessionClient client = new TestSessionClient("deleteSession_afterValidNewSessionService_noSessionId");
		client.start();
		client.join();

		Assert.assertEquals(2, srvCallback.messagesExchanged);
		Assert.assertEquals(true, srvCallback.deleteSessionMsg instanceof SCMessage);
		Assert.assertEquals(false, srvCallback.deleteSessionMsg.getSessionId() == null
				|| srvCallback.deleteSessionMsg.getSessionId().isEmpty());
		Assert.assertEquals(null, srvCallback.deleteSessionMsg.getData());
		Assert.assertEquals(false, srvCallback.deleteSessionMsg.isFault());
		Assert.assertEquals(null, srvCallback.deleteSessionMsg.getMessageInfo());
		Assert.assertEquals(true, srvCallback.deleteSessionMsg.isCompressed());
	}

	// TODO FJU Should exchange 4 messages in total
	@Test
	public void createSession_rejectTheSessionThenCreateValidSessionThenExecuteAMessage_4messagesArrive()
			throws Exception {
		TestSessionClient client = new TestSessionClient(
				"createSession_rejectTheSessionThenCreateValidSessionThenExecuteAMessage_passes");
		client.start();
		client.join();

		Assert.assertEquals(4, srvCallback.messagesExchanged);
		Assert.assertEquals(true, srvCallback.createSessionMsg instanceof SCMessage);
		Assert.assertEquals(false, srvCallback.createSessionMsg.getSessionId() == null
				|| srvCallback.createSessionMsg.getSessionId().isEmpty());
		Assert.assertEquals(null, srvCallback.createSessionMsg.getData());
		Assert.assertEquals(null, srvCallback.createSessionMsg.getMessageInfo());
		Assert.assertEquals(false, srvCallback.createSessionMsg.isFault());
		Assert.assertEquals(true, srvCallback.createSessionMsg.isCompressed());
		Assert.assertEquals(true, srvCallback.executeMsg instanceof SCMessage);
		Assert.assertEquals(srvCallback.createSessionMsg.getSessionId(), srvCallback.executeMsg.getSessionId());
		Assert.assertEquals(null, srvCallback.executeMsg.getData());
		Assert.assertEquals(null, srvCallback.executeMsg.getMessageInfo());
		Assert.assertEquals(false, srvCallback.executeMsg.isFault());
		Assert.assertEquals(true, srvCallback.executeMsg.isCompressed());
		Assert.assertEquals(true, srvCallback.deleteSessionMsg instanceof SCMessage);
		Assert.assertEquals(srvCallback.createSessionMsg.getSessionId(), srvCallback.deleteSessionMsg.getSessionId());
		Assert.assertEquals(null, srvCallback.deleteSessionMsg.getData());
		Assert.assertEquals(null, srvCallback.deleteSessionMsg.getMessageInfo());
		Assert.assertEquals(false, srvCallback.deleteSessionMsg.isFault());
		Assert.assertEquals(true, srvCallback.deleteSessionMsg.isCompressed());
		Assert.assertEquals(null, srvCallback.abortSessionMsg);
	}

	@Test
	public void execute_messageData1MBArray_3messagesArrive() throws Exception {
		TestSessionClient client = new TestSessionClient("execute_messageData1MBArray_returnsTheSameMessageData");
		client.start();
		client.join();

		Assert.assertEquals(3, srvCallback.messagesExchanged);
		Assert.assertEquals(true, srvCallback.executeMsg instanceof SCMessage);
		Assert.assertEquals(srvCallback.createSessionMsg.getSessionId(), srvCallback.executeMsg.getSessionId());
		Assert.assertEquals(TestConstants.dataLength1MB, ((byte[]) srvCallback.executeMsg.getData()).length);
		Assert.assertEquals(null, srvCallback.executeMsg.getMessageInfo());
		Assert.assertEquals(false, srvCallback.executeMsg.isFault());
		Assert.assertEquals(false, srvCallback.executeMsg.isCompressed());
	}

	@Test
	public void createSessionExecuteDeleteSession_twice_6MessagesArrive() throws Exception {
		TestSessionClient client = new TestSessionClient("createSessionExecuteDeleteSession_twice_6MessagesArrive");
		client.start();
		client.join();

		Assert.assertEquals(6, srvCallback.messagesExchanged);
	}

	// TODO FJU how can I access echo messages from the API? probably not...
	@Test
	public void echo_waitFor3EchoMessages_5MessagesArrive() throws Exception {
		TestSessionClient client = new TestSessionClient("echo_waitFor3EchoMessages_5MessagesArrive");
		client.start();
		client.join();

		Assert.assertEquals(5, srvCallback.messagesExchanged);
	}

	private class SrvCallback extends SCSessionServerCallback {

		public SrvCallback(SCSessionServer scSessionServer) {
			super(scSessionServer);
			// TODO Auto-generated constructor stub
		}

		private int messagesExchanged = 0;
		private SCMessage createSessionMsg = null;
		private SCMessage deleteSessionMsg = null;
		private SCMessage abortSessionMsg = null;
		private SCMessage executeMsg = null;

//		public SrvCallback() {
//		}

		@Override
		public SCMessage createSession(SCMessage message, int operationTimeoutInMillis) {
			messagesExchanged++;
			createSessionMsg = message;
			if (message.getData() != null && message.getData() instanceof String) {
				String dataString = (String) message.getData();
				if (dataString.equals("reject")) {
					SCMessageFault response = new SCMessageFault();
					response.setCompressed(message.isCompressed());
					response.setData(message.getData());
					response.setMessageInfo(message.getMessageInfo());
					try {
						response.setAppErrorCode(0);
						response.setAppErrorText("\"This is the app error text\"");
					} catch (SCMPValidatorException e) {
						testLogger.error("rejecting create session", e);
					}
					testLogger.info("rejecting session");
					return response;
				}
			}
			return message;
		}

		@Override
		public void deleteSession(SCMessage message, int operationTimeoutInMillis) {
			messagesExchanged++;
			deleteSessionMsg = message;
		}

		@Override
		public void abortSession(SCMessage message, int operationTimeoutInMillis) {
			messagesExchanged++;
			abortSessionMsg = message;
		}

		@Override
		public SCMessage execute(SCMessage request, int operationTimeoutInMillis) {
			messagesExchanged++;
			Object data = request.getData();
			// watch out for timeout server message
			if (data.getClass() == String.class) {
				String dataString = (String) data;
				if (dataString.startsWith("timeout")) {
					int millis = Integer.parseInt(dataString.split(" ")[1]);
					try {
						testLogger.info("Sleeping " + dataString.split(" ")[1] + "ms in order to timeout.");
						Thread.sleep(millis);
					} catch (InterruptedException e) {
						testLogger.error("sleep in execute", e);
					}
				}
			}
			executeMsg = request;
			return request;
		}
	}
}
