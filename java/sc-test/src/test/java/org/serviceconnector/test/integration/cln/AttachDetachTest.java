/*
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
package org.serviceconnector.test.integration.cln;

import static org.junit.Assert.*;

import java.security.InvalidParameterException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnetor.TestConstants;

public class AttachDetachTest {

	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(AttachDetachTest.class);

	private int threadCount = 0;

	private SCClient client;

	private static ProcessesController ctrl;
	private static Process scProcess;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopSC(scProcess, TestConstants.log4jSCProperties);
		ctrl = null;
		scProcess = null;
	}

	@Before
	public void setUp() {
		// threadCount = Thread.activeCount();
		client = new SCClient();
		((SCClient) client).setConnectionType("netty.http");
	}

	@After
	public void tearDown() {
		client = null;
		// assertEquals("number of threads", threadCount, Thread.activeCount());
	}


	private void testAttachDetachCycle(String host, int port, int cicle, int sleep) throws Exception  {
		int i = 0;
		try {
			for (i = 0; i < cicle; i++) {
				client.attach(host, port);
				assertEquals(true, client.isAttached());
				if (sleep > 0) 
					Thread.sleep(sleep);
				client.detach();
				assertEquals(false, client.isAttached());
				if (((i+1) % 100) == 0)
					testLogger.info("Executing cycle nr. " + (i+1) + "...");
			}
		} catch (Exception ex){
			assertFalse("Clients Count:"+i+"  Exception, error msg:"+ex.getMessage(), true);
		}
	}

	private void testAttachAllDetachALL(String host, int port, int clientsCount) throws Exception {
		SCClient[] clients = new SCClient[clientsCount];
		int i = 0;
		try {
			for (; i < clientsCount; i++) {
				if (((i+1) % 100) == 0) testLogger.info("Attaching client nr. " + (i+1) + "...");
				clients[i] = new SCClient();
				((SCClient) clients[i]).setConnectionType("netty.http");
				clients[i].attach(host, port);
			}
		} catch (InvalidParameterException ex) {
			assertFalse("Attach, clientsCount:"+i+"  InvalidParameterException, error msg:"+ex.getMessage(), true);
		} catch (Exception ex){
			assertFalse("Attach, clientsCount:"+i+"  Exception, error msg:"+ex.getMessage(), true);
		}
		try {
			for (i = 0; i < clientsCount; i++) {
				if (((i+1) % 100) == 0) testLogger.info("Detaching client nr. " + (i+1) + "...");
				assertEquals(true, clients[i].isAttached());
				clients[i].detach();
				assertEquals(false, clients[i].isAttached());
			}
		} catch (Exception ex){
			assertFalse("Detach, clientsCount:"+i+"  Exception, error msg:"+ex.getMessage(), true);
		}
	}

	/**
	 * Description: Attach and detach one time with default host and http-port.<br>
	 * Expectation:	Client is detached.
	 */
	@Test
	public void attachDetach_1() throws Exception {
		assertEquals(false, client.isAttached());
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		assertEquals(true, client.isAttached());
		client.detach();
		assertEquals(false, client.isAttached());
	}

	
	/**
	 * Description: Attach two time the same client and detach one time with default host and http-port.<br>
	 * Expectation:	Throws exception on the second attach and detached.
	 */
	@Test
	public void attachDetach_2() throws Exception {
		Exception ex = null;
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		try {
			client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, client.isAttached());
		client.detach();
		assertEquals(false, client.isAttached());
	}

	/**
	 * Description: Attach two time the same client and detach one time with twice different parameters.<br>
	 * Expectation:	Throws exception on the second attach and detached.
	 */
	@Test
	public void attachDetach_3() throws Exception {
		Exception ex = null;
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		try {
			((SCClient) client).setConnectionType("netty.tcp");
			client.attach(TestConstants.HOST, TestConstants.PORT_TCP);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, client.isAttached());
		client.detach();
		assertEquals(false, client.isAttached());
	}

	/**
	 * Description: Attach two time the same client and detach one time with twice different parameters.<br>
	 * Expectation:	Throws exception on the second attach and detached.
	 */
	@Test
	public void attachDetach_4() throws Exception {
		Exception ex = null;
		assertEquals(false, client.isAttached());

		((SCClient) client).setConnectionType("netty.tcp");
		client.attach(TestConstants.HOST, TestConstants.PORT_TCP);
		try {
			((SCClient) client).setConnectionType("netty.http");
			client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, client.isAttached());
		client.detach();
		assertEquals(false, client.isAttached());
	}

	/**
	 * Description: Detach the client without to attach.<br>
	 * Expectation:	Client is detached.
	 */
	@Test
	public void detach_1() throws Exception {
		assertEquals(false, client.isAttached());
		client.detach();
		assertEquals(false, client.isAttached());
	}

	/**
	 * Description: Attach and detach 10 times with sleep time 1sec between attach and detach.<br>
	 * Expectation:	Client is detached.
	 */
	@Test
	public void attachDetach_5() throws Exception {
		this.testAttachDetachCycle(TestConstants.HOST, TestConstants.PORT_HTTP, 10, 1000);
	}

	/**
	 * Description: Attach first and then detach 100 times.<br>
	 * Expectation:	Client is detached.
	 */
	@Test
	public void attachDetach_6() throws Exception {
		this.testAttachDetachCycle(TestConstants.HOST, TestConstants.PORT_HTTP, 100, 0);
	}

	/**
	 * Description: Attach and detach 5000 times.<br>
	 * Expectation:	Client is detached.
	 */
	@Test
	public void attachDetach_7() throws Exception  {
		this.testAttachDetachCycle(TestConstants.HOST, TestConstants.PORT_HTTP, 5000, 0);
	}

	/**
	 * Description: Attach first and then detach all 100 times.<br>
	 * Expectation:	Client is detached.
	 */
	@Test
	public void attachDetach_8() throws Exception {
		this.testAttachAllDetachALL(TestConstants.HOST, TestConstants.PORT_HTTP, 10);
	}
	
	/**
	 * Description: Attach first and then detach all 500 times.<br>
	 * Expectation:	Client is detached.
	 */
	@Test
	public void attachDetach_9() throws Exception {
		this.testAttachAllDetachALL(TestConstants.HOST, TestConstants.PORT_HTTP, 500);
	}

	/**
	 * Description: Attach first and then detach all 1000 times.<br>
	 * Expectation:	Client is detached.
	 */
	@Test
	public void attachDetach_10() throws Exception {
		this.testAttachAllDetachALL(TestConstants.HOST, TestConstants.PORT_HTTP, 1000);
	}

	
}
