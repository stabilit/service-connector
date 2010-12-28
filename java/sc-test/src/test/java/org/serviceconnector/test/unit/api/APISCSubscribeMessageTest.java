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
package org.serviceconnector.test.unit.api;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.Constants;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.test.unit.SuperUnitTest;

public class APISCSubscribeMessageTest extends SuperUnitTest{

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(APISCSubscribeMessageTest.class);
	
	private SCSubscribeMessage message;

	@Before
	public void beforeOneTest() throws Exception{
		super.beforeOneTest();
		message = new SCSubscribeMessage();
	}
	
	@After
	public void afterOneTest(){
		message = null;
		super.afterOneTest();
	}
	
	/**
	 * Description:	Check default values <br>
	 * Expectation:	passed, all values are default
	 */
	@Test
	public void t01_constructor() {
		Assert.assertEquals("mask is not null",null, message.getMask());
		Assert.assertEquals("noDataInterval is default",Constants.DEFAULT_NO_DATA_INTERVAL_SECONDS, message.getNoDataIntervalInSeconds());
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
		Assert.assertEquals("mask is different", TestConstants.stringLength257, message.getMask());
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
