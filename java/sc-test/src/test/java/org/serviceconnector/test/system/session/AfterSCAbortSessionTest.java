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
package org.serviceconnector.test.system.session;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.SCMessageFault;
import org.serviceconnector.api.SCService;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;

@SuppressWarnings("unused")
public class AfterSCAbortSessionTest {

	/** The Constant testLogger. */
	protected static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(AfterSCAbortSessionTest.class);

	private static boolean messageReceived = false;
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
	 * Description: create session after SC was aborted<br>
	 * Expectation: throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t01_createSession() throws Exception {	
		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		
		ctrl.stopServer(srvCtx); // stop test server now, it cannot be stopped without SC
		ctrl.stopSC(scCtx);
		
		response = service.createSession(request);
	}

	/**
	 * Description: delete session after SC was aborted<br>
	 * Expectation: throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t02_deleteSession() throws Exception {	
		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(request);
		
		ctrl.stopServer(srvCtx); // stop test server now, it cannot be stopped without SC
		ctrl.stopSC(scCtx);
		
		service.deleteSession();
	}
	
	/**
	 * Description: exchange message after SC was aborted<br>
	 * Expectation: throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t03_execute() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(request);
		
		ctrl.stopServer(srvCtx); // stop test server now, it cannot be stopped without SC
		ctrl.stopSC(scCtx);
		
		request.setMessageInfo("echo");
		response = service.execute(request);
	}

	/**
	 * Description: send message after SC was aborted<br>
	 * Expectation: throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t04_send() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.createSession(request);
		request.setMessageInfo("echo");
		messageReceived = false;
		MsgCallback cbk = new MsgCallback(service);
		
		ctrl.stopServer(srvCtx); // stop test server now, it cannot be stopped without SC
		ctrl.stopSC(scCtx);
		
		service.send(request, cbk);
	}
	
	private class MsgCallback extends SCMessageCallback {
		private SCMessage response = null;

		public MsgCallback(SCService service) {
			super(service);
		}

		@Override
		public void receive(SCMessage msg) {
			response = msg;
			AfterSCAbortSessionTest.messageReceived = true;
		}

		@Override
		public void receive(Exception e) {
			logger.error("receive error: "+e.getMessage());
			SCMessageFault fault = new SCMessageFault();
			try {
				fault.setAppErrorCode(1000);
				fault.setAppErrorText(e.getMessage());
			} catch (SCMPValidatorException e1) {
			}
			response = fault;
			AfterSCAbortSessionTest.messageReceived = true;
		}
	}
}
