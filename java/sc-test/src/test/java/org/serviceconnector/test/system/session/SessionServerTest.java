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
import org.serviceconnector.api.SCMessageFault;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.api.srv.SCSessionServerCallback;
import org.serviceconnector.cln.TestSessionClient;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;

public class SessionServerTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SessionServerTest.class);

	private SrvCallback srvCallback;
	private SCSessionServer server;

	private static ProcessCtx scCtx;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		try {
			ctrl.stopSC(scCtx);
		} catch (Exception e) {	}
		scCtx = null;
		ctrl = null;
	}

	@Before
	public void beforeOneTest() throws Exception {
		server = new SCSessionServer(TestConstants.HOST, TestConstants.PORT_LISTENER);
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		srvCallback = new SrvCallback();
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.sesServiceName1, 10, 10,
				srvCallback);

	}

	@After
	public void afterOneTest() throws Exception {
		server.deregister(TestConstants.sesServiceName1);
		server.destroy();
		server = null;
		srvCallback = null;
	}

	@Test
	public void createSession_whiteSpaceSessionInfo_createSessionMessageArrived() throws Exception {
		TestSessionClient client = new TestSessionClient("createSession_whiteSpaceSessionInfo_sessionIdIsNotEmpty");
		client.start();
		client.join();

		assertEquals(2, srvCallback.messagesExchanged);
		assertEquals(true, srvCallback.createSessionMsg instanceof SCMessage);
		assertEquals(false, srvCallback.createSessionMsg.getSessionId() == null
				|| srvCallback.createSessionMsg.getSessionId().isEmpty());
		assertEquals(false, srvCallback.createSessionMsg.isFault());
		assertEquals(null, srvCallback.createSessionMsg.getData());
		assertEquals(true, srvCallback.createSessionMsg.isCompressed());
	}

	@Test
	public void createSession_arbitrarySpaceSessionInfo_createSessionMessageArrived() throws Exception {
		TestSessionClient client = new TestSessionClient("createSession_whiteSpaceSessionInfo_sessionIdIsNotEmpty");
		client.start();
		client.join();

		assertEquals(2, srvCallback.messagesExchanged);
		assertEquals(true, srvCallback.createSessionMsg instanceof SCMessage);
		assertEquals(false, srvCallback.createSessionMsg.getSessionId() == null
				|| srvCallback.createSessionMsg.getSessionId().isEmpty());
		assertEquals(false, srvCallback.createSessionMsg.isFault());
		assertEquals(null, srvCallback.createSessionMsg.getData());
		assertEquals(true, srvCallback.createSessionMsg.isCompressed());
		assertEquals(null, srvCallback.createSessionMsg.getMessageInfo());
	}

	@Test
	public void createSession_arbitrarySpaceSessionInfoDataOneChar_createSessionMessageArrived() throws Exception {
		TestSessionClient client = new TestSessionClient(
				"createSession_arbitrarySpaceSessionInfoDataOneChar_sessionIdIsNotEmpty");
		client.start();
		client.join();

		assertEquals(2, srvCallback.messagesExchanged);
		assertEquals(true, srvCallback.createSessionMsg instanceof SCMessage);
		assertEquals(false, srvCallback.createSessionMsg.getSessionId() == null
				|| srvCallback.createSessionMsg.getSessionId().isEmpty());
		assertEquals("a", srvCallback.createSessionMsg.getData().toString());
		assertEquals(false, srvCallback.createSessionMsg.isFault());
		assertEquals(true, srvCallback.createSessionMsg.isCompressed());
		assertEquals(null, srvCallback.createSessionMsg.getMessageInfo());
	}

	@Test
	public void createSession_256LongSessionInfoData60kBByteArray_createSessionMessageArrived() throws Exception {
		TestSessionClient client = new TestSessionClient(
				"createSession_256LongSessionInfoData60kBByteArray_sessionIdIsNotEmpty");
		client.start();
		client.join();

		assertEquals(2, srvCallback.messagesExchanged);
		assertEquals(true, srvCallback.createSessionMsg instanceof SCMessage);
		assertEquals(false, srvCallback.createSessionMsg.getSessionId() == null
				|| srvCallback.createSessionMsg.getSessionId().isEmpty());
		assertEquals(byte[].class, srvCallback.createSessionMsg.getData().getClass());
		assertEquals(TestConstants.dataLength60kB, ((byte[]) srvCallback.createSessionMsg.getData()).length);
		assertEquals(false, srvCallback.createSessionMsg.isFault());
		assertEquals(true, srvCallback.createSessionMsg.isCompressed());
		assertEquals(null, srvCallback.createSessionMsg.getMessageInfo());
	}

	@Test
	public void deleteSession_beforeCreateSession_noDeleteSessionArrives() throws Exception {
		TestSessionClient client = new TestSessionClient("deleteSession_beforeCreateSession_noSessionId");
		client.start();
		client.join();

		assertEquals(0, srvCallback.messagesExchanged);
		assertEquals(null, srvCallback.createSessionMsg);
	}

	@Test
	public void deleteSession_afterValidNewSessionService_deleteSessionMessageArrives() throws Exception {
		TestSessionClient client = new TestSessionClient("deleteSession_afterValidNewSessionService_noSessionId");
		client.start();
		client.join();

		assertEquals(2, srvCallback.messagesExchanged);
		assertEquals(true, srvCallback.deleteSessionMsg instanceof SCMessage);
		assertEquals(false, srvCallback.deleteSessionMsg.getSessionId() == null
				|| srvCallback.deleteSessionMsg.getSessionId().isEmpty());
		assertEquals(null, srvCallback.deleteSessionMsg.getData());
		assertEquals(false, srvCallback.deleteSessionMsg.isFault());
		assertEquals(null, srvCallback.deleteSessionMsg.getMessageInfo());
		assertEquals(false, srvCallback.deleteSessionMsg.isCompressed());
	}

	@Test
	public void createSession_rejectTheSessionThenCreateValidSessionThenExecuteAMessage_4messagesArrive()
			throws Exception {
		TestSessionClient client = new TestSessionClient(
				"createSession_rejectTheSessionThenCreateValidSessionThenExecuteAMessage_passes");
		client.start();
		client.join();

		assertEquals(4, srvCallback.messagesExchanged);
		assertEquals(true, srvCallback.createSessionMsg instanceof SCMessage);
		assertEquals(false, srvCallback.createSessionMsg.getSessionId() == null
				|| srvCallback.createSessionMsg.getSessionId().isEmpty());
		assertEquals(null, srvCallback.createSessionMsg.getData());
		assertEquals(null, srvCallback.createSessionMsg.getMessageInfo());
		assertEquals(false, srvCallback.createSessionMsg.isFault());
		assertEquals(true, srvCallback.createSessionMsg.isCompressed());
		assertEquals(true, srvCallback.executeMsg instanceof SCMessage);
		assertEquals(srvCallback.createSessionMsg.getSessionId(), srvCallback.executeMsg.getSessionId());
		assertEquals(null, srvCallback.executeMsg.getData());
		assertEquals(null, srvCallback.executeMsg.getMessageInfo());
		assertEquals(false, srvCallback.executeMsg.isFault());
		assertEquals(true, srvCallback.executeMsg.isCompressed());
		assertEquals(true, srvCallback.deleteSessionMsg instanceof SCMessage);
		assertEquals(srvCallback.createSessionMsg.getSessionId(), srvCallback.deleteSessionMsg.getSessionId());
		assertEquals(null, srvCallback.deleteSessionMsg.getData());
		assertEquals(null, srvCallback.deleteSessionMsg.getMessageInfo());
		assertEquals(false, srvCallback.deleteSessionMsg.isFault());
		assertEquals(false, srvCallback.deleteSessionMsg.isCompressed());
		assertEquals(null, srvCallback.abortSessionMsg);
	}

	@Test
	public void execute_messageData1MBArray_3messagesArrive() throws Exception {
		TestSessionClient client = new TestSessionClient("execute_messageData1MBArray_returnsTheSameMessageData");
		client.start();
		client.join();

		assertEquals(3, srvCallback.messagesExchanged);
		assertEquals(true, srvCallback.executeMsg instanceof SCMessage);
		assertEquals(srvCallback.createSessionMsg.getSessionId(), srvCallback.executeMsg.getSessionId());
		assertEquals(TestConstants.dataLength1MB, ((byte[]) srvCallback.executeMsg.getData()).length);
		assertEquals(null, srvCallback.executeMsg.getMessageInfo());
		assertEquals(false, srvCallback.executeMsg.isFault());
		assertEquals(false, srvCallback.executeMsg.isCompressed());
	}

	@Test
	public void createSessionExecuteDeleteSession_twice_6MessagesArrive() throws Exception {
		TestSessionClient client = new TestSessionClient("createSessionExecuteDeleteSession_twice_6MessagesArrive");
		client.start();
		client.join();

		assertEquals(6, srvCallback.messagesExchanged);
	}

	private class SrvCallback extends SCSessionServerCallback {

		private int messagesExchanged = 0;
		private SCMessage createSessionMsg = null;
		private SCMessage deleteSessionMsg = null;
		private SCMessage abortSessionMsg = null;
		private SCMessage executeMsg = null;

		public SrvCallback() {
		}

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
						logger.error("rejecting create session", e);
					}
					logger.info("rejecting session");
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
			if (data != null && data.getClass() == String.class) {
				String dataString = (String) data;
				if (dataString.startsWith("timeout")) {
					int millis = Integer.parseInt(dataString.split(" ")[1]);
					try {
						logger.info("Sleeping " + dataString.split(" ")[1] + "ms in order to timeout.");
						Thread.sleep(millis);
					} catch (InterruptedException e) {
						logger.error("sleep in execute", e);
					}
				}
			}
			executeMsg = request;
			return request;
		}
	}
}
