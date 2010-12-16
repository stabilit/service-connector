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

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;

@SuppressWarnings("unused")
public class ExecuteTest {

	/** The Constant testLogger. */
	protected static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ExecuteTest.class);

	private static ProcessesController ctrl;
	private ProcessCtx scCtx;
	private ProcessCtx srvCtx;
	private SCClient client;
	private SCSessionService service;
	private int threadCount = 0;

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
		testLogger.info("Number of threads :" + Thread.activeCount() + " created :"+(Thread.activeCount() - threadCount));
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		ctrl = null;
	}

	/**
	 * Description: regular exchange messages<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_execute() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(request);
		request.setMessageInfo(TestConstants.echoAppErrorCmd);
		response = service.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same",request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		Assert.assertEquals("appErrorCode is not set",TestConstants.appErrorCode, response.getAppErrorCode());
		Assert.assertEquals("appErrorText is not set",TestConstants.appErrorText, response.getAppErrorText());
		service.deleteSession();
	}

	/**
	 * Description: regular exchange messages<br>
	 * Expectation: passes
	 */
	@Test
	public void t02_executeCompressed() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(true);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(request);
		request.setMessageInfo(TestConstants.echoCmd);
		response = service.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same",request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		service.deleteSession();
	}

	
	/**
	 * Description: exchange 1MB message<br>
	 * Expectation: passes
	 */
	@Test
	public void t03_executeLarge() throws Exception {
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength1MB]);
		request.setCompressed(false);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(new SCMessage());
		request.setMessageInfo(TestConstants.echoCmd);
		response = service.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same",request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		service.deleteSession();
	}

	/**
	 * Description: exchange 1MB message<br>
	 * Expectation: passes
	 */
	@Test
	public void t04_executeLargeCompressed() throws Exception { // TODO JOT this test does not work
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength1MB]);
		request.setCompressed(true);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(new SCMessage());
		request.setMessageInfo(TestConstants.echoCmd);
		response = service.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same",request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		service.deleteSession();
	}
	
	/**
	 * Description: exchange messages on service which has been disabled<br>
	 * Expectation: passes
	 */
	@Test
	public void t05_disabledService() throws Exception {
		SCMessage request = new SCMessage(new byte[128]);
		request.setCompressed(false);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(request);

		// disable service
		SCMgmtClient clientMgmt = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP);
		clientMgmt.attach();
		clientMgmt.disableService(TestConstants.sesServiceName1);
		clientMgmt.detach();
		
		// execute
		response = service.execute(request);
		service.deleteSession();
	}

	/**
	 * Description: exchange before create session<br>
	 * Expectation: throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t06_execute() throws Exception {
		SCMessage request = new SCMessage(new byte[128]);
		request.setCompressed(false);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.execute(request);
	}

	/**
	 * Description: screw up sessionId before create session<br>
	 * Expectation: passes because sessionId is set internally again.
	 */
	@Test
	public void t07_sessionId() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		String sessionId = "aaaa0000-bb11-cc22-dd33-eeeeee444444";
		request.setSessionId(sessionId);
		response = service.createSession(request);
		Assert.assertEquals("sessionId is the same", false ,sessionId == response.getSessionId());

		request.setMessageInfo(TestConstants.echoCmd);
		response = service.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same",request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		service.deleteSession();
	}

	/**
	 * Description: screw up sessionId before message exchange<br>
	 * Expectation: passes because sessionId is set internally again.
	 */
	@Test
	public void t09_sessionId() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(request);
		String sessionId = service.getSessionId();
		request.setMessageInfo(TestConstants.echoCmd);
		request.setSessionId("aaaa0000-bb11-cc22-dd33-eeeeee444444");
		response = service.execute(request);
		Assert.assertEquals("sessionId is not the same", sessionId, response.getSessionId());
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same",request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		service.deleteSession();
	}

	/**
	 * Description: continue message exchange after session rejection<br>
	 * Expectation: throws SCserviceException
	 */
	@Test (expected = SCServiceException.class)
	public void t09_rejectSession() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);

		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		request.setSessionInfo(TestConstants.rejectSessionCmd);
		try {
			response = service.createSession(request);
		} catch (Exception e) {
			// ignore rejection
		}
		request.setMessageInfo(TestConstants.echoCmd);
		response = service.execute(request);
	}

	/**
	 * Description: operation timeout expired during execution<br>
	 * Expectation: throws SCserviceException
	 */
	@Test (expected = SCServiceException.class)
	public void t10_operationTimeout() throws Exception {
		SCMessage request = new SCMessage("hallo");
		request.setCompressed(false);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(request);
		request.setMessageInfo(TestConstants.sleepCmd);
		request.setData("5000"); // server will sleep 5000ms
		// will get exception here
		response = service.execute(3,request);	// SC oti = 3*0.8*1000 = 2400ms
	}
	
	/**
	 * Description: operation timeout expired during execution, catch exception and continue after a while<br>
	 * Expectation: passes
	 */
	@Test
	public void t11_operationTimeout() throws Exception {
		SCMessage request = new SCMessage("hallo");
		request.setCompressed(false);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(request);
		request.setMessageInfo(TestConstants.sleepCmd);
		request.setData("5000"); // server will sleep 5000ms
		try {
			response = service.execute(3,request);	// SC oti = 3*0.8*1000 = 2400ms
		} catch (SCServiceException e) {
			// will get here after 3000 ms, sleep another 4000 to allow SC cleanup
			Thread.sleep(4000);
		}
		request.setMessageInfo(TestConstants.echoCmd);
		request.setData("hallo");
		response = service.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same",request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		service.deleteSession();
	}
	
	/**
	 * Description: operation timeout expired during execution, catch exception and continue immediatelly<br>
	 * Expectation: passes
	 */
	@Test
	public void t12_operationTimeout() throws Exception {
		SCMessage request = new SCMessage();
		request.setCompressed(false);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(request);
		request.setMessageInfo(TestConstants.sleepCmd);
		request.setData("5000"); // server will sleep 5000ms
		try {
			response = service.execute(3,request);	// SC oti = 3*0.8*1000 = 2400ms
		} catch (SCServiceException e) {
			// will get here after 3000 ms
			// continue immediatelly
		}
		request.setMessageInfo(TestConstants.echoCmd);
		request.setData("hallo");
		response = service.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same",request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		service.deleteSession();
	}
}
