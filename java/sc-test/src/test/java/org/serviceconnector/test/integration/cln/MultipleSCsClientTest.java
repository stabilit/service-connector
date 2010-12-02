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


public class MultipleSCsClientTest {
	
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(MultipleSCsClientTest.class);
	
	/** The Constant testLogger. */
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());
	
	private int threadCount = 0;
	private static ProcessesController ctrl;
	private static ProcessCtx scCtx2;
	private static ProcessCtx scCtx1;
	private SCClient client1;
	private SCClient client2;
	
	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
		scCtx1 = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		scCtx2 = ctrl.startSC(TestConstants.log4jSCcascadedProperties, TestConstants.SCcascadedProperties);
	}

	@Before
	public void beforeOneTest() throws Exception {
		threadCount = Thread.activeCount();
		client1 = null;
		client2 = null;		
	}
	
	@After
	public void afterOneTest() throws Exception {
		client1 = null;
		client2 = null;
//		assertEquals("number of threads", threadCount, Thread.activeCount());
		testLogger.info("Number of threads :" + Thread.activeCount() + " created :"+(Thread.activeCount() - threadCount));
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		try {
			ctrl.stopSC(scCtx2);
			scCtx2 = null;
		} catch (Exception e) {
		}
		try {
			ctrl.stopSC(scCtx1);
			scCtx2 = null;
		} catch (Exception e) {
		}
		ctrl = null;
	}

	/**
	 * Description: Attach and detach two clients with:<br>
	 * <table>
	 * <tr><td></td><td>Client 1</td><td>Client 2</td></tr>
	 * <tr><td>connectionType</td><td>netty.http</td><td>netty.http</td></tr>
	 * <tr><td>host</td><td>TestConstants.HOST</td><td>TestConstants.HOST</td></tr>
	 * <tr><td>port</td><td>TestConstants.PORT_HTTP</td><td>TestConstants.PORT_HTTP</td></tr>
	 * </table>
	 * </br>
	 * Expectation:	Both clients are detached.
	 */
	@Test
	public void t01_attachDetach() throws Exception {
		client1 = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		assertEquals("Client1 is detached", false, client1.isAttached());
		client2 = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		assertEquals("Client2 is detached", false, client2.isAttached());

		client1.attach();
		assertEquals("Client1 is attached", true, client1.isAttached());
		client2.attach();
		assertEquals("Client2 is attached", true, client2.isAttached());
		
		client1.detach();
		assertEquals("Client1 is detached", false, client1.isAttached());
		client2.detach();
		assertEquals("Client2 is detached", false, client2.isAttached());
	}


	/**
	 * Description: Attach and detach two clients with:<br>
	 * <table>
	 * <tr><td></td><td>Client 1</td><td>Client 2</td></tr>
	 * <tr><td>connectionType</td><td>netty.tcp</td><td>netty.tcp</td></tr>
	 * <tr><td>host</td><td>TestConstants.HOST</td><td>TestConstants.HOST</td></tr>
	 * <tr><td>port</td><td>TestConstants.PORT_TCP</td><td>TestConstants.PORT_TCP</td></tr>
	 * </table>
	 * </br>
	 * Expectation:	Both clients are detached.
	 */
	@Test
	public void t02_attachDetach() throws Exception {
		client1 = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		assertEquals("Client1 is detached", false, client1.isAttached());
		client2 = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		assertEquals("Client2 is detached", false, client2.isAttached());

		client1.attach();
		assertEquals("Client1 is attached", true, client1.isAttached());
		client2.attach();
		assertEquals("Client2 is attached", true, client2.isAttached());
		
		client1.detach();
		assertEquals("Client1 is detached", false, client1.isAttached());
		client2.detach();
		assertEquals("Client2 is detached", false, client2.isAttached());
	}

	/**
	 * Description: Attach and detach two clients with:<br>
	 * <table>
	 * <tr><td></td><td>Client 1</td><td>Client 2</td></tr>
	 * <tr><td>connectionType</td><td>netty.http</td><td>netty.tcp</td></tr>
	 * <tr><td>host</td><td>TestConstants.HOST</td><td>TestConstants.HOST</td></tr>
	 * <tr><td>port</td><td>TestConstants.PORT_HTTP</td><td>TestConstants.PORT_TCP</td></tr>
	 * </table>
	 * </br>
	 * Expectation:	Both clients are detached.
	 */
	@Test
	public void t03_attachDetach() throws Exception {
		client1 = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		assertEquals("Client1 is detached", false, client1.isAttached());
		client2 = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		assertEquals("Client2 is detached", false, client2.isAttached());

		client1.attach();
		assertEquals("Client1 is attached", true, client1.isAttached());
		client2.attach();
		assertEquals("Client2 is attached", true, client2.isAttached());
		
		client1.detach();
		assertEquals("Client1 is detached", false, client1.isAttached());
		client2.detach();
		assertEquals("Client2 is detached", false, client2.isAttached());
	}

	/**
	 * Description: Attach and detach two clients 500 times with:<br>
	 * <table>
	 * <tr><td></td><td>Client 1</td><td>Client 2</td></tr>
	 * <tr><td>connectionType</td><td>netty.http</td><td>netty.http</td></tr>
	 * <tr><td>host</td><td>TestConstants.HOST</td><td>TestConstants.HOST</td></tr>
	 * <tr><td>port</td><td>TestConstants.PORT_HTTP</td><td>TestConstants.PORT_HTTP</td></tr>
	 * </table>
	 * </br>
	 * Expectation:	Both clients are detached.
	 */	
	@Test
	public void t05_attachDetach() throws Exception {
		client1 = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		assertEquals("Client1 is detached", false, client1.isAttached());
		client2 = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		assertEquals("Client2 is detached", false, client2.isAttached());

		int nr = 500;
		for (int i= 0; i < nr; i++) {
			if (((i+1) % 100) == 0)
				testLogger.info("Attach/detach. " + (i+1) + "...");
			client1.attach();
			assertEquals("Client1 is attached", true, client1.isAttached());
			client2.attach();
			assertEquals("Client2 is attached", true, client2.isAttached());
			
			client1.detach();
			assertEquals("Client1 is detached", false, client1.isAttached());
			client2.detach();
			assertEquals("Client2 is detached", false, client2.isAttached());
		}
	}

	/**
	 * Description: Attach and detach two clients 500 times with:<br>
	 * <table>
	 * <tr><td></td><td>Client 1</td><td>Client 2</td></tr>
	 * <tr><td>connectionType</td><td>netty.tcp</td><td>netty.tcp</td></tr>
	 * <tr><td>host</td><td>TestConstants.HOST</td><td>TestConstants.HOST</td></tr>
	 * <tr><td>port</td><td>TestConstants.PORT_TCP</td><td>TestConstants.PORT_TCP</td></tr>
	 * </table>
	 * </br>
	 * Expectation:	Both clients are detached.
	 */	
	@Test
	public void t06_attachDetach() throws Exception {
		client1 = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		assertEquals("Client1 is detached", false, client1.isAttached());
		client2 = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		assertEquals("Client2 is detached", false, client2.isAttached());

		int nr = 500;
		for (int i= 0; i < nr; i++) {
			if (((i+1) % 100) == 0)
				testLogger.info("Attach/detach. " + (i+1) + "...");
			client1.attach();
			assertEquals("Client1 is attached", true, client1.isAttached());
			client2.attach();
			assertEquals("Client2 is attached", true, client2.isAttached());
			
			client1.detach();
			assertEquals("Client1 is detached", false, client1.isAttached());
			client2.detach();
			assertEquals("Client2 is detached", false, client2.isAttached());
		}

	}
}
