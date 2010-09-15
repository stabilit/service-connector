package integration;

import static org.junit.Assert.assertEquals;

import java.security.InvalidParameterException;

import javax.activity.InvalidActivityException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.sc.common.cmd.SCMPValidatorException;
import com.stabilit.sc.common.net.SCMPCommunicationException;
import com.stabilit.sc.common.service.SCServiceException;
import com.stabilit.sc.ctrl.util.TestConstants;
import com.stabilit.sc.ctrl.util.TestEnvironmentController;
import com.stabilit.sc.srv.ISCServer;
import com.stabilit.sc.srv.ISCServerCallback;
import com.stabilit.sc.srv.SCServer;

public class RegisterServiceServerToSCTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(RegisterServiceServerToSCTest.class);

	private static Process p;
	
	private ISCServer server;
	private Exception ex;

	private static TestEnvironmentController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
		try {
			p = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
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
	}

	@After
	public void tearDown() throws Exception {
		server.destroyServer();
		server = null;
		ex = null;
	}

	// region host == "localhost" (set as only one in
	// scIntegration.properties), all ports
	@Test
	public void registerService_withoutStartListener_throwsException() throws Exception {
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof InvalidActivityException);
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_withStartListenerToSameHostAndPort_throwsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, TestConstants.PORT9000, 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof InvalidActivityException);
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_withValidParamsInSCProperties_registered() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
	}
	
	@Test
	public void registerService_withNotEnabledService_throwsException() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceNameNotEnabled, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, server.isRegistered(TestConstants.serviceNameNotEnabled));
	}

	@Test
	public void registerService_nullCallBack_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 1, 1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof InvalidParameterException);
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_invalidHost_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService("something", TestConstants.PORT9000, TestConstants.serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCServiceException);
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_emptyHostTranslatesAsLocalHost_registered() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		server.registerService("", TestConstants.PORT9000, TestConstants.serviceName, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_whiteSpaceHost_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(" ", TestConstants.PORT9000, TestConstants.serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerService_noHost_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(null, TestConstants.PORT9000, TestConstants.serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void registerService_portNotInSCProperties_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, 9002, TestConstants.serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCServiceException);
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_port0NotInSCProps_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, 0, TestConstants.serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCServiceException);
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_port1NotInSCProps_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, 1, TestConstants.serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCServiceException);
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_portMinus1OutOfRange_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, -1, TestConstants.serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_portMaxAllowedNotInSCProps_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, 0xFFFF, TestConstants.serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCServiceException);
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_portMaxAllowedPlus1_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, 0xFFFF + 1, TestConstants.serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_portIntMaxOutOfRange_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, Integer.MAX_VALUE, TestConstants.serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_portIntMinOutOfRange_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, Integer.MIN_VALUE, TestConstants.serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_noServiceName_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, null, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(null));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_validServiceNameInSCProps_registered() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		server.registerService(TestConstants.HOST, TestConstants.PORT9000, "P01_RTXS_sc1", 1, 1, new CallBack());
		assertEquals(true, server.isRegistered("P01_RTXS_sc1"));
		server.deregisterService("P01_RTXS_sc1");
	}

	@Test
	public void registerService_emptyServiceName_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, "", 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(""));
		assertEquals(true, ex instanceof SCMPValidatorException);
		server.deregisterService("");
	}

	@Test
	public void registerService_whiteSpaceServiceName_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, " ", 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(" "));
		assertEquals(true, ex instanceof SCServiceException);
		server.deregisterService(" ");
	}

	@Test
	public void registerService_arbitraryServiceNameNotInSCProps_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, "Name", 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered("Name"));
		assertEquals(true, ex instanceof SCServiceException);
		server.deregisterService("Name");
	}

	@Test
	public void registerService_serviceNameLength32NotInSCProps_notRegisteredThrowsException()
			throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 32; i++) {
			sb.append("a");
		}
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, sb.toString(), 1, 1, new CallBack());
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
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, sb.toString(), 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(sb.toString()));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_maxSessions0_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 0, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_maxSessionsMinus1_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, -1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_maxSessionsIntMax_registered() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, Integer.MAX_VALUE, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_maxSessionsIntMin_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, Integer.MIN_VALUE, 1,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_maxConnections0_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 1, 0, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_maxConnectionsMinus1_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 1, -1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_maxConnectionsIntMin_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 1, Integer.MIN_VALUE,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_maxConnectionsIntMaxSessions1_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 1, Integer.MAX_VALUE,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_maxConnections2Sessions1_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 1, 2, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_maxConnectionsMAX1024SessionsIntMax_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		server
				.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, Integer.MAX_VALUE, 1024,
						new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_maxConnectionsSameAsSessions1024_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 1024, 1024, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_maxConnectionsSameAsSessions2_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 2, 2, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_maxConnections1023LessThanSessionsIntMax_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		server
				.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, Integer.MAX_VALUE, 1023,
						new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_maxConnections1023LessThanSessions1024_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 1024, 1023, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_maxConnections1024LessThanSessions1025_isRegistered()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 1025, 1024, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
	}
	
	@Test
	public void registerService_maxConnections1025OverAllowedMaximum_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 1025, 1025, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_maxConnectionsLessThanSessions2_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 2, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_maxConnectionsMoreThanSessionsIntMax_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, Integer.MAX_VALUE - 1,
					Integer.MAX_VALUE, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_maxConnectionsMoreThanSessions2_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 1, 2, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_allParamsWrong_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
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
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, 9001, "Name", -1, -1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered("Name"));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_allParamsWrongExceptHostAndPort_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, "Name", -1, -1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered("Name"));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_allParamsWrongExceptHostAndPortAndServiceName_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, -1, -1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_allParamsWrongExcepHostPortServiceNameMaxSessions_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 1, -1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_allParamsWrongExceptHostPortServiceNameMaxSessionsMaxConnections_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT9000, TestConstants.serviceName, 1, 1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void registerService_allParamsWrongExceptCallBack_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
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
		server.startListener(TestConstants.HOST, 9001, 0);
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
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService("host", 9001, TestConstants.serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCServiceException);
	}

	// region end

	private class CallBack implements ISCServerCallback {
	}

}
