package org.serviceconnector.test.system.api.session;

import org.junit.After;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.test.system.api.APISystemSuperSessionClientTest;

@SuppressWarnings("unused")
public class APIAfterServerRestartSessionTest extends APISystemSuperSessionClientTest {

	/**
	 * Description: exchange one message after server has been restarted<br>
	 * Expectation: throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t01_execute() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(sessionService);
		response = sessionService.createSession(request, cbk);
		request.setMessageInfo(TestConstants.echoAppErrorCmd);

		ctrl.stopServer(sesSrvCtx);
		sesSrvCtx = ctrl.startServer(TestConstants.COMMUNICATOR_TYPE_SESSION, TestConstants.log4jSrvProperties,
				TestConstants.sesServerName1, TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, 10,
				TestConstants.sesServiceName1);

		response = sessionService.execute(request);
	}
	
	/**
	 * Description: delete session after server has been restarted<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t05_deleteSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(sessionService);
		response = sessionService.createSession(request, cbk);

		ctrl.stopServer(sesSrvCtx);
		sesSrvCtx = ctrl.startServer(TestConstants.COMMUNICATOR_TYPE_SESSION, TestConstants.log4jSrvProperties,
				TestConstants.sesServerName1, TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, 10,
				TestConstants.sesServiceName1);
		
		sessionService.deleteSession();
	}

}
