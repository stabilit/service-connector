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

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.api.SCPublishMessage;
import org.serviceconnector.test.unit.SuperUnitTest;

public class APISCPublishMessageTest extends SuperUnitTest {
	
	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(APISCPublishMessageTest.class);
	
	private SCPublishMessage message;

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		message = new SCPublishMessage();
	}
	
	@After
	public void afterOneTest(){
		message = null;
		super.afterOneTest();
	}
	
	/**
	 * Description: Check default values <br>
	 * Expectation: passed, all values are default
	 */
	@Test
	public void t01_constructor() {
		Assert.assertEquals("mask is not null",null, message.getMask());
	}


}
