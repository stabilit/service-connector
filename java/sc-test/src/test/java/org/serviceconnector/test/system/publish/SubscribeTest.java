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

import java.security.InvalidParameterException;
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
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.SCService;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.service.SCServiceException;

@SuppressWarnings("unused")
public class SubscribeTest {

	/** The Constant testLogger. */
	protected static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SubscribeTest.class);

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

	/**
	 * Description: subscribe (regular)<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_subscribe() throws Exception {
		service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo("doNothing");
		subMsgRequest.setData("certificate or what so ever");
		subMsgRequest.setNoDataIntervalInSeconds(100);
		MsgCallback cbk = new MsgCallback(service);
		subMsgResponse = service.subscribe(subMsgRequest, cbk);
		Assert.assertNotNull("the session ID is null", service.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
		Assert.assertTrue("is not subscribed", service.isSubscribed());

		service.unsubscribe();
		Assert.assertNull("the session ID is not null", service.getSessionId());
	}

	/**
	 * Description: subscribe with mask = null<br>
	 * Expectation: throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t02_subscribe() throws Exception {
		service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(null);
		subMsgRequest.setSessionInfo("doNothing");
		subMsgRequest.setData("certificate or what so ever");
		subMsgRequest.setNoDataIntervalInSeconds(100);
		MsgCallback cbk = new MsgCallback(service);
		subMsgResponse = service.subscribe(subMsgRequest, cbk);
	}

	/**
	 * Description: subscribe with service name = null<br>
	 * Expectation: throws InvalidParameterException
	 */
	@Test (expected = InvalidParameterException.class)
	public void t04_subscribe() throws Exception {
		service = client.newPublishService(null);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(" ");
		subMsgRequest.setSessionInfo("doNothing");
		subMsgRequest.setData("certificate or what so ever");
		subMsgRequest.setNoDataIntervalInSeconds(100);
		MsgCallback cbk = new MsgCallback(service);
		subMsgResponse = service.subscribe(subMsgRequest, cbk);
	}

	/**
	 * Description: subscribe with service name = ""<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t05_subscribe() throws Exception {
		service = client.newPublishService("");
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(" ");
		subMsgRequest.setSessionInfo("doNothing");
		subMsgRequest.setData("certificate or what so ever");
		subMsgRequest.setNoDataIntervalInSeconds(100);
		MsgCallback cbk = new MsgCallback(service);
		subMsgResponse = service.subscribe(subMsgRequest, cbk);
	}

	/**
	 * Description: subscribe with service name = " "<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t06_subscribe() throws Exception {
		service = client.newPublishService(" ");
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(" ");
		subMsgRequest.setSessionInfo("doNothing");
		subMsgRequest.setData("certificate or what so ever");
		subMsgRequest.setNoDataIntervalInSeconds(100);
		MsgCallback cbk = new MsgCallback(service);
		subMsgResponse = service.subscribe(subMsgRequest, cbk);
	}

	/**
	 * Description: subscribe with non-existing service name<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t07_subscribe() throws Exception {
		service = client.newPublishService("gaga");
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(" ");
		subMsgRequest.setSessionInfo("doNothing");
		subMsgRequest.setData("certificate or what so ever");
		subMsgRequest.setNoDataIntervalInSeconds(100);
		MsgCallback cbk = new MsgCallback(service);
		subMsgResponse = service.subscribe(subMsgRequest, cbk);
	}
	
	/**
	 * Description: subscribe with session service name<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t08_subscribe() throws Exception {
		service = client.newPublishService(TestConstants.sesServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(" ");
		subMsgRequest.setSessionInfo("doNothing");
		subMsgRequest.setData("certificate or what so ever");
		subMsgRequest.setNoDataIntervalInSeconds(100);
		MsgCallback cbk = new MsgCallback(service);
		subMsgResponse = service.subscribe(subMsgRequest, cbk);
	}
	
	/**
	 * Description: subscribe with file service name<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t09_subscribe() throws Exception {
		service = client.newPublishService(TestConstants.filServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(" ");
		subMsgRequest.setSessionInfo("doNothing");
		subMsgRequest.setData("certificate or what so ever");
		subMsgRequest.setNoDataIntervalInSeconds(100);
		MsgCallback cbk = new MsgCallback(service);
		subMsgResponse = service.subscribe(subMsgRequest, cbk);
	}

	/**
	 * Description: subscribe with callback = null<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t10_subscribe() throws Exception {
		service = client.newPublishService(TestConstants.filServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(" ");
		subMsgRequest.setSessionInfo("doNothing");
		subMsgRequest.setData("certificate or what so ever");
		subMsgRequest.setNoDataIntervalInSeconds(100);
		subMsgResponse = service.subscribe(subMsgRequest, null);
	}


	/**
	 * Description: subscribe to disabed service<br>
	 * Expectation: throws SCServiceException
	 */
	@Test (expected = SCServiceException.class )
	public void t20_disabledService() throws Exception {
		// disable service
		SCMgmtClient clientMgmt = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP);
		clientMgmt.attach();
		clientMgmt.disableService(TestConstants.pubServiceName1);
		clientMgmt.detach();

		service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo("doNothing");
		subMsgRequest.setData("certificate or what so ever");
		subMsgRequest.setNoDataIntervalInSeconds(100);
		MsgCallback cbk = new MsgCallback(service);
		subMsgResponse = service.subscribe(subMsgRequest, cbk);
	}

	/**
	 * Description: subscribe service with default noDataInterval <br>
	 * Expectation: passes
	 */
	@Test 
	public void t30_noDataInterval() throws Exception {
		service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo("doNothing");
		subMsgRequest.setData("certificate or what so ever");
		MsgCallback cbk = new MsgCallback(service);
		subMsgResponse = service.subscribe(subMsgRequest, cbk);
		Assert.assertNotNull("the session ID is null", service.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
		Assert.assertTrue("is not subscribed", service.isSubscribed());

		service.unsubscribe();
		Assert.assertNull("the session ID is not null", service.getSessionId());
	}

	/**
	 * Description: two subscriptions from the same client<br>
	 * Expectation: passes
	 */
	@Test
	public void t40_twoSubscriptions() throws Exception {
		SCPublishService service1 = client.newPublishService(TestConstants.pubServiceName1);
		SCPublishService service2 = client.newPublishService(TestConstants.pubServiceName1);
		
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo("doNothing");
		subMsgRequest.setData("certificate or what so ever");
		subMsgRequest.setNoDataIntervalInSeconds(100);
		
		MsgCallback cbk1 = new MsgCallback(service1);
		MsgCallback cbk2 = new MsgCallback(service2);
		
		subMsgResponse = service1.subscribe(subMsgRequest, cbk1);
		Assert.assertNotNull("the session ID is null", service1.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
		Assert.assertTrue("is not subscribed", service1.isSubscribed());
		
		subMsgResponse = service2.subscribe(subMsgRequest, cbk2);
		Assert.assertNotNull("the session ID is null", service2.getSessionId());
		Assert.assertEquals("message body is not the same length", subMsgRequest.getDataLength(), subMsgResponse.getDataLength());
		Assert.assertEquals("compression is not the same", subMsgRequest.isCompressed(), subMsgResponse.isCompressed());
		Assert.assertTrue("is not subscribed", service2.isSubscribed());
		
		service1.unsubscribe();
		Assert.assertNull("the session ID is not null)", service1.getSessionId());
		service2.unsubscribe();
		Assert.assertNull("the session ID is not null)", service2.getSessionId());
	}

	/**
	 * Description: subscribe twice<br>
	 * Expectation: throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t41_subscribeTwice() throws Exception {
		service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage();
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(null);
		subMsgRequest.setSessionInfo("doNothing");
		subMsgRequest.setData("certificate or what so ever");
		MsgCallback cbk = new MsgCallback(service);
		subMsgResponse = service.subscribe(subMsgRequest, cbk);
		Assert.assertNotNull("the session ID is null", service.getSessionId());
		subMsgResponse = service.subscribe(subMsgRequest, cbk);
	}

	
	/**
	 * Description: reject subscription by server <br>
	 * Expectation: throws SCServiceException
	 */
	@Test (expected = SCServiceException.class )
	public void t50_reject() throws Exception {
		service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.rejectSessionCmd);
		subMsgRequest.setData("certificate or what so ever");
		subMsgRequest.setNoDataIntervalInSeconds(100);
		MsgCallback cbk = new MsgCallback(service);
		subMsgResponse = service.subscribe(subMsgRequest, cbk);
	}
	
	/**
	 * Description: reject subscription by server<br>
	 * Expectation: passes 
	 */
	@Test
	public void t51_reject() throws Exception {
		service = client.newPublishService(TestConstants.pubServiceName1);
		SCSubscribeMessage subMsgRequest = new SCSubscribeMessage(TestConstants.pangram);
		SCSubscribeMessage subMsgResponse = null;
		subMsgRequest.setMask(TestConstants.mask);
		subMsgRequest.setSessionInfo(TestConstants.rejectSessionCmd);
		subMsgRequest.setData("certificate or what so ever");
		subMsgRequest.setNoDataIntervalInSeconds(100);
		MsgCallback cbk = new MsgCallback(service);

		Boolean passed = false;
		try {
			subMsgResponse = service.subscribe(subMsgRequest, cbk);
		} catch (SCServiceException e) {
			passed = true;
			Assert.assertEquals("is not appErrorCode", 4000, e.getAppErrorCode());
			Assert.assertEquals("is not appErrorText", false, e.getAppErrorText().equals(""));
		}
		Assert.assertTrue("did not throw exception", passed);
		Assert.assertFalse("is subscribed", service.isSubscribed());
		service.unsubscribe();
		Assert.assertNull("the session ID is not null)", service.getSessionId());
	}

	private class MsgCallback extends SCMessageCallback {
		private SCMessage response = null;

		public MsgCallback(SCService service) {
			super(service);
		}

		@Override
		public void receive(SCMessage msg) {
			response = msg;
			SubscribeTest.messageReceived = true;
		}

		@Override
		public void receive(Exception e) {
			logger.error("receive error: " + e.getMessage());
			if (e instanceof SCServiceException) {
				SCMPError scError = ((SCServiceException) e).getSCMPError();
				logger.info("SC error code:" + scError.getErrorCode() + " text:" + scError.getErrorText());
			}
			response = null;
			SubscribeTest.messageReceived = true;
		}
	}
}