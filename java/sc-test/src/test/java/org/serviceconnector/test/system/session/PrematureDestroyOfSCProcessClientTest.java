package org.serviceconnector.test.system.session;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.service.SCServiceException;


public class PrematureDestroyOfSCProcessClientTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger
			.getLogger(PrematureDestroyOfSCProcessClientTest.class);

	private Process scProcess;
	private Process srvProcess;

	private int threadCount = 0;
	private SCClient client;

	private Exception ex;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
	}

	@Before
	public void setUp() throws Exception {
		threadCount = Thread.activeCount();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
			srvProcess = ctrl.startServer(TestConstants.sessionSrv, TestConstants.log4jSrvProperties, TestConstants.PORT_LISTENER,
					TestConstants.PORT_TCP, 100, new String[] { TestConstants.serviceName, TestConstants.serviceNameAlt });
		} catch (Exception e) {
			logger.error("setUp", e);
		}
		client = new SCClient();
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
	}

	@After
	public void tearDown() throws Exception {
		try {
			client.detach();
		} catch (Exception e) {
		} finally {
			ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
			ctrl.stopProcess(scProcess, TestConstants.log4jSC0Properties);
			client = null;
			srvProcess = null;
			scProcess = null;
		}
		assertEquals("number of threads", threadCount, Thread.activeCount());
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl = null;
	}
	
	@Test
	public void createSession_withoutSC_throwsException() throws Exception {
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		try {
			sessionService.createSession("sessionInfo", 300, 5);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(null, sessionService.getSessionId());
	}

	@Test
	public void deleteSession_withoutSC_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 5);

		ctrl.stopProcess(scProcess, TestConstants.log4jSrvProperties);

		try {
			sessionService.deleteSession();
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void execute_withoutSC_throwsException() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 300, 5);

		ctrl.stopProcess(scProcess, TestConstants.log4jSrvProperties);

		try {
			sessionService.execute(new SCMessage());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
	}

	@Test
	public void deleteSession_withoutSCTimeoutTakes5Seconds_passes() throws Exception {
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 5, 5);

		ctrl.stopProcess(scProcess, TestConstants.log4jSrvProperties);

		Thread.sleep(5000);
		sessionService.deleteSession();
	}

	@Test(expected = SCServiceException.class)
	public void execute_withoutSC_timeoutTakes5SecondsThrowsException() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSrvProperties);
		SCSessionService sessionService = client.newSessionService(TestConstants.serviceName);
		sessionService.createSession("sessionInfo", 5, 5);

		Thread.sleep(5000);
		try {
			sessionService.execute(new SCMessage());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(null, sessionService.getSessionId());
		throw ex;
	}
}
