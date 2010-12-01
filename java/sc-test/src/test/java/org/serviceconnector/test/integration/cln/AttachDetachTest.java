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

import static org.junit.Assert.*;

import java.security.InvalidParameterException;

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

	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(AttachDetachTest.class);

	private static ProcessesController ctrl;
	private ProcessCtx scCtx;
	private SCClient client;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		ctrl = null;
	}

	@Before
	public void beforeOneTest() throws Exception {
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			client.detach();
		} catch (Exception e) {}
		try {
			ctrl.stopSC(scCtx);
		} catch (Exception e) {}
		client = null;
		scCtx = null;
	}

	/**
	 * Description: Attach and detach one time with default host and http-port.<br>
	 * Expectation:	Client is detached.
	 */
	@Test
	public void t01_attachDetach() throws Exception {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		
		assertEquals(false, client.isAttached());
		client.attach();
		assertEquals(true, client.isAttached());
		client.detach();
		assertEquals(false, client.isAttached());
	}

	
	/**
	 * Description: Attach two time the same client and detach one time with default host and http-port.<br>
	 * Expectation:	Throws exception on the second attach and detached.
	 */
	@Test
	public void t02_attachDetach() throws Exception {
		Exception ex = null;
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);

		client.attach();
		try {
			client.attach();
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, client.isAttached());
		client.detach();
		assertEquals(false, client.isAttached());
	}

	/**
	 * Description: Attach and detach 10 times with sleep time 1sec between attach and detach.<br>
	 * Expectation:	Client is detached.
	 */
	@Test
	public void t03_attachDetach() throws Exception {
		int cycle = 10;
		int sleep = 1000;
		int i = 0;
		
		try {
			client = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
			
			for (i = 0; i < cycle; i++) {
				client.attach();
				assertEquals(true, client.isAttached());
				if (sleep > 0) 
					Thread.sleep(sleep);
				client.detach();
				assertEquals(false, client.isAttached());
				if (((i+1) % 100) == 0)
					testLogger.info("Executing cycle nr. " + (i+1));
			}
		} catch (Exception ex){
			testLogger.info("Error on cycle nr. " + (i+1) + "...");
			assertFalse("Clients Count:"+i+"  Exception, error msg:"+ex.getMessage(), true);
		}
	}

	/**
	 * Description: Attach first and then detach 100 times.<br>
	 * Expectation:	Client is detached.
	 */
	@Test
	public void t04_attachDetach() throws Exception {
		int cycle = 100;
		int sleep = 0;
		int i = 0;
		
		try {
			client = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
			
			for (i = 0; i < cycle; i++) {
				client.attach();
				assertEquals(true, client.isAttached());
				if (sleep > 0) 
					Thread.sleep(sleep);
				client.detach();
				assertEquals(false, client.isAttached());
				if (((i+1) % 100) == 0)
					testLogger.info("Executing cycle nr. " + (i+1));
			}
		} catch (Exception ex){
			testLogger.info("Error on cycle nr. " + (i+1) + "...");
			assertFalse("Clients Count:"+i+"  Exception, error msg:"+ex.getMessage(), true);
		}
	}

	/**
	 * Description: Attach and detach 5000 times.<br>
	 * Expectation:	Client is detached.
	 */
	@Test
	public void t05_attachDetach() throws Exception  {
		int cycle = 500;
		int sleep = 2;
		int i = 0;
		
		try {
			client = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
			
			for (i = 0; i < cycle; i++) {
				client.attach();
				assertEquals(true, client.isAttached());
				if (sleep > 0) 
					Thread.sleep(sleep);
				client.detach();
				assertEquals(false, client.isAttached());
				if (((i+1) % 100) == 0)
					testLogger.info("Executing cycle nr. " + (i+1));
			}
		} catch (Exception ex){
			testLogger.info("Error on cycle nr. " + (i+1) + "...");
			assertFalse("Clients Count:"+i+"  Exception, error msg:"+ex.getMessage(), true);
		}
	}

	/**
	 * Description: Attach first and then detach all 100 clients.<br>
	 * Expectation:	All clients are detached.
	 */
	@Test
	public void t06_attachDetach() throws Exception {
		int i = 0;
		int clientsCount = 100;
		SCClient[] clients = new SCClient[clientsCount];
		
		try {
			for (; i < clientsCount; i++) {
				if (((i+1) % 100) == 0) testLogger.info("Attaching client nr. " + (i+1) );
				clients[i] = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
				clients[i].attach();
			}
		} catch (InvalidParameterException ex) {
			testLogger.info("Error on attach client nr. " + (i+1) + "...");
			assertFalse("Attach, clientsCount:"+i+"  InvalidParameterException, error msg:"+ex.getMessage(), true);
		} catch (Exception ex){
			testLogger.info("Error on attach client nr. " + (i+1) + "...");
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
			testLogger.info("Error on detach client nr. " + (i+1) + "...");
			assertFalse("Detach, clientsCount:"+i+"  Exception, error msg:"+ex.getMessage(), true);
		}
	}
	
	/**
	 * Description: Attach first and then detach all 500 clients.<br>
	 * Expectation:	All client are detached.
	 */
	@Test
	public void t07_attachDetach() throws Exception {
		int i = 0;
		int clientsCount = 500;
		SCClient[] clients = new SCClient[clientsCount];
		
		try {
			for (; i < clientsCount; i++) {
				if (((i+1) % 100) == 0) testLogger.info("Attaching client nr. " + (i+1) );
				clients[i] = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
				clients[i].attach();
			}
		} catch (InvalidParameterException ex) {
			testLogger.info("Error on attach client nr. " + (i+1) + "...");
			assertFalse("Attach, clientsCount:"+i+"  InvalidParameterException, error msg:"+ex.getMessage(), true);
		} catch (Exception ex){
			testLogger.info("Error on attach client nr. " + (i+1) + "...");
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
			testLogger.info("Error on detach client nr. " + (i+1) + "...");
			assertFalse("Detach, clientsCount:"+i+"  Exception, error msg:"+ex.getMessage(), true);
		}
	}

	/**
	 * Description: Attach first and then detach all 1'000 clients.<br>
	 * Expectation:	All client are detached.
	 */
	@Test
	public void t08_attachDetach() throws Exception {
		int i = 0;
		int clientsCount = 1000;
		SCClient[] clients = new SCClient[clientsCount];
		
		try {
			for (; i < clientsCount; i++) {
				if (((i+1) % 100) == 0) testLogger.info("Attaching client nr. " + (i+1) );
				clients[i] = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
				clients[i].attach();
			}
		} catch (InvalidParameterException ex) {
			testLogger.info("Error on attach client nr. " + (i+1) + "...");
			assertFalse("Attach, clientsCount:"+i+"  InvalidParameterException, error msg:"+ex.getMessage(), true);
		} catch (Exception ex){
			testLogger.info("Error on attach client nr. " + (i+1) + "...");
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
			testLogger.info("Error on detach client nr. " + (i+1) + "...");
			assertFalse("Detach, clientsCount:"+i+"  Exception, error msg:"+ex.getMessage(), true);
		}
	}


	/**
	 * Description: Detach the client without to attach.<br>
	 * Expectation:	Client is detached.
	 */
	@Test
	public void t10_detach() throws Exception {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);

		assertEquals(false, client.isAttached());
		client.detach();
		assertEquals(false, client.isAttached());
	}

	
}
