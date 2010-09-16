package system;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.sc.cln.StartSessionClient;
import com.stabilit.sc.common.cmd.SCMPValidatorException;
import com.stabilit.sc.common.service.ISCMessage;
import com.stabilit.sc.common.service.SCMessageFault;
import com.stabilit.sc.ctrl.util.TestConstants;
import com.stabilit.sc.ctrl.util.TestEnvironmentController;
import com.stabilit.sc.srv.ISCServer;
import com.stabilit.sc.srv.ISCSessionServerCallback;
import com.stabilit.sc.srv.SCServer;

public class CreateSessionDeleteSessionServerToSCTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger
			.getLogger(CreateSessionDeleteSessionServerToSCTest.class);

	private SrvCallback srvCallback;
	private ISCServer server;

	private static Process p;

	private static TestEnvironmentController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
		try {
			p = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
			throw e;
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(p, TestConstants.log4jSC0Properties);
		ctrl = null;
		p = null;
	}

	@Before
	public void setUp() throws Exception {
		server = new SCServer();
		server.startListener(TestConstants.HOST, 9001, 0);
		srvCallback = new SrvCallback(new SessionServerContext());
		server.registerService(TestConstants.HOST, TestConstants.PORT9000,
				TestConstants.serviceName, 10, 10, srvCallback);

	}

	@After
	public void tearDown() throws Exception {
		server.deregisterService(TestConstants.serviceName);
		server.destroyServer();
		server = null;
		srvCallback = null;
	}

	@Test
	public void createSession_whiteSpaceSessionInfo_createSessionMessageArrived() throws Exception {
		StartSessionClient client = new StartSessionClient(
				"createSession_whiteSpaceSessionInfo_sessionIdIsNotEmpty");
		client.start();
		client.join();

		assertEquals(2, srvCallback.messagesExchanged);
		assertEquals(true, srvCallback.createSessionMsg instanceof ISCMessage);
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
		assertEquals(true, srvCallback.createSessionMsg instanceof ISCMessage);
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
		assertEquals(true, srvCallback.createSessionMsg instanceof ISCMessage);
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
		assertEquals(true, srvCallback.createSessionMsg instanceof ISCMessage);
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
		assertEquals(true, srvCallback.deleteSessionMsg instanceof ISCMessage);
		assertEquals(false, srvCallback.deleteSessionMsg.getSessionId() == null
				|| srvCallback.deleteSessionMsg.getSessionId().isEmpty());
		assertEquals(null, srvCallback.deleteSessionMsg.getData());
		assertEquals(false, srvCallback.deleteSessionMsg.isFault());
		assertEquals(null, srvCallback.deleteSessionMsg.getMessageInfo());
		assertEquals(true, srvCallback.deleteSessionMsg.isCompressed());
	}

	//TODO Should exchange 4 messages in total 
	@Test
	public void createSession_rejectTheSessionThenCreateValidSessionThenExecuteAMessage_4messagesArrive()
			throws Exception {
		StartSessionClient client = new StartSessionClient(
				"createSession_rejectTheSessionThenCreateValidSessionThenExecuteAMessage_passes");
		client.start();
		client.join();
		
		assertEquals(4, srvCallback.messagesExchanged);
		assertEquals(true, srvCallback.createSessionMsg instanceof ISCMessage);
		assertEquals(false, srvCallback.createSessionMsg.getSessionId() == null
				|| srvCallback.createSessionMsg.getSessionId().isEmpty());
		assertEquals(null, srvCallback.createSessionMsg.getData());
		assertEquals(null, srvCallback.createSessionMsg.getMessageInfo());
		assertEquals(false, srvCallback.createSessionMsg.isFault());
		assertEquals(true, srvCallback.createSessionMsg.isCompressed());
		assertEquals(true, srvCallback.executeMsg instanceof ISCMessage);
		assertEquals(srvCallback.createSessionMsg.getSessionId(), srvCallback.executeMsg.getSessionId());
		assertEquals(null, srvCallback.executeMsg.getData());
		assertEquals(null, srvCallback.executeMsg.getMessageInfo());
		assertEquals(false, srvCallback.executeMsg.isFault());
		assertEquals(true, srvCallback.executeMsg.isCompressed());
		assertEquals(true, srvCallback.deleteSessionMsg instanceof ISCMessage);
		assertEquals(srvCallback.createSessionMsg.getSessionId(), srvCallback.deleteSessionMsg.getSessionId());
		assertEquals(null, srvCallback.deleteSessionMsg.getData());
		assertEquals(null, srvCallback.deleteSessionMsg.getMessageInfo());
		assertEquals(false, srvCallback.deleteSessionMsg.isFault());
		assertEquals(true, srvCallback.deleteSessionMsg.isCompressed());
		assertEquals(null, srvCallback.abortSessionMsg);
	}
	
	@Test
	public void execute_messageData1MBArray_3messagesArrive()
			throws Exception {
		StartSessionClient client = new StartSessionClient(
				"execute_messageData1MBArray_returnsTheSameMessageData");
		client.start();
		client.join();
		
		assertEquals(3, srvCallback.messagesExchanged);
		assertEquals(true, srvCallback.executeMsg instanceof ISCMessage);
		assertEquals(srvCallback.createSessionMsg.getSessionId(), srvCallback.executeMsg.getSessionId());
		assertEquals(TestConstants.dataLength1MB, ((byte[])srvCallback.executeMsg.getData()).length);
		assertEquals(null, srvCallback.executeMsg.getMessageInfo());
		assertEquals(false, srvCallback.executeMsg.isFault());
		assertEquals(false, srvCallback.executeMsg.isCompressed());
	}

	private class SessionServerContext {
		public ISCServer getServer() {
			return server;
		}
	}

	class SrvCallback implements ISCSessionServerCallback {

		private int messagesExchanged = 0;
		private ISCMessage createSessionMsg = null;
		private ISCMessage deleteSessionMsg = null;
		private ISCMessage abortSessionMsg = null;
		private ISCMessage executeMsg = null;
		private SessionServerContext outerContext;

		public SrvCallback(SessionServerContext context) {
			this.outerContext = context;
		}

		@Override
		public ISCMessage createSession(ISCMessage message) {
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
		public void deleteSession(ISCMessage message) {
			messagesExchanged++;
			deleteSessionMsg = message;
		}

		@Override
		public void abortSession(ISCMessage message) {
			messagesExchanged++;
			abortSessionMsg = message;
		}

		@Override
		public ISCMessage execute(ISCMessage request) {
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
