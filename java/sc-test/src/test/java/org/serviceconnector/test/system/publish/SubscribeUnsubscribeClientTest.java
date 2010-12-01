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

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
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
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.service.SCServiceException;

public class SubscribeUnsubscribeClientTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SubscribeUnsubscribeClientTest.class);

	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	private static Process scProcess;
	private static Process srvProcess;

	private SCClient client;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
			srvProcess = ctrl.startServer(TestConstants.SERVER_TYPE_PUBLISH, TestConstants.log4jSrvProperties,
					TestConstants.PORT_LISTENER, TestConstants.PORT_TCP, 100,
					new String[] { TestConstants.publishServiceNames });
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@Before
	public void setUp() throws Exception {
		client = new SCClient();
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
	}

	@After
	public void tearDown() throws Exception {
		client.detach();
		client = null;
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSCProperties);
		ctrl.stopProcess(srvProcess, TestConstants.log4jSrvProperties);
		ctrl = null;
		scProcess = null;
		srvProcess = null;
	}

	@Test
	public void unsubscribe_serviceNameEmpty_notSubscribedEmptySessionId() throws Exception {
		SCPublishService service = client.newPublishService("");
		service.unsubscribe();
		assertEquals(null, service.getSessionId());
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void unsubscribe_serviceNameWhiteSpace_notSubscribedEmptySessionId() throws Exception {
		SCPublishService service = client.newPublishService(" ");
		service.unsubscribe();
		assertEquals(null, service.getSessionId());
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void unsubscribe_serviceNameOneChar_notSubscribedEmptySessionId() throws Exception {
		SCPublishService service = client.newPublishService("a");
		service.unsubscribe();
		assertEquals(null, service.getSessionId());
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void unsubscribe_serviceNameWhitePangram_notSubscribedEmptySessionId() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.pangram);
		service.unsubscribe();
		assertEquals(null, service.getSessionId());
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void unsubscribe_serviceNameDisabledService_notSubscribedEmptySessionId() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.publishServiceNames);
		service.unsubscribe();
		assertEquals(null, service.getSessionId());
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void unsubscribe_serviceNameValid_notSubscribedEmptySessionId() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.publishServiceNames);
		service.unsubscribe();
		assertEquals(null, service.getSessionId());
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void unsubscribeSubscribe_subscriptionValid_isSubscribedHasSessionId() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.publishServiceNames);
		service.unsubscribe();
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(true, service.isSubscribed());
		service.unsubscribe();
	}

	@Test
	public void subscribeUnsubscribe_subscriptionValid_isSubscribedThenNot() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.publishServiceNames);
		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(true, service.isSubscribed());
		service.unsubscribe();
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribeUnsubscribe_subscriptionThrowsValidatorException_unsubscribePasses() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.publishServiceNames);
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(null);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (SCMPValidatorException e) {
		}
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
		service.unsubscribe();
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribeUnsubscribe_subscriptionThrowsSCException_unsubscribePasses() throws Exception {
		SCPublishService service = client.newPublishService("");
		try {
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.mask);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		} catch (SCServiceException e) {
		}
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
		service.unsubscribe();
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	// TODO FJU after subscribe -> unsubscribe -> subscribe, NullPointer is thrown
	@Test
	public void subscribeUnsubscribe_twice_isSubscribedThenNot() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.publishServiceNames);

		SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
		subscibeMessage.setMask(TestConstants.mask);
		subscibeMessage.setSessionInfo("sessionInfo");
		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(true, service.isSubscribed());
		service.unsubscribe();
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());

		service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
		assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(true, service.isSubscribed());
		service.unsubscribe();
		assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
		assertEquals(false, service.isSubscribed());
	}

	@Test
	public void subscribeUnsubscribe_10000Times_isSubscribedThenNot() throws Exception {
		SCPublishService service = client.newPublishService(TestConstants.publishServiceNames);

		int loop = 10000;
		for (int i = 0; i < loop; i++) {
			if ((i % 500) == 0)
				testLogger.info("subscribeUnsubscribe_10000Times cycle:\t" + i + " ...");
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask(TestConstants.mask);
			subscibeMessage.setSessionInfo("sessionInfo");
			service.subscribe(subscibeMessage, new DemoPublishClientCallback(service));
			assertEquals(false, service.getSessionId() == null || service.getSessionId().equals(""));
			assertEquals(true, service.isSubscribed());
			service.unsubscribe();
			Thread.sleep(5); // TODO little sleep, Netty has problems sending very fast! must be fixed
			assertEquals(true, service.getSessionId() == null || service.getSessionId().equals(""));
			assertEquals(false, service.isSubscribed());
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
		}
	}
}
