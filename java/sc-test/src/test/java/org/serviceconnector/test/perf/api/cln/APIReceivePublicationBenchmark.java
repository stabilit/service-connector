package org.serviceconnector.test.perf.api.cln;

import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.test.perf.api.APIPerfSuperClientTest;

@SuppressWarnings("unused")
public class APIReceivePublicationBenchmark extends APIPerfSuperClientTest {

	long start = 0;
	long stop = 0;

	/**
	 * Description: receive 100000 compressed messages<br>
	 * Expectation: performance better than 1000 msg/sec
	 */
	@Test
	public void t_100000_msg_compressed() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.publishCompressedMsgCmd);
		int nrMessages = 100000;
		subMsgRequest.setData(Integer.toString(nrMessages));
		MsgCallback cbk = new MsgCallback(publishService);
		cbk.setExpectedMessages(nrMessages);
		subMsgResponse = publishService.subscribe(subMsgRequest, cbk);
		cbk.waitForMessage(120);
		if (cbk.getMessageCount() == nrMessages) {
			long perf = nrMessages * 1000 / cbk.getDifference();
			testLogger.info(nrMessages + "msg à 128 byte performance : " + perf + " msg/sec.");
			Assert.assertTrue("Performance not fast enough, only" + perf + " msg/sec.", perf > 1000);
		}
		publishService.unsubscribe();
	}

	/**
	 * Description: receive 100000 compressed messages<br>
	 * Expectation: performance better than 1000 msg/sec
	 */
	@Test
	public void t_100000_msg_uncompressed() throws Exception {
		publishService = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.publishUncompressedMsgCmd);
		int nrMessages = 100000;
		subMsgRequest.setData(Integer.toString(nrMessages));
		MsgCallback cbk = new MsgCallback(publishService);
		cbk.setExpectedMessages(nrMessages);
		subMsgResponse = publishService.subscribe(subMsgRequest, cbk);
		cbk.waitForMessage(120);
		if (cbk.getMessageCount() == nrMessages) {
			long perf = nrMessages * 1000 / cbk.getDifference();
			testLogger.info(nrMessages + "msg à 128 byte performance : " + perf + " msg/sec.");
			Assert.assertTrue("Performance not fast enough, only" + perf + " msg/sec.", perf > 1000);
		}
		publishService.unsubscribe();
	}
}

