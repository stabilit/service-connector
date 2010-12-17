package org.serviceconnector.test.integration.api.cln;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.test.integration.APIIntegrationSuperClientTest;

public class APIAfterSCAbortClientTest extends APIIntegrationSuperClientTest  {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(APIAfterSCAbortClientTest.class);

	private SCMgmtClient client;
	
	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
	}

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			client.detach();
		} catch (Exception e) {}
		client = null;
		try {
			ctrl.stopSC(scCtx);
		} catch (Exception e) {}
		scCtx = null;
		super.afterOneTest();
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		ctrl = null;
	}


	/**
	 * Description: attach after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t101_attach() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		ctrl.stopSC(scCtx);
		client.attach();
	}

	/**
	 * Description: detach after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t102_detach() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Client is attached", true, client.isAttached());
		ctrl.stopSC(scCtx);
		client.detach();
	}

	/**
	 * Description: enable service after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t103_enableService() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Client is attached", true, client.isAttached());
		ctrl.stopSC(scCtx);
		client.enableService(TestConstants.sesServiceName1);
	}

	/**
	 * Description: disable service after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t104_disableService() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Client is attached", true, client.isAttached());
		ctrl.stopSC(scCtx);
		client.disableService(TestConstants.sesServiceName1);
	}

	/**
	 * Description: getWorkload after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t105_getWorkload() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Client is attached", true, client.isAttached());
		ctrl.stopSC(scCtx);
		client.getWorkload(TestConstants.sesServiceName1);
	}

	/**
	 * Description: attach after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t201_attach() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		ctrl.stopSC(scCtx);
		client.attach();
	}

	/**
	 * Description: detach after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t202_detach() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		Assert.assertEquals("Client is attached", true, client.isAttached());
		ctrl.stopSC(scCtx);
		client.detach();
	}

	/**
	 * Description: enable service after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t203_enableService() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		Assert.assertEquals("Client is attached", true, client.isAttached());
		ctrl.stopSC(scCtx);
		client.enableService(TestConstants.sesServiceName1);
	}

	/**
	 * Description: disable service after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t204_disableService() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		Assert.assertEquals("Client is attached", true, client.isAttached());
		ctrl.stopSC(scCtx);
		client.disableService(TestConstants.sesServiceName1);
	}

	/**
	 * Description: getWorkload after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t205_getWorkload() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		Assert.assertEquals("Client is attached", true, client.isAttached());
		ctrl.stopSC(scCtx);
		client.getWorkload(TestConstants.sesServiceName1);
	}

}
