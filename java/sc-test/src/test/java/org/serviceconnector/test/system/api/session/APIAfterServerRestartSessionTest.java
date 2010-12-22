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

	private SCSessionService service;

	@After
	public void afterOneTest() throws Exception {
		try {
			service.deleteSession();
		} catch (Exception e1) {
		}
		service = null;
		super.afterOneTest();
	}

	/**
	 * Description: exchange one message after server has been restarted<br>
	 * Expectation: throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t01_execute() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
		request.setMessageInfo(TestConstants.echoAppErrorCmd);

		ctrl.stopServer(srvCtx);
		srvCtx = ctrl.startServer(TestConstants.COMMUNICATOR_TYPE_SESSION, TestConstants.log4jSrvProperties,
				TestConstants.sesServerName1, TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, 10,
				TestConstants.sesServiceName1);

		response = service.execute(request);
	}
	
	/**
	 * Description: delete session after server has been restarted<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t05_deleteSession() throws Exception {
		SCMessage request = null;
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);

		ctrl.stopServer(srvCtx);
		srvCtx = ctrl.startServer(TestConstants.COMMUNICATOR_TYPE_SESSION, TestConstants.log4jSrvProperties,
				TestConstants.sesServerName1, TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, 10,
				TestConstants.sesServiceName1);
		
		service.deleteSession();
	}

}
