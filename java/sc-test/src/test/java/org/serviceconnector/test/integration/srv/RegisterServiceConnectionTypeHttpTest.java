package org.serviceconnector.test.integration.srv;

import static org.junit.Assert.assertEquals;

import java.security.InvalidParameterException;

import javax.activity.InvalidActivityException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.ctrl.util.TestEnvironmentController;
import org.serviceconnector.net.SCMPCommunicationException;
import org.serviceconnector.sc.service.SCServiceException;
import org.serviceconnector.srv.ISCSessionServer;
import org.serviceconnector.srv.ISCServerCallback;
import org.serviceconnector.srv.SCSessionServer;


public class RegisterServiceConnectionTypeHttpTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(RegisterServiceConnectionTypeHttpTest.class);
	
	private int threadCount = 0;	
	private ISCSessionServer server;
	private Exception ex;

	private static Process scProcess;
	private static TestEnvironmentController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new TestEnvironmentController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSC0Properties);
		ctrl = null;
		scProcess = null;
	}

	@Before
	public void setUp() throws Exception {
		threadCount = Thread.activeCount();
		server = new SCSessionServer();
		((SCSessionServer) server).setConnectionType("netty.http");
	}

	@After
	public void tearDown() throws Exception {
		server.destroyServer();
		server = null;
		ex = null;
		assertEquals(threadCount, Thread.activeCount());
	}

	// region host == "localhost" (set as only one in
	// scIntegration.properties), all ports
	@Test
	public void registerService_withoutStartListener_throwsException() {
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof InvalidActivityException);
	}

	@Test
	public void registerService_withStartListenerToSameHostAndPort_throwsException()
			throws Exception {
		try {
			server.startListener(TestConstants.HOST, TestConstants.PORT8080, 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		// assertEquals(true, ex instanceof SCServiceException);
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_withValidParamsInSCProperties_registered() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
	}
	
	@Test
	public void registerService_withNotEnabledService_throwsException() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceNameSessionNotEnabled, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSessionNotEnabled));
	}

	@Test
	public void registerService_nullCallBack_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1, 1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void registerService_invalidHost_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService("something", TestConstants.PORT8080, TestConstants.serviceName, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerService_emptyHostTranslatesAsLocalhost_registered() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		server.registerService("", TestConstants.PORT8080, TestConstants.serviceName, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_whiteSpaceHost_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(" ", TestConstants.PORT8080, TestConstants.serviceName, 1, 1, new CallBack());
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
			server.registerService(null, TestConstants.PORT8080, TestConstants.serviceName, 1, 1, new CallBack());
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
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, null, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(null));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_validServiceNameInSCProps_registered() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		server.registerService(TestConstants.HOST, TestConstants.PORT8080, "P01_RTXS_sc1", 1, 1, new CallBack());
		assertEquals(true, server.isRegistered("P01_RTXS_sc1"));
		server.deregisterService("P01_RTXS_sc1");
	}

	@Test
	public void registerService_emptyServiceName_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, "", 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(""));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_whiteSpaceServiceName_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, " ", 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(" "));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerService_arbitraryServiceNameNotInSCProps_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, "Name", 1, 1, new CallBack());
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
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, sb.toString(), 1, 1, new CallBack());
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
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, sb.toString(), 1, 1, new CallBack());
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
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 0, 1, new CallBack());
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
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, -1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceName));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerService_maxSessionsIntMax_registered() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, Integer.MAX_VALUE, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_maxSessionsIntMin_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, Integer.MIN_VALUE, 1,
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
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1, 0, new CallBack());
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
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1, -1, new CallBack());
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
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1, Integer.MIN_VALUE,
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
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1, Integer.MAX_VALUE,
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
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1, 2, new CallBack());
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
				.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, Integer.MAX_VALUE, 1024,
						new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_maxConnectionsSameAsSessions1024_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1024, 1024, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_maxConnectionsSameAsSessions2_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 2, 2, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_maxConnections1023LessThanSessionsIntMax_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		server
				.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, Integer.MAX_VALUE, 1023,
						new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_maxConnections1023LessThanSessions1024_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1024, 1023, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_maxConnections1024LessThanSessions1025_isRegistered()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1025, 1024, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
	}
	
	@Test
	public void registerService_maxConnections1025OverAllowedMaximum_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1025, 1025, new CallBack());
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
		server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 2, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceName));
		server.deregisterService(TestConstants.serviceName);
	}

	@Test
	public void registerService_maxConnectionsMoreThanSessionsIntMax_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, 9001, 0);
		try {
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, Integer.MAX_VALUE - 1,
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
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1, 2, new CallBack());
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
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, "Name", -1, -1, null);
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
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, -1, -1, null);
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
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1, -1, null);
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
			server.registerService(TestConstants.HOST, TestConstants.PORT8080, TestConstants.serviceName, 1, 1, null);
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
