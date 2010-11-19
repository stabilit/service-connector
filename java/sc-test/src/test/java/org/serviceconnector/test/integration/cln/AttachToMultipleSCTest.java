package org.serviceconnector.test.integration.cln;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnetor.TestConstants;


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
		new SCSessionServer();
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
		client1 = new SCClient();
		client2 = new SCClient();		
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
			String connectionType1, String host1, int port1,
			String connectionType2, String host2, int port2){
		
		if (connectionType1!=null)
			((SCClient) client1).setConnectionType(connectionType1);
		if (connectionType2!=null)
			((SCClient) client2).setConnectionType(connectionType2);
		
		assertEquals(false, client1.isAttached());
		assertEquals(false, client2.isAttached());
		int count = 0;
		
		for (int i = 1; i < (cicle+1); i++) {
			count = i;
			try {
				client1.attach(host1, port1);
			}
			catch (Exception  ex) {
				assertFalse("Cicle:"+count+" attach client 1 on host="+host1+"  port="+port1+"  con.type="+connectionType1+", msg="+ex.getMessage(), true);
				i = cicle;  // to end the loop
			}
			assertEquals(true, client1.isAttached());
			assertEquals(false, client2.isAttached());
			try {
				client2.attach(host2, port2);
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
		this.testAttachDetach(1, "netty.http", TestConstants.LOCALHOST, TestConstants.PORT_HTTP, "netty.http", TestConstants.HOST, TestConstants.PORT_MIN);
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
		this.testAttachDetach(1, "netty.http", TestConstants.LOCALHOST, TestConstants.PORT_HTTP, "netty.tcp", TestConstants.HOST, TestConstants.PORT_MAX);
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
		this.testAttachDetach(1, "netty.tcp", TestConstants.LOCALHOST, TestConstants.PORT_TCP, "netty.tcp", TestConstants.HOST, TestConstants.PORT_MAX);
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
		this.testAttachDetach(1, "netty.tcp", TestConstants.LOCALHOST, TestConstants.PORT_TCP, "netty.tcp", TestConstants.HOST, TestConstants.PORT_MIN);
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
		this.testAttachDetach(100, "netty.http", TestConstants.LOCALHOST, TestConstants.PORT_HTTP, "netty.http", TestConstants.HOST, TestConstants.PORT_MIN);
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
		this.testAttachDetach(100, "netty.tcp", TestConstants.LOCALHOST, TestConstants.PORT_TCP, "netty.tcp", TestConstants.HOST, TestConstants.PORT_MAX);
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
		this.testAttachDetach(100, "netty.tcp", TestConstants.LOCALHOST, TestConstants.PORT_TCP, "netty.http", TestConstants.HOST, TestConstants.PORT_MIN);
	}

	/**
	 * Description: Attach and detach two clients 50 times and change the connection type periodically.<br>
	 * Expectation:	Both clients are detached.
	 */	
	@Test
	public void attachDetach_8() throws Exception {
		for (int i = 0; i < 50; i++) {
			((SCClient) client1).setConnectionType("netty.tcp");
			((SCClient) client2).setConnectionType("netty.http");
			client1.attach(TestConstants.LOCALHOST, TestConstants.PORT_TCP);
			client2.attach(TestConstants.HOST, TestConstants.PORT_MIN);
			assertEquals(true, client1.isAttached());
			assertEquals(true, client2.isAttached());
			client1.detach();
			client2.detach();
			assertEquals(false, client1.isAttached());
			assertEquals(false, client2.isAttached());
			
			((SCClient) client1).setConnectionType("netty.http");
			((SCClient) client2).setConnectionType("netty.tcp");
			client1.attach(TestConstants.LOCALHOST, TestConstants.PORT_HTTP);
			client2.attach(TestConstants.HOST, TestConstants.PORT_MAX);
			assertEquals(true, client1.isAttached());
			assertEquals(true, client2.isAttached());
			client1.detach();
			client2.detach();
			assertEquals(false, client1.isAttached());
			assertEquals(false, client2.isAttached());
		}
	}

	/**
	 * Description: Attach and detach two clients 50 times and change the port periodically.<br>
	 * Expectation:	Both clients are detached.
	 */	
	@Test
	public void attachDetach_9() throws Exception {
		((SCClient) client1).setConnectionType("netty.http");
		((SCClient) client2).setConnectionType("netty.http");
		for (int i = 0; i < 50; i++) {
			client1.attach(TestConstants.LOCALHOST, TestConstants.PORT_HTTP);
			client2.attach(TestConstants.HOST, TestConstants.PORT_MIN);
			assertEquals(true, client1.isAttached());
			assertEquals(true, client2.isAttached());
			client1.detach();
			client2.detach();
			assertEquals(false, client1.isAttached());
			assertEquals(false, client2.isAttached());
			client1.attach(TestConstants.LOCALHOST, TestConstants.PORT_MIN);
			client2.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
			assertEquals(true, client1.isAttached());
			assertEquals(true, client2.isAttached());
			client1.detach();
			client2.detach();
			assertEquals(false, client1.isAttached());
			assertEquals(false, client2.isAttached());
		}
	}
}
