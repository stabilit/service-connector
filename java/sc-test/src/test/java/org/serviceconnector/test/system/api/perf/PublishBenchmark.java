package org.serviceconnector.test.system.api.perf;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCPublishMessage;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.srv.SCPublishServer;
import org.serviceconnector.api.srv.SCPublishServerCallback;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.integration.api.srv.PublishServerTest;

public class PublishBenchmark {

	/** The Constant testLogger. */
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PublishServerTest.class);

	private static ProcessesController ctrl;
	private static ProcessCtx scCtx;
	private SCServer server;
	private SCPublishServer publishServer;
	private int threadCount = 0;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
	}

	@Before
	public void beforeOneTest() throws Exception {
		threadCount = Thread.activeCount();
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			publishServer.deregister();
		} catch (Exception e) {}
		publishServer = null;
		try {
			server.stopListener();
		} catch (Exception e) {}
		try {
			server.destroy();
		} catch (Exception e) {}

		server = null;
//		Assert.assertEquals("number of threads", threadCount, Thread.activeCount());
		testLogger.info("Number of threads :" + Thread.activeCount() + " created :"+(Thread.activeCount() - threadCount));
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		try {
			ctrl.stopSC(scCtx);
			scCtx = null;
		} catch (Exception e) {}
		ctrl = null;
	}	

	/**
	 * Description:	publish 10000 compressed messages à 128 bytes to the server.<br>
	 * Expectation:	passes
	 */
	@Test
	public void benchmark_10000_msg_compressed() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new SrvCallback(publishServer);
		publishServer.register(10, 2, cbk);
		SCPublishMessage publishMessage = new SCPublishMessage(new byte[128]);
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setCompressed(true);
		int nrMessages = 10000;
		long start = System.currentTimeMillis();
		long startPart = System.currentTimeMillis();
		long stopPart = 0;
		for (int i = 0; i < nrMessages; i++) {
			if (((i+1) % 1000) == 0) {
				stopPart = System.currentTimeMillis();
				testLogger.info("Publishing message nr. " + (i+1) + "... "+(1000000 / (stopPart - startPart))+ " msg/sec.");
				startPart = System.currentTimeMillis();
			}
			publishServer.publish(publishMessage);
		}
		long stop = System.currentTimeMillis();
		long perf = nrMessages * 1000 / (stop - start);
		testLogger.info(nrMessages + "msg à 128 byte performance : " + perf + " msg/sec.");
		Assert.assertEquals("Performence not fast enough, only"+ perf + " msg/sec.", true, perf > 1000);
	}

	/**
	 * Description:	publish 10000 uncompressed messages à 128 bytes to the server.<br>
	 * Expectation:	passes
	 */
	@Test
	public void benchmark_10000_msg_uncompressed() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER, ConnectionType.NETTY_TCP); 
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new SrvCallback(publishServer);
		publishServer.register(10, 2, cbk);
		SCPublishMessage publishMessage = new SCPublishMessage(new byte[128]);
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setCompressed(false);
		int nrMessages = 10000;
		long start = System.currentTimeMillis();
		long startPart = System.currentTimeMillis();
		long stopPart = 0;
		for (int i = 0; i < nrMessages; i++) {
			if (((i+1) % 1000) == 0) {
				stopPart = System.currentTimeMillis();
				testLogger.info("Publishing message nr. " + (i+1) + "... "+(1000000 / (stopPart - startPart))+ " msg/sec.");
				startPart = System.currentTimeMillis();
			}
			publishServer.publish(publishMessage);
		}
		long stop = System.currentTimeMillis();
		long perf = nrMessages * 1000 / (stop - start);
		testLogger.info(nrMessages + "msg à 128 byte performance : " + perf + " msg/sec.");
		Assert.assertEquals("Performence not fast enough, only"+ perf + " msg/sec.", true, perf > 1200);
	}
	
	private class SrvCallback extends SCPublishServerCallback {

		public SrvCallback(SCPublishServer server) {
			super(server);
		}
		@Override
		public SCMessage changeSubscription(SCSubscribeMessage message, int operationTimeoutInMillis) {
			return message;
		}

		@Override
		public SCMessage subscribe(SCSubscribeMessage message, int operationTimeoutInMillis) {
			return message;
		}

		@Override
		public void unsubscribe(SCSubscribeMessage message, int operationTimeoutInMillis) {
		}
	}

}
