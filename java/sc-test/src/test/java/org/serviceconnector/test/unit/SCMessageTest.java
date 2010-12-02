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
package org.serviceconnector.test.unit;

import static org.junit.Assert.assertEquals;

import java.security.InvalidParameterException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.log.Loggers;

/**
 * @author FJurnecka
 * 
 */
public class SCMessageTest {

	/** The Constant testLogger. */
	protected static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCMessageTest.class);
	
	private SCMessage message;

	@Before
	public void beforeOneTest() {
		message = new SCMessage();
	}
	
	@After
	public void afterOneTest(){
		message = null;
	}
	
	/**
	 * Description:	Check empty message<br>
	 * Expectation:	all values are empty
	 */
	@Test
	public void t01_construtor() {
		assertEquals(null, message.getMessageInfo());
		assertEquals(null, message.getData());
		assertEquals(null, message.getSessionId());
		assertEquals(true, message.isCompressed());
	}

	/**
	 * Description:	Create empty message with Null-DataParameter<br>
	 * Expectation:	all values are empty
	 */
	@Test
	public void t02_constructor() {
		message = new SCMessage();
		message.setData(null);
		assertEquals(null, message.getMessageInfo());
		assertEquals(null, message.getData());
		assertEquals(null, message.getSessionId());
		assertEquals(true, message.isCompressed());
		assertEquals(false, message.isFault());
	}

	/**
	 * Description:	Set compress modus to true<br>
	 * Expectation:	Compress is set to true
	 */
	@Test
	public void t10_Compressed() {
		message.setCompressed(true);
		assertEquals("Compressed", true, message.isCompressed());
	}

	/**
	 * Description:	Set compress modus to false<br>
	 * Expectation:	Compress is set to false
	 */
	@Test
	public void t11_Compressed() {
		message.setCompressed(false);
		assertEquals("Compressed", false, message.isCompressed());
	}

	/**
	 * Description:	Create empty message with Object-DataParameter<br>
	 * Expectation:	InvalidParameter Exception
	 */
	@Test(expected = InvalidParameterException.class)
	public void t20_Data() {
		Object obj = new Object();
		message.setData(obj);
	}

	/**
	 * Description:	Set arbitrary string to DataParameter<br>
	 * Expectation:	DataParameter is set to arbitrary string
	 */
	@Test
	public void t21_Data() {
		message.setData(TestConstants.pangram);
		assertEquals("DataParameter ",TestConstants.pangram, message.getData());
	}

	/**
	 * Description:	Set 1MB Array to DataParameter<br>
	 * Expectation:	DataParameter is set to 1MB Array
	 */
	@Test
	public void t22_Data() {
		message.setData(new byte[1048576]);
		assertEquals("DataParameter ",1048576, ((byte[]) message.getData()).length);
	}

	/**
	 * Description:	Set Null-Value as MessageInfo<br>
	 * Expectation:	MessageInfo is set to Null
	 */
	@Test
	public void t30_MessageInfo() {
		message.setMessageInfo(null);
		assertEquals(null, message.getMessageInfo());
	}

	/**
	 * Description:	Set Empty-Value as MessageInfo<br>
	 * Expectation:	InvalidParameter Exception
	 */
	@Test(expected = InvalidParameterException.class)
	public void t31_MessageInfo() {
		message.setMessageInfo("");
	}
	
	/**
	 * Description:	Set empty Char as MessageInfo<br>
	 * Expectation:	MessageInfo is set to empty Char
	 */
	@Test
	public void t32_MessageInfo() {
		message.setMessageInfo(" ");
		assertEquals(" ", message.getMessageInfo());
	}

	/**
	 * Description:	Set sinlge Char as MessageInfo<br>
	 * Expectation:	MessageInfo is set to a single char Char
	 */
	@Test
	public void t33_MessageInfo() {
		message.setMessageInfo("a");
		assertEquals("a", message.getMessageInfo());
		assertEquals(1, message.getMessageInfo().length());
	}

	/**
	 * Description:	Set 256 Chars as MessageInfo<br>
	 * Expectation:	MessageInfo is set to a single 256 chars
	 */
	@Test
	public void t34_MessageInfo() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 256; i++) {
			sb.append('a');
		}
		message.setMessageInfo(sb.toString());
		assertEquals(sb.toString(), message.getMessageInfo());
		assertEquals(256, message.getMessageInfo().length());
	}

	/**
	 * Description:	Set 257 Chars as MessageInfo<br>
	 * Expectation:	InvalidParameter Exception
	 */
	@Test(expected = InvalidParameterException.class)
	public void t35_MessageInfo() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 257; i++) {
			sb.append('a');
		}
		message.setMessageInfo(sb.toString());
	}
	
	/**
	 * Description:	Set 32767 Chars as MessageInfo<br>
	 * Expectation:	InvalidParameter Exception
	 */
	@Test(expected = InvalidParameterException.class)
	public void t36_setMessageInfo() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Short.MAX_VALUE; i++) {
			sb.append('a');
		}
		message.setMessageInfo(sb.toString());
	}

	/**
	 * Description:	Set Null-Value as session Id<br>
	 * Expectation:	SessionId is set to Null
	 */
	@Test
	public void t40_SessionId() {
		((SCMessage) message).setSessionId(null);
		assertEquals(null, message.getSessionId());
	}

	/**
	 * Description:	Set empty String as session Id<br>
	 * Expectation:	SessionId is set to empty String
	 */
	@Test
	public void t41_SessionId() {
		((SCMessage) message).setSessionId("");
		assertEquals("", message.getSessionId());
	}

	/**
	 * Description:	Set single char as session Id<br>
	 * Expectation:	SessionId is set to single char.
	 */
	@Test
	public void t42_SessionId()
	{
		((SCMessage) message).setSessionId("a");
		assertEquals("a", message.getSessionId());
	}

	/**
	 * Description:	Set 32767 Chars as SessionId<br>
	 * Expectation:	The sessionId is set to 2767 Chars
	 */
	@Test
	public void T43_SessionId() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Short.MAX_VALUE; i++) {
			sb.append('a');
		}
		((SCMessage) message).setSessionId(sb.toString());
		assertEquals(sb.toString(), message.getSessionId());
	}
}
