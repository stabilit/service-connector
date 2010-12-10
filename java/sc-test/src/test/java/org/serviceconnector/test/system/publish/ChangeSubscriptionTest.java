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
package org.serviceconnector.test.system.publish;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.SCService;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;

@SuppressWarnings("unused")
public class ChangeSubscriptionTest {
	
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ChangeSubscriptionTest.class);

	/** The Constant logger. */
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	private static boolean messageReceived = false;
	private static ProcessesController ctrl;
	private ProcessCtx scCtx;
	private ProcessCtx srvCtx;
	private SCClient client;
	private SCPublishService service;
	private int threadCount = 0;

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

	// TODO FJU in most of these tests is needed assertion of the changed mask
	@Test
	public void changeSubscription_serviceNameEmptyNotEstablishedPreviousSubscription_throwsSCException()
			throws Exception {
		SCPublishService service = client.newPublishService("");
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.mask);
			service.changeSubscription(subscibeMessage);
		} catch (Exception e) {
			ex = e;
		} finally {
			Assert.assertEquals(SCServiceException.class, ex.getClass());
			Assert.assertEquals(null, service.getSessionId());
			Assert.assertEquals(false, service.isSubscribed());
		}
	}

	@Test
	public void changeSubscription_serviceNameValidNotEstablishedPreviousSubscription_throwsSCException()
			throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.pubServiceName1);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.mask);
			service.changeSubscription(subscibeMessage);
		} catch (Exception e) {
			ex = e;
		} finally {
			Assert.assertEquals(SCServiceException.class, ex.getClass());
			Assert.assertEquals(null, service.getSessionId());
			Assert.assertEquals(false, service.isSubscribed());
		}
	}

	@Test
	public void changeSubscription_afterUnsubscribed_throwsSCException() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		service.unsubscribe();
		try {
			service.changeSubscription(subscibeMessage);
		} catch (Exception e) {
			ex = e;
		} finally {
			Assert.assertEquals(SCServiceException.class, ex.getClass());
			Assert.assertEquals(null, service.getSessionId());
			Assert.assertEquals(false, service.isSubscribed());
		}
	}

	@Test
	public void changeSubscription_toTheSameMask_passes() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		service.changeSubscription(subscibeMessage);
		Assert.assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		Assert.assertEquals(true, service.isSubscribed());
		service.unsubscribe();
		Assert.assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		Assert.assertEquals(false, service.isSubscribed());
	}

	@Test
	public void changeSubscription_toMaskNull_throwsValidatorException() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		try {
			service.changeSubscription(null);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(SCMPValidatorException.class, ex.getClass());
		Assert.assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		Assert.assertEquals(true, service.isSubscribed());
		service.unsubscribe();
	}

	@Test
	public void changeSubscription_toMaskEmpty_throwsValidatorException() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		try {
			subscibeMessage.setMask("");
			service.changeSubscription(subscibeMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(SCMPValidatorException.class, ex.getClass());
		Assert.assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		Assert.assertEquals(true, service.isSubscribed());
		service.unsubscribe();
	}

	@Test
	public void changeSubscription_toMaskWhiteSpace_passes() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		subscibeMessage.setMask(" ");
		service.changeSubscription(subscibeMessage);
		Assert.assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		Assert.assertEquals(true, service.isSubscribed());
		service.unsubscribe();
	}

	@Test
	public void changeSubscription_toMaskOneChar_passes() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		subscibeMessage.setMask("a");
		service.changeSubscription(subscibeMessage);
		Assert.assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		Assert.assertEquals(true, service.isSubscribed());
		service.unsubscribe();
	}

	// TODO JOT knows about
	@Test
	public void changeSubscription_toMaskPangram_passes() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		subscibeMessage.setMask(TestConstants.pangram);
		service.changeSubscription(subscibeMessage);
		Assert.assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		Assert.assertEquals(true, service.isSubscribed());
		service.unsubscribe();
	}

	@Test
	public void changeSubscription_toMask256LongString_passes() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		subscibeMessage.setMask(TestConstants.stringLength256);
		service.changeSubscription(subscibeMessage);
		Assert.assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		Assert.assertEquals(true, service.isSubscribed());
		service.unsubscribe();
	}

	@Test
	public void changeSubscription_toMask257LongString_throwsValidatorException() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		try {
			subscibeMessage.setMask(TestConstants.stringLength257);
			service.changeSubscription(subscibeMessage);
		} catch (Exception e) {
			ex = e;
		}
		Assert.assertEquals(SCMPValidatorException.class, ex.getClass());
		Assert.assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		Assert.assertEquals(true, service.isSubscribed());
		service.unsubscribe();
	}

	@Test
	public void changeSubscription_twice_passes() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));

		service.changeSubscription(subscibeMessage);
		service.changeSubscription(subscibeMessage);

		Assert.assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		Assert.assertEquals(true, service.isSubscribed());
		service.unsubscribe();
	}

	@Test
	public void changeSubscription_10000Times_passes() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		int loop = 10000;
		for (int i = 0; i < loop; i++) {
			if ((i % 500) == 0)
				testLogger.info("changeSubscription_10000Times cycle:\t" + i + " ...");
			service.changeSubscription(subscibeMessage);
			Thread.sleep(5); // TODO little sleep required in this Netty version. Known bug, will be fixed soon

		}
		Assert.assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		Assert.assertEquals(true, service.isSubscribed());
		service.unsubscribe();
	}

	// TODO JOT knows about
	@Test
	public void subscribeChangeSubscriptionUnsubscribe_10000Times_passes() throws Exception {
		int loop = 10000;
		for (int i = 0; i < loop; i++) {
			if ((i % 500) == 0)
				testLogger.info("ubscribeChangeSubscriptionUnsubscribe_10000Times cycle:\t" + i + " ...");
			SCPublishService service = client.newPublishService(TestConstants.pubServiceName1);
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.mask);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
			service.changeSubscription(subscibeMessage);
			Thread.sleep(5); // TODO little sleep required in this Netty version. Known bug, will be fixed soon
			Assert.assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
			Assert.assertEquals(true, service.isSubscribed());
			service.unsubscribe();
		}
	}

	private class DemoPublishClientCallback extends SCMessageCallback {

		public DemoPublishClientCallback(SCService service) {
			super(service);
		}

		@Override
		public void receive(SCMessage reply) {
			logger.info("Publish client received: " + reply.getData());
		}

		@Override
		public void receive(Exception e) {
			System.err.println(e);
		}
	}
}
