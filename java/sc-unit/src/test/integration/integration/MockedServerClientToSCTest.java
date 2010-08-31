package integration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.InvalidParameterException;

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

public class MockedServerClientToSCTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(MockedServerClientToSCTest.class);

	private static Process p;

	private static String userDir;
	private Process r;

	private ISCClient client;

	private static final String host = "localhost";
	private static final int port8080 = 8080;
	private static final int port9000 = 9000;
	private String serviceName = "simulation";
	private String serviceNameAlt = "P01_RTXS_sc1";

	private Exception ex;

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
		} catch (IOException e) {
			logger.error("oneTimeSetUp - IOExc", e);
		} catch (InterruptedException e) {
			logger.error("oneTimeSetUp - InterruptExc", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() {
		p.destroy();
	}

	/**
	 * @throws java.lang.Exception
	 * 
	 *             Create a new SCClient for each test method.
	 */
	@Before
	public void setUp() throws Exception {
		client = new SCClient();
		client.attach(host, port8080);
		/*
		 * String command = "java -classpath " + userDir +
		 * "\\target\\classes vmstarters.StartSCSessionServer 9000 " +
		 * serviceName; System.out.println(command); r =
		 * Runtime.getRuntime().exec(command);
		 */
		new Thread("SERVER") {
			public void run() {
				try {
					StartSCSessionServer
							.main(new String[] { String.valueOf(port9000), serviceName });
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		Thread.sleep(1000);
	}

	@After
	public void tearDown() throws Exception {
		// r.destroy();
	}

	@Test(expected = InvalidParameterException.class)
	public void newSessionService_NullParam_throwsInvalidParamException() throws Exception {
		client.newSessionService(null);
	}

	@Test
	public void newSessionService_emptyStringParam_returnsISessionService() throws Exception {
		assertEquals(true, client.newSessionService("") instanceof ISessionService);
	}

	@Test
	public void newSessionService_whiteSpaceStringParam_returnsISessionService() throws Exception {
		assertEquals(true, client.newSessionService(" ") instanceof ISessionService);
	}

	@Test
	public void newSessionService_ArbitraryStringParam_returnsISessionService() throws Exception {
		assertEquals(
				true,
				client.newSessionService("The quick brown fox jumps over a lazy dog.") instanceof ISessionService);
	}

	@Test
	public void newSessionService_validServiceName_returnsISessionService() throws Exception {
		assertEquals(true, client.newSessionService(serviceName) instanceof ISessionService);
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
	}

	@Test(expected = SCMPValidatorException.class)
	public void createSession_nullSessionInfo_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession(null, 300, 60);
	}

	@Test(expected = SCMPValidatorException.class)
	public void createSession_emptySessionInfo_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("", 300, 60);
	}

	@Test
	public void createSession_whiteSpaceSessionInfo_passes() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession(" ", 300, 60);
	}

	@Test
	public void createSession_arbitrarySpaceSessionInfo_passes() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("The quick brown fox jumps over a lazy dog.", 300, 60);
	}

	@Test
	public void deleteSession_beforeCreateSession_passes() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.deleteSession();
	}

	@Test
	public void deleteSession_afterValidCreateSession_passes() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60);
		sessionService.deleteSession();
	}

	@Test
	public void createSession_afterCreateSessionWhiteSpaceSessionInfo_passes() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession(" ", 300, 60);
		sessionService.deleteSession();
	}

	@Test
	public void createSession_1000times_passes() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		for (int i = 0; i < 100; i++) {
			System.out.println("createSession_1000times cycle:\t" + i * 10);
			for (int j = 0; j < 10; j++) {
				sessionService.createSession("sessionInfo", 300, 10);
				sessionService.deleteSession();
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
	public void execute_messageSessionIdLikeSessionIdString_returnsTheSameMessage() throws Exception {

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
	public void sessionId_uniqueCheckFor1000sessions_allSessionsHaveUniqueSessionIds() throws Exception {
		ISCMessage message = new SCMessage("a");
		
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60);
	}
}
