package integration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.InvalidParameterException;

import javax.activity.InvalidActivityException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.service.SCServiceException;
import com.stabilit.scm.srv.ISCServer;
import com.stabilit.scm.srv.ISCServerCallback;
import com.stabilit.scm.srv.SCServer;

public class RegisterServiceServerToSCTest {

	private static Process p;
	private ISCServer server;
	private Exception ex;
	private String serviceName = "simulation";
	private String host = "localhost";
	private int port9000 = 9000;

	@BeforeClass
	public static void oneTimeSetUp() {
		try {
			String userDir = System.getProperty("user.dir");
			String command = "java -Dlog4j.configuration=file:" + userDir
					+ "\\src\\test\\resources\\log4jSC0.properties -jar " + userDir
					+ "\\..\\service-connector\\target\\sc.jar -filename " + userDir
					+ "\\src\\test\\resources\\scIntegration.properties";

			p = Runtime.getRuntime().exec(command);

			// lets the SC load before starting communication
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void oneTimeTearDown() {
		p.destroy();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		server = new SCServer();
	}
//TODO solve issue with listeners on taken ports
	// region host == "localhost" (set as only one in
	// scIntegration.properties), all ports
	@Test
	public void registerService_withoutStartListener_throwsException() {
		try {
			server.registerService(host, port9000, serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof InvalidActivityException);
	}

	@Test
	public void registerService_withStartListenerToSameHostAndPort_throwsException() {
		try {
			server.startListener(host, port9000, 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		try {
			server.registerService(host, port9000, serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		// assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerService_sameHostDifferentPortSCalreadyListeningOnGivenPort_notRegisteredthrowsException()
			throws Exception {
		try {
			server.startListener(host, 8080, 1);
		} catch (Exception e) {
			ex = e;
		}
		server.registerService(host, port9000, serviceName, 1, 1, new CallBack());
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerService_ToDifferentHostSamePortServiceNameNotInSCProperties_throwsException()
			throws Exception {
		server.startListener(host, 8080, 1);
		try {
			server.registerService(host, port9000, "Name", 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerService_withValidParamsInSCProperties_registered() throws Exception {
		server.startListener(host, 9001, 0);
		server.registerService(host, port9000, serviceName, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
	}

	@Test
	public void registerService_differentHostForListenerParamsInSCProperties_registered()
			throws Exception {
		server.startListener("host", 9001, 0);
		server.registerService(host, port9000, serviceName, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
	}

	@Test
	public void registerService_nullCallBack_notRegisteredThrowsException() throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, port9000, serviceName, 1, 1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void registerService_invalidHost_notRegisteredThrowsException() throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService("something", port9000, serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_emptyHost_notRegisteredThrowsException() throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService("", port9000, serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_whiteSpaceHost_notRegisteredThrowsException() throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(" ", port9000, serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_noHost_notRegisteredThrowsException() throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(null, port9000, serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void registerService_portNotInSCProperties_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, 9002, serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerService_port0NotInSCProps_notRegisteredThrowsException() throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, 0, serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerService_port1NotInSCProps_notRegisteredThrowsException() throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, 1, serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerService_portMinus1OutOfRange_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, -1, serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_portMaxAllowedNotInSCProps_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, 0xFFFF, serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerService_portMaxAllowedPlus1_notRegisteredThrowsException() throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, 0xFFFF + 1, serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_portIntMaxOutOfRange_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, Integer.MAX_VALUE, serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_portIntMinOutOfRange_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, Integer.MIN_VALUE, serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_noServiceName_notRegisteredThrowsException() throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, port9000, null, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(null));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_validServiceNameInSCProps_registered() throws Exception {
		server.startListener(host, 9001, 0);
		server.registerService(host, port9000, "P01_RTXS_sc1", 1, 1, new CallBack());
		assertEquals(true, server.isRegistered("P01_RTXS_sc1"));
	}

	@Test
	public void registerService_emptyServiceName_notRegisteredThrowsException() throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, port9000, "", 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(""));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_whiteSpaceServiceName_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, port9000, " ", 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(" "));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerService_arbitraryServiceNameNotInSCProps_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, port9000, "Name", 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered("Name"));
		assertEquals(true, ex instanceof SCServiceException);
	}
	
	@Test
	public void registerService_serviceNameLength32NotInSCProps_notRegisteredThrowsException()
			throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 32; i++) {
			sb.append("a");
		}
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, port9000, sb.toString(), 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(sb.toString()));
		assertEquals(true, ex instanceof SCServiceException);
	}
	
	@Test
	public void registerService_serviceNameLength33TooLong_notRegisteredThrowsException()
			throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 33; i++) {
			sb.append("a");
		}
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, port9000, sb.toString(), 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(sb.toString()));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_maxSessions0_notRegisteredThrowsException() throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, port9000, serviceName, 0, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_maxSessionsMinus1_notRegisteredThrowsException() throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, port9000, serviceName, -1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_maxSessionsIntMax_registered() throws Exception {
		server.startListener(host, 9001, 0);
		server.registerService(host, port9000, serviceName, Integer.MAX_VALUE, 1, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
	}

	@Test
	public void registerService_maxSessionsIntMin_notRegisteredThrowsException() throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, port9000, serviceName, Integer.MIN_VALUE, 1,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_maxConnections0_notRegisteredThrowsException() throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, port9000, serviceName, 1, 0, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_maxConnectionsMinus1_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, port9000, serviceName, 1, -1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_maxConnectionsIntMin_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, port9000, serviceName, 1, Integer.MIN_VALUE,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_maxConnectionsIntMaxSessions1_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, port9000, serviceName, 1, Integer.MAX_VALUE,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_maxConnections2Sessions1_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, port9000, serviceName, 1, 2, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_maxConnectionsMAX1024SessionsIntMax_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		server.registerService(host, port9000, serviceName, Integer.MAX_VALUE, 1024,
				new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
	}
	
	@Test
	public void registerService_maxConnectionsSameAsSessions1024_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		server.registerService(host, port9000, serviceName, 1024, 1024, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
	}

	@Test
	public void registerService_maxConnectionsSameAsSessions2_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		server.registerService(host, port9000, serviceName, 2, 2, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
	}

	@Test
	public void registerService_maxConnections1023LessThanSessionsIntMax_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		server.registerService(host, port9000, serviceName, Integer.MAX_VALUE,
				1023, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
	}
	
	@Test
	public void registerService_maxConnections1023LessThanSessions1024_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		server.registerService(host, port9000, serviceName, 1024,
				1023, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
	}

	@Test
	public void registerService_maxConnectionsLessThanSessions2_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		server.registerService(host, port9000, serviceName, 2, 1, new CallBack());
		assertEquals(true, server.isRegistered(serviceName));
	}

	@Test
	public void registerService_maxConnectionsMoreThanSessionsIntMax_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, port9000, serviceName, Integer.MAX_VALUE - 1,
					Integer.MAX_VALUE, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_maxConnectionsMoreThanSessions2_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, port9000, serviceName, 1, 2, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}
	
	@Test
	public void registerService_allParamsWrong_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService("host", 9001, "Name", -1, -1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered("Name"));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}
	
	@Test
	public void registerService_allParamsWrongExceptHost_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, 9001, "Name", -1, -1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered("Name"));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}
	
	@Test
	public void registerService_allParamsWrongExceptHostAndPort_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, port9000, "Name", -1, -1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered("Name"));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}
	
	@Test
	public void registerService_allParamsWrongExceptHostAndPortAndServiceName_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, port9000, serviceName, -1, -1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}
	
	@Test
	public void registerService_allParamsWrongExcepHostPortServiceNameMaxSessions_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, port9000, serviceName, 1, -1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}
	
	@Test
	public void registerService_allParamsWrongExceptHostPortServiceNameMaxSessionsMaxConnections_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService(host, port9000, serviceName, 1, 1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof InvalidParameterException);
	}
	
	@Test
	public void registerService_allParamsWrongExceptCallBack_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService("host", 9001, "Name", -1, -1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered("Name"));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}
	
	@Test
	public void registerService_allParamsWrongExceptCallBackMaxConnectionsMaxSessions_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService("host", 9001, "Name", 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered("Name"));
		assertEquals(true, ex instanceof SCServiceException);
	}
	
	@Test
	public void registerService_allParamsWrongExceptCallBackMaxConnectionsMaxSessionsServiceName_notRegisteredThrowsException()
			throws Exception {
		server.startListener(host, 9001, 0);
		try {
			server.registerService("host", 9001, serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(serviceName));
		assertEquals(true, ex instanceof SCServiceException);
	}
	

	// region end

	private class CallBack implements ISCServerCallback {
	}

}
