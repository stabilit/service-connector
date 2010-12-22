package org.serviceconnector.test.system.api.perf;

import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCMessageCallback;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.service.SCServiceException;

@SuppressWarnings("unused")
public class APIReceivePublicationBenchmark {

	/** The Constant testLogger. */
	protected static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(APIReceivePublicationBenchmark.class);

	private static boolean messageReceived = false;
	private static ProcessesController ctrl;
	private ProcessCtx scCtx;
	private ProcessCtx srvCtx;
	private SCClient client;
	private SCPublishService service;
	private int threadCount = 0;
	long start= 0;
	long stop = 0;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
	}

	@Before
	public void beforeOneTest() throws Exception {
		threadCount = Thread.activeCount();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		srvCtx = ctrl.startServer(TestConstants.SERVER_TYPE_PUBLISH, TestConstants.log4jSrvProperties,
				TestConstants.pubServerName1, TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100, 10,
				TestConstants.pubServiceName1);
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			service.unsubscribe();
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
		testLogger.info("Number of threads :" + Thread.activeCount() + " created :" + (Thread.activeCount() - threadCount));
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		ctrl = null;
	}

	/**
	 * Description: receive 100000 compressed messages<br>
	 * Expectation: performance better than 1000 msg/sec
	 */
	@Test
	public void benchmark_100000_msg_compressed() throws Exception {
		service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.publishCompressedMsgCmd);
		int nrMessages = 100000;
		subMsgRequest.setData(Integer.toString(nrMessages));
		MsgCallback cbk = new MsgCallback(service);
		cbk.expectedMessages = nrMessages;
		subMsgResponse = service.subscribe(subMsgRequest, cbk);
		waitForMessage(120);
		if (cbk.messageCounter == nrMessages) {
			long perf = nrMessages * 1000 / (cbk.stop - cbk.start);
			testLogger.info(nrMessages + "msg à 128 byte performance : " + perf + " msg/sec.");
			Assert.assertEquals("Performence not fast enough, only"+ perf + " msg/sec.", true, perf > 1000);
		}
		service.unsubscribe();
	}
	
	/**
	 * Description: receive 100000 compressed messages<br>
	 * Expectation: performance better than 1000 msg/sec
	 */
	@Test
	public void benchmark_100000_msg_uncompressed() throws Exception {
		service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.publishMsgUncompressedCmd);
		int nrMessages = 100000;
		subMsgRequest.setData(Integer.toString(nrMessages));
		MsgCallback cbk = new MsgCallback(service);
		cbk.expectedMessages = nrMessages;
		subMsgResponse = service.subscribe(subMsgRequest, cbk);
		waitForMessage(120);
		if (cbk.messageCounter == nrMessages) {
			long perf = nrMessages * 1000 / (cbk.stop - cbk.start);
			testLogger.info(nrMessages + "msg à 128 byte performance : " + perf + " msg/sec.");
			Assert.assertEquals("Performence not fast enough, only"+ perf + " msg/sec.", true, perf > 1000);
		}
		service.unsubscribe();
	}

	
	private void waitForMessage(int nrSeconds) throws Exception {
		for (int i = 0; i < (nrSeconds * 10); i++) {
			if (messageReceived) {
				return;
			}
			Thread.sleep(100);
		}
		throw new TimeoutException("No message received within " + nrSeconds + " seconds timeout.");
	}

	private class MsgCallback extends SCMessageCallback {
		
		private SCMessage response = null;
		private int messageCounter = 0;
		private int expectedMessages = 0;
		long start = System.currentTimeMillis();
		long stop = 0;
		long startPart = System.currentTimeMillis();
		long stopPart = 0;

		public MsgCallback(SCPublishService service) {
			super(service);
			APIReceivePublicationBenchmark.messageReceived = false;
			response = null;
			messageCounter = 0;
			expectedMessages = 0;
		}

		@Override
		public void receive(SCMessage msg) {
			response = msg;
			messageCounter++;
			
			if (((messageCounter+1) % 1000) == 0) {
				stopPart = System.currentTimeMillis();
				APIReceivePublicationBenchmark.testLogger.info("Receiving message nr. " + (messageCounter+1) + "... "+(1000000 / (stopPart - startPart))+ " msg/sec.");
				startPart = System.currentTimeMillis();
			}
			if ( expectedMessages == messageCounter) {
				stop = System.currentTimeMillis();
				APIReceivePublicationBenchmark.messageReceived = true;
			}
		}

		@Override
		public void receive(Exception e) {
			logger.error("receive error: " + e.getMessage());
			if (e instanceof SCServiceException) {
				SCMPError scError = ((SCServiceException) e).getSCMPError();
				logger.info("SC error received code:" + scError.getErrorCode() + " text:" + scError.getErrorText());
			}
			response = null;
			APIReceivePublicationBenchmark.messageReceived = true;
		}
	}
}
