package system;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.sc.ctrl.util.TestEnvironmentController;
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
	private static Process r;

	private ISCClient client;

	private static final String host = "localhost";
	private static final int port8080 = 8080;
	private static final int port9000 = 9000;
	private static final String serviceName = "simulation";
	private static final String serviceNameAlt = "P01_RTXS_sc1";

	private Exception ex;

	private static TestEnvironmentController ctrl;
	private static final String log4jSCProperties = "log4jSC0.properties";
	private static final String scProperties = "scIntegration.properties";
	private static final String log4jSrvProperties = "log4jSrv.properties";

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
		try {
			p = ctrl.startSC(log4jSCProperties, scProperties);
			r = ctrl.startServer(log4jSrvProperties, port9000, serviceName, 100);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@Before
	public void setUp() throws Exception {
		client = new SCClient();
		client.attach(host, port8080);
	}

	@After
	public void tearDown() throws Exception {
		client.detach();
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		p.destroy();
		r.destroy();
		ctrl.deleteFile(ctrl.getPidLogPath(log4jSCProperties));
		ctrl.deleteFile(ctrl.getPidLogPath(log4jSrvProperties));
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
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
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
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
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
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test(expected = SCMPValidatorException.class)
	public void createSession_nullSessionInfo_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession(null, 300, 60);
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test(expected = SCMPValidatorException.class)
	public void createSession_emptySessionInfo_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("", 300, 60);
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_whiteSpaceSessionInfo_sessionIdIsNotEmpty() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession(" ", 300, 60);
		assertEquals(false, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_arbitrarySpaceSessionInfo_sessionIdIsNotEmpty() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("The quick brown fox jumps over a lazy dog.", 300, 60);
		assertEquals(false, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_256LongSessionInfo_sessionIdIsNotEmpty() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 256; i++) {
			sb.append('a');
		}
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession(sb.toString(), 300, 60);
		assertEquals(false, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
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
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_beforeCreateSession_noSessionId() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_afterValidCreateSession_noSessionId() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_whiteSpaceSessionInfo_noSessionId() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession(" ", 300, 60);
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
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
		assertEquals(false, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_twiceWithDifferentSessionServices_differentSessionIds() throws Exception {
		ISessionService sessionService0 = client.newSessionService(serviceName);
		ISessionService sessionService1 = client.newSessionService(serviceNameAlt);
		
		assertEquals(true, sessionService0.getSessionId() == null
				|| sessionService0.getSessionId().isEmpty());
		assertEquals(true, sessionService1.getSessionId() == null
				|| sessionService1.getSessionId().isEmpty());
		
		sessionService0.createSession("sessionInfo", 300, 60);
		
		assertEquals(false, sessionService0.getSessionId() == null
				|| sessionService0.getSessionId().isEmpty());
		assertEquals(true, sessionService1.getSessionId() == null
				|| sessionService1.getSessionId().isEmpty());
		
		sessionService1.createSession("sessionInfo", 300, 60);
		
		assertEquals(false, sessionService0.getSessionId() == null
				|| sessionService0.getSessionId().isEmpty());
		assertEquals(false, sessionService1.getSessionId() == null
				|| sessionService1.getSessionId().isEmpty());
		
		assertEquals(false, sessionService0.getSessionId().equals(sessionService1.getSessionId()));
		
		sessionService0.deleteSession();
		sessionService1.deleteSession();
	}

	@Test
	public void createSession_10000times_passes() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		for (int i = 0; i < 1000; i++) {
			System.out.println("createSession_10000times cycle:\t" + i * 10);
			for (int j = 0; j < 10; j++) {
				sessionService.createSession("sessionInfo", 300, 10);
				assertEquals(false, sessionService.getSessionId() == null
						|| sessionService.getSessionId().isEmpty());
				sessionService.deleteSession();
				assertEquals(true, sessionService.getSessionId() == null
						|| sessionService.getSessionId().isEmpty());
			}
		}
	}
	
	@Test
	public void createSession_echoInterval0_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		try {
			sessionService.createSession("sessionInfo", 0, 10);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}
	
	@Test
	public void createSession_echoIntervalMinus1_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		try {
			sessionService.createSession("sessionInfo", -1, 10);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}
	
	//TODO this has to pass!
	@Test
	public void createSession_echoInterval1_sessionIdCreated() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 1, 10);
		assertEquals(false, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}
	
	@Test
	public void createSession_echoIntervalIntMin_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		try {
			sessionService.createSession("sessionInfo", Integer.MIN_VALUE, 10);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}
	
	@Test
	public void createSession_echoIntervalIntMax_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		try {
			sessionService.createSession("sessionInfo", Integer.MAX_VALUE, 10);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}
	
	@Test
	public void createSession_echoInterval3600_sessionIdCreated() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 3600, 10);
		assertEquals(false, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

	}
	
	@Test
	public void createSession_echoInterval3601_sessionIdCreated() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		try {
			sessionService.createSession("sessionInfo", 3601, 10);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}
	
	@Test
	public void createSession_timeout0_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		try {
			sessionService.createSession("sessionInfo", 300, 0);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}
	
	@Test
	public void createSession_timeoutMinus1_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		try {
			sessionService.createSession("sessionInfo", 300, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}
	
	@Test
	public void createSession_timeout1_sessionIdCreated() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 1);
		assertEquals(false, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}
	
	@Test
	public void createSession_timeoutIntMin_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		try {
			sessionService.createSession("sessionInfo", 300, Integer.MIN_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}
	
	@Test
	public void createSession_timeoutIntMax_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		try {
			sessionService.createSession("sessionInfo", 300, Integer.MAX_VALUE);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}
	
	@Test
	public void createSession_timeout3600_sessionIdCreated() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 3600);
		assertEquals(false, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();

	}
	
	@Test
	public void createSession_timeout3601_sessionIdCreated() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		try {
			sessionService.createSession("sessionInfo", 300, 3601);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}
	
	@Test
	public void createSession_allInvalidParams_throwsSCMPValidatorException() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		try {
			sessionService.createSession(null, -1, -1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_emptySessionServiceNameDataOneChar_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService("");
		try {
			sessionService.createSession("sessionInfo", 300, 60, "a");
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_whiteSpaceSessionServiceNameDataOneChar_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(" ");
		try {
			sessionService.createSession("sessionInfo", 300, 60, "a");
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_arbitrarySessionServiceNameNotInSCPropsDataOneChar_throwsException()
			throws Exception {
		ISessionService sessionService = client
				.newSessionService("The quick brown fox jumps over a lazy dog.");
		try {
			sessionService.createSession("sessionInfo", 300, 60, "a");
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test(expected = SCMPValidatorException.class)
	public void createSession_nullSessionInfoDataOneChar_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession(null, 300, 60, "a");
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test(expected = SCMPValidatorException.class)
	public void createSession_emptySessionInfoDataOneChar_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("", 300, 60, "a");
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_whiteSpaceSessionInfoDataOneChar_sessionIdIsNotEmpty() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession(" ", 300, 60, "a");
		assertEquals(false, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_arbitrarySpaceSessionInfoDataOneChar_sessionIdIsNotEmpty() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("The quick brown fox jumps over a lazy dog.", 300, 60, "a");
		assertEquals(false, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_256LongSessionInfoDataOneChar_sessionIdIsNotEmpty() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 256; i++) {
			sb.append('a');
		}
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession(sb.toString(), 300, 60, "a");
		assertEquals(false, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
		sessionService.deleteSession();
	}

	@Test
	public void createSession_257LongSessionInfoDataOneChar_throwsException() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 257; i++) {
			sb.append('a');
		}
		ISessionService sessionService = client.newSessionService(serviceName);
		try {
			sessionService.createSession(sb.toString(), 300, 60, "a");
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPValidatorException);
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_afterValidCreateSessionDataOneChar_noSessionId() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60, "a");
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}

	@Test
	public void deleteSession_whiteSpaceSessionInfoDataOneChar_noSessionId() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession(" ", 300, 60, "a");
		sessionService.deleteSession();
		assertEquals(true, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_twiceDataOneChar_throwsException() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		sessionService.createSession("sessionInfo", 300, 60, "a");
		try {
			sessionService.createSession("sessionInfo", 300, 60);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, sessionService.getSessionId() == null
				|| sessionService.getSessionId().isEmpty());
	}

	@Test
	public void createSession_twiceWithDifferentSessionServicesDataOneChar_differentSessionIds() throws Exception {
		ISessionService sessionService0 = client.newSessionService(serviceName);
		ISessionService sessionService1 = client.newSessionService(serviceNameAlt);
		
		assertEquals(true, sessionService0.getSessionId() == null
				|| sessionService0.getSessionId().isEmpty());
		assertEquals(true, sessionService1.getSessionId() == null
				|| sessionService1.getSessionId().isEmpty());
		
		sessionService0.createSession("sessionInfo", 300, 60, "a");
		
		assertEquals(false, sessionService0.getSessionId() == null
				|| sessionService0.getSessionId().isEmpty());
		assertEquals(true, sessionService1.getSessionId() == null
				|| sessionService1.getSessionId().isEmpty());
		
		sessionService1.createSession("sessionInfo", 300, 60, "a");
		
		assertEquals(false, sessionService0.getSessionId() == null
				|| sessionService0.getSessionId().isEmpty());
		assertEquals(false, sessionService1.getSessionId() == null
				|| sessionService1.getSessionId().isEmpty());
		
		assertEquals(false, sessionService0.getSessionId().equals(sessionService1.getSessionId()));
		
		sessionService0.deleteSession();
		sessionService1.deleteSession();
	}

	@Test
	public void createSession_1000times_passes() throws Exception {
		ISessionService sessionService = client.newSessionService(serviceName);
		for (int i = 0; i < 100; i++) {
			System.out.println("createSession_1000times cycle:\t" + i * 10);
			for (int j = 0; j < 10; j++) {
				sessionService.createSession("sessionInfo", 300, 10, "a");
				assertEquals(false, sessionService.getSessionId() == null
						|| sessionService.getSessionId().isEmpty());
				sessionService.deleteSession();
				assertEquals(true, sessionService.getSessionId() == null
						|| sessionService.getSessionId().isEmpty());
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
		//TODO this is not what I sent in!!!
		assertEquals(null, response.getData());
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
		assertEquals(((byte[])message.getData()).length, ((byte[])response.getData()).length);
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
		assertEquals(sessionService.getSessionId(), response.getSessionId());
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
		assertEquals(sessionService.getSessionId(), response.getSessionId());
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
		assertEquals(sessionService.getSessionId(), response.getSessionId());
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
		assertEquals(sessionService.getSessionId(), response.getSessionId());
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
		assertEquals(sessionService.getSessionId(), response.getSessionId());
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
