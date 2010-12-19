package org.serviceconnector.test.system.api.publish;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.test.system.APISystemSuperPublishClientTest;

public class APIAfterServerRestartReceivePublicationTest extends APISystemSuperPublishClientTest {

	private SCPublishService service;

	@After
	public void afterOneTest() throws Exception {
		try {
			service.unsubscribe();
		} catch (Exception e1) {
		}
		service = null;
		super.afterOneTest();
	}

	/**
	 * Description: receive after server restart <br>
	 * Expectation: ?
	 */
	@Test
	public void t01_receive() throws Exception {
		service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		cbk = new MsgCallback(service);
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.publishCompressedMsgCmd);
		int nrMessages = 1;
		subMsgRequest.setData(Integer.toString(nrMessages));
		cbk.setExpectedMessages(nrMessages);
		subMsgResponse = service.subscribe(subMsgRequest, cbk);
		
		ctrl.stopServer(srvCtx);
		srvCtx = ctrl.startServer(TestConstants.SERVER_TYPE_PUBLISH, TestConstants.log4jSrvProperties,
				TestConstants.pubServerName1, TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, 10,
				TestConstants.pubServiceName1);
		
		waitForMessage(10);
		Assert.assertTrue("Test is not implemented", false);
	}
	}
