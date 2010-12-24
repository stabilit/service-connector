package org.serviceconnector.test.integration.api.cln;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.test.integration.api.APIIntegrationSuperClientTest;

public class APIAfterSCAbortClientTest extends APIIntegrationSuperClientTest  {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(APIAfterSCAbortClientTest.class);

	private SCMgmtClient client;
	
	/**
	 * Description: attach after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t101_attach() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		
		ctrl.stopSC(scCtx);
		
		client.attach();
	}

	/**
	 * Description: detach after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t102_detach() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		
		client.detach();
	}

	/**
	 * Description: enable service after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t103_enableService() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		
		client.enableService(TestConstants.sesServiceName1);
	}

	/**
	 * Description: disable service after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t104_disableService() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		
		client.disableService(TestConstants.sesServiceName1);
	}

	/**
	 * Description: getWorkload after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t105_getWorkload() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		
		client.getWorkload(TestConstants.sesServiceName1);
	}

	/**
	 * Description: attach after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t201_attach() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_HTTP, ConnectionType.NETTY_HTTP);
		
		ctrl.stopSC(scCtx);
		
		client.attach();
	}

	/**
	 * Description: detach after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test (expected = SCServiceException.class)
	public void t202_detach() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		
		client.detach();
	}

	/**
	 * Description: enable service after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t203_enableService() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		
		client.enableService(TestConstants.sesServiceName1);
	}

	/**
	 * Description: disable service after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t204_disableService() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		
		client.disableService(TestConstants.sesServiceName1);
	}

	/**
	 * Description: getWorkload after SC was aborted<br> 
	 * Expectation:	throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t205_getWorkload() throws Exception {
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_HTTP, ConnectionType.NETTY_HTTP);
		client.attach();
		Assert.assertEquals("Client is not attached", true, client.isAttached());
		
		ctrl.stopSC(scCtx);
		
		client.getWorkload(TestConstants.sesServiceName1);
	}

}
