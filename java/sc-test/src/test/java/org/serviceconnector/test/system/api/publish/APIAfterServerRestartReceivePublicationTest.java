package org.serviceconnector.test.system.api.publish;

import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.test.system.api.APISystemSuperPublishClientTest;

public class APIAfterServerRestartReceivePublicationTest extends APISystemSuperPublishClientTest {

	/**
	 * Description: receive after server restart <br>
	 * Expectation: ?
	 */
	@Test
	public void t01_receive() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		cbk = new MsgCallback(publishService);
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.publishCompressedMsgCmd);
		int nrMessages = 1;
		subMsgRequest.setData(Integer.toString(nrMessages));
		cbk.setExpectedMessages(nrMessages);
		subMsgResponse = publishService.subscribe(subMsgRequest, cbk);
		
		ctrl.stopServer(pubSrvCtx);
		pubSrvCtx = ctrl.startServer(TestConstants.COMMUNICATOR_TYPE_PUBLISH, TestConstants.log4jSrvProperties,
				TestConstants.pubServerName1, TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, 10,
				TestConstants.pubServiceName1);
		
		cbk.waitForMessage(10);
		Assert.assertTrue("Test is not implemented", false);
	}
	}
