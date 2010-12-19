/*
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 */
package org.serviceconnector.test.system.api.session;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestMessageCallback;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;

@SuppressWarnings("unused")
public class APICreateSessionTest {

	/** The Constant testLogger. */
	protected static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(APICreateSessionTest.class);

	private static ProcessesController ctrl;
	private ProcessCtx scCtx;
	private ProcessCtx srvCtx;
	private SCClient client;
	private SCSessionService service;
	private int threadCount = 0;
	private TestMessageCallback cbk = null;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
	}

	@Before
	public void beforeOneTest() throws Exception {
		threadCount = Thread.activeCount();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		srvCtx = ctrl.startServer(TestConstants.SERVER_TYPE_SESSION, TestConstants.log4jSrvProperties,
				TestConstants.sesServerName1, TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, 10,
				TestConstants.sesServiceName1);
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			service.deleteSession();
		} catch (Exception e1) {
		}
		service = null;
		try {
			client.detach();
		} catch (Exception e) {
		}
		client = null;
		try {
			ctrl.stopServer(srvCtx);
		} catch (Exception e) {
		}
		srvCtx = null;
		try {
			ctrl.stopSC(scCtx);
		} catch (Exception e) {
		}
		scCtx = null;
		testLogger.info("Number of threads :" + Thread.activeCount() + " created :"
				+ (Thread.activeCount() - threadCount));
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		ctrl = null;
	}

	/**
	 * Description: Create session (regular)<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		this.cbk = new TestMessageCallback(service);
		response = service.createSession(request, cbk);
		Assert.assertNotNull("the session ID is null", service.getSessionId());
		service.deleteSession();
		Assert.assertNull("the session ID is NOT null after deleteSession()", service.getSessionId());
	}

	/**
	 * Description: Create session to publish service<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t02_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService(TestConstants.pubServiceName1);
		this.cbk = new TestMessageCallback(service);
		response = service.createSession(request, cbk);
		service.deleteSession();
	}

	/**
	 * Description: Create session to file service<br>
	 * Expectation: throws SCServiceException (unfortunatelly this passes because file services uses sessions) TODO TRN
	 * file service accepts create session (sessionService)
	 */
	@Test(expected = SCServiceException.class)
	public void t03_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService(TestConstants.filServiceName1);
		this.cbk = new TestMessageCallback(service);
		response = service.createSession(request, cbk);
		service.deleteSession();
	}

	/**
	 * Description: Create session to service which does not exist<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t04_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService("gaga");
		this.cbk = new TestMessageCallback(service);
		response = service.createSession(request, cbk);
		service.deleteSession();
	}

	/**
	 * Description: Create session to service not served by a server<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t05_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName2);
		this.cbk = new TestMessageCallback(service);
		response = service.createSession(request, cbk);
		service.deleteSession();
	}

	/**
	 * Description: Create session with operationTimeout = 0<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t06_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(0, request, cbk);
	}

	/**
	 * Description: Create session with operationTimeout = -1<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t07_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(-1, request, cbk);
	}

	/**
	 * Description: Create session with operationTimeout = 3601<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t08_createSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(3601, request, cbk);
	}

	/**
	 * Description: Create session with 60kB message<br>
	 * Expectation: passes
	 */
	@Test
	public void t09_createSession60kBmsg() throws Exception {
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength60kB]);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		this.cbk = new TestMessageCallback(service);
		response = service.createSession(request, cbk);
		service.deleteSession();
	}

	/**
	 * Description: Create session with large message<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t10_createSession1MBmsg() throws Exception {
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength1MB]);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		this.cbk = new TestMessageCallback(service);
		response = service.createSession(request, cbk);
		service.deleteSession();
	}

	/**
	 * Description: Create session twice<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t11_createSessionTwice() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		this.cbk = new TestMessageCallback(service);
		response = service.createSession(request, cbk);
		Assert.assertNotNull("the session ID is null", service.getSessionId());

		response = service.createSession(request, cbk);
		service.deleteSession();
		Assert.assertNull("the session ID is NOT null after deleteSession()", service.getSessionId());
	}

	/**
	 * Description: Create two sessions to the same service<br>
	 * Expectation: passes
	 */
	@Test
	public void t12_createTwoSessions() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		SCSessionService service1 = client.newSessionService(TestConstants.sesServiceName1);
		SCSessionService service2 = client.newSessionService(TestConstants.sesServiceName1);
		this.cbk = new TestMessageCallback(service);
		response = service1.createSession(request, cbk);
		Assert.assertNotNull("the session ID is null", service1.getSessionId());
		TestMessageCallback cbk2 = new TestMessageCallback(service2);
		response = service2.createSession(request, cbk2);
		Assert.assertNotNull("the session ID is null", service2.getSessionId());

		service1.deleteSession();
		Assert.assertNull("the session ID is NOT null after deleteSession()", service1.getSessionId());
		service2.deleteSession();
		Assert.assertNull("the session ID is NOT null after deleteSession()", service2.getSessionId());
	}

	/**
	 * Description: Create session with service which has been disabled<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t20_disabledService() throws Exception {
		// disable service
		SCMgmtClient clientMgmt = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP);
		clientMgmt.attach();
		clientMgmt.disableService(TestConstants.sesServiceName1);
		clientMgmt.detach();

		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		this.cbk = new TestMessageCallback(service);
		response = service.createSession(request, cbk);
		service.deleteSession();
	}

	/**
	 * Description: Delete session before create session<br>
	 * Expectation: passes
	 */
	@Test
	public void t30_deleteSession() throws Exception {
		service = client.newSessionService(TestConstants.sesServiceName1);
		service.deleteSession();
		Assert.assertNull("the session ID is NOT null after deleteSession()", service.getSessionId());
	}

	/**
	 * Description: Create session with echo interval = 1<br>
	 * Expectation: passes
	 */
	@Test
	public void t13_echoInterval() throws Exception {
		SCMessage request = new SCMessage(new byte[128]);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		service.setEchoIntervalInSeconds(1);
		this.cbk = new TestMessageCallback(service);
		response = service.createSession(request, cbk);
		service.deleteSession();
	}

	/**
	 * Description: Create session with echo interval = 0<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t14_echoInterval() throws Exception {
		SCMessage request = new SCMessage(new byte[128]);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		service.setEchoIntervalInSeconds(0);
		this.cbk = new TestMessageCallback(service);
		response = service.createSession(request, cbk);
		service.deleteSession();
	}

	/**
	 * Description: Reject session by server<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t15_rejectSession() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		request.setSessionInfo(TestConstants.rejectSessionCmd);
		this.cbk = new TestMessageCallback(service);
		response = service.createSession(request, cbk);
		Assert.assertTrue("reject flag not seth", response.isReject());
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		Assert.assertEquals("appErrorCode is not set", TestConstants.appErrorCode, response.getAppErrorCode());
		Assert.assertEquals("appErrorText is not set", TestConstants.appErrorText, response.getAppErrorText());
		service.deleteSession();
	}

	/**
	 * Description: Reject session by server, check error code<br>
	 * Expectation: passes, exception catched
	 */
	@Test
	public void t16_rejectSession() throws Exception {
		SCMessage request = new SCMessage();
		SCMessage response = new SCMessage();
		service = client.newSessionService(TestConstants.sesServiceName1);
		request.setSessionInfo(TestConstants.rejectSessionCmd);
		try {
			this.cbk = new TestMessageCallback(service);
			response = service.createSession(request, cbk);
		} catch (SCServiceException e) {
			Assert.assertEquals("is not appErrorCode", 4000, e.getAppErrorCode());
			Assert.assertEquals("is not appErrorText", false, e.getAppErrorText().equals(""));
		}
		service.deleteSession();
	}
}