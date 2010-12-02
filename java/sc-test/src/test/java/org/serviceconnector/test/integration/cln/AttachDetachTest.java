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
package org.serviceconnector.test.integration.cln;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;

public class AttachDetachTest {

	/** The Constant testLogger. */
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(AttachDetachTest.class);

	private static ProcessesController ctrl;
	private static ProcessCtx scCtx;
	private SCClient client;
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
			client.detach();
		} catch (Exception e) {}
		client = null;
//		assertEquals("number of threads", threadCount, Thread.activeCount());
		testLogger.info("Number of threads :" + Thread.activeCount() + " created :"+(Thread.activeCount() - threadCount));
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		try {
			ctrl.stopSC(scCtx);
			scCtx = null;
		} catch (Exception e) {}
		ctrl = null;
		ctrl = null;
	}


	/**
	 * Description: Attach and detach one time to SC on localhost, http-port and http-connection.<br>
	 * Expectation:	Client is detached.
	 */
	@Test
	public void t01_attachDetach() throws Exception {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		assertEquals("Client is detached", false, client.isAttached());
		client.attach();
		assertEquals("Client is attached", true, client.isAttached());
		client.detach();
		assertEquals("Client is detached", false, client.isAttached());
	}

	
	/**
	 * Description: Attach two times the same client to SC on localhost  http-connection type.<br>
	 * Expectation:	Throws exception on the second attach.
	 */
	@Test (expected = SCServiceException.class)
	public void t02_attachDetach() throws Exception {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		assertEquals("Client is attached", true, client.isAttached());
		client.attach();
	}

	/**
	 * Description: Detach the client without attach.<br>
	 * Expectation:	Client is detached.
	 */
	@Test
	public void t03_attachDetach() throws Exception {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		assertEquals("Client is detached", false, client.isAttached());
		client.detach();
		assertEquals("Client is detached", false, client.isAttached());
	}

	/**
	 * Description: first attach, then detach 100 times.<br>
	 * Expectation:	Client is detached.
	 */
	@Test
	public void t04_attachDetach() throws Exception {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		assertEquals("Client is attached", true, client.isAttached());
		int nr = 100;
		for (int i = 0; i < nr; i++) {
			client.detach();
		}
		assertEquals("Client is detached", false, client.isAttached());
	}
	
	/**
	 * Description: Attach and detach 5000 times to SC on localhost, tcp-connection type.<br>
	 * Expectation:	Client is detached.
	 */
	@Test
	public void t05_attachDetach() throws Exception  {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		int nr = 5000;
		int sleep = 0;
		for (int i = 0; i < nr; i++) {
			if (((i+1) % 100) == 0) testLogger.info("Attach/detach nr. " + (i+1) + "...");
			client.attach();
			assertEquals("Client is attached", true, client.isAttached());
			if (sleep > 0) 
				Thread.sleep(sleep);
			client.detach();
			assertEquals("Client is detached", false, client.isAttached());
		}
	}

	/**
	 * Description: Attach and detach 5000 times to SC on localhost, http-connection type.<br>
	 * Expectation:	Client is detached.
	 */
	@Test
	public void t06_attachDetach() throws Exception  {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		int nr = 5000;
		int sleep = 0;
		for (int i = 0; i < nr; i++) {
			if (((i+1) % 100) == 0) testLogger.info("Attach/detach nr. " + (i+1) + "...");
			client.attach();
			assertEquals("Client is attached", true, client.isAttached());
			if (sleep > 0) 
				Thread.sleep(sleep);
			client.detach();
			assertEquals("Client is detached", false, client.isAttached());
		}
	}


	/**
	 * Description: Attach 5000 clients then detach them all.<br>
	 * Expectation:	All clients are detached.
	 */
	@Test
	public void t07_attachDetach() throws Exception {
		int clientsCount = 5000;
		SCClient[] clients = new SCClient[clientsCount];
		// create clients
		for (int i= 0; i < clientsCount; i++) {
			clients[i] = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		}
		//attach
		for (int i= 0; i < clientsCount; i++) {
			if (((i+1) % 200) == 0) testLogger.info("Attaching client nr. " + (i+1) );
			clients[i].attach();
			assertEquals("Client is attached", true, clients[i].isAttached());
		}
		//detach
		for (int i = 0; i < clientsCount; i++) {
			if (((i+1) % 200) == 0) testLogger.info("Detaching client nr. " + (i+1) + "...");
			clients[i].detach();
			assertEquals("Client is detached", false, clients[i].isAttached());
		}
		clients = null;
	}
		
}
