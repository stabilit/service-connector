package system;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import vmstarters.StartSCSessionServer;

import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.ISCClient;
import com.stabilit.scm.cln.service.ISessionService;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.service.ISCMessage;
import com.stabilit.scm.common.service.SCMessage;
import com.stabilit.scm.common.service.SCServiceException;

public class StubbedServerClientToSCTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(StubbedServerClientToSCTest.class);

	private static Process p;

	private static String userDir;
//	private Process r;

	private ISCClient client;

	private static final String host = "localhost";
	private static final int port8080 = 8080;
	private static final int port9000 = 9000;
	private static String serviceName = "simulation";
	private String serviceNameAlt = "P01_RTXS_sc1";

	private Exception ex;

	private static Thread serverThread;

	@BeforeClass
	public static void oneTimeSetUp() {

		userDir = System.getProperty("user.dir");
		String command = "java -Dlog4j.configuration=file:" + userDir
				+ "\\src\\test\\resources\\log4jSC0.properties -jar " + userDir
				+ "\\..\\service-connector\\target\\sc.jar -filename " + userDir
				+ "\\src\\test\\resources\\scIntegration.properties";

		try {
			p = Runtime.getRuntime().exec(command);

			// lets the SC load before starting communication
			Thread.sleep(1000);
			
			serverThread = new Thread("SERVER") {
				public void run() {
					try {
						StartSCSessionServer.main(new String[] { String.valueOf(port9000), serviceName,
								String.valueOf(100) });
					} catch (Exception e) {
						logger.error("running server thread", e);
					}
				}
			};
			serverThread.start();
			
			// lets the Server load before starting communication
			Thread.sleep(1000);

		} catch (IOException e) {
			logger.error("oneTimeSetUp - IOExc", e);
		} catch (InterruptedException e) {
			logger.error("oneTimeSetUp - InterruptExc", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ISCClient client = new SCClient();
		client.attach(host, port8080);
		while (true) {
			String sessions = client.workload(serviceName);
			if (Integer.parseInt(sessions.substring(0, sessions.indexOf('/'))) > 0) {
				break;
			}
			Thread.sleep(500);
		}
		System.out.println("6.\tTearDown proceed");
		ISessionService session = client.newSessionService(serviceName);
		session.createSession("sessionInfo", 300, 60);
		session.execute(new SCMessage("kill server"));
		Thread.sleep(500);

		p.destroy();
	}

	/**
	 * @throws java.lang.Exception
	 * 
	 *             Create a new SCClient for each test method.
	 */
	@Before
	public void setUp() throws Exception {
		
//		  String command = "java -classpath " + userDir +
//		  "\\target\\classes vmstarters.StartSCSessionServer 9000 " +
//		  serviceName; System.out.println(command);
//		  r = Runtime.getRuntime().exec(command);
		try {
			client = new SCClient();
			while (!client.isAttached()) {
				System.out.println("2.5\tAttaching");
				try {
					client.attach(host, port8080);
				} catch (Exception e) {}
			}
			while (true) {
				String sessions = client.workload(serviceName);
				if (Integer.parseInt(sessions.substring(0, sessions.indexOf('/'))) > 0) {
					break;
				}
				Thread.sleep(500);
			}
		} finally {
			System.out.println("3.\tService is enabled!");
		}
	}

	@After
	public void tearDown() throws Exception {
		// r.destroy();
		client.detach();
		System.out.println("5.\tTearDown");
	}

	@Test
	public void createSession_emptySessionServiceName_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService("");
		try {
			sessionService.createSession("sessionInfo", 300, 60);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
		System.out.println("4.\tend of method");
	}

	@Test
	public void createSession_whiteSpaceSessionServiceName_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(" ");
		try {
			sessionService.createSession("sessionInfo", 300, 60);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	System.out.println("4.\tend of method");
	}

	@Test
	public void createSession_arbitrarySessionServiceNameNotInSCProps_throwsException()
			throws Exception {
		ISessionService sessionService = client
				.newSessionService("The quick brown fox jumps over a lazy dog.");
		try {
			sessionService.createSession("sessionInfo", 300, 60);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	System.out.println("4.\tend of method");
	}

	@Test(expected = SCMPValidatorException.class)
	public void createSession_nullSessionInfo_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession(null, 300, 60);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	System.out.println("4.\tNull session info - end of method");
	}

	@Test(expected = SCMPValidatorException.class)
	public void createSession_emptySessionInfo_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("", 300, 60);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
		System.out.println("4.\tEmpty - end of method");
	}

	@Test
	public void createSession_whiteSpaceSessionInfo_passes() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession(" ", 787, 999);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
		System.out.println("4.\tWhite space - end of method");
	}

	@Test
	public void createSession_arbitrarySpaceSessionInfo_passes() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("The quick brown fox jumps over a lazy dog.", 300, 60);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
		System.out.println("4.\tWhite space - end of method");
	}

	@Test
	public void createSession_256LongSessionInfo_passes() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 256; i++) {
			sb.append('a');
		}
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession(sb.toString(), 300, 60);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_257LongSessionInfo_throwsException() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 257; i++) {
			sb.append('a');
		}
		ISessionService sessionService = client.newSessionService(serviceName);
		try {
			sessionService.createSession(sb.toString(), 300, 60);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_beforeCreateSession_passes() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_afterValidCreateSession_passes() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_afterCreateSessionWhiteSpaceSessionInfo_passes() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession(" ", 300, 60);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}
	
	@Test
	public void createSession_twice_throwsExceptioin() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60);
		try {
			sessionService.createSession("sessionInfo", 300, 60);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_twiceWithDifferentSessionServices_passes() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60);
	}

	@Test
	public void createSession_1000times_passes() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		for (int i = 0; i < 100; i++) {
			System.out.println("createSession_1000times cycle:\t" + i * 10);
			for (int j = 0; j < 10; j++) {
				sessionService.createSession("sessionInfo", 300, 10);
				assertEquals(false, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
				sessionService.deleteSession();
				assertEquals(true, sessionService.getSessionId() == null || sessionService.getSessionId().isEmpty());
			}
		}
	}
	
	@Test
	public void execute_messageDataNull_returnsTheSameMessageData() throws Exception {

		ISCMessage message = new SCMessage();

		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		assertEquals(message.getData(), response.getData());
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(false, response.isFault());
	}

	@Test
	public void execute_messageDataEmptyString_returnsTheSameMessageData() throws Exception {

		ISCMessage message = new SCMessage("");

		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		assertEquals(message.getData(), response.getData());
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(false, response.isFault());
	}

	@Test
	public void execute_messageDataSingleChar_returnsTheSameMessageData() throws Exception {

		ISCMessage message = new SCMessage("a");

		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(false, response.isFault());
	}

	@Test
	public void execute_messageDataArbitrary_returnsTheSameMessageData() throws Exception {

		ISCMessage message = new SCMessage("The quick brown fox jumps over a lazy dog.");

		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		assertEquals(message.getData(), response.getData());
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(false, response.isFault());
	}

	@Test
	public void execute_messageData1MBArray_returnsTheSameMessageData() throws Exception {

		ISCMessage message = new SCMessage(new byte[1048576]);

		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		assertEquals(message.getData(), response.getData());
		assertEquals(message.getMessageInfo(), response.getMessageInfo());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(false, response.isFault());
	}

	@Test
	public void execute_messageWhiteSpaceMessageInfo_returnsTheSameMessage() throws Exception {

		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo(" ");

		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(false, response.isFault());
	}

	@Test
	public void execute_messageSingleCharMessageInfo_returnsTheSameMessage() throws Exception {

		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("a");

		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(false, response.isFault());
	}

	@Test
	public void execute_messageArbitraryMessageInfo_returnsTheSameMessage() throws Exception {

		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");

		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(false, response.isFault());
	}

	@Test
	public void execute_messageCompressedTrue_returnsTheSameMessage() throws Exception {

		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		message.setCompressed(true);

		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(false, response.isFault());
	}

	@Test
	public void execute_messageCompressedFalse_returnsTheSameMessage() throws Exception {

		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		message.setCompressed(false);

		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(false, response.isFault());
	}

	@Test
	public void execute_messageSessionIdEmptyString_returnsTheSameMessage() throws Exception {

		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		((SCMessage) message).setSessionId("");

		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.getSessionId(), response.getSessionId());
		assertEquals(false, response.isFault());
	}

	@Test
	public void execute_messageSessionIdWhiteSpaceString_returnsTheSameMessage() throws Exception {

		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		((SCMessage) message).setSessionId(" ");

		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.getSessionId(), response.getSessionId());
		assertEquals(false, response.isFault());
	}

	@Test
	public void execute_messageSessionIdSingleChar_returnsTheSameMessage() throws Exception {

		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		((SCMessage) message).setSessionId("a");

		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.getSessionId(), response.getSessionId());
		assertEquals(false, response.isFault());
	}

	@Test
	public void execute_messageSessionIdArbitraryString_returnsTheSameMessage() throws Exception {

		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		((SCMessage) message).setSessionId("The quick brown fox jumps over a lazy dog.");

		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.getSessionId(), response.getSessionId());
		assertEquals(false, response.isFault());
	}

	@Test
	public void execute_messageSessionIdLikeSessionIdString_returnsTheSameMessage()
			throws Exception {

		ISCMessage message = new SCMessage("Ahoj");
		message.setMessageInfo("The quick brown fox jumps over a lazy dog.");
		((SCMessage) message).setSessionId("aaaa0000-bb11-cc22-dd33-eeeeee444444");

		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60);

		ISCMessage response = sessionService.execute(message);
		assertEquals(message.getData().toString(), response.getData().toString());
		assertEquals(message.getMessageInfo().toString(), response.getMessageInfo().toString());
		assertEquals(message.isCompressed(), response.isCompressed());
		assertEquals(message.getSessionId(), response.getSessionId());
		assertEquals(false, response.isFault());
	}

	@Test
	public void sessionId_uniqueCheckFor1000IdsByOneClient_allSessionIdsAreUnique()
			throws Exception {
		int clientsCount = 1000;
		
		ISessionService sessionService = client.newSessionService(serviceName);
		String[] sessions = new String[clientsCount];
		
		for (int i = 0; i < clientsCount / 10; i++) {
			System.out.println("Creating session " + i * 10);
			for (int j = 0; j < 10; j++) {
				sessionService.createSession("sessionInfo", 300, 60);
				sessions[j + (10 * i)] = sessionService.getSessionId();
				sessionService.deleteSession();
			}
		}
		
		Arrays.sort(sessions);
		int counter = 0;

		for (int i = 1; i < clientsCount; i++) {
			if (sessions[i].equals(sessions[i - 1])) {
				counter++;
			}
		}
		assertEquals(0, counter);
	}
	
}
