package org.serviceconnector.test.integration.cln;

import static org.junit.Assert.assertEquals;

import java.security.InvalidParameterException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.api.cln.IFileService;
import org.serviceconnector.api.cln.IPublishService;
import org.serviceconnector.api.cln.ISCClient;
import org.serviceconnector.api.cln.ISessionService;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.ctrl.util.ProcessesController;


public class NewServicesTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(NewServicesTest.class);

	private ISCClient client;
	private static Process scProcess;

	private static ProcessesController ctrl;

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
	public void setUp() throws Exception {
		client = new SCClient();
		client.attach(TestConstants.HOST, TestConstants.PORT8080);
	}

	@After
	public void tearDown() throws Exception {
		client.detach();
		client = null;
	}
	
	@Test(expected = InvalidParameterException.class)
	public void newSessionService_NullParam_throwsInvalidParamException() throws Exception {
		client.newSessionService(null);
	}

	@Test
	public void newSessionService_emptyStringParam_returnsISessionService() throws Exception {
		assertEquals(true, client.newSessionService("") instanceof ISessionService);
	}

	@Test
	public void newSessionService_whiteSpaceStringParam_returnsISessionService() throws Exception {
		assertEquals(true, client.newSessionService(" ") instanceof ISessionService);
	}

	@Test
	public void newSessionService_ArbitraryStringParam_returnsISessionService() throws Exception {
		assertEquals(
				true,
				client.newSessionService("The quick brown fox jumps over a lazy dog.") instanceof ISessionService);
	}

	@Test
	public void newSessionService_validServiceName_returnsISessionService() throws Exception {
		assertEquals(true, client.newSessionService(TestConstants.serviceName) instanceof ISessionService);
	}
	
	@Test
	public void newSessionService_validDisabledServiceName_returnsISessionService() throws Exception {
		assertEquals(true, client.newSessionService(TestConstants.serviceNameSessionDisabled) instanceof ISessionService);
	}
	
	@Test
	public void newSessionService_twice_returnsISessionService() throws Exception {
		assertEquals(true, client.newSessionService(TestConstants.serviceName) instanceof ISessionService);
		assertEquals(true, client.newSessionService(TestConstants.serviceName) instanceof ISessionService);
	}
	
	@Test
	public void newSessionService_twiceDifferentServiceName_returnsISessionService() throws Exception {
		assertEquals(true, client.newSessionService(TestConstants.serviceName) instanceof ISessionService);
		assertEquals(true, client.newSessionService(TestConstants.serviceNameAlt) instanceof ISessionService);
	}
	
	@Test
	public void newSessionService_1000TimesDifferentServiceName_returnsISessionService() throws Exception {
		for (int i = 0; i < 500; i++) {
			assertEquals(true, client.newSessionService(TestConstants.serviceName) instanceof ISessionService);
			assertEquals(true, client.newSessionService(TestConstants.serviceNameAlt) instanceof ISessionService);
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void newPublishService_NullParam_throwsInvalidParamException() throws Exception {
		client.newPublishService(null);
	}

	@Test
	public void newPublishService_emptyStringParam_returnsIPublishService() throws Exception {
		assertEquals(true, client.newPublishService("") instanceof IPublishService);
	}

	@Test
	public void newPublishService_whiteSpaceStringParam_returnsIPublishService() throws Exception {
		assertEquals(true, client.newPublishService(" ") instanceof IPublishService);
	}

	@Test
	public void newPublishService_ArbitraryStringParam_returnsIPublishService() throws Exception {
		assertEquals(
				true,
				client.newPublishService("The quick brown fox jumps over a lazy dog.") instanceof IPublishService);
	}

	@Test
	public void newPublishService_serviceNameInSCPropertiesNotPublish_returnsIPublishService() throws Exception {
		assertEquals(true, client.newPublishService(TestConstants.serviceName) instanceof IPublishService);
	}
	
	@Test
	public void newPublishService_serviceNameInSCPropertiesPublish_returnsIPublishService() throws Exception {
		assertEquals(true, client.newPublishService(TestConstants.serviceName) instanceof IPublishService);
	}
	
	@Test
	public void newPublishService_validDisabledServiceName_returnsIPublishService() throws Exception {
		assertEquals(true, client.newPublishService(TestConstants.serviceNameSessionDisabled) instanceof IPublishService);
	}
	
	@Test
	public void newPublishService_twice_returnsIPublishService() throws Exception {
		assertEquals(true, client.newPublishService(TestConstants.serviceName) instanceof IPublishService);
		assertEquals(true, client.newPublishService(TestConstants.serviceName) instanceof IPublishService);
	}
	
	@Test
	public void newPublishService_twiceDifferentServiceName_returnsIPublishService() throws Exception {
		assertEquals(true, client.newPublishService(TestConstants.serviceName) instanceof IPublishService);
		assertEquals(true, client.newPublishService(TestConstants.serviceNameAlt) instanceof IPublishService);
	}
	
	@Test
	public void newPublishService_1000TimesDifferentServiceName_returnsIPublishService() throws Exception {
		for (int i = 0; i < 500; i++) {
			assertEquals(true, client.newPublishService(TestConstants.serviceName) instanceof IPublishService);
			assertEquals(true, client.newPublishService(TestConstants.serviceNameAlt) instanceof IPublishService);
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void newFileService_NullParam_throwsInvalidParamException() throws Exception {
		client.newFileService(null);
	}

	@Test
	public void newFileService_emptyStringParam_returnsIFileService() throws Exception {
		assertEquals(true, client.newFileService("") instanceof IFileService);
	}

	@Test
	public void newFileService_whiteSpaceStringParam_returnsIFileService() throws Exception {
		assertEquals(true, client.newFileService(" ") instanceof IFileService);
	}

	@Test
	public void newFileService_ArbitraryStringParam_returnsIFileService() throws Exception {
		assertEquals(
				true,
				client.newFileService("The quick brown fox jumps over a lazy dog.") instanceof IFileService);
	}

	@Test
	public void newFileService_validServiceName_returnsIFileService() throws Exception {
		assertEquals(true, client.newFileService(TestConstants.serviceName) instanceof IFileService);
	}
	
	@Test
	public void newFileService_validDisabledServiceName_returnsIFileService() throws Exception {
		assertEquals(true, client.newFileService(TestConstants.serviceNameSessionDisabled) instanceof IFileService);
	}
	
	@Test
	public void newFileService_twice_returnsIFileService() throws Exception {
		assertEquals(true, client.newFileService(TestConstants.serviceName) instanceof IFileService);
		assertEquals(true, client.newFileService(TestConstants.serviceName) instanceof IFileService);
	}
	
	@Test
	public void newFileService_twiceDifferentServiceName_returnsIFileService() throws Exception {
		assertEquals(true, client.newFileService(TestConstants.serviceName) instanceof IFileService);
		assertEquals(true, client.newFileService(TestConstants.serviceNameAlt) instanceof IFileService);
	}
	
	@Test
	public void newFileService_1000TimesDifferentServiceName_returnsIFileService() throws Exception {
		for (int i = 0; i < 500; i++) {
			assertEquals(true, client.newFileService(TestConstants.serviceName) instanceof IFileService);
			assertEquals(true, client.newFileService(TestConstants.serviceNameAlt) instanceof IFileService);
		}
	}
}
