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
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.api.srv.SCSessionServerCallback;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.net.SCMPCommunicationException;
import org.serviceconnector.service.SCServiceException;

public class RegisterServerConnectionTypeHttpTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(RegisterServerConnectionTypeHttpTest.class);

	private SCSessionServer server;
	private Exception ex;

	private static Process scProcess;
	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSCProperties);
		ctrl = null;
		scProcess = null;
	}

	@Before
	public void setUp() throws Exception {
		server = new SCSessionServer();
		((SCSessionServer) server).setConnectionType("netty.http");
	}

	@After
	public void tearDown() throws Exception {
		server.destroy();
		server = null;
		ex = null;
		// assertEquals("number of threads", threadCount, Thread.activeCount());
	}

	@Test
	public void registerServer_withoutStartListener_throwsException() {
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1, 1,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof InvalidActivityException);
	}

	@Test
	public void registerServer_withStartListenerToSameHostAndPort_throwsException() throws Exception {
		try {
			server.startListener(TestConstants.HOST, TestConstants.PORT_HTTP, 1);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCMPCommunicationException);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1, 1,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof InvalidActivityException);
		server.deregisterServer(TestConstants.serviceNameSession);
	}

	@Test
	public void registerServer_withValidParamsInSCProperties_registered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1, 1,
				new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
		server.deregisterServer(TestConstants.serviceNameSession);
	}

	@Test
	public void registerServer_withDisabledService_isRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSessionDisabled, 1,
				1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceNameSessionDisabled));
		server.deregisterServer(TestConstants.serviceNameSessionDisabled);
	}

	@Test
	public void registerServer_nullCallBack_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1, 1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_invalidHost_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server
					.registerServer("something", TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1, 1,
							new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerServer_emptyHostTranslatesAsLocalhost_registered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer("", TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
		server.deregisterServer(TestConstants.serviceNameSession);
	}

	@Test
	public void registerServer_whiteSpaceHost_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(" ", TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerServer_noHost_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(null, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof InvalidParameterException);
	}

	@Test
	public void registerServer_portNotInSCProperties_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, 9002, TestConstants.serviceNameSession, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerServer_port0NotInSCProps_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, 0, TestConstants.serviceNameSession, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerServer_portMinNotInSCProps_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_MIN, TestConstants.serviceNameSession, 1, 1,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerServer_portMinus1OutOfRange_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, -1, TestConstants.serviceNameSession, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_portMaxAllowedNotInSCProps_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, 0xFFFF, TestConstants.serviceNameSession, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerServer_portMaxAllowedPlus1_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, 0xFFFF + 1, TestConstants.serviceNameSession, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_portIntMaxOutOfRange_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, Integer.MAX_VALUE, TestConstants.serviceNameSession, 1, 1,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_portIntMinOutOfRange_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, Integer.MIN_VALUE, TestConstants.serviceNameSession, 1, 1,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_noServiceName_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, null, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(null));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_validServiceNameInSCProps_registered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, "sc1-session-service", 1, 1, new CallBack());
		assertEquals(true, server.isRegistered("sc1-session-service"));
		server.deregisterServer("sc1-session-service");
	}

	@Test
	public void registerServer_emptyServiceName_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, "", 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(""));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_whiteSpaceServiceName_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, " ", 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(" "));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerServer_arbitraryServiceNameNotInSCProps_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, "Name", 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered("Name"));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerServer_serviceNameLength32NotInSCProps_notRegisteredThrowsException() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 32; i++) {
			sb.append("a");
		}
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, sb.toString(), 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(sb.toString()));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerServer_serviceNameLength33TooLong_notRegisteredThrowsException() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 33; i++) {
			sb.append("a");
		}
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, sb.toString(), 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(sb.toString()));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_maxSessions0_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 0, 1,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_maxSessionsMinus1_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, -1, 1,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_maxSessionsIntMax_registered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession,
				Integer.MAX_VALUE, 1, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
		server.deregisterServer(TestConstants.serviceNameSession);
	}

	@Test
	public void registerServer_maxSessionsIntMin_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession,
					Integer.MIN_VALUE, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_maxConnections0_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1, 0,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_maxConnectionsMinus1_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1, -1,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_maxConnectionsIntMin_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1,
					Integer.MIN_VALUE, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_maxConnectionsIntMaxSessions1_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1,
					Integer.MAX_VALUE, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_maxConnections2Sessions1_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1, 2,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	// @Test TODO to much
	public void registerServer_maxConnectionsMAX1024SessionsIntMax_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession,
				Integer.MAX_VALUE, 1024, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
		server.deregisterServer(TestConstants.serviceNameSession);
	}

	// @Test TODO to much
	public void registerServer_maxConnectionsSameAsSessions1024_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1024, 1024,
				new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
		server.deregisterServer(TestConstants.serviceNameSession);
	}

	@Test
	public void registerServer_maxConnectionsSameAsSessions2_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 2, 2,
				new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
		server.deregisterServer(TestConstants.serviceNameSession);
	}

	@Test
	public void registerServer_maxConnections1023LessThanSessionsIntMax_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession,
				Integer.MAX_VALUE, 1023, new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
		server.deregisterServer(TestConstants.serviceNameSession);
	}

	@Test
	public void registerServer_maxConnections1023LessThanSessions1024_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1024, 1023,
				new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
		server.deregisterServer(TestConstants.serviceNameSession);
	}

	@Test
	public void registerServer_maxConnections1024LessThanSessions1025_isRegistered() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1025, 1024,
				new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
		server.deregisterServer(TestConstants.serviceNameSession);
	}

	@Test
	public void registerServer_maxConnections1025OverAllowedMaximum_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1025, 1025,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_maxConnectionsLessThanSessions2_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 2, 1,
				new CallBack());
		assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
		server.deregisterServer(TestConstants.serviceNameSession);
	}

	@Test
	public void registerServer_maxConnectionsMoreThanSessionsIntMax_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession,
					Integer.MAX_VALUE - 1, Integer.MAX_VALUE, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_maxConnectionsMoreThanSessions2_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1, 2,
					new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_allParamsWrong_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer("host", TestConstants.PORT_LISTENER, "Name", -1, -1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered("Name"));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_allParamsWrongExceptHost_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_LISTENER, "Name", -1, -1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered("Name"));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_allParamsWrongExceptHostAndPort_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, "Name", -1, -1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered("Name"));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_allParamsWrongExceptHostAndPortAndServiceName_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, -1, -1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_allParamsWrongExcepHostPortServiceNameMaxSessions_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1, -1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_allParamsWrongExceptHostPortServiceNameMaxSessionsMaxConnections_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1, 1, null);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_allParamsWrongExceptCallBack_notRegisteredThrowsException() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer("host", TestConstants.PORT_LISTENER, "Name", -1, -1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered("Name"));
		assertEquals(true, ex instanceof SCMPValidatorException);
	}

	@Test
	public void registerServer_allParamsWrongExceptCallBackMaxConnectionsMaxSessions_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer("host", TestConstants.PORT_LISTENER, "Name", 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered("Name"));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void registerServer_allParamsWrongExceptCallBackMaxConnectionsMaxSessionsServiceName_notRegisteredThrowsException()
			throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		try {
			server.registerServer("host", TestConstants.PORT_LISTENER, TestConstants.serviceNameSession, 1, 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void multipleRegisterServer_differentServiceNames() throws Exception {
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameSession, 1, 1,
				new CallBack());
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNamePublish, 1, 1,
				new CallBack());
		server.registerServer(TestConstants.HOST, TestConstants.PORT_HTTP, TestConstants.serviceNameAlt, 1, 1,
				new CallBack());

		assertEquals(true, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(true, server.isRegistered(TestConstants.serviceNamePublish));
		assertEquals(true, server.isRegistered(TestConstants.serviceNameAlt));
		server.deregisterServer(TestConstants.serviceNameSession);
		server.deregisterServer(TestConstants.serviceNamePublish);
		server.deregisterServer(TestConstants.serviceNameAlt);
		assertEquals(false, server.isRegistered(TestConstants.serviceNameSession));
		assertEquals(false, server.isRegistered(TestConstants.serviceNamePublish));
		assertEquals(false, server.isRegistered(TestConstants.serviceNameAlt));
	}

	private class CallBack extends SCSessionServerCallback {

		@Override
		public SCMessage execute(SCMessage message, int operationTimeoutInMillis) {
			return null;
		}
	}
}
