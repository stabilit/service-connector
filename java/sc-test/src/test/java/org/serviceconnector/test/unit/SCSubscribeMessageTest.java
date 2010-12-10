package org.serviceconnector.test.unit;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.Constants;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.cmd.SCMPValidatorException;

public class SCSubscribeMessageTest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCSubscribeMessageTest.class);
	
	private SCSubscribeMessage message;

	@Before
	public void beforeOneTest() {
		message = new SCSubscribeMessage();
	}
	
	@After
	public void afterOneTest(){
		message = null;
	}
	
	/**
	 * Description:	Check default values <br>
	 * Expectation:	passed, all values are default
	 */
	@Test
	public void t01_constructor() {
		Assert.assertEquals("messageInfo is not null",null, message.getMessageInfo());
		Assert.assertEquals("data is not null",null, message.getData());
		Assert.assertEquals("sessionId is not null",null, message.getSessionId());
		Assert.assertEquals("mask is not null",null, message.getMask());
		Assert.assertEquals("compressed flag is not default",Constants.DEFAULT_COMPRESSION_FLAG, message.isCompressed());
	}

	/**
	 * Description:	Set mask = null<br>
	 * Expectation:	passes
	 */
	@Test 
	public void t10_mask() throws Exception {
		message.setMask(null);
		Assert.assertEquals("mask is not null", null, message.getMask());
	}

	/**
	 * Description:	Set mask = ""<br>
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t11_mask() throws Exception {
		message.setMask("");
	}

	/**
	 * Description:	Set mask = " "<br>
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t12_mask() throws Exception {
		message.setMask(" ");
	}

	/**
	 * Description:	Set mask = string[256] <br>
	 * Expectation:	passes 
	 */
	@Test
	public void t13_mask() throws Exception {
		message.setMask(TestConstants.stringLength256);
	}

	/**
	 * Description:	Set mask = string[257] <br>
	 * Expectation:	throws SCMPValidatorException 
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t14_mask() throws Exception {
		message.setMask(TestConstants.stringLength257);
		Assert.assertEquals("mask is different", TestConstants.stringLength256, message.getMask());
	}

	/**
	 * Description:	Set mask = abc%xy <br>
	 * Expectation:	throws SCMPValidatorException 
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t15_mask() throws Exception {
		message.setMask("abc%xy");
	}

	/**
	 * Description:	Set noDataInteval = -1 <br>
	 * Expectation:	throws SCMPValidatorException 
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t15_noDataInteval() throws Exception {
		message.setNoDataIntervalInSeconds(-1);
	}

	/**
	 * Description:	Set noDataInteval = 0 <br>
	 * Expectation:	throws SCMPValidatorException 
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t16_noDataInteval() throws Exception {
		message.setNoDataIntervalInSeconds(0);
	}
	
	/**
	 * Description:	Set noDataInteval = 1 <br>
	 * Expectation:	passes
	 */
	@Test 
	public void t17_noDataInteval() throws Exception {
		message.setNoDataIntervalInSeconds(1);
	}
	

	/**
	 * Description:	Set noDataInteval = 67000 <br>
	 * Expectation:	throws SCMPValidatorException 
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t18_noDataInteval() throws Exception {
		message.setNoDataIntervalInSeconds(67000);
	}
}
