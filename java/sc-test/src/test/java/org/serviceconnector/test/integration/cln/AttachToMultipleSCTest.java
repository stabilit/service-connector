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
import static org.junit.Assert.assertFalse;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.net.ConnectionType;


public class AttachToMultipleSCTest {
	
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(AttachToMultipleSCTest.class);
	
	private int threadCount = 0;
	private SCClient client1;
	private SCClient client2;
	private static Process scProcess0;
	private static Process scProcess1;

	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		// TODO new SCSessionServer(); Notwendig?
		//new SCSessionServer();
		ctrl = new ProcessesController();
		try {
			scProcess0 = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
			scProcess1 = ctrl.startSC(TestConstants.log4jSCcascadedProperties, TestConstants.SCcascadedProperties);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopSC(scProcess0, TestConstants.log4jSCProperties);
		ctrl.stopSC(scProcess1, TestConstants.log4jSCcascadedProperties);
		ctrl = null;
		scProcess0 = null;
		scProcess1 = null;
	}

	@Before
	public void setUp() throws Exception {
//		threadCount = Thread.activeCount();
		client1 = null;
		client2 = null;		
	}
	
	@After
	public void tearDown() throws Exception {
		client1 = null;
		client2 = null;
//		assertEquals("number of threads", threadCount, Thread.activeCount());
	}

	/**
	 * @param cicle  
	 * 			to repeat
	 * @param connectionType1
	 * 			connectionType for client 1
	 * @param host1
	 * 			host for client 1
	 * @param port1
	 * 			port for client 1
	 * @param connectionType2
	 * 			connectionType for client 2
	 * @param host2
	 * 			host for client 2
	 * @param port2
	 * 			port for client 2
	 */
	private void testAttachDetach(int cicle,
			ConnectionType connectionType1, String host1, int port1,
			ConnectionType connectionType2, String host2, int port2){
		
		if (connectionType1==null) { client1 = new SCClient(host1, port1); }
		else { client1 = new SCClient(host1, port1, connectionType1); }

		if (connectionType2==null) { client2 = new SCClient(host2, port2); }
		else { client2 = new SCClient(host2, port2, connectionType2); }

		assertEquals(false, client1.isAttached());
		assertEquals(false, client2.isAttached());
		int count = 0;
		
		for (int i = 1; i < (cicle+1); i++) {
			count = i;
			try {
				client1.attach();
			}
			catch (Exception  ex) {
				assertFalse("Cicle:"+count+" attach client 1 on host="+host1+"  port="+port1+"  con.type="+connectionType1+", msg="+ex.getMessage(), true);
				i = cicle;  // to end the loop
			}
			assertEquals(true, client1.isAttached());
			assertEquals(false, client2.isAttached());
			try {
				client2.attach();
			}
			catch (Exception  ex) {
				assertFalse("Cicle:"+count+" attach client 2 on host="+host2+"  port="+port2+"  con.type="+connectionType2+", msg="+ex.getMessage(), true);
				i = cicle;  // to end the loop
			}
			assertEquals(true, client1.isAttached());
			assertEquals(true, client2.isAttached());
			
			try{
				client1.detach();
				client2.detach();
			}
			catch (Exception  ex) {
				assertFalse("Cicle:"+count+" setach client 1+2, msg="+ex.getMessage(), true);
				i = cicle;  // to end the loop
			}
			assertEquals(false, client1.isAttached());
			assertEquals(false, client2.isAttached());
		}
	}


	/**
	 * Description: Attach and detach two clients with:<br>
	 * <table>
	 * <tr><td></td><td>Client 1</td><td>Client 2</td></tr>
	 * <tr><td>connectionType</td><td>netty.http</td><td>netty.http</td></tr>
	 * <tr><td>host</td><td>TestConstants.LOCALHOST</td><td>TestConstants.HOST</td></tr>
	 * <tr><td>port</td><td>TestConstants.PORT_HTTP</td><td>TestConstants.PORT_MIN</td></tr>
	 * </table>
	 * </br>
	 * Expectation:	Both clients are detached.
	 */
	@Test
	public void attachDetach_1() throws Exception {
		this.testAttachDetach(1, ConnectionType.NETTY_HTTP, TestConstants.LOCALHOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP, TestConstants.HOST, TestConstants.PORT_MIN);
	}

	/**
	 * Description: Attach and detach two clients with:<br>
	 * <table>
	 * <tr><td></td><td>Client 1</td><td>Client 2</td></tr>
	 * <tr><td>connectionType</td><td>netty.http</td><td>netty.tcp</td></tr>
	 * <tr><td>host</td><td>TestConstants.LOCALHOST</td><td>TestConstants.HOST</td></tr>
	 * <tr><td>port</td><td>TestConstants.PORT_HTTP</td><td>TestConstants.PORT_MAX</td></tr>
	 * </table>
	 * </br>
	 * Expectation:	Both clients are detached.
	 */
	@Test
	public void attachDetach_2() throws Exception {
		this.testAttachDetach(1, ConnectionType.NETTY_HTTP, TestConstants.LOCALHOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP, TestConstants.HOST, TestConstants.PORT_MAX);
	}

