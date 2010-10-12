package org.serviceconnector.test.system.cascade;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageFault;
import org.serviceconnector.api.srv.ISCSessionServerCallback;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.cln.StartSessionClient;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.ctrl.util.TestConstants;

public class SessionServerTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SessionServerTest.class);

	private int threadCount = 0;
	private SrvCallback srvCallback;
	private SCSessionServer server;

	private static Process sc0Process;
	private static Process scProcessCascaded;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
		try {
			sc0Process = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
			sc0Process = ctrl.startSC(TestConstants.log4jSC1Properties, TestConstants.scPropertiesCascaded);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
			throw e;
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(sc0Process, TestConstants.log4jSC0Properties);
		ctrl.stopProcess(scProcessCascaded, TestConstants.log4jSC1Properties);
		ctrl = null;
		sc0Process = null;
		scProcessCascaded = null;
	}

	@Before
	public void setUp() throws Exception {
		threadCount = Thread.activeCount();
		server = new SCSessionServer();
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		srvCallback = new SrvCallback();
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP,
				TestConstants.serviceName, 10, 10, srvCallback);

	}

	@After
	public void tearDown() throws Exception {
		server.deregisterServer(TestConstants.serviceName);
		server.destroyServer();
		server = null;
		srvCallback = null;
		assertEquals("number of threads", threadCount, Thread.activeCount());
	}

	@Test
	public void createSession_whiteSpaceSessionInfo_createSessionMessageArrived() throws Exception {
		StartSessionClient client = new StartSessionClient(
				"createSession_whiteSpaceSessionInfo_sessionIdIsNotEmpty");
		client.start();
		client.join();

		assertEquals(2, srvCallback.messagesExchanged);
		assertEquals(true, srvCallback.createSessionMsg instanceof SCMessage);
		assertEquals(false, srvCallback.createSessionMsg.getSessionId() == null
				|| srvCallback.createSessionMsg.getSessionId().isEmpty());
		assertEquals(false, srvCallback.createSessionMsg.isFault());
		assertEquals(null, srvCallback.createSessionMsg.getData());
		assertEquals(true, srvCallback.createSessionMsg.isCompressed());
		assertEquals(" ", srvCallback.createSessionMsg.getMessageInfo());
	}

	@Test
	public void createSession_arbitrarySpaceSessionInfo_createSessionMessageArrived()
			throws Exception {
		StartSessionClient client = new StartSessionClient(
				"createSession_whiteSpaceSessionInfo_sessionIdIsNotEmpty");
		client.start();
		client.join();

		assertEquals(2, srvCallback.messagesExchanged);
		assertEquals(true, srvCallback.createSessionMsg instanceof SCMessage);
		assertEquals(false, srvCallback.createSessionMsg.getSessionId() == null
				|| srvCallback.createSessionMsg.getSessionId().isEmpty());
		assertEquals(false, srvCallback.createSessionMsg.isFault());
		assertEquals(null, srvCallback.createSessionMsg.getData());
		assertEquals(true, srvCallback.createSessionMsg.isCompressed());
		assertEquals(TestConstants.pangram, srvCallback.createSessionMsg.getMessageInfo());
	}

	@Test
	public void createSession_arbitrarySpaceSessionInfoDataOneChar_createSessionMessageArrived()
			throws Exception {
		StartSessionClient client = new StartSessionClient(
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
		assertEquals(TestConstants.pangram, srvCallback.createSessionMsg.getMessageInfo());
	}

	@Test
	public void createSession_256LongSessionInfoData60kBByteArray_createSessionMessageArrived()
			throws Exception {
		StartSessionClient client = new StartSessionClient(
				"createSession_256LongSessionInfoData60kBByteArray_sessionIdIsNotEmpty");
		client.start();
		client.join();

		assertEquals(2, srvCallback.messagesExchanged);
		assertEquals(true, srvCallback.createSessionMsg instanceof SCMessage);
		assertEquals(false, srvCallback.createSessionMsg.getSessionId() == null
				|| srvCallback.createSessionMsg.getSessionId().isEmpty());
		assertEquals(byte[].class, srvCallback.createSessionMsg.getData().getClass());
		assertEquals(TestConstants.dataLength60kB,
				((byte[]) srvCallback.createSessionMsg.getData()).length);
		assertEquals(false, srvCallback.createSessionMsg.isFault());
		assertEquals(true, srvCallback.createSessionMsg.isCompressed());
		assertEquals(TestConstants.stringLength256, srvCallback.createSessionMsg.getMessageInfo());
	}

	@Test
	public void deleteSession_beforeCreateSession_noDeleteSessionArrives() throws Exception {
		StartSessionClient client = new StartSessionClient(
				"deleteSession_beforeCreateSession_noSessionId");
		client.start();
		client.join();

		assertEquals(0, srvCallback.messagesExchanged);
		assertEquals(null, srvCallback.createSessionMsg);
	}

	@Test
	public void deleteSession_afterValidNewSessionService_deleteSessionMessageArrives()
			throws Exception {
		StartSessionClient client = new StartSessionClient(
				"deleteSession_afterValidNewSessionService_noSessionId");
		client.start();
		client.join();

		assertEquals(2, srvCallback.messagesExchanged);
		assertEquals(true, srvCallback.deleteSessionMsg instanceof SCMessage);
		assertEquals(false, srvCallback.deleteSessionMsg.getSessionId() == null
				|| srvCallback.deleteSessionMsg.getSessionId().isEmpty());
		assertEquals(null, srvCallback.deleteSessionMsg.getData());
		assertEquals(false, srvCallback.deleteSessionMsg.isFault());
		assertEquals(null, srvCallback.deleteSessionMsg.getMessageInfo());
		assertEquals(true, srvCallback.deleteSessionMsg.isCompressed());
	}

	// TODO FJU Should exchange 4 messages in total
	@Test
	public void createSession_rejectTheSessionThenCreateValidSessionThenExecuteAMessage_4messagesArrive()
			throws Exception {
		StartSessionClient client = new StartSessionClient(
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
		assertEquals(srvCallback.createSessionMsg.getSessionId(), srvCallback.executeMsg
				.getSessionId());
		assertEquals(null, srvCallback.executeMsg.getData());
		assertEquals(null, srvCallback.executeMsg.getMessageInfo());
		assertEquals(false, srvCallback.executeMsg.isFault());
		assertEquals(true, srvCallback.executeMsg.isCompressed());
		assertEquals(true, srvCallback.deleteSessionMsg instanceof SCMessage);
		assertEquals(srvCallback.createSessionMsg.getSessionId(), srvCallback.deleteSessionMsg
				.getSessionId());
		assertEquals(null, srvCallback.deleteSessionMsg.getData());
		assertEquals(null, srvCallback.deleteSessionMsg.getMessageInfo());
		assertEquals(false, srvCallback.deleteSessionMsg.isFault());
		assertEquals(true, srvCallback.deleteSessionMsg.isCompressed());
		assertEquals(null, srvCallback.abortSessionMsg);
	}

	@Test
	public void execute_messageData1MBArray_3messagesArrive() throws Exception {
		StartSessionClient client = new StartSessionClient(
				"execute_messageData1MBArray_returnsTheSameMessageData");
		client.start();
		client.join();

		assertEquals(3, srvCallback.messagesExchanged);
		assertEquals(true, srvCallback.executeMsg instanceof SCMessage);
		assertEquals(srvCallback.createSessionMsg.getSessionId(), srvCallback.executeMsg
				.getSessionId());
		assertEquals(TestConstants.dataLength1MB,
				((byte[]) srvCallback.executeMsg.getData()).length);
		assertEquals(null, srvCallback.executeMsg.getMessageInfo());
		assertEquals(false, srvCallback.executeMsg.isFault());
		assertEquals(false, srvCallback.executeMsg.isCompressed());
	}

	@Test
	public void createSessionExecuteDeleteSession_twice_6MessagesArrive() throws Exception {
		StartSessionClient client = new StartSessionClient(
				"createSessionExecuteDeleteSession_twice_6MessagesArrive");
		client.start();
		client.join();

		assertEquals(6, srvCallback.messagesExchanged);
	}

	// TODO FJU how can I access echo messages from the API? probably not...
	@Test
	public void echo_waitFor3EchoMessages_5MessagesArrive() throws Exception {
		StartSessionClient client = new StartSessionClient(
				"echo_waitFor3EchoMessages_5MessagesArrive");
		client.start();
		client.join();

		assertEquals(5, srvCallback.messagesExchanged);
	}

	private class SrvCallback implements ISCSessionServerCallback {

		private int messagesExchanged = 0;
		private SCMessage createSessionMsg = null;
		private SCMessage deleteSessionMsg = null;
		private SCMessage abortSessionMsg = null;
		private SCMessage executeMsg = null;

		public SrvCallback() {
		}

		@Override
		public SCMessage createSession(SCMessage message) {
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
		public void deleteSession(SCMessage message) {
			messagesExchanged++;
			deleteSessionMsg = message;
		}

		@Override
		public void abortSession(SCMessage message) {
			messagesExchanged++;
			abortSessionMsg = message;
		}

		@Override
		public SCMessage execute(SCMessage request) {
			messagesExchanged++;
			Object data = request.getData();
			// watch out for timeout server message
			if (data.getClass() == String.class) {
				String dataString = (String) data;
				if (dataString.startsWith("timeout")) {
					int millis = Integer.parseInt(dataString.split(" ")[1]);
					try {
						logger.info("Sleeping " + dataString.split(" ")[1]
								+ "ms in order to timeout.");
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
