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
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.service.SCServiceException;

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
			scProcess = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSC0Properties);
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


	private void testAttachDetach(String host, int port, int cicle, int sleep) throws Exception  {
		int i = 0;
		try {
			for (i = 0; i < cicle; i++) {
				if ((i % 100) == 0)
					testLogger.info("Executing cycle nr. " + i + "...");
				client.attach(host, port);
				assertEquals(true, client.isAttached());
				if (sleep > 0) 
					Thread.sleep(sleep);
				client.detach();
				assertEquals(false, client.isAttached());
			}
		} catch (Exception ex){
			assertFalse("Clients Count:"+i+"  Exception, error msg:"+ex.getMessage(), true);
		}
	}


	@Test
	public void attach_changesState_initiallyNotAttachedThenAttached() throws Exception {
		assertEquals(false, client.isAttached());
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		assertEquals(true, client.isAttached());
		client.detach();
		assertEquals(false, client.isAttached());
	}

	@Test
	public void detach_changesState_fromAttachedToNotAttached() throws Exception {
		assertEquals(false, client.isAttached());
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		assertEquals(true, client.isAttached());
		client.detach();
		assertEquals(false, client.isAttached());
	}

	@Test
	public void attach_twiceSameParams_throwsExceptionAttached() throws Exception {
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
	}

	@Test
	public void attach_twiceDifferentParamsHttpFirst_throwsExceptionAttached() throws Exception {
		Exception ex = null;
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		((SCClient) client).setConnectionType("netty.tcp");
		try {
			client.attach(TestConstants.HOST, TestConstants.PORT_TCP);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, client.isAttached());
		client.detach();
	}

	@Test
	public void attach_twiceDifferentParamsTcpFirst_throwsExceptionAttached() throws Exception {
		Exception ex = null;
		((SCClient) client).setConnectionType("netty.tcp");
		client.attach(TestConstants.HOST, TestConstants.PORT_TCP);
		((SCClient) client).setConnectionType("netty.http");
		try {
			client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
		assertEquals(true, client.isAttached());
		client.detach();
	}

	@Test
	public void detach_withoutAttach_notAttached() throws Exception {
		client.detach();
		assertEquals(false, client.isAttached());
	}

	@Test
	public void detach_validAttachPortHttp_notAttached() throws Exception {
		try {
			client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		} finally {
			client.detach();
			assertEquals(false, client.isAttached());
		}
	}

	@Test
	public void detach_validAttachPortTcp_notAttached() throws Exception {
		((SCClient) client).setConnectionType("netty.tcp");
		try {
			client.attach(TestConstants.HOST, TestConstants.PORT_TCP);
		} finally {
			client.detach();
			assertEquals(false, client.isAttached());
		}
	}

	@Test
	public void detach_afterDoubleAttemptedAttachDetach_throwsExceptionNotAttached() throws Exception {
		Exception ex = null;
		client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);
		assertEquals(true, client.isAttached());
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

	// @Test
	public void attachDetach_cycle10Times_notAttached() throws Exception {
		this.testAttachDetach(TestConstants.HOST, TestConstants.PORT_HTTP, 10, 1000);
	}

	@Test
	public void attachDetach_cycle100Times_notAttached() throws Exception {
		this.testAttachDetach(TestConstants.HOST, TestConstants.PORT_HTTP, 100, 0);
	}

	
	@Test
	public void attachDetach_cycle5000Times_notAttached() throws Exception  {
		this.testAttachDetach(TestConstants.HOST, TestConstants.PORT_HTTP, 5000, 0);
	}

	// TODO 1000 is too much. Getting very slow exactly after 500. 501,502...
	@Test
	public void attach_500ClientsAttachedBeforeDetach_allAttached() throws Exception {
		int clientsCount = 500;
		SCClient[] clients = new SCClient[clientsCount];
		int i = 0;
		try {
			for (; i < clientsCount; i++) {
				if ((i % 100) == 0) testLogger.info("Attaching client nr. " + i + "...");
				clients[i] = new SCClient();
				((SCClient) clients[i]).setConnectionType("netty.http");
				clients[i].attach(TestConstants.HOST, TestConstants.PORT_HTTP);
			}
		} catch (InvalidParameterException ex) {
			assertFalse("Attach, clientsCount:"+i+"  InvalidParameterException, error msg:"+ex.getMessage(), true);
		} catch (Exception ex){
			assertFalse("Attach, clientsCount:"+i+"  Exception, error msg:"+ex.getMessage(), true);
		}
		try {
			i = 0;
			for (; i < clientsCount; i++) {
				assertEquals(true, clients[i].isAttached());
			}
			i = 0;
			for (; i < clientsCount; i++) {
				if ((i % 100) == 0) testLogger.info("Detaching client nr. " + i + "...");
				clients[i].detach();
			}
			i = 0;
			for (; i < clientsCount; i++) {
				assertEquals(false, clients[i].isAttached());
			}
		} catch (Exception ex){
			assertFalse("Detach, clientsCount:"+i+"  Exception, error msg:"+ex.getMessage(), true);
		}
			
	}
}