	/**
	 * Description: Attach and detach two clients with:<br>
	 * <table>
	 * <tr><td></td><td>Client 1</td><td>Client 2</td></tr>
	 * <tr><td>connectionType</td><td>netty.tcp</td><td>netty.tcp</td></tr>
	 * <tr><td>host</td><td>TestConstants.LOCALHOST</td><td>TestConstants.HOST</td></tr>
	 * <tr><td>port</td><td>TestConstants.PORT_TCP</td><td>TestConstants.PORT_MAX</td></tr>
	 * </table>
	 * </br>
	 * Expectation:	Both clients are detached.
	 */
	@Test
	public void attachDetach_3() throws Exception {
		this.testAttachDetach(1, ConnectionType.NETTY_TCP, TestConstants.LOCALHOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP, TestConstants.HOST, TestConstants.PORT_MAX);
	}

	/**
	 * Description: Attach and detach two clients with:<br>
	 * <table>
	 * <tr><td></td><td>Client 1</td><td>Client 2</td></tr>
	 * <tr><td>connectionType</td><td>netty.tcp</td><td>netty.tcp</td></tr>
	 * <tr><td>host</td><td>TestConstants.LOCALHOST</td><td>TestConstants.HOST</td></tr>
	 * <tr><td>port</td><td>TestConstants.PORT_TCP</td><td>TestConstants.PORT_MIN</td></tr>
	 * </table>
	 * </br>
	 * Expectation:	Both clients are detached.
	 */
	@Test
	public void attachDetach_4() throws Exception {
		this.testAttachDetach(1, ConnectionType.NETTY_TCP, TestConstants.LOCALHOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP, TestConstants.HOST, TestConstants.PORT_MIN);
	}

	/**
	 * Description: Attach and detach two clients 100 times with:<br>
	 * <table>
	 * <tr><td></td><td>Client 1</td><td>Client 2</td></tr>
	 * <tr><td>connectionType</td><td>netty.http</td><td>netty.http</td></tr>
	 * <tr><td>host</td><td>TestConstants.LOCALHOST</td><td>TestConstants.HOST</td></tr>
	 * <tr><td>port</td><td>TestConstants.PORT_HTTP</td><td>TestConstants.PORT_MIN</td></tr>
	 * </table>
	 * </br>
	 * Expectation:	Both clients are detached.
	 */	
	@Test
	public void attachDetach_5() throws Exception {
		this.testAttachDetach(100, ConnectionType.NETTY_HTTP, TestConstants.LOCALHOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP, TestConstants.HOST, TestConstants.PORT_MIN);
	}

	/**
	 * Description: Attach and detach two clients 100 times with:<br>
	 * <table>
	 * <tr><td></td><td>Client 1</td><td>Client 2</td></tr>
	 * <tr><td>connectionType</td><td>netty.tcp</td><td>netty.tcp</td></tr>
	 * <tr><td>host</td><td>TestConstants.LOCALHOST</td><td>TestConstants.HOST</td></tr>
	 * <tr><td>port</td><td>TestConstants.PORT_TCP</td><td>TestConstants.PORT_MAX</td></tr>
	 * </table>
	 * </br>
	 * Expectation:	Both clients are detached.
	 */	
	@Test
	public void attachDetach_6() throws Exception {
		this.testAttachDetach(100, ConnectionType.NETTY_TCP, TestConstants.LOCALHOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP, TestConstants.HOST, TestConstants.PORT_MAX);
	}
	
	/**
	 * Description: Attach and detach two clients 100 times with:<br>
	 * <table>
	 * <tr><td></td><td>Client 1</td><td>Client 2</td></tr>
	 * <tr><td>connectionType</td><td>netty.tcp</td><td>netty.http</td></tr>
	 * <tr><td>host</td><td>TestConstants.LOCALHOST</td><td>TestConstants.HOST</td></tr>
	 * <tr><td>port</td><td>TestConstants.PORT_TCP</td><td>TestConstants.PORT_MIN</td></tr>
	 * </table>
	 * </br>
	 * Expectation:	Both clients are detached.
	 */	
	@Test
	public void attachDetach_7() throws Exception {
		this.testAttachDetach(100, ConnectionType.NETTY_HTTP, TestConstants.LOCALHOST, TestConstants.PORT_TCP, ConnectionType.NETTY_HTTP, TestConstants.HOST, TestConstants.PORT_MIN);
	}

	}
